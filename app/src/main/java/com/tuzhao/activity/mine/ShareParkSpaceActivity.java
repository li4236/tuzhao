package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.LocationManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.customView.ArrowView;
import com.tuzhao.publicwidget.customView.SkipTopBottomDivider;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/4/9.
 */

public class ShareParkSpaceActivity extends BaseRefreshActivity<Park_Info> implements IntentObserver {

    private DecimalFormat mDecimalFormat;

    private TipeDialog mModifyNameDialog;

    private EditText mFirendName;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.no_address_empty_layout, mRecyclerView, false);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.setDimensionRatio(R.id.no_address_empty_iv, "361:300");
        constraintSet.applyTo(constraintLayout);
        ImageView imageView = constraintLayout.findViewById(R.id.no_address_empty_iv);
        ImageUtil.showPic(imageView, R.drawable.ic_noshare2);
        TextView textView = constraintLayout.findViewById(R.id.no_address_empty_tv);
        textView.setText("暂无好友给您分享车位哦...");
        mRecyclerView.setEmptyView(constraintLayout);
        mRecyclerView.setRefreshEnabled(false);
        mRecyclerView.setLoadingMoreEnable(false);
        mRecyclerView.addItemDecoration(new SkipTopBottomDivider(this, true, true));

        mCommonAdapter.setPlaceholderHeaderView(6);
    }

    @Override
    protected void initData() {
        super.initData();
        mDecimalFormat = new DecimalFormat("0.00");
        IntentObserable.registerObserver(this);
    }

    @Override
    protected RecyclerView.LayoutManager createLayouManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    protected int resourceId() {
        return R.layout.activity_share_park_space_layout;
    }

    @NonNull
    @Override
    protected String title() {
        return "好友车位";
    }

    @Override
    protected void loadData() {
        getOkGo(HttpConstants.getFriendShareParkspace)
                .execute(new JsonCallback<Base_Class_List_Info<Park_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<Park_Info> o, Call call, Response response) {
                        LatLng currentLatLng = null;
                        if (LocationManager.getInstance().hasLocation()) {
                            currentLatLng = new LatLng(LocationManager.getInstance().getmAmapLocation().getLatitude(), LocationManager.getInstance().getmAmapLocation().getLongitude());
                        }

                        for (Park_Info parkInfo : o.data) {
                            if (currentLatLng == null) {
                                parkInfo.setDistance(-1);
                            } else {
                                parkInfo.setDistance(AMapUtils.calculateLineDistance(currentLatLng, new LatLng(parkInfo.getLatitude(), parkInfo.getLongitude())));
                            }
                            parkInfo.setPark_status(getParkSpaceStatus(parkInfo));
                        }

                        if (currentLatLng != null && o.data.size() > 1) {
                            Collections.sort(o.data, new Comparator<Park_Info>() {
                                @Override
                                public int compare(Park_Info o1, Park_Info o2) {
                                    return (int) (o1.getDistance() - o2.getDistance());
                                }
                            });
                        }
                        loadDataSuccess(o);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        loadDataFail(e, new LoadFailCallback() {
                            @Override
                            public void onLoadFail(Exception e) {

                            }
                        });
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
    }

    /**
     * 显示修改亲友备注的对话框
     */
    private void showModifyNoteDialog(final int position) {
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
                            if (getTextLength(mFirendName) > 20) {
                                showFiveToast("备注名长度不能大于20");
                            } else {
                                changeParkSpaceNote(position, getText(mFirendName));
                            }
                        }
                    })
                    .create();
        }
        mModifyNameDialog.show();
        mFirendName.setText(mCommonAdapter.get(position).getUserNoteName());
        mFirendName.setSelection(mFirendName.getText().toString().length());
    }

    private void changeParkSpaceNote(final int position, final String note) {
        showLoadingDialog("修改中...");
        getOkGo(HttpConstants.changeParkSpaceNote)
                .params("parkSpaceId", mCommonAdapter.get(position).getId())
                .params("cityCode", mCommonAdapter.get(position).getCityCode())
                .params("parkSpaceNote", note)
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        Park_Info parkInfo = mCommonAdapter.get(position);
                        Park_Info park_info;
                        for (int i = 0; i < mCommonAdapter.getDataSize(); i++) {
                            park_info = mCommonAdapter.get(i);
                            if (park_info.getUser_id().equals(parkInfo.getUser_id())) {
                                //对该车主的全部车位都修改备注
                                park_info.setUserNoteName(note);
                                mCommonAdapter.notifyDataChange(i, park_info, 1);
                            }
                        }
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

    /**
     * 删除好友分享给我的车位之前获取我在该车位是否有预定记录，如果有的话提醒用户
     */
    private void getOneFriendParkSpaceRecord(final int position) {
        showLoadingDialog();
        getOkGo(HttpConstants.getOneFriendParkSpaceRecord)
                .params("parkSpaceId", mCommonAdapter.get(position).getId())
                .params("cityCode", mCommonAdapter.get(position).getCityCode())
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> o, Call call, Response response) {
                        dismmisLoadingDialog();
                        showDeleteParkSpaceDialog(position, "你在" + o.data.replace("*", "至") + "预定了该车位，确定移除吗？");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dismmisLoadingDialog();
                        String name = "".equals(mCommonAdapter.get(position).getUserNoteName()) ? mCommonAdapter.get(position).getUserName() : mCommonAdapter.get(position).getUserNoteName();
                        showDeleteParkSpaceDialog(position, "确定移除" + name + "的车位吗？");
                    }
                });
    }

    private void showDeleteParkSpaceDialog(final int position, String msg) {
        new TipeDialog.Builder(this)
                .setTitle("移除车位")
                .setMessage(msg)
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
                .params("parkSpaceId", mCommonAdapter.get(position).getId())
                .params("cityCode", mCommonAdapter.get(position).getCityCode())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        deleteParkSpaceSuccess(position);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                case "102":
                                    userError();
                                    break;
                                case "103":
                                    //该数据为空？？？
                                    deleteParkSpaceSuccess(position);
                                    break;
                                case "104":
                                    showFiveToast(ConstansUtil.SERVER_ERROR);
                                    break;
                            }
                        }
                    }
                });
    }

    private void deleteParkSpaceSuccess(int position) {
        mCommonAdapter.notifyRemoveData(position);
        showEmpty();
        dismmisLoadingDialog();
        showFiveToast("移除车位成功");
    }

    private String getParkSpaceStatus(Park_Info park_info) {
        if (park_info.getPark_status().equals("2")) {
            Calendar nowCalendar = Calendar.getInstance();
            Calendar openEndCalendar = DateUtil.getYearToDayCalendar(park_info.getOpen_date().substring(park_info.getOpen_date().indexOf(" - ") + 3, park_info.getOpen_date().length()), false);

            if (nowCalendar.compareTo(openEndCalendar) > 0) {
                //如果车位的开放日期已经过了则不能预定，例如开放日期是2018-06-13 - 2018-09-12，今天是2018-09-13
                return "3";
            }

            //如果周一到周日都不出租则不能预定
            if ("0,0,0,0,0,0,0".equals(park_info.getShareDay())) {
                return "3";
            }
        }

        return "2";
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_share_park_space_layout;
    }

    @Override
    protected void bindData(final BaseViewHolder holder, final Park_Info park_info, final int position) {
        final ArrowView arrowView = holder.getView(R.id.more_iv);
        holder.setText(R.id.share_park_space_space_name, park_info.getPark_space_name())
                .setText(R.id.share_park_space_share_name, "业主：" + park_info.getUserNoteName())
                .setText(R.id.distance_to_distination_tv, park_info.isHaveDistination() ? "距目的地" : "距当前位置")
                .setOnClickListener(R.id.open_lock_tv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (park_info.getPark_status()) {
                            case "2":
                                startActivity(AppointmentParkSpaceActivity.class, ConstansUtil.PARK_SPACE_INFO, park_info);
                                break;
                            case "4":
                                showFiveToast("业主已删除了该车位哦");
                                break;
                            default:
                                showFiveToast("该车位暂时不能使用哦");
                                break;
                        }
                    }
                })
                .setOnClickListener(R.id.more_iv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (arrowView.getGravity() == ArrowView.BOTTOM) {
                            //更多则显示修改备注和移除车位
                            arrowView.setGravity(ArrowView.TOP);
                            holder.goneView(R.id.more_tv)
                                    .showView(R.id.modify_note)
                                    .showView(R.id.delete_park_space);

                        } else {
                            arrowView.setGravity(ArrowView.BOTTOM);
                            holder.showView(R.id.more_tv)
                                    .goneView(R.id.modify_note)
                                    .goneView(R.id.delete_park_space);
                        }
                    }
                })
                .setOnClickListener(R.id.modify_note, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showModifyNoteDialog(position);
                    }
                })
                .setOnClickListener(R.id.delete_park_space, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getOneFriendParkSpaceRecord(position);
                    }
                });

        if (park_info.getDistance() == -1) {
            holder.goneView(R.id.distance_to_distination)
                    .setText(R.id.distance_to_distination_tv, "定位失败");
        } else if (park_info.getDistance() >= 1000) {
            holder.setText(R.id.distance_to_distination, mDecimalFormat.format(park_info.getDistance() / 1000) + "km");
        } else {
            holder.setText(R.id.distance_to_distination, (int) park_info.getDistance() + "m");
        }
    }

    @Override
    protected void bindData(BaseViewHolder holder, Park_Info park_info, int position, List<Object> payloads) {
        super.bindData(holder, park_info, position, payloads);
        if (payloads.size() == 1) {
            holder.setText(R.id.share_park_space_share_name, "业主：" + park_info.getUserNoteName());
        }
    }

    @Override
    public void onReceive(Intent intent) {
        if (Objects.equals(intent.getAction(), ConstansUtil.BOOK_PARK_SPACE)) {
            //预定了之后添加预定记录到车位，否则可以再次预定同样的车位，再次筛选的时候也会不准确
            Park_Info parkInfo = intent.getParcelableExtra(ConstansUtil.INTENT_MESSAGE);

            for (Park_Info park_info : mCommonAdapter.getData()) {
                if (park_info.equals(parkInfo)) {
                    //车位添加预约记录
                    if (park_info.getOrder_times().equals("-1")) {
                        park_info.setOrder_times(parkInfo.getParkInterval());
                    } else {
                        park_info.setOrder_times(park_info.getOrder_times() + "," + parkInfo.getParkInterval());
                    }
                    break;
                }
            }

        }
    }

}
