package com.tuzhao.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.tuzhao.R;
import com.tuzhao.adapter.GuideViewPagerAdapter;
import com.tuzhao.fragment.GuideOneFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 欢迎页
 *
 * @author wwj_748
 */
public class WelcomeGuideActivity extends AppCompatActivity{

    private ViewPager vp;

    private List<Fragment> mFragments;

    private GuideViewPagerAdapter adapter;
    private List<View> views;
    private Button startBtn;

    // 引导页图片资源
    private static final int[] pics = {R.drawable.pic_guidepage_1,
            R.drawable.pic_guidepage_2, R.drawable.pic_guidepage_3, R.drawable.pic_guidepage_4};

    // 底部小点图片
    private ImageView[] dots;

    // 记录当前选中位置
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_layout);
        mFragments = new ArrayList<>();
        mFragments.add(new GuideOneFragment());

        /*views = new ArrayList<>();

        ImageView imageView;
        // 初始化引导页视图列表
        for (int i = 0; i < pics.length - 1; i++) {
            imageView = new ImageView(this);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(layoutParams);
            ImageUtil.showPic(imageView, pics[i]);
            views.add(imageView);
        }

        View view = LayoutInflater.from(this).inflate(R.layout.guid_view4, null, false);
        imageView = view.findViewById(R.id.guide_4);
        ImageUtil.showPic(imageView, pics[3]);
        startBtn = view.findViewById(R.id.btn_login);
        startBtn.setTag("enter");
        startBtn.setOnClickListener(this);
        views.add(view);*/

        vp = findViewById(R.id.vp_guide);
        vp.setAdapter(new GuideViewpagerAdapter(getSupportFragmentManager()));
        // 初始化adapter
        /*adapter = new GuideViewPagerAdapter(views);
        vp.setAdapter(adapter);*/

        //initDots();

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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*private void initDots() {
        LinearLayout ll =findViewById(R.id.ll);
        dots = new ImageView[pics.length];

        // 循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            // 得到一个LinearLayout下面的每一个子元素
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setEnabled(false);// 都设为灰色
            dots[i].setOnClickListener(this);
            dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(true); // 设置为白色，即选中状态

    }*/

    /**
     * 设置当前view
     *
     * @param position
     */
    /*private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }
        vp.setCurrentItem(position);
    }

    *//**
     * 设置当前指示点
     *
     *//*
    private void setCurDot(int position) {
        if (position < 0 || position > pics.length || currentIndex == position) {
            return;
        }
        dots[position].setEnabled(true);
        dots[currentIndex].setEnabled(false);
        currentIndex = position;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag().equals("enter")) {
            enterMainActivity();
            return;
        }

        int position = (Integer) v.getTag();
        setCurView(position);
        setCurDot(position);
    }*/


    private void enterMainActivity() {
        Intent intent = new Intent(WelcomeGuideActivity.this, SplashActivity.class);
        startActivity(intent);
        SharedPreferences activityPreferences = WelcomeGuideActivity.this.getSharedPreferences("tuzhaoapp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putBoolean("first_open", true);
        editor.apply();
        finish();
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
        }

    }
}
