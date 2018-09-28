package com.tuzhao.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.mine.ParkSpaceSettingActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Park_Info;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.ImageUtil;

import java.util.ArrayList;

/**
 * Created by TZL13 on 2017/6/28.
 */

public class MyParkAdpater extends RecyclerView.Adapter<MyParkAdpater.MyViewHolder> {
    private Context mContext;
    private ArrayList<Park_Info> mData;

    public MyParkAdpater(Context context, ArrayList<Park_Info> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_mypark_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            if (mData.get(position).getPark_status().equals("3") || mData.get(position).getPark_status().equals("10")) {
                ImageUtil.showImgPic(holder.mIv_icon,
                        HttpConstants.ROOT_IMG_URL_PS + (mData.get(position).getPark_img() == null ? null : (mData.get(position).getPark_img().split(","))[0]));
                holder.mTv_parkInfo.setText(mData.get(position).getLocation_describe());
                holder.mTv_belongPark.setText(mData.get(position).getPark_space_name());
                holder.textview_lockvoltage.setText(mData.get(position).getVoltage() + "V");
                switch (Integer.parseInt(mData.get(position).getType())) {
                    case 1:
                        String[] ccc = mData.get(position).getProfit_ratio().split(":");
                        holder.textview_shouyi.setText(ccc[0] + " : " + ccc[1] + " : " + ccc[2] + " （车位主 : 物业 : 平台）");
                        break;
                    case 2:
                        String[] aaa = mData.get(position).getProfit_ratio().split(":");
                        holder.textview_shouyi.setText((Float.valueOf(aaa[0]) + new Float(aaa[1])) + " : " + aaa[2] + " （车位主 : 平台）");
                        break;
                    case 3:
                        String[] bbb = mData.get(position).getProfit_ratio().split(":");
                        holder.textview_shouyi.setText((Float.valueOf(bbb[0]) + new Float(bbb[1])) + " : " + bbb[2] + " （物业 : 平台）");
                        break;
                }
                if (mData.get(position).getPark_status().equals("2")) {
                    holder.mTv_parkstatus.setText("正在出租");
                } else if (mData.get(position).getPark_status().equals("3")) {
                    holder.mTv_parkstatus.setText("暂停出租");
                }
                holder.textview_cancle.setVisibility(View.GONE);
                holder.mItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mData.get(position).getPark_status().equals("1")) {
                            Intent intent = new Intent(mContext, ParkSpaceSettingActivity.class);
                            intent.putExtra(ConstansUtil.PARK_SPACE_INFO, mData.get(position));
                            mContext.startActivity(intent);
                        }
                    }
                });
            } else {
                ImageUtil.showImgPic(holder.mIv_icon,
                        HttpConstants.ROOT_IMG_URL_PS + (mData.get(position).getPark_img() == null ? null : (mData.get(position).getPark_img().split(","))[0]));
                holder.mTv_parkInfo.setText(mData.get(position).getLocation_describe());
                holder.mTv_belongPark.setText(mData.get(position).getPark_space_name());
                holder.textview_lockvoltage.setText("未知");
                switch (Integer.parseInt(mData.get(position).getType())) {
                    case 1:
                        String[] ccc = mData.get(position).getProfit_ratio().split(":");
                        holder.textview_shouyi.setText(ccc[0] + " : " + ccc[1] + " : " + ccc[2] + " （车位主 : 物业 : 平台）");
                        break;
                    case 2:
                        String[] aaa = mData.get(position).getProfit_ratio().split(":");
                        holder.textview_shouyi.setText((Float.valueOf(aaa[0]) + new Float(aaa[1])) + " : " + aaa[2] + " （车位主 : 平台）");
                        break;
                    case 3:
                        String[] bbb = mData.get(position).getProfit_ratio().split(":");
                        holder.textview_shouyi.setText((Float.valueOf(bbb[0]) + new Float(bbb[1])) + " : " + bbb[2] + " （物业 : 平台）");
                        break;
                }
                if (mData.get(position).getPark_status().equals("1")) {
                    holder.mTv_parkstatus.setText("加装审核中");
                    holder.textview_cancle.setVisibility(View.VISIBLE);
                } else if (mData.get(position).getPark_status().equals("3")) {
                    holder.mTv_parkstatus.setText("成功加装车位");
                    holder.textview_lockvoltage.setText(mData.get(position).getVoltage().equals("-1") ? "未知" : mData.get(position).getVoltage() + "V");
                    holder.textview_cancle.setVisibility(View.GONE);
                } else if (mData.get(position).getPark_status().equals("2")) {
                    holder.mTv_parkstatus.setText("正在进行安装中");
                    holder.textview_cancle.setVisibility(View.GONE);
                } else if (mData.get(position).getPark_status().equals("4")) {
                    holder.mTv_parkstatus.setText("加装车位未通过审核");
                    holder.textview_cancle.setVisibility(View.GONE);
                } else if (mData.get(position).getPark_status().equals("5")) {
                    holder.mTv_parkstatus.setText("拆卸审核中");
                } else if (mData.get(position).getPark_status().equals("8")) {
                    holder.mTv_parkstatus.setText("车位已拆卸");
                    holder.textview_cancle.setVisibility(View.GONE);
                } else if (mData.get(position).getPark_status().equals("6")) {
                    holder.mTv_parkstatus.setText("正在拆卸中");
                    holder.textview_cancle.setVisibility(View.GONE);
                } else if (mData.get(position).getPark_status().equals("7")) {
                    holder.mTv_parkstatus.setText("押金退还中");
                    holder.textview_cancle.setVisibility(View.GONE);
                } else if (mData.get(position).getPark_status().equals("9")) {
                    holder.mTv_parkstatus.setText("车位拆卸未通过审核");
                    holder.textview_cancle.setVisibility(View.GONE);
                }

                holder.textview_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData == null || mData.isEmpty() ? 0 : mData.size();
    }

    class MyViewHolder extends ViewHolder {

        ImageView mIv_icon;
        TextView mTv_belongPark;
        TextView mTv_parkInfo;
        TextView textview_shouyi;
        TextView mTv_parkstatus, textview_cancle, textview_lockvoltage;
        View mItemView;

        MyViewHolder(View itemView) {
            super(itemView);
            mIv_icon = itemView.findViewById(R.id.id_item_parkstation_imageview_icon);
            mTv_belongPark = itemView.findViewById(R.id.id_item_parkstation_textview_belongpark);
            mTv_parkInfo = itemView.findViewById(R.id.id_item_parkstation_textview_parkinfo);
            textview_shouyi = itemView.findViewById(R.id.id_item_parkstation_textview_shouyi);
            mTv_parkstatus = itemView.findViewById(R.id.id_item_parkstation_textview_parkstatus);
            textview_cancle = itemView.findViewById(R.id.id_item_mypark_layout_textview_cancle);
            textview_lockvoltage = itemView.findViewById(R.id.id_item_parkstation_textview_lockvoltage);
            mItemView = itemView;
        }
    }
}
