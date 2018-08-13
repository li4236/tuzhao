package com.tuzhao.activity.mine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.PaymentPasswordHelper;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;
import com.tuzhao.utils.ScreenUtils;

import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/8/10.
 */
public class WithdrawActivity extends BaseStatusActivity implements View.OnClickListener, IntentObserver {

    private int mWithdrawAccountType;

    private EditText mWithdrawMoney;

    private PaymentPasswordHelper mPasswordHelper;

    private CustomDialog mCustomDialog;

    @Override
    protected int resourceId() {
        return R.layout.activity_withdraw_layout;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //mAdapterScreen = true;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mWithdrawAccountType = getIntent().getIntExtra(ConstansUtil.INTENT_MESSAGE, 0);

        TextView withdrawToAccount = findViewById(R.id.withdrawl_to_account);
        TextView avaliableBalance = findViewById(R.id.available_banlance);
        mWithdrawMoney = findViewById(R.id.withdraw_money);

        StringBuilder stringBuilder = new StringBuilder("将提现至");
        if (mWithdrawAccountType == 0) {
            stringBuilder.append("支付宝账号");
            stringBuilder.append(UserManager.getInstance().getUserInfo().getAliNickname());
        } else if (mWithdrawAccountType == 1) {
            stringBuilder.append("微信账号");
            stringBuilder.append(UserManager.getInstance().getUserInfo().getWechatNickname());
        }

        SpannableString spannableString = new SpannableString(stringBuilder);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0979fe")), stringBuilder.indexOf("账号") + 2, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        withdrawToAccount.setText(spannableString);

        avaliableBalance.setText("可用余额");
        avaliableBalance.append(UserManager.getInstance().getUserInfo().getBalance());

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
                mWithdrawMoney.setText(UserManager.getInstance().getUserInfo().getBalance());
                setSelection(mWithdrawMoney);
                break;
            case R.id.withdraw:
                if (getTextLength(mWithdrawMoney) == 0) {
                    showFiveToast("请输入要提现的金额");
                } else {
                    float withdrawlMoney = Float.valueOf(getText(mWithdrawMoney));
                    if (withdrawlMoney == 0) {
                        showFiveToast("提现的金额不能为0哦");
                    } else if (withdrawlMoney > Float.valueOf(UserManager.getInstance().getUserInfo().getBalance())) {
                        showFiveToast("余额没有那么多哦");
                    } else if (withdrawlMoney > 0) {
                        mCustomDialog.show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
        ScreenUtils.cancelAdaptScreen(this);
    }

    private void withdrawlBalance(String paymentPassword) {
        mCustomDialog.setCancelable(false);
        showLoadingDialog("加载中...");
        Log.e(TAG, "withdrawlBalance: " + paymentPassword);
        getOkGo(HttpConstants.withdrawlBalance)
                .params("accountType", mWithdrawAccountType)
                .params("amount", getText(mWithdrawMoney))
                .params("paymentPassword", paymentPassword)
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        mPasswordHelper.setCanControl(true);
                        mCustomDialog.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mPasswordHelper.setCanControl(true);
                        mPasswordHelper.clearPassword();
                        mCustomDialog.setCancelable(true);
                        if (!handleException(e)) {

                        }
                    }
                });
    }

    @Override
    public void onReceive(Intent intent) {
        if (Objects.equals(intent.getAction(), ConstansUtil.PAYMENT_PASSWORD)) {
            withdrawlBalance(intent.getStringExtra(ConstansUtil.INTENT_MESSAGE));
        }
    }

}
