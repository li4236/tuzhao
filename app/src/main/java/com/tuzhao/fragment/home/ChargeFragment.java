package com.tuzhao.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.ChargestationDetailActivity;
import com.tuzhao.fragment.BaseFragment;
import com.tuzhao.info.NearPointPCInfo;

/**
 * Created by TZL12 on 2017/7/7.
 */

public class ChargeFragment extends BaseFragment {

    /**
     * UI
     */
    private View mContentView;
//    private LinearLayout linearlayout_chargestatedetalis,linearlayout_godaohang,linearlayout_datail,linearlayout_collection;
//    private ImageView imageview_show,imageview_ad,imageview_collection;
//    private TextView textview_chargename,textview_ispublic,textview_charaddress,textview_distance,textview_danwei;
//    private CBRatingBar cbratingbar;
//    private CustomDialog mCustomDialog;
//    private LoginDialogFragment loginDialogFragment;
    /**
     * 数据相关
     */
//    private ChargeStationInfo chargestation_info;
//    private String chargestation_id = null;
//    private String city_code = null;
//    private DateUtil dateUtil = new DateUtil();
    private NearPointPCInfo nearPointPCInfo = null;

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
        mContentView = inflater.inflate(R.layout.fragment_charge_layout, container, false);

        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件

        return mContentView;
    }

    private void initData() {
//        chargestation_id = getArguments().getString("belong_id");
//        city_code = getArguments().getString("city_code");
//        if (city_code == null){
//            city_code = LocationManager.getInstance().hasLocation()?LocationManager.getInstance().getmAmapLocation().getCityCode():"010";
//        }
//        if (chargestation_id != null) {
//            initLoading("加载中...");
//            requestGetChargeStationData(chargestation_id);
//        } else {
//            mContext.finish();
//        }
//        holder = (CollectionManager.getInstance().checkCollectionDatas(chargestation_id, "2"));
//        if (holder.isExist) {
//            imageview_collection.setImageDrawable(ContextCompat.getDrawable(mContext,R.mipmap.ic_scchenggong));
//        }
        nearPointPCInfo = (NearPointPCInfo) getArguments().getSerializable("cssinfo");
        ((TextView)mContentView.findViewById(R.id.id_fragment_charge_layout_textview_psname)).setText(nearPointPCInfo.getName());
        ((TextView)mContentView.findViewById(R.id.id_fragment_charge_layout_textview_psprice)).setText("均价"+nearPointPCInfo.getPrice()+"元/度");
    }

    private void initView() {
//        linearlayout_chargestatedetalis = (LinearLayout) mContentView.findViewById(R.id.id_fragment_charge_layout_linearlayout_chargestatedetalis);
//        linearlayout_datail = (LinearLayout) mContentView.findViewById(R.id.id_fragment_charge_layout_linearlayout_datail);
//        imageview_show = (ImageView) mContentView.findViewById(R.id.id_fragment_charge_layout_imageview_show);
//        imageview_collection = (ImageView) mContentView.findViewById(R.id.id_fragment_charge_layout_imageview_collection);
//        textview_chargename = (TextView) mContentView.findViewById(R.id.id_fragment_charge_layout_textview_chargename);
//        textview_ispublic = (TextView) mContentView.findViewById(R.id.id_fragment_charge_layout_textview_ispublic);
//        cbratingbar = (CBRatingBar) mContentView.findViewById(R.id.id_fragment_charge_layout_cbratingbar);
//        textview_charaddress = (TextView) mContentView.findViewById(R.id.id_fragment_charge_layout_textview_charaddress);
//        textview_distance = (TextView) mContentView.findViewById(R.id.id_fragment_charge_layout_textview_distance);
//        textview_danwei = (TextView) mContentView.findViewById(R.id.id_fragment_charge_layout_textview_danwei);
//        imageview_ad = (ImageView) mContentView.findViewById(R.id.id_fragment_charge_layout_imageview_ad);
//        linearlayout_godaohang = (LinearLayout) mContentView.findViewById(R.id.id_fragment_charge_layout_linearlayout_godaohang);
//        linearlayout_collection = (LinearLayout) mContentView.findViewById(R.id.id_fragment_charge_layout_linearlayout_collection);
    }

    private void initEvent() {
//        linearlayout_chargestatedetalis.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //跳转充电站详情页面
//                Intent intent = new Intent(mContext, ChargestationDetailActivity.class);
//                if (chargestation_info == null){
//                    intent.putExtra("chargestation_id",chargestation_id);
//                    intent.putExtra("city_code",city_code);
//                }else {
//                    intent.putExtra("chargestation_info",chargestation_info);
//                }
//                startActivity(intent);
//            }
//        });
//
//        linearlayout_datail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //跳转充电站详情页面
//                Intent intent = new Intent(mContext, ChargestationDetailActivity.class);
//                if (chargestation_info == null){
//                    intent.putExtra("chargestation_id",chargestation_id);
//                    intent.putExtra("city_code",city_code);
//                }else {
//                    intent.putExtra("chargestation_info",chargestation_info);
//                }
//                startActivity(intent);
//            }
//        });
//
//        imageview_ad.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //跳转广告页面
//            }
//        });
//
//        linearlayout_godaohang.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //跳转导航
//                Intent intent = new Intent(MyApplication.getInstance(), RouteNaviActivity.class);
//                intent.putExtra("gps", true);
//                intent.putExtra("start", new NaviLatLng(LocationManager.getInstance().getmAmapLocation().getLatitude(), LocationManager.getInstance().getmAmapLocation().getLongitude()));
//                intent.putExtra("end", new NaviLatLng(chargestation_info.getLatitude(), chargestation_info.getLongitude()));
//                startActivity(intent);
//            }
//        });
//
//        linearlayout_collection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (UserManager.getInstance().hasLogined()) {
//                    holder = (CollectionManager.getInstance().checkCollectionDatas(chargestation_id, "2"));
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
//                                        imageview_collection.setImageDrawable(ContextCompat.getDrawable(mContext,R.mipmap.ic_shoucang2));
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
//                                .params("belong_id", chargestation_id)
//                                .params("user_id", UserManager.getInstance().getUserInfo().getId())
//                                .params("type", "2")
//                                .params("citycode",city_code)
//                                .execute(new JsonCallback<Base_Class_Info<CollectionInfo>>() {
//                                    @Override
//                                    public void onSuccess(Base_Class_Info<CollectionInfo> collection_infoBase_class_list_info, Call call, Response response) {
//                                        if (mCustomDialog.isShowing()) {
//                                            mCustomDialog.dismiss();
//                                        }
//                                        List<CollectionInfo> collection_datas = CollectionManager.getInstance().getCollection_datas();
//                                        if (collection_datas == null){
//                                            collection_datas = new ArrayList<>();
//                                        }
//                                        collection_infoBase_class_list_info.data.setCitycode(city_code);
//                                        collection_datas.add(collection_infoBase_class_list_info.data);
//                                        CollectionManager.getInstance().setCollection_datas(collection_datas);
//                                        MyToast.showToast(mContext, "收藏成功", 5);
//                                        imageview_collection.setImageDrawable(ContextCompat.getDrawable(mContext,R.mipmap.ic_scchenggong));
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

        mContentView.findViewById(R.id.id_fragment_charge_layout_linearlayout_allclick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //啥也不用干，消耗点击事件
                //点击跳转详情页面
                Intent intent = new Intent(mContext, ChargestationDetailActivity.class);
                intent.putExtra("chargestation_id",nearPointPCInfo.getId());
                intent.putExtra("city_code",nearPointPCInfo.getCity_code());
                startActivity(intent);
            }
        });
    }

