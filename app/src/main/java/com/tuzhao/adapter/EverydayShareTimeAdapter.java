package com.tuzhao.adapter;

import android.support.annotation.NonNull;
import android.view.View;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.info.EverydayShareTimeInfo;
import com.tuzhao.utils.DateUtil;

/**
 * Created by juncoder on 2018/3/28.
 */

public class EverydayShareTimeAdapter extends BaseAdapter<EverydayShareTimeInfo> {

    public EverydayShareTimeAdapter() {
        super();
    }

    @Override
    protected void conver(@NonNull BaseViewHolder holder, EverydayShareTimeInfo everydayShareTimeInfo, final int poisition) {
        holder.setText(R.id.add_everyday_share_start_time, DateUtil.getHourWithMinutes(everydayShareTimeInfo.getStartTime()))
                .setText(R.id.add_everyday_share_end_time, DateUtil.getHourWithMinutes(everydayShareTimeInfo.getEndTime()))
                .getView(R.id.delete_everyday_share_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyRemoveData(poisition);
            }
        });

    }

    @Override
    protected int itemViewId() {
        return R.layout.item_add_everyday_share_time;
    }

}
