package com.tuzhao.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.pickerview.OptionsPickerView;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.mine.MyCarActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Car;
import com.tuzhao.info.LongRentInfo;
import com.tuzhao.info.ParkLotInfo;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.TimeManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DataUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/11/9.
 */
public class LongRentActivity extends BaseStatusActivity implements View.OnClickListener, IntentObserver {

    private TextView mCarNumber;

    private TextView mAppointmentIncomeTime;

    private ConstraintLayout mDailyRentCl;

    private TextView mDailyRentDay;

    private TextView mDailyRentDiscount;

    private TextView mDailyRentFinallyMoney;

    private TextView mDailyRentNormalMoney;

    private LongRentAdapter mAdapter;

    private TextView mBookNow;

    private LongRentInfo mDailyRentInfo;

    private ArrayList<Car> mCars;

    private ParkLotInfo mParkLotInfo;

    private List<Park_Info> mParkInfos;

    private List<CanParkList> mCanParkList;

    private Calendar mNowCalendar;

    private long mNowStartTime;

    private ArrayList<String> mDays;

    private ArrayList<ArrayList<String>> mHours;

    private ArrayList<ArrayList<ArrayList<String>>> mMinutes;

    private OptionsPickerView<String> mStartTimeOption;

    private ArrayList<String> mDailyRentDays;

    private OptionsPickerView<String> mDailyRentOption;

    //预约开始和结束的停车时间
    private String mAppointmentStartTime = "";

    private int mChooseRentDay = -1;

    private boolean mHaveCanParkSpace;

    private DecimalFormat mDecimalFormat;

    private OrderInfo mOrderInfo;

