package com.tuzhao.publicwidget.map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.tuzhao.info.RegionItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.tuzhao.activity.MainActivity.ONLYCHARGE;
import static com.tuzhao.activity.MainActivity.ONLYPARK;
import static com.tuzhao.activity.MainActivity.PARKANDCHARGE;
import static com.tuzhao.utils.DensityUtil.dp2px;


/**
 * Created by yiyi.qi on 16/10/10.
 * 整体设计采用了两个线程,一个线程用于计算组织聚合数据,一个线程负责处理Marker相关操作
 * <p>
 * optimization by juncoder
 * </p>
 */
public class ClusterOverlay implements AMap.OnCameraChangeListener, AMap.OnMarkerClickListener {
    private static final String TAG = "ClusterOverlay";

    private AMap mAMap;
    private Context mContext;

    /**
     * 全部聚合点的item
     */
    private List<ClusterItem> mAllClusterItems;

    /**
     * 聚合点
     */
    private List<Cluster> mClusters;
    private List<Cluster> mCurrentClusters; //当前的
    private List<Cluster> mNewClusters;     //新增的
    private List<Cluster> mDisappearClusters;   //消失的
    private List<Cluster> mCopyClusters;

    private HashSet<Cluster> mLastHash;
    private HashSet<Cluster> mCurrentHash;

    private int mClusterSize;
    private ClusterClickListener mClusterClickListener;
    private ClusterRender mClusterRender;
    private List<Marker> mAddMarkers = new ArrayList<>();
    private double mClusterDistance;
    private LruCache<Integer, BitmapDescriptor> mLruCache;
    private LruCache<Integer, BitmapDescriptor> mLruCacheName;
    private HandlerThread mMarkerHandlerThread;
    private HandlerThread mSignClusterThread;
    private Handler mMarkerhandler;
    private Handler mSignClusterHandler;
    private float mPXInMeters;
    private volatile boolean mIsCanceled = false;
    private Animation animation = null;

    private AtomicInteger mAtomicInteger;

    /**
     * 构造函数,批量添加聚合元素时,调用此构造函数
     *
     * @param allClusterItems 聚合元素
     * @param clusterSize     聚合元素之间的距离为多少时开始聚合为一个聚合点
     */
    public ClusterOverlay(AMap amap, List<ClusterItem> allClusterItems, int clusterSize, Context context) {
        //默认最多会缓存40张不同的图片作为聚合显示元素图片,根据自己显示需求和app使用内存情况,可以修改数量
        mLruCache = new LruCache<Integer, BitmapDescriptor>(40) {
            protected void entryRemoved(boolean evicted, Integer key, BitmapDescriptor oldValue, BitmapDescriptor newValue) {
                oldValue.recycle();
            }
        };
        mLruCacheName = new LruCache<Integer, BitmapDescriptor>(3) {
            protected void entryRemoved(boolean evicted, Integer key, BitmapDescriptor oldValue, BitmapDescriptor newValue) {
                oldValue.recycle();
            }
        };
        mAllClusterItems = new ArrayList<>();
        if (allClusterItems != null) {
            mAllClusterItems.addAll(allClusterItems);
        }
        mContext = context;
        mClusters = new ArrayList<>();
        mCurrentClusters = new ArrayList<>();
        mNewClusters = new ArrayList<>();
        mDisappearClusters = new ArrayList<>();
        mCopyClusters = new ArrayList<>();

        mLastHash = new HashSet<>();
        mCurrentHash = new HashSet<>();

        mAtomicInteger = new AtomicInteger();

        this.mAMap = amap;
        mClusterSize = clusterSize;
        mPXInMeters = mAMap.getScalePerPixel();
        mClusterDistance = mPXInMeters * mClusterSize;
        amap.setOnCameraChangeListener(this);
        amap.setOnMarkerClickListener(this);
        initThreadHandler();

        //有可能这时候已经回调了onCameraChangeFinish方法，所以还是需要计算
        assignClusters();
    }

    /**
     * 设置聚合点的点击事件
     */
    public void setOnClusterClickListener(
            ClusterClickListener clusterClickListener) {
        mClusterClickListener = clusterClickListener;
    }

    /**
     * 设置聚合元素的渲染样式，不设置则默认为气泡加数字形式进行渲染
     */
    public void setClusterRenderer(ClusterRender render) {
        mClusterRender = render;
    }

