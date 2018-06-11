package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.fragment.discount.HaveFragment;
import com.tuzhao.fragment.discount.OverFragment;
import com.tuzhao.fragment.discount.UsedFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Discount_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TZL12 on 2017/11/10.
 */

public class DiscountActivity extends BaseActivity {

    /**
     * UI
     */
    private LoadingDialog mLoadingDialog;

    /**
     * 页面相关
     */
    private ArrayList<Discount_Info> mCanDiscount = new ArrayList<>();
    private ArrayList<Discount_Info> mUsedDiscount = new ArrayList<>();
    private ArrayList<Discount_Info> mOldDiscount = new ArrayList<>();

    private ViewPager viewpager;
    private SmartTabLayout viewPagerTab;
    private MyFrageStatePagerAdapter adapter;

    private List<Fragment> fragmentList;
    private HaveFragment haveFragment;
    private UsedFragment usedFragment;
    private OverFragment overFragment;
    private String[] mTitle;

    private double mOrderFee = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosediscount_layout);

        initView();
        initData();
        initEvent();
        setStyle(true);
    }

    private void initView() {
        viewpager = findViewById(R.id.id_activity_discount_layout_viewpager);
        viewPagerTab = findViewById(R.id.id_activity_discount_layout_pagetab);

        haveFragment = new HaveFragment();
        usedFragment = new UsedFragment();
        overFragment = new OverFragment();
        fragmentList = new ArrayList<>();
        mTitle = new String[]{"现有券", "已使用", "已过期"};
        fragmentList.add(haveFragment);
        fragmentList.add(usedFragment);
        fragmentList.add(overFragment);
    }

    private void initData() {
        if (UserManager.getInstance().hasLogined()) {
            if (getIntent().hasExtra(ConstansUtil.DISCOUNT_LIST)) {
                mOrderFee = Double.parseDouble(getIntent().getStringExtra(ConstansUtil.ORDER_FEE));
                ArrayList<Discount_Info> list = getIntent().getParcelableArrayListExtra(ConstansUtil.DISCOUNT_LIST);
                handleDiscount(list);
            } else {
                initLoading("加载中...");
                requestGetUserDiscount();
            }
        } else {
            finish();
        }
    }

    private void initEvent() {
        findViewById(R.id.id_activity_discount_imageview_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void requestGetUserDiscount() {
        OkGo.post(HttpConstants.getUserDiscount)
                .tag(DiscountActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .execute(new JsonCallback<Base_Class_List_Info<Discount_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<Discount_Info> datas, Call call, Response response) {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        handleDiscount(datas.data);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }

                        adapter = new MyFrageStatePagerAdapter(getSupportFragmentManager(), mTitle);
                        viewpager.setAdapter(adapter);
                        viewPagerTab.setViewPager(viewpager);
                        for (int i = 0; i < fragmentList.size(); i++) {
                            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewPagerTab.getTabAt(i).getLayoutParams();
                            lp.leftMargin = DensityUtil.dp2px(DiscountActivity.this, 28);
                            lp.rightMargin = DensityUtil.dp2px(DiscountActivity.this, 28);
                            viewPagerTab.getTabAt(i).setLayoutParams(lp);
                        }

                        if (!DensityUtil.isException(DiscountActivity.this, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
                                    MyToast.showToast(DiscountActivity.this, "没有数据", 5);
                                    break;
                                case 901:
                                    MyToast.showToast(DiscountActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });

    }

    private void handleDiscount(List<Discount_Info> list) {
        for (Discount_Info info : list) {
            if (info.getIs_usable().equals("1")) {
                //可用
                if (info.getWhat_type().equals("1")) {
                    /*if (mOrderFee >= Double.valueOf(info.getMin_fee())) {
                        //大于等于最低消费*/
                        if (DateUtil.isInUsefulDate(info.getEffective_time())) {
                            //在可用范围内
                            mCanDiscount.add(info);
                        } else {
                            mOldDiscount.add(info);
                        }
                    //}
                }
            } else if (info.getIs_usable().equals("2")) {
                mUsedDiscount.add(info);
            }

            /*if (info.getIs_usable().equals("1")) {
                endtime = info.getEffective_time().substring(info.getEffective_time().indexOf(" - ") + 3, info.getEffective_time().length());
                final boolean isnotlater = dateUtil.compareTwoTime(TimeManager.getInstance().getNowTime(false, false), endtime, false);
                if (isnotlater) {
                    //未过期
                    mCanDiscount.add(info);
                } else {
                    //已过期
                    mOldDiscount.add(info);
                }
            } else if (info.getIs_usable().equals("2")) {
                mUsedDiscount.add(info);
            }*/
        }

        adapter = new MyFrageStatePagerAdapter(getSupportFragmentManager(), mTitle);
        viewpager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewpager);
        for (int i = 0; i < fragmentList.size(); i++) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewPagerTab.getTabAt(i).getLayoutParams();
            lp.leftMargin = DensityUtil.dp2px(DiscountActivity.this, 28);
            lp.rightMargin = DensityUtil.dp2px(DiscountActivity.this, 28);
            viewPagerTab.getTabAt(i).setLayoutParams(lp);
        }
    }

    /**
     * 定义自己的ViewPager适配器。
     * 也可以使用FragmentPagerAdapter。关于这两者之间的区别，可以自己去搜一下。
     */
    class MyFrageStatePagerAdapter extends FragmentStatePagerAdapter {

        private String[] TITLES;

         MyFrageStatePagerAdapter(FragmentManager fm, String[] title) {
            super(fm);
            TITLES = title;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragmentList.get(position);
            Bundle bundle = new Bundle();
            switch (position) {
                case 0:
                    bundle.putSerializable("discounts", mCanDiscount);
                    if (mOrderFee != -1) {
                        bundle.putDouble(ConstansUtil.ORDER_FEE, mOrderFee);
                    }
                    break;
                case 1:
                    bundle.putSerializable("discounts", mUsedDiscount);
                    break;
                case 2:
                    bundle.putSerializable("discounts", mOldDiscount);
                    break;
            }
            fragment.setArguments(bundle);
            return fragment;
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
    public void onDestroy() {
        super.onDestroy();
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
    }
}
