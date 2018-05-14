package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lwkandroid.imagepicker.ImagePicker;
import com.lwkandroid.imagepicker.data.ImageBean;
import com.lwkandroid.imagepicker.data.ImagePickType;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.OnLoginListener;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.ImageUtil;

import java.io.File;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by juncoder on 2018/5/2.
 */

public class AddNewCarActivity extends BaseStatusActivity implements View.OnClickListener {

    private EditText mCarNumber;

    private EditText mCarOwner;

    private ImageView mDriveLicensePhoto;

    private ImageView mDeleteDriveLicensePhoto;

    private File mCarNumberFile;

    private String mCityCode;

    private static final int REQUEST_CODE_PICKER = 0x111;

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
        mCarNumber = findViewById(R.id.car_number_et);
        mCarOwner = findViewById(R.id.car_owner_et);
        mDriveLicensePhoto = findViewById(R.id.drive_license_photo);
        mDeleteDriveLicensePhoto = findViewById(R.id.delete_drive_license_photo);
        findViewById(R.id.apply_add_new_car).setOnClickListener(this);
        mDriveLicensePhoto.setOnClickListener(this);
        mDeleteDriveLicensePhoto.setOnClickListener(this);
    }

    @Override
    protected void initData() {

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
            case R.id.delete_drive_license_photo:
                ImageUtil.showPic(mDriveLicensePhoto, R.drawable.ic_addimg);
                mDeleteDriveLicensePhoto.setVisibility(View.INVISIBLE);
                break;
            case R.id.apply_add_new_car:
                if (!DateUtil.isCarNumber(mCarNumber.getText().toString())) {
                    showFiveToast("你输入的车牌号不正确哦");
                } else if (TextUtils.isEmpty(mCarOwner.getText().toString().trim())) {
                    showFiveToast("请输入车牌所有人");
                } else if (mDeleteDriveLicensePhoto.getVisibility() != View.VISIBLE) {
                    showFiveToast("请选择你的驾驶证照片");
                } else {
                    judgeLogin(new OnLoginListener() {
                        @Override
                        public void onLogin() {
                            if (UserManager.getInstance().getUserInfo().getCar_number().contains(mCarNumber.getText().toString())) {
                                showFiveToast("你已添加过该车辆了哦");
                            } else {
                                applyAddNewCar();
                            }
                        }
                    });
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            List<ImageBean> list = data.getParcelableArrayListExtra(ImagePicker.INTENT_RESULT_DATA);
            showLoadingDialog("正在更换");
            Luban.with(AddNewCarActivity.this)
                    .load(list.get(0).getImagePath())
                    .ignoreBy(1)
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onSuccess(File file) {
                            mCarNumberFile = file;
                            ImageUtil.showPic(mDriveLicensePhoto, file.getAbsolutePath());
                            mDeleteDriveLicensePhoto.setVisibility(View.VISIBLE);
                            dismmisLoadingDialog();
                        }

                        @Override
                        public void onError(Throwable e) {
                            dismmisLoadingDialog();
                        }
                    }).launch();
        }
    }

    private void applyAddNewCar() {
        OkGo.post(HttpConstants.applyAddNewCar)
                .tag(AddNewCarActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", com.tuzhao.publicmanager.UserManager.getInstance().getUserInfo().getToken())
                .params("carNumberFile", mCarNumberFile)
                .params("carNumber", mCarNumber.getText().toString())
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
                            }
                        }
                    }
                });

    }

}
