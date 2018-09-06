package com.tuzhao.fragment.parkspace;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.jiguang_notification.MyReceiver;
import com.tuzhao.activity.jiguang_notification.OnLockListener;
import com.tuzhao.activity.mine.AppointmentParkSpaceActivity;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.customView.CircleView;
import com.tuzhao.publicwidget.customView.CircularArcView;
import com.tuzhao.publicwidget.customView.VoltageView;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.ImageUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/9/5.
 */
public class ShareParkSpaceFragment extends BaseStatusFragment implements View.OnClickListener {

    private Park_Info mParkInfo;

    private TextView mNumberOfParkSpace;

    private CircularArcView mCircularArcView;

    private ImageView mLock;

    private CircleView mCircleView;

    private TextView mParkspaceStatus;

    private VoltageView mVoltageView;

    private TextView mOpenLock;

    private int mRecentOrderMinutes;

    private StringBuilder mAppointmentTime = new StringBuilder("暂无预约");

    private int mTotalSize;

    private AnimatorSet mAnimatorSet;

    private AnimatorSet mResumeAnimatorSet;

    private long mAnimatorDuration;

    private Queue<Float> mScaleValues;

    private AccelerateDecelerateInterpolator mInterpolator;

    private int mLockStatus;

    private String mExceptionMessage;

    public static ShareParkSpaceFragment newInstance(Park_Info parkInfo, int totalSize) {
        ShareParkSpaceFragment fragment = new ShareParkSpaceFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstansUtil.PARK_SPACE_INFO, parkInfo);
        bundle.putInt(ConstansUtil.SIZE, totalSize);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setParkSpaceNote(String note) {
        mParkInfo.setParkSpaceNote(note);
        mNumberOfParkSpace.setText(note);
    }

    @Override
    protected int resourceId() {
        return R.layout.fragment_my_parkspace_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mParkInfo = getArguments().getParcelable(ConstansUtil.PARK_SPACE_INFO);
            mTotalSize = getArguments().getInt(ConstansUtil.SIZE);
        } else if (mParkInfo == null) {
            showFiveToast("打开失败，请稍后重试");
            finish();
        }

        mNumberOfParkSpace = view.findViewById(R.id.number_of_park_space);
        TextView parkspaceDescription = view.findViewById(R.id.parkspace_description);
        TextView parkLot = view.findViewById(R.id.parking_lot);

        mCircularArcView = view.findViewById(R.id.circle_arc);
        mLock = view.findViewById(R.id.lock);
        mCircleView = view.findViewById(R.id.parkspace_status_cv);
        mParkspaceStatus = view.findViewById(R.id.parkspace_status_tv);
        mVoltageView = view.findViewById(R.id.voltage_view);
        mOpenLock = view.findViewById(R.id.open_lock);

