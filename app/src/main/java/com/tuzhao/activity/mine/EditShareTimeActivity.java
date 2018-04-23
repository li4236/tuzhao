package com.tuzhao.activity.mine;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.pickerview.OptionsPickerView;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.adapter.EverydayShareTimeAdapter;
import com.tuzhao.adapter.PauseShareDateAdapter;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.EverydayShareTimeInfo;
import com.tuzhao.info.NewParkSpaceInfo;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.ShareTimeInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.others.CheckTextView;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

    private EverydayShareTimeAdapter mEverydayShareTimeAdapter;

    private ArrayList<String> mYears;

    private ArrayList<ArrayList<String>> mMonths;

    private ArrayList<ArrayList<ArrayList<String>>> mDays;

    private OptionsPickerView<String> mDateOption;

    private ArrayList<String> mHours;

    private ArrayList<ArrayList<String>> mMinutes;

    private OptionsPickerView<String> mTimeOption;

    private NewParkSpaceInfo mParkSpaceInfo;

    private Park_Info mParkInfo;

    @Override
    protected int resourceId() {
        return R.layout.activity_modify_share_time;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        if ((mParkSpaceInfo = (NewParkSpaceInfo) getIntent().getSerializableExtra(ConstansUtil.ADD_PARK_SPACE_TEME)) == null) {

            if ((mParkInfo = (Park_Info) getIntent().getSerializableExtra(ConstansUtil.PARK_SPACE_INFO)) == null) {
                showFiveToast("打开失败，请返回重试");
                finish();
            }

        }

        mCheckTextViews = new CheckTextView[7];
        mStartShareDate = findViewById(R.id.modify_share_time_start_share_time);
        mEndShareDate = findViewById(R.id.modify_share_time_end_share_time);
        CheckTextView mMondayShare = findViewById(R.id.modify_share_time_monday);
        CheckTextView mTuesdayShare = findViewById(R.id.modify_share_time_tuesday);
        CheckTextView mWednesdayShare = findViewById(R.id.modify_share_time_wednesday);
        CheckTextView mThursdayShare = findViewById(R.id.modify_share_time_thursday);
        CheckTextView mFridayShare = findViewById(R.id.modify_share_time_friday);
        CheckTextView mSaturdayShare = findViewById(R.id.modify_share_time_saturday);
        CheckTextView mSundayShare = findViewById(R.id.modify_share_time_sunday);
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
        mEverydayShareTimeAdapter = new EverydayShareTimeAdapter();
        pauseShareRecycerview.setAdapter(mPauseShareDateAdapter);
        everydayShareRecycerview.setAdapter(mEverydayShareTimeAdapter);

        mStartShareDate.setOnClickListener(this);
        mEndShareDate.setOnClickListener(this);
        mAddPauseShareDate.setOnClickListener(this);
        mAddEverydayShareTime.setOnClickListener(this);
        findViewById(R.id.modify_share_time_submit).setOnClickListener(this);

        if (mParkSpaceInfo != null) {
            if (!mParkSpaceInfo.isHourRent()) {
                findViewById(R.id.modify_share_time_everyday_share_date).setVisibility(View.GONE);
            }
            for (CheckTextView checkTextView : mCheckTextViews) {
                checkTextView.setChecked(true);
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();

        if (mParkSpaceInfo == null) {
            //从车位设置跳转过来的则请求该车位的共享时间
            getOriginTime();
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
            dismmisLoadingDialog();
        }

        initDateOption();
        initTimeOption();
    }

    private void getOriginTime() {
        getOkGo(HttpConstants.getShareTime)
                .params("parkSpaceId", mParkInfo.getId())
                .params("cityCode", mParkInfo.getCitycode())
                .execute(new JsonCallback<Base_Class_Info<ShareTimeInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ShareTimeInfo> o, Call call, Response response) {
                        ShareTimeInfo shareTimeInfo = o.data;

                        String[] shareDate = shareTimeInfo.getShareDate().split(" - ");

                        mStartShareDate.setText(shareDate[0]);
                        mEndShareDate.setText(shareDate[1]);

                        String[] shareDay = shareTimeInfo.getShareDay().split(",");
                        for (int i = 0; i < shareDay.length; i++) {
                            if (shareDay[i].charAt(0) == '1') {
                                mCheckTextViews[i].setChecked(true);
                            }
                        }

                        if (shareTimeInfo.getPauseShareDate() != null && !shareTimeInfo.getPauseShareDate().equals("")) {
                            String[] pauseShareDate = shareTimeInfo.getPauseShareDate().split(",");
                            mPauseShareDateAdapter.addData(Arrays.asList(pauseShareDate));
                        }

                        String[] everyDayShareTime = shareTimeInfo.getEveryDayShareTime().split(",");
                        EverydayShareTimeInfo everydayShareTimeInfo;
                        int position;
                        for (String dayShareTime : everyDayShareTime) {
                            everydayShareTimeInfo = new EverydayShareTimeInfo();
                            position = dayShareTime.indexOf(" - ");
                            everydayShareTimeInfo.setStartTime(dayShareTime.substring(0, position));
                            everydayShareTimeInfo.setEndTime(dayShareTime.substring(position + 3, dayShareTime.length()));
                            mEverydayShareTimeAdapter.addData(everydayShareTimeInfo);
                        }
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast("获取共享时间失败，请稍后重试");
                            finish();
                        }
                    }
                });
    }

    @NonNull
    @Override
    protected String title() {
        return mParkSpaceInfo == null ? "共享时间修改" : "设置共享时间";
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
                            mStartShareDate.setText(startDate);

                            Calendar endCalendar = Calendar.getInstance();
                            if (!mEndShareDate.getText().toString().equals("")) {
                                String[] endDate = mEndShareDate.getText().toString().split("-");
                                endCalendar.set(Integer.valueOf(endDate[0]), Integer.valueOf(endDate[1]), Integer.valueOf(endDate[2]));
                            }

                            //如果共享的开始日期比结束日期大，则自动修改结束日期
                            if (calendar.compareTo(endCalendar) == 1) {
                                Calendar startCalend = getDateCalendar(startDate);
                                startCalend.add(Calendar.MONTH, 1);
                                mEndShareDate.setText(DateUtil.getYearToDayFormat().format(new Date(startCalend.getTimeInMillis())));
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
                            mEndShareDate.setText(endDate);
                            checkPauseDate();
                        } else {
                            showFiveToast("要选择比开始共享之后的时间哦");
                        }
                    }
                }, mStartShareDate.getText().toString()));
                break;
            case R.id.modify_share_time_add_pause_date:
                showDatePicker("暂停共享日期", new DateCheck(new OnDateCheckListener() {
                    @Override
                    public void onDateCheck(Calendar calendar, int year, int month, int dayOfMonth, int compareResult) {
                        if (compareResult > -1) {
                            String pausDate = year + "-" + DateUtil.thanTen(month) + "-" + DateUtil.thanTen(dayOfMonth);
                            if (mPauseShareDateAdapter.getData().contains(pausDate)) {
                                showFiveToast("不能重复添加哦");
                            } else {
                                Calendar startCalendar = getDateCalendar(mStartShareDate.getText().toString());
                                Calendar endCalendar = getDateCalendar(mEndShareDate.getText().toString());
                                Calendar pauseCalendar = getDateCalendar(pausDate);
                                if (pauseCalendar.compareTo(startCalendar) >= 0 && pauseCalendar.compareTo(endCalendar) <= 0) {
                                    mPauseShareDateAdapter.justAddData(pausDate);
                                    Collections.sort(mPauseShareDateAdapter.getData(), new Comparator<String>() {
                                        @Override
                                        public int compare(String o1, String o2) {
                                            return getDateCalendar(o1).compareTo(getDateCalendar(o2));
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
                            String startTime = DateUtil.thanTen(Integer.valueOf(hourOfDay)) + ":" + DateUtil.thanTen(Integer.valueOf(minute));
                            shareTimeInfo.setStartTime(startTime);
                        }

                        @Override
                        public void onEndTime(String hourOfDay, String minute) {
                            String endTime = DateUtil.thanTen(Integer.valueOf(hourOfDay)) + ":" + DateUtil.thanTen(Integer.valueOf(minute));
                            shareTimeInfo.setEndTime(endTime);
                            if (isRepeat(shareTimeInfo)) {
                                showFiveToast("不能选择有重叠的时间哦");
                            } else {
                                mEverydayShareTimeAdapter.justAddData(shareTimeInfo);
                                Collections.sort(mEverydayShareTimeAdapter.getData(), new Comparator<EverydayShareTimeInfo>() {
                                    @Override
                                    public int compare(EverydayShareTimeInfo o1, EverydayShareTimeInfo o2) {
                                        return getTimeCalendar(o1.getStartTime()).compareTo(getTimeCalendar(o2.getStartTime()));
                                    }
                                });
                                mEverydayShareTimeAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
                break;
            case R.id.modify_share_time_submit:
                if (mParkSpaceInfo == null) {
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

        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 2; i++) {
            mYears.add(String.valueOf(calendar.get(Calendar.YEAR) + i));
        }

        ArrayList<String> month;
        ArrayList<String> day;
        ArrayList<ArrayList<String>> monthWithDay;
        for (int i = 0; i < mYears.size(); i++) {
            month = new ArrayList<>();
            monthWithDay = new ArrayList<>();
            for (int j = 1; j <= 12; j++) {
                month.add(String.valueOf(j));

                calendar.set(Calendar.YEAR, Integer.valueOf(mYears.get(i)));
                calendar.set(Calendar.MONTH, j - 1);
                day = new ArrayList<>();
                for (int k = 1; k <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); k++) {
                    day.add(String.valueOf(k));
                }
                monthWithDay.add(day);
            }
            mMonths.add(month);
            mDays.add(monthWithDay);
        }

        mDateOption = new OptionsPickerView<>(this);
        mDateOption.setPicker(mYears, mMonths, mDays, true);
        mDateOption.setCyclic(false);
        mDateOption.setTextSize(16);
    }

    private void initTimeOption() {
        mHours = new ArrayList<>(24);
        mMinutes = new ArrayList<>(60);

        ArrayList<String> minutes;
        for (int i = 0; i < 24; i++) {
            mHours.add(String.valueOf(i));

            minutes = new ArrayList<>(60);
            for (int j = 0; j < 60; j++) {
                minutes.add(String.valueOf(j));
            }
            mMinutes.add(minutes);
        }

        mTimeOption = new OptionsPickerView<>(this);
        mTimeOption.setPicker(mHours, mMinutes, null, true);
        mTimeOption.setCyclic(true);
        mTimeOption.setTextSize(16);
    }

    /**
     * 修改了共享日期后检查暂停同享日期是否在共享日期内
     */
    private void checkPauseDate() {
        if (!mPauseShareDateAdapter.getData().isEmpty()) {
            Calendar startDate = getDateCalendar(mStartShareDate.getText().toString());
            Calendar endDate = getDateCalendar(mEndShareDate.getText().toString());
            List<String> legalData = new ArrayList<>();
            for (String s : mPauseShareDateAdapter.getData()) {
                Calendar calendar = getDateCalendar(s);
                if (startDate.compareTo(calendar) >= 0 && calendar.compareTo(endDate) <= 0) {
                    legalData.add(s);
                }
            }
            mPauseShareDateAdapter.setNewData(legalData);
        }
    }

    private void modifyShareTime() {
        showLoadingDialog("正在修改");
        final String shareDate = mStartShareDate + "," + mEndShareDate;

        final StringBuilder shareDay = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            if (mCheckTextViews[i].isChecked()) {
                shareDay.append("1");
            } else {
                shareDay.append("0");
            }
            shareDay.append(i + 1);
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
        }

        final StringBuilder everyDayShareTime = new StringBuilder();
        if (!mEverydayShareTimeAdapter.getData().isEmpty()) {
            for (EverydayShareTimeInfo everydayShareTimeInfo : mEverydayShareTimeAdapter.getData()) {
                everyDayShareTime.append(everydayShareTimeInfo.getStartTime());
                everyDayShareTime.append(" - ");
                everyDayShareTime.append(everydayShareTimeInfo.getEndTime());
                everyDayShareTime.append(",");
            }
            everyDayShareTime.deleteCharAt(everyDayShareTime.length() - 1);
        } else {
            everyDayShareTime.append("00:00 - 23:59");
        }

        getOkGo(HttpConstants.editShareTime)
                .params("cityCode",mParkInfo.getCitycode())
                .params("parkSpaceId", mParkInfo.getId())
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
                        intent.putExtra(ConstansUtil.FOR_REQUEST_RESULT, mParkInfo);
                        setResult(RESULT_OK, intent);
                        dismmisLoadingDialog();
                        showFiveToast("修改成功");
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast("修改失败" + e.getMessage());
                        }
                    }
                });
    }

    private void requestAddParkSpace() {

        showLoadingDialog("正在提交");
        String shareDate = mStartShareDate.getText().toString() + "," + mEndShareDate.getText().toString();

        StringBuilder shareDay = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            if (mCheckTextViews[i].isChecked()) {
                shareDay.append("1");
            } else {
                shareDay.append("0");
            }
            shareDay.append(i + 1);
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
        }

        StringBuilder everyDayShareTime = new StringBuilder();
        if (mParkSpaceInfo.isHourRent() && !mEverydayShareTimeAdapter.getData().isEmpty()) {
            for (EverydayShareTimeInfo everydayShareTimeInfo : mEverydayShareTimeAdapter.getData()) {
                everyDayShareTime.append(everydayShareTimeInfo.getStartTime());
                everyDayShareTime.append(" - ");
                everyDayShareTime.append(everydayShareTimeInfo.getEndTime());
                everyDayShareTime.append(",");
            }
            everyDayShareTime.deleteCharAt(everyDayShareTime.length() - 1);
        } else {
            everyDayShareTime.append("00:00 - 23:59");
        }

        getOkGo(HttpConstants.addUserPark)
                .params("parkspace_id", mParkSpaceInfo.getParkspace_id())
                .params("citycode", mParkSpaceInfo.getCitycode())
                .params("address_memo", mParkSpaceInfo.getAddress_memo())
                .params("available_date", shareDate)
                .params("shareDay", shareDay.toString())
                .params("available_time", everyDayShareTime.toString())
                .params("pauseShareDate", pauseShareDate.toString())
                .params("applicant_name", mParkSpaceInfo.getApplicant_name())
                .params("install_time", mParkSpaceInfo.getInstall_time())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> responseData, Call call, Response response) {
                        dismmisLoadingDialog();
                        Intent intent = new Intent();
                        intent.putExtra(ConstansUtil.FOR_REQUEST_RESULT, true);
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
        if (dateCheck.getYear() == 0) {
            int year = mYears.indexOf(String.valueOf(calendar.get(Calendar.YEAR)));
            int month = mMonths.get(year).indexOf(String.valueOf(calendar.get(Calendar.MONTH) + 1));
            int day = mDays.get(year).get(month).indexOf(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
            mDateOption.setSelectOptions(year, month, day);
        } else {
            int year = mYears.indexOf(String.valueOf(dateCheck.getYear()));
            int month = mMonths.get(year).indexOf(String.valueOf(dateCheck.getMonth()));
            int day = mDays.get(year).get(month).indexOf(String.valueOf(dateCheck.getDayOfMonth()));
            mDateOption.setSelectOptions(year, month, day);
        }
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
                    if (getTimeCalendar(hourOfDayParams + ":" + minuteParams).compareTo
                            (getTimeCalendar(mHours.get(options1) + ":" + mMinutes.get(options1).get(option2))) >= 0) {
                        showFiveToast("要选择大于开始的时间哦");
                    } else {
                        listener.onEndTime(mHours.get(options1), mMinutes.get(options1).get(option2));
                        mTimeOption.setAutoDismiss(true);
                        mTimeOption.dismiss();
                    }
                }
            }
        });
        mTimeOption.show();
    }

    /**
     * @param s 日期的字符串表示(2018-3-30)
     * @return 该日期对应的日历
     */
    private Calendar getDateCalendar(String s) {
        Calendar calendar = Calendar.getInstance();
        String[] date = s.split("-");
        calendar.set(Integer.valueOf(date[0]), Integer.valueOf(date[1]), Integer.valueOf(date[2]));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
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
            Calendar otherCanlendar = Calendar.getInstance();

            //如果输入指定的日期则与指定的日期进行比较，否则与当天日期进行比较
            if (mYear != 0) {
                otherCanlendar.set(Calendar.YEAR, mYear);
                otherCanlendar.set(Calendar.MONTH, mMonth);
                otherCanlendar.set(Calendar.DAY_OF_MONTH, mDayOfMonth);
            }
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