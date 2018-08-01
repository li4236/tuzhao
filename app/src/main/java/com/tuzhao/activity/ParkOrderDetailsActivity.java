package com.tuzhao.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.okgo.callback.StringCallback;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.activity.mine.CommentPsActivity;
import com.tuzhao.activity.mine.MapPlanLineActivity;
import com.tuzhao.application.MyApplication;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.TimeManager;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.alipay.AuthResult;
import com.tuzhao.publicwidget.alipay.PayResult;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

import static com.tuzhao.publicwidget.alipay.OrderInfoUtil2_0.SDK_AUTH_FLAG;
import static com.tuzhao.publicwidget.alipay.OrderInfoUtil2_0.SDK_PAY_FLAG;

/**
 * Created by TZL12 on 2017/11/13.
 */

public class ParkOrderDetailsActivity extends BaseActivity {

    private TextView textview_warm1, textview_cancleorder, textview_starparking, textview_orderbumber;
    private LoadingDialog mLoadingDialog;

    private ParkOrderInfo parkOrderInfo = null;
    private String parkorder_number = null, citycode = null;
    private DateUtil dateUtil = new DateUtil();

    /**
     * 支付宝支付完成的回调处理
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    //如果消息是支付成功 则SDK正常运行，将随该消息附带的msg.obj强转回map中，建立新的payresult支付结果
                            MessageHolder messageHolder = (MessageHolder) msg.obj;
                    PayResult payResult = new PayResult(messageHolder.result);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息，从支付结果中取到resultinfo
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        requetFinishOrder(messageHolder.parkOrderInfo);
                        Toast.makeText(MyApplication.getInstance(), "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(MyApplication.getInstance(), "支付失败" + resultInfo, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    //如果消息是授权成功 则SDK已经确认
                            AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Toast.makeText(MyApplication.getInstance(), "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(MyApplication.getInstance(), "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkorderdetail_layout);

        initView();
        initData();
        initEvent();
        setStyle(true);
    }

    private void initView() {

        textview_warm1 = findViewById(R.id.id_activity_parkorderdetail_layout_textview_warm1);
        textview_cancleorder = findViewById(R.id.id_activity_parkorderdetail_layout_textview_cancleorder);
        textview_starparking = findViewById(R.id.id_activity_parkorderdetail_layout_textview_starparking);
        textview_orderbumber = findViewById(R.id.id_activity_parkorderdetail_layout_textview_orderbumber);
    }

    private void initData() {

        if (getIntent().hasExtra("parkorderinfo") || getIntent().hasExtra("parkorder_number")) {
            if (getIntent().hasExtra("parkorderinfo")) {
                parkOrderInfo = getIntent().getParcelableExtra("parkorderinfo");
                initViewData(parkOrderInfo);
            } else {
                parkorder_number = getIntent().getStringExtra("parkorder_number");
                citycode = getIntent().getStringExtra("citycode");
                initLoading("加载中...");
                requestTheParkOrder();
            }
        } else {
            finish();
        }
    }

    private void requestTheParkOrder() {
        OkGo.post(HttpConstants.getDetailOfParkOrder)
                .tag(ParkOrderDetailsActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("order_number", parkorder_number)
                .params("citycode", citycode)
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> parkOrderInfoBase_class_info, Call call, Response response) {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        initViewData(parkOrderInfoBase_class_info.data);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        if (!DensityUtil.isException(ParkOrderDetailsActivity.this, e)) {

                        }
                    }
                });
    }

    private void initViewData(final ParkOrderInfo parkOrderinfo) {
        try {
            float minutes;
            switch (Integer.parseInt(parkOrderinfo.getOrder_status())) {
                case 1:
                    ((TextView) findViewById(R.id.id_activity_parkorderdetail_layout_textview_orderstater)).setText("已预约");
                    //计算时间
                    minutes = dateUtil.getTimeDifferenceMinute(TimeManager.getInstance().getNowTime(true, true), parkOrderinfo.getOrder_starttime(), false);
                    Log.e("哈哈哈", "距离停车分钟时间是：" + minutes);
                    if (minutes > 0) {
                        if (minutes > 1440) {
                            textview_warm1.setText("进场时间：" + parkOrderinfo.getOrder_starttime());
                        } else {
                            textview_warm1.setText("距离预计进场时间还有 " + (int) (Math.floor(minutes / 60)) + "小时 " + (int) (Math.floor(minutes % 60)) + "分钟");
                        }
                    } else {
                        textview_warm1.setText("订单超时，系统已自动取消");
                    }
                    initViewDataAgain(parkOrderinfo);
                    findViewById(R.id.id_activity_parkorderdetail_layout_textview_cancleorder).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TipeDialog.Builder builder = new TipeDialog.Builder(ParkOrderDetailsActivity.this);
                            builder.setMessage("确定取消该订单吗？");
                            builder.setTitle("提示");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    initLoading("取消中...");
                                    cancleAppointOrder(parkOrderinfo);
                                }
                            });

                            builder.setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });

                            builder.create().show();
                        }
                    });
                    break;
                case 2:
                    ((TextView) findViewById(R.id.id_activity_parkorderdetail_layout_textview_orderstater)).setText("租用中");
                    //计算时间
                    minutes = dateUtil.getTimeDifferenceMinute(parkOrderinfo.getPark_start_time(), TimeManager.getInstance().getNowTime(true, false), false);
                    if (minutes > 1440) {
                        textview_warm1.setText("已停时长 " + (int) (Math.floor(minutes / 1440)) + "天 " + (int) (Math.floor((minutes % 1440) / 60)) + "小时 " + (int) (Math.floor((minutes % 1440) % 60)) + "分钟");
                    } else if (minutes < 1440 && minutes > 60) {
                        textview_warm1.setText("已停时长 " + (int) (Math.floor(minutes / 60)) + "小时 " + (int) (Math.floor(minutes % 60)) + "分钟");
                    } else {
                        textview_warm1.setText("已停时长 " + minutes + "分钟");
                    }
                    textview_cancleorder.setVisibility(View.GONE);
                    textview_starparking.setText("结束停车");
                    initViewDataAgain(parkOrderinfo);
                    findViewById(R.id.id_activity_parkorderdetail_layout_textview_starparking).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //结束停车
                        }
                    });
                    break;
                case 3:
                    ((TextView) findViewById(R.id.id_activity_parkorderdetail_layout_textview_orderstater)).setText("待付款");
                    //计算时间
                    minutes = dateUtil.getTimeDifferenceMinute(parkOrderinfo.getPark_start_time(), parkOrderinfo.getPark_end_time(), false);
                    textview_warm1.setText("停车时长 " + (int) (Math.floor(minutes / 60)) + "小时 " + (int) (Math.floor(minutes % 60)) + "分钟");
                    textview_cancleorder.setVisibility(View.GONE);
                    textview_starparking.setText("立即付款");
                    initViewDataAgain(parkOrderinfo);
                    findViewById(R.id.id_activity_parkorderdetail_layout_textview_starparking).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //立即付款
                            payV2(ParkOrderDetailsActivity.this, parkOrderinfo.getId(), parkOrderinfo.getCitycode());
                        }
                    });
                    break;
                case 4:
                    ((TextView) findViewById(R.id.id_activity_parkorderdetail_layout_textview_orderstater)).setText("待评价");
                    //计算时间
                    minutes = dateUtil.getTimeDifferenceMinute(parkOrderinfo.getPark_start_time(), parkOrderinfo.getPark_end_time(), false);
                    textview_warm1.setText("停车时长 " + (int) (Math.floor(minutes / 60)) + "小时 " + (int) (Math.floor(minutes % 60)) + "分钟");
                    textview_cancleorder.setText("立即评价");
                    textview_starparking.setText("再次租用");
                    initViewDataAgain(parkOrderinfo);
                    findViewById(R.id.id_activity_parkorderdetail_layout_textview_cancleorder).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //立即评价
                            Intent intent = new Intent(ParkOrderDetailsActivity.this, CommentPsActivity.class);
                            intent.putExtra("parkspace_id", parkOrderinfo.getParkLotId());
                            intent.putExtra("parkspace_img", parkOrderinfo.getPictures().split(",")[0]);
                            intent.putExtra("order_id", parkOrderinfo.getId());
                            intent.putExtra("city_code", parkOrderinfo.getCitycode());
                            intent.putExtra("park_time", parkOrderinfo.getPark_start_time());
                            startActivity(intent);
                        }
                    });
                    findViewById(R.id.id_activity_parkorderdetail_layout_textview_starparking).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //再次租用
                            Intent intent = new Intent(ParkOrderDetailsActivity.this, ParkspaceDetailActivity.class);
                            intent.putExtra("parkspace_id", parkOrderinfo.getParkLotId());
                            intent.putExtra("city_code", parkOrderinfo.getCitycode());
                            startActivity(intent);
                        }
                    });
                    break;
                case 5:
                    ((TextView) findViewById(R.id.id_activity_parkorderdetail_layout_textview_orderstater)).setText("已完成");
                    //计算时间
                    minutes = dateUtil.getTimeDifferenceMinute(parkOrderinfo.getPark_start_time(), parkOrderinfo.getPark_end_time(), false);
                    textview_warm1.setText("停车时长 " + (int) (Math.floor(minutes / 60)) + "小时 " + (int) (Math.floor(minutes % 60)) + "分钟");
                    textview_cancleorder.setVisibility(View.GONE);
                    textview_starparking.setText("再次租用");
                    initViewDataAgain(parkOrderinfo);
                    findViewById(R.id.id_activity_parkorderdetail_layout_textview_starparking).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //再次租用
                            Intent intent = new Intent(ParkOrderDetailsActivity.this, ParkspaceDetailActivity.class);
                            intent.putExtra("parkspace_id", parkOrderinfo.getParkLotId());
                            intent.putExtra("city_code", parkOrderinfo.getCitycode());
                            startActivity(intent);
                        }
                    });
                    break;
                case 6:
                    ((TextView) findViewById(R.id.id_activity_parkorderdetail_layout_textview_orderstater)).setText("已取消");
                    textview_warm1.setText("进场时间：" + parkOrderinfo.getOrder_starttime());
                    textview_cancleorder.setVisibility(View.GONE);
                    textview_starparking.setText("再次租用");
                    initViewDataAgain(parkOrderinfo);
                    findViewById(R.id.id_activity_parkorderdetail_layout_textview_starparking).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //再次租用
                            Intent intent = new Intent(ParkOrderDetailsActivity.this, ParkspaceDetailActivity.class);
                            intent.putExtra("parkspace_id", parkOrderinfo.getParkLotId());
                            intent.putExtra("city_code", parkOrderinfo.getCitycode());
                            startActivity(intent);
                        }
                    });
                    break;
                case 7:
                    ((TextView) findViewById(R.id.id_activity_parkorderdetail_layout_textview_orderstater)).setText("已过期");
                    textview_warm1.setText("进场时间：" + parkOrderinfo.getOrder_starttime());
                    textview_cancleorder.setVisibility(View.GONE);
                    textview_starparking.setText("再次租用");
                    initViewDataAgain(parkOrderinfo);
                    findViewById(R.id.id_activity_parkorderdetail_layout_textview_starparking).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //再次租用
                            Intent intent = new Intent(ParkOrderDetailsActivity.this, ParkspaceDetailActivity.class);
                            intent.putExtra("parkspace_id", parkOrderinfo.getParkLotId());
                            intent.putExtra("city_code", parkOrderinfo.getCitycode());
                            startActivity(intent);
                        }
                    });
                    break;
            }
        } catch (Exception e) {
            finish();
        }
    }

    private void initViewDataAgain(final ParkOrderInfo parkOrderinfo) {
        ((TextView) findViewById(R.id.id_activity_parkorderdetail_layout_textview_psname)).setText(parkOrderinfo.getParkLotName());
        ((TextView) findViewById(R.id.id_activity_parkorderdetail_layout_textview_pname)).setText(parkOrderinfo.getParkSpaceLocationDescribe());
        ((TextView) findViewById(R.id.id_activity_parkorderdetail_layout_textview_popentime)).setText("开放时间（" + parkOrderinfo.getOpen_time() + "）");
        ((TextView) findViewById(R.id.id_activity_parkorderdetail_layout_textview_psaddress)).setText(parkOrderinfo.getParkLotAddress());
        ((TextView) findViewById(R.id.id_activity_parkorderdetail_layout_textview_order_starttime)).setText(parkOrderinfo.getOrder_starttime());
        ((TextView) findViewById(R.id.id_activity_parkorderdetail_layout_textview_order_endtime)).setText(parkOrderinfo.getOrder_endtime());
        ((TextView) findViewById(R.id.id_activity_parkorderdetail_layout_textview_carnumble)).setText(parkOrderinfo.getCar_numble());
        ((TextView) findViewById(R.id.id_activity_parkorderdetail_layout_textview_phonenumber)).setText(parkOrderinfo.getUsername());
        ((TextView) findViewById(R.id.id_activity_parkorderdetail_layout_textview_finefee)).setText("停车超过最迟离场时间将按" + parkOrderinfo.getFine() + "元/小时扣费");
        textview_orderbumber.setText(parkOrderinfo.getOrder_number());
        ((TextView) findViewById(R.id.id_activity_parkorderdetail_layout_textview_ordertime)).setText("下单时间：" + parkOrderinfo.getOrder_time());
        findViewById(R.id.id_activity_parkorderdetail_layout_imageview_connectrent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用intent启动拨打电话
                TipeDialog.Builder builder = new TipeDialog.Builder(ParkOrderDetailsActivity.this);
                builder.setMessage("确定呼叫车位主人吗？");
                builder.setTitle("提示");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + parkOrderinfo.getPark_username()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                builder.create().show();
            }
        });
        findViewById(R.id.id_activity_parkorderdetail_layout_linearlayout_looklineway).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParkOrderDetailsActivity.this, MapPlanLineActivity.class);
                intent.putExtra("lat", parkOrderinfo.getLatitude());
                intent.putExtra("lon", parkOrderinfo.getLongitude());
                startActivity(intent);
            }
        });

        findViewById(R.id.id_activity_parkorderdetail_layout_linearlayout_lookpicture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] a = parkOrderinfo.getPictures().split(",");
                ArrayList<String> imgList = new ArrayList<>();
                for (String aa : a) {
                    if (!aa.equals("-1")) {
                        imgList.add(HttpConstants.ROOT_IMG_URL_PS + aa);
                    }
                }
                if (imgList.size() == 0) {
                    MyToast.showToast(ParkOrderDetailsActivity.this, "暂无图片哦", 3);
                } else {
                    Intent intent = new Intent(ParkOrderDetailsActivity.this, BigPictureActivity.class);
                    intent.putStringArrayListExtra("picture_list", imgList);
                    intent.putExtra("position", 0);
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.id_activity_parkorderdetail_layout_textview_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", parkOrderinfo.getOrder_number());
                cm.setPrimaryClip(mClipData);
                MyToast.showToast(ParkOrderDetailsActivity.this, "已复制到剪切板", 5);
            }
        });
    }

    private void initEvent() {

        findViewById(R.id.id_activity_parkorderdetail_layout_imageview_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void cancleAppointOrder(ParkOrderInfo parkOrderinfo) {
        OkGo.post(HttpConstants.cancleAppointOrder)
                .tag(ParkOrderDetailsActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("order_id", parkOrderinfo.getId())
                .params("citycode", parkOrderinfo.getCitycode())
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> responseData, Call call, Response response) {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        MyToast.showToast(ParkOrderDetailsActivity.this, "取消成功", 5);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        if (!DensityUtil.isException(ParkOrderDetailsActivity.this, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + "getCollectionDatas" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 102:
                                    MyToast.showToast(ParkOrderDetailsActivity.this, "取消失败", 5);
                                    break;
                                case 901:
                                    MyToast.showToast(ParkOrderDetailsActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }

    /**
     * 支付宝支付业务
     *
     * @param activity
     */
    public void payV2(final Activity activity, String order_Id, String citycode) {

        /**
         * 来实现支付宝支付功能
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
//        boolean rsa2 = true;
//        //填写支付参数
//        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2, money, title, content, paynowtime, order_numble);
//        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
//        //将诸多参数签名，签名是否正确可以证明参数是否被不法篡改，
//        String sign = OrderInfoUtil2_0.getSign(params, private_key, rsa2);
//        final String orderInfo = orderParam + "&" + sign;
//        Log.e("正在支付1",orderInfo);

        OkGo.post("http://119.23.207.14/public/index.php/tianzhili/alipayApplyOrder")
                .tag("http://119.23.207.14/public/index.php/tianzhili/alipayApplyOrder")
                .params("order_id", order_Id)
                .params("citycode", citycode)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(final String s, Call call, Response response) {
                        Runnable payRunnable = new Runnable() {

                            @Override
                            public void run() {
                                Log.e("正在支付", s);
                                PayTask alipay = new PayTask(activity);
                                Map<String, String> result = alipay.payV2(s, true);
                                Log.i("msp", result.toString());

                                Message msg = new Message();
                                msg.what = SDK_PAY_FLAG;
                                MessageHolder messageHolder = new MessageHolder();
                                messageHolder.result = result;
                                msg.obj = messageHolder;
                                mHandler.sendMessage(msg);
                            }
                        };

                        Thread payThread = new Thread(payRunnable);
                        payThread.start();
                    }
                });


    }

    private void requetFinishOrder(ParkOrderInfo parkOrderInfo) {
        //请求改变订单状态，完成订单
        OkGo.post(HttpConstants.finishParkOrder)
                .tag(ParkOrderDetailsActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("order_id", parkOrderInfo.getId())
                .params("citycode", parkOrderInfo.getCitycode())
                .params("pass_code", DensityUtil.MD5code(UserManager.getInstance().getUserInfo().getSerect_code() + "*&*" + UserManager.getInstance().getUserInfo().getCreate_time() + "*&*" + UserManager.getInstance().getUserInfo().getId()))
                .execute(new JsonCallback<Base_Class_Info<User_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<User_Info> info, Call call, Response response) {
                    }
                });
    }

    private static class MessageHolder {
        private Map<String, String> result;
        private ParkOrderInfo parkOrderInfo;
    }

    private void initLoading(String what) {
        mLoadingDialog = new LoadingDialog(ParkOrderDetailsActivity.this, what);
        mLoadingDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
    }
}
