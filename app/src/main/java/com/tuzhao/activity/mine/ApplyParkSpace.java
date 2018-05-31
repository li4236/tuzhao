package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TZL12 on 2017/9/9.
 */

public class ApplyParkSpace extends BaseActivity {

    private ImageView imageview_backbotton;
    private TextView textview_savebotton;
    private EditText parkspace_name,parkspace_address;
    private String mCityCode = "010";
    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applyspace);

        initView();
        initData();
        initEvent();
        setStyle(true);
    }

    private void initView() {

        imageview_backbotton = (ImageView) findViewById(R.id.id_activity_applyspace_layout_imageview_backbotton);
        textview_savebotton = (TextView) findViewById(R.id.id_activity_applyspace_layout_textview_savebotton);
        parkspace_name = (EditText) findViewById(R.id.id_activity_applyspace_layout_edittext_parkspace_name);
        parkspace_address = (EditText) findViewById(R.id.id_activity_applyspace_layout_edittext_parkspace_address);
    }

    private void initData() {

        if (getIntent().hasExtra("citycode")){
            mCityCode = getIntent().getStringExtra("citycode");
        }
    }

    private void initEvent() {

        imageview_backbotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        textview_savebotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (parkspace_name.getText().length()>0 && parkspace_address.getText().length()>0){

                    initLoading("请求中...");
                    requestApplyParkSpace();
                }else {
                    MyToast.showToast(ApplyParkSpace.this,"信息要填写完整哦",5);
                }
            }
        });
    }

    private void requestApplyParkSpace() {

        OkGo.post(HttpConstants.applyParkSpace)
                .tag(HttpConstants.applyParkSpace)
                .params("user_id", UserManager.getInstance().getUserInfo().getId())
                .params("parkspace_name",parkspace_name.getText().toString())
                .params("parkspace_address",parkspace_address.getText().toString())
                .params("city_code",mCityCode)
                .execute(new JsonCallback<Base_Class_Info<Park_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Park_Info> park_infoJsonCallback, Call call, Response response) {

                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.hide();
                        }
                        MyToast.showToast(ApplyParkSpace.this,"已成功申请"+parkspace_name.getText().toString(),5);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.hide();
                        }
                        if (e instanceof ConnectException) {
                            Log.d("TAG", "请求失败，" + " 信息为：连接异常" + e.toString());
                        } else if (e instanceof SocketTimeoutException) {
                            Log.d("TAG", "请求失败，" + " 信息为：超时异常" + e.toString());
                        } else if (e instanceof NoRouteToHostException) {
                            Log.d("TAG", "请求失败，" + " 信息为：没有路由到主机" + e.toString());
                        } else {
                            Log.d("TAG", "请求失败， 信息为："+ e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 102:
                                    MyToast.showToast(ApplyParkSpace.this,"申请失败", 2);
                                    break;
                                case 103:
                                    MyToast.showToast(ApplyParkSpace.this,"申请失败", 2);
                                    break;
                                case 104:
                                    MyToast.showToast(ApplyParkSpace.this,"申请失败", 2);
                                    break;
                                case 105:
                                    MyToast.showToast(ApplyParkSpace.this,"申请失败", 2);
                                    break;
                                case 901:
                                    MyToast.showToast(ApplyParkSpace.this,"服务器正在维护中", 2);
                                    break;
                                default:
                                    MyToast.showToast(ApplyParkSpace.this,"服务器繁忙，稍后再试", 2);
                                    break;
                            }
                        }
                    }
                });
    }

    private void initLoading(String what) {
        mLoadingDialog = new LoadingDialog(this, what);
        mLoadingDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadingDialog !=null){
            mLoadingDialog.cancel();
        }
    }
}
