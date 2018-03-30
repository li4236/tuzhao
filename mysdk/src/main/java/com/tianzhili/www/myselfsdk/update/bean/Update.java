package com.tianzhili.www.myselfsdk.update.bean;

import java.io.Serializable;

/**
 */
public class Update implements Serializable {

    /**
     * original data.
     */
    private String original;
    /**
     * update content,
     */
    private String updateContent;
    /**
     * update checkUrl
     */
    private String updateUrl;
    /**
     * update code
     */
    private int versionCode;
    /**
     * update name
     */
    private String versionName;
    /**
     * update md5
     */
    private String md5;
    /**
     * update name
     */
    private long apkSize;
    /**
     * update force
     */
    private boolean force;

    private String time;

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
