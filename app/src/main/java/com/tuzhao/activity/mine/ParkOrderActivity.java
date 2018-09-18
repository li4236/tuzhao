package com.tuzhao.activity.mine;

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
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.fragment.parkorder.ParkOrderFragment;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TZL12 on 2017/11/16.
 */

public class ParkOrderActivity extends BaseActivity {

    private ImageView imageview_backbotton;

    /**
     * 页面相关
     */
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkorder_layout);
        XStatusBarHelper.tintStatusBar(this, ContextCompat.getColor(this, R.color.w0),0);
        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件
    }

    private void initView() {
        /*
      UI
     */
        ViewPager viewpager = findViewById(R.id.id_activity_parkorder_layout_viewpager);
        SmartTabLayout viewPagerTab = findViewById(R.id.id_activity_parkorder_layout_pagetab);
        imageview_backbotton = findViewById(R.id.id_activity_parkorder_imageview_back);

        fragmentList = new ArrayList<>();

        String[] mTitle = new String[]{"全部", "租用中", "已预约", "已完成", "已取消"};
        fragmentList.add(ParkOrderFragment.newInstance(0));
        fragmentList.add(ParkOrderFragment.newInstance(2));
        fragmentList.add(ParkOrderFragment.newInstance(1));
        fragmentList.add(ParkOrderFragment.newInstance(5));
        fragmentList.add(ParkOrderFragment.newInstance(6));
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
    class MyFrageStatePagerAdapter extends FragmentPagerAdapter {

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
