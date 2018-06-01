package com.tuzhao.publicwidget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;

/**
 * Created by juncoder on 2018/5/31.
 */

public class CustomDialog extends Dialog {

    public CustomDialog(@NonNull Context context, View view) {
        super(context, R.style.ParkDialog);
        setContentView(view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public CustomDialog(@NonNull Context context, ParkOrderInfo parkOrderInfo, boolean showParkFee) {
        super(context, R.style.ParkDialog);
        setContentView(R.layout.dialog_park_detail_layout);
        TextView mAppointmentParkTime = findViewById(R.id.appointment_start_park_time);
        TextView mActualParkTime = findViewById(R.id.actual_start_park_time);
        TextView mAppointParkDuration = findViewById(R.id.appointment_park_duration);
        TextView mActualParkDuration = findViewById(R.id.actual_park_duration);
        TextView mOvertimeDuration = findViewById(R.id.overtime_duration);
        mAppointmentParkTime.setText(DateUtil.deleteSecond(parkOrderInfo.getOrder_starttime()));
        mActualParkTime.setText(DateUtil.deleteSecond(parkOrderInfo.getPark_start_time()));
        mAppointParkDuration.setText(DateUtil.getDateDistanceForHourWithMinute(parkOrderInfo.getOrder_starttime(), parkOrderInfo.getOrder_endtime()));
        mActualParkDuration.setText(DateUtil.getDateDistanceForHourWithMinute(parkOrderInfo.getPark_start_time(), parkOrderInfo.getPark_end_time()));

        if (DateUtil.getYearToSecondCalendar(parkOrderInfo.getOrder_endtime(), parkOrderInfo.getExtensionTime()).compareTo(
                DateUtil.getYearToSecondCalendar(parkOrderInfo.getPark_end_time())) < 0) {
            //停车时长超过预约时长
            String string = DateUtil.getDateDistanceForHourWithMinute(parkOrderInfo.getOrder_endtime(), parkOrderInfo.getPark_end_time(), parkOrderInfo.getExtensionTime());
            SpannableString timeout = new SpannableString(string);
            if (string.contains("小时")) {
                timeout.setSpan(new ForegroundColorSpan(Color.parseColor("#ff2020")), 0, string.indexOf("小时"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (string.contains("分钟")) {
                timeout.setSpan(new ForegroundColorSpan(Color.parseColor("#ff2020")), string.indexOf("小时") + 2, string.indexOf("分钟"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            mOvertimeDuration.setText(timeout);
        }

        if (showParkFee) {
            TextView parkFee = findViewById(R.id.park_fee);
            SpannableString spannableString = new SpannableString(DateUtil.decreseOneZero(Double.parseDouble(parkOrderInfo.getActual_pay_fee())) + "元");
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#1dd0a1")), 0, spannableString.length() - 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            parkFee.setText(spannableString);

            TextView fee = findViewById(R.id.park_fee_tv);
            fee.setVisibility(View.VISIBLE);
            ConstraintLayout constraintLayout = findViewById(R.id.dialog_park_detail_cl);
            constraintLayout.setPadding(0, 0, 0, DensityUtil.dp2px(context, 15));
        }

        ImageView imageView = findViewById(R.id.dialog_dismiss);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
