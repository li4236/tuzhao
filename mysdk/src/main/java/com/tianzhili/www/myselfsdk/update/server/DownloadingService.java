
package com.tianzhili.www.myselfsdk.update.server;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.dou361.download.DownloadManager;
import com.dou361.download.ParamsManager;
import com.tianzhili.www.myselfsdk.R;
import com.tianzhili.www.myselfsdk.update.UpdateHelper;
import com.tianzhili.www.myselfsdk.update.bean.Update;
import com.tianzhili.www.myselfsdk.update.type.UpdateType;
import com.tianzhili.www.myselfsdk.update.util.InstallUtil;
import com.tianzhili.www.myselfsdk.update.util.UpdateConstants;
import com.tianzhili.www.myselfsdk.update.util.UpdateSP;

import java.io.File;

/**
 * 在后台进行版本更新
 * https://github.com/jjdxmashl/jjdxm_update
 */
public class DownloadingService extends Service {

    private RemoteViews contentView;
    private NotificationManager notificationManager;
    private Notification notification;
    private Update update;
    private NotificationCompat.Builder ntfBuilder;
    private String url;
    private Context mContext;
    private DownloadManager manage;
    private int opState = 0;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String strUrl = (String) msg.obj;
            if (url != null && url.equals(strUrl)) {
                switch (msg.what) {
                    case ParamsManager.State_NORMAL:
                        break;
                    case ParamsManager.State_DOWNLOAD:
                        Bundle bundle = msg.getData();
                        long current = bundle.getLong("percent");
                        long total = bundle.getLong("loadSpeed");
                        if (total <= 0) {
                            return;
                        }
                        if (current > total) {
                            return;
                        }
                        notifyNotification(current);
                        sendBroadcastType(current);
                        break;
                    case ParamsManager.State_FINISH:
                        File fil = new File(manage.getDownPath(), url.substring(url.lastIndexOf("/") + 1, url.length()));
                        if (fil.exists() && fil.length() > 0) {
                            //下载完成
                            showInstallNotificationUI(fil);
                            if (UpdateHelper.getInstance().getUpdateType() == UpdateType.autowifidown) {
                                installApk(mContext, fil);
                            } else {
                                InstallUtil.installApk(mContext, fil.getAbsolutePath());
                                if (UpdateHelper.getInstance().getForceListener() != null) {
                                    UpdateHelper.getInstance().getForceListener().onUserCancel(UpdateSP.isForced());
                                }
//                                check(mContext,update,UpdateConstants.UPDATE_INSTALL,fil.getAbsolutePath(),false);
//                                Intent intent = new Intent(mContext, UpdateDialogActivity.class);
//                                intent.putExtra(UpdateConstants.DATA_UPDATE, update);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                intent.putExtra(UpdateConstants.DATA_ACTION, UpdateConstants.UPDATE_INSTALL);
//                                intent.putExtra(UpdateConstants.SAVE_PATH, fil.getAbsolutePath());
//                                intent.putExtra(UpdateConstants.START_TYPE, false);
//                                startActivity(intent);
                            }
                            sendBroadcastType(100);
                        } else {
                            //文件可能被删除
                            if (update != null) {
                                manage.deleteAllDownload();
                            }
//                            check(mContext,update,UpdateConstants.UPDATE_TIE,null,false);
//                            Intent intent = new Intent(mContext, UpdateDialogActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.putExtra(UpdateConstants.DATA_UPDATE, update);
//                            intent.putExtra(UpdateConstants.DATA_ACTION, UpdateConstants.UPDATE_TIE);
//                            intent.putExtra(UpdateConstants.START_TYPE, false);
//                            startActivity(intent);
                        }
                        break;
                    case ParamsManager.State_PAUSE:
                        updateNotification();
                        break;
                    case ParamsManager.State_WAIT:
                        createNotification();
                        break;
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;
        manage = DownloadManager.getInstance(mContext);
        manage.setHandler(handler);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int action = intent.getIntExtra(UpdateConstants.DATA_ACTION, 0);
            if (action == UpdateConstants.START_DOWN) {
                update = (Update) intent.getSerializableExtra(UpdateConstants.DATA_UPDATE);
                url = update.getUpdateUrl();
                if (update != null && !TextUtils.isEmpty(url)) {
                    manage.startDownload(url);
                }
            } else if (action == UpdateConstants.PAUSE_DOWN) {
                if (manage!=null){
                    if (manage.isDownloading(url)) {
                        manage.pauseDownload(url);
                        opState = 0;
                        updateNotification();
                    } else {
                        manage.startDownload(url);
                        opState = 1;
                        updateNotification();
                    }
                }
            } else if (action == UpdateConstants.CANCEL_DOWN) {
                manage.deleteDownload(url);
                opState = 2;
                updateNotification();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressWarnings("deprecation")
    public void createNotification() {
        Handler handle=new Handler(Looper.getMainLooper());
        handle.post(new Runnable(){
            public void run(){
                Toast.makeText(getApplicationContext(),"开始后台下载",Toast.LENGTH_SHORT).show();
            }
        });
        notification = new Notification(getApplicationInfo().icon,"安装包正在下载...",System.currentTimeMillis());
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        /*** 自定义  Notification 的显示****/
        @LayoutRes int layoutId = UpdateSP.getStatusBarLayout();
        if (layoutId > 0) {
            contentView = new RemoteViews(getPackageName(), layoutId);
        } else {
            contentView = new RemoteViews(getPackageName(), R.layout.jjdxm_download_notification);
        }
        contentView.setImageViewResource(R.id.jjdxm_update_iv_icon, getApplicationInfo().icon);
        contentView.setTextViewText(R.id.jjdxm_update_title, getString(getApplicationInfo().labelRes));
        contentView.setProgressBar(R.id.jjdxm_update_progress_bar, 100, 0, false);
        contentView.setTextViewText(R.id.jjdxm_update_progress_text, "0%");

        /**暂停和开始*/
        Intent downIntent = new Intent(this, DownloadingService.class);
        downIntent.putExtra(UpdateConstants.DATA_ACTION, UpdateConstants.PAUSE_DOWN);
        downIntent.putExtra("update", update);
        PendingIntent pendingIntent1 = PendingIntent.getService(this, UpdateConstants.PAUSE_DOWN, downIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.jjdxm_update_rich_notification_continue, pendingIntent1);

        /**取消*/
        Intent cancelIntent = new Intent(this, DownloadingService.class);
        cancelIntent.putExtra(UpdateConstants.DATA_ACTION, UpdateConstants.CANCEL_DOWN);
        cancelIntent.putExtra("update", update);
        PendingIntent pendingIntent2 = PendingIntent.getService(this, UpdateConstants.CANCEL_DOWN, cancelIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        contentView.setOnClickPendingIntent(R.id.jjdxm_update_rich_notification_cancel, pendingIntent2);

        notification.contentView = contentView;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(UpdateConstants.NOTIFICATION_ACTION, notification);
    }

    private void updateNotification() {
        if (opState == 0) {
            /**暂停*/
            if (contentView != null) {
                contentView.setTextViewText(R.id.jjdxm_update_rich_notification_continue, "开始");
                notification.contentView = contentView;
                notificationManager.notify(UpdateConstants.NOTIFICATION_ACTION, notification);
            }
        } else if (opState == 1) {
            /**开始*/
            if (contentView != null) {
                contentView.setTextViewText(R.id.jjdxm_update_rich_notification_continue, "暂停");
                notification.contentView = contentView;
                notificationManager.notify(UpdateConstants.NOTIFICATION_ACTION, notification);
            }
        } else if (opState == 2) {
            /**取消*/
            if (notificationManager == null) {
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            notificationManager.cancel(UpdateConstants.NOTIFICATION_ACTION);
        }
    }

    /**
     * 刷新下载进度
     */
    private void notifyNotification(long percent) {
        contentView.setTextViewText(R.id.jjdxm_update_progress_text, percent + "%");
        contentView.setProgressBar(R.id.jjdxm_update_progress_bar, 100, (int) percent, false);
        notification.contentView = contentView;
        notificationManager.notify(UpdateConstants.NOTIFICATION_ACTION, notification);
    }

    /**
     * 显示安装
     */
    private void showInstallNotificationUI(File file) {
        if (ntfBuilder == null) {
            ntfBuilder = new NotificationCompat.Builder(this);
        }
        Handler handle=new Handler(Looper.getMainLooper());
        handle.post(new Runnable(){
            public void run(){
                Toast.makeText(getApplicationContext(),"下载完成",Toast.LENGTH_SHORT).show();
            }
        });

        ntfBuilder.setSmallIcon(getApplicationInfo().icon).setContentTitle(getString(getApplicationInfo().labelRes)).setContentText("下载完成，点击安装").setTicker("任务下载完成");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        ntfBuilder.setContentIntent(pendingIntent);
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        notificationManager.notify(UpdateConstants.NOTIFICATION_ACTION,ntfBuilder.build());
    }

    /**
     * 安装apk
     *
     * @param context 上下文
     * @param file    APK文件
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    private void sendBroadcastType(long type) {
        Intent intent = new Intent("com.dou361.update.downloadBroadcast");
        intent.putExtra("type", type);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}