package com.tuzhao.publicwidget.mytoast;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tuzhao.R;


/**
 * Created by TZL13 on 2017/5/17.
 */

public class MyToast {

    public static void showToast(Context context, @DrawableRes int resId, String str,int sum){
           //加载Toast布局
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.toast_have_imageview, null);
        //初始化布局控件
        TextView mTextView = (TextView) toastRoot.findViewById(R.id.message);
        ImageView mImageView = (ImageView) toastRoot.findViewById(R.id.imageView);
        //为控件设置属性
        mTextView.setText(str);
        mImageView.setImageResource(resId);
        //Toast的初始化
        Toast toastStart = new Toast(context);
        //获取屏幕高度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        //Toast的Y坐标是屏幕高度的1/3，不会出现不适配的问题
        toastStart.setGravity(Gravity.BOTTOM, 0, height / sum);
        toastStart.setDuration(Toast.LENGTH_LONG);
        toastStart.setView(toastRoot);
        toastStart.show();
    }


    public static void showToast(Context context,String str,int sum){
        //获取屏幕高度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        Toast toast = Toast.makeText(context,
                str, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0,height / sum );
        toast.show();
    }

}
