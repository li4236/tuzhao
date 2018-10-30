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
import com.tianzhili.www.myselfsdk.chenjing.XStatusBarHelper;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.fragment.parklotdetail.ParkLotCommentFragment;
import com.tuzhao.fragment.parklotdetail.ParkLotDetailFragment;
import com.tuzhao.info.ParkLotInfo;
import com.tuzhao.info.Park_Info;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TZL12 on 2017/11/8.
 */

public class ParkspaceDetailActivity extends BaseActivity {

    private ImageView imageView_back;

    /**
     * 页面相关
     */
    private List<Fragment> fragmentList;
    private String parkspace_id, city_code;
    private ParkLotInfo mParkLotInfo = null;
    private ArrayList<Park_Info> mParkInfos = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkspacedetail_layout);
        XStatusBarHelper.tintStatusBar(this, ContextCompat.getColor(this, R.color.w0), 0);

        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件
    }

    private void initData() {
        if (getIntent().hasExtra("parkspace_info")) {
            mParkLotInfo = getIntent().getParcelableExtra("parkspace_info");
        } else {
            parkspace_id = getIntent().getStringExtra("parkspace_id");
            city_code = getIntent().getStringExtra("city_code");
        }
        if (getIntent().hasExtra("park_list")) {
            mParkInfos = getIntent().getParcelableArrayListExtra("park_list");
        }

    }

    private void initView() {
        imageView_back = findViewById(R.id.id_activity_parkspacedetail_imageView_back);
        /*
      UI
     */
        ViewPager viewpager = findViewById(R.id.id_activity_parkspacedetail_layout_viewpager);
        SmartTabLayout viewPagerTab = findViewById(R.id.id_activity_parkspacedetail_layout_pagetab);

        ParkLotDetailFragment parkLotDetailFragment = new ParkLotDetailFragment();
        ParkLotCommentFragment parkLotCommentFragment = new ParkLotCommentFragment();
        fragmentList = new ArrayList<>();
        fragmentList.add(parkLotDetailFragment);
        fragmentList.add(parkLotCommentFragment);

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
                    bundle.putString(ConstansUtil.PARK_LOT_ID, parkspace_id);
                    bundle.putString(ConstansUtil.CITY_CODE, city_code);
                    bundle.putParcelable(ConstansUtil.PARK_LOT_INFO, mParkLotInfo);
                } else {
                    bundle.putParcelable(ConstansUtil.PARK_LOT_INFO, mParkLotInfo);
                }
                bundle.putParcelableArrayList(ConstansUtil.PARK_SPACE_INFO, mParkInfos);
            } else {
                if (mParkLotInfo == null) {
                    bundle.putString(ConstansUtil.PARK_LOT_ID, parkspace_id);
                    bundle.putString(ConstansUtil.CITY_CODE, city_code);
                    bundle.putParcelable(ConstansUtil.PARK_LOT_INFO, mParkLotInfo);
                } else {
                    bundle.putParcelable(ConstansUtil.PARK_LOT_INFO, mParkLotInfo);
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

}
