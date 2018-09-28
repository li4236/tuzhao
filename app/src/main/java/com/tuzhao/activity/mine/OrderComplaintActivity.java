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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.photopicker.controller.PhotoPickConfig;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.UploadPhotoInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.upload.UploadPicture;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/7/27.
 */
public class OrderComplaintActivity extends BaseStatusActivity implements View.OnClickListener {

    private TextView mComplaintReason;

    private ImageView mParkSpaceIv;

    private TextView mParkSpaceName;

    private TextView mParkDuration;

    private EditText mQuestionDescription;

    private TextView mQeustionTextNumber;

    private TextView mConfirmSubmit;

    private String[] mSpinnerString = {"订单计费有误", "结束停车未停止计费", "其他"};

    private ParkOrderInfo mParkOrderInfo;

    private UploadPicture<UploadAdapter> mUploadPicture;

    private CustomDialog mCustomDialog;

    private boolean mHaveChooseReason = false;

    @Override
    protected int resourceId() {
        return R.layout.activity_order_complaint_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mComplaintReason = findViewById(R.id.complaint_reason);
        mParkSpaceIv = findViewById(R.id.park_space_iv);
        mParkSpaceName = findViewById(R.id.park_space_name);
        mParkDuration = findViewById(R.id.park_duration);
        mQuestionDescription = findViewById(R.id.question_descrption_et);
        mQeustionTextNumber = findViewById(R.id.input_text_number);
        mConfirmSubmit = findViewById(R.id.confirm_submit);
        RecyclerView recyclerView = findViewById(R.id.complaint_reason_rv);

        initComplaintReasonDialog();
        initEditText();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mUploadPicture = new UploadPicture<>(this, new UploadAdapter(), 4);
        mUploadPicture.setStartPadding(DensityUtil.dp2px(this, 10));
        mUploadPicture.setTopPadding(DensityUtil.dp2px(this, 12));
        recyclerView.setAdapter(mUploadPicture.getAdapter());
        mUploadPicture.getAdapter().addData(new UploadPhotoInfo());
        mQuestionDescription.clearFocus();

        mComplaintReason.setOnClickListener(this);
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
        }
    }

    private void initComplaintReasonDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_list_layout, null);
        mCustomDialog = new CustomDialog(this, view, true);
        ListView listView = view.findViewById(R.id.dialog_lv);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.item_center_text_b1_layout, mSpinnerString);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mHaveChooseReason) {
                    mComplaintReason.setTextColor(ConstansUtil.B1_COLOR);
                    mHaveChooseReason = true;
                }
                mComplaintReason.setText(mSpinnerString[position]);
                mCustomDialog.dismiss();
            }
        });
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
            case R.id.complaint_reason:
                mCustomDialog.show();
                break;
            case R.id.confirm_submit:
                if (!mHaveChooseReason) {
                    showFiveToast("请选择投诉理由");
                } else if (getText(mComplaintReason).equals("其他") && getTextLength(mQuestionDescription) == 0) {
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
                .params("reason", mHaveChooseReason ? getText(mComplaintReason) : "")
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
