package com.tuzhao.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tianzhili.www.myselfsdk.okgo.request.BaseRequest;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.mine.AuditParkSpaceActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.WechatPayParam;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.alipay.OrderInfoUtil2_0;
import com.tuzhao.publicwidget.alipay.PayResult;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DataUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/2/28.
 */

public class PayActivity extends BaseStatusActivity implements View.OnClickListener, IntentObserver {

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

    private IWXAPI mIWXAPI;

    /**
     * 0(停车订单)  1(车锁押金) 2(购买月卡) 3(长租停车订单)
     */
    private String mPayType;

    private String mParkSpaceId;

    private String mAllotedPeriod;

    private String mOrderNumber;

    private long mExpireTime;

    private Handler mCountdownHandler;

    private TextView mCountdownTv;

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
        mPayMoney.setText(intent.getStringExtra(ConstansUtil.PAY_MONEY));
        mPayType = intent.getStringExtra(ConstansUtil.PAY_TYPE);
        mCityCode = intent.getStringExtra(ConstansUtil.CITY_CODE);
        switch (mPayType) {
            case "0":
                mOrderId = intent.getStringExtra(ConstansUtil.PARK_ORDER_ID);
                mDiscountId = intent.getStringExtra(ConstansUtil.CHOOSE_DISCOUNT);
                mPayDescription.setText("停车费用");
                break;
            case "1":
                mPayDescription.setText("车锁押金");
                mParkSpaceId = intent.getStringExtra(ConstansUtil.PARK_SPACE_ID);
                break;
            case "2":
                mPayDescription.setText("购买月卡");
                mAllotedPeriod = intent.getStringExtra(ConstansUtil.ALLOTED_PERIOD);
                break;
            case "3":
                mPayDescription.setText("长租费用");
                mOrderNumber = intent.getStringExtra(ConstansUtil.ORDER_NUMBER);
                mExpireTime = Long.valueOf(intent.getStringExtra(ConstansUtil.TIME));
                startCountdown();
                break;
        }

