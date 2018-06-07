package com.tuzhao.fragment.parkorder;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.pickerview.OptionsPickerView;
import com.tuzhao.R;
import com.tuzhao.activity.BigPictureActivity;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.IntentObserable;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/6/5.
 */
public class ParkingOrderFragment extends BaseStatusFragment implements View.OnClickListener {

    private ParkOrderInfo mParkOrderInfo;

    private TextView mParkDate;

    private TextView mStartParkTime;

    private TextView mRemainTime;

    private TextView mParkSpaceLocation;

    private TextView mParkDuration;

    private TextView mLeaveTime;

    private TextView mOvertimeFee;

    private ArrayList<String> mParkSpacePictures;

    private CustomDialog mCustomDialog;

    private OptionsPickerView<String> mPickerView;

    private ArrayList<ArrayList<String>> mHours;

    private ArrayList<String> mMinutes;

    public static ParkingOrderFragment newInstance(ParkOrderInfo parkOrderInfo) {
        ParkingOrderFragment fragment = new ParkingOrderFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstansUtil.PARK_ORDER_INFO, parkOrderInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int resourceId() {
        return R.layout.fragment_parking_order_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mParkOrderInfo = (ParkOrderInfo) getArguments().getSerializable(ConstansUtil.PARK_ORDER_INFO);
        }

        mParkDate = view.findViewById(R.id.appointment_park_date);
        mStartParkTime = view.findViewById(R.id.appointment_income_time);
        mRemainTime = view.findViewById(R.id.appointment_income_time_tv);
        mLeaveTime = view.findViewById(R.id.leave_time);
        mOvertimeFee = view.findViewById(R.id.overtime_fee);
        mParkSpaceLocation = view.findViewById(R.id.appointment_park_location);
        mParkDuration = view.findViewById(R.id.park_duration);

        view.findViewById(R.id.appointment_calculate_rule).setOnClickListener(this);
        view.findViewById(R.id.appointment_calculate_rule_iv).setOnClickListener(this);
        view.findViewById(R.id.car_pic_cl).setOnClickListener(this);
        view.findViewById(R.id.cancel_appoint_cl).setOnClickListener(this);
        view.findViewById(R.id.contact_service_cl).setOnClickListener(this);
        view.findViewById(R.id.view_appointment_detail).setOnClickListener(this);
        view.findViewById(R.id.view_appointment_detail_iv).setOnClickListener(this);
        view.findViewById(R.id.finish_park).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mParkDate.setText(DateUtil.getPointToMinute(mParkOrderInfo.getPark_start_time()));
        if (DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getExtensionTime()).compareTo(
                DateUtil.getYearToSecondCalendar(DateUtil.getCurrentYearToSecond())) < 0) {
            //超时了
            mStartParkTime.setText(DateUtil.getDistanceForDayTimeMinuteAddStart(mParkOrderInfo.getOrder_endtime(), DateUtil.getCurrentYearToSecond(), mParkOrderInfo.getExtensionTime()));
            mRemainTime.setText("（已超时）");
        } else {
            mStartParkTime.setText(DateUtil.getDistanceForDayTimeMinuteAddEnd(DateUtil.getCurrentYearToSecond(), mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getExtensionTime()));
        }
        mParkSpaceLocation.setText(mParkOrderInfo.getAddress_memo());
        mParkDuration.setText(DateUtil.getDistanceForDayTimeMinute(mParkOrderInfo.getPark_start_time(), DateUtil.getCurrentYearToSecond()));
        String leaveTime = "需在" + DateUtil.getYearToMinute(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getExtensionTime()) + "前离场";
        mLeaveTime.setText(leaveTime);
        String overtimeFee = "超时按" + mParkOrderInfo.getFine() + "/小时收费";
        mOvertimeFee.setText(overtimeFee);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appointment_calculate_rule:
            case R.id.appointment_calculate_rule_iv:

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
                        .setMessage("确定延长时间吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO: 2018/6/5
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
            case R.id.finish_park:
                finishPark();
                break;
        }
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

    private void initOptionPicker() {
        if (mHours == null) {
            mHours = new ArrayList<>();
            mMinutes = new ArrayList<>();


        }
    }

    private void showOptionPicker() {

    }

    private void finishPark() {
        //请求改变订单状态，完成订单
        showLoadingDialog("正在结束停车");
        getOkGo(HttpConstants.endParking)
                .params("order_id", mParkOrderInfo.getId())
                .params("citycode", mParkOrderInfo.getCitycode())
                .params("pass_code", DensityUtil.MD5code(UserManager.getInstance().getUserInfo().getSerect_code() + "*&*" + UserManager.getInstance().getUserInfo().getCreate_time() + "*&*" + UserManager.getInstance().getUserInfo().getId()))
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> info, Call call, Response response) {
                        Intent intent = new Intent();
                        intent.setAction(ConstansUtil.FINISH_PARK);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
                        intent.putExtra(ConstansUtil.FOR_REQUEST_RESULT, bundle);
                        IntentObserable.dispatch(intent);
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "103":
                                    showFiveToast("该订单不存在");
                                    break;
                                default:
                                    showFiveToast("服务器异常，请稍后重试");
                                    break;
                            }
                        }
                    }
                });
    }

}
