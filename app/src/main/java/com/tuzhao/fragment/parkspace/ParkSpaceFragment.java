package com.tuzhao.fragment.parkspace;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.mine.ParkSpaceSettingActivity;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.info.Park_Info;
import com.tuzhao.publicmanager.TimeManager;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.customView.CircleView;
import com.tuzhao.publicwidget.customView.RoundedRectangleImageView;
import com.tuzhao.publicwidget.customView.VoltageView;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by juncoder on 2018/11/14.
 */
public class ParkSpaceFragment extends BaseStatusFragment implements View.OnClickListener, IntentObserver {

    private Park_Info mParkInfo;

    private RoundedRectangleImageView mImageView;

    private TextView mParkLotName;

    private TextView mParkSpaceDescription;

    private VoltageView mVoltageView;

    private TextView mParkSpaceStatus;

    private CircleView mParkSpaceStatusCv;

    private ImageView mLock;

    private TextView mOpenLock;

    private int mRecentOrderMinutes;

    public static ParkSpaceFragment newInstance(Park_Info parkInfo) {
        ParkSpaceFragment fragment = new ParkSpaceFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstansUtil.PARK_SPACE_INFO, parkInfo);
        fragment.setArguments(bundle);
        fragment.setTAG(fragment.getTAG() + " parkInfoId:" + parkInfo.getId() + " cityCode:" + parkInfo.getCityCode());
        return fragment;
    }

    public void setParkInfo(Park_Info parkInfo) {
        mParkInfo = parkInfo;
        setParkspaceStatus();
    }

    @Override
    protected int resourceId() {
        return R.layout.fragment_park_space_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mParkInfo = getArguments().getParcelable(ConstansUtil.PARK_SPACE_INFO);
        } else if (mParkInfo == null) {
            showFiveToast("打开失败，请稍后重试");
            finish();
        }

        mImageView = findViewById(R.id.park_space_iv);
        mParkLotName = findViewById(R.id.park_lot_name);
        mParkSpaceDescription = findViewById(R.id.park_space_description);
        mVoltageView = findViewById(R.id.voltage_view);
        mParkSpaceStatus = findViewById(R.id.park_space_status);
        mParkSpaceStatusCv = findViewById(R.id.park_space_status_cv);
        mLock = findViewById(R.id.park_space_lock_iv);
        mOpenLock = findViewById(R.id.open_lock);

        findViewById(R.id.park_space_setting_cl).setOnClickListener(this);
        findViewById(R.id.park_space_lock_cl).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        scanOrderTime();
        setParkspaceStatus();
        mParkLotName.setText(mParkInfo.getParkspace_name());
        mParkSpaceDescription.setText(mParkInfo.getLocation_describe());
        mVoltageView.setVoltage((int) ((Double.valueOf(mParkInfo.getVoltage()) - 4.8) * 100 / 1.2));
        ImageUtil.showPicWithNoAnimate(mImageView, mParkInfo.isLongRentParkSpace() ? R.drawable.ic_longtime : R.drawable.ic_owncar);
        IntentObserable.registerObserver(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.park_space_setting_cl:
                Log.e(TAG, "onClick: " + mParkInfo);
                startActivityForResultByFragment(ParkSpaceSettingActivity.class, ConstansUtil.REQUSET_CODE, ConstansUtil.PARK_SPACE_INFO, mParkInfo);
                break;
            case R.id.park_space_lock_cl:
                if (getText(mParkSpaceStatus).equals("未开放")) {
                    showFiveToast("未开放的车位不能开锁哦");
                } else if (getText(mParkSpaceStatus).equals("离线")) {
                    showFiveToast("车锁离线了呢");
                } else if (getText(mOpenLock).equals("开锁")) {
                    if (getText(mParkSpaceStatus).equals("已预约") && mRecentOrderMinutes < 30) {
                        showDialog("开锁", mRecentOrderMinutes + "分钟后有人预约，确定开锁吗？", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                IntentObserable.dispatch(ConstansUtil.OPEN_LOCK, ConstansUtil.PARK_SPACE_INFO, mParkInfo);
                            }
                        });
                    } else {
                        showDialog("开锁", "确定开锁吗？", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                IntentObserable.dispatch(ConstansUtil.OPEN_LOCK, ConstansUtil.PARK_SPACE_INFO, mParkInfo);
                            }
                        });
                    }
                }

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstansUtil.REQUSET_CODE && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                //修改了出租时间
                Park_Info parkInfo = data.getParcelableExtra(ConstansUtil.FOR_REQEUST_RESULT);
                setParkInfo(parkInfo);
            } else if (resultCode == ConstansUtil.RESULT_CODE) {
                //删除车位
                IntentObserable.dispatch(ConstansUtil.DELETE_PARK_SPACE, ConstansUtil.INTENT_MESSAGE, getTAG());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IntentObserable.unregisterObserver(this);
    }

    /**
     * 排除掉那些已过期的订单
     */
    private void scanOrderTime() {
        if (!mParkInfo.getOrder_times().equals("-1") && !mParkInfo.getOrder_times().equals("")) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MILLISECOND, 0);

            Calendar orderCalendar;
            StringBuilder stringBuilder = new StringBuilder();
            for (String string : mParkInfo.getOrder_times().split(",")) {
                orderCalendar = DateUtil.getYearToMinuteCalendar(string.substring(string.indexOf("*") + 1, string.length()));
                if (orderCalendar.compareTo(calendar) >= 0) {
                    stringBuilder.append(string);
                    stringBuilder.append(",");
                }
            }

            if (stringBuilder.length() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            } else {
                stringBuilder.append("-1");
            }

            mParkInfo.setOrder_times(stringBuilder.toString());
        }
    }

    private void setParkspaceStatus() {
        switch (mParkInfo.getPark_status()) {
            case "1":
                mParkSpaceStatus.setText("未开放");
                mParkSpaceStatusCv.setColor(Color.parseColor("#808080"));
                break;
            case "2":
                String status = getParkSpaceStatus();
                mParkSpaceStatus.setText(status);
                switch (status) {
                    case "租用":
                        mParkSpaceStatusCv.setColor(Color.parseColor("#d01d2a"));
                        break;
                    case "停租":
                        mParkSpaceStatusCv.setColor(Color.parseColor("#808080"));
                        break;
                    case "空闲":
                    case "使用":
                        mParkSpaceStatusCv.setColor(Color.parseColor("#1dd0a1"));
                        break;
                    default:
                        mParkSpaceStatusCv.setColor(Color.parseColor("#6a6bd9"));
                        mParkSpaceStatus.setText("已预约");
                        mRecentOrderMinutes = Integer.valueOf(status);
                        break;
                }
                break;
            case "3":
                mParkSpaceStatus.setText("停租");
                mParkSpaceStatusCv.setColor(Color.parseColor("#808080"));
                break;
        }

        switch (mParkInfo.getParkLockStatus()) {
            case "1":
                initCloseLock();
                break;
            case "2":
                initOpenLock();
                break;
            case "3":
                mParkSpaceStatusCv.setColor(Color.parseColor("#808080"));
                mParkSpaceStatus.setText("离线");
                break;
        }
    }

    private String getParkSpaceStatus() {
        if (!mParkInfo.getParking_user_id().equals("-1")) {
            if (!mParkInfo.getParking_user_id().equals(UserManager.getInstance().getUserInfo().getId())) {
                return "租用";
            } else {
                return "使用";
            }
        }

        String nowDate = DateUtil.deleteSecond(TimeManager.getInstance().getCurrentTime());
        Calendar calendar = TimeManager.getInstance().getCurrentCalendar();
        calendar.add(Calendar.MINUTE, 2);
        String afterTwoMinutesDate = DateUtil.getCalenarYearToMinutes(calendar);
        if (DateUtil.notInShareDate(nowDate, afterTwoMinutesDate, mParkInfo.getOpen_date())) {
            return "停租";
        }

        if (DateUtil.isInParkSpacePauseDate(nowDate, afterTwoMinutesDate, mParkInfo.getPauseShareDate())) {
            return "停租";
        }

        if (DateUtil.isNotInShareDay(nowDate, afterTwoMinutesDate, mParkInfo.getShareDay())) {
            return "停租";
        }

        if (DateUtil.isNotInShareTime(nowDate, afterTwoMinutesDate, mParkInfo.getOpen_time())) {
            return "停租";
        }

        String rencentOrder = getRecentOrder();
        if (rencentOrder != null) {
            return rencentOrder;
        }

        return "空闲";
    }

    private String getRecentOrder() {
        if (!mParkInfo.getOrder_times().equals("-1")) {
            String[] orderDate = mParkInfo.getOrder_times().split(",");
            List<Calendar> list = new ArrayList<>(orderDate.length);
            for (String order : orderDate) {
                list.add(DateUtil.getYearToMinuteCalendar(order.split("\\*")[0]));
            }

            Collections.sort(list, new Comparator<Calendar>() {
                @Override
                public int compare(Calendar o1, Calendar o2) {
                    return o1.compareTo(o2);
                }
            });
            Calendar calendar = TimeManager.getInstance().getCurrentCalendar();
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return String.valueOf(DateUtil.getCalendarDistance(calendar, list.get(0)));
        }
        return null;
    }

    /**
     * 初始化为未关锁状态
     */
    private void initCloseLock() {
        mOpenLock.setText("已开锁");
        //ImageUtil.showPic(mLock, R.drawable.ic_unlock);
        mOpenLock.setClickable(false);
    }

    /**
     * 初始化为未开锁状态
     */
    private void initOpenLock() {
        mOpenLock.setText("开锁");
        //ImageUtil.showPic(mLock, R.drawable.ic_lock3);
        mOpenLock.setClickable(true);
    }

    @Override
    public void onReceive(Intent intent) {
        if (ConstansUtil.OPEN_LOCK_SUCCESS.equals(intent.getAction())) {
            Park_Info parkInfo = intent.getParcelableExtra(ConstansUtil.PARK_SPACE_INFO);
            if (mParkInfo.equals(parkInfo)) {
                initCloseLock();
            }
        }
    }
}
