package com.tuzhao.activity.mine;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.application.MyApplication;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicmanager.WeChatManager;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.publicwidget.share.ThreadManager;
import com.tuzhao.publicwidget.share.WebConstants;
import com.tuzhao.utils.DensityUtil;

import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneSession;
import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneTimeline;

/**
 * Created by TZL12 on 2018/2/1.
 */

public class ShareActivity extends BaseActivity implements WbShareCallback {

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private boolean isAnimStart = false;
    private int currentProgress;

    private IUiListener qqShareListener = new IUiListener() {
        //QQ分享回调
        @Override
        public void onCancel() {
            MyToast.showToast(ShareActivity.this,"分享已取消",5);
        }

        @Override
        public void onComplete(Object response) {
            MyToast.showToast(ShareActivity.this,"分享成功",5);
        }

        @Override
        public void onError(UiError e) {
            MyToast.showToast(ShareActivity.this,"分享失败",5);
        }
    };

    private WbShareHandler shareHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_layout);
        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件
        setStyle(true);
    }

    private void initView() {
        mWebView = (WebView) findViewById(R.id.id_activity_share_layout_webview);
        mProgressBar = (ProgressBar) findViewById(R.id.id_activity_share_layout_progressBar);

        WebSettings webSettings = mWebView.getSettings();

        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        // 步骤1：加载JS代码
        // 格式规定为:file:///android_asset/文件名.html
        mWebView.loadUrl("https://admin.toozhao.cn/h5app/share/share.html");
        // 拦截url
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setAlpha(1.0f);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;   // 在当前webview内部打开url
            }
        });
        // 获取网页加载进度
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                currentProgress = mProgressBar.getProgress();
                if (newProgress >= 100 && !isAnimStart) {
                    // 防止调用多次动画
                    isAnimStart = true;
                    mProgressBar.setProgress(newProgress);
                    // 开启属性动画让进度条平滑消失
                    startDismissAnimation(mProgressBar.getProgress());
                } else {
                    // 开启属性动画让进度条平滑递增
                    startProgressAnimation(newProgress);
                }
            }
        });

        //新浪微博初始化
        WbSdk.install(this,new AuthInfo(this, WebConstants.APP_KEY, WebConstants.REDIRECT_URL, WebConstants.SCOPE));//创建微博API接口类对象
        shareHandler = new WbShareHandler(this);
        shareHandler.registerApp();
    }

    private void initData() {
    }

    private void initEvent() {
        // 复写WebViewClient类的shouldOverrideUrlLoading方法
        mWebView.setWebViewClient(new WebViewClient() {
                                      @Override
                                      public boolean shouldOverrideUrlLoading(WebView view, String url) {

                                          Uri uri = Uri.parse(url);
                                          if (uri.getScheme().equals("js")) {

                                              // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                                              // 所以拦截url,下面JS开始调用Android需要的方法
                                              if (uri.getAuthority().equals("webview")) {
                                                  Log.e("网页点击", uri.getQueryParameter("pos"));
                                                  String pos = uri.getQueryParameter("pos");
                                                  if (pos.equals("1")){
                                                      sharetoWeChat(true);
                                                  }
                                                  else if (pos.equals("2")) {
                                                      sharetoWeChat(false);
                                                  }
                                                  else if (pos.equals("3")) {
                                                      doShareToQQ();
                                                  }else {
                                                      sendMessageToWb(true,false);
                                                  }
                                              }
                                              return true;
                                          }
                                          return super.shouldOverrideUrlLoading(view, url);
                                      }
                                  }
        );

        findViewById(R.id.id_activity_share_imageview_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void doShareToQQ() {
        final Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "送你10元停车券，快来一起停车吧");//标题
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "邀请好友，你和好友各拿10元停车立减券哦");//内容
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "https://admin.toozhao.cn/h5app/share/getshare.html" + "?key=" + UserManager.getInstance().getUserInfo().getUsername() + "&pos=3");//链接
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://api.toozhao.cn/public/uploads/logo.png");//配图
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, getString(R.string.app_name));//手Q显示返回app名称
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0x00);
        ThreadManager.getMainHandler().post(new Runnable() {

            @Override
            public void run() {
                if (null != MyApplication.getInstance().getmTencent()) {
                    MyApplication.getInstance().getmTencent().shareToQQ(ShareActivity.this, params, qqShareListener);
                }
            }
        });
    }

    /**
     * progressBar递增动画
     */
    private void startProgressAnimation(int newProgress) {
        ObjectAnimator animator = ObjectAnimator.ofInt(mProgressBar, "progress", currentProgress, newProgress);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    /**
     * progressBar消失动画
     */
    private void startDismissAnimation(final int progress) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(mProgressBar, "alpha", 1.0f, 0.0f);
        anim.setDuration(1500);  // 动画时长
        anim.setInterpolator(new DecelerateInterpolator());     // 减速
        // 关键, 添加动画进度监听器
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();      // 0.0f ~ 1.0f
                int offset = 100 - progress;
                mProgressBar.setProgress((int) (progress + offset * fraction));
            }
        });

        anim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.GONE);
                isAnimStart = false;
            }
        });
        anim.start();
    }

    /**
     * 监听back键
     * 在WebView中回退导航
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {  // 返回键的KEYCODE
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;  // 拦截
            } else {
                return super.onKeyDown(keyCode, event);   //  放行
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 官方文档没这句代码, 但是很很很重要, 不然不会回调!
        Tencent.onActivityResultData(requestCode, resultCode, data, qqShareListener);

        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_QQ_SHARE ||
                    resultCode == Constants.REQUEST_QZONE_SHARE ||
                    resultCode == Constants.REQUEST_OLD_SHARE) {
                Tencent.handleResultData(data, qqShareListener);
            }
        }
    }

    /**
     * 新浪微博或者微信的回调
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        shareHandler.doResultIntent(intent,this);
    }

    //新浪微博的回调
    @Override
    public void onWbShareSuccess() {
        MyToast.showToast(ShareActivity.this,"分享成功",5);
    }

    @Override
    public void onWbShareCancel() {
        MyToast.showToast(ShareActivity.this,"分享已取消",5);
    }

    @Override
    public void onWbShareFail() {
        MyToast.showToast(ShareActivity.this,"分享失败",5);
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     */
    private void sendMessageToWb(boolean hasText, boolean hasImage) {
        sendMultiMessage(hasText, hasImage);
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     */
    private void sendMultiMessage(boolean hasText, boolean hasImage) {

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (hasText) {
            weiboMessage.textObject = getTextObj();
        }
        if (hasImage) {
            weiboMessage.imageObject = getImageObj(this);
        }
        shareHandler.shareMessage(weiboMessage, false);
    }

    /**
     * 创建文本消息对象。
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = "#途找停车#\n送你10元停车券，快来一起停车吧"+"\nhttps://admin.toozhao.cn/h5app/share/getshare.html" + "?key=" + UserManager.getInstance().getUserInfo().getUsername() + "&pos=4";
        textObject.title = "邀请好友，你和好友各拿10元停车立减券哦";
        textObject.actionUrl = "https://admin.toozhao.cn/h5app/share/getshare.html" + "?key=" + UserManager.getInstance().getUserInfo().getUsername();
        return textObject;
    }

    /**
     * 创建图片消息对象。
     * @return 图片消息对象。
     */
    private ImageObject getImageObj(Context context) {
        ImageObject imageObject = new ImageObject();
        Bitmap  bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo);
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    public void sharetoWeChat(boolean isFrinds) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "https://admin.toozhao.cn/h5app/share/getshare.html" + "?key=" + UserManager.getInstance().getUserInfo().getUsername() + "&pos="+(isFrinds?"1":"2");
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "送你10元停车券，快来一起停车吧";
        msg.description = "邀请好友，你和好友各拿10元停车立减券哦";
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        msg.thumbData = DensityUtil.bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = isFrinds?WXSceneSession:WXSceneTimeline;
        WeChatManager.getInstance().api.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
