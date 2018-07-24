package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
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
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/5/17.
 */

public class MyParkspaceActivityRefactor extends BaseActivity implements View.OnClickListener {

    private LoadingDialog mLoadingDialog;

    private ViewPager mViewPager;

    private List<MyParkspaceFragment> mFragments;

    private TextView mCurrentParkspace;

    private ImageView mLeft;

    private ImageView mRight;

    private FragmentAdater mFragmentAdater;

    private ViewStub mViewStub;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_parkspace_layout_refactor);
        mFragments = new ArrayList<>();
        mLeft = findViewById(R.id.left_iv);
        mRight = findViewById(R.id.right_iv);

        mViewPager = findViewById(R.id.my_parkspace_vp);
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

        ImageUtil.showPic(mLeft, R.drawable.ic_left1);
        ImageUtil.showPic(mRight, R.drawable.ic_left2);

        findViewById(R.id.toolbar_back).setOnClickListener(this);
        findViewById(R.id.audit_tv).setOnClickListener(this);
        mLeft.setOnClickListener(this);
        mRight.setOnClickListener(this);
        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstansUtil.REQUSET_CODE && resultCode == RESULT_OK && data != null) {
            if (data.hasExtra(ConstansUtil.PARK_SPACE_ID)) {
                String parkSpaceId = data.getStringExtra(ConstansUtil.PARK_SPACE_ID);
                for (int i = 0; i < mFragments.size(); i++) {
                    if (mFragments.get(i).getParkInfo().getId().equals(parkSpaceId)) {
                        mFragments.remove(i);
                        mFragmentAdater.notifyDataSetChanged();
                        String text = "(" + 1 + "/" + mFragments.size() + ")";
                        mCurrentParkspace.setText(text);
                        if (mFragments.isEmpty()) {
                            showViewStub();
                        }
                        Log.e("TAG", "onActivityResult: ");
                        break;
                    }
                }
            } else if (data.hasExtra(ConstansUtil.FOR_REQUEST_RESULT)) {
                Park_Info parkInfo = (Park_Info) data.getSerializableExtra(ConstansUtil.FOR_REQUEST_RESULT);
                for (int i = 0; i < mFragments.size(); i++) {
                    if (mFragments.get(i).getParkInfo().getId().equals(parkInfo.getId())) {
                        mFragments.get(i).setParkInfo(parkInfo);
                        break;
                    }
                }
            }
        }
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
                        if (o.data.isEmpty()) {
                            showViewStub();
                        } else {
                            if (mLeft.getVisibility() != View.VISIBLE) {
                                mLeft.setVisibility(View.VISIBLE);
                                mRight.setVisibility(View.VISIBLE);
                            }
                            for (Park_Info park_info : o.data) {
                                mFragments.add(MyParkspaceFragment.newInstance(park_info));
                            }
                            mFragmentAdater.notifyDataSetChanged();
                            String text = "(" + 1 + "/" + mFragments.size() + ")";
                            mCurrentParkspace.setText(text);
                        }
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dismmisLoadingDialog();
                        if (mFragments.isEmpty()) {
                            showViewStub();
                        }
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
        mLoadingDialog = new LoadingDialog(this, null);
        mLoadingDialog.show();
    }

    /**
     * 关闭加载对话框
     */
    private void dismmisLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back:
                finish();
                break;
            case R.id.left_iv:
                if (mViewPager.getCurrentItem() != 0) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                }
                break;
            case R.id.right_iv:
                if (mViewPager.getCurrentItem() != mFragments.size() - 1) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                }
                break;
            case R.id.audit_tv:
                Intent intent = new Intent(this, AuditParkSpaceActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void showViewStub() {
        if (mViewStub == null) {
            mViewStub = findViewById(R.id.park_space_vs);
            ConstraintLayout view = (ConstraintLayout) mViewStub.inflate();
            ConstraintLayout constraintLayout = view.findViewById(R.id.content_cl);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(view);
            constraintSet.setMargin(R.id.content_cl, ConstraintSet.BOTTOM, 0);
            constraintSet.applyTo(view);

            ImageView imageView = constraintLayout.findViewById(R.id.monthly_card_iv);
            TextView textView = constraintLayout.findViewById(R.id.no_monthly_card_tv);
            TextView addNow = constraintLayout.findViewById(R.id.buy_now);
            ImageUtil.showPic(imageView, R.drawable.ic_nospace);
            textView.setText("您还没添加车位哦");
            addNow.setText("立即添加");
            addNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MyParkspaceActivityRefactor.this, AddParkSpaceActivity.class));
                }
            });
        }
        if (mViewStub.getVisibility() != View.VISIBLE) {
            mViewStub.setVisibility(View.VISIBLE);
            mLeft.setVisibility(View.INVISIBLE);
            mRight.setVisibility(View.INVISIBLE);
        }

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
