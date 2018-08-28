package com.tuzhao.info;

/**
 * Created by juncoder on 2018/8/27.
 */
public class UpdateInfo {

    private String downloadUrl;

    private String versionCode;

    private String appSize;

    private String versionName;

    private String newContent;

    private String forceUpdateVersion;

    private String newMd5;

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getNewContent() {
        return newContent;
    }

    public void setNewContent(String newContent) {
        this.newContent = newContent;
    }

    public String getForceUpdateVersion() {
        return forceUpdateVersion;
    }

    public void setForceUpdateVersion(String forceUpdateVersion) {
        this.forceUpdateVersion = forceUpdateVersion;
    }

    public String getNewMd5() {
        return newMd5;
    }

    public void setNewMd5(String newMd5) {
        this.newMd5 = newMd5;
    }
}
