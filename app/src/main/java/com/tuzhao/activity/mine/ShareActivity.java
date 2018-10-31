package com.tuzhao.activity.mine;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.tianzhili.www.myselfsdk.chenjing.XStatusBarHelper;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.info.ShareInfo;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.dialog.ShareDialog;

/**
 * Created by TZL12 on 2018/2/1.
 */

public class ShareActivity extends BaseActivity {

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private boolean isAnimStart = false;
    private int currentProgress;

    private ShareDialog mShareDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_layout);
        initView();//初始化控件
        initEvent();//初始化事件
        XStatusBarHelper.tintStatusBar(this, ContextCompat.getColor(this, R.color.w0), 0);
    }

    private void initView() {
        mWebView = findViewById(R.id.id_activity_share_layout_webview);
        mProgressBar = findViewById(R.id.id_activity_share_layout_progressBar);

        WebSettings webSettings = mWebView.getSettings();

        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        mWebView.loadUrl("https://admin.toozhao.cn/h5app/share/inviter.html?device=Android&userName="
                + UserManager.getInstance().getUserInfo().getUsername());
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
                                              /*if (uri.getAuthority().equals("webview")) {
                                                  Log.e("网页点击", uri.getQueryParameter("pos"));
                                                  String pos = uri.getQueryParameter("pos");
                                                  if (pos.equals("1")) {
                                                      sharetoWeChat(true);
                                                  } else if (pos.equals("2")) {
                                                      sharetoWeChat(false);
                                                  } else if (pos.equals("3")) {
                                                      doShareToQQ();
                                                  } else {
                                                      sendMessageToWb(true, false);
                                                  }
                                                  return true;
                                              }*/
                                              if ("btn".equals(uri.getAuthority())) {
                                                  if (mShareDialog == null) {
                                                      ShareInfo shareInfo = new ShareInfo();
                                                      shareInfo.setWebpageUrl(uri.getQueryParameter("webpageUrl"));
                                                      shareInfo.setTitle(uri.getQueryParameter("title"));
                                                      shareInfo.setDescription(uri.getQueryParameter("description"));
                                                      shareInfo.setPath(uri.getQueryParameter("path"));
                                                      mShareDialog = new ShareDialog(ShareActivity.this, shareInfo);
                                                  }
                                                  mShareDialog.show();
                                                  return true;
                                              }
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

    @Override
    protected void onResume() {
        mWebView.onResume();
        mWebView.resumeTimers();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mWebView.onPause();
        mWebView.pauseTimers();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();

    }

}
