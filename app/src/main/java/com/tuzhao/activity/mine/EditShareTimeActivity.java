package com.tuzhao.activity.mine;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.pickerview.OptionsPickerView;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.EverydayShareTimeInfo;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.ShareTimeInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.customView.CheckTextView;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/3/27.
 */

public class EditShareTimeActivity extends BaseStatusActivity implements View.OnClickListener {

    private TextView mStartShareDate;

    private TextView mEndShareDate;

    private CheckTextView[] mCheckTextViews;

    private PauseShareDateAdapter mPauseShareDateAdapter;

    private ShareTimeAdapter mEverydayShareTimeAdapter;

    private ArrayList<String> mYears;

    private ArrayList<ArrayList<String>> mMonths;

    private ArrayList<ArrayList<ArrayList<String>>> mDays;

    private OptionsPickerView<String> mDateOption;

    private ArrayList<String> mHours;

    private ArrayList<ArrayList<String>> mMinutes;

    private OptionsPickerView<String> mTimeOption;

    private Park_Info mParkInfo;

    @Override
    protected int resourceId() {
        return R.layout.activity_modify_share_time;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if ((mParkInfo = getIntent().getParcelableExtra(ConstansUtil.PARK_SPACE_INFO)) == null) {
            showFiveToast("打开失败，请返回重试");
            finish();
        }

        mCheckTextViews = new CheckTextView[7];
        mStartShareDate = findViewById(R.id.modify_share_time_start_share_time);
        mEndShareDate = findViewById(R.id.modify_share_time_end_share_time);
        final CheckTextView mMondayShare = findViewById(R.id.modify_share_time_monday);
        final CheckTextView mTuesdayShare = findViewById(R.id.modify_share_time_tuesday);
        final CheckTextView mWednesdayShare = findViewById(R.id.modify_share_time_wednesday);
        final CheckTextView mThursdayShare = findViewById(R.id.modify_share_time_thursday);
        final CheckTextView mFridayShare = findViewById(R.id.modify_share_time_friday);
        final CheckTextView mSaturdayShare = findViewById(R.id.modify_share_time_saturday);
        final CheckTextView mSundayShare = findViewById(R.id.modify_share_time_sunday);
        TextView mAddPauseShareDate = findViewById(R.id.modify_share_time_add_pause_date);
        TextView mAddEverydayShareTime = findViewById(R.id.modify_share_time_add_everyday_share_time);
        RecyclerView pauseShareRecycerview = findViewById(R.id.modify_share_time_pause_date_rv);
        RecyclerView everydayShareRecycerview = findViewById(R.id.modify_share_time_everyday_share_ime_rv);

        mCheckTextViews[0] = mMondayShare;
        mCheckTextViews[1] = mTuesdayShare;
        mCheckTextViews[2] = mWednesdayShare;
        mCheckTextViews[3] = mThursdayShare;
        mCheckTextViews[4] = mFridayShare;
        mCheckTextViews[5] = mSaturdayShare;
        mCheckTextViews[6] = mSundayShare;

        pauseShareRecycerview.setNestedScrollingEnabled(false);
        everydayShareRecycerview.setNestedScrollingEnabled(false);

        pauseShareRecycerview.setLayoutManager(new LinearLayoutManager(this));
        everydayShareRecycerview.setLayoutManager(new LinearLayoutManager(this));
        mPauseShareDateAdapter = new PauseShareDateAdapter();
        mEverydayShareTimeAdapter = new ShareTimeAdapter();
        pauseShareRecycerview.setAdapter(mPauseShareDateAdapter);
        everydayShareRecycerview.setAdapter(mEverydayShareTimeAdapter);

        mStartShareDate.setOnClickListener(this);
        mEndShareDate.setOnClickListener(this);
        mAddPauseShareDate.setOnClickListener(this);
        mAddEverydayShareTime.setOnClickListener(this);
        findViewById(R.id.modify_share_time_submit).setOnClickListener(this);

        for (int i = 0; i < mCheckTextViews.length; i++) {
            final int finalI = i;
            mCheckTextViews[i].setOnCheckChangeListener(new CheckTextView.OnCheckChangeListener() {
                @Override
                public void onCheckChange(boolean isCheck) {
                    if (!isCheck) {
                        if (DateUtil.isInOrderDay(finalI + 1, getText(mStartShareDate), getText(mEndShareDate), mParkInfo.getOrder_times())) {
                            showFiveToast("星期" + DateUtil.getDayOfNumber(finalI + 1) + "已有人预定了，不可以取消哦");
                        } else {
                            mCheckTextViews[finalI].setChecked(false);
                        }
                    } else {
                        mCheckTextViews[finalI].setChecked(true);
                    }
                }
            });
        }

    }

    @Override
    protected void initData() {
        super.initData();
        //从车位设置跳转过来的则请求该车位的出租时间
        setOriginTime();
        initDateOption();
        initTimeOption();
        dismmisLoadingDialog();
    }

