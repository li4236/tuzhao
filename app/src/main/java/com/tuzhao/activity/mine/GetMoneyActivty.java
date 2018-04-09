package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;

/**
 * Created by TZL12 on 2017/12/2.
 */

public class GetMoneyActivty extends BaseActivity implements View.OnClickListener {

    private LinearLayout linearlayout_getmoney, linearlayout_edit_alinumble;
    private EditText edittext_alinumble, edittext_money;
    private TextView textview_allbalance;
    private CustomDialog mCustomDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getmoney_layout);

        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件
        setStyle(true);
    }

    private void initView() {
        edittext_alinumble = (EditText) findViewById(R.id.id_activity_getmoney_layout_edittext_alinumble);
        edittext_money = (EditText) findViewById(R.id.id_activity_getmoney_layout_edittext_money);
        linearlayout_getmoney = (LinearLayout) findViewById(R.id.id_activity_getmoney_layout_linearlayout_getmoney);
        linearlayout_edit_alinumble = (LinearLayout) findViewById(R.id.id_activity_getmoney_layout_linearlayout_edit_alinumble);
        textview_allbalance = (TextView) findViewById(R.id.id_activity_getmoney_layout_textview_allbalance);
    }

    private void initData() {

        if (UserManager.getInstance().hasLogined()) {
            if (UserManager.getInstance().getUserInfo().getAlinumber().equals("-1")) {
                linearlayout_getmoney.setVisibility(View.GONE);
                linearlayout_edit_alinumble.setVisibility(View.VISIBLE);
            } else {
                linearlayout_getmoney.setVisibility(View.VISIBLE);
                linearlayout_edit_alinumble.setVisibility(View.GONE);
            }
        } else {
            finish();
        }
    }

    private void initEvent() {
        findViewById(R.id.id_activity_getmoney_imageview_back).setOnClickListener(this);
        findViewById(R.id.id_activity_getmoney_layout_textview_next).setOnClickListener(this);
        findViewById(R.id.id_activity_getmoney_layout_textview_tixian).setOnClickListener(this);
        findViewById(R.id.id_activity_getmoney_layout_textview_alinumble).setOnClickListener(this);
        findViewById(R.id.id_activity_getmoney_layout_textview_getallbalance).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_activity_getmoney_imageview_back:
                finish();
                break;
            case R.id.id_activity_getmoney_layout_textview_next:
                if (edittext_alinumble.getText().length() > 0) {
                    initLoading("加载中...");
                    UserManager.getInstance().getUserInfo().setAlinumber(edittext_alinumble.getText().toString());
                    initDataView();
                    linearlayout_getmoney.setVisibility(View.VISIBLE);
                    linearlayout_edit_alinumble.setVisibility(View.GONE);
                    if (mCustomDialog.isShowing()) {
                        mCustomDialog.dismiss();
                    }
                } else {
                    MyToast.showToast(GetMoneyActivty.this, "要先填写支付宝账号哦", 5);
                }
                break;
            case R.id.id_activity_getmoney_layout_textview_alinumble:
                TipeDialog.Builder builder = new TipeDialog.Builder(GetMoneyActivty.this);
                builder.setMessage("确定更换支付宝账号？");
                builder.setTitle("更换支付宝");
                builder.setPositiveButton("立即更换", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //去更换
                        edittext_alinumble.setText("");
                        linearlayout_getmoney.setVisibility(View.GONE);
                        linearlayout_edit_alinumble.setVisibility(View.VISIBLE);
                    }
                });

                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                builder.create().show();
                break;
            case R.id.id_activity_getmoney_layout_textview_getallbalance:
                edittext_money.setText(UserManager.getInstance().getUserInfo().getBalance());
                break;
            case R.id.id_activity_getmoney_layout_textview_tixian:
                try {
                    float balance = new Float(UserManager.getInstance().getUserInfo().getBalance()),
                          themoney = new Float(edittext_money.getText().toString());
                    if (balance>0){
                        if (themoney<=balance){

                        }else {
                            MyToast.showToast(GetMoneyActivty.this,"超出最大提现额度了哦",5);
                        }
                    }else {
                        MyToast.showToast(GetMoneyActivty.this,"余额不足，不可提现哦",5);
                    }
                } catch (Exception e) {
                }

                break;
        }
    }

    private void initDataView() {
        textview_allbalance.setText("可提余额 " + UserManager.getInstance().getUserInfo().getBalance());
    }

    //初始化加载框控件
    private void initLoading(String what) {
        mCustomDialog = new CustomDialog(GetMoneyActivty.this, what);
        mCustomDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCustomDialog != null) {
            mCustomDialog.cancel();
        }
    }

}
