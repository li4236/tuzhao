package com.tuzhao.publicwidget.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.publicwidget.customView.CircularArcView;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.PollingUtil;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by juncoder on 2018/9/21.
 */
public class OpenLockDialog extends Dialog {

    private ImageView mCrossView;

    private CircularArcView mCircularArcView;

    private ImageView mLockIv;

    private TextView mOpenLockTv;

    private TextView mRetryTv;

    private PollingUtil mPollingUtil;

    private AnimatorSet mAnimatorSet;

    private AnimatorSet mResumeAnimatorSet;

    private ObjectAnimator mAlphaAnimator;

    private ValueAnimator mValueAnimator;

    private long mAnimatorDuration;

    private Queue<Float> mScaleValues;

    private AccelerateDecelerateInterpolator mInterpolator;

    /**
     * -1（未开锁），0（开锁成功），1（开锁成功，车位上方有车停留）2（开锁失败）
     */
    private int mOpenLockStatus = -1;

    private String mLockMessage = "开锁";

    private SuccessCallback<Boolean> mSuccessCallback;

    public OpenLockDialog(@NonNull Context context, SuccessCallback<Boolean> callback) {
        super(context, R.style.ParkDialog);
        ConstraintLayout constraintLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.dialog_open_lock_layout, null);
        setContentView(constraintLayout);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initView(constraintLayout);
        mSuccessCallback = callback;
    }

    private void initView(View view) {
        mCrossView = view.findViewById(R.id.close_dialog);
        mCircularArcView = view.findViewById(R.id.circle_arc);
        mLockIv = view.findViewById(R.id.lock_iv);
        mOpenLockTv = view.findViewById(R.id.open_lock_tv);
        mRetryTv = view.findViewById(R.id.retry_tv);
        ImageUtil.showPic(mLockIv, R.drawable.lock);

        mCrossView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        mRetryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSuccessCallback.onSuccess(false);
                mCrossView.setVisibility(View.INVISIBLE);
                mRetryTv.setVisibility(View.INVISIBLE);
                mOpenLockTv.setVisibility(View.VISIBLE);
                mOpenLockTv.setText("正在" + mLockMessage + "中.");
                startOpenLockAnimator();
            }
        });

    }

    /**
     * 开始开锁
     */
    public void startOpenLock() {
        if (mRetryTv.getVisibility() == View.VISIBLE) {
            mCrossView.setVisibility(View.INVISIBLE);
            mRetryTv.setVisibility(View.INVISIBLE);
            mOpenLockTv.setVisibility(View.VISIBLE);
        }
        show();
        startOpenLockAnimator();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        cancelOpenLockAndPolling();
        IntentObserable.dispatch(ConstansUtil.DIALOG_DISMISS);
    }

    @Override
    public void cancel() {
        super.cancel();
        cancelAllAnimator();
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
            mInterpolator = (AccelerateDecelerateInterpolator) ratation.getInterpolator();

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(mCircularArcView, "scaleX", 1, 1.2f);
            scaleX.setRepeatMode(ValueAnimator.REVERSE);
            scaleX.setRepeatCount(ValueAnimator.INFINITE);

            ObjectAnimator scaleY = ObjectAnimator.ofFloat(mCircularArcView, "scaleY", 1, 1.2f);
            scaleY.setRepeatCount(ValueAnimator.INFINITE);
            scaleY.setRepeatMode(ValueAnimator.REVERSE);

            ratation.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    //记录动画时长，用与判断开锁成功后动画距离重复开始执行的时间差
                    mAnimatorDuration = System.currentTimeMillis();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    super.onAnimationRepeat(animation);
                    //记录动画时长，用与判断开锁成功后动画距离重复开始执行的时间差
                    mAnimatorDuration = System.currentTimeMillis();
                }

            });

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

            mAnimatorSet.playTogether(ratation, scaleX, scaleY);
            mAnimatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    if (mOpenLockStatus != -1) {
                        resumeLockAnimator();
                    }
                }
            });
            mAnimatorSet.setDuration(1000);

            mPollingUtil = new PollingUtil(1000, new PollingUtil.OnTimeCallback() {
                @Override
                public void onTime() {
                    switch (mOpenLockTv.getText().toString()) {
                        case "正在开锁中...":
                            mOpenLockTv.setText("正在开锁中.");
                            break;
                        case "正在开锁中..":
                            mOpenLockTv.setText("正在开锁中...");
                            break;
                        case "正在开锁中.":
                            mOpenLockTv.setText("正在开锁中..");
                            break;
                        case "正在关锁中...":
                            mOpenLockTv.setText("正在关锁中.");
                            break;
                        case "正在关锁中..":
                            mOpenLockTv.setText("正在关锁中...");
                            break;
                        case "正在关锁中.":
                            mOpenLockTv.setText("正在关锁中..");
                            break;
                    }
                }
            });
        }
        if (mScaleValues == null) {
            mScaleValues = new LinkedList<>();
        }

        mAnimatorSet.start();
        mPollingUtil.start();
    }

    private void resumeLockAnimator() {
        mResumeAnimatorSet = new AnimatorSet();
        mAnimatorDuration = System.currentTimeMillis() - mAnimatorDuration;

        //计算出当前的旋转角度
        float currentRotation = 360 * mInterpolator.getInterpolation(mAnimatorDuration / 1000f) + 4;
        float firstScale = mScaleValues.poll();
        float secondScale = mScaleValues.poll();

        //因为动画不是每一秒都回调onAnimationUpdate方法的，所以会有点误差，当计算到动画正在放大并且快结束的时候很可能是已经开始缩小了的，所以要进行缩小动画
        boolean isSkip = secondScale > firstScale && currentRotation > 358;

        if (secondScale > firstScale && currentRotation <= 358) {
            //开锁成功后动画正在放大

            //先把动画接着放大
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

            //开锁成功后动画正在缩小

            //接着原来的动画缩小为原来大小
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
                if (mOpenLockStatus == 0 || mOpenLockStatus == 1) {
                    mOpenLockTv.setText(mLockMessage + "成功");
                    startLockCloseAnimator();
                } else if (mOpenLockStatus == 2) {
                    mOpenLockTv.setVisibility(View.INVISIBLE);
                    mCrossView.setVisibility(View.VISIBLE);
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
        ImageUtil.showPicWithNoAnimate(mLockIv, R.drawable.ic_unlock);
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
        mValueAnimator = ValueAnimator.ofInt(0, 100);
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
                mSuccessCallback.onSuccess(true);
                dismiss();
            }
        });
        mValueAnimator.start();
    }

    public void cancelOpenLockAnimator() {
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
        }
    }

    public void cancelOpenLockAndPolling() {
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

    public void setOpenLockStatus(int openLockStatus) {
        mOpenLockStatus = openLockStatus;
        if (mOpenLockStatus == 2) {
            mAnimatorSet.cancel();
        }
    }

}
