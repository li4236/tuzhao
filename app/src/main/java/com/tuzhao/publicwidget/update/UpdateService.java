package com.tuzhao.publicwidget.update;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.okgo.callback.FileCallback;
import com.tianzhili.www.myselfsdk.okgo.request.BaseRequest;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.UpdateInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.DeviceUtils;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;
import com.tuzhao.utils.SpUtils;

import java.io.File;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/9/3.
 */
public class UpdateService extends Service implements IntentObserver {

    private static final String TAG = "UpdateService";

    private UpdateInfo mUpdateInfo;

    private boolean mIsForceUpdate;

    private UpdateNotification mUpdateNotification;

    private int mCurrentProgress = -2;

    private File mFile;

    /**
     * true(用户在设置那里检查更新)    false(启动时自动检查更新)
     */
    private boolean mUserUpdate;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentObserable.registerObserver(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(ConstansUtil.INTENT_MESSAGE)) {
            mUserUpdate = true;
        }
        if (mUserUpdate && mCurrentProgress >= 0) {
            MyToast.showToast(getApplicationContext(), "已经在下载更新了哦", 5);
            IntentObserable.dispatch(ConstansUtil.DIALOG_DISMISS);
        } else {
            OkGo.getInstance().cancelTag(TAG);
            checkUpdate();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(false);
        OkGo.getInstance().cancelTag(TAG);
        IntentObserable.unregisterObserver(this);
        if (mUpdateNotification != null) {
            mUpdateNotification.onDestroy();
            mUpdateNotification = null;
        }
    }

