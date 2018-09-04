package com.tianzhili.www.myselfsdk.update.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dou361.download.DownloadManager;
import com.dou361.download.DownloadModel;
import com.dou361.download.ParamsManager;
import com.tianzhili.www.myselfsdk.R;
import com.tianzhili.www.myselfsdk.update.UpdateHelper;
import com.tianzhili.www.myselfsdk.update.bean.Update;
import com.tianzhili.www.myselfsdk.update.server.DownloadingService;
import com.tianzhili.www.myselfsdk.update.type.UpdateType;
import com.tianzhili.www.myselfsdk.update.util.InstallUtil;
import com.tianzhili.www.myselfsdk.update.util.NetworkUtil;
import com.tianzhili.www.myselfsdk.update.util.UpdateConstants;
import com.tianzhili.www.myselfsdk.update.util.UpdateSP;

import java.io.File;

/**
 * Created by TZL12 on 2017/10/26.
 */

public class UpdateDialog extends DialogFragment {

    /**
     * UI
     */
    private View mContentView;
    private TextView textview_content, textview_update_now, textview_wifi_warm;
    private ImageView imageview_colse;

    /**
     * 数据
     */
    private Context mContext;
    private Update mUpdate;
    private int mAction;
    private String mPath;
    private boolean isActivityEnter;//调起方式

    //是否已经下载完成
    private boolean finshDown;

