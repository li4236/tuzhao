package com.tuzhao.fragment.parkorder;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.pickerview.OptionsPickerView;
import com.tuzhao.R;
import com.tuzhao.activity.BigPictureActivity;
import com.tuzhao.activity.jiguang_notification.MyReceiver;
import com.tuzhao.activity.jiguang_notification.OnLockListener;
import com.tuzhao.activity.mine.BillingRuleActivity;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.ShareTimeInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.BackPressedCallback;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.PollingUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/6/5.
 */
public class ParkingOrderFragment extends BaseStatusFragment implements View.OnClickListener, BackPressedCallback {

    private ParkOrderInfo mParkOrderInfo;

    private TextView mParkDate;

    private TextView mStartParkTime;

    private ImageView mRemainTimeIv;

    //private TextView mRemainTime;

    private TextView mParkSpaceLocation;

    private TextView mParkDuration;

    private TextView mLeaveTime;

    private TextView mOvertimeFee;

    private TextView mFinishPark;

    private DecimalFormat mDecimalFormat;

    private ArrayList<String> mParkSpacePictures;

    private CustomDialog mCustomDialog;

    private OptionsPickerView<String> mPickerView;

    private ArrayList<String> mDays;

    private ArrayList<ArrayList<String>> mHours;

    private ArrayList<ArrayList<ArrayList<String>>> mMinutes;

    private int mMaxExtendMinutes = 24 * 60 * 7 - 1;

    private ShareTimeInfo mShareTimeInfo;

    private Calendar mStartExtendCalendar;

    private PollingUtil mPollingUtil;

    private boolean mCanExtendsionTime;

    private OnLockListener mOnLockListener;

    public static ParkingOrderFragment newInstance(ParkOrderInfo parkOrderInfo) {
        ParkingOrderFragment fragment = new ParkingOrderFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstansUtil.PARK_ORDER_INFO, parkOrderInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int resourceId() {
        return R.layout.fragment_parking_order_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mParkOrderInfo = getArguments().getParcelable(ConstansUtil.PARK_ORDER_INFO);
        }

        mParkDate = view.findViewById(R.id.appointment_park_date);
        mStartParkTime = view.findViewById(R.id.appointment_income_time);
        mRemainTimeIv = view.findViewById(R.id.appointment_income_time_iv);
        //mRemainTime = view.findViewById(R.id.appointment_income_time_tv);
        mLeaveTime = view.findViewById(R.id.leave_time);
        mOvertimeFee = view.findViewById(R.id.overtime_fee);
        mParkSpaceLocation = view.findViewById(R.id.appointment_park_location);
        mParkDuration = view.findViewById(R.id.park_duration);
        mFinishPark = view.findViewById(R.id.finish_park);

        view.setOnClickListener(this);
        view.findViewById(R.id.appointment_calculate_rule).setOnClickListener(this);
        view.findViewById(R.id.appointment_calculate_rule_iv).setOnClickListener(this);
        view.findViewById(R.id.car_pic_cl).setOnClickListener(this);
        view.findViewById(R.id.cancel_appoint_cl).setOnClickListener(this);
        view.findViewById(R.id.contact_service_cl).setOnClickListener(this);
        view.findViewById(R.id.view_appointment_detail).setOnClickListener(this);
        view.findViewById(R.id.view_appointment_detail_iv).setOnClickListener(this);
        mFinishPark.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mDecimalFormat = new DecimalFormat("0.00");

        mParkDate.setText(DateUtil.getDayPointMinute(mParkOrderInfo.getPark_start_time()));
        mParkSpaceLocation.setText(mParkOrderInfo.getParkSpaceLocationDescribe());
        String leaveTime = "需在" + DateUtil.getYearToMinute(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getExtensionTime()) + "前离场";
        mLeaveTime.setText(leaveTime);
        String overtimeFee = "超时按" + mParkOrderInfo.getFine() + "/小时收费";
        mOvertimeFee.setText(overtimeFee);

        if (!mParkOrderInfo.getExtensionTime().equals("-1") && DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getExtensionTime()).compareTo(
                DateUtil.getYearToSecondCalendar(DateUtil.getCurrentYearToSecond())) > 0) {
            //未超时，获取车位共享时间
            getParkSpaceTime();
        }

