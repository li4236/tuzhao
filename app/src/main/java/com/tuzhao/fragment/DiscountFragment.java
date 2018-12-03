package com.tuzhao.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.view.View;

import com.tuzhao.R;
import com.tuzhao.adapter.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.fragment.base.BaseRefreshFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Discount_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/8/17.
 */
public class DiscountFragment extends BaseRefreshFragment<Discount_Info> {

    private View mView;

    private ConstraintLayout mConstraintLayout;

    private int mDiscountType;

    private int mIsUsable;

    /**
     * 停车订单的金额
     */
    private double mOrderFee;

    public static DiscountFragment getInstance(int discountType, int isUsable) {
        DiscountFragment discountFragment = new DiscountFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstansUtil.TYPE, discountType);
        bundle.putInt(ConstansUtil.INTENT_MESSAGE, isUsable);
        discountFragment.setArguments(bundle);
        discountFragment.setTAG(discountFragment.getTag() + " discountType:" + discountType + " isUsable" + isUsable);
        return discountFragment;
    }

    public static DiscountFragment getInstance(int discountType, int isUsable, ArrayList<Discount_Info> discountInfos, double orderFee) {
        DiscountFragment discountFragment = new DiscountFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstansUtil.TYPE, discountType);
        bundle.putInt(ConstansUtil.INTENT_MESSAGE, isUsable);
        bundle.putParcelableArrayList(ConstansUtil.DISCOUNT_LIST, discountInfos);
        bundle.putDouble(ConstansUtil.ORDER_FEE, orderFee);
        discountFragment.setArguments(bundle);
        discountFragment.setTAG(discountFragment.getTag() + " discountType:" + discountType + " isUsable" + isUsable);
        return discountFragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        mView = view;
        mConstraintLayout = view.findViewById(R.id.loading_dialog);
        mCommonAdapter.setPlaceholderHeaderView(18);
    }

    @Override
    protected void initData() {
        if (getArguments() != null) {
            mDiscountType = getArguments().getInt(ConstansUtil.TYPE, 3);
            mIsUsable = getArguments().getInt(ConstansUtil.INTENT_MESSAGE, 1);
            mOrderFee = getArguments().getDouble(ConstansUtil.ORDER_FEE, -1);
            mStartItme = getArguments().getInt(ConstansUtil.START_ITME, 0);
            ArrayList<Discount_Info> list = getArguments().getParcelableArrayList(ConstansUtil.DISCOUNT_LIST);
            mCommonAdapter.setNewArrayData(list);

            if (list == null) {
                showDialog();
                loadData();
            } else if (mOrderFee != -1) {
                //从停车订单跳过来的则显示不选择优惠券
                showView(mView.findViewById(R.id.not_use_discount));
                mView.findViewById(R.id.not_use_discount).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        Bundle data = new Bundle();
                        data.putParcelable(ConstansUtil.CHOOSE_DISCOUNT, null);
                        intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, data);
                        if (getActivity() != null) {
                            getActivity().setResult(Activity.RESULT_OK, intent);
                            getActivity().finish();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!mCommonAdapter.getData().isEmpty()) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                bundle.putParcelableArrayList(ConstansUtil.DISCOUNT_LIST, (ArrayList<? extends Parcelable>) mCommonAdapter.getData());
                bundle.putInt(ConstansUtil.START_ITME, mStartItme);
            }
        }
        dissmissDialog();
    }

    private void showDialog() {
        if (mConstraintLayout.getVisibility() != View.VISIBLE) {
            mConstraintLayout.setVisibility(View.VISIBLE);
        }
    }

    private void dissmissDialog() {
        if (mConstraintLayout.getVisibility() != View.INVISIBLE) {
            mConstraintLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void loadData() {
        getOkgos(HttpConstants.getUserDiscount)
                .params("discountType", mDiscountType)
                .params("isUsable", mIsUsable)
                .execute(new JsonCallback<Base_Class_List_Info<Discount_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<Discount_Info> o, Call call, Response response) {
                        loadDataSuccess(o);
                        dissmissDialog();   //用的不是父类的dialog，需要自己调用
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dissmissDialog();
                        loadDataFail(e, new LoadFailCallback() {
                            @Override
                            public void onLoadFail(Exception e) {
                                if (mCommonAdapter.getDataSize() != 0 && e.getMessage().equals("101")) {
                                    showFiveToast("没有更多数据了哦");
                                }
                            }
                        });
                    }
                });
    }

    private void deleteDiscount(final Discount_Info discount_info) {
        getOkGo(HttpConstants.deleteUserDiscount)
                .params("discount_id", discount_info.getId())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        mCommonAdapter.notifyRemoveData(discount_info);
                        if (mCommonAdapter.getData().isEmpty()) {
                            mRecyclerView.showEmpty(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mStartItme = 0;
                                    showLoadingDialog();
                                    loadData();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }

                });
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_discount_refactory_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, final Discount_Info discount_info, int position) {
        holder.setText(R.id.discount_price, discount_info.getDiscount())
                .setText(R.id.limit_amount, discount_info.getMin_fee().equals("-1") ? "无门槛使用" : "满" + discount_info.getMin_fee() + "元可用")
                .setText(R.id.discount_type, discount_info.getWhat_type().equals("1") ? "停车劵" : "充电券")
                .setText(R.id.discount_time, discount_info.getEffective_time())
                .showViewOrHide(R.id.discount_iv, !discount_info.getIs_usable().equals("1"));

        if (discount_info.getWhat_type().equals("1")) {
            //停车劵
            if (discount_info.getIs_usable().equals("1")) {
                //可用
                holder.setBackground(R.id.top_divider, R.drawable.solid_y3_tr_5dp_tr_5dp)
                        .setTextColor(R.id.yuan_tv, ConstansUtil.Y3_COLOR)
                        .setTextColor(R.id.discount_price, ConstansUtil.Y3_COLOR)
                        .setTextColor(R.id.limit_amount, ConstansUtil.Y3_COLOR)
                        .setTextColor(R.id.discount_type, ConstansUtil.B1_COLOR);
            }
        } else {
            //充电券
            if (discount_info.getIs_usable().equals("1")) {
                //可用
                holder.setBackground(R.id.top_divider, R.drawable.solid_green5_tl_tr_5dp)
                        .setTextColor(R.id.yuan_tv, ConstansUtil.GREEN5_COLOR)
                        .setTextColor(R.id.discount_price, ConstansUtil.GREEN5_COLOR)
                        .setTextColor(R.id.limit_amount, ConstansUtil.GREEN5_COLOR)
                        .setTextColor(R.id.discount_type, ConstansUtil.GREEN5_COLOR);
            }
        }

        if (discount_info.getIs_usable().equals("2") || discount_info.getIs_usable().equals("3")) {
            //已使用和过期
            holder.setBackground(R.id.top_divider, R.drawable.solid_g6_tl_tr_5dp)
                    .setTextColor(R.id.yuan_tv, ConstansUtil.G6_COLOR)
                    .setTextColor(R.id.discount_price, ConstansUtil.G6_COLOR)
                    .setTextColor(R.id.limit_amount, ConstansUtil.G6_COLOR)
                    .setTextColor(R.id.discount_type, ConstansUtil.G6_COLOR)
                    .showPic(R.id.discount_iv, discount_info.getIs_usable().equals("2") ? R.drawable.ic_used : R.drawable.ic_guoqi);
        }

        if (mOrderFee != -1 && discount_info.getWhat_type().equals("1") && discount_info.getIs_usable().equals("1")) {
            //从停车订单跳转过来，并且是可用的停车优惠券则点击后把选择优惠券传回去
            holder.getView(R.id.discount_cl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOrderFee < Double.valueOf(discount_info.getMin_fee())) {
                        showFiveToast("该优惠券的最低消费金额为" + DateUtil.deleteZero(discount_info.getMin_fee()) + "元哦");
                    } else if (getActivity() != null) {
                        Intent intent = new Intent();
                        intent.putExtra(ConstansUtil.CHOOSE_DISCOUNT, discount_info);
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
            });

        }

        holder.getView(R.id.delete_discount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() != null) {
                    new TipeDialog.Builder(getContext())
                            .setTitle("提示")
                            .setMessage("确定删除该优惠券吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteDiscount(discount_info);
                                }
                            })
                            .create()
                            .show();
                }
            }
        });

    }

    @Override
    protected int resourceId() {
        return R.layout.fragment_discount_layout;
    }

}