    @Override
    protected int resourceId() {
        return R.layout.activity_long_rent_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mCarNumber = findViewById(R.id.car_number);
        mAppointmentIncomeTime = findViewById(R.id.appointment_income_time);
        mDailyRentCl = findViewById(R.id.daily_rent_cl);
        mDailyRentDay = findViewById(R.id.daily_rent_day);
        mDailyRentDiscount = findViewById(R.id.daily_rent_discount);
        mDailyRentFinallyMoney = findViewById(R.id.daily_rent_finally_money);
        mDailyRentNormalMoney = findViewById(R.id.daily_rent_normal_money);
        mBookNow = findViewById(R.id.book_now);

        RecyclerView recyclerView = findViewById(R.id.long_rent_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        mAdapter = new LongRentAdapter();
        recyclerView.setAdapter(mAdapter);

        TextView longRentHint = findViewById(R.id.long_rent_hint);
        String hint = "温馨提示：\n在租用期间车位使用权归你所有\n如果你不是一直使用也可以在空闲时间再次出租出去哦";
        longRentHint.setText(hint);

        findViewById(R.id.car_number_tv).setOnClickListener(this);
        findViewById(R.id.appointment_income_time_tv).setOnClickListener(this);
        mDailyRentCl.setOnClickListener(this);
        mBookNow.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mParkLotInfo = getIntent().getParcelableExtra(ConstansUtil.PARK_LOT_INFO);
        mParkInfos = getIntent().getParcelableArrayListExtra("park_list");
        if (mParkLotInfo == null || mParkInfos == null) {
            finish();
        }

        initLongRentInfo();
        initDailyRentOption();
        mDecimalFormat = new DecimalFormat("0.00");
        IntentObserable.registerObserver(this);
    }

    @NonNull
    @Override
    protected String title() {
        return "长期租用";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.car_number_tv:
                //选择车辆
                Intent intent = new Intent(LongRentActivity.this, MyCarActivity.class);
                intent.putExtra(ConstansUtil.INTENT_MESSAGE, true);
                if (mCars != null) {
                    intent.putExtra(ConstansUtil.CAR_NUMBER, mCars);
                }
                startActivityForResult(intent, ConstansUtil.REQUSET_CODE);
                break;
            case R.id.appointment_income_time_tv:
                showAppointmentIncomeTimeOptions();
                break;
            case R.id.daily_rent_cl:
                mDailyRentOption.show();
                break;
            case R.id.book_now:
                if (isEmpty(mCarNumber)) {
                    showFiveToast("请选择车牌号码哦");
                } else if (isEmpty(mAppointmentIncomeTime)) {
                    showFiveToast("请选择入场时间哦");
                } else if (mChooseRentDay == -1) {
                    showFiveToast("请选择要租用的时长哦");
                } else if (!mHaveCanParkSpace) {
                    showFiveToast("选择的条件内没有可停的车位哎");
                } else {
                    if (mOrderInfo == null || !mOrderInfo.isSameOrder()) {
                        addLongRentOrder();
                    } else {
                        startPayOrder();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstansUtil.REQUSET_CODE && resultCode == RESULT_OK && data != null) {
            //选择车辆的结果
            if (mCars == null) {
                mCars = data.getParcelableArrayListExtra(ConstansUtil.CAR_NUMBER);
            }
            String carNumber = data.getStringExtra(ConstansUtil.INTENT_MESSAGE);
            mCarNumber.setText(carNumber);
            performScanParkSpace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
    }

    /**
     * 弹出选择入场时间的选择器
     */
    private void showAppointmentIncomeTimeOptions() {
        if (mStartTimeOption == null) {
            mDays = new ArrayList<>(7);
            mHours = new ArrayList<>(24);
            mMinutes = new ArrayList<>(60);

            mNowCalendar = DateUtil.getYearToSecondCalendar(TimeManager.getInstance().getServerTime());
            mNowStartTime = SystemClock.elapsedRealtime();  //会有上次请求时间到现在时间距离的误差

            ArrayList<String> hours;
            ArrayList<ArrayList<String>> hourWithMinute;
            ArrayList<String> minute;

            mDays.add("今天");
            hours = new ArrayList<>();
            hourWithMinute = new ArrayList<>();

            //添加现在的时分,15:22
            minute = new ArrayList<>();
            hours.add(String.valueOf(mNowCalendar.get(Calendar.HOUR_OF_DAY)));
            for (int j = mNowCalendar.get(Calendar.MINUTE) + 1; j < 60; j++) {
                minute.add(String.valueOf(j));
            }
            hourWithMinute.add(minute);

            //添加往后一小时到23点的时分（即16点到23点）
            for (int i = mNowCalendar.get(Calendar.HOUR_OF_DAY) + 1; i < 24; i++) {
                hours.add(String.valueOf(i));
                minute = new ArrayList<>();
                for (int j = 0; j < 60; j++) {
                    minute.add(String.valueOf(j));
                }
                hourWithMinute.add(minute);
            }

            mHours.add(hours);
            mMinutes.add(hourWithMinute);

            mDays.add("明天");
            mDays.add("后天");
            mNowCalendar.add(Calendar.DAY_OF_MONTH, 3);  //今天，明天，后天
            for (int i = 0; i < 2; i++) {
                addhourWithMinutes();
            }

            //添加往后几天的数据
            for (int i = 0, size = 7 - mDays.size(); i < size; i++) {
                mDays.add((mNowCalendar.get(Calendar.MONTH) + 1) + "月" + mNowCalendar.get(Calendar.DAY_OF_MONTH) + "日");
                addhourWithMinutes();
                mNowCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            mNowCalendar = DateUtil.getYearToSecondCalendar(TimeManager.getInstance().getServerTime());
            initTimeOption();
        }
        mStartTimeOption.show();
    }

    /**
     * 为时间选择器添加小时和分钟
     */
    private void addhourWithMinutes() {
        ArrayList<String> hour = new ArrayList<>(24);
        ArrayList<ArrayList<String>> hourWithMinute = new ArrayList<>(24);
        ArrayList<String> minutes;
        for (int i = 0; i < 24; i++) {
            hour.add(String.valueOf(i));
            minutes = new ArrayList<>(60);
            for (int j = 0; j < 60; j++) {
                minutes.add(String.valueOf(j));
            }
            hourWithMinute.add(minutes);
        }
        mHours.add(hour);
        mMinutes.add(hourWithMinute);
    }

    private void initTimeOption() {
        mStartTimeOption = new OptionsPickerView<>(this);
        mStartTimeOption.setPicker(mDays, mHours, mMinutes, true);
        mStartTimeOption.setLabels(null, "点", "分");
        mStartTimeOption.setCyclic(false);
        mStartTimeOption.setTextSize(18);
        mStartTimeOption.setSelectOptions(0, 0, 0);
        mStartTimeOption.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是三个级别的选中位置
                String tx = mDays.get(options1) + " " + DateUtil.thanTen(mHours.get(options1).get(option2)) + " 点 " + DateUtil.thanTen(mMinutes.get(options1).get(option2).get(options3)) + " 分";

                //记录选中的开始入场时间
                Calendar appointmentStartCalendar = (Calendar) mNowCalendar.clone();
                appointmentStartCalendar.add(Calendar.DAY_OF_MONTH, options1);
                appointmentStartCalendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(mHours.get(options1).get(option2)));
                appointmentStartCalendar.set(Calendar.MINUTE, Integer.valueOf(mMinutes.get(options1).get(option2).get(options3)));
                mAppointmentStartTime = DateUtil.getCurrentYearToMinutes(appointmentStartCalendar.getTimeInMillis());

                Calendar nowCalendar = (Calendar) mNowCalendar.clone();
                nowCalendar.add(Calendar.MILLISECOND, (int) (SystemClock.elapsedRealtime() - mNowStartTime));

                if (appointmentStartCalendar.compareTo(nowCalendar) >= 0) {
                    //入场时间比现在的时间晚则显示选择的入场时间
                    if (!tx.equals(getText(mAppointmentIncomeTime))) {
                        //重置结束时间
                        for (CanParkList canParkList : mCanParkList) {
                            canParkList.reset();
                        }
                    }
                    mAppointmentIncomeTime.setText(tx);
                    if (mChooseRentDay != -1) {
                        performScanParkSpace();
                    }
                } else {
                    //如果入场时间比现在的时间早则是无效时间
                    mAppointmentStartTime = "";
                    mAppointmentIncomeTime.setText("");
                    showFiveToast("请选择有效时间哦");
                }
            }
        });
    }

