package com.tuzhao.utils;

import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by juncoder on 2018/3/27.
 */

public class ImageUtil {

    public static void showPic(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void showPic(ImageView imageView, @DrawableRes int drawableRes) {
        Glide.with(imageView.getContext())
                .load(drawableRes)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void showPic(ImageView imageView, String url, int placeholder) {
        Glide.with(imageView.getContext())
                .load(url)
                .placeholder(placeholder)
                .error(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(imageView);
    }

    public static void showCirclePic(ImageView imageView, int drawableId) {
        Glide.with(imageView.getContext())
                .load(drawableId)
                .crossFade()
                .bitmapTransform(new CropCircleTransformation(imageView.getContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void showCirclePic(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .crossFade()
                .bitmapTransform(new CropCircleTransformation(imageView.getContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void showCirclePic(ImageView imageView, String url, int placeholder) {
        Glide.with(imageView.getContext())
                .load(url)
                .crossFade()
                .placeholder(placeholder)
                .error(placeholder)
                .bitmapTransform(new CropCircleTransformation(imageView.getContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

}
