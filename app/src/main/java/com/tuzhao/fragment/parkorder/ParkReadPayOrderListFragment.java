package com.tuzhao.fragment.parkorder;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.okgo.callback.StringCallback;
import com.tuzhao.R;
import com.tuzhao.adapter.ParkOrderAdapter;
import com.tuzhao.application.MyApplication;
import com.tuzhao.fragment.BaseFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.alipay.AuthResult;
import com.tuzhao.publicwidget.alipay.PayResult;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.publicwidget.swipetoloadlayout.ChangeScrollStateCallback;
import com.tuzhao.publicwidget.swipetoloadlayout.OnLoadMoreListener;
import com.tuzhao.publicwidget.swipetoloadlayout.OnRefreshListener;
import com.tuzhao.publicwidget.swipetoloadlayout.SuperRefreshRecyclerView;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

import static com.tuzhao.publicwidget.alipay.OrderInfoUtil2_0.SDK_AUTH_FLAG;
import static com.tuzhao.publicwidget.alipay.OrderInfoUtil2_0.SDK_PAY_FLAG;

/**
 * Created by TZL12 on 2017/6/26.
 */

public class ParkReadPayOrderListFragment extends BaseFragment {

    /**
     * UI
     */
    private View mContentView;
    private SuperRefreshRecyclerView mRecycleview;
    private ParkOrderAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayout linearlayout_nodata;

