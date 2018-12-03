package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.pickerview.OptionsPickerView;
import com.tuzhao.R;
import com.tuzhao.activity.PayActivity;
import com.tuzhao.adapter.BaseAdapter;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.adapter.BaseViewHolder;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.MonthlyCard;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;
import com.tuzhao.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/7/10.
 * <p>
 * 购买月卡
 * </p>
 */
public class BuyMonthlyCardActivity extends BaseStatusActivity implements View.OnClickListener, IntentObserver {

    private ImageView mAreaCardPark;

    private ImageView mAreaCardIv;

    private TextView mMonthlyCardArea;

    private ImageView mNationalCardPark;

    private ImageView mNationalCardIv;

    private TextView mChooseCardTv;

    private CardPriceAdapter mAdapter;

    private TextView mFirstIndicate;

    private TextView mSecondIndicate;

    private List<MonthlyCard> mMonthlyCards;

    private ArrayList<String> mProvinces;

    private OptionsPickerView<String> mPickerView;

    //private AMapLocationClient mAMapLocationClient;

    /**
     * 选择的城市码，定位成功后如果请求到数据则自动选择当前城市，如果是手动选择城市的则记录选择的是哪个城市，用于支付请求参数
     */
    private String mChooseCityCode = "-1";

    /**
     * 用于记录上一个选择的城市对应mMonthlyCards的哪个位置，当点了全国再点城市时可以快速获取到数据
     */
    private int[] mLastChooseArea = new int[2];

    /**
     * 记录全国对应的mMonthlyCards的位置
     */
    private int mNationalPosition = -1;

    private String mCityCode;

