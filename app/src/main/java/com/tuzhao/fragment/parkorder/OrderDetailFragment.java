package com.tuzhao.fragment.parkorder;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cb.ratingbar.CBRatingBar;
import com.lwkandroid.imagepicker.ImagePicker;
import com.lwkandroid.imagepicker.data.ImageBean;
import com.lwkandroid.imagepicker.data.ImagePickType;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.BigPictureActivity;
import com.tuzhao.activity.mine.BillingRuleActivity;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.NearPointPCInfo;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.ParkspaceCommentInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;
import com.tuzhao.utils.SoftKeyBroadManager;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by juncoder on 2018/6/1.
 */

public class OrderDetailFragment extends BaseStatusFragment implements View.OnClickListener, IntentObserver {

    private TextView mOrderFee;

    private TextView mOrderDiscount;

    private TextView mParkTime;

    private TextView mParkTimeDescription;

    private TextView mChangeCredit;

    private TextView mTotalCredit;

    private ParkOrderInfo mParkOrderInfo;

    private ArrayList<String> mParkSpacePictures;

    private CustomDialog mCustomDialog;

    private TextView mParkComment;

    private View mContainerView;

    private int mContainerHeight;

    private ObjectAnimator mShowAnimator;

    private ObjectAnimator mHideAnimator;

    private CustomDialog mCommentOrderDialog;

    private ImageView mCloseDialog;

    private CBRatingBar mCBRatingBar;

    private EditText mCommentEt;

    private TextView mCommentCount;

    private ImageView mAddIv;

    private ImageView mDeleteAddIv;

    private ImageView mOneIv;

    private ImageView mDeleteOneIv;

    private ImageView mTwoIv;

    private ImageView mDeleteTwoIv;

    private TextView mApplyComment;

    private List<File> mCommentPicFiles;

    private CustomDialog mOrderCommentDialog;

    private ParkspaceCommentInfo mCommentInfo;

    private DecimalFormat mDecimalFormat;

    private SoftKeyBroadManager.SoftKeyboardStateListener mKeyboardStateListener;

    private SoftKeyBroadManager mSoftKeyBroadManager;

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
        mParkTimeDescription = view.findViewById(R.id.appointment_park_date_tv);
        mChangeCredit = view.findViewById(R.id.pay_for_order_credit);
        mTotalCredit = view.findViewById(R.id.pay_for_order_total_credit);
        mParkComment = view.findViewById(R.id.park_comment_tv);

        view.findViewById(R.id.pay_for_order_question_tv).setOnClickListener(this);
        view.findViewById(R.id.appointment_calculate_rule_iv).setOnClickListener(this);
        view.findViewById(R.id.contact_service_cl).setOnClickListener(this);
        view.findViewById(R.id.car_pic_cl).setOnClickListener(this);
        view.findViewById(R.id.delete_order_cl).setOnClickListener(this);
        view.findViewById(R.id.view_appointment_detail).setOnClickListener(this);
        view.findViewById(R.id.view_appointment_detail_iv).setOnClickListener(this);
        view.findViewById(R.id.park_comment_cl).setOnClickListener(this);

