package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.tianzhili.www.myselfsdk.pickerview.OptionsPickerView;
import com.tuzhao.R;
import com.tuzhao.activity.SearchAddressActivity;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Car;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.LocationManager;
import com.tuzhao.publicmanager.TimeManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/8/29.
 * <p>
 * 预约亲友分享给我的车位前的筛选车位
 * </p>
 */
public class AppointmentParkSpaceActivity extends BaseStatusActivity implements View.OnClickListener, IntentObserver {

    private static final int APPOINTMENT_PARK_SAPCE_CODE = 0x123;

    private static final int SELECTE_DESTINATION_CODE = 0x456;

    private TextView mCarNumber;

    private TextView mAppointmentIncomeTime;

    private TextView mParkDurationTv;

    private TextView mDestination;

    private TextView mNextStep;

    private ArrayList<Car> mCars;

    /**
     * 获得分享的车位
     */
    private List<Park_Info> mParkInfos;

    /**
     * 正在预约或停车的订单
     */
    private List<ParkOrderInfo> mParkOrderInfos;

    private List<Park_Info> mCanParkList;

    //预约开始和结束的停车时间
    private String mAppointmentStartTime = "", mAppointmentEndTime = "";

    //预约停车时长（分钟）
    private int mParkDuration = 0;

    private Calendar mCalendar;

    private ArrayList<String> mDays;

    private ArrayList<ArrayList<String>> mHours;

    private ArrayList<ArrayList<ArrayList<String>>> mMinutes;

    private OptionsPickerView<String> mStartTimeOption;

    private ArrayList<String> mDurationHours = new ArrayList<>();

    private ArrayList<ArrayList<String>> mDurationMinutes = new ArrayList<>();

    //停车时长选择器
    private OptionsPickerView<String> mParkDurationOption;

    private double mLatitude;

    private double mLongitude;

    private boolean mIsBookSpecificParkSpace;

    @Override
    protected int resourceId() {
        return R.layout.activity_appointment_park_space_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if (getIntent().hasExtra(ConstansUtil.PARK_SPACE_INFO)) {
            //从指定的亲友车位跳过来的，只能预约这一个车位
            mParkInfos = new ArrayList<>(1);
            mParkInfos.add((Park_Info) getIntent().getParcelableExtra(ConstansUtil.PARK_SPACE_INFO));
            mIsBookSpecificParkSpace = true;
        }
        mCarNumber = findViewById(R.id.car_number);
        mAppointmentIncomeTime = findViewById(R.id.appointment_income_time);
        mParkDurationTv = findViewById(R.id.grace_time);
        mDestination = findViewById(R.id.destination);
        mNextStep = findViewById(R.id.next_step_tv);

        TextView appointmentHint = findViewById(R.id.appointment_hint);
        String hint = "温馨提示：\n请在规定时间前进场，在规定时间离场\n如不方便可延长停车时间，最迟不得超过选定车位的规定时间";
        appointmentHint.setText(hint);

        findViewById(R.id.car_number_tv).setOnClickListener(this);
        findViewById(R.id.appointment_income_time_tv).setOnClickListener(this);
        findViewById(R.id.grace_time_tv).setOnClickListener(this);
        findViewById(R.id.destination_tv).setOnClickListener(this);
        mNextStep.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        if (!mIsBookSpecificParkSpace) {
            mParkInfos = new ArrayList<>();
            getFriendShareParkspace();
        } else {
            mDestination.setVisibility(View.GONE);
            findViewById(R.id.destination_tv).setVisibility(View.GONE);
            findViewById(R.id.destination_copy_tv).setVisibility(View.GONE);
            findViewById(R.id.destination_av).setVisibility(View.GONE);
            mNextStep.setText("预约车位");
        }
        mParkOrderInfos = new ArrayList<>();
        mCanParkList = new LinkedList<>();
        getUserParkOrderForAppoint();
        IntentObserable.registerObserver(this);
    }

