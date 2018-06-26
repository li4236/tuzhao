package com.tuzhao.activity.mine;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.others.OvalView;

import java.util.Calendar;

/**
 * Created by juncoder on 2018/6/26.
 */
public class CreditActivity extends BaseStatusActivity implements View.OnClickListener {

    private ScrollView mScrollView;

    private OvalView mOvalView;

    private TextView mCreditDate;

    private TextView mChangeCredit;

    private TextView mCurrentCredit;

    private TextView mParkGracePeriod;

    private TextView mCreditSituation;

    private TextView mFirstRule;

    private TextView mSecondRule;

    private TextView mThirdRule;

    private TextView mFourthRule;

    @Override
    protected int resourceId() {
        return R.layout.activity_personal_credit_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mOvalView = findViewById(R.id.oval_view);
        mCreditDate = findViewById(R.id.credit_date_tv);
        mChangeCredit = findViewById(R.id.change_credit_tv);
        mCurrentCredit = findViewById(R.id.current_credit);
        mParkGracePeriod = findViewById(R.id.park_grace_period);
        mCreditSituation = findViewById(R.id.credit_situation);
        mFirstRule = findViewById(R.id.first_rule_tv);
        mSecondRule = findViewById(R.id.second_rule_tv);
        mThirdRule = findViewById(R.id.third_rule_tv);
        mFourthRule = findViewById(R.id.fourth_rule_tv);

        findViewById(R.id.no_deposit_cl).setOnClickListener(this);
        findViewById(R.id.leave_grace_cl).setOnClickListener(this);
        findViewById(R.id.advance_end_order_cl).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        Calendar calendar = Calendar.getInstance();
        String yearWithMonth = calendar.get(Calendar.YEAR) + "." + calendar.get(Calendar.MONTH) + 1;
        mCreditDate.setText(yearWithMonth);

        mCurrentCredit.setText(com.tuzhao.publicmanager.UserManager.getInstance().getUserInfo().getCredit());

        String parkGracePeriod = UserManager.getInstance().getUserInfo().getLeave_time() + "分钟";
        mParkGracePeriod.setText(parkGracePeriod);

        SpannableString firstRule = new SpannableString("规则途找信用分是根据用户的每一次停车，每一次履约情况来进行评定的。");
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

    @NonNull
    @Override
    protected String title() {
        return "途找信用";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
