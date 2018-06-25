package com.tuzhao.activity.base;

import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juncoder on 2018/3/27.
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    protected String TAG = this.getClass().getName();

    private List<T> mData;

    private RecyclerView mRecyclerView;

    private View mHeaderView;

    private View mFooterView;

    private BaseViewHolder mBaseViewHolder;

    private OnItemClickListener mOnItemClickListener;

    private static final int HEADER_VIEW = 0x111;

    private static final int FOOTER_VIEW = 0x222;

    public BaseAdapter() {
        mData = new ArrayList<>();
    }

    public BaseAdapter(List<T> data) {
        mData = data;
    }

    public BaseAdapter(RecyclerView recyclerView) {
        mData = new ArrayList<>();
        mRecyclerView = recyclerView;
    }

    public BaseAdapter(List<T> data, RecyclerView recyclerView) {
        mData = data;
        mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADER_VIEW) {
            return new BaseViewHolder(mHeaderView);
        } else if (viewType == FOOTER_VIEW) {
            return new BaseViewHolder(mFooterView);
        }
        mBaseViewHolder = new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(itemViewId(), parent, false));
        mBaseViewHolder.setBaseAdapter(this);
        return mBaseViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (getItemViewType(position) != HEADER_VIEW && getItemViewType(position) != FOOTER_VIEW) {
            conver(holder, mData.get(position - getHeadViewCount()), position - getHeadViewCount());
        }
    }

    @Override
    public int getItemCount() {
        return mData.size() + getHeadViewCount() + getFooterViewCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView != null && position == 0) {
            return HEADER_VIEW;
        }

        if (mFooterView != null && position == getHeadViewCount() + mData.size()) {
            return FOOTER_VIEW;
        }

        return super.getItemViewType(position);
    }

    protected abstract void conver(@NonNull BaseViewHolder holder, T t, int position);

    int getHeadViewCount() {
        if (mHeaderView == null) {
            return 0;
        }
        return 1;
    }

    private int getFooterViewCount() {
        if (mFooterView == null) {
            return 0;
        }
        return 1;
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void setHeaderView(@LayoutRes int headerViewResource) {
        mHeaderView = LayoutInflater.from(mRecyclerView.getContext()).inflate(headerViewResource, mRecyclerView, false);
    }

    public void setHeaderView(View headerView) {
        if (mRecyclerView != null) {
            headerView.setLayoutParams(mRecyclerView.getLayoutParams());
        }
        mHeaderView = headerView;
    }

    public View getFooterView() {
        return mFooterView;
    }

    public void setFooterView(@LayoutRes int footerViewResource) {
        mFooterView = LayoutInflater.from(mRecyclerView.getContext()).inflate(footerViewResource, mRecyclerView, false);
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public BaseViewHolder getBaseViewHolder() {
        return mBaseViewHolder;
    }

    @LayoutRes
    protected abstract int itemViewId();

    public List<T> getData() {
        return mData;
    }

    public T get(int position) {
        return mData.get(position);
    }

    public int getDataSize() {
        return mData.size();
    }

    public void setNewData(List<T> newData) {
        if (!mData.isEmpty()) {
            mData.clear();
        }
        mData = newData;
        notifyDataSetChanged();
    }

    public void setNewArrayData(ArrayList<T> newData) {
        if (newData != null) {
            if (!mData.isEmpty()) {
                mData.clear();
            }
            mData = newData;
            notifyDataSetChanged();
        }
    }

    public void justAddData(T t) {
        mData.add(t);
    }

    public void addFirstData(T t) {
        mData.add(0, t);
        notifyItemInserted(getHeadViewCount());
    }

    public void addData(T t) {
        mData.add(t);
        notifyItemInserted(getHeadViewCount() + mData.size() - 1);
    }

    public void addData(List<T> data) {
        int size = mData.size();
        mData.addAll(data);
        notifyItemInserted(size + getHeadViewCount());
    }

    public void addData(int position, T t) {
        mData.add(position, t);
        notifyItemInserted(getHeadViewCount() + position);
    }

    public void notifyAddData(T t) {
        mData.add(t);
        notifyDataSetChanged();
    }

    public void notifyAddData(int position, T t) {
        mData.add(position, t);
        notifyItemInserted(getHeadViewCount() + position);
        notifyItemRangeChanged(getHeadViewCount() + position, mData.size() - position);
    }

    public void notifyDataChange(int changeDataPosition, T newData) {
        mData.set(changeDataPosition, newData);
        notifyItemChanged(changeDataPosition + getHeadViewCount());
    }

    public void notifyDataChange(int changeDataPosition, T newData, Object payload) {
        mData.set(changeDataPosition, newData);
        notifyItemChanged(changeDataPosition, payload);
    }

    public void notifyDataChange(T newData) {
        int position = mData.indexOf(newData);
        if (position != -1) {
            notifyDataChange(position, newData);
        }
    }

    /**
     * if you use clickListener you should call this method
     */
    public void notifyRemoveData(@IntRange(from = 0) int position) {
        mData.remove(position);
        notifyItemRemoved(getHeadViewCount() + position);
        notifyItemRangeChanged(getHeadViewCount() + position, mData.size() - position);
    }

    /**
     * if you use clickListener you should call this method
     */
    public void notifyRemoveData(T t) {
        int position = mData.indexOf(t);
        if (position != -1) {
            mData.remove(position);
            notifyDataSetChanged();
        }
    }

    /**
     * @param position 在Data中的位置
     */
    public void removeData(int position) {
        if (mData.size() > position) {
            mData.remove(position);
            notifyItemRemoved(position + getHeadViewCount());
        }
    }

    public void removeData(T t) {
        if (mData.contains(t)) {
            int position = mData.indexOf(t);
            if (position != -1) {
                mData.remove(t);
                notifyItemRemoved(position + getHeadViewCount());
            }
        }
    }

    public void removeData(List<T> data) {
        if (mData.containsAll(data)) {
            int startPosition = mData.indexOf(data.get(0));
            int endPosition = mData.indexOf(data.get(data.size() - 1));
            mData.removeAll(data);
            notifyItemMoved(startPosition + getHeadViewCount(), endPosition + getHeadViewCount());
        }
    }

    public void clearAll() {
        if (!mData.isEmpty()) {
            mData.clear();
            notifyDataSetChanged();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

}
