package com.tuzhao.fragment.chargestationdetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.tuzhao.activity.navi.RouteNaviActivity;
import com.tuzhao.application.MyApplication;
import com.tuzhao.fragment.base.BaseFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ChargeStationInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.LocationManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.loader.GlideImageLoader;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DateUtil;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TZL12 on 2017/11/16.
 */

public class ChargeDetailFragment extends BaseFragment {

    /**
     * UI
     */
    private View mContentView;
    private CustomDialog mCustomDialog;
    private Banner banner_image;
    private TextView textview_chargestationname,textview_address,textview_distance,textview_distance_dw,textview_chargefee,textview_serverfee,textview_parkfee,textview_grade,textview_opentime;
    private CBRatingBar cbratingbar;
    private LinearLayout linearlayout_godaohang;

    private ChargeStationInfo chargestation_info = null;
    private String chargestation_id, city_code;
    private ArrayList<String> imgData;
    private DateUtil dateUtil = new DateUtil();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mContentView = inflater.inflate(R.layout.fragment_chargedetail_layout, container, false);

        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件

        return mContentView;
    }

    private void initView() {

        banner_image = (Banner) mContentView.findViewById(R.id.id_fragment_chargedetail_layout_banner_image);
        textview_chargestationname = (TextView) mContentView.findViewById(R.id.id_fragment_chargedetail_layout_textview_chargestationname);
        cbratingbar = (CBRatingBar) mContentView.findViewById(R.id.id_fragment_chargedetail_layout_cbratingbar);
        linearlayout_godaohang = (LinearLayout) mContentView.findViewById(R.id.id_fragment_chargedetail_layout_linearlayout_godaohang);
        textview_address = (TextView) mContentView.findViewById(R.id.id_fragment_chargedetail_layout_textview_address);
        textview_distance = (TextView) mContentView.findViewById(R.id.id_fragment_chargedetail_layout_textview_distance);
        textview_distance_dw = (TextView) mContentView.findViewById(R.id.id_fragment_chargedetail_layout_textview_distance_dw);
        textview_chargefee = (TextView) mContentView.findViewById(R.id.id_fragment_chargedatali_layout_textview_chargefee);
        textview_serverfee = (TextView) mContentView.findViewById(R.id.id_fragment_chargedatali_layout_textview_serverfee);
        textview_parkfee = (TextView) mContentView.findViewById(R.id.id_fragment_chargedatali_layout_textview_parkfee);
        textview_grade = (TextView) mContentView.findViewById(R.id.id_fragment_chargedetail_layout_textview_grade);
        textview_opentime = (TextView) mContentView.findViewById(R.id.id_fragment_chargedetail_layout_textview_opentime);
    }

    private void initData() {
        chargestation_info = (ChargeStationInfo) getArguments().getSerializable("chargestation_info");
        if (chargestation_info == null) {
            chargestation_id = getArguments().getString("chargestation_id");
            city_code = getArguments().getString("city_code");
            initLoading("加载中...");
            requestGetChargestationData();
        } else {
            initViewData(chargestation_info);
        }
    }

    private void requestGetChargestationData() {
        OkGo.post(HttpConstants.getOneChargeStationData)
                .tag(HttpConstants.getOneChargeStationData)
                .params("chargestation_id", chargestation_id)
                .params("citycode", city_code)
                .params("ad_position", "1")
                .execute(new JsonCallback<Base_Class_Info<ChargeStationInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ChargeStationInfo> park_space_infoBase_class_info, Call call, Response response) {
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        chargestation_info = park_space_infoBase_class_info.data;
                        chargestation_info.setCity_code(city_code);
                        initViewData(chargestation_info);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        if (e instanceof ConnectException) {
                            Log.d("TAG", "请求失败，" + " 信息为：连接异常" + e.toString());
                            MyToast.showToast(mContext, "网络异常", 2);
                        } else if (e instanceof SocketTimeoutException) {
                            Log.d("TAG", "请求失败，" + " 信息为：超时异常" + e.toString());
                            MyToast.showToast(mContext, "网络异常", 2);
                        } else if (e instanceof NoRouteToHostException) {
                            Log.d("TAG", "请求失败，" + " 信息为：没有路由到主机" + e.toString());
                            MyToast.showToast(mContext, "网络异常", 2);
                        } else {
                            Log.d("TAG", "请求失败， 信息为：" + e.getMessage());
                        }
                    }
                });
    }

    private void initViewData(ChargeStationInfo chargestation_info) {

        final String img_Url[] = chargestation_info.getImg_url().split(",");
        imgData = new ArrayList<>();
        if (img_Url.length > 0) {
            for (int i = 0; i < img_Url.length; i++) {
                imgData.add(HttpConstants.ROOT_IMG_URL_CS + img_Url[i]);
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
                    ArrayList<String> imgList = new ArrayList<>();
                    for (String aa : img_Url) {
                        imgList.add(HttpConstants.ROOT_IMG_URL_CS + aa);
                    }
                    if (imgList.size() == 0) {
                        MyToast.showToast(mContext, "暂无图片哦", 3);
                    } else {
                        Intent intent = new Intent(mContext, BigPictureActivity.class);
                        intent.putStringArrayListExtra("picture_list", imgList);
                        intent.putExtra("position", position);
                        startActivity(intent);
                    }
                }
            });
        } else {
            List<Integer> noImg = new ArrayList<>();
            noImg.add(R.mipmap.ic_img);
            banner_image.setImages(noImg)
                    .setImageLoader(new GlideImageLoader())
                    .start();
        }
        textview_chargestationname.setText(chargestation_info.getCharge_station_name());
        textview_address.setText(chargestation_info.getCharge_station_address());
        DateUtil.DistanceAndDanwei distanceAndDanwei = dateUtil.isMoreThan1000((int) AMapUtils.calculateLineDistance(new LatLng(chargestation_info.getLatitude(), chargestation_info.getLongitude()), new LatLng(LocationManager.getInstance().getmAmapLocation().getLatitude(), LocationManager.getInstance().getmAmapLocation().getLongitude())));
        textview_distance.setText(distanceAndDanwei.getDistance());
        textview_distance_dw.setText(distanceAndDanwei.getDanwei());
        textview_chargefee.setText(chargestation_info.getCharge_fee()+"元/度");
        textview_serverfee.setText(chargestation_info.getService_fee()+"元/度");
        textview_parkfee.setText(chargestation_info.getPark_fee()+"元/小时");
        cbratingbar.setStarProgress(chargestation_info.getGrade() == null?80:(new Float(chargestation_info.getGrade())*100/5));
        textview_grade.setText(chargestation_info.getGrade()+"分");
        textview_opentime.setText("营业时间（"+chargestation_info.getOpentime()+"）");
    }

    private void initEvent() {

        linearlayout_godaohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //导航
                Intent intent = new Intent(MyApplication.getInstance(), RouteNaviActivity.class);
                intent.putExtra("gps", true);
                intent.putExtra("start", new NaviLatLng(LocationManager.getInstance().getmAmapLocation().getLatitude(), LocationManager.getInstance().getmAmapLocation().getLongitude()));
                intent.putExtra("end", new NaviLatLng(chargestation_info.getLatitude(), chargestation_info.getLongitude()));
                startActivity(intent);
            }
        });
    }

    private void initLoading(String what) {
        mCustomDialog = new CustomDialog(mContext, what);
        mCustomDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCustomDialog!= null){
            mCustomDialog.cancel();
        }
    }
}
