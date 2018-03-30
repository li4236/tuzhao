package com.tuzhao.fragment.discount;

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
import com.tuzhao.fragment.BaseFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Discount_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.publicwidget.swipetoloadlayout.OnLoadMoreListener;
import com.tuzhao.publicwidget.swipetoloadlayout.OnRefreshListener;
import com.tuzhao.publicwidget.swipetoloadlayout.SuperRefreshRecyclerView;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TZL12 on 2017/6/26.
 */

public class UsedFragment extends BaseFragment {

    /**
     * UI
     */
    private View mContentView;
    private SuperRefreshRecyclerView mRecycleview;
    private LinearLayoutManager linearLayoutManager;
    private DiscountAdapter mAdapter;
    private CustomDialog mCustomDialog;

    private LinearLayout linearlayout_nodata;

    private ArrayList<Discount_Info> mDatas = new ArrayList<>();

    private DiscountAdapter.IonSlidingViewClickListener slidingViewClickListener;

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
        mRecycleview = (SuperRefreshRecyclerView) mContentView.findViewById(R.id.id_fragment_allorderlist_layout_recycleview);
        linearLayoutManager = new LinearLayoutManager(mContext);
        mRecycleview.init(linearLayoutManager, new onMyRefresh(), new onMyLoadMore());
        mRecycleview.setRefreshEnabled(true);
        mRecycleview.setLoadingMoreEnable(false);

        slidingViewClickListener = new DiscountAdapter.IonSlidingViewClickListener() {
            @Override
            public void onDeleteBtnCilck(String discount_id, int pos) {
                Log.e("点击了删除","哈哈哈");
                initLoading("删除中...");
                requestDeleteDiscount(discount_id,pos);
            }
        };

        mAdapter = new DiscountAdapter(mContext, mDatas,slidingViewClickListener);
        mRecycleview.setAdapter(mAdapter);
        linearlayout_nodata = (LinearLayout) mContentView.findViewById(R.id.id_fragment_allorderlist_layout_linearlayout_nodata);
        if (mDatas.size()<=0){
            linearlayout_nodata.setVisibility(View.VISIBLE);
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

    private void requestDeleteDiscount(String discount_id, final int pos) {
        OkGo.post(HttpConstants.deleteUserDiscount)
                .tag(mContext)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("discount_id",discount_id)
                .execute(new JsonCallback<Base_Class_Info<Discount_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Discount_Info> datas, Call call, Response response) {
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }

                        mAdapter.removeItem(pos);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
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
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }

                        mDatas.clear();
                        for (Discount_Info info : datas.data) {
                            if (info.getIs_usable().equals("2")) {
                                mDatas.add(info);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        if (mDatas.size()>0){
                            linearlayout_nodata.setVisibility(View.GONE);
                        }else {
                            linearlayout_nodata.setVisibility(View.VISIBLE);
                        }
                        mRecycleview.setRefreshing(false);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        mRecycleview.setRefreshing(false);
                        if (!DensityUtil.isException(mContext, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
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



    private void initLoading(String what) {
        mCustomDialog = new CustomDialog(mContext, what);
        mCustomDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCustomDialog != null) {
            mCustomDialog.cancel();
        }
    }
}
