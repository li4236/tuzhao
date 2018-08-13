package com.tuzhao.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.info.Park_Space_Info;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TZL12 on 2017/5/23.
 */

public class SearchParkSpaceAdapter extends BaseAdapter implements Filterable {

    private ArrayFilter mFilter;
    private List<Park_Space_Info> mList;
    private Context context;
    private ArrayList<Park_Space_Info> mUnfilteredData;
    private  Activity mActivity;
    private OnSearchStateChange stateChange;

    public SearchParkSpaceAdapter(List<Park_Space_Info> mList, Context context, Activity activity) {
        this.mList = mList;
        this.context = context;
        this.mActivity = activity;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_searchaddress_layout, null);
            holder.keyword = (TextView) convertView.findViewById(R.id.id_item_searchaddress_layout_textview_keyword);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.keyword.setText(mList.get(position).getParkLotName());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("park", mList.get(position));
                mActivity.setResult(101, intent);
                mActivity.finish();
            }
        });
        return convertView;
    }

    public interface OnSearchStateChange{
        void showSearchState(boolean hasresult);
    }

    public void setOnSearchStateChange(OnSearchStateChange stateChange){
        this.stateChange = stateChange;
    }

    class ViewHolder {
        public TextView keyword;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    //建立 过滤器
    private class ArrayFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            // 过滤后利用FilterResults将过滤结果返回
            FilterResults results = new FilterResults();
            if (mUnfilteredData == null) {
                mUnfilteredData = new ArrayList<Park_Space_Info>(mList);
            }

            if (prefix == null || prefix.length() == 0) {

                ArrayList<Park_Space_Info> list = mUnfilteredData;
                results.values = list; // list是上面的过滤结果
                results.count = list.size();// 结果数量

            } else {
                String prefixString = prefix.toString().toLowerCase();
                ArrayList<Park_Space_Info> unfilteredValues = mUnfilteredData;
                int count = unfilteredValues.size();

                ArrayList<Park_Space_Info> newValues = new ArrayList<Park_Space_Info>(count);

                for (int i = 0; i < count; i++) {
                    Park_Space_Info value = mUnfilteredData.get(i);
                    if (value!= null){
                        String valueText = value.getParkLotName().toLowerCase();

                        if (valueText.indexOf(prefixString) != -1) { //实现模糊查询
                            //    valueText.contains(prefixString) 源码 ,匹配开头
                            newValues.add(value);
                        }
                    }

                    results.values = newValues;
                    results.count = newValues.size();
                }
            }
            return results;
        }

        // 用于更新自动完成列表
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,FilterResults results) {

            mList = (List<Park_Space_Info>) results.values;
            if (results.count > 0 && results != null) {
                stateChange.showSearchState(true);
                notifyDataSetChanged();
            } else {// 无过滤结果，关闭列表
                if (stateChange!=null){
                    stateChange.showSearchState(false);
                }
                notifyDataSetInvalidated();
            }

        }
    }
}
