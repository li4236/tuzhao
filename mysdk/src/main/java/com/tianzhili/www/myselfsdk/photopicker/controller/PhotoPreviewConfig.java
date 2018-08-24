package com.tianzhili.www.myselfsdk.photopicker.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tianzhili.www.myselfsdk.R;
import com.tianzhili.www.myselfsdk.photopicker.PhotoPick;
import com.tianzhili.www.myselfsdk.photopicker.bean.Photo;
import com.tianzhili.www.myselfsdk.photopicker.bean.PhotoPreviewBean;
import com.tianzhili.www.myselfsdk.photopicker.ui.PhotoPreviewActivity;

import java.util.ArrayList;


/**
 * Describe : 仿微信图片预览
 * Email:baossrain99@163.com
 * Created by Rain on 17-5-3.
 */
public class PhotoPreviewConfig {

    private static final String TAG = "PhotoPreviewConfig";

    public static final String EXTRA_BUNDLE = "extra_bundle";
    public static final String EXTRA_BEAN = "extra_bean";
    public static final String EXTRA_ORIGINAL_PIC = "original_picture";
    public final static int REQUEST_CODE = 10504;

    public PhotoPreviewConfig(Activity activity, Builder builder) {
        PhotoPreviewBean photoPreviewBean = builder.bean;
        if (photoPreviewBean == null) {
            throw new NullPointerException("Builder#photoPagerBean is null");
        }
        if (photoPreviewBean.getPhotos() == null || photoPreviewBean.getPhotos().isEmpty()) {
            throw new NullPointerException("photos is null or size is 0");
        }
        if (photoPreviewBean.getSelectPhotos() != null && (photoPreviewBean.getSelectPhotos().size() > photoPreviewBean.getMaxPickSize())) {
            throw new IndexOutOfBoundsException("seleced photo size out maxPickSize size,select photo size = " + photoPreviewBean.getSelectPhotos().size() + ",maxPickSize size = " + photoPreviewBean.getMaxPickSize());
        }
        Bundle bundle = new Bundle();
        bundle.putInt(PhotoPickConfig.REQUEST_CODE, builder.requestCode);
        bundle.putParcelable(EXTRA_BEAN, photoPreviewBean);
        startPreviewActivity(activity, bundle);
    }

    private void startPreviewActivity(Activity activity, Bundle bundle) {
        Intent intent = new Intent(activity, PhotoPreviewActivity.class);
        intent.putExtra(EXTRA_BUNDLE, bundle);
        intent.putExtra(PhotoPickConfig.REQUEST_CODE, bundle.getInt(PhotoPickConfig.REQUEST_CODE));
        activity.startActivityForResult(intent, REQUEST_CODE);
        activity.overridePendingTransition(R.anim.image_pager_enter_animation, 0);
    }

    public static class Builder {
        private Activity context;
        private PhotoPreviewBean bean;
        private int requestCode;

        public Builder(Activity context) {
            PhotoPick.checkInit();
            if (context == null) {
                throw new NullPointerException("context is null");
            }
            this.context = context;
            bean = new PhotoPreviewBean();
        }

        public Builder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Builder setPhotoPreviewBean(PhotoPreviewBean bean) {
            this.bean = bean;
            return this;
        }

        public Builder setPosition(int position) {
            if (position < 0) {
                position = 0;
            }
            bean.setPosition(position);
            return this;
        }

        public Builder setPhotos(ArrayList<Photo> photos) {
            if (photos == null || photos.isEmpty()) {
                throw new NullPointerException("photos is null or size is 0");
            }
            bean.setPhotos(photos);
            return this;
        }

        public Builder setSelectPhotos(ArrayList<String> selectPhotos) {
            bean.setSelectPhotos(selectPhotos);
            return this;
        }

        public Builder setOriginalPicture(boolean originalPicture) {//是否设置原图,默认false
            bean.setOriginalPicture(originalPicture);
            return this;
        }

        public Builder setMaxPickSize(int maxPickSize) {
            bean.setMaxPickSize(maxPickSize);
            return this;
        }

        public PhotoPreviewConfig build() {
            return new PhotoPreviewConfig(context, this);
        }

    }
}

/*
 *   ┏┓　　　┏┓
 * ┏┛┻━━━┛┻┓
 * ┃　　　　　　　┃
 * ┃　　　━　　　┃
 * ┃　┳┛　┗┳　┃
 * ┃　　　　　　　┃
 * ┃　　　┻　　　┃
 * ┃　　　　　　　┃
 * ┗━┓　　　┏━┛
 *     ┃　　　┃
 *     ┃　　　┃
 *     ┃　　　┗━━━┓
 *     ┃　　　　　　　┣┓
 *     ┃　　　　　　　┏┛
 *     ┗┓┓┏━┳┓┏┛
 *       ┃┫┫　┃┫┫
 *       ┗┻┛　┗┻┛
 *        神兽保佑
 *        代码无BUG!
 */
