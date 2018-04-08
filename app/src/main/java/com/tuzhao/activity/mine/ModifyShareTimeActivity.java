package com.tuzhao.activity.mine;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.adapter.EverydayShareTimeAdapter;
import com.tuzhao.adapter.PauseShareDateAdapter;
import com.tuzhao.info.EverydayShareTimeInfo;
import com.tuzhao.publicwidget.others.CheckTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Created by juncoder on 2018/3/27.
 */

public class ModifyShareTimeActivity extends BaseStatusActivity implements View.OnClickListener {

    private TextView mStartShareDate;

    private TextView mEndShareDate;

    private CheckTextView mMondayShare;

    private CheckTextView mTuesdayShare;

    private CheckTextView mWednesdayShare;

    private CheckTextView mThursdayShare;

    private CheckTextView mFridayShare;

    private CheckTextView mSaturdayShare;

    private CheckTextView mSundayShare;

    private TextView mAddPauseShareDate;

    private TextView mAddEverydayShareTime;

    private PauseShareDateAdapter mPauseShareDateAdapter;

    private EverydayShareTimeAdapter mEverydayShareTimeAdapter;

    @Override
    protected int resourceId() {
        return R.layout.activity_modify_share_time;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mStartShareDate = findViewById(R.id.modify_share_time_start_share_time);
        mEndShareDate = findViewById(R.id.modify_share_time_end_share_time);
        mMondayShare = findViewById(R.id.modify_share_time_monday);
        mTuesdayShare = findViewById(R.id.modify_share_time_tuesday);
        mWednesdayShare = findViewById(R.id.modify_share_time_wednesday);
        mThursdayShare = findViewById(R.id.modify_share_time_thursday);
        mFridayShare = findViewById(R.id.modify_share_time_friday);
        mSaturdayShare = findViewById(R.id.modify_share_time_saturday);
        mSundayShare = findViewById(R.id.modify_share_time_sunday);
        mAddPauseShareDate = findViewById(R.id.modify_share_time_add_pause_date);
        mAddEverydayShareTime = findViewById(R.id.modify_share_time_add_everyday_share_time);
        RecyclerView pauseShareRecycerview = findViewById(R.id.modify_share_time_pause_date_rv);
        RecyclerView everydayShareRecycerview = findViewById(R.id.modify_share_time_everyday_share_ime_rv);
        pauseShareRecycerview.setNestedScrollingEnabled(false);
        everydayShareRecycerview.setNestedScrollingEnabled(false);

        pauseShareRecycerview.setLayoutManager(new LinearLayoutManager(this));
        everydayShareRecycerview.setLayoutManager(new LinearLayoutManager(this));
        mPauseShareDateAdapter = new PauseShareDateAdapter();
        mEverydayShareTimeAdapter = new EverydayShareTimeAdapter();
        pauseShareRecycerview.setAdapter(mPauseShareDateAdapter);
        everydayShareRecycerview.setAdapter(mEverydayShareTimeAdapter);
        mPauseShareDateAdapter.setChildClickListener(new BaseAdapter.OnChildClickListener() {
            @Override
            public void onChildClick(View view, int position) {
                Log.e(TAG, "onChildClick: " + position);
            }
        });

        mStartShareDate.setOnClickListener(this);
        mEndShareDate.setOnClickListener(this);
        mAddPauseShareDate.setOnClickListener(this);
        mAddEverydayShareTime.setOnClickListener(this);
        findViewById(R.id.modify_share_time_submit).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        // TODO: 2018/3/28 先获取网络上的数据设置好再取消加载对话框
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        mStartShareDate.setText(dateFormat.format(new Date(System.currentTimeMillis())));
        mEndShareDate.setText(dateFormat.format(new Date(System.currentTimeMillis())));
        dismmisLoadingDialog();
    }

