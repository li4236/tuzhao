package com.tuzhao.activity.base;

import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tuzhao.R;
import com.tuzhao.utils.DensityUtil;

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
        } else if (viewType != 0) {
            return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(itemViewId(viewType), parent, false));
        }
        return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(itemViewId(), parent, false));
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

        int temp = position - getHeadViewCount();
        if ((temp = converGetItemViewType(mData.get(temp), temp)) != 0) {
            return temp;
        }

        return super.getItemViewType(position);
    }

    protected abstract void conver(@NonNull BaseViewHolder holder, T t, int position);

    private int getHeadViewCount() {
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
        if (headerView.getLayoutParams() == null) {
            headerView.setLayoutParams(mRecyclerView.getLayoutParams());
        }
        mHeaderView = headerView;
    }

    /**
     * 添加一个空的头部，用于占位
     *
     * @param height headerView的高度，单位dp
     */
    public void setPlaceholderHeaderView(int height) {
        mHeaderView = LayoutInflater.from(mRecyclerView.getContext()).inflate(R.layout.layout_placeholder, mRecyclerView, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                DensityUtil.dp2px(mRecyclerView.getContext(), height));
        mHeaderView.setLayoutParams(layoutParams);
    }

    public View getFooterView() {
        return mFooterView;
    }

    public void setFooterView(@LayoutRes int footerViewResource) {
        mFooterView = LayoutInflater.from(mRecyclerView.getContext()).inflate(footerViewResource, mRecyclerView, false);
    }

    public void setFooterView(View footerView) {
        if (footerView.getLayoutParams() == null) {
            footerView.setLayoutParams(mRecyclerView.getLayoutParams());
        }
        mFooterView = footerView;
    }

    public void setPlaceholderFooterView(int height) {
        mFooterView = LayoutInflater.from(mRecyclerView.getContext()).inflate(R.layout.layout_placeholder, mRecyclerView, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                DensityUtil.dp2px(mRecyclerView.getContext(), height));
        mFooterView.setLayoutParams(layoutParams);
    }

    public void notifyRemoveFooterView() {
        mFooterView = null;
        notifyItemRemoved(getHeadViewCount() + mData.size());
    }

    public void notifyAddFooterView(View footerView) {
        mFooterView = footerView;
        notifyItemInserted(getHeadViewCount() + mData.size());
    }

    @LayoutRes
    protected abstract int itemViewId();

    /**
     * 如果子类需要不同的itemViewType则重写该方法
     *
     * @param position 对应data的实际position
     */
    protected int converGetItemViewType(T t, int position) {
        return 0;
    }

    /**
     * 如果子类需要不同的布局则重写该方法
     * 建议converGetItemViewType返回的是布局的id，否则重写该方法时不要调super
     */
    @LayoutRes
    protected int itemViewId(int viewType) {
        if (viewType != 0) {
            return viewType;
        }
        return 0;
    }

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
        mData.addAll(newData);
        notifyDataSetChanged();
    }

    public void setNewArrayData(ArrayList<T> newData) {
        if (newData != null) {
            if (!mData.isEmpty()) {
                mData.clear();
            }
            mData.addAll(newData);
            notifyDataSetChanged();
        }
    }

    /**
     * 只是添加新的数据进来，并不会通知更新布局
     */
    public void justAddData(T t) {
        mData.add(t);
    }

    /**
     * 在position为0的位置插入一条数据
     */
    public void addFirstData(T t) {
        mData.add(0, t);
        notifyItemInserted(getHeadViewCount());
        notifyItemRangeChanged(1, mData.size() - 1);
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

    /**
     * 在position位置插入一条数据
     */
    public void addData(int position, T t) {
        mData.add(position, t);
        notifyItemInserted(getHeadViewCount() + position);
    }

    public void notifyAddData(T t) {
        mData.add(t);
        notifyItemInserted(getHeadViewCount() + mData.size() - 1);
        notifyItemRangeChanged(getHeadViewCount() + mData.size() - 1, 1);
    }

    public void notifyAddData(List<T> data) {
        mData.addAll(data);
        notifyItemInserted(getHeadViewCount() + mData.size() - data.size());
        notifyItemRangeChanged(getHeadViewCount() + mData.size() - data.size(), data.size());
    }

    public void notifyAddData(int position, T t) {
        mData.add(position, t);
        notifyItemInserted(getHeadViewCount() + position);
        notifyItemRangeChanged(getHeadViewCount() + position, mData.size() - position);
    }

    /**
     * 修改changeDataPosition的数据为newData
     */
    public void notifyDataChange(int changeDataPosition, T newData) {
        mData.set(changeDataPosition, newData);
        notifyItemChanged(changeDataPosition + getHeadViewCount());
    }

    public void notifyDataChange(int changeDataPosition, T newData, Object payload) {
        mData.set(changeDataPosition, newData);
        notifyItemChanged(changeDataPosition, payload);
    }

    /**
     * 需要重写T的equals方法已便找到需要修改的是哪项
     */
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
            notifyItemRemoved(getHeadViewCount() + position);
            notifyItemRangeChanged(getHeadViewCount() + position, mData.size() - position);
        } else {
            Log.e(TAG, "notifyRemoveData: dont find" + t + " in the data!");
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
            boolean isSequence = true;
            int index = startPosition;
            for (int i = 1; i < data.size(); i++) {
                if (mData.indexOf(data.get(i)) == index + 1) {
                    index++;
                } else {
                    isSequence = false;
                    break;
                }
            }
            mData.removeAll(data);
            if (isSequence) {
                //如果要删除的数据在原数据中是顺序存放的则删除后局部刷新
                notifyItemRangeRemoved(startPosition + getHeadViewCount(), data.size());
            } else {
                //如果不是顺序存放的则全局刷新
                notifyDataSetChanged();
            }
        }
    }

    /**
     * 清除所有数据并通知布局修改
     */
    public void clearAll() {
        if (!mData.isEmpty()) {
            mData.clear();
            notifyDataSetChanged();
        }
    }

}
