package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.IntentObserable;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/10/23.
 */
public class FreeParkActivity extends BaseStatusActivity implements View.OnClickListener {

    private EditText mExperienceCode;

    private TextView mFreeParkHint;

    private TextView mBindParkLotName;

    private TextView mUnbindFreeParkLot;

    private User_Info mUserInfo;

    @Override
    protected int resourceId() {
        return R.layout.activity_free_park_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mExperienceCode = findViewById(R.id.experience_code_et);
        mFreeParkHint = findViewById(R.id.free_park_hint);
        mBindParkLotName = findViewById(R.id.bind_park_lot_name);
        mUnbindFreeParkLot = findViewById(R.id.unbind_free_park_lot);

        findViewById(R.id.binding_free_park).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mFreeParkHint.setText("1、作为受邀用户，可在所绑定车场内免费停车\n2、一个账号暂时只能绑定一个车场");
        mUserInfo = UserManager.getInstance().getUserInfo();
        if (!"-1".equals(mUserInfo.getParkLotName())) {
            showBindInfo();
        }
    }

    @NonNull
    @Override
    protected String title() {
        return "免费停车";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.binding_free_park:
                if (!"-1".equals(mUserInfo.getParkLotName())) {
                    showFiveToast("最多只能绑定一个免费车场哦");
                } else if (isEmpty(mExperienceCode)) {
                    showFiveToast("请输入车场体验码");
                } else {
                    bindFreeParkLot();
                }
                break;
            case R.id.unbind_free_park_lot:
                unbindFreeParkLot();
                break;
        }
    }

    private void showBindInfo() {
        showView(mBindParkLotName);
        showView(mUnbindFreeParkLot);
        mUnbindFreeParkLot.setOnClickListener(this);
        mBindParkLotName.setText(mUserInfo.getParkLotName());
    }

    private void bindFreeParkLot() {
        showLoadingDialog("绑定中...");
        getOkGo(HttpConstants.bindFreeParkLot)
                .params("experienceCode", getText(mExperienceCode))
                .execute(new JsonCallback<Base_Class_Info<User_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<User_Info> o, Call call, Response response) {
                        mUserInfo.setParkLotName(o.data.getParkLotName());
                        showBindInfo();
                        IntentObserable.dispatch(ConstansUtil.CHANGE_BIND_FREE_PARK);
                        dismmisLoadingDialog();
                        mExperienceCode.setText("");
                        showFiveToast("绑定成功!");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    showFiveToast("体验码错误，请重新输入");
                                    break;
                                case "102":
                                    showFiveToast("体验码无效");
                                    break;
                                case "103":
                                    showFiveToast("最多只能绑定一个车场哦");
                                    break;
                                default:
                                    showFiveToast(ConstansUtil.SERVER_ERROR);
                                    break;
                            }
                        }
                    }
                });
    }

    private void unbindFreeParkLot() {
        showFiveToast("解绑中...");
        getOkGo(HttpConstants.unbindFreeParkLot)
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        mUserInfo.setParkLotName("-1");
                        goneView(mBindParkLotName);
                        goneView(mUnbindFreeParkLot);
                        IntentObserable.dispatch(ConstansUtil.CHANGE_BIND_FREE_PARK);
                        dismmisLoadingDialog();
                        showFiveToast("解除绑定成功!");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            if ("101".equals(e.getMessage())) {
                                mUserInfo.setParkLotName("-1");
                                goneView(mBindParkLotName);
                                goneView(mUnbindFreeParkLot);
                                IntentObserable.dispatch(ConstansUtil.CHANGE_BIND_FREE_PARK);
                                showFiveToast("解除绑定成功!");
                            } else {
                                showFiveToast(ConstansUtil.SERVER_ERROR);
                            }
                        }
                    }
                });
    }

}
