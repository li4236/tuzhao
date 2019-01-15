package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.chenjing.XStatusBarHelper;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.adapter.SearchParkSpaceAdapter;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkLotInfo;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.LocationManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DensityUtil;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TZL12 on 2017/5/23.
 * 搜索相关地址页面
 */

public class SearchParkSpaceActivity extends BaseActivity {

    private AutoCompleteTextView etextview_input;
    private SearchParkSpaceAdapter adapter;
    private ArrayList<ParkLotInfo> mData = new ArrayList<>();
    private ProgressBar progressbar;
    private ImageView imageview_clean;
    private TextView textview_goback, textview_goapply;
    private LinearLayout linearlayout_has_no_search;
    private String citycode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchparkspace_layout);
        XStatusBarHelper.tintStatusBar(this, ContextCompat.getColor(this, R.color.w0), 0);
        initData();//初始化数据
        initView();//初始化控件
        initEvent();//初始化事件
        setStyle(true);
    }

    private void initData() {
        if (getIntent().hasExtra("info_list")) {
            mData = getIntent().getParcelableArrayListExtra("info_list");
            citycode = getIntent().getStringExtra("citycode");
        } else {
            if (progressbar.getVisibility() == View.GONE) {
                progressbar.setVisibility(View.VISIBLE);
            }
            requestParkData();
        }
    }

    private void initView() {

        etextview_input = findViewById(R.id.id_activity_searchparkspace_layout_etextview_input);
        adapter = new SearchParkSpaceAdapter(mData, SearchParkSpaceActivity.this, SearchParkSpaceActivity.this);
        etextview_input.setAdapter(adapter);
        etextview_input.setDropDownVerticalOffset(DensityUtil.dp2px(this, 13));
        etextview_input.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击item后的回调在adapter中处理
            }
        });

        progressbar = findViewById(R.id.id_activity_searchparkspace_layout_progressbar);

        textview_goback = findViewById(R.id.id_activity_searchparkspace_layout_textview_goback);
        textview_goapply = findViewById(R.id.id_activity_searchparkspace_layout_textview_goapply);
        imageview_clean = findViewById(R.id.id_activity_searchparkspace_layout_imageview_clean);
        linearlayout_has_no_search = findViewById(R.id.id_activity_searchparkspace_layout_linearlayout_has_no_search);
    }

    private void initEvent() {
        etextview_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0 || start > 0) {
                    if (imageview_clean.getVisibility() == View.GONE) {
                        imageview_clean.setVisibility(View.VISIBLE);
                    }

                } else {
                    if (imageview_clean.getVisibility() == View.VISIBLE) {
                        imageview_clean.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textview_goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageview_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etextview_input.setText("");
                v.setVisibility(View.GONE);
            }
        });

        adapter.setOnSearchStateChange(new SearchParkSpaceAdapter.OnSearchStateChange() {
            @Override
            public void showSearchState(boolean hasresult) {
                if (hasresult) {
                    linearlayout_has_no_search.setVisibility(View.GONE);
                } else {
                    linearlayout_has_no_search.setVisibility(View.VISIBLE);
                }
            }
        });

        textview_goapply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2018/12/11  跳转到申请停车场的页面
                Intent intent = new Intent(SearchParkSpaceActivity.this, AddParkSpaceActivity.class);
                intent.putExtra("citycode", citycode);
                startActivity(intent);
            }
        });
    }

    private void requestParkData() {
        OkGo.post(HttpConstants.getParkSpaceDatasForCity)//请求数据的接口地址
                .tag(SearchParkSpaceActivity.this)//
                .params("citycode", LocationManager.getInstance().getmAmapLocation().getAdCode() + LocationManager.getInstance().getmAmapLocation().getCityCode() + "")
                .execute(new JsonCallback<Base_Class_List_Info<ParkLotInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<ParkLotInfo> responseData, Call call, Response response) {
                        //请求成功
                        if (progressbar.getVisibility() == View.VISIBLE) {
                            progressbar.setVisibility(View.GONE);
                        }
                        mData = responseData.data;
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (progressbar.getVisibility() == View.VISIBLE) {
                            progressbar.setVisibility(View.GONE);
                        }
                        if (e instanceof ConnectException) {
                            Log.d("TAG", "请求失败，" + " 信息为：连接异常" + e.toString());
                        } else if (e instanceof SocketTimeoutException) {
                            Log.d("TAG", "请求失败，" + " 信息为：超时异常" + e.toString());
                        } else if (e instanceof NoRouteToHostException) {
                            Log.d("TAG", "请求失败，" + " 信息为：没有路由到主机" + e.toString());
                        } else {
                            Log.d("TAG", "请求失败， 信息为：" + "getCollectionDatas" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 102:
                                    //定位失败
                                    MyToast.showToast(SearchParkSpaceActivity.this, "定位失败，退出应用打开定位开关再试试哦", 2);
                                    break;
                                case 103:
                                    //本城市不存在停车场
                                    break;
                                case 901:
                                    MyToast.showToast(SearchParkSpaceActivity.this, "服务器正在维护中", 2);
                                    break;
                            }
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
