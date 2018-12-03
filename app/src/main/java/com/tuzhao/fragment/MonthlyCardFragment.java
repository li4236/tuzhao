package com.tuzhao.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.tuzhao.R;
import com.tuzhao.adapter.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.fragment.base.BaseRefreshFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.MonthlyCardBean;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.customView.CornerImageView;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/7/9.
 */
public class MonthlyCardFragment extends BaseRefreshFragment<MonthlyCardBean> implements IntentObserver {

    /**
     * 1（全部卡），2（过期卡）
     */
    private int mType;

    public static MonthlyCardFragment newInstance(int type) {
        MonthlyCardFragment monthlyCardFragment = new MonthlyCardFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstansUtil.TYPE, type);
        monthlyCardFragment.setArguments(bundle);
        monthlyCardFragment.setTAG(monthlyCardFragment.getTAG() + " type:" + type);
        return monthlyCardFragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        mCommonAdapter.setPlaceholderHeaderView(8);
    }

    @Override
    protected void initData() {
        if (getArguments() != null) {
            mType = getArguments().getInt(ConstansUtil.TYPE);
            ArrayList<MonthlyCardBean> list = getArguments().getParcelableArrayList(ConstansUtil.CARD_INFO_LIST);
            if (list == null) {
                getUserMonthlyCards();
            } else {
                Base_Class_List_Info<MonthlyCardBean> cardInfoBaseClassListInfo = new Base_Class_List_Info<>();
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
        getUserMonthlyCards();
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

    private void getUserMonthlyCards() {
        if (mCommonAdapter.getData().size() == 0) {
            showLoadingDialog();
        }
        getOkgos(HttpConstants.getUserMonthlyCards)
                .params("type", mType)
                .execute(new JsonCallback<Base_Class_List_Info<MonthlyCardBean>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<MonthlyCardBean> o, Call call, Response response) {
                        if (mStartItme == 0 && mCommonAdapter.getData().isEmpty()) {
                            //如果一开始是没有月卡显示了购买界面的，则购买之后刷新把购买界面隐藏掉
                            notifyShowEmptyView(false);
                        }
                        loadDataSuccess(o);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        loadDataFail(e, new LoadFailCallback() {
                            @Override
                            public void onLoadFail(Exception e) {
                                if (e.getMessage().equals("101")) {
                                    if (mStartItme == 0) {
                                        mCommonAdapter.clearAll();
                                        //刷新之后显示没有数据的，则显示购买界面
                                        notifyShowEmptyView(true);
                                    } else if (mCommonAdapter.getDataSize() != 0) {
                                        showFiveToast("没有更多数据了哦");
                                    }
                                } else {
                                    showFiveToast(e.getMessage());
                                }
                            }
                        });
                    }
                });
    }

    private void notifyShowEmptyView(boolean show) {
        Intent intent = new Intent(ConstansUtil.SHOW_EMPTY_VIEW);
        intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, show);
        IntentObserable.dispatch(intent);
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_monthly_card_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, MonthlyCardBean cardInfo, int position) {
        CornerImageView imageView = holder.getView(R.id.monthly_card_iv);
        imageView.setCornerText(DateUtil.deleteZero(cardInfo.getDiscount() * 10) + "折");
        imageView.setStartText(cardInfo.getArea().replace("市", "") + "卡");
        imageView.setEndText(cardInfo.getExpiredDate().substring(0, cardInfo.getExpiredDate().indexOf(" ")) + "过期");

        if (mType == 2) {
            if (cardInfo.getArea().equals("全国")) {
                holder.showPic(R.id.monthly_card_iv, R.drawable.ic_grayallcity)
                        .showPic(R.id.area_monthly_card_park, R.drawable.ic_graylogo);
            } else {
                holder.showPic(R.id.monthly_card_iv, R.drawable.ic_graycitycard)
                        .showPic(R.id.area_monthly_card_park, R.drawable.ic_blacklogo);
            }
            imageView.setCornerColor(Color.parseColor("#808082"));
        } else {
            if (cardInfo.getArea().equals("全国")) {
                holder.showPic(R.id.monthly_card_iv, R.drawable.ic_allcity)
                        .showPic(R.id.area_monthly_card_park, R.drawable.ic_pinklogo);
                imageView.setCornerColor(Color.parseColor("#fe9b40"));
            } else {
                holder.showPic(R.id.monthly_card_iv, R.drawable.ic_citycard)
                        .showPic(R.id.area_monthly_card_park, R.drawable.ic_blacklogo);
                imageView.setCornerColor(Color.parseColor("#ff0101"));
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
