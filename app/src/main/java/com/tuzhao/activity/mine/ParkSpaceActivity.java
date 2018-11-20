package com.tuzhao.activity.mine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseFragmentAdapter;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.activity.jiguang_notification.MyReceiver;
import com.tuzhao.activity.jiguang_notification.OnLockListenerAdapter;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.fragment.parkspace.AddParkSpaceFragment;
import com.tuzhao.fragment.parkspace.SelectParkSpaceFragment;
import com.tuzhao.fragment.parkspace.ParkSpaceFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.customView.TransformerViewPager;
import com.tuzhao.publicwidget.customView.ZoomOutPageTransformer;
import com.tuzhao.publicwidget.dialog.OpenLockDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/11/14.
 */
public class ParkSpaceActivity extends BaseStatusActivity implements IntentObserver {

    private BaseFragmentAdapter<BaseStatusFragment> mMyParkSpaceAdapter;

    private BaseFragmentAdapter<BaseStatusFragment> mLongRentParkSpaceAdapter;

    private TextView mMyParkSpaceCount;

    private TextView mLongRentParkSpaceCount;

    private TransformerViewPager mMyParkSpaceVp;

    private TransformerViewPager mLongRentParkSpaceVp;

    private OpenLockDialog mOpenLockDialog;

    private Park_Info mOpenLockParkInfo;

