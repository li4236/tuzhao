package com.tuzhao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.info.Search_Address_Info;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TZL12 on 2017/5/23.
 */

public class SearchAddressAdapter extends BaseAdapter implements Filterable {

    private ArrayFilter mFilter;
    private List<Search_Address_Info> mList;
    private Context context;
    private ArrayList<Search_Address_Info> mUnfilteredData;

    public SearchAddressAdapter(Context context) {
        this.mList = new ArrayList<>();
        this.context = context;
    }

    public void setNewData(List<Search_Address_Info> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public Search_Address_Info get(int position) {
        return mList.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_searchaddress_layout, null);
            holder.keyword = (TextView) convertView.findViewById(R.id.id_item_searchaddress_layout_textview_keyword);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.keyword.setText(mList.get(position).getKeyword());

        return convertView;

    }

    class ViewHolder {
         TextView keyword;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mUnfilteredData == null) {
                mUnfilteredData = new ArrayList<>(mList);
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<Search_Address_Info> list = mUnfilteredData;
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<Search_Address_Info> unfilteredValues = mUnfilteredData;
                int count = unfilteredValues.size();

                ArrayList<Search_Address_Info> newValues = new ArrayList<Search_Address_Info>(count);

                for (int i = 0; i < count; i++) {
                    Search_Address_Info pc = unfilteredValues.get(i);
                    if (pc != null) {

                        String valueText = pc.getKeyword().toLowerCase();

                        if (valueText.contains(prefixString)) { //实现模糊查询
                            //    valueText.contains(prefixString) 源码 ,匹配开头
                            newValues.add(pc);
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,FilterResults results) {
            mList = (List<Search_Address_Info>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}
