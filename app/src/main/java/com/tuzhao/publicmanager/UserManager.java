package com.tuzhao.publicmanager;

import com.tuzhao.info.User_Info;
import com.tuzhao.utils.DateUtil;

/**
 * Created by TZL12 on 2017/5/16.
 * 单例，用来管理用户信息
 */

public class UserManager {

    private static UserManager userManager = null;
    private User_Info userInfo = null;
    private boolean mHasLogin;
    private String mLoginTime;

    public static UserManager getInstance() {

        if (userManager == null) {

            synchronized (UserManager.class) {

                if (userManager == null) {

                    userManager = new UserManager();
                }
                return userManager;
            }
        } else {
            return userManager;
        }
    }

    /**
     * 设置用户信息 userInfo
     */
    public void setUserInfo(User_Info userInfo) {
        if (userInfo == null || userInfo.getToken() == null) {
            mHasLogin = false;
        }
        this.userInfo = userInfo;
    }

    public boolean hasLogined() {
        return mHasLogin;
    }

    public void setHasLogin(boolean hasLogin) {
        mHasLogin = hasLogin;
        if (hasLogin) {
            mLoginTime = DateUtil.getCurrentYearToSecond();
        }
    }

    public String getLoginTime() {
        return mLoginTime;
    }

    /**
     * 获得用户信息 userInfo info
     */
    public User_Info getUserInfo() {
        return userInfo == null ? new User_Info() : userInfo;
    }

    public String getToken() {
        return userInfo == null ? "" : userInfo.getToken();
    }

}
