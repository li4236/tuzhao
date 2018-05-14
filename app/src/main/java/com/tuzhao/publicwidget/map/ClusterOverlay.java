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
import java.util.List;

import static com.tuzhao.activity.MainActivity.ONLYCHARGE;
import static com.tuzhao.activity.MainActivity.ONLYPARK;
import static com.tuzhao.activity.MainActivity.PARKANDCHARGE;
import static com.tuzhao.utils.DensityUtil.dp2px;


/**
 * Created by yiyi.qi on 16/10/10.
 * 整体设计采用了两个线程,一个线程用于计算组织聚合数据,一个线程负责处理Marker相关操作
 */
public class ClusterOverlay implements AMap.OnCameraChangeListener, AMap.OnMarkerClickListener {
    private static final String TAG = "ClusterOverlay";

    private AMap mAMap;
    private Context mContext;
    private List<ClusterItem> mClusterItems;        //全部的聚合点位置
    private List<Cluster> mClusters;
    private List<Cluster> mLastClusters; //已经添加过的Cluster
    private List<Cluster> mCurrentClusters; //当前的
    private List<Cluster> mNewClusters;     //新增的
    private List<Cluster> mDisappearClusters;   //消失的

    private int mClusterSize;
    private ClusterClickListener mClusterClickListener;
    private ClusterRender mClusterRender;
    private List<Marker> mAddMarkers = new ArrayList<>();
    private double mClusterDistance;
    private LruCache<Integer, BitmapDescriptor> mLruCache;
    private LruCache<Integer, BitmapDescriptor> mLruCacheName;
    private HandlerThread mMarkerHandlerThread = new HandlerThread("addMarker");
    private HandlerThread mSignClusterThread = new HandlerThread("calculateCluster");
    private Handler mMarkerhandler;
    private Handler mSignClusterHandler;
    private float mPXInMeters;
    private boolean mIsCanceled = false;
    private Marker screeMarker = null;
    private Animation animation = null;

    private OnCameraMoveRequestData onCameraMoveRequestData;

    /**
     * 构造函数
     *
     * @param amap
     * @param clusterSize 聚合范围的大小（指点像素单位距离内的点会聚合到一个点显示）
     * @param context
     */
    public ClusterOverlay(AMap amap, int clusterSize, Context context) {
        this(amap, null, clusterSize, context);
    }

    /**
     * 构造函数,批量添加聚合元素时,调用此构造函数
     *
     * @param amap
     * @param clusterItems 聚合元素
     * @param clusterSize
     * @param context
     */
    public ClusterOverlay(AMap amap, List<ClusterItem> clusterItems, int clusterSize, Context context) {
//默认最多会缓存80张图片作为聚合显示元素图片,根据自己显示需求和app使用内存情况,可以修改数量
        mLruCache = new LruCache<Integer, BitmapDescriptor>(10) {
            protected void entryRemoved(boolean evicted, Integer key, BitmapDescriptor oldValue, BitmapDescriptor newValue) {
                oldValue.getBitmap().recycle();
            }
        };
        mLruCacheName = new LruCache<Integer, BitmapDescriptor>(5) {
            protected void entryRemoved(boolean evicted, Integer key, BitmapDescriptor oldValue, BitmapDescriptor newValue) {
                oldValue.getBitmap().recycle();
            }
        };
        if (clusterItems != null) {
            mClusterItems = clusterItems;
        } else {
            mClusterItems = new ArrayList<>();
        }
        mContext = context;
        mClusters = new ArrayList<>();
        mLastClusters = new ArrayList<>();
        mCurrentClusters = new ArrayList<>();
        mNewClusters = new ArrayList<>();
        mDisappearClusters = new ArrayList<>();
        this.mAMap = amap;
        mClusterSize = clusterSize;
        mPXInMeters = mAMap.getScalePerPixel();
        mClusterDistance = mPXInMeters * mClusterSize;
        amap.setOnCameraChangeListener(this);
        amap.setOnMarkerClickListener(this);
        initThreadHandler();
        assignClusters();
    }

