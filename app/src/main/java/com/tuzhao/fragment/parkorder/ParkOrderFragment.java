package com.tuzhao.fragment.parkorder;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.activity.mine.OrderActivity;
import com.tuzhao.fragment.base.BaseRefreshFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.customView.CircleView;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/5/21.
 */

public class ParkOrderFragment extends BaseRefreshFragment<ParkOrderInfo> implements IntentObserver {

    private ConstraintLayout mConstraintLayout;

    private int mOrderStatus;

    public static ParkOrderFragment newInstance(int orderStatus) {
        ParkOrderFragment parkOrderFragment = new ParkOrderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstansUtil.STATUS, orderStatus);
        parkOrderFragment.setArguments(bundle);
        return parkOrderFragment;
    }

    @Override
    protected int resourceId() {
        return R.layout.base_loading_refresh_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        if (getArguments() != null) {
            mOrderStatus = getArguments().getInt(ConstansUtil.STATUS);
            mStartItme = getArguments().getInt(ConstansUtil.START_ITME,0);
            ArrayList<ParkOrderInfo> list = getArguments().getParcelableArrayList(ConstansUtil.PARK_ORDER_LIST);
            mCommonAdapter.setNewArrayData(list);
        }
        mConstraintLayout = view.findViewById(R.id.loading_dialog);
    }

    @Override
    protected void initData() {
        //防止滑动到其他界面时把网络请求关闭了
        setTAG(this.getClass().getName() + "status:" + mOrderStatus);
        showDialog();
        loadData();
        IntentObserable.registerObserver(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!mCommonAdapter.getData().isEmpty()) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                bundle.putParcelableArrayList(ConstansUtil.PARK_ORDER_LIST, (ArrayList<? extends Parcelable>) mCommonAdapter.getData());
                bundle.putInt(ConstansUtil.START_ITME, mStartItme);
            }
        }
        IntentObserable.unregisterObserver(this);
        dissmissDialog();
    }

    @Override
    protected void loadData() {
        getOkgos(HttpConstants.getKindParkOrder)
                .params("order_status", mOrderStatus)
                .execute(new JsonCallback<Base_Class_List_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<ParkOrderInfo> o, Call call, Response response) {
                        loadDataSuccess(o);
                        dissmissDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dissmissDialog();
                        loadDataFail(e, new LoadFailCallback() {
                            @Override
                            public void onLoadFail(Exception e) {

                            }
                        });

                    }
                });
    }

    private void showDialog() {
        if (mConstraintLayout.getVisibility() != View.VISIBLE) {
            mConstraintLayout.setVisibility(View.VISIBLE);
        }
    }

    private void dissmissDialog() {
        if (mConstraintLayout.getVisibility() != View.INVISIBLE) {
            mConstraintLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_my_order_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, final ParkOrderInfo parkOrderInfo, int position) {
        CircleView circleView = holder.getView(R.id.my_order_status_iv);
        TextView orderTime = holder.getView(R.id.my_order_time);
        TextView orderTimeDescription = holder.getView(R.id.my_order_time_description);
        TextView orderStatus = holder.getView(R.id.my_order_waiting_for_pay);
        switch (parkOrderInfo.getOrderStatus()) {
            case "1":
                //已预约
                circleView.setColor(Color.parseColor("#6a6bd9"));
                orderTime.setText(DateUtil.getMonthToMinute(parkOrderInfo.getOrderStartTime()));
                String appointParkDistance = "预停" + DateUtil.getDistanceForDayHourMinute(parkOrderInfo.getOrderStartTime(), parkOrderInfo.getOrderEndTime());
                orderTimeDescription.setText(appointParkDistance);
                orderStatus.setTextColor(Color.parseColor("#6a6bd9"));
                orderStatus.setText("已预约");
                break;
            case "2":
                //停车中
                circleView.setColor(Color.parseColor("#ffa830"));
                orderTime.setText(DateUtil.getMonthToMinute(parkOrderInfo.getPark_start_time()));
                String parkTimeDistance;
                if (DateUtil.getYearToSecondCalendar(parkOrderInfo.getOrderEndTime(), parkOrderInfo.getExtensionTime()).compareTo(
                        DateUtil.getYearToSecondCalendar(DateUtil.getCurrentYearToSecond())) < 0) {
                    //停车时长超过预约时长
                    parkTimeDistance = "已超时" + DateUtil.getDistanceForDayHourMinuteAddStart(parkOrderInfo.getOrderEndTime(), DateUtil.getCurrentYearToSecond(), parkOrderInfo.getExtensionTime());
                } else if (DateUtil.getYearToSecondCalendar(parkOrderInfo.getOrderEndTime()).compareTo(
                        DateUtil.getYearToSecondCalendar(DateUtil.getCurrentYearToSecond())) < 0) {
                    //在顺延时长内
                    parkTimeDistance = "宽限剩余" + DateUtil.getDistanceForDayHourMinuteAddEnd(DateUtil.getCurrentYearToSecond(), parkOrderInfo.getOrderEndTime(), parkOrderInfo.getExtensionTime());
                } else {
                    parkTimeDistance = "剩余" + DateUtil.getDistanceForDayHourMinute(DateUtil.getCurrentYearToSecond(), parkOrderInfo.getOrderEndTime());
                }
                orderTimeDescription.setText(parkTimeDistance);
                orderStatus.setTextColor(Color.parseColor("#ffa830"));
                orderStatus.setText("租用中");
                break;
            case "3":
                //待付款
                circleView.setColor(Color.parseColor("#ff6c6c"));
                orderTime.setText(DateUtil.getDistanceForDayHourMinute(parkOrderInfo.getPark_start_time(), parkOrderInfo.getPark_end_time()));
                double orderFee = Double.parseDouble(parkOrderInfo.getOrderFee());
                if (orderFee <= 0) {
                    orderFee = 0.01;
                }
                String shouldPay = "￥" + orderFee;
                orderTimeDescription.setText(shouldPay);
                orderStatus.setTextColor(Color.parseColor("#ff6c6c"));
                orderStatus.setText("待支付");
                break;
            case "4":
            case "5":
                //已完成（待评论、已完成）
                circleView.setColor(Color.parseColor("#1dd0a1"));
                /*if (DateUtil.getYearToSecondCalendar(parkOrderInfo.getOrderEndTime(), parkOrderInfo.getExtensionTime()).compareTo(
                        DateUtil.getYearToSecondCalendar(parkOrderInfo.getPark_end_time())) < 0) {
                    //停车时长超过预约时长加顺延时长
                    orderTime.setText(DateUtil.getDistanceForDayHourMinute(parkOrderInfo.getOrderStartTime(), parkOrderInfo.getPark_end_time()));
                } else if (DateUtil.getYearToSecondCalendar(parkOrderInfo.getOrderEndTime()).compareTo(
                        DateUtil.getYearToSecondCalendar(parkOrderInfo.getPark_end_time())) < 0) {
                    //停车时间在顺延时长内
                    orderTime.setText(DateUtil.getDistanceForDayHourMinute(parkOrderInfo.getOrderStartTime(), parkOrderInfo.getPark_end_time()));
                } else {
                    orderTime.setText(DateUtil.getDistanceForDayHourMinute(parkOrderInfo.getOrderStartTime(), parkOrderInfo.getOrderEndTime()));
                }*/
                orderTime.setText(DateUtil.getDistanceForDayHourMinute(parkOrderInfo.getPark_start_time(), parkOrderInfo.getPark_end_time()));
                String actualPay = "￥" + parkOrderInfo.getActual_pay_fee();
                orderTimeDescription.setText(actualPay);

                orderStatus.setTextColor(Color.parseColor("#1dd0a1"));
                if (parkOrderInfo.getOrderStatus().equals("4")) {
                    orderStatus.setText("待评论");
                } else {
                    orderStatus.setText("已完成");
                }
                break;
            case "6":
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
                .setText(R.id.my_order_park_car_number, parkOrderInfo.getCarNumber())
                .itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(OrderActivity.class, ConstansUtil.PARK_ORDER_INFO, parkOrderInfo);
            }
        });

    }

    @Override
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ConstansUtil.FINISH_APPOINTMENT:
                    Bundle bundle = intent.getBundleExtra(ConstansUtil.FOR_REQEUST_RESULT);
                    ParkOrderInfo parkOrderInfo = bundle.getParcelable(ConstansUtil.PARK_ORDER_INFO);
                    if (parkOrderInfo != null) {
                        parkOrderInfo.setOrderStatus("2");
                        if (mOrderStatus == 0) {
                            //全部订单则把预约的改为停车中
                            int position = mCommonAdapter.getData().indexOf(parkOrderInfo);
                            if (position != -1) {
                                mCommonAdapter.notifyDataChange(position, parkOrderInfo);
                            }
                        } else if (mOrderStatus == 1) {
                            //预约订单则把它删掉
                            mCommonAdapter.notifyRemoveData(parkOrderInfo);
                        } else if (mOrderStatus == 2) {
                            mCommonAdapter.addFirstData(parkOrderInfo);
                        }
                    }
                    break;
                case ConstansUtil.CHANGE_APPOINTMENT_INFO:
                    Bundle changBundle = intent.getBundleExtra(ConstansUtil.FOR_REQEUST_RESULT);
                    ParkOrderInfo changeOrderInfo = changBundle.getParcelable(ConstansUtil.PARK_ORDER_INFO);
                    if (changeOrderInfo != null) {
                        mCommonAdapter.notifyDataChange(changeOrderInfo);
                    }
                    break;
                case ConstansUtil.CANCEL_ORDER:
                    Bundle cancelBundle = intent.getBundleExtra(ConstansUtil.FOR_REQEUST_RESULT);
                    ParkOrderInfo cancelParkOrderInfo = cancelBundle.getParcelable(ConstansUtil.PARK_ORDER_INFO);
                    if (cancelParkOrderInfo != null) {
                        cancelParkOrderInfo.setOrderStatus("6");
                        if (mOrderStatus == 0 || mOrderStatus == 1) {
                            mCommonAdapter.removeData(cancelParkOrderInfo);
                        }
                    }
                    break;
                case ConstansUtil.CHANGE_PARK_ORDER_INRO:
                    Bundle changeBundle = intent.getBundleExtra(ConstansUtil.FOR_REQEUST_RESULT);
                    ParkOrderInfo changeParkOrderInfo = changeBundle.getParcelable(ConstansUtil.PARK_ORDER_INFO);
                    if (changeParkOrderInfo != null) {
                        mCommonAdapter.notifyDataChange(changeParkOrderInfo);
                    }
                    break;
                case ConstansUtil.FINISH_PARK:
                    if (mOrderStatus == 0 || mOrderStatus == 2) {
                        //结束租用中
                        Bundle parkingBundle = intent.getBundleExtra(ConstansUtil.FOR_REQEUST_RESULT);
                        ParkOrderInfo parkingOrderInfo = parkingBundle.getParcelable(ConstansUtil.PARK_ORDER_INFO);
                        if (parkingOrderInfo != null) {
                            if (mOrderStatus == 0) {
                                mCommonAdapter.notifyDataChange(parkingOrderInfo);
                            } else if (mOrderStatus == 2) {
                                mCommonAdapter.removeData(parkingOrderInfo);
                            }
                        }
                    }
                    break;
                case ConstansUtil.FINISH_PAY_ORDER:
                    if (mOrderStatus == 0 || mOrderStatus == 3 || mOrderStatus == 4 || mOrderStatus == 5) {
                        Bundle finishBundle = intent.getBundleExtra(ConstansUtil.FOR_REQEUST_RESULT);
                        mCommonAdapter.notifyDataChange((ParkOrderInfo) finishBundle.getParcelable(ConstansUtil.PARK_ORDER_INFO));
                    }
                    break;
                case ConstansUtil.COMMENT_SUCCESS:
                    if (mOrderStatus == 0 || mOrderStatus == 4 || mOrderStatus == 5) {
                        Bundle commentBundle = intent.getBundleExtra(ConstansUtil.FOR_REQEUST_RESULT);
                        ParkOrderInfo commentOrder = commentBundle.getParcelable(ConstansUtil.PARK_ORDER_INFO);
                        mCommonAdapter.notifyDataChange(commentOrder);
                    }
                    break;
                case ConstansUtil.DELETE_PARK_ORDER:
                    if (mOrderStatus == 0 || mOrderStatus == 4 || mOrderStatus == 5 || mOrderStatus == 6) {
                        Bundle orderBundle = intent.getBundleExtra(ConstansUtil.FOR_REQEUST_RESULT);
                        ParkOrderInfo orderInfo = orderBundle.getParcelable(ConstansUtil.PARK_ORDER_INFO);
                        mCommonAdapter.removeData(orderInfo);
                    }
                    break;
            }
        }
    }
}
