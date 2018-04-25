package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.InvoiceSituation;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.others.SkipTopBottomDivider;
import com.tuzhao.utils.ConstansUtil;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/4/14.
 */

public class InvoiceSituationActivity extends BaseRefreshActivity<InvoiceSituation> {

    @Override
    protected RecyclerView.LayoutManager createLayouManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mRecyclerView.addItemDecoration(new SkipTopBottomDivider(this, true, true));
    }

    @Override
    protected void loadData() {
        getOkgo(HttpConstants.getInvoiceSituation)
                .execute(new JsonCallback<Base_Class_List_Info<InvoiceSituation>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<InvoiceSituation> o, Call call, Response response) {
                        loadDataSuccess(o);
                    }

                    @Override
                    public void onError(final Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        loadDataFail(e, new LoadFailCallback() {
                            @Override
                            public void onLoadFail(Exception e) {
                                switch (e.getMessage()) {
                                    case "101":
                                        userNotExist();
                                        break;
                                    case "102":
                                        showFiveToast("你还没选择发票哦");
                                        break;
                                    case "103":
                                    case "104":
                                        showFiveToast("请求失败，请稍后重试");
                                        break;
                                }
                            }
                        });
                    }
                });
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_invoice_progress_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, final InvoiceSituation invoiceSituation, int position) {
        holder.setText(R.id.invoice_progress_company_item, invoiceSituation.getCompany())
                .setText(R.id.invoice_progress_type_item, invoiceSituation.getType())
                .setText(R.id.invoice_progress_status_item, invoiceSituation.getStatus())
                .setText(R.id.invoice_progress_apply_item, invoiceSituation.getInvoiceDate() == null ? "暂未开票" : invoiceSituation.getDeliveryDate())
                .setText(R.id.invoice_progress_price_item, "￥" + invoiceSituation.getTotalPrice())
                .itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(InvoiceDetailActivity.class, ConstansUtil.INVOICE_SITUATION, invoiceSituation);
            }
        });
    }

    @Override
    protected int resourceId() {
        return 0;
    }

    @NonNull
    @Override
    protected String title() {
        return "开票进度";
    }

}
