package com.tuzhao.activity.mine;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.request.BaseRequest;
import com.tuzhao.R;
import com.tuzhao.activity.MainActivity;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.application.MyApplication;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.ImageUtil;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/7/25.
 */
public class ChangePasswordRefactoryActivity extends BaseStatusActivity implements View.OnClickListener {

    private String mLocalOriginalPassword;

    private EditText mOriginalPassword;

    private ImageView mOriginalPasswordStatus;

    private ImageView mClearOriginalPassword;

    private TextView mOriginalPasswordError;

    private EditText mNewPassword;

    private ImageView mNewPassowrdStatus;

    private ImageView mClearNewPassword;

    private TextView mNewPasswordError;

    private EditText mConfirmPassword;

    private ImageView mConfirmPassowrdStatus;

    private ImageView mClearConfirmPassowrd;

    private TextView mConfirmPasswordError;

    private TextView mChangePassword;

    private ObjectAnimator mOriginPasswordErrorAnimator;

    private ObjectAnimator mNewPasswrodErrorAnimator;

    private ObjectAnimator mConfirmPasswordErrorAnimator;

    private Handler mHandler;

    private static final int ORIGIN_PASSWROD_ERROR = 0x111;

    private static final int NEW_PASSWROD_ERROR = 0x222;

    private static final int CONFIRM_PASSWROD_ERROR = 0x333;

    private static final int HIDE_ORIGINAL_PASSWORD_ERROR = 0x444;

    private static final int HIDE_NEW_PASSWORD_ERROR = 0x555;

    private static final int HIDE_CONFIRM_PASSWORD_ERROR = 0x666;

    private boolean mOriginPasswrodShow;

    private boolean mNewPasswordShow;

    private boolean mConfirmPasswordShow;

    private String mPassCode;

    private User_Info mUserInfo;