//    private void requestGetChargeStationData(String chargestation_id) {
//        OkGo.post(HttpConstants.getOneChargeStationData)
//                .tag(HttpConstants.getOneChargeStationData)
//                .params("chargestation_id",chargestation_id)
//                .params("citycode", city_code)
//                .params("ad_position","1")
//                .execute(new JsonCallback<Base_Class_Info<ChargeStationInfo>>() {
//                    @Override
//                    public void onSuccess(Base_Class_Info<ChargeStationInfo> chargestation_class_info, Call call, Response response) {
//                            if (mCustomDialog.isShowing()) {
//                                mCustomDialog.dismiss();
//                            }
//                        chargestation_info = chargestation_class_info.data;
//                        chargestation_info.setCity_code(city_code);
//                        try {
//                            initViewData(chargestation_info);
//                        }catch (Exception e){}
//                    }
//                });
//    }
//
//    private void initViewData(ChargeStationInfo chargestation_info) {
//        String img_Url[] = chargestation_info.getImg_url().split(",");
//        if (img_Url.length>0){
//            Glide.with(mContext)
//                    .load(HttpConstants.ROOT_IMG_URL_CS + img_Url[0])
//                    .placeholder(R.mipmap.ic_img)
//                    .error(R.mipmap.ic_img)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .crossFade()
//                    .into(imageview_show);
//        }
//        Glide.with(mContext)
//                .load(HttpConstants.ROOT_IMG_URL_CS + chargestation_info.getAd_img())
//                .placeholder(R.mipmap.ic_imge)
//                .error(R.mipmap.ic_imge)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .crossFade()
//                .into(imageview_ad);
//
//        textview_chargename.setText(chargestation_info.getCharge_station_name());
//        textview_ispublic.setText(chargestation_info.getIspublic().equals("1")?"公":"私");
//        textview_charaddress.setText(chargestation_info.getCharge_station_address());
//        DateUtil.DistanceAndDanwei distanceAndDanwei = dateUtil.isMoreThan1000((int) AMapUtils.calculateLineDistance(new LatLng(chargestation_info.getLatitude(), chargestation_info.getLongitude()), new LatLng(LocationManager.getInstance().getmAmapLocation().getLatitude(), LocationManager.getInstance().getmAmapLocation().getLongitude())));
//        textview_distance.setText(distanceAndDanwei.getDistance());
//        textview_danwei.setText(distanceAndDanwei.getDanwei());
//    }
//
//    public void login() {
//        loginDialogFragment = new LoginDialogFragment();
//        loginDialogFragment.show(((BaseActivity)mContext).getSupportFragmentManager(), "hahah");
//    }
//
//    private void initLoading(String what) {
//        mCustomDialog = new CustomDialog(mContext, what);
//        mCustomDialog.show();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mCustomDialog!= null){
//            mCustomDialog.cancel();
//        }
//    }
}
