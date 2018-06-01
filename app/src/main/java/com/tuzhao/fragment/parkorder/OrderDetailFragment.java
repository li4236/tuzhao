package com.tuzhao.fragment.parkorder;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.BigPictureActivity;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.IntentObserable;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/6/1.
 */

public class OrderDetailFragment extends BaseStatusFragment implements View.OnClickListener {

    private TextView mOrderFee;

    private TextView mOrderDiscount;

    private TextView mParkTime;

    private TextView mParkTimeDescription;

    private TextView mChangeCredit;

    private TextView mTotalCredit;

    private ParkOrderInfo mParkOrderInfo;

    private ArrayList<String> mParkSpacePictures;

    private CustomDialog mCustomDialog;

    public static OrderDetailFragment newInstance(ParkOrderInfo parkOrderInfo) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstansUtil.PARK_ORDER_INFO, parkOrderInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int resourceId() {
        return R.layout.fragment_order_detail_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

        if (getArguments() != null) {
            mParkOrderInfo = getArguments().getParcelable(ConstansUtil.PARK_ORDER_INFO);
        } else {
            return;
        }

        mOrderFee = view.findViewById(R.id.pay_for_order_fee);
        mOrderDiscount = view.findViewById(R.id.pay_for_order_discount);
        mParkTime = view.findViewById(R.id.pay_for_order_time);
        mParkTimeDescription = view.findViewById(R.id.pay_for_order_time_description);
        mChangeCredit = view.findViewById(R.id.pay_for_order_credit);
        mTotalCredit = view.findViewById(R.id.pay_for_order_total_credit);

        view.findViewById(R.id.pay_for_order_question).setOnClickListener(this);
        view.findViewById(R.id.contact_service_cl).setOnClickListener(this);
        view.findViewById(R.id.car_pic_cl).setOnClickListener(this);
        view.findViewById(R.id.delete_order_cl).setOnClickListener(this);
        view.findViewById(R.id.view_park_order_detail).setOnClickListener(this);

        if (mParkOrderInfo.getOrder_status().equals("4")) {
            view.findViewById(R.id.park_comment_cl).setVisibility(View.VISIBLE);
            view.findViewById(R.id.park_comment_cl).setOnClickListener(this);
        }
    }

    @Override
    protected void initData() {
        mOrderFee.setText(DateUtil.decreseOneZero(mParkOrderInfo.getActual_pay_fee()));
        if (mParkOrderInfo.getDiscount() != null && !mParkOrderInfo.getDiscount().getId().equals("-1")) {
            String disount = "（优惠券—" + DateUtil.decreseOneZero(mParkOrderInfo.getDiscount().getDiscount()) + "）";
            SpannableString spannableString = new SpannableString(disount);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("1dd0a1")), 4, disount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mOrderDiscount.setText(spannableString);
        }

        mParkTime.setText(DateUtil.getHourToMinute(mParkOrderInfo.getPark_start_time()));
        if (DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_starttime(), mParkOrderInfo.getExtensionTime()).compareTo(
                DateUtil.getYearToSecondCalendar(mParkOrderInfo.getPark_end_time())) < 0) {
            String timeout = "超时" + DateUtil.getDateDistanceForHourWithMinute(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getPark_end_time(), mParkOrderInfo.getExtensionTime());
            mParkTimeDescription.setText(timeout);
            mChangeCredit.setText("+3");
        } else {
            mChangeCredit.setTextColor(Color.parseColor("#ff6c6c"));
            mChangeCredit.setText("-5");
        }

        String totalCredit = "（总分" + com.tuzhao.publicmanager.UserManager.getInstance().getUserInfo().getCredit() + "）";
        mTotalCredit.setText(totalCredit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_for_order_question:

                break;
            case R.id.contact_service_cl:
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4006505058"));
                startActivity(intent);
                break;
            case R.id.car_pic_cl:
                if (mParkOrderInfo.getPictures() == null || mParkOrderInfo.getPictures().equals("-1")) {
                    showFiveToast("暂无车位图片");
                } else {
                    showParkSpacePic();
                }
                break;
            case R.id.delete_order_cl:
                TipeDialog dialog = new TipeDialog.Builder(getContext())
                        .setTitle("提示")
                        .setMessage("确定删除该订单吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletelOrder();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                dialog.show();
                break;
            case R.id.view_park_order_detail:
                showParkDetail();
                break;
            case R.id.park_comment_cl:
                Intent commentIntent = new Intent();
                commentIntent.setAction(ConstansUtil.OPEN_PARK_COMMENT);
                IntentObserable.dispatch(commentIntent);
                break;
        }
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

    private void deletelOrder() {
        showLoadingDialog("正在删除");
        getOkGo(HttpConstants.deletelParkOrder)
                .params("order_id", mParkOrderInfo.getId())
                .params("citycode", mParkOrderInfo.getCitycode())
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> responseData, Call call, Response response) {
                        Intent intent = new Intent();
                        intent.setAction(ConstansUtil.DELETE_PARK_ORDER);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
                        intent.putExtra(ConstansUtil.FOR_REQUEST_RESULT, bundle);
                        IntentObserable.dispatch(intent);
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "102":
                                case "103":
                                case "104":
                                    showFiveToast("删除失败，请稍后重试");
                                    break;
                            }
                        }
                    }
                });
    }

    private void showParkDetail() {
        if (mCustomDialog == null) {
            if (getContext() != null) {
                mCustomDialog = new CustomDialog(getContext(), mParkOrderInfo, true);
            }
        }
        mCustomDialog.show();
    }

}
