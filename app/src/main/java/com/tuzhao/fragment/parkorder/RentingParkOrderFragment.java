package com.tuzhao.fragment.parkorder;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.ParkOrderDetailsActivity;
import com.tuzhao.fragment.BaseFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.TimeManager;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.publicwidget.popupwindow.CustomPopWindow;
import com.tuzhao.publicwidget.timer.CountupView;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TZL12 on 2017/6/26.
 */

public class RentingParkOrderFragment extends BaseFragment {

    /**
     * UI
     */
    private View mContentView;
    private CustomDialog mCustomDialog;
    private LinearLayout linearlayout_nodata, linearlayout12;
    private RelativeLayout relativelayout_orderdetail, relativelayout_detailmoney;
    private TextView textview_fee, textview_warm1, textview_warm2, textview_finish, textview_order_1, textview_order_2, textview_zheceng1, textview_zheceng2;
    private CountupView countdownview;

    private List<ParkOrderInfo> mData;

    private DateUtil.ParkFee parkFee;
    private boolean isFirstIn = true, isFirstOrder = true;
    DateUtil dateUtil = new DateUtil();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        mContentView = inflater.inflate(R.layout.fragment_rentingparkorder_layout, container, false);
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件
    }

    private void initView() {
        linearlayout_nodata = mContentView.findViewById(R.id.id_fragment_rentingparkorder_layout_linearlayout_nodata);
        linearlayout12 = mContentView.findViewById(R.id.id_fragment_rentingparkorder_layout_linearlayout12);
        relativelayout_orderdetail = mContentView.findViewById(R.id.id_fragment_rentingparkorder_layout_relativelayout_orderdetail);
        relativelayout_detailmoney = mContentView.findViewById(R.id.id_fragment_rentingparkorder_layout_relativelayout_detailmoney);
        textview_fee = mContentView.findViewById(R.id.id_fragment_rentingparkorder_layout_textview_fee);
        textview_warm1 = mContentView.findViewById(R.id.id_fragment_rentingparkorder_layout_textview_warm1);
        textview_warm2 = mContentView.findViewById(R.id.id_fragment_rentingparkorder_layout_textview_warm2);
        textview_finish = mContentView.findViewById(R.id.id_fragment_rentingparkorder_layout_textview_finish);
        textview_order_1 = mContentView.findViewById(R.id.id_fragment_rentingparkorder_layout_textview_order_1);
        textview_order_2 = mContentView.findViewById(R.id.id_fragment_rentingparkorder_layout_textview_order_2);
        textview_zheceng1 = mContentView.findViewById(R.id.id_fragment_rentingparkorder_layout_textview_zheceng1);
        textview_zheceng2 = mContentView.findViewById(R.id.id_fragment_rentingparkorder_layout_textview_zheceng2);
        countdownview = mContentView.findViewById(R.id.id_fragment_rentingparkorder_layout_countdownview);
    }

    private void initData() {
        initLoading("加载中...");
        requestGetRentintParkOrder();

    }

    private void requestGetRentintParkOrder() {

        OkGo.post(HttpConstants.getKindParkOrder)
                .tag(mContext)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("order_status", "2")
                .execute(new JsonCallback<Base_Class_List_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(final Base_Class_List_Info<ParkOrderInfo> appointParkOrderInfo, Call call, Response response) {

                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        if (isFirstIn) {
                            isFirstIn = false;
                        }
                        linearlayout_nodata.setVisibility(View.GONE);
                        linearlayout12.setVisibility(View.VISIBLE);
                        mData = appointParkOrderInfo.data;
                        initDataView();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        if (!DensityUtil.isException(mContext, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + "getCollectionDatas" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 102:
                                    //该用户不存在正在使用停车位的订单
                                    linearlayout_nodata.setVisibility(View.VISIBLE);
                                    linearlayout12.setVisibility(View.GONE);
                                    break;
                                case 901:
                                    MyToast.showToast(mContext, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }

    private void initDataView() {
        if (mData.size() > 0) {
            linearlayout_nodata.setVisibility(View.GONE);
            if (isFirstOrder && mData.size() == 1) {
                textview_order_1.setVisibility(View.GONE);
                textview_order_2.setVisibility(View.GONE);
                /*textview_order_1.setBackgroundColor(ContextCompat.getColor(mContext, R.color.w1));
                textview_order_2.setText("暂无订单");
                textview_order_2.setBackgroundColor(Color.WHITE);*/
                textview_zheceng1.setVisibility(View.VISIBLE);
                textview_zheceng2.setVisibility(View.GONE);
                initDataView1(mData.get(0));
            } else if (isFirstOrder && mData.size() == 2) {
                textview_order_1.setBackgroundColor(ContextCompat.getColor(mContext, R.color.w1));
                textview_order_2.setText("订单二");
                textview_order_2.setBackgroundColor(Color.WHITE);
                textview_zheceng1.setVisibility(View.VISIBLE);
                textview_zheceng2.setVisibility(View.GONE);
                initDataView1(mData.get(0));
            } else if (!isFirstOrder && mData.size() == 2) {
                textview_order_1.setBackgroundColor(Color.WHITE);
                textview_order_2.setText("订单二");
                textview_order_2.setBackgroundColor(ContextCompat.getColor(mContext, R.color.w1));
                textview_zheceng1.setVisibility(View.GONE);
                textview_zheceng2.setVisibility(View.VISIBLE);
                initDataView1(mData.get(1));
            }
        } else {
            linearlayout_nodata.setVisibility(View.VISIBLE);
        }
    }

    private void initDataView1(final ParkOrderInfo parkOrderInfo) {
        textview_warm1.setText("需在" + parkOrderInfo.getOrder_endtime() + "前离场");
        textview_warm2.setText("超时按" + parkOrderInfo.getFine() + "元/小时收费");
        final int parkontime = dateUtil.getTimeDifferenceMinuteMoreDetail(parkOrderInfo.getPark_start_time(), TimeManager.getInstance().getNowTime(true, true));
        Log.e("停车时长妈的", parkontime + "  ");
        if (parkontime > 86400) {
            countdownview.start(parkontime * 1000);
        } else if (parkontime < 86400 && parkontime > 3600) {
            countdownview.customTimeShow(false, true, true, true, false);
            countdownview.start(parkontime * 1000);
        } else {
            countdownview.customTimeShow(false, false, true, true, false);
            countdownview.start(parkontime * 1000);
        }

        countdownview.setOnCountdownIntervalListener(1000 * 10, new CountupView.OnCountdownIntervalListener() {
            @Override
            public void onInterval(CountupView cv, long remainTime) {
                //每过一分钟，计算一次价钱
                parkFee = dateUtil.countCost(parkOrderInfo.getPark_start_time(), TimeManager.getInstance().getNowTime(true, false), parkOrderInfo.getOrder_endtime(), parkOrderInfo.getHigh_time().substring(0, parkOrderInfo.getHigh_time().indexOf(" - ")), parkOrderInfo.getHigh_time().substring(parkOrderInfo.getHigh_time().indexOf(" - ") + 3, parkOrderInfo.getHigh_time().length()), parkOrderInfo.getHigh_fee(), parkOrderInfo.getLow_fee(), parkOrderInfo.getFine());
                textview_fee.setText((parkFee.parkfee + parkFee.outtimefee) + "");
                if (parkontime > 86400) {
                    countdownview.customTimeShow(true, true, true, true, false);
                } else if (parkontime < 86400 && parkontime > 3600) {
                    countdownview.customTimeShow(false, true, true, true, false);
                } else {
                    countdownview.customTimeShow(false, false, true, true, false);
                }
            }
        });

        relativelayout_detailmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出费用详情的popupwindow
                View view = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_parkorderfee_layout, null);
                ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_discount_fee)).setText((parkFee.parkfee + parkFee.outtimefee) + "");
                ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_park_orderfee)).setText(parkFee.parkfee + "元");
                ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_park_finefee)).setText(parkFee.outtimefee + "元");
                String start = parkOrderInfo.getHigh_time().substring(0, parkOrderInfo.getHigh_time().indexOf(" - ")),
                        end = parkOrderInfo.getHigh_time().substring(parkOrderInfo.getHigh_time().indexOf(" - ") + 3, parkOrderInfo.getHigh_time().length()),
                        hightime1 = parkOrderInfo.getHigh_time().substring(0, parkOrderInfo.getHigh_time().indexOf(" - ")),
                        hightime2 = parkOrderInfo.getHigh_time().substring(parkOrderInfo.getHigh_time().indexOf(" - ") + 3, parkOrderInfo.getHigh_time().length());
                if (Integer.parseInt(start.substring(0, start.indexOf(":"))) < Integer.parseInt(end.substring(0, end.lastIndexOf(":")))) {
                    ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_heightime)).setText(hightime1 + " - " + hightime2);
                    ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_lowtime)).setText(hightime2 + " - 次日" + hightime1);
                } else {
                    ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_heightime)).setText(hightime1 + " - 次日" + hightime2);
                    ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_lowtime)).setText("次日" + hightime2 + " - 次日" + hightime1);
                }
                ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_fine)).setText("停车超出顺延时间按" + parkOrderInfo.getFine() + "元/小时收取额外超时费");
                ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_heighfee)).setText(parkOrderInfo.getHigh_fee().equals("0.00") ? "免费停车" : parkOrderInfo.getHigh_fee() + "元/小时");
                ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_lowfee)).setText(parkOrderInfo.getLow_fee().equals("0.00") ? "免费停车" : parkOrderInfo.getLow_fee() + "元/小时");
                ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_redpacket)).setText("未优惠");

                CustomPopWindow customPopWindow = new CustomPopWindow.PopupWindowBuilder(mContext)
                        .setView(view)
                        .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                        .setBgDarkAlpha(0.7f) // 控制亮度
                        .setFocusable(true)
                        .setOutsideTouchable(true)
                        .setAnimationStyle(R.style.CustomPopWindowStyle)
                        .enableOutsideTouchableDissmiss(true)// 设置false点击PopupWindow之外的地方，popWindow不关闭，如果不设置这个属性或者为true，则关闭
                        .create()
                        .showAtLocation(relativelayout_detailmoney, Gravity.CENTER, 0, 0);
            }
        });

        relativelayout_orderdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ParkOrderDetailsActivity.class);
                intent.putExtra("parkorderinfo", parkOrderInfo);
                mContext.startActivity(intent);
            }
        });

        textview_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndParkAlertDialog(parkOrderInfo);
            }
        });
    }

    private void initEvent() {

        textview_order_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirstOrder = true;
                initDataView();
            }
        });

        textview_order_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData != null) {
                    if (mData.size() > 1) {
                        isFirstOrder = false;
                        initDataView();
                    } else {
                        MyToast.showToast(mContext, "暂无订单", 5);
                    }
                } else {
                    MyToast.showToast(mContext, "暂无订单", 5);
                }

            }
        });
    }

    private void requestEndParking(ParkOrderInfo parkOrderInfo) {

        OkGo.post(HttpConstants.endParking)
                .tag(mContext)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("order_id", parkOrderInfo.getId())
                .params("citycode", parkOrderInfo.getCitycode())
                .params("pass_code", DensityUtil.MD5code(UserManager.getInstance().getUserInfo().getSerect_code() + "*&*" + UserManager.getInstance().getUserInfo().getCreate_time() + "*&*" + UserManager.getInstance().getUserInfo().getId()))
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> appointParkOrderInfoBase_class_info, Call call, Response response) {
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        requestGetRentintParkOrder();
                        MyToast.showToast(mContext, "已结束停车", 5);
//                        ParkOrderActivity.addRedPoint(3);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        if (!DensityUtil.isException(mContext, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + "getCollectionDatas" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 103:
                                    //该用户不存在正在使用停车位的订单
                                    linearlayout_nodata.setVisibility(View.VISIBLE);
                                    break;
                                case 901:
                                    MyToast.showToast(mContext, "服务器正在维护中", 2);
                                    break;
                            }
                        }
                    }
                });
    }

    private void showEndParkAlertDialog(final ParkOrderInfo parkOrderInfo) {

        final TipeDialog.Builder builder = new TipeDialog.Builder(getContext());
        builder.setMessage("确定结束停车吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                initLoading("正在提交...");
                requestEndParking(parkOrderInfo);
            }
        });

        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.create().show();
    }

    private void initLoading(String what) {
        mCustomDialog = new CustomDialog(mContext, what);
        mCustomDialog.show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCustomDialog != null) {
            mCustomDialog.cancel();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if ((isVisibleToUser && isResumed())) {
            Log.e("我是正在停车中的页面", "我被显示啦setUserVisibleHint");
            if (!isFirstIn) {
                requestGetRentintParkOrder();
            }
        } else if (!isVisibleToUser) {
            Log.e("我是正在停车中的页面", "我被隐藏啦setUserVisibleHint");
            if (!isFirstIn) {
                requestGetRentintParkOrder();
            }
        } else if ((isVisibleToUser && !isResumed())) {
            Log.e("我是正在停车中的页面", "我被显示啦setUserVisibleHint");
            if (!isFirstIn) {
                requestGetRentintParkOrder();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            Log.e("我是正在停车中的页面", "我被显示啦onResume");
            if (!isFirstIn) {
                requestGetRentintParkOrder();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
