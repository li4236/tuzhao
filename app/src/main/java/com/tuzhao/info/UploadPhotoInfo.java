package com.tuzhao.info;

/**
 * Created by juncoder on 2018/6/15.
 */
public class UploadPhotoInfo {

    private String path = "-1";

    private boolean showProgress;

    private String progress;

    private boolean uploadSuccess;

    public UploadPhotoInfo() {

    }

    public UploadPhotoInfo(String path) {
        this.path = path;
        showProgress = true;
        progress = "0%";
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
}
