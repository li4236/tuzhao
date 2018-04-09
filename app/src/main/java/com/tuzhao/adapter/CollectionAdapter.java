package com.tuzhao.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.ChargestationDetailActivity;
import com.tuzhao.activity.ParkspaceDetailActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.CollectionInfo;
import com.tuzhao.publicmanager.CollectionManager;
import com.tuzhao.utils.ImageUtil;

import java.util.List;


/**
 * Created by TZL12 on 2017/5/9.
 */

public class CollectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<CollectionInfo> mData;
    private boolean isEdit;

    public CollectionAdapter(Context mContext, List<CollectionInfo> mData, boolean isEdit) {
        this.mContext = mContext;
        this.mData = mData;
        this.isEdit = isEdit;
    }

    @Override
    public int getItemCount() {
        return mData == null || mData.isEmpty() ? 0 : mData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_collection_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            switch (Integer.parseInt(mData.get(position).getType())) {
                case 1:
                    ImageUtil.showPic(((MyViewHolder) holder).imageview_show,HttpConstants.ROOT_IMG_URL_PS + (mData.get(position).getPimgs_url() != null ? ((mData.get(position).getPimgs_url().split(","))[0]) : "")
                    ,R.mipmap.ic_img);
                    ((MyViewHolder) holder).textview_ps_name.setText(mData.get(position).getPs_name());
                    ((MyViewHolder) holder).textview_address.setText(mData.get(position).getPs_address());
                    ((MyViewHolder) holder).textview_pcount.setText(mData.get(position).getP_count() + "个车位");
                    ((MyViewHolder) holder).textview_pcount.setVisibility(View.VISIBLE);
                    holder.itemView.setTag(position);
                    ((MyViewHolder) holder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            return false;
                        }
                    });
                    break;
                case 2:
                    ImageUtil.showPic(((MyViewHolder) holder).imageview_show,
                            HttpConstants.ROOT_IMG_URL_CS + (mData.get(position).getPimgs_url() != null ? ((mData.get(position).getCimgs_url().split(","))[0]) : ""),
                            R.mipmap.ic_img);
                    ((MyViewHolder) holder).textview_ps_name.setText(mData.get(position).getCs_name());
                    ((MyViewHolder) holder).textview_address.setText(mData.get(position).getCs_address());
                    ((MyViewHolder) holder).textview_pcount.setText(mData.get(position).getC_count() + "个电桩");
                    ((MyViewHolder) holder).textview_pcount.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    ImageUtil.showPic(((MyViewHolder) holder).imageview_show,
                            HttpConstants.ROOT_IMG_URL_CS + (mData.get(position).getPimgs_url() != null ? ((mData.get(position).getPimgs_url().split(","))[0]) : ""),
                            R.mipmap.ic_img);
                    String[] aaa = mData.get(position).getPlace().split(",");
                    ((MyViewHolder) holder).textview_ps_name.setText("标记点" + (position + 1));
                    ((MyViewHolder) holder).textview_address.setText(aaa[2]);
                    ((MyViewHolder) holder).textview_pcount.setVisibility(View.INVISIBLE);
                    break;
            }

            if (isEdit) {
                ((MyViewHolder) holder).imageview_select.setVisibility(View.VISIBLE);
                if (mData.get(position).isSelect()) {
                    ((MyViewHolder) holder).imageview_select.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.ic_youhuixuanze));
                } else {
                    ((MyViewHolder) holder).imageview_select.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.ic_youhuiweixuan));
                }
            } else {
                ((MyViewHolder) holder).imageview_select.setVisibility(View.GONE);
            }

            //为ItemView设置监听器

            ((MyViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEdit) {
                        //编辑删除模式
                        if (mData.get(position).isSelect()) {
                            ((MyViewHolder) holder).imageview_select.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.ic_youhuiweixuan));
                            mData.get(position).setSelect(false);
                            for (CollectionInfo info : CollectionManager.getInstance().getCollection_datas()) {
                                if (mData.get(position).getId().equals(info.getId())) {
                                    info.setSelect(false);
                                }
                            }
                        } else {
                            ((MyViewHolder) holder).imageview_select.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.ic_youhuixuanze));
                            mData.get(position).setSelect(true);
                            for (CollectionInfo info : CollectionManager.getInstance().getCollection_datas()) {
                                if (mData.get(position).getId().equals(info.getId())) {
                                    info.setSelect(true);
                                }
                            }
                        }
                    } else {
                        //正常点击跳转模式
                        if (mData.get(position).getType().equals("1")) {
                            Intent intent = new Intent(mContext, ParkspaceDetailActivity.class);
                            intent.putExtra("parkspace_id", mData.get(position).getParkspace_id());
                            intent.putExtra("city_code", mData.get(position).getCitycode());
                            mContext.startActivity(intent);
                        } else if (mData.get(position).getType().equals("2")) {
                            Intent intent = new Intent(mContext, ChargestationDetailActivity.class);
                            intent.putExtra("chargestation_id", mData.get(position).getChargestation_id());
                            intent.putExtra("city_code", mData.get(position).getCitycode());
                            mContext.startActivity(intent);
                        } else if (mData.get(position).getType().equals("3")) {
                            String[] bbb = mData.get(position).getPlace().split(",");
                            if (bbb.length == 4) {
                                Intent intent = new Intent(mContext, ChargestationDetailActivity.class);
                                intent.putExtra("lat", bbb[0]);
                                intent.putExtra("lon", bbb[1]);
                                intent.putExtra("citycode", bbb[3]);
                                ((Activity) mContext).setResult(1, intent);
                                ((Activity) mContext).finish();
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageview_show, imageview_select;
        private TextView textview_ps_name, textview_pcount, textview_address;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageview_show = (ImageView) itemView.findViewById(R.id.id_item_collection_layout_imageview_show);
            textview_ps_name = (TextView) itemView.findViewById(R.id.id_item_collection_layout_textview_ps_name);
            textview_pcount = (TextView) itemView.findViewById(R.id.id_item_collection_layout_textview_pcount);
            textview_address = (TextView) itemView.findViewById(R.id.id_item_collection_layout_textview_address);
            imageview_select = (ImageView) itemView.findViewById(R.id.id_item_collection_layout_imageview_select);
        }
    }
}
