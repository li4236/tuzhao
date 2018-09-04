package com.tuzhao.publicwidget.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.tuzhao.R;

import java.lang.ref.WeakReference;

/**
 * Created by juncoder on 2018/8/27.
 */
public class UpdateNotification {

    private WeakReference<Context> mWeakReference;

    private NotificationManager mNotificationManager;

    UpdateNotification(Context context) {
        mWeakReference = new WeakReference<>(context);
        mNotificationManager = (NotificationManager) mWeakReference.get().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public Notification showNotification(int progress, String versionName) {
        if (mNotificationManager != null) {
            Notification notification = new NotificationCompat.Builder(mWeakReference.get(), "621")
                    .setContentTitle("更新到" + versionName + "版本")
                    .setContentText("已下载" + progress + "%")
                    .setWhen(System.currentTimeMillis())
                    .setProgress(100, progress, false)
                    .setSmallIcon(R.drawable.logo)
                    .setOnlyAlertOnce(true)
                    .setLargeIcon(BitmapFactory.decodeResource(mWeakReference.get().getResources(), R.drawable.logo))
                    .setAutoCancel(false)
                    .build();
            /*if (progress == 100) {
                File file = new File(mWeakReference.get().getExternalFilesDir("upgrade_apk") + File.separator,
                        mWeakReference.get().getPackageName() + ".apk");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setDataAndType(FileProvider.getUriForFile(mWeakReference.get(), "com.tuzhao.photopicker.provider", file),
                            "application/vnd.android.package-archive");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                }
                notification.contentIntent = PendingIntent.getActivity(mWeakReference.get(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }*/
            mNotificationManager.notify(621, notification);
            return notification;
        }
        return null;
    }

    public void cancelNotification() {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(621);
        }
    }

    public void onDestroy() {
        cancelNotification();
        mNotificationManager = null;
        mWeakReference.clear();
    }

}
