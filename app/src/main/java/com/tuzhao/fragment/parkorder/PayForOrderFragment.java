package com.tuzhao.fragment.parkorder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.okgo.callback.StringCallback;
import com.tuzhao.R;
import com.tuzhao.activity.mine.DiscountActivity;
import com.tuzhao.application.MyApplication;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Discount_Info;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.alipay.AuthResult;
import com.tuzhao.publicwidget.alipay.OrderInfoUtil2_0;
import com.tuzhao.publicwidget.alipay.PayResult;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/5/29.
 */

public class PayForOrderFragment extends BaseStatusFragment implements View.OnClickListener, IntentObserver {

    private ParkOrderInfo mParkOrderInfo;

    private TextView mParkTime;

    private TextView mParkTimeDescription;

    private TextView mParkOrderFee;

    private TextView mParkOrderDiscount;

    private TextView mParkOrderCredit;

    private TextView mUserTotalCredit;

    private TextView mShouldPayFee;

    private ArrayList<Discount_Info> mDiscountInfos;

    private Discount_Info mChooseDiscount;

    private Handler mHandler;

    private Thread mPayThread;

    public static PayForOrderFragment newInstance(ParkOrderInfo parkOrderInfo) {
        PayForOrderFragment fragment = new PayForOrderFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstansUtil.PARK_ORDER_INFO, parkOrderInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int resourceId() {
        return R.layout.fragment_pay_for_order_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mParkOrderInfo = (ParkOrderInfo) getArguments().getSerializable(ConstansUtil.PARK_ORDER_INFO);
        }

        mParkTime = view.findViewById(R.id.pay_for_order_time);
        mParkTimeDescription = view.findViewById(R.id.pay_for_order_time_description);
        mParkOrderFee = view.findViewById(R.id.pay_for_order_fee);
        mParkOrderDiscount = view.findViewById(R.id.pay_for_order_discount);
        mParkOrderCredit = view.findViewById(R.id.pay_for_order_credit);
        mUserTotalCredit = view.findViewById(R.id.pay_for_order_total_credit);
        mShouldPayFee = view.findViewById(R.id.pay_for_order_should_pay);

        view.findViewById(R.id.pay_for_order_question).setOnClickListener(this);
        view.findViewById(R.id.pay_for_order_time_iv).setOnClickListener(this);
        view.findViewById(R.id.pay_for_order_fee_iv).setOnClickListener(this);
        view.findViewById(R.id.pay_for_order_credit_iv).setOnClickListener(this);
        mParkOrderDiscount.setOnClickListener(this);
        mShouldPayFee.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mDiscountInfos = new ArrayList<>();
        getDiscount();
        if (DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getExtensionTime()).compareTo(
                DateUtil.getYearToSecondCalendar(mParkOrderInfo.getPark_end_time())) < 0) {
            //停车时长超过预约时长
            mParkTime.setText(DateUtil.getDateDistanceForHourWithMinute(mParkOrderInfo.getOrder_starttime(), mParkOrderInfo.getPark_end_time()));
            String timeout = "超时" + DateUtil.getDateDistanceForHourWithMinute(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getPark_end_time());
            if (timeout.charAt(timeout.length() - 1) == '分') {
                timeout += "钟";
            }
            mParkTimeDescription.setText(timeout);

            mParkOrderCredit.setText("-5");
        } else if (DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_endtime()).compareTo(
                DateUtil.getYearToSecondCalendar(mParkOrderInfo.getPark_end_time())) < 0) {
            //停车时间在顺延时长内
            mParkTime.setText(DateUtil.getDateDistanceForHourWithMinute(mParkOrderInfo.getOrder_starttime(), mParkOrderInfo.getPark_end_time()));
            mParkOrderCredit.setText("+3");
        } else {
            //停车时间不到预约的结束时间
            mParkTime.setText(DateUtil.getDateDistanceForHourWithMinute(mParkOrderInfo.getOrder_starttime(), mParkOrderInfo.getOrder_endtime()));
            mParkOrderCredit.setText("+3");
        }
        mParkOrderFee.setText(DateUtil.decreseOneZero(Double.parseDouble(mParkOrderInfo.getOrder_fee())));
        String totalCredit = "（总分" + com.tuzhao.publicmanager.UserManager.getInstance().getUserInfo().getCredit() + "）";
        mUserTotalCredit.setText(totalCredit);

        calculateShouldPayFee();
        IntentObserable.registerObserver(this);

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
                        String resultInfo = payResult.getResult();// 同步返回需要验证的信息，从支付结果中取到resultinfo
                        String resultStatus = payResult.getResultStatus();
                        // 判断resultStatus 为9000则代表支付成功
                        if (TextUtils.equals(resultStatus, "9000")) {
                            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                            requetFinishOrder();
                            Toast.makeText(MyApplication.getInstance(), "支付成功", Toast.LENGTH_SHORT).show();
                        } else {
                            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                            Toast.makeText(MyApplication.getInstance(), "支付失败" + payResult.getResult() + resultInfo, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                    case OrderInfoUtil2_0.SDK_AUTH_FLAG: {
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
                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IntentObserable.unregisterObserver(this);
        if (mPayThread != null) {
            mPayThread.interrupt();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_for_order_question:

                break;
            case R.id.pay_for_order_time_iv:

                break;
            case R.id.pay_for_order_fee_iv:

                break;
            case R.id.pay_for_order_credit_iv:

                break;
            case R.id.pay_for_order_discount:
                if (mDiscountInfos.isEmpty()) {
                    showFiveToast("暂无可用优惠券哦");
                } else {
                    startActivityForResult(DiscountActivity.class, ConstansUtil.DISOUNT_REQUEST_CODE, ConstansUtil.DISCOUNT_LIST, mDiscountInfos);
                }
                break;
            case R.id.pay_for_order_should_pay:
                payV2();
                break;
        }
    }

    /**
     * 获取优惠券
     */
    private void getDiscount() {
        getOkGo(HttpConstants.getUserDiscount)
                .execute(new JsonCallback<Base_Class_List_Info<Discount_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<Discount_Info> o, Call call, Response response) {
                        mDiscountInfos = o.data;
                        List<Discount_Info> mCanUseDiscount = new ArrayList<>();
                        for (Discount_Info discount_info : mDiscountInfos) {
                            if (discount_info.getIs_usable().equals("1")) {
                                //可用
                                if (discount_info.getWhat_type().equals("1")) {
                                    //是停车券
                                    if (Double.valueOf(mParkOrderInfo.getOrder_fee()) >= Double.valueOf(discount_info.getMin_fee())) {
                                        //大于最低消费
                                        if (DateUtil.isInUsefulDate(discount_info.getEffective_time())) {
                                            //在可用范围内
                                            mCanUseDiscount.add(discount_info);
                                        }
                                    }
                                }
                            }
                        }

                        //按照优惠金额从大到小排序
                        Collections.sort(mCanUseDiscount, new Comparator<Discount_Info>() {
                            @Override
                            public int compare(Discount_Info o1, Discount_Info o2) {
                                return (int) (Double.valueOf(o2.getDiscount()) - Double.valueOf(o1.getDiscount()));
                            }
                        });

                        for (Discount_Info discount_info : mCanUseDiscount) {
                            //找到低于消费金额的最大优惠券
                            if (Double.valueOf(mParkOrderInfo.getOrder_fee()) >= Double.valueOf(discount_info.getDiscount())) {
                                mChooseDiscount = discount_info;
                                break;
                            }
                        }
                        calculateShouldPayFee();
                        dismmisLoadingDialog();
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
     * 根据优惠券金额计算显示相应的优惠金额以及支付金额
     */
    private void calculateShouldPayFee() {
        String shouldPay = "确认支付" + DateUtil.decreseOneZero(Double.valueOf(mParkOrderInfo.getOrder_fee())) + "元";
        if (mChooseDiscount != null) {
            if (Double.valueOf(mChooseDiscount.getDiscount()) != 0) {
                String discount = "（优惠券-" + Double.valueOf(mChooseDiscount.getDiscount()) + "）";
                SpannableString spannableString = new SpannableString(discount);
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#1dd0a1")),
                        discount.indexOf("-"), discount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mParkOrderDiscount.setText(spannableString);
                shouldPay = "确认支付" + DateUtil.decreseOneZero(Double.valueOf(mParkOrderInfo.getOrder_fee()) - Double.valueOf(mChooseDiscount.getDiscount())) + "元";
            }
        }
        mShouldPayFee.setText(shouldPay);
    }

    /**
     * 支付宝支付业务
     */
    public void payV2() {
        OkGo.post(HttpConstants.alipayApplyOrder)
                .tag(TAG)
                .params("order_id", mParkOrderInfo.getId())
                .params("citycode", mParkOrderInfo.getCitycode())
                .params("discount_id", mChooseDiscount == null ? "-1" : mChooseDiscount.getId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(final String s, Call call, Response response) {
                        Runnable payRunnable = new Runnable() {

                            @Override
                            public void run() {
                                PayTask alipay = new PayTask(getActivity());
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
                    }
                });
    }

    private void requetFinishOrder() {
        //请求改变订单状态，完成订单
        getOkGo(HttpConstants.finishParkOrder)
                .params("order_id", mParkOrderInfo.getId())
                .params("citycode", mParkOrderInfo.getCitycode())
                .params("pass_code", DensityUtil.MD5code(UserManager.getInstance().getUserInfo().getSerect_code() + "*&*" + UserManager.getInstance().getUserInfo().getCreate_time() + "*&*" + UserManager.getInstance().getUserInfo().getId()))
                .execute(new JsonCallback<Base_Class_Info<User_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<User_Info> info, Call call, Response response) {
                        if (getActivity() != null) {
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(ConstansUtil.PAY_ORDER_FINISH, mParkOrderInfo);
                            intent.putExtra(ConstansUtil.FOR_REQUEST_RESULT, bundle);
                            getActivity().setResult(Activity.RESULT_OK, intent);
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast(e.getMessage());
                        }
                    }
                });
    }

    @Override
    public void onReceive(Intent intent) {
        if (Objects.equals(intent.getAction(), ConstansUtil.CHOOSE_DISCOUNT)) {
            if (intent.hasExtra(ConstansUtil.FOR_REQUEST_RESULT)) {
                Bundle bundle = intent.getBundleExtra(ConstansUtil.FOR_REQUEST_RESULT);
                mChooseDiscount = bundle.getParcelable(ConstansUtil.CHOOSE_DISCOUNT);
                if (mChooseDiscount != null) {
                    calculateShouldPayFee();
                }
            }
        }
    }

}
