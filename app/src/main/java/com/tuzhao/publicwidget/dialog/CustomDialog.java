package com.tuzhao.publicwidget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.IntentObserable;

import java.text.DecimalFormat;

/**
 * Created by juncoder on 2018/5/31.
 */

public class CustomDialog extends Dialog {

    private boolean mIsShowAnimation;

    public CustomDialog(@NonNull Context context, View view) {
        super(context, R.style.ParkDialog);
        setContentView(view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public CustomDialog(@NonNull Context context, View view, boolean isShowAnimation) {
        super(context, R.style.ParkDialog);
        setContentView(view);
        mIsShowAnimation = isShowAnimation;
        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.ParkAnimationStyle);
        }
    }

    public CustomDialog(@NonNull Context context, ParkOrderInfo parkOrderInfo, boolean showParkFee) {
        super(context, R.style.ParkDialog);
        setContentView(R.layout.dialog_order_detail_layout);
        TextView appointmentParkTime = findViewById(R.id.appointment_start_park_time);
        TextView actualParkTime = findViewById(R.id.actual_start_park_time);
        TextView appointParkDuration = findViewById(R.id.appointment_park_duration);
        TextView actualParkDuration = findViewById(R.id.actual_park_duration);
        TextView gracePeriodDuration = findViewById(R.id.grace_period);
        TextView overtimeDuration = findViewById(R.id.overtime_duration);
        TextView overtimeFee = findViewById(R.id.overtime_fee);
        appointmentParkTime.setText(DateUtil.deleteSecond(parkOrderInfo.getOrder_starttime()));
        actualParkTime.setText(DateUtil.deleteSecond(parkOrderInfo.getPark_start_time()));
        appointParkDuration.setText(DateUtil.getDistanceForDayHourMinute(parkOrderInfo.getOrder_starttime(), parkOrderInfo.getOrder_endtime()));
        actualParkDuration.setText(DateUtil.getDistanceForDayHourMinute(parkOrderInfo.getPark_start_time(), parkOrderInfo.getPark_end_time()));

        String gracePeriod = Integer.valueOf(parkOrderInfo.getExtensionTime()) / 60 + "分钟";
        gracePeriodDuration.setText(gracePeriod);

        if (DateUtil.getYearToSecondCalendar(parkOrderInfo.getOrder_endtime(), parkOrderInfo.getExtensionTime()).compareTo(
                DateUtil.getYearToSecondCalendar(parkOrderInfo.getPark_end_time())) < 0) {
            //停车时长超过预约时长

            int overtimeMinutes = 0;    //超时的分钟数
            String string = DateUtil.getDistanceForDayHourMinuteAddStart(parkOrderInfo.getOrder_endtime(), parkOrderInfo.getPark_end_time(), parkOrderInfo.getExtensionTime());
            SpannableString timeout = new SpannableString(string);
            int dayPosition = -1;
            int hourPosition = -1;
            if (string.contains("天")) {
                dayPosition = string.indexOf("天");
                overtimeMinutes = Integer.valueOf(string.substring(0, dayPosition)) * 60 * 24;
                timeout.setSpan(new ForegroundColorSpan(Color.parseColor("#ff2020")), 0, dayPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (string.contains("小时")) {
                hourPosition = string.indexOf("小时");
                if (dayPosition != -1) {
                    overtimeMinutes = Integer.valueOf(string.substring(dayPosition + 1, hourPosition)) * 60;
                } else {
                    overtimeMinutes = Integer.valueOf(string.substring(0, hourPosition)) * 60;
                }
                timeout.setSpan(new ForegroundColorSpan(Color.parseColor("#ff2020")), dayPosition + 1, hourPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (string.contains("分钟")) {
                if (hourPosition != -1) {
                    overtimeMinutes += Integer.valueOf(string.substring(hourPosition + 2, string.indexOf("分钟")));
                    timeout.setSpan(new ForegroundColorSpan(Color.parseColor("#ff2020")), hourPosition + 2, string.indexOf("分钟"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    overtimeMinutes += Integer.valueOf(string.substring(dayPosition + 1, string.indexOf("分钟")));
                    timeout.setSpan(new ForegroundColorSpan(Color.parseColor("#ff2020")), 0, string.indexOf("分钟"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            overtimeDuration.setText(timeout);

            if (showParkFee) {
                //订单详情界面直接显示超时费用
                String fineFee = DateUtil.decreseOneZero(parkOrderInfo.getFine_fee()) + "元";
                SpannableString spannableString = new SpannableString(fineFee);
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff2020")), 0, fineFee.length() - 1,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                overtimeFee.setText(spannableString);
            } else {
                //计算出超时费用
                String fineFee = DateUtil.decreseOneZero(new DecimalFormat("0.00").format(
                        Double.valueOf(parkOrderInfo.getFine()) * overtimeMinutes / 60)) + "元";
                SpannableString spannableString = new SpannableString(fineFee);
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff2020")), 0, fineFee.length() - 1,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                overtimeFee.setText(spannableString);
            }

            findViewById(R.id.overtime_duration_tv).setVisibility(View.VISIBLE);
            findViewById(R.id.overtime_fee_tv).setVisibility(View.VISIBLE);
        }

        if (showParkFee) {
            //已完成的订单则显示停车费用
            TextView parkFee = findViewById(R.id.park_fee);
            SpannableString spannableString = new SpannableString(DateUtil.decreseOneZero(Double.parseDouble(parkOrderInfo.getActual_pay_fee())) + "元");
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#1dd0a1")), 0, spannableString.length() - 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            parkFee.setText(spannableString);

            TextView fee = findViewById(R.id.park_fee_tv);
            fee.setVisibility(View.VISIBLE);
        }

        ImageView imageView = findViewById(R.id.dialog_dismiss);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public CustomDialog(@NonNull Context context, ParkOrderInfo parkOrderInfo, int status) {
        super(context, R.style.ParkDialog);
        setContentView(R.layout.dialog_order_detail_layout);
        TextView appointmentParkTime = findViewById(R.id.appointment_start_park_time);
        TextView actualParkTime = findViewById(R.id.actual_start_park_time);
        TextView appointParkDuration = findViewById(R.id.appointment_park_duration);
        TextView actualPark = findViewById(R.id.actual_park_duration_tv);
        TextView actualParkDuration = findViewById(R.id.actual_park_duration);
        TextView gracePeriodDuration = findViewById(R.id.grace_period);
        TextView overtimeDuration = findViewById(R.id.overtime_duration);
        TextView overtimeFee = findViewById(R.id.overtime_fee);
        appointmentParkTime.setText(DateUtil.deleteSecond(parkOrderInfo.getOrder_starttime()));
        actualParkTime.setText(DateUtil.deleteSecond(parkOrderInfo.getPark_start_time()));
        appointParkDuration.setText(DateUtil.getDistanceForDayHourMinute(parkOrderInfo.getOrder_starttime(), parkOrderInfo.getOrder_endtime()));
        actualPark.setText("预计离场");
        actualParkDuration.setText(DateUtil.deleteSecond(parkOrderInfo.getOrder_endtime()));

        String gracePeriod = Integer.valueOf(parkOrderInfo.getExtensionTime()) / 60 + "分钟";
        gracePeriodDuration.setText(gracePeriod);

        if (DateUtil.getYearToSecondCalendar(parkOrderInfo.getOrder_endtime(), parkOrderInfo.getExtensionTime()).compareTo(
                DateUtil.getYearToSecondCalendar(DateUtil.getCurrentYearToSecond())) < 0) {
            //停车时长超过预约时长

            int overtimeMinutes = 0;    //超时的分钟数
            String string = DateUtil.getDistanceForDayHourMinuteAddStart(parkOrderInfo.getOrder_endtime(), DateUtil.getCurrentYearToSecond(), parkOrderInfo.getExtensionTime());
            SpannableString timeout = new SpannableString(string);
            int dayPosition = -1;
            int hourPosition = -1;
            if (string.contains("天")) {
                dayPosition = string.indexOf("天");
                overtimeMinutes = Integer.valueOf(string.substring(0, dayPosition)) * 60 * 24;
                timeout.setSpan(new ForegroundColorSpan(Color.parseColor("#ff2020")), 0, dayPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (string.contains("小时")) {
                hourPosition = string.indexOf("小时");
                if (dayPosition != -1) {
                    overtimeMinutes = Integer.valueOf(string.substring(dayPosition + 1, hourPosition)) * 60;
                } else {
                    overtimeMinutes = Integer.valueOf(string.substring(0, hourPosition)) * 60;
                }
                timeout.setSpan(new ForegroundColorSpan(Color.parseColor("#ff2020")), dayPosition + 1, hourPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (string.contains("分钟")) {
                if (hourPosition != -1) {
                    overtimeMinutes += Integer.valueOf(string.substring(hourPosition + 2, string.indexOf("分钟")));
                    timeout.setSpan(new ForegroundColorSpan(Color.parseColor("#ff2020")), hourPosition + 2, string.indexOf("分钟"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    overtimeMinutes += Integer.valueOf(string.substring(dayPosition + 1, string.indexOf("分钟")));
                    timeout.setSpan(new ForegroundColorSpan(Color.parseColor("#ff2020")), 0, string.indexOf("分钟"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            overtimeDuration.setText(timeout);

            //计算出超时费用
            String fineFee = DateUtil.decreseOneZero(new DecimalFormat("0.00").format(
                    Double.valueOf(parkOrderInfo.getFine()) * overtimeMinutes / 60)) + "元";
            SpannableString spannableString = new SpannableString(fineFee);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff2020")), 0, fineFee.length() - 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            overtimeFee.setText(spannableString);

            findViewById(R.id.overtime_duration_tv).setVisibility(View.VISIBLE);
            findViewById(R.id.overtime_fee_tv).setVisibility(View.VISIBLE);
        }

        TextView parkFee = findViewById(R.id.park_fee);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        SpannableString spannableString = new SpannableString("约" + decimalFormat.format(DateUtil.caculateParkFee(DateUtil.deleteSecond(parkOrderInfo.getOrder_starttime()),
                DateUtil.getCurrentYearToMinutes(), parkOrderInfo.getHigh_time(), Double.valueOf(parkOrderInfo.getHigh_fee()),
                Double.valueOf(parkOrderInfo.getLow_fee()))) + "元");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#1dd0a1")), 1, spannableString.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        parkFee.setText(spannableString);

        TextView fee = findViewById(R.id.park_fee_tv);
        fee.setText("预计费用");
        fee.setVisibility(View.VISIBLE);

        ImageView imageView = findViewById(R.id.dialog_dismiss);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public CustomDialog(@NonNull Context context, ParkOrderInfo parkOrderInfo) {
        super(context, R.style.ParkDialog);
        setContentView(R.layout.dialog_appointment_detail_layout);
        TextView parkSpaceNumber = findViewById(R.id.real_name_et);
        TextView carNumber = findViewById(R.id.car_number);
        TextView appointStartParkTime = findViewById(R.id.appointment_start_park_time);
        TextView appointParkDuration = findViewById(R.id.appointment_park_duration);
        TextView gracePeriodDuration = findViewById(R.id.grace_period);
        TextView estimatedCost = findViewById(R.id.estimated_cost);

        parkSpaceNumber.setText(parkOrderInfo.getParkNumber());
        carNumber.setText(parkOrderInfo.getCar_numble());
        appointStartParkTime.setText(parkOrderInfo.getOrder_starttime().substring(0, parkOrderInfo.getOrder_starttime().lastIndexOf(":")));
        appointParkDuration.setText(DateUtil.getDistanceForDayHourMinute(parkOrderInfo.getOrder_starttime(), parkOrderInfo.getOrder_endtime()));
        String gracePeriod = Integer.valueOf(parkOrderInfo.getExtensionTime()) / 60 + "分钟";
        gracePeriodDuration.setText(gracePeriod);

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        SpannableString spannableString = new SpannableString("约" + decimalFormat.format(DateUtil.caculateParkFee(DateUtil.deleteSecond(parkOrderInfo.getOrder_starttime()),
                DateUtil.deleteSecond(parkOrderInfo.getOrder_endtime()), parkOrderInfo.getHigh_time(), Double.valueOf(parkOrderInfo.getHigh_fee()),
                Double.valueOf(parkOrderInfo.getLow_fee()))) + "元");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#1dd0a1")), 1, spannableString.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        estimatedCost.setText(spannableString);

        ImageView imageView = findViewById(R.id.dialog_dismiss);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        if (mIsShowAnimation) {
            Intent intent = new Intent();
            intent.setAction(ConstansUtil.DIALOG_SHOW);
            IntentObserable.dispatch(intent);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mIsShowAnimation) {
            Intent intent = new Intent();
            intent.setAction(ConstansUtil.DIALOG_DISMISS);
            IntentObserable.dispatch(intent);
        }
    }
}
