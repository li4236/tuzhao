package com.tuzhao.fragment.parkorder;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.okgo.callback.StringCallback;
import com.tuzhao.R;
import com.tuzhao.activity.BigPictureActivity;
import com.tuzhao.activity.PayActivity;
import com.tuzhao.activity.mine.DiscountActivity;
import com.tuzhao.application.MyApplication;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.CollectionInfo;
import com.tuzhao.info.Discount_Info;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.WechatPayParam;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.CollectionManager;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.alipay.AuthResult;
import com.tuzhao.publicwidget.alipay.OrderInfoUtil2_0;
import com.tuzhao.publicwidget.alipay.PayResult;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

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

    private ImageView mCollectIv;

    private TextView mCollectTv;

    private TextView mParkDiscount;

    private TextView mShouldPayFee;

    private ArrayList<Discount_Info> mDiscountInfos;

    private ArrayList<Discount_Info> mCanUseDiscounts;

    private ArrayList<String> mParkSpacePictures;

    private Discount_Info mChooseDiscount;

    private Handler mHandler;

    private Thread mPayThread;

    private CustomDialog mCustomDialog;

    private DecimalFormat mDecimalFormat;

    private double mShouldPay;

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
        mParkTimeDescription = view.findViewById(R.id.appointment_park_date_tv);
        mCollectIv = view.findViewById(R.id.collect_park_lot_iv);
        mCollectTv = view.findViewById(R.id.collect_park_lot_tv);
        mParkOrderFee = view.findViewById(R.id.pay_for_order_fee);
        mParkOrderDiscount = view.findViewById(R.id.pay_for_order_discount);
        mParkOrderCredit = view.findViewById(R.id.pay_for_order_credit);
        mUserTotalCredit = view.findViewById(R.id.pay_for_order_total_credit);
        mParkDiscount = view.findViewById(R.id.park_discount);
        mShouldPayFee = view.findViewById(R.id.pay_for_order_should_pay);

        view.findViewById(R.id.pay_for_order_question_tv).setOnClickListener(this);
        view.findViewById(R.id.appointment_calculate_rule_iv).setOnClickListener(this);
        view.findViewById(R.id.car_pic_cl).setOnClickListener(this);
        view.findViewById(R.id.contact_service_cl).setOnClickListener(this);
        view.findViewById(R.id.collect_park_lot_cl).setOnClickListener(this);
        view.findViewById(R.id.park_discount_cl).setOnClickListener(this);
        view.findViewById(R.id.view_appointment_detail).setOnClickListener(this);
        view.findViewById(R.id.view_appointment_detail_iv).setOnClickListener(this);
        mShouldPayFee.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mDiscountInfos = new ArrayList<>();
        mCanUseDiscounts = new ArrayList<>();
        getDiscount();
        if (DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getExtensionTime()).compareTo(
                DateUtil.getYearToSecondCalendar(mParkOrderInfo.getPark_end_time())) < 0) {
            //停车时长超过预约时长
            String timeout = "超时" + DateUtil.getDateDistanceForHourWithMinute(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getPark_end_time(), mParkOrderInfo.getExtensionTime());
            mParkTimeDescription.setText(timeout);

            mParkOrderCredit.setText("-5");
        } else if (DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_endtime()).compareTo(
                DateUtil.getYearToSecondCalendar(mParkOrderInfo.getPark_end_time())) < 0) {
            //停车时间在顺延时长内
            mParkOrderCredit.setText("+3");
        } else {
            //停车时间不到预约的结束时间
            mParkTime.setText(DateUtil.getDateDistanceForHourWithMinute(mParkOrderInfo.getOrder_starttime(), mParkOrderInfo.getOrder_endtime()));
            mParkOrderCredit.setText("+3");
        }
        mParkTime.setText(DateUtil.getDistanceForDayTimeMinute(mParkOrderInfo.getPark_start_time(), mParkOrderInfo.getPark_end_time()));
        mParkOrderFee.setText(DateUtil.decreseOneZero(Double.parseDouble(mParkOrderInfo.getOrder_fee())));
        String startDate = DateUtil.deleteSecond(mParkOrderInfo.getPark_start_time());
        String endDate = DateUtil.deleteSecond(mParkOrderInfo.getPark_end_time());
        if (DateUtil.compareYearToSecond(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getPark_end_time()) > 0) {
            endDate = DateUtil.deleteSecond(mParkOrderInfo.getOrder_endtime());
        }

        Log.e(TAG, "initData: " + new DecimalFormat("0.00").format(DateUtil.caculateParkFee(startDate, endDate, mParkOrderInfo.getHigh_time(),
                Double.valueOf(mParkOrderInfo.getHigh_fee()), Double.valueOf(mParkOrderInfo.getLow_fee()))));

        String totalCredit = "（总分" + com.tuzhao.publicmanager.UserManager.getInstance().getUserInfo().getCredit() + "）";
        mUserTotalCredit.setText(totalCredit);

        setCollection(CollectionManager.getInstance().isContainParkLot(mParkOrderInfo.getBelong_park_space()));
        calculateShouldPayFee();
        IntentObserable.registerObserver(this);

        initHandler();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IntentObserable.unregisterObserver(this);
        if (mPayThread != null) {
            mPayThread.interrupt();
        }

        if (mCustomDialog != null && mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_for_order_question_tv:
            case R.id.appointment_calculate_rule_iv:
                break;
            case R.id.car_pic_cl:
                if (mParkOrderInfo.getPictures() == null || mParkOrderInfo.getPictures().equals("-1")) {
                    showFiveToast("暂无车位图片");
                } else {
                    showParkSpacePic();
                }
                break;
            case R.id.contact_service_cl:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:4006505058"));
                startActivity(intent);
                break;
            case R.id.collect_park_lot_cl:
                if (getText(mCollectTv).equals("已收藏")) {
                    deleteCollectParkLot(CollectionManager.getInstance().getCollection(mParkOrderInfo.getBelong_park_space()));
                } else {
                    collectParkLot();
                }
                break;
            case R.id.view_appointment_detail:
            case R.id.view_appointment_detail_iv:
                showParkDetail();
                break;
            case R.id.park_discount_cl:
                if (mCanUseDiscounts.isEmpty()) {
                    showFiveToast("暂无可用优惠券哦");
                } else {
                    startActivityForResult(DiscountActivity.class, ConstansUtil.DISOUNT_REQUEST_CODE,
                            ConstansUtil.ORDER_FEE, mParkOrderInfo.getOrder_fee(), ConstansUtil.DISCOUNT_LIST, mDiscountInfos);
                }
                break;
            case R.id.pay_for_order_should_pay:
                if (mShouldPay >= 0.01) {
                    Bundle bundle = new Bundle();
                    bundle.putString(ConstansUtil.ORDER_FEE, mShouldPay+"元");
                    bundle.putString(ConstansUtil.PARK_ORDER_ID, mParkOrderInfo.getId());
                    bundle.putString(ConstansUtil.CITY_CODE, mParkOrderInfo.getCitycode());
                    bundle.putString(ConstansUtil.CHOOSE_DISCOUNT, mChooseDiscount == null ? "-1" : mChooseDiscount.getId());
                    startActivity(PayActivity.class, bundle);
                    //payV2();
                } else {
                    requetFinishOrder();
                }
                break;
        }
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
                            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                            requetFinishOrder();
                            Toast.makeText(MyApplication.getInstance(), "支付成功", Toast.LENGTH_SHORT).show();
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

    private void setCollection(boolean isCollection) {
        if (isCollection) {
            ImageUtil.showPic(mCollectIv, R.drawable.ic_collecting);
            mCollectTv.setText("已收藏");
            mCollectTv.setTextColor(Color.parseColor("#ffa830"));
        } else {
            ImageUtil.showPic(mCollectIv, R.drawable.ic_not_collection);
            mCollectTv.setText("收藏车场");
            mCollectTv.setTextColor(Color.parseColor("#808080"));
        }
    }

    private void collectParkLot() {
        showLoadingDialog("正在添加收藏...");
        getOkGo(HttpConstants.addCollection)
                .params("belong_id", mParkOrderInfo.getBelong_park_space())
                .params("type", 1)
                .params("citycode", mParkOrderInfo.getCitycode())
                .execute(new JsonCallback<Base_Class_Info<CollectionInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<CollectionInfo> o, Call call, Response response) {
                        CollectionManager.getInstance().addCollectionData(o.data);
                        setCollection(true);
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast("收藏失败");
                        }
                    }
                });
    }

    private void deleteCollectParkLot(final CollectionInfo collectionInfo) {
        showLoadingDialog("正在取消收藏...");
        getOkGo(HttpConstants.deleteCollection)
                .params("id", collectionInfo.getId())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> info, Call call, Response response) {
                        CollectionManager.getInstance().removeCollection(collectionInfo);
                        setCollection(false);
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast("取消收藏失败");
                        }
                    }
                });
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
                        for (Discount_Info discount_info : mDiscountInfos) {
                            if (discount_info.getIs_usable().equals("1")) {
                                //可用
                                if (discount_info.getWhat_type().equals("1")) {
                                    //是停车券
                                   /* if (Double.valueOf(mParkOrderInfo.getOrder_fee()) >= Double.valueOf(discount_info.getMin_fee())) {
                                        //大于最低消费*/
                                    if (DateUtil.isInUsefulDate(discount_info.getEffective_time())) {
                                        //在可用范围内
                                        mCanUseDiscounts.add(discount_info);
                                    }
                                    //}
                                }
                            }
                        }

                        /*//按照优惠金额从大到小排序
                        Collections.sort(mCanUseDiscounts, new Comparator<Discount_Info>() {
                            @Override
                            public int compare(Discount_Info o1, Discount_Info o2) {
                                return (int) (Double.valueOf(o2.getDiscount()) - Double.valueOf(o1.getDiscount()));
                            }
                        });

                        for (Discount_Info discount_info : mCanUseDiscounts) {
                            //找到低于消费金额的最大优惠券
                            if (Double.valueOf(mParkOrderInfo.getOrder_fee()) >= Double.valueOf(discount_info.getDiscount())) {
                                mChooseDiscount = discount_info;
                                break;
                            }
                        }*/
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

    private void showParkSpacePic() {
        if (mParkSpacePictures == null) {
            mParkSpacePictures = new ArrayList<>();
            String[] pictures = mParkOrderInfo.getPictures().split(",");
            for (String picture : pictures) {
                if (!picture.equals("-1")) {
                    mParkSpacePictures.add(HttpConstants.ROOT_IMG_URL_PS + picture);
                }
            }
        }
        Intent intent = new Intent(getActivity(), BigPictureActivity.class);
        intent.putStringArrayListExtra("picture_list", mParkSpacePictures);
        startActivity(intent);
    }

    private void showParkDetail() {
        if (mCustomDialog == null) {
            if (getContext() != null) {
                mCustomDialog = new CustomDialog(getContext(), mParkOrderInfo, false);
            }
        }
        mCustomDialog.show();
    }

    /**
     * 根据优惠券金额计算显示相应的优惠金额以及支付金额
     */
    private void calculateShouldPayFee() {
        mShouldPay = Double.parseDouble(DateUtil.decreseOneZero(Double.valueOf(mParkOrderInfo.getOrder_fee())));
        String shouldPay = "确认支付" + mShouldPay + "元";
        if (mChooseDiscount != null) {
            double discountFee = Double.valueOf(mChooseDiscount.getDiscount());
            if (Double.valueOf(mChooseDiscount.getDiscount()) >= 0) {
                String discount = "（优惠券—" + discountFee + "）";
                SpannableString spannableString = new SpannableString(discount);
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#1dd0a1")),
                        discount.indexOf("—"), discount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mParkOrderDiscount.setText(spannableString);

                String parkDiscount = "—￥" + discountFee;
                mParkDiscount.setText(parkDiscount);

                if (mDecimalFormat == null) {
                    mDecimalFormat = new DecimalFormat("0.00");
                }

                mShouldPay = Double.parseDouble(mDecimalFormat.format(Double.valueOf(mParkOrderInfo.getOrder_fee()) - discountFee));
                if (mShouldPay >= 0) {
                    shouldPay = "确认支付" + DateUtil.decreseOneZero(mShouldPay) + "元";
                } else {
                    shouldPay = "确认支付0.0元";
                }
            }
        } else {
            mParkDiscount.setText("（未用优惠券）");
            String discountCount = mCanUseDiscounts.size() + "张优惠券";
            mParkDiscount.setText(discountCount);
        }
        mShouldPayFee.setText(shouldPay);
    }

    private void wechatPay() {
        getOkGo(HttpConstants.getWechatPayOrder)
                .params("orderId", mParkOrderInfo.getId())
                .params("cityCode", mParkOrderInfo.getCitycode())
                .params("discountId", mChooseDiscount == null ? "-1" : mChooseDiscount.getId())
                .execute(new JsonCallback<Base_Class_Info<WechatPayParam>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<WechatPayParam> o, Call call, Response response) {
                        IWXAPI iwxapi = WXAPIFactory.createWXAPI(getContext(), null);
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
                            intent.setAction(ConstansUtil.FINISH_PAY_ORDER);
                            /*Bundle bundle = new Bundle();
                            bundle.putParcelable(ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
                            intent.putExtra(ConstansUtil.FOR_REQUEST_RESULT, bundle);*/
                            IntentObserable.dispatch(intent);
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
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ConstansUtil.CHOOSE_DISCOUNT:
                    Bundle bundle = intent.getBundleExtra(ConstansUtil.FOR_REQUEST_RESULT);
                    mChooseDiscount = bundle.getParcelable(ConstansUtil.CHOOSE_DISCOUNT);
                    calculateShouldPayFee();
                    break;
                case ConstansUtil.PAY_SUCCESS:
                    requetFinishOrder();
                    break;
                case ConstansUtil.PAY_ERROR:
                    showFiveToast(intent.getStringExtra(ConstansUtil.INTENT_MESSAGE));
                    break;
                case ConstansUtil.PAY_CANCEL:
                    showFiveToast("支付取消");
                    break;
            }
        }
    }

}