    @NonNull
    @Override
    protected String title() {
        return "共享时间修改";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modify_share_time_start_share_time:
                showDatePicker(new DateCheck(new OnDateCheckListener() {
                    @Override
                    public void onDateCheck(Calendar calendar, int year, int month, int dayOfMonth, int compareResult) {
                        if (compareResult > -1) {
                            String startDate = year + "-" + month + "-" + dayOfMonth;
                            mStartShareDate.setText(startDate);

                            String[] endDate = mEndShareDate.getText().toString().split("-");
                            Calendar endCalendar = Calendar.getInstance();
                            endCalendar.set(Integer.valueOf(endDate[0]), Integer.valueOf(endDate[1]), Integer.valueOf(endDate[2]));
                            if (calendar.compareTo(endCalendar) == 1) {
                                mEndShareDate.setText(startDate);
                            }
                        } else {
                            Log.e(TAG, "onDateCheck: " );
                            showFiveToast("开始共享的时间不能小于当天哦");
                        }
                    }
                }));
                break;
            case R.id.modify_share_time_end_share_time:
                showDatePicker(new DateCheck(new OnDateCheckListener() {
                    @Override
                    public void onDateCheck(Calendar calendar, int year, int month, int dayOfMonth, int compareResult) {
                        if (compareResult > -1) {
                            String endDate = year + "-" + month + "-" + dayOfMonth;
                            mEndShareDate.setText(endDate);
                        } else {
                            showFiveToast("要选择比开始共享之后的时间哦");
                        }
                    }
                }, mStartShareDate.getText().toString()));
                break;
            case R.id.modify_share_time_add_pause_date:
                showDatePicker(new DateCheck(new OnDateCheckListener() {
                    @Override
                    public void onDateCheck(Calendar calendar, int year, int month, int dayOfMonth, int compareResult) {
                        if (compareResult > -1) {
                            String pausDate = year + "-" + month + "-" + dayOfMonth;
                            if (mPauseShareDateAdapter.getData().contains(pausDate)) {
                                showFiveToast("不能重复添加哦");
                            } else {
                                mPauseShareDateAdapter.justAddData(pausDate);
                                Collections.sort(mPauseShareDateAdapter.getData(), new Comparator<String>() {
                                    @Override
                                    public int compare(String o1, String o2) {
                                        return getDateCalendar(o1).compareTo(getDateCalendar(o2));
                                    }
                                });
                                mPauseShareDateAdapter.notifyDataSetChanged();
                            }
                        } else {
                            showFiveToast("要选择不小于当天的日期哦");
                        }
                    }
                }));
                break;
            case R.id.modify_share_time_add_everyday_share_time:
                if (mEverydayShareTimeAdapter.getData().size() == 3) {
                    showFiveToast("只能选择三个时段哦");
                } else {
                    Calendar calendar = Calendar.getInstance();
                    final EverydayShareTimeInfo shareTimeInfo = new EverydayShareTimeInfo();
                    showTimePicker(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true, new OnTimePickerListener() {
                        @Override
                        public void onStartTime(int hourOfDay, int minute) {
                            String startTime = hourOfDay + ":" + minute;
                            shareTimeInfo.setStartTime(startTime);
                        }

                        @Override
                        public void onEndTime(int hourOfDay, int minute) {
                            String endTime = hourOfDay + ":" + minute;
                            shareTimeInfo.setEndTime(endTime);
                            if (isRepeat(shareTimeInfo)) {
                                showFiveToast("不能选择有重叠的时间哦");
                            } else {
                                mEverydayShareTimeAdapter.addData(shareTimeInfo);
                            }
                        }
                    });
                }
                break;
            case R.id.modify_share_time_submit:
                Log.e(TAG, "one:" + mMondayShare.isChecked() + " tuesday:" + mTuesdayShare.isChecked() + " wednesday:" + mWednesdayShare.isChecked());
                finish();
                break;
        }
    }

    /**
     * 打开日期选择器
     *
     * @param dateCheck 日期的选择结果将回调到构造函数的接口方法中
     */
    private void showDatePicker(DateCheck dateCheck) {
        if (dateCheck.getYear() == 0) {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, dateCheck, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        } else {
            new DatePickerDialog(this, dateCheck, dateCheck.getYear(), dateCheck.getMonth(), dateCheck.getDayOfMonth()).show();
        }
    }

    /**
     * @param hourOfDayParams 打开时间选择器时默认显示的小时
     * @param minuteParams    默认显示的分钟
     * @param contiue         ture:选择了时间之后再打开一次时间选择器
     */
    private void showTimePicker(final int hourOfDayParams, final int minuteParams, final boolean contiue, final OnTimePickerListener listener) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (contiue) {
                    listener.onStartTime(hourOfDay, minute);
                    showTimePicker(hourOfDay + 1, minute, false, listener);
                } else {
                    if (getTimeCalendar(hourOfDayParams - 1 + ":" + minuteParams).compareTo(getTimeCalendar(hourOfDay + ":" + minute)) >= 0) {
                        showFiveToast("要选择大于开始的时间哦");
                    } else {
                        listener.onEndTime(hourOfDay, minute);
                    }
                }
            }
        }, hourOfDayParams, minuteParams, true);
        timePickerDialog.show();
    }

    /**
     * @param s 日期的字符串表示(2018-3-30)
     * @return 该日期对应的日历
     */
    private Calendar getDateCalendar(String s) {
        Calendar calendar = Calendar.getInstance();
        String[] date = s.split("-");
        calendar.set(Integer.valueOf(date[0]), Integer.valueOf(date[1]), Integer.valueOf(date[2]));
        return calendar;
    }

    /**
     * @param s 时间的字符串表示(15:14)
     * @return 该时间对应的日历
     */
    private Calendar getTimeCalendar(String s) {
        Calendar calendar = Calendar.getInstance();
        String[] time = s.split(":");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(time[1]));

        return calendar;
    }


    /**
     * @param shareTimeInfo 需要比较的时间段
     * @return ture:已经添加的时段中与需要比较的时间段有重叠
     */
    private boolean isRepeat(EverydayShareTimeInfo shareTimeInfo) {
        Calendar otherStartCalendar = getTimeCalendar(shareTimeInfo.getStartTime());
        Calendar otherEndCalendar = getTimeCalendar(shareTimeInfo.getEndTime());

        for (EverydayShareTimeInfo timeInfo : mEverydayShareTimeAdapter.getData()) {
            Calendar startCalendar = getTimeCalendar(timeInfo.getStartTime());
            Calendar endCalendar = getTimeCalendar(timeInfo.getEndTime());
            if (otherStartCalendar.compareTo(startCalendar) >= 0 && otherStartCalendar.compareTo(endCalendar) < 0) {
                return true;
            }
            if (otherEndCalendar.compareTo(startCalendar) > 0 && otherEndCalendar.compareTo(endCalendar) <= 0) {
                return true;
            }
        }
        return false;
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
            String[] date = dateText.split("-");
            mYear = Integer.parseInt(date[0]);
            mMonth = Integer.parseInt(date[1]);
            mDayOfMonth = Integer.parseInt(date[2]);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Calendar otherCanlendar = Calendar.getInstance();

            //如果输入指定的日期则与指定的日期进行比较，否则与当天日期进行比较
            if (mYear != 0) {
                otherCanlendar.set(Calendar.YEAR, mYear);
                otherCanlendar.set(Calendar.MONTH, mMonth);
                otherCanlendar.set(Calendar.DAY_OF_MONTH, mDayOfMonth);
            }
            mDateCheckListener.onDateCheck(calendar, year, month + 1, dayOfMonth, calendar.compareTo(otherCanlendar));
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
        void onStartTime(int hourOfDay, int minute);

        /**
         * 结束选择的时间
         */
        void onEndTime(int hourOfDay, int minute);
    }

}
