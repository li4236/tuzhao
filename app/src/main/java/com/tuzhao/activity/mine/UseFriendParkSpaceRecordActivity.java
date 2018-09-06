package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.MyFragmentAdapter;
import com.tuzhao.fragment.UseFriendParkSpaceRecordFragment;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juncoder on 2018/8/30.
 */
public class UseFriendParkSpaceRecordActivity extends BaseStatusActivity {

    @Override
    protected int resourceId() {
        return R.layout.activity_use_friend_park_space_record_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        SmartTabLayout smartTabLayout = findViewById(R.id.use_friend_park_space_record_stl);
        ViewPager viewPager = findViewById(R.id.use_friend_park_space_record_vp);

        List<UseFriendParkSpaceRecordFragment> fragments = new ArrayList<>();
        fragments.add(UseFriendParkSpaceRecordFragment.getInstance("0"));
        fragments.add(UseFriendParkSpaceRecordFragment.getInstance("5"));
        viewPager.setAdapter(new MyFragmentAdapter<>(getSupportFragmentManager(), fragments));
        smartTabLayout.setViewPager(viewPager);

        for (int i = 0; i < fragments.size(); i++) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) smartTabLayout.getTabAt(i).getLayoutParams();
            lp.leftMargin = DensityUtil.dp2px(this, 16);
            lp.rightMargin = DensityUtil.dp2px(this, 16);
            smartTabLayout.getTabAt(i).setLayoutParams(lp);
        }

    }

    @Override
    protected void initData() {
    }

    @NonNull
    @Override
    protected String title() {
        return "预定记录";
    }

}
