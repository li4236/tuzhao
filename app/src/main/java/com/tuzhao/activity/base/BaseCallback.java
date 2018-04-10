package com.tuzhao.activity.base;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/4/10.
 */

public interface BaseCallback<T> {

    void onSuccess(T t, Call call, Response response);

    void onError(Call call, Response response, Exception e);

}
