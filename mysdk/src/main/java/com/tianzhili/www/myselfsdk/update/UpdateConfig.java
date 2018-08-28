package com.tianzhili.www.myselfsdk.update;

import android.content.Context;
import android.util.Log;

import com.tianzhili.www.myselfsdk.update.bean.Update;
import com.tianzhili.www.myselfsdk.update.type.RequestType;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by TZL12 on 2017/10/30.
 */

public class UpdateConfig {

    private static String checkUrl = "http://119.23.207.14/public/index.php/tianzhili/checkVersion";

    public static void init(Context context) {
        UpdateHelper.init(context);
        Log.e("版本检查更新的数据", "  fsa");
        UpdateHelper.getInstance()
                /*可填：请求方式*/
                .setMethod(RequestType.post)
                .setCheckUrl(checkUrl)
                /*可填：清除旧的自定义布局设置。之前有设置过自定义布局，建议这里调用下*/
                .setClearCustomLayoutSetting()
                /*可填：自定义更新弹出的dialog的布局样式，主要案例中的布局样式里面的id为（jjdxm_update_content、jjdxm_update_id_ok、jjdxm_update_id_cancel）的view类型和id不能修改，其他的都可以修改或删除*/
//                .setDialogLayout(R.layout.custom_update_dialog)
                /*可填：自定义更新状态栏的布局样式，主要案例中的布局样式里面的id为（jjdxm_update_rich_notification_continue、jjdxm_update_rich_notification_cancel、jjdxm_update_title、jjdxm_update_progress_text、jjdxm_update_progress_bar）的view类型和id不能修改，其他的都可以修改或删除*/
//                .setStatusBarLayout(R.layout.custom_download_notification)
                /*可填：自定义强制更新弹出的下载进度的布局样式，主要案例中的布局样式里面的id为(jjdxm_update_iv_icon、jjdxm_update_progress_bar、jjdxm_update_progress_text)的view类型和id不能修改，其他的都可以修改或删除*/
//                .setDialogDownloadLayout(R.layout.custom_download_dialog)
                /*必填：用于从数据更新接口获取的数据response中。解析出Update实例。以便框架内部处理*/
                .setCheckJsonParser(new ParseData() {
                    @Override
                    public Update parse(String response) {
                        Log.e("版本检查更新的数据", response);
                        Update update = new Update();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            /*必填：此apk包的下载地址*/
                            update.setUpdateUrl(jsonObject.optString("downloadurl"));
                            /*必填：此apk包的版本号*/
                            update.setVersionCode(jsonObject.optInt("versioncode"));
                            /*可填：此版本apk包的大小*/
                            update.setApkSize(jsonObject.optLong("appsize"));
                            /*必填：此apk包的版本名称*/
                            update.setVersionName(jsonObject.optString("versionname"));
                            /*可填：此apk包的更新内容*/
                            update.setUpdateContent(jsonObject.optString("newcontent"));
                            /*可填：此apk包是否为强制更新*/
                            update.setForce(!jsonObject.optString("ismustupdate").equals("1"));
                            /*可填：此apk包的md5*/
                            update.setMd5(jsonObject.optString("new_md5"));
                            update.setTime(jsonObject.optString("time"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return update;
                    }
                });
    }
}
