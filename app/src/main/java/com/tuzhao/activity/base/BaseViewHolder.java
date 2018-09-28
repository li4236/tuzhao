package com.tuzhao.activity.base;

import android.support.annotation.ColorInt;
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

import com.tuzhao.R;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.publicwidget.customView.CircleImageView;
import com.tuzhao.utils.ImageUtil;

/**
 * Created by juncoder on 2018/3/27.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "BaseViewHolder";

    private SparseArray<View> mViews;

    private View mItemView;

    BaseViewHolder(View itemView) {
        super(itemView);
        mItemView = itemView;
        mViews = new SparseArray<>();
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

    public BaseViewHolder appendText(@IdRes int id, String text) {
        ((TextView) getView(id)).append(text);
        return this;
    }

    public BaseViewHolder setTextColor(@IdRes int id, int color) {
        ((TextView) getView(id)).setTextColor(color);
        return this;
    }

    public BaseViewHolder setBackground(@IdRes int id, @DrawableRes int drawableRes) {
        getView(id).setBackgroundResource(drawableRes);
        return this;
    }

    public BaseViewHolder setBackgroundColor(@IdRes int id, @ColorInt int color) {
        getView(id).setBackgroundColor(color);
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

    public BaseViewHolder setCustomCheckboxCheck(@IdRes int id, boolean check) {
        ((com.tuzhao.publicwidget.customView.CheckBox) getView(id)).setChecked(check);
        return this;
    }

    public BaseViewHolder showPic(@IdRes int id, @DrawableRes int drawableRes) {
        ImageUtil.showPic((ImageView) getView(id), drawableRes);
        return this;
    }

    public BaseViewHolder showPic(@IdRes int id, String url) {
        if (url == null || url.equals("")) {
            Log.e(TAG, "showPic: url is null or empty");
            return this;
        }

        ImageUtil.showPic((ImageView) getView(id), url);
        return this;
    }

    public BaseViewHolder showPic(@IdRes int id, String url, int drawableId) {
        ImageUtil.showPic((ImageView) getView(id), url, drawableId);
        return this;
    }

    public BaseViewHolder showImpPic(@IdRes int id, String url) {
        ImageUtil.showImgPic((ImageView)getView(id), url);
        return this;
    }

    public BaseViewHolder showPicWithNoAnimate(@IdRes int id, @DrawableRes int drawableId) {
        ImageUtil.showPicWithNoAnimate((ImageView) getView(id), drawableId);
        return this;
    }

    public BaseViewHolder showCirclePic(@IdRes int id, String url) {
        if (url == null || url.equals("")) {
            Log.e(TAG, "showCirclePic: url is null or empty");
            return this;
        }

        ImageUtil.showCirclePic((ImageView) getView(id), url);
        return this;
    }

    public BaseViewHolder showCircleUserPic(@IdRes int id, String url) {
        if (url == null || url.equals("")) {
            Log.e(TAG, "showCircleUserPic: url is null or empty");
            return this;
        }

        ImageUtil.showPic((CircleImageView) getView(id), HttpConstants.ROOT_IMG_URL_USER + url, R.mipmap.ic_usericon);
        return this;
    }

    public BaseViewHolder showView(@IdRes int id) {
        if (getView(id).getVisibility() != View.VISIBLE) {
            getView(id).setVisibility(View.VISIBLE);
        }
        return this;
    }

    public BaseViewHolder hideView(@IdRes int id) {
        if (getView(id).getVisibility() != View.INVISIBLE) {
            getView(id).setVisibility(View.INVISIBLE);
        }
        return this;
    }

    public BaseViewHolder goneView(@IdRes int id) {
        if (getView(id).getVisibility() != View.GONE) {
            getView(id).setVisibility(View.GONE);
        }
        return this;
    }

    public BaseViewHolder showViewOrHide(@IdRes int id, boolean show) {
        View view = getView(id);
        if (show) {
            if (view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
            }
        } else {
            if (view.getVisibility() != View.INVISIBLE) {
                view.setVisibility(View.INVISIBLE);
            }
        }
        return this;
    }

    public BaseViewHolder setOnClickListener(@IdRes int id, View.OnClickListener onClickListener) {
        getView(id).setOnClickListener(onClickListener);
        return this;
    }

}
