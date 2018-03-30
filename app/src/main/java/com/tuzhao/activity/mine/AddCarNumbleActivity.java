package com.tuzhao.activity.mine;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.pickerview.OptionsPickerView;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by TZL13 on 2017/5/24.
 */

public class AddCarNumbleActivity extends BaseActivity implements View.OnClickListener {

    private TextView mBtn_ok;
    private EditText mEt_carnumber;
    private ImageView mIv_back;
    private TextView mTv_prefix;
    private CustomDialog mCustomDialog;

    //选择器UI
    OptionsPickerView pvOptions;
    View vMasker;

    private ArrayList<String> options1Items = new ArrayList<String>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<ArrayList<String>>();
    private String mCarNumber;
    private User_Info mUserInfo;
    private Intent mIntent;
    private DateUtil dateUtil = new DateUtil();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcaractivity_layout);
        initView();
        initData();
        initEvent();
        setStyle(true);
    }

    private void initEvent() {
        mBtn_ok.setOnClickListener(this);
        mEt_carnumber.setOnClickListener(this);
        mIv_back.setOnClickListener(this);
        mTv_prefix.setOnClickListener(this);
    }

    private void initData() {
        mUserInfo = UserManager.getInstance().getUserInfo();
        mIntent =getIntent();
    }

    private void initView() {
        mBtn_ok = (TextView) findViewById(R.id.id_activity_addcaractivity_layout_btn_ok);
        mEt_carnumber = (EditText) findViewById(R.id.id_activity_addcaractivity_layout_edittext_carnumber);
        mIv_back = (ImageView) findViewById(R.id.id_activity_addcaractivity_layout_imageView_back);
        mTv_prefix = (TextView) findViewById(R.id.id_activity_addcaractivity_layout_textview_prefix);
        vMasker = findViewById(R.id.vMasker);
        mEt_carnumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    final String temp = s.toString();
                    String tem = temp.substring(temp.length() - 1, temp.length());
                    char[] temC = tem.toCharArray();
                    int mid = temC[0];
                    if (mid >= 97 && mid <= 122) {//小写字母
                        new Handler().postDelayed(new Runnable() {
                            @SuppressLint("DefaultLocale")
                            @Override
                            public void run() {
                                mEt_carnumber.setText(temp.toUpperCase());
                                mEt_carnumber.setSelection(temp.length());
                            }
                        },0);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_activity_addcaractivity_layout_imageView_back:
                finish();
                break;
            case R.id.id_activity_addcaractivity_layout_btn_ok:
                if (mEt_carnumber.getText().toString().isEmpty()) {
                    Toast.makeText(this, "请输入您的车牌号码", Toast.LENGTH_SHORT).show();
                } else if (mEt_carnumber.getText().toString().length() < 5) {
                    Toast.makeText(this, "请输入正确的车牌号码", Toast.LENGTH_SHORT).show();
                } else {
                    showAlertDialog(v);
                }
                break;
            case R.id.id_activity_addcaractivity_layout_textview_prefix:
                showOptions();
                break;
            case R.id.id_activity_addcaractivity_layout_edittext_carnumber:
                break;
        }
    }

    public void showAlertDialog(View view) {
        final TipeDialog.Builder builder = new TipeDialog.Builder(this);
        mCarNumber = mTv_prefix.getText().toString() + mEt_carnumber.getText().toString();
        builder.setMessage("请确认您填写的车牌号：" + mCarNumber);
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
                        if (mUserInfo.getCar_number() == null) {
                            initLoading("添加中...");
                            addUserCarNumber(mCarNumber,true);
                        }else {
                            if (mUserInfo.getCar_number().equals("-1")){
                                initLoading("添加中...");
                                addUserCarNumber(mCarNumber,true);
                            }else {
                                initLoading("添加中...");
                                addUserCarNumber(mCarNumber,false);
                            }
                        }
                    }
                });
        builder.create().show();
    }

    private void addUserCarNumber(String number, final boolean isNull) {
        OkGo.post(HttpConstants.addUserCarNumber)
                .tag(AddCarNumbleActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token",mUserInfo.getToken())
                .params("car_number", number)
                .execute(new JsonCallback<Base_Class_Info<User_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<User_Info> responseData, Call call, Response response) {
                        if (mCustomDialog.isShowing()){
                            mCustomDialog.dismiss();
                        }
                        MyToast.showToast(AddCarNumbleActivity.this, "添加成功", 2);
                        if (isNull){
                            mUserInfo.setCar_number(mCarNumber);
                        }else {
                            mUserInfo.setCar_number(mUserInfo.getCar_number() + "," + mCarNumber);
                        }
                        mIntent.putExtra("car_number", mCarNumber);
                        setResult(0, mIntent);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (mCustomDialog.isShowing()){
                            mCustomDialog.dismiss();
                        }
                        if (!DensityUtil.isException(AddCarNumbleActivity.this,e)){
                            Log.d("TAG", "请求失败， 信息为：" + "addUserCarNumber" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
                                    MyToast.showToast(AddCarNumbleActivity.this, "不可以添加重复的车牌号哦", 5);
                                    break;
                                case 901:
                                    MyToast.showToast(AddCarNumbleActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }

    private void showOptions() {
        //选项选择器
        pvOptions = new OptionsPickerView(this);
        // 初始化列表数据
        dateUtil.initData(options1Items, options2Items);
        //三级联动效果
        pvOptions.setPicker(options1Items, options2Items, false);

        pvOptions.setCyclic(false);
        //设置默认选中的三级项目
        pvOptions.setSelectOptions(0, 0);
        pvOptions.setTextSize(18);
        //监听确定选择按钮
        pvOptions.show();
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1)
                        + options2Items.get(options1).get(option2);
                mTv_prefix.setText(tx);
                vMasker.setVisibility(View.GONE);
            }
        });
    }

    private void initLoading(String what) {
        mCustomDialog = new CustomDialog(this, what);
        mCustomDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCustomDialog != null) {
            mCustomDialog.cancel();
        }
    }
}
