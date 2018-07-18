package com.tuzhao.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.fragment.base.BaseRefreshFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.CardBean;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/7/9.
 */
public class MonthlyCardFragment extends BaseRefreshFragment<CardBean> implements IntentObserver {

    /**
     * 1（全部卡），2（过期卡）
     */
    private int mType;

    public static MonthlyCardFragment newInstance(int type) {
        MonthlyCardFragment monthlyCardFragment = new MonthlyCardFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstansUtil.TYPE, type);
        monthlyCardFragment.setArguments(bundle);
        return monthlyCardFragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        View footerView = getLayoutInflater().inflate(R.layout.layout_placeholder, mRecyclerView.getRecyclerView(), false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                DensityUtil.dp2px(mRecyclerView.getContext(), 20));
        footerView.setLayoutParams(layoutParams);
        mCommonAdapter.setFooterView(footerView);
    }

    @Override
    protected void initData() {
        if (getArguments() != null) {
            mType = getArguments().getInt(ConstansUtil.TYPE);
            ArrayList<CardBean> list = getArguments().getParcelableArrayList(ConstansUtil.CARD_INFO_LIST);
            if (list == null) {
                getUserExpiredCards();
            } else {
                Base_Class_List_Info<CardBean> cardInfoBaseClassListInfo = new Base_Class_List_Info<>();
                cardInfoBaseClassListInfo.data = list;
                if (!list.isEmpty()) {
                    loadDataSuccess(cardInfoBaseClassListInfo);
                } else {
                    notifyShowEmptyView(true);
                }
            }
            IntentObserable.registerObserver(this);
        }
    }

    @Override
    protected void loadData() {
        getUserExpiredCards();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Bundle bundle = getArguments();
        if (bundle != null) {
            bundle.clear();
            bundle.putParcelableArrayList(ConstansUtil.CARD_INFO_LIST, (ArrayList<? extends Parcelable>) mCommonAdapter.getData());
            bundle.putInt(ConstansUtil.TYPE, mType);
        }
        IntentObserable.unregisterObserver(this);
    }

    private void getUserExpiredCards() {
        if (mCommonAdapter.getData().size() == 0) {
            showLoadingDialog();
        }
        getOkgos(HttpConstants.getUserMonthlyCards)
                .params("type", mType)
                .execute(new JsonCallback<Base_Class_List_Info<CardBean>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<CardBean> o, Call call, Response response) {
                        loadDataSuccess(o);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mCommonAdapter.getData().size() == 0) {
                            notifyShowEmptyView(true);
                        }
                        stopLoadStatus();
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    if (!mCommonAdapter.getData().isEmpty()) {
                                        showFiveToast("没有更多数据了哦");
                                    }
                                    break;
                                default:
                                    showFiveToast(e.getMessage());
                                    break;
                            }
                        }
                    }
                });
    }

    private void notifyShowEmptyView(boolean show) {
        Intent intent = new Intent(ConstansUtil.SHOW_EMPTY_VIEW);
        intent.putExtra(ConstansUtil.FOR_REQUEST_RESULT, show);
        IntentObserable.dispatch(intent);
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_monthly_card_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, CardBean cardInfo, int position) {
        holder.setText(R.id.area_monthly_card, cardInfo.getArea().replace("市", "") + "卡")
                .setText(R.id.monthly_card_expried_date, cardInfo.getExpiredDate().substring(0, cardInfo.getExpiredDate().indexOf(" ")) + "过期");
        if (mType == 2) {
            if (cardInfo.getArea().equals("全国")) {
                holder.showPic(R.id.monthly_card_iv, R.drawable.ic_grayallcity_shadow);
                holder.showPic(R.id.area_monthly_card_park, R.drawable.ic_graylogo);
            } else {
                holder.showPic(R.id.monthly_card_iv, R.drawable.ic_graycitycard_shadow);
                holder.showPic(R.id.area_monthly_card_park, R.drawable.ic_blacklogo);
            }
        } else {
            if (cardInfo.getArea().equals("全国")) {
                holder.showPic(R.id.monthly_card_iv, R.drawable.ic_allcity_shadow);
                holder.showPic(R.id.area_monthly_card_park, R.drawable.ic_pinklogo);
            } else {
                holder.showPic(R.id.monthly_card_iv, R.drawable.ic_citycard_shadow);
                holder.showPic(R.id.area_monthly_card_park, R.drawable.ic_blacklogo);
            }
        }
    }

    @Override
    protected int resourceId() {
        return 0;
    }

    @Override
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(ConstansUtil.PAY_SUCCESS)) {
                if (mType == 1) {
                    onRefresh();
                }
            }
        }
    }
}
