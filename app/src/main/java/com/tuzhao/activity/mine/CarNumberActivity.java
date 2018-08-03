package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.adapter.CarNumberAdpater;
import com.tuzhao.application.MyApplication;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by TZL13 on 2017/5/23.
 */

public class CarNumberActivity extends BaseActivity implements View.OnClickListener, CarNumberAdpater.IonSlidingViewClickListener {

    private ImageView mIv_back;
    private TextView mTv_add;
    private RecyclerView mRecyclerView;
    private CarNumberAdpater mCarNumberAdpater;
    private User_Info mUserInfo;
    private List<String> mCar_numbers = new ArrayList<>();
    private String mNumberInTextview = null;

    private LinearLayout linearLayout_nadata;

    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carnumber_layout);
        initView();
        initData();
        initEvent();
        setStyle(true);
    }

    private void initView() {
        mIv_back = findViewById(R.id.id_activity_carnumber_imageView_back);
        mTv_add = findViewById(R.id.id_activity_carnumber_textView_add);
        linearLayout_nadata = findViewById(R.id.id_activity_carnumber_linearlayout_nodata);
        mRecyclerView = findViewById(R.id.id_activity_carnumber_listview_carnumber);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mCarNumberAdpater = new CarNumberAdpater(this);
        mRecyclerView.setAdapter(mCarNumberAdpater);
    }

    private void initData() {
        mUserInfo = UserManager.getInstance().getUserInfo();
        if (getIntent().hasExtra("numberInTextview")) {
            mNumberInTextview = getIntent().getStringExtra("numberInTextview");
            String[] strings = mUserInfo.getCar_number().split(",");
            mCar_numbers.addAll(Arrays.asList(strings));
            mCarNumberAdpater.setData(mCar_numbers);
        } else {
            getCarNumber();
        }
    }

    private void initEvent() {
        mIv_back.setOnClickListener(this);
        mTv_add.setOnClickListener(this);
        mCarNumberAdpater.setOnItemClickListener(new CarNumberAdpater.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (MyApplication.getInstance().getHandler() != null) {
                    Handler handler = MyApplication.getInstance().getHandler();
                    Message message = new Message();
                    message.obj = ((TextView) v).getText().toString();
                    handler.sendMessage(message);
                    finish();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_activity_carnumber_imageView_back:
                finish();
                break;
            case R.id.id_activity_carnumber_textView_add:
                Intent intent = new Intent(CarNumberActivity.this, AddNewCarActivity.class);
                intent.putExtra("cityCode", getIntent().getStringExtra("cityCode"));

                startActivityForResult(intent, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (mCar_numbers.isEmpty()) {
            mCar_numbers.add(data.getStringExtra("car_number"));
            mCarNumberAdpater = new CarNumberAdpater(this, mCar_numbers);
            mRecyclerView.setAdapter(mCarNumberAdpater);
            mCarNumberAdpater.setOnItemClickListener(new CarNumberAdpater.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Handler handler = MyApplication.getInstance().getHandler();
                    Message message = new Message();
                    message.obj = ((TextView) v).getText().toString();
                    handler.sendMessage(message);
                    finish();
                }
            });
        } else {
            mCar_numbers.add(data.getStringExtra("car_number"));
            mCarNumberAdpater.notifyItemInserted(mCar_numbers.size());
        }

        linearLayout_nadata.setVisibility(View.GONE);
    }


    @Override
    public void onDeleteBtnCilck(View view, int position) {
        if (!(mNumberInTextview == null)) {
            if (mNumberInTextview.equals(mCarNumberAdpater.getItemData(position))) {
                Handler handler = MyApplication.getInstance().getHandler();
                Message message = new Message();
                message.obj = null;
                handler.sendMessage(message);
            }
        }
        deleteUserCarNumber(position);
    }

    private void getCarNumber() {
        showLoadingDialog("加载中...");
        OkGo.post(HttpConstants.getCarNumber)
                .tag(CarNumberActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", mUserInfo.getToken())
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> stringBase_class_info, Call call, Response response) {
                        mUserInfo.setCar_number(stringBase_class_info.data);
                        if (!mUserInfo.getCar_number().equals("")) {
                            mCar_numbers.addAll(Arrays.asList(mUserInfo.getCar_number().split(",")));
                            mCarNumberAdpater.setData(mCar_numbers);
                        } else {
                            linearLayout_nadata.setVisibility(View.VISIBLE);
                        }
                        cancelDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        cancelDialog();
                        if (!DensityUtil.isException(CarNumberActivity.this, e)) {
                            linearLayout_nadata.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void deleteUserCarNumber(final int position) {
        showLoadingDialog("正在删除...");
        OkGo.post(HttpConstants.deleteUserCarNumber)
                .tag(CarNumberActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", mUserInfo.getToken())
                .params("car_number", mCar_numbers.get(position))
                .execute(new JsonCallback<Base_Class_Info<User_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<User_Info> responseData, Call call, Response response) {
                        mCarNumberAdpater.removeData(position);
                        cancelDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        cancelDialog();
                        if (!DensityUtil.isException(CarNumberActivity.this, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + "getCollectionDatas" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 901:
                                    MyToast.showToast(CarNumberActivity.this, "服务器正在维护中", 2);
                                    break;
                            }
                        }
                    }
                });
    }

    private void showLoadingDialog(String msg) {
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
        mLoadingDialog = new LoadingDialog(this, msg);
        mLoadingDialog.show();
    }

    private void cancelDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.cancel();
        }
    }

}
