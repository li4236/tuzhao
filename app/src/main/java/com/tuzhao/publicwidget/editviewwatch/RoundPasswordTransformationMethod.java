package com.tuzhao.publicwidget.editviewwatch;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

/**
 * Created by juncoder on 2018/8/21.
 * <p>
 * 把TextView输入的数字改为圆点显示
 * </p>
 */
public class RoundPasswordTransformationMethod extends PasswordTransformationMethod {

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source);
    }

    private class PasswordCharSequence implements CharSequence {
        private CharSequence mSource;

        PasswordCharSequence(CharSequence source) {
            mSource = source; // Store char sequence
        }

        public char charAt(int index) {
        /*
        当在编辑框中输入1的时候，会连续打印0...
        当在编辑框中继续输入2的时候，会连续01...
        不影响功能使用，但是出现原因不知，待解决
         */
            //这里返回的char，就是密码的样式，注意，是char类型的
            return '●'; // This is the important part
        }

        public int length() {
            return mSource.length(); // Return default
        }

        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end); // Return default
        }
    }

}
