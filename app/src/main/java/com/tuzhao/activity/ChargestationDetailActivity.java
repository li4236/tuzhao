package com.tuzhao.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.tianzhili.www.myselfsdk.chenjing.XStatusBarHelper;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.fragment.chargestationdetail.ChargeDetailFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ChargeStationInfo;
import com.tuzhao.info.CollectionInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.CollectionManager;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.dialog.LoginDialogFragment;
import com.tuzhao.publicwidget.mytoast.MyToast;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TZL12 on 2017/11/16.
 */

public class ChargestationDetailActivity extends BaseActivity {
    private ChargeDetailFragment chargeDetailFragment;
    private Bundle mBundle = new Bundle();
    private FragmentTransaction mFt;
    private ChargeStationInfo chargestation_info = null;
    private String chargestation_id, city_code;
    private ImageView imageview_back, imageview_collection;
    /**
     * 收藏
     */
    private CollectionManager.MessageHolder holder;
    private LoadingDialog mLoadingDialog;
    private LoginDialogFragment loginDialogFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chargestationdetail_layout);
        XStatusBarHelper.tintStatusBar(this, ContextCompat.getColor(this, R.color.w0), 0);
        mBundle = new Bundle();
        initView();
        initData();
        initEvent();
    }

    private void initView() {

        imageview_back = (ImageView) findViewById(R.id.id_activity_chargestationdetail_imageview_back);
        imageview_collection = (ImageView) findViewById(R.id.id_activity_chargestationdetail_imageview_collection);
    }

    private void initData() {

        if (getIntent().hasExtra("chargestation_info")) {
            chargestation_info = (ChargeStationInfo) getIntent().getSerializableExtra("chargestation_info");
        } else {
            chargestation_id = getIntent().getStringExtra("chargestation_id");
            city_code = getIntent().getStringExtra("city_code");
        }
        holder = (CollectionManager.getInstance().checkCollectionDatas(chargestation_info == null ? chargestation_id : chargestation_info.getId(), "2"));
        if (holder.isExist) {
            imageview_collection.setImageDrawable(ContextCompat.getDrawable(ChargestationDetailActivity.this, R.mipmap.ic_scchenggong));
        }

        if (chargestation_info == null) {
            mBundle.putString("chargestation_id", chargestation_id);
            mBundle.putString("city_code", city_code);
            mBundle.putSerializable("chargestation_info", chargestation_info);
        } else {
            mBundle.putSerializable("chargestation_info", chargestation_info);
        }
        mFt = getSupportFragmentManager().beginTransaction();
        if (chargeDetailFragment == null) {
            chargeDetailFragment = new ChargeDetailFragment();
            chargeDetailFragment.setArguments(mBundle);
            mFt.add(R.id.id_activity_chargestationdetail_layout_linerlayout_content, chargeDetailFragment);
        } else {
            mFt.show(chargeDetailFragment);
        }
        mFt.commit();
        mFt = getSupportFragmentManager().beginTransaction();
    }

    private void initEvent() {

        imageview_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageview_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserManager.getInstance().hasLogined()) {
                    holder = (CollectionManager.getInstance().checkCollectionDatas(chargestation_info == null ? chargestation_id : chargestation_info.getId(), "2"));
                    if (holder.isExist) {
                        initLoading("正在取消收藏...");
                        OkGo.post(HttpConstants.deleteCollection)
                                .tag(ChargestationDetailActivity.this)
                                .addInterceptor(new TokenInterceptor())
                                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                                .params("id", CollectionManager.getInstance().getCollection_datas().get(holder.position).getId())
                                .execute(new JsonCallback<Base_Class_Info<CollectionInfo>>() {
                                    @Override
                                    public void onSuccess(Base_Class_Info<CollectionInfo> collection_infoBase_class_info, Call call, Response response) {
                                        if (mLoadingDialog.isShowing()) {
                                            mLoadingDialog.dismiss();
                                        }
                                        MyToast.showToast(ChargestationDetailActivity.this, "已取消收藏", 5);
                                        imageview_collection.setImageDrawable(ContextCompat.getDrawable(ChargestationDetailActivity.this, R.mipmap.ic_shoucang2));
                                        CollectionManager.getInstance().removeOneCollection(holder.position);
                                    }

                                    @Override
                                    public void onError(Call call, Response response, Exception e) {
                                        super.onError(call, response, e);
                                        if (mLoadingDialog.isShowing()) {
                                            mLoadingDialog.dismiss();
                                        }
                                        MyToast.showToast(ChargestationDetailActivity.this, "取消失败", 5);
                                    }
                                });
                    } else {
                        initLoading("正在添加收藏...");
                        OkGo.post(HttpConstants.addCollection)
                                .tag(ChargestationDetailActivity.this)
                                .addInterceptor(new TokenInterceptor())
                                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                                .params("belong_id", chargestation_info == null ? chargestation_id : chargestation_info.getId())
                                .params("type", "2")
                                .params("citycode", chargestation_info == null ? city_code : chargestation_info.getCity_code())
                                .execute(new JsonCallback<Base_Class_Info<CollectionInfo>>() {
                                    @Override
                                    public void onSuccess(Base_Class_Info<CollectionInfo> collection_infoBase_class_list_info, Call call, Response response) {
                                        if (mLoadingDialog.isShowing()) {
                                            mLoadingDialog.dismiss();
                                        }
                                        List<CollectionInfo> collection_datas = CollectionManager.getInstance().getCollection_datas();
                                        if (collection_datas == null) {
                                            collection_datas = new ArrayList<>();
                                        }
                                        collection_infoBase_class_list_info.data.setCitycode(chargestation_info == null ? city_code : chargestation_info.getCity_code());
                                        collection_datas.add(collection_infoBase_class_list_info.data);
                                        CollectionManager.getInstance().setCollection_datas(collection_datas);
                                        MyToast.showToast(ChargestationDetailActivity.this, "收藏成功", 5);
                                        imageview_collection.setImageDrawable(ContextCompat.getDrawable(ChargestationDetailActivity.this, R.mipmap.ic_scchenggong));
                                    }

                                    @Override
                                    public void onError(Call call, Response response, Exception e) {
                                        super.onError(call, response, e);
                                        if (mLoadingDialog.isShowing()) {
                                            mLoadingDialog.dismiss();
                                        }
                                        MyToast.showToast(ChargestationDetailActivity.this, "收藏失败", 5);
                                    }
                                });
                    }

                } else {
                    login();
                }
            }
        });
    }

    public void login() {
        loginDialogFragment = new LoginDialogFragment();
        loginDialogFragment.show(getSupportFragmentManager(), "hahah");
    }

    private void initLoading(String what) {
        mLoadingDialog = new LoadingDialog(ChargestationDetailActivity.this, what);
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
