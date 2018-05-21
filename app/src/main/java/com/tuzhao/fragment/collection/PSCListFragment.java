package com.tuzhao.fragment.collection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tuzhao.R;
import com.tuzhao.adapter.CollectionAdapter;
import com.tuzhao.fragment.base.BaseFragment;
import com.tuzhao.info.CollectionInfo;
import com.tuzhao.publicmanager.CollectionManager;
import com.tuzhao.publicwidget.swipetoloadlayout.SuperRefreshRecyclerView;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by TZL12 on 2017/6/26.
 */

public class PSCListFragment extends BaseFragment {

    /**
     * UI
     */
    private View mContentView;
    private SuperRefreshRecyclerView mRecycleview;
    private LinearLayoutManager linearLayoutManager;
    private CollectionAdapter mAdapter;

    private ArrayList<CollectionInfo> mDatas = new ArrayList<>();
    private boolean mIsEdit = false;

    /**
     * 页面相关
     */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_allorderlist_layout, container, false);

            initData();//初始化数据
            initView();//初始化控件
            initEvent();//初始化事件
        }
        return mContentView;
    }

    private void initView() {
        mRecycleview = (SuperRefreshRecyclerView) mContentView.findViewById(R.id.id_fragment_allorderlist_layout_recycleview);
        linearLayoutManager = new LinearLayoutManager(mContext);
        mRecycleview.init(linearLayoutManager, null, null);
        mRecycleview.setRefreshEnabled(false);
        mRecycleview.setLoadingMoreEnable(false);

        if (CollectionManager.getInstance().hasCollectionData()){
            for (CollectionInfo info:CollectionManager.getInstance().getCollection_datas()){
                if (info.getType().equals("1")){
                    mDatas.add(info);
                }
            }
        }

        if (!(mDatas.size()>0)){
            mContentView.findViewById(R.id.id_fragment_allorderlist_layout_linearlayout_nodata).setVisibility(View.VISIBLE);
        }
        mAdapter = new CollectionAdapter(mContext, mDatas,false);
        mRecycleview.setAdapter(mAdapter);
    }

    private void initData() {

    }

    private void initEvent() {
    }

    public void changeItem(boolean isEdit){
        if (mIsEdit != isEdit){
            mIsEdit = isEdit;
            mAdapter = new CollectionAdapter(mContext, mDatas,isEdit);
            mRecycleview.setAdapter(mAdapter);
        }
    }

    public void dataSetChange(){
        Iterator<CollectionInfo> it = mDatas.iterator();
        while(it.hasNext()){
            if (it.next().isSelect()){
                it.remove();
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    public void uploadData(){
        mDatas.clear();
        for (CollectionInfo info:CollectionManager.getInstance().getCollection_datas()){
            if (info.getType().equals("1")){
                mDatas.add(info);
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}