        if (mParkOrderInfo.getOrder_status().equals("5")) {
            mParkComment.setText("已评价");
            //getOrderComment(false);
        }
    }

    @Override
    protected void initView(View view, final ViewGroup container, Bundle savedInstanceState) {
        super.initView(view, container, savedInstanceState);
        mContainerView = container;
        container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mContainerHeight = container.getHeight();
                container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected void initData() {
        mOrderFee.setText(DateUtil.decreseOneZero(mParkOrderInfo.getActual_pay_fee()));
        if (mParkOrderInfo.getDiscount() != null && !mParkOrderInfo.getDiscount().getId().equals("-1")) {
            String disount = "（优惠券—" + DateUtil.decreseOneZero(mParkOrderInfo.getDiscount().getDiscount()) + "）";
            SpannableString spannableString = new SpannableString(disount);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#1dd0a1")), 4, disount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mOrderDiscount.setText(spannableString);
        }

        mParkTime.setText(DateUtil.getDistanceForDayTimeMinute(mParkOrderInfo.getOrder_starttime(), mParkOrderInfo.getOrder_endtime()));
        if (DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getExtensionTime()).compareTo(
                DateUtil.getYearToSecondCalendar(mParkOrderInfo.getPark_end_time())) < 0) {
            String timeout = "超时" + DateUtil.getDateDistanceForHourWithMinute(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getPark_end_time(), mParkOrderInfo.getExtensionTime());
            mParkTimeDescription.setText(timeout);
            mChangeCredit.setTextColor(Color.parseColor("#ff6c6c"));
            mChangeCredit.setText("-5");
        } else {
            mChangeCredit.setText("+3");
        }

        String totalCredit = "（总分" + com.tuzhao.publicmanager.UserManager.getInstance().getUserInfo().getCredit() + "）";
        mTotalCredit.setText(totalCredit);

        mDecimalFormat = new DecimalFormat("0.0");
        IntentObserable.registerObserver(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IntentObserable.unregisterObserver(this);
        //mSoftKeyBroadManager.removeSoftKeyboardStateListener(mKeyboardStateListener);
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
            case R.id.contact_service_cl:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:4006505058"));
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
            case R.id.view_appointment_detail:
            case R.id.view_appointment_detail_iv:
                showParkDetail();
                break;
            case R.id.park_comment_cl:
                if (mParkOrderInfo.getOrder_status().equals("4")) {
                    showComentDialog();
                }/* else {
                    showOrderCommentDialog();
                }*/
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

    private void showOrderDetail() {
        if (mShowAnimator == null) {
            mShowAnimator = ObjectAnimator.ofFloat(mContainerView, "translationY", 0);
            mShowAnimator.setDuration(300);
        }
        mShowAnimator.start();
    }

    private void hideOrderDetail() {
        if (mHideAnimator == null) {
            mHideAnimator = ObjectAnimator.ofFloat(mContainerView, "translationY", 0, mContainerHeight);
            mHideAnimator.setDuration(300);
        }
        mHideAnimator.start();
    }

    private void showComentDialog() {
        if (mCommentOrderDialog == null) {
            View commentView = getLayoutInflater().inflate(R.layout.dialog_coment_order_layout, null);
            mCommentOrderDialog = new CustomDialog(requireContext(), commentView, true);

            mCBRatingBar = commentView.findViewById(R.id.comment_order_crb);
            mCommentEt = commentView.findViewById(R.id.comment_order_et);
            mCommentCount = commentView.findViewById(R.id.comment_order_comment_count);
            mAddIv = commentView.findViewById(R.id.comment_order_pic_add);
            mDeleteAddIv = commentView.findViewById(R.id.comment_order_pic_add_delete);
            mOneIv = commentView.findViewById(R.id.comment_order_pic_one);
            mDeleteOneIv = commentView.findViewById(R.id.comment_order_pic_one_delete);
            mTwoIv = commentView.findViewById(R.id.comment_order_pic_two);
            mDeleteTwoIv = commentView.findViewById(R.id.comment_order_pic_two_delete);
            mApplyComment = commentView.findViewById(R.id.comment_order_apply);
            mCloseDialog = commentView.findViewById(R.id.close_comment);
            initCommentView();
            //initCommentSoftKeyBorad();
        }
        mCommentOrderDialog.show();
    }

    private void initCommentView() {
        mCommentPicFiles = new ArrayList<>(3);

        mAddIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommentPicFiles.size() < 3) {
                    if (getActivity() != null) {
                        new ImagePicker()
                                .cachePath(Environment.getExternalStorageDirectory().getAbsolutePath())
                                .needCamera(true) //是否需要在界面中显示相机入口(类似微信那样)
                                .pickType(ImagePickType.MULTI) //设置选取类型(单选SINGLE、多选MUTIL、拍照ONLY_CAMERA)
                                .maxNum(3 - mCommentPicFiles.size())  //设置最大选择数量(此选项只对多选生效，拍照和单选都是1，修改后也无效)
                                .start(getActivity(), ConstansUtil.PICTURE_REQUEST_CODE);
                    }
                }
            }
        });

        mDeleteAddIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentPicFiles.remove(0);
                showChooesPic();
            }
        });

        mDeleteOneIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommentPicFiles.size() == 3) {
                    mCommentPicFiles.remove(1);
                } else {
                    mCommentPicFiles.remove(0);
                }
                showChooesPic();
            }
        });

        mDeleteTwoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommentPicFiles.size() == 3) {
                    mCommentPicFiles.remove(2);
                } else {
                    mCommentPicFiles.remove(1);
                }
                showChooesPic();
            }
        });

        mApplyComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAddPsComment();
            }
        });

        mCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentOrderDialog.dismiss();
            }
        });

        mCommentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCommentCount.setText(mCommentEt.getText().length() + "/150");
            }
        });
    }

    private void getOrderComment(final boolean showDialog) {
        getOkGo(HttpConstants.getOrderComment)
                .params("orderId", mParkOrderInfo.getId())
                .params("cityCode", mParkOrderInfo.getCitycode())
                .execute(new JsonCallback<Base_Class_Info<ParkspaceCommentInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkspaceCommentInfo> o, Call call, Response response) {
                        mCommentInfo = o.data;
                        if (showDialog) {
                            showOrderCommentDialog();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            if (showDialog) {
                                showFiveToast("获取评论信息失败，请稍后重试");
                            }
                        }
                    }
                });
    }

    private void showOrderCommentDialog() {
        if (mOrderCommentDialog == null) {
            if (mCommentInfo == null) {
                getOrderComment(true);
                return;
            }

            ConstraintLayout constraintLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.dialog_order_comment_layout, null);
            mOrderCommentDialog = new CustomDialog(requireContext(), constraintLayout, true);
            CBRatingBar cbRatingBar = constraintLayout.findViewById(R.id.comment_order_crb);
            View divider = constraintLayout.findViewById(R.id.center_divider);
            TextView commentContent = constraintLayout.findViewById(R.id.comment_order_et);
            ImageView firstPic = constraintLayout.findViewById(R.id.comment_order_pic_add);
            ImageView secondPic = constraintLayout.findViewById(R.id.comment_order_pic_one);
            ImageView thirdPic = constraintLayout.findViewById(R.id.comment_order_pic_two);
            cbRatingBar.setStarProgress(Float.valueOf(mCommentInfo.getGrade()) * 20);
            if ((mCommentInfo.getContent() == null || mCommentInfo.getContent().equals("")) && mCommentInfo.getImg_url().equals("-1")) {
                divider.setVisibility(View.GONE);
            }
            if (mCommentInfo.getContent() == null || mCommentInfo.getContent().equals("")) {
                commentContent.setVisibility(View.GONE);
            } else {
                commentContent.setText(mCommentInfo.getContent());
            }

            if (!mCommentInfo.getImg_url().equals("-1")) {
                String[] commentPics = mCommentInfo.getImg_url().split(",");
                if (commentPics.length >= 1 && !commentPics[0].equals("")) {
                    switch (commentPics.length) {
                        case 1:
                            firstPic.setVisibility(View.VISIBLE);
                            ImageUtil.showPic(firstPic, HttpConstants.ROOT_IMG_URL_PSCOM + commentPics[0]);
                            break;
                        case 2:
                            firstPic.setVisibility(View.VISIBLE);
                            ImageUtil.showPic(firstPic, HttpConstants.ROOT_IMG_URL_PSCOM + commentPics[0]);
                            secondPic.setVisibility(View.VISIBLE);
                            ImageUtil.showPic(secondPic, HttpConstants.ROOT_IMG_URL_PSCOM + commentPics[1]);
                            break;
                        case 3:
                            firstPic.setVisibility(View.VISIBLE);
                            ImageUtil.showPic(firstPic, HttpConstants.ROOT_IMG_URL_PSCOM + commentPics[0]);
                            secondPic.setVisibility(View.VISIBLE);
                            ImageUtil.showPic(secondPic, HttpConstants.ROOT_IMG_URL_PSCOM + commentPics[1]);
                            thirdPic.setVisibility(View.VISIBLE);
                            ImageUtil.showPic(thirdPic, HttpConstants.ROOT_IMG_URL_PSCOM + commentPics[2]);
                            break;
                    }
                }
            }

            constraintLayout.findViewById(R.id.close_comment).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOrderCommentDialog.dismiss();
                }
            });
        }
        mOrderCommentDialog.show();
    }

    private void initCommentSoftKeyBorad() {
        mSoftKeyBroadManager = new SoftKeyBroadManager(mContainerView);
        mKeyboardStateListener = new SoftKeyBroadManager.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                /*ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mCommentView, "translationY", 0, -keyboardHeightInPx / 5);
                objectAnimator.setDuration(100);
                objectAnimator.start();*/
            }

            @Override
            public void onSoftKeyboardClosed() {
               /* ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mCommentView, "translationY", 0);
                objectAnimator.setDuration(100);
                objectAnimator.start();*/
            }
        };
        mSoftKeyBroadManager.addSoftKeyboardStateListener(mKeyboardStateListener);
    }

    private void requestAddPsComment() {
        OkGo.post(HttpConstants.addPsComment)
                .tag(TAG)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("parkspace_id", mParkOrderInfo.getBelong_park_space())
                .params("city_code", mParkOrderInfo.getCitycode())
                .params("order_id", mParkOrderInfo.getId())
                .params("grade", mDecimalFormat.format(mCBRatingBar.getStarProgress() / 20.0))
                .params("content", mCommentEt.getText().toString())
                .addFileParams("imgs[]", mCommentPicFiles)
                .execute(new JsonCallback<Base_Class_Info<NearPointPCInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<NearPointPCInfo> homePCInfoBase_class_info, Call call, Response response) {
                        for (File file : mCommentPicFiles) {
                            if (file.exists()) {
                                file.delete();
                            }
                        }

                        Intent intent = new Intent();
                        intent.setAction(ConstansUtil.COMMENT_SUCCESS);
                        Bundle bundle = new Bundle();
                        mParkOrderInfo.setOrder_status("5");
                        bundle.putSerializable(ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
                        intent.putExtra(ConstansUtil.FOR_REQUEST_RESULT, bundle);
                        IntentObserable.dispatch(intent);

                        showFiveToast("评价成功");
                        dismmisLoadingDialog();
                        mCommentOrderDialog.dismiss();
                        mParkComment.setText("已评价");
                        //getOrderComment(false);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    showFiveToast("评论失败");
                                    break;
                                case "102":
                                    showFiveToast("评论失败");
                                    break;
                                case "103":
                                    showFiveToast("评论失败");
                                    break;
                                case "104":
                                    showFiveToast("抱歉，已评论过了哦");
                                    break;
                                case "105":
                                    showFiveToast("评论失败");
                                    break;
                            }
                        }
                    }
                });
    }

    private void showChooesPic() {
        switch (mCommentPicFiles.size()) {
            case 0:
                setAddIvNormal();
                setPicOneVisibility(View.GONE);
                setPicTwoVisibility(View.GONE);
                break;
            case 1:
                setAddIvNormal();
                ImageUtil.showRoundPic(mOneIv, mCommentPicFiles.get(0).getAbsolutePath());
                setPicOneVisibility(View.VISIBLE);
                setPicTwoVisibility(View.GONE);
                break;
            case 2:
                setAddIvNormal();
                ImageUtil.showRoundPic(mOneIv, mCommentPicFiles.get(0).getAbsolutePath());
                ImageUtil.showRoundPic(mTwoIv, mCommentPicFiles.get(1).getAbsolutePath());
                setPicOneVisibility(View.VISIBLE);
                setPicTwoVisibility(View.VISIBLE);
                break;
            case 3:
                ImageUtil.showRoundPic(mAddIv, mCommentPicFiles.get(0).getAbsolutePath());
                ImageUtil.showRoundPic(mOneIv, mCommentPicFiles.get(1).getAbsolutePath());
                ImageUtil.showRoundPic(mTwoIv, mCommentPicFiles.get(2).getAbsolutePath());
                mDeleteAddIv.setVisibility(View.VISIBLE);
                setPicOneVisibility(View.VISIBLE);
                setPicTwoVisibility(View.VISIBLE);
                break;
        }
    }

    private void setAddIvNormal() {
        if (mDeleteAddIv.getVisibility() != View.GONE) {
            ImageUtil.showPic(mAddIv, R.drawable.ic_addimg);
            mDeleteAddIv.setVisibility(View.GONE);
        }
    }

    private void setPicOneVisibility(int visibility) {
        if (mOneIv.getVisibility() != visibility) {
            mOneIv.setVisibility(visibility);
            mDeleteOneIv.setVisibility(visibility);
        }
    }

    private void setPicTwoVisibility(int visibility) {
        if (mTwoIv.getVisibility() != visibility) {
            mTwoIv.setVisibility(visibility);
            mDeleteTwoIv.setVisibility(visibility);
        }
    }

    @Override
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ConstansUtil.PHOTO_IMAGE:
                    final List<ImageBean> imageBeans = intent.getParcelableArrayListExtra(ImagePicker.INTENT_RESULT_DATA);
                    for (ImageBean imageBean : imageBeans) {
                        //进行图片逐个压缩
                        if (getContext() != null)
                            Luban.with(getContext())
                                    .load(imageBean.getImagePath())
                                    .ignoreBy(1)
                                    .setTargetDir(getContext().getApplicationContext().getFilesDir().getAbsolutePath())
                                    .setCompressListener(new OnCompressListener() {
                                        @Override
                                        public void onStart() {
                                            // 压缩开始前调用，可以在方法内启动 loading UI
                                        }

                                        @Override
                                        public void onSuccess(File file) {
                                            // 压缩成功后调用，返回压缩后的图片文件
                                            mCommentPicFiles.add(file);
                                            showChooesPic();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            // 当压缩过程出现问题时调用

                                        }
                                    }).launch();
                    }
                    break;
                case ConstansUtil.DIALOG_SHOW:
                    hideOrderDetail();
                    break;
                case ConstansUtil.DIALOG_DISMISS:
                    showOrderDetail();
                    break;
            }
        }
    }
}
