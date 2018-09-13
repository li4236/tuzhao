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
import android.text.method.DigitsKeyListener;
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
 * <p>
 * 通过原密码修改密码，通过短信验证码修改密码（不需要输入原密码）
 * </p>
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

    private static final String INPUT_ORIGIN_PASSWORD = "请输入原密码";

    private static final String ORIGIN_PASSWROD_INCORRECT = "原密码不正确";

    private static final String INPUT_NEW_PASSWORD = "请输入新密码";

    private static final String PASSWROD_LENGTH_CANNOT_LESS_THAN_EIGHT = "密码长度不能少于8位";

    private static final String SAME_OF_ORIGIN_PASSWORD = "新密码不能与原密码一样";

    private static final String INPUT_NEW_PASSWORD_AGAIN = "请再次输入新密码";

    private static final String PASSWORD_IS_DIFFERENT = "两次输入的密码不一样";

    private boolean mOriginPasswrodShow;

    private boolean mNewPasswordShow;

    private boolean mConfirmPasswordShow;

    private String mPassCode;

    private User_Info mUserInfo;

    private boolean mAlreadyClick;

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

        initHandle();
        initEditTextChange();
        initDigits();
        //initEditTextFocusChange();
    }

    private void initHandle() {
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case ORIGIN_PASSWROD_ERROR:
                        startOriginPasswordErrorAnimator();
                        return true;
                    case NEW_PASSWROD_ERROR:
                        startNewPasswrodErrorAnimator();
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
    }

    private void initEditTextChange() {
        mOriginalPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getTextLength(mOriginalPassword) == 0) {
                    //输入框为空的时候不显示删除按钮
                    if (isVisible(mClearOriginalPassword)) {
                        mClearOriginalPassword.setVisibility(View.INVISIBLE);
                    }
                } else {
                    showView(mClearOriginalPassword);
                    if (mAlreadyClick) {
                        //如果点击了确认修改按钮后，原密码错误的则显示错误信息
                        originPasswrodIsCorrect();
                    }
                    /*if (!DensityUtil.MD5code(getText(mOriginalPassword)).equals(mLocalOriginalPassword)) {
                        mHandler.removeMessages(ORIGIN_PASSWROD_ERROR);
                        mHandler.sendEmptyMessageDelayed(ORIGIN_PASSWROD_ERROR, 1000);
                    }*/
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                    hideView(mClearNewPassword);
                } else {
                    showView(mClearNewPassword);
                    newPasswordIsCorrect();
                    confirmPasswordIsCorrect();
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
                    hideView(mClearConfirmPassowrd);
                } else {
                    showView(mClearConfirmPassowrd);
                    confirmPasswordIsCorrect();
                    /*if (!getText(mNewPassword).equals(getText(mConfirmPassword))) {
                        mHandler.removeMessages(CONFIRM_PASSWROD_ERROR);
                        mHandler.sendEmptyMessageDelayed(CONFIRM_PASSWROD_ERROR, 800);
                    }*/
                }
            }
        });
    }

    private void initDigits() {
        mOriginalPassword.setKeyListener(DigitsKeyListener.getInstance("0123456789abcdefghijklmnopqistuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,./?';:[]{}!@#$%^&*()~<>！￥？【】、《》，。-=+_"));
        mNewPassword.setKeyListener(DigitsKeyListener.getInstance("0123456789abcdefghijklmnopqistuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,./?';:[]{}!@#$%^*()~&<>！￥？【】、《》，。-=+_"));
        mConfirmPassword.setKeyListener(DigitsKeyListener.getInstance("0123456789abcdefghijklmnopqistuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,./?';:[]{}!@#$%^*()~&<>！￥？【】、《》，。-=+_"));
    }

    private void initEditTextFocusChange() {
        if (isVisible(mOriginalPassword)) {
            mOriginalPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        if (DensityUtil.MD5code(getText(mOriginalPassword)).equals(mLocalOriginalPassword)) {
                            if (newPasswordIsCorrect()) {
                                if (!getText(mNewPassword).equals(getText(mConfirmPassword))) {
                                    mOriginalPassword.clearFocus();
                                    startConfirmPasswordErrorAnimator();
                                }
                            } else {
                                mOriginalPassword.clearFocus();
                            }
                        }
                    }
                }
            });
        }

        mNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (isVisible(mOriginalPassword)) {
                        if (!DensityUtil.MD5code(getText(mOriginalPassword)).equals(mLocalOriginalPassword)) {
                            mNewPassword.clearFocus();
                            startOriginPasswordErrorAnimator();
                        }
                    }
                }
            }
        });

        mConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (isVisible(mOriginalPassword)) {
                        if (!DensityUtil.MD5code(getText(mOriginalPassword)).equals(mLocalOriginalPassword)) {
                            mConfirmPassword.clearFocus();
                            startOriginPasswordErrorAnimator();
                            return;
                        }
                    }
                    newPasswordIsCorrect();
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

    /**
     * 原密码错误时，输入框左右移动300毫秒
     */
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
                   /* mHandler.removeMessages(HIDE_ORIGINAL_PASSWORD_ERROR);
                    mHandler.sendEmptyMessageDelayed(HIDE_ORIGINAL_PASSWORD_ERROR, 1500);

                    mOriginalPassword.setBackgroundResource(R.drawable.normal_g6_focus_y3_stroke_all_3dp);*/
                    //mOriginalPassword.requestFocus();
                    //动画结束后原密码输入框获取焦点
                    mOriginalPassword.setSelection(getTextLength(mOriginalPassword));
                    setFocus();
                }
            });
        }
        /*if (!isVisible(mOriginalPasswordError)) {
            mOriginalPasswordError.setVisibility(View.VISIBLE);
        }
        mOriginalPassword.setBackgroundResource(R.drawable.r8_stroke_all_3dp);*/
        //mOriginalPassword.clearFocus();
        mOriginPasswordErrorAnimator.start();
    }

    /**
     * 开始新密码的错误提示动画
     */
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
                    /*mHandler.sendEmptyMessageDelayed(HIDE_NEW_PASSWORD_ERROR, 1500);
                    mNewPassword.setBackgroundResource(R.drawable.normal_g6_focus_y3_stroke_all_3dp);*/
                    //mNewPassword.requestFocus();
                    mNewPassword.setSelection(getTextLength(mNewPassword));
                    setFocus();
                }
            });
        }
        /*mNewPasswordError.setVisibility(View.VISIBLE);
        mNewPassword.setBackgroundResource(R.drawable.r8_stroke_all_3dp);*/
        //mNewPassword.clearFocus();
        mNewPasswrodErrorAnimator.start();
    }

    /**
     * 开启确认密码的错误提示动画
     */
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
                    /*mHandler.removeMessages(HIDE_CONFIRM_PASSWORD_ERROR);
                    mHandler.sendEmptyMessageDelayed(HIDE_CONFIRM_PASSWORD_ERROR, 1500);
                    mConfirmPassword.setBackgroundResource(R.drawable.normal_g6_focus_y3_stroke_all_3dp);*/
                    //mConfirmPassword.requestFocus();
                    mConfirmPassword.setSelection(getTextLength(mConfirmPassword));
                    setFocus();
                }
            });
        }
        /*if (!isVisible(mConfirmPasswordError)) {
            mConfirmPasswordError.setVisibility(View.VISIBLE);
        }
        mConfirmPassword.setBackgroundResource(R.drawable.r8_stroke_all_3dp);*/
        //mConfirmPassword.clearFocus();
        mConfirmPasswordErrorAnimator.start();
    }

    /**
     * 从上往下，如果有错误的则获取焦点
     */
    private void setFocus() {
        if (isVisible(mOriginalPasswordError)) {
            mOriginalPassword.requestFocus();
        } else if (isVisible(mNewPasswordError)) {
            mNewPassword.requestFocus();
        } else if (isVisible(mConfirmPasswordError)) {
            mConfirmPassword.requestFocus();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.original_password_status:
                if (mOriginPasswrodShow) {
                    //密码显示为圆点
                    mOriginalPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ImageUtil.showPic(mOriginalPasswordStatus, R.drawable.ic_nosee);
                } else {
                    //显示出具体的密码
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
                mAlreadyClick = true;
                if (allPasswrodIsLegal()) {
                    mChangePassword.setClickable(false);
                    changePasswordByOriginal();
                }
                break;
        }
    }

    /**
     * @return true(原密码正确) false(原密码错误)
     */
    private boolean originPasswrodIsCorrect() {
        if (getTextLength(mOriginalPassword) == 0) {
            //没有输入密码，输入框变红色，并在左下方显示请输入原密码
            mOriginalPassword.setBackgroundResource(R.drawable.r8_stroke_all_3dp);
            if (!getText(mOriginalPasswordError).equals(INPUT_ORIGIN_PASSWORD)) {
                mOriginalPasswordError.setText(INPUT_ORIGIN_PASSWORD);
            }
            showView(mOriginalPasswordError);
            return false;
        } else if (!DensityUtil.MD5code(getText(mOriginalPassword)).equals(mLocalOriginalPassword)) {
            //密码错误
            mOriginalPassword.setBackgroundResource(R.drawable.r8_stroke_all_3dp);
            if (!getText(mOriginalPasswordError).equals(ORIGIN_PASSWROD_INCORRECT)) {
                mOriginalPasswordError.setText(ORIGIN_PASSWROD_INCORRECT);
            }
            showView(mOriginalPasswordError);
            return false;
        } else {
            mOriginalPassword.setBackgroundResource(R.drawable.normal_g6_focus_y3_stroke_all_3dp);
            hideView(mOriginalPasswordError);
            return true;
        }
    }

    /**
     * @return 新密码是否正确
     */
    private boolean newPasswordIsCorrect() {
        if (getTextLength(mNewPassword) == 0) {
            mNewPassword.setBackgroundResource(R.drawable.r8_stroke_all_3dp);
            if (!getText(mNewPasswordError).equals(INPUT_NEW_PASSWORD)) {
                mNewPasswordError.setText(INPUT_NEW_PASSWORD);
            }
            showView(mNewPasswordError);
            return false;
        } else if (getTextLength(mNewPassword) < 8) {
            mNewPassword.setBackgroundResource(R.drawable.r8_stroke_all_3dp);
            if (!getText(mNewPasswordError).equals(PASSWROD_LENGTH_CANNOT_LESS_THAN_EIGHT)) {
                mNewPasswordError.setText(PASSWROD_LENGTH_CANNOT_LESS_THAN_EIGHT);
            }
            showView(mNewPasswordError);
            return false;
        } else if (DensityUtil.MD5code(getText(mNewPassword)).equals(mLocalOriginalPassword)) {
            mNewPassword.setBackgroundResource(R.drawable.r8_stroke_all_3dp);
            if (!getText(mNewPasswordError).equals(SAME_OF_ORIGIN_PASSWORD)) {
                mNewPasswordError.setText(SAME_OF_ORIGIN_PASSWORD);
            }
            showView(mNewPasswordError);
            return false;
        } else {
            mNewPassword.setBackgroundResource(R.drawable.normal_g6_focus_y3_stroke_all_3dp);
            hideView(mNewPasswordError);
            return true;
        }
    }

    /**
     *
     * @return  确认密码是否正确
     */
    private boolean confirmPasswordIsCorrect() {
        if (mAlreadyClick) {
            if (getTextLength(mConfirmPassword) == 0) {
                mConfirmPassword.setBackgroundResource(R.drawable.r8_stroke_all_3dp);
                if (!getText(mConfirmPasswordError).equals(INPUT_NEW_PASSWORD_AGAIN)) {
                    mConfirmPasswordError.setText(INPUT_NEW_PASSWORD_AGAIN);
                }
                showView(mConfirmPasswordError);
                return false;
            } else if (!getText(mNewPassword).equals(getText(mConfirmPassword))) {
                mConfirmPassword.setBackgroundResource(R.drawable.r8_stroke_all_3dp);
                if (!getText(mConfirmPasswordError).equals(PASSWORD_IS_DIFFERENT)) {
                    mConfirmPasswordError.setText(PASSWORD_IS_DIFFERENT);
                }
                showView(mConfirmPasswordError);
                return false;
            } else {
                mConfirmPassword.setBackgroundResource(R.drawable.normal_g6_focus_y3_stroke_all_3dp);
                hideView(mConfirmPasswordError);
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return  全部密码是否都合法，如果有不正确的则会开启动画
     */
    private boolean allPasswrodIsLegal() {
        boolean result = true;
        if (isVisible(mOriginalPassword)) {
            if (!originPasswrodIsCorrect()) {
                startOriginPasswordErrorAnimator();
                result = false;
            }
        }
        if (!newPasswordIsCorrect()) {
            startNewPasswrodErrorAnimator();
            result = false;
        }
        if (!confirmPasswordIsCorrect()) {
            startConfirmPasswordErrorAnimator();
            result = false;
        }
        return result;
    }

    /**
     * 通过原密码修改密码
     */
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
                startActivity(MainActivity.class, ConstansUtil.REQUEST_FOR_RESULT, ConstansUtil.CHANGE_PASSWORD);
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