        IntentObserable.registerObserver(this);
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
                if (mAlipayCb.isChecked()) {
                    switch (mPayType) {
                        case "0":
                            alipayPay(HttpConstants.alipayApplyOrder, "orderId", mOrderId, "cityCode", mCityCode, "discountId", mDiscountId);
                            break;
                        case "1":
                            alipayPay(HttpConstants.getAlipayLockDepositInfo, "parkSpaceId", mParkSpaceId, "cityCode", mCityCode);
                            break;
                        case "2":
                            alipayPay(HttpConstants.getAlipayBuyMonthlyCardInfo, "allotedPeriod", mAllotedPeriod, "cityCode", mCityCode);
                            break;
                        case "3":
                            alipayPay(HttpConstants.getAlipayLongRentOrderInfo, "orderNumber", mOrderNumber);
                            break;
                    }
                } else {
                    switch (mPayType) {
                        case "0":
                            wechatPay(HttpConstants.getWechatPayOrder, "orderId", mOrderId, "cityCode", mCityCode, "discountId", mDiscountId);
                            break;
                        case "1":
                            wechatPay(HttpConstants.getWechatLockDepositInfo, "parkSpaceId", mParkSpaceId, "cityCode", mCityCode);
                            break;
                        case "2":
                            wechatPay(HttpConstants.getWechatBuyMonthlyCardInfo, "allotedPeriod", mAllotedPeriod, "cityCode", mCityCode);
                            break;
                        case "3":
                            wechatPay(HttpConstants.getWechatLongRentOrderInfo, "orderNumber", mOrderNumber);
                    }
                }
                mPayImmediately.setClickable(false);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mPayImmediately.isClickable()) {
            if (mExpireTime != 0 && mExpireTime <= SystemClock.elapsedRealtime()) {
                handleOrderExpire();
            } else {
                //某些手机会有微信双开，点击微信支付会弹出选择哪个微信支付的对话框，如果此时用户没有选择而是把对话框关闭了，则会导致支付按钮不可点击
                mPayImmediately.setClickable(true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
        if (mPayThread != null) {
            mPayThread.interrupt();
        }
        if (mCountdownHandler != null) {
            mCountdownHandler.removeCallbacksAndMessages(null);
        }
        if (mIWXAPI != null) {
            //解决内存泄漏问题
            mIWXAPI.detach();
            mIWXAPI = null;
            DataUtil.cleanWXLeak();
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

    private void alipayPay(String url, String... params) {
        showLoadingDialog();
        getOkgos(url, params)
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> o, Call call, Response response) {
                        initHandler();
                        startAlipay(o.data);
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mPayImmediately.setClickable(true);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    if ("3".equals(mPayType)) {
                                        showFiveToast("客户端异常，请稍后重试");
                                        finish();
                                        break;
                                    }
                                case "102":
                                    if ("3".equals(mPayType)) {
                                        showFiveToast("该订单已过期，请重新选择");
                                        IntentObserable.dispatch(ConstansUtil.RESET_LONG_RENT_ORDER);
                                    } else {
                                        showFiveToast("客户端异常，请稍后重试");
                                    }
                                    finish();
                                    break;
                                case "103":
                                    if ("3".equals(mPayType)) {
                                        showFiveToast("该车场的长租时间已修改了哎，请重新选择");
                                        notifyLongRentAgain();
                                        finish();
                                        break;
                                    }
                                case "104":
                                    if ("3".equals(mPayType)) {
                                        showFiveToast("订单价格已经改变了，请重新选择");
                                        notifyLongRentAgain();
                                        finish();
                                        break;
                                    }
                                default:
                                    showFiveToast(e.getMessage());
                                    break;
                            }
                        }
                    }
                });
    }

    private void wechatPay(String url, String... params) {
        showLoadingDialog();
        getOkgos(url, params)
                .execute(new JsonCallback<Base_Class_Info<WechatPayParam>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<WechatPayParam> o, Call call, Response response) {
                        startWechatPay(o.data);
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mPayImmediately.setClickable(true);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    if ("3".equals(mPayType)) {
                                        showFiveToast("客户端异常，请稍后重试");
                                        finish();
                                        break;
                                    }
                                case "102":
                                    if ("3".equals(mPayType)) {
                                        showFiveToast("该订单已过期，请重新选择");
                                        IntentObserable.dispatch(ConstansUtil.RESET_LONG_RENT_ORDER);
                                    } else {
                                        showFiveToast("客户端异常，请稍后重试");
                                    }
                                    finish();
                                    break;
                                case "103":
                                    if ("3".equals(mPayType)) {
                                        showFiveToast("该车场的长租时间已修改了哎，请重新选择");
                                        notifyLongRentAgain();
                                        finish();
                                        break;
                                    }
                                case "104":
                                    if ("3".equals(mPayType)) {
                                        showFiveToast("订单价格已经改变了，请重新选择");
                                        notifyLongRentAgain();
                                        finish();
                                        break;
                                    }
                                default:
                                    showFiveToast(e.getMessage());
                                    break;
                            }
                        }
                    }
                });
    }

    protected BaseRequest getOkgos(String url, String... params) {
        BaseRequest baseRequest = getOkGo(url);
        for (int i = 0; i < params.length; i += 2) {
            baseRequest.params(params[i], params[i + 1]);
        }
        return baseRequest;
    }

    private void startAlipay(final String orderInfo) {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(PayActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);     //调用支付接口，获取支付结果

                //把支付结果发送到主线程的Handler进行处理
                Message msg = Message.obtain();
                msg.what = OrderInfoUtil2_0.SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        if (mPayThread != null) {
            mPayThread.interrupt();
            mPayThread = null;
        }
        mPayThread = new Thread(payRunnable);
        mPayThread.start();
    }

    private void startWechatPay(WechatPayParam wechatPayParam) {
        mIWXAPI = WXAPIFactory.createWXAPI(PayActivity.this, null);
        mIWXAPI.registerApp(ConstansUtil.WECHAT_APP_ID);

        PayReq payReq = new PayReq();
        payReq.appId = ConstansUtil.WECHAT_APP_ID;
        payReq.partnerId = wechatPayParam.getPartnerId();
        payReq.packageValue = "Sign=WXPay";
        payReq.prepayId = wechatPayParam.getPrepayId();
        payReq.nonceStr = wechatPayParam.getNonceStr();
        payReq.timeStamp = wechatPayParam.getTimeStamp();
        payReq.sign = wechatPayParam.getSign();
        mIWXAPI.sendReq(payReq);

        if (!payReq.checkArgs()) {
            showFiveToast("微信支付暂不可用，请使用支付宝支付");
        }
    }

    private void initHandler() {
        if (mHandler == null) {
            mHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    switch (msg.what) {
                        case OrderInfoUtil2_0.SDK_PAY_FLAG: {
                            @SuppressWarnings("unchecked")
                            //如果消息是支付成功 则SDK正常运行，将随该消息附带的msg.obj强转回map中，建立新的payresult支付结果
                                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);

                            //对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
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

    private void notifyLongRentAgain() {
        IntentObserable.dispatch(ConstansUtil.LONG_RENT_AGAIN);
    }

    private void showPaySuccessDialog() {
        new TipeDialog.Builder(this)
                .setTitle("提示")
                .setMessage("支付成功！")
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //通知审核中的车位已经缴纳押金
                        Intent payIntent = new Intent(ConstansUtil.PAY_DEPOSIT_SUM_SUCCESS);
                        payIntent.putExtra(ConstansUtil.PARK_SPACE_ID, mParkSpaceId);
                        IntentObserable.dispatch(payIntent);
                        startActivity(AuditParkSpaceActivity.class);
                        finish();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //通知审核中的车位已经缴纳押金
                        Intent payIntent = new Intent(ConstansUtil.PAY_DEPOSIT_SUM_SUCCESS);
                        payIntent.putExtra(ConstansUtil.PARK_SPACE_ID, mParkSpaceId);
                        IntentObserable.dispatch(payIntent);
                        startActivity(AuditParkSpaceActivity.class);
                        finish();
                    }
                })
                .create().show();
    }

    private void startCountdown() {
        if (mExpireTime > SystemClock.elapsedRealtime()) {
            ViewStub viewStub = findViewById(R.id.countdown_vs);
            mCountdownTv = viewStub.inflate().findViewById(R.id.countdown_tv);
            viewStub.setVisibility(View.VISIBLE);

            mCountdownHandler = new Handler(Looper.getMainLooper());
            mCountdownHandler.post(new Runnable() {
                @Override
                public void run() {
                    int remainTime = (int) ((mExpireTime - SystemClock.elapsedRealtime()) / 1000);
                    if (remainTime > 0) {
                        StringBuilder stringBuilder = new StringBuilder();
                        int time = remainTime / 60;
                        stringBuilder.append(DateUtil.thanTen(time));
                        stringBuilder.append(":");

                        time = remainTime - time * 60;
                        stringBuilder.append(DateUtil.thanTen(time));
                        mCountdownTv.setText(stringBuilder.toString());

                        mCountdownHandler.postDelayed(this, 1000);
                    } else {
                        if (mPayImmediately.isClickable()) {
                            //没有正在支付则关闭页面
                            handleOrderExpire();
                        }
                    }

                }
            });
        } else {
            handleOrderExpire();
        }
    }

    /**
     * 订单过时未支付,则关闭页面
     */
    private void handleOrderExpire() {
        showFiveToast("该订单已过期，请重新预定");
        mPayImmediately.setClickable(false);
        finish();
    }

    @Override
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            Log.e(TAG, "onReceive: " + intent.getAction());
            switch (intent.getAction()) {
                case ConstansUtil.PAY_SUCCESS:
                    if ("1".equals(mPayType)) {
                        showPaySuccessDialog();
                    } else {
                        if ("3".equals(mPayType)) {
                            if (mExpireTime <= SystemClock.elapsedRealtime()) {
                                mExpireTime = SystemClock.elapsedRealtime() + 10000;
                            }
                        }
                        finish();
                    }
                    break;
                case ConstansUtil.PAY_CANCEL:
                    showFiveToast("支付取消");
                    break;
                case ConstansUtil.PAY_ERROR:
                    if (intent.getStringExtra(ConstansUtil.INTENT_MESSAGE) != null) {
                        showFiveToast(intent.getStringExtra(ConstansUtil.INTENT_MESSAGE));
                    } else {
                        showFiveToast("支付失败");
                    }
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (Objects.equals(mPayType, "1")) {
            startActivity(AuditParkSpaceActivity.class);
            finish();
        } else {
            super.onBackPressed();
        }
    }

}
