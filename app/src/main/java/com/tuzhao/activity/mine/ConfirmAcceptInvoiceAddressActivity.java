package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.AcceptTicketAddressInfo;
import com.tuzhao.info.InvoiceInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.customView.CheckBox;
import com.tuzhao.utils.ConstansUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/8/3.
 */
public class ConfirmAcceptInvoiceAddressActivity extends BaseRefreshActivity<AcceptTicketAddressInfo> {

    private TextView mTotalMoney;

    private View mFooterView;

    private List<InvoiceInfo> mInvoiceInfos;

    private String mTotalPrice;

    /**
     * 默认收票地址的位置，用于选择别的收票地址为默认时把上一次的默认取消
     */
    private int mDefalutAddressPosition = -1;

    /**
     * 修改发票地址的位置
     */
    private int mChangeAddressPosition;

    private static final int REQUEST_CODE = 0x233;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        if ((mInvoiceInfos = getIntent().getParcelableArrayListExtra(ConstansUtil.INVOICE_LIST)) == null) {
            showFiveToast("获取订单信息失败，请返回重试");
            finish();
        } else {
            mTotalPrice = getIntent().getStringExtra(ConstansUtil.FOR_REQEUST_RESULT);
        }

        mTotalMoney = findViewById(R.id.confirm_ticket_order_total_money);
        findViewById(R.id.confirm_ticket_order_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommonAdapter.getDataSize() == 0) {
                    showFiveToast("你还没有收票地址哦");
                } else if (mDefalutAddressPosition == -1) {
                    showFiveToast("请选择收票地址");
                } else {
                    applyInvoiceReimbursement();
                }
            }
        });

        mFooterView = getLayoutInflater().inflate(R.layout.add_accept_ticket_address_footer_layout, mRecyclerView.getRecyclerView(), false);
        mFooterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommonAdapter.getData().size() < 10) {
                    startActivityForResult(AddAcceptTicketAddressActivity.class, REQUEST_CODE);
                } else {
                    showFiveToast("最多只能有10个收票地址哦");
                }
            }
        });
        mCommonAdapter.setFooterView(mFooterView);
        mRecyclerView.setEmptyView(null);
    }

    @Override
    protected RecyclerView.LayoutManager createLayouManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    protected void initData() {
        super.initData();
        String price = "开票总额:" + mTotalPrice + "元";
        mTotalMoney.setText(price);
    }

    @Override
    protected void loadData() {
        getOkgos(HttpConstants.getAcceptTicketAddress)
                .execute(new JsonCallback<Base_Class_List_Info<AcceptTicketAddressInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<AcceptTicketAddressInfo> o, Call call, Response response) {
                        loadDataSuccess(o);
                        adjustFooterView();
                    }

                    @Override
                    public void onError(final Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        loadDataFail(e, new LoadFailCallback() {
                            @Override
                            public void onLoadFail(Exception e) {
                                if (e instanceof IllegalStateException) {
                                    switch (e.getMessage()) {
                                        case "101":
                                            showFiveToast("账号异常，请重新登录");
                                            startLogin();
                                            break;
                                        case "102":
                                            showFiveToast("你还没有收票地址哦");
                                            break;
                                    }
                                }
                            }
                        });
                    }
                });
    }

    /**
     * 如果收票地址超过10条则隐藏添加入口，如果不够则显示
     */
    private void adjustFooterView() {
        if (mCommonAdapter.getDataSize() >= 10) {
            if (mCommonAdapter.getFooterView() != null) {
                mCommonAdapter.notifyRemoveFooterView();
            }
        } else {
            if (mCommonAdapter.getFooterView() == null) {
                mCommonAdapter.notifyAddFooterView(mFooterView);
            }
        }
    }

    private void findDefaultAddressPosition() {
        mDefalutAddressPosition = -1;
        for (int i = 0; i < mCommonAdapter.getDataSize(); i++) {
            if (mCommonAdapter.get(i).getIsDefault().equals("1")) {
                mDefalutAddressPosition = i;
                break;
            }
        }
    }

    /**
     * 设置默认的收票地址
     *
     * @param addressInfo 要设置的收票地址
     */
    private void setDefaultAddress(final AcceptTicketAddressInfo addressInfo, final int position) {
        if (mDefalutAddressPosition != -1) {
            //取消上次的默认收票地址
            AcceptTicketAddressInfo acceptTicketAddressInfo = mCommonAdapter.get(mDefalutAddressPosition);
            acceptTicketAddressInfo.setIsDefault("0");
            mCommonAdapter.notifyDataChange(acceptTicketAddressInfo);
        }

        //设置新的默认收票地址，不管请求成功与否
        mDefalutAddressPosition = position;
        if (addressInfo.getIsDefault().equals("0")) {
            //如果是新添加了收票地址的话不用重新更新数据，否则会出错
            addressInfo.setIsDefault("1");
            mCommonAdapter.notifyDataChange(addressInfo);
        }

        getOkgos(HttpConstants.setDefaultAcceptTicketAddress)
                .params("ticketId", addressInfo.getTicketId())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> voidBase_class_info, Call call, Response response) {
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                        }
                    }
                });
    }

    /**
     * 删除收票地址
     *
     * @param addressInfo 需要删除的收票地址
     */
    private void deleteAddress(final AcceptTicketAddressInfo addressInfo) {
        getOkgos(HttpConstants.deleteAcceptTicketAddress)
                .params("ticketId", addressInfo.getTicketId())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> base_class_info, Call call, Response response) {
                        mCommonAdapter.notifyRemoveData(addressInfo);
                        findDefaultAddressPosition();
                        adjustFooterView();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    userError();
                                    break;
                                case "102":
                                    mCommonAdapter.notifyRemoveData(addressInfo);
                                    break;
                                case "103":
                                    showFiveToast("网络请求失败，请稍后再试");
                                    break;
                            }
                        }
                    }
                });
    }

    private void applyInvoiceReimbursement() {
        showLoadingDialog();
        StringBuilder orderId = new StringBuilder();
        for (InvoiceInfo invoiceInfo : mInvoiceInfos) {
            orderId.append(invoiceInfo.getOrderId());
            orderId.append(",");
        }
        orderId.deleteCharAt(orderId.length() - 1);
        getOkGo(HttpConstants.applyInvoiceReimbursement)
                .params("orderId", orderId.toString())
                .params("ticketId", mCommonAdapter.get(mDefalutAddressPosition).getTicketId())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        dismmisLoadingDialog();
                        showFiveToast("开票成功,我们将尽快为您发货");
                        Intent intent = new Intent();
                        intent.putParcelableArrayListExtra(ConstansUtil.FOR_REQEUST_RESULT, (ArrayList<? extends Parcelable>) mInvoiceInfos);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dismmisLoadingDialog();
                        if (!handleException(e)) {
                            if (e instanceof IllegalStateException) {
                                switch (e.getMessage()) {
                                    case "101":
                                        showFiveToast("用户不存在");
                                        break;
                                    case "102":
                                        showFiveToast("发票地址不存在，请重新选择");
                                        break;
                                    case "103":
                                        showFiveToast("订单不存在");
                                        break;
                                    case "104":
                                        showFiveToast("停车订单不存在");
                                        break;
                                    case "105":
                                        showFiveToast("停车订单不可报销");
                                        break;
                                    case "106":
                                        showFiveToast("服务器异常，请稍后再试");
                                        break;
                                }
                            }
                        }
                    }
                });
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_accept_ticket_address_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, final AcceptTicketAddressInfo acceptTicketAddressInfo, final int position) {
        //如果发票类型是电子发票则把手机号隐藏
        if (acceptTicketAddressInfo.getType().equals("电子")) {
            goneView(holder.getView(R.id.accept_ticket_address_telephone));
        } else {
            showView(holder.getView(R.id.accept_ticket_address_telephone));
            holder.setText(R.id.accept_ticket_address_telephone, acceptTicketAddressInfo.getAcceptPersonTelephone());
        }

        //如果发票类型是电子发票则地址显示邮箱地址，否则显示物理地址
        final String address = acceptTicketAddressInfo.getType().equals("电子") ? acceptTicketAddressInfo.getAcceptPersonEmail() :
                acceptTicketAddressInfo.getAcceptArea() + acceptTicketAddressInfo.getAcceptAddress();

        //记录默认收货地址的位置
        if (mDefalutAddressPosition == -1 && acceptTicketAddressInfo.getIsDefault().equals("1")) {
            mDefalutAddressPosition = position;
        }

        final com.tuzhao.publicwidget.customView.CheckBox checkBox = holder.getView(R.id.accept_ticket_address_set_default);
        checkBox.setCheckDrawable(ContextCompat.getDrawable(this, R.drawable.ic_chose));
        checkBox.setNoCheckDrawble(ContextCompat.getDrawable(this, R.drawable.ic_nochose));
        checkBox.setChecked(acceptTicketAddressInfo.getIsDefault().equals("1"));

        holder.setText(R.id.accept_ticket_address_name, acceptTicketAddressInfo.getAcceptPersonName())
                .setText(R.id.accept_ticket_address_type, acceptTicketAddressInfo.getType())
                .setText(R.id.accept_ticket_address_address, address)
                .itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()) {
                    setDefaultAddress(acceptTicketAddressInfo, position);
                }
            }
        });

        checkBox.setOnCheckHandeListener(new CheckBox.OnCheckHandeListener() {
            @Override
            public boolean onCheckChange(boolean isChecked) {
                if (mRecyclerView.getRecyclerView().getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                        && !mRecyclerView.getRecyclerView().isComputingLayout()) {
                    if (!checkBox.isChecked() && isChecked) {
                        setDefaultAddress(acceptTicketAddressInfo, position);
                        return true;
                    }
                }
                return false;
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
                TipeDialog tipeDialog = new TipeDialog.Builder(ConfirmAcceptInvoiceAddressActivity.this)
                        .setTitle("提示")
                        .setMessage("确定删除收票地址吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAddress(acceptTicketAddressInfo);
                            }
                        })
                        .create();
                tipeDialog.show();
            }
        });

    }

    @Override
    protected int resourceId() {
        return R.layout.activity_confirm_accept_invoice_address_layout;
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
            if ((addressInfo = data.getParcelableExtra(ConstansUtil.ADD_ACCEPT_ADDRESS)) != null) {
                if (mDefalutAddressPosition != -1) {
                    mDefalutAddressPosition += 1;                 //新添加收票地址则之前默认收票地址的位置加1
                }
                addressInfo.setIsDefault("1");
                mCommonAdapter.addFirstData(addressInfo);        //如果是新增发票则返回后把新增的发票添加到头部
                setDefaultAddress(addressInfo, 0);
                adjustFooterView();
                mRecyclerView.getRecyclerView().scrollToPosition(0);
            } else if ((addressInfo = data.getParcelableExtra(ConstansUtil.CHAGNE_ACCEPT_ADDRESS)) != null) {
                mCommonAdapter.notifyDataChange(mChangeAddressPosition, addressInfo);       //如果是编辑发票则更新编辑后的发票
            }
        }
    }

}
