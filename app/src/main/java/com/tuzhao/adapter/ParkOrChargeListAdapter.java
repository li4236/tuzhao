package com.tuzhao.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cb.ratingbar.CBRatingBar;
import com.tuzhao.R;
import com.tuzhao.activity.ChargestationDetailActivity;
import com.tuzhao.activity.ParkspaceDetailActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.NearPointPCInfo;
import com.tuzhao.publicmanager.LocationManager;
import com.tuzhao.utils.DateUtil;

import java.util.List;

/**
 * Created by TZL12 on 2017/5/9.
 */

public class ParkOrChargeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<NearPointPCInfo> mData;
    private DateUtil dateUtil = new DateUtil();

    public ParkOrChargeListAdapter(Context mContext, List<NearPointPCInfo> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getItemCount() {
        return mData == null || mData.isEmpty() ? 0 : mData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_parkorcharge_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        try {
            ((MyViewHolder) holder).textview_name.setText(mData.get(position).getName());
            ((MyViewHolder) holder).textview_grade.setText(mData.get(position).getGrade()+"");
            ((MyViewHolder) holder).textview_price.setText(mData.get(position).getPrice()+"");
            ((MyViewHolder) holder).textview_address.setText(mData.get(position).getAddress());
            ((MyViewHolder) holder).cbratingbar.setStarProgress((float) (mData.get(position).getGrade()*100/5));
            if (LocationManager.getInstance().hasLocation()){
                DateUtil.DistanceAndDanwei distanceAndDanwei = dateUtil.isMoreThan1000((int) AMapUtils.calculateLineDistance(new LatLng(mData.get(position).getLatitude(), mData.get(position).getLongitude()), new LatLng(LocationManager.getInstance().getmAmapLocation().getLatitude(), LocationManager.getInstance().getmAmapLocation().getLongitude())));
                ((MyViewHolder) holder).textview_distance.setText(distanceAndDanwei.getDistance()+distanceAndDanwei.getDanwei());
            }else {
                ((MyViewHolder) holder).textview_distance.setText("未知");
            }

            switch (Integer.parseInt(mData.get(position).getIsparkspace())){
                case 1:
                    ((MyViewHolder) holder).textview_price1.setText(" 元/小时");
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ParkspaceDetailActivity.class);
                            intent.putExtra("parkspace_id", mData.get(position).getId());
                            intent.putExtra("city_code", mData.get(position).getCity_code());
                            mContext.startActivity(intent);
                        }
                    });
                    break;
                case 0:
                    ((MyViewHolder) holder).textview_price1.setText(" 元/度");
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ChargestationDetailActivity.class);
                            intent.putExtra("chargestation_id",mData.get(position).getId());
                            intent.putExtra("city_code",mData.get(position).getCity_code());
                            mContext.startActivity(intent);
                        }
                    });
                    break;
            }
            String[] urlList = mData.get(position).getPicture().split(",");
            Glide.with(mContext)
                    .load(urlList.length>0? HttpConstants.ROOT_IMG_URL_PS + urlList[0]:"")
                    .placeholder(R.mipmap.ic_img) //占位图
                    .error(R.mipmap.ic_img)  //出错的占位图
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .into(((MyViewHolder) holder).imageview_bigshow);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textview_name, textview_grade, textview_price,textview_price1,textview_address,textview_distance;
        private ImageView imageview_bigshow;
        private CBRatingBar cbratingbar;

        public MyViewHolder(View itemView) {
            super(itemView);
            textview_name = (TextView) itemView.findViewById(R.id.id_item_parkorcharge_layout_textview_name);
            textview_grade = (TextView) itemView.findViewById(R.id.id_item_parkorcharge_layout_textview_grade);
            textview_price = (TextView) itemView.findViewById(R.id.id_item_parkorcharge_layout_textview_price);
            textview_price1 = (TextView) itemView.findViewById(R.id.id_item_parkorcharge_layout_textview_price1);
            textview_address = (TextView) itemView.findViewById(R.id.id_item_parkorcharge_layout_textview_address);
            textview_distance = (TextView) itemView.findViewById(R.id.id_item_parkorcharge_layout_textview_distance);
            imageview_bigshow = (ImageView) itemView.findViewById(R.id.id_item_parkorcharge_layout_imageview_bigshow);
            cbratingbar = (CBRatingBar) itemView.findViewById(R.id.id_item_parkorcharge_layout_cbratingbar);
        }
    }
}
