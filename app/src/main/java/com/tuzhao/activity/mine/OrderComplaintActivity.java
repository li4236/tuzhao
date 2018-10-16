package com.tuzhao.activity.mine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.photopicker.controller.PhotoPickConfig;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Pair;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.UploadPhotoInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.customView.CheckTextView;
import com.tuzhao.publicwidget.customView.FlexBoxLayoutManager;
import com.tuzhao.publicwidget.upload.UploadPicture;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/7/27.
 */
public class OrderComplaintActivity extends BaseStatusActivity implements View.OnClickListener {

    private ImageView mParkSpaceIv;

    private TextView mParkSpaceName;

    private TextView mParkDuration;

    private EditText mQuestionDescription;

    private TextView mQeustionTextNumber;

    private TextView mConfirmSubmit;

    private ReasonApdater mApdater;

    private ParkOrderInfo mParkOrderInfo;

    private UploadPicture<UploadAdapter> mUploadPicture;

    private int mReasonPosition = -1;

    @Override
    protected int resourceId() {
        return R.layout.activity_order_complaint_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mParkSpaceIv = findViewById(R.id.park_space_iv);
        mParkSpaceName = findViewById(R.id.park_space_name);
        mParkDuration = findViewById(R.id.grace_time);
        mQuestionDescription = findViewById(R.id.question_descrption_et);
        mQeustionTextNumber = findViewById(R.id.input_text_number);
        mConfirmSubmit = findViewById(R.id.confirm_submit);
        RecyclerView reasonRecyclerView = findViewById(R.id.complaint_reason_rv);
        RecyclerView recyclerView = findViewById(R.id.complaint_pictrue_rv);

        initEditText();

        reasonRecyclerView.setLayoutManager(new FlexBoxLayoutManager());
        mApdater = new ReasonApdater();
        reasonRecyclerView.setAdapter(mApdater);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mUploadPicture = new UploadPicture<>(this, new UploadAdapter(), 4);
        mUploadPicture.setStartPadding(DensityUtil.dp2px(this, 10));
        mUploadPicture.setTopPadding(DensityUtil.dp2px(this, 12));
        recyclerView.setAdapter(mUploadPicture.getAdapter());
        mUploadPicture.getAdapter().addData(new UploadPhotoInfo());
        mQuestionDescription.clearFocus();

        mConfirmSubmit.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        mParkOrderInfo = getIntent().getParcelableExtra(ConstansUtil.PARK_ORDER_INFO);
        if (mParkOrderInfo == null) {
            showFiveToast("获取订单信息失败，请稍后重试");
            finish();
        } else {
            ImageUtil.showImgPic(mParkSpaceIv, HttpConstants.ROOT_IMG_URL_PS + mParkOrderInfo.getPictures().split(",")[0]);
            mParkSpaceName.setText(mParkOrderInfo.getParkLotName() + mParkOrderInfo.getParkSpaceLocation());
            String parkDuration = "时长：" + DateUtil.getDistanceForDayHourMinute(mParkOrderInfo.getPark_start_time(),
                    mParkOrderInfo.getPark_end_time()) + "  日期：" + DateUtil.getYearToDayWithPointText(mParkOrderInfo.getPark_start_time());
            mParkDuration.setText(parkDuration);

            initcomplaintReason();
        }
    }

    private void initcomplaintReason() {
        List<Pair<String, Boolean>> list = new ArrayList<>();
        switch (mParkOrderInfo.getOrderStatus()) {
            case "1":
                list.add(new Pair<>("无法开锁", false));
                list.add(new Pair<>("车位有车", false));
                list.add(new Pair<>("其他", false));
                break;
            case "2":
                list.add(new Pair<>("无法关锁", false));
                list.add(new Pair<>("结束停车未停止计费", false));
                list.add(new Pair<>("其他", false));
                break;
            case "3":
            case "4":
            case "5":
                list.add(new Pair<>("订单计费有误", false));
                list.add(new Pair<>("其他", false));
                break;
        }
        mApdater.setNewData(list);
    }

    private void initEditText() {
        mQuestionDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mQeustionTextNumber.setText(getTextLength(mQuestionDescription) + "/500");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @NonNull
    @Override
    protected String title() {
        return "订单投诉";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PhotoPickConfig.PICK_MORE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //选择的图片
            Intent intent = new Intent(ConstansUtil.PHOTO_IMAGE);
            intent.putStringArrayListExtra(ConstansUtil.INTENT_MESSAGE, data.getStringArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST));
            IntentObserable.dispatch(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUploadPicture.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_submit:
                if (mReasonPosition == -1) {
                    showFiveToast("请选择投诉理由");
                } else if (mApdater.get(mReasonPosition).getFirst().equals("其他") && getTextLength(mQuestionDescription) == 0) {
                    showFiveToast("请对您要投诉的问题进行说明");
                } else {
                    mConfirmSubmit.setClickable(false);
                    orderComplaint();
                }
                break;
        }
    }

