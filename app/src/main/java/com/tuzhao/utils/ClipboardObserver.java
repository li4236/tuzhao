package com.tuzhao.utils;

import android.content.ClipboardManager;
import android.content.Context;

import com.tuzhao.activity.base.SuccessCallback;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by juncoder on 2018/8/7.
 * <p>
 * 监听复制事件
 * </p>
 */
public class ClipboardObserver {

    private Context mContext;

    private SuccessCallback<String> mCallback;

    private ClipboardManager mClipboardManager;

    private ClipboardManager.OnPrimaryClipChangedListener mClipChangedListener;

    public ClipboardObserver(Context context, SuccessCallback<String> callback) {
        mContext = context;
        mCallback = callback;
    }

    public void registerClipEvents() {
        mClipboardManager = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
        if (mClipboardManager != null) {
            mClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    if (mClipboardManager.hasPrimaryClip() && mClipboardManager.getPrimaryClip().getItemCount() > 0) {
                        String clipData = (String) mClipboardManager.getPrimaryClip().getItemAt(0).getText();
                        if (clipData.length() == 4) {
                            for (int i = 0; i < clipData.length(); i++) {
                                if (!Character.isDigit(clipData.charAt(i))) {
                                    //判断复制的是否是纯数字
                                    clipData = null;
                                    break;
                                }
                            }
                        } else {
                            clipData = null;
                        }

                        //短信来了直接在弹窗就复制了的，则直接输入
                        if (clipData != null && mCallback != null) {
                            mCallback.onSuccess(clipData);
                        }
                    }
                }
            };
            mClipboardManager.addPrimaryClipChangedListener(mClipChangedListener);
        }
    }

    public void unregisterClipEvents() {
        if (mClipboardManager != null) {
            mClipboardManager.removePrimaryClipChangedListener(mClipChangedListener);
        }
    }

}
