package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.chenjing.XStatusBarHelper;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.info.User_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.db.DatabaseImp;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.publicwidget.update.UpdateService;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.util.Objects;

import static com.tuzhao.publicwidget.dialog.LoginDialogFragment.LOGOUT_ACTION;

/**
 * Created by TZL12 on 2017/12/14.
 */

public class SetActivity extends BaseActivity implements View.OnClickListener, IntentObserver {

    private LinearLayout linearlayout_suggest, linearlayout_law, linearlayout_checkversion, linearlayout_loginout;

    private DateUtil dateUtil = new DateUtil();
    private DatabaseImp databaseImp;

    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_layout);
        initView();//初始化控件
        initEvent();//初始化事件
        XStatusBarHelper.tintStatusBar(this, ContextCompat.getColor(this, R.color.w0),0);
    }

    private void initView() {
        ((TextView) findViewById(R.id.id_activity_set_layout_textview_thisversion)).setText("当前版本：" + dateUtil.getVersion(this));
        linearlayout_suggest = findViewById(R.id.id_activity_set_layout_linearlayout_suggest);
        linearlayout_law = findViewById(R.id.id_activity_set_layout_linearlayout_law);
        linearlayout_checkversion = findViewById(R.id.id_activity_set_layout_linearlayout_checkversion);
        linearlayout_loginout = findViewById(R.id.id_activity_set_layout_linearlayout_loginout);
    }

    private void initEvent() {
        findViewById(R.id.id_activity_set_imageview_back).setOnClickListener(this);
        linearlayout_suggest.setOnClickListener(this);
        linearlayout_law.setOnClickListener(this);
        linearlayout_checkversion.setOnClickListener(this);
        linearlayout_loginout.setOnClickListener(this);

        IntentObserable.registerObserver(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.id_activity_set_imageview_back:
                finish();
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
                intent = new Intent(SetActivity.this, UpdateService.class);
                intent.putExtra(ConstansUtil.INTENT_MESSAGE, true);
                startService(intent);
                showLoadingDialg();
                break;
            case R.id.id_activity_set_layout_linearlayout_loginout:
                TipeDialog.Builder builder = new TipeDialog.Builder(SetActivity.this);
                builder.setMessage("确定退出吗？");
                builder.setTitle("提示");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //退出登录，设置不能再自动登录
                        databaseImp = new DatabaseImp(SetActivity.this);
                        User_Info user_info = databaseImp.getUserFormDatabase();
                        user_info.setAutologin("0");
                        databaseImp.insertUserToDatabase(user_info);
                        //清空缓存的登录信息
                        UserManager.getInstance().setUserInfo(null);
                        UserManager.getInstance().setHasLogin(false);
                        //发送退出登录的广播
                        sendLogoutBroadcast();
                        MyToast.showToast(SetActivity.this, "已退出登录", 5);
                        finish();
                    }
                });
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.create().show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
    }

    /**
     * 发送退出登录的局部广播
     */
    private void sendLogoutBroadcast() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(LOGOUT_ACTION));
    }

    private void showLoadingDialg() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this, "检查中...");
        }
        mLoadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void onReceive(Intent intent) {
        if (Objects.equals(intent.getAction(), ConstansUtil.DIALOG_DISMISS)) {
            dismissLoadingDialog();
        }
    }

}