    @Override
    protected int resourceId() {
        return R.layout.activity_buy_monthly_card_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mAreaCardPark = findViewById(R.id.area_monthly_card_park);
        mAreaCardIv = findViewById(R.id.area_monthly_card_iv);
        mMonthlyCardArea = findViewById(R.id.area_monthly_card);
        mNationalCardPark = findViewById(R.id.national_monthly_card_park);
        mNationalCardIv = findViewById(R.id.national_monthly_card_iv);
        mChooseCardTv = findViewById(R.id.current_choose_monthly_card);
        RecyclerView recyclerView = findViewById(R.id.monthly_card_price_rv);
        mFirstIndicate = findViewById(R.id.first_indicate);
        mSecondIndicate = findViewById(R.id.second_indicate);

        mAdapter = new CardPriceAdapter(recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(mAdapter);

        mAreaCardIv.setOnClickListener(this);
        mNationalCardIv.setOnClickListener(this);
        mChooseCardTv.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        //订单跳转过来的，则默认选择对应的城市月卡
        mCityCode = getIntent().getStringExtra(ConstansUtil.CITY_CODE);

        for (int i = 0; i < mLastChooseArea.length; i++) {
            mLastChooseArea[i] = -1;
        }

        initOriginMonthlyCardPrice();
        getOpenAreaMonthlyCard(false, false);
        ImageUtil.showPicWithNoAnimate(mAreaCardPark, R.drawable.ic_blacklogo);
        ImageUtil.showPicWithNoAnimate(mAreaCardIv, R.drawable.ic_graycitycard_shadow);
        ImageUtil.showPicWithNoAnimate(mNationalCardPark, R.drawable.ic_graylogo);
        ImageUtil.showPicWithNoAnimate(mNationalCardIv, R.drawable.ic_grayallcity_shadow);
        mFirstIndicate.setText(ViewUtil.getFirstTwoTransparentSpannable("拥有月卡的用户，每次费用结算均享受对应折扣优惠，如有优惠券，优先减去券额再按月卡优惠折算。"));
        mSecondIndicate.setText(ViewUtil.getFirstTwoTransparentSpannable("各地区停车费用均有差异，地区卡只能在选定的一个城市使用，全国卡则全国通用。"));

        /*if (LocationManager.getInstance().hasLocation()) {
            //如果已经定位成功的则显示当前的城市
            mChooseCityCode = LocationManager.getInstance().getmAmapLocation().getCityCode();
            setCurrenNationalMonthlyCard();
        } else {
            //没有定位成功则开始定位
            initLocation();
            mAMapLocationClient.startLocation();
        }*/

        IntentObserable.registerObserver(this);
    }

    @NonNull
    @Override
    protected String title() {
        return "购买月卡";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.area_monthly_card_iv:
                if (mMonthlyCards == null) {
                    //还没有请求到数据的就开始请求
                    getOpenAreaMonthlyCard(true, false);
                } else if (mLastChooseArea[0] == -1) {
                    //如果之前没有定位成功的则让用户自己选城市
                    showPickerView(true);
                } else {
                    if (mChooseCityCode.equals("0000")) {
                        //由全国月卡再点击地区月卡，则显示之前的地区月卡
                        setLastChooseCityMonthlyCard();
                    } else {
                        //显示地区月卡选择器
                        showPickerView(true);
                    }
                }
                break;
            case R.id.national_monthly_card_iv:
                if (mNationalPosition != -1) {
                    //显示全国月卡
                    mChooseCityCode = "0000";
                    setCurrenNationalMonthlyCard();
                } else {
                    if (mMonthlyCards == null) {
                        getOpenAreaMonthlyCard(false, true);
                    } else {
                        showFiveToast("全国月卡暂未开放，敬请期待!");
                    }
                }
                break;
            case R.id.current_choose_monthly_card:
                if (mMonthlyCards == null) {
                    getOpenAreaMonthlyCard(true, false);
                } else {
                    showPickerView(true);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mPickerView != null && mPickerView.isShowing()) {
            mPickerView.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if (mAMapLocationClient != null) {
            mAMapLocationClient.onDestroy();
        }*/
        IntentObserable.unregisterObserver(this);
    }

    /**
     * 为了让一开始进入界面价格那里不为空，显示一些假数据
     */
    private void initOriginMonthlyCardPrice() {
        ArrayList<MonthlyCard.City.MonthlyCardPrice> list = new ArrayList<>();
        MonthlyCard.City.MonthlyCardPrice monthlyCardPrice = new MonthlyCard.City.MonthlyCardPrice();
        monthlyCardPrice.setAllotedPeriod("30");
        monthlyCardPrice.setPrice("80");
        list.add(monthlyCardPrice);

        monthlyCardPrice = new MonthlyCard.City.MonthlyCardPrice();
        monthlyCardPrice.setAllotedPeriod("66");
        monthlyCardPrice.setPrice("120");
        list.add(monthlyCardPrice);

        monthlyCardPrice = new MonthlyCard.City.MonthlyCardPrice();
        monthlyCardPrice.setAllotedPeriod("100");
        monthlyCardPrice.setPrice("180");
        list.add(monthlyCardPrice);

        monthlyCardPrice = new MonthlyCard.City.MonthlyCardPrice();
        monthlyCardPrice.setAllotedPeriod("192");
        monthlyCardPrice.setPrice("460");
        list.add(monthlyCardPrice);

        monthlyCardPrice = new MonthlyCard.City.MonthlyCardPrice();
        monthlyCardPrice.setAllotedPeriod("286");
        monthlyCardPrice.setPrice("680");
        list.add(monthlyCardPrice);

        monthlyCardPrice = new MonthlyCard.City.MonthlyCardPrice();
        monthlyCardPrice.setAllotedPeriod("370");
        monthlyCardPrice.setPrice("900");
        list.add(monthlyCardPrice);
        mAdapter.setNewData(list);
    }

    /**
     * 请求开放地区的月卡
     *
     * @param showPickerView     true(请求成功后弹出选择月卡地区的对话框)
     * @param seleteNationalCard true(请求成功后默认选择全国月卡)
     */
    private void getOpenAreaMonthlyCard(final boolean showPickerView, final boolean seleteNationalCard) {
        if (showPickerView) {
            showLoadingDialog();
        }
        getOkGo(HttpConstants.getOpenAreaMonthlyCard)
                .execute(new JsonCallback<Base_Class_List_Info<MonthlyCard>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<MonthlyCard> o, Call call, Response response) {
                        mMonthlyCards = o.data;
                        if (!showPickerView) {
                            if (!mMonthlyCards.isEmpty()) {
                               /* if (mChooseCityCode.equals("-1") && !seleteNationalCard) {
                                    //如果没有定位成功则默认选择第一个城市
                                    mChooseCityCode = mMonthlyCards.get(0).getCitys().get(0).getCityCode();
                                } else*/
                                if (seleteNationalCard) {
                                    if (!mMonthlyCards.get(mMonthlyCards.size() - 1).getCitys().get(0).getCityCode().equals("0000")) {
                                        showFiveToast("全国月卡暂未开放，敬请期待!");
                                    } else {
                                        //全国月卡的位置为最后一个
                                        mNationalPosition = mMonthlyCards.size() - 1;
                                        mChooseCityCode = "0000";
                                        setCurrenNationalMonthlyCard();
                                    }
                                } else if (mCityCode != null) {
                                    //订单跳转过来的，则默认选择订单所在城市的月卡
                                    for (int i = 0; i < mMonthlyCards.size(); i++) {
                                        for (int j = 0; j < mMonthlyCards.get(i).getCitys().size(); j++) {
                                            if (mCityCode.equals(mMonthlyCards.get(i).getCitys().get(j).getCityCode())) {
                                                setCurrentChooseCardPosition(i, j);
                                                break;
                                            }
                                        }
                                    }
                                }
                                //setCurrenNationalMonthlyCard();
                            }
                        }
                        showPickerView(showPickerView);
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            if (showPickerView) {
                                showFiveToast("获取月卡失败，请稍后重试");
                            }
                        }
                    }
                });
    }

    /**
     * 显示省市地区月卡的选择器
     *
     * @param show true(显示地区月卡选择器)  false(只加载数据)
     */
    private void showPickerView(boolean show) {
        if (mMonthlyCards != null && mProvinces == null) {
            //保证只初始化一次数据
            mProvinces = new ArrayList<>();
            ArrayList<ArrayList<String>> citys = new ArrayList<>();

            ArrayList<String> provinceWithCity;
            for (int i = 0, size = mMonthlyCards.size(); i < size; i++) {
                if (mMonthlyCards.get(i).getProvince().contains("全国")) {
                    mNationalPosition = i;
                    continue;
                }
                mProvinces.add(mMonthlyCards.get(i).getProvince());

                provinceWithCity = new ArrayList<>();
                for (int j = 0, citySize = mMonthlyCards.get(i).getCitys().size(); j < citySize; j++) {
                    provinceWithCity.add(mMonthlyCards.get(i).getCitys().get(j).getCity());
                }
                citys.add(provinceWithCity);
            }

            mPickerView = new OptionsPickerView<>(this);
            mPickerView.setPicker(mProvinces, citys, null, true);
            mPickerView.setTextSize(16);
            mPickerView.setTitle("月卡地区");
            mPickerView.setCyclic(false);
            mPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3) {
                    if (mLastChooseArea[0] != options1 || mLastChooseArea[1] != option2 ||
                            !mChooseCityCode.equals(mMonthlyCards.get(options1).getCitys().get(option2).getCityCode())) {
                        //只有选择不一样的城市才刷新数据
                        setCurrentChooseCardPosition(options1, option2);
                    }
                }
            });
        }

        if (mPickerView != null && show) {
            mPickerView.show();
        }

    }

    /**
     * 记录当前选择的月卡位置并显示
     */
    private void setCurrentChooseCardPosition(int province, int city) {
        mChooseCityCode = mMonthlyCards.get(province).getCitys().get(city).getCityCode();
        mAdapter.setNewData(mMonthlyCards.get(province).getCitys().get(city).getCityMonthlyCards());
        mLastChooseArea[0] = province;
        mLastChooseArea[1] = city;
        setCurrentChooseCard(true);

        String cityName = mMonthlyCards.get(province).getCitys().get(city).getCity().replace("市", "");
        mChooseCardTv.setText("当前选择：地区月卡（" + cityName + "）");
        mMonthlyCardArea.setText(cityName + "卡");
        mFirstIndicate.setText(ViewUtil.getFirstTwoTransparentSpannable("拥有月卡的用户，每次费用结算均享受"
                + DateUtil.deleteZero(mMonthlyCards.get(province).getCitys().get(city).getDiscount() * 10) +
                "折优惠，如有优惠券，优先减去券额再按月卡优惠折算。"));
    }

