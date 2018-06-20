package com.tuzhao.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by juncoder on 2018/6/20.
 */
public class PollingUtil {

    private Handler mHandler;

    private Timer mTimer;

    private OnTimeCallback mOnTimeCallback;

    private long mPeriod;

    private static final int WHAT = 0x111;

    public PollingUtil(long period, OnTimeCallback onTimeCallback) {
        mPeriod = period;
        mOnTimeCallback = onTimeCallback;
        mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == WHAT) {
                    mOnTimeCallback.onTime();
                    return true;
                }
                return false;
            }
        });
    }

    public void start() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = mHandler.obtainMessage();
                message.what = WHAT;
                mHandler.sendMessage(message);
            }
        },0,mPeriod);
    }

    public void cancel() {
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    public interface OnTimeCallback {
        void onTime();
    }

}
