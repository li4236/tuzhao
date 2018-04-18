package com.tuzhao.activity.base;

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.tuzhao.utils.ImageUtil;

import java.util.HashSet;

/**
 * Created by juncoder on 2018/3/27.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;

    private View mItemView;

    private BaseAdapter mBaseAdapter;

    private HashSet<Integer> mChildClickIds;

    BaseViewHolder(View itemView) {
        super(itemView);
        mItemView = itemView;
        mViews = new SparseArray<>();
        mChildClickIds = new HashSet<>();
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBaseAdapter.getOnItemClickListener() != null) {
                    mBaseAdapter.getOnItemClickListener().onItemClick(mItemView, getAdapterPosition() - mBaseAdapter.getHeadViewCount());
                }
            }
        });

        for (Integer integer : mChildClickIds) {
            final View view = getView(integer);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBaseAdapter.getOnItemClickListener() != null) {
                        Log.e("tag", "onClick: " + getAdapterPosition());
                        mBaseAdapter.getOnItemClickListener().onItemClick(view, getAdapterPosition() - mBaseAdapter.getHeadViewCount());
                    }
                }
            });
        }
    }

    public <T extends View> T getView(@IdRes int id) {
        putView(id);
        return (T) mViews.get(id);
    }

    private void putView(@IdRes int id) {
        if (mViews.get(id) == null) {
            mViews.put(id, mItemView.findViewById(id));
        }
    }

    public BaseViewHolder setText(@IdRes int id, String text) {
        ((TextView) getView(id)).setText(text);
        return this;
    }

    public BaseViewHolder setRadioCheck(@IdRes int id, boolean check) {
        ((RadioButton) getView(id)).setChecked(check);
        return this;
    }

    public BaseViewHolder setCheckboxCheck(@IdRes int id, boolean check) {
        ((CheckBox) getView(id)).setChecked(check);
        return this;
    }

    public BaseViewHolder showPic(@IdRes int id, @DrawableRes int drawableRes) {
        ImageUtil.showPic((ImageView) getView(id), drawableRes);
        return this;
    }

    public BaseViewHolder showPic(@IdRes int id, String url) {
        ImageUtil.showPic((ImageView) getView(id), url);
        return this;
    }

    public BaseViewHolder showPic(@IdRes int id, String url, int drawableId) {
        ImageUtil.showPic((ImageView) getView(id), url, drawableId);
        return this;
    }

    public BaseViewHolder showCirclePic(@IdRes int id, String url) {
        ImageUtil.showCirclePic((ImageView) getView(id), url);
        return this;
    }

    private int getClickPosition() {
        if (getLayoutPosition() > mBaseAdapter.getHeadViewCount()) {
            return getLayoutPosition() - mBaseAdapter.getHeadViewCount();
        }
        return 0;
    }

    public void setBaseAdapter(BaseAdapter baseAdapter) {
        mBaseAdapter = baseAdapter;
    }

    public void addChildClickId(@IdRes int id) {
        mChildClickIds.add(id);
    }

}
