package com.tuzhao.application;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.internal.tls.OkHostnameVerifier;

/**
 * Created by juncoder on 2018/9/25.
 */
public class HostnameVertifier implements HostnameVerifier {

    @Override
    public boolean verify(String hostname, SSLSession session) {
        //验证服务器端域名是否正确
        return OkHostnameVerifier.INSTANCE.verify("api.toozhao.cn", session);
    }

}
