package com.tuzhao.adapter;

import android.support.annotation.NonNull;
import android.view.View;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseViewHolder;

/**
 * Created by juncoder on 2018/3/28.
 */

public class PauseShareDateAdapter extends BaseAdapter<String> {

    public PauseShareDateAdapter() {
        super();
    }

    @Override
    protected void conver(@NonNull BaseViewHolder holder, String s, final int position) {
        holder.setText(R.id.add_pause_share_date, s);
        holder.getView(R.id.delete_pause_share_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyRemoveData(position);
            }
        });
    }

    @Override
    protected int itemViewId() {
        return R.layout.item_add_pause_share_date;
    }

}
