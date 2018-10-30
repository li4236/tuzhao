package com.tuzhao.activity.jiguang_notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tuzhao.activity.MainActivity;
import com.tuzhao.activity.mine.ShareActivity;
import com.tuzhao.application.MyApplication;
import com.tuzhao.info.User_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.IntentObserable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;

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

        if (bundle != null) {
            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.e(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                MyApplication.getInstance().getDatabaseImp().setRegistrationId(regId);
            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                // 自定义消息不会展示在通知栏
                Log.e("收到的自定义消息", bundle.getString(JPushInterface.EXTRA_EXTRA));
                try {
                    JSONObject jsonObject = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA, ""));
                    switch (jsonObject.optString("type")) {
                        case "ctrl":
                            notifyListeners(jsonObject);
                            break;
                        case "logout":
                            logout(jsonObject, context);
                            break;
                        case "order":
                            IntentObserable.dispatch(ConstansUtil.JI_GUANG_PARK_END, ConstansUtil.CITY_CODE, jsonObject.optString("cityCode"),
                                    ConstansUtil.PARK_ORDER_ID, jsonObject.optString("parkOrderId"),
                                    ConstansUtil.ORDER_FEE, jsonObject.optString("orderFee"), ConstansUtil.TIME, jsonObject.optString("time"));
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
    }

    /**
     * 处理退出登录消息
     */
    private void logout(JSONObject jsonObject, Context context) {
        String time = jsonObject.optString("time");
        if (UserManager.getInstance().hasLogined() && UserManager.getInstance().getUserInfo().getUsername().equals(jsonObject.optString("phone"))
                && DateUtil.getYearToSecondCalendar(time).compareTo(
                DateUtil.getYearToSecondCalendar(UserManager.getInstance().getLoginTime())) >= 0) {
            //如果登录之后别人在别的设备登录了则退出登录
            User_Info user_info = MyApplication.getInstance().getDatabaseImp().getUserFormDatabase();
            user_info.setAutologin("0");
            MyApplication.getInstance().getDatabaseImp().insertUserToDatabase(user_info);
            //清空缓存的登录信息
            UserManager.getInstance().setUserInfo(null);
            UserManager.getInstance().setHasLogin(false);

            Intent intent1 = new Intent(context, MainActivity.class);
            context.startActivity(intent1);

            Intent intent2 = new Intent(ConstansUtil.FORCE_LOGOUT);
            intent2.putExtra(ConstansUtil.REQUEST_FOR_RESULT, time);
            IntentObserable.dispatch(intent2);
        }
    }

    public static void addLockListener(String lockId, OnLockListener onLockListener) {
        sListenerHashMap.put(lockId, onLockListener);
    }

    public static void removeLockListener(String lockId) {
        if (sListenerHashMap.containsKey(lockId)) {
            sListenerHashMap.remove(lockId);
        }
    }

    private void notifyListeners(JSONObject jsonObject) {
        if (!sListenerHashMap.isEmpty()) {
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
                            case "open_failed_offline":
                                onLockListener.openFailed();
                                break;
                            case "close_successful":
                                onLockListener.closeSuccess();
                                break;
                            case "close_failed":
                            case "close_failed_offline":
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
        }
    }

}
