package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.pickerview.OptionsPickerView;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.MyFragmentAdapter;
import com.tuzhao.fragment.parkspace.ShareParkSpaceFragment;
import com.tuzhao.info.Park_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;
import com.tuzhao.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juncoder on 2018/9/5.
 */
public class ShareParkSpaceDetailActivity extends BaseStatusActivity implements View.OnClickListener, IntentObserver {

    private ViewPager mViewPager;

    private List<ShareParkSpaceFragment> mFragments;

    private List<Park_Info> mParkInfos;

    private TextView mApponitmentTv;

    private MyFragmentAdapter<ShareParkSpaceFragment> mFragmentAdater;

    private ViewStub mViewStub;

    private OptionsPickerView<String> mOptionsPickerView;

    @Override
    protected int resourceId() {
        return R.layout.activity_share_park_space_detail_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mParkInfos = getIntent().getParcelableArrayListExtra(ConstansUtil.PARK_SPACE_INFO);
        mFragments = new ArrayList<>(mParkInfos.size());
        for (int i = 0, size = mParkInfos.size(); i < size; i++) {
            mFragments.add(ShareParkSpaceFragment.newInstance(mParkInfos.get(i), (i + 1), size, 0));
        }

        mViewPager = findViewById(R.id.my_parkspace_vp);
        mApponitmentTv = findViewById(R.id.appointment_tv);

        ImageUtil.showPic((ImageView) findViewById(R.id.modify_friend_nickname_iv), R.drawable.ic_revisenotes);
        ImageUtil.showPic((ImageView) findViewById(R.id.my_parkspace_setting_iv), R.drawable.ic_deleteposition);

        mFragmentAdater = new MyFragmentAdapter<>(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mFragmentAdater);
        mViewPager.setCurrentItem(getIntent().getIntExtra(ConstansUtil.POSITION, 0), false);
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

        findViewById(R.id.modify_friend_nickname_iv).setOnClickListener(this);
        findViewById(R.id.my_parkspace_setting_iv).setOnClickListener(this);
        loadData();

        IntentObserable.registerObserver(this);
    }

    @NonNull
    @Override
    protected String title() {
        return "共享车位";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstansUtil.REQUSET_CODE && resultCode == RESULT_OK && data != null) {
            if (data.hasExtra(ConstansUtil.PARK_SPACE_ID)) {
                String parkSpaceId = data.getStringExtra(ConstansUtil.PARK_SPACE_ID);
                for (int i = 0; i < mParkInfos.size(); i++) {
                    if (mParkInfos.get(i).getId().equals(parkSpaceId)) {
                        mFragments.remove(i);
                        initDialog();
                        mFragmentAdater.notifyDataSetChanged();
                        if (mFragments.isEmpty()) {
                            showViewStub();
                        }
                        break;
                    }
                }
            } else if (data.hasExtra(ConstansUtil.FOR_REQEUST_RESULT)) {
                Park_Info parkInfo = data.getParcelableExtra(ConstansUtil.FOR_REQEUST_RESULT);
                for (int i = 0; i < mFragments.size(); i++) {
                    if (mParkInfos.get(i).getId().equals(parkInfo.getId())) {
                        mFragments.get(i).setParkInfo(parkInfo);
                        break;
                    }
                }
            }
        }
    }

    private void loadData() {
        /*showLoadingDialog();
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
                });*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modify_friend_nickname_iv:
                Intent intent = new Intent(this, AuditParkSpaceActivity.class);
                startActivity(intent);
                break;
            case R.id.my_parkspace_setting_iv:
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
                        ViewUtil.showCertificationDialog(ShareParkSpaceDetailActivity.this, "添加车位");
                    } else {
                        Intent intent1 = new Intent(ShareParkSpaceDetailActivity.this, AddParkSpaceActivity.class);
                        startActivity(intent1);
                    }
                }
            });
        }
        if (mViewStub.getVisibility() != View.VISIBLE) {
            mViewStub.setVisibility(View.VISIBLE);
        }

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
