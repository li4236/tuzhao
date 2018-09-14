package com.tuzhao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.fragment.login.LoginFragment;
import com.tuzhao.fragment.login.LoginInputFragment;
import com.tuzhao.fragment.login.PasswordLoginFragment;
import com.tuzhao.fragment.login.SmsLoginFragment;
import com.tuzhao.info.User_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.db.DatabaseImp;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;
import com.umeng.analytics.MobclickAgent;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by juncoder on 2018/8/31.
 */
public class LoginActivity extends BaseStatusActivity implements IntentObserver {

    @Override
    protected int resourceId() {
        return R.layout.activity_login_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
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
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
    }

    @Override
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ConstansUtil.PASSWORD_LOGIN:
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    PasswordLoginFragment passwordLoginFragment = PasswordLoginFragment.getInstance(intent.getStringExtra(ConstansUtil.TELEPHONE_NUMBER));
                    transaction.replace(R.id.login_container, passwordLoginFragment);
                    transaction.addToBackStack(passwordLoginFragment.getTAG());
                    transaction.commit();
                    break;
                case ConstansUtil.FORGET_PASSWORD:
                    FragmentTransaction forgetPasswordSmsTransaction = getSupportFragmentManager().beginTransaction();
                    SmsLoginFragment forgetPasswordSmsFragment = SmsLoginFragment.getInstance(
                            1, intent.getStringExtra(ConstansUtil.TELEPHONE_NUMBER));
                    forgetPasswordSmsTransaction.replace(R.id.login_container, forgetPasswordSmsFragment);
                    forgetPasswordSmsTransaction.addToBackStack(forgetPasswordSmsFragment.getTAG());
                    forgetPasswordSmsTransaction.commit();
                    break;
                case ConstansUtil.SMS_LOGIN:
                    FragmentTransaction smsTransaction = getSupportFragmentManager().beginTransaction();
                    SmsLoginFragment smsLoginFragment;
                    if (intent.hasExtra(ConstansUtil.USER_INFO)) {
                        smsLoginFragment = SmsLoginFragment.getInstance(2, (User_Info) intent.getParcelableExtra(ConstansUtil.USER_INFO));
                    } else {
                        Bundle bundle = intent.getBundleExtra(ConstansUtil.INTENT_MESSAGE);
                        if (bundle != null) {
                            smsLoginFragment = SmsLoginFragment.getInstance(bundle.getInt(ConstansUtil.STATUS), bundle.getString(ConstansUtil.TELEPHONE_NUMBER));
                        } else {
                            smsLoginFragment = SmsLoginFragment.getInstance(0, intent.getStringExtra(ConstansUtil.TELEPHONE_NUMBER));
                        }
                    }
                    smsTransaction.replace(R.id.login_container, smsLoginFragment);
                    smsTransaction.addToBackStack(smsLoginFragment.getTAG());
                    smsTransaction.commit();
                    break;
                case ConstansUtil.SET_NEW_USER_PASSWORD:
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
                case ConstansUtil.FINISH_FRAGMENT:
                    getSupportFragmentManager().popBackStack();
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

        startActivity(MainActivity.class);
    }

}
