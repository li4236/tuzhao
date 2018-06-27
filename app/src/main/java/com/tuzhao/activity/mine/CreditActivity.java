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
import android.widget.ScrollView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.others.CreditView;
import com.tuzhao.utils.ImageUtil;

/**
 * Created by juncoder on 2018/6/26.
 */
public class CreditActivity extends BaseStatusActivity implements View.OnClickListener {

    private ScrollView mScrollView;

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
        if (mCredit <= 350) {
            mCreditStage.setText("极差");
            mCreditStage.setBackgroundResource(R.drawable.r7_all_3dp);
            ImageUtil.showPic(mNoDepositIv, R.drawable.ic_nodeposit);
        } else if (mCredit <= 550) {
            mCreditStage.setText("差");
            mCreditStage.setBackgroundResource(R.drawable.y2_all_3dp);
            ImageUtil.showPic(mNoDepositIv, R.drawable.ic_nodeposit);
        } else if (mCredit <= 650) {
            mCreditStage.setText("良好");
            mCreditStage.setBackgroundResource(R.drawable.y3_all_3dp);
            ImageUtil.showPic(mNoDepositIv, R.drawable.ic_nodeposit);
        } else if (mCredit <= 750) {
            mCreditStage.setText("优秀");
            mCreditStage.setBackgroundResource(R.drawable.green8_all_3dp);
        } else {
            mCreditStage.setText("极好");
            mCreditStage.setBackgroundResource(R.drawable.green9_all_3dp);
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

        SpannableString secondRule = new SpannableString("规则“免担保金”指的是途找信用免押，要求途找信用700及以上，若未达此条件则需交押金");
        hideTwoWord(secondRule);
        mSecondRule.setText(secondRule);

        SpannableString thirdRule = new SpannableString("规则“离开宽限”指的是当您有急事未能及时离开停车车位时，根据您的履约情况给您的一个离开宽限时间，" +
                "宽限时间内离开按原时价计费不收取滞留金，宽限时长随信用增加而增加。");
        hideTwoWord(thirdRule);
        mThirdRule.setText(thirdRule);

        SpannableString fourthRule = new SpannableString("规则“提前结单”指的是未达到预约时长提早离开，预约过长时间而停留过短会影响平台结算以及车主利益。" +
                "为保障共享用户的权益，请合理预约停车时间，若临时有事，可延长停车时间。");
        hideTwoWord(fourthRule);
        mFourthRule.setText(fourthRule);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAnimatorSet == null) {
            ObjectAnimator creditViewAnimator = ObjectAnimator.ofFloat(mCreditView, "currentCredit", 200, mCredit);
            creditViewAnimator.setDuration(1500);

            ValueAnimator valueAnimator = ValueAnimator.ofInt(0, mCredit);
            valueAnimator.setDuration(1500);
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

                break;
            case R.id.no_deposit_cl:

                break;
            case R.id.leave_grace_cl:

                break;
            case R.id.advance_end_order_cl:

                break;
        }
    }

    private void hideTwoWord(SpannableString spannableString) {
        spannableString.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

}
