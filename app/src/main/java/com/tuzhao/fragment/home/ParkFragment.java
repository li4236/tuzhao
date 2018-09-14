package com.tuzhao.fragment.home;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.ParkspaceDetailActivity;
import com.tuzhao.fragment.base.BaseFragment;
import com.tuzhao.info.NearPointPCInfo;


/**
 * Created by TZL11 on 2017/4/18.
 */

public class ParkFragment extends BaseFragment {

    /**
     * UI
     */
    private View mContentView;
//    private LinearLayout linearlayout_parkspacedetalis, linearlayout_goorder, linearlayout_detail, linearlayout_all, linearlayout_daohang, linearlayout_comment, linearlayout_collection;
//    private ImageView imageview_show, imageview_ad, imageview_collection;
//    private TextView textview_parksapce, textview_ispublic, textview_psaddress, textview_distance, textview_distance_dw, textview_hightime, textview_highfee, textview_highmaxtime, textview_lowtime, textview_lowfee, textview_lowmaxfee,textview_parkcount;
//    private CBRatingBar cbratingbar;
//    private LoadingDialog mCustomDialog;
//    private LoginDialogFragment loginDialogFragment;

    /**
     * 数据相关
     */
    private NearPointPCInfo nearPointPCInfo = null;
    private String parkspace_id = null;
    private String city_code = null;
//    private boolean parkspace_issuccess = true, park_issuccess = false;
//    private ArrayList<Park_Info> mData = new ArrayList<>();//已当前时间来比较可以停的车位
//    private ArrayList<ParkOrderInfo> mOrderList = null;
//    private DateUtil dateUtil = new DateUtil();

//    /**
//     * 收藏
//     */
//    private CollectionManager.MessageHolder holder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mContentView = inflater.inflate(R.layout.fragment_parkspace_layout_refactor, container, false);

        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件

