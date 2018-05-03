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
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.ShareTimeInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.ImageUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/3/28.
 */

public class ParkSpaceSettingActivity extends BaseStatusActivity {

    //private TextView mParkSpaceNumber;

    private ImageView mRentalRecordIv;

    private TextView mRentalRecordParkNumber;

    private TextView mRentalRecordParkStatus;

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

        //mParkSpaceNumber = findViewById(R.id.park_space_setting_space_number);
        mRentalRecordIv = findViewById(R.id.rental_record_iv);
        mRentalRecordParkNumber = findViewById(R.id.rental_record_car_number);
        mRentalRecordParkStatus = findViewById(R.id.rental_record_park_status);

        mRentDate = findViewById(R.id.park_space_space_setting_renten_date);
        mPauseRentDate = findViewById(R.id.park_space_setting_pause_date);
        RecyclerView recyclerView = findViewById(R.id.park_space_setting_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ParkSpaceRentTimeAdapter();
        recyclerView.setAdapter(mAdapter);

        mSwitchButton = findViewById(R.id.park_space_setting_renten_sb);
        mSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeParkSpaceStatus(isChecked);
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
                startActivity(BluetoothBindingActivity.class, ConstansUtil.PARK_SPACE_ID, mPark_info.getId());
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
        getOriginTime();
        ImageUtil.showImpPic(mRentalRecordIv, mPark_info.getPark_img());
        String parkNumber = "车位编号:" + mPark_info.getPark_number();
        mRentalRecordParkNumber.setText(parkNumber);
        String parkStatus;
        switch (mPark_info.getPark_status()) {
            case "1":
                parkStatus = "车位状态:正在审核";
                break;
            case "2":
                parkStatus = "车位状态:正在出租";
                break;
            case "3":
                parkStatus = "车位状态:暂未开放";
                break;
            default:
                parkStatus = "车位状态:未知状态";
                break;
        }
        mRentalRecordParkStatus.setText(parkStatus);
    }

    private void getOriginTime() {
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

    private void setParkSpaceInfo() {
        mAdapter.clearAll();
        mRentDate.setText(mPark_info.getOpen_date().replace(",", "-"));

        String[] shareDays = mPark_info.getShareDay().split(",");
        for (int i = 0; i < shareDays.length; i++) {
            if (shareDays[i].charAt(0) == '1') {
                if (mPark_info.getOpen_time().equals("-1") || mPark_info.getOpen_time().equals("")) {
                    mAdapter.addData("全天" + dayToWeek(i + 1));
                } else {
                    mAdapter.addData(mPark_info.getOpen_time() + dayToWeek(i + 1));
                }
            }
        }

        if (mPark_info.getPauseShareDate().equals("-1")
                || mPark_info.getPauseShareDate().equals("")) {
            mPauseRentDate.setText("无");
        } else {
            SimpleDateFormat dateFormat = DateUtil.getYearToDayFormat();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日", Locale.getDefault());
            String[] pauseDate = mPark_info.getPauseShareDate().split(",");
            Date[] dates = new Date[pauseDate.length];
            for (int i = 0; i < pauseDate.length; i++) {
                try {
                    dates[i] = dateFormat.parse(pauseDate[i]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            Arrays.sort(dates, new Comparator<Date>() {
                @Override
                public int compare(Date o1, Date o2) {
                    return o1.compareTo(o2);
                }
            });

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < dates.length; i++) {
                stringBuilder.append(simpleDateFormat.format(dates[i]));
                stringBuilder.append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            mPauseRentDate.setText(stringBuilder.toString());

        }

        mSwitchButton.setChecked(mPark_info.getPark_status().equals("2"));
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
                return "每周一";
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
                        mSwitchButton.setChecked(open);
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
