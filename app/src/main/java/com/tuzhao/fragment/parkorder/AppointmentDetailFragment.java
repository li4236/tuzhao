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
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
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
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.others.CircularArcView;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.MainTimeUtil;
import com.tuzhao.utils.TimeUtil;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;

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

    private TextView mOpenLock;

    private ArrayList<String> mParkSpacePictures;

    private boolean mIsTimeOut;

    private boolean mOpenLockSuccess;

    private boolean mIsOpening;

    private CustomDialog mLockDialog;

    private AnimatorSet mAnimatorSet;

    private AnimatorSet mResumeAnimatorSet;

    private ObjectAnimator mAlphaAnimator;

    private ValueAnimator mValueAnimator;

    private long mAnimatorDuration;

    private int mAnimatorRepeatCount;

    private MainTimeUtil mMainTimeUtil;

    private CircularArcView mCircularArcView;

    private ImageView mLockIv;

    private TextView mOpenLockTv;

    private TextView mRetryTv;

    private CustomDialog mCustomDialog;

    private TimeUtil mTimeUtil;

    private long mInterval = 60 * 1000;

    private Calendar mCurrentCalendar;

    private Calendar mOrderStartCalendar;

    private Handler mHandler;

    private static final int TIME_IN_MILLISS = 0x123;

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

        mParkDate = view.findViewById(R.id.appointment_park_date);
        mStartParkTime = view.findViewById(R.id.appointment_income_time);
        mParkSpaceLocation = view.findViewById(R.id.appointment_park_location);
        mParkDuration = view.findViewById(R.id.park_duration);
        mOpenLock = view.findViewById(R.id.open_lock);

        view.findViewById(R.id.appointment_calculate_rule).setOnClickListener(this);
        view.findViewById(R.id.appointment_calculate_rule_iv).setOnClickListener(this);
        view.findViewById(R.id.car_pic_cl).setOnClickListener(this);
        view.findViewById(R.id.cancel_appoint_cl).setOnClickListener(this);
        view.findViewById(R.id.contact_service_cl).setOnClickListener(this);
        view.findViewById(R.id.view_appointment_detail).setOnClickListener(this);
        view.findViewById(R.id.view_appointment_detail_iv).setOnClickListener(this);
        mOpenLock.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mParkDate.setText(DateUtil.getMonthToDay(mParkOrderInfo.getOrder_starttime()));
        mStartParkTime.setText(DateUtil.getHourToMinute(mParkOrderInfo.getOrder_starttime()));
        mParkSpaceLocation.setText(mParkOrderInfo.getAddress_memo());
        mParkDuration.setText(DateUtil.getDistanceForDayTimeMinute(mParkOrderInfo.getOrder_starttime(), mParkOrderInfo.getOrder_endtime()));

        mCurrentCalendar = Calendar.getInstance();
        mOrderStartCalendar = DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_starttime());
        initHandler();
        startPollingTime();

        OnLockListener lockListener = new OnLockListener() {
            @Override
            public void openSuccess() {
                mOpenLockSuccess = true;
                mIsOpening = false;
                cancelOpenLockAnimator();
                if (mLockDialog != null && !mLockDialog.isShowing()) {
                    finishAppointment(mParkOrderInfo);
                    showFiveToast("开锁成功");
                }
            }

            @Override
            public void openFailed() {
                mIsOpening = false;
                if (mLockDialog.isShowing()) {
                    mOpenLockTv.setVisibility(View.INVISIBLE);
                    mRetryTv.setVisibility(View.VISIBLE);
                    cancelOpenLockAnimator();
                } else {
                    showFiveToast("开锁失败，请稍后重试");
                }
            }

            @Override
            public void openSuccessHaveCar() {
                mOpenLockSuccess = true;
                mIsOpening = false;
                cancelOpenLockAnimator();
                if (mLockDialog != null && !mLockDialog.isShowing()) {
                    finishAppointment(mParkOrderInfo);
                    showFiveToast("开锁成功");
                }

                /*dismmisLoadingDialog();
                showFiveToast("车锁已开，因为车位上方有车辆滞留");
                mOpenLock.setText("已开锁");
                handleOpenLock();
                cancelOpenLockAnimator();
                mLockDialog.dismiss();*/
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

            @Override
            public void onError() {
                mIsOpening = false;
                if (mLockDialog.isShowing()) {
                    mOpenLockTv.setVisibility(View.INVISIBLE);
                    mRetryTv.setVisibility(View.VISIBLE);
                    cancelOpenLockAnimator();
                } else {
                    showFiveToast("开锁失败，请稍后重试");
                }
            }
        };
        MyReceiver.addLockListener(mParkOrderInfo.getLockId(), lockListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MyReceiver.removeLockListener(mParkOrderInfo.getLockId());
        if (mTimeUtil != null) {
            mTimeUtil.cancel();
        }
        mHandler.removeCallbacksAndMessages(null);
        cancelAllAnimator();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appointment_calculate_rule:
            case R.id.appointment_calculate_rule_iv:
                Bundle bundle = new Bundle();
                bundle.putString(ConstansUtil.PARK_LOT_ID, mParkOrderInfo.getBelong_park_space());
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
                if (!mIsTimeOut) {
                    showFiveToast("还没到开始停车时间哦");
                } else if (mIsOpening) {
                    showOpenLockDialog();
                } else {
                    openParkLock();
                }
                break;
        }
    }

    /**
     * 获取网络当前时间的时间戳
     */
    private void getCurrentTimeInMillis() {
        try {
            URL url = new URL("http://www.baidu.com");// 取得资源对象
            URLConnection uc = url.openConnection();// 生成连接对象
            uc.connect();// 发出连接
            Message message = mHandler.obtainMessage();
            message.what = TIME_IN_MILLISS;
            message.obj = uc.getDate();// 读取网站日期时间
            mHandler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initHandler() {
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TIME_IN_MILLISS) {
                    mCurrentCalendar.setTimeInMillis((long) msg.obj);
                    if (mOrderStartCalendar.compareTo(mCurrentCalendar) > 0) {
                        //未到预约开始停车时间
                        mOpenLock.setBackgroundResource(R.drawable.all_g1_5dp);
                        if (mInterval == 60 * 1000) {
                            caculateIntervalTime();
                            mTimeUtil.setInterval(mInterval);
                        }
                    } else {
                        mIsTimeOut = true;
                        mOpenLock.setBackgroundResource(R.drawable.little_yuan_yellow_5dp);
                        mTimeUtil.cancel();
                        mTimeUtil = null;
                    }
                }
                return true;
            }
        });
    }

    private void caculateIntervalTime() {
        long intervalTime = (mCurrentCalendar.getTimeInMillis() - mOrderStartCalendar.getTimeInMillis());
        if (intervalTime >= 60 * 1000) {
            mInterval = intervalTime / 2;
        } else {
            mInterval = Math.abs(intervalTime) + 1;     //防止等于0报错
        }
    }

    private void startPollingTime() {
        mTimeUtil = new TimeUtil(mInterval, new TimeUtil.TimeCallback() {
            @Override
            public void onTimeIn() {
                getCurrentTimeInMillis();
            }
        });
        mTimeUtil.start();
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
                        bundle.putSerializable(ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
                        intent.putExtra(ConstansUtil.FOR_REQUEST_RESULT, bundle);
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
                    openParkLock();
                    mRetryTv.setVisibility(View.INVISIBLE);
                    mOpenLockTv.setVisibility(View.VISIBLE);
                    mOpenLockTv.setText("正在开锁中.");
                    startOpenLockAnimator();
                }
            });
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
                    if (mOpenLockSuccess) {
                        mLockDialog.setCancelable(false);
                        resumeLockAnimator();
                    }
                }
            });
            mAnimatorSet.setDuration(1000);

            mMainTimeUtil = new MainTimeUtil(1000, new TimeUtil.TimeCallback() {
                @Override
                public void onTimeIn() {
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
        mMainTimeUtil.start();
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
                mOpenLockTv.setText("开锁成功");
                startLockCloseAnimator();
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
        mAlphaAnimator.setDuration(800);
        mAlphaAnimator.start();
    }

    /**
     * 在中间逐渐显示√
     */
    private void startOpenLockSuccessAnimatior() {
        mValueAnimator = ValueAnimator.ofInt(0, 100);
        mValueAnimator.setDuration(800);
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
        if (mMainTimeUtil != null) {
            mMainTimeUtil.cancel();
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

    private void openParkLock() {
        showLoadingDialog();
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
                        dismmisLoadingDialog();
                        mIsOpening = true;
                        if (mLockDialog == null || !mLockDialog.isShowing()) {
                            showOpenLockDialog();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mIsOpening = false;
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
        intent.putExtra(ConstansUtil.FOR_REQUEST_RESULT, bundle);
        IntentObserable.dispatch(intent);
    }

}
