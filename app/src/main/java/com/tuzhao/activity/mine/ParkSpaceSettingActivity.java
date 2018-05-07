package com.tuzhao.activity.mine;

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
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.adapter.ParkSpaceRentTimeAdapter;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.EverydayShareTimeInfo;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.ShareTimeInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.ImageUtil;

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

    private ImageView mParkspaceIv;

    private TextView mParkspaceNumber;

    private TextView mParkspaceStatus;

    private TextView mParkspaceLockVoltage;

    private TextView mRentDate;

    private TextView mPauseRentDate;

    private SwitchButton mSwitchButton;

    private ParkSpaceRentTimeAdapter mAdapter;

    private Park_Info mPark_info;

    private static final int REQUEST_CODE = 0x111;

    @Override
    protected int resourceId() {
        return R.layout.activity_park_space_setting_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        if ((mPark_info = (Park_Info) getIntent().getSerializableExtra(ConstansUtil.PARK_SPACE_INFO)) == null) {
            showFiveToast("获取车位信息失败，请稍后重试");
            finish();
        }

        mParkspaceIv = findViewById(R.id.parkspace_iv);
        mParkspaceNumber = findViewById(R.id.parkspace_number);
        mParkspaceStatus = findViewById(R.id.rental_record_park_status);
        mParkspaceLockVoltage = findViewById(R.id.rental_record_electricity);

        mRentDate = findViewById(R.id.park_space_space_setting_renten_date);
        mPauseRentDate = findViewById(R.id.park_space_setting_pause_date);
        RecyclerView recyclerView = findViewById(R.id.park_space_setting_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        mAdapter = new ParkSpaceRentTimeAdapter();
        recyclerView.setAdapter(mAdapter);

        mSwitchButton = findViewById(R.id.park_space_setting_renten_sb);
        mSwitchButton.setCheckedNoEvent(!mPark_info.getPark_status().equals("10"));
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

        mAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                startActivityForResult(EditShareTimeActivity.class, REQUEST_CODE, ConstansUtil.PARK_SPACE_INFO, mPark_info);
            }
        });

        findViewById(R.id.park_space_space_setting_cl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(EditShareTimeActivity.class, REQUEST_CODE, ConstansUtil.PARK_SPACE_INFO, mPark_info);
            }
        });

        findViewById(R.id.park_space_setting_bluetooth_binding).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MyFriendsActivity.class, ConstansUtil.PARK_SPACE_INFO, mPark_info);
            }
        });

        findViewById(R.id.park_space_setting_renten_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(RentalRecordActivity.class, "parkdata", mPark_info);
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
        super.initData();
        getShareTime();
        ImageUtil.showImpPic(mParkspaceIv, mPark_info.getPark_img());
        String parkNumber = "车位编号:" + mPark_info.getPark_number();
        mParkspaceNumber.setText(parkNumber);
        setParkspaceStatus();
        String voltage = "电量:" + (int) ((Double.valueOf(mPark_info.getVoltage()) - 4.8) * 100 / 1.2) + "%";
        mParkspaceLockVoltage.setText(voltage);
    }

    /**
     * 获取车位的共享时间
     */
    private void getShareTime() {
        getOkGo(HttpConstants.getShareTime)
                .params("parkId", mPark_info.getId())
                .params("cityCode", mPark_info.getCitycode())
                .execute(new JsonCallback<Base_Class_Info<ShareTimeInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ShareTimeInfo> o, Call call, Response response) {
                        ShareTimeInfo shareTimeInfo = o.data;
                        mPark_info.setOpen_date(shareTimeInfo.getShareDate());
                        mPark_info.setOpen_time(shareTimeInfo.getEveryDayShareTime());
                        mPark_info.setShareDay(shareTimeInfo.getShareDay());
                        mPark_info.setPauseShareDate(shareTimeInfo.getPauseShareDate());
                        setParkSpaceInfo();
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast("获取共享时间失败，请稍后重试");
                            finish();
                        }
                    }
                });
    }

    /**
     * 设置车位的共享时间以及出租状态
     */
    private void setParkSpaceInfo() {
        mAdapter.clearAll();
        mRentDate.setText(mPark_info.getOpen_date());

        //显示出租时段
        String[] shareDays = mPark_info.getShareDay().split(",");
        if (mPark_info.getOpen_time().equals("-1")) {
            //全天出租的则显示全天
            for (int i = 0; i < shareDays.length; i++) {
                if (shareDays[i].charAt(0) == '1') {
                    mAdapter.addData("全天" + dayToWeek(i + 1));
                }
            }
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
                    return DateUtil.getHourMinuteCalendar(o1.getStartTime()).compareTo(DateUtil.getHourMinuteCalendar(o2.getStartTime()));
                }
            });

            StringBuilder stringBuilder = new StringBuilder();
            for (EverydayShareTimeInfo shareTimeInfo : shareTimeInfos) {
                stringBuilder.append(shareTimeInfo.getStartTime());
                stringBuilder.append(" - ");
                stringBuilder.append(shareTimeInfo.getEndTime());
                stringBuilder.append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

            for (int i = 0; i < shareDays.length; i++) {
                if (shareDays[i].charAt(0) == '1') {
                    mAdapter.justAddData(stringBuilder.toString() + dayToWeek(i + 1));
                }
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

            boolean hasDifferentYear = false;
            int year = 2018;
            for (int i = 0; i < pauseDate.length; i++) {
                pauseCalendar = DateUtil.getYearToDayCalendar(pauseDate[i], false);
                if (i == 0) {
                    year = pauseCalendar.get(Calendar.YEAR);
                }

                if (pauseCalendar.compareTo(nowCalendar) >= 0) {
                    //如果暂停日期在今后的则添加
                    pauseCalendars.add(pauseCalendar);

                    //判断暂停日期是否有隔年的
                    if (i != 0 && !hasDifferentYear) {
                        if (year != (pauseCalendar.get(Calendar.YEAR))) {
                            hasDifferentYear = true;
                        }
                    }
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
                StringBuilder stringBuilder = new StringBuilder();
                for (Calendar calendar : pauseCalendars) {
                    usefulPauseDate.append(DateUtil.getCalendarYearToDay(calendar));
                    usefulPauseDate.append(",");

                    if (hasDifferentYear) {
                        //如果有跨年了的，则显示xxxx年xx月xx日
                        stringBuilder.append(DateUtil.getCalendarYearToDayWithText(calendar));
                    } else {
                        //都在同一年的，则显示xx月xx日
                        stringBuilder.append(DateUtil.getCalendarMonthToDayWithText(calendar));
                    }
                    stringBuilder.append(",");
                }
                usefulPauseDate.deleteCharAt(usefulPauseDate.length() - 1);
                mPark_info.setPauseShareDate(usefulPauseDate.toString());

                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                mPauseRentDate.setText(stringBuilder.toString());
            }

        }

    }

    private void setParkspaceStatus() {
        String parkStatus;
        switch (mPark_info.getPark_status()) {
            case "1":
                parkStatus = "车位状态:正在审核";
                break;
            case "10":
                parkStatus = "车位状态:暂停出租";
                break;
            case "3":
                parkStatus = "车位状态:正在出租";
                break;
            default:
                parkStatus = "车位状态:未知状态";
                break;
        }
        mParkspaceStatus.setText(parkStatus);
    }

    private String dayToWeek(int day) {
        switch (day) {
            case 1:
                return "(每周一)";
            case 2:
                return "(每周二)";
            case 3:
                return "(每周三)";
            case 4:
                return "(每周四)";
            case 5:
                return "(每周五)";
            case 6:
                return "(每周六)";
            case 7:
                return "(每周日)";
            default:
                return "(每周一)";
        }
    }

    private void stopParkspaceRent() {
        if (DateUtil.isInOrderDate(DateUtil.getCurrentYearToMinutes(), mPark_info.getOpen_date().split(" - ")[1] + " 24:00", mPark_info.getOrder_times())) {
            mSwitchButton.setCheckedNoEvent(true);
            showFiveToast("车位已有人预约，暂时不能停止出租哦");
        } else {
            changeParkSpaceStatus(false);
        }
    }

    private void changeParkSpaceStatus(final boolean open) {
        showLoadingDialog("正在修改出租状态");
        getOkGo(HttpConstants.changeParkSpaceStatus)
                .params("parkSpaceId", mPark_info.getId())
                .params("parkSpaceStatus", open ? "1" : "2")
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        mSwitchButton.setCheckedNoEvent(open);
                        if (open) {
                            mPark_info.setPark_status("2");
                        } else {
                            mPark_info.setPark_status("3");
                        }
                        Intent intent = new Intent();
                        intent.putExtra(ConstansUtil.FOR_REQUEST_RESULT, mPark_info);
                        setResult(RESULT_OK, intent);
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mSwitchButton.setCheckedNoEvent(!open);
                        if (!handleException(e)) {

                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data.getSerializableExtra(ConstansUtil.FOR_REQUEST_RESULT) != null) {
                mPark_info = (Park_Info) data.getSerializableExtra(ConstansUtil.FOR_REQUEST_RESULT);
                setParkSpaceInfo();

                Intent intent = new Intent();
                intent.putExtra(ConstansUtil.FOR_REQUEST_RESULT, mPark_info);
                setResult(RESULT_OK, intent);
            }
        }
    }
}
