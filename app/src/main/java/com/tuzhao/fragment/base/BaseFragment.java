package com.tuzhao.fragment.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by 汪何龙 on 2017/4/8.
 * 创建首页所有的fragment
 */

public class BaseFragment extends Fragment {

    protected Activity mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
