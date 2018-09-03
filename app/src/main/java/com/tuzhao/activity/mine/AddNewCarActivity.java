package com.tuzhao.activity.mine;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.photopicker.controller.PhotoPickConfig;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.customView.PlusView;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.IdentifyPhotoHelper;
import com.tuzhao.publicwidget.upload.MyFile;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DataUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.GlideApp;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.ViewUtil;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/5/2.
 */

public class AddNewCarActivity extends BaseStatusActivity implements View.OnClickListener {

    private EditText mCarNumber;

    private EditText mCarOwner;

    private PlusView[] mPlusViews;

    private ImageView[] mImageViews;

    private TextView[] mTextViews;

    private CustomDialog mCustomDialog;

    private IdentifyPhotoHelper[] mIdentifyPhotoHelpers;

    private int mChoosePosition;

    private String[] mPath;

    private CheckBox mCheckBox;

    private TextView mAgressProtocol;

    @Override
    protected int resourceId() {
        return R.layout.activity_add_new_car_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mCarNumber = findViewById(R.id.car_number_et);
        mCarOwner = findViewById(R.id.car_owner_et);
        PlusView idCardPositivePv = findViewById(R.id.id_card_positive_pv);
        PlusView idCardNegativePv = findViewById(R.id.id_card_negative_pv);
        PlusView driveLicensePv = findViewById(R.id.driver_license_pv);
        PlusView vehicleLicensePositivePv = findViewById(R.id.vehicle_license_positive_pv);
        PlusView vehicleLicenseNegativePv = findViewById(R.id.vehicle_license_negativie_pv);
        PlusView groupPhotoPv = findViewById(R.id.group_photo_pv);
        ImageView idCardPositiveIv = findViewById(R.id.id_card_positive_iv);
        ImageView idCardNegativeIv = findViewById(R.id.id_card_negativie_iv);
        ImageView driveLicenseIv = findViewById(R.id.driver_license_iv);
        ImageView vehicleLicensePositiveIv = findViewById(R.id.vehicle_license_positive_iv);
        ImageView vehicleLicenseNagativeIv = findViewById(R.id.vehicle_license_negativie_iv);
        ImageView groupPhotoIv = findViewById(R.id.group_photo_iv);
        TextView idCardPositiveUploadTv = findViewById(R.id.id_card_positive_upload_tv);
        TextView idCardNegativieUploadTv = findViewById(R.id.id_card_negative_upload_tv);
        TextView driveLicenseUploadTv = findViewById(R.id.driver_license_upload_tv);
        TextView vehicleLiensePositiveUploadTv = findViewById(R.id.vehicle_license_positive_upload_tv);
        TextView vehicleLienseNegativieUploadTv = findViewById(R.id.vehicle_license_negativie_upload_tv);
        TextView groupPhotoUploadTv = findViewById(R.id.group_photo_upload_tv);
        mCheckBox = findViewById(R.id.agress_protocol_cb);
        mAgressProtocol = findViewById(R.id.agress_protocol_tv);

        mPlusViews = new PlusView[]{idCardPositivePv, idCardNegativePv, driveLicensePv, vehicleLicensePositivePv, vehicleLicenseNegativePv, groupPhotoPv};
        mImageViews = new ImageView[]{idCardPositiveIv, idCardNegativeIv, driveLicenseIv, vehicleLicensePositiveIv, vehicleLicenseNagativeIv, groupPhotoIv};
        mTextViews = new TextView[]{idCardPositiveUploadTv, idCardNegativieUploadTv, driveLicenseUploadTv, vehicleLiensePositiveUploadTv, vehicleLienseNegativieUploadTv, groupPhotoUploadTv};

        TextView carNumber = findViewById(R.id.car_number_tv);
        ViewUtil.addEndText(carNumber, 1);

        findViewById(R.id.apply_add_new_car).setOnClickListener(this);

        mCarNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (DataUtil.containLowerCase(getText(mCarNumber))) {
                    mCarNumber.setText(getText(mCarNumber).toUpperCase());
                    setSelection(mCarNumber);
                }
            }
        });

    }

    @Override
    protected void initData() {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder("我同意《途找信息平台用户协议》请仔细阅读并勾选确认");
        stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0101")), 15, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                showFiveToast("哈哈");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ConstansUtil.Y3_COLOR);
                ds.setUnderlineText(false);
            }
        };// TODO: 2018/9/3
        stringBuilder.setSpan(clickableSpan, 3, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mAgressProtocol.setMovementMethod(LinkMovementMethod.getInstance());        //不设置点击事件没反应
        mAgressProtocol.setText(stringBuilder);

        mPath = new String[mImageViews.length];
        for (int i = 0; i < mPath.length; i++) {
            mPath[i] = "-1";
            final int finalI = i;
            mImageViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mChoosePosition = finalI;
                    if (mPath[finalI].equals("-1")) {
                        showDialog();
                    } else {
                        ImageUtil.startTakePhotoAndCrop(AddNewCarActivity.this);
                    }
                }
            });
            mTextViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        initDialogHelper();
    }

    @NonNull
    @Override
    protected String title() {
        return "添加车辆";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.apply_add_new_car:
                if (getTextLength(mCarNumber) == 0) {
                    showFiveToast("请输入车牌号码");
                } else if (!DateUtil.isCarNumber(getText(mCarNumber))) {
                    showFiveToast("你输入的车牌号不正确哦");
                } else if (TextUtils.isEmpty(mCarOwner.getText().toString().trim())) {
                    showFiveToast("请输入车牌所有人");
                } else if (mPath[0].equals("-1")) {
                    showFiveToast("请上传身份证人像面照片");
                } else if (mPath[1].equals("-1")) {
                    showFiveToast("请上传身份证国徽面照片");
                } else if (mPath[2].equals("-1")) {
                    showFiveToast("请上传驾驶证主页照片");
                } else if (mPath[3].equals("-1")) {
                    showFiveToast("请上传行驶证主页照片");
                } else if (mPath[4].equals("-1")) {
                    showFiveToast("请上传行驶证副页照片");
                } else if (mPath[5].equals("-1")) {
                    showFiveToast("请上传人车合影");
                } else if (!mCheckBox.isChecked()) {
                    showFiveToast("请仔细阅读《途找信息平台用户协议》并勾选确认");
                } else if (!mPath[0].startsWith(HttpConstants.ROOT_IMG_URL_ID_CARD)) {
                    showFiveToast("请等待身份证人像面上传完成");
                } else if (!mPath[1].startsWith(HttpConstants.ROOT_IMG_URL_ID_CARD)) {
                    showFiveToast("请等待身份证国徽面上传完成");
                } else if (!mPath[2].startsWith(HttpConstants.ROOT_IMG_URL_DRIVER_LICENSE)) {
                    showFiveToast("请等待驾驶证主页照上传完成");
                } else if (!mPath[3].startsWith(HttpConstants.ROOT_IMG_URL_VEHICLE)) {
                    showFiveToast("请等待行驶证主页照上传完成");
                } else if (!mPath[4].startsWith(HttpConstants.ROOT_IMG_URL_VEHICLE)) {
                    showFiveToast("请等待行驶证副页照上传完成");
                } else if (!mPath[5].startsWith(HttpConstants.ROOT_IMG_URL_GROUP_PHOTO)) {
                    showFiveToast("请等待人车合照上传完成");
                } else {
                    applyAddNewCar();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PhotoPickConfig.PICK_CLIP_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri resultUri = Uri.parse(data.getStringExtra(PhotoPickConfig.EXTRA_CLIP_PHOTO));
            final File file = new File(resultUri.getPath());
            if (file.exists()) {
                mPath[mChoosePosition] = file.getAbsolutePath();
                showLoadingDialog("压缩中...");
                ImageUtil.compressPhoto(AddNewCarActivity.this, file.getAbsolutePath(), new SuccessCallback<MyFile>() {
                    @Override
                    public void onSuccess(MyFile myFile) {
                        for (int i = 0; i < mPath.length; i++) {
                            if (mPath[i].equals(myFile.getUncompressName())) {
                                mPath[i] = myFile.getAbsolutePath();
                                ImageUtil.showPic(mImageViews[i], mPath[i]);
                                hideView(mPlusViews[mChoosePosition]);
                                dismmisLoadingDialog();
                                uploadPicture(myFile, i);
                                break;
                            }
                        }
                    }
                });
            } else {
                showFiveToast("获取图片失败，请更换图片");
            }
        }
    }

    private void initDialogHelper() {
        mIdentifyPhotoHelpers = new IdentifyPhotoHelper[mPath.length];
        IdentifyPhotoHelper identifyPhotoHelper;
        for (int i = 0; i < mIdentifyPhotoHelpers.length; i++) {
            identifyPhotoHelper = new IdentifyPhotoHelper(this, i);
            identifyPhotoHelper.setCancelListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCustomDialog.dismiss();
                }
            });
            identifyPhotoHelper.setUploadListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageUtil.startTakePhotoAndCrop(AddNewCarActivity.this);
                    mCustomDialog.dismiss();
                }
            });
            mIdentifyPhotoHelpers[i] = identifyPhotoHelper;
        }
    }

    private void showDialog() {
        if (mCustomDialog != null) {
            mCustomDialog.cancel();
        }
        mCustomDialog = new CustomDialog(this, mIdentifyPhotoHelpers[mChoosePosition].getView(), true);
        mCustomDialog.show();
    }

    private void setUploadProgress(String filePath, float progress) {
        String progressString = (int) (progress * 100) + "%";
        for (int i = 0; i < mPath.length; i++) {
            if (mPath[i].equals(filePath)) {
                if (progress == 1.0) {
                    goneView(mTextViews[i]);
                } else {
                    mTextViews[i].setText(progressString);
                }
                break;
            }
        }
    }

    private void setServerUrl(String filePath, String url) {
        for (int i = 0; i < mPath.length; i++) {
            if (mPath[i].equals(filePath)) {
                mPath[i] = url;
                goneView(mTextViews[i]);
                break;
            }
        }
    }

    private void uploadPicture(final File file, final int choosePosition) {
        int type = 0;
        if (choosePosition == 2) {
            type = 3;
        } else if (mChoosePosition == 3 || mChoosePosition == 4) {
            type = 5;
        } else if (mChoosePosition == 5) {
            type = 6;
        }
        showView(mTextViews[mChoosePosition]);

        OkGo.post(HttpConstants.uploadPicture)
                .retryCount(0)
                .headers("token", com.tuzhao.publicmanager.UserManager.getInstance().getUserInfo().getToken())
                .params("type", type)
                .params("picture", file)
                .execute(new JsonCallback<Base_Class_Info<String>>() {

                    @Override
                    public void onSuccess(Base_Class_Info<String> stringBase_class_info, Call call, Response response) {
                        switch (choosePosition) {
                            case 0:
                            case 1:
                                setServerUrl(file.getAbsolutePath(), HttpConstants.ROOT_IMG_URL_ID_CARD + stringBase_class_info.data);
                                break;
                            case 2:
                                setServerUrl(file.getAbsolutePath(), HttpConstants.ROOT_IMG_URL_DRIVER_LICENSE + stringBase_class_info.data);
                                break;
                            case 3:
                            case 4:
                                setServerUrl(file.getAbsolutePath(), HttpConstants.ROOT_IMG_URL_VEHICLE + stringBase_class_info.data);
                                break;
                            case 5:
                                setServerUrl(file.getAbsolutePath(), HttpConstants.ROOT_IMG_URL_GROUP_PHOTO + stringBase_class_info.data);
                                break;
                        }
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
                        if (!handleException(e)) {

                        }
                    }
                });
    }

    private void deletePhoto(String photoPath) {
        for (int i = 0; i < mPath.length; i++) {
            if (mPath[i].equals(photoPath)) {
                mPath[i] = "-1";
                showView(mPlusViews[i]);
                mImageViews[i].setImageDrawable(null);
                goneView(mTextViews[i]);
                break;
            }
        }
    }

    private void applyAddNewCar() {
        getOkGo(HttpConstants.applyAddNewCar)
                .params("carNumber", getText(mCarNumber))
                .params("carOwner", mCarOwner.getText().toString())
                .params("idCard", mPath[0].replaceAll(HttpConstants.ROOT_IMG_URL_ID_CARD, "") + "," +
                        mPath[1].replaceAll(HttpConstants.ROOT_IMG_URL_ID_CARD, ""))
                .params("driverLicense", mPath[2].replaceAll(HttpConstants.ROOT_IMG_URL_DRIVER_LICENSE, ""))
                .params("vehicleLicense", mPath[3].replaceAll(HttpConstants.ROOT_IMG_URL_VEHICLE, "") + "," +
                        mPath[4].replaceAll(HttpConstants.ROOT_IMG_URL_VEHICLE, ""))
                .params("groupPhoto", mPath[5].replaceAll(HttpConstants.ROOT_IMG_URL_GROUP_PHOTO, ""))
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> voidBase_class_info, Call call, Response response) {
                        Intent intent = new Intent();
                        intent.putExtra(ConstansUtil.INTENT_MESSAGE, getText(mCarNumber));
                        setResult(RESULT_OK, intent);
                        showFiveToast("提交成功，我们会尽快为你审核");
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    showFiveToast("请输入您的车牌号码");
                                    break;
                                case "102":
                                    showFiveToast("请输入车辆所有人");
                                    break;
                                case "103":
                                    ViewUtil.showCertificationDialog(AddNewCarActivity.this, "添加车辆");
                                    break;
                                case "104":
                                    showFiveToast("驾驶证照片异常，请重新选择");
                                    break;
                                case "105":
                                    showFiveToast("身份证照片异常，请重新选择");
                                    break;
                                case "106":
                                    showFiveToast("行驶证照片异常，请重新选择");
                                    break;
                                case "107":
                                    showFiveToast("人车合照异常，请重新选择");
                                    break;
                                case "108":
                                    showFiveToast(ConstansUtil.SERVER_ERROR);
                                    break;
                                case "109":
                                    showFiveToast("你已添加过该车辆了哦");
                                    break;
                            }
                        }
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlideApp.get(getApplicationContext()).clearMemory();
    }

}
