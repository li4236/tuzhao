package com.tuzhao.fragment.addParkSpace;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.okgo.callback.StringCallback;
import com.tuzhao.R;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.WechatPayParam;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.alipay.OrderInfoUtil2_0;
import com.tuzhao.publicwidget.alipay.PayResult;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/6/19.
 */
public class DepositPaymentFragment extends BaseStatusFragment implements View.OnClickListener, IntentObserver {

    private TextView mPayMoney;

    private android.widget.CheckBox mAlipayCb;

    private android.widget.CheckBox mWechatPayCb;

    private TextView mPayImmediately;

    private String mCityCode;

    private String mDepositSum;

    private Handler mHandler;

    private Thread mPayThread;

    public DepositPaymentFragment newInstance(String cityCode) {
        DepositPaymentFragment fragment = new DepositPaymentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ConstansUtil.CITY_CODE, cityCode);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int resourceId() {
        return R.layout.fragment_deposit_payment_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mPayMoney = view.findViewById(R.id.pay_money);
        mAlipayCb = view.findViewById(R.id.alipay_cb);
        mWechatPayCb = view.findViewById(R.id.wechat_pay_cb);
        mPayImmediately = view.findViewById(R.id.pay_immediately);

        mAlipayCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setAlipayCheck(isChecked);
            }
        });

        mWechatPayCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setAlipayCheck(!isChecked);
            }
        });

        mPayImmediately.setOnClickListener(this);
        view.findViewById(R.id.alipay_cl).setOnClickListener(this);
        view.findViewById(R.id.wechat_pay_cl).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCityCode = bundle.getString(ConstansUtil.CITY_CODE);
            mDepositSum = bundle.getString(ConstansUtil.DEPOSIT_SUM);
        }

        if (mDepositSum == null) {
            getDepositSum();
        } else {
            String depositSum = mDepositSum + "元";
            mPayMoney.setText(depositSum);
        }

        initHandler();
        IntentObserable.registerObserver(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alipay_cl:
                setAlipayCheck(true);
                break;
            case R.id.wechat_pay_cl:
                setAlipayCheck(false);
                break;
            case R.id.pay_immediately:
                if (mAlipayCb.isChecked()) {
                    alipayPay();
                } else {
                    wechatPay();
                }
                mPayImmediately.setClickable(false);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPayThread != null) {
            mPayThread.interrupt();
        }
        IntentObserable.unregisterObserver(this);
    }

    private void getDepositSum() {
        getOkGo(HttpConstants.getDepositSum)
                .params("cityCode", mCityCode)
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> o, Call call, Response response) {
                        mDepositSum = o.data;
                        String depositSum = o.data + "元";
                        mPayMoney.setText(depositSum);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast("获取押金金额失败，请稍后重试");
                        }
                    }
                });
    }

    private void setAlipayCheck(boolean isCheck) {
        if (isCheck) {
            mAlipayCb.setChecked(true);
            mWechatPayCb.setChecked(false);
        } else {
            mAlipayCb.setChecked(false);
            mWechatPayCb.setChecked(true);
        }
    }

    private void alipayPay() {
        OkGo.post(HttpConstants.alipayApplyOrder)
                .tag(TAG)
                .params("citycode", mCityCode)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(final String s, Call call, Response response) {
                        Runnable payRunnable = new Runnable() {

                            @Override
                            public void run() {
                                PayTask alipay = new PayTask(requireActivity());
                                Map<String, String> result = alipay.payV2(s, true);

                                Message msg = new Message();
                                msg.what = OrderInfoUtil2_0.SDK_PAY_FLAG;
                                msg.obj = result;
                                mHandler.sendMessage(msg);
                            }
                        };

                        if (mPayThread != null) {
                            mPayThread.interrupt();
                        }
                        mPayThread = new Thread(payRunnable);
                        mPayThread.start();

                        mPayImmediately.setClickable(true);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mPayImmediately.setClickable(true);
                        if (!handleException(e)) {
                            showFiveToast("支付失败");
                        }
                    }
                });
    }

    private void wechatPay() {
        getOkGo(HttpConstants.getWechatPayOrder)
                .params("cityCode", mCityCode)
                .execute(new JsonCallback<Base_Class_Info<WechatPayParam>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<WechatPayParam> o, Call call, Response response) {
                        IWXAPI iwxapi = WXAPIFactory.createWXAPI(requireContext(), null);
                        iwxapi.registerApp(ConstansUtil.WECHAT_APP_ID);

                        PayReq payReq = new PayReq();
                        payReq.appId = ConstansUtil.WECHAT_APP_ID;
                        payReq.partnerId = "1499403182";
                        payReq.packageValue = "Sign=WXPay";
                        payReq.prepayId = o.data.getPrepayId();
                        payReq.nonceStr = o.data.getNonceStr();
                        payReq.timeStamp = o.data.getTimeStamp();
                        payReq.sign = o.data.getSign();
                        iwxapi.sendReq(payReq);

                        mPayImmediately.setClickable(true);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mPayImmediately.setClickable(true);
                        if (!handleException(e)) {
                            showFiveToast("获取支付信息失败");
                        }
                    }
                });
    }

    private void initHandler() {
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

                switch (msg.what) {
                    case OrderInfoUtil2_0.SDK_PAY_FLAG: {
                        @SuppressWarnings("unchecked")
                        PayResult payResult = new PayResult((Map<String, String>) msg.obj);

                        // 同步返回需要验证的信息，从支付结果中取到resultinfo
                        String resultStatus = payResult.getResultStatus();
                        // 判断resultStatus 为9000则代表支付成功
                        if (TextUtils.equals(resultStatus, "9000")) {
                            IntentObserable.dispatch(new Intent(ConstansUtil.PAY_SUCCESS));
                            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        } else if (TextUtils.equals(resultStatus, "6001")) {
                            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                            showFiveToast("支付取消");
                        } else if (TextUtils.equals(resultStatus, "6002")) {
                            showFiveToast("网络异常，请稍后再试");
                        } else if (TextUtils.equals(resultStatus, "4000")) {
                            showFiveToast("系统异常，请稍后再试");
                        } else {
                            showFiveToast("支付失败");
                        }
                        mPayImmediately.setClickable(true);
                        break;
                    }
                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ConstansUtil.PAY_SUCCESS:
                    // TODO: 2018/6/19
                    finish();
                    break;
                case ConstansUtil.PAY_CANCEL:
                    showFiveToast("支付取消");
                    mPayImmediately.setClickable(true);
                    break;
                case ConstansUtil.PAY_ERROR:
                    if (intent.getStringExtra(ConstansUtil.INTENT_MESSAGE) != null) {
                        showFiveToast(intent.getStringExtra(ConstansUtil.INTENT_MESSAGE));
                    } else {
                        showFiveToast("支付失败");
                    }
                    mPayImmediately.setClickable(true);
                    break;
            }
        }
    }
}
