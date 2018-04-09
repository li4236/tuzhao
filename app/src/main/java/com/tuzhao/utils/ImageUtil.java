package com.tuzhao.utils;

import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tuzhao.R;

/**
 * Created by juncoder on 2018/3/27.
 */

public class ImageUtil {

    public static void showPic(ImageView imageView, String url) {
        GlideApp.with(imageView.getContext())
                .load(url)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void showPic(ImageView imageView, @DrawableRes int drawableRes) {
        GlideApp.with(imageView.getContext())
                .load(drawableRes)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void showPic(ImageView imageView, String url, int placeholder) {
        GlideApp.with(imageView.getContext())
                .load(url)
                .centerCrop()
                .placeholder(placeholder)
                .error(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void showImpPic(ImageView imageView, String url) {
        GlideApp.with(imageView.getContext())
                .load(url)
                .centerCrop()
                .placeholder(R.mipmap.ic_img)
                .error(R.mipmap.ic_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void showImpPic(ImageView imageView, int url) {
        GlideApp.with(imageView.getContext())
                .load(url)
                .centerCrop()
                .placeholder(R.mipmap.ic_img)
                .error(R.mipmap.ic_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void showCirclePic(ImageView imageView, int drawableId) {
        GlideApp.with(imageView.getContext())
                .load(drawableId)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void showCirclePic(ImageView imageView, String url) {
        GlideApp.with(imageView.getContext())
                .load(url)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void showCirclePic(ImageView imageView, String url, int placeholder) {
        GlideApp.with(imageView.getContext())
                .load(url)
                .circleCrop()
                .placeholder(placeholder)
                .error(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

}
