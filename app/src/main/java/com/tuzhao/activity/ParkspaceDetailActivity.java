package com.tuzhao.activity;

import android.content.Intent;
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
import com.tianzhili.www.myselfsdk.chenjing.XStatusBarHelper;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.fragment.parklotdetail.ParkLotCommentFragment;
import com.tuzhao.fragment.parklotdetail.ParkLotDetailFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.CollectionInfo;
import com.tuzhao.info.ParkLotInfo;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TZL12 on 2017/11/8.
 */

public class ParkspaceDetailActivity extends BaseActivity implements SuccessCallback<Boolean> {

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
    private ParkLotInfo mParkLotInfo = null;
    private ArrayList<Park_Info> park_info = null;
    private LoadingDialog mLoadingDialog;

    private boolean mIsCollection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkspacedetail_layout);
        XStatusBarHelper.tintStatusBar(this, ContextCompat.getColor(this, R.color.w0),0);

        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件
    }

    private void initData() {
        if (getIntent().hasExtra("parkspace_info")) {
            mParkLotInfo = (ParkLotInfo) getIntent().getSerializableExtra("parkspace_info");
        } else {
            parkspace_id = getIntent().getStringExtra("parkspace_id");
            city_code = getIntent().getStringExtra("city_code");
        }
        if (getIntent().hasExtra("park_list")) {
            park_info = (ArrayList<Park_Info>) getIntent().getSerializableExtra("park_list");
        }

    }

    private void initView() {

        imageView_back = findViewById(R.id.id_activity_parkspacedetail_imageView_back);
        imageview_collection = findViewById(R.id.id_activity_parkspacedetail_imageview_collection);
        viewpager = findViewById(R.id.id_activity_parkspacedetail_layout_viewpager);
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
                    if (mParkLotDetailFragment != null && mParkLotDetailFragment.getParkLot() != null) {
                        if (mIsCollection) {
                            initLoading("正在取消收藏...");
                            OkGo.post(HttpConstants.deleteCollection)
                                    .tag(ParkspaceDetailActivity.this)
                                    .addInterceptor(new TokenInterceptor())
                                    .headers("token", UserManager.getInstance().getUserInfo().getToken())
                                    .params("belong_id", mParkLotDetailFragment.getParkLot().getId())
                                    .params("type", "1")
                                    .params("citycode", mParkLotDetailFragment.getParkLot().getCity_code())
                                    .execute(new JsonCallback<Base_Class_Info<CollectionInfo>>() {
                                        @Override
                                        public void onSuccess(Base_Class_Info<CollectionInfo> collection_infoBase_class_info, Call call, Response response) {
                                            if (mLoadingDialog.isShowing()) {
                                                mLoadingDialog.dismiss();
                                            }
                                            MyToast.showToast(ParkspaceDetailActivity.this, "已取消收藏", 5);
                                            imageview_collection.setImageDrawable(ContextCompat.getDrawable(ParkspaceDetailActivity.this, R.mipmap.ic_shoucang2));
                                            mIsCollection = false;
                                            if (mParkLotDetailFragment != null) {
                                                mParkLotDetailFragment.setCollection(false);
                                            }
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
                                    .params("belong_id", mParkLotDetailFragment.getParkLot().getId())
                                    .params("type", "1")
                                    .params("citycode", mParkLotDetailFragment.getParkLot().getCity_code())
                                    .execute(new JsonCallback<Base_Class_Info<Void>>() {
                                        @Override
                                        public void onSuccess(Base_Class_Info<Void> collection_infoBase_class_list_info, Call call, Response response) {
                                            if (mLoadingDialog.isShowing()) {
                                                mLoadingDialog.dismiss();
                                            }
                                            MyToast.showToast(ParkspaceDetailActivity.this, "收藏成功", 5);
                                            imageview_collection.setImageDrawable(ContextCompat.getDrawable(ParkspaceDetailActivity.this, R.mipmap.ic_scchenggong));
                                            mIsCollection = false;
                                            if (mParkLotDetailFragment != null) {
                                                mParkLotDetailFragment.setCollection(false);
                                            }
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
                        MyToast.showToast(ParkspaceDetailActivity.this, "获取车场信息失败，请稍后再试", 5);
                    }
                } else {
                    MyToast.showToast(ParkspaceDetailActivity.this, "请先登录再操作哦", 5);
                    login();
                }
            }
        });
    }

    @Override
    public void onSuccess(Boolean aBoolean) {
        if (aBoolean) {
            imageview_collection.setImageDrawable(ContextCompat.getDrawable(ParkspaceDetailActivity.this, R.mipmap.ic_scchenggong));
        } else {
            imageview_collection.setImageDrawable(ContextCompat.getDrawable(ParkspaceDetailActivity.this, R.mipmap.ic_shoucang2));
        }
        mIsCollection = aBoolean;
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
                if (mParkLotInfo == null) {
                    bundle.putString("parkspace_id", parkspace_id);
                    bundle.putString("city_code", city_code);
                    bundle.putSerializable("mParkLotInfo", mParkLotInfo);
                } else {
                    bundle.putSerializable("mParkLotInfo", mParkLotInfo);
                }
                bundle.putSerializable("park_info", park_info);
            } else {
                if (mParkLotInfo == null) {
                    bundle.putString("parkspace_id", parkspace_id);
                    bundle.putString("city_code", city_code);
                    bundle.putSerializable("mParkLotInfo", mParkLotInfo);
                } else {
                    bundle.putSerializable("mParkLotInfo", mParkLotInfo);
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
        startActivity(new Intent(this, LoginActivity.class));
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
