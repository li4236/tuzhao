package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
        ImageUtil.showPic((ImageView) findViewById(R.id.audit_iv), mCar.getStatus().equals("1") ? R.drawable.ic_audit3 : R.drawable.ic_auditfail);
        findViewById(R.id.cancel_apply_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelDialog();
            }
        });

        if (mCar.getStatus().equals("1")) {
            findViewById(R.id.contact_service).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewUtil.contactService(AuditCarActivity.this);
                }
            });
        } else {
            ((TextView) findViewById(R.id.audit_tv)).setText("审核未通过");
            ((TextView) findViewById(R.id.complete_audit_tv)).setText(mCar.getResaon());
            ((TextView) findViewById(R.id.cancel_apply_tv)).setText("重新申请");
            findViewById(R.id.contact_service).setVisibility(View.GONE);
            findViewById(R.id.cancel_apply_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(AddNewCarActivity.class, ConstansUtil.REQUSET_CODE, ConstansUtil.INTENT_MESSAGE, mCar);
                }
            });
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstansUtil.REQUSET_CODE && resultCode == RESULT_OK) {
            setResult(ConstansUtil.RESULT_CODE, data);
            finish();
        }
    }

}
