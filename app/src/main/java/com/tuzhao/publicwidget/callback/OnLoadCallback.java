package com.tuzhao.publicwidget.callback;

/**
 * Created by juncoder on 2018/8/22.
 */
public interface OnLoadCallback<T, E> {

    void onSuccess(T t);

    void onFail(E e);

}
