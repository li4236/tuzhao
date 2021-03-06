
package com.tuzhao.application;


import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.LocalBroadcastManager;

import com.squareup.leakcanary.LeakCanary;
import com.tianzhili.www.myselfsdk.netStateLib.NetStateReceiver;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.okgo.model.HttpParams;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.db.DatabaseImp;
import com.tuzhao.utils.DensityUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.logging.Level;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Response;

import static com.tianzhili.www.myselfsdk.okgo.OkGo.getContext;

/**
 * Created by 82991 on 2017/4/8.
 * 应用全局的Application
 */

public class MyApplication extends MultiDexApplication {

    private static MyApplication mApplication = null;
    private DatabaseImp databaseImp;
    public static boolean SKIP_WELCOME;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        mApplication = this;
        SKIP_WELCOME = false;//跳过启动欢迎见面

        JPushInterface.init(this);//整个应用初始化极光推送，一次就好
        JPushInterface.setDebugMode(true);//true代表为调试模式，可以打印日志

        /*开启网络广播监听*/
        NetStateReceiver.registerNetworkStateReceiver(this);

        OkGo.init(this);

        try {
            //以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
            OkGo.getInstance()

                    // 打开该调试开关,打印级别INFO,并不是异常,是为了显眼,不需要就不要加入该行
                    // 最后的true表示是否打印okgo的内部异常，一般打开方便调试错误
                    .debug("OKGO", Level.INFO, true)
                    .addCommonParams(new HttpParams("base_prove", "tuzhaoapp"))
                    .setHostnameVerifier(new HostnameVertifier())

                    //可以全局统一设置超时重连次数,默认为三次,那么最差的情况会请求4次(一次原始请求,三次重连请求),不需要可以设置为0

                    //如果不想让框架管理cookie（或者叫session的保持）,以下不需要
                    //              .setCookieStore(new MemoryCookieStore())            //cookie使用内存缓存（app退出后，cookie消失）
                    //                    .setCookieStore(new PersistentCookieStore())        //cookie持久化存储，如果cookie不过期，则一直有效

                    //可以设置https的证书,以下几种方案根据需要自己设置
                    .setCertificates(getAssets().open("1_api.toozhao.cn_bundle.crt"));                            //方法一：信任所有证书,不安全有风险
            //              .setCertificates(new SafeTrustManager())            //方法二：自定义信任规则，校验服务端证书
            //              .setCertificates(getAssets().open("srca.cer"))      //方法三：使用预埋证书，校验服务端证书（自签名证书）
            //              //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
            //               .setCertificates(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"))//

            //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
            //               .setHostnameVerifier(new SafeHostnameVerifier())

            //可以添加全局拦截器，不需要就不要加入，错误写法直接导致任何回调不执行
            //                .addInterceptor(new Interceptor() {
            //                    @Override
            //                    public Response intercept(Chain chain) throws IOException {
            //                        return chain.proceed(chain.request());
            //                    }
            //                })

            //这两行同上，不需要就不要加入
            //                    .addCommonHeaders(headers)  //设置全局公共头
            //                    .addCommonParams(params);   //设置全局公共参数


            autoLogin();//自动登录
            LeakCanary.install(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static MyApplication getInstance() {
        return mApplication;
    }

    /**
     * 自动登录
     */
    private void autoLogin() {
        if (!JPushInterface.getRegistrationID(this).equals("")) {
            getDatabaseImp().setRegistrationId(JPushInterface.getRegistrationID(this));
        }
        User_Info user_info = getDatabaseImp().getUserFormDatabase();
        if (user_info != null) {
            //本地数据库有之前登录过的用户信息，则后台自动登录
            if (user_info.getUsername() != null && user_info.getPassword() != null && user_info.getAutologin().equals("1")) {
                requestLogin(user_info.getUsername(), user_info.getPassword());
            }
        }
    }

    private void requestLogin(final String username, String password) {
        OkGo.post(HttpConstants.requestPasswordLogin)//请求数据的接口地址
                .tag(HttpConstants.requestPasswordLogin)//
                .params("telephoneNumber", username)
                .params("password", password)
                .params("registrationId", databaseImp.getRegistrationId())
                .execute(new JsonCallback<Base_Class_Info<User_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<User_Info> responseData, Call call, Response response) {
                        //请求成功,储存用户登录信息
                        User_Info userInfo = responseData.data;
                        userInfo.setAutologin("1");
                        UserManager.getInstance().setUserInfo(userInfo);
                        UserManager.getInstance().setHasLogin(true);
                        //更新覆盖本地数据库的用户信息
                        databaseImp.insertUserToDatabase(userInfo);

                        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent("LOGIN_ACTION"));
                        JPushInterface.setAlias(MyApplication.this, 1, username);//登录成功后给该用户设置极光推送的别名

                        MobclickAgent.onProfileSignIn(username);//友盟在用户登录操作统计
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!DensityUtil.isException(getContext(), e)) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void attachBaseContext(Context base) {
        //我修复重构项目后防止出现一些红色错误
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public DatabaseImp getDatabaseImp() {
        if (databaseImp == null) {
            databaseImp = new DatabaseImp(this);
        }
        return databaseImp;
    }

}


