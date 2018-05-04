package com.tuzhao.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseGuideFragment;
import com.tuzhao.fragment.welcome.GuideOneFragment;
import com.tuzhao.fragment.welcome.GuideThreeFragment;
import com.tuzhao.fragment.welcome.GuideTwoFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 欢迎页
 *
 * @author wwj_748
 */
public class WelcomeGuideActivity extends AppCompatActivity {

    private List<BaseGuideFragment> mFragments;

    private View[] mIndicators;

    // 记录当前选中位置
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_layout);
        mFragments = new ArrayList<>();
        mFragments.add(new GuideOneFragment());
        mFragments.add(new GuideTwoFragment());
        mFragments.add(new GuideThreeFragment());

        mIndicators = new View[3];
        mIndicators[0] = findViewById(R.id.guide_one_indicator);
        mIndicators[1] = findViewById(R.id.guide_two_indicator);
        mIndicators[2] = findViewById(R.id.guide_three_indicator);

        ViewPager viewPager = findViewById(R.id.vp_guide);
        viewPager.setAdapter(new GuideViewpagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new PageChangeListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 如果切换到后台，就设置下次不进入功能引导页
        SharedPreferences activityPreferences = WelcomeGuideActivity.this.getSharedPreferences("tuzhaoapp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putBoolean("first_open", true);
        editor.apply();
        finish();
    }

    private void setCurrentIndex(int currentIndex) {
        mIndicators[this.currentIndex].setBackground(ContextCompat.getDrawable(this, R.drawable.yuan_little_gray_5dp));
        mIndicators[currentIndex].setBackground(ContextCompat.getDrawable(this, R.drawable.yuan_little_y2_all_5dp));
        mFragments.get(this.currentIndex).isVisibilityToUser(false);
        mFragments.get(currentIndex).isVisibilityToUser(true);
        this.currentIndex = currentIndex;
    }

    private class GuideViewpagerAdapter extends FragmentPagerAdapter {

        GuideViewpagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

    }

    private class PageChangeListener implements OnPageChangeListener {
        // 当滑动状态改变时调用
        @Override
        public void onPageScrollStateChanged(int position) {
            // arg0 ==1的时辰默示正在滑动，arg0==2的时辰默示滑动完毕了，arg0==0的时辰默示什么都没做。

        }

        // 当前页面被滑动时调用
        @Override
        public void onPageScrolled(int position, float arg1, int arg2) {
            // arg0 :当前页面，及你点击滑动的页面
            // arg1:当前页面偏移的百分比
            // arg2:当前页面偏移的像素位置

        }

        // 当新的页面被选中时调用
        @Override
        public void onPageSelected(int position) {
            // 设置底部小点选中状态
            //setCurDot(position);
            Log.e("TAG", "onPageSelected: " + position);
            setCurrentIndex(position);
        }

    }
}