    @NonNull
    @Override
    protected String title() {
        return "预约车位";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.car_number_tv:
                //选择车辆
                Intent intent = new Intent(AppointmentParkSpaceActivity.this, MyCarActivity.class);
                intent.putExtra(ConstansUtil.INTENT_MESSAGE, true);
                if (mCars != null) {
                    intent.putExtra(ConstansUtil.CAR_NUMBER, mCars);
                }
                startActivityForResult(intent, ConstansUtil.REQUSET_CODE);
                break;
            case R.id.appointment_income_time_tv:
                showAppointmentIncomeTimeOptions();
                break;
            case R.id.grace_time_tv:
                showParkDurationOptions();
                break;
            case R.id.destination_tv:
                startActivityForResult(SearchAddressActivity.class, SELECTE_DESTINATION_CODE, "whatPage", "3");
                break;
            case R.id.next_step_tv:
                if (TextUtils.isEmpty(getText(mCarNumber))) {
                    showFiveToast("请选择要停车辆");
                } else if (mAppointmentStartTime.equals("")) {
                    showFiveToast("请选择入场时间");
                } else if (mParkDuration == 0) {
                    showFiveToast("请选择停车时长");
                } else if (mCanParkList.isEmpty()) {
                    showFiveToast("在您选择的时间内没有适合的车位");
                } else {
                    if (mIsBookSpecificParkSpace) {
                        showAppointmentDialog(mCanParkList.get(0));
                    } else {
                        startActivityForResult(BookParkSpaceActivity.class, APPOINTMENT_PARK_SAPCE_CODE, ConstansUtil.REQUEST_FOR_RESULT, new ArrayList<>(mCanParkList));
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == ConstansUtil.REQUSET_CODE) {
                //选择车辆的结果
                if (mCars == null) {
                    mCars = data.getParcelableArrayListExtra(ConstansUtil.CAR_NUMBER);
                }
                String carNumber = data.getStringExtra(ConstansUtil.INTENT_MESSAGE);
                mCarNumber.setText(carNumber);
                performScanParkSpace();
            } else if (requestCode == SELECTE_DESTINATION_CODE) {
                //选择的目的地
                mLatitude = data.getDoubleExtra(ConstansUtil.LATITUDE, 0);
                mLongitude = data.getDoubleExtra(ConstansUtil.LONGITUDE, 0);
                mDestination.setTextColor(ConstansUtil.B1_COLOR);
                mDestination.setText(data.getStringExtra("keyword"));
                performScanParkSpace();
            }
        } else if (resultCode == 2 && requestCode == SELECTE_DESTINATION_CODE) {
            //取消选择目的地
            mLatitude = 0;
            mLongitude = 0;
            mDestination.setTextColor(ConstansUtil.G6_COLOR);
            mDestination.setText("可选");
            performScanParkSpace();
        }
    }

