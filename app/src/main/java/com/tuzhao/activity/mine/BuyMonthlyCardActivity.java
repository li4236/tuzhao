package com.tuzhao.activity.mine;

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
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.MonthlyCard;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DataUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/7/10.
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

    private TextView mThirdIndicate;

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
        mThirdIndicate = findViewById(R.id.third_indicate);

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
        for (int i = 0; i < mLastChooseArea.length; i++) {
            mLastChooseArea[i] = -1;
        }

        initOriginMonthlyCardPrice();
        getOpenAreaMonthlyCard(false, false);
        ImageUtil.showPicWithNoAnimate(mAreaCardPark, R.drawable.ic_blacklogo);
        ImageUtil.showPicWithNoAnimate(mAreaCardIv, R.drawable.ic_graycitycard_shadow);
        ImageUtil.showPicWithNoAnimate(mNationalCardPark, R.drawable.ic_graylogo);
        ImageUtil.showPicWithNoAnimate(mNationalCardIv, R.drawable.ic_grayallcity_shadow);
        mFirstIndicate.setText(DataUtil.getFirstTwoTransparentSpannable("为方便经常停车的用户，途找推出了月卡功能，若停车次数较多，购买月卡停车较为划算。"));
        mSecondIndicate.setText(DataUtil.getFirstTwoTransparentSpannable("由于各地区停车费用各不相同，所以月卡采取分地区制，请选择自己常在停车地区购买月卡。"));
        mThirdIndicate.setText(DataUtil.getFirstTwoTransparentSpannable("为方便多地停车经常出差用户，推出全国月卡，全国月卡在各地都能使用，收费采取统一收费。"));

        /*if (LocationManager.getInstance().hasLocation()) {
            //如果已经定位成功的则显示当前的城市
            mChooseCityCode = LocationManager.getInstance().getmAmapLocation().getCityCode();
            setCurrenCityMonthlyCard();
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
                    //显示之前的城市月卡
                    if (mChooseCityCode.equals("0000")) {
                        setLastChooseCityMonthlyCard();
                    } else {
                        showPickerView(true);
                    }
                }
                break;
            case R.id.national_monthly_card_iv:
                if (mNationalPosition != -1) {
                    //显示全国月卡
                    mChooseCityCode = "0000";
                    setCurrenCityMonthlyCard();
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
                                        setCurrenCityMonthlyCard();
                                    }
                                }
                                //setCurrenCityMonthlyCard();
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

    private void showPickerView(boolean show) {
        if (mMonthlyCards != null && mProvinces == null) {
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
                        mChooseCityCode = mMonthlyCards.get(options1).getCitys().get(option2).getCityCode();
                        mAdapter.setNewData(mMonthlyCards.get(options1).getCitys().get(option2).getCityMonthlyCards());
                        mLastChooseArea[0] = options1;
                        mLastChooseArea[1] = option2;
                        setCurrentChooseCard(true);

                        String city = mMonthlyCards.get(options1).getCitys().get(option2).getCity().replace("市", "");
                        mChooseCardTv.setText("当前选择：地区月卡（" + city + "）");
                        mMonthlyCardArea.setText(city + "卡");
                    }
                }
            });
        }

        if (mPickerView != null && show) {
            mPickerView.show();
        }

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
                            setCurrenCityMonthlyCard();
                        }
                    }
                }
            });
        }
    }*/

    private void setCurrenCityMonthlyCard() {
        if (mMonthlyCards != null) {
            if (mChooseCityCode.equals("0000")) {
                MonthlyCard.City city = mMonthlyCards.get(mNationalPosition).getCitys().get(0);
                mAdapter.setNewData(city.getCityMonthlyCards());
                setCurrentChooseCard(false);
                mChooseCardTv.setText("当前选择：全国月卡");
            } else {
                for (int i = 0, size = mMonthlyCards.size(); i < size; i++) {
                    for (int j = 0, citySize = mMonthlyCards.get(i).getCitys().size(); j < citySize; j++) {
                        if (mChooseCityCode.equals(mMonthlyCards.get(i).getCitys().get(j).getCityCode())) {
                            mAdapter.setNewData(mMonthlyCards.get(i).getCitys().get(j).getCityMonthlyCards());
                            String cityName = mMonthlyCards.get(i).getCitys().get(j).getCity();
                            mLastChooseArea[0] = i;
                            mLastChooseArea[1] = j;
                            setCurrentChooseCard(true);
                            cityName = cityName.replace("市", "");
                            mChooseCardTv.setText("当前选择：地区月卡（" + cityName + "）");
                            mMonthlyCardArea.setText(cityName + "卡");
                            break;
                        }
                    }
                }
            }
        }
    }

    private void setLastChooseCityMonthlyCard() {
        MonthlyCard.City city = mMonthlyCards.get(mLastChooseArea[0]).getCitys().get(mLastChooseArea[1]);
        mChooseCityCode = city.getCityCode();
        mAdapter.setNewData(city.getCityMonthlyCards());
        String cityName = city.getCity().replace("市", "");
        setCurrentChooseCard(true);
        mChooseCardTv.setText("当前选择：地区月卡（" + cityName + "）");
        mMonthlyCardArea.setText(cityName + "卡");
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
                    .setBackground(R.id.monthly_card_price_cl, canChoose ? R.drawable.stroke_y3_all_5dp : R.drawable.stroke_g10_all_5dp)
                    .itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (canChoose) {
                        Bundle bundle = new Bundle();
                        bundle.putString(ConstansUtil.PAY_TYPE, "2");
                        bundle.putString(ConstansUtil.PAY_MONEY, monthlyCardPrice.getPrice() + "元");
                        bundle.putString(ConstansUtil.CITY_CODE, mChooseCityCode);
                        bundle.putString(ConstansUtil.ALLOTED_PERIOD, monthlyCardPrice.getAllotedPeriod());
                        startActivity(PayActivity.class, bundle);
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
