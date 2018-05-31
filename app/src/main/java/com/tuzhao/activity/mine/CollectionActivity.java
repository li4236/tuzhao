package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.fragment.collection.CSCListFragment;
import com.tuzhao.fragment.collection.LoCListFragment;
import com.tuzhao.fragment.collection.PSCListFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.CollectionInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.CollectionManager;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.tuzhao.http.HttpConstants.getCollectionDatas;

/**
 * Created by TZL12 on 2017/12/21.
 */

public class CollectionActivity extends BaseActivity {

    /**
     * UI
     */
    private ViewPager viewpager;
    private SmartTabLayout viewPagerTab;
    private MyFrageStatePagerAdapter adapter;
    private TextView textview_edit,textview_delete;
    private LoadingDialog mLoadingDialog;

    private List<Fragment> fragmentList;
    private PSCListFragment pscListFragment;
    private CSCListFragment cscListFragment;
    private LoCListFragment loCListFragment;
    private String[] mTitle;
    private int mPosition = 0,collection_count = 0;
    private boolean mIsEdit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_layout);

        initView();
        initData();
        initEvent();
        setStyle(true);
    }

    private void initData() {

        if (UserManager.getInstance().hasLogined()) {
            if (!CollectionManager.getInstance().hasCollectionData()){
                initLoading("加载中...");
                getCollectionData();
            }else {
                collection_count = CollectionManager.getInstance().getCollection_datas().size();
            }
        } else {
            finish();
        }
    }

    private void getCollectionData() {

        OkGo.post(getCollectionDatas)
                .tag(CollectionActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token",UserManager.getInstance().getUserInfo().getToken())
                .execute(new JsonCallback<Base_Class_List_Info<CollectionInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<CollectionInfo> list_info, Call call, Response response) {
                        if (mLoadingDialog.isShowing()){
                            mLoadingDialog.dismiss();
                        }
                        collection_count = list_info.data.size();
                        CollectionManager.getInstance().setCollection_datas(list_info.data);
                        adapter = new MyFrageStatePagerAdapter(getSupportFragmentManager(), mTitle);
                        viewpager.setAdapter(adapter);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (mLoadingDialog.isShowing()){
                            mLoadingDialog.dismiss();
                        }
                        CollectionManager.getInstance().setCollection_datas(null);
                        adapter = new MyFrageStatePagerAdapter(getSupportFragmentManager(), mTitle);
                        viewpager.setAdapter(adapter);
                        if (!DensityUtil.isException(CollectionActivity.this,e)){
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 102:
//                                    MyToast.showToast(CollectionActivity.this, "没有数据", 5);
                                    break;
                                case 901:
                                    MyToast.showToast(CollectionActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }

    private void initView() {

        viewpager = (ViewPager) findViewById(R.id.id_activity_collection_layout_viewpager);
        viewPagerTab = (SmartTabLayout) findViewById(R.id.id_activity_collection_layout_pagetab);
        textview_edit = (TextView) findViewById(R.id.id_activity_collection_layout_textview_edit);
        textview_delete = (TextView) findViewById(R.id.id_activity_collection_layout_textview_delete);

        pscListFragment = new PSCListFragment();
        cscListFragment = new CSCListFragment();
        loCListFragment = new LoCListFragment();
        fragmentList = new ArrayList<>();
        mTitle = new String[]{"停车场", "充电桩", "标记点"};
        fragmentList.add(pscListFragment);
        fragmentList.add(cscListFragment);
        fragmentList.add(loCListFragment);
        adapter = new MyFrageStatePagerAdapter(getSupportFragmentManager(), mTitle);
        viewpager.setAdapter(adapter);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (mPosition!=position){
                    if (mIsEdit){
                        textview_edit.setText("编辑");
                        textview_delete.setVisibility(View.GONE);
                        mIsEdit = false;
                    }
                }
                mPosition = position;
                switch (mPosition){
                    case 0:
                        if (pscListFragment!=null){
                            pscListFragment.changeItem(mIsEdit);
                        }
                        break;
                    case 1:
                        if (cscListFragment!=null){
                            cscListFragment.changeItem(mIsEdit);
                        }
                        break;
                    case 2:
                        if (loCListFragment!=null){
                            loCListFragment.changeItem(mIsEdit);
                        }
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPagerTab.setViewPager(viewpager);

        for (int i = 0; i < fragmentList.size(); i++) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewPagerTab.getTabAt(i).getLayoutParams();
            lp.leftMargin = DensityUtil.dp2px(this, 25);
            lp.rightMargin = DensityUtil.dp2px(this, 25);
            viewPagerTab.getTabAt(i).setLayoutParams(lp);
        }
    }

    private void initEvent() {

        findViewById(R.id.id_activity_collection_layout_imageview_backbotton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        textview_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsEdit){
                    textview_edit.setText("编辑");
                    textview_delete.setVisibility(View.GONE);
                    mIsEdit = false;
                }else {
                    textview_edit.setText("取消");
                    textview_delete.setVisibility(View.VISIBLE);
                    mIsEdit = true;
                }
                switch (mPosition){
                    case 0:
                        if (pscListFragment!=null){
                            pscListFragment.changeItem(mIsEdit);
                        }
                        break;
                    case 1:
                        if (cscListFragment!=null){
                            cscListFragment.changeItem(mIsEdit);
                        }
                        break;
                    case 2:
                        if (loCListFragment!=null){
                            loCListFragment.changeItem(mIsEdit);
                        }
                        break;
                }
            }
        });

        textview_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ids = "";
                for (CollectionInfo info:CollectionManager.getInstance().getCollection_datas()){
                    switch (mPosition){
                        case 0:
                            if (info.getType().equals("1")&&info.isSelect()){
                                ids = ids + info.getId() + ",";
                            }
                            break;
                        case 1:
                            if (info.getType().equals("2")&&info.isSelect()){
                                ids = ids + info.getId() + ",";
                            }
                            break;
                        case 2:
                            if (info.getType().equals("3")&&info.isSelect()){
                                ids = ids + info.getId() + ",";
                            }
                            break;
                    }
                }
                if (!ids.equals("")){
                    TipeDialog.Builder builder = new TipeDialog.Builder(CollectionActivity.this);
                    builder.setMessage("确定取消收藏吗？");
                    builder.setTitle("提示");
                    final String finalIds = ids;
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            initLoading("加载中...");
                            String aaa = finalIds;
                            aaa = aaa.substring(0,aaa.length()-1);
                            deleteCollections(aaa);
                        }
                    });

                    builder.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                    builder.create().show();
                }else {
                    MyToast.showToast(CollectionActivity.this,"要先选择再删除哦!",5);
                }


            }
        });
    }

    private void deleteCollections(final String ids) {
        OkGo.post(HttpConstants.deleteCollection)
                .tag(CollectionActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token",UserManager.getInstance().getUserInfo().getToken())
                .params("id",ids)
                .execute(new JsonCallback<Base_Class_Info<CollectionInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<CollectionInfo> collectionInfoBase_class_info, Call call, Response response) {
                        if (mLoadingDialog.isShowing()){
                            mLoadingDialog.dismiss();
                        }
                        if (mIsEdit){
                            textview_edit.setText("编辑");
                            textview_delete.setVisibility(View.GONE);
                            mIsEdit = false;
                        }
                        switch (mPosition){
                            case 0:
                                if (pscListFragment!=null){
                                    pscListFragment.dataSetChange();
                                    pscListFragment.changeItem(mIsEdit);
                                }
                                break;
                            case 1:
                                if (cscListFragment!=null){
                                    cscListFragment.dataSetChange();
                                    cscListFragment.changeItem(mIsEdit);
                                }
                                break;
                            case 2:
                                if (loCListFragment!=null){
                                    loCListFragment.dataSetChange();
                                    loCListFragment.changeItem(mIsEdit);
                                }
                                break;
                        }
                        String[] aaa = ids.split(",");
                        for (String a:aaa){
                            CollectionManager.getInstance().removeOneCollectionByBelongId(a);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        getCollectionData();
                    }
                });
    }

    /**
     * 定义自己的ViewPager适配器。
     * 也可以使用FragmentPagerAdapter。关于这两者之间的区别，可以自己去搜一下。
     */
    class MyFrageStatePagerAdapter extends FragmentStatePagerAdapter {

        private String[] TITLES;

        public MyFrageStatePagerAdapter(FragmentManager fm, String[] title) {
            super(fm);
            TITLES = title;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);

        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    private void initLoading(String what) {
        mLoadingDialog = new LoadingDialog(this, what);
        mLoadingDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CollectionManager.getInstance().hasCollectionData()){
            if (collection_count != CollectionManager.getInstance().getCollection_datas().size()){
                switch (mPosition){
                    case 0:
                        if (pscListFragment!=null){
                            pscListFragment.uploadData();
                        }
                        break;
                    case 1:
                        if (cscListFragment!=null){
                            cscListFragment.uploadData();
                        }
                        break;
                    case 2:
                        if (loCListFragment!=null){
                            loCListFragment.uploadData();
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
    }
}
