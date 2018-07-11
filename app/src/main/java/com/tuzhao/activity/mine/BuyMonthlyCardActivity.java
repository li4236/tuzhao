package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.tianzhili.www.myselfsdk.pickerview.OptionsPickerView;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.MonthlyCard;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.LocationManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DataUtil;
import com.tuzhao.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/7/10.
 */
public class BuyMonthlyCardActivity extends BaseStatusActivity implements View.OnClickListener {

    private ImageView mAreaCardIv;

    private TextView mMonthlyCardArea;

    private ImageView mNationalCardIv;

    private TextView mChooseCardTv;

    private RecyclerView mRecyclerView;

    private CardPriceAdapter mAdapter;

    private TextView mFirstIndicate;

    private TextView mSecondIndicate;

    private TextView mThirdIndicate;

    private List<MonthlyCard> mMonthlyCards;

    private ArrayList<String> mProvinces;

    private OptionsPickerView<String> mPickerView;

    private AMapLocationClient mAMapLocationClient;

    private String mChooseCityCode = "-1";

    private int[] mLastChooseArea = new int[2];

    private int mNationalPosition = -1;

    @Override
    protected int resourceId() {
        return R.layout.activity_buy_monthly_card_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mAreaCardIv = findViewById(R.id.area_monthly_card_iv);
        mMonthlyCardArea = findViewById(R.id.monthly_card_area);
        mNationalCardIv = findViewById(R.id.national_monthly_card_iv);
        mChooseCardTv = findViewById(R.id.current_choose_monthly_card);
        mRecyclerView = findViewById(R.id.monthly_card_price_rv);
        mFirstIndicate = findViewById(R.id.first_indicate);
        mSecondIndicate = findViewById(R.id.second_indicate);
        mThirdIndicate = findViewById(R.id.third_indicate);

        mAdapter = new CardPriceAdapter(mRecyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(mAdapter);

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

        ImageUtil.showPic(mAreaCardIv, R.drawable.ic_grayvip);
        ImageUtil.showPic(mNationalCardIv, R.drawable.ic_grayallcity);
        mFirstIndicate.setText(DataUtil.getFirstTwoTransparentSpannable("为方便经常停车的用户，途找推出了月卡功能，若停车次数较多，购买月卡停车较为划算。"));
        mSecondIndicate.setText(DataUtil.getFirstTwoTransparentSpannable("由于各地区停车费用各不相同，所以月卡采取分地区制，请选择自己常在停车地区购买月卡。"));
        mThirdIndicate.setText(DataUtil.getFirstTwoTransparentSpannable("为方便多地停车经常出差用户，推出全国月卡，全国月卡在各地都能使用，收费采取统一收费。"));

        if (LocationManager.getInstance().hasLocation()) {
            mChooseCityCode = LocationManager.getInstance().getmAmapLocation().getCityCode();
            setCurrenCityMonthlyCard();
        } else {
            initLocation();
            mAMapLocationClient.startLocation();
        }

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
                    getOpenAreaMonthlyCard(true, false);
                } else if (mLastChooseArea[0] == -1) {
                    showPickerView(true);
                } else {
                    setLastChooseCityMonthlyCard();
                }
                break;
            case R.id.national_monthly_card_iv:
                if (mNationalPosition != -1) {
                    setCurrenCityMonthlyCard();
                } else {
                    getOpenAreaMonthlyCard(true, true);
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
        if (mAMapLocationClient != null) {
            mAMapLocationClient.onDestroy();
        }
    }

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
                                if (mChooseCityCode.equals("-1") && !seleteNationalCard) {
                                    mChooseCityCode = mMonthlyCards.get(0).getCitys().get(0).getCityCode();
                                } else if (seleteNationalCard) {
                                    mNationalPosition = mMonthlyCards.size() - 1;
                                    mChooseCityCode = mMonthlyCards.get(mNationalPosition).getCitys().get(0).getCityCode();
                                }
                                setCurrenCityMonthlyCard();
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
                provinceWithCity = new ArrayList<>();
                for (int j = 0, citySize = mMonthlyCards.get(i).getCitys().size(); j < citySize; j++) {
                    provinceWithCity.add(mMonthlyCards.get(i).getCitys().get(j).getCity());
                }
                citys.add(provinceWithCity);
            }

