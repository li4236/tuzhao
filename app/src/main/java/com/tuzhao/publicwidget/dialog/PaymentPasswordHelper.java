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
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.publicwidget.customView.CrossView;
import com.tuzhao.publicwidget.customView.GridDivider;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;

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

    private boolean mCanControl = true;

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
            mPasswordText[i].setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

        mSixthPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(mSixthPassword.getText())) {
                    mCanControl = false;
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < 6; i++) {
                        stringBuilder.append(mPasswordText[i].getText().toString());
                    }
                    Intent intent = new Intent(ConstansUtil.PAYMENT_PASSWORD);
                    intent.putExtra(ConstansUtil.INTENT_MESSAGE, stringBuilder.toString());
                    IntentObserable.dispatch(intent);
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

    public Context getContext() {
        return mContext;
    }

    public View getView() {
        return mView;
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
        protected int converGetItemViewType(int position) {
            if (position == getDataSize() - 1) {
                return R.layout.item_image_layout;
            }
            return super.converGetItemViewType(position);
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