    /**
     * 页面相关
     */
    private List<ParkOrderInfo> mOrdersData = new ArrayList<>();
    private boolean isFirstIn = true;
    private int mLoadingtimes = 0;
    /**
     * 支付宝支付完成的回调处理
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    //如果消息是支付成功 则SDK正常运行，将随该消息附带的msg.obj强转回map中，建立新的payresult支付结果
                            MessageHolder messageHolder = (MessageHolder) msg.obj;
                    PayResult payResult = new PayResult(messageHolder.result);
                    Log.e("TAG", "handleMessage: ");
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息，从支付结果中取到resultinfo
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        requetFinishOrder(messageHolder.position);
                        Toast.makeText(MyApplication.getInstance(), "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Log.e("支付失败", payResult.getResult() + "是啥");
                        Toast.makeText(MyApplication.getInstance(), "支付失败" + payResult.getResult() + resultInfo, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    //如果消息是授权成功 则SDK已经确认
                            AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Toast.makeText(MyApplication.getInstance(), "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(MyApplication.getInstance(), "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);//支付宝沙箱环境测试
        mContext = getActivity();
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_allorderlist_layout, container, false);
            initView();//初始化控件
            initData();//初始化数据
            initEvent();//初始化事件
        }
        return mContentView;
    }

    private void initView() {
        linearLayoutManager = new LinearLayoutManager(mContext);
        mRecycleview = mContentView.findViewById(R.id.id_fragment_allorderlist_layout_recycleview);
        mRecycleview.init(linearLayoutManager, new onMyRefresh(), new onMyLoadMore());
        mRecycleview.setRefreshEnabled(true);
        mRecycleview.setLoadingMoreEnable(true);
        mRecycleview.setChangeScrollStateCallback(new ChangeScrollStateCallback() {
            @Override
            public void change(int c) {
                switch (c) {
                    case 2:
                        Glide.with(mContext).pauseRequests();
                        break;
                    case 0:
                        Glide.with(mContext).resumeRequests();
                        break;
                    case 1:
                        Glide.with(mContext).resumeRequests();
                        break;
                }
            }
        });
        mAdapter = new ParkOrderAdapter(mContext, mOrdersData);
        mAdapter.setOnItemOrderToPay(new ParkOrderAdapter.OnItemOrderToPay() {
            @Override
            public void onItemOrderToPay(int position) {
                payV2(mContext, mOrdersData.get(position).getId(), mOrdersData.get(position).getCitycode(), position);
            }
        });
        mRecycleview.setAdapter(mAdapter);
        linearlayout_nodata = mContentView.findViewById(R.id.id_fragment_allorderlist_layout_linearlayout_nodata);
    }

    private void initData() {
        requestGetAllOrders(null, null);
    }

    private void initEvent() {
        if (getActivity() != null) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, 0x111);
        }
    }

    private class onMyRefresh implements OnRefreshListener {
        @Override
        public void onRefresh() {
            //开始下拉刷新
            mLoadingtimes = 0;
            requestGetAllOrders(null, null);
        }
    }

    private class onMyLoadMore implements OnLoadMoreListener {
        @Override
        public void onLoadMore() {
            //开始上拉加载更多数据
            mLoadingtimes++;
            requestGetAllOrders((mLoadingtimes * 10) + "", null);
        }
    }

    private void requestGetAllOrders(String startItem, String pageSize) {
        OkGo.post(HttpConstants.getKindParkOrder)
                .tag(mContext)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("user_confim", DensityUtil.MD5code(UserManager.getInstance().getUserInfo().getSerect_code() + UserManager.getInstance().getUserInfo().getId() + UserManager.getInstance().getUserInfo().getCreate_time()))
                .params("startItem", startItem == null ? "0" : startItem)
                .params("pageSize", pageSize == null ? "10" : pageSize)
                .params("order_status", "3")
                .execute(new JsonCallback<Base_Class_List_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<ParkOrderInfo> datas, Call call, Response response) {

                        if (isFirstIn) {
                            isFirstIn = false;
                            mOrdersData = datas.data;
                            mAdapter = new ParkOrderAdapter(mContext, mOrdersData);
                            mAdapter.setOnItemOrderToPay(new ParkOrderAdapter.OnItemOrderToPay() {
                                @Override
                                public void onItemOrderToPay(int position) {
                                    payV2(mContext, mOrdersData.get(position).getId(), mOrdersData.get(position).getCitycode(), position);
                                }
                            });
                            mRecycleview.setAdapter(mAdapter);
                        }

                        if (mRecycleview.isRefreshing()) {
                            mRecycleview.setRefreshing(false);
                            mOrdersData.clear();
                            mOrdersData.addAll(datas.data);
                            mAdapter.notifyDataSetChanged();
                        }
                        if (mRecycleview.isLoadingMore()) {
                            if (datas.data.size() > 0) {
                                mRecycleview.setLoadingMore(false);
                                mOrdersData.addAll(datas.data);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!DensityUtil.isException(mContext, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + "getCollectionDatas" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 102:
                                    //
                                    if (isFirstIn) {
                                        isFirstIn = false;
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                    }
                                    if (mRecycleview.isRefreshing()) {
                                        mRecycleview.setRefreshing(false);
                                        mOrdersData.clear();
                                        mAdapter.notifyDataSetChanged();
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                        Toast.makeText(mContext, "没有数据哦", Toast.LENGTH_SHORT).show();
                                    }
                                    if (mRecycleview.isLoadingMore()) {
                                        mLoadingtimes--;
                                        mRecycleview.setLoadingMore(false);
                                        Toast.makeText(mContext, "没有更多数据", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 901:
                                    if (isFirstIn) {
                                        isFirstIn = false;
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                    }
                                    if (mRecycleview.isRefreshing()) {
                                        mRecycleview.setRefreshing(false);
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                    }
                                    if (mRecycleview.isLoadingMore()) {
                                        mLoadingtimes--;
                                        mRecycleview.setLoadingMore(false);
                                    }
                                    MyToast.showToast(mContext, "服务器正在维护中", 2);
                                    break;
                            }
                        }
                    }
                });
    }

    /**
     * 支付宝支付业务
     *
     * @param activity
     */
    public void payV2(final Activity activity, String order_Id, String citycode, final int position) {

        /**
         * 来实现支付宝支付功能
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
//        boolean rsa2 = true;
//        //填写支付参数
//        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2, money, title, content, paynowtime, order_numble);
//        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
//        //将诸多参数签名，签名是否正确可以证明参数是否被不法篡改，
//        String sign = OrderInfoUtil2_0.getSign(params, private_key, rsa2);
//        final String orderInfo = orderParam + "&" + sign;
//        Log.e("正在支付1",orderInfo);

        OkGo.post("http://119.23.207.14/public/index.php/tianzhili/alipayApplyOrder")
                .tag("http://119.23.207.14/public/index.php/tianzhili/alipayApplyOrder")
                .params("order_id", order_Id)
                .params("citycode", citycode)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(final String s, Call call, Response response) {
                        Runnable payRunnable = new Runnable() {

                            @Override
                            public void run() {
                                Log.e("正在支付", s);
                                PayTask alipay = new PayTask(activity);
                                Map<String, String> result = alipay.payV2(s, true);
                                Log.e("msp", result.toString());

                                Message msg = new Message();
                                msg.what = SDK_PAY_FLAG;
                                MessageHolder messageHolder = new MessageHolder();
                                messageHolder.result = result;
                                messageHolder.position = position;
                                msg.obj = messageHolder;
                                mHandler.sendMessage(msg);
                            }
                        };

                        Thread payThread = new Thread(payRunnable);
                        payThread.start();
                    }
                });


    }

    private void requetFinishOrder(final int position) {
        //请求改变订单状态，完成订单
        OkGo.post(HttpConstants.finishParkOrder)
                .tag(mContext)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("order_id", mOrdersData.get(position).getId())
                .params("citycode", mOrdersData.get(position).getCitycode())
                .params("pass_code", DensityUtil.MD5code(UserManager.getInstance().getUserInfo().getSerect_code() + "*&*" + UserManager.getInstance().getUserInfo().getCreate_time() + "*&*" + UserManager.getInstance().getUserInfo().getId()))
                .execute(new JsonCallback<Base_Class_Info<User_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<User_Info> info, Call call, Response response) {
                        mOrdersData.remove(position);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    private static class MessageHolder {
        private Map<String, String> result;
        private int position;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if ((isVisibleToUser && isResumed())) {
            Log.e("我是待付款的页面", "我被显示啦setUserVisibleHint");
            requestGetAllOrdersAgain(0 + "", ((mLoadingtimes + 1) * 10) + "");
        } else if (!isVisibleToUser) {
            Log.e("我是待付款的页面", "我被隐藏啦setUserVisibleHint");
        } else if (!isResumed()) {
            Log.e("我是待付款的页面", "我被显示啦setUserVisibleHint");
            if (!isFirstIn) {
                requestGetAllOrdersAgain(0 + "", ((mLoadingtimes + 1) * 10) + "");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            Log.e("我是待付款的页面", "我被显示啦onResume");
            if (!isFirstIn) {
                requestGetAllOrdersAgain(0 + "", ((mLoadingtimes + 1) * 10) + "");
            }
        }
    }

    private void requestGetAllOrdersAgain(String startItem, String pageSize) {
        //获取订单信息，判断是否超时
        OkGo.post(HttpConstants.getKindParkOrder)
                .tag(mContext)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("startItem", startItem == null ? "0" : startItem)
                .params("pageSize", pageSize == null ? "10" : pageSize)
                .params("order_status", "3")
                .execute(new JsonCallback<Base_Class_List_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<ParkOrderInfo> datas, Call call, Response response) {
                        if (!isFirstIn) {
                            int position = linearLayoutManager.findFirstVisibleItemPosition();
                            mOrdersData.clear();
                            mOrdersData.addAll(datas.data);
                            mAdapter.notifyDataSetChanged();

                            if (position < mOrdersData.size()) {
                                View view = mRecycleview.getRecyclerView().getChildAt(position);
                                if (view != null) {
                                    int top = view.getTop();
                                    linearLayoutManager.scrollToPositionWithOffset(position, top);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!DensityUtil.isException(mContext, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + "getCollectionDatas" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 102:
                                    //
                                    mOrdersData.clear();
                                    mAdapter.notifyDataSetChanged();
                                    break;
                            }
                        }
                    }
                });
    }
}
