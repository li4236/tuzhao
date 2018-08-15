package com.tuzhao.publicwidget.others;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;

import com.tuzhao.R;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juncoder on 2018/8/14.
 */
public class PasswordLinearLayout extends LinearLayout {

    private int mChildCount;

    private int mChildMargin;

    private int mChildWidth;

    private int mIndex;

    private List<String> mList;

    private SuccessCallback<String> mSuccessCallback;

    public PasswordLinearLayout(Context context) {
        this(context, null);
    }

    public PasswordLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PasswordLinearLayout, defStyleAttr, 0);
        mChildWidth = (int) typedArray.getDimension(R.styleable.PasswordLinearLayout_child_width, dpToPx(36));
        mChildMargin = (int) typedArray.getDimension(R.styleable.PasswordLinearLayout_child_margin, dpToPx(20));
        mChildCount = typedArray.getInt(R.styleable.PasswordLinearLayout_child_count, 4);
        typedArray.recycle();

        setOrientation(HORIZONTAL);
        mList = new ArrayList<>();

        initListener();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() == 0) {
            //如果没有在xml添加子view则在这添加
            addChildView();
        } else {
            //子view的个数就是xml添加的个数
            mChildCount = getChildCount();
        }
    }

    private void addChildView() {
        PasswordView passwordView;
        LayoutParams layoutParams;
        for (int i = 0; i < mChildCount; i++) {
            passwordView = new PasswordView(getContext());
            layoutParams = new LayoutParams(mChildWidth, mChildWidth);      //默认为正方形
            if (i != 0) {
                layoutParams.leftMargin = mChildMargin; //如果不是第一个的话设置距离左边view的margin
            }

            passwordView.setFocusColor(ConstansUtil.Y3_COLOR);
            passwordView.setNormalColor(ConstansUtil.G6_COLOR);
            passwordView.setErrorColor(Color.parseColor("#ff0101"));
            passwordView.setStrokeWidth(dpToPx(1));
            passwordView.setLineWidth(2);
            passwordView.setCornerRadius(dpToPx(3));
            passwordView.setTextSize(DensityUtil.sp2px(getContext(), 16));
            passwordView.setTextColor(ConstansUtil.B1_COLOR);

            addView(passwordView, layoutParams);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void initListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocusable(true);
                setFocusableInTouchMode(true);
                requestFocus();
                ViewUtil.openInputMethod(PasswordLinearLayout.this);
            }
        });

        setOnKeyListener(new MyKeyListener());

        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.e("TAG", "onFocusChange: "+hasFocus );
                PasswordView passwordView = (PasswordView) getChildAt(mIndex);
                if (passwordView != null) {
                    passwordView.updateFocus(hasFocus);
                }
            }
        });
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.inputType = InputType.TYPE_CLASS_NUMBER;          //显示数字键盘
        outAttrs.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI;
        return new ZanyInputConnection(this, false);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SavedState savedState = new SavedState(parcelable);
        savedState.saveString = this.mList;
        savedState.saveInt = this.mIndex;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mList.clear();
        mList.addAll(savedState.saveString);
        mIndex = savedState.saveInt;
        restoreText();
    }

    /**
     * 由于屏幕旋转调了onRestoreInstanceState方法后子view还没有添加，所以等添加完之后再设置
     */
    private void restoreText() {
        post(new Runnable() {
            @Override
            public void run() {
                PasswordView passwordView;
                if (mList.isEmpty()) {
                    //如果之前为空的话，则第一个view获取焦点
                    passwordView = (PasswordView) getChildAt(0);
                    if (passwordView != null) {
                        passwordView.setText(null);
                        passwordView.updateFocus(true);
                    }

                    if (mIndex != 0) {
                        //如果之前的焦点不是第一个，但是现在密码为空，则把其他的view清空并清除焦点
                        for (int i = 1; i < mChildCount; i++) {
                            passwordView = (PasswordView) getChildAt(i);
                            if (passwordView != null) {
                                passwordView.setText(null);
                                passwordView.updateFocus(false);
                            }
                        }
                        mIndex = 0;
                    }

                } else {
                    for (int i = 0; i < mChildCount; i++) {
                        passwordView = (PasswordView) getChildAt(i);
                        if (passwordView != null) {
                            //如果当前有密码的则设置密码，没有就设置为null
                            if (i < mList.size()) {
                                passwordView.setText(mList.get(i));
                            } else {
                                passwordView.setText(null);
                            }
                            //如果是遍历到的第一个没有密码的，或者是最后一个并且有密码的则设置光标
                            if (i == mList.size() || (mList.size() == mChildCount && i == mChildCount - 1)) {
                                passwordView.updateFocus(true);
                            } else {
                                passwordView.updateFocus(false);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 添加一个text到密码框
     */
    private void addText(String text) {
        if (mList.size() < mChildCount && !((PasswordView) getChildAt(0)).isShowError()) {
            mList.add(text);

            if (mIndex < mChildCount) {
                PasswordView passwordView = (PasswordView) getChildAt(mIndex);
                passwordView.setText(text);

                mIndex++;

                if (mIndex < mChildCount) {
                    //没到最后那个，当前设置数字，并且让下一个获取焦点
                    passwordView.updateFocus(false);
                    passwordView = (PasswordView) getChildAt(mIndex);
                    passwordView.updateFocus(true);
                } else {
                    mIndex--;   //还是最后那个获取焦点
                    passwordView.updateFocus(true);
                }
            }
        }

        //如果密码输入完成则回调
        if (mList.size() == mChildCount) {
            if (mSuccessCallback != null) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String s : mList) {
                    stringBuilder.append(s);
                }
                mSuccessCallback.onSuccess(stringBuilder.toString());
            }
        }

    }

    /**
     * 删掉一个密码
     */
    private void deleteNumber() {
        if (!mList.isEmpty()) {
            PasswordView passwordView = (PasswordView) getChildAt(mIndex);
            if (passwordView.getText() == null) {
                if (mIndex > 0) {
                    //当前view没有密码，则删掉它前一个view的密码，并让前一个view获取焦点
                    passwordView.updateFocus(false);
                    mIndex--;
                    passwordView = (PasswordView) getChildAt(mIndex);
                    passwordView.setText(null);
                    passwordView.updateFocus(true);

                    mList.remove(mList.size() - 1);
                } else if (mIndex == 0) {
                    //如果是第一个view则让他获取焦点就行了
                    passwordView.updateFocus(true);
                }
            } else {
                //如果当前view有密码，则清空它的密码
                passwordView.setText(null);
                passwordView.updateFocus(true);

                mList.remove(mList.size() - 1);
            }
        }
    }

    /**
     * 删除全部密码，并让第一个view获取焦点
     */
    public void deleteAll() {
        PasswordView passwordView;
        for (int i = mChildCount - 1; i >= 0; i--) {
            passwordView = (PasswordView) getChildAt(i);
            passwordView.setText(null);
            passwordView.updateFocus(i == 0);
        }
        mList.clear();
        mIndex = 0;
    }

    /**
     * 把String的文字设置进来，长度需要和子view个数一样
     */
    public void setText(String string) {
        if (string.length() == mChildCount) {
            mList.clear();
            PasswordView passwordView;
            String text;
            for (int i = 0; i < mChildCount; i++) {
                passwordView = (PasswordView) getChildAt(i);
                text = String.valueOf(string.charAt(i));
                passwordView.setText(text);
                mList.add(text);

                if (i == mChildCount - 1) {
                    //最后一个获取焦点
                    passwordView.updateFocus(true);
                } else {
                    passwordView.updateFocus(false);
                }
            }

            if (mSuccessCallback != null) {
                mSuccessCallback.onSuccess(string);
            }
        }
    }

    public void setShowError(boolean showError) {
        PasswordView passwordView;
        for (int i = 0; i < mChildCount; i++) {
            passwordView = (PasswordView) getChildAt(i);
            passwordView.setShowError(showError);
        }

        if (!showError) {
            for (int i = 0; i < mChildCount; i++) {
                passwordView = (PasswordView) getChildAt(i);
                if (passwordView.getText() == null || i == mChildCount - 1) {
                    //如果不是显示错误的，则第一个没有密码或者最后view获取焦点
                    passwordView.updateFocus(true);
                    break;
                }
            }
        }
    }

    private class MyKeyListener implements OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                    //接收到0-9则添加到密码框
                    addText(String.valueOf(keyCode - 7));
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DEL) {
                    //删除密码
                    deleteNumber();
                    return true;
                }
            }
            return false;
        }
    }

    private class ZanyInputConnection extends BaseInputConnection {

        @Override
        public boolean commitText(CharSequence txt, int newCursorPosition) {
            return super.commitText(txt, newCursorPosition);
        }

        ZanyInputConnection(View targetView, boolean fullEditor) {
            super(targetView, fullEditor);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            return super.sendKeyEvent(event);
        }


        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            if (beforeLength == 1 && afterLength == 0) {
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)) && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }

            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }

    public static class SavedState extends BaseSavedState {

        private List<String> saveString;

        private int saveInt;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeStringList(saveString);
            dest.writeInt(saveInt);
        }

        private SavedState(Parcel in) {
            super(in);
            in.readStringList(saveString);
            in.readInt();
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public void setSuccessCallback(SuccessCallback<String> successCallback) {
        mSuccessCallback = successCallback;
    }

    private int dpToPx(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
