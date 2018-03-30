package com.tuzhao.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tuzhao.R;
import com.tuzhao.activity.mine.EditParkPicturesActivity;
import com.tuzhao.http.HttpConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TZL12 on 2017/5/9.
 */

public class ParkPicturesAdapter extends RecyclerView.Adapter<ParkPicturesAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<EditParkPicturesActivity.PicBean> mData;
    private OnItemClickListener onItemLongClickListener;
    public List<String> sign_list = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(List<String> sign_data);

        void onItemLongClick();
    }

    public ParkPicturesAdapter(Context context, ArrayList<EditParkPicturesActivity.PicBean> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_parkpictures_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Glide.with(mContext)
                .load(HttpConstants.ROOT_IMG_URL_PS + (mData.get(position).getUrl() == null ? null : mData.get(position).getUrl()))
                .placeholder(R.mipmap.ic_img)
                .error(R.mipmap.ic_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(holder.image_content);

        if (mData.get(position).isdelete()) {
            holder.image_delete.setVisibility(View.VISIBLE);
        } else {
            holder.image_delete.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData.get(position).isdelete()) {
                    if (isExist(sign_list, mData.get(position).getUrl())) {
                        holder.image_delete.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_youhuiweixuan));

                    } else {
                        sign_list.add(mData.get(position).getUrl());
                        holder.image_delete.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_youhuixuanze));
                    }
                    onItemLongClickListener.onItemClick(sign_list);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemLongClickListener.onItemLongClick();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null || mData.isEmpty() ? 0 : mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView image_content, image_delete;
        private View itemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            image_content = (ImageView) itemView.findViewById(R.id.item_parkpictures_layout_image_content);
            image_delete = (ImageView) itemView.findViewById(R.id.item_parkpictures_layout_image_delete);
            this.itemView = itemView;
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    private boolean isExist(List<String> data, String target) {

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).equals(target)) {
                data.remove(i);
                return true;
            }
        }
        return false;
    }
}