    public void onDestroy() {
        mIsCanceled = true;
        mSignClusterHandler.removeCallbacksAndMessages(null);
        mMarkerhandler.removeCallbacksAndMessages(null);
        mSignClusterThread.quit();
        mMarkerHandlerThread.quit();
        for (Marker marker : mAddMarkers) {
            marker.remove();
        }
        mAddMarkers.clear();
        mCurrentClusters.clear();
        mDisappearClusters.clear();
        mNewClusters.clear();
        mLastHash.clear();
        mCurrentHash.clear();
        mCopyClusters.clear();
        mLruCache.evictAll();
        mLruCacheName.evictAll();
        if (mClusterClickListener != null) {
            mClusterClickListener = null;
        }
    }

    //初始化Handler
    private void initThreadHandler() {
        mMarkerHandlerThread = new HandlerThread("addMarker");
        mSignClusterThread = new HandlerThread("calculateCluster" + mAtomicInteger.getAndIncrement());
        mMarkerHandlerThread.start();
        mSignClusterThread.start();
        mMarkerhandler = new MarkerHandler(mMarkerHandlerThread.getLooper());
        mSignClusterHandler = new SignClusterHandler(mSignClusterThread.getLooper());
    }

    @Override
    public void onCameraChange(CameraPosition arg0) {
    }

    @Override
    public void onCameraChangeFinish(CameraPosition arg0) {
        mPXInMeters = mAMap.getScalePerPixel();
        mClusterDistance = mPXInMeters * mClusterSize;
        assignClusters();
    }

    //点击事件
    @Override
    public boolean onMarkerClick(Marker arg0) {
        if (mClusterClickListener == null) {
            return true;
        }
        Cluster cluster = (Cluster) arg0.getObject();
        if (cluster != null) {
            mClusterClickListener.onClick(arg0, cluster.getClusterItems());
            return true;
        }
        return false;
    }

    /**
     * 将聚合元素添加至地图上
     */
    private void addClusterToMap(List<Cluster> clusters) {
        ArrayList<Marker> removeMarkers = new ArrayList<>(mAddMarkers);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        MyAnimationListener myAnimationListener = new MyAnimationListener(removeMarkers);
        for (Marker marker : removeMarkers) {
            marker.setAnimation(alphaAnimation);
            marker.setAnimationListener(myAnimationListener);
            marker.startAnimation();
        }

        for (Cluster cluster : clusters) {
            addSingleClusterToMap(cluster);
        }
    }

    /**
     * 将聚合元素添加至地图上
     */
    private void AddAndRemoveClusterToMap(List<Cluster> clusters) {
        int nullPosition = 0;

        //找出需要被删除的marker
        List<Marker> removeMarkers = new ArrayList<>(clusters.size());
        Marker removeMarker;
        for (int i = 0; i < clusters.size(); i++) {
            if (clusters.get(i) == null) {
                nullPosition = i;
                break;
            } else {
                if ((removeMarker = findMarker(clusters.get(i))) != null) {
                    removeMarkers.add(removeMarker);
                }
            }
        }

        //对需要删除的marker做透明度动画并删除
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        MyAnimationListener myAnimationListener = new MyAnimationListener(removeMarkers);
        for (Marker marker : removeMarkers) {
            marker.setAnimation(alphaAnimation);
            marker.setAnimationListener(myAnimationListener);
            marker.startAnimation();
        }

        //把新的marker添加到地图上
        for (int i = nullPosition + 1; i < clusters.size(); i++) {
            addSingleClusterToMap(clusters.get(i));
        }

    }

    private Marker findMarker(Cluster cluster) {
        LatLng latLng = cluster.getCenterLatLng();
        LatLng markerLatLng;
        for (int i = 0; i < mAddMarkers.size(); i++) {
            markerLatLng = mAddMarkers.get(i).getPosition();
            if (markerLatLng.latitude == latLng.latitude && markerLatLng.longitude == latLng.longitude) {
                return mAddMarkers.remove(i);
            }
        }
        return null;
    }

    private ScaleAnimation mADDAnimation = new ScaleAnimation(0, 1, 0, 1);

    /**
     * 将单个聚合元素添加至地图显示
     */
    private void addSingleClusterToMap(Cluster cluster) {
        if (cluster != null) {
            LatLng latlng = cluster.getCenterLatLng();
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.anchor(1.0f, 1.0f).icon(getBitmapDes(cluster)).position(latlng);
            Marker marker = mAMap.addMarker(markerOptions);
            marker.setAnimation(mADDAnimation);
            marker.setObject(cluster);
            marker.startAnimation();

            cluster.setMarker(marker);
            mAddMarkers.add(marker);
        } else {
            Log.e(TAG, "addSingleClusterToMap: cluseter is null!");
        }
    }

