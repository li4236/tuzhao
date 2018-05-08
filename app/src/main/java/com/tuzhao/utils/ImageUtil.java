package com.tuzhao.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.tuzhao.R;
import com.tuzhao.http.HttpConstants;

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

    public static void showNoCenterPic(ImageView imageView, int resourceId) {
        GlideApp.with(imageView.getContext())
                .load(resourceId)
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

    public static void showCircleImgPic(ImageView imageView, String url) {
        GlideApp.with(imageView.getContext())
                .load(url)
                .circleCrop()
                .centerCrop()
                .placeholder(R.mipmap.ic_img)
                .error(R.mipmap.ic_img)
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

    /**
     * 显示带圆角的图片
     */
    public static void showRoundPic(final ImageView imageView, int drawableId) {
        GlideApp.with(imageView.getContext())
                .load(drawableId)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new GlideLoadListener(imageView, drawableId))
                .into(new GlideTarget(imageView));
    }

    /**
     * 显示带圆角的图片
     */
    public static void showRoundPic(final ImageView imageView, String url) {
        GlideApp.with(imageView.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new GlideLoadListener(imageView))
                .into(new GlideTarget(imageView));
    }

    /**
     * 显示带圆角的图片
     */
    public static void showRoundPic(final ImageView imageView, String url, int drawableId) {
        GlideApp.with(imageView.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new GlideLoadListener(imageView, drawableId))
                .into(new GlideTarget(imageView));
    }

    public static void showUserRouindPic(final ImageView imageView, String url) {
        GlideApp.with(imageView.getContext())
                .load(HttpConstants.ROOT_IMG_URL_USER + url)
                .centerCrop()
                .placeholder(R.mipmap.ic_usericon)
                .error(R.mipmap.ic_usericon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        imageView.setImageBitmap(createRoundBitmapByXfermode(((BitmapDrawable) resource).getBitmap(), imageView.getWidth(),
                                imageView.getHeight(), DensityUtil.dp2px(imageView.getContext(), 4)));
                        //ViewCompat.setElevation(imageView, 4);
                    }
                });
    }

    /**
     * 利用Xfermode绘制圆角图片
     *
     * @param bitmap    待处理图片
     * @param outWidth  结果图片宽度，一般为控件的宽度
     * @param outHeight 结果图片高度，一般为控件的高度
     * @param radius    圆角半径大小
     * @return 结果图片
     */
    private static Bitmap createRoundBitmapByXfermode(Bitmap bitmap, int outWidth, int outHeight, int radius) {
        if (bitmap == null) {
            throw new NullPointerException("Bitmap can't be null");
        }

        // 等比例缩放拉伸
        float widthScale = outWidth * 1.0f / bitmap.getWidth();
        float heightScale = outHeight * 1.0f / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(widthScale, heightScale);
        Bitmap newBt = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        // 初始化目标bitmap
        Bitmap targetBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        canvas.drawARGB(0, 0, 0, 0);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        RectF rectF = new RectF(0f, 0f, (float) outWidth, (float) outHeight);

        // 在画布上绘制圆角图
        canvas.drawRoundRect(rectF, radius, radius, paint);

        // 设置叠加模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        // 在画布上绘制原图片
        Rect ret = new Rect(0, 0, outWidth, outHeight);
        canvas.drawBitmap(newBt, ret, ret, paint);

        return targetBitmap;
    }

    private static class GlideLoadListener implements RequestListener<Drawable> {

        private ImageView mImageView;

        private int mDrawablId;

        GlideLoadListener(ImageView imageView) {
            mImageView = imageView;
        }

        GlideLoadListener(ImageView imageView, int drawablId) {
            mImageView = imageView;
            mDrawablId = drawablId;
        }

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            mImageView.setImageBitmap(createRoundBitmapByXfermode(BitmapFactory.decodeResource(mImageView.getResources(), mDrawablId == 0 ?
                            R.mipmap.ic_usericon : mDrawablId)
                    , mImageView.getWidth(), mImageView.getHeight(), DensityUtil.dp2px(mImageView.getContext(), 4)));
            return true;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            return false;
        }
    }

    private static class GlideTarget extends SimpleTarget<Drawable> {

        private ImageView mImageView;

        GlideTarget(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
            mImageView.setImageBitmap(createRoundBitmapByXfermode(((BitmapDrawable) resource).getBitmap(), mImageView.getWidth(), mImageView.getHeight(),
                    DensityUtil.dp2px(mImageView.getContext(), 4)));
        }

    }

}
