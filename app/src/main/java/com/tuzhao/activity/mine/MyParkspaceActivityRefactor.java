package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.fragment.MyParkspaceFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/5/17.
 */

public class MyParkspaceActivityRefactor extends BaseActivity {

    private CustomDialog mCustomDialog;

    private List<Fragment> mFragments;

    private TextView mCurrentParkspace;

    private FragmentAdater mFragmentAdater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_parkspace_layout_refactor);
        mFragments = new ArrayList<>();
        ImageView left = findViewById(R.id.left_iv);
        ImageView right = findViewById(R.id.right_iv);

        final ViewPager mViewPager = findViewById(R.id.my_parkspace_vp);
        mCurrentParkspace = findViewById(R.id.my_parkspace_current);
        mFragmentAdater = new FragmentAdater(getSupportFragmentManager());
        mViewPager.setAdapter(mFragmentAdater);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String text = "(" + (position + 1) + "/" + mFragments.size() + ")";
                mCurrentParkspace.setText(text);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ImageUtil.showPic(left, R.drawable.ic_left1);
        ImageUtil.showPic(right, R.drawable.ic_left2);

        findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager.getCurrentItem() != 0) {
                    mViewPager.setCurrentItem((mViewPager.getCurrentItem() - 1), true);
                }
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager.getCurrentItem() != mFragments.size() - 1) {
                    mViewPager.setCurrentItem((mViewPager.getCurrentItem() + 1), true);
                }
            }
        });
        loadData();
    }

    private void loadData() {
        showLoadingDialog();
        OkGo.post(HttpConstants.getParkFromUser)
                .tag(this.getClass().getName())
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .execute(new JsonCallback<Base_Class_List_Info<Park_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<Park_Info> o, Call call, Response response) {
                        for (Park_Info park_info : o.data) {
                            mFragments.add(MyParkspaceFragment.newInstance(park_info));
                        }
                        mFragmentAdater.notifyDataSetChanged();
                        String text = "(" + 1 + "/" + mFragments.size() + ")";
                        mCurrentParkspace.setText(text);
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dismmisLoadingDialog();
                        if (!DensityUtil.isException(MyParkspaceActivityRefactor.this, e)) {
                            if (e instanceof IllegalStateException) {
                                switch (e.getMessage()) {
                                    case "801":
                                        showFiveToast("数据存储异常，请稍后重试");
                                    case "802":
                                        showFiveToast("客户端异常，请稍后重试");
                                    case "803":
                                        showFiveToast("参数异常，请检查是否全都填写了哦");
                                    case "804":
                                        showFiveToast("获取数据异常，请稍后重试");
                                    case "805":
                                        showFiveToast("账户异常，请重新登录");
                                        finish();
                                    case "901":
                                        showFiveToast("服务器正在维护中");
                                        break;
                                    default:
                                }
                            } else {
                                showFiveToast(e.getMessage());
                            }
                        } else {
                            showFiveToast(e.getMessage());
                        }
                    }
                });

    }

    private void showLoadingDialog() {
        dismmisLoadingDialog();
        mCustomDialog = new CustomDialog(this, null);
        mCustomDialog.show();
    }

    /**
     * 关闭加载对话框
     */
    private void dismmisLoadingDialog() {
        if (mCustomDialog != null && mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }
    }

    /**
     * 显示位置在屏幕1/5的Toast
     *
     * @param msg 显示的消息
     */
    protected void showFiveToast(String msg) {
        MyToast.showToast(this, msg, 5);
    }

    class FragmentAdater extends FragmentPagerAdapter {

        FragmentAdater(FragmentManager fm) {
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
}
