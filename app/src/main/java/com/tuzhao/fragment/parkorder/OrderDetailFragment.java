package com.tuzhao.fragment.parkorder;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.activity.mine.BillingRuleActivity;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.NearPointPCInfo;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.ParkspaceCommentInfo;
import com.tuzhao.info.PropertyPhoto;
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
import com.tuzhao.utils.ViewUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

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

    private CustomDialog mOrderDetailDialog;

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

/*    private ImageView mAddIv;

    private ImageView mDeleteAddIv;

    private ImageView mOneIv;

    private ImageView mDeleteOneIv;

    private ImageView mTwoIv;

    private ImageView mDeleteTwoIv;*/

    private TextView mApplyComment;

    private ImagePicker mPropertyImagePicker;

    private PropertyAdapter mPropertyAdapter;

    private CustomDialog mCustomDialog;

    private CustomDialog mOrderCommentDialog;

    private ParkspaceCommentInfo mCommentInfo;

    private DecimalFormat mDecimalFormat;

    private SoftKeyBroadManager.SoftKeyboardStateListener mKeyboardStateListener;

    private SoftKeyBroadManager mSoftKeyBroadManager;

    private int mChoosePosition;

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
        if (mOrderDetailDialog == null) {
            if (getContext() != null) {
                mOrderDetailDialog = new CustomDialog(getContext(), mParkOrderInfo, true);
            }
        }
        mOrderDetailDialog.show();
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
            /*mAddIv = commentView.findViewById(R.id.comment_order_pic);
            mDeleteAddIv = commentView.findViewById(R.id.comment_order_pic_delete);
            mOneIv = commentView.findViewById(R.id.comment_order_pic_one);
            mDeleteOneIv = commentView.findViewById(R.id.comment_order_pic_one_delete);
            mTwoIv = commentView.findViewById(R.id.comment_order_pic_two);
            mDeleteTwoIv = commentView.findViewById(R.id.comment_order_pic_two_delete);*/
            mApplyComment = commentView.findViewById(R.id.comment_order_apply);
            mCloseDialog = commentView.findViewById(R.id.close_comment);
            RecyclerView recyclerView = commentView.findViewById(R.id.comment_order_pic_rv);
            recyclerView.setLayoutManager(new LinearLayoutManager(commentView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            mPropertyAdapter = new PropertyAdapter();
            recyclerView.setAdapter(mPropertyAdapter);
            mPropertyAdapter.addData(new PropertyPhoto());
            initCommentView();
            //initCommentSoftKeyBorad();
        }
        mCommentOrderDialog.show();
    }

    private void initCommentView() {
        // mCommentPicFiles = new ArrayList<>(3);

        /*mAddIv.setOnClickListener(new View.OnClickListener() {
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
        });*/

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

    private void showDialog() {
        if (mCustomDialog == null) {
            View view = getLayoutInflater().inflate(R.layout.dialog_selete_photo_layout, null);
            mCustomDialog = new CustomDialog(requireContext(), view, true);
            view.findViewById(R.id.exchang_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTakePropertyPhoto();
                    mCustomDialog.dismiss();
                }
            });

            view.findViewById(R.id.delete_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletePhoto(mPropertyAdapter.get(mChoosePosition).getPath());
                    mCustomDialog.dismiss();
                }
            });

            view.findViewById(R.id.cancel_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCustomDialog.dismiss();
                }
            });
        }

        mCustomDialog.show();
    }

    private void startTakePropertyPhoto() {
        if (mPropertyImagePicker == null) {
            mPropertyImagePicker = new ImagePicker()
                    .cachePath(Environment.getExternalStorageDirectory().getAbsolutePath())
                    .needCamera(true)
                    .pickType(ImagePickType.MULTI)
                    .maxNum(3);
        }
        int maxNum = 1;
        if (mPropertyAdapter.get(0).getPath().equals("-1")) {
            maxNum = 3;
        } else if (mPropertyAdapter.getDataSize() == 2 && mPropertyAdapter.get(1).getPath().equals("-1")) {
            maxNum = 2;
        }
        mPropertyImagePicker.maxNum(maxNum);
        mPropertyImagePicker.start(requireActivity(), ConstansUtil.PICTURE_REQUEST_CODE);
    }

    private void handleImageBean(final List<ImageBean> imageBeans) {
        if (imageBeans.size() == 2) {
            switch (mChoosePosition) {
                case 0:
                    compressFirstPhoto(imageBeans.get(0).getImagePath(), new SuccessCallback<File>() {
                        @Override
                        public void onSuccess(File file) {
                            handleCompressPhoto(file, 0);
                            compressSecondPhoto(imageBeans.get(1).getImagePath(), new SuccessCallback<File>() {
                                @Override
                                public void onSuccess(File file) {
                                    handleCompressPhoto(file, 1);
                                }
                            });
                        }
                    });
                    break;
                case 1:
                    compressSecondPhoto(imageBeans.get(0).getImagePath(), new SuccessCallback<File>() {
                        @Override
                        public void onSuccess(File file) {
                            handleCompressPhoto(file, 1);
                            compressThirdPhoto(imageBeans.get(1).getImagePath(), new SuccessCallback<File>() {
                                @Override
                                public void onSuccess(File file) {
                                    handleCompressPhoto(file, 2);
                                }
                            });
                        }
                    });
                    break;
            }
        } else {
            compressFirstPhoto(imageBeans.get(0).getImagePath(), new SuccessCallback<File>() {
                @Override
                public void onSuccess(File file) {
                    handleCompressPhoto(file, 0);
                    compressSecondPhoto(imageBeans.get(1).getImagePath(), new SuccessCallback<File>() {
                        @Override
                        public void onSuccess(File file) {
                            handleCompressPhoto(file, 1);
                            compressThirdPhoto(imageBeans.get(2).getImagePath(), new SuccessCallback<File>() {
                                @Override
                                public void onSuccess(File file) {
                                    handleCompressPhoto(file, 2);
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    private void compressFirstPhoto(String path, SuccessCallback<File> callback) {
        ImageUtil.compressPhoto(requireContext(), path, callback);
    }

    private void compressSecondPhoto(String path, SuccessCallback<File> callback) {
        ImageUtil.compressPhoto(requireContext(), path, callback);
    }

    private void compressThirdPhoto(String path, SuccessCallback<File> callback) {
        ImageUtil.compressPhoto(requireContext(), path, callback);
    }

    private void handleCompressPhoto(File file, int position) {
        switch (position) {
            case 0:
                if (mPropertyAdapter.getDataSize() == 1 && mPropertyAdapter.get(0).getPath().equals("-1")) {
                    //还没有图片
                    PropertyPhoto firstProperty = new PropertyPhoto(file.getAbsolutePath());
                    mPropertyAdapter.notifyAddData(0, firstProperty);
                } else {
                    PropertyPhoto firstProperty = mPropertyAdapter.getData().get(0);
                    firstProperty.setPath(file.getAbsolutePath());
                    firstProperty.setShowProgress(true);
                    firstProperty.setProgress("0%");
                    mPropertyAdapter.notifyDataChange(0, firstProperty);
                }
                uploadPhoto(file);
                break;
            case 1:
                if (mPropertyAdapter.getDataSize() < 3) {
                    PropertyPhoto secondProperty = new PropertyPhoto(file.getAbsolutePath());
                    mPropertyAdapter.notifyAddData(mPropertyAdapter.getDataSize() - 1, secondProperty);
                } else {
                    PropertyPhoto secondProperty = mPropertyAdapter.getData().get(1);
                    secondProperty.setPath(file.getAbsolutePath());
                    secondProperty.setProgress("0%");
                    secondProperty.setShowProgress(true);
                    mPropertyAdapter.notifyDataChange(1, secondProperty);
                }
                uploadPhoto(file);
                break;
            case 2:
                if (mPropertyAdapter.getDataSize() < 3) {
                    PropertyPhoto thirdProperty = new PropertyPhoto(file.getAbsolutePath());
                    mPropertyAdapter.notifyAddData(thirdProperty);
                } else if (mPropertyAdapter.getDataSize() == 3) {
                    PropertyPhoto thirdProperty = mPropertyAdapter.getData().get(2);
                    thirdProperty.setPath(file.getAbsolutePath());
                    thirdProperty.setUploadSuccess(false);
                    thirdProperty.setShowProgress(true);
                    thirdProperty.setProgress("0%");
                    mPropertyAdapter.notifyDataChange(2, thirdProperty);
                }
                uploadPhoto(file);
                break;
        }
    }

    private void uploadPhoto(final File file) {
        OkGo.post(HttpConstants.uploadPicture)
                .retryCount(0)
                .headers("token", com.tuzhao.publicmanager.UserManager.getInstance().getUserInfo().getToken())
                .params("type", 2)
                .params("picture", file)
                .execute(new JsonCallback<Base_Class_Info<String>>() {

                    @Override
                    public void onSuccess(Base_Class_Info<String> stringBase_class_info, Call call, Response response) {
                        setServerUrl(file.getAbsolutePath(), HttpConstants.ROOT_IMG_URL_PSCOM + stringBase_class_info.data);
                        setUploadProgress(file.getAbsolutePath(), 1);
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.upProgress(currentSize, totalSize, progress, networkSpeed);
                        if (progress != 1) {
                            setUploadProgress(file.getAbsolutePath(), progress);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        deletePhoto(file.getAbsolutePath());
                    }
                });
    }

    private void setUploadProgress(String filePath, float progress) {
        String progressString = (int) (progress * 100) + "%";
        for (int i = 0; i < mPropertyAdapter.getDataSize(); i++) {
            if (mPropertyAdapter.get(i).getPath().equals(filePath)) {
                PropertyPhoto firstProperty = mPropertyAdapter.getData().get(i);
                firstProperty.setProgress(progressString);
                if (progress == 1.0) {
                    firstProperty.setShowProgress(false);
                }
                mPropertyAdapter.notifyDataChange(i, firstProperty, 1);
                break;
            }
        }
    }

    private void setServerUrl(String filePath, String url) {
        for (int i = 0; i < mPropertyAdapter.getDataSize(); i++) {
            if (mPropertyAdapter.get(i).getPath().equals(filePath)) {
                PropertyPhoto firstProperty = mPropertyAdapter.get(i);
                firstProperty.setPath(url);
                firstProperty.setShowProgress(false);
                firstProperty.setUploadSuccess(true);
                mPropertyAdapter.notifyDataChange(i, firstProperty, 1);
                break;
            }
        }
    }

    private void deletePhoto(String filePath) {
        for (int i = 0; i < mPropertyAdapter.getDataSize(); i++) {
            if (mPropertyAdapter.get(i).getPath().equals(filePath)) {
                if (i == 2) {
                    mPropertyAdapter.notifyDataChange(2, new PropertyPhoto());
                } else {
                    mPropertyAdapter.notifyRemoveData(i);
                }
                break;
            }
        }

        if (mPropertyAdapter.getDataSize() == 0 || !mPropertyAdapter.get(mPropertyAdapter.getDataSize() - 1).getPath().equals("-1")) {
            //如果第一张张不是拍摄图，则添加拍摄图
            mPropertyAdapter.notifyAddData(new PropertyPhoto());
        }

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
            ImageView firstPic = constraintLayout.findViewById(R.id.comment_order_pic);
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
        showLoadingDialog("评价中");
        StringBuilder stringBuilder = new StringBuilder();
        for (PropertyPhoto propertyPhoto : mPropertyAdapter.getData()) {
            if (!propertyPhoto.getPath().equals("-1")) {
                stringBuilder.append(propertyPhoto.getPath().replace(HttpConstants.ROOT_IMG_URL_PSCOM, ""));
                stringBuilder.append(",");
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        } else {
            stringBuilder.append("-1");
        }
        OkGo.post(HttpConstants.addPsComment)
                .tag(TAG)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("parkspace_id", mParkOrderInfo.getBelong_park_space())
                .params("city_code", mParkOrderInfo.getCitycode())
                .params("order_id", mParkOrderInfo.getId())
                .params("grade", mDecimalFormat.format(mCBRatingBar.getStarProgress() / 20.0))
                .params("content", mCommentEt.getText().toString())
                .params("imgs", stringBuilder.toString())
                .execute(new JsonCallback<Base_Class_Info<NearPointPCInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<NearPointPCInfo> homePCInfoBase_class_info, Call call, Response response) {
                        Intent intent = new Intent();
                        intent.setAction(ConstansUtil.COMMENT_SUCCESS);
                        Bundle bundle = new Bundle();
                        mParkOrderInfo.setOrder_status("5");
                        bundle.putSerializable(ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
                        intent.putExtra(ConstansUtil.FOR_REQUEST_RESULT, bundle);
                        IntentObserable.dispatch(intent);

                        mParkComment.setText("已评价");
                        showFiveToast("评价成功");
                        dismmisLoadingDialog();
                        mCommentOrderDialog.dismiss();
                        //getOrderComment(false);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "104":
                                    showFiveToast("抱歉，已评论过了哦");
                                    break;
                                default:
                                    showFiveToast("评论失败，请稍后重试");
                                    break;
                            }
                        }
                    }
                });
    }

    /*private void showChooesPic() {
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
    }*/

    @Override
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ConstansUtil.PHOTO_IMAGE:
                    final List<ImageBean> imageBeans = intent.getParcelableArrayListExtra(ImagePicker.INTENT_RESULT_DATA);
                    if (imageBeans.size() == 1) {
                        ImageUtil.compressPhoto(requireContext(), imageBeans.get(0).getImagePath(), new SuccessCallback<File>() {
                            @Override
                            public void onSuccess(File file) {
                                handleCompressPhoto(file, mChoosePosition);
                            }
                        });
                    } else {
                        handleImageBean(imageBeans);
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

    class PropertyAdapter extends BaseAdapter<PropertyPhoto> {

        @Override
        protected void conver(@NonNull BaseViewHolder holder, final PropertyPhoto propertyPhoto, final int position) {
            ImageView imageView = holder.getView(R.id.property_photo_iv);
            TextView textView = holder.getView(R.id.property_upload_tv);
            Log.e(TAG, "conver: " + propertyPhoto.getPath() + "  position:" + position);
            if (propertyPhoto.getPath().equals("-1")) {
                ImageUtil.showPic(imageView, R.drawable.ic_addimg);
                if (isVisible(textView)) {
                    textView.setVisibility(View.GONE);
                }
                ViewUtil.showProgressStatus(textView, false);
            } else {
                if (imageView.getPaddingTop() != 0) {
                    imageView.setPadding(0, 0, 0, 0);
                    imageView.setBackgroundResource(0);
                }
                ImageUtil.showPicWithNoAnimate(imageView, propertyPhoto.getPath());
                ViewUtil.showProgressStatus(textView, propertyPhoto.isShowProgress());
                textView.setText(propertyPhoto.getProgress());
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mChoosePosition = position;
                    if (propertyPhoto.getPath().equals("-1")) {
                        startTakePropertyPhoto();
                    } else {
                        showDialog();
                    }
                }
            });

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //do nothing
                }
            });
        }

        @Override
        protected int itemViewId() {
            return R.layout.item_property_photo_layout;
        }

    }

}