    @Override
    public void onBackPressed() {
        if (mDateOption.isShowing()) {
            mDateOption.dismiss();
        } else if (mTimeOption.isShowing()) {
            mTimeOption.forceDismiss();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 设置原来的出租时间
     */
    private void setOriginTime() {
        ShareTimeInfo shareTimeInfo = new ShareTimeInfo();
        shareTimeInfo.setShareDate(mParkInfo.getOpen_date());
        shareTimeInfo.setEveryDayShareTime(mParkInfo.getOpen_time());
        shareTimeInfo.setPauseShareDate(mParkInfo.getPauseShareDate());
        shareTimeInfo.setShareDay(mParkInfo.getShareDay());

        String[] shareDate = shareTimeInfo.getShareDate().split(" - ");
        mStartShareDate.setText(shareDate[0]);
        mEndShareDate.setText(shareDate[1]);

        String[] shareDay = shareTimeInfo.getShareDay().split(",");
        for (int i = 0; i < shareDay.length; i++) {
            if (shareDay[i].charAt(0) == '1') {
                mCheckTextViews[i].setChecked(true);
            }
        }

        if (shareTimeInfo.getPauseShareDate() != null && !shareTimeInfo.getPauseShareDate().equals("-1")) {
            String[] pauseShareDate = shareTimeInfo.getPauseShareDate().split(",");
            for (String date : pauseShareDate) {
                mPauseShareDateAdapter.addData(date);
            }
        }

        //显示每天出租时段
        if (!shareTimeInfo.getEveryDayShareTime().equals("-1")) {
            String[] everyDayShareTime = shareTimeInfo.getEveryDayShareTime().split(",");
            EverydayShareTimeInfo everydayShareTimeInfo;
            String startTime;
            String endTime;
            int position;
            for (String dayShareTime : everyDayShareTime) {
                everydayShareTimeInfo = new EverydayShareTimeInfo();
                position = dayShareTime.indexOf(" - ");
                startTime = "2018-04-24 " + dayShareTime.substring(0, position);
                endTime = "2018-04-24 " + dayShareTime.substring(position + 3, dayShareTime.length());
                if (DateUtil.getYearToMinuteCalendar(startTime).compareTo(DateUtil.getYearToMinuteCalendar(endTime)) >= 0) {
                    //出租时段是跨天的
                    endTime = "2018-04-25 " + dayShareTime.substring(position + 3, dayShareTime.length());
                }
                everydayShareTimeInfo.setStartDate(startTime);
                everydayShareTimeInfo.setEndDate(endTime);
                mEverydayShareTimeAdapter.addData(everydayShareTimeInfo);
            }
        }
    }

    @NonNull
    @Override
    protected String title() {
        return "修改出租时间";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modify_share_time_start_share_time:
                showDatePicker("开始出租日期", new DateCheck(new OnDateCheckListener() {
                    @Override
                    public void onDateCheck(Calendar calendar, int year, int month, int dayOfMonth, int compareResult) {
                        if (compareResult > -1) {
                            String startDate = year + "-" + DateUtil.thanTen(month) + "-" + DateUtil.thanTen(dayOfMonth);
                            Calendar endCalendar = Calendar.getInstance();
                            String[] endDate = mEndShareDate.getText().toString().split("-");
                            endCalendar.set(Integer.valueOf(endDate[0]), Integer.valueOf(endDate[1]), Integer.valueOf(endDate[2]));
                            Calendar originStartCalendar = DateUtil.getYearToDayCalendar(mStartShareDate.getText().toString(), false);
                            if (originStartCalendar.compareTo(calendar) < 0) {
                                //只有当想修改开始日期比原来的日期往后时才需要判断
                                String orderDate = DateUtil.getOrderBetweenDate(mStartShareDate.getText().toString() + " 00:00", startDate + " 00:00", mParkInfo.getOrder_times());
                                if (orderDate != null) {
                                    //修改出租时间时如果修改的时间段内已被别人预定过则不能修改
                                    if (orderDate.substring(0, orderDate.indexOf("至")).equals(orderDate.substring(orderDate.indexOf("至") + 1, orderDate.length()))) {
                                        showFiveToast(orderDate.substring(0, orderDate.indexOf("至")) + "这天已有人预定了，不能修改哦");
                                    } else {
                                        showFiveToast(orderDate + "这个时间段已有人预定了，不能修改哦");
                                    }
                                    return;
                                }
                            }

                            mStartShareDate.setText(startDate);

                            //如果出租的开始日期比结束日期大，则自动修改结束日期
                            if (calendar.compareTo(endCalendar) > 0) {
                                Calendar startCalendar = getYearToDayCalendar(startDate);
                                startCalendar.add(Calendar.MONTH, 1);
                                mEndShareDate.setText(DateUtil.getYearToDayFormat().format(new Date(startCalendar.getTimeInMillis())));
                                showFiveToast("已自动为你修改结束的出租时间");
                            }
                            checkPauseDate();
                        } else {
                            showFiveToast("开始出租的时间不能小于当天哦");
                        }
                    }
                }));
                break;
            case R.id.modify_share_time_end_share_time:
                showDatePicker("结束出租日期", new DateCheck(new OnDateCheckListener() {
                    @Override
                    public void onDateCheck(Calendar calendar, int year, int month, int dayOfMonth, int compareResult) {
                        if (compareResult > -1) {
                            String endDate = year + "-" + DateUtil.thanTen(month) + "-" + DateUtil.thanTen(dayOfMonth);
                            Calendar originEndCalendar = Calendar.getInstance();
                            String[] originEndDate = mEndShareDate.getText().toString().split("-");
                            originEndCalendar.set(Integer.valueOf(originEndDate[0]), Integer.valueOf(originEndDate[1]), Integer.valueOf(originEndDate[2]));
                            if (originEndCalendar.compareTo(calendar) > 0) {
                                //想把出租日期的结束时间修改为比原来的提前
                                String orderDate = DateUtil.getOrderBetweenDate(endDate + " 00:00", mEndShareDate.getText().toString() + " 24:00", mParkInfo.getOrder_times());
                                if (orderDate != null) {
                                    if (orderDate.substring(0, orderDate.indexOf("至")).equals(orderDate.substring(orderDate.indexOf("至") + 1, orderDate.length()))) {
                                        showFiveToast(orderDate.substring(0, orderDate.indexOf("至")) + "这天已有人预定了，不能修改哦");
                                    } else {
                                        showFiveToast(orderDate + "这个时间段已有人预定了，不能修改哦");
                                    }
                                } else {
                                    mEndShareDate.setText(endDate);
                                    checkPauseDate();
                                }
                            } else {
                                mEndShareDate.setText(endDate);
                            }
                        } else {
                            showFiveToast("要选择比开始出租之后的时间哦");
                        }
                    }
                }, mStartShareDate.getText().toString()));
                break;
            case R.id.modify_share_time_add_pause_date:
                if (mPauseShareDateAdapter.getData().size() >= 5) {
                    showFiveToast("最多只能选择5个暂停日期哦");
                } else {
                    showDatePicker("暂停出租日期", new DateCheck(new OnDateCheckListener() {
                        @Override
                        public void onDateCheck(Calendar calendar, int year, int month, int dayOfMonth, int compareResult) {
                            if (compareResult > -1) {
                                String pausDate = year + "-" + DateUtil.thanTen(month) + "-" + DateUtil.thanTen(dayOfMonth);
                                if (mPauseShareDateAdapter.getData().contains(pausDate)) {
                                    showFiveToast("不能重复添加哦");
                                } else {
                                    Calendar startCalendar = getYearToDayCalendar(mStartShareDate.getText().toString());
                                    Calendar endCalendar = getYearToDayCalendar(mEndShareDate.getText().toString());
                                    Calendar pauseCalendar = getYearToDayCalendar(pausDate);
                                    if (pauseCalendar.compareTo(startCalendar) >= 0 && pauseCalendar.compareTo(endCalendar) <= 0) {
                                        //暂停出租的日期要在开放的日期之内
                                        if (isOrderInPauseDate(pausDate)) {
                                            showFiveToast(pausDate + "这天已被人预定了，不能暂停哦");
                                            return;
                                        }

                                        mPauseShareDateAdapter.justAddData(pausDate);
                                        Collections.sort(mPauseShareDateAdapter.getData(), new Comparator<String>() {
                                            @Override
                                            public int compare(String o1, String o2) {
                                                return getYearToDayCalendar(o1).compareTo(getYearToDayCalendar(o2));
                                            }
                                        });
                                        mPauseShareDateAdapter.notifyDataSetChanged();
                                    } else {
                                        showFiveToast("暂停出租日期要在出租日期之内哦");
                                    }
                                }
                            } else {
                                showFiveToast("要选择不小于当天的日期哦");
                            }
                        }
                    }));
                }
                break;
            case R.id.modify_share_time_add_everyday_share_time:
                if (mEverydayShareTimeAdapter.getData().size() == 3) {
                    showFiveToast("只能选择三个时段哦");
                } else {
                    Calendar calendar = Calendar.getInstance();
                    final EverydayShareTimeInfo shareTimeInfo = new EverydayShareTimeInfo();
                    showTimePicker("开始时段", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true, new OnTimePickerListener() {
                        @Override
                        public void onStartTime(String hourOfDay, String minute) {
                            String startTime = "2018-04-24 " + DateUtil.thanTen(Integer.valueOf(hourOfDay)) + ":" + DateUtil.thanTen(Integer.valueOf(minute));
                            shareTimeInfo.setStartDate(startTime);
                        }

                        @Override
                        public void onEndTime(String hourOfDay, String minute) {
                            String endTime = "2018-04-24 " + DateUtil.thanTen(Integer.valueOf(hourOfDay)) + ":" + DateUtil.thanTen(Integer.valueOf(minute));
                            Calendar startTimeCalendar = DateUtil.getYearToMinuteCalendar(shareTimeInfo.getStartDate());
                            Calendar endTimeCalendar = DateUtil.getYearToMinuteCalendar(endTime);

                            if (startTimeCalendar.compareTo(endTimeCalendar) == 0) {
                                showFiveToast("全天出租不用设置哦");
                                return;
                            } else if (startTimeCalendar.compareTo(endTimeCalendar) >= 0) {
                                endTime = "2018-04-25 " + DateUtil.thanTen(Integer.valueOf(hourOfDay)) + ":" + DateUtil.thanTen(Integer.valueOf(minute));
                            }
                            shareTimeInfo.setEndDate(endTime);
                            if (isRepeat(shareTimeInfo, null)) {
                                showFiveToast("不能选择有重叠的时间哦");
                            } else {
                                String orderTime = isInNotShareTime(startTimeCalendar, endTimeCalendar);
                                if (orderTime != null) {
                                    showFiveToast(orderTime + "已被人预定，不能中断哦");
                                    return;
                                }

                                mEverydayShareTimeAdapter.justAddData(shareTimeInfo);
                                Collections.sort(mEverydayShareTimeAdapter.getData(), new Comparator<EverydayShareTimeInfo>() {
                                    @Override
                                    public int compare(EverydayShareTimeInfo o1, EverydayShareTimeInfo o2) {
                                        return DateUtil.getYearToMinuteCalendar(o1.getStartDate()).compareTo(DateUtil.getYearToMinuteCalendar(o2.getStartDate()));
                                    }
                                });
                                mEverydayShareTimeAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
                break;
            case R.id.modify_share_time_submit:
                modifyShareTime();
                break;
        }
    }

    private void initDateOption() {
        mYears = new ArrayList<>();
        mMonths = new ArrayList<>();
        mDays = new ArrayList<>();
        DateUtil.initRecentTwoYear(mYears, mMonths, mDays);

        mDateOption = new OptionsPickerView<>(this);
        mDateOption.setPicker(mYears, mMonths, mDays, true);
        mDateOption.setCyclic(false);
        mDateOption.setTextSize(16);
    }

    private void initTimeOption() {
        mHours = new ArrayList<>(24);
        mMinutes = new ArrayList<>(60);
        DateUtil.initHourWithMinute(mHours, mMinutes);

        mTimeOption = new OptionsPickerView<>(this);
        mTimeOption.setPicker(mHours, mMinutes, null, true);
        mTimeOption.setCyclic(true);
        mTimeOption.setTextSize(16);
    }

    /**
     * @return true(有已预定的订单在pauseDate这天)
     */
    private boolean isOrderInPauseDate(String pauseDate) {
        if (!mParkInfo.getOrder_times().equals("-1") && !mParkInfo.getOrder_times().equals("")) {
            for (String orderTime : mParkInfo.getOrder_times().split(",")) {
                if (DateUtil.isInParkSpacePauseDate(orderTime.substring(0, orderTime.indexOf("*")),
                        orderTime.substring(orderTime.indexOf("*") + 1, orderTime.length()), pauseDate)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 修改了出租日期后检查暂停同享日期是否在出租日期内,如果不在则删除
     */
    private void checkPauseDate() {
        if (!mPauseShareDateAdapter.getData().isEmpty()) {
            Calendar startDate = getYearToDayCalendar(mStartShareDate.getText().toString());
            Calendar endDate = getYearToDayCalendar(mEndShareDate.getText().toString());
            List<String> legalData = new ArrayList<>();
            Calendar calendar;
            for (String s : mPauseShareDateAdapter.getData()) {
                calendar = getYearToDayCalendar(s);
                if (startDate.compareTo(calendar) <= 0 && calendar.compareTo(endDate) <= 0) {
                    legalData.add(s);
                }
            }
            mPauseShareDateAdapter.setNewData(legalData);
        }
    }

    /**
     * 如果之前是全天出租的，现在加了出租时段，则判断那些不在出租时段内的是否已被预定
     *
     * @return null(不被出租的时间段内没有已预定订单)
     */
    private String isInNotShareTime(Calendar startCalendar, Calendar endCalendar) {
        Log.e(TAG, "onEndTime   startCalendar: " + DateUtil.getCalendarMonthToMinute(startCalendar));
        Log.e(TAG, "onEndTime   endCalendar: " + DateUtil.getCalendarMonthToMinute(endCalendar));
        //如果之间是全天出租的，则设置了出租时段后会影响那些不在出租时段内预定的订单
        //如果之间就已经设置了出租时段的，则别人肯定只能在出租时段内预定订单，增加了出租时段对原来的订单没影响
        if (mEverydayShareTimeAdapter.getData().isEmpty()) {
            String notShareTime;
            if (startCalendar.compareTo(endCalendar) > 0) {
                //08:00 - 03:00的则判断03:00到08:00这个时间段是否被预定
                notShareTime = DateUtil.getHourWithMinutes(endCalendar) + " - " + DateUtil.getHourWithMinutes(startCalendar);
                Log.e(TAG, "isInNotShareTime0: " + notShareTime);
                return DateUtil.isInShareTime(mParkInfo.getOrder_times(), notShareTime);
            } else {
                if (startCalendar.compareTo(DateUtil.getSpecialTodayStartCalendar()) == 0) {
                    //00:00 - 03:00的，则判断03:00 - 24:00这个时间段是否被预定
                    notShareTime = DateUtil.getHourWithMinutes(endCalendar) + " - 24:00";
                    Log.e(TAG, "isInNotShareTime1 : " + notShareTime);
                    return DateUtil.isInShareTime(mParkInfo.getOrder_times(), notShareTime);
                } else {
                    //03:00 - 08:00的
                    //判断00:00 - 03:00这个时间段有没有被预定
                    String orderTime;
                    notShareTime = "00:00 - " + DateUtil.getHourWithMinutes(startCalendar);
                    Log.e(TAG, "isInNotShareTime3: " + notShareTime);
                    orderTime = DateUtil.isInShareTime(mParkInfo.getOrder_times(), notShareTime);
                    if (orderTime != null) {
                        return orderTime;
                    }

                    //判断08:00 - 24:00这个时段段有没有被预定
                    notShareTime = DateUtil.getHourWithMinutes(endCalendar) + " - 24:00";
                    Log.e(TAG, "isInNotShareTime3: " + notShareTime);
                    return DateUtil.isInShareTime(mParkInfo.getOrder_times(), notShareTime);
                }
            }
        }
        return null;
    }

    /**
     * 提交修改出租时间
     */
    private void modifyShareTime() {
        showLoadingDialog("正在修改");
        final String shareDate = mStartShareDate.getText().toString() + " - " + mEndShareDate.getText().toString();

        final StringBuilder shareDay = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            if (mCheckTextViews[i].isChecked()) {
                shareDay.append("1");
            } else {
                shareDay.append("0");
            }
            shareDay.append(",");
        }
        shareDay.deleteCharAt(shareDay.length() - 1);

        final StringBuilder pauseShareDate = new StringBuilder();
        if (!mPauseShareDateAdapter.getData().isEmpty()) {
            for (String pause : mPauseShareDateAdapter.getData()) {
                pauseShareDate.append(pause);
                pauseShareDate.append(",");
            }
            pauseShareDate.deleteCharAt(pauseShareDate.length() - 1);
        } else {
            pauseShareDate.append("-1");
        }

        final StringBuilder everyDayShareTime = new StringBuilder();
        if (!mEverydayShareTimeAdapter.getData().isEmpty()) {
            for (EverydayShareTimeInfo everydayShareTimeInfo : mEverydayShareTimeAdapter.getData()) {
                everyDayShareTime.append(everydayShareTimeInfo.getStatTime());
                everyDayShareTime.append(" - ");
                everyDayShareTime.append(everydayShareTimeInfo.getEndTime());
                everyDayShareTime.append(",");
            }
            everyDayShareTime.deleteCharAt(everyDayShareTime.length() - 1);
        } else {
            everyDayShareTime.append("-1");
        }

        getOkGo(HttpConstants.editShareTime)
                .params("cityCode", mParkInfo.getCityCode())
                .params("parkId", mParkInfo.getId())
                .params("shareDate", shareDate)
                .params("shareDay", shareDay.toString())
                .params("pauseShareDate", pauseShareDate.toString())
                .params("everyDayShareTime", everyDayShareTime.toString())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        mParkInfo.setOpen_date(shareDate);
                        mParkInfo.setShareDay(shareDay.toString());
                        mParkInfo.setPauseShareDate(pauseShareDate.toString());
                        mParkInfo.setOpen_time(everyDayShareTime.toString());

                        Intent intent = new Intent();
                        intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, mParkInfo);
                        setResult(RESULT_OK, intent);
                        dismmisLoadingDialog();
                        showFiveToast("修改成功");
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    userError();
                                    break;
                                case "102":
                                    showFiveToast("城市码错误，请稍后再试");
                                    break;
                                case "103":
                                    showFiveToast("车位不存在");
                                    break;
                                case "104":
                                    showFiveToast("该车位不是你的哦");
                                    break;
                                case "105":
                                    finish();
                                    break;
                                case "106":
                                    showFiveToast("修改失败，请稍后再试");
                                    break;
                                case "107":
                                    showFiveToast("修改的时间段内已被预定");
                                    break;
                            }
                        }
                    }
                });
    }

    /**
     * 打开日期选择器
     *
     * @param dateCheck 日期的选择结果将回调到构造函数的接口方法中
     */
    private void showDatePicker(String title, final DateCheck dateCheck) {
        Calendar calendar = Calendar.getInstance();
        boolean seleteToday = false;

        //打开日期选择器时显示的年月日的位置
        int year = 0;
        int month = 0;
        int day = 0;
        if (dateCheck.getYear() != 0) {
            year = mYears.indexOf(String.valueOf(dateCheck.getYear()));
            if (year == -1) {
                seleteToday = true;
            } else {
                month = mMonths.get(year).indexOf(String.valueOf(dateCheck.getMonth()));
                if (month == -1) {
                    seleteToday = true;
                } else {
                    day = mDays.get(year).get(month).indexOf(String.valueOf(dateCheck.getDayOfMonth()));
                    if (day == -1) {
                        seleteToday = true;
                    }
                }
            }
        }

        if (seleteToday || dateCheck.getYear() == 0) {
            //如果选择了显示今天或者没有指定显示特定的年月日则显示今天所在的年月日
            year = mYears.indexOf(String.valueOf(calendar.get(Calendar.YEAR)));
            month = mMonths.get(year).indexOf(String.valueOf(calendar.get(Calendar.MONTH) + 1));
            day = mDays.get(year).get(month).indexOf(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        }

        mDateOption.setSelectOptions(year, month, day);
        mDateOption.setTitle(title);
        mDateOption.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                dateCheck.onDateSet(null, Integer.valueOf(mYears.get(options1)), Integer.valueOf(mMonths.get(options1).get(option2)),
                        Integer.valueOf(mDays.get(options1).get(option2).get(options3)));
            }
        });
        mDateOption.show();
    }

    /**
     * @param hourOfDayParams 打开时间选择器时默认显示的小时
     * @param minuteParams    默认显示的分钟
     * @param contiue         ture:选择了开始时段之后，继续选择结束时段
     */
    private void showTimePicker(String title, final int hourOfDayParams, final int minuteParams, final boolean contiue, final OnTimePickerListener listener) {
        mTimeOption.setTitle(title);
        if (contiue) {
            mTimeOption.setBtnSubmit("下一步");
        } else {
            mTimeOption.setBtnSubmit("确定");
        }
        int hourPosition = mHours.indexOf(String.valueOf(hourOfDayParams));
        int minutePosition = mMinutes.get(hourPosition).indexOf(String.valueOf(minuteParams));
        mTimeOption.setSelectOptions(hourPosition, minutePosition);
        mTimeOption.setAutoDismiss(false);

        mTimeOption.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                if (contiue) {
                    listener.onStartTime(mHours.get(options1), mMinutes.get(options1).get(option2));
                    //结束时段默认为开始时段的两个小时后
                    int hours = Integer.valueOf(mHours.get(options1)) + 2;
                    showTimePicker("结束时段", hours > 23 ? hours - 23 : hours, Integer.valueOf(mMinutes.get(options1).get(option2)),
                            false, listener);
                } else {
                    listener.onEndTime(mHours.get(options1), mMinutes.get(options1).get(option2));
                    mTimeOption.setAutoDismiss(true);
                    mTimeOption.dismiss();
                }
            }
        });
        mTimeOption.show();
    }

    private void showTimePicker(final EverydayShareTimeInfo everydayShareTimeInfo, final boolean isStartTime) {
        int hourPosition;
        int minutesPosition;
        if (isStartTime) {
            mTimeOption.setTitle("开始时段");
            hourPosition = mHours.indexOf(DateUtil.deleteTen(everydayShareTimeInfo.getStatTime().substring(0, everydayShareTimeInfo.getStatTime().indexOf(":"))));
            minutesPosition = mMinutes.get(hourPosition).indexOf(DateUtil.deleteTen(everydayShareTimeInfo.getStatTime().substring(
                    everydayShareTimeInfo.getStatTime().indexOf(":") + 1, everydayShareTimeInfo.getStatTime().length())));
        } else {
            mTimeOption.setTitle("结束时段");
            hourPosition = mHours.indexOf(DateUtil.deleteTen(everydayShareTimeInfo.getEndTime().substring(0, everydayShareTimeInfo.getEndTime().indexOf(":"))));
            minutesPosition = mMinutes.get(hourPosition).indexOf(DateUtil.deleteTen(everydayShareTimeInfo.getEndTime().substring(
                    everydayShareTimeInfo.getEndTime().indexOf(":") + 1, everydayShareTimeInfo.getEndTime().length())));
        }
        mTimeOption.setBtnSubmit("确定");
        mTimeOption.setAutoDismiss(true);
        mTimeOption.setSelectOptions(hourPosition, minutesPosition);
        mTimeOption.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                String seleteDate = "2018-04-24 " + DateUtil.thanTen(Integer.valueOf(mHours.get(options1))) + ":"
                        + DateUtil.thanTen(Integer.valueOf(mMinutes.get(options1).get(option2)));
                EverydayShareTimeInfo shareTimeInfo = new EverydayShareTimeInfo();  //修改后的时间段

                if (isStartTime) {
                    shareTimeInfo.setStartDate(seleteDate);
                    shareTimeInfo.setEndDate(everydayShareTimeInfo.getEndDate());
                } else {
                    shareTimeInfo.setStartDate(everydayShareTimeInfo.getStartDate());
                    shareTimeInfo.setEndDate(seleteDate);
                }

                if (shareTimeInfo.equals(everydayShareTimeInfo)) {
                    //没有修改
                    return;
                }

                Calendar startTimeCalendar = DateUtil.getYearToMinuteCalendar(shareTimeInfo.getStartDate());
                Calendar endTimeCalendar = DateUtil.getYearToMinuteCalendar(shareTimeInfo.getEndDate());

                if (startTimeCalendar.compareTo(endTimeCalendar) >= 0) {
                    //跨天的
                    seleteDate = seleteDate.replaceFirst("04-24", "04-25");
                    if (isStartTime) {
                        shareTimeInfo.setStartDate(seleteDate);
                    } else {
                        shareTimeInfo.setEndDate(seleteDate);
                    }
                }

                if (shareTimeInfo.equals(everydayShareTimeInfo)) {
                    //没有修改
                    return;
                }

                if (isRepeat(shareTimeInfo, everydayShareTimeInfo)) {
                    showFiveToast("不能选择有重叠的时间哦");
                } else {
                    String orderTime = isInNotShareTime(startTimeCalendar, endTimeCalendar);
                    if (orderTime != null) {
                        showFiveToast(orderTime + "已被人预定，不能中断哦");
                        return;
                    }

                    //把原来的时间段替换为新的时间段
                    mEverydayShareTimeAdapter.getData().set(mEverydayShareTimeAdapter.getData().indexOf(everydayShareTimeInfo), shareTimeInfo);

                    if (mEverydayShareTimeAdapter.getDataSize() > 1) {
                        //按开始出租的时间排序
                        Collections.sort(mEverydayShareTimeAdapter.getData(), new Comparator<EverydayShareTimeInfo>() {
                            @Override
                            public int compare(EverydayShareTimeInfo o1, EverydayShareTimeInfo o2) {
                                return DateUtil.getYearToMinuteCalendar(o1.getStartDate()).compareTo(DateUtil.getYearToMinuteCalendar(o2.getStartDate()));
                            }
                        });
                    }
                    mEverydayShareTimeAdapter.notifyDataSetChanged();
                }
            }
        });
        mTimeOption.show();
    }

    private void changePauseDate(final String originPauseDate) {
        showDatePicker("暂停出租日期", new DateCheck(new OnDateCheckListener() {
            @Override
            public void onDateCheck(Calendar calendar, int year, int month, int dayOfMonth, int compareResult) {
                String pausDate = year + "-" + DateUtil.thanTen(month) + "-" + DateUtil.thanTen(dayOfMonth);
                if (mPauseShareDateAdapter.getData().contains(pausDate)) {
                    if (!originPauseDate.equals(pausDate)) {
                        showFiveToast("已经有这个暂停出租日期了哦");
                    }
                } else {
                    Calendar startCalendar = getYearToDayCalendar(mStartShareDate.getText().toString());
                    Calendar endCalendar = getYearToDayCalendar(mEndShareDate.getText().toString());
                    Calendar pauseCalendar = getYearToDayCalendar(pausDate);
                    if (pauseCalendar.compareTo(startCalendar) >= 0 && pauseCalendar.compareTo(endCalendar) <= 0) {

                        if (isOrderInPauseDate(pausDate)) {
                            showFiveToast(pausDate + "这天已被人预定了，不能暂停哦");
                            return;
                        }

                        //修改为新的暂停日期
                        int position = mPauseShareDateAdapter.getData().indexOf(originPauseDate);
                        mPauseShareDateAdapter.getData().set(position, pausDate);
                        if (mPauseShareDateAdapter.getDataSize() > 1) {
                            Collections.sort(mPauseShareDateAdapter.getData(), new Comparator<String>() {
                                @Override
                                public int compare(String o1, String o2) {
                                    return getYearToDayCalendar(o1).compareTo(getYearToDayCalendar(o2));
                                }
                            });
                        }
                        mPauseShareDateAdapter.notifyDataSetChanged();
                    } else {
                        showFiveToast("暂停出租日期要在出租日期之内哦");
                    }
                }
            }
        }, originPauseDate));
    }

    /**
     * @param s 日期的字符串表示(2018-3-30)
     * @return 该日期对应的日历
     */
    private Calendar getYearToDayCalendar(String s) {
        Calendar calendar = Calendar.getInstance();
        String[] date = s.split("-");
        calendar.set(Integer.valueOf(date[0]), Integer.valueOf(date[1]) - 1, Integer.valueOf(date[2]));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * @param shareTimeInfo 需要比较的时间段
     * @return ture:已经添加的时段中与需要比较的时间段有重叠
     */
    private boolean isRepeat(EverydayShareTimeInfo shareTimeInfo, EverydayShareTimeInfo skipShareTime) {
        Calendar otherStartCalendar = DateUtil.getYearToMinuteCalendar(shareTimeInfo.getStartDate());
        Calendar otherEndCalendar = DateUtil.getYearToMinuteCalendar(shareTimeInfo.getEndDate());
        Calendar startCalendar;
        Calendar endCalendar;
        for (EverydayShareTimeInfo timeInfo : mEverydayShareTimeAdapter.getData()) {
            if (skipShareTime != null && timeInfo.equals(skipShareTime)) {
                //如果是修改原本的时间段的话需要跳过检查原本的时间段,否则肯定是有重叠的啦
                continue;
            }
            startCalendar = DateUtil.getYearToMinuteCalendar(timeInfo.getStartDate());
            endCalendar = DateUtil.getYearToMinuteCalendar(timeInfo.getEndDate());

            if (DateUtil.isIntersection(otherStartCalendar, otherEndCalendar, startCalendar, endCalendar)) {
                //2018-04-24 10:30 - 2018-04-24 12:30   和   2018-04-24 11:00 - 2018-04-24 13:00
                return true;
            }

            if (endCalendar.compareTo(DateUtil.getSpecialTodayEndCalendar()) >= 0) {
                //2018-04-24 10:30 - 2018-04-25 06:30   和   2018-04-24 01:00 - 2018-04-24 05:00
                //如果原来的时间段是跨天的则再比较第二天的时段是否有重复,比较2018-04-24 00:00 - 2018-04-24 06:30   和   2018-04-24 01:00 - 2018-04-24 05:00
                startCalendar = DateUtil.getSpecialTodayStartCalendar();
                endCalendar.add(Calendar.DAY_OF_MONTH, -1);
                if (DateUtil.isIntersection(otherStartCalendar, otherEndCalendar, startCalendar, endCalendar)) {
                    return true;
                }
            }

            if (otherEndCalendar.compareTo(DateUtil.getSpecialTodayEndCalendar()) >= 0) {
                //2018-04-24 10:30 - 2018-04-24 16:30   和   2018-04-24 18:00 - 2018-04-25 05:00
                //如果新的的时间段是跨天的则再比较第二天的时段是否有重复,比较2018-04-24 10:30 - 2018-04-24 16:30   和   2018-04-24 00:00 - 2018-04-24 05:00
                otherStartCalendar = DateUtil.getSpecialTodayStartCalendar();
                otherEndCalendar.add(Calendar.DAY_OF_MONTH, -1);
                if (DateUtil.isIntersection(otherStartCalendar, otherEndCalendar, startCalendar, endCalendar)) {
                    return true;
                }
            }
        }
        return false;
    }

    private class PauseShareDateAdapter extends BaseAdapter<String> {

        PauseShareDateAdapter() {
            super();
        }

        @Override
        protected void conver(@NonNull BaseViewHolder holder, final String s, final int position) {
            holder.setText(R.id.add_pause_share_date, s)
                    .getView(R.id.add_pause_share_date).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changePauseDate(s);
                }
            });
            holder.getView(R.id.delete_pause_share_date).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyRemoveData(position);
                }
            });
        }

        @Override
        protected int itemViewId() {
            return R.layout.item_add_pause_share_date;
        }

    }

    public class ShareTimeAdapter extends BaseAdapter<EverydayShareTimeInfo> {

        ShareTimeAdapter() {
            super();
        }

        @Override
        protected void conver(@NonNull BaseViewHolder holder, final EverydayShareTimeInfo everydayShareTimeInfo, final int poisition) {
            holder.setText(R.id.add_everyday_share_start_time, DateUtil.getHourWithMinutes(everydayShareTimeInfo.getStartDate()))
                    .setText(R.id.add_everyday_share_end_time, DateUtil.getHourWithMinutes(everydayShareTimeInfo.getEndDate()))
                    .getView(R.id.delete_everyday_share_time).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getDataSize() == 1) {
                        notifyRemoveData(poisition);
                    } else {
                        String orderTime = DateUtil.isInShareTime(mParkInfo.getOrder_times(), everydayShareTimeInfo.getStatTime() + " - " + everydayShareTimeInfo.getEndTime());
                        if (orderTime != null) {
                            showFiveToast(orderTime + "已被人预定了，不可取消哦");
                        } else {
                            notifyRemoveData(poisition);
                        }
                    }
                }
            });
            holder.getView(R.id.add_everyday_share_start_time).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimePicker(everydayShareTimeInfo, true);
                }
            });

            holder.getView(R.id.add_everyday_share_end_time).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimePicker(everydayShareTimeInfo, false);
                }
            });
        }

        @Override
        protected int itemViewId() {
            return R.layout.item_add_everyday_share_time;
        }

    }

    /**
     * 将日期选择器的接口替换为自定义的日期选择器接口
     */
    class DateCheck implements DatePickerDialog.OnDateSetListener {

        private OnDateCheckListener mDateCheckListener;

        private int mYear;

        private int mMonth;

        private int mDayOfMonth;

        /**
         * 使用当前的日期来比较
         */
        DateCheck(OnDateCheckListener listener) {
            mDateCheckListener = listener;
        }

        /**
         * 需要比较特定的日期的时候使用
         *
         * @param dateText 直接传入TextView里面的日期即可
         */
        DateCheck(OnDateCheckListener listener, String dateText) {
            mDateCheckListener = listener;
            if (!dateText.equals("")) {
                String[] date = dateText.split("-");
                mYear = Integer.parseInt(date[0]);
                mMonth = Integer.parseInt(date[1]);
                mDayOfMonth = Integer.parseInt(date[2]);
            }
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            DateUtil.initHourToMilli(calendar);

            //如果输入指定的日期则与指定的日期进行比较，否则与当天日期进行比较
            Calendar otherCanlendar = Calendar.getInstance();
            if (mYear != 0) {
                otherCanlendar.set(Calendar.YEAR, mYear);
                otherCanlendar.set(Calendar.MONTH, mMonth);
                otherCanlendar.set(Calendar.DAY_OF_MONTH, mDayOfMonth);
            }
            DateUtil.initHourToMilli(otherCanlendar);
            mDateCheckListener.onDateCheck(calendar, year, month, dayOfMonth, calendar.compareTo(otherCanlendar));
        }

        int getYear() {
            return mYear;
        }

        int getMonth() {
            return mMonth;
        }

        int getDayOfMonth() {
            return mDayOfMonth;
        }
    }

    interface OnDateCheckListener {

        /**
         * 返回选中的年月日
         *
         * @param calendar      选中的日期
         * @param compareResult -1,选中的日期比需要比较的日期小；0选中了需要比较的日期；选中了比需要比较的日期大
         */
        void onDateCheck(Calendar calendar, int year, int month, int dayOfMonth, int compareResult);
    }

    interface OnTimePickerListener {

        /**
         * 开始选择的时间
         */
        void onStartTime(String hourOfDay, String minute);

        /**
         * 结束选择的时间
         */
        void onEndTime(String hourOfDay, String minute);
    }

}