    /**
     * 设置聚合点的点击事件
     *
     * @param clusterClickListener
     */
    public void setOnClusterClickListener(
            ClusterClickListener clusterClickListener) {
        mClusterClickListener = clusterClickListener;
    }

    /**
     * 添加一个聚合点
     *
     * @param item
     */
    public void addClusterItem(ClusterItem item) {
        Message message = Message.obtain();
        message.what = SignClusterHandler.CALCULATE_SINGLE_CLUSTER;
        message.obj = item;
        mSignClusterHandler.sendMessage(message);
    }

    /**
     * 设置聚合元素的渲染样式，不设置则默认为气泡加数字形式进行渲染
     *
     * @param render
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
//        mLruCache.evictAll();
    }

    //初始化Handler
    private void initThreadHandler() {
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
        if (screeMarker != null) {
            if (screeMarker.isVisible() && animation == null) {
                screenMarkerJump(mAMap, screeMarker);
                if (onCameraMoveRequestData != null) {
                    onCameraMoveRequestData.OnCameraMoveRequestData();
                }
            }
        }

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
        ArrayList<Marker> removeMarkers = new ArrayList<>();
        removeMarkers.addAll(mAddMarkers);
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
    private void justAddClusterToMap(List<Cluster> clusters) {
        int nullPosition = 0;
        List<Marker> removeMarkers = new ArrayList<>(clusters.size() - nullPosition);
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

        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        MyAnimationListener myAnimationListener = new MyAnimationListener(removeMarkers);
        for (Marker marker : removeMarkers) {
            marker.setAnimation(alphaAnimation);
            marker.setAnimationListener(myAnimationListener);
            marker.startAnimation();
        }

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
     *
     * @param cluster
     */
    private void addSingleClusterToMap(Cluster cluster) {
        LatLng latlng = cluster.getCenterLatLng();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.anchor(1.0f, 1.0f).icon(getBitmapDes(cluster)).position(latlng);
        Marker marker = mAMap.addMarker(markerOptions);
        marker.setAnimation(mADDAnimation);
        marker.setObject(cluster);
        marker.startAnimation();

        cluster.setMarker(marker);
        mAddMarkers.add(marker);
    }

    /**
     * 计算出在当前地图上的聚合点
     */
    private void calculateClusters() {
        mIsCanceled = false;
        mClusters.clear();
        mCurrentClusters.clear();
        mNewClusters.clear();
        mDisappearClusters.clear();

        //复制一份数据，规避同步
        List<Cluster> copyClusters = new ArrayList<>();

        LatLngBounds visibleBounds = mAMap.getProjection().getVisibleRegion().latLngBounds;
        for (ClusterItem clusterItem : mClusterItems) {
            if (mIsCanceled) {
                return;
            }
            LatLng latlng = clusterItem.getPosition();

            //如果聚合点在地图可视范围内
            if (visibleBounds.contains(latlng)) {
                Cluster cluster = getCluster(latlng, mClusters);
                if (cluster != null) {
                    //把能聚合的点聚合成一个点
                    cluster.addClusterItem(clusterItem);
                    mCurrentClusters.add(cluster);
                    Log.e(TAG, "calculateClusters: not null");

                    mDisappearClusters.add(cluster);
                    mNewClusters.add(cluster);
                    /*if (!mLastClusters.contains(cluster)) {
                        Log.e(TAG, "calculateClusters: not null and not contain");
                        mNewClusters.add(cluster);
                    }*/
                } else {
                    //没有能聚合的点
                    cluster = new Cluster(latlng);
                    cluster.addClusterItem(clusterItem);
                    mClusters.add(cluster);
                    mCurrentClusters.add(cluster);
                    Log.e(TAG, "calculateClusters: null");
                    if (!mLastClusters.contains(cluster)) {
                        mNewClusters.add(cluster);
                    }
                }

            }
        }

        for (Cluster cluster : mLastClusters) {
            if (!mCurrentClusters.contains(cluster)) {
                mDisappearClusters.add(cluster);
            }
        }
        Log.e(TAG, "calculateClusters  mLastClusters: " + mLastClusters.size());
        mLastClusters.clear();
        mLastClusters.addAll(mCurrentClusters);

        copyClusters.addAll(mDisappearClusters);
        copyClusters.add(null);
        copyClusters.addAll(mNewClusters);

        Log.e(TAG, "calculateClusters  mCurrentClusters: " + mCurrentClusters.size());
        Log.e(TAG, "calculateClusters  mClusters: " + mClusters.size());
        Log.e(TAG, "calculateClusters  copyClusters: " + copyClusters.size());
        Log.e(TAG, "calculateClusters mNewClusters: " + mNewClusters.size());
        Log.e(TAG, "calculateClusters  mDisappearClusters: " + mDisappearClusters.size());
        Message message = Message.obtain();
        message.what = MarkerHandler.JUST_ADD_CLUSTER_LIST;
        message.obj = copyClusters;
        if (mIsCanceled) {
            return;
        }
        mMarkerhandler.sendMessage(message);
    }

