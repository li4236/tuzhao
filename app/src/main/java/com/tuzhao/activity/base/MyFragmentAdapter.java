package com.tuzhao.activity.base;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tuzhao.fragment.base.BaseStatusFragment;

import java.util.List;

/**
 * Created by juncoder on 2018/8/31.
 */
public class MyFragmentAdapter<T extends BaseStatusFragment> extends FragmentPagerAdapter {

    private List<T> mFragments;

    public MyFragmentAdapter(FragmentManager fm, List<T> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragments.get(position).getTAG();
    }

}
