package com.tuzhao.activity.mine;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.circleprogress.CircleProgress;
import com.tianzhili.www.myselfsdk.circleprogress.utils.Constant;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.activity.jiguang_notification.MyReceiver;
import com.tuzhao.activity.jiguang_notification.OnLockListener;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DensityUtil;

import okhttp3.Call;
import okhttp3.Response;

import static com.tuzhao.http.HttpConstants.controlParkLock;

/**
 * Created by TZL12 on 2018/1/4.
 */

public class OpenParkLockActivity extends BaseActivity {

    private CircleProgress circleProgress;
    private TextView textview_tryagain, textview_state;

    private ParkOrderInfo parkOrderInfo;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            textview_tryagain.setEnabled(true);
            textview_tryagain.setBackground(ContextCompat.getDrawable(OpenParkLockActivity.this, R.drawable.little_yuan_yellow_5dp));
            textview_tryagain.setTextColor(ContextCompat.getColor(OpenParkLockActivity.this, R.color.b1));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openparklock_layout);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        OnLockListener lockListener = new OnLockListener() {
            @Override
            public void openSuccess() {
                circleProgress.setValue(100, Constant.DEFAULT_ANIM_TIME);
                textview_state.setText("成功开锁！");
                startfinish();
            }

            @Override
            public void openFailed() {
                textview_state.setText("开锁失败！");
            }

            @Override
            public void openSuccessHaveCar() {
                circleProgress.setValue(100, Constant.DEFAULT_ANIM_TIME);
                startfinish();
                textview_state.setText("车锁已开，因为车位上方有车辆滞留");
            }

            @Override
            public void closeSuccess() {
                circleProgress.setValue(100, Constant.DEFAULT_ANIM_TIME);
                textview_state.setText("成功关锁！");
            }

            @Override
            public void closeFailed() {
                textview_state.setText("关锁失败！");
            }

            @Override
            public void closeFailedHaveCar() {
                textview_state.setText("关锁失败，因为车位上方有车辆滞留");
            }

        };

        MyReceiver.addLockListener(parkOrderInfo.getLockId(), lockListener);

        circleProgress = findViewById(R.id.id_activity_openparklock_layout_circleprogress);
        circleProgress.setValue(30, Constant.DEFAULT_ANIM_TIME);

        textview_tryagain = findViewById(R.id.id_activity_openparklock_layout_textview_tryagain);
        textview_tryagain.setEnabled(false);
        textview_state = findViewById(R.id.id_activity_openparklock_layout_textview_state);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyReceiver.removeLockListener(parkOrderInfo.getLockId());
    }

    private void startfinish() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        }).start();
    }

    private void initData() {

        parkOrderInfo = (ParkOrderInfo) getIntent().getSerializableExtra("orderInfo");

        controlParkLock();
    }

    private void controlParkLock() {
        OkGo.post(controlParkLock)
                .tag(OpenParkLockActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("ctrl_type", "1")
                .params("order_id", parkOrderInfo.getId())
                .params("citycode", parkOrderInfo.getCitycode())
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> parkOrderInfoBase_class_info, Call call, Response response) {
                        circleProgress.setValue(70, Constant.DEFAULT_ANIM_TIME);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 6; i > 0; i--) {
                                    if (i <= 0) {
                                        break;
                                    }
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                handler.sendEmptyMessage(1);
                            }
                        }).start();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!DensityUtil.isException(OpenParkLockActivity.this, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
                                    //暂无自己的车位
                                    textview_state.setText("设备暂时离线，请重试");
                                    break;
                                case 901:
                                    textview_state.setText("开锁失败，请重试");
                                    MyToast.showToast(OpenParkLockActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        } else {
                            textview_state.setText("网络异常，请重试");
                        }
                        textview_tryagain.setEnabled(true);
                        textview_tryagain.setBackground(ContextCompat.getDrawable(OpenParkLockActivity.this, R.drawable.little_yuan_yellow_5dp));
                        textview_tryagain.setTextColor(ContextCompat.getColor(OpenParkLockActivity.this, R.color.b1));
                    }
                });
    }

    private void initEvent() {
        textview_tryagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textview_tryagain.setEnabled(false);
                textview_tryagain.setBackground(ContextCompat.getDrawable(OpenParkLockActivity.this, R.drawable.yuan_little_gray_login_5dp));
                textview_tryagain.setTextColor(Color.WHITE);
                controlParkLock();
                textview_state.setText("开锁中...");
                circleProgress.setValue(0, 100);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mzhuanHandle.sendMessage(new Message());
                    }
                }).start();
            }
        });

        findViewById(R.id.id_activity_openparklock_layout_imageview_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private Handler mzhuanHandle = new Handler() {
        public void handleMessage(Message msg) {
            circleProgress.setValue(30, Constant.DEFAULT_ANIM_TIME);
        }
    };
}