        return mContentView;
    }

    private void initData() {
        parkspace_id = getArguments().getString("belong_id");
        city_code = getArguments().getString("city_code");
        nearPointPCInfo = (NearPointPCInfo) getArguments().getSerializable("pssinfo");
//        if (parkspace_id != null) {
//            initLoading("加载中...");
//            if (city_code == null) {
//                city_code = LocationManager.getInstance().hasLocation() ? LocationManager.getInstance().getmAmapLocation().getCityCode() : "010";
//            }
//            requestGetParkspaceData(parkspace_id);
//            requestGetParkListData(parkspace_id);
//        } else {
//            mContext.finish();
//        }
//
//        holder = (CollectionManager.getInstance().checkCollectionDatas(parkspace_id, "1"));
//        if (holder.isExist) {
//            imageview_collection.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.ic_scchenggong));
//        }
//
//        if (UserManager.getInstance().hasLogined()) {
//            requestGetUserParkOrderForAppoint();
//        }

        ((TextView)mContentView.findViewById(R.id.id_fragment_parkspace_layout_textview_psname)).setText(nearPointPCInfo.getName());
        ((TextView)mContentView.findViewById(R.id.id_fragment_parkspace_layout_textview_psprice)).setText("均价"+nearPointPCInfo.getPrice()+"元/小时");
    }

    private void initView() {
//        linearlayout_parkspacedetalis = (LinearLayout) mContentView.findViewById(R.id.id_fragment_parkspace_layout_linearlayout_parkspacedetalis);
//        linearlayout_comment = (LinearLayout) mContentView.findViewById(R.id.id_fragment_parkspace_layout_linearlayout_comment);
//        linearlayout_all = (LinearLayout) mContentView.findViewById(R.id.id_fragment_parkspace_layout_linearlayout_all);
//        linearlayout_daohang = (LinearLayout) mContentView.findViewById(R.id.id_fragment_parkspace_layout_linearlayout_daohang);
//        linearlayout_collection = (LinearLayout) mContentView.findViewById(R.id.id_fragment_parkspace_layout_linearlayout_collection);
//        imageview_show = (ImageView) mContentView.findViewById(R.id.id_fragment_parkspace_layout_imageview_show);
//        imageview_ad = (ImageView) mContentView.findViewById(R.id.id_fragment_parkspace_layout_imageview_ad);
//        imageview_collection = (ImageView) mContentView.findViewById(R.id.id_fragment_parkspace_layout_imageview_collection);
//        textview_parksapce = (TextView) mContentView.findViewById(R.id.id_fragment_parkspace_layout_textview_parksapce);
//        textview_ispublic = (TextView) mContentView.findViewById(R.id.id_fragment_parkspace_layout_textview_ispublic);
//        textview_distance = (TextView) mContentView.findViewById(R.id.id_fragment_parkspace_layout_textview_distance);
//        textview_distance_dw = (TextView) mContentView.findViewById(R.id.id_fragment_parkspace_layout_textview_distance_dw);
//        textview_hightime = (TextView) mContentView.findViewById(R.id.id_fragment_parkspace_layout_textview_hightime);
//        textview_highfee = (TextView) mContentView.findViewById(R.id.id_fragment_parkspace_layout_textview_highfee);
//        textview_highmaxtime = (TextView) mContentView.findViewById(R.id.id_fragment_parkspace_layout_textview_highmaxtime);
//        textview_lowtime = (TextView) mContentView.findViewById(R.id.id_fragment_parkspace_layout_textview_lowtime);
//        textview_lowfee = (TextView) mContentView.findViewById(R.id.id_fragment_parkspace_layout_textview_lowfee);
//        textview_lowmaxfee = (TextView) mContentView.findViewById(R.id.id_fragment_parkspace_layout_textview_lowmaxfee);
//        linearlayout_goorder = (LinearLayout) mContentView.findViewById(R.id.id_fragment_parkspace_layout_linearlayout_goorder);
//        linearlayout_detail = (LinearLayout) mContentView.findViewById(R.id.id_fragment_parkspace_layout_linearlayout_detail);
//        textview_psaddress = (TextView) mContentView.findViewById(R.id.id_fragment_parkspace_layout_textview_psaddress);
//        textview_parkcount = (TextView) mContentView.findViewById(R.id.id_fragment_parkspace_layout_textview_parkcount);
//        cbratingbar = (CBRatingBar) mContentView.findViewById(R.id.id_fragment_parkspace_layout_cbratingbar);
    }

    private void initEvent() {

//        linearlayout_parkspacedetalis.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, ParkspaceDetailActivity.class);
//                if (parkspace_info == null) {
//                    intent.putExtra("parkspace_id", parkspace_id);
//                    intent.putExtra("city_code", city_code);
//                } else {
//                    intent.putExtra("parkspace_info", parkspace_info);
//                }
//                intent.putExtra("park_list", mData.size() > 0 ? mData : null);
//                startActivity(intent);
//            }
//        });
//        linearlayout_detail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, ParkspaceDetailActivity.class);
//                if (parkspace_info == null) {
//                    intent.putExtra("parkspace_id", parkspace_id);
//                    intent.putExtra("city_code", city_code);
//                } else {
//                    intent.putExtra("parkspace_info", parkspace_info);
//                }
//                intent.putExtra("park_list", mData.size() > 0 ? mData : null);
//                startActivity(intent);
//            }
//        });
//        linearlayout_comment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, ParkspaceDetailActivity.class);
//                if (parkspace_info == null) {
//                    intent.putExtra("parkspace_id", parkspace_id);
//                    intent.putExtra("city_code", city_code);
//                } else {
//                    intent.putExtra("parkspace_info", parkspace_info);
//                }
//                intent.putExtra("position", 1);
//                intent.putExtra("park_list", mData.size() > 0 ? mData : null);
//                startActivity(intent);
//            }
//        });
//
//        linearlayout_goorder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (UserManager.getInstance().hasLogined()) {
//                    if (mData == null || mData.size() <= 0) {
//                        MyToast.showToast(mContext, "暂无停车位，稍后再来试试哦", 5);
//                    } else {
//                        if (mOrderList == null) {
//                            initLoading("加载中...");
//                            requestGetUserParkOrderForAppoint();
//                        } else {
//                            int aingcount = 0;
//                            for (ParkOrderInfo parkOrderInfo : mOrderList) {
//                                if (parkOrderInfo.getOrder_status().equals("3")) {
//                                    MyToast.showToast(mContext, "您当前还有为付款订单", 5);
//                                    return;
//                                }else {
//                                    if (parkOrderInfo.getOrderStatus().equals("1")||parkOrderInfo.getOrderStatus().equals("2")){
//                                        aingcount++;
//                                    }
//                                }
//                            }
//                            if (aingcount>1){
//                                MyToast.showToast(mContext, "预定订单数量已达上限哦", 5);
//                                return;
//                            }else {
//                                Intent intent = new Intent(mContext, OrderParkActivity.class);
//                                intent.putExtra("parkspace_info", parkspace_info);
//                                intent.putExtra("park_list", mData);
//                                intent.putExtra("order_list", mOrderList);
//                                startActivity(intent);
//                            }
//                        }
//                    }
//                } else {
//                    login();
//                }
//            }
//        });
//
        mContentView.findViewById(R.id.id_fragment_parkspace_layout_linearlayout_allclick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //啥也不用干，消耗点击事件
                //点击跳转详情页面
                Intent intent = new Intent(mContext, ParkspaceDetailActivity.class);
                intent.putExtra("parkspace_id", nearPointPCInfo.getId());
                intent.putExtra("city_code", nearPointPCInfo.getCity_code());
                startActivity(intent);
            }
        });
