package com.tuzhao.fragment.parkorder;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.BigPictureActivity;
import com.tuzhao.activity.jiguang_notification.MyReceiver;
import com.tuzhao.activity.jiguang_notification.OnCtrlLockListener;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/5/29.
 */

public class AppointmentDetailFragment extends BaseStatusFragment implements View.OnClickListener {

    private ParkOrderInfo mParkOrderInfo;

    private TextView mStartParkTime;

    private TextView mParkSpaceLocation;

    private TextView mParkDuration;

    private ConstraintLayout mConstraintLayout;

    private TextView mParkLotName;

    private TextView mParkSpaceNumber;

    private TextView mAppointStartParkTime;

    private TextView mAppointParkDuration;

    private TextView mEstimatedCost;

    private TextView mOpenLock;

    private ArrayList<String> mParkSpacePictures;

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

        mStartParkTime = view.findViewById(R.id.start_park_time);
        mParkSpaceLocation = view.findViewById(R.id.park_space_location);
        mParkDuration = view.findViewById(R.id.park_duration);
        mParkLotName = view.findViewById(R.id.park_lot_name);
        mParkSpaceNumber = view.findViewById(R.id.park_space_number);
        mAppointStartParkTime = view.findViewById(R.id.appointment_start_park_time);
        mAppointParkDuration = view.findViewById(R.id.appointment_park_duration);
        mEstimatedCost = view.findViewById(R.id.estimated_cost);
        mConstraintLayout = view.findViewById(R.id.appointment_detail_cl);
        mOpenLock = view.findViewById(R.id.open_lock);

        view.findViewById(R.id.appointment_calculate_rule).setOnClickListener(this);
        view.findViewById(R.id.car_pic_cl).setOnClickListener(this);
        view.findViewById(R.id.cancel_appoint_cl).setOnClickListener(this);
        view.findViewById(R.id.contact_service_cl).setOnClickListener(this);
        view.findViewById(R.id.view_appointment_detail_cl).setOnClickListener(this);
        mOpenLock.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mStartParkTime.setText(DateUtil.getHourToMinute(mParkOrderInfo.getOrder_starttime()));
        mParkSpaceLocation.setText(mParkOrderInfo.getAddress_memo());

        int parkDuration = DateUtil.getMinutesDistance(mParkOrderInfo.getOrder_starttime(), mParkOrderInfo.getOrder_endtime());
        StringBuilder duration = new StringBuilder();
        if (parkDuration / 24 / 60 >= 1) {
            duration.append(parkDuration / 24 / 60);
            duration.append("天");
            parkDuration -= (parkDuration / 24 / 60) * 24 * 60;
        }
        if (parkDuration / 60 >= 1) {
            duration.append(parkDuration / 60);
            duration.append("小时");
            parkDuration -= (parkDuration / 60) * 60;
        }
        if (parkDuration != 0) {
            duration.append(parkDuration);
            duration.append("分");
        }
        mParkDuration.setText(duration.toString());

        mParkLotName.setText(mParkOrderInfo.getPark_space_name());
        mParkSpaceNumber.setText(mParkOrderInfo.getParkNumber());
        mAppointStartParkTime.setText(mParkOrderInfo.getOrder_starttime().substring(0, mParkOrderInfo.getOrder_starttime().lastIndexOf(":")));
        mAppointParkDuration.setText(duration.toString());

        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        SpannableString spannableString = new SpannableString("约" + decimalFormat.format(DateUtil.caculateParkFee(DateUtil.deleteSecond(mParkOrderInfo.getOrder_starttime()),
                DateUtil.deleteSecond(mParkOrderInfo.getOrder_endtime()), mParkOrderInfo.getHigh_time(), Double.valueOf(mParkOrderInfo.getHigh_fee()),
                Double.valueOf(mParkOrderInfo.getLow_fee()))) + "元");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#1dd0a1")), 1, spannableString.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mEstimatedCost.setText(spannableString);

        OnCtrlLockListener lockListener = new OnCtrlLockListener() {
            @Override
            public void onCtrlLock(String ctrlMessage) {
                try {
                    JSONObject jsonObject = new JSONObject(ctrlMessage);
                    if (jsonObject.optString("type").equals("ctrl")) {
                        if (jsonObject.optString("msg").equals("open_successful")) {
                            showFiveToast("成功开锁");
                        } else if (jsonObject.optString("msg").equals("open_successful_car")) {
                            showFiveToast("车锁已开，因为车位上方有车辆滞留");
                        } else if (jsonObject.optString("msg").equals("open_failed")) {
                            showFiveToast("开锁失败，请稍后重试");
                        } else if (jsonObject.optString("msg").equals("close_successful")) {
                            showFiveToast("成功关锁");
                        } else if (jsonObject.optString("msg").equals("close_failed")) {
                            //textview_state.setText("关锁失败！");
                        } else if (jsonObject.optString("msg").equals("close_failed_car")) {
                            //textview_state.setText("关锁失败，因为车位上方有车辆滞留");
                        }
                        mOpenLock.setClickable(true);
                    }
                } catch (Exception e) {
                    mOpenLock.setClickable(true);
                    showFiveToast("开锁失败，请稍后重试");
                }
            }
        };
        MyReceiver.setOnCtrlLockListener(lockListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MyReceiver.setOnCtrlLockListener(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appointment_calculate_rule:

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
                        .setMessage("确定取消该订单吗？")
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
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4006505058"));
                startActivity(intent);
                break;
            case R.id.view_appointment_detail_cl:
                if (mConstraintLayout.getVisibility() == View.VISIBLE) {
                    mConstraintLayout.setVisibility(View.GONE);
                } else {
                    mConstraintLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.open_lock:
                openParkLock();
                break;
        }
    }

    private void cancelAppointment() {
        showLoadingDialog("取消预约");
        getOkGo(HttpConstants.cancleAppointOrder)
                .params("order_id", mParkOrderInfo.getId())
                .params("citycode", mParkOrderInfo.getCitycode())
                .execute(new JsonCallback<Void>() {
                    @Override
                    public void onSuccess(Void o, Call call, Response response) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(ConstansUtil.CANCEL_ORDER, mParkOrderInfo);
                        intent.putExtra(ConstansUtil.FOR_REQUEST_RESULT, bundle);
                        dismmisLoadingDialog();
                        if (getActivity() != null) {
                            getActivity().setResult(Activity.RESULT_OK, intent);
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

}
