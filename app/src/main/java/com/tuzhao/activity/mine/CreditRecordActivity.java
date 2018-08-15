package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.CreditRecordInfo;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.customView.SkipTopBottomDivider;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/6/30.
 */
public class CreditRecordActivity extends BaseRefreshActivity<CreditRecordInfo> {

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mRecyclerView.addItemDecoration(new SkipTopBottomDivider(this, true, true));
    }

    @Override
    protected RecyclerView.LayoutManager createLayouManager() {
        return new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true);
    }

    @Override
    protected void loadData() {
        getOkgos(HttpConstants.getCreditRecord)
                .execute(new JsonCallback<Base_Class_List_Info<CreditRecordInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<CreditRecordInfo> o, Call call, Response response) {
                        loadDataSuccess(o);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        loadDataFail(e, new LoadFailCallback() {
                            @Override
                            public void onLoadFail(Exception e) {

                            }
                        });
                    }
                });
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_credit_recoder_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, CreditRecordInfo creditRecordInfo, int position) {
        int creditDiversification = Integer.valueOf(creditRecordInfo.getCreditDiversification());
        if (creditDiversification >= 10) {
            holder.setText(R.id.credit_diversification, "本期途找信用 +" + creditDiversification + "分")
                    .setText(R.id.encourage_word, "突飞猛进，守信达人");
        } else if (creditDiversification > 0) {
            holder.setText(R.id.credit_diversification, "本期途找信用 +" + creditDiversification + "分")
                    .setText(R.id.encourage_word, "稍有进步，继续加油");
        } else if (creditDiversification == 0) {
            holder.setText(R.id.credit_diversification, "本期途找信用 +" + creditDiversification + "分")
                    .setText(R.id.encourage_word, "停歇不前，不进则退");
        } else if (creditDiversification <= -10) {
            holder.setText(R.id.credit_diversification, "本期途找信用 " + creditDiversification + "分")
                    .setText(R.id.encourage_word, "失信于人，每况愈下");
        } else {
            holder.setText(R.id.credit_diversification, "本期途找信用 " + creditDiversification + "分")
                    .setText(R.id.encourage_word, "稍有失误，继续努力");
        }
        holder.setText(R.id.credit_update_date, creditRecordInfo.getUpdateDate())
                .setText(R.id.credit_text, creditRecordInfo.getCredit() + "分");

    }

    @Override
    protected int resourceId() {
        return 0;
    }

    @NonNull
    @Override
    protected String title() {
        return "途找信用";
    }

}
