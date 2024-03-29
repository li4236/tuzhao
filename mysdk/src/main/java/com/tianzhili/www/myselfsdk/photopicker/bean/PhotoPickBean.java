package com.tianzhili.www.myselfsdk.photopicker.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.tianzhili.www.myselfsdk.photopicker.loader.ImageLoader;


/**
 * Descriptions 照片选择器Bean类
 * GitHub : https://github.com/Rain0413
 * Blog   : http://blog.csdn.net/sinat_33680954
 * Created by Rain on 16-12-7.
 */
public class PhotoPickBean implements Parcelable {

    private int maxPickSize;            //最多可以选择多少张图片
    private int pickMode;               //单选还是多选
    private int spanCount;              //recyclerView有多少列
    private boolean showClipCircle;     //圆形裁剪方式
    private boolean showCamera;         //是否展示拍照icon
    private boolean clipPhoto;          //是否启动裁剪图片
    private boolean originalPicture;    //是否选择的是原图
    private ImageLoader imageLoader;    //加载方式

    public PhotoPickBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.maxPickSize);
        dest.writeInt(this.pickMode);
        dest.writeInt(this.spanCount);
        dest.writeInt(this.showClipCircle ? (byte) 1 : (byte) 0);
        dest.writeByte(this.showCamera ? (byte) 1 : (byte) 0);
        dest.writeByte(this.clipPhoto ? (byte) 1 : (byte) 0);
        dest.writeByte(this.originalPicture ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.imageLoader);
    }

    protected PhotoPickBean(Parcel in) {
        this.maxPickSize = in.readInt();
        this.pickMode = in.readInt();
        this.spanCount = in.readInt();
        this.showClipCircle = in.readByte() != 0;
        this.showCamera = in.readByte() != 0;
        this.clipPhoto = in.readByte() != 0;
        this.originalPicture = in.readByte() != 0;
        this.imageLoader = (ImageLoader) in.readSerializable();
    }

    public static final Creator<PhotoPickBean> CREATOR = new Creator<PhotoPickBean>() {
        @Override
        public PhotoPickBean createFromParcel(Parcel source) {
            return new PhotoPickBean(source);
        }

        @Override
        public PhotoPickBean[] newArray(int size) {
            return new PhotoPickBean[size];
        }
    };

    public int getMaxPickSize() {
        return maxPickSize;
    }

    public void setMaxPickSize(int maxPickSize) {
        this.maxPickSize = maxPickSize;
    }

    public int getPickMode() {
        return pickMode;
    }

    public void setPickMode(int pickMode) {
        this.pickMode = pickMode;
    }

    public int getSpanCount() {
        return spanCount;
    }

    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public boolean isClipPhoto() {
        return clipPhoto;
    }

    public void setClipPhoto(boolean clipPhoto) {
        this.clipPhoto = clipPhoto;
    }

    public boolean isOriginalPicture() {
        return originalPicture;
    }

    public boolean getClipMode() {
        return showClipCircle;
    }

    public void setClipMode(boolean showClipCircle) {
        this.showClipCircle = showClipCircle;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public void setOriginalPicture(boolean originalPicture) {
        this.originalPicture = originalPicture;
    }

}
