package com.tuzhao.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.application.MyApplication;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @desc 启动屏
 * Created by devilwwj on 16/1/23.
 */
public class SplashActivity extends Activity {

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
        //两秒后转到正式主页
        textview_skip = (TextView) findViewById(R.id.id_activity_splash_layout_textview_skip);

        final Timer timer=new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {      // UI thread
                    @Override
                    public void run() {
                        recLen--;
                        textview_skip.setText("跳过："+recLen+"s");
                        if(recLen < 1){
                            timer.cancel();
                            enterHomeActivity();
                            textview_skip.setText("跳过：0s");
                        }
                    }
                });
            }
        };
        timer.schedule(task,0,1000);//此处0秒后，每隔1秒执行一次

        textview_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer!=null){
                    timer.cancel();
                    enterHomeActivity();
                }
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
