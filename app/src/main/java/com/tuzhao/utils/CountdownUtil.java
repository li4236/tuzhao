package com.tuzhao.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by juncoder on 2018/7/23.
 */
public class CountdownUtil {

    private Handler mHandler;

    private Timer mTimer;

    private int mCountdown;

    private int mCurrentCountdown;

    private static final int WHAT = 0x111;

    public CountdownUtil(int countdown, final OnTimeCallback callback) {
        mCountdown = countdown;
        mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == WHAT) {
                    int time = msg.arg1;
                    if (time <= 0) {
                        callback.onTimeEnd();
                    } else {
                        callback.onTime(time);
                    }
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
        mCurrentCountdown = mCountdown;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = mHandler.obtainMessage();
                message.what = WHAT;
                message.arg1 = mCurrentCountdown--;
                mHandler.sendMessage(message);
                if (mCurrentCountdown < 0) {
                    mTimer.cancel();
                }
            }
        }, 0, 1000);
    }

    public void cancel() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    public interface OnTimeCallback {

        void onTime(int time);

        void onTimeEnd();

    }

}
