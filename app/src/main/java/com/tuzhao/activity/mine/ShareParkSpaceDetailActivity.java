package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.tianzhili.www.myselfsdk.pickerview.OptionsPickerView;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.MyFragmentAdapter;
import com.tuzhao.fragment.parkspace.ShareParkSpaceFragment;
import com.tuzhao.info.Park_Info;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juncoder on 2018/9/5.
 */
public class ShareParkSpaceDetailActivity extends BaseStatusActivity implements View.OnClickListener, IntentObserver {

    private ViewPager mViewPager;

    private List<ShareParkSpaceFragment> mFragments;

    private List<Park_Info> mParkInfos;

    private MyFragmentAdapter<ShareParkSpaceFragment> mFragmentAdater;

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
            mFragments.add(ShareParkSpaceFragment.newInstance(mParkInfos.get(i), i, size));
        }

        mViewPager = findViewById(R.id.my_parkspace_vp);

        ImageUtil.showPic((ImageView) findViewById(R.id.modify_friend_nickname_iv), R.drawable.ic_revisenotes);
        ImageUtil.showPic((ImageView) findViewById(R.id.my_parkspace_setting_iv), R.drawable.ic_deleteposition);

        mFragmentAdater = new MyFragmentAdapter<>(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mFragmentAdater);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        findViewById(R.id.modify_friend_nickname_iv).setOnClickListener(this);
        findViewById(R.id.my_parkspace_setting_iv).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mViewPager.setCurrentItem(getIntent().getIntExtra(ConstansUtil.POSITION, 0), false);
                mViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        initDialog();
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
                            // TODO: 2018/9/5
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
