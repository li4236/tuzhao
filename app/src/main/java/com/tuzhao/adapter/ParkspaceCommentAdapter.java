package com.tuzhao.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cb.ratingbar.CBRatingBar;
import com.tuzhao.R;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkspaceCommentInfo;

import java.util.ArrayList;

/**
 * Created by TZL12 on 2017/11/8.
 */

public class ParkspaceCommentAdapter extends RecyclerView.Adapter<ParkspaceCommentAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ParkspaceCommentInfo> mCommentList;

    public ParkspaceCommentAdapter(Context context, ArrayList<ParkspaceCommentInfo> commentList) {
        mContext = context;
        mCommentList = commentList;
    }

    @Override
    public int getItemCount() {
        return mCommentList == null || mCommentList.isEmpty() ? 0 : mCommentList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_parkspacecomment_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(mContext)
                .load(HttpConstants.ROOT_IMG_URL_USER + mCommentList.get(position).getUser_img_url())
                .placeholder(R.mipmap.ic_img)
                .error(R.mipmap.ic_img)
                .centerCrop()
                .into(holder.imageview_user);
        if (mCommentList.get(position).getImg_url().equals("-1") || mCommentList.get(position).getImg_url().equals("")){
            holder.linearlayout_show.setVisibility(View.GONE);
        }else {
            String img_Url[] = mCommentList.get(position).getImg_url().split(",");
            if (!(img_Url.length>0)){
                holder.linearlayout_show.setVisibility(View.GONE);
            }else if (img_Url.length==1){
                Glide.with(mContext)
                        .load(HttpConstants.ROOT_IMG_URL_PSCOM + img_Url[0])
                        .placeholder(R.mipmap.ic_img)
                        .error(R.mipmap.ic_img)
                        .centerCrop()
                        .into(holder.imageview_show1);
                holder.imageview_show1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                holder.linearlayout_show.setVisibility(View.VISIBLE);
            }else if (img_Url.length==2){
                Glide.with(mContext)
                        .load(HttpConstants.ROOT_IMG_URL_PSCOM + img_Url[0])
                        .placeholder(R.mipmap.ic_img)
                        .error(R.mipmap.ic_img)
                        .centerCrop()
                        .into(holder.imageview_show1);
                holder.imageview_show1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                Glide.with(mContext)
                        .load(HttpConstants.ROOT_IMG_URL_PSCOM + img_Url[1])
                        .placeholder(R.mipmap.ic_img)
                        .error(R.mipmap.ic_img)
                        .centerCrop()
                        .into(holder.imageview_show2);
                holder.imageview_show2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                holder.linearlayout_show.setVisibility(View.VISIBLE);
            }else if (img_Url.length==3){
                Glide.with(mContext)
                        .load(HttpConstants.ROOT_IMG_URL_PSCOM + img_Url[0])
                        .placeholder(R.mipmap.ic_img)
                        .error(R.mipmap.ic_img)
                        .centerCrop()
                        .into(holder.imageview_show1);
                holder.imageview_show1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                Glide.with(mContext)
                        .load(HttpConstants.ROOT_IMG_URL_PSCOM + img_Url[1])
                        .placeholder(R.mipmap.ic_img)
                        .error(R.mipmap.ic_img)
                        .centerCrop()
                        .into(holder.imageview_show2);
                holder.imageview_show2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                Glide.with(mContext)
                        .load(HttpConstants.ROOT_IMG_URL_PSCOM + img_Url[2])
                        .placeholder(R.mipmap.ic_img)
                        .error(R.mipmap.ic_img)
                        .centerCrop()
                        .into(holder.imageview_show3);
                holder.imageview_show3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                holder.linearlayout_show.setVisibility(View.VISIBLE);
            }
        }

        if (position == mCommentList.size()-1){
            holder.imageview_downline.setVisibility(View.GONE);
        }
        holder.cbratingbar.setStarProgress(mCommentList.get(position).getGrade() == null?80:(new Float(mCommentList.get(position).getGrade())*100/5));
        holder.textview_user.setText(mCommentList.get(position).getNickname().equals("-1")?mCommentList.get(position).getUsername().substring(0, 3) + "*****" + mCommentList.get(position).getUsername().substring(8, mCommentList.get(position).getUsername().length()):mCommentList.get(position).getNickname());
        holder.textview_grade.setText(mCommentList.get(position).getGrade() == null?(4+"分"):mCommentList.get(position).getGrade()+"分");
        holder.textview_content.setText(mCommentList.get(position).getContent());
        holder.textview_parktime.setText("停车时间 "+mCommentList.get(position).getPark_time());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textview_user,textview_grade,textview_content,textview_parktime;
        public ImageView imageview_user,imageview_show1,imageview_show2,imageview_show3,imageview_downline;
        public CBRatingBar cbratingbar;
        public LinearLayout linearlayout_show;

        public ViewHolder(View itemView) {
            super(itemView);
            textview_user = (TextView) itemView.findViewById(R.id.id_item_parkspacemoment_layout_textview_user);
            imageview_user = (ImageView) itemView.findViewById(R.id.id_item_parkspacemoment_layout_imageview_user);
            textview_grade = (TextView) itemView.findViewById(R.id.id_item_parkspacemoment_layout_textview_grade);
            textview_content = (TextView) itemView.findViewById(R.id.id_item_parkspacemoment_layout_textview_content);
            textview_parktime = (TextView) itemView.findViewById(R.id.id_item_parkspacemoment_layout_textview_parktime);
            imageview_show1 = (ImageView) itemView.findViewById(R.id.id_item_parkspacemoment_layout_imageview_show1);
            imageview_show2 = (ImageView) itemView.findViewById(R.id.id_item_parkspacemoment_layout_imageview_show2);
            imageview_show3 = (ImageView) itemView.findViewById(R.id.id_item_parkspacemoment_layout_imageview_show3);
            cbratingbar = (CBRatingBar) itemView.findViewById(R.id.id_item_parkspacecomment_layout_cbratingbar);
            imageview_downline = (ImageView) itemView.findViewById(R.id.id_item_parkspacemoment_layout_imageview_downline);
            linearlayout_show = (LinearLayout) itemView.findViewById(R.id.id_item_parkspacemoment_layout_linearlayout_show);
        }
    }
}