    /**
     * 计算出在当前地图上的聚合点
     */
    private void calculateClusters() {
        mClusters.clear();
        mCurrentClusters.clear();
        mCurrentHash.clear();
        mDisappearClusters.clear();
        mNewClusters.clear();
        mCopyClusters.clear();

        //地图获取转换器, 获取可视区域, 获取可视区域的四个点形成的经纬度范围, 得到一个经纬度范围
        LatLngBounds visibleBounds = mAMap.getProjection().getVisibleRegion().latLngBounds;
        for (ClusterItem clusterItem : mAllClusterItems) {
            if (mIsCanceled) {
                return;
            }
            LatLng latlng = clusterItem.getPosition();  //聚合元素的地理位置

            //如果点在地图可视范围内
            if (visibleBounds.contains(latlng)) {
                Cluster cluster = getCluster(latlng, mClusters);    //根据这个位置和聚合物的集合, 获得一个聚合器
                if (cluster != null) {
                    //把能聚合的点聚合成一个点
                    if (mCurrentHash.contains(cluster)) {
                        for (Cluster currentCluster : mCurrentHash) {
                            if (currentCluster.equals(cluster)) {
                                cluster = currentCluster;
                                //如果之前就是聚合点了的则把clusterItem添加进去就行,要不显示不出聚合点正确的数量
                                break;
                            }
                        }
                    }
                    cluster.addClusterItem(clusterItem);
                    cluster.setAggregation(true);
                    mCurrentHash.add(cluster);
                } else {
                    //没有能聚合的点,下次扫描出的点会以该点为标准判断是否为聚合点。如果两个点能聚合则返回上面的聚合点，但是该点并不会删除
                    cluster = new Cluster(latlng);
                    cluster.addClusterItem(clusterItem);
                    mClusters.add(cluster);
                    mCurrentHash.add(cluster);
                }
            }
        }

        Cluster lastCluster;
        for (Cluster currentCluster : mCurrentHash) {
            if (mLastHash.contains(currentCluster)) {
                mCurrentClusters.add(currentCluster);
                lastCluster = findCluster(currentCluster, mLastHash);
                if (lastCluster != null) {
                    if (currentCluster.isAggregation() && !lastCluster.isAggregation()) {
                        //如果当前是聚合点而上次不是，则把上次的点删掉，并添加当前点
                        mDisappearClusters.add(lastCluster);
                        mNewClusters.add(currentCluster);
                    } else if (!currentCluster.isAggregation() && lastCluster.isAggregation()) {
                        //如果当前不是聚合点而上次是
                        mDisappearClusters.add(lastCluster);
                        mNewClusters.add(currentCluster);
                    } else if (currentCluster.isAggregation() && lastCluster.isAggregation() && currentCluster.getClusterCount() != lastCluster.getClusterCount()) {
                        //如果两次都是聚合点但是数量不一样还是要刷新
                        mDisappearClusters.add(lastCluster);
                        mNewClusters.add(currentCluster);
                    }
                }
            } else {
                //如果上次没有这个点的则是新增的点
                mNewClusters.add(currentCluster);
            }
        }

        for (Cluster cluster : mLastHash) {
            if (!mCurrentClusters.contains(cluster)) {
                mDisappearClusters.add(cluster);
            }
        }

        mLastHash.clear();
        mLastHash.addAll(mCurrentHash);

        mCopyClusters.addAll(mDisappearClusters);
        mCopyClusters.add(null);                        //添加一个null作为分割点
        mCopyClusters.addAll(mNewClusters);

        Message message = Message.obtain();
        message.what = MarkerHandler.ADD_AND_REMOVE_CLUSTER_LIST;
        message.obj = mCopyClusters;
        if (mIsCanceled) {
            return;
        }
        mMarkerhandler.sendMessage(message);
    }

    private Cluster findCluster(Cluster cluster, HashSet<Cluster> hashSet) {
        LatLng latLng = cluster.getCenterLatLng();
        LatLng clusterLatLng;
        for (Cluster cluster1 : hashSet) {
            clusterLatLng = cluster1.getCenterLatLng();
            if (latLng.latitude == clusterLatLng.latitude && latLng.longitude == clusterLatLng.longitude) {
                return cluster1;
            }
        }
        return null;
    }

    /**
     * 对点进行聚合
     */
    private void assignClusters() {
        mIsCanceled = true;
        mSignClusterHandler.removeMessages(SignClusterHandler.CALCULATE_CLUSTER);
        mSignClusterHandler.sendEmptyMessage(SignClusterHandler.CALCULATE_CLUSTER);
    }

