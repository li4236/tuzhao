package com.tuzhao.fragment.parkorder;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.BigPictureActivity;
import com.tuzhao.activity.jiguang_notification.MyReceiver;
import com.tuzhao.activity.jiguang_notification.OnCtrlLockListener;
import com.tuzhao.activity.mine.BillingRuleActivity;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.TimeUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/5/29.
 */

public class AppointmentDetailFragment extends BaseStatusFragment implements View.OnClickListener {

    private ParkOrderInfo mParkOrderInfo;

    private TextView mParkDate;

    private TextView mStartParkTime;

    private TextView mParkSpaceLocation;

    private TextView mParkDuration;

    private TextView mOpenLock;

    private ArrayList<String> mParkSpacePictures;

    private boolean mIsTimeOut;

    private CustomDialog mCustomDialog;

    private TimeUtil mTimeUtil;

    private long mInterval = 60 * 1000;

    private Calendar mCurrentCalendar;

    private Calendar mOrderStartCalendar;

    private Handler mHandler;

    private static final int TIME_IN_MILLISS = 0x123;

    public static AppointmentDetailFragment newInstance(ParkOrderInfo parkOrderInfo) {
        AppointmentDetailFragment fragment = new AppointmentDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstansUtil.PARK_ORDER_INFO, parkOrderInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int resourceId() {
        return R.layout.fragment_appointment_detail_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mParkOrderInfo = (ParkOrderInfo) getArguments().getSerializable(ConstansUtil.PARK_ORDER_INFO);
        }

        mParkDate = view.findViewById(R.id.appointment_park_date);
        mStartParkTime = view.findViewById(R.id.appointment_income_time);
        mParkSpaceLocation = view.findViewById(R.id.appointment_park_location);
        mParkDuration = view.findViewById(R.id.park_duration);
        mOpenLock = view.findViewById(R.id.open_lock);

        view.findViewById(R.id.appointment_calculate_rule).setOnClickListener(this);
        view.findViewById(R.id.appointment_calculate_rule_iv).setOnClickListener(this);
        view.findViewById(R.id.car_pic_cl).setOnClickListener(this);
        view.findViewById(R.id.cancel_appoint_cl).setOnClickListener(this);
        view.findViewById(R.id.contact_service_cl).setOnClickListener(this);
        view.findViewById(R.id.view_appointment_detail).setOnClickListener(this);
        view.findViewById(R.id.view_appointment_detail_iv).setOnClickListener(this);
        mOpenLock.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mParkDate.setText(DateUtil.getMonthToDay(mParkOrderInfo.getOrder_starttime()));
        mStartParkTime.setText(DateUtil.getHourToMinute(mParkOrderInfo.getOrder_starttime()));
        mParkSpaceLocation.setText(mParkOrderInfo.getAddress_memo());
        mParkDuration.setText(DateUtil.getDistanceForDayTimeMinute(mParkOrderInfo.getOrder_starttime(), mParkOrderInfo.getOrder_endtime()));

        mCurrentCalendar = Calendar.getInstance();
        mOrderStartCalendar = DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_starttime());
        initHandler();
        startPollingTime();

