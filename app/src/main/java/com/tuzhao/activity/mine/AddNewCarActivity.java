package com.tuzhao.activity.mine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lwkandroid.imagepicker.ImagePicker;
import com.lwkandroid.imagepicker.data.ImageBean;
import com.lwkandroid.imagepicker.data.ImagePickType;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.UploadPhotoInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.upload.MyFile;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DataUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.ViewUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/5/2.
 */

public class AddNewCarActivity extends BaseStatusActivity implements View.OnClickListener {

    //private TextView mLicensePlateAttribution;

    private EditText mCarNumber;

    private EditText mCarOwner;

    private static final int REQUEST_CODE_PICKER = 0x111;

    private ImagePicker mImagePicker;

    private CustomDialog mCustomDialog;

    private int mChoosePosition;

    private IdentifyAdapter mAdapter;

    private TextView mAgressProtocol;

    @Override
    protected int resourceId() {
        return R.layout.activity_add_new_car_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //mLicensePlateAttribution = findViewById(R.id.license_plate_attribution);
        mCarNumber = findViewById(R.id.car_number_et);
        mCarOwner = findViewById(R.id.car_owner_et);
        mAgressProtocol = findViewById(R.id.agress_protocol_tv);

        findViewById(R.id.apply_add_new_car).setOnClickListener(this);

        RecyclerView recyclerView = findViewById(R.id.identify_information_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setNestedScrollingEnabled(false);
        mAdapter = new IdentifyAdapter(getAdapterData());
        recyclerView.setAdapter(mAdapter);

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
                Log.e(TAG, "onClick: " + widget.getId());
                showFiveToast("哈哈");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ConstansUtil.Y3_COLOR);
                ds.setUnderlineText(false);
            }
        };
        stringBuilder.setSpan(clickableSpan, 3, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mAgressProtocol.setMovementMethod(LinkMovementMethod.getInstance());        //不设置点击事件没反应
        mAgressProtocol.setText(stringBuilder);

        initImagePicker();
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
                } else {
                    if (UserManager.getInstance().getUserInfo().getCar_number().contains(getText(mCarNumber))) {
                        showFiveToast("你已添加过该车辆了哦");
                    } else {
                        applyAddNewCar();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            List<ImageBean> list = data.getParcelableArrayListExtra(ImagePicker.INTENT_RESULT_DATA);
            ImageUtil.compressPhoto(AddNewCarActivity.this, list.get(0).getImagePath(), new SuccessCallback<MyFile>() {
                @Override
                public void onSuccess(MyFile file) {
                    UploadPhotoInfo uploadPhotoInfo = mAdapter.get(mChoosePosition);
                    uploadPhotoInfo.setShowProgress(true);
                    uploadPhotoInfo.setPath(file.getAbsolutePath());
                    uploadPhotoInfo.setProgress("0%");
                    mAdapter.notifyDataChange(mChoosePosition, uploadPhotoInfo);
                    uploadPicture(file, mChoosePosition);
                }
            });
        }
    }

    private List<UploadPhotoInfo> getAdapterData() {
        List<UploadPhotoInfo> uploadPhotoInfos = new ArrayList<>();
        uploadPhotoInfos.add(new UploadPhotoInfo().setName("上传身份证（人像面）"));
        uploadPhotoInfos.add(new UploadPhotoInfo().setName("上传身份证（国徽面）"));
        uploadPhotoInfos.add(new UploadPhotoInfo().setName("上传驾照主页"));
        uploadPhotoInfos.add(new UploadPhotoInfo().setName("上传驾照副页"));
        uploadPhotoInfos.add(new UploadPhotoInfo().setName("上传行驶证正面"));
        uploadPhotoInfos.add(new UploadPhotoInfo().setName("上传行驶证反面"));
        uploadPhotoInfos.add(new UploadPhotoInfo().setName("上传人车合影"));
        return uploadPhotoInfos;
    }

    private void initImagePicker() {
        mImagePicker = new ImagePicker()
                .cachePath(Environment.getExternalStorageDirectory().getAbsolutePath())
                .needCamera(true)
                .pickType(ImagePickType.MULTI)
                .maxNum(1);
    }

    private void setUploadProgress(String filePath, float progress) {
        String progressString = (int) (progress * 100) + "%";
        for (int i = 0; i < mAdapter.getDataSize(); i++) {
            if (mAdapter.get(i).getPath().equals(filePath)) {
                UploadPhotoInfo firstProperty = mAdapter.getData().get(i);
                firstProperty.setProgress(progressString);
                if (progress == 1.0) {
                    firstProperty.setShowProgress(false);
                }
                mAdapter.notifyDataChange(i, firstProperty, 1);
                break;
            }
        }
    }

    private void setServerUrl(String filePath, String url) {
        for (int i = 0; i < mAdapter.getDataSize(); i++) {
            if (mAdapter.get(i).getPath().equals(filePath)) {
                UploadPhotoInfo uploadPhotoInfo = mAdapter.get(i);
                uploadPhotoInfo.setPath(url);
                uploadPhotoInfo.setShowProgress(false);
                uploadPhotoInfo.setUploadSuccess(true);
                mAdapter.notifyDataChange(i, uploadPhotoInfo, 1);
                break;
            }
        }
    }

    private void uploadPicture(final File file, final int choosePosition) {
        int type = 0;
        if (choosePosition == 2 || mChoosePosition == 3) {
            type = 3;
        } else if (mChoosePosition == 4 || mChoosePosition == 5) {
            type = 5;
        } else if (mChoosePosition == 6) {
            type = 6;
        }
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
                            case 3:
                                setServerUrl(file.getAbsolutePath(), HttpConstants.ROOT_IMG_URL_DRIVER_LICENSE + stringBase_class_info.data);
                                break;
                            case 4:
                            case 5:
                                setServerUrl(file.getAbsolutePath(), HttpConstants.ROOT_IMG_URL_VEHICLE + stringBase_class_info.data);
                                break;
                            case 6:
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
        for (int i = 0; i < 6; i++) {
            if (mAdapter.get(i).getPath().equals(photoPath)) {
                UploadPhotoInfo uploadPhotoInfo = mAdapter.get(i);
                uploadPhotoInfo.setPath("-1");
                uploadPhotoInfo.setProgress("0%");
                uploadPhotoInfo.setShowProgress(false);
                uploadPhotoInfo.setUploadSuccess(false);
                mAdapter.getData().set(i, uploadPhotoInfo);
                mAdapter.notifyDataChange(i, uploadPhotoInfo);
                break;
            }
        }
    }

    private void applyAddNewCar() {
        getOkGo(HttpConstants.applyAddNewCar)
                .params("carNumber", getText(mCarNumber))
                .params("carOwner", mCarOwner.getText().toString())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> voidBase_class_info, Call call, Response response) {
                        showFiveToast("提交成功，我们会尽快为你审核");
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                case "102":
                                    showFiveToast("客户端异常，请稍后重试");
                                    break;
                                case "103":
                                    showFiveToast("该车牌号码已被删除");
                                    break;
                                case "104":
                                    showFiveToast("该车主已被删除");
                                    break;
                                case "105":
                                    showFiveToast("请选择你的驾驶证照片");
                                    break;
                                case "106":
                                    showFiveToast("该照片异常，请重新选择");
                                    break;
                                case "107":
                                    showFiveToast("服务器异常，请稍后重试");
                                    break;
                                case "108":
                                    showFiveToast("你已添加过该车辆了哦");
                                    break;
                            }
                        }
                    }
                });

    }

    private class IdentifyAdapter extends BaseAdapter<UploadPhotoInfo> {

        IdentifyAdapter(List<UploadPhotoInfo> data) {
            super(data);
        }

        @Override
        protected void conver(@NonNull BaseViewHolder holder, final UploadPhotoInfo uploadPhotoInfo, final int position) {
            holder.setText(R.id.identify_tv, uploadPhotoInfo.getName());
            ImageView imageView = holder.getView(R.id.upload_iv);
            TextView textView = holder.getView(R.id.upload_tv);
            if (uploadPhotoInfo.getPath().equals("-1")) {
                imageView.setImageDrawable(null);
                goneView(textView);
                ViewUtil.showProgressStatus(textView, false);
            } else {
                ImageUtil.showPicWithNoAnimate(imageView, uploadPhotoInfo.getPath(), new LoadFailCallback() {
                    @Override
                    public void onLoadFail(Exception e) {
                        deletePhoto(e.getMessage());
                    }
                });
                ViewUtil.showProgressStatus(textView, uploadPhotoInfo.isShowProgress());
                textView.setText(uploadPhotoInfo.getProgress());
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mChoosePosition = position;
                    mImagePicker.start(AddNewCarActivity.this, REQUEST_CODE_PICKER);
                }
            });

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        protected int itemViewId() {
            return R.layout.item_identify_information_layout;
        }

    }

}
