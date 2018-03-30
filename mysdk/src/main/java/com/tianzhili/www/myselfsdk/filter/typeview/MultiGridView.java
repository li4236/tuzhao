package com.tianzhili.www.myselfsdk.filter.typeview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.tianzhili.www.myselfsdk.R;
import com.tianzhili.www.myselfsdk.filter.FocusGridLayoutManager;
import com.tianzhili.www.myselfsdk.filter.adapter.MultiGridAdapter;
import com.tianzhili.www.myselfsdk.filter.demobean.FilterBean;
import com.tianzhili.www.myselfsdk.filter.interfaces.OnFilterDoneListener;

import java.util.List;

/**
 * 多个girdView的筛选
 * <p>
 * 本app只做了5个list的适配，若是更多，可根据样式自行修改
 */
public class MultiGridView<FirstBean, SeconBean, ThirdBean, ForthBean, FifthBean> extends LinearLayout implements View.OnClickListener {

    RecyclerView recyclerView;

    private Context mcontext;
    private MultiGridAdapter multiGridAdapter;
    //总数据
    private FilterBean filterBean;
    //分数据
    private List<FilterBean.InstitutionObject> obj_list;//收住对象
    private List<FilterBean.PlaceProperty> property_list;//机构性质
    private List<FilterBean.Bed> bed_list;//机构床位
    private List<FilterBean.InstitutionPlace> type_list;//机构类型
    private List<FilterBean.InstitutionFeature> special_list;//机构特色

    //返回参数 默认初始值为0
    private int objId;
    private int propertyId;
    private int bedId;
    private int typeId;
    private String serviceId = "0";

    //构造
    public MultiGridView(Context context) {
        this(context, null);
        mcontext = context;
    }

