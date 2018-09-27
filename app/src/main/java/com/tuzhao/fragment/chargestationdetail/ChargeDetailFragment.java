package com.tuzhao.fragment.chargestationdetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.tuzhao.activity.LoginActivity;
import com.tuzhao.activity.mine.NavigationActivity;
import com.tuzhao.fragment.base.BaseFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ChargeStationInfo;
import com.tuzhao.info.CollectionInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.LocationManager;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.loader.GlideImageLoader;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;

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

    private static final String TAG = "ChargeDetailFragment";

    /**
     * UI
     */
    private View mContentView;
    private LoadingDialog mLoadingDialog;
    private Banner banner_image;
    private TextView textview_chargestationname, textview_address, textview_distance, mChargeCollecTv,
            textview_chargefee, textview_serverfee, textview_parkfee, textview_grade, textview_opentime;
    private ImageView mChargeCollectIv;
    private CBRatingBar cbratingbar;
    private LinearLayout linearlayout_godaohang, mChargeCollectLl;

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
        banner_image = mContentView.findViewById(R.id.id_fragment_chargedetail_layout_banner_image);
        textview_chargestationname = mContentView.findViewById(R.id.id_fragment_chargedetail_layout_textview_chargestationname);
        cbratingbar = mContentView.findViewById(R.id.id_fragment_chargedetail_layout_cbratingbar);
        linearlayout_godaohang = mContentView.findViewById(R.id.id_fragment_chargedetail_layout_linearlayout_godaohang);
        textview_address = mContentView.findViewById(R.id.id_fragment_chargedetail_layout_textview_address);
        textview_distance = mContentView.findViewById(R.id.id_fragment_chargedetail_layout_textview_distance);
        textview_chargefee = mContentView.findViewById(R.id.id_fragment_chargedatali_layout_textview_chargefee);
        textview_serverfee = mContentView.findViewById(R.id.id_fragment_chargedatali_layout_textview_serverfee);
        textview_parkfee = mContentView.findViewById(R.id.id_fragment_chargedatali_layout_textview_parkfee);
        textview_grade = mContentView.findViewById(R.id.id_fragment_chargedetail_layout_textview_grade);
        textview_opentime = mContentView.findViewById(R.id.id_fragment_chargedetail_layout_textview_opentime);
        mChargeCollectLl = mContentView.findViewById(R.id.charge_collect_ll);
        mChargeCollectIv = mContentView.findViewById(R.id.charge_collect_iv);
        mChargeCollecTv = mContentView.findViewById(R.id.charge_collect_tv);
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
                .headers("token", UserManager.getInstance().getToken())
                .params("chargestation_id", chargestation_id)
                .params("citycode", city_code)
                .params("ad_position", "1")
                .execute(new JsonCallback<Base_Class_Info<ChargeStationInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ChargeStationInfo> park_space_infoBase_class_info, Call call, Response response) {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        chargestation_info = park_space_infoBase_class_info.data;
                        chargestation_info.setCity_code(city_code);
                        initViewData(chargestation_info);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
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
        if (chargestation_info.isCollection()) {
            mChargeCollectIv.setImageResource(R.drawable.ic_collect);
            mChargeCollecTv.setText("已收藏");
        }

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
                        if (!aa.equals("-1")) {
                            imgList.add(HttpConstants.ROOT_IMG_URL_CS + aa);
                        }
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
        SpannableString spannableString = new SpannableString(distanceAndDanwei.getDistance() + distanceAndDanwei.getDanwei());
        spannableString.setSpan(new AbsoluteSizeSpan((int) DensityUtil.sp2px(getContext(), 8)), distanceAndDanwei.getDistance().length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textview_distance.setText(spannableString);
        textview_chargefee.setText(chargestation_info.getCharge_fee() + "元/度");
        textview_serverfee.setText(chargestation_info.getService_fee() + "元/度");
        textview_parkfee.setText(chargestation_info.getPark_fee() + "元/小时");
        cbratingbar.setStarProgress(chargestation_info.getGrade() == null ? 80 : (new Float(chargestation_info.getGrade()) * 100 / 5));
        textview_grade.setText(chargestation_info.getGrade() + "分");
        textview_opentime.setText("营业时间（" + chargestation_info.getOpentime() + "）");
    }

    private void initEvent() {
        mChargeCollectLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chargestation_info != null) {
                    if (UserManager.getInstance().hasLogined()) {
                        if (chargestation_info.isCollection()) {
                            initLoading("正在取消收藏...");
                            OkGo.post(HttpConstants.deleteCollection)
                                    .tag(TAG)
                                    .addInterceptor(new TokenInterceptor())
                                    .headers("token", UserManager.getInstance().getUserInfo().getToken())
                                    .params("belong_id", chargestation_info.getId())
                                    .params("type", 2)
                                    .params("citycode", chargestation_info.getCity_code())
                                    .execute(new JsonCallback<Base_Class_Info<CollectionInfo>>() {
                                        @Override
                                        public void onSuccess(Base_Class_Info<CollectionInfo> collection_infoBase_class_info, Call call, Response response) {
                                            if (mLoadingDialog.isShowing()) {
                                                mLoadingDialog.dismiss();
                                            }
                                            chargestation_info.setIsCollection("0");
                                            mChargeCollectIv.setImageResource(R.drawable.ic_nocollect);
                                            mChargeCollecTv.setText("未收藏");
                                        }

                                        @Override
                                        public void onError(Call call, Response response, Exception e) {
                                            super.onError(call, response, e);
                                            if (mLoadingDialog.isShowing()) {
                                                mLoadingDialog.dismiss();
                                            }
                                            MyToast.showToast(getContext(), "取消失败", 5);
                                        }
                                    });
                        } else {
                            initLoading("正在添加收藏...");
                            OkGo.post(HttpConstants.addCollection)
                                    .tag(getContext())
                                    .addInterceptor(new TokenInterceptor())
                                    .headers("token", UserManager.getInstance().getUserInfo().getToken())
                                    .params("belong_id", chargestation_info == null ? chargestation_id : chargestation_info.getId())
                                    .params("type", "2")
                                    .params("citycode", chargestation_info == null ? city_code : chargestation_info.getCity_code())
                                    .execute(new JsonCallback<Base_Class_Info<CollectionInfo>>() {
                                        @Override
                                        public void onSuccess(Base_Class_Info<CollectionInfo> collection_infoBase_class_list_info, Call call, Response response) {
                                            if (mLoadingDialog.isShowing()) {
                                                mLoadingDialog.dismiss();
                                            }
                                            chargestation_info.setIsCollection("1");
                                            mChargeCollectIv.setImageResource(R.drawable.ic_collect);
                                            mChargeCollecTv.setText("已收藏");
                                        }

                                        @Override
                                        public void onError(Call call, Response response, Exception e) {
                                            super.onError(call, response, e);
                                            if (!DensityUtil.isException(getContext(), e)) {
                                                if ("102".equals(e.getMessage())) {
                                                    chargestation_info.setIsCollection("1");
                                                    mChargeCollectIv.setImageResource(R.drawable.ic_collect);
                                                    mChargeCollecTv.setText("已收藏");
                                                } else {
                                                    MyToast.showToast(getContext(), "收藏失败", 5);
                                                }
                                            } else {
                                                MyToast.showToast(getContext(), "收藏失败", 5);
                                            }
                                            if (mLoadingDialog.isShowing()) {
                                                mLoadingDialog.dismiss();
                                            }
                                        }
                                    });
                        }

                    } else {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.putExtra(ConstansUtil.INTENT_MESSAGE, true);
                        startActivity(intent);
                    }
                }
            }
        });

        linearlayout_godaohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //导航
                if (chargestation_info != null && LocationManager.getInstance().hasLocation()) {
                    Intent intent = new Intent(getActivity(), NavigationActivity.class);
                    intent.putExtra("gps", true);
                    intent.putExtra("start", new NaviLatLng(LocationManager.getInstance().getmAmapLocation().getLatitude(), LocationManager.getInstance().getmAmapLocation().getLongitude()));
                    intent.putExtra("end", new NaviLatLng(chargestation_info.getLatitude(), chargestation_info.getLongitude()));
                    intent.putExtra(ConstansUtil.PARK_LOT_NAME, chargestation_info.getCharge_station_name());
                    intent.putExtra("address", chargestation_info.getCharge_station_address());
                    startActivity(intent);
                }
            }
        });
    }

    private void initLoading(String what) {
        mLoadingDialog = new LoadingDialog(mContext, what);
        mLoadingDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
        OkGo.getInstance().cancelTag(TAG);
    }
}
