package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.fragment.discount.DiscountFragment;
import com.tuzhao.info.Discount_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TZL12 on 2017/11/10.
 */

public class DiscountActivity extends BaseActivity {
    private ViewPager viewpager;
    private SmartTabLayout viewPagerTab;

    private List<DiscountFragment> fragmentList;
    private String[] mTitle;

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

        fragmentList = new ArrayList<>();
        mTitle = new String[]{"现有券", "已使用", "已过期"};
    }

    private void initData() {
        if (UserManager.getInstance().hasLogined()) {
            int discountType = getIntent().getIntExtra(ConstansUtil.TYPE, 3);
            if (getIntent().hasExtra(ConstansUtil.DISCOUNT_LIST)) {
                //从停车订单跳转过来的
                double orderFee = Double.parseDouble(getIntent().getStringExtra(ConstansUtil.ORDER_FEE));
                ArrayList<Discount_Info> list = getIntent().getParcelableArrayListExtra(ConstansUtil.DISCOUNT_LIST);
                fragmentList.add(DiscountFragment.getInstance(discountType, 1, list, orderFee));
            } else {
                fragmentList.add(DiscountFragment.getInstance(discountType, 1));
            }
            fragmentList.add(DiscountFragment.getInstance(discountType, 2));
            fragmentList.add(DiscountFragment.getInstance(discountType, 3));

            MyFrageStatePagerAdapter adapter = new MyFrageStatePagerAdapter(getSupportFragmentManager(), mTitle);
            viewpager.setAdapter(adapter);
            viewPagerTab.setViewPager(viewpager);
            for (int i = 0; i < fragmentList.size(); i++) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewPagerTab.getTabAt(i).getLayoutParams();
                lp.leftMargin = DensityUtil.dp2px(DiscountActivity.this, 28);
                lp.rightMargin = DensityUtil.dp2px(DiscountActivity.this, 28);
                viewPagerTab.getTabAt(i).setLayoutParams(lp);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
