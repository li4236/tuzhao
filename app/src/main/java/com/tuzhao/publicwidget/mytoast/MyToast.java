package com.tuzhao.publicwidget.mytoast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;


/**
 * Created by TZL13 on 2017/5/17.
 */

public class MyToast {

    private static Toast sToast;

    private static int sWindowHeight;

    @SuppressLint("ShowToast")
    public static void showToast(Context context, String str, int sum) {
        if (sToast == null) {
            //获取屏幕高度
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (wm != null) {
                Point point = new Point();
                wm.getDefaultDisplay().getSize(point);
                sWindowHeight = point.y;
            }
            sToast = Toast.makeText(context.getApplicationContext(),
                    str, Toast.LENGTH_LONG);
        }
        sToast.setText(str);
        sToast.setGravity(Gravity.BOTTOM, 0, sWindowHeight / sum);
        sToast.show();
    }

}
