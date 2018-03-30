package com.tuzhao.activity.mine;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.SuspensionIndexBar.bean.ParkBean;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.pickerview.OptionsPickerView;
import com.tianzhili.www.myselfsdk.pickerview.TimePickerView;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.InstallWorkerInfo;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.Park_Space_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DateUtil;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Response;

import static com.tianzhili.www.myselfsdk.pickerview.TimePickerView.Type.HOURS_MINS;
import static com.tianzhili.www.myselfsdk.pickerview.TimePickerView.Type.YEAR_MONTH_DAY;

/**
 * Created by TZL13 on 2017/6/30.
 */

public class AddParkActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEt_park,et_realname;
    private TextView mTt_parkSpace,textview_installtime;
    private TextView textview_begindate, textview_enddate, textview_begintime, textview_endtime, textview_profit_ratio;
    private ParkBean mPark = new ParkBean();
    private LinearLayout linearlayout_chooseparkspace, linearlayout_profit_ratio,linearlayout_sd,linearlayout_sh;
    private CustomDialog mCustomDialog;
    private boolean isSelectHour = true;
    private ImageView imageview_sh,imageview_sd;

    //选择器UI
    TimePickerView timePickerView;

    DateUtil dateUtil = new DateUtil();

    //安装时间选择器
    OptionsPickerView pvOptions;
    private ArrayList<String> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private String install_time = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_park_layout);
        initView();
        initData();
        initEvent();
        setStyle(true);
    }

    private void initView() {
        mEt_park = (EditText) findViewById(R.id.id_activity_add_park_layout_et_park);
        et_realname = (EditText) findViewById(R.id.id_activity_add_park_layout_et_realname);
        textview_installtime = (TextView) findViewById(R.id.id_activity_add_park_layout_textview_installtime);
        mTt_parkSpace = (TextView) findViewById(R.id.id_activity_add_park_layout_textview_parkspace);
        textview_begindate = (TextView) findViewById(R.id.id_activity_add_park_layout_textview_begindate);
        textview_enddate = (TextView) findViewById(R.id.id_activity_add_park_layout_textview_enddate);
        textview_begintime = (TextView) findViewById(R.id.id_activity_add_park_layout_textview_begintime);
        textview_endtime = (TextView) findViewById(R.id.id_activity_add_park_layout_textview_endtime);
        textview_profit_ratio = (TextView) findViewById(R.id.id_activity_add_park_layout_textview_profit_ratio);
        linearlayout_chooseparkspace = (LinearLayout) findViewById(R.id.id_activity_add_park_layout_linearlayout_chooseparkspace);
        linearlayout_profit_ratio = (LinearLayout) findViewById(R.id.id_activity_add_park_layout_linearlayout_profit_ratio);
        linearlayout_sh = (LinearLayout) findViewById(R.id.id_activity_add_park_layout_linearlayout_sh);
        linearlayout_sd = (LinearLayout) findViewById(R.id.id_activity_add_park_layout_linearlayout_sd);
        imageview_sh = (ImageView) findViewById(R.id.id_activity_add_park_layout_imageview_sh);
        imageview_sd = (ImageView) findViewById(R.id.id_activity_add_park_layout_imageview_sd);
    }

    private void initData() {
    }

    private void initEvent() {
        findViewById(R.id.id_activity_add_park_layout_imageview_back).setOnClickListener(this);
        findViewById(R.id.id_activity_add_park_layout_textview_ensure).setOnClickListener(this);
        linearlayout_chooseparkspace.setOnClickListener(this);
        textview_begindate.setOnClickListener(this);
        textview_enddate.setOnClickListener(this);
        textview_begintime.setOnClickListener(this);
        textview_endtime.setOnClickListener(this);
        linearlayout_sh.setOnClickListener(this);
        linearlayout_sd.setOnClickListener(this);
        textview_installtime.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 1:
                //选择停车场页面返回的数据
                if (data.hasExtra("park")) {
                    mPark = (ParkBean) data.getSerializableExtra("park");
                    mTt_parkSpace.setText(mPark.getparkStation());
                    String[] ccc = mPark.getProfit_ratio().split(":");
                    textview_profit_ratio.setText(ccc[0]+" : "+ccc[1]+" : "+ccc[2] + " （车位主 : 物业 : 平台）");
                    linearlayout_profit_ratio.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                if (data.hasExtra("park")) {
                    mPark = (ParkBean) data.getSerializableExtra("park");
                    mTt_parkSpace.setText(mPark.getparkStation());
                    String[] ccc = mPark.getProfit_ratio().split(":");
                    textview_profit_ratio.setText(ccc[0]+" : "+ccc[1]+" : "+ccc[2] + " （车位主 : 物业 : 平台）");
                    linearlayout_profit_ratio.setVisibility(View.VISIBLE);
                }
                break;
            case 102:
                //搜索停车场页面返回的数据
                if (data.hasExtra("park")) {
                    Park_Space_Info park_space_info = (Park_Space_Info) data.getSerializableExtra("park");
                    mPark.setParkID(park_space_info.getId());
                    mPark.setparkStation(park_space_info.getPark_space_name());
                    mPark.setCitycode(park_space_info.getCity_code());
                    mPark.setProfit_ratio(park_space_info.getProfit_ratio());
                    mTt_parkSpace.setText(mPark.getparkStation());
                    String[] ccc = mPark.getProfit_ratio().split(":");
                    textview_profit_ratio.setText(ccc[0]+" : "+ccc[1]+" : "+ccc[2] + " （车位主 : 物业 : 平台）");
                    linearlayout_profit_ratio.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_activity_add_park_layout_imageview_back:
                finish();
                break;
            case R.id.id_activity_add_park_layout_textview_ensure:
                if (mTt_parkSpace.getText().length() > 0 && mEt_park.getText().length() > 0 && et_realname.getText().length() > 0 && textview_begindate.getText().length() > 0 && textview_enddate.getText().length() > 0 && textview_begintime.getText().length() > 0 && textview_endtime.getText().length() > 0 && textview_installtime.getText().length()>0) {
                    showAlertDialog();
                } else {
                    MyToast.showToast(this, "要将信息填写完整哦", 2);
                }
                break;
            case R.id.id_activity_add_park_layout_linearlayout_chooseparkspace:
                Intent intent = new Intent(AddParkActivity.this, SelectParkSpaceActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.id_activity_add_park_layout_textview_begintime:
                //选择开始时间
                closeKeyboard();
                showOptions(true);
                break;
            case R.id.id_activity_add_park_layout_textview_endtime:
                //选择结束时间
                closeKeyboard();
                showOptions(false);
                break;
            case R.id.id_activity_add_park_layout_textview_begindate:
                closeKeyboard();
                showDateOptions(true);
                break;
            case R.id.id_activity_add_park_layout_textview_enddate:
                closeKeyboard();
                showDateOptions(false);
                break;
            case R.id.id_activity_add_park_layout_linearlayout_sh:
                if (!isSelectHour){
                    isSelectHour = true;
                    imageview_sh.setImageDrawable(ContextCompat.getDrawable(AddParkActivity.this,R.mipmap.ic_xuanzhong5));
                    imageview_sd.setImageDrawable(ContextCompat.getDrawable(AddParkActivity.this,R.mipmap.ic_weixuanzhong5));
                    textview_begintime.setEnabled(true);
                    textview_begintime.setBackground(ContextCompat.getDrawable(AddParkActivity.this,R.drawable.frame_gray_1px));
                    textview_begintime.setText("");
                    textview_begintime.setTextColor(ContextCompat.getColor(AddParkActivity.this,R.color.b1));
                    textview_endtime.setEnabled(true);
                    textview_endtime.setBackground(ContextCompat.getDrawable(AddParkActivity.this,R.drawable.frame_gray_1px));
                    textview_endtime.setText("");
                    textview_endtime.setTextColor(ContextCompat.getColor(AddParkActivity.this,R.color.b1));
                }
                break;
            case R.id.id_activity_add_park_layout_linearlayout_sd:
                if (isSelectHour){
                    isSelectHour = false;
                    imageview_sh.setImageDrawable(ContextCompat.getDrawable(AddParkActivity.this,R.mipmap.ic_weixuanzhong5));
                    imageview_sd.setImageDrawable(ContextCompat.getDrawable(AddParkActivity.this,R.mipmap.ic_xuanzhong5));
                    textview_begintime.setEnabled(false);
                    textview_begintime.setBackground(ContextCompat.getDrawable(AddParkActivity.this,R.drawable.frame_gray_noselect_2px));
                    textview_begintime.setText("00:00");
                    textview_begintime.setTextColor(ContextCompat.getColor(AddParkActivity.this,R.color.g6));
                    textview_endtime.setEnabled(false);
                    textview_endtime.setBackground(ContextCompat.getDrawable(AddParkActivity.this,R.drawable.frame_gray_noselect_2px));
                    textview_endtime.setText("23:59");
                    textview_endtime.setTextColor(ContextCompat.getColor(AddParkActivity.this,R.color.g6));
                }
                break;
            case R.id.id_activity_add_park_layout_textview_installtime:
                closeKeyboard();
                showDateAndTimeOptions();
                break;
        }
    }

    public void showAlertDialog() {
        final TipeDialog.Builder builder = new TipeDialog.Builder(this);
        builder.setMessage("是否确定出租车位\n请保持手机通讯正常");
        builder.setTitle("提示");
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.setNegativeButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //设置你的操作事项
                        initLoading("提交中...");
                        sendPark();
                    }
                });
        builder.create().show();
    }

    private void sendPark() {
        OkGo.post(HttpConstants.addUserPark)
                .tag(AddParkActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("parkspace_id", mPark.getParkID())
                .params("citycode",mPark.getCitycode())
                .params("applicant_name",et_realname.getText().toString())
                .params("address_memo", mEt_park.getText().toString())
                .params("available_date", textview_begindate.getText().toString() + " - " + textview_enddate.getText().toString())
                .params("available_time", isSelectHour?(textview_begintime.getText().toString() + " - " + textview_endtime.getText().toString()):"00:00 - 23:59")
                .params("install_time",install_time)
                .execute(new JsonCallback<Base_Class_List_Info<Park_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<Park_Info> responseData, Call call, Response response) {
                        if (mCustomDialog.isShowing()){
                            mCustomDialog.dismiss();
                        }
                        MyToast.showToast(AddParkActivity.this, "提交成功", 2);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (mCustomDialog.isShowing()){
                            mCustomDialog.dismiss();
                        }
                        if (e instanceof ConnectException) {
                            Log.d("TAG", "请求失败，" + " 信息为：连接异常" + e.toString());
                        } else if (e instanceof SocketTimeoutException) {
                            Log.d("TAG", "请求失败，" + " 信息为：超时异常" + e.toString());
                        } else if (e instanceof NoRouteToHostException) {
                            Log.d("TAG", "请求失败，" + " 信息为：没有路由到主机" + e.toString());
                        } else {
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 108:
                                    MyToast.showToast(AddParkActivity.this,"添加失败", 2);
                                    break;
                                case 901:
                                    MyToast.showToast(AddParkActivity.this,"服务器拥挤，请稍后重试", 2);
                                    break;
                            }
                        }
                    }
                });
    }

    private void getInstallWorkerTime() {
        OkGo.post(HttpConstants.getInstallWorkerTime)
                .tag(AddParkActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("parkspace_id", mPark.getParkID())
                .params("citycode",mPark.getCitycode())
                .execute(new JsonCallback<Base_Class_List_Info<InstallWorkerInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<InstallWorkerInfo> responseData, Call call, Response response) {
                        if (mCustomDialog.isShowing()){
                            mCustomDialog.dismiss();
                        }
                        MyToast.showToast(AddParkActivity.this, "提交成功", 2);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (mCustomDialog.isShowing()){
                            mCustomDialog.dismiss();
                        }
                        if (e instanceof ConnectException) {
                            Log.d("TAG", "请求失败，" + " 信息为：连接异常" + e.toString());
                        } else if (e instanceof SocketTimeoutException) {
                            Log.d("TAG", "请求失败，" + " 信息为：超时异常" + e.toString());
                        } else if (e instanceof NoRouteToHostException) {
                            Log.d("TAG", "请求失败，" + " 信息为：没有路由到主机" + e.toString());
                        } else {
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 108:
                                    MyToast.showToast(AddParkActivity.this,"添加失败", 2);
                                    break;
                                case 109:
                                    MyToast.showToast(AddParkActivity.this,"读取用户信息失败", 2);
                                    break;
                                case 101:
                                    MyToast.showToast(AddParkActivity.this,"数据库连接失败", 2);
                                    break;
                                case 203:
                                    MyToast.showToast(AddParkActivity.this,"服务器拥挤，请稍后重试", 2);
                                    break;
                            }
                        }
                    }
                });
    }

    private void showOptions(final boolean isStart) {
        //选项选择器
        timePickerView = new TimePickerView(this, HOURS_MINS);
        timePickerView.show();

        timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                if (isStart) {
                    textview_begintime.setText(sdf.format(date));
                } else {
                    textview_endtime.setText(sdf.format(date));
                }
            }
        });
    }

    private void showDateOptions(final boolean isStart) {
        //选项选择器
        timePickerView = new TimePickerView(this, YEAR_MONTH_DAY);
        timePickerView.show();

        timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                if (isStart) {
                    if (dateUtil.compareNowTime(sdf.format(date),false)) {
                        textview_begindate.setText(sdf.format(date));
                    } else {
                        textview_begindate.setText("");
                        MyToast.showToast(AddParkActivity.this, "需要选择有效时间哦", 5);
                    }
                    if (!dateUtil.compareTwoTime(sdf.format(date), textview_enddate.getText().toString(),false)) {
                        textview_enddate.setText("");
                    }
                } else {
                    if (dateUtil.compareTwoTime(textview_begindate.getText().toString(), sdf.format(date),false)) {
                        if (dateUtil.compareNowTime(sdf.format(date),false)) {
                            textview_enddate.setText(sdf.format(date));
                        } else {
                            textview_enddate.setText("");
                            MyToast.showToast(AddParkActivity.this, "需要选择有效时间哦", 5);
                        }
                    } else {
                        textview_enddate.setText("");
                        MyToast.showToast(AddParkActivity.this, "时间选择不对，需要重新选择哦", 5);
                    }
                }
            }
        });
    }

    private void showDateAndTimeOptions() {
        //选项选择器
        options1Items.clear();
        options2Items.clear();
        options3Items.clear();
        //选项选择器
        pvOptions = new OptionsPickerView(AddParkActivity.this);
        // 初始化列表数据
        dateUtil.initStartParkTimeData(options1Items, options2Items, options3Items);
        //三级联动效果
        pvOptions.setPicker(options1Items, options2Items, options3Items, false);        //设置选择的三级单位
        pvOptions.setLabels(null, "点", "分");

        pvOptions.setCyclic(false);
        //设置默认选中的三级项目
        pvOptions.setSelectOptions(0, 0, 0);
        pvOptions.setTextSize(18);
        //监听确定选择按钮
        pvOptions.show();
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1) + " "+ options2Items.get(options1).get(option2) + " 点 " + options3Items.get(options1).get(option2).get(options3) + " 分";

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + options1);//让日期加N
                install_time = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " " + options2Items.get(options1).get(option2) + ":" + options3Items.get(options1).get(option2).get(options3);
                if (dateUtil.compareNowTime(install_time, true)) {
                    textview_installtime.setText(tx);
                } else {
                    textview_installtime.setText("");
                    MyToast.showToast(AddParkActivity.this, "请选择有效时间哦", 5);
                }
            }
        });
    }

    /**
     * 关闭键盘
     */
    private void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void initLoading(String what) {
        mCustomDialog = new CustomDialog(this, what);
        mCustomDialog.show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCustomDialog!= null){
            mCustomDialog.cancel();
        }
    }
}
