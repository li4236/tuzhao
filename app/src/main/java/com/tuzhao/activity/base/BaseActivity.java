package com.tuzhao.activity.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.tianzhili.www.myselfsdk.chenjing.XStatusBarHelper;
import com.tuzhao.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by 汪何龙 on 2017/4/8.
 *
 * @function: 所有Activity的基类，用来处理一些公共的事件
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //友盟SDK场景设置
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 禁用横屏
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    protected int setStyle(boolean style) {
        int statusBarHeight1 = -1;
        if (style) {
            XStatusBarHelper.tintStatusBar(this, ContextCompat.getColor(this, R.color.barcolor));
//            XStatusBarHelper.forceFitsSystemWindows(this);
//            XStatusBarHelper.immersiveStatusBar(this);
        } else {
            //获取status_bar_height资源的ID
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                //根据资源ID获取响应的尺寸值
                statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
            }
            XStatusBarHelper.forceFitsSystemWindows(this);
            XStatusBarHelper.immersiveStatusBar(this);
        }
        return statusBarHeight1;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}
