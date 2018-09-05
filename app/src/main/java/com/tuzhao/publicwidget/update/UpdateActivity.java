package com.tuzhao.publicwidget.update;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.info.UpdateInfo;
import com.tuzhao.publicwidget.customView.UpdateProgress;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.DeviceUtils;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;
import com.tuzhao.utils.SpUtils;

import java.io.File;
import java.util.Objects;

/**
 * Created by juncoder on 2018/9/3.
 */
public class UpdateActivity extends AppCompatActivity implements IntentObserver {

    private CustomDialog mCustomDialog;

    private UpdateProgress mUpdateProgress;

    private boolean mStartDownload;

    private boolean mForceUpdate;

    private boolean mUserUpdate;

    private static final int PERMISSION_REQUEST_CODE = 0x789;

    private static final int INSTALL_REQUEST_CODE = 0x987;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra(ConstansUtil.SHOW_UPDATE_DIALOG)) {
            Bundle bundle = getIntent().getBundleExtra(ConstansUtil.SHOW_UPDATE_DIALOG);
            mUserUpdate = bundle.getBoolean(ConstansUtil.USER_UPDATE);
            showUpdateDialog((UpdateInfo) Objects.requireNonNull(bundle.getParcelable(ConstansUtil.UPDATE_INFO)), bundle.getBoolean(ConstansUtil.INTENT_MESSAGE, false));
        } else if (getIntent().hasExtra(ConstansUtil.REQUEST_INSTALL_PERMISSION)) {
            showInstallDialog(getIntent().getBooleanExtra(ConstansUtil.REQUEST_INSTALL_PERMISSION, true));
        }
        IntentObserable.registerObserver(this);
    }

    private void showUpdateDialog(final UpdateInfo updateInfo, final boolean forceUpdate) {
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_layout, null);
        mCustomDialog = new CustomDialog(this, view);
        mCustomDialog.setCancelable(false);

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
                    mUpdateProgress = null;
                    mCustomDialog.dismiss();
                    if (!mStartDownload) {
                        //用户取消更新
                        stopService(new Intent(UpdateActivity.this, UpdateService.class));
                    } else {
                        createNotificationChannel();
                        performShowNotification();
                    }
                    IntentObserable.dispatch(ConstansUtil.UPDATE_ACTIVITY_FINISH);
                    finish();
                }
            });

            if (!mUserUpdate) {
                ignoreCb.setVisibility(View.VISIBLE);
                view.findViewById(R.id.ignore_tv).setVisibility(View.VISIBLE);
                ignoreCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            SpUtils.getInstance(UpdateActivity.this).putString(SpUtils.IGNORE_VERSION, updateInfo.getVersionCode());
                        } else {
                            SpUtils.getInstance(UpdateActivity.this).putString(SpUtils.IGNORE_VERSION, "-1");
                        }
                    }
                });

                ConstraintLayout constraintLayout = view.findViewById(R.id.bottom_cl);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.setMargin(R.id.update_now, ConstraintSet.TOP, DensityUtil.dp2px(this, 4));
                constraintSet.applyTo(constraintLayout);
            }
        }

        updateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getExternalFilesDir("upgrade_apk") + File.separator, getPackageName() + ".apk");
                if (file.exists() && Objects.equals(DeviceUtils.getFileMd5(file), updateInfo.getNewMd5())) {
                    //如果安装包已存在就检查权限并安装
                    mCustomDialog.dismiss();
                    checkInstallPermission(forceUpdate);
                } else {
                    mUpdateProgress.setVisibility(View.VISIBLE);

                    if (forceUpdate) {
                        view.findViewById(R.id.update_hint).setVisibility(View.VISIBLE);
                    } else {
                        backgroundUpdate.setVisibility(View.VISIBLE);
                    }
                    updateNow.setVisibility(View.GONE);
                    ignoreCb.setVisibility(View.GONE);
                    view.findViewById(R.id.ignore_tv).setVisibility(View.GONE);

                    mStartDownload = true;
                    IntentObserable.dispatch(ConstansUtil.DOWNLOAD_APK);
                }
            }
        });

        backgroundUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpdateProgress = null;
                createNotificationChannel();
                performShowNotification();
                mCustomDialog.dismiss();
            }
        });

        mCustomDialog.show();
    }

    public void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                NotificationChannel notificationChannel = new NotificationChannel("621", "下载通知", NotificationManager.IMPORTANCE_LOW);
                notificationChannel.setSound(null, null);
                notificationChannel.enableVibration(false);
                notificationManager.createNotificationChannel(notificationChannel);

                if (notificationChannel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                    DeviceUtils.openNotificationChannel(this);
                }

            }
        }
    }

    public void performShowNotification() {
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            String message;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                message = "为了在通知栏上显示下载进度，请开启下载通知";
            } else {
                message = "为了在通知栏上显示下载进度，请开启通知";
            }
            new TipeDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("权限申请")
                    .setMessage(message)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            IntentObserable.dispatch(ConstansUtil.UPDATE_ACTIVITY_FINISH);
                            finish();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DeviceUtils.openNotification(UpdateActivity.this);
                            IntentObserable.dispatch(ConstansUtil.SHOW_UPDATE_NOTIFICATION);
                            IntentObserable.dispatch(ConstansUtil.UPDATE_ACTIVITY_FINISH);
                            finish();
                        }
                    })
                    .create()
                    .show();
        } else {
            IntentObserable.dispatch(ConstansUtil.UPDATE_ACTIVITY_FINISH);
            IntentObserable.dispatch(ConstansUtil.SHOW_UPDATE_NOTIFICATION);
            finish();
        }
    }

    private void checkInstallPermission(boolean isForceUpdate) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //适配android8的未知来源应用权限
            if (getApplicationContext().getPackageManager().canRequestPackageInstalls()) {
                showInstallDialog(isForceUpdate);
            } else {
                IntentObserable.dispatch(ConstansUtil.INSTALL_APK);
                finish();
            }
        } else {
            IntentObserable.dispatch(ConstansUtil.INSTALL_APK);
            finish();
        }
    }

    private void showInstallDialog(final boolean isForceUpdate) {
        mForceUpdate = isForceUpdate;
        new TipeDialog.Builder(this)
                .autoDissmiss(true)
                .setCancelable(false)
                .setTitle("权限申请")
                .setMessage("新版本下载完成，请开启允许安装应用以完成更新")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyToast.showToast(UpdateActivity.this, "没有权限，更新版本失败", 5);
                        if (isForceUpdate) {
                            Process.killProcess(Process.myPid());
                        } else {
                            finish();
                        }
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //华为手机再次申请WRITE_EXTERNAL_STORAGE权限才会跳转到允许安装未知来源应用
                        ActivityCompat.requestPermissions(UpdateActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, INSTALL_REQUEST_CODE);
            } else if (mForceUpdate) {
                Process.killProcess(Process.myPid());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INSTALL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                IntentObserable.dispatch(ConstansUtil.INSTALL_APK);
                finish();
            } else if (mForceUpdate) {
                Process.killProcess(Process.myPid());
            }
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUpdateProgress = null;
        IntentObserable.unregisterObserver(this);
    }

    @Override
    public void onReceive(Intent intent) {
        if (Objects.equals(intent.getAction(), ConstansUtil.UPDATE_PROGRESS)) {
            int progress = intent.getIntExtra(ConstansUtil.INTENT_MESSAGE, 0);
            if (progress >= 0) {
                if (mUpdateProgress != null) {
                    mUpdateProgress.setProgress(progress);
                }
            } else {
                //更新失败
                finish();
            }
        }
    }

}