        if (mTotalSize == 1) {
            view.findViewById(R.id.left_park_space_iv).setVisibility(View.GONE);
            view.findViewById(R.id.right_park_space_iv).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.right_park_space_iv).setOnClickListener(this);
            view.findViewById(R.id.left_park_space_iv).setOnClickListener(this);
            parkspaceDescription.setOnClickListener(this);
        }

        mOpenLock.setOnClickListener(this);

        ImageUtil.showPic(mLock, R.drawable.lock);
        mNumberOfParkSpace.setText(mParkInfo.getParkSpaceNote());
        parkspaceDescription.setText(mParkInfo.getLocation_describe());
        parkLot.setText(mParkInfo.getParkspace_name());
    }

    @Override
    protected void initData() {
        setTAG(this.getClass().getName() + " parkInfoId:" + mParkInfo.getId() + " cityCode:" + mParkInfo.getCityCode());
        setAutoCancelRequest(false);
        setParkspaceStatus();
        mVoltageView.setVoltage((int) ((Double.valueOf(mParkInfo.getVoltage()) - 4.8) * 100 / 1.2));

        OnLockListener lockListener = new OnLockListener() {
            @Override
            public void openSuccess() {
                mLockStatus = 1;
                cancleAnimation();
            }

            @Override
            public void openFailed() {
                mLockStatus = 2;
                cancleAnimation();
            }

            @Override
            public void openSuccessHaveCar() {
                mLockStatus = 3;
                cancleAnimation();
            }

            @Override
            public void closeSuccess() {
                mLockStatus = 4;
                cancleAnimation();
            }

            @Override
            public void closeFailed() {
                mLockStatus = 5;
                cancleAnimation();
            }

            @Override
            public void closeFailedHaveCar() {
                mLockStatus = 6;
                cancleAnimation();
            }

        };

        MyReceiver.addLockListener(mParkInfo.getParkLockId(), lockListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_lock:
                if (getText(mParkspaceStatus).equals("未开放")) {
                    showFiveToast("该车位未开放");
                } else if (getText(mParkspaceStatus).equals("离线中")) {
                    showFiveToast("车锁离线中");
                } else if (getText(mOpenLock).equals("开锁")) {
                    if (getText(mParkspaceStatus).equals("已预约") && mRecentOrderMinutes <= 180) {
                        TipeDialog dialog = new TipeDialog.Builder(getContext())
                                .setTitle("开锁")
                                .setMessage(mAppointmentTime.toString() + ",确定开锁吗?")
                                .setNegativeButton("取消", null)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        controlParkLock(true);
                                    }
                                }).create();
                        dialog.show();
                    } else {
                        new TipeDialog.Builder(requireContext())
                                .setTitle("提示")
                                .setMessage("确定开锁吗？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        controlParkLock(true);
                                    }
                                })
                                .create()
                                .show();
                    }
                } else if (getText(mOpenLock).equals("关锁")) {
                    new TipeDialog.Builder(requireContext())
                            .setTitle("提示")
                            .setMessage("确定关锁吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    controlParkLock(false);
                                }
                            })
                            .create()
                            .show();
                } else if (getText(mOpenLock).equals("预定")) {
                    startActivity(AppointmentParkSpaceActivity.class, ConstansUtil.PARK_SPACE_INFO, mParkInfo);
                }
                break;
            case R.id.parkspace_description:
                dispatchIntent(ConstansUtil.SHOW_DIALOG);
                break;
            case R.id.left_park_space_iv:
                dispatchIntent(ConstansUtil.LEFT);
                break;
            case R.id.right_park_space_iv:
                dispatchIntent(ConstansUtil.RIGHT);
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        OkGo.getInstance().cancelTag(TAG);
        MyReceiver.removeLockListener(mParkInfo.getParkLockId());
        cancleAnimation();
        if (mResumeAnimatorSet != null) {
            mResumeAnimatorSet.cancel();
        }
    }

    private void setParkspaceStatus() {
        switch (mParkInfo.getPark_status()) {
            case "1":
                mParkspaceStatus.setText("未开放");
                mCircleView.setColor(Color.parseColor("#808080"));
                break;
            case "2":
                String status = getParkSpaceStatus();
                mParkspaceStatus.setText(status);
                switch (status) {
                    case "租用中":
                        mCircleView.setColor(Color.parseColor("#d01d2a"));
                        break;
                    case "停租中":
                        mCircleView.setColor(Color.parseColor("#808080"));
                        break;
                    case "空闲中":
                    case "使用中":
                        mCircleView.setColor(Color.parseColor("#1dd0a1"));
                        break;
                    default:
                        mCircleView.setColor(Color.parseColor("#6a6bd9"));
                        mParkspaceStatus.setText("已预约");
                        mRecentOrderMinutes = Integer.valueOf(status.substring(3, status.length()));
                        mAppointmentTime.delete(0, mAppointmentTime.length());
                        if (mRecentOrderMinutes >= 60) {
                            mAppointmentTime.append(mRecentOrderMinutes / 60);
                            mAppointmentTime.append("小时");
                        }
                        mAppointmentTime.append(mRecentOrderMinutes % 60);
                        mAppointmentTime.append("分钟后有预约");
                        break;
                }
                break;
            case "3":
                mParkspaceStatus.setText("停租中");
                mCircleView.setColor(Color.parseColor("#808080"));
                break;
        }

        switch (mParkInfo.getParkLockStatus()) {
            case "1":
                initCloseLock();
                break;
            case "2":
                initOpenLock();
                break;
            case "3":
                mCircleView.setColor(Color.parseColor("#808080"));
                mParkspaceStatus.setText("离线中");
                break;
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

    /*private void getParkLockStatus() {
        OkGo.post(HttpConstants.getParkLockStatus)
                .tag(this.getClass().getName())
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("cityCode", mParkInfo.getCityCode())
                .params("parkSpaceId", mParkInfo.getId())
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> stringBase_class_info, Call call, Response response) {
                        mParkLockStatus = stringBase_class_info.data;
                        switch (mParkLockStatus) {
                            case "1":
                                initCloseLock();
                                break;
                            case "2":
                                initOpenLock();
                                break;
                            case "3":
                                cantOpenLock();
                                mCircleView.setColor(Color.parseColor("#808080"));
                                mParkspaceStatus.setText("离线中");
                                break;
                        }
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                case "102":
                                    showFiveToast("客户端异常，请稍后重试");
                                    break;
                                case "103":
                                    showFiveToast("账户异常，请重新登录");
                                    if (getActivity() != null) {
                                        getActivity().finish();
                                    }
                                    break;
                            }
                        }
                    }
                });
    }*/

    /**
     * @param isOpen true(开锁)    false(关锁)
     */
    private void controlParkLock(final boolean isOpen) {
        mOpenLock.setClickable(false);
        startAnimation();
        getOkGo(HttpConstants.userControlParkLock)
                .params("cityCode", mParkInfo.getCityCode())
                .params("parkSpaceId", mParkInfo.getId())
                .params("controlType", isOpen ? "1" : "2")
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> voidBase_class_info, Call call, Response response) {

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mLockStatus = 7;
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    mExceptionMessage = "设备不在线";
                                    break;
                                case "102":
                                    mExceptionMessage = "客户端异常，请稍后重试";
                                    break;
                                case "105":
                                    mExceptionMessage = "账号异常，请重新登录";
                                    break;
                            }
                        }
                        cancleAnimation();
                    }
                });

    }

    /**
     * 初始化为未关锁状态
     */
    private void initCloseLock() {
        mOpenLock.setText("关锁");
        ImageUtil.showPic(mLock, R.drawable.ic_unlock);
        mOpenLock.setClickable(true);
    }

    /**
     * 初始化为未开锁状态
     */
    private void initOpenLock() {
        mOpenLock.setText("开锁");
        ImageUtil.showPic(mLock, R.drawable.lock);
        mOpenLock.setClickable(true);
    }

    private void startAnimation() {
        if (mAnimatorSet == null) {
            mAnimatorSet = new AnimatorSet();
            ObjectAnimator ratation = ObjectAnimator.ofFloat(mCircularArcView, "rotation", 0, 360);
            ratation.setRepeatCount(ValueAnimator.INFINITE);
            ratation.setRepeatMode(ValueAnimator.RESTART);
            mInterpolator = (AccelerateDecelerateInterpolator) ratation.getInterpolator();

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(mCircularArcView, "scaleX", 1, 1.2f);
            scaleX.setRepeatMode(ValueAnimator.REVERSE);
            scaleX.setRepeatCount(ValueAnimator.INFINITE);

            ObjectAnimator scaleY = ObjectAnimator.ofFloat(mCircularArcView, "scaleY", 1, 1.2f);
            scaleY.setRepeatCount(ValueAnimator.INFINITE);
            scaleY.setRepeatMode(ValueAnimator.REVERSE);

            mAnimatorSet.playTogether(ratation, scaleX, scaleY);
            mAnimatorSet.setDuration(1000);

            scaleX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (mScaleValues.size() >= 2) {
                        //只保存两次的缩放值，用于之后判断当前动画是正在缩小还是放大
                        mScaleValues.poll();
                    }
                    mScaleValues.add((float) animation.getAnimatedValue());
                }
            });

            ratation.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationRepeat(Animator animation) {
                    super.onAnimationRepeat(animation);
                    //记录动画时长，用与判断开锁成功后动画距离重复开始执行的时间差
                    mAnimatorDuration = System.currentTimeMillis();
                }

            });

            mAnimatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    resumeLockAnimator();
                }
            });
        }

        if (mScaleValues == null) {
            mScaleValues = new LinkedList<>();
        }
        if (!mAnimatorSet.isRunning()) {
            mAnimatorSet.start();
        }

    }

    /**
     * 过渡到view的初始状态
     */
    private void resumeLockAnimator() {
        mAnimatorDuration = System.currentTimeMillis() - mAnimatorDuration;

        //计算出当前的旋转角度
        float currentRotation = 360 * mInterpolator.getInterpolation(mAnimatorDuration / 1000f) + 4;
        float firstScale = mScaleValues.poll();
        float secondScale = mScaleValues.poll();

        //因为动画不是每一秒都回调onAnimationUpdate方法的，所以会有点误差，当计算到动画正在放大并且快结束的时候很可能是已经开始缩小了的，所以要进行缩小动画
        boolean isSkip = secondScale > firstScale && currentRotation > 358;

        mResumeAnimatorSet = new AnimatorSet();
        if (secondScale > firstScale && currentRotation <= 358) {
            //开锁成功后动画正在放大,先把动画接着放大
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator ratation = ObjectAnimator.ofFloat(mCircularArcView, "rotation", currentRotation, 360);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(mCircularArcView, "scaleX", 1.2f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(mCircularArcView, "scaleY", 1.2f);
            animatorSet.playTogether(ratation, scaleX, scaleY);
            animatorSet.setDuration(1000 - mAnimatorDuration);

            //然后缩小为原来大小
            AnimatorSet animator = new AnimatorSet();
            ObjectAnimator ratationResume = ObjectAnimator.ofFloat(mCircularArcView, "rotation", 0, 360);
            ObjectAnimator scaleXResume = ObjectAnimator.ofFloat(mCircularArcView, "scaleX", 1.2f, 1);
            ObjectAnimator scaleYResume = ObjectAnimator.ofFloat(mCircularArcView, "scaleY", 1.2f, 1);
            animator.playTogether(ratationResume, scaleXResume, scaleYResume);
            animator.setDuration(1000);
            mResumeAnimatorSet.playSequentially(animatorSet, animator);
        } else {
            if (isSkip) {
                mAnimatorDuration = 0;
                currentRotation = 0;
            }
            //开锁成功后动画正在缩小,接着原来的动画缩小为原来大小
            ObjectAnimator ratation = ObjectAnimator.ofFloat(mCircularArcView, "rotation", currentRotation, 360);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(mCircularArcView, "scaleX", 1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(mCircularArcView, "scaleY", 1);
            mResumeAnimatorSet.playTogether(ratation, scaleX, scaleY);
            mResumeAnimatorSet.setDuration(1000 - mAnimatorDuration);
        }
        mResumeAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                showAnimationResult();
            }
        });
        mResumeAnimatorSet.start();
    }

    private void cancleAnimation() {
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
        }
    }

    private void showAnimationResult() {
        switch (mLockStatus) {
            case 1:
                initCloseLock();
                mParkInfo.setParkLockStatus("1");
                mCircleView.setColor(Color.parseColor("#1dd0a1"));
                mParkspaceStatus.setText("使用中");
                break;
            case 2:
                initOpenLock();
                showFiveToast("开锁失败，请稍后重试");
                break;
            case 3:
                initCloseLock();
                showFiveToast("车锁已开，因为车位上方有车辆滞留");
                break;
            case 4:
                initOpenLock();
                showFiveToast("成功关锁！");
                mParkInfo.setParkLockStatus("2");
                mCircleView.setColor(Color.parseColor("#1dd0a1"));
                mParkspaceStatus.setText("空闲中");
                break;
            case 5:
                initCloseLock();
                showFiveToast("关锁失败，请稍后重试");
                break;
            case 6:
                initCloseLock();
                showFiveToast("关锁失败，因为车位上方有车辆滞留");
                break;
            case 7:
                mOpenLock.setClickable(true);
                if (mExceptionMessage != null) {
                    showFiveToast(mExceptionMessage);
                    mExceptionMessage = null;
                }
                break;
        }
    }

}
