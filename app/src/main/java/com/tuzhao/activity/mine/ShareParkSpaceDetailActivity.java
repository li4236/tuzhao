package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
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

    private TipeDialog mModifyNameDialog;

    private EditText mFirendName;

    @Override
    protected int resourceId() {
        return R.layout.activity_share_park_space_detail_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mParkInfos = getIntent().getParcelableArrayListExtra(ConstansUtil.PARK_SPACE_INFO);
        mFragments = new ArrayList<>(mParkInfos.size());
        for (int i = 0, size = mParkInfos.size(); i < size; i++) {
            if ("".equals(mParkInfos.get(i).getParkSpaceNote())) {
                mParkInfos.get(i).setParkSpaceNote((i + 1) + "号车位");
            }
            mFragments.add(ShareParkSpaceFragment.newInstance(mParkInfos.get(i), size));
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modify_friend_nickname_cl:
                showNameDialog(mViewPager.getCurrentItem());
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

    /**
     * 显示修改亲友备注的对话框
     */
    private void showNameDialog(final int position) {
        if (mModifyNameDialog == null) {
            ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.dialog_edit_layout, null);
            mFirendName = constraintLayout.findViewById(R.id.dialog_et);
            mFirendName.setHint("请输入车位备注");

            mModifyNameDialog = new TipeDialog.Builder(this)
                    .setContentView(constraintLayout)
                    .setTitle("修改备注")
                    .autoDissmiss(false)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mModifyNameDialog.dismiss();
                        }
                    })
                    .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (TextUtils.isEmpty(mFirendName.getText().toString().trim())) {
                                showFiveToast("备注不能为空哦");
                            } else {
                                changeParkSpaceNote(position, getText(mFirendName));
                            }
                        }
                    })
                    .create();
        }
        mModifyNameDialog.show();
        mFirendName.setText(mParkInfos.get(position).getParkSpaceNote());
        mFirendName.setSelection(mFirendName.getText().toString().length());
    }

    private void changeParkSpaceNote(final int position, final String note) {
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
                        showFiveToast("修改成功");
                        dismmisLoadingDialog();
                        mModifyNameDialog.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                case "102":
                                    showFiveToast(ConstansUtil.SERVER_ERROR);
                                    break;
                                case "103":
                                    showFiveToast("备注不能为空哦");
                                    break;
                                case "104":
                                    showFiveToast("修改失败，请稍后重试");
                                    break;
                            }
                        }
                    }
                });
    }

    private void showDeleteParkSpaceDialog(final int position) {
        new TipeDialog.Builder(this)
                .setTitle("移除车位")
                .setMessage("确定移除" + mParkInfos.get(position).getParkSpaceNote() + "吗")
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
        showLoadingDialog("移除中...");
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
