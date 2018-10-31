package com.tuzhao.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.publicmanager.WeChatManager;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.IntentObserable;

/**
 * Created by juncoder on 2018/6/11.
 */
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry_layout);
        WeChatManager.getInstance().api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        WeChatManager.getInstance().api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            Log.e(TAG, "onResp: " + baseResp.errCode);
            Intent intent = new Intent();
            switch (baseResp.errCode) {
                case 0:
                    intent.setAction(ConstansUtil.PAY_SUCCESS);
                    break;
                case -1:
                    intent.setAction(ConstansUtil.PAY_ERROR);
                    intent.putExtra(ConstansUtil.INTENT_MESSAGE, baseResp.errStr);
                    break;
                case -2:
                    intent.setAction(ConstansUtil.PAY_CANCEL);
                    break;
            }
            IntentObserable.dispatch(intent);
            finish();
        }
    }

}
