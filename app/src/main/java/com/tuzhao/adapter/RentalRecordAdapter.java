package com.tuzhao.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.info.RentalRecordItemInfo;

import java.util.List;

/**
 * Created by juncoder on 2018/3/27.
 */

public class RentalRecordAdapter extends BaseAdapter<RentalRecordItemInfo> {

    public RentalRecordAdapter(List<RentalRecordItemInfo> data, RecyclerView recyclerView) {
        super(data, recyclerView);
    }

    @Override
    protected void conver(@NonNull BaseViewHolder holder, RentalRecordItemInfo rentalRecordItemInfo, int position) {
        holder.setText(R.id.rental_record_time_item, rentalRecordItemInfo.getRentalTime())
                .setText(R.id.rental_record_car_number_item, rentalRecordItemInfo.getRentalCarNumber())
                .setText(R.id.rental_record_data_item, rentalRecordItemInfo.getRentalStartDate())
                .setText(R.id.rental_record_earn_item, rentalRecordItemInfo.getRentalFee());
    }

    @Override
    protected int itemViewId() {
        return R.layout.item_rental_record_layout;
    }

}
