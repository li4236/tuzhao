package com.tuzhao.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.View;
import android.widget.TextView;

import com.amap.api.navi.model.NaviLatLng;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.activity.jiguang_notification.MyReceiver;
import com.tuzhao.activity.jiguang_notification.OnLockListener;
import com.tuzhao.activity.mine.NavigationActivity;
import com.tuzhao.fragment.base.BaseRefreshFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.LocationManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.customView.ArrowView;
import com.tuzhao.publicwidget.customView.CircleView;
import com.tuzhao.publicwidget.customView.SkipTopBottomDivider;
import com.tuzhao.publicwidget.dialog.OpenLockDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DeviceUtils;
import com.tuzhao.utils.IntentObserable;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/8/30.
 */
public class UseFriendParkSpaceRecordFragment extends BaseRefreshFragment<ParkOrderInfo> {

    private static final int TIME_IN_MILLISS = 0x123;

    private static final int GET_TIME_FAILE = 0x456;

    private String mStatus;

    private boolean mIsOpening;

    private OpenLockDialog mOpenLockDialog;

    /**
     * 正在开锁的订单
     */
    private ParkOrderInfo mOpenLockOrder;

    private Handler mHandler;

    public static UseFriendParkSpaceRecordFragment getInstance(String status) {
        UseFriendParkSpaceRecordFragment fragment = new UseFriendParkSpaceRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ConstansUtil.STATUS, status);
        fragment.setArguments(bundle);
        if (status.equals("1")) {
            fragment.setTAG("预定中");
        } else {
            fragment.setTAG("历史记录");
        }
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        if (getArguments() != null) {
            mStatus = getArguments().getString(ConstansUtil.STATUS);
        }
        if ("0".equals(mStatus)) {
            mRecyclerView.setEmptyView(R.drawable.ic_noreview, "你没有正在预定中的记录哦");
        } else {
            mRecyclerView.setEmptyView(R.drawable.ic_noreview, "你没有历史记录哦");
        }
        mRecyclerView.addItemDecoration(new SkipTopBottomDivider(getContext(), true, true));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for (int i = 0; i < mCommonAdapter.getDataSize(); i++) {
            //因为有可能是从预约状态改为停车中状态的，所以需要全部遍历移除监听
            MyReceiver.removeLockListener(mCommonAdapter.get(i).getLockId());
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void loadData() {
        getOkgos(HttpConstants.getFriendParkSpaceRecord, "status", mStatus)
                .execute(new JsonCallback<Base_Class_List_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<ParkOrderInfo> o, Call call, Response response) {
                        loadDataSuccess(o);
                        for (ParkOrderInfo parkOrderInfo : o.data) {
                            if (parkOrderInfo.getOrderStatus().equals("0")) {
                                //预定中的添加开锁监听
                                addLockListener(parkOrderInfo);
                            }
                        }
                        initHandler();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        loadDataFail(e, new LoadFailCallback() {
                            @Override
                            public void onLoadFail(Exception e) {
                                switch (e.getMessage()) {
                                    case "101":
                                    case "102":
                                    case "103":
                                        showFiveToast(ConstansUtil.SERVER_ERROR);
                                        break;
                                }
                            }
                        });
                    }
                });
    }

    private void initHandler() {
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TIME_IN_MILLISS) {
                    //获取到网络时间
                    if ((Long) msg.obj >= DateUtil.getYearToSecondCalendar(mOpenLockOrder.getOrderStartTime(), "-1800").getTimeInMillis()) {
                        openLockByRecord(mOpenLockOrder);
                    } else {
                        showFiveToast("预定开始停车前30分钟才可以开锁哦");
                        mOpenLockOrder = null;
                    }
                } else if (msg.what == GET_TIME_FAILE) {
                    //获取网络时间失败，使用本地时间
                    if (((Long) msg.obj - System.currentTimeMillis()) / 1000 <= 30 * 60) {
                        openLockByRecord(mOpenLockOrder);
                    } else {
                        showFiveToast("预定开始停车前30分钟才可以开锁哦");
                        mOpenLockOrder = null;
                    }
                }
                return true;
            }
        });
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
            message.obj = uc.getDate();// 读取网站日期时间,时间戳
            mHandler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
            Message message = mHandler.obtainMessage();
            message.what = GET_TIME_FAILE;
            mHandler.sendMessage(message);
        }
    }

    /**
     * @param parkOrderInfo 为每个车锁添加监听器，监听开锁结果
     */
    private void addLockListener(final ParkOrderInfo parkOrderInfo) {
        OnLockListener lockListener = new OnLockListener() {
            @Override
            public void openSuccess() {
                mOpenLockDialog.setOpenLockStatus(0);
                mIsOpening = false;
                mOpenLockDialog.cancelOpenLockAnimator();
                if (!mOpenLockDialog.isShowing()) {
                    parkOrderInfo.setOrderStatus("1");
                    parkOrderInfo.setParkStartTime(DateUtil.getCurrentYearToMinutes());
                    mCommonAdapter.notifyDataChange(parkOrderInfo);
                    showFiveToast("开锁成功");
                }
            }

            @Override
            public void openFailed() {
                mOpenLockDialog.setOpenLockStatus(2);
                mIsOpening = false;
                if (mOpenLockDialog.isShowing()) {
                    mOpenLockDialog.cancelOpenLockAnimator();
                } else {
                    showFiveToast("开锁失败，请稍后重试");
                }
            }

            @Override
            public void openSuccessHaveCar() {
                mOpenLockDialog.setOpenLockStatus(1);
                mIsOpening = false;
                mOpenLockDialog.cancelOpenLockAnimator();
                mOpenLockDialog.dismiss();
                showFiveToast("车位上方有车辆滞留");
            }

            @Override
            public void closeSuccess() {

            }

            @Override
            public void closeFailed() {

            }

            @Override
            public void closeFailedHaveCar() {

            }

        };
        MyReceiver.addLockListener(parkOrderInfo.getLockId(), lockListener);
    }

    private void showOpenLockDialog() {
        if (mOpenLockDialog == null) {
            mOpenLockDialog = new OpenLockDialog(requireContext(), new SuccessCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    if (aBoolean) {
                        //开锁成功
                        openLockSuccess();
                    } else {
                        //开锁失败，点了重试
                        openLockByRecord(mOpenLockOrder);
                    }
                }
            });
        }
        mOpenLockDialog.startOpenLock();
    }

    /**
     * 开锁成功改变订单状态
     */
    private void openLockSuccess() {
        mOpenLockOrder.setOrderStatus("1");
        mOpenLockOrder.setParkStartTime(DateUtil.getCurrentYearToMinutes());
        mCommonAdapter.notifyDataChange(mOpenLockOrder);
        showFiveToast("开锁成功");
        mOpenLockOrder = null;
    }

    /**
     * 把该记录的状态改为已取消
     */
    private void cancelRecord(final ParkOrderInfo parkOrderInfo) {
        showLoadingDialog();
        getOkGo(HttpConstants.cancelRecordOnFriendParkSpace)
                .params("id", parkOrderInfo.getId())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        IntentObserable.dispatch(ConstansUtil.CANCEL_RECORD, ConstansUtil.PARK_ORDER_INFO, parkOrderInfo);
                        notifyRemoveData(parkOrderInfo);
                        showFiveToast("取消成功");
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                case "102":
                                case "104":
                                case "105":
                                    showFiveToast(ConstansUtil.SERVER_ERROR);
                                    break;
                                case "103":
                                    notifyRemoveData(parkOrderInfo);
                                    showFiveToast("该预定记录已被删除");
                                    break;
                            }
                        }
                    }
                });
    }

    private void performOpenLock(ParkOrderInfo parkOrderInfo) {
        if (mIsOpening) {
            if (parkOrderInfo.getLockId().equals(mOpenLockOrder.getLockId())) {
                //如果当前正在开锁，但是关闭了开锁对话框的，再次点击开锁则只显示对话框
                showOpenLockDialog();
            } else {
                //如果其他的锁还没开启完毕，这时点了开启另一个锁则提示
                showFiveToast("请等待其他车锁开启完毕");
            }
        } else {
            //没有正在开的锁的，则获取网络时间判断当前是否已到了预定开始停车前30分钟。
            mOpenLockOrder = parkOrderInfo;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getCurrentTimeInMillis();
                }
            }).start();
        }
    }

    /**
     * 发送开锁请求
     */
    private void openLockByRecord(ParkOrderInfo parkOrderInfo) {
        showOpenLockDialog();
        mIsOpening = true;
        /*showLoadingDialog();
        getOkGo(HttpConstants.openLockByRecord)
                .params("id", parkOrderInfo.getId())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        dismmisLoadingDialog();
                        showOpenLockDialog();
                        mIsOpening = true;
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mIsOpening = false;
                        if (mOpenLockDialog != null) {
                            mOpenLockDialog.setOpenLockStatus(2);
                            mOpenLockDialog.cancelOpenLockAnimator();
                        }
                        mOpenLockOrder = null;
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    showFiveToast("设备暂时离线，请稍后重试");
                                    break;
                                case "102":
                                case "103":
                                    showFiveToast(ConstansUtil.SERVER_ERROR);
                                    break;
                                case "104":
                                    showFiveToast("该时间段已被别的用户预定");
                                    break;
                                case "105":
                                    showFiveToast("锁不存在，请联系车主");
                                    break;
                            }
                        }
                    }
                });*/
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_my_order_layout;
    }

    @Override
    protected int converGetItmeViewType(ParkOrderInfo parkOrderInfo, int position) {
        if ("0".equals(parkOrderInfo.getOrderStatus())) {
            return R.layout.item_reserving_friend_park_space_layout;
        }
        return super.converGetItmeViewType(parkOrderInfo, position);
    }

    @Override
    protected void bindData(final BaseViewHolder holder, final ParkOrderInfo parkOrderInfo, int position) {
        if ("0".equals(parkOrderInfo.getOrderStatus())) {
            bindAppointmentRecord(holder, parkOrderInfo);
            return;
        }
        CircleView circleView = holder.getView(R.id.my_order_status_iv);
        TextView orderTime = holder.getView(R.id.my_order_time);
        TextView orderTimeDescription = holder.getView(R.id.my_order_time_description);
        TextView orderStatus = holder.getView(R.id.my_order_waiting_for_pay);
        if (holder.itemView.getTag() == null) {
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone((ConstraintLayout) holder.getView(R.id.top_cl));
            constraintSet.constrainWidth(R.id.my_order_park_car_number_iv, dpToPx(12));
            constraintSet.constrainHeight(R.id.my_order_park_car_number_iv, dpToPx(13));
            constraintSet.applyTo((ConstraintLayout) holder.getView(R.id.top_cl));
            holder.itemView.setTag(constraintSet);
        } else {
            ConstraintSet constraintSet = (ConstraintSet) holder.itemView.getTag();
            constraintSet.applyTo((ConstraintLayout) holder.getView(R.id.top_cl));
        }
        holder.showPic(R.id.my_order_park_car_number_iv, R.drawable.ic_carman);
        switch (parkOrderInfo.getOrderStatus()) {
            /*case "0":
                //已预约
                circleView.setColor(Color.parseColor("#6a6bd9"));
                orderTime.setText(DateUtil.getMonthToMinute(parkOrderInfo.getOrderStartTime()));
                String appointParkDistance = "预停" + DateUtil.getDistanceForDayHourMinute(parkOrderInfo.getOrderStartTime(), parkOrderInfo.getOrderEndTime());
                orderTimeDescription.setText(appointParkDistance);
                orderStatus.setTextColor(Color.parseColor("#6a6bd9"));
                orderStatus.setText("已预约");
                break;*/
            case "1":
                //停车中
                circleView.setColor(Color.parseColor("#ffa830"));
                orderTime.setText(DateUtil.getMonthToMinute(parkOrderInfo.getPark_start_time()));
                String parkTimeDistance;
                if (DateUtil.getYearToSecondCalendar(parkOrderInfo.getOrderEndTime()).compareTo(
                        DateUtil.getYearToSecondCalendar(DateUtil.getCurrentYearToSecond())) < 0) {
                    //停车时长超过预约时长
                    parkTimeDistance = "已超时" + DateUtil.getDistanceForDayHourMinuteAddStart(parkOrderInfo.getOrderEndTime(), DateUtil.getCurrentYearToSecond(), parkOrderInfo.getExtensionTime());
                } else {
                    parkTimeDistance = "剩余" + DateUtil.getDistanceForDayHourMinute(DateUtil.getCurrentYearToSecond(), parkOrderInfo.getOrderEndTime());
                }
                orderTimeDescription.setText(parkTimeDistance);
                orderStatus.setTextColor(Color.parseColor("#ffa830"));
                orderStatus.setText("租用中");
                break;
            case "2":
                //已完成
                circleView.setColor(Color.parseColor("#1dd0a1"));
                orderTime.setText(DateUtil.getMonthToMinute(parkOrderInfo.getPark_start_time()));
                String actualPay = "停车" + DateUtil.getDistanceForDayHourMinute(parkOrderInfo.getPark_start_time(), parkOrderInfo.getPark_end_time());
                orderTimeDescription.setText(actualPay);

                orderStatus.setTextColor(Color.parseColor("#1dd0a1"));
                orderStatus.setText("已完成");
                break;
            case "3":
                //已取消（超时取消、正常手动取消）
                circleView.setColor(Color.parseColor("#808080"));
                orderTime.setText(DateUtil.getMonthToMinute(parkOrderInfo.getOrderStartTime()));
                String appointDistance = "预停" + DateUtil.getDistanceForDayHourMinute(parkOrderInfo.getOrderStartTime(), parkOrderInfo.getOrderEndTime());
                orderTimeDescription.setText(appointDistance);
                orderStatus.setTextColor(Color.parseColor("#808080"));
                orderStatus.setText("已取消");
                break;
        }

        holder.setText(R.id.my_order_appoint_date, parkOrderInfo.getOrderTime().substring(0, parkOrderInfo.getOrderTime().indexOf(" ")))
                .setText(R.id.my_order_park_lot, parkOrderInfo.getParkLotName())
                .setText(R.id.my_order_park_car_number, parkOrderInfo.getUserNoteName())
                .itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected int resourceId() {
        return 0;
    }

    private void bindAppointmentRecord(final BaseViewHolder holder, final ParkOrderInfo parkOrderInfo) {
        final ArrowView arrowView = holder.getView(R.id.more_iv);
        holder.setText(R.id.share_park_space_space_name, parkOrderInfo.getParkLotName())
                .setText(R.id.share_park_space_share_name, "业主：" + parkOrderInfo.getUserNoteName())
                .setText(R.id.park_space_description, parkOrderInfo.getParkSpaceLocation())
                .setText(R.id.start_park_date, parkOrderInfo.getOrderStartTime().substring(0, parkOrderInfo.getOrderStartTime().indexOf(" ")))
                .setText(R.id.appointment_income_time, DateUtil.getHourWithMinutesByYearToSecond(parkOrderInfo.getOrderStartTime()))
                .setText(R.id.appointment_leave_time, DateUtil.isInSameDay(parkOrderInfo.getOrderStartTime(), parkOrderInfo.getOrderEndTime()) ?
                        DateUtil.getHourWithMinutesByYearToSecond(parkOrderInfo.getOrderEndTime()) : parkOrderInfo.getOrderEndTime().substring(0, parkOrderInfo.getOrderEndTime().lastIndexOf(":")))
                .setText(R.id.park_duration, DateUtil.getDateDistanceForHourWithMinute(parkOrderInfo.getOrderStartTime(), parkOrderInfo.getOrderEndTime()))
                .setOnClickListener(R.id.more_iv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (arrowView.getGravity() == ArrowView.BOTTOM) {
                            arrowView.setGravity(ArrowView.TOP);
                            holder.goneView(R.id.more_tv)
                                    .goneView(R.id.park_space_description_tv)
                                    .goneView(R.id.park_space_description)
                                    .goneView(R.id.start_park_date_tv)
                                    .goneView(R.id.start_park_date)
                                    .goneView(R.id.appointment_income_time_tv)
                                    .goneView(R.id.appointment_income_time)
                                    .goneView(R.id.appointment_leave_time_tv)
                                    .goneView(R.id.appointment_leave_time)
                                    .goneView(R.id.park_duration_tv)
                                    .goneView(R.id.park_duration)
                                    .goneView(R.id.navigation_cl);
                        } else {
                            arrowView.setGravity(ArrowView.BOTTOM);
                            holder.showView(R.id.more_tv)
                                    .showView(R.id.park_space_description_tv)
                                    .showView(R.id.park_space_description)
                                    .showView(R.id.start_park_date_tv)
                                    .showView(R.id.start_park_date)
                                    .showView(R.id.appointment_income_time_tv)
                                    .showView(R.id.appointment_income_time)
                                    .showView(R.id.appointment_leave_time_tv)
                                    .showView(R.id.appointment_leave_time)
                                    .showView(R.id.park_duration_tv)
                                    .showView(R.id.park_duration)
                                    .showView(R.id.navigation_cl);
                        }
                    }
                })
                .setOnClickListener(R.id.navigation_cl, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (LocationManager.getInstance().hasLocation()) {
                            Intent intent = new Intent(getActivity(), NavigationActivity.class);
                            intent.putExtra("gps", true);
                            intent.putExtra("start", new NaviLatLng(LocationManager.getInstance().getmAmapLocation().getLatitude(), LocationManager.getInstance().getmAmapLocation().getLongitude()));
                            intent.putExtra("end", new NaviLatLng(parkOrderInfo.getLatitude(), parkOrderInfo.getLongitude()));
                            intent.putExtra(ConstansUtil.PARK_LOT_NAME, parkOrderInfo.getParkLotName());
                            intent.putExtra("address", parkOrderInfo.getParkSpaceLocation());
                            startActivity(intent);
                        } else {
                            if (DeviceUtils.isGpsOpen(holder.itemView.getContext())) {
                                showFiveToast("请稍后重试");
                            } else {
                                showFiveToast("定位失败，请开启GPS后重试");
                            }
                        }
                    }
                })
                .setOnClickListener(R.id.open_lock_tv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performOpenLock(parkOrderInfo);
                    }
                })
                .setOnClickListener(R.id.cancel_record_tv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog("提示", "确定取消记录吗？", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelRecord(parkOrderInfo);
                            }
                        });
                    }
                });
    }

}
