package com.tuzhao.info;

/**
 * Created by juncoder on 2018/6/15.
 */
public class UploadPhotoInfo {

    private String name;

    private String path = "-1";

    private boolean showProgress;

    private String progress = "0%";

    private boolean uploadSuccess;

    public UploadPhotoInfo() {

    }

    public UploadPhotoInfo(String path) {
        this.path = path;
        showProgress = true;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public boolean isUploadSuccess() {
        return uploadSuccess;
    }

    public void setUploadSuccess(boolean uploadSuccess) {
        this.uploadSuccess = uploadSuccess;
    }

    public String getName() {
        return name;
    }

    public UploadPhotoInfo setName(String name) {
        this.name = name;
        return this;
    }

}
