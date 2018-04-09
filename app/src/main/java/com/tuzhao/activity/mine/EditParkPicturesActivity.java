package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lwkandroid.imagepicker.ImagePicker;
import com.lwkandroid.imagepicker.data.ImageBean;
import com.lwkandroid.imagepicker.data.ImagePickType;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.adapter.ParkPicturesAdapter;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.publicwidget.square.SpaceItemDecoration;
import com.tuzhao.utils.DensityUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by TZL12 on 2017/9/23.
 */

public class EditParkPicturesActivity extends BaseActivity {

    //调用相册-选择图片
    private final int REQUEST_CODE_PICKER = 100;
    private final int RESULT_CODE_PICKER = 101;
    private ImageView imageview_back;
    private TextView textview_edit_or_cancle;
    private RecyclerView recycleview;
    private LinearLayout linearlayout_deletepic, linearlayout_uploadpic;
    private CustomDialog mCustomDialog;

    private RecyclerView.LayoutManager mLayoutManager;
    private ParkPicturesAdapter mAdapter;
    private ArrayList<PicBean> mData = new ArrayList<>();
    private List<String> mSignData = new ArrayList<>();
    private String park_id,citycode;
    private ArrayList<String> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editparkpictures_layout);

        initView();
        initData();
        initEvent();
        setStyle(true);
    }

    private void initView() {

        imageview_back = (ImageView) findViewById(R.id.id_activity_editparkpictures_layout_imageview_back);
        textview_edit_or_cancle = (TextView) findViewById(R.id.id_activity_editparkpictures_layout_textview_edit_or_cancle);
        recycleview = (RecyclerView) findViewById(R.id.id_activity_editparkpictures_layout_recycleview);
        linearlayout_deletepic = (LinearLayout) findViewById(R.id.id_activity_editparkpictures_layout_linearlayout_deletepic);
        linearlayout_uploadpic = (LinearLayout) findViewById(R.id.id_activity_editparkpictures_layout_linearlayout_uploadpic);
    }

    private void initData() {

        if (getIntent().hasExtra("pic_list")) {
            for (String a : getIntent().getStringArrayListExtra("pic_list")) {
                PicBean bean = new PicBean();
                bean.setUrl(a);
                bean.setIsdelete(false);
                mData.add(bean);
            }
            park_id = getIntent().getStringExtra("park_id");
            citycode = getIntent().getStringExtra("citycode");
        } else {
            finish();
        }
        mLayoutManager = new GridLayoutManager(this, 3);
        mAdapter = new ParkPicturesAdapter(this, mData);
        mAdapter.setOnItemClickListener(new ParkPicturesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(List<String> sign_data) {
                mSignData = new ArrayList<>();
                mSignData = sign_data;
            }

            @Override
            public void onItemLongClick() {
                ArrayList<PicBean> datalist = new ArrayList<>();
                for (int i = 0; i < mData.size(); i++) {
                    PicBean info = mData.get(i);
                    info.setIsdelete(true);
                    datalist.add(info);
                }
                mData.clear();
                mData.addAll(datalist);
                mAdapter.notifyDataSetChanged();
                textview_edit_or_cancle.setText("取消");
                linearlayout_uploadpic.setVisibility(View.GONE);
            }
        });

        recycleview.setLayoutManager(mLayoutManager);
        recycleview.setAdapter(mAdapter);
        recycleview.addItemDecoration(new SpaceItemDecoration(3,5,true));
    }

    private void initEvent() {

        imageview_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list = new ArrayList<>();
                for (PicBean bean : mData) {
                    list.add(bean.getUrl());
                }

                Intent intent = new Intent();
                intent.putStringArrayListExtra("pic_list", list);
                setResult(102, intent);
                finish();
            }
        });

        textview_edit_or_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData.size()>0){
                    if (mData.get(0).isdelete()) {
                        ArrayList<PicBean> datalist = new ArrayList<>();
                        for (int i = 0; i < mData.size(); i++) {
                            PicBean info = mData.get(i);
                            info.setIsdelete(false);
                            datalist.add(info);
                        }
                        mData.clear();
                        mData.addAll(datalist);
                        mAdapter.notifyDataSetChanged();
                        textview_edit_or_cancle.setText("编辑");
                        linearlayout_uploadpic.setVisibility(View.VISIBLE);
                    } else {
                        ArrayList<PicBean> datalist = new ArrayList<>();
                        for (int i = 0; i < mData.size(); i++) {
                            PicBean info = mData.get(i);
                            info.setIsdelete(true);
                            datalist.add(info);
                        }
                        mData.clear();
                        mData.addAll(datalist);
                        mAdapter.notifyDataSetChanged();
                        textview_edit_or_cancle.setText("取消");
                        linearlayout_uploadpic.setVisibility(View.GONE);
                    }
                }else {
                    MyToast.showToast(EditParkPicturesActivity.this,"先上传图片才能编辑哦",5);
                }
            }
        });

        linearlayout_deletepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSignData.size() > 0) {
                    showAlertDialog(mSignData.size());
                } else {
                    MyToast.showToast(EditParkPicturesActivity.this, "要先选择图片再删除哦", 5);
                }
            }
        });

        linearlayout_uploadpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用相册
                new ImagePicker()
                        .cachePath(Environment.getExternalStorageDirectory().getAbsolutePath())
                        .needCamera(true) //是否需要在界面中显示相机入口(类似微信那样)
                        .pickType(ImagePickType.MULTI) //设置选取类型(单选SINGLE、多选MUTIL、拍照ONLY_CAMERA)
                        .maxNum(3)  //设置最大选择数量(此选项只对多选生效，拍照和单选都是1，修改后也无效)
                        .start(EditParkPicturesActivity.this, REQUEST_CODE_PICKER);
            }
        });
    }

    private void showAlertDialog(int count) {
        final TipeDialog.Builder builder = new TipeDialog.Builder(this);
        builder.setMessage("是否确定删除这" + count + "张图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //设置你的操作事项
                initLoading("删除中...");
                requestDeleteParkPicture();
            }
        });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }

    private void requestDeleteParkPicture() {
        String newparklist = "";
        for (int j =0;j<mData.size();j++) {
            for (int i = 0; i < mSignData.size(); i++) {
                if (mData.get(j).getUrl().equals(mSignData.get(i))) {
                    newparklist = newparklist + j + ",";
                    break;
                }
            }
        }
        if (newparklist.split(",").length>0){
            newparklist = newparklist.substring(0, newparklist.length() - 1);
        }
        OkGo.post(HttpConstants.deleteParkPicture)
                .tag(EditParkPicturesActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token",UserManager.getInstance().getUserInfo().getToken())
                .params("park_id", park_id)
                .params("citycode",citycode)
                .params("park_img", newparklist)
                .execute(new JsonCallback<Base_Class_Info<Park_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Park_Info> park_infoBase_class_info, Call call, Response response) {
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        MyToast.showToast(EditParkPicturesActivity.this, "删除成功", 5);

                        ArrayList<PicBean> datalist = new ArrayList<>();
                        for (int i = 0; i < mData.size(); i++) {
                            PicBean info = mData.get(i);
                            if (!isDelete(info)) {
                                info.setIsdelete(false);
                                datalist.add(info);
                            }
                        }
                        mAdapter.sign_list.clear();
                        mData.clear();
                        mData.addAll(datalist);
                        mAdapter.notifyDataSetChanged();
                        textview_edit_or_cancle.setText("编辑");
                        linearlayout_uploadpic.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);

                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        if (!DensityUtil.isException(EditParkPicturesActivity.this,e)){
                            Log.d("TAG", "请求失败， 信息为：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
                                    MyToast.showToast(EditParkPicturesActivity.this, "删除失败", 5);
                                    break;
                                case 900:
                                    MyToast.showToast(EditParkPicturesActivity.this, "服务器异常", 5);
                                    break;
                                case 901:
                                    MyToast.showToast(EditParkPicturesActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }

    private void initLoading(String what) {
        mCustomDialog = new CustomDialog(this, what);
        mCustomDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICKER && data!=null) {
            final List<ImageBean> list = data.getParcelableArrayListExtra(ImagePicker.INTENT_RESULT_DATA);
            final List<File> file_list = new ArrayList<>();
            initLoading("正在上传...");
            for (ImageBean imageBean : list) {
                //进行图片逐个压缩
                Luban.with(EditParkPicturesActivity.this)
                        .load(imageBean.getImagePath())
                        .ignoreBy(1)
                        .setTargetDir(getApplicationContext().getFilesDir().getAbsolutePath())
                        .setCompressListener(new OnCompressListener() {
                            @Override
                            public void onStart() {
                                // 压缩开始前调用，可以在方法内启动 loading UI
                            }

                            @Override
                            public void onSuccess(File file) {
                                // 压缩成功后调用，返回压缩后的图片文件
                                file_list.add(file);
                                if (file_list.size() == list.size()) {
                                    uploadParkPicture(file_list);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                // 当压缩过程出现问题时调用

                            }
                        }).launch();
            }

        }
    }

    private void uploadParkPicture(List<File> file_list) {

        OkGo.post(HttpConstants.uploadParkPicture)
                .tag(EditParkPicturesActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token",UserManager.getInstance().getUserInfo().getToken())
                .params("park_id", park_id)
                .params("citycode",citycode)
                .addFileParams("park_pics[]", file_list)
                .execute(new JsonCallback<Base_Class_Info<Park_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Park_Info> change_userImage_infoBase_class_info, Call call, Response response) {
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        Log.e("dsa", "返回的数据：" + change_userImage_infoBase_class_info.data.getPark_img());
                        ArrayList<PicBean> datalist = new ArrayList<>();
                        for (String a : change_userImage_infoBase_class_info.data.getPark_img().split(",")) {
                            PicBean info = new PicBean();
                            info.setUrl(a);
                            info.setIsdelete(false);
                            datalist.add(info);
                        }
                        mData.clear();
                        mData.addAll(datalist);
                        mAdapter.notifyDataSetChanged();
                        textview_edit_or_cancle.setText("编辑");
                        linearlayout_uploadpic.setVisibility(View.VISIBLE);
                        MyToast.showToast(EditParkPicturesActivity.this, "上传成功", 5);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        if (!DensityUtil.isException(EditParkPicturesActivity.this,e)){
                            Log.d("TAG", "请求失败， 信息为：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 104:
                                    MyToast.showToast(EditParkPicturesActivity.this, "图片上传失败", 5);
                                    break;
                                case 901:
                                    MyToast.showToast(EditParkPicturesActivity.this,"服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.upProgress(currentSize, totalSize, progress, networkSpeed);
                        Log.e("dsa", "上传的进度：" + progress);
                    }
                });
    }

    private boolean isDelete(PicBean info) {
        for (int j = 0; j < mSignData.size(); j++) {
            if (info.getUrl().equals(mSignData.get(j))) {
                mSignData.remove(j);
                return true;
            }
        }
        return false;
    }

    public class PicBean {
        private String url;
        private boolean isdelete;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isdelete() {
            return isdelete;
        }

        public void setIsdelete(boolean isdelete) {
            this.isdelete = isdelete;
        }
    }

    /**
     * 监听返回键按钮事件
     */
    @Override
    public void onBackPressed() {
        list = new ArrayList<>();
        for (PicBean bean : mData) {
            list.add(bean.getUrl());
        }
        Intent intent = new Intent();
        intent.putStringArrayListExtra("pic_list", list);
        setResult(102, intent);
        finish();
//        super.onBackPressed();会关闭页面
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCustomDialog != null) {
            mCustomDialog.cancel();
        }
    }
}
