package com.tuzhao.activity.mine;

import android.content.DialogInterface;
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
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

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

        findViewById(R.id.delete_friend_park_space_cl).setOnClickListener(this);
        findViewById(R.id.modify_friend_nickname_cl).setOnClickListener(this);

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
            case R.id.modify_friend_nickname_cl:

                break;
            case R.id.delete_friend_park_space_cl:
                showDeleteParkSpaceDialog(mViewPager.getCurrentItem());
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

    private void changeParkSpaceNote(final int position, final String note, final TipeDialog tipeDialog) {
        showLoadingDialog("修改中...");
        getOkGo(HttpConstants.changeParkSpaceNote)
                .params("parkSpaceId", mParkInfos.get(position).getId())
                .params("cityCode", mParkInfos.get(position).getCityCode())
                .params("parkSpaceNote", note)
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        mParkInfos.get(position).setParkSpaceNote(note);
                        mFragments.get(position).setParkSpaceNote(note);
                        Bundle bundle = new Bundle();
                        bundle.putString(ConstansUtil.PARK_SPACE_ID, mParkInfos.get(position).getId());
                        bundle.putString(ConstansUtil.CITY_CODE, mParkInfos.get(position).getCityCode());
                        bundle.putString(ConstansUtil.INTENT_MESSAGE, note);
                        IntentObserable.dispatch(ConstansUtil.CHANGE_PARK_SPACE_NOTE, bundle);
                        tipeDialog.dismiss();
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {

                        }
                    }
                });
    }

    private void showDeleteParkSpaceDialog(final int position) {
        new TipeDialog.Builder(this)
                .setTitle("移除车位")
                .setMessage("确定移除" + mParkInfos.get(position).getParkSpaceNoteOrAddress() + "吗")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteParkSpace(position);
                    }
                })
                .create()
                .show();
    }

    private void deleteParkSpace(final int position) {
        showFiveToast("移除中...");
        getOkGo(HttpConstants.deleteFriendParkSpace)
                .params("parkSpaceId", mParkInfos.get(position).getId())
                .params("cityCode", mParkInfos.get(position).getCityCode())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        Bundle bundle = new Bundle();
                        bundle.putString(ConstansUtil.PARK_SPACE_ID, mParkInfos.get(position).getId());
                        bundle.putString(ConstansUtil.CITY_CODE, mParkInfos.get(position).getCityCode());
                        IntentObserable.dispatch(ConstansUtil.DELETE_FRIENT_PARK_SPACE, bundle);
                        mFragments.remove(position);
                        mParkInfos.remove(position);
                        mFragmentAdater.notifyDataSetChanged();
                        dismmisLoadingDialog();
                        showFiveToast("移除车位成功");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {

                        }
                    }
                });
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
