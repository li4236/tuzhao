package com.tuzhao.publicwidget.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.adapter.BaseAdapter;
import com.tuzhao.adapter.BaseViewHolder;
import com.tuzhao.publicwidget.customView.CrossView;
import com.tuzhao.publicwidget.customView.GridDivider;
import com.tuzhao.publicwidget.editviewwatch.RoundPasswordTransformationMethod;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juncoder on 2018/8/11.
 */
public class PaymentPasswordHelper {

    private Context mContext;

    private View mView;

    private CrossView mCrossView;

    private TextView mTitle;

    private TextView mPleaseInputPasswordHint;

    private TextView mSixthPassword;

    private TextView mPasswordError;

    private TextView mForgetPassword;

    private TextView[] mPasswordText;

    /**
     * 是否可以输入和删除，在网络请求期间不可以
     */
    private boolean mCanControl = true;

    /**
     * 是否是设置支付密码,0（输入密码），1（设置密码），2（重置密码）
     */
    private int mPaymentPasswordType;

    /**
     * 按返回键是否可以关掉对话框
     */
    private boolean mBackPressedCanCancel = true;

    /**
     * 设置密码时第一次输入的密码
     */
    private String mOriginPassword;

    public PaymentPasswordHelper(Context context) {
        mContext = context;
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_payment_password_layout, null);
        mCrossView = mView.findViewById(R.id.close_dialog);
        mTitle = mView.findViewById(R.id.please_input_payment_password);
        mPleaseInputPasswordHint = mView.findViewById(R.id.input_password_hint);
        TextView firstPassword = mView.findViewById(R.id.first_password);
        TextView secondPassword = mView.findViewById(R.id.second_password);
        TextView thirdPassowrd = mView.findViewById(R.id.third_password);
        TextView fourthPassword = mView.findViewById(R.id.fourth_password);
        TextView fifthPassword = mView.findViewById(R.id.fifth_password);
        mSixthPassword = mView.findViewById(R.id.sixth_password);
        mPasswordError = mView.findViewById(R.id.password_error);
        mForgetPassword = mView.findViewById(R.id.forget_password);
        RecyclerView recyclerView = mView.findViewById(R.id.dialog_rv);

        mPasswordText = new TextView[6];
        mPasswordText[0] = firstPassword;
        mPasswordText[1] = secondPassword;
        mPasswordText[2] = thirdPassowrd;
        mPasswordText[3] = fourthPassword;
        mPasswordText[4] = fifthPassword;
        mPasswordText[5] = mSixthPassword;

        for (int i = 0; i < 6; i++) {
            mPasswordText[i].setTransformationMethod(new RoundPasswordTransformationMethod());
        }

        mSixthPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(mSixthPassword.getText())) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < 6; i++) {
                        stringBuilder.append(mPasswordText[i].getText().toString());
                    }

                    if (mPaymentPasswordType != 0) {
                        if (mOriginPassword == null) {
                            //设置完第一次密码
                            setPasswordAgain(stringBuilder.toString());
                        } else {
                            //设置密码的确认密码
                            if (!mOriginPassword.equals(stringBuilder.toString())) {
                                ViewUtil.setVisible(mPasswordError);
                                mPasswordError.setText("两次输入的密码不一样");
                            } else {
                                if (mPaymentPasswordType == 1) {
                                    //第一次设置支付密码
                                    IntentObserable.dispatch(ConstansUtil.SET_PAYMENT_PASSWORD, ConstansUtil.PAYMENT_PASSWORD, mOriginPassword);
                                } else if (mPaymentPasswordType == 2) {
                                    //重新设置支付密码
                                    IntentObserable.dispatch(ConstansUtil.RESET_PAYMENT_PASSWORD, ConstansUtil.PAYMENT_PASSWORD, mOriginPassword);
                                }
                                mCanControl = false;
                            }
                        }
                    } else {
                        //支付密码
                        Intent intent = new Intent(ConstansUtil.PAYMENT_PASSWORD);
                        intent.putExtra(ConstansUtil.INTENT_MESSAGE, stringBuilder.toString());
                        IntentObserable.dispatch(intent);
                        mCanControl = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView.addItemDecoration(new GridDivider());
        PasswordAdapter adapter = new PasswordAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setNewData(getData());
    }

    public PaymentPasswordHelper(Context context, int paymentPasswordType) {
        this(context);
        mPaymentPasswordType = paymentPasswordType;
        if (mPaymentPasswordType != 0) {
            setPasswordFirst();
            mForgetPassword.setVisibility(View.INVISIBLE);
        }

    }

    public Context getContext() {
        return mContext;
    }

    public View getView() {
        return mView;
    }

    public boolean isBackPressedCanCancel() {
        return mBackPressedCanCancel;
    }

    public void setBackPressedCanCancel(boolean backPressedCanCancel) {
        mBackPressedCanCancel = backPressedCanCancel;
    }

    public void setPasswordFirst() {
        if (mPaymentPasswordType == 1) {
            mTitle.setText("设置支付密码");
        } else if (mPaymentPasswordType == 2) {
            mTitle.setText("重置支付密码");
        }
        mPleaseInputPasswordHint.setText("请设置您的6位支付密码");
        mOriginPassword = null;
        mBackPressedCanCancel = true;
        mCanControl = true;
        clearPassword();
        ViewUtil.setGone(mPasswordError);
    }

    private void setPasswordAgain(String originPassword) {
        mOriginPassword = originPassword;
        mPleaseInputPasswordHint.setText("请确认您的6位支付密码");
        clearPassword();
        mBackPressedCanCancel = false;
    }

    private List<String> getData() {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            list.add(String.valueOf(i));
        }
        list.add("");
        list.add("0");
        list.add("×");
        return list;
    }

    private void addNumber(String number) {
        for (int i = 0; i < 6; i++) {
            if (TextUtils.isEmpty(mPasswordText[i].getText())) {
                mPasswordText[i].setText(number);
                break;
            }
        }
    }

    private void deleteNumber() {
        for (int i = 5; i >= 0; i--) {
            if (!TextUtils.isEmpty(mPasswordText[i].getText())) {
                mPasswordText[i].setText("");
                break;
            }
        }
        if (mPaymentPasswordType != 0) {
            ViewUtil.setGone(mPasswordError);
        }
    }

    public String getOriginPassword() {
        return mOriginPassword;
    }

    public String getConfirmPassword() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            stringBuilder.append(mPasswordText[i].getText().toString());
        }
        if (mOriginPassword.length() == 6 && !mBackPressedCanCancel && stringBuilder.length() == 6) {
            return stringBuilder.toString();
        }
        return "";
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void clearPassword() {
        for (TextView textView : mPasswordText) {
            textView.setText("");
        }
    }

    public void showPasswordError(String errorMsg) {
        if (!mPasswordError.getText().toString().equals(errorMsg)) {
            mPasswordError.setText(errorMsg);
        }
        if (mPasswordError.getVisibility() != View.VISIBLE) {
            mPasswordError.setVisibility(View.VISIBLE);
        }
    }

    public void setCloseListener(View.OnClickListener closeListener) {
        mCrossView.setOnClickListener(closeListener);
    }

    public void setForgetPasswordListener(View.OnClickListener onClickListener) {
        mForgetPassword.setOnClickListener(onClickListener);
    }

    public void setCanControl(boolean canControl) {
        mCanControl = canControl;
    }

    public int getPaymentPasswordType() {
        return mPaymentPasswordType;
    }

    private class PasswordAdapter extends BaseAdapter<String> {

        private float mScale;

        PasswordAdapter() {
            mScale = mContext.getResources().getDisplayMetrics().density;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        protected void conver(@NonNull BaseViewHolder holder, final String s, int position) {
            switch (s) {
                case "":
                    holder.setBackgroundColor(R.id.number_tv, Color.parseColor("#e4e5eb"));
                    break;
                case "×":
                    final ImageView imageView = holder.getView(R.id.item_iv);
                    imageView.setPadding(dpToPx(46), dpToPx(16.667f), dpToPx(46), dpToPx(16.667f));
                    imageView.setBackgroundColor(Color.parseColor("#e4e5eb"));
                    ImageUtil.showPic(imageView, R.drawable.ic_backspace);
                    imageView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                ImageUtil.showPic(imageView, R.drawable.ic_backspace2);
                                return true;
                            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                                ImageUtil.showPic(imageView, R.drawable.ic_backspace);
                                if (mCanControl && event.getAction() == MotionEvent.ACTION_UP) {
                                    deleteNumber();
                                }
                                return true;
                            }
                            return false;
                        }
                    });
                    break;
                default:
                    holder.setText(R.id.number_tv, s)
                            .setOnClickListener(R.id.number_tv, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mCanControl) {
                                        addNumber(s);
                                    }
                                }
                            });
                    break;
            }

        }

        @Override
        protected int converGetItemViewType(String s, int position) {
            if (position == getDataSize() - 1) {
                return R.layout.item_image_layout;
            }
            return super.converGetItemViewType(s, position);
        }

        @Override
        protected int itemViewId(int viewType) {
            if (viewType == R.layout.item_image_layout) {
                return R.layout.item_image_layout;
            }
            return super.itemViewId(viewType);
        }

        @Override
        protected int itemViewId() {
            return R.layout.item_number_layout;
        }

        private int dpToPx(float dp) {
            return (int) (dp * mScale + 0.5f);
        }
    }

}
