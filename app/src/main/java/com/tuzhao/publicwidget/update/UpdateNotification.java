package com.tuzhao.publicwidget.update;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.tuzhao.R;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.utils.DeviceUtils;

import java.lang.ref.WeakReference;

/**
 * Created by juncoder on 2018/8/27.
 */
public class UpdateNotification {

    private static final String TAG = "UpdateNotification";

    private WeakReference<Activity> mWeakReference;

    private NotificationManager mNotificationManager;

    UpdateNotification(Activity activity) {
        mWeakReference = new WeakReference<>(activity);
        mNotificationManager = (NotificationManager) mWeakReference.get().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (mNotificationManager != null) {
                NotificationChannel notificationChannel = new NotificationChannel("621", "版本更新", NotificationManager.IMPORTANCE_LOW);
                notificationChannel.setSound(null, null);
                notificationChannel.enableVibration(false);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    public void performShowNotification(int progress, String versionName) {
        if (!NotificationManagerCompat.from(mWeakReference.get()).areNotificationsEnabled()) {
            String message;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                message = "为了在通知栏上显示下载进度，请开启通知并打开版本更新";
            } else {
                message = "为了在通知栏上显示下载进度，请开启通知";
            }
            new TipeDialog.Builder(mWeakReference.get())
                    .setCancelable(false)
                    .setTitle("权限申请")
                    .setMessage(message)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DeviceUtils.openNotification(mWeakReference.get());
                        }
                    })
                    .create()
                    .show();
        } else {
            showNotification(progress, versionName);
        }
    }

    public void showNotification(int progress, String versionName) {
        if (mNotificationManager != null) {
            Notification notification = new NotificationCompat.Builder(mWeakReference.get(), "621")
                    .setContentTitle("更新到" + versionName + "版本")
                    .setContentText("已下载" + progress + "/" + 100)
                    .setWhen(System.currentTimeMillis())
                    .setProgress(100, progress, false)
                    .setSmallIcon(R.drawable.logo)
                    .setOnlyAlertOnce(true)
                    .setLargeIcon(BitmapFactory.decodeResource(mWeakReference.get().getResources(), R.drawable.logo))
                    .setAutoCancel(false)
                    .build();
            mNotificationManager.notify(TAG, 621, notification);
        }
    }

    public void cancelNotification() {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(TAG, 621);
        }
    }

    public void onDestroy() {
        mNotificationManager = null;
        mWeakReference.clear();
    }

}
