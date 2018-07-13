package com.tuzhao.activity.jiguang_notification;

/**
 * Created by juncoder on 2018/6/20.
 */
public interface OnLockListener {

    void openSuccess();

    void openFailed();

    void openSuccessHaveCar();

    void closeSuccess();

    void closeFailed();

    void closeFailedHaveCar();

}
