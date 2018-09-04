package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by juncoder on 2018/8/27.
 */
public class UpdateInfo implements Parcelable{

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.downloadUrl);
        dest.writeString(this.versionCode);
        dest.writeString(this.appSize);
        dest.writeString(this.versionName);
        dest.writeString(this.newContent);
        dest.writeString(this.forceUpdateVersion);
        dest.writeString(this.newMd5);
    }

    public UpdateInfo() {
    }

    protected UpdateInfo(Parcel in) {
        this.downloadUrl = in.readString();
        this.versionCode = in.readString();
        this.appSize = in.readString();
        this.versionName = in.readString();
        this.newContent = in.readString();
        this.forceUpdateVersion = in.readString();
        this.newMd5 = in.readString();
    }

    public static final Creator<UpdateInfo> CREATOR = new Creator<UpdateInfo>() {
        @Override
        public UpdateInfo createFromParcel(Parcel source) {
            return new UpdateInfo(source);
        }

        @Override
        public UpdateInfo[] newArray(int size) {
            return new UpdateInfo[size];
        }
    };
}