    private boolean containCluster(Cluster cluster, List<Cluster> clusters) {
        LatLng latLng = cluster.getCenterLatLng();
        LatLng clusterLatLng;
        for (Cluster cluster1 : clusters) {
            clusterLatLng = cluster1.getCenterLatLng();
            if (latLng.latitude == clusterLatLng.latitude && latLng.longitude == clusterLatLng.longitude) {
                return true;
            }
        }
        return false;
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
     * 在已有的聚合基础上，对添加的单个元素进行聚合
     *
     * @param clusterItem
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
                return cluster;
            }
        }

        return null;
    }


    /**
     * 获取每个聚合点的绘制样式
     */
    private BitmapDescriptor getBitmapDes(Cluster cluster) {

        BitmapDescriptor bitmapDescriptor = null;
        if (cluster.getClusterCount() > 1) {//当数量》1设置个数

            bitmapDescriptor = mLruCache.get(cluster.getClusterCount());

            if (bitmapDescriptor == null) {
                TextView textView = new TextView(mContext);
                String tile = String.valueOf(cluster.getClusterCount());
                textView.setText(tile);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(Color.BLACK);
                if (cluster.getClusterCount() < 10) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                } else if (cluster.getClusterCount() >= 10 && cluster.getClusterCount() <= 99) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                } else {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
                }
                if (mClusterRender != null && mClusterRender.getDrawAble(cluster.getClusterCount(), 0) != null) {
                    textView.setBackgroundDrawable(mClusterRender.getDrawAble(cluster.getClusterCount(), 0));
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
                if (mClusterRender != null && mClusterRender.getDrawAble(cluster.getClusterCount(), type) != null) {
                    textView.setBackgroundDrawable(mClusterRender.getDrawAble(cluster.getClusterCount(), type));
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
     * 处理market添加，更新等操作
     */
    class MarkerHandler extends Handler {

        /**
         * 没有能聚合起来的点，仅添加新的聚合点
         */
        static final int JUST_ADD_CLUSTER_LIST = 3;

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
                case JUST_ADD_CLUSTER_LIST:
                    List<Cluster> clusterList = (List<Cluster>) message.obj;
                    justAddClusterToMap(clusterList);
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
                    calculateClusters();
                    break;
                case CALCULATE_SINGLE_CLUSTER:
                    ClusterItem item = (ClusterItem) message.obj;
                    mClusterItems.add(item);
                    Log.i("yiyi.qi", "calculate single cluster");
                    calculateSingleCluster(item);
                    break;
            }
        }
    }

    public Marker getScreeMarker() {
        return screeMarker;
    }

    public void setScreeMarker(Marker screeMarker) {
        this.screeMarker = screeMarker;
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

        public ClusterAndType(Cluster cluster, int type) {
            this.cluster = cluster;
            this.type = type;
        }

        public Cluster getCluster() {
            return cluster;
        }

        public void setCluster(Cluster cluster) {
            this.cluster = cluster;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    public interface OnCameraMoveRequestData {
        void OnCameraMoveRequestData();
    }

    public void setOnCameraMoveRequestData(OnCameraMoveRequestData onCameraMoveRequestData) {
        this.onCameraMoveRequestData = onCameraMoveRequestData;
    }
}