    public MultiGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mcontext = context;
        init(context);
    }

    public MultiGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mcontext = context;
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MultiGridView(Context context, AttributeSet attrs, int defStyleAttr,
                         int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mcontext = context;
        init(context);
    }


    private void init(Context context) {
        setBackgroundColor(Color.WHITE);
        //布局
        inflate(context, R.layout.act_filter_double_grid, this);
        recyclerView = (RecyclerView) findViewById(R.id.act_recyclerView);
        findViewById(R.id.btact_confirm).setOnClickListener(this);
        findViewById(R.id.bta_reset).setOnClickListener(this);
//        ButterKnife.bind(this, this);
    }

    //01设置 筛选视图收起 回调
    private OnFilterDoneListener mOnFilterDoneListener;

    public MultiGridView<FirstBean, SeconBean, ThirdBean, ForthBean, FifthBean> setOnFilterDoneListener(OnFilterDoneListener listener) {
        mOnFilterDoneListener = listener;
        return this;
    }

    //02添加筛选回调
    private OnMultiGridViewCallbackListener onMultiGridViewCallbackListener;

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btact_confirm) {
//            if (mOnFilterDoneListener != null) {
//                mOnFilterDoneListener.onFilterDone(1, "", "");//1是数据显示在位置1的tab的上
//            }
            //若只点 确定 按钮，则只传初始值给回调
            if (onMultiGridViewCallbackListener != null) {
                onMultiGridViewCallbackListener.onSureClickListener(objId, propertyId, bedId, typeId, serviceId);
            }

        } else if (i == R.id.bta_reset) {
            if (bed_list.get(0).isSelect()
                    && obj_list.get(0).isSelect()
                    && property_list.get(0).isSelect()
                    && special_list.get(0).isSelect()
                    && type_list.get(0).isSelect()) {
            } else {

                //利用遍历修改状态
                for (FilterBean.Bed bean : bed_list) {
                    if (bean.isSelect()) {
                        bean.setSelect(false);
                    }
                }

                for (FilterBean.PlaceProperty bean : property_list) {
                    if (bean.isSelect()) {
                        bean.setSelect(false);
                    }
                }
                for (FilterBean.InstitutionObject bean : obj_list) {
                    if (bean.isSelect()) {
                        bean.setSelect(false);
                    }
                }

                for (FilterBean.InstitutionFeature bean : special_list) {
                    if (bean.isSelect()) {
                        bean.setSelect(false);
                    }
                }

                for (FilterBean.InstitutionPlace bean : type_list) {
                    if (bean.isSelect()) {
                        bean.setSelect(false);
                    }
                }

                //递默认选中值
                multiGridAdapter.setObj_list(obj_list, 0);
                multiGridAdapter.setProperty_list(property_list, 0);
                multiGridAdapter.setBed_list(bed_list, 0);
                multiGridAdapter.setType_list(type_list, 0);
                multiGridAdapter.setSpecial_list(special_list, 0);

                //回复默认选中值
                objId = 0;
                propertyId = 0;
                bedId = 0;
                typeId = 0;
                serviceId = 0 + "";

                //更新adpater
                multiGridAdapter.notifyDataSetChanged();
            }
        }
    }

    public interface OnMultiGridViewCallbackListener {
        void onSureClickListener(int objId, int propertyId, int bedId, int typeId, String serviceId);
        //        void onResetClickListener(int position);
    }

    public MultiGridView<FirstBean, SeconBean, ThirdBean, ForthBean, FifthBean>
    setOnMultiGridViewClick(OnMultiGridViewCallbackListener onMultiGridViewCallbackListener) {
        this.onMultiGridViewCallbackListener = onMultiGridViewCallbackListener;
        return this;
    }

    //获取总数据
    public MultiGridView<FirstBean, SeconBean, ThirdBean, ForthBean, FifthBean> setFilterBean(FilterBean filterBean) {
        this.filterBean = filterBean;

        //获取分数据
        obj_list = filterBean.getObject();
        property_list = filterBean.getProperty();
        bed_list = filterBean.getBed();
        type_list = filterBean.getType();
        special_list = filterBean.getFeature();
        return this;
    }

    //最终创建视图
    public MultiGridView<FirstBean, SeconBean, ThirdBean, ForthBean, FifthBean> build() {

        FocusGridLayoutManager gridLayoutManager = new FocusGridLayoutManager(this.getContext(), 20);

        //多个gridView合并设置
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                //标题设置
                if (position == 0
                        || position == (obj_list.size() + 1)//机构性质标题位置
                        || position == ((obj_list.size() + 1) + (property_list.size() + 1))//机构床位标题位置
                        || position == ((obj_list.size() + 1) + (property_list.size() + 1) + (bed_list.size() + 1))//机构类型标题位置
                        || position == ((obj_list.size() + 1) + (property_list.size() + 1) + (bed_list.size() + 1) + (type_list.size() + 1))//机构特色标题位置
                        ) {
                    return 20;//

                }
                //第一个布局设置
                if (position > 0 && position < (obj_list.size() + 1)) {//第一个布局5条数据，一行显示设置
                    return 4;
                }
                //其他布局统一设置
                return 5;//
            }
        });

        //初始化adpater
        multiGridAdapter = new MultiGridAdapter(getContext());

        //递默认选中值
        multiGridAdapter.setBed_list(bed_list, 0);
        multiGridAdapter.setObj_list(obj_list, 0);
        multiGridAdapter.setProperty_list(property_list, 0);
        multiGridAdapter.setSpecial_list(special_list, 0);
        multiGridAdapter.setType_list(type_list, 0);

        //关联设置
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(multiGridAdapter);

        //获取回调数据，若一个都不选，值不会获取，只传默认的初始值
        multiGridAdapter.setAdpaterCallback(new MultiGridAdapter.OnAdpaterCallbackListener() {
            @Override
            public void onItemClickListener(int objID, int propertyID, int bedID, int typeID, String serviceID) {
                objId = objID;
                propertyId = propertyID;
                bedId = bedID;
                typeId = typeID;
                serviceId = serviceID;
            }
        });
        return this;
    }

    //设置 选中条件 返回监听




}
