package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.lwkandroid.imagepicker.ImagePicker;
import com.lwkandroid.imagepicker.data.ImageBean;
import com.lwkandroid.imagepicker.data.ImagePickType;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.customView.CircleImageView;
import com.tuzhao.publicwidget.upload.MyFile;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.io.File;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/8/20.
 */
public class ChangePersonalInformationActivity extends BaseStatusActivity implements View.OnClickListener, IntentObserver {

    private CircleImageView mUserProtrait;

    private TextView mUserName;

    private TextView mNickname;

    private TextView mGender;

    private TextView mBirthday;

    private ImagePicker mImagePicker;

    private User_Info mUserInfo;

    @Override
    protected int resourceId() {
        return R.layout.activity_change_personal_information_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mUserProtrait = findViewById(R.id.user_protrait);
        mUserName = findViewById(R.id.user_name);
        mNickname = findViewById(R.id.nickname);
        mGender = findViewById(R.id.gender);
        mBirthday = findViewById(R.id.birthday);

        findViewById(R.id.user_portrait_tv).setOnClickListener(this);
        findViewById(R.id.nickname_tv).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mUserInfo = UserManager.getInstance().getUserInfo();
        ImageUtil.showPic(mUserProtrait, HttpConstants.ROOT_IMG_URL_USER + mUserInfo.getImg_url(), R.mipmap.ic_usericon);
        if (mUserInfo.getRealName().equals("") || mUserInfo.getRealName().equals("-1")) {
            mUserName.setText("未认证");
        } else {
            mUserName.setText(mUserInfo.getRealName());
        }

        if (mUserInfo.getNickname().equals("-1")) {
            mNickname.setText("昵称（未设置）");
        } else {
            mNickname.setText(mUserInfo.getNickname());
        }

        mGender.setText(mUserInfo.getGender().equals("1") ? "男" : "女");

        if (mUserInfo.getBirthday().equals("0000-00-00")) {
            mBirthday.setText("未认证");
        } else {
            mBirthday.setText(mUserInfo.getBirthday());
        }

        IntentObserable.registerObserver(this);
    }

    @NonNull
    @Override
    protected String title() {
        return "修改信息";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_portrait_tv:
                showImagePicker();
                break;
            case R.id.nickname_tv:
                startActivity(ChangeNicknameActivity.class);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstansUtil.REQUSET_CODE && resultCode == RESULT_OK && data != null) {
            List<ImageBean> list = data.getParcelableArrayListExtra(ImagePicker.INTENT_RESULT_DATA);
            final File file = new File(list.get(0).getImagePath());
            if (file.exists()) {
                //图片大小大于1M才压缩，否则看大图的时候会很模糊
                if (file.length() > 1024 * 1024) {
                    ImageUtil.compressPhoto(ChangePersonalInformationActivity.this, file.getAbsolutePath(), new SuccessCallback<MyFile>() {
                        @Override
                        public void onSuccess(MyFile myFile) {
                            if (myFile.getUncompressName().equals(file.getAbsolutePath())) {
                                changeUserPortrait(myFile);
                            }
                        }
                    });
                } else {
                    changeUserPortrait(file);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
    }

    private void showImagePicker() {
        if (mImagePicker == null) {
            mImagePicker = new ImagePicker()
                    .cachePath(Environment.getExternalStorageDirectory().getAbsolutePath())
                    .needCamera(true)
                    .pickType(ImagePickType.SINGLE);
        }
        mImagePicker.start(this, ConstansUtil.REQUSET_CODE);
    }

    private void changeUserPortrait(File file) {
        showLoadingDialog();
        OkGo.post(HttpConstants.changeUserImage)
                .tag(TAG)
                .headers("token", mUserInfo.getToken())
                .params("img_file", file)
                .execute(new JsonCallback<Base_Class_Info<User_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<User_Info> stringBase_class_info, Call call, Response response) {
                        mUserInfo.setImg_url(stringBase_class_info.data.getImg_url());
                        ImageUtil.showPicWithNoAnimate(mUserProtrait, HttpConstants.ROOT_IMG_URL_USER + stringBase_class_info.data.getImg_url(), R.mipmap.ic_usericon);
                        IntentObserable.dispatch(ConstansUtil.CHANGE_PORTRAIT);
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast("修改头像失败，请稍后重试");
                        }
                    }
                });
    }

    @Override
    public void onReceive(Intent intent) {
        if (Objects.equals(intent.getAction(), ConstansUtil.CHANGE_NICKNAME)) {
            mNickname.setText(UserManager.getInstance().getUserInfo().getNickname());
        }
    }

}