    public void checkUpdate() {
        OkGo.post(HttpConstants.checkVersion)
                .tag(TAG)
                .execute(new JsonCallback<Base_Class_Info<UpdateInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<UpdateInfo> updateInfoBase_class_info, Call call, Response response) {
                        if (mUserUpdate) {
                            IntentObserable.dispatch(ConstansUtil.DIALOG_DISMISS);
                        }
                        mUpdateInfo = updateInfoBase_class_info.data;
                        int localVersion = DeviceUtils.getLocalVersion(getApplicationContext());
                        mIsForceUpdate = localVersion < Integer.valueOf(updateInfoBase_class_info.data.getForceUpdateVersion());
                        if (mIsForceUpdate) {
                            //是强制更新
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(ConstansUtil.UPDATE_INFO, updateInfoBase_class_info.data);
                            bundle.putBoolean(ConstansUtil.INTENT_MESSAGE, true);
                            bundle.putBoolean(ConstansUtil.USER_UPDATE, mUserUpdate);
                            Intent intent = new Intent(UpdateService.this, UpdateActivity.class);
                            intent.putExtra(ConstansUtil.SHOW_UPDATE_DIALOG, bundle);
                            startActivity(intent);
                        } else if (localVersion < Integer.valueOf(updateInfoBase_class_info.data.getVersionCode())) {
                            if (mUserUpdate || !SpUtils.getInstance(getApplicationContext()).getString(SpUtils.IGNORE_VERSION).equals(updateInfoBase_class_info.data.getVersionCode())) {
                                //不是强制更新并且没有忽略该版本或者是用户手动检查更新的
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(ConstansUtil.UPDATE_INFO, updateInfoBase_class_info.data);
                                bundle.putBoolean(ConstansUtil.INTENT_MESSAGE, false);
                                bundle.putBoolean(ConstansUtil.USER_UPDATE, mUserUpdate);
                                Intent intent = new Intent(UpdateService.this, UpdateActivity.class);
                                intent.putExtra(ConstansUtil.SHOW_UPDATE_DIALOG, bundle);
                                startActivity(intent);
                            }
                        } else if (mUserUpdate) {
                            MyToast.showToast(getApplicationContext(), "当前已是最新版本了哦", 5);
                        }
                        SpUtils.getInstance(getApplicationContext()).putBoolean(SpUtils.ALREADY_CHECK_UPDATE, true);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mUserUpdate) {
                            IntentObserable.dispatch(ConstansUtil.DIALOG_DISMISS);
                            if (!DensityUtil.isException(getApplicationContext(), e)) {
                                MyToast.showToast(getApplicationContext(), "检查更新失败，请稍后重试", 5);
                            }
                        }
                    }

                });
    }

    private void downloadApk(final UpdateInfo updateInfo, final boolean forceUpdate) {
        OkGo.get(updateInfo.getDownloadUrl())
                .tag(TAG)
                .execute(new FileCallback(getApplicationContext().getExternalFilesDir("upgrade_apk") +
                        File.separator, getApplicationContext().getPackageName() + ".apk") {

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        mCurrentProgress = 0;
                    }

                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        mFile = file;
                        if (mUpdateNotification != null) {
                            mUpdateNotification.showNotification(100, updateInfo.getVersionName());
                        } else {
                            IntentObserable.dispatch(ConstansUtil.UPDATE_PROGRESS, ConstansUtil.INTENT_MESSAGE, 100);
                        }
                        if (Objects.equals(DeviceUtils.getFileMd5(file), updateInfo.getNewMd5())) {
                            checkInstallPermission();
                        } else {
                            if (!forceUpdate) {
                                //更新失败
                                MyToast.showToast(getApplicationContext(), "安装包异常，请下次再试", 5);
                                IntentObserable.dispatch(ConstansUtil.UPDATE_PROGRESS, ConstansUtil.INTENT_MESSAGE, -1);
                                stopSelf();
                            } else {
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        }
                    }

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                        if (mCurrentProgress != (int) (progress * 100)) {
                            //防止频繁更新造成卡顿
                            mCurrentProgress = (int) (progress * 100);
                            if (mUpdateNotification != null) {
                                mUpdateNotification.showNotification(mCurrentProgress, updateInfo.getVersionName());
                            } else {
                                IntentObserable.dispatch(ConstansUtil.UPDATE_PROGRESS, ConstansUtil.INTENT_MESSAGE, mCurrentProgress);
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mCurrentProgress = -2;
                        if (mUpdateNotification != null) {
                            mUpdateNotification.cancelNotification();
                        }
                        if (forceUpdate) {
                            if (getApplicationContext() != null) {
                                //如果需要强制更新但是更新失败的就退出app
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        } else {
                            MyToast.showToast(getApplicationContext(), "下载安装包失败", 5);
                            IntentObserable.dispatch(ConstansUtil.UPDATE_PROGRESS, ConstansUtil.INTENT_MESSAGE, -1);
                            stopSelf();
                        }
                    }
                });
    }

    private void checkInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //适配android8的未知来源应用权限
            if (getApplicationContext().getPackageManager().canRequestPackageInstalls()) {
                Intent intent = new Intent(this, UpdateActivity.class);
                intent.putExtra(ConstansUtil.REQUEST_INSTALL_PERMISSION, mIsForceUpdate);
                startActivity(intent);
            } else {
                installApk();
            }
        } else {
            installApk();
        }
    }

    private void installApk() {
        if (mFile == null) {
            //如果是UpdateActivity检查到已下载好安装包的则会null
            mFile = new File(getExternalFilesDir("upgrade_apk") + File.separator, getPackageName() + ".apk");
        }
        if (mFile.exists() && getApplicationContext() != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(), "com.tuzhao.photopicker.provider", mFile),
                        "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else {
                intent.setDataAndType(Uri.fromFile(mFile), "application/vnd.android.package-archive");
            }
            startActivity(intent);
            stopSelf();
        }
    }

    @Override
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ConstansUtil.DOWNLOAD_APK:
                    downloadApk(mUpdateInfo, mIsForceUpdate);
                    break;
                case ConstansUtil.SHOW_UPDATE_NOTIFICATION:
                    mUpdateNotification = new UpdateNotification(getApplicationContext());
                    startForeground(621, mUpdateNotification.showNotification(mCurrentProgress, mUpdateInfo.getVersionName()));
                    break;
                case ConstansUtil.INSTALL_APK:
                    installApk();
                    break;
            }
        }
    }

}
