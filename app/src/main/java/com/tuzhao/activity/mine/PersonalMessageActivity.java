package com.tuzhao.activity.mine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lwkandroid.imagepicker.ImagePicker;
import com.lwkandroid.imagepicker.data.ImageBean;
import com.lwkandroid.imagepicker.data.ImagePickType;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ChangeUserImageInfo;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.publicwidget.others.CircleImageView;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.ImageUtil;

import java.io.File;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.tuzhao.publicwidget.dialog.LoginDialogFragment.LOGIN_ACTION;

/**
 * Created by TZL12 on 2017/11/30.
 */

public class PersonalMessageActivity extends BaseActivity implements View.OnClickListener {

    private CircleImageView imageview_user;
    private TextView textview_nickname, textview_realname, textview_credit;
    private CustomDialog mCustomDialog;

    //调用相册-选择图片
    private final int REQUEST_CODE_PICKER = 100;
    private final int RESULT_CODE_PICKER = 101;

    /**
     * 登录的广播接收器
     */
    private LoginBroadcastReceiver loginBroadcastReceiver = new LoginBroadcastReceiver();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalmessage_layout);
        initView();
        initData();
        initEvent();
        setStyle(true);
    }

    private void initView() {

        imageview_user = findViewById(R.id.id_activity_personalmessage_layout_imageview_user);
        textview_nickname = findViewById(R.id.id_activity_personalmessage_layout_textview_nickname);
        textview_realname = findViewById(R.id.id_activity_personalmessage_layout_textview_realname);
        textview_credit = findViewById(R.id.id_activity_personalmessage_layout_textview_credit);
    }

    private void initData() {

        if (UserManager.getInstance().hasLogined()) {
            registerLogin();//注册登录广播接收器
            ImageUtil.showPic(imageview_user, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url(), R.mipmap.ic_usericon);
            textview_nickname.setText(UserManager.getInstance().getUserInfo().getNickname().equals("-1") ? UserManager.getInstance().getUserInfo().getUsername() : UserManager.getInstance().getUserInfo().getNickname());
            textview_realname.setText("未认证");
            textview_credit.setText(UserManager.getInstance().getUserInfo().getCredit());
        } else {
            finish();
        }
    }

    private void initEvent() {

        findViewById(R.id.id_activity_personalmessage_layout_imageview_back).setOnClickListener(this);
        findViewById(R.id.id_activity_personalmessage_layout_linearlayout_imguser).setOnClickListener(this);
        findViewById(R.id.id_activity_personalmessage_layout_linearlayout_nickname).setOnClickListener(this);
        findViewById(R.id.id_activity_personalmessage_layout_linearlayout_realname).setOnClickListener(this);
        findViewById(R.id.id_activity_personalmessage_layout_linearlayout_credit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.id_activity_personalmessage_layout_imageview_back:
                finish();
                break;
            case R.id.id_activity_personalmessage_layout_linearlayout_imguser:
                //调用相册，更换头像
                new ImagePicker()
                        .cachePath(Environment.getExternalStorageDirectory().getAbsolutePath())
                        .needCamera(true) //是否需要在界面中显示相机入口(类似微信那样)
                        .pickType(ImagePickType.SINGLE) //设置选取类型(单选SINGLE、多选MUTIL、拍照ONLY_CAMERA)
                        .doCrop(1, 1, 200, 200) //裁剪功能需要调用这个方法，多选模式下无效，参数：aspectX,aspectY,outputX,outputY
                        .start(PersonalMessageActivity.this, REQUEST_CODE_PICKER);
                break;
            case R.id.id_activity_personalmessage_layout_linearlayout_nickname:
                //跳转修改昵称
                intent = new Intent(PersonalMessageActivity.this, ChangeNicknameActivity.class);
                startActivity(intent);
                break;
            case R.id.id_activity_personalmessage_layout_linearlayout_realname:
                // TODO: 2018/3/26 实名认证
                finish();
                break;
            case R.id.id_activity_personalmessage_layout_linearlayout_credit:
                intent = new Intent(PersonalMessageActivity.this, PersonalCreditActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void initLoading(String what) {
        mCustomDialog = new CustomDialog(this, what);
        mCustomDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICKER && data!=null) {
            final List<ImageBean> list = data.getParcelableArrayListExtra(ImagePicker.INTENT_RESULT_DATA);
            initLoading("正在更换...");
            Log.e("hhaha", getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath());
            //进行图片逐个压缩
            Luban.with(PersonalMessageActivity.this)
                    .load(list.get(0).getImagePath())
                    .ignoreBy(1)
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
                            // 压缩开始前调用，可以在方法内启动 loading UI
                        }

                        @Override
                        public void onSuccess(File file) {
                            // 压缩成功后调用，返回压缩后的图片文件
                            Log.e("hhaha", file.getAbsolutePath());
                            File file1 = new File(list.get(0).getImagePath());
                            if (file1.exists()) {
                                file1.delete();
                            }
                            uploadUserImage(file);
                        }

                        @Override
                        public void onError(Throwable e) {
                            // 当压缩过程出现问题时调用
                        }
                    }).launch();
        }
    }

    private void uploadUserImage(final File file) {
        OkGo.post(HttpConstants.changeUserImage)
                .tag(PersonalMessageActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("img_file", file)
                .execute(new JsonCallback<Base_Class_Info<ChangeUserImageInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ChangeUserImageInfo> data_info, Call call, Response response) {
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        MyToast.showToast(PersonalMessageActivity.this, "更换成功", 2);
                        if (file.exists()) {
                            file.delete();
                        }

                        ImageUtil.showCircleImgPic(imageview_user, HttpConstants.ROOT_IMG_URL_USER + data_info.data.getImg_url());
                        User_Info userInfo = UserManager.getInstance().getUserInfo();
                        userInfo.setImg_url(data_info.data.getImg_url());
                        UserManager.getInstance().setUserInfo(userInfo);
                        sendLoginBroadcast();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        if (!DensityUtil.isException(PersonalMessageActivity.this, e)) {
                            Log.d("TAG", "请求失败， 信息为changeUserImage：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 102:
                                    MyToast.showToast(PersonalMessageActivity.this, "头像未能成功上传", 5);
                                    break;
                                case 101:
                                    MyToast.showToast(PersonalMessageActivity.this, "更换失败，稍后重试", 5);
                                    break;
                                case 901:
                                    MyToast.showToast(PersonalMessageActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.upProgress(currentSize, totalSize, progress, networkSpeed);
                        Log.e("dsa", "上传的进度：" + progress);
                    }
                });
    }

    /**
     * 发送登录的局部广播
     */
    private void sendLoginBroadcast() {

        LocalBroadcastManager.getInstance(PersonalMessageActivity.this).sendBroadcast(new Intent(LOGIN_ACTION));
    }

    /**
     * 自定义登录的局部广播接收器，用来处理登录广播
     */
    private class LoginBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (UserManager.getInstance().hasLogined()) {
                ImageUtil.showPic(imageview_user, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url(),
                        R.mipmap.ic_usericon);
                textview_nickname.setText(UserManager.getInstance().getUserInfo().getNickname().equals("-1") ? UserManager.getInstance().getUserInfo().getUsername() : UserManager.getInstance().getUserInfo().getNickname());
                textview_realname.setText("未认证");
                textview_credit.setText(UserManager.getInstance().getUserInfo().getCredit());
            }
        }
    }

    //注册登录广播接收器的方法
    private void registerLogin() {
        IntentFilter filter = new IntentFilter(LOGIN_ACTION);
        LocalBroadcastManager.getInstance(PersonalMessageActivity.this).registerReceiver(loginBroadcastReceiver, filter);
    }

    //注销登录广播接收器
    private void unregisterLogin() {
        LocalBroadcastManager.getInstance(PersonalMessageActivity.this).unregisterReceiver(loginBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterLogin();//注销登录广播接收器
        if (mCustomDialog != null) {
            mCustomDialog.cancel();
        }
    }
}
