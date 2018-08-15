package com.tuzhao.fragment.applyParkSpaceProgress;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.pickerview.OptionsPickerView;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.info.EverydayShareTimeInfo;
import com.tuzhao.info.ShareTimeInfo;
import com.tuzhao.publicwidget.customView.CheckTextView;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.IntentObserable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by juncoder on 2018/6/13.
 */
public class TimeSettingFragment extends BaseStatusFragment implements View.OnClickListener {

    private TextView mStartShareDate;

    private TextView mEndShareDate;

    private ImageView mHourShareIv;

    private ImageView mDailyShareIv;

    private CheckTextView[] mCheckTextViews;

    private ConstraintLayout mShareTimeConstraintLayout;

    private PauseShareDateAdapter mPauseShareDateAdapter;

    private ShareTimeAdapter mEverydayShareTimeAdapter;

    private ArrayList<String> mYears;

    private ArrayList<ArrayList<String>> mMonths;

    private ArrayList<ArrayList<ArrayList<String>>> mDays;

    private OptionsPickerView<String> mDateOption;

    private ArrayList<String> mHours;

    private ArrayList<ArrayList<String>> mMinutes;

    private OptionsPickerView<String> mTimeOption;

    private ArrayList<String> mAppointmentDays;

    private ArrayList<ArrayList<String>> mAppointmentTimeFramne;

    private OptionsPickerView<String> mAppointmentOption;

    private TextView mChooseAppointmentTime;

    private ShareTimeInfo mShareTimeInfo;

