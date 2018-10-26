package com.tuzhao.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.utils.ConstansUtil;

/**
 * Created by juncoder on 2018/10/24.
 */
public class WebActivity extends BaseStatusActivity {

    private int mType;

    private WebView mWebView;

    private ProgressBar mProgressBar;

    private int mLastProgress;

    private ObjectAnimator mStartObjectAnimator;

    private ObjectAnimator mDissmissObjectAnimator;

    @Override
    protected int resourceId() {
        return R.layout.activity_web_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mType = getIntent().getIntExtra(ConstansUtil.TYPE, 1);
        mWebView = findViewById(R.id.common_wv);
        mProgressBar = findViewById(R.id.webview_pb);
    }

    @Override
    protected void initData() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setAlpha(1.0f);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 100) {
                    startDismissAnimation();
                } else {
                    mLastProgress = mProgressBar.getProgress();
                    startProgressAnimation(newProgress);
                }
            }
        });

        if (mType == 1) {
            mWebView.loadUrl("https://admin.toozhao.cn/h5app/agreement.html");
        }
    }

    /**
     * progressBar递增动画
     */
    private void startProgressAnimation(int newProgress) {
        mStartObjectAnimator = ObjectAnimator.ofInt(mProgressBar, "progress", newProgress);
        mStartObjectAnimator.setDuration((newProgress - mLastProgress) * 10);
        mStartObjectAnimator.setInterpolator(new AccelerateInterpolator());
        mStartObjectAnimator.setAutoCancel(true);
        mStartObjectAnimator.start();
    }

    /**
     * progressBar消失动画
     */
    private void startDismissAnimation() {
        mDissmissObjectAnimator = ObjectAnimator.ofFloat(mProgressBar, "alpha", 1.0f, 0.0f);
        mDissmissObjectAnimator.setDuration(500);  // 动画时长
        mDissmissObjectAnimator.setInterpolator(new DecelerateInterpolator());     // 减速
        // 关键, 添加动画进度监听器
        mDissmissObjectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();      // 0.0f ~ 1.0f
                mProgressBar.setProgress((int) (100 * fraction));
            }
        });

        mDissmissObjectAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.GONE);
            }
        });
        mDissmissObjectAnimator.start();
    }

    @NonNull
    @Override
    protected String title() {
        if (mType == 1) {
            return "途找服务条款";
        }
        return "";
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mStartObjectAnimator != null) {
            mStartObjectAnimator.cancel();
        }
        if (mDissmissObjectAnimator != null) {
            mDissmissObjectAnimator.cancel();
        }
    }

}
