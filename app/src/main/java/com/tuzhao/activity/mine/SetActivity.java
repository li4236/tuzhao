package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.update.UpdateHelper;
import com.tianzhili.www.myselfsdk.update.listener.UpdateListener;
import com.tianzhili.www.myselfsdk.update.type.UpdateType;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.info.User_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.db.DatabaseImp;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DateUtil;

import static com.tuzhao.publicwidget.dialog.LoginDialogFragment.LOGOUT_ACTION;

/**
 * Created by TZL12 on 2017/12/14.
 */

public class SetActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout linearlayout_safe, linearlayout_suggest, linearlayout_law, linearlayout_checkversion, linearlayout_loginout;

    private DateUtil dateUtil = new DateUtil();
    private DatabaseImp databaseImp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_layout);
        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件
        setStyle(true);
    }

    private void initView() {

        ((TextView) findViewById(R.id.id_activity_set_layout_textview_thisversion)).setText("当前版本：" + dateUtil.getVersion(this));
        linearlayout_safe = (LinearLayout) findViewById(R.id.id_activity_set_layout_linearlayout_safe);
        linearlayout_suggest = (LinearLayout) findViewById(R.id.id_activity_set_layout_linearlayout_suggest);
        linearlayout_law = (LinearLayout) findViewById(R.id.id_activity_set_layout_linearlayout_law);
        linearlayout_checkversion = (LinearLayout) findViewById(R.id.id_activity_set_layout_linearlayout_checkversion);
        linearlayout_loginout = (LinearLayout) findViewById(R.id.id_activity_set_layout_linearlayout_loginout);
    }

    private void initData() {
    }

    private void initEvent() {
        findViewById(R.id.id_activity_set_imageview_back).setOnClickListener(this);
        linearlayout_safe.setOnClickListener(this);
        linearlayout_suggest.setOnClickListener(this);
        linearlayout_law.setOnClickListener(this);
        linearlayout_checkversion.setOnClickListener(this);
        linearlayout_loginout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.id_activity_set_imageview_back:
                finish();
                break;
            case R.id.id_activity_set_layout_linearlayout_safe:
                intent = new Intent(SetActivity.this, SetAccountAndSafeActivity.class);
                startActivity(intent);
                break;
            case R.id.id_activity_set_layout_linearlayout_suggest:
                intent = new Intent(SetActivity.this, SuggestActivity.class);
                startActivity(intent);
                break;
//            case R.id.id_activity_set_layout_linearlayout_law:
//                intent = new Intent(SetActivity.this, );
//                startActivity(intent);
//                break;
            case R.id.id_activity_set_layout_linearlayout_checkversion:
                //检查版本更新
                UpdateHelper.getInstance()
                        .setUpdateType(UpdateType.checkupdate)
                        .setUpdateListener(new UpdateListener() {
                            @Override
                            public void noUpdate() {
                                MyToast.showToast(SetActivity.this,"已经是最新版本了哦",5);
                            }

                            @Override
                            public void onCheckError(int code, String errorMsg) {
                                MyToast.showToast(SetActivity.this,"检测更新失败",5);
                            }
                        })
                        .check(SetActivity.this);
                break;
            case R.id.id_activity_set_layout_linearlayout_loginout:
                TipeDialog.Builder builder = new TipeDialog.Builder(SetActivity.this);
                builder.setMessage("确定退出吗？");
                builder.setTitle("提示");
                builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //退出登录，设置不能再自动登录
                                databaseImp = new DatabaseImp(SetActivity.this);
                                User_Info user_info = databaseImp.getUserFormDatabase();
                                user_info.setAutologin("0");
                                databaseImp.insertUserToDatabase(user_info);
                                //清空缓存的登录信息
                                UserManager.getInstance().setUserInfo(null);
                                //发送退出登录的广播
                                sendLogoutBroadcast();
                                MyToast.showToast(SetActivity.this, "已退出登录", 5);
                                finish();
                            }
                        });
                builder.create().show();
                break;
        }
    }

    /**
     * 发送退出登录的局部广播
     */
    private void sendLogoutBroadcast() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(LOGOUT_ACTION));
    }
}
