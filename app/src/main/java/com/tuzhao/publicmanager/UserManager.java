package com.tuzhao.publicmanager;

import android.util.Log;

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
        Log.e("TAG", "setUserInfo: " + userInfo);
        try {
            if (userInfo!=null&&userInfo.getCredit() != null) {
                int credit = Integer.parseInt(userInfo.getCredit());
                int leave_time;
                int early_time;
                String[] st = userInfo.getStage_add_late_time().split(","), et = userInfo.getStage_add_earlt_time().split(",");
                int lv = (int) Math.ceil((credit - 600) / Integer.parseInt(userInfo.getStage()));
                if (credit >= 600) {
                    leave_time = (Integer.parseInt(userInfo.getDefault_late_time()) + lv * Integer.parseInt(userInfo.getAdd_late_time()));
                    early_time = (Integer.parseInt(userInfo.getDefault_early_time()) + lv * Integer.parseInt(userInfo.getAdd_early_time()));
                    if (lv >= 4 && lv < 8) {
                        leave_time = leave_time + Integer.parseInt(st[0]);
                        early_time = early_time + Integer.parseInt(et[0]);
                    } else if (lv >= 8 && lv < 12) {
                        leave_time = leave_time + Integer.parseInt(st[0]) + Integer.parseInt(st[1]);
                        early_time = early_time + Integer.parseInt(et[0]) + Integer.parseInt(et[1]);
                    } else if (lv >= 12 && lv < 16) {
                        leave_time = leave_time + Integer.parseInt(st[0]) + Integer.parseInt(st[1]) + Integer.parseInt(st[2]);
                        early_time = early_time + Integer.parseInt(et[0]) + Integer.parseInt(et[1]) + Integer.parseInt(et[2]);
                    }
                } else {
                    if (credit >= 500) {
                        leave_time = 12;
                        early_time = 8;
                    } else if (credit >= 400) {
                        leave_time = 9;
                        early_time = 5;
                    } else {
                        leave_time = 5;
                        early_time = 3;
                    }
                }
                //userInfo.setLeave_time(leave_time);
                userInfo.setRide_time(early_time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (userInfo == null||userInfo.getToken()==null) {
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

    /**
     * 清除用户信息 the userInfo info
     */
    public void removeUser() {

        this.userInfo = null;
    }

    public String getToken() {
        return userInfo == null ? "" : userInfo.getToken();
    }

}
