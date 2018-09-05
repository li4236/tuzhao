package com.tuzhao.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.fragment.parklotdetail.ParkLotCommentFragment;
import com.tuzhao.fragment.parklotdetail.ParkLotDetailFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.CollectionInfo;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.Park_Space_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.CollectionManager;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.dialog.LoginDialogFragment;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TZL12 on 2017/11/8.
 */

public class ParkspaceDetailActivity extends BaseActivity {

    /**
     * UI
     */
    private ViewPager viewpager;
    private SmartTabLayout viewPagerTab;
    private ImageView imageView_back, imageview_collection;
    private ParkLotDetailFragment mParkLotDetailFragment;
    private ParkLotCommentFragment mParkLotCommentFragment;

    /**
     * 页面相关
     */
    private List<Fragment> fragmentList;
    private String parkspace_id, city_code;
    private Park_Space_Info parkspace_info = null;
    private ArrayList<Park_Info> park_info = null;
    /**
     * 收藏
     */
    private CollectionManager.MessageHolder holder;
    private LoadingDialog mLoadingDialog;
    private LoginDialogFragment loginDialogFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkspacedetail_layout);

        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件
        setStyle(true);
    }

    private void initData() {
        if (getIntent().hasExtra("parkspace_info")) {
            parkspace_info = (Park_Space_Info) getIntent().getSerializableExtra("parkspace_info");
        } else {
            parkspace_id = getIntent().getStringExtra("parkspace_id");
            city_code = getIntent().getStringExtra("city_code");
        }
        if (getIntent().hasExtra("park_list")) {
            park_info = (ArrayList<Park_Info>) getIntent().getSerializableExtra("park_list");
        }

        holder = (CollectionManager.getInstance().checkCollectionDatas(parkspace_info == null ? parkspace_id : parkspace_info.getId(), "1"));
        if (holder.isExist) {
            imageview_collection.setImageDrawable(ContextCompat.getDrawable(ParkspaceDetailActivity.this, R.mipmap.ic_scchenggong));
        }
    }

    private void initView() {

        imageView_back =  findViewById(R.id.id_activity_parkspacedetail_imageView_back);
        imageview_collection =  findViewById(R.id.id_activity_parkspacedetail_imageview_collection);
        viewpager =  findViewById(R.id.id_activity_parkspacedetail_layout_viewpager);
        viewPagerTab = findViewById(R.id.id_activity_parkspacedetail_layout_pagetab);

        mParkLotDetailFragment = new ParkLotDetailFragment();
        mParkLotCommentFragment = new ParkLotCommentFragment();
        fragmentList = new ArrayList<>();
        fragmentList.add(mParkLotDetailFragment);
        fragmentList.add(mParkLotCommentFragment);

        viewpager.setAdapter(new MyFrageStatePagerAdapter(getSupportFragmentManager()));
        viewPagerTab.setViewPager(viewpager);

        for (int i = 0; i < fragmentList.size(); i++) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewPagerTab.getTabAt(i).getLayoutParams();
            lp.leftMargin = DensityUtil.dp2px(this, 12);
            lp.rightMargin = DensityUtil.dp2px(this, 12);
            viewPagerTab.getTabAt(i).setLayoutParams(lp);
        }

        if (getIntent().hasExtra("position")) {
            viewpager.setCurrentItem(getIntent().getIntExtra("position", 1), false);
        }
    }

    private void initEvent() {

        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageview_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserManager.getInstance().hasLogined()) {
                    holder = (CollectionManager.getInstance().checkCollectionDatas(parkspace_info == null ? parkspace_id : parkspace_info.getId(), "1"));
                    if (holder.isExist) {
                        initLoading("正在取消收藏...");
                        OkGo.post(HttpConstants.deleteCollection)
                                .tag(ParkspaceDetailActivity.this)
                                .addInterceptor(new TokenInterceptor())
                                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                                .params("id", CollectionManager.getInstance().getCollection_datas().get(holder.position).getId())
                                .execute(new JsonCallback<Base_Class_Info<CollectionInfo>>() {
                                    @Override
                                    public void onSuccess(Base_Class_Info<CollectionInfo> collection_infoBase_class_info, Call call, Response response) {
                                        if (mLoadingDialog.isShowing()) {
                                            mLoadingDialog.dismiss();
                                        }
                                        MyToast.showToast(ParkspaceDetailActivity.this, "已取消收藏", 5);
                                        imageview_collection.setImageDrawable(ContextCompat.getDrawable(ParkspaceDetailActivity.this, R.mipmap.ic_shoucang2));
                                        CollectionManager.getInstance().removeOneCollection(holder.position);
                                    }

                                    @Override
                                    public void onError(Call call, Response response, Exception e) {
                                        super.onError(call, response, e);
                                        if (mLoadingDialog.isShowing()) {
                                            mLoadingDialog.dismiss();
                                        }
                                        MyToast.showToast(ParkspaceDetailActivity.this, "取消失败", 5);
                                    }
                                });
                    } else {
                        initLoading("正在添加收藏...");
                        OkGo.post(HttpConstants.addCollection)
                                .tag(ParkspaceDetailActivity.this)
                                .addInterceptor(new TokenInterceptor())
                                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                                .params("belong_id", parkspace_info == null ? parkspace_id : parkspace_info.getId())
                                .params("type", "1")
                                .params("citycode", parkspace_info == null ? city_code : parkspace_info.getCity_code())
                                .execute(new JsonCallback<Base_Class_Info<CollectionInfo>>() {
                                    @Override
                                    public void onSuccess(Base_Class_Info<CollectionInfo> collection_infoBase_class_list_info, Call call, Response response) {
                                        if (mLoadingDialog.isShowing()) {
                                            mLoadingDialog.dismiss();
                                        }
                                        List<CollectionInfo> collection_datas = CollectionManager.getInstance().getCollection_datas();
                                        if (collection_datas == null) {
                                            collection_datas = new ArrayList<>();
                                        }
                                        collection_infoBase_class_list_info.data.setCitycode(parkspace_info == null ? city_code : parkspace_info.getCity_code());
                                        collection_datas.add(collection_infoBase_class_list_info.data);
                                        CollectionManager.getInstance().setCollection_datas(collection_datas);
                                        MyToast.showToast(ParkspaceDetailActivity.this, "收藏成功", 5);
                                        imageview_collection.setImageDrawable(ContextCompat.getDrawable(ParkspaceDetailActivity.this, R.mipmap.ic_scchenggong));
                                    }

                                    @Override
                                    public void onError(Call call, Response response, Exception e) {
                                        super.onError(call, response, e);
                                        if (mLoadingDialog.isShowing()) {
                                            mLoadingDialog.dismiss();
                                        }
                                        MyToast.showToast(ParkspaceDetailActivity.this, "收藏失败", 5);
                                    }
                                });
                    }
                } else {
                    login();
                }
            }
        });
    }

    /**
     * 定义自己的ViewPager适配器。
     * 也可以使用FragmentPagerAdapter。关于这两者之间的区别，可以自己去搜一下。
     */
    class MyFrageStatePagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"详 情", "评 论"};

        MyFrageStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragmentList.get(position);
            Bundle bundle = new Bundle();
            if (position == 0) {
                if (parkspace_info == null) {
                    bundle.putString("parkspace_id", parkspace_id);
                    bundle.putString("city_code", city_code);
                    bundle.putSerializable("parkspace_info", parkspace_info);
                } else {
                    bundle.putSerializable("parkspace_info", parkspace_info);
                }
                bundle.putSerializable("park_info", park_info);
            } else {
                if (parkspace_info == null) {
                    bundle.putString("parkspace_id", parkspace_id);
                    bundle.putString("city_code", city_code);
                    bundle.putSerializable("parkspace_info", parkspace_info);
                } else {
                    bundle.putSerializable("parkspace_info", parkspace_info);
                }
            }
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    public void login() {
        loginDialogFragment = new LoginDialogFragment();
        loginDialogFragment.show(getSupportFragmentManager(), "hahah");
    }

    private void initLoading(String what) {
        mLoadingDialog = new LoadingDialog(ParkspaceDetailActivity.this, what);
        mLoadingDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
    }
}
