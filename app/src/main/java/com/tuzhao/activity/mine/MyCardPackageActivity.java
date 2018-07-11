package com.tuzhao.activity.mine;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.fragment.CardFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.CardBean;
import com.tuzhao.info.CardInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/7/9.
 */
public class MyCardPackageActivity extends BaseStatusActivity implements View.OnClickListener, SuccessCallback<ArrayList<CardInfo>> {

    private TextView mAllCard;

    private TextView mAreaCard;

    private TextView mNationalCard;

    private TextView mExpiredCard;

    private ViewStub mViewStub;

    private ArrayList<CardInfo> mAllCardList;

    private ArrayList<CardInfo> mAreaCardList;

    private ArrayList<CardInfo> mNationalCardList;

    private ArrayList<CardInfo> mExpiredCardList;

    private int mLastPosition = 0;

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
    }

    @Override
    protected void initData() {
        super.initData();
        mAllCardList = new ArrayList<>();
        mAreaCardList = new ArrayList<>();
        mNationalCardList = new ArrayList<>();
        mExpiredCardList = new ArrayList<>();
        getUserCards();
    }

    private void getUserCards() {
        getOkGo(HttpConstants.getUserCards)
                .execute(new JsonCallback<Base_Class_Info<CardBean>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(Base_Class_Info<CardBean> o, Call call, Response response) {
                        mAreaCardList = o.data.getAreaCards();
                        mNationalCardList = o.data.getNationalCards();
                        mAllCardList.addAll(mAreaCardList);
                        mAllCardList.addAll(mNationalCardList);

                        mAllCard.setText("全部（" + mAllCardList.size() + "）");
                        mAreaCard.setText("地区月卡（" + mAreaCardList.size() + "）");
                        mNationalCard.setText("全国月卡（" + mNationalCardList.size() + ")");
                        mExpiredCard.setText("过期月卡（" + o.data.getExpiredCardSize() + "）");

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.monthly_card_container, CardFragment.newInstance(mAllCardList, 0));
                        transaction.commit();
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
                transaction.replace(R.id.monthly_card_container, CardFragment.newInstance(mAllCardList, 0));
                transaction.commit();
                break;
            case R.id.area_card:
                setTextNormalColor(1);
                mAreaCard.setTextColor(ConstansUtil.Y3_COLOR);
                transaction.replace(R.id.monthly_card_container, CardFragment.newInstance(mAreaCardList, 1));
                transaction.commit();
                break;
            case R.id.national_card:
                setTextNormalColor(2);
                mNationalCard.setTextColor(ConstansUtil.Y3_COLOR);
                transaction.replace(R.id.monthly_card_container, CardFragment.newInstance(mNationalCardList, 2));
                transaction.commit();
                break;
            case R.id.expired_card:
                setTextNormalColor(3);
                mExpiredCard.setTextColor(ConstansUtil.Y3_COLOR);
                transaction.replace(R.id.monthly_card_container, CardFragment.newInstance(mExpiredCardList, 3));
                transaction.commit();
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
    public void onSuccess(ArrayList<CardInfo> cardInfos) {
        if (mExpiredCardList.containsAll(cardInfos)) {
            //下拉刷新的则把旧数据清空，显示新的数据
            mExpiredCardList.clear();
        }
        mExpiredCardList.addAll(cardInfos);
    }

}