    private void orderComplaint() {
        showFiveToast("正在投诉...");
        getOkGo(HttpConstants.orderComplaint)
                .params("cityCode", mParkOrderInfo.getCityCode())
                .params("orderId", mParkOrderInfo.getId())
                .params("reason", mApdater.get(mReasonPosition).getFirst())
                .params("detailDescription", getText(mQuestionDescription))
                .params("complaintPhoto", mUploadPicture.getUploadPictures())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        showFiveToast("投诉成功，我们将尽快为您处理");
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mConfirmSubmit.setClickable(true);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    showFiveToast("请选择投诉理由");
                                    break;
                                case "102":
                                case "103":
                                case "104":
                                case "105":
                                case "106":
                                    showFiveToast(ConstansUtil.SERVER_ERROR);
                                    break;
                            }
                        }
                    }
                });
    }

    class ReasonApdater extends BaseAdapter<com.tuzhao.info.Pair<String, Boolean>> {

        @Override
        protected void conver(@NonNull BaseViewHolder holder, com.tuzhao.info.Pair<String, Boolean> stringBooleanPair, final int position) {
            CheckTextView checkedTextView = holder.getView(R.id.reason);
            checkedTextView.setText(stringBooleanPair.getFirst());
            checkedTextView.setNoCheckDrawble(R.drawable.g6_stroke_all_3dp);
            checkedTextView.setCheckDrawable(R.drawable.solide_y18_stroke_y3_all_3dp);

            checkedTextView.setChecked(stringBooleanPair.getSecond());
            if (stringBooleanPair.getSecond()) {
                checkedTextView.setTextColor(ConstansUtil.Y3_COLOR);
            } else {
                checkedTextView.setTextColor(ConstansUtil.B1_COLOR);
            }

            if (checkedTextView.getOnCheckChangeListener() == null) {
                checkedTextView.setOnCheckChangeListener(new CheckTextView.OnCheckChangeListener() {
                    @Override
                    public void onCheckChange(boolean isCheck) {
                        if (isCheck) {
                            if (mReasonPosition == -1) {
                                mReasonPosition = position;
                                get(position).setSecond(true);
                                notifyDataChange(position, position);
                                // TODO: 2018/10/13 如果position位置的Viewholder已经被回收了，则会调用onBindViewHolder方法来更新数据,而不会调用带payload方法的onBindViewHolder来更新
                            } else if (mReasonPosition != position) {
                                get(mReasonPosition).setSecond(false);
                                notifyDataChange(mReasonPosition, position);

                                mReasonPosition = position;
                                get(position).setSecond(true);
                                notifyDataChange(position, position);
                            }
                        }
                    }
                });
            }
        }

        @Override
        protected void conver(@NonNull BaseViewHolder holder, com.tuzhao.info.Pair<String, Boolean> stringBooleanPair, int position, @NonNull List<Object> payloads) {
            super.conver(holder, stringBooleanPair, position, payloads);
            if (payloads.size() == 1) {
                CheckTextView checkedTextView = holder.getView(R.id.reason);
                checkedTextView.setChecked(stringBooleanPair.getSecond());
                if (stringBooleanPair.getSecond()) {
                    checkedTextView.setTextColor(ConstansUtil.Y3_COLOR);
                } else {
                    checkedTextView.setTextColor(ConstansUtil.B1_COLOR);
                }

            }
        }

        @Override
        protected int itemViewId() {
            return R.layout.item_check_text_view_layout;
        }

    }

    class UploadAdapter extends BaseAdapter<UploadPhotoInfo> {

        @Override
        protected void conver(@NonNull BaseViewHolder holder, final UploadPhotoInfo uploadPhotoInfo, final int position) {
            mUploadPicture.conver((ImageView) holder.getView(R.id.complaint_photo_iv), (TextView) holder.getView(R.id.complaint_upload_tv), uploadPhotoInfo, position);
            holder.getView(R.id.complaint_photo_iv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUploadPicture.setChoosePosition(position);
                    if (uploadPhotoInfo.getPath().equals("-1")) {
                        mUploadPicture.startTakePropertyPhoto();
                    } else {
                        mUploadPicture.startTakePropertyPhoto(1);
                    }
                }
            });

            holder.getView(R.id.delete_complaint_photo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUploadPicture.deletePhoto(uploadPhotoInfo.getPath());
                }
            });

            if (!uploadPhotoInfo.getPath().equals("-1") && uploadPhotoInfo.isUploadSuccess() && !uploadPhotoInfo.isShowProgress()) {
                showView(holder.getView(R.id.delete_complaint_photo));
            } else {
                goneView(holder.getView(R.id.delete_complaint_photo));
            }
        }

        @Override
        protected int itemViewId() {
            return R.layout.item_complaint_suggest_layout;
        }

    }

}