    @Override
    protected int resourceId() {
        return R.layout.fragment_time_setting_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mCheckTextViews = new CheckTextView[7];
        mStartShareDate = view.findViewById(R.id.modify_share_time_start_share_time);
        mEndShareDate = view.findViewById(R.id.modify_share_time_end_share_time);
        mHourShareIv = view.findViewById(R.id.hour_share_iv);
        mDailyShareIv = view.findViewById(R.id.daily_share_iv);
        mShareTimeConstraintLayout = view.findViewById(R.id.modify_share_time_everyday_share_date);
        final CheckTextView mMondayShare = view.findViewById(R.id.modify_share_time_monday);
        final CheckTextView mTuesdayShare = view.findViewById(R.id.modify_share_time_tuesday);
        final CheckTextView mWednesdayShare = view.findViewById(R.id.modify_share_time_wednesday);
        final CheckTextView mThursdayShare = view.findViewById(R.id.modify_share_time_thursday);
        final CheckTextView mFridayShare = view.findViewById(R.id.modify_share_time_friday);
        final CheckTextView mSaturdayShare = view.findViewById(R.id.modify_share_time_saturday);
        final CheckTextView mSundayShare = view.findViewById(R.id.modify_share_time_sunday);
        TextView mAddPauseShareDate = view.findViewById(R.id.modify_share_time_add_pause_date);
        TextView mAddEverydayShareTime = view.findViewById(R.id.modify_share_time_add_everyday_share_time);
        RecyclerView pauseShareRecycerview = view.findViewById(R.id.modify_share_time_pause_date_rv);
        RecyclerView everydayShareRecycerview = view.findViewById(R.id.modify_share_time_everyday_share_ime_rv);
        mChooseAppointmentTime = view.findViewById(R.id.choose_appointment_time);

        mCheckTextViews[0] = mMondayShare;
        mCheckTextViews[1] = mTuesdayShare;
        mCheckTextViews[2] = mWednesdayShare;
        mCheckTextViews[3] = mThursdayShare;
        mCheckTextViews[4] = mFridayShare;
        mCheckTextViews[5] = mSaturdayShare;
        mCheckTextViews[6] = mSundayShare;

        pauseShareRecycerview.setNestedScrollingEnabled(false);
        everydayShareRecycerview.setNestedScrollingEnabled(false);

        pauseShareRecycerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        everydayShareRecycerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        mPauseShareDateAdapter = new PauseShareDateAdapter();
        mEverydayShareTimeAdapter = new ShareTimeAdapter();
        pauseShareRecycerview.setAdapter(mPauseShareDateAdapter);
        everydayShareRecycerview.setAdapter(mEverydayShareTimeAdapter);

        mStartShareDate.setOnClickListener(this);
        mEndShareDate.setOnClickListener(this);
        mAddPauseShareDate.setOnClickListener(this);
        mAddEverydayShareTime.setOnClickListener(this);
        mChooseAppointmentTime.setOnClickListener(this);
        view.findViewById(R.id.daily_share_cl).setOnClickListener(this);
        view.findViewById(R.id.hour_share_cl).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        if (getArguments() == null) {
            setArguments(new Bundle());
            mShareTimeInfo = new ShareTimeInfo();
            mShareTimeInfo.setHourRent(true);
            //共享日期显示为当天-之后一个月
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            date.setTime(calendar.getTimeInMillis());

            mStartShareDate.setText(DateUtil.getYearToDayFormat().format(date));

            calendar.add(Calendar.MONTH, 3);
            date.setTime(calendar.getTimeInMillis());
            mEndShareDate.setText(DateUtil.getYearToDayFormat().format(date));

            for (CheckTextView checkTextView : mCheckTextViews) {
                checkTextView.setChecked(true);
            }

        } else {
            mShareTimeInfo = getArguments().getParcelable(ConstansUtil.SHARE_TIME_INFO);
            initShareTime();
        }

        initDateOption();
        initTimeOption();
        initAppointmentOption();
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
                            mStartShareDate.setText(startDate);

                            //如果共享的开始日期比结束日期大，则自动修改结束日期
                            if (calendar.compareTo(endCalendar) >= 0) {
                                Calendar startCalendar = getYearToDayCalendar(startDate);
                                startCalendar.add(Calendar.MONTH, 3);
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
                            mEndShareDate.setText(endDate);
                            checkPauseDate();
                        } else {
                            showFiveToast("要选择比开始共享之后的时间哦");
                        }
                    }
                }, mStartShareDate.getText().toString()));
                break;
            case R.id.hour_share_cl:
                mHourShareIv.setImageResource(R.mipmap.ic_xuanzhong5);
                mDailyShareIv.setImageResource(R.mipmap.ic_weixuanzhong5);
                if (!isVisible(mShareTimeConstraintLayout)) {
                    mShareTimeConstraintLayout.setVisibility(View.VISIBLE);
                }
                mShareTimeInfo.setHourRent(true);
                break;
            case R.id.daily_share_cl:
                mHourShareIv.setImageResource(R.mipmap.ic_weixuanzhong5);
                mDailyShareIv.setImageResource(R.mipmap.ic_xuanzhong5);
                if (isVisible(mShareTimeConstraintLayout)) {
                    mShareTimeConstraintLayout.setVisibility(View.GONE);
                }
                mShareTimeInfo.setHourRent(false);
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
            case R.id.choose_appointment_time:
                mAppointmentOption.show();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Bundle bundle = getArguments();
        if (bundle != null) {
            saveShareTimeInfo();
            bundle.putParcelable(ConstansUtil.SHARE_TIME_INFO, mShareTimeInfo);
        }
    }

    private void saveShareTimeInfo() {
        mShareTimeInfo.setShareDate(getText(mStartShareDate) + " - " + getText(mEndShareDate));
        StringBuilder stringBuilder = new StringBuilder();
        for (CheckTextView checkTextView : mCheckTextViews) {
            if (checkTextView.isChecked()) {
                stringBuilder.append("1");
            } else {
                stringBuilder.append("0");
            }
            stringBuilder.append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        mShareTimeInfo.setShareDay(stringBuilder.toString());

        if (mPauseShareDateAdapter.getDataSize() == 0) {
            mShareTimeInfo.setPauseShareDate("-1");
        } else {
            StringBuilder pauseShareDate = new StringBuilder();
            for (String s : mPauseShareDateAdapter.getData()) {
                pauseShareDate.append(s);
                pauseShareDate.append(",");
            }
            pauseShareDate.deleteCharAt(pauseShareDate.length() - 1);
            mShareTimeInfo.setPauseShareDate(pauseShareDate.toString());
        }

        if (mShareTimeInfo.isHourRent() && mEverydayShareTimeAdapter.getDataSize() > 0) {
            StringBuilder everydayShareTime = new StringBuilder();
            for (EverydayShareTimeInfo shareTimeInfo : mEverydayShareTimeAdapter.getData()) {
                everydayShareTime.append(shareTimeInfo.getStartDate());
                everydayShareTime.append(" - ");
                everydayShareTime.append(shareTimeInfo.getEndDate());
                everydayShareTime.append(",");
            }
            everydayShareTime.deleteCharAt(everydayShareTime.length() - 1);
            mShareTimeInfo.setEveryDayShareTime(everydayShareTime.toString());
        } else {
            mShareTimeInfo.setEveryDayShareTime("-1");
        }

        mShareTimeInfo.setAppointmentDate(getText(mChooseAppointmentTime));
    }

    private void initDateOption() {
        mYears = new ArrayList<>();
        mMonths = new ArrayList<>();
        mDays = new ArrayList<>();
        DateUtil.initRecentTwoYear(mYears, mMonths, mDays);

        mDateOption = new OptionsPickerView<>(requireContext());
        mDateOption.setPicker(mYears, mMonths, mDays, true);
        mDateOption.setCyclic(false);
        mDateOption.setTextSize(16);
    }

    private void initTimeOption() {
        mHours = new ArrayList<>(24);
        mMinutes = new ArrayList<>(60);
        DateUtil.initHourWithMinute(mHours, mMinutes);

        mTimeOption = new OptionsPickerView<>(requireContext());
        mTimeOption.setPicker(mHours, mMinutes, null, true);
        mTimeOption.setCyclic(true);
        mTimeOption.setTextSize(16);
    }

    private void initAppointmentOption() {
        mAppointmentDays = new ArrayList<>(7);
        mAppointmentTimeFramne = new ArrayList<>(7);
        Calendar calendar = Calendar.getInstance();
        mAppointmentDays.add("明天");
        mAppointmentDays.add("后天");
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        for (int i = 1; i <= 5; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            mAppointmentDays.add(DateUtil.getCalendarMonthToDayWithText(calendar));
        }

        ArrayList<String> timeFrame;
        for (int i = 0; i < 7; i++) {
            timeFrame = new ArrayList<>(2);
            timeFrame.add("上午");
            timeFrame.add("下午");
            mAppointmentTimeFramne.add(timeFrame);
        }

        mAppointmentOption = new OptionsPickerView<>(requireContext());
        mAppointmentOption.setPicker(mAppointmentDays, mAppointmentTimeFramne, true);
        mAppointmentOption.setTextSize(16);
        mAppointmentOption.setCyclic(false);
        mAppointmentOption.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                mChooseAppointmentTime.setTextColor(Color.parseColor("#323232"));
                String chooseTime = mAppointmentDays.get(options1) + " " + mAppointmentTimeFramne.get(options1).get(option2);
                mChooseAppointmentTime.setText(chooseTime);
            }
        });
    }

    private void initShareTime() {
        mStartShareDate.setText(mShareTimeInfo.getShareDate().substring(0, mShareTimeInfo.getShareDate().indexOf(" - ")));
        mEndShareDate.setText(mShareTimeInfo.getShareDate().substring(mShareTimeInfo.getShareDate().indexOf(" - ") + 3, mShareTimeInfo.getShareDate().length()));
        if (!mShareTimeInfo.isHourRent()) {
            mHourShareIv.setImageResource(R.mipmap.ic_weixuanzhong5);
            mDailyShareIv.setImageResource(R.mipmap.ic_xuanzhong5);
            mShareTimeConstraintLayout.setVisibility(View.GONE);
        } else {
            if (!mShareTimeInfo.getEveryDayShareTime().equals("-1")) {
                String[] shareTime = mShareTimeInfo.getEveryDayShareTime().split(",");
                for (String s : shareTime) {
                    mEverydayShareTimeAdapter.addData(new EverydayShareTimeInfo(s.substring(0, s.indexOf(" - ")),
                            s.substring(s.indexOf(" - ") + 3, s.length())));
                }
            }
        }

        String[] shareDays = mShareTimeInfo.getShareDay().split(",");
        for (int i = 0; i < 7; i++) {
            if (shareDays[i].equals("1")) {
                mCheckTextViews[i].setChecked(true);
            } else {
                mCheckTextViews[i].setChecked(false);
            }
        }

        if (!mShareTimeInfo.getPauseShareDate().equals("-1")) {
            String[] pauseDate = mShareTimeInfo.getPauseShareDate().split(",");
            for (String date : pauseDate) {
                mPauseShareDateAdapter.addData(date);
            }
        }

        mChooseAppointmentTime.setTextColor(Color.parseColor("#323232"));
        mChooseAppointmentTime.setText(mShareTimeInfo.getAppointmentDate());
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

    public void verifyTime() {
        if (getText(mChooseAppointmentTime).startsWith("选择")) {
            showFiveToast("请选择安装师傅上门安装时间");
        } else {
            Intent intent = new Intent();
            intent.setAction(ConstansUtil.JUMP_TO_DEPOSIT_PAYMENT);
            IntentObserable.dispatch(intent);
        }
    }

    public ShareTimeInfo getShareTimeInfo() {
        saveShareTimeInfo();
        return mShareTimeInfo;
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
                    notifyRemoveData(poisition);
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
