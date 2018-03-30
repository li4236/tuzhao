package com.tuzhao.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.mine.ConsumRecordDetailActivity;
import com.tuzhao.info.ConsumRecordInfo;

import java.util.List;

/**
 * Created by TZL12 on 2017/5/9.
 */

public class ConsumRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ConsumRecordInfo> mData;


    public ConsumRecordAdapter(Context mContext, List<ConsumRecordInfo> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getItemCount() {
        return mData == null || mData.isEmpty() ? 0 : mData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_consumrecord_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        try {
            switch (Integer.parseInt(mData.get(position).getType())){
                case 1:
                    ((MyViewHolder) holder).textview_type.setText("车位收益");
                    ((MyViewHolder) holder).textview_time.setText(mData.get(position).getIncome_time());
                    ((MyViewHolder) holder).textview_amount.setText("+"+mData.get(position).getIncome_amount());
                    break;
                case 2:
                    ((MyViewHolder) holder).textview_type.setText("停车消费");
                    ((MyViewHolder) holder).textview_time.setText(mData.get(position).getPay_time());
                    ((MyViewHolder) holder).textview_amount.setText("-"+mData.get(position).getActual_pay_fee());
                    break;
                case 3:
                    ((MyViewHolder) holder).textview_type.setText("余额提现");
                    ((MyViewHolder) holder).textview_time.setText(mData.get(position).getApplymoney_time());
                    ((MyViewHolder) holder).textview_amount.setText("-"+mData.get(position).getAmount());
                    break;
            }
            if (position == mData.size()-1){
                ((MyViewHolder) holder).imageview_down.setVisibility(View.GONE);
            }else {
                ((MyViewHolder) holder).imageview_down.setVisibility(View.VISIBLE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ConsumRecordDetailActivity.class);
                    intent.putExtra("consumrecord_info",mData.get(position));
                    mContext.startActivity(intent);
                }
            });
        }catch (Exception e){}
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textview_type, textview_time, textview_amount;
        private ImageView imageview_down;

        public MyViewHolder(View itemView) {
            super(itemView);
            textview_type = (TextView) itemView.findViewById(R.id.id_item_consumrecod_layout_textview_type);
            textview_time = (TextView) itemView.findViewById(R.id.id_item_consumrecod_layout_textview_time);
            textview_amount = (TextView) itemView.findViewById(R.id.id_item_consumrecod_layout_textview_amount);
            imageview_down = (ImageView) itemView.findViewById(R.id.id_item_consumrecord_layout_imageview_down);
        }
    }
}