    /**
     * 开始计算聚合点并显示
     */
    public void assignClusters(List<ClusterItem> clusterItems) {
        //把原来的计算停止掉
        mIsCanceled = true;
        mSignClusterThread.quit();
        mSignClusterHandler.removeMessages(SignClusterHandler.CALCULATE_CLUSTER);
        mMarkerHandlerThread.quit();
        mMarkerhandler.removeCallbacksAndMessages(null);

        mAllClusterItems.clear();
        mAllClusterItems.addAll(clusterItems);

        mMarkerHandlerThread = new HandlerThread("addMarker" + mAtomicInteger.getAndIncrement());
        mMarkerHandlerThread.start();
        mMarkerhandler = new MarkerHandler(mMarkerHandlerThread.getLooper());

        //发送消息开始计算
        mSignClusterThread = new HandlerThread("calculateCluster" + mAtomicInteger.getAndIncrement());
        mSignClusterThread.start();
        mSignClusterHandler = new SignClusterHandler(mSignClusterThread.getLooper());
        mSignClusterHandler.sendEmptyMessage(SignClusterHandler.CALCULATE_CLUSTER);
    }

    /**
     * 在已有的聚合基础上，对添加的单个元素进行聚合
     */
    private void calculateSingleCluster(ClusterItem clusterItem) {
        LatLngBounds visibleBounds = mAMap.getProjection().getVisibleRegion().latLngBounds;
        LatLng latlng = clusterItem.getPosition();
        if (!visibleBounds.contains(latlng)) {
            return;
        }
        Cluster cluster = getCluster(latlng, mClusters);
        if (cluster != null) {
            cluster.addClusterItem(clusterItem);
            Message message = Message.obtain();
            message.what = MarkerHandler.UPDATE_SINGLE_CLUSTER;
            ClusterAndType clusterAndType;
            if (clusterItem.isparkspace()) {
                if (clusterItem.getCancharge().equals("-1")) {
                    clusterAndType = new ClusterAndType(cluster, ONLYPARK);
                } else {
                    clusterAndType = new ClusterAndType(cluster, PARKANDCHARGE);
                }
            } else {
                clusterAndType = new ClusterAndType(cluster, ONLYCHARGE);
            }

            message.obj = clusterAndType;
            mMarkerhandler.removeMessages(MarkerHandler.UPDATE_SINGLE_CLUSTER);
            mMarkerhandler.sendMessageDelayed(message, 5);
        } else {
            cluster = new Cluster(latlng);
            mClusters.add(cluster);
            cluster.addClusterItem(clusterItem);
            Message message = Message.obtain();
            message.what = MarkerHandler.ADD_SINGLE_CLUSTER;
            message.obj = cluster;
            mMarkerhandler.sendMessage(message);
        }
    }

    /**
     * 根据一个点获取是否可以依附的聚合点，没有则返回null
     */
    private Cluster getCluster(LatLng latLng, List<Cluster> clusters) {
        for (Cluster cluster : clusters) {
            LatLng clusterCenterPoint = cluster.getCenterLatLng();
            double distance = AMapUtils.calculateLineDistance(latLng, clusterCenterPoint);
            if (distance < mClusterDistance && mAMap.getCameraPosition().zoom < 19) {
                //cluster.setCenterLatLng(latLng);
                return cluster;
            }
        }
        return null;
    }

    /**
     * 获取每个聚合点的绘制样式
     */
    private BitmapDescriptor getBitmapDes(Cluster cluster) {
        BitmapDescriptor bitmapDescriptor;
        if (cluster.getClusterCount() > 1) {//当数量》1设置个数

            bitmapDescriptor = mLruCache.get(cluster.getClusterCount());

            if (bitmapDescriptor == null) {
                TextView textView = new TextView(mContext);
                int number = cluster.getClusterCount();
                textView.setText(String.valueOf(number));
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(Color.BLACK);
                if (number < 10) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                } else if (number <= 99) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                } else if (number <= 999) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                } else {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                }

