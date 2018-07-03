package com.tuzhao.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by juncoder on 2018/7/3.
 */
public class MainTimeUtil {

    private Timer mTimer;

    private long mInterval;

    private Handler mHandler;

    private TimeUtil.TimeCallback mTimeCallback;

    private static final int ON_TIME_IN = 0x678;

    public MainTimeUtil(long interval, TimeUtil.TimeCallback timeCallback) {
        mInterval = interval;
        mTimeCallback = timeCallback;
        mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == ON_TIME_IN) {
                    mTimeCallback.onTimeIn();
                }
                return true;
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
                message.what = ON_TIME_IN;
                mHandler.sendMessage(message);
            }
        }, 0, mInterval);
    }

    public void cancel() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mHandler.removeCallbacksAndMessages(null);
    }

}
