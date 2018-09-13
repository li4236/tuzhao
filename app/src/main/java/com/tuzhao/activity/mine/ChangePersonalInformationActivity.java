package com.tuzhao.activity.mine;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.photopicker.controller.PhotoPickConfig;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.OnLoadCallback;
import com.tuzhao.publicwidget.customView.CircleImageView;
import com.tuzhao.publicwidget.upload.MyFile;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.io.File;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/8/20.
 * <p>
 * 修改个人信息（头像，昵称）
 * </p>
 */
public class ChangePersonalInformationActivity extends BaseStatusActivity implements View.OnClickListener, IntentObserver {

    private CircleImageView mUserProtrait;

    private TextView mNickname;

    private User_Info mUserInfo;

    @Override
    protected int resourceId() {
        return R.layout.activity_change_personal_information_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mUserProtrait = findViewById(R.id.user_protrait);
        mNickname = findViewById(R.id.nickname);

        findViewById(R.id.user_portrait_tv).setOnClickListener(this);
        findViewById(R.id.nickname_tv).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mUserInfo = UserManager.getInstance().getUserInfo();
        ImageUtil.showPic(mUserProtrait, HttpConstants.ROOT_IMG_URL_USER + mUserInfo.getImg_url(), R.mipmap.ic_usericon);

        if (mUserInfo.getNickname().equals("-1")) {
            mNickname.setText("昵称（未设置）");
        } else {
            mNickname.setText(mUserInfo.getNickname());
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
                ImageUtil.startTakePhotoAndCrop(ChangePersonalInformationActivity.this);
                break;
            case R.id.nickname_tv:
                startActivity(ChangeNicknameActivity.class);
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
                //图片大小大于1M才压缩，否则看大图的时候会很模糊
                if (file.length() > 1024 * 1024) {
                    showLoadingDialog("压缩中...");
                    ImageUtil.compressPhoto(ChangePersonalInformationActivity.this, file.getAbsolutePath(), new SuccessCallback<MyFile>() {
                        @Override
                        public void onSuccess(MyFile myFile) {
                            dismmisLoadingDialog();
                            if (myFile.getUncompressName().equals(file.getAbsolutePath())) {
                                changeUserPortrait(myFile);
                            }
                        }
                    });
                } else {
                    changeUserPortrait(file);
                }
            } else {
                showFiveToast("获取图片失败，请更换图片");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
    }

    /**
     * 修改用户头像
     */
    private void changeUserPortrait(File file) {
        showLoadingDialog();
        OkGo.post(HttpConstants.changeUserImage)
                .tag(TAG)
                .headers("token", mUserInfo.getToken())
                .params("img_file", file)
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(final Base_Class_Info<String> stringBase_class_info, Call call, Response response) {
                        ImageUtil.showPicWithNoAnimate(mUserProtrait, HttpConstants.ROOT_IMG_URL_USER + stringBase_class_info.data, new OnLoadCallback<Drawable, Exception>() {
                            @Override
                            public void onSuccess(Drawable drawable) {
                                mUserInfo.setImg_url(stringBase_class_info.data);
                                IntentObserable.dispatch(ConstansUtil.CHANGE_PORTRAIT);
                                mUserProtrait.setImageDrawable(drawable);
                                dismmisLoadingDialog();
                            }

                            @Override
                            public void onFail(Exception e) {
                                showFiveToast("修改头像失败，请稍后重试");
                                dismmisLoadingDialog();
                            }
                        });
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
            //修改用户昵称后更新
            mNickname.setText(UserManager.getInstance().getUserInfo().getNickname());
        }
    }

}
