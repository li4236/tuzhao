package com.tuzhao.publicwidget.loader;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tianzhili.www.myselfsdk.banner.loader.ImageLoader;
import com.tuzhao.R;
import com.tuzhao.utils.GlideApp;
import com.tuzhao.utils.ImageUtil;


public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        //具体方法内容自己去选择，次方法是为了减少banner过多的依赖第三方包，所以将这个权限开放给使用者去选择
        if (path instanceof Integer){
            ImageUtil.showImpPic(imageView,(Integer)path);
        }else {
            GlideApp.with(imageView.getContext())
                    .load(path)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .error(R.mipmap.ic_img)
                    .placeholder(R.mipmap.ic_img)
                    .into(imageView);
        }
    }
}
