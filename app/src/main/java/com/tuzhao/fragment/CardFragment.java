package com.tuzhao.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.fragment.base.BaseRefreshFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.MonthlyCardBean;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/7/9.
 */
public class CardFragment extends BaseRefreshFragment<MonthlyCardBean.CardBean> {

    /**
     * 0（全部卡），1（地区卡），2（全国卡），3（过期卡）
     */
    private int mType;

    public static CardFragment newInstance(ArrayList<MonthlyCardBean.CardBean> list, int type) {
        CardFragment cardFragment = new CardFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ConstansUtil.CARD_INFO_LIST, list);
        bundle.putInt(ConstansUtil.TYPE, type);
        cardFragment.setArguments(bundle);
        return cardFragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        View footerView = getLayoutInflater().inflate(R.layout.layout_placeholder, mRecyclerView.getRecyclerView(),false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                DensityUtil.dp2px(mRecyclerView.getContext(), 20));
        footerView.setLayoutParams(layoutParams);
        mCommonAdapter.setFooterView(footerView);
    }

    @Override
    protected void initData() {
        if (getArguments() != null) {
            mType = getArguments().getInt(ConstansUtil.TYPE);
            ArrayList<MonthlyCardBean.CardBean> list = getArguments().getParcelableArrayList(ConstansUtil.CARD_INFO_LIST);
            Base_Class_List_Info<MonthlyCardBean.CardBean> cardInfoBaseClassListInfo = new Base_Class_List_Info<>();
            cardInfoBaseClassListInfo.data = list;
            if (mType != 3) {
                mRecyclerView.setRefreshEnabled(false);
                mRecyclerView.setLoadingMoreEnable(false);
                loadDataSuccess(cardInfoBaseClassListInfo);
            } else {
                if (list != null && !list.isEmpty()) {
                    loadDataSuccess(cardInfoBaseClassListInfo);
                } else {
                    showLoadingDialog();
                    getUserExpiredCards();
                }
            }
        }
    }

    @Override
    protected void loadData() {
        if (mType == 3) {
            getUserExpiredCards();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mType == 3) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                bundle.clear();
                bundle.putParcelableArrayList(ConstansUtil.CARD_INFO_LIST, (ArrayList<? extends Parcelable>) mCommonAdapter.getData());
                bundle.putInt(ConstansUtil.TYPE, mType);
            }
        }
    }

    private void getUserExpiredCards() {
        getOkgos(HttpConstants.getUserExpiredCards)
                .execute(new JsonCallback<Base_Class_List_Info<MonthlyCardBean.CardBean>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<MonthlyCardBean.CardBean> o, Call call, Response response) {
                        loadDataSuccess(o);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            loadDataFail(e, new LoadFailCallback() {
                                @Override
                                public void onLoadFail(Exception e) {

                                }
                            });
                        }
                    }
                });
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_monthly_card_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, MonthlyCardBean.CardBean cardInfo, int position) {
        holder.setText(R.id.monthly_card_area, cardInfo.getArea().replace("市", "") + "卡")
                .setText(R.id.monthly_card_expried_date, cardInfo.getExpiredDate().substring(0, cardInfo.getExpiredDate().indexOf(" ")) + "过期")
                .showPicWithNoAnimate(R.id.monthly_card_iv, cardInfo.getArea().equals("全国") ? R.drawable.ic_allcity : R.drawable.ic_vip)
                .setText(R.id.monthly_card_status, cardInfo.getStatus().equals("1") ? "生效中" : "已过期");
    }

    @Override
    protected int resourceId() {
        return 0;
    }

}