//
//        linearlayout_daohang.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MyApplication.getInstance(), RouteNaviActivity.class);
//                intent.putExtra("gps", true);
//                intent.putExtra("start", new NaviLatLng(LocationManager.getInstance().getmAmapLocation().getLatitude(), LocationManager.getInstance().getmAmapLocation().getLongitude()));
//                intent.putExtra("end", new NaviLatLng(parkspace_info.getLatitude(), parkspace_info.getLongitude()));
//                startActivity(intent);
//            }
//        });
//
//        linearlayout_collection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (UserManager.getInstance().hasLogined()) {
//                    holder = (CollectionManager.getInstance().checkCollectionDatas(parkspace_id, "1"));
//                    if (holder.isExist) {
//                        initLoading("正在取消收藏...");
//                        OkGo.post(HttpConstants.deleteCollection)
//                                .tag(HttpConstants.deleteCollection)
//                                .params("id", CollectionManager.getInstance().getCollection_datas().get(holder.position).getId())
//                                .execute(new JsonCallback<Base_Class_Info<CollectionInfo>>() {
//                                    @Override
//                                    public void onSuccess(Base_Class_Info<CollectionInfo> collection_infoBase_class_info, Call call, Response response) {
//                                        if (mCustomDialog.isShowing()) {
//                                            mCustomDialog.dismiss();
//                                        }
//                                        MyToast.showToast(mContext, "已取消收藏", 5);
//                                        imageview_collection.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.ic_shoucang2));
//                                        CollectionManager.getInstance().removeOneCollection(holder.position);
//                                    }
//
//                                    @Override
//                                    public void onError(Call call, Response response, Exception e) {
//                                        super.onError(call, response, e);
//                                        if (mCustomDialog.isShowing()) {
//                                            mCustomDialog.dismiss();
//                                        }
//                                        MyToast.showToast(mContext, "取消失败", 5);
//                                    }
//                                });
//                    } else {
//                        initLoading("正在添加收藏...");
//                        OkGo.post(HttpConstants.addCollection)
//                                .tag(HttpConstants.addCollection)
//                                .params("belong_id", parkspace_id)
//                                .params("user_id", UserManager.getInstance().getUserInfo().getId())
//                                .params("type", "1")
//                                .params("citycode",city_code)
//                                .execute(new JsonCallback<Base_Class_Info<CollectionInfo>>() {
//                                    @Override
//                                    public void onSuccess(Base_Class_Info<CollectionInfo> collection_infoBase_class_list_info, Call call, Response response) {
//                                        if (mCustomDialog.isShowing()) {
//                                            mCustomDialog.dismiss();
//                                        }
//                                        List<CollectionInfo> collection_datas = CollectionManager.getInstance().getCollection_datas();
//                                        if (collection_datas == null) {
//                                            collection_datas = new ArrayList<>();
//                                        }
//                                        collection_infoBase_class_list_info.data.setCitycode(city_code);
//                                        collection_datas.add(collection_infoBase_class_list_info.data);
//                                        CollectionManager.getInstance().setCollection_datas(collection_datas);
//                                        MyToast.showToast(mContext, "收藏成功", 5);
//                                        imageview_collection.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.ic_scchenggong));
//                                    }
//
//                                    @Override
//                                    public void onError(Call call, Response response, Exception e) {
//                                        super.onError(call, response, e);
//                                        if (mCustomDialog.isShowing()) {
//                                            mCustomDialog.dismiss();
//                                        }
//                                        MyToast.showToast(mContext, "收藏失败", 5);
//                                    }
//                                });
//                    }
//
//                } else {
//                    login();
//                }
//            }
//        });
    }

