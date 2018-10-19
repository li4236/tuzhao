package com.tuzhao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.fragment.login.LoginFragment;
import com.tuzhao.fragment.login.LoginInputFragment;
import com.tuzhao.fragment.login.PasswordLoginFragment;
import com.tuzhao.fragment.login.SmsLoginFragment;
import com.tuzhao.info.Pair;
import com.tuzhao.info.User_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.db.DatabaseImp;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;
import com.tuzhao.utils.KeyboardHeightUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.Objects;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by juncoder on 2018/8/31.
 */
public class LoginActivity extends BaseStatusActivity implements IntentObserver, SuccessCallback<Pair<Integer, Integer>> {

    public Pair<Integer, Integer> mPair;

    private KeyboardHeightUtil mKeyboardHeightUtil;

    @Override
    protected int resourceId() {
        return R.layout.activity_login_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mPair = new Pair<>(0,0);
        mKeyboardHeightUtil = new KeyboardHeightUtil(this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.login_container, new LoginFragment());
        transaction.commit();
    }

    @Override
    protected void initData() {
        IntentObserable.registerObserver(this);
    }

    @NonNull
    @Override
    protected String title() {
        return "";
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(ConstansUtil.INTENT_MESSAGE)) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            for (int i = 0, size = fragmentManager.getBackStackEntryCount(); i < size; i++) {
                fragmentManager.popBackStackImmediate();
            }
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.login_container, new LoginFragment());
            transaction.commit();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mKeyboardHeightUtil.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mKeyboardHeightUtil.setKeyboardHeightObserver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mKeyboardHeightUtil.setKeyboardHeightObserver(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
        mKeyboardHeightUtil.close();
    }

    @Override
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ConstansUtil.PASSWORD_LOGIN:
                    //跳转到输入密码登录
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    PasswordLoginFragment passwordLoginFragment = PasswordLoginFragment.getInstance(intent.getStringExtra(ConstansUtil.TELEPHONE_NUMBER));
                    transaction.replace(R.id.login_container, passwordLoginFragment);
                    transaction.addToBackStack(passwordLoginFragment.getTAG());
                    transaction.commit();
                    break;
                case ConstansUtil.SMS_LOGIN:
                    //跳转到获取短信验证码界面
                    FragmentTransaction smsTransaction = getSupportFragmentManager().beginTransaction();
                    SmsLoginFragment smsLoginFragment;
                    Bundle smsBundle = intent.getBundleExtra(ConstansUtil.INTENT_MESSAGE);
                    if (smsBundle.getString(ConstansUtil.TELEPHONE_NUMBER) != null) {
                        smsLoginFragment = SmsLoginFragment.getInstance(smsBundle.getInt(ConstansUtil.STATUS), smsBundle.getString(ConstansUtil.TELEPHONE_NUMBER));
                    } else {
                        smsLoginFragment = SmsLoginFragment.getInstance(smsBundle.getInt(ConstansUtil.STATUS), (User_Info) Objects.requireNonNull(smsBundle.getParcelable(ConstansUtil.USER_INFO)));
                    }
                    smsTransaction.replace(R.id.login_container, smsLoginFragment);
                    smsTransaction.addToBackStack(smsLoginFragment.getTAG());
                    smsTransaction.commit();
                    break;
                case ConstansUtil.SET_NEW_USER_PASSWORD:
                    //设置新用户密码
                    FragmentTransaction setNewUserPasswordTransation = getSupportFragmentManager().beginTransaction();
                    Bundle setNewUserPasswordBundle = intent.getBundleExtra(ConstansUtil.INTENT_MESSAGE);
                    LoginInputFragment setNewUserPasswordFragment = LoginInputFragment.getInstance(setNewUserPasswordBundle.getString(ConstansUtil.TELEPHONE_NUMBER),
                            setNewUserPasswordBundle.getString(ConstansUtil.PASS_CODE), 0);
                    setNewUserPasswordTransation.replace(R.id.login_container, setNewUserPasswordFragment);
                    setNewUserPasswordTransation.commit();
                    break;
                case ConstansUtil.WECHAT_TELEPHONE_LOGIN:
                    FragmentTransaction wechatTelephoneTransaction = getSupportFragmentManager().beginTransaction();
                    LoginInputFragment telephoneInputFragment = LoginInputFragment.getInstance(
                            (User_Info) intent.getParcelableExtra(ConstansUtil.USER_INFO));
                    wechatTelephoneTransaction.replace(R.id.login_container, telephoneInputFragment);
                    wechatTelephoneTransaction.addToBackStack(telephoneInputFragment.getTAG());
                    wechatTelephoneTransaction.commit();
                    break;
                case ConstansUtil.WECHAT_PASSWORD_LOGIN:
                    FragmentTransaction wechatTransaction = getSupportFragmentManager().beginTransaction();
                    Bundle bundle = intent.getBundleExtra(ConstansUtil.INTENT_MESSAGE);
                    LoginInputFragment loginInputFragment = LoginInputFragment.getInstance(
                            (User_Info) bundle.getParcelable(ConstansUtil.USER_INFO), bundle.getString(ConstansUtil.PASS_CODE));
                    wechatTransaction.replace(R.id.login_container, loginInputFragment);
                    wechatTransaction.addToBackStack(loginInputFragment.getTAG());
                    wechatTransaction.commit();
                    break;
                case ConstansUtil.CHANGE_PASSWORD:
                    FragmentTransaction forgetPasswordTransaction = getSupportFragmentManager().beginTransaction();
                    Bundle forgetPasswordBundle = intent.getBundleExtra(ConstansUtil.INTENT_MESSAGE);
                    LoginInputFragment forgetPasswordFragment = LoginInputFragment.getInstance(
                            forgetPasswordBundle.getString(ConstansUtil.TELEPHONE_NUMBER), forgetPasswordBundle.getString(ConstansUtil.PASS_CODE), 1);
                    forgetPasswordTransaction.replace(R.id.login_container, forgetPasswordFragment);
                    forgetPasswordTransaction.addToBackStack(forgetPasswordFragment.getTAG());
                    forgetPasswordTransaction.commit();
                    break;
                case ConstansUtil.LOGIN_SUCCESS:
                    loginSuccess((User_Info) intent.getParcelableExtra(ConstansUtil.INTENT_MESSAGE));
                    break;
            }
        }
    }

    private void loginSuccess(User_Info userInfo) {
        //请求成功,储存用户登录信息
        userInfo.setAutologin("1");
        UserManager.getInstance().setUserInfo(userInfo);
        UserManager.getInstance().setHasLogin(true);
        //更新覆盖本地数据库的用户信息
        new DatabaseImp(this).insertUserToDatabase(userInfo);

        JPushInterface.setAlias(this, 1, userInfo.getUsername());//登录成功后给该用户设置极光推送的别名
        MobclickAgent.onProfileSignIn(userInfo.getUsername());//友盟在用户登录操作统计
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("LOGIN_ACTION"));

        if (getIntent().hasExtra(ConstansUtil.INTENT_MESSAGE)) {
            //如果是在其他界面需要登录的，则登录后返回其他界面
            finish();
        } else {
            startActivity(MainActivity.class);
        }
    }

    @Override
    public void onSuccess(Pair<Integer, Integer> pair) {
        mPair = pair;
        IntentObserable.dispatch(ConstansUtil.KEYBOARD_HEIGHT_CHANGE, ConstansUtil.INTENT_MESSAGE, pair.getFirst(), ConstansUtil.HEIGHT, pair.getSecond());
    }

}
