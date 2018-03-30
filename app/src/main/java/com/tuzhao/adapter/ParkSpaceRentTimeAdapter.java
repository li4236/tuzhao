package com.tuzhao.adapter;

import android.support.annotation.NonNull;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseViewHolder;

/**
 * Created by juncoder on 2018/3/28.
 */

public class ParkSpaceRentTimeAdapter extends BaseAdapter<String> {

    @Override
    protected void conver(@NonNull BaseViewHolder holder, String s, int position) {
        holder.setText(R.id.common_tv, s);
    }

    @Override
    protected int itemViewId() {
        return R.layout.item_common_text_layout;
    }

}