/*    private void initLocation() {
        if (mAMapLocationClient == null) {
            mAMapLocationClient = new AMapLocationClient(this);
            AMapLocationClientOption aMapLocationClientOption = new AMapLocationClientOption();
            aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            aMapLocationClientOption.setGpsFirst(true);
            aMapLocationClientOption.setInterval(1000);
            aMapLocationClientOption.setNeedAddress(true);

            mAMapLocationClient.setLocationOption(aMapLocationClientOption);
            mAMapLocationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {
                    if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                        mAMapLocationClient.stopLocation();
                        LocationManager.getInstance().setmAmapLocation(aMapLocation);
                        if (mChooseCityCode.equals("-1")) {
                            mChooseCityCode = aMapLocation.getCityCode();
                            setCurrenNationalMonthlyCard();
                        }
                    }
                }
            });
        }
    }*/

    /**
     * 设置当前选择的全国月卡
     */
    private void setCurrenNationalMonthlyCard() {
        if (mMonthlyCards != null) {
            MonthlyCard.City city = mMonthlyCards.get(mNationalPosition).getCitys().get(0);
            mAdapter.setNewData(city.getCityMonthlyCards());
            setCurrentChooseCard(false);
            mChooseCardTv.setText("当前选择：全国月卡");

            mFirstIndicate.setText(ViewUtil.getFirstTwoTransparentSpannable("拥有月卡的用户，每次费用结算均享受"
                    + DateUtil.deleteZero(city.getDiscount() * 10) +
                    "折优惠，如有优惠券，优先减去券额再按月卡优惠折算。"));
        }
    }

    /**
     * 设置当前选择的月卡为上次选择的地区月卡
     */
    private void setLastChooseCityMonthlyCard() {
        MonthlyCard.City city = mMonthlyCards.get(mLastChooseArea[0]).getCitys().get(mLastChooseArea[1]);
        mChooseCityCode = city.getCityCode();
        mAdapter.setNewData(city.getCityMonthlyCards());
        String cityName = city.getCity().replace("市", "");
        setCurrentChooseCard(true);
        mChooseCardTv.setText("当前选择：地区月卡（" + cityName + "）");
        mMonthlyCardArea.setText(cityName + "卡");
        mFirstIndicate.setText(ViewUtil.getFirstTwoTransparentSpannable("拥有月卡的用户，每次费用结算均享受"
                + DateUtil.deleteZero(city.getDiscount() * 10) +
                "折优惠，如有优惠券，优先减去券额再按月卡优惠折算。"));
    }

    /**
     * @param isArea true（选择地区卡） false（选择全国卡）
     */
    private void setCurrentChooseCard(boolean isArea) {
        if (isArea) {
            ImageUtil.showPicWithNoAnimate(mAreaCardIv, R.drawable.ic_citycard_shadow);
            ImageUtil.showPicWithNoAnimate(mNationalCardPark, R.drawable.ic_graylogo);
            ImageUtil.showPicWithNoAnimate(mNationalCardIv, R.drawable.ic_grayallcity_shadow);
        } else {
            ImageUtil.showPicWithNoAnimate(mAreaCardIv, R.drawable.ic_graycitycard_shadow);
            ImageUtil.showPicWithNoAnimate(mNationalCardPark, R.drawable.ic_pinklogo);
            ImageUtil.showPicWithNoAnimate(mNationalCardIv, R.drawable.ic_allcity_shadow);
        }
    }

    @Override
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(ConstansUtil.PAY_SUCCESS)) {
                if (mCityCode != null) {
                    if (mCityCode.equals(mChooseCityCode) || "0000".equals(mChooseCityCode)) {
                        //如果是订单跳转过来则购买了对应的城市月卡或者全国月卡才传递折扣回去
                        Intent monthlyCardIntent = new Intent();
                        monthlyCardIntent.putExtra(ConstansUtil.INTENT_MESSAGE, mMonthlyCards.get(mLastChooseArea[0]).getCitys().get(mLastChooseArea[1]).getDiscount());
                        setResult(RESULT_OK, monthlyCardIntent);
                    }
                }
                finish();
            }
        }
    }

    class CardPriceAdapter extends BaseAdapter<MonthlyCard.City.MonthlyCardPrice> {

        CardPriceAdapter(RecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        protected void conver(@NonNull BaseViewHolder holder, final MonthlyCard.City.MonthlyCardPrice monthlyCardPrice, int position) {
            final boolean canChoose = mMonthlyCards != null && !mMonthlyCards.isEmpty();
            holder.setText(R.id.validity_period, monthlyCardPrice.getAllotedPeriod() + "天")
                    .setText(R.id.monthly_card_price, monthlyCardPrice.getPrice() + "元")
                    .setTextColor(R.id.validity_period, canChoose ? ConstansUtil.Y3_COLOR : ConstansUtil.G10_COLOR)
                    .setTextColor(R.id.monthly_card_price, canChoose ? ConstansUtil.Y3_COLOR : ConstansUtil.G10_COLOR)
                    .setBackground(R.id.monthly_card_price_cl, canChoose ? R.drawable.stroke_y3_corner_5dp : R.drawable.stroke_g10_corner_5dp)
                    .itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (canChoose) {
                        TipeDialog tipeDialog = new TipeDialog.Builder(BuyMonthlyCardActivity.this)
                                .setTitle("购买月卡")
                                .setMessage("购买" + monthlyCardPrice.getAllotedPeriod() + "天月卡，支付" + monthlyCardPrice.getPrice() + "元")
                                .setNegativeButton("取消", null)
                                .setPositiveButton("去支付", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString(ConstansUtil.PAY_TYPE, "2");
                                        bundle.putString(ConstansUtil.PAY_MONEY, monthlyCardPrice.getPrice() + "元");
                                        bundle.putString(ConstansUtil.CITY_CODE, mChooseCityCode);
                                        bundle.putString(ConstansUtil.ALLOTED_PERIOD, monthlyCardPrice.getAllotedPeriod());
                                        startActivity(PayActivity.class, bundle);
                                    }
                                }).create();
                        tipeDialog.show();
                    } else {
                        showFiveToast("请先选择要购买的月卡类型哦");
                    }
                }
            });
        }

        @Override
        protected int itemViewId() {
            return R.layout.item_monthly_card_price_layout;
        }

    }

}
