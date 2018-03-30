package com.tianzhili.www.myselfsdk.SuspensionIndexBar.adpater;

import android.content.Context;

import com.tianzhili.www.myselfsdk.R;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.ViewHolder;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.bean.CityBean;

import java.util.List;

/**
 * Created by TZL13 .
 * Date: 17/7/5
 */

public class CityAdapter extends CommonAdapter<CityBean> {
    public CityAdapter(Context context, int layoutId, List<CityBean> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolder holder, final CityBean cityBean) {
        holder.setText(R.id.id_item_select_tv_City, cityBean.getCityname());
    }
}