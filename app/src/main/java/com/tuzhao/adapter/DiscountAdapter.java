package com.tuzhao.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.info.Discount_Info;
import com.tuzhao.publicmanager.TimeManager;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;

/**
 * Created by TZL12 on 2017/11/8.
 */

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Discount_Info> mDiscountList;
    private DateUtil dateUtil = new DateUtil();
    private IonSlidingViewClickListener mIDeleteBtnClickListener;

    public DiscountAdapter(Context context, ArrayList<Discount_Info> discountList , IonSlidingViewClickListener listener) {
        mContext = context;
        mDiscountList = discountList;

        mIDeleteBtnClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return mDiscountList == null || mDiscountList.isEmpty() ? 0 : mDiscountList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_discount_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        try {
            String endtime;
            if (mDiscountList.get(position).getIs_usable().equals("1")) {
                endtime = mDiscountList.get(position).getEffective_time().substring(mDiscountList.get(position).getEffective_time().indexOf(" - ") + 3, mDiscountList.get(position).getEffective_time().length());
                final boolean isnotlater = dateUtil.compareTwoTime(TimeManager.getInstance().getNowTime(false, false), endtime, false);
                if (isnotlater) {
                    //未过期
                    if (mDiscountList.get(position).getWhat_type().equals("1")){
                        holder.textview_topcolor.setTextColor(ContextCompat.getColor(mContext,R.color.y3));
                        holder.textview_yuan.setTextColor(ContextCompat.getColor(mContext,R.color.y3));
                        holder.textview_discount.setTextColor(ContextCompat.getColor(mContext,R.color.y3));
                        holder.textview_minfee.setTextColor(ContextCompat.getColor(mContext,R.color.y3));
                        holder.textview_ispark.setText("停车券");
                        holder.textview_ispark.setTextColor(ContextCompat.getColor(mContext,R.color.b1));
                    }else {
                        holder.textview_topcolor.setBackgroundColor(ContextCompat.getColor(mContext,R.color.green5));
                        holder.textview_yuan.setTextColor(ContextCompat.getColor(mContext,R.color.green5));
                        holder.textview_discount.setTextColor(ContextCompat.getColor(mContext,R.color.green5));
                        holder.textview_minfee.setTextColor(ContextCompat.getColor(mContext,R.color.green5));
                        holder.textview_ispark.setText("充电券");
                        holder.textview_ispark.setTextColor(ContextCompat.getColor(mContext,R.color.b1));
                    }
                    holder.imageview_isold.setVisibility(View.GONE);
                } else {
                    //已过期
                    holder.textview_topcolor.setBackgroundColor(ContextCompat.getColor(mContext,R.color.g6));
                    holder.textview_yuan.setTextColor(ContextCompat.getColor(mContext,R.color.g6));
                    holder.textview_discount.setTextColor(ContextCompat.getColor(mContext,R.color.g6));
                    holder.textview_minfee.setTextColor(ContextCompat.getColor(mContext,R.color.g6));
                    holder.textview_ispark.setTextColor(ContextCompat.getColor(mContext,R.color.g6));
                    holder.imageview_isold.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_guoqi));
                    holder.imageview_isold.setVisibility(View.VISIBLE);
                }
            } else if (mDiscountList.get(position).getIs_usable().equals("2")) {
                //已使用
                holder.textview_topcolor.setBackgroundColor(ContextCompat.getColor(mContext,R.color.g6));
                holder.textview_yuan.setTextColor(ContextCompat.getColor(mContext,R.color.g6));
                holder.textview_discount.setTextColor(ContextCompat.getColor(mContext,R.color.g6));
                holder.textview_minfee.setTextColor(ContextCompat.getColor(mContext,R.color.g6));
                holder.textview_ispark.setTextColor(ContextCompat.getColor(mContext,R.color.g6));
                holder.imageview_isold.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_used));
                holder.imageview_isold.setVisibility(View.VISIBLE);
            }
            holder.textview_discount.setText(DensityUtil.subZeroAndDot(mDiscountList.get(position).getDiscount()));
            if (mDiscountList.get(position).getMin_fee().equals("-1")) {
                holder.textview_minfee.setText("无门槛使用");
            } else {
                holder.textview_minfee.setText("满" + mDiscountList.get(position).getMin_fee() + "元可用");
            }
            holder.textview_effective_time.setText(mDiscountList.get(position).getEffective_time());

            holder.textview_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    TipeDialog.Builder builder = new TipeDialog.Builder(mContext);
                    builder.setMessage("确定删除该优惠券吗？");
                    builder.setTitle("提示");
                    builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mIDeleteBtnClickListener.onDeleteBtnCilck(mDiscountList.get(position).getId(),position);
                                }
                            });

                    builder.create().show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

         TextView textview_discount, textview_minfee, textview_ispark, textview_effective_time,textview_yuan,textview_topcolor,textview_delete;
         ImageView imageview_isold;

        public ViewHolder(View itemView) {
            super(itemView);
            textview_discount = (TextView) itemView.findViewById(R.id.id_item_discount_layout_textview_discount);
            imageview_isold = (ImageView) itemView.findViewById(R.id.id_item_discount_layout_imageview_isold);
            textview_minfee = (TextView) itemView.findViewById(R.id.id_item_discount_layout_textview_minfee);
            textview_ispark = (TextView) itemView.findViewById(R.id.id_item_discount_layout_textview_ispark);
            textview_yuan = (TextView) itemView.findViewById(R.id.id_item_discount_layout_textview_yuan);
            textview_topcolor = (TextView) itemView.findViewById(R.id.id_item_discount_layout_textview_topcolor);
            textview_effective_time = (TextView) itemView.findViewById(R.id.id_item_discount_layout_textview_effective_time);
            textview_delete = (TextView) itemView.findViewById(R.id.id_item_discount_layout_textview_delete);
//            layout_content = (ViewGroup) itemView.findViewById(R.id.id_item_discount_layout_content);
        }
    }

    public interface IonSlidingViewClickListener {
        void onDeleteBtnCilck(String discount_id,int pos);
    }

    public void removeItem(int position){

        mDiscountList.remove(position);

        notifyItemRemoved(position);

        if(position != mDiscountList.size()-1){      // 这个判断的意义就是如果移除的是最后一个，就不用管它了
            notifyItemRangeChanged(position, mDiscountList.size() - position);
        }
    }
}
