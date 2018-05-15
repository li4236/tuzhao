package com.tuzhao.fragment.parkspacedetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.NaviLatLng;
import com.cb.ratingbar.CBRatingBar;
import com.tianzhili.www.myselfsdk.banner.Banner;
import com.tianzhili.www.myselfsdk.banner.BannerConfig;
import com.tianzhili.www.myselfsdk.banner.listener.OnBannerListener;
import com.tianzhili.www.myselfsdk.banner.transformer.DefaultTransformer;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.BigPictureActivity;
import com.tuzhao.activity.OrderParkActivity;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.activity.navi.RouteNaviActivity;
import com.tuzhao.application.MyApplication;
import com.tuzhao.fragment.BaseFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.Park_Space_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.LocationManager;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.LoginDialogFragment;
import com.tuzhao.publicwidget.loader.GlideImageLoader;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TZL12 on 2017/11/8.
 */

public class ParkspaceDetailFragment extends BaseFragment {

    /**
     * UI
     */
    private View mContentView;
    private CustomDialog mCustomDialog;
    private Banner banner_image;
    private ImageView mNoPictureIv;
    private TextView textview_hightime, textview_highfee, textview_lowtime, textview_lowfee, textview_finewarm, textview_distance, textview_distance_dw, textview_parkspacename, textview_parkspaceaddress, textview_parkcount, textview_grade, textview_opentime;
    private ConstraintLayout linearlayout_goorder, linearlayout_daohang;
    private LoginDialogFragment loginDialogFragment;
    private CBRatingBar cbratingbar;

    /**
     * 页面相关
     */
    private String parkspace_id, city_code;
    private Park_Space_Info parkspace_info = null;
    private ArrayList<Park_Info> mData = null;
    private boolean parkspace_issuccess = false, park_issuccess = false;
    private ArrayList<String> imgData;
    private ArrayList<ParkOrderInfo> mOrderList = null;
    private DateUtil dateUtil = new DateUtil();

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mContentView = inflater.inflate(R.layout.fragment_parkspace_detail_layout, container, false);

        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件

