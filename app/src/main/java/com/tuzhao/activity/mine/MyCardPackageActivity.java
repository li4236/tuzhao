package com.tuzhao.activity.mine;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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

    private ArrayList<CardInfo> mAllCardList;

    private ArrayList<CardInfo> mAreaCardList;

    private ArrayList<CardInfo> mNationalCardList;

    private ArrayList<CardInfo> mExpiredCardList;

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
                        mNationalCardList = o.data.getNationalCard();
                        mAllCardList.addAll(mAreaCardList);
                        mAllCardList.addAll(mNationalCardList);

                        mAllCard.setText("全部（" + mAllCardList.size() + "）");
                        mAreaCard.setText("地区月卡（" + mAreaCardList.size() + "）");
                        mNationalCard.setText("全国月卡（" + mNationalCardList.size() + ")");
                        mExpiredCard.setText("过期月卡（" + o.data.getExpiredCardSize() + "）");

                        if (mAllCardList.isEmpty() && o.data.getExpiredCardSize().equals("0")) {
                            ViewStub viewStub = findViewById(R.id.no_monthly_card_vs);
                            viewStub.setVisibility(View.VISIBLE);
                        } else {
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.monthly_card_container, CardFragment.newInstance(mAllCardList, 0));
                            transaction.commit();
                        }
                    }
                });
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
                transaction.replace(R.id.monthly_card_container, CardFragment.newInstance(mAllCardList, 0));
                transaction.commit();
                break;
            case R.id.area_card:
                transaction.replace(R.id.monthly_card_container, CardFragment.newInstance(mAreaCardList, 1));
                transaction.commit();
                break;
            case R.id.national_card:
                transaction.replace(R.id.monthly_card_container, CardFragment.newInstance(mNationalCardList, 2));
                transaction.commit();
                break;
            case R.id.expired_card:
                transaction.replace(R.id.monthly_card_container, CardFragment.newInstance(mExpiredCardList, 3));
                transaction.commit();
                break;
        }
        Log.e(TAG, "onClick: " + v.getId());
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
