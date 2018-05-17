package com.tuzhao.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.mine.AddParkActivity;
import com.tuzhao.activity.mine.ParkSpaceSettingActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.others.CircleView;
import com.tuzhao.publicwidget.others.CircularArcView;
import com.tuzhao.publicwidget.others.VoltageView;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.ImageUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/5/17.
 */

public class MyParkspaceFragment extends BaseStatusFragment implements View.OnClickListener {

    private Park_Info mParkInfo;

    private CircularArcView mCircularArcView;

    private ImageView mLock;

    private CircleView mCircleView;

    private TextView mParkspaceStatus;

    private VoltageView mVoltageView;

    private TextView mApponitmentTv;

    private AnimatorSet mAnimatorSet;

    private TextView mOpenLock;

    private int mRecentOrderMinutes;

    private String mParkLockStatus;

    public static Fragment newInstance(Park_Info mParkInfo) {
        MyParkspaceFragment fragment = new MyParkspaceFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstansUtil.PARK_SPACE_INFO, mParkInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int resourceId() {
        return R.layout.fragment_my_parkspace_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mParkInfo = (Park_Info) getArguments().getSerializable(ConstansUtil.PARK_SPACE_INFO);
        } else if (mParkInfo == null) {
            showFiveToast("打开失败，请稍后重试");
            if (getActivity() != null) {
                getActivity().finish();
            }
        }

        TextView parkspaceDescription = view.findViewById(R.id.parkspace_description);
        TextView parkLot = view.findViewById(R.id.parking_lot);
        TextView addNewParkspace = view.findViewById(R.id.add_new_parkspace);
        ImageView apponitmentIv = view.findViewById(R.id.appointment_iv);
        ImageView settingIv = view.findViewById(R.id.my_parkspace_setting_iv);

        mCircularArcView = view.findViewById(R.id.circle_arc);
        mLock = view.findViewById(R.id.lock);
        mCircleView = view.findViewById(R.id.parkspace_status_cv);
        mParkspaceStatus = view.findViewById(R.id.parkspace_status_tv);
        mVoltageView = view.findViewById(R.id.voltage_view);
        mOpenLock = view.findViewById(R.id.open_lock);
        mApponitmentTv = view.findViewById(R.id.appointment_tv);

        mOpenLock.setOnClickListener(this);
        addNewParkspace.setOnClickListener(this);
        view.findViewById(R.id.my_parkspace_setting).setOnClickListener(this);

        ImageUtil.showPic(mLock, R.drawable.lock);
        ImageUtil.showPic(apponitmentIv, R.drawable.ic_time2);
        ImageUtil.showPic(settingIv, R.drawable.ic_setting2);

        parkspaceDescription.setText(mParkInfo.getLocation_describe());
        parkLot.setText(mParkInfo.getParkspace_name());
        SpannableString spannableString = new SpannableString(getText(addNewParkspace));
        spannableString.setSpan(new UnderlineSpan(), 0, getText(addNewParkspace).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        addNewParkspace.setText(spannableString);
    }

    @Override
    protected void initData() {
        super.initData();
        getParkLockStatus();
        scrennOrderTime();
        setParkspaceStatus();
        mVoltageView.setVoltage((int) ((Double.valueOf(mParkInfo.getVoltage()) - 4.8) * 100 / 1.2));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_lock:
                if (getText(mOpenLock).equals("开锁")) {
                    if (getText(mParkspaceStatus).equals("已预约") && mRecentOrderMinutes <= 180) {
                        TipeDialog dialog = new TipeDialog.Builder(getContext())
                                .setTitle("开锁")
                                .setMessage(getText(mApponitmentTv) + ",确定开锁吗?")
                                .setNegativeButton("取消", null)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        controlParkLock(true);
                                    }
                                }).create();
                        dialog.show();
                    } else {
                        controlParkLock(true);
                    }
                } else {
                    controlParkLock(false);
                }
                break;
            case R.id.add_new_parkspace:
                startActivity(AddParkActivity.class);
                break;
            case R.id.my_parkspace_setting:
                startActivityForResult(ParkSpaceSettingActivity.class, ConstansUtil.REQUSET_CODE, ConstansUtil.PARK_SPACE_INFO, mParkInfo);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
        }
    }

    /**
     * 排除掉那些已过期的订单
     */
    private void scrennOrderTime() {
        if (!mParkInfo.getOrder_times().equals("-1") && !mParkInfo.getOrder_times().equals("")) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MILLISECOND, 0);

            Calendar orderCalendar;
            StringBuilder stringBuilder = new StringBuilder();
            for (String string : mParkInfo.getOrder_times().split(",")) {
                orderCalendar = DateUtil.getYearToMinuteCalendar(string.substring(string.indexOf("*") + 1, string.length()));
                if (orderCalendar.compareTo(calendar) >= 0) {
                    stringBuilder.append(string);
                    stringBuilder.append(",");
                }
            }

            if (stringBuilder.length() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            } else {
                stringBuilder.append("-1");
            }

            mParkInfo.setOrder_times(stringBuilder.toString());
        }
    }

    private void setParkspaceStatus() {
        if (mParkInfo.getPark_status().equals("1")) {
            mParkspaceStatus.setText("未开放");
            mCircleView.setPaintColor(Color.parseColor("#808080"));
            cantOpenLock();
        } else if (mParkInfo.getPark_status().equals("2")) {
            String status = getParkSpaceStatus();
            mParkspaceStatus.setText(status);
            switch (status) {
                case "租用中":
                    mCircleView.setPaintColor(Color.parseColor("#d01d2a"));
                    cantOpenLock();
                    break;
                case "停租中":
                    mCircleView.setPaintColor(Color.parseColor("#808080"));
                    break;
                case "空闲中":
                case "使用中":
                    mCircleView.setPaintColor(Color.parseColor("#1dd0a1"));
                    break;
                default:
                    mCircleView.setPaintColor(Color.parseColor("#6a6bd9"));
                    mParkspaceStatus.setText("已预约");
                    mRecentOrderMinutes = Integer.valueOf(status.substring(3, status.length()));
                    StringBuilder stringBuilder = new StringBuilder();
                    if (mRecentOrderMinutes >= 60) {
                        stringBuilder.append(mRecentOrderMinutes / 60);
                        stringBuilder.append("小时");
                    }
                    stringBuilder.append(mRecentOrderMinutes % 60);
                    stringBuilder.append("分钟后有预约");
                    mApponitmentTv.setText(stringBuilder.toString());
                    break;
            }
        } else if (mParkInfo.getPark_status().equals("3")) {
            mParkspaceStatus.setText("停租中");
            mCircleView.setPaintColor(Color.parseColor("#808080"));
        }
    }

    private String getParkSpaceStatus() {
        if (!mParkInfo.getParking_user_id().equals("-1")) {
            if (!mParkInfo.getParking_user_id().equals(UserManager.getInstance().getUserInfo().getId())) {
                return "租用中";
            } else {
                return "使用中";
            }
        }

        String nowDate = DateUtil.getCurrentYearToMinutes();
        String afterTwoMinutesDate = DateUtil.getCurrentYearToMinutes(System.currentTimeMillis() + 1000 * 60);

        if (DateUtil.isInShareDate(nowDate, afterTwoMinutesDate, mParkInfo.getOpen_date()) == 0) {
            return "停租中";
        }

        if (0 == DateUtil.isInPauseDate(nowDate, afterTwoMinutesDate, mParkInfo.getPauseShareDate())) {
            return "停租中";
        }

        if (0 == DateUtil.isInShareDay(nowDate, afterTwoMinutesDate, mParkInfo.getShareDay())) {
            return "停租中";
        }

        if (null == DateUtil.isInShareTime(nowDate, afterTwoMinutesDate, mParkInfo.getOpen_time(), false)) {
            return "停租中";
        }

        String rencentOrder = getRecentOrder();
        if (rencentOrder != null) {
            return rencentOrder;
        }

        return "空闲中";
    }

    private String getRecentOrder() {
        if (!mParkInfo.getOrder_times().equals("-1") && mParkInfo.getOrder_times().equals("")) {
            String[] orderDate = mParkInfo.getOrder_times().split(",");
            List<Calendar> list = new ArrayList<>(orderDate.length);
            for (String order : orderDate) {
                list.add(DateUtil.getYearToMinuteCalendar(order.split("\\*")[0]));
            }

            Collections.sort(list, new Comparator<Calendar>() {
                @Override
                public int compare(Calendar o1, Calendar o2) {
                    return o1.compareTo(o2);
                }
            });
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return String.valueOf(DateUtil.getCalendarDistance(calendar, list.get(0)));
        }
        return null;
    }

    private void getParkLockStatus() {
        OkGo.post(HttpConstants.getParkLockStatus)
                .tag(this.getClass().getName())
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("cityCode", mParkInfo.getCitycode())
                .params("parkspaceId", mParkInfo.getId())
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> stringBase_class_info, Call call, Response response) {
                        mParkLockStatus = stringBase_class_info.data;
                        if (!mParkLockStatus.equals("2")) {
                            cantOpenLock();
                        }
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {

                        }
                    }
                });
    }

    /**
     * @param isOpen true(开锁)    false(关锁)
     */
    private void controlParkLock(final boolean isOpen) {
        mOpenLock.setClickable(false);
        startAnimation();
        OkGo.post(HttpConstants.userControlParkLock)
                .tag(this.getClass().getName())
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("cityCode", mParkInfo.getCitycode())
                .params("parkspaceId", mParkInfo.getId())
                .params("controlType", isOpen ? "1" : "2")
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> voidBase_class_info, Call call, Response response) {
                        if (isOpen) {
                            mOpenLock.setText("关锁");
                        } else {
                            mOpenLock.setText("开锁");
                        }
                        cancleAnimation();
                        mOpenLock.setClickable(true);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        cancleAnimation();
                        mOpenLock.setClickable(true);
                    }
                });

    }

    private void startAnimation() {
        if (mAnimatorSet == null) {
            mAnimatorSet = new AnimatorSet();
            ObjectAnimator ratation = ObjectAnimator.ofFloat(mCircularArcView, "rotation", 0, 360);
            ratation.setRepeatCount(ValueAnimator.INFINITE);
            ratation.setRepeatMode(ValueAnimator.RESTART);

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(mCircularArcView, "scaleX", 1, 1.2f);
            scaleX.setRepeatMode(ValueAnimator.REVERSE);
            scaleX.setRepeatCount(ValueAnimator.INFINITE);

            ObjectAnimator scaleY = ObjectAnimator.ofFloat(mCircularArcView, "scaleY", 1, 1.2f);
            scaleY.setRepeatCount(ValueAnimator.INFINITE);
            scaleY.setRepeatMode(ValueAnimator.REVERSE);

            mAnimatorSet.playTogether(ratation, scaleX, scaleY);
            mAnimatorSet.setDuration(1000);
        }
        if (!mAnimatorSet.isRunning()) {
            mAnimatorSet.start();
        }
    }

    private void cancleAnimation() {
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
        }
    }

    private void cantOpenLock() {
        mOpenLock.setClickable(false);
        mOpenLock.setBackgroundResource(R.drawable.g14_all_18dp);
    }

}
