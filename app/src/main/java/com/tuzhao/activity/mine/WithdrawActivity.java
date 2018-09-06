package com.tuzhao.activity.mine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCodeCallback;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.PaymentPasswordHelper;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DataUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;
import com.tuzhao.utils.ViewUtil;

import java.text.DecimalFormat;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/8/10.
 */
public class WithdrawActivity extends BaseStatusActivity implements View.OnClickListener, IntentObserver {

    private static final int RESET_REQUEST_CODE = 0x123;

    private int mWithdrawAccountType;

    private EditText mWithdrawMoney;

    private PaymentPasswordHelper mPasswordHelper;

    private CustomDialog mCustomDialog;

    private CustomDialog mSetPasswordDialog;

    private User_Info mUserInfo;

    @Override
    protected int resourceId() {
        return R.layout.activity_withdraw_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mWithdrawAccountType = getIntent().getIntExtra(ConstansUtil.INTENT_MESSAGE, 0);
        mUserInfo = UserManager.getInstance().getUserInfo();

        TextView withdrawToAccount = findViewById(R.id.withdrawl_to_account);
        TextView avaliableBalance = findViewById(R.id.available_banlance);
        mWithdrawMoney = findViewById(R.id.withdraw_money);

        StringBuilder stringBuilder = new StringBuilder("将提现至");
        if (mWithdrawAccountType == 0) {
            stringBuilder.append("支付宝账号");
            stringBuilder.append(mUserInfo.getAliNickname());
        } else if (mWithdrawAccountType == 1) {
            stringBuilder.append("微信账号");
            stringBuilder.append(mUserInfo.getWechatNickname());
        }

        SpannableString spannableString = new SpannableString(stringBuilder);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0979fe")), stringBuilder.indexOf("账号") + 2, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        withdrawToAccount.setText(spannableString);

        avaliableBalance.setText("可用余额");
        avaliableBalance.append(mUserInfo.getBalance());
        ViewUtil.openInputMethod(mWithdrawMoney);

        findViewById(R.id.full_withdraw).setOnClickListener(this);
        findViewById(R.id.withdraw).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mWithdrawMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int index = getText(mWithdrawMoney).indexOf(".");
                if (index == 0) {
                    mWithdrawMoney.setText("");
                } else if (index > 0) {
                    int textLength = getTextLength(mWithdrawMoney);
                    if (textLength == 6 && index == textLength - 1) {
                        mWithdrawMoney.setText(getText(mWithdrawMoney).substring(0, textLength - 1));
                        setSelection(mWithdrawMoney);
                    } else if (textLength - index > 3) {
                        mWithdrawMoney.setText(getText(mWithdrawMoney).substring(0, index + 3));
                        setSelection(mWithdrawMoney);
                    }
                }
            }
        });

        mPasswordHelper = new PaymentPasswordHelper(this);
        mCustomDialog = new CustomDialog(mPasswordHelper);
        mPasswordHelper.setForgetPasswordListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomDialog.dismiss();
                showSetPasswordDialog(2);
            }
        });

        IntentObserable.registerObserver(this);
    }

    @NonNull
    @Override
    protected String title() {
        return "提现";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.full_withdraw:
                mWithdrawMoney.setText(mUserInfo.getBalance());
                setSelection(mWithdrawMoney);
                break;
            case R.id.withdraw:
                if (getTextLength(mWithdrawMoney) == 0) {
                    showFiveToast("请输入要提现的金额");
                } else {
                    float withdrawlMoney = Float.valueOf(getText(mWithdrawMoney));
                    if (withdrawlMoney == 0) {
                        showFiveToast("提现的金额不能为0哦");
                    } else if (!DataUtil.numberLegal(getText(mWithdrawMoney))) {
                        showFiveToast("金额格式不对哦");
                    } else if (withdrawlMoney > Float.valueOf(mUserInfo.getBalance())) {
                        showFiveToast("余额没有那么多哦");
                    } else if (mUserInfo.getPaymentPassword().equals("-1")) {
                        showFiveToast("请先设置您的支付密码");
                        showSetPasswordDialog(1);
                    } else {
                        mCustomDialog.show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSetPasswordDialog != null && mSetPasswordDialog.isShowing()) {
            if (!mSetPasswordDialog.getPasswordHelper().isBackPressedCanCancel() && !mSetPasswordDialog.getPasswordHelper().getConfirmPassword().equals("")) {
                mSetPasswordDialog.getPasswordHelper().setPasswordFirst();
                showFiveToast("设置密码失败");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
    }

    private void showSetPasswordDialog(int type) {
        //设置支付密码
        if (mSetPasswordDialog == null || mSetPasswordDialog.getPasswordType() != type) {
            mSetPasswordDialog = new CustomDialog(new PaymentPasswordHelper(WithdrawActivity.this, type));
        }
        mSetPasswordDialog.show();
    }

    private void withdrawlBalance(String paymentPassword) {
        showLoadingDialog("加载中...");
        Log.e(TAG, "withdrawlBalance: " + paymentPassword);
        getOkGo(HttpConstants.withdrawlBalance)
                .params("accountType", mWithdrawAccountType)
                .params("amount", getText(mWithdrawMoney))
                .params("paymentPassword", DensityUtil.MD5code(paymentPassword))
                .params("passCode", DensityUtil.MD5code(mUserInfo.getSerect_code() + "*&*" +
                        mUserInfo.getCreate_time() + "*&*" + mUserInfo.getId()))
                .execute(new JsonCodeCallback<Base_Class_Info<Integer>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Integer> o, Call call, Response response) {
                        dismmisLoadingDialog();
                        switch (o.code) {
                            case "0":
                                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                                mUserInfo.setBalance(decimalFormat.format(Double.valueOf(mUserInfo.getBalance()) - Double.valueOf(getText(mWithdrawMoney))));
                                mPasswordHelper.setCanControl(true);
                                mCustomDialog.dismiss();
                                IntentObserable.dispatch(ConstansUtil.WITHDRAWL_SUCCESS);
                                showFiveToast("提现成功");
                                finish();
                                break;
                            case "101":
                            case "102":
                            case "103":
                            case "104":
                            case "105":
                                mCustomDialog.dismiss();
                                userError();
                                break;
                            case "106":
                                //未设置支付密码
                                mCustomDialog.dismiss();
                                showSetPasswordDialog(1);
                                break;
                            case "107":
                                //密码错误超过三次
                                showFiveToast("您已输错三次支付密码，请重置密码后再使用");
                                mCustomDialog.dismiss();
                                showSetPasswordDialog(2);
                                break;
                            case "108":
                                //密码错误
                                showFiveToast("支付密码错误，您还可以输入" + (4 - o.data) + "次");
                                mPasswordHelper.showPasswordError("支付密码错误，请重新输入");
                                mPasswordHelper.setCanControl(true);
                                mPasswordHelper.clearPassword();
                                break;
                            case "109":
                                showFiveToast("提现的金额异常，请重新输入");
                                mCustomDialog.dismiss();
                                mWithdrawMoney.setText("");
                                break;
                            case "110":
                                showFiveToast("微信账号绑定异常,请重新绑定");
                                break;
                            case "111":
                                showFiveToast("支付宝账号绑定异常，请重新绑定");
                                break;
                            case "112":
                                showFiveToast("支付宝提现不可低于0.1元");
                                mCustomDialog.dismiss();
                                mWithdrawMoney.setText("");
                                break;
                            case "113":
                                showFiveToast("提现失败，请稍后重试");
                                mCustomDialog.dismiss();
                                break;
                            default:
                                mPasswordHelper.setCanControl(true);
                                mPasswordHelper.clearPassword();
                                showFiveToast("服务器异常，请稍后重试");
                                break;
                        }
                    }
                });
    }

    @Override
    public void onReceive(Intent intent) {
        if (Objects.equals(intent.getAction(), ConstansUtil.PAYMENT_PASSWORD)) {
            withdrawlBalance(intent.getStringExtra(ConstansUtil.INTENT_MESSAGE));
        } else if (Objects.equals(intent.getAction(), ConstansUtil.SET_PAYMENT_PASSWORD)) {
            Intent verifyIntent = new Intent(WithdrawActivity.this, SMSVerificationActivity.class);
            verifyIntent.setAction(ConstansUtil.SET_PAYMENT_PASSWORD);
            verifyIntent.putExtra(ConstansUtil.PAYMENT_PASSWORD, intent.getStringExtra(ConstansUtil.PAYMENT_PASSWORD));
            startActivityForResult(verifyIntent, ConstansUtil.REQUSET_CODE);
        } else if (Objects.equals(intent.getAction(), ConstansUtil.RESET_PAYMENT_PASSWORD)) {
            Intent resetIntent = new Intent(WithdrawActivity.this, SMSVerificationActivity.class);
            resetIntent.setAction(ConstansUtil.RESET_PAYMENT_PASSWORD);
            resetIntent.putExtra(ConstansUtil.PAYMENT_PASSWORD, intent.getStringExtra(ConstansUtil.PAYMENT_PASSWORD));
            startActivityForResult(resetIntent, RESET_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ConstansUtil.REQUSET_CODE || requestCode == RESET_REQUEST_CODE) && resultCode == RESULT_OK) {
            //设置密码后弹出输入密码框继续输入
            mSetPasswordDialog.getPasswordHelper().clearPassword();
            mSetPasswordDialog.dismiss();
            mPasswordHelper.setCanControl(true);
            mPasswordHelper.clearPassword();
            mCustomDialog.show();
        }
    }

}
