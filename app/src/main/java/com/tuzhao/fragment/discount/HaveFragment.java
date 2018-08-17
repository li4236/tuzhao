package com.tuzhao.fragment.discount;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.adapter.DiscountAdapter;
import com.tuzhao.fragment.base.BaseFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Discount_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.TimeManager;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.publicwidget.swipetoloadlayout.OnLoadMoreListener;
import com.tuzhao.publicwidget.swipetoloadlayout.OnRefreshListener;
import com.tuzhao.publicwidget.swipetoloadlayout.SuperRefreshRecyclerView;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TZL12 on 2017/6/26.
 */

public class HaveFragment extends BaseFragment {

    /**
     * UI
     */
    private View mContentView;
    private SuperRefreshRecyclerView mRecycleview;
    private LinearLayoutManager linearLayoutManager;
    private DiscountAdapter mAdapter;
    private LoadingDialog mLoadingDialog;
    private DateUtil dateUtil = new DateUtil();

    private LinearLayout linearlayout_nodata;

    private ArrayList<Discount_Info> mDatas = new ArrayList<>();

    private DiscountAdapter.IonSlidingViewClickListener slidingViewClickListener;

    private double mOrderFee;

    /**
     * 页面相关
     */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_allorderlist_layout, container, false);

            initData();//初始化数据
            initView();//初始化控件
            initEvent();//初始化事件
        }
        return mContentView;
    }

    private void initView() {
        mRecycleview = mContentView.findViewById(R.id.base_srrv);
        linearLayoutManager = new LinearLayoutManager(mContext);
        mRecycleview.init(linearLayoutManager, new onMyRefresh(), new onMyLoadMore());
        mRecycleview.setRefreshEnabled(true);
        mRecycleview.setLoadingMoreEnable(false);

        slidingViewClickListener = new DiscountAdapter.IonSlidingViewClickListener() {
            @Override
            public void onDeleteBtnCilck(String discount_id, int pos) {
                Log.e("点击了删除", "哈哈哈");
                initLoading("删除中...");
                requestDeleteDiscount(discount_id, pos);
            }
        };

        mAdapter = new DiscountAdapter(mContext, mDatas, slidingViewClickListener);
        mRecycleview.setAdapter(mAdapter);
        linearlayout_nodata = mContentView.findViewById(R.id.id_fragment_allorderlist_layout_linearlayout_nodata);
        if (mDatas.size() <= 0) {
            linearlayout_nodata.setVisibility(View.VISIBLE);
        }

        if (getArguments() != null && getArguments().getDouble(ConstansUtil.ORDER_FEE, -1) != -1) {
            mAdapter.setOnItemClickListener(new DiscountAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Discount_Info discountInfo) {
                    if (mOrderFee < Double.valueOf(discountInfo.getMin_fee())) {
                        MyToast.showToast(requireContext(), "该优惠券的最低消费金额为" + DateUtil.deleteZero(discountInfo.getMin_fee()) + "元哦", 5);
                    } else if (getActivity() != null) {
                        Intent intent = new Intent();
                        Bundle data = new Bundle();
                        data.putParcelable(ConstansUtil.CHOOSE_DISCOUNT, discountInfo);
                        intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, data);
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
                    }
                }
            });

            mContentView.findViewById(R.id.not_use_discount).setVisibility(View.VISIBLE);
            mContentView.findViewById(R.id.not_use_discount).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    Bundle data = new Bundle();
                    data.putParcelable(ConstansUtil.CHOOSE_DISCOUNT, null);
                    intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, data);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                }
            });
        }
    }

    private void initData() {
        Bundle bundle = getArguments();
        mDatas = (ArrayList<Discount_Info>) bundle.getSerializable("discounts");
    }

    private void initEvent() {
    }

    private class onMyRefresh implements OnRefreshListener {
        @Override
        public void onRefresh() {
            //开始下拉刷新
            initLoading("加载中...");
            requestGetUserDiscount();
        }
    }

    private class onMyLoadMore implements OnLoadMoreListener {
        @Override
        public void onLoadMore() {
            //开始上拉加载更多数据
        }
    }

    private void requestGetUserDiscount() {
        OkGo.post(HttpConstants.getUserDiscount)
                .tag(mContext)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .execute(new JsonCallback<Base_Class_List_Info<Discount_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<Discount_Info> datas, Call call, Response response) {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }

                        mDatas.clear();
                        String endtime;
                        for (Discount_Info info : datas.data) {
                            if (info.getIs_usable().equals("1")) {
                                endtime = info.getEffective_time().substring(info.getEffective_time().indexOf(" - ") + 3, info.getEffective_time().length());
                                final boolean isnotlater = dateUtil.compareTwoTime(TimeManager.getInstance().getNowTime(false, false), endtime, false);
                                if (isnotlater) {
                                    //未过期
                                    mDatas.add(info);
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        if (mDatas.size() > 0) {
                            linearlayout_nodata.setVisibility(View.GONE);
                        } else {
                            linearlayout_nodata.setVisibility(View.VISIBLE);
                        }
                        mRecycleview.setRefreshing(false);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        mRecycleview.setRefreshing(false);
                        if (!DensityUtil.isException(mContext, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
                                    MyToast.showToast(mContext, "没有数据", 5);
                                    mDatas.clear();
                                    mAdapter.notifyDataSetChanged();
                                    linearlayout_nodata.setVisibility(View.VISIBLE);
                                    break;
                                case 901:
                                    MyToast.showToast(mContext, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });

    }

    private void requestDeleteDiscount(String discount_id, final int pos) {
        OkGo.post(HttpConstants.deleteUserDiscount)
                .tag(mContext)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("discount_id", discount_id)
                .execute(new JsonCallback<Base_Class_Info<Discount_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Discount_Info> datas, Call call, Response response) {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        mAdapter.removeItem(pos);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }

                        if (!DensityUtil.isException(mContext, e)) {
                            Log.e("TAG", "请求失败， 信息为：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
//                                    mDatas.clear();
//                                    mAdapter.notifyDataSetChanged();
//                                    linearlayout_nodata.setVisibility(View.VISIBLE);
                                    break;
                                case 901:
                                    MyToast.showToast(mContext, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });

    }

    private void initLoading(String what) {
        mLoadingDialog = new LoadingDialog(mContext, what);
        mLoadingDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
    }
}
