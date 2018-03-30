package com.tuzhao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.info.Search_Address_Info;

import java.util.List;

/**
 * Created by TZL12 on 2017/5/23.
 */

public class SearchAddressHistoryAdapter extends BaseAdapter {

    private List<Search_Address_Info> mList;
    private Context context;

    public SearchAddressHistoryAdapter(List<Search_Address_Info> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @Override
    public int getCount() {

        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_searchhistory_layout, null);
            holder.keyword = (TextView) convertView.findViewById(R.id.id_item_searchhistory_layout_textview_keyword);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.keyword.setText(mList.get(position).getKeyword());
        return convertView;
    }

    class ViewHolder {
        public TextView keyword;
    }
}
