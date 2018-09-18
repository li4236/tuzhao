package com.tuzhao.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.chenjing.XStatusBarHelper;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.application.MyApplication;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.DeviceUtils;
import com.tuzhao.utils.ImageUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @desc 启动屏
 * Created by devilwwj on 16/1/23.
 */
public class SplashActivity extends BaseActivity {

    private TextView textview_skip;
    private int recLen = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 判断是否是第一次开启应用
        SharedPreferences setPreferences = this.getSharedPreferences("tuzhaoapp", Context.MODE_PRIVATE);
        boolean isFirstOpen = setPreferences.getBoolean("first_open", false);
        // 如果是第一次启动，则先进入功能引导页
        if (!isFirstOpen) {
            Intent intent = new Intent(this, WelcomeGuideActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // 如果不是第一次启动app，判断是否要显示启动屏
        if (MyApplication.SKIP_WELCOME) {
            enterHomeActivity();
            return;
        }

        setContentView(R.layout.activity_splash_layout);

        XStatusBarHelper.fullScreen(this);

        //两秒后转到正式主页
        textview_skip = findViewById(R.id.id_activity_splash_layout_textview_skip);
        ImageUtil.showPic((ImageView) findViewById(R.id.iv_splash), R.drawable.ic_splash);

        DeviceUtils.adpterNotchHeight(this, new SuccessCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) textview_skip.getLayoutParams();
                layoutParams.topMargin = integer + DensityUtil.dp2px(SplashActivity.this, 10);
                textview_skip.setLayoutParams(layoutParams);
            }
        });

        final Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {      // UI thread
                    @Override
                    public void run() {
                        recLen--;
                        textview_skip.setText("跳过：" + recLen + "s");
                        if (recLen < 1) {
                            timer.cancel();
                            enterHomeActivity();
                            textview_skip.setText("跳过：0s");
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 1000);//此处0秒后，每隔1秒执行一次

        textview_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                enterHomeActivity();
            }
        });

        // 如果刚打开过，下一次启动就跳过欢迎界面；
        MyApplication.SKIP_WELCOME = true;
    }

    private void enterHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
