package com.tuzhao.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.tuzhao.R;
import com.tuzhao.utils.GlideApp;

import java.util.ArrayList;


/**
 * Created by TZL12 on 2017/7/28.
 */

public class PicturePagerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<String> mimgList;
    private Activity mActivity;

    public PicturePagerAdapter(Context context, ArrayList<String> imgList, Activity activity) {
        mContext = context;
        mimgList = imgList;
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return mimgList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        final PhotoView photoView = new PhotoView(mContext);
        GlideApp.with(mContext)
                .load(mimgList.get(position))
                .placeholder(R.mipmap.ic_img)
                .error(R.mipmap.ic_img)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        photoView.setImageDrawable(resource);
                    }
                });
        photoView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                mActivity.finish();
            }
        });
        container.addView(photoView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
