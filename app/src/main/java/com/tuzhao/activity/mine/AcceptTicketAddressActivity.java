package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseCallback;
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.AcceptTicketAddressInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/3/29.
 */

public class AcceptTicketAddressActivity extends BaseRefreshActivity<AcceptTicketAddressInfo> {

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mRecyclerView.setLoadingMoreEnable(false);
        mRecyclerView.setEmptyView(R.layout.no_address_empty_layout);
        findViewById(R.id.accept_ticket_address_add_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AddAcceptTicketAddressActivity.class);
            }
        });
    }

    @Override
    protected RecyclerView.LayoutManager createLayouManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    protected void loadData() {
        requestData(HttpConstants.getAcceptTicketAddress, new BaseCallback<Base_Class_List_Info<AcceptTicketAddressInfo>>() {
            @Override
            public void onSuccess(Base_Class_List_Info<AcceptTicketAddressInfo> acceptTicketAddressInfoBase_class_list_info, Call call, Response response) {

            }

            @Override
            public void onError(Call call, Response response, Exception e) {

            }
        });
    }

    private void setDefaultAddress(final String ticketId, final String isDefault) {
        showLoadingDialog();
        getOkgo(HttpConstants.setDefaultAcceptTicketAddress)
                .params("ticketId", ticketId)
                .params("isDefault", isDefault)
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> voidBase_class_info, Call call, Response response) {
                        notifyDefaultAddressChange(ticketId);
                        if (isDefault.equals("0")) {
                            showFiveToast("设置默认地址成功");
                        } else {
                            showFiveToast("取消默认地址成功");
                        }
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            if (isDefault.equals("0")) {
                                showFiveToast("取消默认地址失败，请稍后重试");
                            } else {
                                showFiveToast("设置默认地址成功，请稍后重试");
                            }
                        }
                    }
                });
    }

    private void notifyDefaultAddressChange(String ticketId) {
        for (int i = 0; i < mCommonAdapter.getData().size(); i++) {
            if (mCommonAdapter.getData().get(i).getTicketId().equals(ticketId)) {
                mCommonAdapter.getData().get(i).setIsDefault(ticketId.equals("0") ? "1" : "0");
                mCommonAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    private void deleteAddress(final String ticketId) {
        getOkgo(HttpConstants.deleteAcceptTicketAddress)
                .params("ticketId", ticketId)
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> base_class_info, Call call, Response response) {
                        notifyAddressDelete(ticketId);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast("删除失败，请稍后重试");
                        }
                    }
                });
    }

    private void notifyAddressDelete(String ticketId) {
        for (int i = 0; i < mCommonAdapter.getData().size(); i++) {
            if (mCommonAdapter.getData().get(i).getTicketId().equals(ticketId)) {
                mCommonAdapter.notifyRemoveData(i);
            }
        }
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_accept_ticket_address_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, final AcceptTicketAddressInfo acceptTicketAddressInfo, final int position) {
        String address = acceptTicketAddressInfo.getType().equals("电子") ? acceptTicketAddressInfo.getAcceptPersonEmail() :
                acceptTicketAddressInfo.getAcceptArea() + acceptTicketAddressInfo.getAcceptAddress();
        holder.setText(R.id.accept_ticket_address_name, acceptTicketAddressInfo.getAcceptPersonName())
                .setText(R.id.accept_ticket_address_telephone, acceptTicketAddressInfo.getAcceptPersonTelephone())
                .setText(R.id.accept_ticket_address_type, acceptTicketAddressInfo.getType())
                .setText(R.id.accept_ticket_address_address, address)
                .setCheckboxCheck(R.id.accept_ticket_address_set_default, acceptTicketAddressInfo.getIsDefault().equals("1"));

        ((CheckBox) holder.getView(R.id.accept_ticket_address_set_default)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mRecyclerView.getRecyclerView().getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                        && !mRecyclerView.getRecyclerView().isComputingLayout()) {
                    if (isChecked) {
                        setDefaultAddress(acceptTicketAddressInfo.getTicketId(), "1");
                        acceptTicketAddressInfo.setIsDefault("1");
                    } else if (acceptTicketAddressInfo.getTicketId().equals("1")) {
                        setDefaultAddress(acceptTicketAddressInfo.getTicketId(), "0");
                    }
                    mCommonAdapter.notifyDataSetChanged();
                }
            }
        });

        holder.getView(R.id.accept_ticket_address_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAddress(acceptTicketAddressInfo.getTicketId());
            }
        });

        holder.getView(R.id.accept_ticket_address_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommonAdapter.getData().remove(position);
                mCommonAdapter.notifyDataSetChanged();
                if (mCommonAdapter.getData().isEmpty()) {
                    mRecyclerView.showEmpty(null);
                }
            }
        });
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

  /*  private void requestData() {
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
                addressInfo.setIsDefault("1");
            } else {
                addressInfo.setIsDefault("0");
            }
            list.add(addressInfo);
        }
        mCommonAdapter.addData(list);
    }*/

}
