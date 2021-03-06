package com.tuzhao.publicwidget.loader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tianzhili.www.myselfsdk.banner.loader.ImageLoader;
import com.tuzhao.R;
import com.tuzhao.application.MyApplication;
import com.tuzhao.utils.GlideApp;


public class GlideImageLoader extends ImageLoader implements com.tianzhili.www.myselfsdk.photopicker.loader.ImageLoader {

    private boolean mClearMemoryCache = true;

    public GlideImageLoader() {

    }

    public GlideImageLoader(boolean clearMemoryCache) {
        mClearMemoryCache = clearMemoryCache;
    }

    @Override
    public void displayImage(Context context, Object path, final ImageView imageView) {
        //具体方法内容自己去选择，次方法是为了减少banner过多的依赖第三方包，所以将这个权限开放给使用者去选择
        GlideApp.with(imageView.getContext())
                .load(path)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        imageView.setScaleType(ImageView.ScaleType.CENTER);
                        imageView.setImageResource(R.drawable.ic_logo_p);
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        return false;
                    }
                })
                .into(imageView);
    }

    @Override
    public void displayImage(Context context, String path, final ImageView imageView, boolean resize) {
        if (resize) {
            displayImage(context, path, imageView);
        } else {
            GlideApp.with(context)
                    .load(path)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(imageView);
        }
    }

    @Override
    public void clearMemoryCache() {
        if (mClearMemoryCache) {
            GlideApp.get(MyApplication.getInstance()).clearMemory();
        }
    }

}