    public UpdateDialog(Context context, Update info, int action, String path, boolean isactivityEnter) {
        super();
        mContext = context;
        mUpdate = info;
        mAction = action;
        mPath = path;
        isActivityEnter = isactivityEnter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//设置没有title
        getDialog().setCanceledOnTouchOutside(false);//设置阴影部分点击不可消失
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (UpdateSP.isForced()) {
            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
                //监听返回键，不让返回键点击消失
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (UpdateHelper.getInstance().getUpdateType() == UpdateType.autowifiupdate) {
                            ((Activity) mContext).finish();
                        }
                        if (UpdateHelper.getInstance().getForceListener() != null) {
                            UpdateHelper.getInstance().getForceListener().onUserCancel(UpdateSP.isForced());
                        }
                        return true;
                    }
                    return false;
                }
            });
        }

        mContentView = inflater.inflate(R.layout.dialog_update_layout, container);

        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件
        return mContentView;
    }

    private void initView() {
        textview_wifi_warm = (TextView) mContentView.findViewById(R.id.id_dialog_update_layout_textview_wifi_warm);
        textview_content = (TextView) mContentView.findViewById(R.id.id_dialog_update_layout_textview_content);
        textview_update_now = (TextView) mContentView.findViewById(R.id.id_dialog_update_layout_textview_update_now);
        imageview_colse = (ImageView) mContentView.findViewById(R.id.id_dialog_update_layout_imageview_colse);
    }

    private void initData() {

        if (UpdateSP.isForced()) {
            textview_update_now.setVisibility(View.VISIBLE);
            imageview_colse.setVisibility(View.GONE);
        }

        if (NetworkUtil.isConnectedByWifi()) {
            //WiFi环境
            textview_wifi_warm.setVisibility(View.VISIBLE);
        } else {
            textview_wifi_warm.setVisibility(View.GONE);
        }
        textview_content.setText(mUpdate.getUpdateContent());

        if (TextUtils.isEmpty(mPath)) {
            String url = mUpdate.getUpdateUrl();
            mPath = DownloadManager.getInstance(mContext).getDownPath() + File.separator + url.substring(url.lastIndexOf("/") + 1, url.length());
        }

        if (mAction == 0) {
            DownloadModel dd = DownloadManager.getInstance(mContext).getDownloadByUrl(mUpdate.getUpdateUrl());
            if (dd != null) {
                finshDown = (dd.getDOWNLOAD_STATE() == ParamsManager.State_FINISH);
                File fil = new File(mPath);
                if (finshDown && fil.exists() && (fil.length() > 0) && (fil.length() + "").equals(dd.getDOWNLOAD_TOTALSIZE())) {
                    finshDown = true;
                } else {
                    DownloadManager.getInstance(mContext).deleteAllDownload();
                    finshDown = false;
                }
            } else {
                finshDown = false;
            }
        } else {
            finshDown = true;
        }

        if (finshDown) {
            //完成下载
            if (isActivityEnter) {
                /**Activity方式调起的*/
                textview_update_now.setText("立即安装");
            } else {
                /**服务方式方式调起的*/
                InstallUtil.installApk(mContext, mPath);
                dismiss();
                if (UpdateHelper.getInstance().getForceListener() != null) {
                    UpdateHelper.getInstance().getForceListener().onUserCancel(UpdateSP.isForced());
                }
            }
        } else {
            //有更新下载
            textview_update_now.setText("立即下载");
        }
    }

    private void initEvent() {

        imageview_colse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSP.setIgnore(mUpdate.getVersionCode() + "," + mUpdate.getTime());
                if (UpdateHelper.getInstance().getForceListener() != null) {
                    UpdateHelper.getInstance().getForceListener().onUserCancel(UpdateSP.isForced());
                }
                if (UpdateHelper.getInstance().getUpdateListener() != null) {
                    if (!finshDown) {
                        UpdateHelper.getInstance().getUpdateListener().onUserCancelDowning();
                    } else {
                        UpdateHelper.getInstance().getUpdateListener().onUserCancelInstall();
                    }
                }
                dismiss();
            }
        });

        textview_update_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击更新
                if (finshDown) {
                    DownloadModel dd = DownloadManager.getInstance(mContext).getDownloadByUrl(mUpdate.getUpdateUrl());
                    if (dd != null) {
                        finshDown = (dd.getDOWNLOAD_STATE() == ParamsManager.State_FINISH);
                        File fil = new File(mPath);
                        if (finshDown && fil.exists() && (fil.length() > 0) && (fil.length() + "").equals(dd.getDOWNLOAD_TOTALSIZE())) {
                            finshDown = true;
                        } else {
                            DownloadManager.getInstance(mContext).deleteAllDownload();
                            finshDown = false;
                        }
                    } else {
                        finshDown = false;
                    }
                    if (finshDown) {
                        InstallUtil.installApk(mContext, mPath);
                        dismiss();
                        if (UpdateHelper.getInstance().getForceListener() != null) {
                            UpdateHelper.getInstance().getForceListener().onUserCancel(UpdateSP.isForced());
                        }
                    } else {
                        Intent intent = new Intent(mContext, DownloadingService.class);
                        intent.putExtra(UpdateConstants.DATA_ACTION, UpdateConstants.START_DOWN);
                        intent.putExtra(UpdateConstants.DATA_UPDATE, mUpdate);
                        mContext.startService(intent);
                        if (UpdateSP.isForced()) {
                            Intent intenta = new Intent(mContext, DownloadDialogActivity.class);
                            startActivity(intenta);
                        } else {
                            if (UpdateHelper.getInstance().getForceListener() != null) {
                                UpdateHelper.getInstance().getForceListener().onUserCancel(UpdateSP.isForced());
                            }
                        }
                        dismiss();
                    }
                } else {
                    Toast.makeText(mContext, "后台下载中...", Toast.LENGTH_SHORT);
                    Intent intent = new Intent(mContext, DownloadingService.class);
                    intent.putExtra(UpdateConstants.DATA_ACTION, UpdateConstants.START_DOWN);
                    intent.putExtra(UpdateConstants.DATA_UPDATE, mUpdate);
                    mContext.startService(intent);
                    if (UpdateSP.isForced()) {
                        Intent intenta = new Intent(mContext, DownloadDialogActivity.class);
                        startActivity(intenta);
                    } else {
                        if (UpdateHelper.getInstance().getForceListener() != null) {
                            UpdateHelper.getInstance().getForceListener().onUserCancel(UpdateSP.isForced());
                        }
                    }
                    dismiss();
                }
            }
        });
    }
}