        mPollingUtil = new PollingUtil(1000 * 60, new PollingUtil.OnTimeCallback() {
            @Override
            public void onTime() {
                calculateDuration();
            }
        });
        mPollingUtil.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPollingUtil.cancel();
        if (mOnLockListener != null) {
            MyReceiver.removeLockListener(mParkOrderInfo.getLockId());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appointment_calculate_rule:
            case R.id.appointment_calculate_rule_iv:
                Bundle bundle = new Bundle();
                bundle.putString(ConstansUtil.PARK_LOT_ID, mParkOrderInfo.getParkLotId());
                bundle.putString(ConstansUtil.CITY_CODE, mParkOrderInfo.getCitycode());
                startActivity(BillingRuleActivity.class, bundle);
                break;
            case R.id.car_pic_cl:
                if (mParkOrderInfo.getPictures() == null || mParkOrderInfo.getPictures().equals("-1")) {
                    showFiveToast("暂无车位图片");
                } else {
                    showParkSpacePic();
                }
                break;
            case R.id.cancel_appoint_cl:
                if (mParkOrderInfo.getExtensionTime().equals("-1")) {
                    showFiveToast("只能延长一次时间哦");
                } else if (DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getExtensionTime()).compareTo(
                        DateUtil.getYearToSecondCalendar(DateUtil.getCurrentYearToSecond())) <= 0) {
                    showFiveToast("您已超时，不可延长时间");
                } else if (!mCanExtendsionTime) {
                    showFiveToast("暂无可延长时间");
                } else {
                    showOptionPicker();
                }
                break;
            case R.id.contact_service_cl:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:4006505058"));
                startActivity(intent);
                break;
            case R.id.view_appointment_detail:
            case R.id.view_appointment_detail_iv:
                showAppointmentDetail();
                break;
            case R.id.finish_park:
                showLoadingDialog("正在结单...");
                closeParkLock();
                mFinishPark.setClickable(false);
                break;
        }
    }

    private void showParkSpacePic() {
        if (mParkSpacePictures == null) {
            mParkSpacePictures = new ArrayList<>();
            String[] pictures = mParkOrderInfo.getPictures().split(",");
            for (String picture : pictures) {
                if (!picture.equals("-1")) {
                    mParkSpacePictures.add(HttpConstants.ROOT_IMG_URL_PS + picture);
                }
            }
        }
        Intent intent = new Intent(getActivity(), BigPictureActivity.class);
        intent.putStringArrayListExtra("picture_list", mParkSpacePictures);
        startActivity(intent);
    }

    private void showAppointmentDetail() {
        if (mCustomDialog != null) {
            mCustomDialog.dismiss();
            mCustomDialog = null;
        }
        mCustomDialog = new CustomDialog(requireContext(), mParkOrderInfo, 0);
        mCustomDialog.show();
    }

    private void calculateDuration() {
        if (DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getExtensionTime()).compareTo(
                DateUtil.getYearToSecondCalendar(DateUtil.getCurrentYearToSecond())) < 0) {
            mStartParkTime.setText(DateUtil.getDistanceForHourMinuteAddStart(mParkOrderInfo.getOrder_endtime(), DateUtil.getCurrentYearToSecond(), mParkOrderInfo.getExtensionTime()));
            ImageUtil.showPic(mRemainTimeIv, R.drawable.ic_overtime);
            //超时了
          /*  if (!getText(mRemainTime).equals("（已超时）")) {
                mRemainTime.setText("（已超时）");
            }*/
            if (mCanExtendsionTime) {
                mCanExtendsionTime = false;
            }

            Calendar calendar = DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_endtime());
            calendar.add(Calendar.SECOND, Integer.valueOf(mParkOrderInfo.getExtensionTime()));

            double parkFee = DateUtil.caculateParkFee(DateUtil.deleteSecond(mParkOrderInfo.getOrder_starttime()), DateUtil.getCalenarYearToMinutes(calendar),
                    mParkOrderInfo.getHigh_time(), Double.valueOf(mParkOrderInfo.getHigh_fee()), Double.valueOf(mParkOrderInfo.getLow_fee()));

            long overTimeMinutes = DateUtil.getCalendarDistance(calendar, Calendar.getInstance());
            parkFee += Double.valueOf(mParkOrderInfo.getFine()) / 60.0 * overTimeMinutes;

            mParkDuration.setText(String.valueOf(mDecimalFormat.format(parkFee)));
        } else if (DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_endtime()).compareTo(
                DateUtil.getYearToSecondCalendar(DateUtil.getCurrentYearToSecond())) < 0
                && DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getExtensionTime()).compareTo(
                DateUtil.getYearToSecondCalendar(DateUtil.getCurrentYearToSecond())) > 0) {
            //在顺延时间内
            mStartParkTime.setText(DateUtil.getDistanceForDayHourMinuteAddEnd(DateUtil.getCurrentYearToSecond(),
                    mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getExtensionTime()));
            ImageUtil.showPic(mRemainTimeIv, R.drawable.ic_shunyan);
            /*if (!getText(mRemainTime).equals("（剩余宽限时长）")) {
                mRemainTime.setText("（剩余宽限时长）");
            }*/
            mParkDuration.setText(mDecimalFormat.format(DateUtil.caculateParkFee(DateUtil.deleteSecond(mParkOrderInfo.getOrder_starttime()), DateUtil.deleteSecond(DateUtil.getCurrentYearToSecond()),
                    mParkOrderInfo.getHigh_time(), Double.valueOf(mParkOrderInfo.getHigh_fee()), Double.valueOf(mParkOrderInfo.getLow_fee()))));
        } else {
            //未到顺延时间
            mStartParkTime.setText(DateUtil.getDistanceForDayHourMinute(DateUtil.getCurrentYearToSecond(), mParkOrderInfo.getOrder_endtime()));
            mParkDuration.setText(mDecimalFormat.format(DateUtil.caculateParkFee(DateUtil.deleteSecond(mParkOrderInfo.getOrder_starttime()), DateUtil.deleteSecond(DateUtil.getCurrentYearToSecond()),
                    mParkOrderInfo.getHigh_time(), Double.valueOf(mParkOrderInfo.getHigh_fee()), Double.valueOf(mParkOrderInfo.getLow_fee()))));
        }

    }

    private void calculateMaxMinutes() {
        if (mShareTimeInfo != null) {
            if (!mShareTimeInfo.getOrderTime().equals("-1")) {
                //根据该车位被预约的时间计算能延长的最大时间
                mMaxExtendMinutes = Math.min(mMaxExtendMinutes, DateUtil.getDistanceOfRecentOrder("0", mParkOrderInfo.getOrder_endtime(),
                        mShareTimeInfo.getOrderTime()));
            }

            Calendar nowCalendar = Calendar.getInstance();
            nowCalendar.set(Calendar.SECOND, 0);
            nowCalendar.set(Calendar.MILLISECOND, 0);

            mStartExtendCalendar = DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_endtime());

            //按共享日期的最后一天的最后时刻来计算延长的时间
            //现在是06-08 08:58，共享日期为2018-05-08 - 2018-06-08,则最大延长时间为现在到今天的最后一刻
            Calendar calendar = DateUtil.getYearToDayCalendar(mShareTimeInfo.getShareDate().substring(
                    mShareTimeInfo.getShareDate().indexOf(" - ") + 3, mShareTimeInfo.getShareDate().length()), false);
            calendar.set(Calendar.HOUR_OF_DAY, 24);
            calendar.set(Calendar.MINUTE, 0);
            mMaxExtendMinutes = Math.min(mMaxExtendMinutes, (int) DateUtil.getCalendarDistance(mStartExtendCalendar, calendar));

            //按每周共享时间来计算延长时间
            nowCalendar = (Calendar) mStartExtendCalendar.clone();
            nowCalendar.add(Calendar.DAY_OF_MONTH, 1);
            String[] days = mShareTimeInfo.getShareDay().split(",");
            for (int i = 0; i < 6; i++) {
                if (!days[DateUtil.getDayOfWeek(nowCalendar) - 1].equals("1")) {
                    //明天不共享则最长的延长时间为到今晚
                    nowCalendar.add(Calendar.DAY_OF_MONTH, -1);
                    nowCalendar.set(Calendar.HOUR_OF_DAY, 24);
                    nowCalendar.set(Calendar.MINUTE, 0);
                    break;
                } else {
                    nowCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }
            }
            mMaxExtendMinutes = Math.min(mMaxExtendMinutes, (int) DateUtil.getCalendarDistance(mStartExtendCalendar, nowCalendar));

            nowCalendar = (Calendar) mStartExtendCalendar.clone();

            if (!mShareTimeInfo.getPauseShareDate().equals("-1")) {
                List<Calendar> pauseDateCalendars = new ArrayList<>();
                Calendar pauseCalendar;
                for (String pauseDate : mShareTimeInfo.getPauseShareDate().split(",")) {
                    pauseCalendar = DateUtil.getYearToDayCalendar(pauseDate, false);
                    if (pauseCalendar.compareTo(nowCalendar) >= 0) {
                        //保存在开始延长时间之后的暂停共享日期
                        pauseDateCalendars.add(DateUtil.getYearToDayCalendar(pauseDate, false));
                    }
                }

                if (!pauseDateCalendars.isEmpty()) {
                    //从小到大排序
                    Collections.sort(pauseDateCalendars, new Comparator<Calendar>() {
                        @Override
                        public int compare(Calendar o1, Calendar o2) {
                            return o1.compareTo(o2);
                        }
                    });

                    //算出到最近那天暂停共享的那天的时间距离
                    mMaxExtendMinutes = (int) Math.min(mMaxExtendMinutes, DateUtil.getCalendarDistance(nowCalendar, pauseDateCalendars.get(0)));
                }
            }

            //获取在共享时间段内的最大可延长时间
            mMaxExtendMinutes = Math.min(mMaxExtendMinutes, DateUtil.getDistanceForRecentShareTime(mStartExtendCalendar, mShareTimeInfo.getEveryDayShareTime()));
        }
        mCanExtendsionTime = mMaxExtendMinutes > Integer.valueOf(mParkOrderInfo.getExtensionTime()) / 60;
    }

    private void initOptionPickerData() {
        mDays = new ArrayList<>();
        mHours = new ArrayList<>();
        mMinutes = new ArrayList<>();
        int day = mMaxExtendMinutes / 60 / 24;
        int extendsionTime = Integer.valueOf(mParkOrderInfo.getExtensionTime()) / 60;
        int hour;

        ArrayList<String> hours;
        ArrayList<String> minutes;
        ArrayList<ArrayList<String>> hourWithMinutes;
        for (int l = 0; l <= day; l++) {
            mDays.add(String.valueOf(l));

            hours = new ArrayList<>();
            hourWithMinutes = new ArrayList<>();
            if (l == day) {
                //最后那天的小时为总分钟数减去之前天数的小时除以60
                hour = (mMaxExtendMinutes - 60 * 24 * l) / 60;
            } else {
                hour = 23;
            }

            for (int i = 0; i <= hour; i++) {
                if (extendsionTime / 60 > i) {
                    continue;
                }
                hours.add(String.valueOf(i));
                minutes = new ArrayList<>();

                int startExtendsionTime = extendsionTime - (l * 24 * 60 + i * 60);
                if (l == day && i == hour) {
                    for (int j = 0, k = (mMaxExtendMinutes - 60 * 24 * l) - 60 * hour; j <= k; j++) {
                        if (j > startExtendsionTime) {
                            minutes.add(String.valueOf(j));
                        }
                    }
                } else {
                    for (int j = i == 0 && l == 0 ? 1 : 0; j < 60; j++) {
                        if (j > startExtendsionTime) {
                            minutes.add(String.valueOf(j));
                        }
                    }
                }
                hourWithMinutes.add(minutes);
            }
            mHours.add(hours);
            mMinutes.add(hourWithMinutes);
        }

    }

    private void initOptionPicker() {
        mPickerView = new OptionsPickerView<>(getContext());
        mPickerView.setPicker(mDays, mHours, mMinutes, true);
        mPickerView.setLabels("天", "小时", "分钟");
        mPickerView.setTitle("延长时间");
        mPickerView.setTextSize(16);
        mPickerView.setCyclic(false);
        mPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                final Calendar calendar = (Calendar) mStartExtendCalendar.clone();
                calendar.add(Calendar.DAY_OF_MONTH, Integer.valueOf(mDays.get(options1)));
                calendar.add(Calendar.HOUR_OF_DAY, Integer.valueOf(mHours.get(options1).get(option2)));
                calendar.add(Calendar.MINUTE, Integer.valueOf(mMinutes.get(options1).get(option2).get(options3)));
                String message;
                if (DateUtil.isInSameDay(mStartExtendCalendar, calendar)) {
                    message = "停车时间将延长至 今天" + DateUtil.getHourWithMinutes(calendar);
                } else {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    if (DateUtil.isInSameDay(mStartExtendCalendar, calendar)) {
                        message = "停车时间将延长至 明天" + DateUtil.getHourWithMinutes(calendar);
                    } else {
                        calendar.add(Calendar.DAY_OF_MONTH, -1);
                        message = "停车时间将延长至 " + DateUtil.getCalendarMonthToMinute(calendar);
                    }
                }
                SpannableString spannableString = new SpannableString(message);
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#808080")), 0, message.indexOf(" "), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                TipeDialog tipeDialog = new TipeDialog.Builder(getContext())
                        .setTitle("延长确认")
                        .setMessage(spannableString)
                        .setPositiveButton("确认延长", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                extendParkingTime(calendar);
                            }
                        })
                        .create();
                tipeDialog.show();
            }
        });
    }

    private void showOptionPicker() {
        if (mDays == null) {
            initOptionPickerData();
        }
        if (mPickerView == null) {
            initOptionPicker();
        }
        mPickerView.show();
    }

    private void getParkSpaceTime() {
        getOkGo(HttpConstants.getParkSpaceTime)
                .params("parkSpaceId", mParkOrderInfo.getParkSpaceid())
                .params("cityCode", mParkOrderInfo.getCitycode())
                .execute(new JsonCallback<Base_Class_Info<ShareTimeInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ShareTimeInfo> o, Call call, Response response) {
                        mShareTimeInfo = o.data;
                        calculateMaxMinutes();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {

                        }
                    }
                });
    }

    private void extendParkingTime(final Calendar parkEndCalendar) {
        showLoadingDialog("正在延长...");
        getOkGo(HttpConstants.extendParkingTime)
                .params("orderId", mParkOrderInfo.getId())
                .params("extendTime", DateUtil.getCalendarDistance(mStartExtendCalendar, parkEndCalendar))
                .params("cityCode", mParkOrderInfo.getCitycode())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        mParkOrderInfo.setOrder_endtime(DateUtil.getCalenarYearToSecond(parkEndCalendar));
                        mParkOrderInfo.setExtensionTime("-1");
                        String leaveTime = "需在" + DateUtil.getYearToMinute(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getExtensionTime()) + "前离场";
                        mLeaveTime.setText(leaveTime);
                        mCanExtendsionTime = false;
                        calculateDuration();

                        Intent intent = new Intent(ConstansUtil.CHANGE_PARK_ORDER_INRO);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
                        intent.putExtra(ConstansUtil.FOR_REQUEST_RESULT, bundle);
                        IntentObserable.dispatch(intent);
                        showFiveToast("延长时间成功");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                case "102":
                                    showFiveToast("数据异常，请稍后重试");
                                    finish();
                                    break;
                                case "103":
                                    showFiveToast("您选择的时间段与其他人冲突");
                                    break;
                                case "104":
                                    showFiveToast("只能延长一次哦");
                                    break;
                                case "105":
                                    showFiveToast("只有停车中的订单才可以延长时间");
                                    break;
                                case "106":
                                    showFiveToast("选择的时间太短");
                                    break;
                                case "107":
                                    showFiveToast("已经超时的订单不可以延长时间");
                                    break;
                                case "109":
                                case "110":
                                case "111":
                                    showFiveToast("所延长的时间车位暂不开放");
                                    break;
                                default:
                                    showFiveToast("服务器异常，请稍后再试");
                                    break;
                            }
                        }
                    }
                });
    }

    private void getParkLockStatus() {
        showLoadingDialog("正在查询");
        getOkGo(HttpConstants.selectParkState)
                .params("device_name", "")
                .params("select_type", 2)
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dismmisLoadingDialog();
                        showFiveToast("结束停车失败，请联系客服");
                    }
                });
    }

    private void closeParkLock() {
        initLockListener();
        getOkGo(HttpConstants.controlParkLock)
                .params("citycode", mParkOrderInfo.getCitycode())
                .params("order_id", mParkOrderInfo.getId())
                .params("ctrl_type", 2)
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        mFinishPark.setClickable(true);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mFinishPark.setClickable(true);
                        if (!handleException(e)) {

                        }
                    }
                });
    }

    private void initLockListener() {
        if (mOnLockListener == null) {
            mOnLockListener = new OnLockListener() {
                @Override
                public void openSuccess() {

                }

                @Override
                public void openFailed() {

                }

                @Override
                public void openSuccessHaveCar() {

                }

                @Override
                public void closeSuccess() {
                    finishPark();
                }

                @Override
                public void closeFailed() {
                    dismmisLoadingDialog();
                    showFiveToast("关锁失败，请稍后重试或联系客服");
                }

                @Override
                public void closeFailedHaveCar() {
                    dismmisLoadingDialog();
                    showFiveToast("请先把车移除车位再结单哦");
                }

            };
            MyReceiver.addLockListener(mParkOrderInfo.getLockId(), mOnLockListener);
        }
    }

    private void getOrderStatus() {
        getOkGo(HttpConstants.getDetailOfParkOrder)
                .params("citycode", mParkOrderInfo.getCitycode())
                .params("order_number", mParkOrderInfo.getOrder_number())
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> o, Call call, Response response) {
                        if (o.data.getOrder_status().equals("2")) {
                            closeParkLock();
                        } else if (o.data.getOrder_status().equals("3")) {
                            notifyFinishPark(o.data);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {

                        }
                    }
                });
    }

    private void finishPark() {
        //请求改变订单状态，完成订单
        getOkGo(HttpConstants.endParking)
                .params("order_id", mParkOrderInfo.getId())
                .params("citycode", mParkOrderInfo.getCitycode())
                .params("pass_code", DensityUtil.MD5code(UserManager.getInstance().getUserInfo().getSerect_code() + "*&*" + UserManager.getInstance().getUserInfo().getCreate_time() + "*&*" + UserManager.getInstance().getUserInfo().getId()))
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> info, Call call, Response response) {
                        mParkOrderInfo.setOrder_status("3");
                        mParkOrderInfo.setOrder_fee(info.data.getOrder_fee());
                        mParkOrderInfo.setFine_fee(info.data.getFine_fee());
                        mParkOrderInfo.setPark_end_time(info.data.getPark_end_time());
                        notifyFinishPark(mParkOrderInfo);
                        showFiveToast("已结束停车");
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "103":
                                    showFiveToast("该订单不存在");
                                    break;
                                default:
                                    showFiveToast("服务器异常，请稍后重试");
                                    break;
                            }
                        }
                    }
                });
    }

    private void notifyFinishPark(ParkOrderInfo parkOrderInfo) {
        Intent intent = new Intent();
        intent.setAction(ConstansUtil.FINISH_PARK);
        Bundle bundle = new Bundle();
        mParkOrderInfo.setOrder_status("3");
        bundle.putParcelable(ConstansUtil.PARK_ORDER_INFO, parkOrderInfo);
        intent.putExtra(ConstansUtil.FOR_REQUEST_RESULT, bundle);
        IntentObserable.dispatch(intent);
    }

    @Override
    public boolean hanleBackPressed() {
        if (mPickerView != null && mPickerView.isShowing()) {
            mPickerView.dismiss();
            return true;
        }
        return false;
    }

}