        return mContentView;
    }

    private void initData() {

        parkspace_info = (Park_Space_Info) getArguments().getSerializable("parkspace_info");
        mData = (ArrayList<Park_Info>) getArguments().getSerializable("park_info");

        if (parkspace_info == null) {
            parkspace_id = getArguments().getString("parkspace_id");
            city_code = getArguments().getString("city_code");
            initLoading("加载中...");
            requestGetParkspaceData();
        } else {
            parkspace_issuccess = true;
            initViewData(parkspace_info);
        }

        if (mData == null) {
            parkspace_id = getArguments().getString("parkspace_id");
            city_code = getArguments().getString("city_code");
            if (mCustomDialog == null) {
                initLoading("加载中...");
            } else if (!mCustomDialog.isShowing()) {
                initLoading("加载中...");
            }
            requestGetParkListData();
        } else {
            park_issuccess = true;
            textview_parkcount.setText(mData.size() + "车位");
        }

        if (UserManager.getInstance().hasLogined()) {
            requestGetUserParkOrderForAppoint();
        }
    }

    private void initView() {
        banner_image = mContentView.findViewById(R.id.id_fragment_parkspacedetail_layout_banner_image);
        mNoPictureIv = mContentView.findViewById(R.id.parkspace_detail_no_picture);
        textview_hightime = mContentView.findViewById(R.id.id_fragment_parkspacedatali_layout_textview_hightime);
        textview_highfee = mContentView.findViewById(R.id.id_fragment_parkspacedatali_layout_textview_highfee);
        textview_opentime = mContentView.findViewById(R.id.id_fragment_parkspacedetail_layout_textview_opentime);
        textview_lowtime = mContentView.findViewById(R.id.id_fragment_parkspacedatali_layout_textview_lowtime);
        textview_lowfee = mContentView.findViewById(R.id.id_fragment_parkspacedatali_layout_textview_lowfee);
        textview_finewarm = mContentView.findViewById(R.id.id_fragment_parkspacedatali_layout_textview_finewarm);
        textview_parkspacename = mContentView.findViewById(R.id.id_fragment_parkspacedetail_layout_textview_parkspacename);
        textview_distance = mContentView.findViewById(R.id.id_fragment_parkspacedetail_layout_textview_distance);
        textview_distance_dw = mContentView.findViewById(R.id.id_fragment_parkspacedetail_layout_textview_distance_dw);
        textview_parkspaceaddress = mContentView.findViewById(R.id.id_fragment_parkspacedetail_layout_textview_parkspaceaddress);
        textview_parkcount = mContentView.findViewById(R.id.id_fragment_parkspacedetail_layout_textview_parkcount);
        textview_grade = mContentView.findViewById(R.id.id_fragment_parkspacedetail_layout_textview_grade);
        linearlayout_goorder = mContentView.findViewById(R.id.id_fragment_parkspacedetail_layout_linearlayout_goorder);
        linearlayout_daohang = mContentView.findViewById(R.id.id_fragment_parkspacedetail_layout_linearlayout_daohang);
        cbratingbar = mContentView.findViewById(R.id.id_fragment_parkspacedetail_layout_cbratingbar);

        //ImageUtil.showNoCenterPic(mNoPictureIv, R.mipmap.ic_img);

        banner_image.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
            }
        });
    }

    private void initEvent() {

        linearlayout_goorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserManager.getInstance().hasLogined()) {
                    if (mData == null || mData.size() <= 0) {
                        MyToast.showToast(mContext, "暂无停车位，稍后再来试试哦", 5);
                    } else {
                        if (mOrderList == null) {
                            initLoading("加载中...");
                            requestGetUserParkOrderForAppoint();
                        } else {
                            int aingcount = 0;
                            for (ParkOrderInfo parkOrderInfo : mOrderList) {
                                if (parkOrderInfo.getOrder_status().equals("3")) {
                                    MyToast.showToast(mContext, "您当前还有为付款订单", 5);
                                    return;
                                } else {
                                    if (parkOrderInfo.getOrder_status().equals("1") || parkOrderInfo.getOrder_status().equals("2")) {
                                        aingcount++;
                                    }
                                }
                            }
                            if (aingcount > 1) {
                                MyToast.showToast(mContext, "预定订单数量已达上限哦", 5);
                            } else {
                                Intent intent = new Intent(mContext, OrderParkActivity.class);
                                intent.putExtra("parkspace_info", parkspace_info);
                                intent.putExtra("park_list", mData);
                                intent.putExtra("order_list", mOrderList);
                                startActivity(intent);
                            }
                        }
                    }
                } else {
                    login();
                }
            }
        });

        linearlayout_daohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyApplication.getInstance(), RouteNaviActivity.class);
                intent.putExtra("gps", true);
                intent.putExtra("start", new NaviLatLng(LocationManager.getInstance().getmAmapLocation().getLatitude(), LocationManager.getInstance().getmAmapLocation().getLongitude()));
                intent.putExtra("end", new NaviLatLng(parkspace_info.getLatitude(), parkspace_info.getLongitude()));
                startActivity(intent);
            }
        });
    }

    private void requestGetParkspaceData() {
        OkGo.post(HttpConstants.getOneParkSpaceData)
                .tag(HttpConstants.getOneParkSpaceData)
                .params("parkspace_id", parkspace_id)
                .params("citycode", city_code)
                .params("ad_position", "2")
                .execute(new JsonCallback<Base_Class_Info<Park_Space_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Park_Space_Info> park_space_infoBase_class_info, Call call, Response response) {
                        parkspace_issuccess = true;
                        if (park_issuccess) {
                            if (mCustomDialog.isShowing()) {
                                mCustomDialog.dismiss();
                            }
                        }
                        parkspace_info = park_space_infoBase_class_info.data;
                        parkspace_info.setCity_code(city_code);
                        initViewData(parkspace_info);
                    }
                });
    }

    private void requestGetParkListData() {
        OkGo.post(HttpConstants.getParkList)//请求数据的接口地址
                .tag(HttpConstants.getParkList)
                .params("parkspace_id", parkspace_id)
                .params("citycode", city_code)
                .execute(new JsonCallback<Base_Class_List_Info<Park_Info>>() {
                    @Override
                    public void onSuccess(final Base_Class_List_Info<Park_Info> responseData, Call call, Response response) {
                        //请求成功
                        Log.e("TAG", "onSuccess: " + responseData.data);
                        park_issuccess = true;
                        if (parkspace_issuccess) {
                            if (mCustomDialog.isShowing()) {
                                mCustomDialog.dismiss();
                            }
                        }
                        textview_parkcount.setText(responseData.data.size() + "车位");
                        mData = responseData.data;
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        textview_parkcount.setText("暂无车位");
                        if (!DensityUtil.isException(mContext, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + e.getMessage());
                        }
                    }
                });
    }

    private void requestGetUserParkOrderForAppoint() {
        OkGo.post(HttpConstants.getUserParkOrderForAppoint)
                .tag(mContext)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .execute(new JsonCallback<Base_Class_List_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(final Base_Class_List_Info<ParkOrderInfo> responseData, Call call, Response response) {
                        if (mCustomDialog != null) {
                            if (mCustomDialog.isShowing()) {
                                mCustomDialog.dismiss();
                            }
                        }
                        if (mOrderList != null) {
                            mOrderList.clear();
                        }
                        mOrderList = responseData.data;
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mCustomDialog != null) {
                            if (mCustomDialog.isShowing()) {
                                mCustomDialog.dismiss();
                            }
                        }
                        if (!DensityUtil.isException(mContext, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + "getUserParkOrderForAppoint" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
                                    //还没有订单
                                    mOrderList = new ArrayList<>();
                                    break;
                                case 901:
                                    mOrderList = null;
                                    MyToast.showToast(mContext, "服务器正在维护中", 5);
                                    break;
                            }
                        } else {
                            mOrderList = null;
                        }
                    }
                });
    }

    private void initViewData(Park_Space_Info parkspace_info) {
        try {
            String imgUrl = parkspace_info.getParkspace_img();
            if (imgData == null) {
                imgData = new ArrayList<>();
            } else {
                imgData.clear();
            }

            if (imgUrl != null && !imgUrl.equals("-1") && !imgUrl.equals("")) {
                final String img_Url[] = imgUrl.split(",");
                for (int i = 0; i < img_Url.length; i++) {
                    imgData.add(HttpConstants.ROOT_IMG_URL_PS + img_Url[i]);
                }
                banner_image.setImages(imgData)
                        .setBannerStyle(BannerConfig.NUM_INDICATOR)
                        .setBannerAnimation(DefaultTransformer.class)
                        .setImageLoader(new GlideImageLoader())
                        .setDelayTime(6000)
                        .start();

                banner_image.setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        if (imgData.size() == 0) {
                            MyToast.showToast(mContext, "暂无图片哦", 5);
                        } else {
                            Intent intent = new Intent(mContext, BigPictureActivity.class);
                            intent.putStringArrayListExtra("picture_list", imgData);
                            intent.putExtra("position", position);
                            startActivity(intent);
                        }
                    }
                });

                mNoPictureIv.setVisibility(View.GONE);
            } else {
                Log.e("TAG", "initViewData: no_pic");
                imgData.add("place");
                banner_image.setImages(imgData)
                        .setBannerStyle(BannerConfig.NUM_INDICATOR)
                        .setBannerAnimation(DefaultTransformer.class)
                        .setImageLoader(new GlideImageLoader())
                        .setDelayTime(6000)
                        .start();
            }

            textview_parkspacename.setText(parkspace_info.getPark_space_name());
            textview_parkspaceaddress.setText(parkspace_info.getPark_address());
            DateUtil.DistanceAndDanwei distanceAndDanwei = dateUtil.isMoreThan1000((int) AMapUtils.calculateLineDistance(new LatLng(parkspace_info.getLatitude(), parkspace_info.getLongitude()), new LatLng(LocationManager.getInstance().getmAmapLocation().getLatitude(), LocationManager.getInstance().getmAmapLocation().getLongitude())));
            textview_distance.setText(distanceAndDanwei.getDistance());
            textview_distance_dw.setText(distanceAndDanwei.getDanwei());
            String start = parkspace_info.getHigh_time().substring(0, parkspace_info.getHigh_time().indexOf(" - ")),
                    end = parkspace_info.getHigh_time().substring(parkspace_info.getHigh_time().indexOf(" - ") + 3, parkspace_info.getHigh_time().length()),
                    hightime1 = parkspace_info.getHigh_time().substring(0, parkspace_info.getHigh_time().indexOf(" - ")),
                    hightime2 = parkspace_info.getHigh_time().substring(parkspace_info.getHigh_time().indexOf(" - ") + 3, parkspace_info.getHigh_time().length());
            if (Integer.parseInt(start.substring(0, start.indexOf(":"))) < Integer.parseInt(end.substring(0, end.lastIndexOf(":")))) {
                textview_hightime.setText(hightime1 + " - " + hightime2);
                textview_lowtime.setText(hightime2 + " - 次日" + hightime1);
            } else {
                textview_hightime.setText(hightime1 + " - 次日" + hightime2);
                textview_lowtime.setText("次日" + hightime2 + " - 次日" + hightime1);
            }
            textview_highfee.setText(parkspace_info.getHigh_fee());
            textview_lowfee.setText(parkspace_info.getLow_fee());
            textview_finewarm.setText("※ 停车超过顺延时间会按" + parkspace_info.getFine() + "元/小时收取额外超时费哦 ※");
            cbratingbar.setStarProgress(parkspace_info.getGrade() == null ? 80 : (Float.valueOf(parkspace_info.getGrade()) * 100 / 5));
            textview_grade.setText(parkspace_info.getGrade() + "分");
            textview_opentime.setText("(" + parkspace_info.getOpentime() + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login() {
        loginDialogFragment = new LoginDialogFragment();
        loginDialogFragment.show(((BaseActivity) mContext).getSupportFragmentManager(), "hahah");
    }

    private void initLoading(String what) {
        mCustomDialog = new CustomDialog(mContext, what);
        mCustomDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCustomDialog != null) {
            mCustomDialog.cancel();
        }
    }
}