                if (mClusterRender != null && mClusterRender.getDrawable(cluster.getClusterCount(), 0) != null) {
                    textView.setBackground(mClusterRender.getDrawable(cluster.getClusterCount(), 0));
                }
                bitmapDescriptor = BitmapDescriptorFactory.fromView(textView);
                mLruCache.put(cluster.getClusterCount(), bitmapDescriptor);
            }
        } else {//否则，设置名称
            RegionItem mRegionItem = (RegionItem) cluster.getClusterItems().get(0);
            int type;
            if (mRegionItem.isparkspace()) {
                if (mRegionItem.getCancharge().equals("-1")) {
                    type = ONLYPARK;
                } else {
                    type = PARKANDCHARGE;
                }
            } else {
                type = ONLYCHARGE;
            }
            bitmapDescriptor = mLruCacheName.get(type);
            if (bitmapDescriptor == null) {
                TextView textView = new TextView(mContext);
                if (mClusterRender != null && mClusterRender.getDrawable(cluster.getClusterCount(), type) != null) {
                    textView.setBackground(mClusterRender.getDrawable(cluster.getClusterCount(), type));
                }
                bitmapDescriptor = BitmapDescriptorFactory.fromView(textView);
                mLruCacheName.put(type, bitmapDescriptor);
            }
        }
        return bitmapDescriptor;
    }

    /**
     * 更新已加入地图聚合点的样式
     */
    private void updateCluster(Cluster cluster) {
        Marker marker = cluster.getMarker();
        marker.setIcon(getBitmapDes(cluster));
    }


//-----------------------辅助内部类用---------------------------------------------

    /**
     * marker渐变动画，动画结束后将Marker删除
     */
    class MyAnimationListener implements Animation.AnimationListener {
        private List<Marker> mRemoveMarkers;

        MyAnimationListener(List<Marker> removeMarkers) {
            mRemoveMarkers = removeMarkers;
        }

        @Override
        public void onAnimationStart() {

        }

        @Override
        public void onAnimationEnd() {
            for (Marker marker : mRemoveMarkers) {
                marker.remove();
            }
            mRemoveMarkers.clear();
        }
    }

    /**
     * 处理marker添加，更新等操作
     */
    class MarkerHandler extends Handler {

        /**
         * 没有能聚合起来的点，仅添加新的聚合点和删除不再显示的点
         */
        static final int ADD_AND_REMOVE_CLUSTER_LIST = 3;

        static final int ADD_CLUSTER_LIST = 0;

        static final int ADD_SINGLE_CLUSTER = 1;

        static final int UPDATE_SINGLE_CLUSTER = 2;

        MarkerHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case ADD_CLUSTER_LIST:
                    List<Cluster> clusters = (List<Cluster>) message.obj;
                    addClusterToMap(clusters);
                    break;
                case ADD_AND_REMOVE_CLUSTER_LIST:
                    List<Cluster> clusterList = (List<Cluster>) message.obj;
                    AddAndRemoveClusterToMap(clusterList);
                    break;
                case ADD_SINGLE_CLUSTER:
                    Cluster cluster = (Cluster) message.obj;
                    addSingleClusterToMap(cluster);
                    break;
                case UPDATE_SINGLE_CLUSTER:
                    ClusterAndType clusterAndType = (ClusterAndType) message.obj;
                    updateCluster(clusterAndType.getCluster());
                    break;
            }
        }
    }

    /**
     * 处理聚合点算法线程
     */
    class SignClusterHandler extends Handler {
        static final int CALCULATE_CLUSTER = 0;
        static final int CALCULATE_SINGLE_CLUSTER = 1;

        SignClusterHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case CALCULATE_CLUSTER:
                    mIsCanceled = false;
                    calculateClusters();
                    break;
                case CALCULATE_SINGLE_CLUSTER:
                    ClusterItem item = (ClusterItem) message.obj;
                    mAllClusterItems.add(item);
                    calculateSingleCluster(item);
                    break;
            }
        }
    }

    private void screenMarkerJump(AMap aMap, Marker screenMarker) {
        if (screenMarker != null) {
            final LatLng latLng = screenMarker.getPosition();
            Point point = aMap.getProjection().toScreenLocation(latLng);
            point.y -= dp2px(mContext, 20);
            LatLng target = aMap.getProjection().fromScreenLocation(point);
            //使用TranslateAnimation,填写一个需要移动的目标点
            animation = new TranslateAnimation(target);
            animation.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    // 模拟重加速度的interpolator
                    if (input <= 0.5) {
                        return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
                    } else {
                        return (float) (0.5f - Math.sqrt((input - 0.5f) * (1.5f - input)));
                    }
                }
            });
            //整个移动所需要的时间
            animation.setDuration(600);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationEnd() {
                    animation = null;
                }
            });
            //设置动画
            screenMarker.setAnimation(animation);
            //开始动画
            screenMarker.startAnimation();
        }
    }

    class ClusterAndType {
        private Cluster cluster;
        private int type;

        ClusterAndType(Cluster cluster, int type) {
            this.cluster = cluster;
            this.type = type;
        }

        public Cluster getCluster() {
            return cluster;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

}