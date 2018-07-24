package com.tuzhao.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.activity.navi.Constant;
import com.tuzhao.activity.navi.DictationJsonParseUtil;
import com.tuzhao.activity.navi.VoiceDialog;
import com.tuzhao.adapter.SearchAddressAdapter;
import com.tuzhao.adapter.SearchAddressHistoryAdapter;
import com.tuzhao.info.Search_Address_Info;
import com.tuzhao.publicmanager.LocationManager;
import com.tuzhao.publicwidget.db.DatabaseImp;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by TZL12 on 2017/5/23.
 * 搜索相关地址页面
 */

public class SearchAddressActivity extends BaseActivity {

    private String whatPage, city;
    private DatabaseImp databaseImp;
    private List<Search_Address_Info> historyDatas;

    private AutoCompleteTextView etextview_input;
    private SearchAddressAdapter adapter;
    private ProgressBar progressbar;
    private ImageView imageview_clean;
    private TextView textview_goback, linearlayout_clean;
    private ImageView imageview_mic;
    private ConstraintLayout linearlayout_downpart;

    /**
     * 定位相关
     */
    private AMapLocationClient locationClient;
    private String keyword;

    /**
     * 语音识别
     */
    private String dictationResultStr = "[", finalResult;
    private VoiceDialog mVoiceDialog;
    private Dialog mDialog;
    private SpeechRecognizer mAsr;// 语音识别对象
    private RecognizerDialog iatDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private String mCloudGrammar = null;// 云端语法文件
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private boolean mUserReject;//用户拒绝开启录音权限

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address_layout_refactor);
        initLocation();//初始化定位
        initData();//初始化数据
        initView();//初始化控件
        initEvent();//初始化事件

        initVoice();
        setStyle(true);
    }

    private void initData() {

        if (getIntent().hasExtra("whatPage")) {

            whatPage = getIntent().getStringExtra("whatPage");
            city = getIntent().getStringExtra("cityCode");
            if (city == null) {
                locationClient.startLocation();//启动定位
            }
        } else {
            finish();
        }
    }

    private void initView() {
        linearlayout_downpart = findViewById(R.id.id_activity_searchaddress_layout_linearlayout_downpart);
        databaseImp = new DatabaseImp(SearchAddressActivity.this);
        historyDatas = databaseImp.getSearchLog();
        if (historyDatas.size() > 0) {
            ListView layout_listview = findViewById(R.id.id_activity_searchaddress_layout_listview);
            SearchAddressHistoryAdapter historyAdapter = new SearchAddressHistoryAdapter(historyDatas, SearchAddressActivity.this);
            layout_listview.setAdapter(historyAdapter);
            layout_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent;
                    switch (Integer.parseInt(whatPage)) {
                        case 1:
                            intent = new Intent(SearchAddressActivity.this, MainActivity.class);
                            intent.putExtra("keyword", historyDatas.get(position).getKeyword());
                            intent.putExtra("lat", historyDatas.get(position).getLatitude() + "");
                            intent.putExtra("lon", historyDatas.get(position).getLongitude() + "");
                            intent.putExtra("citycode", historyDatas.get(position).getCitycode());
                            setResult(1, intent);
                            finish();
                            break;
                        case 2:
                            intent = new Intent(SearchAddressActivity.this, MainActivity.class);
                            intent.putExtra("keyword", historyDatas.get(position).getKeyword());
                            intent.putExtra("lat", historyDatas.get(position).getLatitude() + "");
                            intent.putExtra("lon", historyDatas.get(position).getLongitude() + "");
                            intent.putExtra("citycode", historyDatas.get(position).getCitycode());
                            setResult(2, intent);
                            finish();
                            break;
                    }
                    finish();
                }
            });
        } else {
            linearlayout_downpart.setVisibility(View.GONE);
        }

        etextview_input = findViewById(R.id.id_activity_searchaddress_layout_etextview_input);
        adapter = new SearchAddressAdapter(SearchAddressActivity.this);
        etextview_input.setAdapter(adapter);
        etextview_input.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etextview_input.setText(adapter.get(position).getKeyword());
                Intent intent;
                switch (Integer.parseInt(whatPage)) {
                    case 1:
                        intent = new Intent(SearchAddressActivity.this, MainActivity.class);
                        intent.putExtra("keyword", adapter.get(position).getKeyword());
                        intent.putExtra("lat", adapter.get(position).getLatitude() + "");
                        intent.putExtra("lon", adapter.get(position).getLongitude() + "");
                        intent.putExtra("citycode", adapter.get(position).getCitycode());
                        setResult(1, intent);
                        finish();
                        break;
                    case 2:
                        intent = new Intent(SearchAddressActivity.this, MainActivity.class);
                        intent.putExtra("keyword", adapter.get(position).getKeyword());
                        intent.putExtra("lat", adapter.get(position).getLatitude() + "");
                        intent.putExtra("lon", adapter.get(position).getLongitude() + "");
                        intent.putExtra("citycode", adapter.get(position).getCitycode());
                        setResult(2, intent);
                        finish();
                        break;
                }
                if (historyDatas.size() > 5) {
                    databaseImp.deleteSearchLog("");
                    databaseImp.insertSearchLog(adapter.get(position));
                } else {
                    databaseImp.insertSearchLog(adapter.get(position));
                }

                finish();
            }
        });

        progressbar = findViewById(R.id.id_activity_searchaddress_layout_progressbar);

        textview_goback = findViewById(R.id.id_activity_searchaddress_layout_textview_goback);
        imageview_clean = findViewById(R.id.id_activity_searchaddress_layout_imageview_clean);

        if (whatPage.equals("1") || whatPage.equals("2")) {
            keyword = getIntent().getStringExtra("keyword");
            if (!keyword.equals("")) {
                etextview_input.setText(keyword);
                etextview_input.setSelection(keyword.length());//光标移动到最后
                if (progressbar.getVisibility() == View.GONE) {
                    progressbar.setVisibility(View.VISIBLE);
                }
                if (imageview_clean.getVisibility() == View.GONE) {
                    imageview_clean.setVisibility(View.VISIBLE);
                }
                SearchWord(etextview_input.getText().toString());
            }
        }

        linearlayout_clean = findViewById(R.id.id_activity_searchaddress_layout_linearlayout_clean);

        imageview_mic = findViewById(R.id.id_activity_searchaddres_layout_imageview_mic);
    }

    private void initEvent() {
        etextview_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((count > 0 || start > 0) || s.toString().length() > 0) {
                    if (progressbar.getVisibility() == View.GONE) {
                        progressbar.setVisibility(View.VISIBLE);
                    }

                    if (imageview_clean.getVisibility() == View.GONE) {
                        imageview_clean.setVisibility(View.VISIBLE);
                    }

                    SearchWord(etextview_input.getText().toString());
                } else {
                    if (imageview_clean.getVisibility() == View.VISIBLE) {
                        imageview_clean.setVisibility(View.GONE);
                    }
                    if (progressbar.getVisibility() == View.VISIBLE) {
                        progressbar.setVisibility(View.GONE);
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
                if (LocationManager.getInstance().hasLocation()) {
                    //返回主页的城市选择
                    Intent intent = new Intent(SearchAddressActivity.this, MainActivity.class);
                    setResult(2, intent);
                    finish();
                } else {
                    finish();
                }
            }
        });

        imageview_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etextview_input.setText("");
                v.setVisibility(View.GONE);
                if (progressbar.getVisibility() == View.VISIBLE) {
                    progressbar.setVisibility(View.GONE);
                }
            }
        });

        linearlayout_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TipeDialog.Builder builder = new TipeDialog.Builder(SearchAddressActivity.this);
                builder.setMessage("确定清空历史记录吗？");
                builder.setTitle("提示");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //清除数据库内容并隐藏下部
                        databaseImp.deleteSearchLog("-1");
                        linearlayout_downpart.setVisibility(View.GONE);
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
    }

    private void initLocation() {

        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        AMapLocationClientOption locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void SearchWord(String keyword) {
        PoiSearch.Query query = new PoiSearch.Query(keyword, "");
        query.setLocation(new LatLonPoint(LocationManager.getInstance().getmAmapLocation().getLatitude(), LocationManager.getInstance().getmAmapLocation().getLongitude()));
        query.setDistanceSort(false);
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
//        query.setPageNum(currentPage);//设置查询页码
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int i) {
                List<Search_Address_Info> list = new ArrayList<>();
                for (PoiItem pois : poiResult.getPois()) {
                    Search_Address_Info info = new Search_Address_Info();
                    info.setKeyword(pois.getTitle());
                    info.setLatitude(pois.getLatLonPoint().getLatitude());
                    info.setLongitude(pois.getLatLonPoint().getLongitude());
                    info.setCitycode(pois.getCityCode());
                    list.add(info);
                }

                if (progressbar.getVisibility() == View.VISIBLE) {
                    progressbar.setVisibility(View.GONE);
                }

                adapter.setNewData(list);
                etextview_input.setAdapter(adapter);
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        });
        //调用 PoiSearch 的 searchPOIAsyn() 方法发送请求。
        poiSearch.searchPOIAsyn();
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {

        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(true);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {

                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {

                    //定位成功以后的回调
                    Log.e("dsad", "搜索页面的定位成功了");
                    city = location.getCityCode();

                    if (etextview_input.getText().length() > 0) {
                        SearchWord(etextview_input.getText().toString());
                    }
                } else {
                    //定位失败
                    Log.e("dsa", "定位失败");
                }
            } else {
                //定位失败
                Log.e("dsa", "定位失败");
            }
        }
    };

    /**
     * 监听Back键按下事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (LocationManager.getInstance().hasLocation()) {
                //返回主页的城市选择
                Intent intent = new Intent(SearchAddressActivity.this, MainActivity.class);
                setResult(2, intent);
                finish();
            } else {
                finish();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void initVoice() {
        //1.创建SpeechRecognizer对象，第二个参数：本地识别时传InitListener
        final SpeechRecognizer mIat = SpeechRecognizer.createRecognizer(SearchAddressActivity.this, null);
        //dialogManager = DialogManager.getInstance();
        //recordDialogShow = dialogManager.recordDialogShow(this);
        mVoiceDialog = new VoiceDialog(this);
        mDialog = mVoiceDialog.createDialog();
        //2.设置听写参数，详见SDK中《MSC Reference Manual》文件夹下的SpeechConstant类
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");

        //听写监听器
        final RecognizerListener mRecoListener = new RecognizerListener() {
            public void onResult(RecognizerResult results, boolean isLast) {
                if (!isLast) {
                    dictationResultStr += results.getResultString() + ",";
                } else {
                    dictationResultStr += results.getResultString() + "]";

                    finalResult = DictationJsonParseUtil.parseJsonData(dictationResultStr);
                    etextview_input.setText(finalResult);// 设置输入框的文本
                    etextview_input.setSelection(etextview_input.length());//把光标定位末尾
                }
            }

            //会话发生错误回调接口
            public void onError(SpeechError error) {
                //打印错误码描述
                if (ActivityCompat.checkSelfPermission(SearchAddressActivity.this, Manifest.permission.RECORD_AUDIO) !=
                        PackageManager.PERMISSION_GRANTED && error.getPlainDescription(false).contains("启动录音失败")) {
                    TipeDialog tipeDialog = new TipeDialog.Builder(SearchAddressActivity.this)
                            .setTitle("提示")
                            .setMessage("打开录音权限才可以进行语音输入")
                            .setPositiveButton("授权", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mUserReject = false;
                                    requestPermission(Manifest.permission.RECORD_AUDIO);
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create();
                    tipeDialog.show();
                } else {
                    MyToast.showToast(SearchAddressActivity.this, error.getPlainDescription(false) + "，请重试", 5);
                }
            }

            //开始录音
            public void onBeginOfSpeech() {
                Log.e("语音识别正确", "开始录音:");
            }

            //    volume音量值0~30，data音频数据
            public void onVolumeChanged(int volume, byte[] data) {
                int mipmapId = (volume + 1) / 3;
                if (mipmapId == Constant.VALUE_9 || mipmapId == Constant.VALUE_10) {
                    mipmapId = Constant.VALUE_8;
                }
                switch (mipmapId) {
                    case 0:
                        mipmapId = R.mipmap.listener01;
                        break;
                    case 1:
                        mipmapId = R.mipmap.listener02;
                        break;
                    case 2:
                        mipmapId = R.mipmap.listener03;
                        break;
                    case 3:
                        mipmapId = R.mipmap.listener03;
                        break;
                    case 4:
                        mipmapId = R.mipmap.listener04;
                        break;
                    case 5:
                        mipmapId = R.mipmap.listener05;
                        break;
                    case 6:
                        mipmapId = R.mipmap.listener06;
                        break;
                    case 7:
                        mipmapId = R.mipmap.listener07;
                        break;
                    case 8:
                        mipmapId = R.mipmap.listener08;
                        break;
                }
                mVoiceDialog.updateUI(mipmapId, "正在录音中");
            }

            //结束录音
            public void onEndOfSpeech() {
                mVoiceDialog.updateUI(R.mipmap.listener01, "录音已结束");
            }

            //扩展用接口
            public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            }
        };

        imageview_mic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    requestPermission(Manifest.permission.RECORD_AUDIO);
                    mDialog.show();
                    mVoiceDialog.updateUI(R.mipmap.listener01, "正在录音");
                    dictationResultStr = "[";
                    mIat.startListening(mRecoListener);
                    imageview_mic.setBackground(ContextCompat.getDrawable(SearchAddressActivity.this, R.drawable.ic_touchmic));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mIat.stopListening();
                    mVoiceDialog.updateUI(R.mipmap.listener01, "录音结束");
                    imageview_mic.setBackground(ContextCompat.getDrawable(SearchAddressActivity.this, R.drawable.ic_bigmic));
                    mDialog.dismiss();
                }
                return true;
            }
        });

    }

    private void requestPermission(String permission) {
        if (!mUserReject && ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, 0x111);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0x111) {
            if (permissions[0].equals(Manifest.permission.RECORD_AUDIO) && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                mUserReject = true;
            }
        }
    }

}
