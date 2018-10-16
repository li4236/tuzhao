package com.tuzhao.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by juncoder on 2018/6/20.
 * <p>
 * 定时器，OnTimeCallback在主线程回调
 * </p>
 */
public class PollingUtil {

    private Handler mHandler;

    private Timer mTimer;

    private long mPeriod;

    private boolean mIsRunning;

    private static final int WHAT = 0x111;

    public PollingUtil(long period, final OnTimeCallback onTimeCallback) {
        mPeriod = period;
        mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == WHAT) {
                    onTimeCallback.onTime();
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
        mIsRunning = true;
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = mHandler.obtainMessage();
                message.what = WHAT;
                mHandler.sendMessage(message);
            }
        }, 0, mPeriod);
    }

    public void cancel() {
        mIsRunning = false;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    public interface OnTimeCallback {
        void onTime();
    }

}
