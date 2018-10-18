package com.tuzhao.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.IntentObserable;

/**
 * Created by TZL12 on 2018/3/14.
 */

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private static final String TAG = "WXEntryActivity";

    private static final int LOGIN = 1;

    private static final int SHARE = 2;

    private IWXAPI mIWXAPI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry_layout);
        mIWXAPI = WXAPIFactory.createWXAPI(this, ConstansUtil.WECHAT_APP_ID);
        mIWXAPI.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mIWXAPI.handleIntent(intent, this);
    }

    @Override
    public void onResp(BaseResp resp) { //在这个方法中处理微信传回的数据
//        Intent intent = new Intent(this, ShareActivity.class);
//        intent.putExtra("errCode", resp.errCode);
//        intent.putExtra("isWeChat", 1);
//        startActivity(intent);
//        finish();

        //形参resp 有下面两个个属性比较重要
        //1.resp.errCode
        //2.resp.transaction则是在分享数据的时候手动指定的字符创,用来分辨是那次分享(参照4.中req.transaction)
        switch (resp.errCode) { //根据需要的情况进行处理
            case BaseResp.ErrCode.ERR_OK:
                //正确返回
                if (resp.getType() == LOGIN) {
                    Intent intent = new Intent(ConstansUtil.WECHAT_CODE);
                    intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, ((SendAuth.Resp) resp).code);
                    IntentObserable.dispatch(intent);
                } else {
                    MyToast.showToast(WXEntryActivity.this, "分享成功", 5);
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (resp.getType() == LOGIN) {
                    MyToast.showToast(WXEntryActivity.this, "取消登录", 5);
                } else {
                    MyToast.showToast(WXEntryActivity.this, "分享已取消", 5);
                }
                //用户取消
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                MyToast.showToast(WXEntryActivity.this, "分享认证被否决", 5);
                //认证被否决
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                MyToast.showToast(WXEntryActivity.this, "分享发送失败", 5);
                //发送失败
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                MyToast.showToast(WXEntryActivity.this, "分享不支持错误", 5);
                //不支持错误
                break;
            case BaseResp.ErrCode.ERR_COMM:
                MyToast.showToast(WXEntryActivity.this, "分享一般错误", 5);
                //一般错误
                break;
            default:
                MyToast.showToast(WXEntryActivity.this, "分享其他不可名状的情况", 5);
                //其他不可名状的情况
                break;
        }
        finish();
    }

    @Override
    public void onReq(BaseReq req) {

        //......这里是用来处理接收的请求,暂不做讨论
    }

}
