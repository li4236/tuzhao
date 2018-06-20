package com.tuzhao.activity.jiguang_notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tuzhao.activity.mine.ShareActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.data.JPushLocalNotification;

import static cn.jpush.android.api.JPushInterface.EXTRA_EXTRA;

/**
 * Created by TZL12 on 2017/10/25.
 */

public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = "MyReceiver";

    private NotificationManager nm;

    private static HashMap<String, OnLockListener> sListenerHashMap;

    static {
        sListenerHashMap = new HashMap<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + bundle);

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            // 自定义消息不会展示在通知栏
            Log.e("收到的自定义消息", bundle.getString(JPushInterface.EXTRA_EXTRA) + bundle.toString());
            notifyListeners(bundle.getString(JPushInterface.EXTRA_EXTRA) + bundle.toString());
//            processCustomMessage(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "收到了通知。消息内容是：" + bundle.getString(JPushInterface.EXTRA_EXTRA));

            // 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知" + bundle.getString(JPushInterface.EXTRA_EXTRA));
            // 在这里可以自己写代码去定义用户点击后的行为
            Intent i = new Intent(context, ShareActivity.class);  //自定义打开的界面
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    private void receivingNotification(Context context, Bundle bundle) {
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        Log.d(TAG, " title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        Log.d(TAG, "message : " + message);
        String extras = bundle.getString(EXTRA_EXTRA);
        Log.d(TAG, "extras : " + extras);
    }

    private void openNotification(Context context, Bundle bundle) {
        String extras = bundle.getString(EXTRA_EXTRA);
        String myValue = "";
        try {
            JSONObject extrasJson = new JSONObject(extras);
            myValue = extrasJson.optString("myKey");
        } catch (Exception e) {
            Log.w(TAG, "Unexpected: extras is not a valid json", e);
            return;
        }
//        if (TYPE_THIS.equals(myValue)) {
//            Intent mIntent = new Intent(context, ThisActivity.class);
//            mIntent.putExtras(bundle);
//            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(mIntent);
//        } else if (TYPE_ANOTHER.equals(myValue)){
//            Intent mIntent = new Intent(context, AnotherActivity.class);
//            mIntent.putExtras(bundle);
//            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(mIntent);
//        }
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {

        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(EXTRA_EXTRA);
        Intent msgIntent = new Intent("com.yitingchong.www.yitingchong.MESSAGE_RECEIVED_ACTION");
        msgIntent.putExtra("message", message);
        if (extras != null) {
            try {
                JSONObject extraJson = new JSONObject(extras);
                if (extraJson.length() > 0) {
                    msgIntent.putExtra("extras", extras);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JPushLocalNotification ln = new JPushLocalNotification();
        ln.setBuilderId(0);
        ln.setContent(message);
        ln.setTitle(message);
        ln.setNotificationId(11111111);
        ln.setBroadcastTime(System.currentTimeMillis() + 1000);
        JPushInterface.addLocalNotification(context, ln);
    }

    public static void addLockListener(String lockId, OnLockListener onLockListener) {
        sListenerHashMap.put(lockId, onLockListener);
    }

    public static void removeLockListener(String lockId) {
        if (sListenerHashMap.containsKey(lockId)) {
            sListenerHashMap.remove(lockId);
        }
    }

    private void notifyListeners(String message) {
        if (!sListenerHashMap.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(message);
                if (jsonObject.optString("type").equals("ctrl")) {
                    for (String key : sListenerHashMap.keySet()) {
                        if (jsonObject.optString("lock_id").equals(key)) {
                            OnLockListener onLockListener = sListenerHashMap.get(key);
                            switch (jsonObject.optString("msg")) {
                                case "open_successful":
                                    onLockListener.openSuccess();
                                    break;
                                case "open_successful_car":
                                    onLockListener.openSuccessHaveCar();
                                    break;
                                case "open_failed":
                                    onLockListener.openFailed();
                                    break;
                                case "close_successful":
                                    onLockListener.closeSuccess();
                                    break;
                                case "close_failed":
                                    onLockListener.closeFailed();
                                    break;
                                case "close_failed_car":
                                    onLockListener.closeFailedHaveCar();
                                    break;
                            }
                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
