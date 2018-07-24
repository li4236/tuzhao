package com.tuzhao.fragment.parkorder;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.BigPictureActivity;
import com.tuzhao.activity.PayActivity;
import com.tuzhao.activity.mine.BillingRuleActivity;
import com.tuzhao.activity.mine.DiscountActivity;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.MonthlyCardBean;
import com.tuzhao.info.CollectionInfo;
import com.tuzhao.info.Discount_Info;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.CollectionManager;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;
import com.tuzhao.utils.PollingUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/5/29.
 */

public class PayForOrderFragment extends BaseStatusFragment implements View.OnClickListener, IntentObserver {

    private ParkOrderInfo mParkOrderInfo;

    private ImageView mParkDurationIv;

    private TextView mParkTime;

    //private TextView mParkTimeDescription;

    private TextView mParkOrderFee;

    //private TextView mParkOrderDiscount;

    private TextView mParkOrderCredit;

    //private TextView mUserTotalCredit;

    private ImageView mCollectIv;

    private TextView mCollectTv;

    private TextView mParkDiscount;

    private TextView mShouldPayFee;

    private ArrayList<Discount_Info> mDiscountInfos;

    private ArrayList<Discount_Info> mCanUseDiscounts;

    private ArrayList<MonthlyCardBean> mMonthlyCards;

    private ArrayList<String> mParkSpacePictures;

    private Discount_Info mChooseDiscount;

    private CustomDialog mCustomDialog;

    private DecimalFormat mDecimalFormat;

    private double mShouldPay;

    private PollingUtil mPollingUtil;

    private int mRequestCount;

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

        mParkDurationIv = view.findViewById(R.id.pay_for_order_time_iv);
        mParkTime = view.findViewById(R.id.pay_for_order_time);
        //mParkTimeDescription = view.findViewById(R.id.appointment_park_date_tv);
        mCollectIv = view.findViewById(R.id.collect_park_lot_iv);
        mCollectTv = view.findViewById(R.id.collect_park_lot_tv);
        mParkOrderFee = view.findViewById(R.id.pay_for_order_fee);
        //mParkOrderDiscount = view.findViewById(R.id.pay_for_order_discount);
        mParkOrderCredit = view.findViewById(R.id.pay_for_order_credit);
        //mUserTotalCredit = view.findViewById(R.id.pay_for_order_total_credit);
        mParkDiscount = view.findViewById(R.id.park_discount);
        mShouldPayFee = view.findViewById(R.id.pay_for_order_should_pay);

        view.setOnClickListener(this);
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
        mMonthlyCards = new ArrayList<>();
        mDecimalFormat = new DecimalFormat("0.00");

        getDiscount();
        getMonthlyCard();