    private void initLongRentInfo() {
        ArrayList<LongRentInfo> longRentInfos = mParkLotInfo.getLongRentInfos();
        mDailyRentInfo = longRentInfos.get(0);
        longRentInfos.remove(0);
        mAdapter.setNewArrayData(longRentInfos);

        mCanParkList = new ArrayList<>(14 + longRentInfos.size());
        for (int i = 1, size = 14 + longRentInfos.size() + 1; i < size; i++) {
            if (i <= 14) {
                mCanParkList.add(new CanParkList(1));
            } else {
                mCanParkList.add(new CanParkList(longRentInfos.get(i - 15).getRentDay()));
            }
        }
    }

    private void initDailyRentOption() {
        if (mDailyRentDays == null) {
            mDailyRentDays = new ArrayList<>(14);
            for (int i = 1; i <= 14; i++) {
                mDailyRentDays.add(String.valueOf(i));
            }

            mDailyRentOption = new OptionsPickerView<>(this);
            mDailyRentOption.setPicker(mDailyRentDays);
            mDailyRentOption.setTitle("选择日租时间");
            mDailyRentOption.setCyclic(false);
            mDailyRentOption.setTextSize(18);
            mDailyRentOption.setLabels("天");
            mDailyRentOption.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onOptionsSelect(int options1, int option2, int options3) {
                    int day = Integer.valueOf(mDailyRentDays.get(options1));
                    if (mChooseRentDay != day) {
                        mDailyRentDay.setText("日租" + mDailyRentDays.get(options1) + "天");
                        mDailyRentCl.setBackgroundResource(R.drawable.stroke_y3_corner_7dp);
                        if (mDailyRentInfo.getDiscount() != 1) {
                            mDailyRentDiscount.setText(DateUtil.deleteZero(mDailyRentInfo.getDiscount() * 10) + "折");
                            mDailyRentFinallyMoney.setText("¥" + mDecimalFormat.format(
                                    mDailyRentInfo.getNormalPrice() * mDailyRentInfo.getDiscount() * day));
                            mDailyRentNormalMoney.setText(ConstansUtil.YUAN + mDecimalFormat.format(mDailyRentInfo.getNormalPrice() * day));
                            if (!isVisible(mDailyRentDiscount)) {
                                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mDailyRentFinallyMoney.getLayoutParams();
                                layoutParams.topMargin = dpToPx(4);
                                mDailyRentNormalMoney.setPadding(0, 0, 0, dpToPx(4));
                                mDailyRentNormalMoney.setVisibility(View.VISIBLE);
                                mDailyRentNormalMoney.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                            }
                            showView(mDailyRentDiscount);
                        } else {
                            mDailyRentFinallyMoney.setText(ConstansUtil.YUAN + mDecimalFormat.format(mDailyRentInfo.getNormalPrice() * day));
                        }

                        if (mChooseRentDay > Integer.valueOf(mDailyRentDays.get(mDailyRentDays.size() - 1))) {
                            notifyAdapterNoChoose();
                        }
                        mChooseRentDay = day;
                        performScanParkSpace();
                    }
                }
            });
        }
    }

    /**
     * 让长租的选择取消掉
     */
    private void notifyAdapterNoChoose() {
        for (int i = 0; i < mAdapter.getDataSize(); i++) {
            if (mAdapter.get(i).getRentDay() == mChooseRentDay) {
                mAdapter.get(i).setChoose(false);
                mAdapter.notifyDataChange(i, 1);
                break;
            }
        }
    }

    private void performScanParkSpace() {
        if (!isEmpty(mCarNumber) && !isEmpty(mAppointmentIncomeTime)) {
            for (int i = 0; i < mCanParkList.size(); i++) {
                if (mCanParkList.get(i).rentDay == mChooseRentDay) {
                    if (mCanParkList.get(i).haveScan) {
                        //之前已经筛选过车位了
                        if (mCanParkList.get(i).parkInfos.isEmpty()) {
                            //选择的时间内没有车位
                            setBookNow(false);
                        } else {
                            setBookNow(true);
                        }
                    } else {
                        scanParkSpace(i);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 找出可停的车位
     */
    private void scanParkSpace(int position) {
        DataUtil.findCanLongParkList(mCanParkList.get(position).parkInfos, mParkInfos,
                mAppointmentStartTime, mCanParkList.get(position).getAppointmentEndTime());
        mCanParkList.get(position).haveScan = true;
        if (mCanParkList.get(position).parkInfos.isEmpty()) {
            setBookNow(false);
        } else {
            setBookNow(true);
        }
    }

    private void setBookNow(boolean haveCanParkSpace) {
        if (haveCanParkSpace) {
            mBookNow.setTextColor(ConstansUtil.B1_COLOR);
            mBookNow.setBackgroundResource(R.drawable.normal_y6_to_y15_press_y7_to_y16_corner_5dp);
        } else {
            mBookNow.setTextColor(Color.WHITE);
            mBookNow.setBackgroundResource(R.drawable.solid_g5_corner_5dp);
        }
        mHaveCanParkSpace = haveCanParkSpace;
    }

    private void addLongRentOrder() {
        showLoadingDialog();
        for (int i = 0; i < mCanParkList.size(); i++) {
            if (mCanParkList.get(i).rentDay == mChooseRentDay) {
                final CanParkList canParkList = mCanParkList.get(i);
                if (!canParkList.haveSort) {
                    Collections.sort(canParkList.parkInfos, new Comparator<Park_Info>() {
                        @Override
                        public int compare(Park_Info o1, Park_Info o2) {
                            return Integer.valueOf(o1.getIndicator()) - Integer.valueOf(o2.getIndicator());
                        }
                    });
                    canParkList.haveSort = true;
                }

                final String orderPrice = getOrderPrice();
                getOkGo(HttpConstants.addLongRentOrder)
                        .params("parkSpaceId", canParkList.parkInfos.get(0).getId())
                        .params("carNumber", getText(mCarNumber))
                        .params("parkInterval", mAppointmentStartTime + "*" + canParkList.getAppointmentEndTime())
                        .params("alternateParkSpaceId", canParkList.parkInfos.size() > 1 ? canParkList.parkInfos.get(1).getId() : "-1")
                        .params("price", getOrderPrice())
                        .params("cityCode", mParkLotInfo.getCity_code())
                        .execute(new JsonCallback<Base_Class_Info<String>>() {
                            @Override
                            public void onSuccess(Base_Class_Info<String> o, Call call, Response response) {
                                if (mOrderInfo == null) {
                                    mOrderInfo = new OrderInfo();
                                }
                                mOrderInfo.rentDay = mChooseRentDay;
                                mOrderInfo.carNumber = getText(mCarNumber);
                                mOrderInfo.appointmentStartTime = mAppointmentStartTime;
                                mOrderInfo.price = orderPrice;
                                mOrderInfo.expireTime = SystemClock.elapsedRealtime() + ConstansUtil.MAX_KEEP_LONG_RENT_ORDER_MINUTE * 60 * 1000;
                                mOrderInfo.orderNumber = o.data;
                                startPayOrder();
                                dismmisLoadingDialog();
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                if (!handleException(e)) {
                                    switch (e.getMessage()) {
                                        case "106":
                                            showFiveToast("该车牌号码已经被删除啦");
                                            for (int j = 0; j < mCars.size(); j++) {
                                                if (mCars.get(j).getCarNumber().equals(getText(mCarNumber))) {
                                                    mCars.remove(j);
                                                    break;
                                                }
                                            }
                                            break;
                                        case "109":
                                            showFiveToast("租用所需的价格已改变，正在为你刷新数据");
                                            break;
                                        case "110":
                                            canParkList.parkInfos.remove(0);
                                            if (!canParkList.parkInfos.isEmpty()) {
                                                //删除备选车位
                                                canParkList.parkInfos.remove(0);
                                            }
                                            if (canParkList.parkInfos.isEmpty()) {
                                                setBookNow(false);
                                                showFiveToast("刚刚的车位已经被别人抢走了哎,换个时间试试吧");
                                            } else {
                                                showFiveToast("刚刚的车位已经被别人抢走了哎，这次手快一点哦");
                                            }
                                            break;
                                        default:
                                            showFiveToast(ConstansUtil.SERVER_ERROR);
                                            break;
                                    }
                                }
                            }
                        });
                break;
            }
        }
    }

    private String getOrderPrice() {
        String result = "0";
        if (mChooseRentDay <= Integer.valueOf(mDailyRentDays.get(mDailyRentDays.size() - 1))) {
            result = mDecimalFormat.format(mChooseRentDay * mDailyRentInfo.getNormalPrice() * mDailyRentInfo.getDiscount());
        } else {
            for (int i = 0; i < mAdapter.getDataSize(); i++) {
                if (mChooseRentDay == mAdapter.get(i).getRentDay()) {
                    result = mDecimalFormat.format(mAdapter.get(i).getNormalPrice() * mAdapter.get(i).getDiscount());
                    break;
                }
            }
        }
        return result;
    }

    private void startPayOrder() {
        startActivity(PayActivity.class, ConstansUtil.PAY_TYPE, "3", ConstansUtil.PAY_MONEY, mOrderInfo.price,
                ConstansUtil.ORDER_NUMBER, mOrderInfo.orderNumber, ConstansUtil.TIME, String.valueOf(mOrderInfo.expireTime));
    }

    private void getParkLotData() {
        showLoadingDialog();
        getOkGo(HttpConstants.getOneParkSpaceData)
                .params("citycode", mParkLotInfo.getCity_code())
                .params("parkspace_id", mParkLotInfo.getId())
                .params("ad_position", 2)
                .execute(new JsonCallback<Base_Class_Info<ParkLotInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkLotInfo> o, Call call, Response response) {
                        mParkLotInfo = o.data;
                        initLongRentInfo();
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onReceive(Intent intent) {
        if (ConstansUtil.PAY_SUCCESS.equals(intent.getAction())) {
            finish();
        } else if (ConstansUtil.LONG_RENT_AGAIN.equals(intent.getAction())) {
            getParkLotData();
        } else if (ConstansUtil.RESET_LONG_RENT_ORDER.equals(intent.getAction())) {
            mOrderInfo = null;
            performScanParkSpace();
        }
    }

    private class LongRentAdapter extends BaseAdapter<LongRentInfo> {

        @Override
        protected void conver(@NonNull BaseViewHolder holder, final LongRentInfo longRentInfo, final int position) {
            holder.setText(R.id.rent_day, "租用" + longRentInfo.getRentDay() + "天")
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mChooseRentDay != longRentInfo.getRentDay()) {
                                if (mChooseRentDay < get(0).getRentDay()) {
                                    mDailyRentCl.setBackgroundResource(R.drawable.stroke_g10_corner_7dp);
                                } else {
                                    notifyAdapterNoChoose();
                                }
                                longRentInfo.setChoose(true);
                                mChooseRentDay = longRentInfo.getRentDay();
                                notifyDataChange(position, 1);
                                performScanParkSpace();
                            }
                        }
                    });

            if (longRentInfo.getDiscount() != 1) {
                holder.showView(R.id.rent_normal_money)
                        .showView(R.id.rent_discount)
                        .setText(R.id.rent_discount, DateUtil.deleteZero(longRentInfo.getDiscount() * 10) + "折")
                        .setText(R.id.rent_normal_money, "¥" + longRentInfo.getNormalPrice())
                        .setText(R.id.rent_finally_money, "¥" +
                                mDecimalFormat.format(longRentInfo.getNormalPrice() * longRentInfo.getDiscount()))
                        .setMarginTop(R.id.rent_finally_money, dpToPx(4))
                        .setPaddingBottom(R.id.rent_normal_money, dpToPx(4))
                        .setTextCenterLine(R.id.rent_normal_money);

            } else {
                holder.setText(R.id.rent_finally_money, "¥" + longRentInfo.getNormalPrice());
            }
        }

        @Override
        protected void conver(@NonNull BaseViewHolder holder, LongRentInfo longRentInfo, int position, @NonNull List<Object> payloads) {
            super.conver(holder, longRentInfo, position, payloads);
            holder.setBackground(longRentInfo.isChoose() ? R.drawable.stroke_y3_corner_7dp : R.drawable.stroke_g10_corner_7dp);
        }

        @Override
        protected int itemViewId() {
            return R.layout.item_long_rent_layout;
        }
    }

    /**
     * 记录对应天数的车位信息
     */
    private class CanParkList {

        private int rentDay;

        private boolean haveScan;

        private String appointmentEndTime;

        List<Park_Info> parkInfos;

        private boolean haveSort;

        CanParkList(int rentDay) {
            this.rentDay = rentDay;
            this.parkInfos = new LinkedList<>();
        }

        String getAppointmentEndTime() {
            if (appointmentEndTime == null) {
                appointmentEndTime = DateUtil.getYearToMinute(mAppointmentStartTime + ":00", rentDay * 24 * 60 * 60);
            }
            return appointmentEndTime;
        }

        void reset() {
            haveScan = false;
            appointmentEndTime = null;
            parkInfos.clear();
            haveSort = false;
        }

    }

    private class OrderInfo {

        private String orderNumber;

        private int rentDay = -2;

        private String carNumber = "";

        private String appointmentStartTime;

        private String price;

        /**
         * 订单的过期时间
         */
        private long expireTime;

        /**
         * 如果之前下单了，但是还没付款又返回了，再次点击预定如果是同样的参数并且订单还没过时就不需要再次下单
         */
        private boolean isSameOrder() {
            return rentDay == mChooseRentDay && carNumber.equals(getText(mCarNumber)) &&
                    mAppointmentStartTime.equals(appointmentStartTime) && SystemClock.elapsedRealtime() < expireTime - 2000;  //防止刚跳到支付页面又马上被关闭了
        }

    }

}
