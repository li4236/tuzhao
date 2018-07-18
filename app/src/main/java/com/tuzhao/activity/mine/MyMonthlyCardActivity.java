package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.fragment.MonthlyCardFragment;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

/**
 * Created by juncoder on 2018/7/9.
 */
public class MyMonthlyCardActivity extends BaseStatusActivity implements View.OnClickListener, IntentObserver {

    private TextView mAllCard;

    private View mAllCardIndicate;

    private TextView mExpiredCard;

    private View mExpiredCardIndicate;

    private ViewStub mViewStub;

    private TextView mNoMonthlyCardTv;

    private TextView mBuyNowTv;

    private MonthlyCardFragment mAllCardFragment;

    private MonthlyCardFragment mExpriredFragment;

    @Override
    protected int resourceId() {
        return R.layout.activity_monthly_card_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mAllCard = findViewById(R.id.all_card);
        mAllCardIndicate = findViewById(R.id.all_card_indicate);
        mExpiredCard = findViewById(R.id.expired_card);
        mExpiredCardIndicate = findViewById(R.id.expired_card_indicate);

        mAllCard.setOnClickListener(this);
        mAllCardIndicate.setOnClickListener(this);
        mExpiredCard.setOnClickListener(this);
        mExpiredCardIndicate.setOnClickListener(this);
        findViewById(R.id.buy_monthly_card).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        IntentObserable.registerObserver(this);
        mAllCardFragment = MonthlyCardFragment.newInstance(1);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.monthly_card_container, mAllCardFragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
    }

    private void showViewStub(int visibility) {
        if (mViewStub == null) {
            mViewStub = findViewById(R.id.no_monthly_card_vs);
            View view = mViewStub.inflate();
            mNoMonthlyCardTv = view.findViewById(R.id.no_monthly_card_tv);
            mBuyNowTv = view.findViewById(R.id.buy_now);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            view.findViewById(R.id.buy_now).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(BuyMonthlyCardActivity.class);
                }
            });
        }
        if (visibility == View.VISIBLE) {
            if (isVisible(mAllCardIndicate)) {
                mNoMonthlyCardTv.setText("您暂时没有月卡哦");
                if (!isVisible(mBuyNowTv)) {
                    mBuyNowTv.setVisibility(View.VISIBLE);
                }
            } else {
                mNoMonthlyCardTv.setText("您没有过期的月卡哦");
                if (isVisible(mBuyNowTv)) {
                    mBuyNowTv.setVisibility(View.GONE);
                }
            }
        }
        mViewStub.setVisibility(visibility);
    }

    @NonNull
    @Override
    protected String title() {
        return "月卡";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_card:
            case R.id.all_card_indicate:
                if (!isVisible(mAllCardIndicate)) {
                    showViewStub(View.GONE);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    mAllCard.setTextColor(ConstansUtil.B1_COLOR);
                    mExpiredCard.setTextColor(ConstansUtil.G6_COLOR);
                    mAllCardIndicate.setVisibility(View.VISIBLE);
                    mExpiredCardIndicate.setVisibility(View.INVISIBLE);
                    transaction.replace(R.id.monthly_card_container, mAllCardFragment);
                    transaction.commit();
                }
                break;
            case R.id.expired_card:
            case R.id.expired_card_indicate:
                if (!isVisible(mExpiredCardIndicate)) {
                    showViewStub(View.GONE);
                    mAllCard.setTextColor(ConstansUtil.G6_COLOR);
                    mExpiredCard.setTextColor(ConstansUtil.B1_COLOR);
                    mAllCardIndicate.setVisibility(View.INVISIBLE);
                    mExpiredCardIndicate.setVisibility(View.VISIBLE);
                    if (mExpriredFragment == null) {
                        mExpriredFragment = MonthlyCardFragment.newInstance(2);
                    }
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.monthly_card_container, mExpriredFragment);
                    fragmentTransaction.commit();
                }
                break;
            case R.id.buy_monthly_card:
                startActivity(BuyMonthlyCardActivity.class);
                break;
        }
    }

    @Override
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(ConstansUtil.SHOW_EMPTY_VIEW)) {
                showViewStub(View.VISIBLE);
            } else if (intent.getAction().equals(ConstansUtil.PAY_SUCCESS)) {
                showViewStub(View.INVISIBLE);
            }
        }
    }

}
