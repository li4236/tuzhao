package com.tuzhao.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.tuzhao.R;
import com.tuzhao.http.HttpConstants;
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

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        final PhotoView photoView = new PhotoView(mContext);
        GlideApp.with(mContext)
                .load(mimgList.get(position))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .placeholder(mimgList.get(position).startsWith(HttpConstants.ROOT_IMG_URL_PSCOM) ? R.mipmap.ic_img : R.mipmap.ic_usericon)
                .error(mimgList.get(position).startsWith(HttpConstants.ROOT_IMG_URL_PSCOM) ? R.mipmap.ic_img : R.mipmap.ic_usericon)
                .into(photoView);

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
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
