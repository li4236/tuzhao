package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.tianzhili.www.myselfsdk.chenjing.XStatusBarHelper;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.fragment.CollectionFragment;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.IntentObserable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TZL12 on 2017/12/21.
 */

public class CollectionActivity extends BaseActivity implements SuccessCallback<Boolean> {

    private TextView textview_edit, textview_delete;

    private List<CollectionFragment> fragmentList;
    private boolean mIsEdit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_layout);
        XStatusBarHelper.tintStatusBar(this, ContextCompat.getColor(this, R.color.w0), 0);
        initView();
        initEvent();
    }

    private void initView() {
        /*
      UI
     */
        ViewPager viewpager = findViewById(R.id.id_activity_collection_layout_viewpager);
        SmartTabLayout viewPagerTab = findViewById(R.id.id_activity_collection_layout_pagetab);
        textview_edit = findViewById(R.id.id_activity_collection_layout_textview_edit);
        textview_delete = findViewById(R.id.id_activity_collection_layout_textview_delete);

        String[] title = new String[]{"停车场", "充电桩"};
        fragmentList = new ArrayList<>();
        fragmentList.add(CollectionFragment.getInstance("1"));
        fragmentList.add(CollectionFragment.getInstance("2"));

        MyFrageStatePagerAdapter adapter = new MyFrageStatePagerAdapter(getSupportFragmentManager(), title);
        viewpager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewpager);

        for (int i = 0; i < fragmentList.size(); i++) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewPagerTab.getTabAt(i).getLayoutParams();
            lp.leftMargin = DensityUtil.dp2px(this, 25);
            lp.rightMargin = DensityUtil.dp2px(this, 25);
            viewPagerTab.getTabAt(i).setLayoutParams(lp);
        }
    }

    private void initEvent() {

        findViewById(R.id.id_activity_collection_layout_imageview_backbotton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        textview_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsEdit) {
                    textview_edit.setText("编辑");
                    textview_delete.setVisibility(View.GONE);
                    mIsEdit = false;
                } else {
                    textview_edit.setText("取消");
                    textview_delete.setVisibility(View.VISIBLE);
                    mIsEdit = true;
                }
                IntentObserable.dispatch(ConstansUtil.EDIT_STATUS, ConstansUtil.INTENT_MESSAGE, mIsEdit);
            }
        });

        textview_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentList.get(0).getCheckCount() + fragmentList.get(1).getCheckCount() > 0) {
                    showDeleteDialog();
                } else {
                    MyToast.showToast(CollectionActivity.this, "请选择要删除的收藏", 5);
                }
            }
        });
    }

    private void showDeleteDialog() {
        new TipeDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确定删除选中的收藏记录吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        IntentObserable.dispatch(ConstansUtil.DELETE_COLLECTION);
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onSuccess(Boolean aBoolean) {
        textview_edit.setText("编辑");
        textview_delete.setVisibility(View.GONE);
        mIsEdit = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

}
