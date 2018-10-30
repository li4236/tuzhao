package com.tianzhili.www.myselfsdk.SuspensionIndexBar.adpater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.R;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.bean.ParkBean;

import java.util.List;


/**
 * Created by zhangxutong .
 * Date: 16/08/28
 */

public class ParkSelectAdapter extends RecyclerView.Adapter<ParkSelectAdapter.ViewHolder> {
    private List<ParkBean> mDatas;
    private LayoutInflater mInflater;

    public void setItemClickListener(MyItemClickListener itemClickListener) {
        ItemClickListener = itemClickListener;
    }

    private MyItemClickListener ItemClickListener;


    public ParkSelectAdapter(Context mContext, List<ParkBean> mDatas) {
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(mContext);
    }

    public List<ParkBean> getDatas() {
        return mDatas;
    }

    public ParkSelectAdapter setDatas(List<ParkBean> datas) {
        mDatas = datas;
        return this;
    }

    @Override
    public ParkSelectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_city, parent, false));
    }

    @Override
    public void onBindViewHolder(final ParkSelectAdapter.ViewHolder holder, final int position) {
        final ParkBean ParkBean = mDatas.get(position);
        holder.tvCity.setText(ParkBean.getparkStation());

    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCity;
        View content;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCity = (TextView) itemView.findViewById(R.id.tvCity);
            content = itemView.findViewById(R.id.content);
            content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ItemClickListener !=null)
                        ItemClickListener.onItemClick(v,getLayoutPosition());
                }
            });
        }
    }
    public interface MyItemClickListener {
         void onItemClick(View view, int postion);
    }
}
