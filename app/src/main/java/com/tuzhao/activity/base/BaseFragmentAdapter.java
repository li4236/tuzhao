package com.tuzhao.activity.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tuzhao.fragment.base.BaseStatusFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juncoder on 2018/8/31.
 */
public class BaseFragmentAdapter<T extends BaseStatusFragment> extends FragmentPagerAdapter {

    private List<T> mFragments;

    public BaseFragmentAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        mFragments = new ArrayList<>();
    }

    public BaseFragmentAdapter(FragmentManager fm, List<T> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public T getItem(int position) {
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

    @Override
    public int getItemPosition(@NonNull Object object) {
        for (int i = 0; i < mFragments.size(); i++) {
            if (mFragments.get(i).hashCode() == object.hashCode()) {
                return super.getItemPosition(object);
            }
        }
        //返回POSITION_NONE才会进行刷新
        return POSITION_NONE;
    }

    @Override
    public long getItemId(int position) {
        //FragmentManager会根据这个来为Fragment生成Tag(不是BaseStatusFragment里面的TAG），同样的name会进行复用
        return mFragments.get(position).getTAG().hashCode();
    }

    public List<T> getFragments() {
        return mFragments;
    }

    public void setData(List<T> fragments) {
        mFragments.clear();
        mFragments.addAll(fragments);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mFragments.remove(position);
        notifyDataSetChanged();
    }

}
