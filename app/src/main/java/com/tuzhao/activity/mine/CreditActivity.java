package com.tuzhao.activity.mine;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.customView.CreditView;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.ImageUtil;

/**
 * Created by juncoder on 2018/6/26.
 * <p>
 * 途找信用
 * </p>
 */
public class CreditActivity extends BaseStatusActivity implements View.OnClickListener {

    private TextView mCurrentCredit;

    private TextView mCreditStage;

    private TextView mParkGracePeriod;

    private TextView mCreditSituation;

    private ImageView mNoDepositIv;

    private ImageView mLeaveGraceIv;

    private ImageView mAdvanceEndOrderIv;

    private TextView mFirstRule;

    private TextView mSecondRule;

    private TextView mThirdRule;

    private TextView mFourthRule;

    private CreditView mCreditView;

    private AnimatorSet mAnimatorSet;

    private int mCredit;

    @Override
    protected int resourceId() {
        return R.layout.activity_personal_credit_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mCurrentCredit = findViewById(R.id.current_credit);
        mCreditStage = findViewById(R.id.credit_stage_tv);
        mParkGracePeriod = findViewById(R.id.park_grace_period);
        mCreditSituation = findViewById(R.id.credit_situation);
        mCreditView = findViewById(R.id.credit_view);
        mNoDepositIv = findViewById(R.id.no_deposit_iv);
        mLeaveGraceIv = findViewById(R.id.leave_grace_iv);
        mAdvanceEndOrderIv = findViewById(R.id.advance_end_order_iv);
        mFirstRule = findViewById(R.id.first_rule_tv);
        mSecondRule = findViewById(R.id.second_rule_tv);
        mThirdRule = findViewById(R.id.third_rule_tv);
        mFourthRule = findViewById(R.id.fourth_rule_tv);

        findViewById(R.id.credit_record_cl).setOnClickListener(this);
        findViewById(R.id.no_deposit_cl).setOnClickListener(this);
        findViewById(R.id.leave_grace_cl).setOnClickListener(this);
        findViewById(R.id.advance_end_order_cl).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mCurrentCredit.setText(com.tuzhao.publicmanager.UserManager.getInstance().getUserInfo().getCredit());
        mCredit = Integer.valueOf(UserManager.getInstance().getUserInfo().getCredit());
        if (mCredit <= ConstansUtil.POOR_CREDIT_SCORE) {
            mCreditStage.setText("较差");
            mCreditStage.setBackgroundResource(R.drawable.r7_all_3dp);
            ImageUtil.showPic(mNoDepositIv, R.drawable.ic_nodeposit);
        } else if (mCredit <= ConstansUtil.FINE_CREDIT_SCORE) {
            mCreditStage.setText("一般");
            mCreditStage.setBackgroundResource(R.drawable.y2_all_3dp);
            ImageUtil.showPic(mNoDepositIv, R.drawable.ic_nodeposit);
        } else if (mCredit <= ConstansUtil.GOOD_CREDIT_SCORE) {
            mCreditStage.setText("良好");
            mCreditStage.setBackgroundResource(R.drawable.y3_all_3dp);
        } else if (mCredit <= ConstansUtil.VERY_GOOD_CREDIT_SCORE) {
            mCreditStage.setText("优秀");
            mCreditStage.setBackgroundResource(R.drawable.blue5_all_3dp);
        } else {
            mCreditStage.setText("极好");
            mCreditStage.setBackgroundResource(R.drawable.green11_all_3dp);
        }

        String parkGracePeriod = String.valueOf(UserManager.getInstance().getUserInfo().getLeave_time());
        if (parkGracePeriod.equals("0")) {
            ImageUtil.showPic(mLeaveGraceIv, R.drawable.ic_noleave);
        }
        parkGracePeriod = parkGracePeriod + "分钟";
        mParkGracePeriod.setText(parkGracePeriod);

        SpannableString firstRule = new SpannableString("规则途找信用分是根据用户的每一次停车，每一次履约情况来进行评定的，" +
                "每个月均有波动。分值视历史履约情况而改变。");
        hideTwoWord(firstRule);
        mFirstRule.setText(firstRule);

        SpannableString secondRule = new SpannableString("规则“免保证金”：途找信用等级为良好及以上，用户下单预约车位免担保金，" +
                "保证金会在每次订单结算后退还到个人途找余额账户，余额可随时提现。");
        hideTwoWord(secondRule);
        mSecondRule.setText(secondRule);

        SpannableString thirdRule = new SpannableString("规则”宽限时长”：当实际停车时间超出预订结束停车时间，系统会根据该车位的实际情况和用户的信用等级自动分配一个结束停车的顺延时长，" +
                "在宽限时长内离开仍按原价计费不收取超时费，信用等级为极好、优秀、良好、一般、较差的顺延时长最大值分别为60、45、30、15、10分钟。");
        hideTwoWord(thirdRule);
        mThirdRule.setText(thirdRule);

        SpannableString fourthRule = new SpannableString("规则“提前结单”：当用户停车出现过早离开的违规行为，提前结单的特权可以免除用户该次停车的违规行为。" +
                "信用分等级分别为良好、优秀、极好的用户每个自然月对应可以有一、三、五次的提前结单的特权。");
        hideTwoWord(fourthRule);
        mFourthRule.setText(fourthRule);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAnimatorSet == null) {
            //第一次进入页面的时候显示过渡到当前信用分的动画

            int duration = (mCredit - 200) * 2;

            //信用分的进度条上面的三角形平移动画
            ObjectAnimator creditViewAnimator = ObjectAnimator.ofFloat(mCreditView, "currentCredit", 200, mCredit);
            creditViewAnimator.setDuration(duration);

            //信用分的数字更新动画
            ValueAnimator valueAnimator = ValueAnimator.ofInt(0, mCredit);
            valueAnimator.setDuration(duration);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentCredit.setText(String.valueOf((int) animation.getAnimatedValue()));
                }
            });

            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(creditViewAnimator, valueAnimator);
            mAnimatorSet.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
        }
    }

    @NonNull
    @Override
    protected String title() {
        return "途找信用";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.credit_record_cl:
                startActivity(CreditRecordActivity.class);
                break;
            case R.id.no_deposit_cl:
// TODO: 2018/9/12  
                break;
            case R.id.leave_grace_cl:

                break;
            case R.id.advance_end_order_cl:

                break;
        }
    }

    /**
     * 隐藏前面的两个文字，用于占位的作用
     */
    private void hideTwoWord(SpannableString spannableString) {
        spannableString.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

}
