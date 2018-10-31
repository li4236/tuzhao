package com.tuzhao.publicwidget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tuzhao.R;
import com.tuzhao.info.ShareInfo;
import com.tuzhao.publicmanager.WeChatManager;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;

import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneSession;
import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneTimeline;

/**
 * Created by juncoder on 2018/10/31.
 * <p>
 * 分享对话框
 * </p>
 */
public class ShareDialog extends Dialog implements View.OnClickListener {

    private View mView;

    private ShareInfo mShareInfo;

    public ShareDialog(@NonNull Context context, ShareInfo shareInfo) {
        super(context, R.style.CustomDialogStyle);
        initView();
        setContentView(mView);
        setCanceledOnTouchOutside(true);
        mShareInfo = shareInfo;
        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.SlideAnimationStyle);
        }
    }

    private void initView() {
        mView = getLayoutInflater().inflate(R.layout.dialog_share_layout, null);
        setClickListener(R.id.share_to_wechat_iv);
        setClickListener(R.id.share_to_wechat_tv);
        setClickListener(R.id.share_to_frends_iv);
        setClickListener(R.id.share_to_frends_tv);
        setClickListener(R.id.share_to_wechat_mini_iv);
        setClickListener(R.id.share_to_wechat_mini_tv);
        setClickListener(R.id.cancel_tv);
    }

    private void setClickListener(@IdRes int id) {
        mView.findViewById(id).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_to_wechat_iv:
            case R.id.share_to_wechat_tv:
                sharetoWeChat(true);
                break;
            case R.id.share_to_frends_iv:
            case R.id.share_to_frends_tv:
                sharetoWeChat(false);
                break;
            case R.id.share_to_wechat_mini_iv:
            case R.id.share_to_wechat_mini_tv:
                shareToMini();
                break;
            case R.id.cancel_tv:
                dismiss();
                break;
        }
    }

    /**
     * @param isFrinds true:分享到好友   false:分享到朋友圈
     */
    private void sharetoWeChat(boolean isFrinds) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = mShareInfo.getWebpageUrl();
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = mShareInfo.getTitle();
        msg.description = mShareInfo.getDescription();
        Bitmap thumb = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_launcher);
        msg.thumbData = DensityUtil.bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction();
        req.message = msg;
        req.scene = isFrinds ? WXSceneSession : WXSceneTimeline;
        WeChatManager.getInstance().api.sendReq(req);
    }

    private String buildTransaction() {
        return "webpage" + System.currentTimeMillis();
    }

    /**
     * 分享到小程序
     */
    private void shareToMini() {
        WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
        miniProgramObj.webpageUrl = mShareInfo.getWebpageUrl(); // 兼容低版本的网页链接
        miniProgramObj.miniprogramType = WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE;// 正式版:0，测试版:1，体验版:2
        miniProgramObj.userName = ConstansUtil.WECHAT_MINI_ID;     // 小程序原始id
        miniProgramObj.path = mShareInfo.getPath();            //小程序页面路径
        WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
        msg.title = mShareInfo.getTitle();                    // 小程序消息title
        msg.description = mShareInfo.getDescription();               // 小程序消息desc

        Bitmap thumb = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_launcher);
        msg.thumbData = DensityUtil.bmpToByteArray(thumb, true);                      // 小程序消息封面图片，小于128k

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction();
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;  // 目前支持会话
        WeChatManager.getInstance().api.sendReq(req);
    }

}
