package com.tuzhao.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.info.User_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.dialog.TipeDialog;

import java.util.List;


/**
 * Created by TZL13 on 2017/5/23.
 */

public class CarNumberAdpater extends RecyclerView.Adapter<CarNumberAdpater.MyViewHolder> {
    private Context mContext;
    private OnItemClickListener onItemClickListener;
    private List<String> mData;
    private IonSlidingViewClickListener mIDeleteBtnClickListener;
    private User_Info mUserInfo;

    public CarNumberAdpater(Context context) {
        mContext = context;
        mIDeleteBtnClickListener = (IonSlidingViewClickListener) mContext;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public CarNumberAdpater(Context context, List<String> data) {
        mContext = context;
        mData = data;
        mIDeleteBtnClickListener = (IonSlidingViewClickListener) mContext;
    }

    public void setData(List<String> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mUserInfo = UserManager.getInstance().getUserInfo();
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_carnumber,parent,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.mCarNumber.setText(mData.get(position));
        //设置内容布局的宽为屏幕宽度

        holder.btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                TipeDialog.Builder builder = new TipeDialog.Builder(mContext);
                builder.setMessage("确定删除该车牌号吗？");
                builder.setTitle("提示");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int n = holder.getLayoutPosition();
                        mIDeleteBtnClickListener.onDeleteBtnCilck(v, n);
                    }
                });

                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null? 0:mData.size();
    }

    public void removeData(int position){
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public String getItemData(int position) {
        return mData.get(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mCarNumber;
        private TextView btn_Delete;

        public MyViewHolder(View itemView) {
            super(itemView);
            mCarNumber = (TextView) itemView.findViewById(R.id.id_item_carnumber_textview_usercar);
            btn_Delete = (TextView) itemView.findViewById(R.id.id_item_carnumber_textview_delete);
            mCarNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v,getLayoutPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }


    public interface IonSlidingViewClickListener {
        void onDeleteBtnCilck(View view, int position);
    }
}