        OnCtrlLockListener lockListener = new OnCtrlLockListener() {
            @Override
            public void onCtrlLock(String ctrlMessage) {
                try {
                    JSONObject jsonObject = new JSONObject(ctrlMessage);
                    if (jsonObject.optString("type").equals("ctrl")) {
                        if (jsonObject.optString("msg").equals("open_successful")) {
                            showFiveToast("成功开锁");
                            mOpenLock.setText("已开锁");
                            finishAppointment(mParkOrderInfo);
                        } else if (jsonObject.optString("msg").equals("open_successful_car")) {
                            showFiveToast("车锁已开，因为车位上方有车辆滞留");
                            mOpenLock.setText("已开锁");
                            handleOpenLock();
                        }
                        mOpenLock.setClickable(true);
                        dismmisLoadingDialog();
                    }
                } catch (Exception e) {
                    dismmisLoadingDialog();
                    mOpenLock.setClickable(true);
                    showFiveToast("开锁失败，请稍后重试");
                }
            }
        };
        MyReceiver.setOnCtrlLockListener(lockListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MyReceiver.setOnCtrlLockListener(null);
        if (mTimeUtil != null) {
            mTimeUtil.cancel();
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appointment_calculate_rule:
            case R.id.appointment_calculate_rule_iv:
                Bundle bundle = new Bundle();
                bundle.putString(ConstansUtil.PARK_LOT_ID, mParkOrderInfo.getBelong_park_space());
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
                TipeDialog dialog = new TipeDialog.Builder(getContext())
                        .setTitle("提示")
                        .setMessage("确定取消预约吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelAppointment();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                dialog.show();
                break;
            case R.id.contact_service_cl:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:4006505058"));
                startActivity(intent);
                break;
            case R.id.view_appointment_detail:
            case R.id.view_appointment_detail_iv:
                showAppointmentDetail();
                break;
            case R.id.open_lock:
                if (getText(mOpenLock).equals("已开锁")) {
                    showFiveToast("锁已经开啦");
                } else if (!mIsTimeOut) {
                    showFiveToast("还没到开始停车时间哦");
                } else {
                    openParkLock();
                }
                break;
        }
    }

    /**
     * 获取网络当前时间的时间戳
     */
    private void getCurrentTimeInMillis() {
        try {
            URL url = new URL("http://www.baidu.com");// 取得资源对象
            URLConnection uc = url.openConnection();// 生成连接对象
            uc.connect();// 发出连接
            Message message = mHandler.obtainMessage();
            message.what = TIME_IN_MILLISS;
            message.obj = uc.getDate();// 读取网站日期时间
            mHandler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initHandler() {
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TIME_IN_MILLISS) {
                    mCurrentCalendar.setTimeInMillis((long) msg.obj);
                    if (mOrderStartCalendar.compareTo(mCurrentCalendar) > 0) {
                        //未到预约开始停车时间
                        mOpenLock.setBackgroundResource(R.drawable.all_g1_5dp);
                        if (mInterval == 60 * 1000) {
                            caculateIntervalTime();
                            mTimeUtil.setInterval(mInterval);
                        }
                    } else {
                        mIsTimeOut = true;
                        mOpenLock.setBackgroundResource(R.drawable.little_yuan_yellow_5dp);
                        mTimeUtil.cancel();
                        mTimeUtil = null;
                    }
                }
                return true;
            }
        });
    }

    private void caculateIntervalTime() {
        long intervalTime = (mCurrentCalendar.getTimeInMillis() - mOrderStartCalendar.getTimeInMillis());
        if (intervalTime >= 60 * 1000) {
            mInterval = intervalTime / 2;
        } else {
            mInterval = Math.abs(intervalTime) + 1;     //防止等于0报错
        }
    }

    private void startPollingTime() {
        mTimeUtil = new TimeUtil(mInterval, new TimeUtil.TimeCallback() {
            @Override
            public void onTimeIn() {
                getCurrentTimeInMillis();
            }
        });
        mTimeUtil.start();
    }

    private void cancelAppointment() {
        showLoadingDialog("取消预约");
        getOkGo(HttpConstants.cancleAppointOrder)
                .params("order_id", mParkOrderInfo.getId())
                .params("citycode", mParkOrderInfo.getCitycode())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        Intent intent = new Intent();
                        intent.setAction(ConstansUtil.CANCEL_ORDER);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
                        intent.putExtra(ConstansUtil.FOR_REQUEST_RESULT, bundle);
                        IntentObserable.dispatch(intent);
                        dismmisLoadingDialog();
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast(e.getMessage());
                        }
                    }
                });
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
        if (mCustomDialog == null) {
            if (getContext() != null) {
                mCustomDialog = new CustomDialog(getContext(), mParkOrderInfo);
            }
        }
        mCustomDialog.show();
    }

    private void openParkLock() {
        mOpenLock.setClickable(false);
        showLoadingDialog("正在开锁");
        OkGo.post(HttpConstants.controlParkLock)
                .tag(TAG)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("ctrl_type", "1")
                .params("order_id", mParkOrderInfo.getId())
                .params("citycode", mParkOrderInfo.getCitycode())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> aVoid, Call call, Response response) {

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mOpenLock.setClickable(true);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    showFiveToast("设备暂时离线，请稍后重试");
                                    break;
                                default:
                                    showFiveToast("开锁失败，请稍后重试");
                                    break;
                            }
                        }
                    }
                });
    }

    /**
     * 处理开锁时已有人停车的情况
     */
    private void handleOpenLock() {
        if (mParkOrderInfo.getParkingUserId().equals("-1")) {
            //正在停车用户id为-1，可能是后台帮忙开的，但是这里还没更新
            getParkInfo();
        } else if (mParkOrderInfo.getParkingUserId().equals(UserManager.getInstance().getUserInfo().getId())) {
            finishAppointment(mParkOrderInfo);
        } else {
            //有人延迟停车还没走
            // TODO: 2018/6/6
        }
    }

    private void getParkInfo() {
        getOkGo(HttpConstants.getKindParkOrder)
                .params("order_status", "2")
                .params("pageSize", 3)
                .params("startItme", 0)
                .execute(new JsonCallback<Base_Class_List_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<ParkOrderInfo> o, Call call, Response response) {
                        //如果停车中有订单id和当前预约的一样，则把界面更换到停车中
                        for (ParkOrderInfo parkOrderInfo : o.data) {
                            if (parkOrderInfo.getId().equals(mParkOrderInfo.getId())) {
                                finishAppointment(parkOrderInfo);
                                break;
                            }
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

    private void finishAppointment(ParkOrderInfo parkOrderInfo) {
        Intent intent = new Intent();
        intent.setAction(ConstansUtil.FINISH_APPOINTMENT);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstansUtil.PARK_ORDER_INFO, parkOrderInfo);
        intent.putExtra(ConstansUtil.FOR_REQUEST_RESULT, bundle);
        IntentObserable.dispatch(intent);
    }

}
