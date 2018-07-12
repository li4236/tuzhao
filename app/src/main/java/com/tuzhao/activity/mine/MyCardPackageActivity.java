package com.tuzhao.activity.mine;

import android.annotation.SuppressLint;
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
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.MonthlyCardBean;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.IntentObserver;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/7/9.
 */
public class MyCardPackageActivity extends BaseStatusActivity implements View.OnClickListener, IntentObserver {

    private TextView mAllCard;

    private TextView mAreaCard;

    private TextView mNationalCard;

    private TextView mExpiredCard;

    private ViewStub mViewStub;

    private ArrayList<MonthlyCardBean.CardBean> mAllCardList;

    private ArrayList<MonthlyCardBean.CardBean> mAreaCardList;

    private ArrayList<MonthlyCardBean.CardBean> mNationalCardList;

    private int mLastPosition = 0;

    private MonthlyCardFragment mAllCardFragment;

    private MonthlyCardFragment mAreaCardFragment;

    private MonthlyCardFragment mNationalCardFragment;

    private MonthlyCardFragment mExpriredFragment;

    @Override
    protected int resourceId() {
        return R.layout.activity_card_package_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mAllCard = findViewById(R.id.all_card);
        mAreaCard = findViewById(R.id.area_card);
        mNationalCard = findViewById(R.id.national_card);
        mExpiredCard = findViewById(R.id.expired_card);

        mAllCard.setOnClickListener(this);
        mAreaCard.setOnClickListener(this);
        mNationalCard.setOnClickListener(this);
        mExpiredCard.setOnClickListener(this);
        findViewById(R.id.buy_monthly_card).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mAllCardList = new ArrayList<>(4);
        mAreaCardList = new ArrayList<>(1);
        mNationalCardList = new ArrayList<>();
        getUserCards(false);
    }

    private void getUserCards(final boolean reset) {
        getOkGo(HttpConstants.getUserCards)
                .execute(new JsonCallback<Base_Class_Info<MonthlyCardBean>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(Base_Class_Info<MonthlyCardBean> o, Call call, Response response) {
                        mAreaCardList = (ArrayList<MonthlyCardBean.CardBean>) o.data.getAreaCards();
                        mNationalCardList = (ArrayList<MonthlyCardBean.CardBean>) o.data.getNationalCards();
                        if (reset) {
                            mAllCardList.clear();
                            mAreaCardFragment = null;
                            mNationalCardFragment = null;
                        }
                        mAllCardList.addAll(mAreaCardList);
                        mAllCardList.addAll(mNationalCardList);
                        mAllCardFragment = MonthlyCardFragment.newInstance(mAllCardList, 0);

                        mAllCard.setText("全部（" + mAllCardList.size() + "）");
                        mAreaCard.setText("地区月卡（" + mAreaCardList.size() + "）");
                        mNationalCard.setText("全国月卡（" + mNationalCardList.size() + "）");
                        mExpiredCard.setText("过期月卡（" + o.data.getExpiredCardSize() + "）");

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.monthly_card_container, mAllCardFragment);
                        transaction.commit();
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    showViewStub(View.VISIBLE);
                                    break;
                                default:
                                    showFiveToast(e.getMessage());
                                    break;
                            }
                        }
                    }
                });
    }

    private void showViewStub(int visibility) {
        if (mViewStub == null) {
            mViewStub = findViewById(R.id.no_monthly_card_vs);
            View view = mViewStub.inflate();
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
        mViewStub.setVisibility(visibility);
    }

    @NonNull
    @Override
    protected String title() {
        return "卡包";
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.all_card:
                setTextNormalColor(0);
                mAllCard.setTextColor(ConstansUtil.Y3_COLOR);
                if (mAllCardFragment == null) {
                    mAllCardFragment = MonthlyCardFragment.newInstance(mAllCardList, 0);
                }
                transaction.replace(R.id.monthly_card_container, mAllCardFragment);
                transaction.commit();
                break;
            case R.id.area_card:
                setTextNormalColor(1);
                mAreaCard.setTextColor(ConstansUtil.Y3_COLOR);
                if (mAreaCardFragment == null) {
                    mAreaCardFragment = MonthlyCardFragment.newInstance(mAreaCardList, 1);
                }
                transaction.replace(R.id.monthly_card_container, mAreaCardFragment);
                transaction.commit();
                break;
            case R.id.national_card:
                setTextNormalColor(2);
                mNationalCard.setTextColor(ConstansUtil.Y3_COLOR);
                if (mNationalCardFragment == null) {
                    mNationalCardFragment = MonthlyCardFragment.newInstance(mNationalCardList, 2);
                }
                transaction.replace(R.id.monthly_card_container, mNationalCardFragment);
                transaction.commit();
                break;
            case R.id.expired_card:
                setTextNormalColor(3);
                mExpiredCard.setTextColor(ConstansUtil.Y3_COLOR);
                if (mExpriredFragment == null) {
                    mExpriredFragment = MonthlyCardFragment.newInstance(new ArrayList<MonthlyCardBean.CardBean>(), 3);
                }
                transaction.replace(R.id.monthly_card_container, mExpriredFragment);
                transaction.commit();
                break;
            case R.id.buy_monthly_card:
                startActivity(BuyMonthlyCardActivity.class);
                break;
        }
    }

    private void setTextNormalColor(int position) {
        switch (mLastPosition) {
            case 0:
                mAllCard.setTextColor(ConstansUtil.B1_COLOR);
                break;
            case 1:
                mAreaCard.setTextColor(ConstansUtil.B1_COLOR);
                break;
            case 2:
                mNationalCard.setTextColor(ConstansUtil.B1_COLOR);
                break;
            case 3:
                mExpiredCard.setTextColor(ConstansUtil.B1_COLOR);
                break;
        }
        mLastPosition = position;
    }

    @Override
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(ConstansUtil.PAY_SUCCESS)) {
                getUserCards(true);
            }
        }
    }
}
