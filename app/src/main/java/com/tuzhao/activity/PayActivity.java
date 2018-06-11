package com.tuzhao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.okgo.callback.StringCallback;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.application.MyApplication;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.WechatPayParam;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.alipay.OrderInfoUtil2_0;
import com.tuzhao.publicwidget.alipay.PayResult;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.IntentObserable;

import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TZL12 on 2018/2/28.
 */

public class PayActivity extends BaseStatusActivity implements View.OnClickListener {

    private TextView mPayMoney;

    private TextView mPayDescription;

    private android.widget.CheckBox mAlipayCb;

    private android.widget.CheckBox mWechatPayCb;

    private TextView mPayImmediately;

    private String mOrderId;

    private String mCityCode;

    private String mDiscountId;

    private Handler mHandler;

    private Thread mPayThread;

    @Override
    protected int resourceId() {
        return R.layout.activity_pay_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mPayMoney = findViewById(R.id.pay_money);
        mPayDescription = findViewById(R.id.pay_description);
        mAlipayCb = findViewById(R.id.alipay_cb);
        mWechatPayCb = findViewById(R.id.wechat_pay_cb);
        mPayImmediately = findViewById(R.id.pay_immediately);

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
        findViewById(R.id.alipay_cl).setOnClickListener(this);
        findViewById(R.id.wechat_pay_cl).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        mPayMoney.setText(intent.getStringExtra(ConstansUtil.ORDER_FEE));
        mOrderId = intent.getStringExtra(ConstansUtil.PARK_ORDER_ID);
        mCityCode = intent.getStringExtra(ConstansUtil.CITY_CODE);
        mDiscountId = intent.getStringExtra(ConstansUtil.CHOOSE_DISCOUNT);

        initHandler();
    }

    @NonNull
    @Override
    protected String title() {
        return "订单支付";
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
                mPayImmediately.setClickable(false);
                if (mAlipayCb.isChecked()) {
                    alipayPay();
                } else {
                    wechatPay();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPayThread != null) {
            mPayThread.interrupt();
        }
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
                .params("order_id", mOrderId)
                .params("citycode", mCityCode)
                .params("discount_id", mDiscountId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(final String s, Call call, Response response) {
                        Runnable payRunnable = new Runnable() {

                            @Override
                            public void run() {
                                PayTask alipay = new PayTask(PayActivity.this);
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

                        }
                    }
                });
    }

    private void wechatPay() {
        getOkGo(HttpConstants.getWechatPayOrder)
                .params("orderId", mOrderId)
                .params("cityCode", mCityCode)
                .params("discountId", mDiscountId)
                .execute(new JsonCallback<Base_Class_Info<WechatPayParam>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<WechatPayParam> o, Call call, Response response) {
                        IWXAPI iwxapi = WXAPIFactory.createWXAPI(PayActivity.this, null);
                        iwxapi.registerApp(ConstansUtil.WECHAT_APP_ID);

                        PayReq payReq = new PayReq();
                        payReq.appId = ConstansUtil.WECHAT_APP_ID;
                        payReq.partnerId = "1499403182";
                        payReq.packageValue = "Sign=WXPay";
                        payReq.prepayId = o.data.getPrePayId();
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
                        //如果消息是支付成功 则SDK正常运行，将随该消息附带的msg.obj强转回map中，建立新的payresult支付结果
                                PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                        /**
                         对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                         */
                        // 同步返回需要验证的信息，从支付结果中取到resultinfo
                        String resultStatus = payResult.getResultStatus();
                        // 判断resultStatus 为9000则代表支付成功
                        if (TextUtils.equals(resultStatus, "9000")) {
                            IntentObserable.dispatch(new Intent(ConstansUtil.PAY_SUCCESS));
                            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                            Toast.makeText(MyApplication.getInstance(), "支付成功", Toast.LENGTH_SHORT).show();
                            finish();
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
                        break;
                    }
                    default:
                        break;
                }
                return true;
            }
        });
    }

}
