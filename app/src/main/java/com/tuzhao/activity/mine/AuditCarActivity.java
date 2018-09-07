package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Car;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.ViewUtil;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/9/7.
 */
public class AuditCarActivity extends BaseStatusActivity {

    private Car mCar;

    @Override
    protected int resourceId() {
        return R.layout.activity_audit_car_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if ((mCar = getIntent().getParcelableExtra(ConstansUtil.INTENT_MESSAGE)) == null) {
            showFiveToast("获取车辆信息失败，请稍后重试");
            finish();
        }
        Log.e(TAG, "initView: "+mCar.getCarNumber() );
        ImageUtil.showPic((ImageView) findViewById(R.id.audit_iv), R.drawable.ic_audit3);
        findViewById(R.id.cancel_apply_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelDialog();
            }
        });

        findViewById(R.id.contact_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.contactService(AuditCarActivity.this);
            }
        });
    }

    @Override
    protected void initData() {
    }

    @NonNull
    @Override
    protected String title() {
        return "审核进度";
    }

    private void showCancelDialog() {
        TipeDialog.Builder builder = new TipeDialog.Builder(this);
        builder.setMessage("确定取消申请添加该车辆吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                cancelAddCar();
            }
        });

        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        builder.create().show();
    }

    private void cancelAddCar() {
        showLoadingDialog("取消中...");
        getOkGo(HttpConstants.deleteUserCarNumber)
                .params("car_number", mCar.getCarNumber())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        dismmisLoadingDialog();
                        Intent intent = new Intent();
                        intent.putExtra(ConstansUtil.INTENT_MESSAGE, mCar);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {

                        }
                    }
                });
    }

}
