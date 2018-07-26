package com.tuzhao.publicwidget.callback;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.application.MyApplication;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.User_Info;
import com.tuzhao.publicmanager.UserManager;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by TZL12 on 2018/2/5.
 * token过期失效后的同步登录操作处理
 */

public class TokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        ResponseBody responseBody = response.newBuilder().build().body();
        String body = responseBody.string();
        if (isTokenExpired(body)) {//根据和服务端的约定判断token过期
            //同步请求方式，获取最新的Token
            String newToken = getNewToken();
            if (newToken != null) {
                //使用新的Token，创建新的请求
                Request newRequest = chain.request()
                        .newBuilder()
                        .header("token", newToken)
                        .build();
                //重新请求
                return chain.proceed(newRequest);
            } else {
                return response.newBuilder().body(ResponseBody.create(responseBody.contentType(), body)).build();
            }
        }
        return response.newBuilder().body(ResponseBody.create(responseBody.contentType(), body)).build();
    }

    private boolean isTokenExpired(String body) {
        try {
            JSONObject jsonObject = new JSONObject(body);
            if (jsonObject.getString("code").equals("805")) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 同步请求方式，获取最新的Token
     */
    private String getNewToken() throws IOException {
        // 通过一个特定的接口获取新的token，此处要用到同步的retrofit请求
        User_Info user_info = MyApplication.getInstance().getDatabaseImp().getUserFormDatabase();
        if (user_info != null) {
            Response response = OkGo.post(HttpConstants.requestLogin)
                    .tag(this)
                    .params("username", user_info.getUsername())
                    .params("password", user_info.getPassword())
                    .params("registrationId", MyApplication.getInstance().getDatabaseImp().getRegistrationId())
                    .execute();
            try {
                String reBody = response.body().string();
                JSONObject jsonObject = new JSONObject(reBody);
                JSONObject job = jsonObject.getJSONObject("data");
                UserManager.getInstance().getUserInfo().setToken(job.getString("token"));
                if (job.has("token")) {
                    return job.getString("token");
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
