package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.tianzhili.www.myselfsdk.chenjing.XStatusBarHelper;
import com.tuzhao.R;

public class TextActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_layout);
        XStatusBarHelper.tintStatusBar(this, ContextCompat.getColor(this, R.color.w0),0);
        initView();
        initData();

    }



    /**
     * 初始化
     */
    private void initView() {


    }





    private void initData() {


    }

}
