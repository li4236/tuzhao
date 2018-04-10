package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.info.AcceptTicketAddressInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juncoder on 2018/3/29.
 */

public class AcceptTicketAddressActivity extends BaseRefreshActivity {

    private int mDefaultAddressPosition;

    private AcceptTicketAddressAdapter mAdapter;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mRecyclerView.setLoadingMoreEnable(false);
        mRecyclerView.setEmptyView(R.layout.no_address_empty_layout);
        mAdapter = new AcceptTicketAddressAdapter();
        mRecyclerView.setAdapter(mAdapter);
        findViewById(R.id.accept_ticket_address_add_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AddAcceptTicketAddressActivity.class);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        requestData();
        dismmisLoadingDialog();
    }

    @Override
    protected RecyclerView.LayoutManager createLayouManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    protected void onRefresh() {
        requestData();
        mRecyclerView.setRefreshing(false);
    }

    @Override
    protected void onLoadMore() {

    }

    @Override
    protected int resourceId() {
        return R.layout.activity_accept_ticket_address_layout;
    }

    @NonNull
    @Override
    protected String title() {
        return "收票地址";
    }

    private void requestData() {
        List<AcceptTicketAddressInfo> list = new ArrayList<>();
        AcceptTicketAddressInfo addressInfo;
        for (int i = 0; i < 10; i++) {
            addressInfo = new AcceptTicketAddressInfo();
            addressInfo.setAcceptPersonName("赤炎火狮");
            addressInfo.setAcceptPersonTelephone("18219111679");
            if (i % 2 == 0) {
                addressInfo.setAcceptPersonEmail("1247660633@qq.com");
                addressInfo.setType("电子");
            } else {
                addressInfo.setType("普票");
                addressInfo.setAcceptAddress("广东省中山市五桂山长命水长逸路5号1栋");
            }
            if (i == 2) {
                addressInfo.setIsDefault("ture");
                mDefaultAddressPosition = i;
            } else {
                addressInfo.setIsDefault("false");
            }
            list.add(addressInfo);
        }
        mAdapter.setNewData(list);
    }

    class AcceptTicketAddressAdapter extends BaseAdapter<AcceptTicketAddressInfo> {

        @Override
        protected void conver(@NonNull BaseViewHolder holder, final AcceptTicketAddressInfo acceptTicketAddressInfo, final int position) {
            holder.setText(R.id.accept_ticket_address_name, acceptTicketAddressInfo.getAcceptPersonName())
                    .setText(R.id.accept_ticket_address_telephone, acceptTicketAddressInfo.getAcceptPersonTelephone())
                    .setText(R.id.accept_ticket_address_type, acceptTicketAddressInfo.getType())
                    .setText(R.id.accept_ticket_address_address, acceptTicketAddressInfo.getAcceptAddress())
                    .setCheckboxCheck(R.id.accept_ticket_address_set_default, acceptTicketAddressInfo.getIsDefault().equals("ture"));
            if (acceptTicketAddressInfo.getIsDefault().equals("ture")) {
                mDefaultAddressPosition = position;
            }
            ((CheckBox) holder.getView(R.id.accept_ticket_address_set_default)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (mRecyclerView.getRecyclerView().getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                            && !mRecyclerView.getRecyclerView().isComputingLayout()) {
                        if (isChecked) {
                            if (mDefaultAddressPosition >= 0) {
                                getData().get(mDefaultAddressPosition).setIsDefault("false");
                            }
                            acceptTicketAddressInfo.setIsDefault("ture");
                            mDefaultAddressPosition = position;
                        } else {
                            if (mDefaultAddressPosition == position) {
                                getData().get(mDefaultAddressPosition).setIsDefault("false");
                            }
                            acceptTicketAddressInfo.setIsDefault("false");
                        }
                        notifyDataSetChanged();
                    }
                }
            });

            holder.getView(R.id.accept_ticket_address_edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            holder.getView(R.id.accept_ticket_address_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (acceptTicketAddressInfo.getIsDefault().equals("ture")) {
                        mDefaultAddressPosition = -1;
                    }
                    getData().remove(position);
                    notifyDataSetChanged();
                    if (getData().isEmpty()) {
                        mRecyclerView.showEmpty(null);
                    }
                }
            });
        }

        @Override
        protected int itemViewId() {
            return R.layout.item_accept_ticket_address_layout;
        }
    }
}
