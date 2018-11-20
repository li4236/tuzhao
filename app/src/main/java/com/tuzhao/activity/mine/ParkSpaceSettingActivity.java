package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.adapter.ParkSpaceRentTimeAdapter;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.EverydayShareTimeInfo;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/3/28.
 */

public class ParkSpaceSettingActivity extends BaseStatusActivity {

    private TextView mParkspaceNumber;

    private TextView mParkspaceStatus;

    private TextView mRentDate;

    private TextView[] mTextViews;

    private TextView mPauseRentDate;

    private SwitchButton mSwitchButton;

    private View mRentModeDivider;

    private TextView mRentMode;

    private TextView mRentModeStatus;

    private SwitchButton mRentModeSb;

    private TextView mFriends;

    private ImageView mFriendsIv;

    private View mFriendsDivider;

    private ParkSpaceRentTimeAdapter mAdapter;

    private Park_Info mPark_info;

    private static final int REQUEST_CODE = 0x111;

    @Override
    protected int resourceId() {
        return R.layout.activity_park_space_setting_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if ((mPark_info = getIntent().getParcelableExtra(ConstansUtil.PARK_SPACE_INFO)) == null) {
            showFiveToast("获取车位信息失败，请稍后重试");
            finish();
        }

        mParkspaceNumber = findViewById(R.id.parkspace_number);
        mParkspaceStatus = findViewById(R.id.park_space_status);
        mRentModeDivider = findViewById(R.id.park_space_rent_mode_divider);
        mRentMode = findViewById(R.id.parkspace_rent_mode_tv);
        mRentModeStatus = findViewById(R.id.park_space_setting_rent_mode_status);
        mRentModeSb = findViewById(R.id.park_space_setting_rent_mode_sb);

        mRentDate = findViewById(R.id.park_space_space_setting_renten_date);
        mPauseRentDate = findViewById(R.id.park_space_setting_pause_date);

        mFriendsDivider = findViewById(R.id.bluetooth_divider);
        mFriends = findViewById(R.id.park_space_setting_bluetooth_binding);
        mFriendsIv = findViewById(R.id.my_friends_iv);

        mTextViews = new TextView[7];
        mTextViews[0] = findViewById(R.id.modify_share_time_monday);
        mTextViews[1] = findViewById(R.id.modify_share_time_tuesday);
        mTextViews[2] = findViewById(R.id.modify_share_time_wednesday);
        mTextViews[3] = findViewById(R.id.modify_share_time_thursday);
        mTextViews[4] = findViewById(R.id.modify_share_time_friday);
        mTextViews[5] = findViewById(R.id.modify_share_time_saturday);
        mTextViews[6] = findViewById(R.id.modify_share_time_sunday);

        RecyclerView recyclerView = findViewById(R.id.park_space_setting_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        mAdapter = new ParkSpaceRentTimeAdapter();
        recyclerView.setAdapter(mAdapter);

        mSwitchButton = findViewById(R.id.park_space_setting_renten_sb);
        mSwitchButton.setCheckedNoEvent(mPark_info.getPark_status().equals("2"));
        mSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    stopParkspaceRent();
                } else {
                    changeParkSpaceStatus(true);
                }
            }
        });

        findViewById(R.id.edit_share_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(EditShareTimeActivity.class, REQUEST_CODE, ConstansUtil.PARK_SPACE_INFO, mPark_info);
            }
        });

        findViewById(R.id.park_space_setting_renten_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(RentalRecordActivity.class, "parkdata", mPark_info);
            }
        });

        findViewById(R.id.delete_park_space).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPark_info.getParking_user_id().equals("-1")) {
                    showFiveToast("该车位已有人停车了，暂时不能删除哦");
                } else if (!mPark_info.getOrder_times().equals("-1") && !mPark_info.getOrder_times().equals("")) {
                    showFiveToast("该车位还有预约的订单，暂时不能删除哦");
                } else {
                    TipeDialog dialog = new TipeDialog.Builder(ParkSpaceSettingActivity.this)
                            .setTitle("删除车位")
                            .setMessage("删除车位后将无法恢复，是否删除?")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteParkspace();
                                }
                            })
                            .create();
                    dialog.show();
                }
            }
        });
    }

    @NonNull
    @Override
    protected String title() {
        return "车位设置";
    }

    @Override
    protected void initData() {
        setParkspaceStatus();
        initLongRentStatus();
        setParkSpaceInfo();
        initMyFriends();
        mParkspaceNumber.setText(mPark_info.getPark_number());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data.getParcelableExtra(ConstansUtil.FOR_REQEUST_RESULT) != null) {
                mPark_info = data.getParcelableExtra(ConstansUtil.FOR_REQEUST_RESULT);
                setParkSpaceInfo();

                Intent intent = new Intent();
                intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, mPark_info);
                setResult(RESULT_OK, intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, mPark_info);
        setResult(RESULT_OK, intent);
    }

    private void setParkspaceStatus() {
        switch (mPark_info.getPark_status()) {
            case "1":
                mParkspaceStatus.setText("未出租");
                break;
            case "2":
                mParkspaceStatus.setText("出租");
                break;
            case "3":
                mParkspaceStatus.setText("暂停");
                break;
        }
    }

    private void initLongRentStatus() {
        if (!mPark_info.canLongRent() || mPark_info.isLongRentParkSpace()) {
            goneView(mRentModeDivider);
            goneView(mRentMode);
            goneView(mRentModeStatus);
            goneView(mRentModeSb);
        } else {
            mRentModeStatus.setText(mPark_info.isLongRent() ? "支持" : "不支持");
            mRentModeSb.setCheckedNoEvent(mPark_info.isLongRent());
            mRentModeSb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        new TipeDialog.Builder(ParkSpaceSettingActivity.this)
                                .setTitle("提示")
                                .setMessage("开启长租会将每日共享时段重置为全天24小时出租，在其他车主长租期间，您将暂时失去对车位的使用权，直到其他车主租期结束！！！")
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mRentModeSb.setCheckedNoEvent(mPark_info.isLongRent());
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        editLongRentStatus();
                                    }
                                })
                                .create()
                                .show();
                    } else {
                        editLongRentStatus();
                    }
                }
            });
        }
    }

    private void initMyFriends() {
        if (mPark_info.isLongRentParkSpace()) {
            goneView(mFriends);
            goneView(mFriendsIv);
            goneView(mFriendsDivider);
        } else {
            mFriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(MyFriendsActivity.class, ConstansUtil.PARK_SPACE_INFO, mPark_info);
                }
            });
        }
    }

    /**
     * 设置车位的共享时间以及出租状态
     */
    private void setParkSpaceInfo() {
        mAdapter.clearAll();
        mRentDate.setText(mPark_info.getOpen_date());

        String[] shareDays = mPark_info.getShareDay().split(",");
        for (int i = 0; i < shareDays.length; i++) {
            if (shareDays[i].equals("1")) {
                mTextViews[i].setBackgroundResource(R.drawable.solid_y2_corner_5dp);
            } else {
                mTextViews[i].setBackgroundResource(R.drawable.solid_g10_corner_5dp);
            }
        }

        //显示出租时段
        if (mPark_info.getOpen_time().equals("-1")) {
            //全天出租的则显示全天
            mAdapter.addData("全天");
        } else {
            //判断出租时段是否有跨天的，并且按开始时段排序
            List<EverydayShareTimeInfo> shareTimeInfos = new ArrayList<>(6);
            String startTime;
            String endTime;
            int position;
            for (String dayShareTime : mPark_info.getOpen_time().split(",")) {
                position = dayShareTime.indexOf(" - ");
                startTime = dayShareTime.substring(0, position);
                endTime = dayShareTime.substring(position + 3, dayShareTime.length());
                if (DateUtil.getYearToMinuteCalendar("2018-04-24 " + startTime).compareTo(DateUtil.getYearToMinuteCalendar("2018-04-24 " + endTime)) >= 0) {
                    //如果出租时间是跨天的则分别显示两个时间段
                    shareTimeInfos.add(new EverydayShareTimeInfo("00:00", endTime));
                    shareTimeInfos.add(new EverydayShareTimeInfo(startTime, "24:00"));
                } else {
                    shareTimeInfos.add(new EverydayShareTimeInfo(startTime, endTime));
                }
            }

            //根据开始的出租时段排序
            Collections.sort(shareTimeInfos, new Comparator<EverydayShareTimeInfo>() {
                @Override
                public int compare(EverydayShareTimeInfo o1, EverydayShareTimeInfo o2) {
                    return DateUtil.getHourMinuteCalendar(o1.getStartDate()).compareTo(DateUtil.getHourMinuteCalendar(o2.getStartDate()));
                }
            });

            for (EverydayShareTimeInfo shareTimeInfo : shareTimeInfos) {
                mAdapter.justAddData(shareTimeInfo.getStartDate() + " - " + shareTimeInfo.getEndDate());
            }
            mAdapter.notifyDataSetChanged();
        }

        //显示暂停出租日期
        if (mPark_info.getPauseShareDate().equals("-1")
                || mPark_info.getPauseShareDate().equals("")) {
            mPauseRentDate.setText("无");
        } else {
            String[] pauseDate = mPark_info.getPauseShareDate().split(",");

            Calendar nowCalendar = DateUtil.getYearToDayCalendar();
            Calendar pauseCalendar;
            List<Calendar> pauseCalendars = new ArrayList<>(pauseDate.length);

            for (int i = 0; i < pauseDate.length; i++) {
                pauseCalendar = DateUtil.getYearToDayCalendar(pauseDate[i], false);

                if (pauseCalendar.compareTo(nowCalendar) >= 0) {
                    //如果暂停日期在今后的则添加
                    pauseCalendars.add(pauseCalendar);
                }
            }

            if (pauseCalendars.isEmpty()) {
                mPauseRentDate.setText("无");
                mPark_info.setPauseShareDate("-1");
            } else {
                Collections.sort(pauseCalendars, new Comparator<Calendar>() {
                    @Override
                    public int compare(Calendar o1, Calendar o2) {
                        return o1.compareTo(o2);
                    }
                });

                StringBuilder usefulPauseDate = new StringBuilder();    //在今后的暂停日期，重新设置，这样跳到修改共享时间那就不用再判断了
                for (Calendar calendar : pauseCalendars) {
                    usefulPauseDate.append(DateUtil.getCalendarYearToDay(calendar));
                    usefulPauseDate.append("，");
                }

                usefulPauseDate.deleteCharAt(usefulPauseDate.length() - 1);
                mPauseRentDate.setText(usefulPauseDate.toString());
                mPark_info.setPauseShareDate(usefulPauseDate.toString().replaceAll("，", ","));
            }

        }

    }

    private void stopParkspaceRent() {
        if (!DateUtil.isNotInOrderDate(DateUtil.getCurrentYearToMinutes(), mPark_info.getOpen_date().split(" - ")[1] + " 24:00", mPark_info.getOrder_times())) {
            mSwitchButton.setCheckedNoEvent(true);
            showFiveToast("车位已有人预约，暂时不能停止出租哦");
        } else {
            changeParkSpaceStatus(false);
        }
    }

    private void deleteParkspace() {
        showLoadingDialog("正在删除车位");
        getOkGo(HttpConstants.deleteParkSpace)
                .params("parkSpaceId", mPark_info.getId())
                .params("cityCode", mPark_info.getCityCode())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        Intent intent = new Intent();
                        intent.putExtra(ConstansUtil.PARK_SPACE_INFO, mPark_info);
                        setResult(ConstansUtil.RESULT_CODE, intent);
                        showFiveToast("删除成功");
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                case "102":
                                case "104":
                                    paramsError();
                                    break;
                                case "103":
                                    userError();
                                    break;
                                case "105":
                                    showFiveToast("服务器异常，删除失败");
                                    break;
                            }
                        }
                    }
                });
    }

    private void changeParkSpaceStatus(final boolean open) {
        showLoadingDialog("正在修改出租状态");
        getOkGo(HttpConstants.editPark)
                .params("park_id", mPark_info.getId())
                .params("citycode", mPark_info.getCityCode())
                .params("park_status", open ? "2" : "3")
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        mSwitchButton.setCheckedNoEvent(open);
                        if (open) {
                            mPark_info.setPark_status("2");
                        } else {
                            mPark_info.setPark_status("3");
                        }
                        setParkspaceStatus();
                        Intent intent = new Intent();
                        intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, mPark_info);
                        setResult(RESULT_OK, intent);
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mSwitchButton.setCheckedNoEvent(!open);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                case "102":
                                case "103":
                                    showFiveToast("客户端异常，请退出重试");
                                    finish();
                                    break;
                                case "104":
                                    showFiveToast("该车位已被预约，暂时不能修改状态哦");
                                    break;
                                case "105":
                                    showFiveToast("修改失败，请稍后重试");
                                    break;
                            }
                        }
                    }
                });
    }

    private void editLongRentStatus() {
        showLoadingDialog("正在修改长租状态");
        getOkGo(HttpConstants.editLongRentStatus)
                .params("parkSpaceId", mPark_info.getId())
                .params("cityCode", mPark_info.getCityCode())
                .params("status", mPark_info.isLongRent() ? "0" : "1")
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        mPark_info.setIsLongRent(!mPark_info.isLongRent());
                        if (mPark_info.isLongRent() && !mPark_info.getOpen_time().equals("-1")) {
                            mPark_info.setOpen_time("-1");
                            mAdapter.getData().clear();
                            mAdapter.justAddData("全天");
                            mAdapter.notifyDataSetChanged();
                        }
                        mRentModeSb.setCheckedNoEvent(mPark_info.isLongRent());
                        mRentModeStatus.setText(mPark_info.isLongRent() ? "支持" : "不支持");

                        Intent intent = new Intent();
                        intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, mPark_info);
                        setResult(RESULT_OK, intent);
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mRentModeSb.setCheckedNoEvent(mPark_info.isLongRent());
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                case "102":
                                case "103":
                                case "104":
                                case "106":
                                    showFiveToast("客户端异常，请退出重试");
                                    finish();
                                    break;
                                case "105":
                                    showFiveToast("该车位已经删除了哦");
                                    Intent intent = new Intent();
                                    intent.putExtra(ConstansUtil.PARK_SPACE_INFO, mPark_info);
                                    setResult(ConstansUtil.RESULT_CODE, intent);
                                    showFiveToast("删除成功");
                                    finish();
                                    break;
                                default:
                                    showFiveToast("修改失败，请稍后重试");
                                    break;
                            }
                        }
                    }
                });
    }

}
