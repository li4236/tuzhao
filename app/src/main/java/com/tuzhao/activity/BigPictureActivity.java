package com.tuzhao.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.adapter.PicturePagerAdapter;
import com.tuzhao.publicwidget.customView.FixViewPager;

import java.util.ArrayList;

/**
 * Created by TZL12 on 2017/7/27.
 */

public class BigPictureActivity extends BaseActivity {

    /**
     * UI
     */
    private FixViewPager viewpager;
    private TextView textview_selectcount;

    /**
     * 数据相关
     */
    private ArrayList<String> imgList = new ArrayList<>();
    private int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bigpicture_layout);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        viewpager = findViewById(R.id.id_activity_bigpicture_layout_viewpager);
        textview_selectcount = findViewById(R.id.id_activity_bigpicture_layout_textview_selectcount);
        int barheigh = setStyle(false);
        FrameLayout.LayoutParams rp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rp.setMargins(0, barheigh, 0, 0);
        textview_selectcount.setLayoutParams(rp);
    }

    private void initData() {
        if (getIntent().hasExtra("picture_list")) {
            imgList = getIntent().getStringArrayListExtra("picture_list");
            position = getIntent().getIntExtra("position", 0);
        }
        textview_selectcount.setText((position + 1) + "/" + imgList.size());
        PicturePagerAdapter adapter = new PicturePagerAdapter(this, imgList, this);
        viewpager.setAdapter(adapter);
        if (position != 0) {
            viewpager.setCurrentItem(position);
        }
    }

    private void initEvent() {
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                textview_selectcount.setText((position + 1) + "/" + imgList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
