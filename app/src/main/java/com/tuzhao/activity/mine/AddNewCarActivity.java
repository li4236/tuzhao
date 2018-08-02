package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lwkandroid.imagepicker.ImagePicker;
import com.lwkandroid.imagepicker.data.ImageBean;
import com.lwkandroid.imagepicker.data.ImagePickType;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.pickerview.OptionsPickerView;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
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

    private TextView mLicensePlateAttribution;

    private EditText mCarNumber;

    private EditText mCarOwner;

    private ImageView mDriveLicensePhoto;

    private ImageView mDeleteDriveLicensePhoto;

    private TextView mUploadProgress;

    private String mDriveLicensePhotoPath;

    private String mCityCode;

    private static final int REQUEST_CODE_PICKER = 0x111;

    private ArrayList<String> mCitys;

    private ArrayList<ArrayList<String>> mLetters;

    private OptionsPickerView<String> mPickerView;

    @Override
    protected int resourceId() {
        return R.layout.activity_add_new_car_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if ((mCityCode = getIntent().getStringExtra("cityCode")) == null) {
            showFiveToast("获取当前位置失败，暂不能添加车辆");
            finish();
        }
        mLicensePlateAttribution = findViewById(R.id.license_plate_attribution);
        mCarNumber = findViewById(R.id.car_number_et);
        mCarOwner = findViewById(R.id.car_owner_et);
        mDriveLicensePhoto = findViewById(R.id.drive_license_photo);
        mDeleteDriveLicensePhoto = findViewById(R.id.delete_drive_license_photo);
        mUploadProgress = findViewById(R.id.upload_progress_tv);

        findViewById(R.id.apply_add_new_car).setOnClickListener(this);
        mLicensePlateAttribution.setOnClickListener(this);
        mDriveLicensePhoto.setOnClickListener(this);
        mDeleteDriveLicensePhoto.setOnClickListener(this);

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
        mCitys = new ArrayList<>(32);
        mLetters = new ArrayList<>(32);
        DataUtil.initLicensePlateAttribution(mCitys, mLetters);
        mPickerView = new OptionsPickerView<>(this);
        mPickerView.setPicker(mCitys, mLetters, true);
        mPickerView.setTitle("车牌归属地");
        mPickerView.setTextSize(18);
        mPickerView.setCyclic(false);
        int index = mCitys.indexOf("粤");
        mPickerView.setSelectOptions(index, mLetters.get(index).indexOf("T"));
        mPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                mLicensePlateAttribution.setText(mCitys.get(options1) + mLetters.get(options1).get(option2));
            }
        });
    }

    @NonNull
    @Override
    protected String title() {
        return "添加车辆";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drive_license_photo:
                new ImagePicker()
                        .cachePath(Environment.getExternalStorageDirectory().getAbsolutePath())
                        .needCamera(true) //是否需要在界面中显示相机入口(类似微信那样)
                        .pickType(ImagePickType.SINGLE) //设置选取类型(单选SINGLE、多选MUTIL、拍照ONLY_CAMERA)
                        .doCrop(1, 1, 200, 200) //裁剪功能需要调用这个方法，多选模式下无效，参数：aspectX,aspectY,outputX,outputY
                        .start(AddNewCarActivity.this, REQUEST_CODE_PICKER);
                break;
            case R.id.license_plate_attribution:
                ViewUtil.closeInputMethod(mCarNumber);
                mPickerView.show();
                break;
            case R.id.delete_drive_license_photo:
                mDriveLicensePhotoPath = null;
                ImageUtil.showPic(mDriveLicensePhoto, R.drawable.ic_addimg);
                mDeleteDriveLicensePhoto.setVisibility(View.INVISIBLE);
                break;
            case R.id.apply_add_new_car:
                if (getTextLength(mCarNumber) == 0) {
                    showFiveToast("请输入车牌号码");
                } else if (!DateUtil.isCarNumber(getText(mLicensePlateAttribution) + getText(mCarNumber))) {
                    showFiveToast("你输入的车牌号不正确哦");
                } else if (TextUtils.isEmpty(mCarOwner.getText().toString().trim())) {
                    showFiveToast("请输入车牌所有人");
                } else if (mDriveLicensePhotoPath == null) {
                    showFiveToast("请选择你的驾驶证照片");
                } else {
                    if (UserManager.getInstance().getUserInfo().getCar_number().contains(getText(mLicensePlateAttribution) + getText(mCarNumber))) {
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
            mDriveLicensePhotoPath = null;
            ImageUtil.compressPhoto(AddNewCarActivity.this, list.get(0).getImagePath(), new SuccessCallback<File>() {
                @Override
                public void onSuccess(File file) {
                    ImageUtil.showPic(mDriveLicensePhoto, file.getAbsolutePath());
                    mDeleteDriveLicensePhoto.setVisibility(View.VISIBLE);
                    uploadPicture(file);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (mPickerView.isShowing()) {
            mPickerView.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    private void uploadPicture(File file) {
        mUploadProgress.setText("0%");
        showView(mUploadProgress);
        goneView(mDeleteDriveLicensePhoto);

        OkGo.post(HttpConstants.uploadPicture)
                .retryCount(0)
                .headers("token", com.tuzhao.publicmanager.UserManager.getInstance().getUserInfo().getToken())
                .params("type", 3)
                .params("picture", file)
                .execute(new JsonCallback<Base_Class_Info<String>>() {

                    @Override
                    public void onSuccess(Base_Class_Info<String> stringBase_class_info, Call call, Response response) {
                        goneView(mUploadProgress);
                        showView(mDeleteDriveLicensePhoto);
                        mDriveLicensePhotoPath = stringBase_class_info.data;
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.upProgress(currentSize, totalSize, progress, networkSpeed);
                        mUploadProgress.setText((int) (progress * 100) + "%");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        goneView(mUploadProgress);
                        mDriveLicensePhotoPath = null;
                        ImageUtil.showPic(mDriveLicensePhoto, R.drawable.ic_addimg);
                        mDeleteDriveLicensePhoto.setVisibility(View.INVISIBLE);
                        if (!handleException(e)) {

                        }
                    }
                });
    }

    private void applyAddNewCar() {
        getOkGo(HttpConstants.applyAddNewCar)
                .params("driverLicense", mDriveLicensePhotoPath.replace(HttpConstants.ROOT_IMG_URL_DRIVER_LICENSE, ""))
                .params("carNumber", getText(mLicensePlateAttribution) + getText(mCarNumber))
                .params("carOwner", mCarOwner.getText().toString())
                .params("cityCode", mCityCode)
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

}
