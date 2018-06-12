package com.tuzhao.utils;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by juncoder on 2018/6/12.
 */
public class TimeUtil {

    private Timer mTimer;

    private long mInterval;

    private TimeCallback mTimeCallback;

    public TimeUtil(long mInterval, final TimeCallback timeCallback) {
        this.mInterval = mInterval;
        mTimeCallback = timeCallback;
    }

    public void start() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        schedule();
    }

    public void setInterval(long interval) {
        this.mInterval = interval;
        start();
    }

    private void schedule() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mTimeCallback.onTimeIn();
            }
        }, 10, mInterval);
    }

    public void cancel() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public interface TimeCallback {
        void onTimeIn();
    }

}
