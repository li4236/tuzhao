package com.tuzhao.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.ParkOrderDetailsActivity;
import com.tuzhao.activity.ParkspaceDetailActivity;
import com.tuzhao.activity.mine.CommentOrderActivity;
import com.tuzhao.activity.mine.OpenParkLockActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Discount_Info;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.popupwindow.CustomPopWindow;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.ImageUtil;

import java.util.List;

/**
 * Created by TZL12 on 2017/5/9.
 */

public class ParkOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ParkOrderInfo> mData;
    private OnItemOrderToPay onItemOrderToPay;
    private OnItemDeleteOrder onItemDeleteOrder;
    private OnItemCancleOrder onItemCancleOrder;
    DateUtil dateUtil = new DateUtil();

    public enum ITEM_TYPE {
        ITEM_TYPE_APPOINT, ITEM_TYPE_PAY, ITEM_TYPE_FINISH, ITEM_TYPE_CANCLE;
    }

    public ParkOrderAdapter(Context mContext, List<ParkOrderInfo> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getItemCount() {
        return mData == null || mData.isEmpty() ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (Integer.parseInt(mData.get(position).getOrder_status())) {
            case 1:
                //预约订单
                return ITEM_TYPE.ITEM_TYPE_APPOINT.ordinal();
            case 3:
                //待付款
                return ITEM_TYPE.ITEM_TYPE_PAY.ordinal();
            case 4:
                //待评论
                return ITEM_TYPE.ITEM_TYPE_FINISH.ordinal();
            case 5:
                //已完成
                return ITEM_TYPE.ITEM_TYPE_FINISH.ordinal();
            case 6:
                //（取消的）预约订单
                return ITEM_TYPE.ITEM_TYPE_CANCLE.ordinal();
            case 7:
                //（过期的）预约订单
                return ITEM_TYPE.ITEM_TYPE_CANCLE.ordinal();
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_APPOINT.ordinal()) {
            return new AppointParkViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_appointparkorder_layout, parent, false));
        } else if (viewType == ITEM_TYPE.ITEM_TYPE_PAY.ordinal()) {
            return new ReadyPayViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_parkreadypay_layout, parent, false));
        } else if (viewType == ITEM_TYPE.ITEM_TYPE_FINISH.ordinal()) {
            return new FinishParkViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_finishparkorder_layout, parent, false));
        } else {
            return new CancleParkViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_cancleparkorder_layout, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AppointParkViewHolder) {
            String[] urlList = mData.get(position).getPictures().split(",");
            ImageUtil.showPic(((AppointParkViewHolder) holder).imageview_bigshow,
                    urlList.length > 0 ? HttpConstants.ROOT_IMG_URL_PS + urlList[0] : "", R.mipmap.ic_img);
            ((AppointParkViewHolder) holder).textview_parkspace_name.setText(mData.get(position).getParkLotName());
            ((AppointParkViewHolder) holder).textview_address.setText(mData.get(position).getParkLotAddress());
            ((AppointParkViewHolder) holder).textview_address_description.setText(mData.get(position).getParkSpaceLocationDescribe());
            ((AppointParkViewHolder) holder).textview_startime.setText("进场时间：" + mData.get(position).getOrder_starttime().substring(5, mData.get(position).getOrder_starttime().length() - 3));
            ((AppointParkViewHolder) holder).textview_carnumber.setText("预停车辆：" + mData.get(position).getCar_numble());

            ((AppointParkViewHolder) holder).textview_openlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //立即开锁的点击事件
                    Intent intent = new Intent(mContext, OpenParkLockActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("orderInfo", mData.get(position));
                    intent.putExtra("orderInfoBundle", bundle);
                    mContext.startActivity(intent);
                }
            });
            ((AppointParkViewHolder) holder).linearlayout_orderdetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //订单详情点击事件
                    Intent intent = new Intent(mContext, ParkOrderDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("parkorderinfo", mData.get(position));
                    intent.putExtra("orderInfoBundle", bundle);
                    mContext.startActivity(intent);
                }
            });

            ((AppointParkViewHolder) holder).textview_cancleorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TipeDialog.Builder builder = new TipeDialog.Builder(mContext);
                    builder.setMessage("确定取消该订单吗？");
                    builder.setTitle("提示");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            onItemCancleOrder.onItemCancleOrder(position);
                        }
                    });

                    builder.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                    builder.create().show();
                }
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (position == 0) {
                lp.topMargin = DensityUtil.dp2px(mContext, 8);
            } else {
                lp.topMargin = DensityUtil.dp2px(mContext, 5);
            }
            ((AppointParkViewHolder) holder).linearlayout_all.setLayoutParams(lp);
        } else if (holder instanceof ReadyPayViewHolder) {
            String[] urlList = mData.get(position).getPictures().split(",");
            ImageUtil.showPic(((ReadyPayViewHolder) holder).imageview_bigshow,
                    urlList.length > 0 ? HttpConstants.ROOT_IMG_URL_PS + urlList[0] : "", R.mipmap.ic_img);
            ((ReadyPayViewHolder) holder).textview_parkspace_name.setText(mData.get(position).getParkLotName());
            ((ReadyPayViewHolder) holder).textview_address_description.setText(mData.get(position).getParkSpaceLocationDescribe());
            ((ReadyPayViewHolder) holder).textview_carnumber.setText("预停车辆：" + mData.get(position).getCar_numble());
            //计算时间
            float minutes = dateUtil.getTimeDifferenceMinute(mData.get(position).getPark_start_time(), mData.get(position).getPark_end_time(), false);
            ((ReadyPayViewHolder) holder).textview_parktime.setText("停车时长：" + (int) Math.floor(minutes / 60) + "小时" + (int) Math.floor(minutes % 60) + "分钟");
            ((ReadyPayViewHolder) holder).textview_order_fee.setText("¥ " + mData.get(position).getActual_pay_fee());
            final Discount_Info discount_info = mData.get(position).getDiscount();

            ((ReadyPayViewHolder) holder).linearlayout_fee_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //弹出费用详情的popupwindow
                    View view = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_parkorderfee_layout, null);
                    ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_discount_fee)).setText(mData.get(position).getActual_pay_fee());
                    ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_park_orderfee)).setText(mData.get(position).getOrder_fee() + "元");
                    ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_park_finefee)).setText(mData.get(position).getFine_fee() + "元");
                    String start = mData.get(position).getHigh_time().substring(0, mData.get(position).getHigh_time().indexOf(" - ")),
                            end = mData.get(position).getHigh_time().substring(mData.get(position).getHigh_time().indexOf(" - ") + 3, mData.get(position).getHigh_time().length()),
                            hightime1 = mData.get(position).getHigh_time().substring(0, mData.get(position).getHigh_time().indexOf(" - ")),
                            hightime2 = mData.get(position).getHigh_time().substring(mData.get(position).getHigh_time().indexOf(" - ") + 3, mData.get(position).getHigh_time().length());
                    if (Integer.parseInt(start.substring(0, start.indexOf(":"))) < Integer.parseInt(end.substring(0, end.lastIndexOf(":")))) {
                        ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_heightime)).setText(hightime1 + " - " + hightime2);
                        ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_lowtime)).setText(hightime2 + " - 次日" + hightime1);
                    } else {
                        ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_heightime)).setText(hightime1 + " - 次日" + hightime2);
                        ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_lowtime)).setText("次日" + hightime2 + " - 次日" + hightime1);
                    }
                    ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_fine)).setText("停车超出顺延时间按" + mData.get(position).getFine() + "元/小时收取额外超时费");
                    ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_heighfee)).setText(mData.get(position).getHigh_fee().equals("0.00") ? "免费停车" : mData.get(position).getHigh_fee() + "元/小时");
                    ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_lowfee)).setText(mData.get(position).getLow_fee().equals("0.00") ? "免费停车" : mData.get(position).getLow_fee() + "元/小时");
                    ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_redpacket)).setText(discount_info.getId().equals("-1") ? "未优惠" : "已优惠" + ((float) (Math.round(new Float(discount_info.getDiscount()) * 100)) / 100) + "元");

                    CustomPopWindow customPopWindow = new CustomPopWindow.PopupWindowBuilder(mContext)
                            .setView(view)
                            .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                            .setBgDarkAlpha(0.7f) // 控制亮度
                            .setFocusable(true)
                            .setOutsideTouchable(true)
                            .setAnimationStyle(R.style.CustomPopWindowStyle)
                            .enableOutsideTouchableDissmiss(true)// 设置false点击PopupWindow之外的地方，popWindow不关闭，如果不设置这个属性或者为true，则关闭
                            .create()
                            .showAtLocation(((ReadyPayViewHolder) holder).linearlayout_fee_details, Gravity.CENTER, 0, 0);
                }
            });
            ((ReadyPayViewHolder) holder).linearlayout_orderdetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转订单详情页面
                    Intent intent = new Intent(mContext, ParkOrderDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("parkorderinfo", mData.get(position));
                    intent.putExtra("orderInfoBundle", bundle);
                    mContext.startActivity(intent);
                }
            });
            ((ReadyPayViewHolder) holder).textview_gopay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //去支付按钮的点击事件
                    onItemOrderToPay.onItemOrderToPay(position);
                }
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (position == 0) {
                lp.topMargin = DensityUtil.dp2px(mContext, 8);
            } else {
                lp.topMargin = DensityUtil.dp2px(mContext, 5);
            }
            ((ReadyPayViewHolder) holder).linearlayout_all.setLayoutParams(lp);
        } else if (holder instanceof FinishParkViewHolder) {
            String[] urlList = mData.get(position).getPictures().split(",");
            ImageUtil.showImpPic(((FinishParkViewHolder) holder).imageview_bigshow,
                    urlList.length > 0 ? HttpConstants.ROOT_IMG_URL_PS + urlList[0] : "");

            ((FinishParkViewHolder) holder).textview_parkspace_name.setText(mData.get(position).getParkLotName());
            ((FinishParkViewHolder) holder).textview_address_description.setText(mData.get(position).getParkSpaceLocationDescribe());
            ((FinishParkViewHolder) holder).textview_carnumber.setText("预停车辆：" + mData.get(position).getCar_numble());
            //计算时间
            float minutes = dateUtil.getTimeDifferenceMinute(mData.get(position).getPark_start_time(), mData.get(position).getPark_end_time(), false);
            ((FinishParkViewHolder) holder).textview_parktime.setText("停车时长 " + (int) Math.floor(minutes / 60) + "小时" + (int) Math.floor(minutes % 60) + "分钟");
            ((FinishParkViewHolder) holder).textview_order_fee.setText("¥ " + mData.get(position).getActual_pay_fee());
            final Discount_Info discount_info = mData.get(position).getDiscount();

            switch (Integer.parseInt(mData.get(position).getOrder_status())) {
                case 4:
                    ((FinishParkViewHolder) holder).textview_order_state.setText("待评价");
                    ((FinishParkViewHolder) holder).textview_goevaluate.setVisibility(View.VISIBLE);
                    ((FinishParkViewHolder) holder).textview_goevaluate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //去评论按钮的点击事件
                            Intent intent = new Intent(mContext, CommentOrderActivity.class);
                            intent.putExtra("parkspace_id", mData.get(position).getParkLotId());
                            intent.putExtra("parkspace_img", mData.get(position).getPictures().split(",")[0]);
                            intent.putExtra("order_id", mData.get(position).getId());
                            intent.putExtra("city_code", mData.get(position).getCitycode());
                            intent.putExtra("park_time", mData.get(position).getPark_start_time());
                            mContext.startActivity(intent);
                        }
                    });
                    break;
                case 5:
                    ((FinishParkViewHolder) holder).textview_order_state.setText("已完成");
                    ((FinishParkViewHolder) holder).textview_goevaluate.setVisibility(View.GONE);
                    break;
            }
            ((FinishParkViewHolder) holder).linearlayout_fee_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //弹出费用详情的popupwindow
                    View view = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_parkorderfee_layout, null);
                    ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_discount_fee)).setText(mData.get(position).getActual_pay_fee());
                    ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_park_orderfee)).setText(mData.get(position).getOrder_fee() + "元");
                    ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_park_finefee)).setText(mData.get(position).getFine_fee() + "元");
                    String start = mData.get(position).getHigh_time().substring(0, mData.get(position).getHigh_time().indexOf(" - ")),
                            end = mData.get(position).getHigh_time().substring(mData.get(position).getHigh_time().indexOf(" - ") + 3, mData.get(position).getHigh_time().length()),
                            hightime1 = mData.get(position).getHigh_time().substring(0, mData.get(position).getHigh_time().indexOf(" - ")),
                            hightime2 = mData.get(position).getHigh_time().substring(mData.get(position).getHigh_time().indexOf(" - ") + 3, mData.get(position).getHigh_time().length());
                    if (Integer.parseInt(start.substring(0, start.indexOf(":"))) < Integer.parseInt(end.substring(0, end.lastIndexOf(":")))) {
                        ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_heightime)).setText(hightime1 + " - " + hightime2);
                        ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_lowtime)).setText(hightime2 + " - 次日" + hightime1);
                    } else {
                        ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_heightime)).setText(hightime1 + " - 次日" + hightime2);
                        ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_lowtime)).setText("次日" + hightime2 + " - 次日" + hightime1);
                    }
                    ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_fine)).setText("停车超出顺延时间按" + mData.get(position).getFine() + "元/小时收取额外超时费");
                    ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_heighfee)).setText(mData.get(position).getHigh_fee().equals("0.00") ? "免费停车" : mData.get(position).getHigh_fee() + "元/小时");
                    ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_lowfee)).setText(mData.get(position).getLow_fee().equals("0.00") ? "免费停车" : mData.get(position).getLow_fee() + "元/小时");
                    ((TextView) view.findViewById(R.id.id_popupwindow_parkorderfee_layout_textview_redpacket)).setText(discount_info.getId().equals("-1") ? "未优惠" : "已优惠" + ((float) (Math.round(new Float(discount_info.getDiscount()) * 100)) / 100) + "元");

                    CustomPopWindow customPopWindow = new CustomPopWindow.PopupWindowBuilder(mContext)
                            .setView(view)
                            .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                            .setBgDarkAlpha(0.7f) // 控制亮度
                            .setFocusable(true)
                            .setOutsideTouchable(true)
                            .setAnimationStyle(R.style.CustomPopWindowStyle)
                            .enableOutsideTouchableDissmiss(true)// 设置false点击PopupWindow之外的地方，popWindow不关闭，如果不设置这个属性或者为true，则关闭
                            .create()
                            .showAtLocation(((FinishParkViewHolder) holder).linearlayout_fee_details, Gravity.CENTER, 0, 0);
                }
            });
            ((FinishParkViewHolder) holder).textview_rent_again.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //再次租用按钮的点击事件
                    Intent intent = new Intent(mContext, ParkspaceDetailActivity.class);
                    intent.putExtra("parkspace_id", mData.get(position).getParkLotId());
                    intent.putExtra("city_code", mData.get(position).getCitycode());
                    mContext.startActivity(intent);
                }
            });

            ((FinishParkViewHolder) holder).linearlayout_orderdetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转订单详情页面
                    Intent intent = new Intent(mContext, ParkOrderDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("parkorderinfo", mData.get(position));
                    intent.putExtra("orderInfoBundle", bundle);
                    mContext.startActivity(intent);
                }
            });

            ((FinishParkViewHolder) holder).imageview_delete_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //删除订单按钮的点击事件
                    TipeDialog.Builder builder = new TipeDialog.Builder(mContext);
                    builder.setMessage("确定删除该订单吗？");
                    builder.setTitle("提示");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            onItemDeleteOrder.onItemDeleteOrder(position);
                        }
                    });

                    builder.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                    builder.create().show();
                }
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (position == 0) {
                lp.topMargin = DensityUtil.dp2px(mContext, 8);
            } else {
                lp.topMargin = DensityUtil.dp2px(mContext, 5);
            }
            ((FinishParkViewHolder) holder).linearlayout_all.setLayoutParams(lp);
        } else {

            String[] urlList = mData.get(position).getPictures().split(",");
            ImageUtil.showImpPic(((CancleParkViewHolder) holder).imageview_bigshow,
                    urlList.length > 0 ? HttpConstants.ROOT_IMG_URL_PS + urlList[0] : "");
            ((CancleParkViewHolder) holder).textview_parkspace_name.setText(mData.get(position).getParkLotName());
            ((CancleParkViewHolder) holder).textview_address.setText(mData.get(position).getParkLotAddress());
            ((CancleParkViewHolder) holder).textview_address_description.setText(mData.get(position).getParkSpaceLocationDescribe());
            ((CancleParkViewHolder) holder).textview_startime.setText("进场时间：" + mData.get(position).getOrder_starttime().substring(5, mData.get(position).getOrder_starttime().length() - 3));
            ((CancleParkViewHolder) holder).textview_carnumber.setText("预停车辆：" + mData.get(position).getCar_numble());

            ((CancleParkViewHolder) holder).imageview_delete_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //删除订单按钮的点击事件
                    TipeDialog.Builder builder = new TipeDialog.Builder(mContext);
                    builder.setMessage("确定删除该订单吗？");
                    builder.setTitle("提示");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            onItemDeleteOrder.onItemDeleteOrder(position);
                        }
                    });

                    builder.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                    builder.create().show();
                }
            });
            ((CancleParkViewHolder) holder).textview_rent_again.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //再次租用按钮的点击事件
                    Intent intent = new Intent(mContext, ParkspaceDetailActivity.class);
                    intent.putExtra("parkspace_id", mData.get(position).getParkLotId());
                    intent.putExtra("city_code", mData.get(position).getCitycode());
                    mContext.startActivity(intent);
                }
            });

            ((CancleParkViewHolder) holder).linearlayout_orderdetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转订单详情页面
                    Intent intent = new Intent(mContext, ParkOrderDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("parkorderinfo", mData.get(position));
                    intent.putExtra("orderInfoBundle", bundle);
                    mContext.startActivity(intent);
                }
            });

            switch (Integer.parseInt(mData.get(position).getOrder_status())) {
                case 6:
                    ((CancleParkViewHolder) holder).textview_order_state.setText("已取消");
                    break;
                case 7:
                    ((CancleParkViewHolder) holder).textview_order_state.setText("已过期");
                    break;
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (position == 0) {
                lp.topMargin = DensityUtil.dp2px(mContext, 8);
            } else {
                lp.topMargin = DensityUtil.dp2px(mContext, 5);
            }
            ((CancleParkViewHolder) holder).linearlayout_all.setLayoutParams(lp);
        }
    }

    public class AppointParkViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageview_bigshow;
        private TextView textview_parkspace_name, textview_startime, textview_address, textview_address_description, textview_cancleorder, textview_openlock, textview_carnumber;
        private LinearLayout linearlayout_orderdetail, linearlayout_all;

        AppointParkViewHolder(View itemView) {
            super(itemView);
            imageview_bigshow = itemView.findViewById(R.id.id_item_appointparkorder_layout_imageview_bigshow);
            textview_parkspace_name = itemView.findViewById(R.id.id_item_appointparkorder_layout_textview_parkspace_name);
            textview_startime = itemView.findViewById(R.id.id_item_appointparkorder_layout_textview_startime);
            textview_address = itemView.findViewById(R.id.id_item_appointparkorder_layout_textview_address);
            textview_address_description = itemView.findViewById(R.id.id_item_appointparkorder_layout_textview_address_description);
            textview_cancleorder = itemView.findViewById(R.id.id_item_appointparkorder_layout_textview_cancleorder);
            textview_openlock = itemView.findViewById(R.id.id_item_appointparkorder_layout_textview_openlock);
            textview_carnumber = itemView.findViewById(R.id.id_item_appointparkorder_layout_textview_carnumber);
            linearlayout_orderdetail = itemView.findViewById(R.id.id_item_appointparkorder_layout_linearlayout_orderdetail);
            linearlayout_all = itemView.findViewById(R.id.id_item_appointparkorder_layout_linearlayout_all);
        }
    }

    public class ReadyPayViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageview_bigshow;
        private TextView textview_parkspace_name, textview_parktime, textview_address_description, textview_order_fee, textview_gopay, textview_carnumber;
        private LinearLayout linearlayout_orderdetail, linearlayout_fee_details, linearlayout_all;

        ReadyPayViewHolder(View itemView) {
            super(itemView);
            imageview_bigshow = itemView.findViewById(R.id.id_item_parkreadypay_layout_imageview_bigshow);
            textview_parkspace_name = itemView.findViewById(R.id.id_item_parkreadypay_layout_textview_parkspace_name);
            textview_parktime = itemView.findViewById(R.id.id_item_parkreadypay_layout_textview_parktime);
            textview_address_description = itemView.findViewById(R.id.id_item_parkreadypay_layout_textview_address_description);
            textview_order_fee = itemView.findViewById(R.id.id_item_parkreadypay_layout_textview_order_fee);
            textview_gopay = itemView.findViewById(R.id.id_item_parkreadypay_layout_textview_gopay);
            textview_carnumber = itemView.findViewById(R.id.id_item_parkreadypay_layout_textview_carnumber);
            linearlayout_orderdetail = itemView.findViewById(R.id.id_item_parkreadypay_layout_linearlayout_orderdetail);
            linearlayout_fee_details = itemView.findViewById(R.id.id_item_parkreadpay_layout_linearlayout_fee_details);
            linearlayout_all = itemView.findViewById(R.id.id_item_parkreadypay_layout_linearlayout_all);
        }
    }

    public class FinishParkViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageview_delete_order, imageview_bigshow;
        private TextView textview_parkspace_name, textview_order_state, textview_parktime, textview_address_description,
                textview_order_fee, textview_goevaluate, textview_rent_again, textview_carnumber;
        private LinearLayout linearlayout_fee_details, linearlayout_orderdetail, linearlayout_all;

        FinishParkViewHolder(View itemView) {
            super(itemView);
            imageview_delete_order = itemView.findViewById(R.id.id_item_finishparkorder_layout_imageview_delete_order);
            imageview_bigshow = itemView.findViewById(R.id.id_item_finishparkorder_layout_imageview_bigshow);
            textview_parkspace_name = itemView.findViewById(R.id.id_item_finishparkorder_layout_textview_parkspace_name);
            textview_order_state = itemView.findViewById(R.id.id_item_finishparkorder_layout_textview_order_state);
            textview_parktime = itemView.findViewById(R.id.id_item_finishparkorder_layout_textview_parktime);
            textview_address_description = itemView.findViewById(R.id.id_item_finishparkorder_layout_textview_address_description);
            textview_goevaluate = itemView.findViewById(R.id.id_item_finishparkorder_layout_textview_goevaluate);
            textview_rent_again = itemView.findViewById(R.id.id_item_finishparkorder_layout_textview_rent_again);
            textview_order_fee = itemView.findViewById(R.id.id_item_finishparkorder_layout_textview_order_fee);
            textview_carnumber = itemView.findViewById(R.id.id_item_finishparkorder_layout_textview_carnumber);
            linearlayout_fee_details = itemView.findViewById(R.id.id_item_finishparkorder_layout_linearlayout_fee_details);
            linearlayout_orderdetail = itemView.findViewById(R.id.id_item_finishparkorder_layout_linearlayout_orderdetail);
            linearlayout_all = itemView.findViewById(R.id.id_item_finishparkorder_layout_linearlayout_all);
        }
    }

    public class CancleParkViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageview_delete_order, imageview_bigshow;
        private TextView textview_parkspace_name, textview_order_state, textview_startime, textview_address, textview_address_description, textview_rent_again, textview_carnumber;
        private LinearLayout linearlayout_orderdetail, linearlayout_all;

        public CancleParkViewHolder(View itemView) {
            super(itemView);
            imageview_delete_order = itemView.findViewById(R.id.id_item_cancleparkorder_layout_imageview_delete_order);
            imageview_bigshow = itemView.findViewById(R.id.id_item_cancleparkorder_layout_imageview_bigshow);
            textview_parkspace_name = (TextView) itemView.findViewById(R.id.id_item_cancleparkorder_layout_textview_parkspace_name);
            textview_order_state = (TextView) itemView.findViewById(R.id.id_item_cancleparkorder_layout_textview_order_state);
            textview_startime = (TextView) itemView.findViewById(R.id.id_item_cancleparkorder_layout_textview_startime);
            textview_address = (TextView) itemView.findViewById(R.id.id_item_cancleparkorder_layout_textview_address);
            textview_address_description = (TextView) itemView.findViewById(R.id.id_item_cancleparkorder_layout_textview_address_description);
            textview_rent_again = (TextView) itemView.findViewById(R.id.id_item_cancleparkorder_layout_textview_rent_again);
            textview_carnumber = (TextView) itemView.findViewById(R.id.id_item_cancleparkorder_layout_textview_carnumber);
            linearlayout_orderdetail = (LinearLayout) itemView.findViewById(R.id.id_item_cancleparkorder_layout_linearlayout_orderdetail);
            linearlayout_all = (LinearLayout) itemView.findViewById(R.id.id_item_cancleparkorder_layout_linearlayout_all);
        }
    }

    public interface OnItemOrderToPay {
        void onItemOrderToPay(int position);
    }

    public void setOnItemOrderToPay(OnItemOrderToPay onItemOrderToPay) {
        this.onItemOrderToPay = onItemOrderToPay;
    }

    public interface OnItemDeleteOrder {
        void onItemDeleteOrder(int position);
    }

    public void setOnItemDeleteOrder(OnItemDeleteOrder onItemDeleteOrder) {
        this.onItemDeleteOrder = onItemDeleteOrder;
    }

    public interface OnItemCancleOrder {
        void onItemCancleOrder(int position);
    }

    public void setOnItemCancleOrder(OnItemCancleOrder onItemCancleOrder) {
        this.onItemCancleOrder = onItemCancleOrder;
    }
}
