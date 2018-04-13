package com.tuzhao.publicwidget.callback;

import com.tianzhili.www.myselfsdk.okgo.callback.AbsCallback;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/4/13.
 */

public class JsonListCallback<T> extends AbsCallback<T> {

    @Override
    public T convertSuccess(Response response) throws Exception {
        return null;
    }

    @Override
    public void onSuccess(T t, Call call, Response response) {

    }
}
