package com.tianzhili.www.myselfsdk.update;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.tianzhili.www.myselfsdk.update.bean.Update;
import com.tianzhili.www.myselfsdk.update.download.IUpdateExecutor;
import com.tianzhili.www.myselfsdk.update.download.OnlineCheckWorker;
import com.tianzhili.www.myselfsdk.update.download.UpdateExecutor;
import com.tianzhili.www.myselfsdk.update.download.UpdateNoUrlWorker;
import com.tianzhili.www.myselfsdk.update.download.UpdateWorker;
import com.tianzhili.www.myselfsdk.update.listener.OnlineCheckListener;
import com.tianzhili.www.myselfsdk.update.listener.UpdateListener;
import com.tianzhili.www.myselfsdk.update.server.DownloadingService;
import com.tianzhili.www.myselfsdk.update.type.RequestType;
import com.tianzhili.www.myselfsdk.update.type.UpdateType;
import com.tianzhili.www.myselfsdk.update.util.UpdateConstants;
import com.tianzhili.www.myselfsdk.update.view.UpdateDialog;

public class UpdateAgent {
    private static UpdateAgent updater;
    private IUpdateExecutor executor;
    private UpdateDialog updateDialog;

    private UpdateAgent() {
        executor = UpdateExecutor.getInstance();
    }

    public static UpdateAgent getInstance() {
        if (updater == null) {
            updater = new UpdateAgent();
        }
        return updater;
    }


    /**
     * check out whether or not there is a new version on internet
     *
     * @param activity The activity who need to show update dialog
     */
    public void onlineCheck(Activity activity) {
        OnlineCheckWorker checkWorker = new OnlineCheckWorker();
        RequestType requestType = UpdateHelper.getInstance().getRequestMethod();
        checkWorker.setRequestMethod(requestType);
        checkWorker.setUrl(UpdateHelper.getInstance().getOnlineUrl());
        checkWorker.setParams(UpdateHelper.getInstance().getOnlineParams());
        checkWorker.setParser(UpdateHelper.getInstance().getOnlineJsonParser());

        final OnlineCheckListener mOnlinelistener = UpdateHelper.getInstance().getOnlineCheckListener();
        checkWorker.setUpdateListener(new OnlineCheckListener() {

            @Override
            public void hasParams(String value) {
                if (mOnlinelistener != null) {
                    mOnlinelistener.hasParams(value);
                }
            }
        });
        executor.onlineCheck(checkWorker);

    }

    /**
     * check out whether or not there is a new version on internet
     *
     * @param activity The activity who need to show update dialog
     */
    public void checkUpdate(final Activity activity) {
        UpdateWorker checkWorker = new UpdateWorker();
        RequestType requestType = UpdateHelper.getInstance().getRequestMethod();
        checkWorker.setRequestMethod(requestType);
        checkWorker.setUrl(UpdateHelper.getInstance().getCheckUrl());
        checkWorker.setParams(UpdateHelper.getInstance().getCheckParams());
        checkWorker.setParser(UpdateHelper.getInstance().getCheckJsonParser());

        final UpdateListener mUpdate = UpdateHelper.getInstance().getUpdateListener();
        checkWorker.setUpdateListener(new UpdateListener() {
            @Override
            public void hasUpdate(Update update) {
                if (mUpdate != null) {
                    mUpdate.hasUpdate(update);
                }
                if (UpdateHelper.getInstance().getUpdateType() == UpdateType.autowifidown) {
                    Intent intent = new Intent(activity, DownloadingService.class);
                    intent.putExtra(UpdateConstants.DATA_ACTION, UpdateConstants.START_DOWN);
                    intent.putExtra(UpdateConstants.DATA_UPDATE, update);
                    activity.startService(intent);
                    return;
                }
                check(activity,update,UpdateConstants.UPDATE_TIE,null,true);
//                Intent intent = new Intent(activity, UpdateDialogActivity.class);
//                intent.putExtra(UpdateConstants.DATA_UPDATE, update);
//                intent.putExtra(UpdateConstants.DATA_ACTION, UpdateConstants.UPDATE_TIE);
//                intent.putExtra(UpdateConstants.START_TYPE, true);
//                activity.startActivity(intent);
            }

            @Override
            public void noUpdate() {
                if (mUpdate != null) {
                    mUpdate.noUpdate();
                }
            }

            @Override
            public void onCheckError(int code, String errorMsg) {
                if (mUpdate != null) {
                    mUpdate.onCheckError(code, errorMsg);
                }
            }

            @Override
            public void onUserCancel() {
                if (mUpdate != null) {
                    mUpdate.onUserCancel();
                }
            }

            @Override
            public void onUserCancelDowning() {
                if (mUpdate != null) {
                    mUpdate.onUserCancelDowning();
                }
            }

            @Override
            public void onUserCancelInstall() {
                if (mUpdate != null) {
                    mUpdate.onUserCancelDowning();
                }
            }
        });
        executor.check(checkWorker);
    }

    public void checkNoUrlUpdate(final Activity activity) {
        UpdateNoUrlWorker checkWorker = new UpdateNoUrlWorker();
        checkWorker.setRequestResultData(UpdateHelper.getInstance().getRequestResultData());
        checkWorker.setParser(UpdateHelper.getInstance().getCheckJsonParser());

        final UpdateListener mUpdate = UpdateHelper.getInstance().getUpdateListener();
        checkWorker.setUpdateListener(new UpdateListener() {
            @Override
            public void hasUpdate(Update update) {
                if (mUpdate != null) {
                    mUpdate.hasUpdate(update);
                }
                if (UpdateHelper.getInstance().getUpdateType() == UpdateType.autowifidown) {
                    Intent intent = new Intent(activity, DownloadingService.class);
                    intent.putExtra(UpdateConstants.DATA_ACTION, UpdateConstants.START_DOWN);
                    intent.putExtra(UpdateConstants.DATA_UPDATE, update);
                    activity.startService(intent);
                    return;
                }
                check(activity,update,UpdateConstants.UPDATE_TIE,null,true);
//                Intent intent = new Intent(activity, UpdateDialogActivity.class);
//                intent.putExtra(UpdateConstants.DATA_UPDATE, update);
//                intent.putExtra(UpdateConstants.DATA_ACTION, UpdateConstants.UPDATE_TIE);
//                intent.putExtra(UpdateConstants.START_TYPE, true);
//                activity.startActivity(intent);
            }

            @Override
            public void noUpdate() {
                if (mUpdate != null) {
                    mUpdate.noUpdate();
                }
            }

            @Override
            public void onCheckError(int code, String errorMsg) {
                if (mUpdate != null) {
                    mUpdate.onCheckError(code, errorMsg);
                }
            }

            @Override
            public void onUserCancel() {
                if (mUpdate != null) {
                    mUpdate.onUserCancel();
                }
            }

            @Override
            public void onUserCancelDowning() {
                if (mUpdate != null) {
                    mUpdate.onUserCancelDowning();
                }
            }

            @Override
            public void onUserCancelInstall() {
                if (mUpdate != null) {
                    mUpdate.onUserCancelDowning();
                }
            }
        });
        executor.checkNoUrl(checkWorker);
    }

    public void check(Context context, Update info, int action, String path, boolean isactivityEnter) {
        if (context!=null){
            updateDialog = new UpdateDialog(context,info,action,path,isactivityEnter);
            updateDialog.show(((AppCompatActivity)context).getSupportFragmentManager(), "hahah");
        }
    }
}