        if (DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getExtensionTime()).compareTo(
                DateUtil.getYearToSecondCalendar(mParkOrderInfo.getPark_end_time())) < 0) {
            //停车时长超过预约时长
            /*String timeout = "超时" + DateUtil.getDateDistanceForHourWithMinute(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getPark_end_time(), mParkOrderInfo.getExtensionTime());
            mParkTimeDescription.setText(timeout);*/

            ImageUtil.showPic(mParkDurationIv, R.drawable.ic_overtime);
            mParkTime.setText(DateUtil.getDistanceForDayTimeMinute(mParkOrderInfo.getOrder_starttime(), mParkOrderInfo.getPark_end_time()));
            mParkOrderCredit.setText("-5");
        } else if (DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_endtime()).compareTo(
                DateUtil.getYearToSecondCalendar(mParkOrderInfo.getPark_end_time())) < 0) {
            //停车时间在顺延时长内
            mParkOrderCredit.setText("+3");
            mParkTime.setText(DateUtil.getDistanceForDayTimeMinute(mParkOrderInfo.getPark_start_time(), mParkOrderInfo.getPark_end_time()));
        } else {
            //停车时间不到预约的结束时间
            int totalAppointmentMinute = DateUtil.getDateMinutesDistance(mParkOrderInfo.getOrder_starttime(), mParkOrderInfo.getOrder_endtime());
            int parkToAppointmentMinute = DateUtil.getDateMinutesDistance(mParkOrderInfo.getPark_end_time(), mParkOrderInfo.getOrder_endtime());
            if (parkToAppointmentMinute >= totalAppointmentMinute / 5) {
                //剩余时长大于总时长的1/5则减5分
                mParkOrderCredit.setText("-5");
            }
            mParkTime.setText(DateUtil.getDateDistanceForHourWithMinute(mParkOrderInfo.getOrder_starttime(), mParkOrderInfo.getOrder_endtime()));
            mParkOrderCredit.setText("+3");
        }

        mParkOrderFee.setText(DateUtil.decreseOneZero(Double.parseDouble(mParkOrderInfo.getOrder_fee())));
        String startDate = DateUtil.deleteSecond(mParkOrderInfo.getPark_start_time());
        String endDate = DateUtil.deleteSecond(mParkOrderInfo.getPark_end_time());
        if (DateUtil.compareYearToSecond(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getPark_end_time()) > 0) {
            endDate = DateUtil.deleteSecond(mParkOrderInfo.getOrder_endtime());
        }

        Log.e(TAG, "initData: " + new DecimalFormat("0.00").format(DateUtil.caculateParkFee(startDate, endDate, mParkOrderInfo.getHigh_time(),
                Double.valueOf(mParkOrderInfo.getHigh_fee()), Double.valueOf(mParkOrderInfo.getLow_fee()))));

        /*String totalCredit = "（总分" + com.tuzhao.publicmanager.UserManager.getInstance().getUserInfo().getCredit() + "）";
        mUserTotalCredit.setText(totalCredit);*/

        setCollection(CollectionManager.getInstance().isContainParkLot(mParkOrderInfo.getBelong_park_space()));
        calculateShouldPayFee();
        IntentObserable.registerObserver(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IntentObserable.unregisterObserver(this);

        if (mCustomDialog != null && mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_for_order_question_tv:
            case R.id.appointment_calculate_rule_iv:
                Bundle parkLotBundle = new Bundle();
                parkLotBundle.putString(ConstansUtil.PARK_LOT_ID, mParkOrderInfo.getBelong_park_space());
                parkLotBundle.putString(ConstansUtil.CITY_CODE, mParkOrderInfo.getCitycode());
                startActivity(BillingRuleActivity.class, parkLotBundle);
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
                    bundle.putString(ConstansUtil.PAY_TYPE, "0");
                    bundle.putString(ConstansUtil.PAY_MONEY, mShouldPay + "元");
                    bundle.putString(ConstansUtil.PARK_ORDER_ID, mParkOrderInfo.getId());
                    bundle.putString(ConstansUtil.CITY_CODE, mParkOrderInfo.getCitycode());
                    bundle.putString(ConstansUtil.CHOOSE_DISCOUNT, mChooseDiscount == null ? "-1" : mChooseDiscount.getId());
                    startActivity(PayActivity.class, bundle);
                } else {
                    requetFinishOrder();
                }
                break;
        }
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
     * 获取有效的月卡
     */
    private void getMonthlyCard() {
        getOkGo(HttpConstants.getUserMonthlyCards)
                .params("startItem", 0)
                .params("pageSize", 15)
                .params("type", "1")
                .execute(new JsonCallback<Base_Class_List_Info<MonthlyCardBean>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<MonthlyCardBean> o, Call call, Response response) {
                        for (MonthlyCardBean monthlyCardBean : o.data) {
                            if (monthlyCardBean.getCityCode().equals(mParkOrderInfo.getCitycode()) ||
                                    monthlyCardBean.getCityCode().equals("0000")) {
                                mMonthlyCards.add(monthlyCardBean);
                            }
                        }
                        calculateShouldPayFee();
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
                        mDiscountInfos.addAll(o.data);
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
                mCustomDialog = new CustomDialog(getContext(), mParkOrderInfo, true);
            }
        }
        mCustomDialog.show();
    }

    /**
     * 根据优惠券金额计算显示相应的优惠金额以及支付金额
     */
    private void calculateShouldPayFee() {
        mShouldPay = Double.parseDouble(DateUtil.decreseOneZero(Double.valueOf(mParkOrderInfo.getOrder_fee())));
        String shouldPay;
        if (mChooseDiscount != null) {
            double discountFee = Double.valueOf(mChooseDiscount.getDiscount());
            if (Double.valueOf(mChooseDiscount.getDiscount()) >= 0) {
                String discount = "（优惠券—" + discountFee + "）";
                SpannableString spannableString = new SpannableString(discount);
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#1dd0a1")),
                        discount.indexOf("—"), discount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //mParkOrderDiscount.setText(spannableString);

                String parkDiscount = "—￥" + discountFee;
                mParkDiscount.setText(parkDiscount);

                mShouldPay = Double.parseDouble(mDecimalFormat.format(Double.valueOf(mParkOrderInfo.getOrder_fee()) - discountFee));
                calculateParkFeeWithMonthlyCard();
                shouldPay = "确认支付" + DateUtil.decreseOneZero(mShouldPay) + "元";
            } else {
                calculateParkFeeWithMonthlyCard();
                shouldPay = "确认支付" + mShouldPay + "元";
            }
        } else {
            //mParkDiscount.setText("（未用优惠券）");
            calculateParkFeeWithMonthlyCard();
            shouldPay = "确认支付" + mShouldPay + "元";
            String discountCount = mCanUseDiscounts.size() + "张优惠券";
            mParkDiscount.setText(discountCount);
        }
        mShouldPayFee.setText(shouldPay);
    }

    /**
     * 如果有月卡的话则价格打七折
     */
    private void calculateParkFeeWithMonthlyCard() {
        if (!mMonthlyCards.isEmpty()) {
            mShouldPay = Double.parseDouble(mDecimalFormat.format(mShouldPay * 0.7));
        }
        if (mShouldPay <= 0) {
            mShouldPay = 0.01;
        }
    }

    /**
     * 支付成功后查询订单是否已经完成
     */
    private void getParkOrder() {
        if (mRequestCount >= 3) {
            dismmisLoadingDialog();
            showFiveToast("查询订单状态失败，请刷新后再查看");
            finish();
        }
        showLoadingDialog("正在查询订单状态");
        mRequestCount++;
        getOkGo(HttpConstants.getParkOrder)
                .params("cityCode", mParkOrderInfo.getCitycode())
                .params("orderId", mParkOrderInfo.getId())
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> o, Call call, Response response) {
                        if (o.data.getOrder_status().equals("4") || o.data.getOrder_status().equals("5")) {
                            Intent intent = new Intent();
                            intent.setAction(ConstansUtil.FINISH_PAY_ORDER);
                            Bundle bundle = new Bundle();
                            mParkOrderInfo.setOrder_status(o.data.getOrder_status());
                            mParkOrderInfo.setActual_pay_fee(o.data.getActual_pay_fee());
                            bundle.putParcelable(ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
                            intent.putExtra(ConstansUtil.FOR_REQUEST_RESULT, bundle);
                            IntentObserable.dispatch(intent);
                            dismmisLoadingDialog();
                        } else if (o.data.getOrder_status().equals("3")) {
                            mPollingUtil = new PollingUtil(1000, new PollingUtil.OnTimeCallback() {
                                @Override
                                public void onTime() {
                                    getParkOrder();
                                    mPollingUtil.cancel();
                                    mPollingUtil = null;
                                }
                            });
                            mPollingUtil.start();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast("查询订单状态失败，请刷新后再查看");
                            finish();
                        }
                    }
                });
    }

    private void requetFinishOrder() {
        //请求改变订单状态，完成订单
        showLoadingDialog("正在完成订单...");
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
                            Bundle bundle = new Bundle();
                            mParkOrderInfo.setOrder_status("4");
                            mParkOrderInfo.setActual_pay_fee("0.0");
                            bundle.putParcelable(ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
                            intent.putExtra(ConstansUtil.FOR_REQUEST_RESULT, bundle);
                            IntentObserable.dispatch(intent);
                            dismmisLoadingDialog();
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
                    getParkOrder();
                    break;
            }
        }
    }

}
