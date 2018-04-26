package com.tuzhao.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.pickerview.OptionsPickerView;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.activity.mine.CarNumberActivity;
import com.tuzhao.application.MyApplication;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.Park_Space_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.LoginDialogFragment;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TZL12 on 2017/11/7.
 */

public class OrderParkActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "OrderParkActivity";

    private CustomDialog mCustomDialog;
    private ImageView imageView_back;
    private LinearLayout linearlayout_carnumble, linearlayout_starttime, linearlayout_parktime;
    private TextView textview_carnumble, textview_starttime, textview_parktime, textview_fee, textview_ordernow;
    private LoginDialogFragment loginDialogFragment;

    private Park_Space_Info parkspace_info;
    private ArrayList<Park_Info> park_list;
    private ArrayList<ParkOrderInfo> order_list;
    private List<Park_Info> mCanParkInfo;
    private ArrayList<Holder> mChooseData = new ArrayList<>();//以选择时间来比较可以停的车位
    private DateUtil dateUtil = new DateUtil();
    private String start_time = "", end_time = "";//预定开始和结束的停车时间
    private int park_time = 0;//预定停车时长（分钟）
    //停车时长选择器
    OptionsPickerView<String> pvOptions;
    private ArrayList<String> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    //优惠券
    private Handler mHandler;

    private ArrayList<String> mDays;

    private ArrayList<ArrayList<String>> mHours;

    private ArrayList<ArrayList<ArrayList<String>>> mMinutes;

    private OptionsPickerView<String> mStartTimeOption;

    private DecimalFormat mDecimalFormat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderpark_layout);

        initView();
        initData();
        initEvent();
        setStyle(true);
    }

    private void initData() {
        if (getIntent().hasExtra("parkspace_info")) {
            parkspace_info = (Park_Space_Info) getIntent().getSerializableExtra("parkspace_info");
            park_list = (ArrayList<Park_Info>) getIntent().getSerializableExtra("park_list");
            order_list = (ArrayList<ParkOrderInfo>) getIntent().getSerializableExtra("order_list");
        } else {
            finish();
        }

        mHandler = new Handler(Looper.myLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                String obj = (String) msg.obj;
                textview_carnumble.setText(obj);
                if (order_list.size() > 0) {
                    for (ParkOrderInfo parkOrderInfo : order_list) {
                        if (parkOrderInfo.getOrder_status().equals("2")) {
                            if (parkOrderInfo.getCar_numble().equals(obj)) {
                                MyToast.showToast(OrderParkActivity.this, "该车辆当前正在停车中，请重新选择哦", 5);
                                textview_carnumble.setText("");
                                return true;
                            }
                        }
                    }
                }
                if (obj == null) {
                    textview_ordernow.setBackground(ContextCompat.getDrawable(OrderParkActivity.this, R.drawable.yuan_little_graynall_8dp));
                    textview_ordernow.setTextColor(ContextCompat.getColor(OrderParkActivity.this, R.color.w0));
                } else {
                    if (textview_starttime.getText().length() > 0 && textview_parktime.getText().length() > 0 && mCanParkInfo.size() > 0) {
                        textview_ordernow.setBackground(ContextCompat.getDrawable(OrderParkActivity.this, R.drawable.little_yuan_yellow_8dp));
                        textview_ordernow.setTextColor(ContextCompat.getColor(OrderParkActivity.this, R.color.b1));
                        try {
                            setOrderFee();
                          /*  DateUtil.ParkFee parkFee = dateUtil.countCost(start_time, end_time, mChooseData.get(0).parktime_qujian.substring(mChooseData.get(0).parktime_qujian.indexOf("*") + 1, mChooseData.get(0).parktime_qujian.length()), parkspace_info.getHigh_time().substring(0, parkspace_info.getHigh_time().indexOf(" - ")), parkspace_info.getHigh_time().substring(parkspace_info.getHigh_time().indexOf(" - ") + 3, parkspace_info.getHigh_time().length()), parkspace_info.getHigh_fee(), parkspace_info.getLow_fee(), parkspace_info.getFine());
                            textview_fee.setText("约￥" + parkFee.parkfee);*/
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("TAG", "handleMessage4: ");
                        textview_fee.setText("约￥0.00");
                    }
                }
                return false;
            }
        });

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
                    MyApplication.getInstance().setHandler(mHandler);
                    intent = new Intent(OrderParkActivity.this, CarNumberActivity.class);
                    if (textview_carnumble.getText().length() > 0) {
                        intent.putExtra("numberInTextview", textview_carnumble.getText().toString().trim());
                    }
                    startActivity(intent);
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
                        textview_fee.setText("约￥0.00");
                        MyToast.showToast(OrderParkActivity.this, "未能匹配到合适的车位，请尝试更换时间", 5);
                    }
                }
                break;
        }
    }

    private void showStartTimeOptions() {
        textview_ordernow.setBackground(ContextCompat.getDrawable(OrderParkActivity.this, R.drawable.yuan_little_graynall_8dp));
        textview_ordernow.setTextColor(ContextCompat.getColor(OrderParkActivity.this, R.color.w0));
        if (mStartTimeOption == null) {
            mDays = new ArrayList<>(10);
            mHours = new ArrayList<>(24);
            mMinutes = new ArrayList<>(60);
            dateUtil.initStartParkTimeData(mDays, mHours, mMinutes);
            mStartTimeOption = new OptionsPickerView<>(this);
            mStartTimeOption.setPicker(mDays, mHours, mMinutes, false);
            mStartTimeOption.setLabels(null, "点", "分");
            mStartTimeOption.setCyclic(false);
            mStartTimeOption.setTextSize(18);
            mStartTimeOption.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3) {
                    //返回的分别是三个级别的选中位置
                    String tx = mDays.get(options1) + " " + mHours.get(options1).get(option2) + " 点 " + mMinutes.get(options1).get(option2).get(options3) + " 分";
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + options1);//让日期加N
                    start_time = calendar.get(Calendar.YEAR) + "-" + DateUtil.thanTen((calendar.get(Calendar.MONTH) + 1)) + "-" +
                            DateUtil.thanTen(calendar.get(Calendar.DAY_OF_MONTH)) + " " + mHours.get(options1).get(option2) + ":" + mMinutes.get(options1).get(option2).get(options3);

                    Log.e("哈哈哈，", "选中时间" + start_time);
                    if (dateUtil.compareNowTime(start_time, true)) {
                        textview_starttime.setText(tx);
                        end_time = dateUtil.addTime(start_time, park_time);
                        if (order_list.size() > 0) {
                            for (ParkOrderInfo parkOrderInfo : order_list) {
                                if (parkOrderInfo.getOrder_status().equals("1") && dateUtil.betweenStartAndEnd(start_time, parkOrderInfo.getOrder_starttime(), parkOrderInfo.getOrder_endtime())) {
                                    //预约订单
                                    MyToast.showToast(OrderParkActivity.this, "在该时间您已有过预约，请重新选择哦", 5);
                                    return;
                                }
                            }
                        }
                        if (textview_parktime.getText().length() > 0 && park_time > 0) {
                            screenPark();
                        }
                    } else {
                        start_time = "";
                        textview_starttime.setText("");
                        MyToast.showToast(OrderParkActivity.this, "请选择有效时间哦", 5);
                    }
                }
            });
        }
        String[] currentHourAndMinute = DateUtil.getCurrentDate(false).split(":");
        mStartTimeOption.setSelectOptions(0, Integer.valueOf(currentHourAndMinute[0]), Integer.valueOf(currentHourAndMinute[1]));
        mStartTimeOption.show();
    }

    private void showParktimeOptions() {
        textview_ordernow.setBackground(ContextCompat.getDrawable(OrderParkActivity.this, R.drawable.yuan_little_graynall_8dp));
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
                    String tx;
                    if (options1Items.get(options1).equals("0")) {
                        if (options2Items.get(options1).get(option2).equals("0")) {
                            if (options3Items.get(options1).get(option2).get(options3).equals("0")) {
                                MyToast.showToast(OrderParkActivity.this, "请选择有效时段哦", 5);
                                textview_parktime.setText("");
                                park_time = 0;
                            } else {
                                tx = options3Items.get(options1).get(option2).get(options3) + "分钟";
                                textview_parktime.setText(tx);
                                park_time = Integer.parseInt(options1Items.get(options1)) * 60 * 24 + Integer.parseInt(options2Items.get(options1).get(option2)) * 60 + Integer.parseInt(options3Items.get(options1).get(option2).get(options3));
                                end_time = dateUtil.addTime(start_time, park_time);
                                if (order_list.size() > 0) {
                                    for (ParkOrderInfo parkOrderInfo : order_list) {
                                        if (parkOrderInfo.getOrder_status().equals("1") && dateUtil.betweenStartAndEnd(end_time, parkOrderInfo.getOrder_starttime(), parkOrderInfo.getOrder_endtime())) {
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
                                    screenPark();
                                }
                            }
                        } else {
                            tx = options2Items.get(options1).get(option2) + "小时" + options3Items.get(options1).get(option2).get(options3) + "分钟";
                            textview_parktime.setText(tx);
                            park_time = Integer.parseInt(options1Items.get(options1)) * 60 * 24 + Integer.parseInt(options2Items.get(options1).get(option2)) * 60 + Integer.parseInt(options3Items.get(options1).get(option2).get(options3));
                            end_time = dateUtil.addTime(start_time, park_time);
                            if (order_list.size() > 0) {
                                for (ParkOrderInfo parkOrderInfo : order_list) {
                                    if (parkOrderInfo.getOrder_status().equals("1") && dateUtil.betweenStartAndEnd(end_time, parkOrderInfo.getOrder_starttime(), parkOrderInfo.getOrder_endtime())) {
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
                                screenPark();
                            }
                        }
                    } else {
                        tx = options1Items.get(options1) + "天" + options2Items.get(options1).get(option2) + "小时" + options3Items.get(options1).get(option2).get(options3) + "分钟";
                        textview_parktime.setText(tx);
                        park_time = Integer.parseInt(options1Items.get(options1)) * 60 * 24 + Integer.parseInt(options2Items.get(options1).get(option2)) * 60 + Integer.parseInt(options3Items.get(options1).get(option2).get(options3));
                        end_time = dateUtil.addTime(start_time, park_time);
                        if (order_list.size() > 0) {
                            for (ParkOrderInfo parkOrderInfo : order_list) {
                                if (parkOrderInfo.getOrder_status().equals("1") && dateUtil.betweenStartAndEnd(end_time, parkOrderInfo.getOrder_starttime(), parkOrderInfo.getOrder_endtime())) {
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
                            screenPark();
                        }
                    }
                }
            });
        }
        //监听确定选择按钮
        pvOptions.show();
    }

    private void screenPark() {
        Log.e("TAG", "startDate: " + start_time + "  endDate:" + end_time);
        if (mCanParkInfo == null) {
            mCanParkInfo = new LinkedList<>();
        }
        mCanParkInfo.clear();
        mCanParkInfo.addAll(park_list);

        long shareTimeDistance;
        int status;
        for (Park_Info parkInfo : park_list) {
            Log.e("TAG", "screenPark parkInfo:" + parkInfo);

            int currentStatus;
            //排除不在共享日期之内的(根据共享日期)
            if ((currentStatus = DateUtil.isInShareDate(start_time, end_time, parkInfo.getOpen_date())) == 0) {
                mCanParkInfo.remove(parkInfo);
                Log.e(TAG, "screenPark: notInShareDate");
                continue;
            } else {
                status = currentStatus;
            }

            //排除暂停时间在预定时间内的(根据暂停日期)
            if ((currentStatus = DateUtil.isInPauseDate(start_time, end_time, parkInfo.getPauseShareDate())) == 0) {
                mCanParkInfo.remove(parkInfo);
                Log.e(TAG, "screenPark: inPauseDate");
                continue;
            } else {
                if (status != 1) {
                    status = currentStatus;
                }
            }

            //排除预定时间当天不共享的(根据共享星期)
            if ((currentStatus = DateUtil.isInShareDay(start_time, end_time, parkInfo.getShareDay())) == 0) {
                mCanParkInfo.remove(parkInfo);
                Log.e("TAG", "screenPark: notInShareDay");
                continue;
            } else {
                if (status != 1) {
                    status = currentStatus;
                }
            }

            //排除该时间段被别人预约过的(根据车位的被预约时间)
            if (DateUtil.isInOrderDate(start_time, end_time, parkInfo.getOrder_times())) {
                mCanParkInfo.remove(parkInfo);
                Log.e(TAG, "screenPark: isInOrderDate");
                continue;
            }

            Log.e("TAG", "Open_time: " + parkInfo.getOpen_time());
            //排除不在共享时间段内的(根据共享的时间段)
            if ((shareTimeDistance = DateUtil.isInShareTime(start_time, end_time, parkInfo.getOpen_time(), status == 1)) != 0) {
                //获取车位可共享的时间差
                Log.e("TAG", "shareTimeDistance: " + shareTimeDistance);
                int position = mCanParkInfo.indexOf(parkInfo);
                parkInfo.setShareTimeDistance(shareTimeDistance);
                mCanParkInfo.set(position, parkInfo);
            } else {
                mCanParkInfo.remove(parkInfo);
            }
        }

        Collections.sort(mCanParkInfo, new Comparator<Park_Info>() {
            @Override
            public int compare(Park_Info o1, Park_Info o2) {
                long result;

                //根据车位的指标排序
                result = Integer.valueOf(o1.getIndicator()) - Integer.valueOf(o2.getIndicator());
                if (result == 0) {
                    result = DateUtil.getDateMillisDistance(start_time, end_time) + UserManager.getInstance().getUserInfo().getLeave_time() * 60 * 1000;    //停车时间加上顺延时长
                    if ((o1.getShareTimeDistance() - result) >= 0 && (o2.getShareTimeDistance() - result) >= 0) {
                        //如果两个车位的共享时段都大于等于停车时间加上顺延时间则按照车位的共享时段的大小从小到大排序
                        result = (int) (o1.getShareTimeDistance() - o2.getShareTimeDistance());
                    } else if ((o1.getShareTimeDistance() - result) > 0) {
                        result = -1;
                    } else if ((o2.getShareTimeDistance() - result) > 0) {
                        result = -1;
                    } else {
                        result = (o2.getShareTimeDistance() - result) - (o1.getShareTimeDistance() - result);
                    }
                }
                return (int) result;
            }
        });

        Log.e("TAG", "mCanParkInfo: " + mCanParkInfo);
        setOrderFee();
        /*mChooseData.clear();
        int my_leavetime = UserManager.getInstance().getUserInfo().getLeave_time();
        try {
            if (park_time >= 1440) {
                //日租模式
                for (Park_Info info : park_list) {
                    if (info.getOpen_time().equals("00:00 - 23:59")) {
                        String opent1 = info.getOpen_date().substring(0, info.getOpen_date().indexOf(" - ")) + " " + info.getOpen_time().substring(0, info.getOpen_time().indexOf(" - ")),
                                opent2 = info.getOpen_date().substring(info.getOpen_date().indexOf(" - ") + 3, info.getOpen_date().length()) + " " + info.getOpen_time().substring(info.getOpen_time().indexOf(" - ") + 3, info.getOpen_time().length());
                        if (dateUtil.isTheIntervalBeginorEnd(start_time, end_time, opent1, opent2)) {
                            //预定时间在开放时间范围
                            if (info.getOrder_times().equals("-1")) {
                                //未有预定
                                int retime = dateUtil.getTimeDifferenceMinute(end_time, opent2, false);
                                Holder holder = new Holder(info.getId(), start_time + "*" + dateUtil.addTime(start_time, my_leavetime <= retime ? park_time + my_leavetime : park_time + retime), info.getUpdate_time(), retime);
                                mChooseData.add(holder);
                            } else {
                                //有预定
                                String[] ordertimes = info.getOrder_times().split(",");
                                List<String> sad = Arrays.asList(ordertimes);
                                Collections.sort(sad, new Comparator<String>() {
                                    @Override
                                    public int compare(String lhs, String rhs) {
                                        Date date1 = dateUtil.stringToDate(lhs.substring(0, lhs.indexOf("*")));
                                        Date date2 = dateUtil.stringToDate(rhs.substring(0, rhs.indexOf("*")));
                                        // 对日期字段进行升序，如果欲降序可采用after方法
                                        if (date1.after(date2)) {
                                            return 1;
                                        }
                                        return -1;
                                    }
                                });

                                Log.e("车位预定时间排序", sad.toString());

                                for (int i = 0; i < sad.size() + 1; i++) {
                                    String str, ed;//每个空闲时段的开始结束时间
                                    if (i == 0) {
                                        str = opent1;
                                        ed = sad.get(i).substring(0, sad.get(i).indexOf("*"));
                                    } else if (i == sad.size()) {
                                        str = sad.get(i - 1).substring(sad.get(i - 1).indexOf("*") + 1, sad.get(i - 1).length());
                                        ed = opent2;
                                    } else {
                                        str = sad.get(i - 1).substring(sad.get(i - 1).indexOf("*") + 1, sad.get(i - 1).length());
                                        ed = sad.get(i).substring(0, sad.get(i).indexOf("*"));
                                    }
                                    if (dateUtil.isTheIntervalBeginorEnd(start_time, end_time, str, ed)) {
                                        //预定时间在空闲时段内
                                        int retime = dateUtil.getTimeDifferenceMinute(end_time, ed, false);
                                        Holder holder = new Holder(info.getId(), start_time + "*" + dateUtil.addTime(start_time, my_leavetime <= retime ? park_time + my_leavetime : park_time + retime), info.getUpdate_time(), retime);
                                        mChooseData.add(holder);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                //时租模式
                for (Park_Info info : park_list) {
                    if (info.getOpen_time().equals("00:00 - 23:59")) {
                        String opent1 = info.getOpen_date().substring(0, info.getOpen_date().indexOf(" - ")) + " " + info.getOpen_time().substring(0, info.getOpen_time().indexOf(" - ")),
                                opent2 = info.getOpen_date().substring(info.getOpen_date().indexOf(" - ") + 3, info.getOpen_date().length()) + " " + info.getOpen_time().substring(info.getOpen_time().indexOf(" - ") + 3, info.getOpen_time().length());
                        if (dateUtil.isTheIntervalBeginorEnd(start_time, end_time, opent1, opent2)) {
                            //预定时间在开放时间范围
                            if (info.getOrder_times().equals("-1")) {
                                //未有预定
                                int retime = dateUtil.getTimeDifferenceMinute(end_time, opent2, false);
                                Holder holder = new Holder(info.getId(), start_time + "*" + dateUtil.addTime(start_time, my_leavetime <= retime ? park_time + my_leavetime : park_time + retime), info.getUpdate_time(), retime);
                                mChooseData.add(holder);
                            } else {
                                //有预定
                                String[] ordertimes = info.getOrder_times().split(",");
                                List<String> sad = Arrays.asList(ordertimes);
                                Collections.sort(sad, new Comparator<String>() {
                                    @Override
                                    public int compare(String lhs, String rhs) {
                                        Date date1 = dateUtil.stringToDate(lhs.substring(0, lhs.indexOf("*")));
                                        Date date2 = dateUtil.stringToDate(rhs.substring(0, rhs.indexOf("*")));
                                        // 对日期字段进行升序，如果欲降序可采用after方法
                                        if (date1.after(date2)) {
                                            return 1;
                                        }
                                        return -1;
                                    }
                                });

                                for (int i = 0; i < sad.size() + 1; i++) {
                                    String str, ed;//每个空闲时段的开始结束时间
                                    if (i == 0) {
                                        str = opent1;
                                        ed = sad.get(i).substring(0, sad.get(i).indexOf("*"));
                                    } else if (i == sad.size()) {
                                        str = sad.get(i - 1).substring(sad.get(i - 1).indexOf("*") + 1, sad.get(i - 1).length());
                                        ed = opent2;
                                    } else {
                                        str = sad.get(i - 1).substring(sad.get(i - 1).indexOf("*") + 1, sad.get(i - 1).length());
                                        ed = sad.get(i).substring(0, sad.get(i).indexOf("*"));
                                    }
                                    if (dateUtil.isTheIntervalBeginorEnd(start_time, end_time, str, ed)) {
                                        //预定时间在空闲时段内
                                        int retime = dateUtil.getTimeDifferenceMinute(end_time, ed, false);
                                        Holder holder = new Holder(info.getId(), start_time + "*" + dateUtil.addTime(start_time, my_leavetime <= retime ? park_time + my_leavetime : park_time + retime), info.getUpdate_time(), retime);
                                        mChooseData.add(holder);
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        String opentime_hour1 = info.getOpen_time().substring(0, info.getOpen_time().indexOf(":")),//车位开放开始时间的小时
                                opentime_hour2 = (info.getOpen_time().substring(info.getOpen_time().indexOf(" - ") + 3, info.getOpen_time().lastIndexOf(":"))); //车位开放结束时间的小时
                        String opentime_min1 = info.getOpen_time().substring(info.getOpen_time().indexOf(":") + 1, info.getOpen_time().indexOf(" - ")),//车位开放开始时间的分钟
                                opentime_min2 = (info.getOpen_time().substring(info.getOpen_time().lastIndexOf(":") + 1, info.getOpen_time().length())); //车位开放结束时间的分钟
                        String tdaytime1 = start_time.substring(0, start_time.indexOf(" ")) + " " + info.getOpen_time().substring(0, info.getOpen_time().indexOf(" - ")),
                                tdaytime2 = start_time.substring(0, start_time.indexOf(" ")) + " " + info.getOpen_time().substring(info.getOpen_time().indexOf(" - ") + 3, info.getOpen_time().length());
                        String time_frame = "2125-01-01 00:00,2125-01-01 23:59";//确定预定当天时间范围
                        if (Integer.parseInt(opentime_hour1) > Integer.parseInt(opentime_hour2)) {
                            //是跨天
                            String adddaytime = addOneDay(start_time, info),
                                    reducedaytime = reduceOneDay(start_time, info);
                            if (dateUtil.betweenStartAndEnd(start_time, tdaytime1, adddaytime)) {
                                //跨今天到明天
                                time_frame = tdaytime1 + "," + adddaytime;
                            } else if (dateUtil.betweenStartAndEnd(start_time, reducedaytime, tdaytime2)) {
                                //跨昨天到今天
                                time_frame = reducedaytime + "," + tdaytime2;
                            }
                        } else if (Integer.parseInt(opentime_hour1) < Integer.parseInt(opentime_hour2)) {
                            //是同天
                            time_frame = tdaytime1 + "," + tdaytime2;
                        } else if (Integer.parseInt(opentime_hour1) == Integer.parseInt(opentime_hour2)) {
                            if (Integer.parseInt(opentime_min1) > Integer.parseInt(opentime_min2)) {
                                //是奇葩跨天
                                String adddaytime = addOneDay(start_time, info),
                                        reducedaytime = reduceOneDay(start_time, info);
                                if (dateUtil.betweenStartAndEnd(start_time, tdaytime1, adddaytime)) {
                                    //跨今天到明天
                                    time_frame = tdaytime1 + "," + adddaytime;
                                } else if (dateUtil.betweenStartAndEnd(start_time, reducedaytime, tdaytime2)) {
                                    //跨昨天到今天
                                    time_frame = reducedaytime + "," + tdaytime2;
                                }
                            } else {
                                //是奇葩同天
                                time_frame = tdaytime1 + "," + tdaytime2;
                            }
                        }
                        String time_frame1 = time_frame.substring(0, time_frame.indexOf(",")),
                                time_frame2 = time_frame.substring(time_frame.indexOf(",") + 1, time_frame.length());
                        if (dateUtil.isTheIntervalBeginorEnd(start_time, end_time, time_frame1, time_frame2)) {
                            //预定时间在开放时间范围
                            if (info.getOrder_times().equals("-1")) {
                                //未有预定
                                int retime = dateUtil.getTimeDifferenceMinute(end_time, time_frame2, false);
                                Holder holder = new Holder(info.getId(), start_time + "*" + dateUtil.addTime(start_time, my_leavetime <= retime ? park_time + my_leavetime : park_time + retime), info.getUpdate_time(), retime);
                                mChooseData.add(holder);
                            } else {
                                //有预定
                                String[] allordertimes = info.getOrder_times().split(",");
                                ArrayList<String> ordertimes = new ArrayList<>();
                                for (int a = 0; a < allordertimes.length; a++) {
                                    String sty = allordertimes[a].substring(0, allordertimes[a].indexOf("*")),
                                            ety = allordertimes[a].substring(allordertimes[a].indexOf("*") + 1, allordertimes[a].length());
                                    if (dateUtil.isTheIntervalBeginorEnd(sty, ety, time_frame1, time_frame2)) {
                                        //所有已预约的时间在开放时间范围的
                                        ordertimes.add(allordertimes[a]);
                                    }
                                }
                                if (ordertimes.size() == 0) {
                                    //开放时间范围内没有预定
                                    int retime = dateUtil.getTimeDifferenceMinute(end_time, time_frame2, false);
                                    Holder holder = new Holder(info.getId(), start_time + "*" + dateUtil.addTime(start_time, my_leavetime <= retime ? park_time + my_leavetime : park_time + retime), info.getUpdate_time(), retime);
                                    mChooseData.add(holder);
                                } else {
                                    //开放时间范围内有预定
                                    Collections.sort(ordertimes, new Comparator<String>() {
                                        @Override
                                        public int compare(String lhs, String rhs) {
                                            Date date1 = dateUtil.stringToDate(lhs.substring(0, lhs.indexOf("*")));
                                            Date date2 = dateUtil.stringToDate(rhs.substring(0, rhs.indexOf("*")));
                                            // 对日期字段进行升序，如果欲降序可采用after方法
                                            if (date1.after(date2)) {
                                                return 1;
                                            }
                                            return -1;
                                        }
                                    });

                                    for (int i = 0; i < ordertimes.size() + 1; i++) {
                                        String str, ed;//每个空闲时段的开始结束时间
                                        if (i == 0) {
                                            str = time_frame1;
                                            ed = ordertimes.get(i).substring(0, ordertimes.get(i).indexOf("*"));
                                        } else if (i == ordertimes.size()) {
                                            str = ordertimes.get(i - 1).substring(ordertimes.get(i - 1).indexOf("*") + 1, ordertimes.get(i - 1).length());
                                            ed = time_frame2;
                                        } else {
                                            str = ordertimes.get(i - 1).substring(ordertimes.get(i - 1).indexOf("*") + 1, ordertimes.get(i - 1).length());
                                            ed = ordertimes.get(i).substring(0, ordertimes.get(i).indexOf("*"));
                                        }
                                        if (dateUtil.isTheIntervalBeginorEnd(start_time, end_time, str, ed)) {
                                            //预定时间在空闲时段内
                                            int retime = dateUtil.getTimeDifferenceMinute(end_time, ed, false);
                                            Holder holder = new Holder(info.getId(), start_time + "*" + dateUtil.addTime(start_time, my_leavetime <= retime ? park_time + my_leavetime : park_time + retime), info.getUpdate_time(), retime);
                                            mChooseData.add(holder);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (textview_carnumble.getText().length() > 0 && mChooseData.size() > 0) {
                textview_ordernow.setBackground(getResources().getDrawable(R.drawable.little_yuan_yellow_8dp));
                textview_ordernow.setTextColor(ContextCompat.getColor(OrderParkActivity.this, R.color.b1));
                try {
                    DateUtil.ParkFee parkFee = dateUtil.countCost(start_time, end_time, mChooseData.get(0).parktime_qujian.substring(mChooseData.get(0).parktime_qujian.indexOf("*") + 1, mChooseData.get(0).parktime_qujian.length()), parkspace_info.getHigh_time().substring(0, parkspace_info.getHigh_time().indexOf(" - ")), parkspace_info.getHigh_time().substring(parkspace_info.getHigh_time().indexOf(" - ") + 3, parkspace_info.getHigh_time().length()), parkspace_info.getHigh_fee(), parkspace_info.getLow_fee(), parkspace_info.getFine());
                    textview_fee.setText("约￥" + parkFee.parkfee);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                textview_fee.setText("约￥0.00");
                textview_ordernow.setBackground(getResources().getDrawable(R.drawable.yuan_little_graynall_8dp));
                textview_ordernow.setTextColor(ContextCompat.getColor(OrderParkActivity.this, R.color.w0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void setOrderFee() {
        if (textview_carnumble.getText().length() > 0 && mCanParkInfo.size() > 0) {
            textview_ordernow.setBackground(getResources().getDrawable(R.drawable.little_yuan_yellow_8dp));
            textview_ordernow.setTextColor(ContextCompat.getColor(OrderParkActivity.this, R.color.b1));
            double orderFee = DateUtil.caculateParkFee(start_time, end_time, mCanParkInfo.get(0).getHigh_time(),
                    Double.valueOf(mCanParkInfo.get(0).getHigh_fee()),
                    Double.valueOf(mCanParkInfo.get(0).getLow_fee()));
            textview_fee.setText("约￥" + mDecimalFormat.format(orderFee));
        } else {
            textview_fee.setText("约￥0.00");
            textview_ordernow.setBackground(getResources().getDrawable(R.drawable.yuan_little_graynall_8dp));
            textview_ordernow.setTextColor(ContextCompat.getColor(OrderParkActivity.this, R.color.w0));
        }
    }

    private class Holder {
        private String park_id, parktime_qujian, update_time;
        private int rest_time;

        Holder(String park_id, String parktime_qujian, String update_time, int rest_time) {
            this.park_id = park_id;
            this.parktime_qujian = parktime_qujian;
            this.update_time = update_time;
            this.rest_time = rest_time;
        }
    }

    private String reduceOneDay(String thedate, Park_Info info) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date date = dateFormat.parse(thedate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);//让日期减1
            String month = (calendar.get(Calendar.MONTH) + 1) >= 10 ? String.valueOf((calendar.get(Calendar.MONTH) + 1)) : "0" + String.valueOf((calendar.get(Calendar.MONTH) + 1));
            return calendar.get(Calendar.YEAR) + "-" + month + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " " + info.getOpen_time().substring(0, info.getOpen_time().indexOf(" - "));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String addOneDay(String thedate, Park_Info info) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date date = dateFormat.parse(thedate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);//让日期加1
            return calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " " + info.getOpen_time().substring(info.getOpen_time().indexOf(" - ") + 3, info.getOpen_time().length());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void login() {
        loginDialogFragment = new LoginDialogFragment();
        loginDialogFragment.show(getSupportFragmentManager(), "hahah");
    }

    private void showAlertDialog() {
        //给mChooseData进行排序
        /*Collections.sort(mChooseData, new Comparator<Holder>() {
            @Override
            public int compare(Holder o1, Holder o2) {
                return Integer.valueOf(o1.rest_time).compareTo(o2.rest_time);
            }
        });

        Holder ctpark = null;
        int pos = 0;
        for (int i = 0; i < mChooseData.size(); i++) {
            if (mChooseData.get(i).rest_time >= UserManager.getInstance().getUserInfo().getLeave_time()) {
                ctpark = mChooseData.get(i);
                pos = i;
                break;
            }
        }

        final ArrayList<Holder> readypark = new ArrayList<>();
        if (ctpark == null) {
            if (mChooseData.size() > 1) {
                readypark.add(mChooseData.get(mChooseData.size() - 1));
                readypark.add(mChooseData.get(mChooseData.size() - 2));
            } else {
                readypark.add(mChooseData.get(mChooseData.size() - 1));
            }
            if (readypark.get(0).rest_time <= 20) {
                builder.setMessage("可分配车位的顺延时长较短\n" + "顺延时长为" + readypark.get(0).rest_time + "分钟，是否预定？");
            } else {
                builder.setMessage("最优车位顺延时长为" + readypark.get(0).rest_time + "分钟，是否预定？");
            }
        } else {
            builder.setMessage("将为您预定最优车位，确认预定？");
            if (pos > 1) {
                readypark.add(mChooseData.get(pos - 1));
                readypark.add(mChooseData.get(pos - 2));
            } else if (pos > 0) {
                readypark.add(mChooseData.get(pos - 1));
            } else if (pos == 0 && mChooseData.size() == 2) {
                readypark.add(mChooseData.get(1));
            } else if (pos == 0 && mChooseData.size() == 3) {
                readypark.add(mChooseData.get(1));
                readypark.add(mChooseData.get(2));
            }
        }
        Log.e("哈哈哈", "时间范围是" + pos + "   " + mChooseData.size() + "   " + readypark.size() + "   " + (readypark.size() > 0 ? (readypark.get(0).park_id + (readypark.size() > 1 ? "," + readypark.get(1).park_id : "")) : ""));
        final Holder perfectpark = ctpark;*/
        final TipeDialog.Builder builder = new TipeDialog.Builder(OrderParkActivity.this);
        long time = DateUtil.getDateMillisDistance(start_time, end_time) + UserManager.getInstance().getUserInfo().getLeave_time() * 60 * 1000;
        Log.e("TAG", "showAlertDialog MillisDistance: " + DateUtil.getDateMillisDistance(start_time, end_time) + " time:" + time);
        Log.e("TAG", "showAlertDialog shareTime: " + mCanParkInfo.get(0).getShareTimeDistance());
        if (mCanParkInfo.get(0).getShareTimeDistance() - time >= 0) {
            builder.setMessage("最优车位顺延时长为" + UserManager.getInstance().getUserInfo().getLeave_time() + "分钟，是否预定？");
        } else {
            builder.setMessage("可分配车位顺延时长为" + mCanParkInfo.get(0).getShareTimeDistance() / 1000 / 60 + "分钟，是否预定？");
        }

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

    private void addNewParkOrder() {
        StringBuilder readyParkId = new StringBuilder();
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

        OkGo.post(HttpConstants.addNewParkOrder)
                .tag(OrderParkActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("parkspace_id", parkspace_info.getId())
                .params("park_id", mCanParkInfo.get(0).getId())
                .params("car_number", textview_carnumble.getText().toString())
                .params("park_interval", start_time + "*" + end_time)
                .params("park_updatetime", mCanParkInfo.get(0).getUpdate_time())
                .params("readypark_id", readyParkId.toString())
                .params("readypark_updatetime", readyParkUpdateTime.toString())
                .params("citycode", parkspace_info.getCity_code())
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> responseData, Call call, Response response) {
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        MyToast.showToast(OrderParkActivity.this, "预约成功", 5);
                        Intent intent = new Intent(OrderParkActivity.this, ParkOrderDetailsActivity.class);
                        intent.putExtra("parkorder_number", responseData.data.getOrder_number());
                        intent.putExtra("citycode", parkspace_info.getCity_code());
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        if (!DensityUtil.isException(OrderParkActivity.this, e)) {
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
                                    //
                                    showRequestAppointOrderDialog(mCanParkInfo.get(1));
                                    break;
                                case 106:
                                    showRequestAppointOrderDialog(mCanParkInfo.get(2));
                                    break;
                                case 102:
                                    //
                                    MyToast.showToast(OrderParkActivity.this, "未匹配到合适您时间的车位，请尝试更换时间", 5);
                                    break;
                                case 104:
                                    //
                                    MyToast.showToast(OrderParkActivity.this, "您有效订单已达上限，暂不可预约车位哦", 5);
                                    break;
                                case 105:
                                    //
                                    MyToast.showToast(OrderParkActivity.this, "您当前车位在该时段内已有过预约，请尝试更换时间", 5);
                                    break;
                                case 107:
                                    //
                                    MyToast.showToast(OrderParkActivity.this, "您有订单需要前去付款，要先处理哦", 5);
                                    break;
                                case 901:
                                    MyToast.showToast(OrderParkActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }

   /* private void sendOrder(Holder park, final ArrayList<Holder> readypark) {

        OkGo.post(HttpConstants.addNewParkOrder)
                .tag(OrderParkActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("parkspace_id", parkspace_info.getId())
                .params("park_id", park.park_id)
                .params("car_number", textview_carnumble.getText().toString())
                .params("park_interval", park.parktime_qujian)
                .params("park_updatetime", park.update_time)
                .params("readypark_id", readypark.size() > 0 ? (readypark.get(0).park_id + (readypark.size() > 1 ? "," + readypark.get(1).park_id : "")) : "")
                .params("readypark_updatetime", readypark.size() > 0 ? readypark.get(0).update_time + (readypark.size() > 1 ? "," + readypark.get(1).update_time : "") : "")
                .params("citycode", parkspace_info.getCity_code())
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> responseData, Call call, Response response) {
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        MyToast.showToast(OrderParkActivity.this, "预约成功", 5);
                        Intent intent = new Intent(OrderParkActivity.this, ParkOrderDetailsActivity.class);
                        intent.putExtra("parkorder_number", responseData.data.getOrder_number());
                        intent.putExtra("citycode", parkspace_info.getCity_code());
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        if (!DensityUtil.isException(OrderParkActivity.this, e)) {
                            int code = Integer.parseInt(e.getMessage());
                            TipeDialog.Builder builder;
                            switch (code) {
                                case 101:
                                    //
                                    builder = new TipeDialog.Builder(OrderParkActivity.this);
                                    builder.setMessage("最优车位顺延时长为" + readypark.get(0).rest_time + "分钟，是否预定？");
                                    builder.setTitle("确认预定");
                                    builder.setPositiveButton("立即预定", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            initLoading("提交中...");
                                            requestAppointOrderLockPark(readypark.get(0));
                                        }
                                    });

                                    builder.setNegativeButton("取消",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            });

                                    builder.create().show();
                                    break;
                                case 106:
                                    //
                                    builder = new TipeDialog.Builder(OrderParkActivity.this);
                                    builder.setMessage("最优车位顺延时长为" + readypark.get(1).rest_time + "分钟，是否预定？");
                                    builder.setTitle("确认预定");
                                    builder.setPositiveButton("立即预定", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            initLoading("提交中...");
                                            requestAppointOrderLockPark(readypark.get(1));
                                        }
                                    });

                                    builder.setNegativeButton("取消",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            });

                                    builder.create().show();
                                    break;
                                case 102:
                                    //
                                    MyToast.showToast(OrderParkActivity.this, "未匹配到合适您时间的车位，请尝试更换时间", 5);
                                    break;
                                case 104:
                                    //
                                    MyToast.showToast(OrderParkActivity.this, "您有效订单已达上限，暂不可预约车位哦", 5);
                                    break;
                                case 105:
                                    //
                                    MyToast.showToast(OrderParkActivity.this, "您当前车位在该时段内已有过预约，请尝试更换时间", 5);
                                    break;
                                case 107:
                                    //
                                    MyToast.showToast(OrderParkActivity.this, "您有订单需要前去付款，要先处理哦", 5);
                                    break;
                                case 901:
                                    MyToast.showToast(OrderParkActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }*/

    private void showRequestAppointOrderDialog(final Park_Info park_info) {
        TipeDialog.Builder builder = new TipeDialog.Builder(this);
        long time = DateUtil.getDateMillisDistance(start_time, end_time) + UserManager.getInstance().getUserInfo().getLeave_time() * 60 * 1000;
        if (park_info.getShareTimeDistance() - time >= 0) {
            builder.setMessage("最优车位顺延时长为" + UserManager.getInstance().getUserInfo().getLeave_time() + "分钟，是否预定？");
        } else {
            builder.setMessage("可分配车位顺延时长为" + park_info.getShareTimeDistance() / 1000 / 60 + "分钟，是否预定？");
        }
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
                .params("parkspace_id", parkspace_info.getId())
                .params("car_number", textview_carnumble.getText().toString())
                .params("park_id", park_info.getId())
                .params("park_interval", start_time + "*" + end_time)
                .params("citycode", parkspace_info.getCity_code())
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> responseData, Call call, Response response) {
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        MyToast.showToast(OrderParkActivity.this, "预约成功", 5);
                        Intent intent = new Intent(OrderParkActivity.this, ParkOrderDetailsActivity.class);
                        intent.putExtra("parkorder_number", responseData.data.getOrder_number());
                        intent.putExtra("citycode", parkspace_info.getCity_code());
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
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

    /*private void requestAppointOrderLockPark(Holder lockpark) {

        OkGo.post(HttpConstants.appointOrderLockPark)
                .tag(OrderParkActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("parkspace_id", parkspace_info.getId())
                .params("car_number", textview_carnumble.getText().toString())
                .params("park_id", lockpark.park_id)
                .params("park_interval", lockpark.parktime_qujian)
                .params("citycode", parkspace_info.getCity_code())
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> responseData, Call call, Response response) {
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        MyToast.showToast(OrderParkActivity.this, "预约成功", 5);
                        Intent intent = new Intent(OrderParkActivity.this, ParkOrderDetailsActivity.class);
                        intent.putExtra("parkorder_number", responseData.data.getOrder_number());
                        intent.putExtra("citycode", parkspace_info.getCity_code());
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
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
    }*/

    private void initLoading(String what) {
        mCustomDialog = new CustomDialog(this, what);
        mCustomDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCustomDialog != null) {
            mCustomDialog.cancel();
        }
        mHandler.removeCallbacksAndMessages(null);
    }
}
