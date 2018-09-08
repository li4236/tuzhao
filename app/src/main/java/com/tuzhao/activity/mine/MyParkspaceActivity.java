package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.pickerview.OptionsPickerView;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.activity.base.MyFragmentAdapter;
import com.tuzhao.fragment.parkspace.MyParkspaceFragment;
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
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;
import com.tuzhao.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/5/17.
 */

public class MyParkspaceActivity extends BaseActivity implements View.OnClickListener, IntentObserver {

    private LoadingDialog mLoadingDialog;

    private ViewPager mViewPager;

    private List<MyParkspaceFragment> mFragments;

    private List<Park_Info> mParkInfos;

    private TextView mApponitmentTv;

    private MyFragmentAdapter mFragmentAdater;

    private ViewStub mViewStub;

    private OptionsPickerView<String> mOptionsPickerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_parkspace_layout_refactor);
        mFragments = new ArrayList<>();
        mParkInfos = new ArrayList<>();

        mViewPager = findViewById(R.id.my_parkspace_vp);
        mApponitmentTv = findViewById(R.id.appointment_tv);

        ImageUtil.showPic((ImageView) findViewById(R.id.appointment_iv), R.drawable.ic_time2);
        ImageUtil.showPic((ImageView) findViewById(R.id.add_park_space_iv), R.drawable.ic_addposition);
        ImageUtil.showPic((ImageView) findViewById(R.id.my_parkspace_setting_iv), R.drawable.ic_setting2);

        mFragmentAdater = new MyFragmentAdapter<>(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mFragmentAdater);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mApponitmentTv.setText(mFragments.get(position).getAppointmentTime());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        findViewById(R.id.toolbar_back).setOnClickListener(this);
        findViewById(R.id.audit_tv).setOnClickListener(this);
        findViewById(R.id.appointment_cl).setOnClickListener(this);
        findViewById(R.id.add_park_space_cl).setOnClickListener(this);
        findViewById(R.id.my_parkspace_setting).setOnClickListener(this);
        loadData();

        IntentObserable.registerObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstansUtil.REQUSET_CODE) {
            if (resultCode == RESULT_OK) {
                //修改了出租时间
                Park_Info parkInfo = data.getParcelableExtra(ConstansUtil.FOR_REQEUST_RESULT);
                for (int i = 0; i < mFragments.size(); i++) {
                    if (mParkInfos.get(i).getId().equals(parkInfo.getId()) && mParkInfos.get(i).getCityCode().equals(parkInfo.getCityCode())) {
                        mParkInfos.set(i, parkInfo);
                        mFragments.get(i).setParkInfo(parkInfo);
                        break;
                    }
                }
            } else if (resultCode == ConstansUtil.RESULT_CODE) {
                //删除车位
                Park_Info parkInfo = data.getParcelableExtra(ConstansUtil.PARK_SPACE_INFO);
                for (int i = 0; i < mParkInfos.size(); i++) {
                    if (mParkInfos.get(i).equals(parkInfo)) {
                        mFragments.remove(i);
                        initDialog();
                        mFragmentAdater.notifyDataSetChanged();
                        if (mFragments.isEmpty()) {
                            showViewStub();
                        }
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
                            mParkInfos.addAll(o.data);
                            int size = mParkInfos.size();
                            for (int i = 0; i < size; i++) {
                                mFragments.add(MyParkspaceFragment.newInstance(mParkInfos.get(i), i, size));
                            }
                            initDialog();
                            mFragmentAdater.notifyDataSetChanged();
                            findViewById(R.id.bottom_cl).setVisibility(View.VISIBLE);
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
                        if (!DensityUtil.isException(MyParkspaceActivity.this, e)) {
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
            case R.id.audit_tv:
                Intent intent = new Intent(this, AuditParkSpaceActivity.class);
                startActivity(intent);
                break;
            case R.id.add_park_space_cl:
                if (!UserManager.getInstance().getUserInfo().isCertification()) {
                    ViewUtil.showCertificationDialog(MyParkspaceActivity.this, "添加车位");
                } else {
                    Intent intent1 = new Intent(MyParkspaceActivity.this, AddParkSpaceActivity.class);
                    startActivity(intent1);
                }
                break;
            case R.id.my_parkspace_setting:
                Intent intent2 = new Intent(this, ParkSpaceSettingActivity.class);
                intent2.putExtra(ConstansUtil.PARK_SPACE_INFO, mParkInfos.get(mViewPager.getCurrentItem()));
                startActivityForResult(intent2, ConstansUtil.REQUSET_CODE);
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
                    if (!UserManager.getInstance().getUserInfo().isCertification()) {
                        ViewUtil.showCertificationDialog(MyParkspaceActivity.this, "添加车位");
                    } else {
                        Intent intent1 = new Intent(MyParkspaceActivity.this, AddParkSpaceActivity.class);
                        startActivity(intent1);
                    }
                }
            });
        }
        if (mViewStub.getVisibility() != View.VISIBLE) {
            mViewStub.setVisibility(View.VISIBLE);
        }
        findViewById(R.id.bottom_cl).setVisibility(View.GONE);
    }

    private void initDialog() {
        ArrayList<String> parkSpaceName = new ArrayList<>();
        for (int i = 0; i < mFragments.size(); i++) {
            parkSpaceName.add(mParkInfos.get(i).getLocation_describe());
        }
        mOptionsPickerView = new OptionsPickerView<>(this);
        mOptionsPickerView.setTitle("我的车位");
        mOptionsPickerView.setPicker(parkSpaceName);
        mOptionsPickerView.setTextSize(16);
        mOptionsPickerView.setCyclic(false);
        mOptionsPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                mViewPager.setCurrentItem(options1, true);
            }
        });
    }

    private void showDialog() {
        if (mOptionsPickerView != null) {
            mOptionsPickerView.setSelectOptions(mViewPager.getCurrentItem());
            mOptionsPickerView.show();
        }
    }

    @Override
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ConstansUtil.SHOW_DIALOG:
                    showDialog();
                    break;
                case ConstansUtil.LEFT:
                    if (mViewPager.getCurrentItem() != 0) {
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
                    }
                    break;
                case ConstansUtil.RIGHT:
                    if (mViewPager.getCurrentItem() != mFragments.size() - 1) {
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                    }
                    break;
            }
        }
    }

}
