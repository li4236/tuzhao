package com.tuzhao.fragment.parkorder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.BigPictureActivity;
import com.tuzhao.activity.jiguang_notification.MyReceiver;
import com.tuzhao.activity.jiguang_notification.OnLockListener;
import com.tuzhao.activity.mine.BillingRuleActivity;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.JsonCodeCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.customView.CircularArcView;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DataUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.PollingUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/5/29.
 */

public class AppointmentDetailFragment extends BaseStatusFragment implements View.OnClickListener {

    private ParkOrderInfo mParkOrderInfo;

    private TextView mParkDate;

    private TextView mStartParkTime;

    private TextView mParkSpaceLocation;

    private TextView mParkDuration;

    private ArrayList<String> mParkSpacePictures;

    /**
     * -1（未开锁），0（开锁成功），1（开锁成功，车位上方有车停留）2（开锁失败）
     */
    private int mOpenLockStatus = -1;

    private boolean mIsOpening;

    private CustomDialog mLockDialog;

    private AnimatorSet mAnimatorSet;

    private AnimatorSet mResumeAnimatorSet;

    private ObjectAnimator mAlphaAnimator;

    private ValueAnimator mValueAnimator;

    private long mAnimatorDuration;

    private int mAnimatorRepeatCount;

    private PollingUtil mPollingUtil;

    private CircularArcView mCircularArcView;

    private ImageView mLockIv;

    private TextView mOpenLockTv;

    private TextView mRetryTv;

    private CustomDialog mCustomDialog;

    private List<Park_Info> mParkSpaceList;

    private List<Park_Info> mCanParkList;

    private int mExtensionTime;