//    private void requestGetParkspaceData(String parkspace_id) {
//        OkGo.post(HttpConstants.getOneParkSpaceData)
//                .tag(HttpConstants.getOneParkSpaceData)
//                .params("parkspace_id", parkspace_id)
//                .params("citycode", city_code)
//                .params("ad_position", "2")
//                .execute(new JsonCallback<Base_Class_Info<ParkLotInfo>>() {
//                    @Override
//                    public void onSuccess(Base_Class_Info<ParkLotInfo> park_space_infoBase_class_info, Call call, Response response) {
//                        parkspace_issuccess = true;
//                        if (park_issuccess) {
//                            if (mCustomDialog.isShowing()) {
//                                mCustomDialog.dismiss();
//                            }
//                        }
//                        parkspace_info = park_space_infoBase_class_info.data;
//                        parkspace_info.setCity_code(city_code);
//                        initViewData(parkspace_info);
//                    }
//
//                    @Override
//                    public void onError(Call call, Response response, Exception e) {
//                        super.onError(call, response, e);
//                        Log.d("TAG", "请求失败，" + "网络异常" + e.toString());
//                    }
//                });
//    }
//
//    private void requestGetParkListData(String _id) {
//        OkGo.post(HttpConstants.getParkList)
//                .tag(HttpConstants.getParkList)
//                .params("parkspace_id", _id)
//                .params("citycode", city_code)
//                .execute(new JsonCallback<Base_Class_List_Info<Park_Info>>() {
//                    @Override
//                    public void onSuccess(final Base_Class_List_Info<Park_Info> responseData, Call call, Response response) {
//                        //请求成功
//                        park_issuccess = true;
//                        if (parkspace_issuccess) {
//                            if (mCustomDialog.isShowing()) {
//                                mCustomDialog.dismiss();
//                            }
//                        }
//                        textview_parkcount.setText(responseData.data.size()+"车位");
//                        mData = responseData.data;
//                    }
//
//                    @Override
//                    public void onError(Call call, Response response, Exception e) {
//                        super.onError(call, response, e);
//                        if (mCustomDialog.isShowing()) {
//                            mCustomDialog.dismiss();
//                        }
//                        if (!DensityUtil.isException(mContext, e)) {
//                            Log.d("TAG", "请求失败， 信息为：" + "getUserParkOrderForAppoint" + e.getMessage());
//                            int code = Integer.parseInt(e.getMessage());
//                            switch (code) {
//                                case 102:
//                                    textview_parkcount.setText("暂无车位");
//                                    break;
//                                case 901:
//                                    MyToast.showToast(mContext, "服务器正在维护中", 5);
//                                    break;
//                            }
//                        }
//                    }
//                });
//    }
//
//    private void requestGetUserParkOrderForAppoint() {
//        OkGo.post(HttpConstants.getUserParkOrderForAppoint)
//                .tag(HttpConstants.getUserParkOrderForAppoint)
//                .params("user_id", UserManager.getInstance().getUserInfo().getId())
//                .execute(new JsonCallback<Base_Class_List_Info<ParkOrderInfo>>() {
//                    @Override
//                    public void onSuccess(final Base_Class_List_Info<ParkOrderInfo> responseData, Call call, Response response) {
//                        if (mCustomDialog != null) {
//                            if (mCustomDialog.isShowing()) {
//                                mCustomDialog.dismiss();
//                            }
//                        }
//                        mOrderList = new ArrayList<>();
//                        mOrderList = responseData.data;
//                    }
//
//                    @Override
//                    public void onError(Call call, Response response, Exception e) {
//                        super.onError(call, response, e);
//                        if (mCustomDialog != null) {
//                            if (mCustomDialog.isShowing()) {
//                                mCustomDialog.dismiss();
//                            }
//                        }
//                        if (!DensityUtil.isException(mContext, e)) {
//                            Log.d("TAG", "请求失败， 信息为：" + "getUserParkOrderForAppoint" + e.getMessage());
//                            int code = Integer.parseInt(e.getMessage());
//                            switch (code) {
//                                case 101:
//                                    //还没有订单
//                                    mOrderList = new ArrayList<>();
//                                    break;
//                                case 102:
//                                    mOrderList = null;
//                                    break;
//                                case 901:
//                                    mOrderList = null;
//                                    MyToast.showToast(mContext, "服务器正在维护中", 5);
//                                    break;
//                            }
//                        }else {
//                            mOrderList = null;
//                        }
//                    }
//                });
//    }
//
//    private void initViewData(ParkLotInfo parkspace_info) {
//        try {
//            String img_Url[] = parkspace_info.getParkspace_img().split(",");
//            if (img_Url.length > 0) {
//                Glide.with(mContext)
//                        .load(HttpConstants.ROOT_IMG_URL_PS + img_Url[0])
//                        .placeholder(R.mipmap.ic_img)
//                        .error(R.mipmap.ic_img)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .crossFade()
//                        .into(imageview_show);
//            }
//
//            textview_parksapce.setText(parkspace_info.getPark_space_name());
//            textview_psaddress.setText(parkspace_info.getPark_address());
//            DateUtil.DistanceAndDanwei distanceAndDanwei = dateUtil.isMoreThan1000((int) AMapUtils.calculateLineDistance(new LatLng(parkspace_info.getLatitude(), parkspace_info.getLongitude()), new LatLng(LocationManager.getInstance().getmAmapLocation().getLatitude(), LocationManager.getInstance().getmAmapLocation().getLongitude())));
//            textview_distance.setText(distanceAndDanwei.getDistance());
//            textview_distance_dw.setText(distanceAndDanwei.getDanwei());
//            String start = parkspace_info.getHigh_time().substring(0, parkspace_info.getHigh_time().indexOf(" - ")),
//                    end = parkspace_info.getHigh_time().substring(parkspace_info.getHigh_time().indexOf(" - ") + 3, parkspace_info.getHigh_time().length()),
//                    hightime1 = parkspace_info.getHigh_time().substring(0, parkspace_info.getHigh_time().indexOf(" - ")),
//                    hightime2 = parkspace_info.getHigh_time().substring(parkspace_info.getHigh_time().indexOf(" - ") + 3, parkspace_info.getHigh_time().length());
//            if (Integer.parseInt(start.substring(0, start.indexOf(":"))) < Integer.parseInt(end.substring(0, end.lastIndexOf(":")))) {
//                textview_hightime.setText(hightime1 + " - " + hightime2);
//                textview_lowtime.setText(hightime2 + " - 次日" + hightime1);
//            } else {
//                textview_hightime.setText(hightime1 + " - 次日" + hightime2);
//                textview_lowtime.setText("次日" + hightime2 + " - 次日" + hightime1);
//            }
//            textview_highfee.setText(parkspace_info.getHigh_fee());
//            textview_highmaxtime.setText(parkspace_info.getHigh_max_fee().equals("-1") ? "—" : parkspace_info.getHigh_max_fee());
//            textview_lowfee.setText(parkspace_info.getLow_fee());
//            textview_lowmaxfee.setText(parkspace_info.getLow_max_fee().equals("-1") ? "—" : parkspace_info.getLow_max_fee());
//
//            Glide.with(mContext)
//                    .load(HttpConstants.ROOT_IMG_URL_PS + parkspace_info.getAd_img())
//                    .placeholder(R.mipmap.ic_imge)
//                    .error(R.mipmap.ic_imge)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .crossFade()
//                    .into(imageview_ad);
//        }catch (Exception e){}
//    }
//
//    private void initLoading(String what) {
//        mCustomDialog = new LoadingDialog(mContext, what);
//        mCustomDialog.show();
//    }
//
//    public void login() {
//        loginDialogFragment = new LoginDialogFragment();
//        loginDialogFragment.show(((BaseActivity) mContext).getSupportFragmentManager(), "hahah");
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mCustomDialog != null) {
//            mCustomDialog.cancel();
//        }
//    }
}