    @Override
    protected int resourceId() {
        return R.layout.activity_change_password_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mOriginalPassword = findViewById(R.id.original_password_et);
        mOriginalPasswordStatus = findViewById(R.id.original_password_status);
        mClearOriginalPassword = findViewById(R.id.clear_original_password);
        mOriginalPasswordError = findViewById(R.id.original_password_error);
        mNewPassword = findViewById(R.id.new_password_et);
        mNewPassowrdStatus = findViewById(R.id.new_password_status);
        mClearNewPassword = findViewById(R.id.clear_new_password);
        mNewPasswordError = findViewById(R.id.new_password_error);
        mConfirmPassword = findViewById(R.id.confirm_new_password_et);
        mConfirmPassowrdStatus = findViewById(R.id.confirm_password_status);
        mClearConfirmPassowrd = findViewById(R.id.clear_confirm_password);
        mConfirmPasswordError = findViewById(R.id.confirm_password_error);
        mChangePassword = findViewById(R.id.change_password);

        mOriginalPassword.requestFocus();
        mOriginalPasswordStatus.setOnClickListener(this);
        mClearOriginalPassword.setOnClickListener(this);
        mNewPassowrdStatus.setOnClickListener(this);
        mClearNewPassword.setOnClickListener(this);
        mConfirmPassowrdStatus.setOnClickListener(this);
        mClearConfirmPassowrd.setOnClickListener(this);
        mChangePassword.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mUserInfo = UserManager.getInstance().getUserInfo();
        mLocalOriginalPassword = mUserInfo.getPassword();
        mPassCode = getIntent().getStringExtra(ConstansUtil.PASS_CODE);
        if (mPassCode != null) {
            mOriginalPassword.setVisibility(View.GONE);
            mOriginalPasswordStatus.setVisibility(View.GONE);
            mOriginalPasswordError.setVisibility(View.GONE);
            mClearOriginalPassword.setVisibility(View.GONE);
        }

        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case ORIGIN_PASSWROD_ERROR:
                        startOriginPasswordErrorAnimator();
                        showFiveToast("原密码错误");
                        return true;
                    case NEW_PASSWROD_ERROR:
                        startNewPasswrodErrorAnimator();
                        showFiveToast("密码长度不能小于8位");
                        return true;
                    case CONFIRM_PASSWROD_ERROR:
                        startConfirmPasswordErrorAnimator();
                        return true;
                    case HIDE_ORIGINAL_PASSWORD_ERROR:
                        mOriginalPasswordError.setVisibility(View.INVISIBLE);
                        return true;
                    case HIDE_NEW_PASSWORD_ERROR:
                        mNewPasswordError.setVisibility(View.INVISIBLE);
                        break;
                    case HIDE_CONFIRM_PASSWORD_ERROR:
                        mConfirmPasswordError.setVisibility(View.INVISIBLE);
                        return true;
                }
                return false;
            }
        });

        mOriginalPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (getTextLength(mOriginalPassword) == 0) {
                    if (isVisible(mClearOriginalPassword)) {
                        mClearOriginalPassword.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (!isVisible(mClearOriginalPassword)) {
                        mClearOriginalPassword.setVisibility(View.VISIBLE);
                    }
                    /*if (!getText(mOriginalPassword).equals(mLocalOriginalPassword)) {
                        mHandler.removeMessages(ORIGIN_PASSWROD_ERROR);
                        mHandler.sendEmptyMessageDelayed(ORIGIN_PASSWROD_ERROR, 800);
                    }*/
                }
            }
        });

        mNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (getTextLength(mNewPassword) == 0) {
                    if (isVisible(mClearNewPassword)) {
                        mClearNewPassword.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (!isVisible(mClearNewPassword)) {
                        mClearNewPassword.setVisibility(View.VISIBLE);
                    }
                    /*if (getTextLength(mNewPassword) < 8) {
                        mHandler.removeMessages(NEW_PASSWROD_ERROR);
                        mHandler.sendEmptyMessageDelayed(NEW_PASSWROD_ERROR, 800);
                    }*/
                }
            }
        });

        mConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (getTextLength(mConfirmPassword) == 0) {
                    if (isVisible(mClearConfirmPassowrd)) {
                        mClearConfirmPassowrd.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (!isVisible(mClearConfirmPassowrd)) {
                        mClearConfirmPassowrd.setVisibility(View.VISIBLE);
                    }
                    /*if (!getText(mNewPassword).equals(getText(mConfirmPassword))) {
                        mHandler.removeMessages(CONFIRM_PASSWROD_ERROR);
                        mHandler.sendEmptyMessageDelayed(CONFIRM_PASSWROD_ERROR, 800);
                    }*/
                }
            }
        });

    }

    @NonNull
    @Override
    protected String title() {
        return "修改密码";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOriginPasswordErrorAnimator != null) {
            mOriginPasswordErrorAnimator.cancel();
        }
        if (mNewPasswrodErrorAnimator != null) {
            mNewPasswrodErrorAnimator.cancel();
        }
        if (mConfirmPasswordErrorAnimator != null) {
            mConfirmPasswordErrorAnimator.cancel();
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    private void startOriginPasswordErrorAnimator() {
        if (mOriginPasswordErrorAnimator == null) {
            mOriginPasswordErrorAnimator = ObjectAnimator.ofFloat(mOriginalPassword, "translationX", DensityUtil.dp2px(this, 8));
            mOriginPasswordErrorAnimator.setDuration(300);
            mOriginPasswordErrorAnimator.setAutoCancel(true);

            CycleInterpolator cycleInterpolator = new CycleInterpolator(3);
            mOriginPasswordErrorAnimator.setInterpolator(cycleInterpolator);
            mOriginPasswordErrorAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mHandler.removeMessages(HIDE_ORIGINAL_PASSWORD_ERROR);
                    mHandler.sendEmptyMessageDelayed(HIDE_ORIGINAL_PASSWORD_ERROR, 1500);

                    mOriginalPassword.setBackgroundResource(R.drawable.normal_g6_focus_y3_stroke_all_3dp);
                    mOriginalPassword.requestFocus();
                    mOriginalPassword.setSelection(getTextLength(mOriginalPassword));
                }
            });
        }
        if (!isVisible(mOriginalPasswordError)) {
            mOriginalPasswordError.setVisibility(View.VISIBLE);
        }
        mOriginalPassword.setBackgroundResource(R.drawable.r8_stroke_all_3dp);
        mOriginalPassword.clearFocus();
        mOriginPasswordErrorAnimator.start();
    }

    private void startNewPasswrodErrorAnimator() {
        if (mNewPasswrodErrorAnimator == null) {
            mNewPasswrodErrorAnimator = ObjectAnimator.ofFloat(mNewPassword, "translationX", DensityUtil.dp2px(this, 8));
            mNewPasswrodErrorAnimator.setDuration(300);
            mNewPasswrodErrorAnimator.setAutoCancel(true);

            CycleInterpolator cycleInterpolator = new CycleInterpolator(3);
            mNewPasswrodErrorAnimator.setInterpolator(cycleInterpolator);
            mNewPasswrodErrorAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mHandler.sendEmptyMessageDelayed(HIDE_NEW_PASSWORD_ERROR, 1500);
                    mNewPassword.setBackgroundResource(R.drawable.normal_g6_focus_y3_stroke_all_3dp);
                    mNewPassword.requestFocus();
                    mNewPassword.setSelection(getTextLength(mNewPassword));
                }
            });
        }
        mNewPasswordError.setVisibility(View.VISIBLE);
        mNewPassword.setBackgroundResource(R.drawable.r8_stroke_all_3dp);
        mNewPassword.clearFocus();
        mNewPasswrodErrorAnimator.start();
    }

    private void startConfirmPasswordErrorAnimator() {
        if (mConfirmPasswordErrorAnimator == null) {
            mConfirmPasswordErrorAnimator = ObjectAnimator.ofFloat(mConfirmPassword, "translationX", DensityUtil.dp2px(this, 8));
            mConfirmPasswordErrorAnimator.setDuration(300);
            mConfirmPasswordErrorAnimator.setAutoCancel(true);

            CycleInterpolator cycleInterpolator = new CycleInterpolator(3);
            mConfirmPasswordErrorAnimator.setInterpolator(cycleInterpolator);
            mConfirmPasswordErrorAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mHandler.removeMessages(HIDE_CONFIRM_PASSWORD_ERROR);
                    mHandler.sendEmptyMessageDelayed(HIDE_CONFIRM_PASSWORD_ERROR, 1500);
                    mConfirmPassword.setBackgroundResource(R.drawable.normal_g6_focus_y3_stroke_all_3dp);
                    mConfirmPassword.requestFocus();
                    mConfirmPassword.setSelection(getTextLength(mConfirmPassword));
                }
            });
        }
        if (!isVisible(mConfirmPasswordError)) {
            mConfirmPasswordError.setVisibility(View.VISIBLE);
        }
        mConfirmPassword.setBackgroundResource(R.drawable.r8_stroke_all_3dp);
        mConfirmPassword.clearFocus();
        mConfirmPasswordErrorAnimator.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.original_password_status:
                if (mOriginPasswrodShow) {
                    mOriginalPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ImageUtil.showPic(mOriginalPasswordStatus, R.drawable.ic_nosee);
                } else {
                    mOriginalPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ImageUtil.showPic(mOriginalPasswordStatus, R.drawable.ic_see);
                }
                setSelection(mOriginalPassword);
                mOriginPasswrodShow = !mOriginPasswrodShow;
                break;
            case R.id.clear_original_password:
                mOriginalPassword.setText("");
                mClearOriginalPassword.setVisibility(View.INVISIBLE);
                break;
            case R.id.new_password_status:
                if (mNewPasswordShow) {
                    ImageUtil.showPic(mNewPassowrdStatus, R.drawable.ic_nosee);
                    mNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    ImageUtil.showPic(mNewPassowrdStatus, R.drawable.ic_see);
                    mNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                setSelection(mNewPassword);
                mNewPasswordShow = !mNewPasswordShow;
                break;
            case R.id.clear_new_password:
                mNewPassword.setText("");
                mClearNewPassword.setVisibility(View.INVISIBLE);
                break;
            case R.id.confirm_password_status:
                if (mConfirmPasswordShow) {
                    ImageUtil.showPic(mConfirmPassowrdStatus, R.drawable.ic_nosee);
                    mConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    ImageUtil.showPic(mConfirmPassowrdStatus, R.drawable.ic_see);
                    mConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                setSelection(mConfirmPassword);
                mConfirmPasswordShow = !mConfirmPasswordShow;
                break;
            case R.id.clear_confirm_password:
                mConfirmPassword.setText("");
                mClearConfirmPassowrd.setVisibility(View.INVISIBLE);
                break;
            case R.id.change_password:
                if (isVisible(mOriginalPassword) && !DensityUtil.MD5code(getText(mOriginalPassword)).equals(mLocalOriginalPassword)) {
                    startOriginPasswordErrorAnimator();
                } else if (getTextLength(mNewPassword) < 8) {
                    startNewPasswrodErrorAnimator();
                } else if(getText(mOriginalPassword).equals(getText(mNewPassword))){
                    showFiveToast("新密码不能与原密码相同哦");
                }else if (!getText(mConfirmPassword).equals(getText(mNewPassword))) {
                    startConfirmPasswordErrorAnimator();
                } else {
                    mChangePassword.setClickable(false);
                    changePasswordByOriginal();
                }
                break;
        }
    }

    private void changePasswordByOriginal() {
        showLoadingDialog("正在修改");
        BaseRequest baseRequest = getOkGo(HttpConstants.requestChangePassword)
                .params("passCode", mPassCode == null ? DensityUtil.MD5code(mUserInfo.getSerect_code() + "*&*" +
                        mUserInfo.getCreate_time() + "*&*" + mUserInfo.getId()) : mPassCode)
                .params("type", mPassCode == null ? "1" : "0")
                .params("newPassword", DensityUtil.MD5code(getText(mNewPassword)));

        if (mPassCode == null) {
            baseRequest.params("originalPassword", DensityUtil.MD5code(getText(mOriginalPassword)));
        }

        baseRequest.execute(new JsonCallback<Base_Class_Info<Void>>() {
            @Override
            public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                mUserInfo.setPassword(DensityUtil.MD5code(getText(mNewPassword)));
                MyApplication.getInstance().getDatabaseImp().insertUserToDatabase(mUserInfo);
                dismmisLoadingDialog();
                showFiveToast("密码修改成功");
                startActivity(MainActivity.class,ConstansUtil.REQUEST_FOR_RESULT,ConstansUtil.CHANGE_PASSWORD);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                mChangePassword.setClickable(true);
                if (!handleException(e)) {
                    switch (e.getMessage()) {
                        case "101":
                        case "104":
                        case "105":
                            showFiveToast("服务器异常，请稍后再试");
                            break;
                        case "103":
                            startOriginPasswordErrorAnimator();
                            break;
                    }
                }
            }
        });
    }

}