    public static AppointmentDetailFragment newInstance(ParkOrderInfo parkOrderInfo) {
        AppointmentDetailFragment fragment = new AppointmentDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstansUtil.PARK_ORDER_INFO, parkOrderInfo);
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
            mParkOrderInfo = getArguments().getParcelable(ConstansUtil.PARK_ORDER_INFO);
        }

        mParkDate = view.findViewById(R.id.appointment_park_date);
        mStartParkTime = view.findViewById(R.id.appointment_income_time);
        mParkSpaceLocation = view.findViewById(R.id.appointment_park_location);
        mParkDuration = view.findViewById(R.id.park_duration);

        view.setOnClickListener(this);
        view.findViewById(R.id.appointment_calculate_rule).setOnClickListener(this);
        view.findViewById(R.id.appointment_calculate_rule_iv).setOnClickListener(this);
        view.findViewById(R.id.car_pic_cl).setOnClickListener(this);
        view.findViewById(R.id.cancel_appoint_cl).setOnClickListener(this);
        view.findViewById(R.id.contact_service_cl).setOnClickListener(this);
        view.findViewById(R.id.view_appointment_detail).setOnClickListener(this);
        view.findViewById(R.id.view_appointment_detail_iv).setOnClickListener(this);
        view.findViewById(R.id.open_lock).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mParkDate.setText(DateUtil.getMonthToDay(mParkOrderInfo.getOrder_starttime()));
        mStartParkTime.setText(DateUtil.getPointToMinute(mParkOrderInfo.getOrder_starttime()));
        mParkSpaceLocation.setText(mParkOrderInfo.getParkSpaceLocationDescribe());
        mParkDuration.setText(DateUtil.getDistanceForDayTimeMinute(mParkOrderInfo.getOrder_starttime(), mParkOrderInfo.getOrder_endtime()));

        OnLockListener lockListener = new OnLockListener() {
            @Override
            public void openSuccess() {
                mOpenLockStatus = 0;
                mIsOpening = false;
                cancelOpenLockAnimator();
                if (mLockDialog != null && !mLockDialog.isShowing()) {
                    finishAppointment(mParkOrderInfo);
                    showFiveToast("开锁成功");
                }
            }

            @Override
            public void openFailed() {
                mOpenLockStatus = 2;
                mIsOpening = false;
                if (mLockDialog.isShowing()) {
                    cancelOpenLockAnimator();
                } else {
                    showFiveToast("开锁失败，请稍后重试");
                }
            }

            @Override
            public void openSuccessHaveCar() {
                mOpenLockStatus = 1;
                mIsOpening = false;
                cancelOpenLockAnimator();
                mLockDialog.dismiss();
                TipeDialog tipeDialog = new TipeDialog.Builder(requireContext())
                        .setTitle("提示")
                        .setMessage("车锁已开，因为车位上方有车辆滞留，是否为您重新分配车位？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getParkSpaceList();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                tipeDialog.show();
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
        MyReceiver.addLockListener(mParkOrderInfo.getLockId(), lockListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MyReceiver.removeLockListener(mParkOrderInfo.getLockId());
        mOpenLockStatus = -1;
        cancelAllAnimator();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appointment_calculate_rule:
            case R.id.appointment_calculate_rule_iv:
                Bundle bundle = new Bundle();
                bundle.putString(ConstansUtil.PARK_LOT_ID, mParkOrderInfo.getParkLotId());
                bundle.putString(ConstansUtil.CITY_CODE, mParkOrderInfo.getCitycode());
                startActivity(BillingRuleActivity.class, bundle);
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
                        .setMessage("确定取消预约吗？")
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
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:4006505058"));
                startActivity(intent);
                break;
            case R.id.view_appointment_detail:
            case R.id.view_appointment_detail_iv:
                showAppointmentDetail();
                break;
            case R.id.open_lock:
                if (mIsOpening) {
                    showOpenLockDialog();
                } else {
                    requestOrderPark();
                }
                break;
        }
    }

    private void cancelAppointment() {
        showLoadingDialog("取消预约");
        getOkGo(HttpConstants.cancleAppointOrder)
                .params("order_id", mParkOrderInfo.getId())
                .params("citycode", mParkOrderInfo.getCitycode())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        Intent intent = new Intent();
                        intent.setAction(ConstansUtil.CANCEL_ORDER);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
                        intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, bundle);
                        IntentObserable.dispatch(intent);
                        dismmisLoadingDialog();
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                case "103":
                                case "104":
                                    showFiveToast("服务器异常，请稍后重试");
                                    break;
                                case "102":
                                    showFiveToast("取消订单失败，请稍后重试");
                                    break;
                            }
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

    private void showOpenLockDialog() {
        if (mLockDialog == null) {
            ConstraintLayout constraintLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.dialog_open_lock_layout, null);
            mLockDialog = new CustomDialog(requireContext(), constraintLayout);
            mCircularArcView = constraintLayout.findViewById(R.id.circle_arc);
            mLockIv = constraintLayout.findViewById(R.id.lock_iv);
            mOpenLockTv = constraintLayout.findViewById(R.id.open_lock_tv);
            mRetryTv = constraintLayout.findViewById(R.id.retry_tv);
            ImageUtil.showPic(mLockIv, R.drawable.lock);

            mRetryTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestOrderPark();
                    mRetryTv.setVisibility(View.INVISIBLE);
                    mOpenLockTv.setVisibility(View.VISIBLE);
                    mOpenLockTv.setText("正在开锁中.");
                    startOpenLockAnimator();
                }
            });
        }

        if (isVisible(mRetryTv)) {
            mRetryTv.setVisibility(View.INVISIBLE);
            mOpenLockTv.setVisibility(View.VISIBLE);
        }
        mLockDialog.show();
        startOpenLockAnimator();
    }

    /**
     * 打开dialog后开始放大旋转和缩小旋转动画
     */
    private void startOpenLockAnimator() {
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

            ratation.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationRepeat(Animator animation) {
                    super.onAnimationRepeat(animation);
                    //记录动画重复的次数，用于判断开锁成功后动画是正在放大还是缩小
                    mAnimatorRepeatCount++;

                    //记录动画时长，用与判断开锁成功后动画距离重复开始执行的时间差
                    mAnimatorDuration = System.currentTimeMillis();
                }

            });

            mAnimatorSet.playTogether(ratation, scaleX, scaleY);
            mAnimatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    if (mOpenLockStatus == 0 || mOpenLockStatus == 2) {
                        if (mOpenLockStatus == 0) {
                            mLockDialog.setCancelable(false);
                        }
                        resumeLockAnimator();
                    }
                }
            });
            mAnimatorSet.setDuration(1000);

            mPollingUtil = new PollingUtil(1000, new PollingUtil.OnTimeCallback() {
                @Override
                public void onTime() {
                    if (getText(mOpenLockTv).equals("正在开锁中...")) {
                        mOpenLockTv.setText("正在开锁中.");
                    } else if (getText(mOpenLockTv).equals("正在开锁中..")) {
                        mOpenLockTv.setText("正在开锁中...");
                    } else {
                        mOpenLockTv.setText("正在开锁中..");
                    }
                }
            });
        }

        mAnimatorSet.start();
        mPollingUtil.start();
    }

    private void resumeLockAnimator() {
        mResumeAnimatorSet = new AnimatorSet();
        mAnimatorDuration = System.currentTimeMillis() - mAnimatorDuration;
        if (mAnimatorRepeatCount % 2 == 0) {
            //开锁成功后动画正在放大

            //先把动画接着放大
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator ratation = ObjectAnimator.ofFloat(mCircularArcView, "rotation", (float) (360 * mAnimatorDuration / 1000.0), 360);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(mCircularArcView, "scaleX", 1.2f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(mCircularArcView, "scaleY", 1.2f);
            animatorSet.playTogether(ratation, scaleX, scaleY);
            animatorSet.setDuration(1000 - mAnimatorDuration);

            //然后缩小为原来大小
            AnimatorSet animator = new AnimatorSet();
            ObjectAnimator ratationResume = ObjectAnimator.ofFloat(mCircularArcView, "rotation", 0, 360);
            ObjectAnimator scaleXResume = ObjectAnimator.ofFloat(mCircularArcView, "scaleX", 1);
            ObjectAnimator scaleYResume = ObjectAnimator.ofFloat(mCircularArcView, "scaleY", 1);
            animator.playTogether(ratationResume, scaleXResume, scaleYResume);
            animator.setDuration(1000);
            mResumeAnimatorSet.playSequentially(animatorSet, animator);
        } else {
            //开锁成功后动画正在缩小

            //接着原来的动画缩小为原来大小
            ObjectAnimator ratation = ObjectAnimator.ofFloat(mCircularArcView, "rotation", (float) (360 * mAnimatorDuration / 1000.0), 360);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(mCircularArcView, "scaleX", 1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(mCircularArcView, "scaleY", 1);
            mResumeAnimatorSet.playTogether(ratation, scaleX, scaleY);
            mResumeAnimatorSet.setDuration(mAnimatorDuration);
        }

        mResumeAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mOpenLockStatus == 0) {
                    mOpenLockTv.setText("开锁成功");
                    startLockCloseAnimator();
                } else if (mOpenLockStatus == 2) {
                    mOpenLockTv.setVisibility(View.INVISIBLE);
                    mRetryTv.setVisibility(View.VISIBLE);
                }
            }
        });
        mResumeAnimatorSet.start();
    }

    /**
     * 缩小为原来大小后把中间的透明度逐渐变为0，同时把圆弧闭合
     */
    private void startLockCloseAnimator() {
        ImageUtil.showPic(mLockIv, R.drawable.ic_unlock);
        mAlphaAnimator = ObjectAnimator.ofInt(mLockIv, "alpha", 255, 0);
        mAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int alpha = (int) animation.getAnimatedValue();
                mCircularArcView.setCicleAlpha(alpha);
            }
        });
        mAlphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startOpenLockSuccessAnimatior();
            }
        });
        mAlphaAnimator.setDuration(1000);
        mAlphaAnimator.start();
    }

    /**
     * 在中间逐渐显示√
     */
    private void startOpenLockSuccessAnimatior() {
        mValueAnimator = ValueAnimator.ofInt(0, 120);
        mValueAnimator.setDuration(1200);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                mCircularArcView.setProgress(progress);
            }
        });

        mValueAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                finishAppointment(mParkOrderInfo);
                mLockDialog.dismiss();
            }
        });
        mValueAnimator.start();
    }

    private void cancelOpenLockAnimator() {
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
        }
        if (mPollingUtil != null) {
            mPollingUtil.cancel();
        }
    }

    private void cancelAllAnimator() {
        cancelOpenLockAnimator();

        if (mResumeAnimatorSet != null && mResumeAnimatorSet.isRunning()) {
            mResumeAnimatorSet.cancel();
        }

        if (mAlphaAnimator != null && mAlphaAnimator.isRunning()) {
            mAlphaAnimator.cancel();
        }

        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }
    }

    private void showAppointmentDetail() {
        if (mCustomDialog == null) {
            if (getContext() != null) {
                mCustomDialog = new CustomDialog(getContext(), mParkOrderInfo);
            }
        }
        mCustomDialog.show();
    }

    private void requestOrderPark() {
        showLoadingDialog();
        OkGo.post(HttpConstants.requestOrderPark)
                .tag(TAG)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("orderId", mParkOrderInfo.getId())
                .params("cityCode", mParkOrderInfo.getCitycode())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> aVoid, Call call, Response response) {
                        dismmisLoadingDialog();
                        showOpenLockDialog();
                        mIsOpening = true;
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mIsOpening = false;
                        mOpenLockStatus = 2;
                        cancelOpenLockAnimator();
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    showFiveToast("设备暂时离线，请稍后重试");
                                    break;
                                case "103":
                                    Calendar calendar = Calendar.getInstance();
                                    if (calendar.compareTo(DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_starttime())) < 0) {
                                        showFiveToast("该车位已有别人预约，请到您预约的时间再停车哦");
                                    } else {
                                        showFiveToast(ConstansUtil.SERVER_ERROR);
                                    }
                                case "102":
                                case "104":
                                case "105":
                                case "106":
                                    showFiveToast(ConstansUtil.SERVER_ERROR);
                                    break;
                                default:
                                    showFiveToast("开锁失败，请稍后重试");
                                    break;
                            }
                        }
                    }
                });
    }

    /**
     * 处理开锁时已有人停车的情况
     */
    private void handleOpenLock() {
        if (mParkOrderInfo.getParkingUserId().equals("-1")) {
            //正在停车用户id为-1，可能是后台帮忙开的，但是这里还没更新
            getParkInfo();
        } else if (mParkOrderInfo.getParkingUserId().equals(UserManager.getInstance().getUserInfo().getId())) {
            finishAppointment(mParkOrderInfo);
        } else {
            //有人延迟停车还没走
            // TODO: 2018/6/6
            getParkSpaceList();
        }
    }

    private void getParkInfo() {
        getOkGo(HttpConstants.getKindParkOrder)
                .params("order_status", "2")
                .params("pageSize", 3)
                .params("startItme", 0)
                .execute(new JsonCallback<Base_Class_List_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<ParkOrderInfo> o, Call call, Response response) {
                        //如果停车中有订单id和当前预约的一样，则把界面更换到停车中
                        for (ParkOrderInfo parkOrderInfo : o.data) {
                            if (parkOrderInfo.getId().equals(mParkOrderInfo.getId())) {
                                finishAppointment(parkOrderInfo);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {

                        }
                    }
                });
    }

    private void finishAppointment(ParkOrderInfo parkOrderInfo) {
        Intent intent = new Intent();
        intent.setAction(ConstansUtil.FINISH_APPOINTMENT);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstansUtil.PARK_ORDER_INFO, parkOrderInfo);
        intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, bundle);
        IntentObserable.dispatch(intent);
    }

    private void getParkSpaceList() {
        getOkGo(HttpConstants.getParkList)
                .params("citycode", mParkOrderInfo.getCitycode())
                .params("parkspace_id", mParkOrderInfo.getParkLotId())
                .execute(new JsonCallback<Base_Class_List_Info<Park_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<Park_Info> o, Call call, Response response) {
                        mParkSpaceList = o.data;
                        if (mCanParkList == null) {
                            mCanParkList = new LinkedList<>();
                        }
                        DataUtil.findCanParkList(mCanParkList, mParkSpaceList, DateUtil.getYearToMinute(mParkOrderInfo.getOrder_starttime(), 0),
                                DateUtil.deleteSecond(mParkOrderInfo.getOrder_endtime()));
                        if (mCanParkList.isEmpty()) {
                            showNoParkSpaceDialog();
                        } else {
                            if (mCanParkList.size() > 1) {
                                DataUtil.sortCanParkByIndicator(mCanParkList, mParkOrderInfo.getOrder_endtime());
                            }
                            redistributionOrderParkSpace();
                            //startRedistributionOrderParkSpace();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast("获取车位信息失败，请检查网络后返回重试");
                        }
                    }
                });
    }

    private void showNoParkSpaceDialog() {
        TipeDialog tipeDialog = new TipeDialog.Builder(requireContext())
                .setTitle("提示")
                .setMessage("很抱歉，暂无适合您的车位。\n是否需要取消该订单")
                .setNegativeButton("不取消", null)
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelAppointment();
                    }
                })
                .create();
        tipeDialog.show();
    }

    /**
     * 开始重新分配给该订单，如果车位的顺延时长不足则提示
     */
    private void startRedistributionOrderParkSpace() {
        Calendar canParkEndCalendar = DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_endtime());
        canParkEndCalendar.add(Calendar.MINUTE, UserManager.getInstance().getUserInfo().getLeave_time());
        Log.e("TAG", "startRedistributionOrderParkSpace shareTime: " + DateUtil.getTwoYearToMinutesString(
                mCanParkList.get(0).getShareTimeCalendar()[0], mCanParkList.get(0).getShareTimeCalendar()[1]));
        if (DateUtil.getCalendarDistance(canParkEndCalendar, mCanParkList.get(0).getShareTimeCalendar()[1]) >= 0) {
            redistributionOrderParkSpace();
        } else {
            final TipeDialog.Builder builder = new TipeDialog.Builder(requireContext());
            mExtensionTime = (int) DateUtil.getCalendarDistance(mCanParkList.get(0).getShareTimeCalendar()[1], canParkEndCalendar);
            builder.setMessage("可分配车位宽限时长为" + mExtensionTime + "分钟，是否预定？");
            builder.setTitle("确认预定");
            builder.setPositiveButton("立即预定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    showLoadingDialog("匹配中...");
                    redistributionOrderParkSpace();
                }
            });

            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

            builder.create().show();
        }

    }

    /**
     * 请求重新分配车位给该订单
     */
    private void redistributionOrderParkSpace() {
        final StringBuilder readyParkId = new StringBuilder();
        for (int i = 1, size = mCanParkList.size() > 3 ? 3 : mCanParkList.size(); i < size; i++) {
            readyParkId.append(mCanParkList.get(i).getId());
            readyParkId.append(",");
        }
        if (readyParkId.length() > 0) {
            readyParkId.deleteCharAt(readyParkId.length() - 1);
        }

        showLoadingDialog("重新分配...");
        getOkGo(HttpConstants.redistributionOrderParkSpace)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("orderId", mParkOrderInfo.getId())
                .params("parkSpaceId", mCanParkList.get(0).getId())
                .params("alternateParkSpaceId", readyParkId.toString().equals("") ? "-1" : readyParkId.toString())
                .params("cityCode", mParkOrderInfo.getCitycode())
                .execute(new JsonCodeCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> responseData, Call call, Response response) {
                        switch (responseData.code) {
                            case "0":
                                redistributionParkSpace(responseData.data);
                                break;
                            case "101":
                                mCanParkList.remove(0);
                                String[] readyPark = readyParkId.toString().split(",");
                                if (!readyPark[0].equals("")) {
                                    for (int i = 0; i < readyPark.length; i++) {
                                        mCanParkList.remove(0);
                                    }
                                }

                                if (mCanParkList.size() > 0) {
                                    if (mCanParkList.size() != 1) {
                                        DataUtil.sortCanParkByIndicator(mCanParkList, mParkOrderInfo.getOrder_endtime());
                                    }
                                    redistributionOrderParkSpace();
                                } else {
                                    showLoadingDialog();
                                    showFiveToast("未匹配到合适您时间的车位，请尝试更换时间");
                                }
                                break;
                            case "106":
                                mCanParkList.remove(0);
                                showRequestAppointOrderDialog(mCanParkList.get(0), Integer.valueOf(responseData.data.getExtensionTime()) / 60);
                                break;
                            case "102":
                                for (int i = 0; i < mCanParkList.size(); i++) {
                                    if (mCanParkList.get(i).getId().equals(responseData.data.getParkSpaceid())) {
                                        showRequestAppointOrderDialog(mCanParkList.get(i), Integer.valueOf(responseData.data.getExtensionTime()));
                                        break;
                                    }
                                }
                                break;
                            case "103":
                                showFiveToast("内部错误，请重新选择");
                                finish();
                                break;
                            case "104":
                                showFiveToast("您有效订单已达上限，暂不可预约车位哦");
                                break;
                            case "105":
                                showFiveToast("您当前车位在该时段内已有过预约，请尝试更换时间");
                                break;
                            case "107":
                                showFiveToast("您有订单需要前去付款，要先处理哦");
                            default:
                                showFiveToast("服务器正在维护中");
                                break;
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (!handleException(e)) {
                            showFiveToast(e.getMessage());
                        }
                    }
                });
    }

    /**
     * @param park_info     预约的车位
     * @param extensionTime 可停车的顺延时长（分钟）
     */
    private void showRequestAppointOrderDialog(final Park_Info park_info, int extensionTime) {
        TipeDialog.Builder builder = new TipeDialog.Builder(requireContext());
        mExtensionTime = extensionTime;
        builder.setMessage("可分配车位宽限时长为" + mExtensionTime + "分钟，是否预定？");
        builder.setTitle("确认预定");
        builder.setPositiveButton("立即预定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showLoadingDialog("提交中...");
                reserveLockedParkSpaceForOrder(park_info);
            }
        });

        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        builder.create().show();
    }

    /**
     * 预约被锁定的车位
     */
    private void reserveLockedParkSpaceForOrder(Park_Info park_info) {
        getOkGo(HttpConstants.reserveLockedParkSpaceForOrder)
                .params("orderId", mParkOrderInfo.getId())
                .params("parkSpaceId", park_info.getId())
                .params("cityCode", mParkOrderInfo.getCitycode())
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> responseData, Call call, Response response) {
                        redistributionParkSpace(responseData.data);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        showLoadingDialog();
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "102":
                                    showFiveToast("请求已超时，请重新预定");
                                    break;
                                case "901":
                                    showFiveToast("服务器正在维护中");
                                    break;
                            }
                        }
                    }
                });
    }

    /**
     * 重新分配车位给该订单
     */
    private void redistributionParkSpace(ParkOrderInfo parkOrderInfo) {
        mParkOrderInfo.setPark_id(parkOrderInfo.getParkSpaceid());
        mParkOrderInfo.setExtensionTime(parkOrderInfo.getExtensionTime());
        for (int i = 0; i < mCanParkList.size(); i++) {
            if (mCanParkList.get(i).getId().equals(parkOrderInfo.getParkSpaceid())) {
                mCanParkList.remove(i);
                break;
            }
        }

        Intent intent = new Intent(ConstansUtil.CHANGE_PARK_ORDER_INRO);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
        intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, bundle);
        IntentObserable.dispatch(intent);
    }

    /*private void requestOrderData(String orderNumber) {
        OkGo.post(HttpConstants.getDetailOfParkOrder)
                .tag(TAG)
                .headers("token", UserManager.getInstance().getToken())
                .params("citycode", mParkOrderInfo.getCitycode())
                .params("order_number", orderNumber)
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> parkOrderInfoBase_class_info, Call call, Response response) {
                        showFiveToast("预约成功");
                        Intent intent = new Intent(getActivity(), OrderActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(ConstansUtil.PARK_ORDER_INFO, parkOrderInfoBase_class_info.data);
                        intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, bundle);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast(e.getMessage());
                        }
                    }
                });
    }*/

}
