package com.tuzhao.activity.mine;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.GlideApp;

/**
 * Created by juncoder on 2018/5/15.
 */

public class PhotoActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_layout);
        if (!getIntent().hasExtra(ConstansUtil.PHOTO_IMAGE)) {
            finish();
        } else {
            setStyle(false);
            final PhotoView photoView = findViewById(R.id.photo_view);
            GlideApp.with(this)
                    .load(getIntent().getStringExtra(ConstansUtil.PHOTO_IMAGE))
                    .placeholder(R.mipmap.ic_img)
                    .error(R.mipmap.ic_img)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            photoView.setImageDrawable(resource);
                        }
                    });
        }
    }

}