    @Override
    public void onBackPressed() {
        if (mStartTimeOption != null && mStartTimeOption.isShowing()) {
            mStartTimeOption.dismiss();
        } else if (mParkDurationOption != null && mParkDurationOption.isShowing()) {
            mParkDurationOption.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
    }

    /**
     * 获取亲友分享给我的停车位
     */
    private void getFriendShareParkspace() {
        getOkGo(HttpConstants.getFriendShareParkspace)
                .execute(new JsonCallback<Base_Class_List_Info<Park_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<Park_Info> o, Call call, Response response) {
                        mParkInfos.addAll(o.data);
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    showFiveToast("还没有好友给你分享车位哦");
                                    finish();
                                    break;
                                default:
                                    showFiveToast("获取车位信息失败，请稍后再试");
                                    finish();
                                    break;
                            }
                        }
                    }
                });
    }

    /**
     * 获取亲友的预约记录，包括预约不是亲友车位的订单
     */
    private void getUserParkOrderForAppoint() {
        getOkGo(HttpConstants.getUserParkOrderForAppoint)
                .execute(new JsonCallback<Base_Class_List_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<ParkOrderInfo> o, Call call, Response response) {
                        for (ParkOrderInfo parkOrderInfo : o.data) {
                            if (parkOrderInfo.getOrderStatus().equals("1") || parkOrderInfo.getOrderStatus().equals("2")) {
                                //添加已预约和停车中的
                                mParkOrderInfos.add(parkOrderInfo);
                            } else if (parkOrderInfo.getOrderStatus().equals("3")) {
                                showPayDialog();
                                break;
                            }
                        }
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dismmisLoadingDialog();
                        switch (e.getMessage()) {
                            case "101":
                                break;
                            default:
                                showFiveToast("获取订单信息失败，请稍后重试");
                                finish();
                                break;
                        }
                    }
                });
    }

    private void showPayDialog() {
        new TipeDialog.Builder(this)
                .setTitle("提示")
                .setMessage("您当前还有未支付的订单，需要支付完成才可以预约，现在支付吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("去支付", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(ParkOrderActivity.class);
                        finish();
                    }
                })
                .create()
                .show();
    }

    /**
     * 弹出选择入场时间的选择器
     */
    private void showAppointmentIncomeTimeOptions() {
        if (mStartTimeOption == null) {
            mDays = new ArrayList<>(7);
            mHours = new ArrayList<>(24);
            mMinutes = new ArrayList<>(60);

            mCalendar = TimeManager.getInstance().getCurrentCalendar();
            Calendar todayEndCalendar = (Calendar) mCalendar.clone();
            todayEndCalendar.set(Calendar.HOUR_OF_DAY, 24);
            todayEndCalendar.set(Calendar.MINUTE, 0);
            todayEndCalendar.set(Calendar.SECOND, 0);
            todayEndCalendar.set(Calendar.MILLISECOND, 0);

            ArrayList<String> hours;
            ArrayList<ArrayList<String>> hourWithMinute;
            ArrayList<String> minute;
            if (DateUtil.getCalendarDistance(mCalendar, todayEndCalendar) > 1) {
                //如果距离凌晨大于一分钟才显示今天可选
                mDays.add("今天");

                hours = new ArrayList<>();
                hourWithMinute = new ArrayList<>();

                //添加现在的时分,如果现在是15:59分则不添加
                if (mCalendar.get(Calendar.MINUTE) != 59 && mCalendar.get(Calendar.MINUTE) != 60) {
                    minute = new ArrayList<>();
                    hours.add(String.valueOf(mCalendar.get(Calendar.HOUR_OF_DAY)));
                    for (int j = mCalendar.get(Calendar.MINUTE) + 1; j < 60; j++) {
                        minute.add(String.valueOf(j));
                    }
                    hourWithMinute.add(minute);
                }

                //添加往后一小时到23点的时分（即16点到23点）
                for (int i = mCalendar.get(Calendar.HOUR_OF_DAY) + 1; i < 24; i++) {
                    hours.add(String.valueOf(i));
                    minute = new ArrayList<>();
                    for (int j = 0; j < 60; j++) {
                        minute.add(String.valueOf(j));
                    }
                    hourWithMinute.add(minute);
                }

                mHours.add(hours);
                mMinutes.add(hourWithMinute);
            }

            mDays.add("明天");
            mDays.add("后天");
            mCalendar.add(Calendar.DAY_OF_MONTH, 3);  //今天，明天，后天
            for (int i = 0; i < 2; i++) {
                addhourWithMinutes();
            }

            //添加往后几天的数据
            for (int i = 0, size = 7 - mDays.size(); i < size; i++) {
                mDays.add((mCalendar.get(Calendar.MONTH) + 1) + "月" + mCalendar.get(Calendar.DAY_OF_MONTH) + "日");
                addhourWithMinutes();
                mCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            //上面加了几天，要重置
            mCalendar = TimeManager.getInstance().getCurrentCalendar();

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
                    Calendar appointmentStartCalendar = (Calendar) mCalendar.clone();
                    appointmentStartCalendar.add(Calendar.DAY_OF_MONTH, mDays.get(0).equals("今天") ? options1 : options1 + 1);
                    appointmentStartCalendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(mHours.get(options1).get(option2)));
                    appointmentStartCalendar.set(Calendar.MINUTE, Integer.valueOf(mMinutes.get(options1).get(option2).get(options3)));
                    appointmentStartCalendar.set(Calendar.SECOND, 0);
                    appointmentStartCalendar.set(Calendar.MILLISECOND, 0);
                    mAppointmentStartTime = DateUtil.getCurrentYearToMinutes(appointmentStartCalendar.getTimeInMillis());
                    Calendar calendar = TimeManager.getInstance().getCurrentCalendar();
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    if (appointmentStartCalendar.compareTo(calendar) >= 0) {
                        //入场时间比现在的时间晚则显示选择的入场时间
                        mAppointmentIncomeTime.setText(tx);
                        appointmentStartCalendar.add(Calendar.MINUTE, mParkDuration);
                        mAppointmentEndTime = DateUtil.getCalenarYearToMinutes(appointmentStartCalendar);
                        performScanParkSpace();
                    } else {
                        //如果入场时间比现在的时间早则是无效时间
                        mAppointmentStartTime = "";
                        mAppointmentIncomeTime.setText("");
                        showFiveToast("请选择有效时间哦");
                    }
                }
            });
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

    /**
     * 停车时长选择器
     */
    private void showParkDurationOptions() {
        if (mParkDurationOption == null) {
            mParkDurationOption = new OptionsPickerView<>(this);
            mDurationHours = new ArrayList<>(3);
            mDurationMinutes = new ArrayList<>(3);

            ArrayList<String> minutes;
            for (int i = 0; i <= 2; i++) {
                mDurationHours.add(String.valueOf(i));

                if (i != 2) {
                    minutes = new ArrayList<>(23);
                    for (int j = i == 0 ? 5 : 0; j < 60; j += 5) {
                        //0小时的时候不添加0分钟
                        minutes.add(String.valueOf(j));
                    }
                } else {
                    //最多只能停2个小时，所以两小时0分钟
                    minutes = new ArrayList<>(1);
                    minutes.add("0");
                }
                mDurationMinutes.add(minutes);
            }

            mParkDurationOption.setPicker(mDurationHours, mDurationMinutes, true);
            mParkDurationOption.setLabels("小时", "分钟");
            mParkDurationOption.setCyclic(false);
            mParkDurationOption.setTextSize(18);

            mParkDurationOption.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

                @Override
                public void onOptionsSelect(int options1, int option2, int options3) {
                    //记录停车时长
                    mParkDuration = Integer.valueOf(mDurationHours.get(options1)) * 60 + Integer.valueOf(mDurationMinutes.get(options1).get(option2));
                    if (!mAppointmentStartTime.equals("")) {
                        //如果选了入场时间的则算出停车的结束时间
                        mAppointmentEndTime = DateUtil.getYearToMinute(mAppointmentStartTime + ":00", mParkDuration * 60);
                    }

                    //显示选择的停车时长是多少
                    setNewText(mParkDurationTv, "");
                    if (!mDurationHours.get(options1).equals("0")) {
                        mParkDurationTv.append(mDurationHours.get(options1));
                        mParkDurationTv.append("小时");
                    }
                    if (!mDurationMinutes.get(options1).get(option2).equals("0")) {
                        mParkDurationTv.append(mDurationMinutes.get(options1).get(option2));
                        mParkDurationTv.append("分钟");
                    }
                    performScanParkSpace();
                }
            });
        }
        //监听确定选择按钮
        mParkDurationOption.show();
    }

    private void performScanParkSpace() {
        if (!TextUtils.isEmpty(getText(mCarNumber)) && !mAppointmentStartTime.equals("") && mParkDuration != 0) {
            Calendar appointmentStartCalendar = DateUtil.getYearToMinuteCalendar(mAppointmentStartTime);
            Calendar appointmentEndCalendar = DateUtil.getYearToMinuteCalendar(mAppointmentEndTime);
            Calendar orderStartCalendar;
            Calendar orderEndCalendar;
            for (ParkOrderInfo parkOrderInfo : mParkOrderInfos) {
                orderStartCalendar = DateUtil.getYearToSecondCalendar(parkOrderInfo.getOrderStartTime());
                orderEndCalendar = DateUtil.getYearToSecondCalendar(parkOrderInfo.getOrderEndTime());

                //判断选择的停车时间是否和已有的订单有冲突的
                if (DateUtil.isIntersection(appointmentStartCalendar, appointmentEndCalendar, orderStartCalendar, orderEndCalendar) && getText(mCarNumber).equals(parkOrderInfo.getCarNumber())) {
                    if (parkOrderInfo.getOrderStatus().equals("1")) {
                        showFiveToast("在该时间您已有过预约，请重新选择哦");
                    } else {
                        showFiveToast("该车正在停车中哦");
                        mCarNumber.setText("");
                    }
                    mNextStep.setBackgroundResource(R.drawable.solid_g5_corner_8dp);
                    mNextStep.setTextColor(Color.WHITE);
                    return;
                }
            }
            scanParkSpace();
        }
    }

    /**
     * 筛选出符合要求的车位
     */
    private void scanParkSpace() {
        mCanParkList.clear();
        mCanParkList.addAll(mParkInfos);
        boolean haveDestination = mLatitude != 0 && mLongitude != 0;    //是否选择目的地
        final LatLng latLng = new LatLng(mLatitude, mLongitude);
        LatLng currentLatLng = new LatLng(LocationManager.getInstance().getmAmapLocation().getLatitude(), LocationManager.getInstance().getmAmapLocation().getLongitude());
        float distance;
        Log.e(TAG, "scanParkSpace  mAppointmentStartTime:" + mAppointmentStartTime + "   mAppointmentEndTime:" + mAppointmentEndTime);
        for (Park_Info parkInfo : mParkInfos) {
            Log.e(TAG, "scanParkSpace: " + parkInfo);

            if (!parkInfo.getPark_status().equals("2")) {
                //排除不是开放中的车位
                mCanParkList.remove(parkInfo);
                continue;
            }

            //排除不在共享日期之内的(根据共享日期)
            if (DateUtil.notInShareDate(mAppointmentStartTime, mAppointmentEndTime, parkInfo.getOpen_date())) {
                mCanParkList.remove(parkInfo);
                Log.e(TAG, "scanPark: notInShareDate");
                continue;
            }

            //排除暂停时间在预约时间内的(根据暂停日期)
            if (DateUtil.isInParkSpacePauseDate(mAppointmentStartTime, mAppointmentEndTime, parkInfo.getPauseShareDate())) {
                mCanParkList.remove(parkInfo);
                Log.e(TAG, "scanPark: inPauseDate");
                continue;
            }

            //排除预约时间当天不共享的(根据共享星期)
            if (DateUtil.isNotInShareDay(mAppointmentStartTime, mAppointmentEndTime, parkInfo.getShareDay())) {
                mCanParkList.remove(parkInfo);
                Log.e("TAG", "scanPark: notInShareDay");
                continue;
            }

            //排除该时间段被别人预约过的(根据车位的被预约时间)
            if (!DateUtil.isNotInOrderDate(mAppointmentStartTime, mAppointmentEndTime, parkInfo.getOrder_times())) {
                mCanParkList.remove(parkInfo);
                Log.e(TAG, "scanPark: isInOrderDate");
                continue;
            }

            Log.e("TAG", "Open_time: " + parkInfo.getOpen_time());
            //排除不在共享时间段内的(根据共享的时间段)
            if (DateUtil.isInShareTime(mAppointmentStartTime, mAppointmentEndTime, parkInfo.getOpen_time()) != -1) {
                //获取车位可共享的时间差
                int position = mCanParkList.indexOf(parkInfo);
                mCanParkList.set(position, parkInfo);
            } else {
                mCanParkList.remove(parkInfo);
            }

            if (haveDestination) {
                //如果选了目的地，则计算目的地和车位之间的距离
                distance = AMapUtils.calculateLineDistance(latLng, new LatLng(parkInfo.getLatitude(), parkInfo.getLongitude()));
            } else {
                //没有目的地则计算用户所在当前位置和车位之间的距离
                distance = AMapUtils.calculateLineDistance(currentLatLng, new LatLng(parkInfo.getLatitude(), parkInfo.getLongitude()));
            }

            parkInfo.setHaveDistination(haveDestination);
            parkInfo.setDistance(distance);
            parkInfo.setCarNumber(getText(mCarNumber));
            parkInfo.setParkInterval(mAppointmentStartTime + "*" + mAppointmentEndTime);
        }

        if (!mCanParkList.isEmpty()) {
            //根据距离排序
            Collections.sort(mCanParkList, new Comparator<Park_Info>() {
                @Override
                public int compare(Park_Info o1, Park_Info o2) {
                    return (int) (o1.getDistance() - o2.getDistance());
                }
            });
            mNextStep.setBackgroundResource(R.drawable.little_yuan_yellow_8dp);
            mNextStep.setTextColor(ConstansUtil.B1_COLOR);
        } else {
            mNextStep.setBackgroundResource(R.drawable.solid_g5_corner_8dp);
            mNextStep.setTextColor(Color.WHITE);
        }

    }

    private void showAppointmentDialog(final Park_Info parkInfo) {
        new TipeDialog.Builder(this)
                .setTitle("预约车位")
                .setMessage("确定预约" + parkInfo.getLocation_describe() + "车位吗")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reserveFriendParkSpace(parkInfo);
                    }
                })
                .create()
                .show();
    }

    /**
     * 预约亲友车位
     */
    private void reserveFriendParkSpace(final Park_Info parkInfo) {
        showLoadingDialog("预约中...");
        getOkGo(HttpConstants.reserveFriendParkSpace)
                .params("parkLotId", parkInfo.getParkLotId())
                .params("parkSpaceId", parkInfo.getId())
                .params("carNumber", parkInfo.getCarNumber())
                .params("parkInterval", parkInfo.getParkInterval())
                .params("cityCode", parkInfo.getCityCode())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        IntentObserable.dispatch(ConstansUtil.BOOK_PARK_SPACE, ConstansUtil.INTENT_MESSAGE, parkInfo);
                        dismmisLoadingDialog();
                        showFiveToast("预约成功");
                        startActivity(UseFriendParkSpaceRecordActivity.class);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "106":
                                    IntentObserable.dispatch(ConstansUtil.DELETE_SHARE_PARK_SPACE, ConstansUtil.INTENT_MESSAGE, parkInfo);
                                    if (mParkInfos.size() == 1) {
                                        finish();
                                        showFiveToast("业主已经不再分享该车位给你了");
                                    } else {
                                        mParkInfos.remove(parkInfo);
                                        performScanParkSpace();
                                        showFiveToast("业主已经不再分享该车位给你了，请重新选择");
                                    }
                                    break;
                                case "107":
                                    showFiveToast("该时间段车位已有预约，请重新选择");
                                    break;
                                case "108":
                                    showPayDialog();
                                    break;
                                default:
                                    showFiveToast(e.getMessage());
                                    break;
                            }
                        }
                    }
                });
    }

    @Override
    public void onReceive(Intent intent) {
        if (Objects.equals(intent.getAction(), ConstansUtil.BOOK_PARK_SPACE)) {
            //预约了之后添加预约记录到订单和车位，否则可以再次预约同样的车位，再次筛选的时候也会不准确
            Park_Info parkInfo = intent.getParcelableExtra(ConstansUtil.INTENT_MESSAGE);

            //添加预约订单
            ParkOrderInfo parkOrderInfo = new ParkOrderInfo();
            parkOrderInfo.setOrderStartTime(mAppointmentStartTime + ":00");
            parkOrderInfo.setOrderEndTime(mAppointmentEndTime + ":00");
            parkOrderInfo.setCarNumber(parkInfo.getCarNumber());
            parkOrderInfo.setOrderStatus("1");
            mParkOrderInfos.add(parkOrderInfo);

            for (Park_Info park_info : mParkInfos) {
                if (park_info.equals(parkInfo)) {
                    //车位添加预约记录
                    if (park_info.getOrder_times().equals("-1")) {
                        park_info.setOrder_times(parkInfo.getParkInterval());
                    } else {
                        park_info.setOrder_times(park_info.getOrder_times() + "," + parkInfo.getParkInterval());
                    }
                    break;
                }
            }

            mCanParkList.remove(parkInfo);
            if (mCanParkList.isEmpty()) {
                mNextStep.setBackgroundResource(R.drawable.solid_g5_corner_8dp);
                mNextStep.setTextColor(Color.WHITE);
            }
        }
    }

}
