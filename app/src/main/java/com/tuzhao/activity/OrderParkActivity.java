package com.tuzhao.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.chenjing.XStatusBarHelper;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.pickerview.OptionsPickerView;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.activity.mine.MyCarActivity;
import com.tuzhao.activity.mine.ParkOrderAppointmentActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkLotInfo;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.JsonCodeCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;

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
 * Created by TZL12 on 2017/11/7.
 */

public class OrderParkActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "OrderParkActivity";

    private LoadingDialog mLoadingDialog;
    private ImageView imageView_back;
    private LinearLayout linearlayout_carnumble, linearlayout_starttime, linearlayout_parktime;
    private TextView textview_carnumble, textview_starttime, textview_parktime, textview_fee, textview_ordernow;

    private ParkLotInfo mParkLotInfo;
    private ArrayList<Park_Info> park_list;
    private ArrayList<ParkOrderInfo> order_list;
    private List<Park_Info> mCanParkInfo;
    private DateUtil dateUtil = new DateUtil();
    private String start_time = "", end_time = "";//预定开始和结束的停车时间
    private int park_time = 0;//预定停车时长（分钟）
    //停车时长选择器
    private OptionsPickerView<String> pvOptions;
    private ArrayList<String> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

    private ArrayList<String> mDays;

    private ArrayList<ArrayList<String>> mHours;

    private ArrayList<ArrayList<ArrayList<String>>> mMinutes;

    private OptionsPickerView<String> mStartTimeOption;

    private DecimalFormat mDecimalFormat;

    private Calendar mCanParkEndCalendar;

    private int mExtensionTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderpark_layout);
        XStatusBarHelper.tintStatusBar(this, ContextCompat.getColor(this, R.color.w0), 0);

        initView();
        initData();
        initEvent();
    }

    private void initData() {
        if (getIntent().hasExtra(ConstansUtil.PARK_LOT_INFO)) {
            mParkLotInfo = getIntent().getParcelableExtra(ConstansUtil.PARK_LOT_INFO);
            if (getIntent().hasExtra("park_list")) {
                park_list = getIntent().getParcelableArrayListExtra("park_list");
                order_list = getIntent().getParcelableArrayListExtra("order_list");
            } else {
                requestGetParkListData();
                requestGetUserParkOrderForAppoint();
            }
        } else {
            finish();
        }

        mDecimalFormat = new DecimalFormat("0.00");
    }

    private void initView() {
        imageView_back = findViewById(R.id.id_activity_orderpark_layout_imageView_back);
        linearlayout_carnumble = findViewById(R.id.id_activity_orderpark_layout_linearlayout_carnumble);
        linearlayout_starttime = findViewById(R.id.id_activity_orderpark_layout_linearlayout_starttime);
        linearlayout_parktime = findViewById(R.id.id_activity_orderpark_layout_linearlayout_parktime);
        textview_carnumble = findViewById(R.id.id_activity_orderpark_layout_textview_carnumble);
        textview_starttime = findViewById(R.id.id_activity_orderpark_layout_textview_starttime);
        textview_parktime = findViewById(R.id.id_activity_orderpark_layout_textview_parktime);
        textview_fee = findViewById(R.id.id_activity_orderpark_layout_textview_fee);
        textview_ordernow = findViewById(R.id.id_activity_orderpark_layout_textview_ordernow);
    }

    private void initEvent() {
        linearlayout_carnumble.setOnClickListener(this);
        linearlayout_starttime.setOnClickListener(this);
        linearlayout_parktime.setOnClickListener(this);
        imageView_back.setOnClickListener(this);
        textview_ordernow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.id_activity_orderpark_layout_imageView_back:
                finish();
                break;
            case R.id.id_activity_orderpark_layout_linearlayout_carnumble:
                if (UserManager.getInstance().hasLogined()) {
                    intent = new Intent(OrderParkActivity.this, MyCarActivity.class);
                    intent.putExtra(ConstansUtil.INTENT_MESSAGE, true);
                    startActivityForResult(intent, ConstansUtil.REQUSET_CODE);
                } else {
                    login();
                }
                break;
            case R.id.id_activity_orderpark_layout_linearlayout_starttime:
                showStartTimeOptions();
                break;
            case R.id.id_activity_orderpark_layout_linearlayout_parktime:
                showParktimeOptions();
                break;
            case R.id.id_activity_orderpark_layout_textview_ordernow:
                if (!(textview_starttime.getText().length() > 0) || !(textview_carnumble.getText().length() > 0) || !(textview_parktime.getText().length() > 0)) {
                    MyToast.showToast(OrderParkActivity.this, "请确认信息是否填写完整", 5);
                } else {
                    if (mCanParkInfo.size() > 0) {
                        showAlertDialog();
                    } else {
                        String price = "约￥0.00";
                        textview_fee.setText(price);
                        MyToast.showToast(OrderParkActivity.this, "未能匹配到合适的车位，请尝试更换时间", 5);
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstansUtil.REQUSET_CODE && resultCode == RESULT_OK && data != null) {
            String carNumber = data.getStringExtra(ConstansUtil.INTENT_MESSAGE);
            Log.e(TAG, "onActivityResult: " + carNumber);
            textview_carnumble.setText(carNumber);
            if (order_list.size() > 0) {
                for (ParkOrderInfo parkOrderInfo : order_list) {
                    if (parkOrderInfo.getOrderStatus().equals("2")) {
                        if (parkOrderInfo.getCarNumber().equals(carNumber)) {
                            MyToast.showToast(OrderParkActivity.this, "该车辆当前正在停车中，请重新选择哦", 5);
                            textview_carnumble.setText("");
                            return;
                        }
                    }
                }
            }
            if (carNumber == null) {
                textview_ordernow.setBackground(ContextCompat.getDrawable(OrderParkActivity.this, R.drawable.solid_g5_corner_8dp));
                textview_ordernow.setTextColor(ContextCompat.getColor(OrderParkActivity.this, R.color.w0));
            } else {
                if (textview_starttime.getText().length() > 0 && textview_parktime.getText().length() > 0 && mCanParkInfo.size() > 0) {
                    textview_ordernow.setBackground(ContextCompat.getDrawable(OrderParkActivity.this, R.drawable.little_yuan_yellow_8dp));
                    textview_ordernow.setTextColor(ContextCompat.getColor(OrderParkActivity.this, R.color.b1));
                    try {
                        setOrderFee();
                          /*  DateUtil.ParkFee parkFee = dateUtil.countCost(start_time, end_time, mChooseData.get(0).parktime_qujian.substring(mChooseData.get(0).parktime_qujian.indexOf("*") + 1, mChooseData.get(0).parktime_qujian.length()), mParkLotInfo.getHigh_time().substring(0, mParkLotInfo.getHigh_time().indexOf(" - ")), mParkLotInfo.getHigh_time().substring(mParkLotInfo.getHigh_time().indexOf(" - ") + 3, mParkLotInfo.getHigh_time().length()), mParkLotInfo.getHigh_fee(), mParkLotInfo.getLow_fee(), mParkLotInfo.getFine());
                            textview_fee.setText("约￥" + parkFee.parkfee);*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("TAG", "handleMessage4: ");
                    textview_fee.setText("约￥0.00");
                }
            }
        }
    }

    private void requestGetParkListData() {
        OkGo.post(HttpConstants.getParkList)//请求数据的接口地址
                .tag(OrderParkActivity.this.getClass().getName())
                .params("parkspace_id", mParkLotInfo.getId())
                .params("citycode", mParkLotInfo.getCity_code())
                .execute(new JsonCallback<Base_Class_List_Info<Park_Info>>() {
                    @Override
                    public void onSuccess(final Base_Class_List_Info<Park_Info> responseData, Call call, Response response) {
                        //请求成功
                        Log.e("TAG", "onSuccess: " + responseData.data);
                        dissmissLoading();
                        park_list = responseData.data;
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dissmissLoading();
                        if ("102".equals(e.getMessage())) {
                            MyToast.showToast(OrderParkActivity.this, "该车场暂无停车位，无法预约", 5);
                        } else {
                            MyToast.showToast(OrderParkActivity.this, "获取停车位信息失败，请稍后重试", 5);
                        }
                        finish();
                    }
                });
    }

    private void requestGetUserParkOrderForAppoint() {
        initLoading("加载中...");
        OkGo.post(HttpConstants.getUserParkOrderForAppoint)
                .tag(OrderParkActivity.this.getClass().getName())
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .execute(new JsonCallback<Base_Class_List_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(final Base_Class_List_Info<ParkOrderInfo> responseData, Call call, Response response) {
                        if (mLoadingDialog != null) {
                            if (mLoadingDialog.isShowing()) {
                                mLoadingDialog.dismiss();
                            }
                        }
                        order_list = responseData.data;
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dissmissLoading();
                        if ("101".equals(e.getMessage())) {
                            //还没有订单
                            order_list = new ArrayList<>();
                        } else {
                            MyToast.showToast(OrderParkActivity.this, "获取订单信息失败，请稍后重试", 5);
                            finish();
                        }
                    }
                });
    }

    private void showStartTimeOptions() {
        textview_ordernow.setBackground(ContextCompat.getDrawable(OrderParkActivity.this, R.drawable.solid_g5_corner_8dp));
        textview_ordernow.setTextColor(ContextCompat.getColor(OrderParkActivity.this, R.color.w0));
        if (mStartTimeOption == null) {
            mDays = new ArrayList<>(7);
            mHours = new ArrayList<>(24);
            mMinutes = new ArrayList<>(60);

            Calendar nowCalendar = Calendar.getInstance();
            Calendar todayEndCalendar = Calendar.getInstance();
            todayEndCalendar.set(Calendar.HOUR_OF_DAY, 24);
            todayEndCalendar.set(Calendar.MINUTE, 0);
            todayEndCalendar.set(Calendar.SECOND, 0);
            todayEndCalendar.set(Calendar.MILLISECOND, 0);

            ArrayList<String> hours;
            ArrayList<ArrayList<String>> hourWithMinute;
            ArrayList<String> minute;
            if (DateUtil.getCalendarDistance(nowCalendar, todayEndCalendar) > 1) {
                //如果距离凌晨大于一分钟才显示今天可选
                mDays.add("今天");

                hours = new ArrayList<>();
                hourWithMinute = new ArrayList<>();
                //添加现在的时分
                if (nowCalendar.get(Calendar.MINUTE) != 59 && nowCalendar.get(Calendar.MINUTE) != 60) {
                    minute = new ArrayList<>();
                    hours.add(String.valueOf(nowCalendar.get(Calendar.HOUR_OF_DAY)));
                    for (int j = nowCalendar.get(Calendar.MINUTE) + 1; j < 60; j++) {
                        minute.add(String.valueOf(j));
                    }
                    hourWithMinute.add(minute);
                }

                //添加往后一小时到23点的时分
                for (int i = nowCalendar.get(Calendar.HOUR_OF_DAY) + 1; i < 24; i++) {
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
            nowCalendar.add(Calendar.DAY_OF_MONTH, 3);
            for (int i = 0; i < 2; i++) {
                addhourWithMinutes();
            }

            for (int i = 0, size = 7 - mDays.size(); i < size; i++) {
                mDays.add((nowCalendar.get(Calendar.MONTH) + 1) + "月" + nowCalendar.get(Calendar.DAY_OF_MONTH) + "日");
                addhourWithMinutes();
                nowCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }

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
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_MONTH, mDays.get(0).equals("今天") ? options1 : options1 + 1);
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(mHours.get(options1).get(option2)));
                    calendar.set(Calendar.MINUTE, Integer.valueOf(mMinutes.get(options1).get(option2).get(options3)));
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    start_time = DateUtil.getCurrentYearToMinutes(calendar.getTimeInMillis());

                    Calendar nowCalendar = Calendar.getInstance();
                    nowCalendar.set(Calendar.SECOND, 0);
                    nowCalendar.set(Calendar.MILLISECOND, 0);

                    if (calendar.compareTo(nowCalendar) >= 0) {
                        textview_starttime.setText(tx);
                        end_time = dateUtil.addTime(start_time, park_time);
                        if (order_list.size() > 0) {
                            for (ParkOrderInfo parkOrderInfo : order_list) {
                                if (parkOrderInfo.getOrderStatus().equals("1") && dateUtil.betweenStartAndEnd(start_time, parkOrderInfo.getOrderStartTime(), parkOrderInfo.getOrderEndTime())) {
                                    //预约订单
                                    MyToast.showToast(OrderParkActivity.this, "在该时间您已有过预约，请重新选择哦", 5);
                                    textview_starttime.setText("");
                                    return;
                                }
                            }
                        }
                        if (textview_parktime.getText().length() > 0 && park_time > 0) {
                            scanPark();
                        }
                    } else {
                        start_time = "";
                        textview_starttime.setText("");
                        MyToast.showToast(OrderParkActivity.this, "请选择有效时间哦", 5);
                    }
                }
            });
        }

        mStartTimeOption.show();
    }

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

    private void showParktimeOptions() {
        textview_ordernow.setBackground(ContextCompat.getDrawable(OrderParkActivity.this, R.drawable.solid_g5_corner_8dp));
        textview_ordernow.setTextColor(ContextCompat.getColor(OrderParkActivity.this, R.color.w0));
        //选项选择器
        if (pvOptions == null) {
            pvOptions = new OptionsPickerView<>(OrderParkActivity.this);
            // 初始化列表数据
            dateUtil.initParktimeData(options1Items, options2Items, options3Items);
            //三级联动效果
            pvOptions.setPicker(options1Items, options2Items, options3Items, false);
            //设置选择的三级单位
            pvOptions.setLabels("天", "小时", "分钟");
            pvOptions.setCyclic(false);
            //设置默认选中的三级项目
            pvOptions.setSelectOptions(0, 1, 0);
            pvOptions.setTextSize(18);

            pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

                @Override
                public void onOptionsSelect(int options1, int option2, int options3) {
                    //返回的分别是三个级别的选中位置
                    //String tx;
                    if (options1 == 0 && option2 == 0 && options3 == 0) {
                        MyToast.showToast(OrderParkActivity.this, "请选择有效时段哦", 5);
                        textview_parktime.setText("");
                        park_time = 0;
                    } else {
                        park_time = options1 * 60 * 24 + option2 * 60 + Integer.valueOf(options3Items.get(options1).get(option2).get(options3));
                        Calendar startParkCalendar;
                        if (start_time.equals("")) {
                            startParkCalendar = Calendar.getInstance();
                        } else {
                            startParkCalendar = DateUtil.getYearToMinuteCalendar(start_time);
                        }

                        startParkCalendar.add(Calendar.MINUTE, park_time);
                        end_time = DateUtil.getCalenarYearToMinutes(startParkCalendar);

                        int parkMuintes = park_time;
                        StringBuilder stringBuilder = new StringBuilder();
                        if (parkMuintes / (60 * 24) != 0) {
                            stringBuilder.append(parkMuintes / (60 * 24));
                            stringBuilder.append("天");
                        }

                        parkMuintes -= 24 * 60 * options1;
                        if (parkMuintes / 60 != 0) {
                            stringBuilder.append(parkMuintes / 60);
                            stringBuilder.append("小时");
                        }

                        parkMuintes -= 60 * option2;
                        if (parkMuintes > 0) {
                            stringBuilder.append(parkMuintes);
                            stringBuilder.append("分钟");
                        }
                        textview_parktime.setText(stringBuilder.toString());

                        park_time = Integer.parseInt(options1Items.get(options1)) * 60 * 24 + Integer.parseInt(options2Items.get(options1).get(option2)) * 60 + Integer.parseInt(options3Items.get(options1).get(option2).get(options3));
                        end_time = dateUtil.addTime(start_time, park_time);
                        if (order_list.size() > 0) {
                            for (ParkOrderInfo parkOrderInfo : order_list) {
                                if (parkOrderInfo.getOrderStatus().equals("1") && dateUtil.betweenStartAndEnd(end_time, parkOrderInfo.getOrderStartTime(), parkOrderInfo.getOrderEndTime())) {
                                    //预约订单
                                    MyToast.showToast(OrderParkActivity.this, "在该时段内您已有过预约，请重新选择哦", 5);
                                    end_time = "";
                                    textview_parktime.setText("");
                                    park_time = 0;
                                    return;
                                }
                            }
                        }
                        if (textview_starttime.getText().length() > 0 && !start_time.equals("")) {
                            scanPark();
                        }

                    }
                }
            });
        }
        //监听确定选择按钮
        pvOptions.show();
    }

    private void scanPark() {
        Log.e("TAG", "startDate: " + start_time + "  endDate:" + end_time);
        if (mCanParkInfo == null) {
            mCanParkInfo = new LinkedList<>();
        } else {
            mCanParkInfo.clear();
        }
        mCanParkInfo.addAll(park_list);

        Calendar[] shareTimeCalendar;
        int status;
        for (Park_Info parkInfo : park_list) {
            Log.e("TAG", "scanPark parkInfo:" + parkInfo);
            if (!parkInfo.getPark_status().equals("2")) {
                mCanParkInfo.remove(parkInfo);
                continue;
            }

            int currentStatus;
            //排除不在共享日期之内的(根据共享日期)
            if ((currentStatus = DateUtil.isInShareDate(start_time, end_time, parkInfo.getOpen_date())) == 0) {
                mCanParkInfo.remove(parkInfo);
                Log.e(TAG, "scanPark: notInShareDate");
                continue;
            } else {
                status = currentStatus;
            }

            //排除暂停时间在预定时间内的(根据暂停日期)
            if ((currentStatus = DateUtil.isInPauseDate(start_time, end_time, parkInfo.getPauseShareDate())) == 0) {
                mCanParkInfo.remove(parkInfo);
                Log.e(TAG, "scanPark: inPauseDate");
                continue;
            } else {
                if (status != 1) {
                    status = currentStatus;
                }
            }

            //排除预定时间当天不共享的(根据共享星期)
            if ((currentStatus = DateUtil.isInShareDay(start_time, end_time, parkInfo.getShareDay())) == 0) {
                mCanParkInfo.remove(parkInfo);
                Log.e("TAG", "scanPark: notInShareDay");
                continue;
            } else {
                if (status != 1) {
                    status = currentStatus;
                }
            }

            //排除该时间段被别人预约过的(根据车位的被预约时间)
            if (DateUtil.isInOrderDate(start_time, end_time, parkInfo.getOrder_times())) {
                mCanParkInfo.remove(parkInfo);
                Log.e(TAG, "scanPark: isInOrderDate");
                continue;
            }

            Log.e("TAG", "Open_time: " + parkInfo.getOpen_time());
            //排除不在共享时间段内的(根据共享的时间段)
            if ((shareTimeCalendar = DateUtil.isInShareTime(start_time, end_time, parkInfo.getOpen_time(), status == 1)) != null) {
                //获取车位可共享的时间差
                Log.e("TAG", "shareTimeDistance: " + DateUtil.getCalendarMonthToMinute(shareTimeCalendar[0]) + "  end:" + DateUtil.getCalendarMonthToMinute(shareTimeCalendar[1]));
                int position = mCanParkInfo.indexOf(parkInfo);
                parkInfo.setShareTimeCalendar(shareTimeCalendar);
                mCanParkInfo.set(position, parkInfo);
            } else {
                mCanParkInfo.remove(parkInfo);
            }
        }

        //停车时间加上宽限时长
        mCanParkEndCalendar = DateUtil.getYearToMinuteCalendar(end_time);
        mCanParkEndCalendar.add(Calendar.MINUTE, UserManager.getInstance().getUserInfo().getLeave_time());

        Collections.sort(mCanParkInfo, new Comparator<Park_Info>() {
            @Override
            public int compare(Park_Info o1, Park_Info o2) {
                long result;

                if ((o1.getShareTimeCalendar()[1].getTimeInMillis() - mCanParkEndCalendar.getTimeInMillis()) >= 0
                        && (o2.getShareTimeCalendar()[1].getTimeInMillis() - mCanParkEndCalendar.getTimeInMillis()) >= 0) {
                    //如果两个车位的共享结束时段都比预约停车时间加上宽限时间长，则按照车位的可共享时段的大小从小到大排序
                    result = DateUtil.getCalendarDistance(o1.getShareTimeCalendar()[0], o1.getShareTimeCalendar()[1]) -
                            DateUtil.getCalendarDistance(o2.getShareTimeCalendar()[0], o2.getShareTimeCalendar()[1]);
                } else if ((o1.getShareTimeCalendar()[1].getTimeInMillis() - mCanParkEndCalendar.getTimeInMillis()) > 0) {
                    //如果第一个车位的共享结束时段都比预约停车时间加上宽限时间长，第二个不是，则第一个排前面
                    result = -1;
                } else if ((o2.getShareTimeCalendar()[1].getTimeInMillis() - mCanParkEndCalendar.getTimeInMillis()) > 0) {
                    //如果第二个车位的共享结束时段都比预约停车时间加上宽限时间长，第二个不是，则第二个排前面
                    result = -1;
                } else {
                    //如果两个车位的共享结束时段都不比预约停车时间加上宽限时间长，则停车的宽限时长大的排前面
                    result = (o2.getShareTimeCalendar()[1].getTimeInMillis() - mCanParkEndCalendar.getTimeInMillis())
                            - (o1.getShareTimeCalendar()[1].getTimeInMillis() - mCanParkEndCalendar.getTimeInMillis());
                }
                return (int) result;
            }
        });

        if (mCanParkInfo.size() >= 1) {
            if (TextUtils.isEmpty(textview_carnumble.getText())) {
                MyToast.showToast(this, "选了车牌号码才可以预定车位哦", 5);
            }
            if (mCanParkInfo.size() != 1) {
                sortCanParkByIndicator();
            }
        }

        Log.e("TAG", "mCanParkInfo: " + mCanParkInfo);
        setOrderFee();
    }

    /**
     * 把共享时长大于mCanParkEndCalendar的前三个预选车位按照指标排序
     */
    private void sortCanParkByIndicator() {
        if (mCanParkInfo.get(1).getShareTimeCalendar()[1].getTimeInMillis() - mCanParkEndCalendar.getTimeInMillis() <= 0) {
            //如果第二个的共享时间没有比停车时间加上宽限时间更长则不用比较了
            return;
        }

        if (mCanParkInfo.size() == 2 ||
                (mCanParkInfo.size() >= 3 && mCanParkInfo.get(2).getShareTimeCalendar()[1].getTimeInMillis() - mCanParkEndCalendar.getTimeInMillis() <= 0)) {
            //如果只有两个预选车位或者预选车位大于三个，但是第三个的共享时间没有比停车时间加上宽限时间更长
            Park_Info parkInfoOne = mCanParkInfo.get(0);
            Park_Info parkInfoTwo = mCanParkInfo.get(1);
            if (Integer.valueOf(parkInfoOne.getIndicator()) > Integer.valueOf(parkInfoTwo.getIndicator())) {
                mCanParkInfo.set(0, parkInfoTwo);
                mCanParkInfo.set(1, parkInfoOne);
            }
        } else if (mCanParkInfo.size() >= 3) {

            //预选车位大于等于三个则只需要比较前三个
            List<Park_Info> list = new ArrayList<>(3);
            for (int i = 0; i < 3; i++) {
                list.add(mCanParkInfo.get(i));
            }

            //把前三个预选车位按照指标从小到大排序
            Collections.sort(list, new Comparator<Park_Info>() {
                @Override
                public int compare(Park_Info o1, Park_Info o2) {
                    return Integer.valueOf(o1.getIndicator()) - Integer.valueOf(o2.getIndicator());
                }
            });

            for (int i = 0; i < 3; i++) {
                mCanParkInfo.set(i, list.get(i));
            }
        }

    }

    private void setOrderFee() {
        String price;
        if (textview_carnumble.getText().length() > 0 && mCanParkInfo.size() > 0) {
            textview_ordernow.setBackground(getResources().getDrawable(R.drawable.little_yuan_yellow_8dp));
            textview_ordernow.setTextColor(ContextCompat.getColor(OrderParkActivity.this, R.color.b1));
            double orderFee = DateUtil.caculateParkFee(start_time, end_time, mCanParkInfo.get(0).getHigh_time(),
                    Double.valueOf(mCanParkInfo.get(0).getHigh_fee()),
                    Double.valueOf(mCanParkInfo.get(0).getLow_fee()));
            price = "约￥" + mDecimalFormat.format(orderFee);
            textview_fee.setText(price);
        } else {
            price = "约￥0.00";
            textview_fee.setText(price);
            textview_ordernow.setBackground(getResources().getDrawable(R.drawable.solid_g5_corner_8dp));
            textview_ordernow.setTextColor(ContextCompat.getColor(OrderParkActivity.this, R.color.w0));
        }
    }

    private void login() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void showAlertDialog() {
        Calendar canParkEndCalendar = DateUtil.getYearToMinuteCalendar(end_time);
        canParkEndCalendar.add(Calendar.MINUTE, UserManager.getInstance().getUserInfo().getLeave_time());
        Log.e("TAG", "showAlertDialog shareTime: " + DateUtil.getTwoYearToMinutesString(
                mCanParkInfo.get(0).getShareTimeCalendar()[0], mCanParkInfo.get(0).getShareTimeCalendar()[1]));
        if (DateUtil.getCalendarDistance(canParkEndCalendar, mCanParkInfo.get(0).getShareTimeCalendar()[1]) >= 0) {
        /*mExtensionTime = UserManager.getInstance().getUserInfo().getLeave_time();
        builder.setMessage("最优车位宽限时长为" + mExtensionTime + "分钟，是否预定？");*/
            //builder.setMessage("已为你匹配到最优车位，是否预订？");
            addNewParkOrder();
        } else {
            final TipeDialog.Builder builder = new TipeDialog.Builder(OrderParkActivity.this);
            mExtensionTime = (int) DateUtil.getCalendarDistance(mCanParkInfo.get(0).getShareTimeCalendar()[1], canParkEndCalendar);
            builder.setMessage("可分配车位宽限时长为" + mExtensionTime + "分钟，是否预定？");
            builder.setTitle("确认预定");
            builder.setPositiveButton("立即预定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    initLoading("匹配中...");
                    addNewParkOrder();
                    //sendOrder(perfectpark, readypark);
                }
            });

            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

            builder.create().show();
        }

    }

    private void addNewParkOrder() {
        final StringBuilder readyParkId = new StringBuilder();
        StringBuilder readyParkUpdateTime = new StringBuilder();
        for (int i = 1, size = mCanParkInfo.size() > 3 ? 3 : mCanParkInfo.size(); i < size; i++) {
            readyParkId.append(mCanParkInfo.get(i).getId());
            readyParkId.append(",");

            readyParkUpdateTime.append(mCanParkInfo.get(i).getUpdate_time());
            readyParkUpdateTime.append(",");
        }
        if (readyParkId.length() > 0) {
            readyParkId.deleteCharAt(readyParkId.length() - 1);
            readyParkUpdateTime.deleteCharAt(readyParkUpdateTime.length() - 1);
        }

        initLoading("正在下单...");
        OkGo.post(HttpConstants.addNewParkOrder)
                .tag(OrderParkActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("parkspace_id", mParkLotInfo.getId())
                .params("park_id", mCanParkInfo.get(0).getId())
                .params("car_number", textview_carnumble.getText().toString())
                .params("park_interval", start_time + "*" + end_time)
                .params("park_updatetime", mCanParkInfo.get(0).getUpdate_time())
                .params("readypark_id", readyParkId.toString().equals("") ? "-1" : readyParkId.toString())
                .params("readypark_updatetime", readyParkUpdateTime.toString().equals("") ? "-1" : readyParkUpdateTime.toString())
                .params("citycode", mParkLotInfo.getCity_code())
                .execute(new JsonCodeCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> responseData, Call call, Response response) {
                        switch (responseData.code) {
                            case "0":
                                requestOrderData(responseData.data);
                                break;
                            case "101":
                                mCanParkInfo.remove(0);
                                String[] readyPark = readyParkId.toString().split(",");
                                if (!readyPark[0].equals("")) {
                                    for (int i = 0; i < readyPark.length; i++) {
                                        mCanParkInfo.remove(0);
                                    }
                                }

                                if (mCanParkInfo.size() > 0) {
                                    if (mCanParkInfo.size() != 1) {
                                        sortCanParkByIndicator();
                                    }
                                    addNewParkOrder();
                                } else {
                                    dissmissLoading();
                                    MyToast.showToast(OrderParkActivity.this, "未匹配到合适您时间的车位，请尝试更换时间", 5);
                                }
                                break;
                            case "106":
                                mCanParkInfo.remove(0);
                                showRequestAppointOrderDialog(mCanParkInfo.get(0), Integer.valueOf(responseData.data) / 60);
                                break;
                            case "102":
                                String parkSpaceId = responseData.data.substring(0, responseData.data.indexOf(","));
                                for (int i = 0; i < mCanParkInfo.size(); i++) {
                                    if (mCanParkInfo.get(i).getId().equals(parkSpaceId)) {
                                        showRequestAppointOrderDialog(mCanParkInfo.get(i), Integer.valueOf(responseData.data.split(",")[1]));
                                        break;
                                    }
                                }
                                break;
                            case "103":
                                dissmissLoading();
                                MyToast.showToast(OrderParkActivity.this, "内部错误，请重新选择", 5);
                                finish();
                                break;
                            case "104":
                                dissmissLoading();
                                MyToast.showToast(OrderParkActivity.this, "您有效订单已达上限，暂不可预约车位哦", 5);
                                break;
                            case "105":
                                dissmissLoading();
                                MyToast.showToast(OrderParkActivity.this, "您当前车位在该时段内已有过预约，请尝试更换时间", 5);
                                break;
                            case "107":
                                dissmissLoading();
                                MyToast.showToast(OrderParkActivity.this, "您有订单需要前去付款，要先处理哦", 5);
                            default:
                                dissmissLoading();
                                MyToast.showToast(OrderParkActivity.this, "服务器正在维护中", 5);
                                break;
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        MyToast.showToast(OrderParkActivity.this, e.getMessage(), 5);
                    }
                });
    }

    /**
     * @param park_info     预约的车位
     * @param extensionTime 可停车的顺延时长（分钟）
     */
    private void showRequestAppointOrderDialog(final Park_Info park_info, int extensionTime) {
        TipeDialog.Builder builder = new TipeDialog.Builder(this);
        mExtensionTime = extensionTime;
        builder.setMessage("可分配车位宽限时长为" + mExtensionTime + "分钟，是否预定？");
        builder.setTitle("确认预定");
        builder.setPositiveButton("立即预定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                initLoading("提交中...");
                requestAppointOrderLockPark(park_info);
            }
        });

        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        builder.create().show();
    }

    private void requestAppointOrderLockPark(Park_Info park_info) {
        OkGo.post(HttpConstants.appointOrderLockPark)
                .tag(OrderParkActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("parkspace_id", mParkLotInfo.getId())
                .params("car_number", textview_carnumble.getText().toString())
                .params("park_id", park_info.getId())
                .params("park_interval", start_time + "*" + end_time)
                .params("citycode", mParkLotInfo.getCity_code())
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> responseData, Call call, Response response) {
                        requestOrderData(responseData.data.getOrder_number());
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dissmissLoading();
                        if (!DensityUtil.isException(OrderParkActivity.this, e)) {
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 102:
                                    MyToast.showToast(OrderParkActivity.this, "请求已超时，请重新预定", 5);
                                    break;
                                case 901:
                                    MyToast.showToast(OrderParkActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }

    private void requestOrderData(final String orderNumber) {
        OkGo.post(HttpConstants.getDetailOfParkOrder)
                .tag(TAG)
                .headers("token", UserManager.getInstance().getToken())
                .params("citycode", mParkLotInfo.getCity_code())
                .params("order_number", orderNumber)
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> parkOrderInfoBase_class_info, Call call, Response response) {
                        dissmissLoading();
                        MyToast.showToast(OrderParkActivity.this, "预约成功", 5);
                        Intent intent = new Intent(OrderParkActivity.this, ParkOrderAppointmentActivity.class);
                        parkOrderInfoBase_class_info.data.setOrder_number(orderNumber);
                        intent.putExtra(ConstansUtil.PARK_ORDER_INFO, parkOrderInfoBase_class_info.data);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dissmissLoading();
                        if (!DensityUtil.isException(OrderParkActivity.this, e)) {
                            MyToast.showToast(OrderParkActivity.this, e.getMessage(), 5);
                        }
                    }
                });
    }

    private void initLoading(String what) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this, what);
        } else if (!mLoadingDialog.getContent().equals(what) && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = new LoadingDialog(this, what);
        }
        mLoadingDialog.show();
    }

    private void dissmissLoading() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
        OkGo.getInstance().cancelTag(OrderParkActivity.this.getClass().getName());
    }

}
