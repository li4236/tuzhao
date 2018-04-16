package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.info.InvoiceSituation;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.others.SkipTopBottomDivider;
import com.tuzhao.utils.ConstansUtil;

import java.util.ArrayList;

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
        mRecyclerView.addItemDecoration(new SkipTopBottomDivider(this,true,true));
    }

    @Override
    protected void loadData() {
        ArrayList<InvoiceSituation> list = new ArrayList<>();
        InvoiceSituation situation;
        for (int i = 0; i < 3; i++) {
            situation = new InvoiceSituation();
            situation.setAddress("广东省中山市五桂山长命水长逸路天之力新能源科技有限公司");
            situation.setCompany("天之力新能源科技有限公司");
            situation.setCourierNumber("ABC13554354");
            situation.setDeliveryDate("2018-4-16 18:00");
            situation.setPersonName("钟俊");
            situation.setProgress("已发出 EMS ABC13554354");
            situation.setTelephone("18219111679");
            situation.setStatus("已发出");
            situation.setTicketContent("停车费用");
            situation.setTotalPrice("105.00");
            situation.setTicketRise("23546436245345");
            situation.setTaxpayerNumber("iejj3254353");
            situation.setType("普票");
            list.add(situation);
        }
        Base_Class_List_Info<InvoiceSituation> base_class_list_info = new Base_Class_List_Info<>();
        base_class_list_info.code = "0";
        base_class_list_info.data = list;
        loadDataSuccess(base_class_list_info);
        /*getOkgo(HttpConstants.getInvoiceSituation)
                .execute(new JsonCallback<Base_Class_List_Info<InvoiceSituation>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<InvoiceSituation> o, Call call, Response response) {
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
                });*/
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_invoice_progress_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, final InvoiceSituation invoiceSituation, int position) {
        holder.setText(R.id.invoice_progress_company_item, invoiceSituation.getCompany())
                .setText(R.id.invoice_progress_type_item, invoiceSituation.getType())
                .setText(R.id.invoice_progress_status_item, invoiceSituation.getProgress())
                .setText(R.id.invoice_progress_apply_item, invoiceSituation.getDeliveryDate())
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
