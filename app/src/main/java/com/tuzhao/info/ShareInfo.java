package com.tuzhao.info;

/**
 * Created by juncoder on 2018/10/31.
 */
public class ShareInfo {

    private String mWebpageUrl;

    private String mTitle;

    private String mDescription;

    /**
     * 小程序页面路径
     */
    private String mPath;

    public String getWebpageUrl() {
        return mWebpageUrl;
    }

    public void setWebpageUrl(String webpageUrl) {
        mWebpageUrl = webpageUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    @Override
    public String toString() {
        return "ShareInfo{" +
                "mWebpageUrl='" + mWebpageUrl + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mPath='" + mPath + '\'' +
                '}';
    }

}
