package com.tuzhao.publicwidget.update;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.okgo.callback.FileCallback;
import com.tianzhili.www.myselfsdk.okgo.request.BaseRequest;
import com.tuzhao.R;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.UpdateInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.customView.UpdateProgress;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.DeviceUtils;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.SpUtils;

import java.io.File;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/8/27.
 */
public class UpdateManager {

    private static final String TAG = "UpdateManager";

    private static final int PERMISSION_REQUEST_CODE = 0x789;

    private static final int INSTALL_REQUEST_CODE = 0x987;

    private WeakReference<Activity> mWeakReference;

    private CustomDialog mCustomDialog;

    private File sFile;

    private boolean mStartDownload;

    private UpdateNotification mUpdateNotification;

    private UpdateProgress mUpdateProgress;

    public UpdateManager(Activity activity) {
        mWeakReference = new WeakReference<>(activity);
    }

    public void checkUpdate() {
        OkGo.post(HttpConstants.checkVersion)
                .tag(TAG)
                .execute(new JsonCallback<Base_Class_Info<UpdateInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<UpdateInfo> updateInfoBase_class_info, Call call, Response response) {
                        int localVersion = DeviceUtils.getLocalVersion(mWeakReference.get());
                        if (localVersion < Integer.valueOf(updateInfoBase_class_info.data.getForceUpdateVersion())) {
                            showUpdateDialog(updateInfoBase_class_info.data, localVersion < Integer.valueOf(updateInfoBase_class_info.data.getForceUpdateVersion()));
                        } else if (!SpUtils.getInstance(mWeakReference.get()).getString(SpUtils.IGNORE_VERSION).equals(updateInfoBase_class_info.data.getVersionCode())) {
                            showUpdateDialog(updateInfoBase_class_info.data, localVersion < Integer.valueOf(updateInfoBase_class_info.data.getForceUpdateVersion()));
                        }
                        SpUtils.getInstance(mWeakReference.get()).putBoolean(SpUtils.ALREADY_UPDATE, true);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);

                    }

                });
    }

    private void showUpdateDialog(final UpdateInfo updateInfo, final boolean forceUpdate) {
        final View view = LayoutInflater.from(mWeakReference.get()).inflate(R.layout.dialog_update_layout, null);
        mCustomDialog = new CustomDialog(mWeakReference.get(), view);

        ImageUtil.showPic((ImageView) view.findViewById(R.id.dialog_iv), R.drawable.ic_update_bg);
        final TextView updateContent = view.findViewById(R.id.update_content);
        updateContent.setText(Html.fromHtml(updateInfo.getNewContent()));

        final TextView updateNow = view.findViewById(R.id.update_now);
        mUpdateProgress = view.findViewById(R.id.update_pb);
        final TextView backgroundUpdate = view.findViewById(R.id.backgound_update_tv);
        final CheckBox ignoreCb = view.findViewById(R.id.ignore_cb);

        if (!forceUpdate) {
            ImageView close = view.findViewById(R.id.dialog_close);
            close.setVisibility(View.VISIBLE);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startShowNotification(updateInfo.getVersionName());
                    mUpdateProgress = null;
                    mCustomDialog.dismiss();
                }
            });

            ignoreCb.setVisibility(View.VISIBLE);
            view.findViewById(R.id.ignore_tv).setVisibility(View.VISIBLE);
            ignoreCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        SpUtils.getInstance(mWeakReference.get()).putString(SpUtils.IGNORE_VERSION, updateInfo.getVersionCode());
                    } else {
                        SpUtils.getInstance(mWeakReference.get()).putString(SpUtils.IGNORE_VERSION, "-1");
                    }
                }
            });

            ConstraintLayout constraintLayout = view.findViewById(R.id.bottom_cl);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.setMargin(R.id.update_now, ConstraintSet.TOP, DensityUtil.dp2px(mWeakReference.get(), 4));
            constraintSet.applyTo(constraintLayout);
        } else {
            mCustomDialog.setCancelable(false);
        }

        updateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpdateProgress.setVisibility(View.VISIBLE);
                downloadApk(updateInfo, forceUpdate);
                if (forceUpdate) {
                    view.findViewById(R.id.update_hint).setVisibility(View.VISIBLE);
                } else {
                    backgroundUpdate.setVisibility(View.VISIBLE);
                }
                updateNow.setVisibility(View.GONE);
                ignoreCb.setVisibility(View.GONE);
                view.findViewById(R.id.ignore_tv).setVisibility(View.GONE);
            }
        });

        backgroundUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomDialog.dismiss();
                mUpdateProgress = null;
                startShowNotification(updateInfo.getVersionName());
            }
        });

        mCustomDialog.show();
    }

    public void setDialogProgress(int progress) {
        if (mUpdateProgress != null) {
            mUpdateProgress.setProgress(progress);
        }
    }

    private void downloadApk(final UpdateInfo updateInfo, final boolean forceUpdate) {
        OkGo.get(updateInfo.getDownloadUrl())
                .tag(TAG)
                .execute(new FileCallback(mWeakReference.get().getExternalFilesDir("upgrade_apk") +
                        File.separator, mWeakReference.get().getPackageName() + ".apk") {

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        mStartDownload = true;
                    }

                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        sFile = file;
                        if (mUpdateNotification != null) {
                            mUpdateNotification.showNotification(100, updateInfo.getVersionName());
                        } else {
                            setDialogProgress(100);
                        }
                        checkInstallPermission();
                    }

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                        if (mUpdateNotification != null) {
                            mUpdateNotification.showNotification((int) (progress * 100), updateInfo.getVersionName());
                        } else {
                            setDialogProgress((int) (progress * 100));
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        MyToast.showToast(mWeakReference.get(), "更新失败", 5);
                        mCustomDialog.cancel();
                        if (mUpdateNotification != null) {
                            mUpdateNotification.cancelNotification();
                        }
                        if (forceUpdate) {
                            if (mWeakReference.get() != null) {
                                //如果需要强制更新但是更新失败的就退出app
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        }
                    }
                });
    }

    private void startShowNotification(String versionName) {
        if (mUpdateNotification == null && mStartDownload) {
            mUpdateNotification = new UpdateNotification(mWeakReference.get());
            mUpdateNotification.createNotificationChannel();
            mUpdateNotification.performShowNotification(0, versionName);
        }
    }

    private void checkInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //适配android8的未知来源应用权限
            if (mWeakReference.get() != null && !mWeakReference.get().getPackageManager().canRequestPackageInstalls()) {
                showInstallDialog();
            } else {
                installApk();
            }
        } else {
            installApk();
        }
    }

    private void showInstallDialog() {
        new TipeDialog.Builder(mWeakReference.get())
                .autoDissmiss(true)
                .setCancelable(false)
                .setTitle("权限申请")
                .setMessage("新版本下载完成，请开启允许安装应用以完成更新")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //华为手机再次申请WRITE_EXTERNAL_STORAGE权限才会跳转到允许安装未知来源应用
                        ActivityCompat.requestPermissions(mWeakReference.get(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                })
                .create()
                .show();
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + mWeakReference.get().getPackageName()));
            mWeakReference.get().startActivityForResult(intent, INSTALL_REQUEST_CODE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode) {
        if (requestCode == INSTALL_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            installApk();
        }
    }

    private void installApk() {
        if (sFile != null && mWeakReference.get() != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setDataAndType(FileProvider.getUriForFile(mWeakReference.get(), "com.tuzhao.photopicker.provider", sFile),
                        "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else {
                intent.setDataAndType(Uri.fromFile(sFile), "application/vnd.android.package-archive");
            }
            mWeakReference.get().startActivity(intent);
        }
    }

    public void onDestroy() {
        mWeakReference.clear();
        OkGo.getInstance().cancelTag(TAG);
        if (mUpdateNotification != null) {
            mUpdateNotification.onDestroy();
        }
    }

}
