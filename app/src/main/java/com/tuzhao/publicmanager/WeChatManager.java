package com.tuzhao.publicmanager;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tuzhao.application.MyApplication;

/**
 * Created by TZL12 on 2018/3/14.
 */

public class WeChatManager {

    private static WeChatManager weChatManager = null;

    private static final String APP_ID = "wxb68fabefc83d5c48";    //这个APP_ID就是注册APP的时候生成的

    public IWXAPI api;      //这个对象是专门用来向微信发送数据的一个重要接口,使用强引用持有,所有的信息发送都是基于这个对象的

    private WeChatManager() {
        api = WXAPIFactory.createWXAPI(MyApplication.getInstance(), APP_ID, true);
        api.registerApp(APP_ID);
    }

    public static WeChatManager getInstance() {
        if (weChatManager == null) {
            synchronized (WeChatManager.class) {
                if (weChatManager == null) {
                    weChatManager = new WeChatManager();
                }
            }
        }
        return weChatManager;
    }


}
