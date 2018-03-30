package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.fragment.parkorder.AppointParkOrderListFragment;
import com.tuzhao.fragment.parkorder.CancleParkOrderFragment;
import com.tuzhao.fragment.parkorder.FinishParkOrderFragment;
import com.tuzhao.fragment.parkorder.ParkReadPayOrderListFragment;
import com.tuzhao.fragment.parkorder.RentingParkOrderFragment;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TZL12 on 2017/11/16.
 */

public class ParkOrderActivity extends BaseActivity {

    /**
     * UI
     */
    private ViewPager viewpager;
    private SmartTabLayout viewPagerTab;
    private ImageView imageview_backbotton;

    /**
     * 页面相关
     */
    private List<Fragment> fragmentList;
    private RentingParkOrderFragment rentingParkOrderFragment;
    private AppointParkOrderListFragment appointmentParkOdrderListFragment;
    private ParkReadPayOrderListFragment parkReadPayOrderListFragment;
    private FinishParkOrderFragment allorderlistfragment;
    private CancleParkOrderFragment cancleparkorderFragment;
    private String[] mTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkorder_layout);
        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件
        setStyle(true);
    }

    private void initView() {

        viewpager = (ViewPager) findViewById(R.id.id_activity_parkorder_layout_viewpager);
        viewPagerTab = (SmartTabLayout) findViewById(R.id.id_activity_parkorder_layout_pagetab);
        imageview_backbotton = (ImageView) findViewById(R.id.id_activity_parkorder_imageview_back);

        allorderlistfragment = new FinishParkOrderFragment();
        parkReadPayOrderListFragment = new ParkReadPayOrderListFragment();
        rentingParkOrderFragment = new RentingParkOrderFragment();
        appointmentParkOdrderListFragment = new AppointParkOrderListFragment();
        cancleparkorderFragment = new CancleParkOrderFragment();

        fragmentList = new ArrayList<>();

        mTitle = new String[]{"租用中", "已预约", "待付款", "已完成", "已取消"};
        fragmentList.add(rentingParkOrderFragment);
        fragmentList.add(appointmentParkOdrderListFragment);
        fragmentList.add(parkReadPayOrderListFragment);
        fragmentList.add(allorderlistfragment);
        fragmentList.add(cancleparkorderFragment);
        viewpager.setAdapter(new MyFrageStatePagerAdapter(getSupportFragmentManager(), mTitle));
        viewPagerTab.setViewPager(viewpager);

        for (int i = 0; i < fragmentList.size(); i++) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewPagerTab.getTabAt(i).getLayoutParams();
            lp.leftMargin = DensityUtil.dp2px(this, 13);
            lp.rightMargin = DensityUtil.dp2px(this, 13);
            viewPagerTab.getTabAt(i).setLayoutParams(lp);
        }
    }

    private void initData() {
    }

    private void initEvent() {

        imageview_backbotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

            return fragmentList.get(position);

        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }
}