            mPickerView = new OptionsPickerView<>(this);
            mPickerView.setCyclic(false);
            mPickerView.setTextSize(16);
            mPickerView.setTitle("月卡地区");
            mPickerView.setPicker(mProvinces, citys, null, true);
            mPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3) {
                    mChooseCityCode = mMonthlyCards.get(options1).getCitys().get(option2).getCityCode();
                    mAdapter.setNewData(mMonthlyCards.get(options1).getCitys().get(option2).getMonthlyCardPrices());
                    mLastChooseArea[0] = options1;
                    mLastChooseArea[1] = option2;
                    setCurrentChooseCard(true);

                    String city = mMonthlyCards.get(options1).getCitys().get(option2).getCity().replace("市", "");
                    mChooseCardTv.setText("当前选择：地区月卡（" + city + "）");
                    mMonthlyCardArea.setText(city + "卡");
                }
            });
        }

        if (mPickerView != null && show) {
            mPickerView.show();
        }

    }

    private void initLocation() {
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
    }

    private void setCurrenCityMonthlyCard() {
        if (mMonthlyCards != null) {
            if (mChooseCityCode.equals("0000")) {
                MonthlyCard.City city = mMonthlyCards.get(mNationalPosition).getCitys().get(0);
                mChooseCityCode = city.getCityCode();
                mAdapter.setNewData(city.getMonthlyCardPrices());
                setCurrentChooseCard(false);
                mChooseCardTv.setText("当前选择：全国月卡");
            } else {
                for (int i = 0, size = mMonthlyCards.size(); i < size; i++) {
                    for (int j = 0, citySize = mMonthlyCards.get(i).getCitys().size(); j < citySize; j++) {
                        if (mChooseCityCode.equals(mMonthlyCards.get(i).getCitys().get(j).getCityCode())) {
                            mAdapter.setNewData(mMonthlyCards.get(i).getCitys().get(j).getMonthlyCardPrices());
                            String cityName = mMonthlyCards.get(i).getCitys().get(j).getCity();
                            if (cityName.contains("全国")) {
                                mChooseCardTv.setText("当前选择：全国月卡");
                                setCurrentChooseCard(false);
                            } else {
                                mLastChooseArea[0] = i;
                                mLastChooseArea[1] = j;
                                setCurrentChooseCard(true);
                                cityName = cityName.replace("市", "");
                                mChooseCardTv.setText("当前选择：地区月卡（" + cityName + "）");
                                mMonthlyCardArea.setText(cityName + "卡");
                            }
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
        mAdapter.setNewData(city.getMonthlyCardPrices());
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
            ImageUtil.showPic(mAreaCardIv, R.drawable.ic_vip);
            ImageUtil.showPic(mNationalCardIv, R.drawable.ic_grayallcity);
        } else {
            ImageUtil.showPic(mAreaCardIv, R.drawable.ic_grayvip);
            ImageUtil.showPic(mNationalCardIv, R.drawable.ic_allcity);
        }
    }

    class CardPriceAdapter extends BaseAdapter<MonthlyCard.City.MonthlyCardPrice> {

        CardPriceAdapter(RecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        protected void conver(@NonNull BaseViewHolder holder, MonthlyCard.City.MonthlyCardPrice monthlyCardPrice, int position) {
            final boolean canChoose = mMonthlyCards != null && !mMonthlyCards.isEmpty();
            Log.e(TAG, "conver: " + monthlyCardPrice.getAllotedPeriod());
            holder.setText(R.id.validity_period, monthlyCardPrice.getAllotedPeriod() + "天")
                    .setText(R.id.monthly_card_price, monthlyCardPrice.getPrice() + "元")
                    .setTextColor(R.id.validity_period, canChoose ? ConstansUtil.Y3_COLOR : ConstansUtil.G10_COLOR)
                    .setTextColor(R.id.monthly_card_price, canChoose ? ConstansUtil.Y3_COLOR : ConstansUtil.G10_COLOR)
                    .setBackground(R.id.monthly_card_price_cl, canChoose ? R.drawable.stroke_y3_all_5dp : R.drawable.stroke_g10_all_5dp)
                    .itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (canChoose) {

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