    @Override
    protected int resourceId() {
        return R.layout.activity_my_park_space_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mMyParkSpaceCount = findViewById(R.id.my_park_space_tv);
        mLongRentParkSpaceCount = findViewById(R.id.long_rent_park_space_tv);
        mMyParkSpaceVp = findViewById(R.id.my_park_space_vp);
        mLongRentParkSpaceVp = findViewById(R.id.long_rent_park_space_vp);

        mMyParkSpaceAdapter = new BaseFragmentAdapter<>(getSupportFragmentManager());
        mMyParkSpaceVp.setAdapter(mMyParkSpaceAdapter);
        mMyParkSpaceVp.setPageTransformer(false, new ZoomOutPageTransformer());

        mLongRentParkSpaceAdapter = new BaseFragmentAdapter<>(getSupportFragmentManager());
        mLongRentParkSpaceVp.setAdapter(mLongRentParkSpaceAdapter);
        mLongRentParkSpaceVp.setPageTransformer(false, new ZoomOutPageTransformer());

        findViewById(R.id.audit_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AuditParkSpaceActivity.class);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        getParkSpace();
        IntentObserable.registerObserver(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showLoadingDialog();
        getParkSpace();
    }

    @NonNull
    @Override
    protected String title() {
        return "我的车位";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
        if (mOpenLockParkInfo != null) {
            MyReceiver.removeLockListener(mOpenLockParkInfo.getParkLockId());
        }
        if (mOpenLockDialog != null) {
            mOpenLockDialog.setOpenLockStatus(-1);
            mOpenLockDialog.cancel();
        }
    }

    private void getParkSpace() {
        getOkGo(HttpConstants.getParkFromUser)
                .execute(new JsonCallback<Base_Class_List_Info<Park_Info>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(Base_Class_List_Info<Park_Info> o, Call call, Response response) {
                        List<BaseStatusFragment> myParkSpace = new ArrayList<>();
                        List<BaseStatusFragment> longRentParkSpace = new ArrayList<>();
                        for (Park_Info parkInfo : o.data) {
                            if (parkInfo.isLongRentParkSpace()) {
                                longRentParkSpace.add(ParkSpaceFragment.newInstance(parkInfo));
                            } else {
                                myParkSpace.add(ParkSpaceFragment.newInstance(parkInfo));
                            }
                        }

                        mLongRentParkSpaceCount.setText("长租车位（" + longRentParkSpace.size() + "）");
                        longRentParkSpace.add(new SelectParkSpaceFragment());
                        mLongRentParkSpaceAdapter.setData(longRentParkSpace);
                        adjustPage(mLongRentParkSpaceVp);

                        mMyParkSpaceCount.setText("个人车位（" + myParkSpace.size() + "）");
                        myParkSpace.add(new AddParkSpaceFragment());
                        mMyParkSpaceAdapter.setData(myParkSpace);
                        adjustPage(mMyParkSpaceVp);

                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dismmisLoadingDialog();
                        if (!DensityUtil.isException(ParkSpaceActivity.this, e)) {
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

    private void adjustPage(final TransformerViewPager viewPager) {
        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewPager.onPageScrolled(0, 0, 0);
                viewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void onReceive(Intent intent) {
        if (ConstansUtil.DELETE_PARK_SPACE.equals(intent.getAction())) {
            String tag = intent.getStringExtra(ConstansUtil.INTENT_MESSAGE);
            for (int i = 0; i < mMyParkSpaceAdapter.getCount(); i++) {
                if (mMyParkSpaceAdapter.getItem(i).getTAG().equals(tag)) {
                    mMyParkSpaceAdapter.remove(i);
                    adjustPage(mMyParkSpaceVp);
                    break;
                }
            }
        } else if (ConstansUtil.OPEN_LOCK.equals(intent.getAction())) {
            Park_Info parkInfo = intent.getParcelableExtra(ConstansUtil.PARK_SPACE_INFO);
            if (!parkInfo.equals(mOpenLockParkInfo)) {
                if (mOpenLockParkInfo != null) {
                    MyReceiver.removeLockListener(mOpenLockParkInfo.getParkLockId());
                }
                mOpenLockParkInfo = parkInfo;
                mOpenLockDialog = null;
            }
            controlParkLock();
        }
    }

    private void controlParkLock() {
        registerLockListener();
        getOkGo(HttpConstants.userControlParkLock)
                .params("cityCode", mOpenLockParkInfo.getCityCode())
                .params("parkSpaceId", mOpenLockParkInfo.getId())
                .params("controlType", "1")
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> voidBase_class_info, Call call, Response response) {

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mOpenLockDialog.setOpenLockStatus(2);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    showFiveToast("设备不在线");
                                    break;
                                case "102":
                                    showFiveToast("客户端异常，请稍后重试");
                                    break;
                                case "105":
                                    userError();
                                    break;
                                default:
                                    showFiveToast(ConstansUtil.SERVER_ERROR);
                                    break;
                            }
                        }
                    }
                });

    }

    private void registerLockListener() {
        if (mOpenLockDialog == null) {
            mOpenLockDialog = new OpenLockDialog(this, new SuccessCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    if (aBoolean) {
                        //开锁成功
                        showFiveToast("开锁成功");
                        IntentObserable.dispatch(ConstansUtil.OPEN_LOCK_SUCCESS, ConstansUtil.PARK_SPACE_INFO, mOpenLockParkInfo);
                    } else {
                        //重试
                        controlParkLock();
                    }
                }
            });
            OnLockListenerAdapter lockListenerAdapter = new OnLockListenerAdapter() {
                @Override
                public void openSuccess() {
                    super.openSuccess();
                    mOpenLockDialog.setOpenLockStatus(0);
                    mOpenLockDialog.cancelOpenLockAnimator();
                }

                @Override
                public void openFailed() {
                    super.openFailed();
                    mOpenLockDialog.setOpenLockStatus(2);
                    showFiveToast("开锁失败，请稍后重试");
                }

                @Override
                public void openSuccessHaveCar() {
                    super.openSuccessHaveCar();
                    mOpenLockDialog.setOpenLockStatus(1);
                    mOpenLockDialog.cancelOpenLockAnimator();
                    mOpenLockDialog.cancel();
                    showFiveToast("车锁已开");
                }
            };
            MyReceiver.addLockListener(mOpenLockParkInfo.getParkLockId(), lockListenerAdapter);
        }
        mOpenLockDialog.startOpenLock();
    }

}

