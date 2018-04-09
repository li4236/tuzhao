package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kyleduo.switchbutton.SwitchButton;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.TimeManager;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by TZL12 on 2017/9/22.
 */

public class EditParkActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imageview_back, imageview_show1, imageview_show2,imageview_show3,imageview_probar;
    private TextView textview_img_count, textview_park_statue,textview_delete;
    private LinearLayout linearlayout_parkpicture;
    private SwitchButton switchview_button;
    private CustomDialog mCustomDialog;

    private Park_Info mData;
    private String[] pic_list;
    private DateUtil dateUtil = new DateUtil();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpark_layout);

        initView();
        initData();
        initEvent();
        setStyle(true);
    }

    private void initView() {
        imageview_back = (ImageView) findViewById(R.id.id_activity_editpark_layout_imageview_back);
        imageview_show1 = (ImageView) findViewById(R.id.id_activity_editpark_layout_imageview_show1);
        imageview_show2 = (ImageView) findViewById(R.id.id_activity_editpark_layout_imageview_show2);
        imageview_show3 = (ImageView) findViewById(R.id.id_activity_editpark_layout_imageview_show3);
        imageview_probar = (ImageView) findViewById(R.id.id_activity_editpark_layout_imageview_probar);
        textview_img_count = (TextView) findViewById(R.id.id_activity_editpark_layout_textview_img_count);
        textview_park_statue = (TextView) findViewById(R.id.id_activity_editpark_layout_textview_park_statue);
        textview_delete = (TextView) findViewById(R.id.id_activity_editpark_layout_textview_delete);
        linearlayout_parkpicture = (LinearLayout) findViewById(R.id.id_activity_editpark_layout_linearlayout_parkpicture);
        switchview_button = (SwitchButton) findViewById(R.id.id_activity_editpark_layout_switchview_button);
    }

    private void initData() {
        if (getIntent().hasExtra("parkdata")) {
            mData = (Park_Info) getIntent().getSerializableExtra("parkdata");

            if (mData.getPark_img()!=null){
                if (!mData.getPark_img().equals("-1")){
                    pic_list = mData.getPark_img().split(",");
                    if (pic_list.length > 0) {
                        if (!pic_list[0].equals("")){
                            imageview_show1.setVisibility(View.VISIBLE);
                            Glide.with(this)
                                    .load(HttpConstants.ROOT_IMG_URL_PS + pic_list[0])
                                    .placeholder(R.mipmap.ic_img)
                                    .error(R.mipmap.ic_img)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .crossFade()
                                    .into(imageview_show1);
                            if (pic_list.length > 1) {
                                imageview_show2.setVisibility(View.VISIBLE);
                                Glide.with(this)
                                        .load(HttpConstants.ROOT_IMG_URL_PS + pic_list[1])
                                        .placeholder(R.mipmap.ic_img)
                                        .error(R.mipmap.ic_img)
                                        .centerCrop()
                                        .into(imageview_show2);
                            }
                            if (pic_list.length > 2) {
                                imageview_show3.setVisibility(View.VISIBLE);
                                Glide.with(this)
                                        .load(HttpConstants.ROOT_IMG_URL_PS + pic_list[2])
                                        .placeholder(R.mipmap.ic_img)
                                        .error(R.mipmap.ic_img)
                                        .centerCrop()
                                        .into(imageview_show3);
                            }
                            textview_img_count.setText(pic_list.length + "张");
                        }else {
                            textview_img_count.setText("未上传");
                        }
                    } else {
                        textview_img_count.setText("未上传");
                    }
                }else {
                    textview_img_count.setText("未上传");
                }
            }else {
                textview_img_count.setText("未上传");
            }

            if (mData.getPark_status().equals("3")) {
                switchview_button.setChecked(true);
                textview_park_statue.setText("出租中");
            } else if (mData.getPark_status().equals("10")){
                switchview_button.setChecked(false);
                textview_park_statue.setText("已暂停");
            }
        } else {
            finish();
        }
    }

    private void initEvent() {
        imageview_back.setOnClickListener(this);

        switchview_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!switchview_button.isChecked()){
                    switchview_button.setChecked(true);
                    if (mData.getOrder_times()!= null){
                        if (!mData.getOrder_times().equals("-1")){
                            String[] aaa = mData.getOrder_times().split(",");
                            boolean cannotChange = false;
                            for (String a : aaa){
                                if (dateUtil.compareTwoTime(TimeManager.getInstance().getNowTime(true,false),a.substring(a.indexOf("*")+1,a.length()),true)){
                                    cannotChange = true;
                                    break;
                                }
                            }
                            if (!cannotChange){
                                ((AnimationDrawable) imageview_probar.getBackground()).start();
                                imageview_probar.setVisibility(View.VISIBLE);
                                textview_park_statue.setVisibility(View.INVISIBLE);
                                requestEditPark("10");
                            }else {
                                MyToast.showToast(EditParkActivity.this,"该车位有其他用户已预约，暂不能修改",5);
                            }
                        }else {
                            ((AnimationDrawable) imageview_probar.getBackground()).start();
                            imageview_probar.setVisibility(View.VISIBLE);
                            textview_park_statue.setVisibility(View.INVISIBLE);
                            requestEditPark("10");
                        }
                    }
                }else {
                    switchview_button.setChecked(false);
                    ((AnimationDrawable) imageview_probar.getBackground()).start();
                    imageview_probar.setVisibility(View.VISIBLE);
                    textview_park_statue.setVisibility(View.INVISIBLE);
                    requestEditPark("3");
                }
            }
        });

        linearlayout_parkpicture.setOnClickListener(this);

        textview_delete.setOnClickListener(this);
    }

    private void initLoading(String what) {
        mCustomDialog = new CustomDialog(this, what);
        mCustomDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_activity_editpark_layout_imageview_back:
                finish();
                break;
            case R.id.id_activity_editpark_layout_linearlayout_parkpicture:
                ArrayList<String> list = new ArrayList<>();
                if (pic_list!=null){
                    for (String a : pic_list) {
                        if (!a.equals("")){
                            list.add(a);
                        }
                    }
                }
                Intent intent = new Intent(EditParkActivity.this, EditParkPicturesActivity.class);
                intent.putStringArrayListExtra("pic_list", list);
                intent.putExtra("park_id", mData.getId());
                intent.putExtra("citycode",mData.getCity_code());
                startActivityForResult(intent, 1);
                break;
            case R.id.id_activity_editpark_layout_textview_delete:
                if (dateUtil.getTimeDifferenceHour(mData.getCreat_time(),TimeManager.getInstance().getNowTime(true,false))>=2106){
                    if (mData.getOrder_times()!= null){
                        if (!mData.getOrder_times().equals("-1")){
                            String[] aaa = mData.getOrder_times().split(",");
                            boolean cannotChange = false;
                            for (String a : aaa){
                                if (dateUtil.compareTwoTime(a.substring(0,a.indexOf("*")),TimeManager.getInstance().getNowTime(true,false),true)){
                                    cannotChange = true;
                                }
                            }
                            if (!cannotChange){
                                requestDeletePark();
                            }else {
                                MyToast.showToast(EditParkActivity.this,"该车位有其他用户已预约，暂不能删除",5);
                            }
                        }else {
                            requestDeletePark();
                        }
                    }
                }else {
                    MyToast.showToast(EditParkActivity.this,"您的车位使用日期不足90天，还不可以删除哦",5);
                }
                break;
        }
    }

    private void requestDeletePark(){
        TipeDialog.Builder builder = new TipeDialog.Builder(EditParkActivity.this);
        builder.setMessage("确定删除该车位吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                initLoading("提交中...");
                OkGo.post(HttpConstants.deleteParkForUser)
                        .tag(EditParkActivity.this)
                        .headers("token",UserManager.getInstance().getUserInfo().getToken())
                        .params("park_id",mData.getId())
                        .params("citycode",mData.getCity_code())
                        .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                            @Override
                            public void onSuccess(Base_Class_Info<ParkOrderInfo> parkOrderInfoBase_class_info, Call call, Response response) {
                                if (mCustomDialog.isShowing()){
                                    mCustomDialog.dismiss();
                                }
                                MyToast.showToast(EditParkActivity.this,"提交成功",5);
                                finish();
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                if (mCustomDialog.isShowing()){
                                    mCustomDialog.dismiss();
                                }
                                if (!DensityUtil.isException(EditParkActivity.this,e)){
                                    Log.d("TAG", "请求失败， 信息为：" + e.getMessage());
                                    int code = Integer.parseInt(e.getMessage());
                                    switch (code) {
                                        case 101:
                                            MyToast.showToast(EditParkActivity.this, "您已提交过删除请求，稍后会有客服人员与您联系", 5);
                                            break;
                                        case 901:
                                            MyToast.showToast(EditParkActivity.this, "服务器正在维护中", 5);
                                            break;
                                    }
                                }
                            }
                        });
            }
        });

        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }

    private void requestEditPark(final String isopen) {

        OkGo.post(HttpConstants.editPark)
                .tag(EditParkActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token",UserManager.getInstance().getUserInfo().getToken())
                .params("park_id",mData.getId())
                .params("citycode",mData.getCity_code())
                .params("park_status",isopen)
                .execute(new JsonCallback<Base_Class_Info<Park_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Park_Info> park_infoBase_class_info, Call call, Response response) {
                        ((AnimationDrawable) imageview_probar.getBackground()).stop();
                        imageview_probar.setVisibility(View.GONE);
                        textview_park_statue.setVisibility(View.VISIBLE);
                        if (isopen.equals("3")){
                            textview_park_statue.setText("出租中");
                            switchview_button.setChecked(true);
                        }else {
                            textview_park_statue.setText("已暂停");
                            switchview_button.setChecked(false);
                        }
                        MyToast.showToast(EditParkActivity.this, "修改成功", 5);
                        mData.setPark_status(switchview_button.isChecked()? "1":"2");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ((AnimationDrawable) imageview_probar.getBackground()).stop();
                        imageview_probar.setVisibility(View.GONE);
                        textview_park_statue.setVisibility(View.VISIBLE);
                        if (!DensityUtil.isException(EditParkActivity.this,e)){
                            Log.d("TAG", "请求失败， 信息为：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
                                    MyToast.showToast(EditParkActivity.this, "修改失败，稍后再试", 5);
                                    break;
                                case 901:
                                    MyToast.showToast(EditParkActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 102) {
            ArrayList<String> newpiclist = data.getStringArrayListExtra("pic_list");

            if (newpiclist.size() > 0) {
                switch (newpiclist.size()){
                    case 1:
                        imageview_show1.setVisibility(View.VISIBLE);
                        imageview_show2.setVisibility(View.GONE);
                        imageview_show3.setVisibility(View.GONE);
                        Glide.with(this)
                                .load(HttpConstants.ROOT_IMG_URL_PS + newpiclist.get(0))
                                .placeholder(R.mipmap.ic_img)
                                .error(R.mipmap.ic_img)
                                .centerCrop()
                                .into(imageview_show1);
                        break;
                    case 2:
                        imageview_show1.setVisibility(View.VISIBLE);
                        imageview_show2.setVisibility(View.VISIBLE);
                        imageview_show3.setVisibility(View.GONE);
                        Glide.with(this)
                                .load(HttpConstants.ROOT_IMG_URL_PS + newpiclist.get(0))
                                .placeholder(R.mipmap.ic_img)
                                .error(R.mipmap.ic_img)
                                .centerCrop()
                                .into(imageview_show1);
                        Glide.with(this)
                                .load(HttpConstants.ROOT_IMG_URL_PS + newpiclist.get(1))
                                .placeholder(R.mipmap.ic_img)
                                .error(R.mipmap.ic_img)
                                .centerCrop()
                                .into(imageview_show2);
                        break;
                    case 3:
                        imageview_show1.setVisibility(View.VISIBLE);
                        imageview_show2.setVisibility(View.VISIBLE);
                        imageview_show3.setVisibility(View.VISIBLE);
                        Glide.with(this)
                                .load(HttpConstants.ROOT_IMG_URL_PS + newpiclist.get(0))
                                .placeholder(R.mipmap.ic_img)
                                .error(R.mipmap.ic_img)
                                .centerCrop()
                                .into(imageview_show1);
                        Glide.with(this)
                                .load(HttpConstants.ROOT_IMG_URL_PS + newpiclist.get(1))
                                .placeholder(R.mipmap.ic_img)
                                .error(R.mipmap.ic_img)
                                .centerCrop()
                                .into(imageview_show2);
                        Glide.with(this)
                                .load(HttpConstants.ROOT_IMG_URL_PS + newpiclist.get(2))
                                .placeholder(R.mipmap.ic_img)
                                .error(R.mipmap.ic_img)
                                .centerCrop()
                                .into(imageview_show3);
                        break;
                }
                textview_img_count.setText(newpiclist.size() + "张");
            } else {
                imageview_show1.setVisibility(View.GONE);
                imageview_show2.setVisibility(View.GONE);
                textview_img_count.setText("未上传");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCustomDialog != null) {
            mCustomDialog.cancel();
        }
    }
}
