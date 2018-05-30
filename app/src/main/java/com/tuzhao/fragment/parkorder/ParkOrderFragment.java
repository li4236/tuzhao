package com.tuzhao.fragment.parkorder;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import com.tuzhao.publicwidget.others.CircleView;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/5/21.
 */

public class ParkOrderFragment extends BaseRefreshFragment<ParkOrderInfo> {

    private ConstraintLayout mConstraintLayout;

    private int mOrderStatus;

    public static ParkOrderFragment newInstance(int orderStatus) {
        ParkOrderFragment parkOrderFragment = new ParkOrderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstansUtil.ORDER_STATUS, orderStatus);
        parkOrderFragment.setArguments(bundle);
        return parkOrderFragment;
    }

    @Override
    protected int resourceId() {
        return R.layout.fragment_park_order_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        if (getArguments() != null) {
            mOrderStatus = getArguments().getInt(ConstansUtil.ORDER_STATUS);
            ArrayList<ParkOrderInfo> list = getArguments().getParcelableArrayList(ConstansUtil.PARK_ORDER_LIST);
            mCommonAdapter.setNewArrayData(list);
        }
        mStartItme = 0;
        mConstraintLayout = view.findViewById(R.id.park_order_dialog);
    }

    @Override
    protected void initData() {
        setTAG(TAG + " status:" + mOrderStatus);
        showDialog();
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dissmissDialog();
        if (!mCommonAdapter.getData().isEmpty()) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                bundle.putParcelableArrayList(ConstansUtil.PARK_ORDER_LIST, (ArrayList<? extends Parcelable>) mCommonAdapter.getData());
            }
        }
    }

    @Override
    protected void loadData() {
        getOkgo(HttpConstants.getKindParkOrder)
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
                        Log.e(TAG, "onError: " + mOrderStatus);
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
        switch (parkOrderInfo.getOrder_status()) {
            case "1":
                //已预约
                circleView.setColor(Color.parseColor("#6a6bd9"));
                orderTime.setText(DateUtil.getMonthToMinute(parkOrderInfo.getOrder_starttime()));
                String appointParkDistance = "预停" + DateUtil.getDateDistanceForHourWithMinute(parkOrderInfo.getOrder_starttime(), parkOrderInfo.getOrder_endtime());
                orderTime.setText(appointParkDistance);
                break;
            case "2":
                //停车中
                circleView.setColor(Color.parseColor("#ffa830"));
                orderTime.setText(DateUtil.getMonthToMinute(parkOrderInfo.getPark_start_time()));
                String parkTimeDistance;
                if (parkOrderInfo.getPark_end_time().equals("0000-00-00 00:00:00")) {
                    parkTimeDistance = "剩余" + DateUtil.getDateDistanceForHourWithMinute(DateUtil.getCurrentYearToSecond(), parkOrderInfo.getOrder_endtime());
                    orderTimeDescription.setText(parkTimeDistance);
                } else {
                    if (DateUtil.getYearToSecondCalendar(parkOrderInfo.getOrder_endtime()).compareTo(
                            DateUtil.getYearToSecondCalendar(parkOrderInfo.getPark_end_time())) < 0) {
                        //停车时长超过预约时长
                        parkTimeDistance = "已超时" + DateUtil.getDateDistanceForHourWithMinute(parkOrderInfo.getOrder_endtime(), DateUtil.getCurrentYearToSecond());
                        orderTimeDescription.setText(parkTimeDistance);
                    } else {
                        parkTimeDistance = "剩余" + DateUtil.getDateDistanceForHourWithMinute(DateUtil.getCurrentYearToSecond(), parkOrderInfo.getOrder_endtime());
                        orderTimeDescription.setText(parkTimeDistance);
                    }
                }
                break;
            case "3":
                //待付款
                circleView.setColor(Color.parseColor("#ff6c6c"));
                holder.setText(R.id.my_order_waiting_for_pay, "待支付");
                orderTime.setText(DateUtil.getParkTime(parkOrderInfo));
                String shouldPay = "￥" + parkOrderInfo.getOrder_fee();
                orderTimeDescription.setText(shouldPay);
                break;
            case "4":
            case "5":
                //已完成（待评论、已完成）
                circleView.setColor(Color.parseColor("#1dd0a1"));
                if (DateUtil.getYearToSecondCalendar(parkOrderInfo.getOrder_endtime()).compareTo(
                        DateUtil.getYearToSecondCalendar(parkOrderInfo.getPark_end_time())) < 0) {
                    //停车时长超过预约时长
                    orderTime.setText(DateUtil.getDateDistanceForHourWithMinute(parkOrderInfo.getOrder_starttime(), parkOrderInfo.getPark_end_time()));
                } else {
                    orderTime.setText(DateUtil.getDateDistanceForHourWithMinute(parkOrderInfo.getOrder_starttime(), parkOrderInfo.getOrder_endtime()));
                }
                String actualPay = "￥" + parkOrderInfo.getActual_pay_fee();
                orderTimeDescription.setText(actualPay);
                break;
            case "6":
                //已取消（超时取消、正常手动取消）
                circleView.setColor(Color.parseColor("#808080"));
                orderTime.setText(DateUtil.getMonthToMinute(parkOrderInfo.getOrder_starttime()));
                orderTimeDescription.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.g6));
                orderTimeDescription.setText("已取消");
                break;
        }
        holder.setText(R.id.my_order_appoint_date, parkOrderInfo.getOrder_time().substring(0, parkOrderInfo.getOrder_time().indexOf(" ")))
                .setText(R.id.my_order_park_lot, parkOrderInfo.getPark_space_name())
                .setText(R.id.my_order_park_car_number, parkOrderInfo.getCar_numble())
                .itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(OrderActivity.class, ConstansUtil.PARK_ORDER_INFO, parkOrderInfo);
            }
        });

    }

}
