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
import com.tuzhao.info.NewParkSpaceInfo;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.ShareTimeInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.customView.CheckTextView;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;

import java.text.SimpleDateFormat;
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

    private NewParkSpaceInfo mNewParkSpaceInfo;

    private Park_Info mParkInfo;

    @Override
    protected int resourceId() {
        return R.layout.activity_modify_share_time;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        if ((mNewParkSpaceInfo =  getIntent().getParcelableExtra(ConstansUtil.ADD_PARK_SPACE_TEME)) == null) {

            if ((mParkInfo = getIntent().getParcelableExtra(ConstansUtil.PARK_SPACE_INFO)) == null) {
                showFiveToast("打开失败，请返回重试");
                finish();
            }

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

        if (mNewParkSpaceInfo != null) {
            if (!mNewParkSpaceInfo.isHourRent()) {
                findViewById(R.id.modify_share_time_everyday_share_date).setVisibility(View.GONE);
            }
            for (CheckTextView checkTextView : mCheckTextViews) {
                checkTextView.setChecked(true);
            }
        } else if (mParkInfo != null && !mParkInfo.getOrder_times().equals("-1") && !mParkInfo.getOrder_times().equals("")) {
            mMondayShare.setOnCheckChangeListener(new CheckTextView.OnCheckChangeListener() {
                @Override
                public void onCheckChange(boolean isCheck) {
                    if (!isCheck) {
                        if (DateUtil.isInOrderDay(1, getText(mStartShareDate), getText(mEndShareDate), mParkInfo.getOrder_times())) {
                            showFiveToast("星期一已被人预约了，不可以取消哦");
                        } else {
                            mMondayShare.setChecked(false);
                        }
                    } else {
                        mMondayShare.setChecked(true);
                    }
                }
            });

            mTuesdayShare.setOnCheckChangeListener(new CheckTextView.OnCheckChangeListener() {
                @Override
                public void onCheckChange(boolean isCheck) {
                    if (!isCheck) {
                        if (DateUtil.isInOrderDay(2, getText(mStartShareDate), getText(mEndShareDate), mParkInfo.getOrder_times())) {
                            showFiveToast("星期二已被人预约了，不可以取消哦");
                        } else {
                            mTuesdayShare.setChecked(false);
                        }
                    } else {
                        mTuesdayShare.setChecked(true);
                    }
                }
            });

            mWednesdayShare.setOnCheckChangeListener(new CheckTextView.OnCheckChangeListener() {
                @Override
                public void onCheckChange(boolean isCheck) {
                    if (!isCheck) {
                        if (DateUtil.isInOrderDay(3, getText(mStartShareDate), getText(mEndShareDate), mParkInfo.getOrder_times())) {
                            showFiveToast("星期三已被人预约了，不可以取消哦");
                        } else {
                            mWednesdayShare.setChecked(false);
                        }
                    } else {
                        mWednesdayShare.setChecked(true);
                    }
                }
            });

            mThursdayShare.setOnCheckChangeListener(new CheckTextView.OnCheckChangeListener() {
                @Override
                public void onCheckChange(boolean isCheck) {
                    if (!isCheck) {
                        if (DateUtil.isInOrderDay(4, getText(mStartShareDate), getText(mEndShareDate), mParkInfo.getOrder_times())) {
                            showFiveToast("星期四已被人预约了，不可以取消哦");
                        } else {
                            mThursdayShare.setChecked(false);
                        }
                    } else {
                        mThursdayShare.setChecked(true);
                    }
                }
            });

            mFridayShare.setOnCheckChangeListener(new CheckTextView.OnCheckChangeListener() {
                @Override
                public void onCheckChange(boolean isCheck) {
                    if (!isCheck) {
                        if (DateUtil.isInOrderDay(5, getText(mStartShareDate), getText(mEndShareDate), mParkInfo.getOrder_times())) {
                            showFiveToast("星期五已被人预约了，不可以取消哦");
                        } else {
                            mFridayShare.setChecked(false);
                        }
                    } else {
                        mFridayShare.setChecked(true);
                    }
                }
            });

            mSaturdayShare.setOnCheckChangeListener(new CheckTextView.OnCheckChangeListener() {
                @Override
                public void onCheckChange(boolean isCheck) {
                    if (!isCheck) {
                        if (DateUtil.isInOrderDay(6, getText(mStartShareDate), getText(mEndShareDate), mParkInfo.getOrder_times())) {
                            showFiveToast("星期六已被人预约了，不可以取消哦");
                        } else {
                            mSaturdayShare.setChecked(false);
                        }
                    } else {
                        mSaturdayShare.setChecked(true);
                    }
                }
            });

            mSundayShare.setOnCheckChangeListener(new CheckTextView.OnCheckChangeListener() {
                @Override
                public void onCheckChange(boolean isCheck) {
                    if (!isCheck) {
                        if (DateUtil.isInOrderDay(7, getText(mStartShareDate), getText(mEndShareDate), mParkInfo.getOrder_times())) {
                            showFiveToast("星期日已被人预约了，不可以取消哦");
                        } else {
                            mSundayShare.setChecked(false);
                        }
                    } else {
                        mSundayShare.setChecked(true);
                    }
                }
            });
        }
    }

    @Override
    protected void initData() {
        super.initData();

        if (mNewParkSpaceInfo == null) {
            //从车位设置跳转过来的则请求该车位的共享时间
            setOriginTime();
        } else {
            //从添加车位跳转过来的则共享日期显示为当天-之后一个月
            SimpleDateFormat dateFormat = DateUtil.getYearToDayFormat();
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            date.setTime(calendar.getTimeInMillis());

            mStartShareDate.setText(dateFormat.format(date));

            calendar.add(Calendar.MONTH, 1);
            date.setTime(calendar.getTimeInMillis());
            mEndShareDate.setText(dateFormat.format(date));
        }

        initDateOption();
        initTimeOption();
        dismmisLoadingDialog();
    }

    @Override
    public void onBackPressed() {
        if (mDateOption.isShowing()) {
            mDateOption.dismiss();
        } else {
            super.onBackPressed();
        }
    }

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
        return mNewParkSpaceInfo == null ? "共享时间修改" : "设置共享时间";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modify_share_time_start_share_time:
                showDatePicker("开始共享日期", new DateCheck(new OnDateCheckListener() {
                    @Override
                    public void onDateCheck(Calendar calendar, int year, int month, int dayOfMonth, int compareResult) {
                        if (compareResult > -1) {
                            String startDate = year + "-" + DateUtil.thanTen(month) + "-" + DateUtil.thanTen(dayOfMonth);
                            Calendar endCalendar = Calendar.getInstance();
                            String[] endDate = mEndShareDate.getText().toString().split("-");
                            endCalendar.set(Integer.valueOf(endDate[0]), Integer.valueOf(endDate[1]), Integer.valueOf(endDate[2]));
                            if (mParkInfo != null) {
                                Calendar originStartCalendar = DateUtil.getYearToDayCalendar(mStartShareDate.getText().toString(), false);
                                if (originStartCalendar.compareTo(calendar) < 0) {
                                    //只有当想修改开始日期比原来的日期往后时才需要判断
                                    if (DateUtil.isInOrderDate(mStartShareDate.getText().toString() + " 00:00", startDate + " 00:00", mParkInfo.getOrder_times())) {
                                        //修改共享时间时如果修改的时间段内已被别人预约过则不能修改
                                        showFiveToast(mStartShareDate.getText().toString() + "至" + startDate + "这个时间段已被人预约了，不能修改哦");
                                        return;
                                    }
                                }
                            }

                            mStartShareDate.setText(startDate);

                            //如果共享的开始日期比结束日期大，则自动修改结束日期
                            if (calendar.compareTo(endCalendar) == 1) {
                                Calendar startCalendar = getYearToDayCalendar(startDate);
                                startCalendar.add(Calendar.MONTH, 1);
                                mEndShareDate.setText(DateUtil.getYearToDayFormat().format(new Date(startCalendar.getTimeInMillis())));
                                showFiveToast("已自动为你修改结束的共享时间");
                            }
                            checkPauseDate();
                        } else {
                            showFiveToast("开始共享的时间不能小于当天哦");
                        }
                    }
                }));
                break;
            case R.id.modify_share_time_end_share_time:
                showDatePicker("结束共享日期", new DateCheck(new OnDateCheckListener() {
                    @Override
                    public void onDateCheck(Calendar calendar, int year, int month, int dayOfMonth, int compareResult) {
                        if (compareResult > -1) {
                            String endDate = year + "-" + DateUtil.thanTen(month) + "-" + DateUtil.thanTen(dayOfMonth);
                            if (mParkInfo != null) {
                                Calendar originEndCalendar = Calendar.getInstance();
                                String[] originEndDate = mEndShareDate.getText().toString().split("-");
                                originEndCalendar.set(Integer.valueOf(originEndDate[0]), Integer.valueOf(originEndDate[1]), Integer.valueOf(originEndDate[2]));
                                if (originEndCalendar.compareTo(calendar) > 0) {
                                    //想把共享日期的结束时间修改为比原来的提前
                                    if (DateUtil.isInOrderDate(endDate + " 00:00", mEndShareDate.getText().toString() + " 24:00", mParkInfo.getOrder_times())) {
                                        showFiveToast(endDate + "至" + mEndShareDate.getText() + "这个时间段已被人预约了，不能修改哦");
                                    } else {
                                        mEndShareDate.setText(endDate);
                                        checkPauseDate();
                                    }
                                } else {
                                    mEndShareDate.setText(endDate);
                                }
                            } else {
                                mEndShareDate.setText(endDate);
                                checkPauseDate();
                            }
                        } else {
                            showFiveToast("要选择比开始共享之后的时间哦");
                        }
                    }
                }, mStartShareDate.getText().toString()));
                break;
            case R.id.modify_share_time_add_pause_date:
                if (mPauseShareDateAdapter.getData().size() >= 5) {
                    showFiveToast("最多只能选择5个暂停日期哦");
                } else {
                    showDatePicker("暂停共享日期", new DateCheck(new OnDateCheckListener() {
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

                                        if (mParkInfo != null) {
                                            if (isOrderInPauseDate(pausDate)) {
                                                showFiveToast(pausDate + "这天已被人预约了，不能暂停哦");
                                                return;
                                            }
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
                                        showFiveToast("暂停共享日期要在共享日期之内哦");
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

                            if (startTimeCalendar.compareTo(endTimeCalendar) >= 0) {
                                endTime = "2018-04-25 " + DateUtil.thanTen(Integer.valueOf(hourOfDay)) + ":" + DateUtil.thanTen(Integer.valueOf(minute));
                            }
                            shareTimeInfo.setEndDate(endTime);
                            if (isRepeat(shareTimeInfo, null)) {
                                showFiveToast("不能选择有重叠的时间哦");
                            } else {
                                if (mParkInfo != null) {
                                    String orderTime = isInNotShareTime(startTimeCalendar, endTimeCalendar);
                                    if (orderTime != null) {
                                        showFiveToast(orderTime + "已被人预约，不能中断哦");
                                        return;
                                    }
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
                if (mNewParkSpaceInfo == null) {
                    modifyShareTime();
                } else {
                    requestAddParkSpace();
                }
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
     * @return true(有已预约的订单在pauseDate这天)
     */
    private boolean isOrderInPauseDate(String pauseDate) {
        if (!mParkInfo.getOrder_times().equals("-1") && !mParkInfo.getOrder_times().equals("")) {
            for (String orderTime : mParkInfo.getOrder_times().split(",")) {
                if (DateUtil.isInPauseDate(orderTime.substring(0, orderTime.indexOf("*")),
                        orderTime.substring(orderTime.indexOf("*") + 1, orderTime.length()), pauseDate) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 修改了共享日期后检查暂停同享日期是否在共享日期内
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
     * 如果之前是全天共享的，现在加了共享时段，则判断那些不在共享时段内的是否已被预约
     *
     * @return null(不被共享的时间段内没有已预约订单)
     */
    private String isInNotShareTime(Calendar startCalendar, Calendar endCalendar) {
        Log.e(TAG, "onEndTime   startCalendar: " + DateUtil.getCalendarMonthToMinute(startCalendar));
        Log.e(TAG, "onEndTime   endCalendar: " + DateUtil.getCalendarMonthToMinute(endCalendar));
        if (mEverydayShareTimeAdapter.getData().isEmpty()) {
            String notShareTime;
            if (startCalendar.compareTo(endCalendar) > 0) {
                //08:00 - 03:00的则判断03:00到08:00这个时间段是否被预约
                notShareTime = DateUtil.getHourWithMinutes(endCalendar) + " - " + DateUtil.getHourWithMinutes(startCalendar);
                Log.e(TAG, "isInNotShareTime0: " + notShareTime);
                return DateUtil.isInShareTime(mParkInfo.getOrder_times(), notShareTime);
            } else {
                if (startCalendar.compareTo(DateUtil.getSpecialTodayStartCalendar()) == 0) {
                    //开始共享时间为凌晨的
                    notShareTime = DateUtil.getHourWithMinutes(endCalendar) + " - 24:00";
                    Log.e(TAG, "isInNotShareTime1 : " + notShareTime);
                    return DateUtil.isInShareTime(mParkInfo.getOrder_times(), notShareTime);
                } else {
                    String orderTime;
                    notShareTime = "00:00 - " + DateUtil.getHourWithMinutes(startCalendar);
                    Log.e(TAG, "isInNotShareTime3: " + notShareTime);
                    orderTime = DateUtil.isInShareTime(mParkInfo.getOrder_times(), notShareTime);
                    if (orderTime != null) {
                        return orderTime;
                    }

                    notShareTime = DateUtil.getHourWithMinutes(endCalendar) + " - 24:00";
                    Log.e(TAG, "isInNotShareTime3: " + notShareTime);
                    return DateUtil.isInShareTime(mParkInfo.getOrder_times(), notShareTime);
                }
            }
        }
        return null;
    }

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
                everyDayShareTime.append(getHourWithMinutes(everydayShareTimeInfo.getStartDate()));
                everyDayShareTime.append(" - ");
                everyDayShareTime.append(getHourWithMinutes(everydayShareTimeInfo.getEndDate()));
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
                                    showFiveToast("修改的时间段内已被预约");
                                    break;
                            }
                        }
                    }
                });
    }

    private void requestAddParkSpace() {
        showLoadingDialog("正在提交");
        String shareDate = mStartShareDate.getText().toString() + " - " + mEndShareDate.getText().toString();

        StringBuilder shareDay = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            if (mCheckTextViews[i].isChecked()) {
                shareDay.append("1");
            } else {
                shareDay.append("0");
            }
            shareDay.append(",");
        }
        shareDay.deleteCharAt(shareDay.length() - 1);

        StringBuilder pauseShareDate = new StringBuilder();
        if (!mPauseShareDateAdapter.getData().isEmpty()) {
            for (String pause : mPauseShareDateAdapter.getData()) {
                pauseShareDate.append(pause);
                pauseShareDate.append(",");
            }
            pauseShareDate.deleteCharAt(pauseShareDate.length() - 1);
        } else {
            pauseShareDate.append("-1");
        }

        StringBuilder everyDayShareTime = new StringBuilder();
        if (mNewParkSpaceInfo.isHourRent() && !mEverydayShareTimeAdapter.getData().isEmpty()) {
            for (EverydayShareTimeInfo everydayShareTimeInfo : mEverydayShareTimeAdapter.getData()) {
                everyDayShareTime.append(getHourWithMinutes(everydayShareTimeInfo.getStartDate()));
                everyDayShareTime.append(" - ");
                everyDayShareTime.append(getHourWithMinutes(everydayShareTimeInfo.getEndDate()));
                everyDayShareTime.append(",");
            }
            everyDayShareTime.deleteCharAt(everyDayShareTime.length() - 1);
        } else {
            everyDayShareTime.append("-1");
        }

        getOkGo(HttpConstants.addUserPark)
                .params("parkspace_id", mNewParkSpaceInfo.getParkspace_id())
                .params("citycode", mNewParkSpaceInfo.getCitycode())
                .params("address_memo", mNewParkSpaceInfo.getAddress_memo())
                .params("available_date", shareDate)
                .params("shareDay", shareDay.toString())
                .params("available_time", everyDayShareTime.toString())
                .params("pauseShareDate", pauseShareDate.toString())
                .params("applicant_name", mNewParkSpaceInfo.getApplicant_name())
                .params("install_time", mNewParkSpaceInfo.getInstall_time())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> responseData, Call call, Response response) {
                        dismmisLoadingDialog();
                        Intent intent = new Intent();
                        intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, true);
                        setResult(RESULT_OK, intent);
                        showFiveToast("提交成功");
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (!handleException(e)) {
                            if (e instanceof IllegalStateException) {
                                int code = Integer.parseInt(e.getMessage());
                                switch (code) {
                                    case 108:
                                        showFiveToast("添加失败");
                                        break;
                                    case 901:
                                        showFiveToast("服务器拥挤，请稍后重试");
                                        break;
                                }
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
     * @param contiue         ture:选择了时间之后再打开一次时间选择器
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
                    showTimePicker("结束时段", Integer.valueOf(mHours.get(options1)), Integer.valueOf(mMinutes.get(options1).get(option2)),
                            false, listener);
                } else {
                    if (hourOfDayParams == Integer.valueOf(mHours.get(options1)) && minuteParams == Integer.valueOf(mMinutes.get(options1).get(option2))) {
                        showFiveToast("全天共享不用设置哦");
                    } else {
                        listener.onEndTime(mHours.get(options1), mMinutes.get(options1).get(option2));
                    }
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
            hourPosition = mHours.indexOf(everydayShareTimeInfo.getStatTime().substring(0, everydayShareTimeInfo.getStatTime().indexOf(":")));
            minutesPosition = mMinutes.get(hourPosition).indexOf(everydayShareTimeInfo.getStatTime().substring(
                    everydayShareTimeInfo.getStatTime().indexOf(":") + 1, everydayShareTimeInfo.getStatTime().length()));
        } else {
            mTimeOption.setTitle("结束时段");
            hourPosition = mHours.indexOf(everydayShareTimeInfo.getEndTime().substring(0, everydayShareTimeInfo.getEndTime().indexOf(":")));
            minutesPosition = mMinutes.get(hourPosition).indexOf(everydayShareTimeInfo.getEndTime().substring(
                    everydayShareTimeInfo.getEndTime().indexOf(":") + 1, everydayShareTimeInfo.getEndTime().length()));
        }
        mTimeOption.setBtnSubmit("确定");
        mTimeOption.setAutoDismiss(true);
        mTimeOption.setSelectOptions(hourPosition, minutesPosition);
        mTimeOption.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                String seleteDate = "2018-04-24 " + DateUtil.thanTen(Integer.valueOf(mHours.get(options1))) + ":"
                        + DateUtil.thanTen(Integer.valueOf(mMinutes.get(options1).get(option2)));
                EverydayShareTimeInfo shareTimeInfo = new EverydayShareTimeInfo();

                if (isStartTime) {
                    shareTimeInfo.setStartDate(seleteDate);
                    shareTimeInfo.setEndDate(everydayShareTimeInfo.getEndDate());
                } else {
                    shareTimeInfo.setStartDate(everydayShareTimeInfo.getStartDate());
                    shareTimeInfo.setEndDate(seleteDate);
                }

                Calendar startTimeCalendar = DateUtil.getYearToMinuteCalendar(shareTimeInfo.getStartDate());
                Calendar endTimeCalendar = DateUtil.getYearToMinuteCalendar(shareTimeInfo.getEndDate());

                if (startTimeCalendar.compareTo(endTimeCalendar) >= 0) {
                    seleteDate = seleteDate.replaceFirst("04-24", "04-25");
                    if (isStartTime) {
                        shareTimeInfo.setStartDate(seleteDate);
                    } else {
                        shareTimeInfo.setEndDate(seleteDate);
                    }
                }

                if (isRepeat(shareTimeInfo, everydayShareTimeInfo)) {
                    showFiveToast("不能选择有重叠的时间哦");
                } else {
                    if (mParkInfo != null) {
                        String orderTime = isInNotShareTime(startTimeCalendar, endTimeCalendar);
                        if (orderTime != null) {
                            showFiveToast(orderTime + "已被人预约，不能中断哦");
                            return;
                        }
                    }

                    if (isStartTime) {
                        everydayShareTimeInfo.setStartDate(shareTimeInfo.getStartDate());
                    } else {
                        everydayShareTimeInfo.setEndDate(shareTimeInfo.getEndDate());
                    }

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
        mTimeOption.show();
    }

    private void changePauseDate(final String originPauseDate) {
        showDatePicker("暂停共享日期", new DateCheck(new OnDateCheckListener() {
            @Override
            public void onDateCheck(Calendar calendar, int year, int month, int dayOfMonth, int compareResult) {
                String pausDate = year + "-" + DateUtil.thanTen(month) + "-" + DateUtil.thanTen(dayOfMonth);
                if (mPauseShareDateAdapter.getData().contains(pausDate)) {
                    showFiveToast("已经有这个暂停共享日期了哦");
                } else {
                    Calendar startCalendar = getYearToDayCalendar(mStartShareDate.getText().toString());
                    Calendar endCalendar = getYearToDayCalendar(mEndShareDate.getText().toString());
                    Calendar pauseCalendar = getYearToDayCalendar(pausDate);
                    if (pauseCalendar.compareTo(startCalendar) >= 0 && pauseCalendar.compareTo(endCalendar) <= 0) {

                        if (mParkInfo != null) {
                            if (isOrderInPauseDate(pausDate)) {
                                showFiveToast(pausDate + "这天已被人预约了，不能暂停哦");
                                return;
                            }
                        }
                        int position = mPauseShareDateAdapter.getData().indexOf(originPauseDate);
                        mPauseShareDateAdapter.getData().set(position, pausDate);
                        Collections.sort(mPauseShareDateAdapter.getData(), new Comparator<String>() {
                            @Override
                            public int compare(String o1, String o2) {
                                return getYearToDayCalendar(o1).compareTo(getYearToDayCalendar(o2));
                            }
                        });
                        mPauseShareDateAdapter.notifyDataSetChanged();
                    } else {
                        showFiveToast("暂停共享日期要在共享日期之内哦");
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
                continue;
            }
            startCalendar = DateUtil.getYearToMinuteCalendar(timeInfo.getStartDate());
            endCalendar = DateUtil.getYearToMinuteCalendar(timeInfo.getEndDate());

            if (DateUtil.isIntersection(otherStartCalendar, otherEndCalendar, startCalendar, endCalendar)) {
                return true;
            }

            if (endCalendar.compareTo(DateUtil.getSpecialTodayEndCalendar()) >= 0) {
                //如果是跨天的则再比较第二天的时段是否有重复
                startCalendar = DateUtil.getSpecialTodayStartCalendar();
                endCalendar.add(Calendar.DAY_OF_MONTH, -1);
                if (DateUtil.isIntersection(otherStartCalendar, otherEndCalendar, startCalendar, endCalendar)) {
                    return true;
                }
            }

            if (otherEndCalendar.compareTo(DateUtil.getSpecialTodayEndCalendar()) >= 0) {
                otherStartCalendar = DateUtil.getSpecialTodayStartCalendar();
                otherEndCalendar.add(Calendar.DAY_OF_MONTH, -1);
                if (DateUtil.isIntersection(otherStartCalendar, otherEndCalendar, startCalendar, endCalendar)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getHourWithMinutes(String date) {
        return date.substring(date.indexOf(" ") + 1, date.length());
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
                    if (mParkInfo != null && mEverydayShareTimeAdapter.getData().size() > 1) {
                        String orderTime = DateUtil.isInShareTime(mParkInfo.getOrder_times(),
                                everydayShareTimeInfo.getStartDate().substring(
                                        everydayShareTimeInfo.getStartDate().indexOf(" ") + 1, everydayShareTimeInfo.getStartDate().length()) + " - "
                                        + everydayShareTimeInfo.getEndDate().substring(
                                        everydayShareTimeInfo.getEndDate().indexOf(" ") + 1, everydayShareTimeInfo.getEndDate().length()
                                ));
                        if (orderTime != null) {
                            showFiveToast(orderTime + "已被人预约了，不可取消哦");
                        } else {
                            notifyRemoveData(poisition);
                        }
                    } else {
                        notifyRemoveData(poisition);
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
