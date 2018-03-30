package com.tuzhao.publicwidget.loader;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tianzhili.www.myselfsdk.banner.loader.ImageLoader;
import com.tuzhao.R;


public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        //具体方法内容自己去选择，次方法是为了减少banner过多的依赖第三方包，所以将这个权限开放给使用者去选择
        if (path instanceof Integer){
            Glide.with(context)
                    .load(path)
                    .crossFade()
                    .placeholder(R.mipmap.ic_img) //占位图
                    .error(R.mipmap.ic_img)  //出错的占位图
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }else {
            Glide.with(context)
                    .load(path)
                    .crossFade()
                    .placeholder(R.mipmap.ic_img) //占位图
                    .error(R.mipmap.ic_img)  //出错的占位图
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }
    }
}
