package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.AcceptTicketAddressInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/3/29.
 */

public class AcceptTicketAddressActivity extends BaseRefreshActivity<AcceptTicketAddressInfo> {

    /**
     * 是否需要传选中的收票地址给确认订单
     */
    private boolean mIsForResult;

    private static final int REQUEST_CODE = 0x233;

    /**
     * 修改发票地址的位置
     */
    private int mChangeAddressPosition;

    /**
     * 默认收票地址的位置，用于选择别的收票地址为默认时把上一次的默认取消
     */
    private int mDefalutAddressPosition = -1;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mIsForResult = getIntent().getBooleanExtra(ConstansUtil.FOR_REQUEST_RESULT, false);

        mRecyclerView.setLoadingMoreEnable(false);
        mRecyclerView.setEmptyView(R.layout.no_address_empty_layout);
        findViewById(R.id.accept_ticket_address_add_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(AddAcceptTicketAddressActivity.class, REQUEST_CODE);
            }
        });

    }

    @Override
    protected RecyclerView.LayoutManager createLayouManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    protected void loadData() {
        getOkgo(HttpConstants.getAcceptTicketAddress)
                .execute(new JsonCallback<Base_Class_List_Info<AcceptTicketAddressInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<AcceptTicketAddressInfo> o, Call call, Response response) {
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

    /**
     * 设置是否是默认的收票地址
     *
     * @param ticketId  要设置的收票地址id
     * @param isDefault 1：默认    0：取消默认
     */
    private void setDefaultAddress(final String ticketId, final String isDefault) {
        showLoadingDialog();
        getOkgo(HttpConstants.setDefaultAcceptTicketAddress)
                .params("ticketId", ticketId)
                .params("isDefault", isDefault)
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> voidBase_class_info, Call call, Response response) {
                        if (isDefault.equals("0")) {
                            notifyLastDefaultAddressChange();
                            mDefalutAddressPosition = -1;
                        } else {
                            notifyDefaultAddressChange(ticketId);
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
                                showFiveToast("设置默认地址失败，请稍后重试");
                            }
                        }
                    }
                });
    }

    /**
     * 把上次的默认地址取消并显示新的默认地址
     *
     * @param ticketId 新的地址的id
     */
    private void notifyDefaultAddressChange(String ticketId) {
        for (int i = 0; i < mCommonAdapter.getData().size(); i++) {
            if (mCommonAdapter.getData().get(i).getTicketId().equals(ticketId)) {
                mCommonAdapter.getData().get(i).setIsDefault("1");
                notifyLastDefaultAddressChange();
                mDefalutAddressPosition = i;
                mCommonAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    /**
     * 把上次的默认地址取消
     */
    private void notifyLastDefaultAddressChange() {
        if (mDefalutAddressPosition != -1) {
            mCommonAdapter.getData().get(mDefalutAddressPosition).setIsDefault("0");
            mCommonAdapter.notifyItemChanged(mDefalutAddressPosition);
        }
    }

    /**
     * 删除收票地址
     *
     * @param ticketId 需要删除的收票地址的id
     */
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

    /**
     * 删除收票地址
     */
    private void notifyAddressDelete(String ticketId) {
        int position = 0;
        for (int i = 0, size = mCommonAdapter.getData().size(); i < size; i++) {
            if (mCommonAdapter.getData().get(i).getTicketId().equals(ticketId)) {
                position = i;
                if (mCommonAdapter.getData().get(i).getIsDefault().equals("1")) {
                    mDefalutAddressPosition = -1;
                }
                break;
            }
        }
        mCommonAdapter.getData().remove(position);
        mCommonAdapter.notifyDataSetChanged();
        if (mCommonAdapter.getData().isEmpty()) {
            mRecyclerView.showEmpty(null);
        }
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_accept_ticket_address_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, final AcceptTicketAddressInfo acceptTicketAddressInfo, final int position) {

        //如果发票类型是电子发票则把手机号隐藏
        if (acceptTicketAddressInfo.getType().equals("电子")) {
            holder.getView(R.id.accept_ticket_address_telephone).setVisibility(View.GONE);
        } else {
            holder.setText(R.id.accept_ticket_address_telephone, acceptTicketAddressInfo.getAcceptPersonTelephone());
        }

        //如果发票类型是电子发票则地址显示邮箱地址，否则显示物理地址
        String address = acceptTicketAddressInfo.getType().equals("电子") ? acceptTicketAddressInfo.getAcceptPersonEmail() :
                acceptTicketAddressInfo.getAcceptArea() + acceptTicketAddressInfo.getAcceptAddress();

        //记录默认收货地址的位置
        if (mDefalutAddressPosition == -1 && acceptTicketAddressInfo.getIsDefault().equals("1")) {
            mDefalutAddressPosition = position;
        }

        holder.setText(R.id.accept_ticket_address_name, acceptTicketAddressInfo.getAcceptPersonName())
                .setText(R.id.accept_ticket_address_type, acceptTicketAddressInfo.getType())
                .setText(R.id.accept_ticket_address_address, address)
                .setCheckboxCheck(R.id.accept_ticket_address_set_default, acceptTicketAddressInfo.getIsDefault().equals("1"));

        //根据选择状态来设置默认的收票地址
        ((CheckBox) holder.getView(R.id.accept_ticket_address_set_default)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mRecyclerView.getRecyclerView().getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                        && !mRecyclerView.getRecyclerView().isComputingLayout()) {
                    if (isChecked) {
                        setDefaultAddress(acceptTicketAddressInfo.getTicketId(), "1");
                    } else if (acceptTicketAddressInfo.getTicketId().equals("1")) {
                        setDefaultAddress(acceptTicketAddressInfo.getTicketId(), "0");
                    }
                }
            }
        });

        //修改收票地址
        holder.getView(R.id.accept_ticket_address_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChangeAddressPosition = position;
                startActivityForResult(AddAcceptTicketAddressActivity.class, REQUEST_CODE, ConstansUtil.CHAGNE_ACCEPT_ADDRESS, acceptTicketAddressInfo);
            }
        });

        holder.getView(R.id.accept_ticket_address_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAddress(acceptTicketAddressInfo.getTicketId());
            }
        });

        //如果是确认订单跳转过来的，则点击发票后把发票传回去作为收票地址
        if (mIsForResult) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(ConstansUtil.ACCEPT_ADDRESS_INFO, acceptTicketAddressInfo);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            AcceptTicketAddressInfo addressInfo;
            if ((addressInfo = (AcceptTicketAddressInfo) data.getSerializableExtra(ConstansUtil.ADD_ACCEPT_ADDRESS)) != null) {
                mCommonAdapter.addData(addressInfo);        //如果是新增发票则返回后把新增的发票添加
            } else if ((addressInfo = (AcceptTicketAddressInfo) data.getSerializableExtra(ConstansUtil.CHAGNE_ACCEPT_ADDRESS)) != null) {
                mCommonAdapter.notifyDataChange(mChangeAddressPosition, addressInfo);       //如果是编辑发票则更新编辑后的发票
            }
        }
    }

}
