package com.tianzhili.www.myselfsdk.filter.typeview.grid_holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.tianzhili.www.myselfsdk.R;
import com.tianzhili.www.myselfsdk.filter.util.DpUtils;
import com.tianzhili.www.myselfsdk.filter.view.FilterCheckedTextView;


/**
 *
 */
public class ItemViewHolder extends RecyclerView.ViewHolder {

    private final FilterCheckedTextView textView;
    private View.OnClickListener mListener;

    public ItemViewHolder(Context mContext, ViewGroup parent, View.OnClickListener mListener) {
        super(DpUtils.infalte(mContext, R.layout.holder_item, parent));
//        textView = ButterKnife.findById(itemView, R.id.tv_item);
        textView = (FilterCheckedTextView) itemView.findViewById(R.id.tv_item);
        this.mListener = mListener;
    }

    /**
     * tag标记的字段规则：eg:"obj_s"
     *
     * @param s
     * @param tag
     */
    public void bind(String s, Object tag) {
        textView.setText(s);
        textView.setTag(tag);
        textView.setOnClickListener(mListener);
    }
}
