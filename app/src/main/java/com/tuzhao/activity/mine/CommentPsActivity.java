package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cb.ratingbar.CBRatingBar;
import com.lwkandroid.imagepicker.ImagePicker;
import com.lwkandroid.imagepicker.data.ImageBean;
import com.lwkandroid.imagepicker.data.ImagePickType;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.NearPointPCInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.ImageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by TZL12 on 2017/12/7.
 */

public class CommentPsActivity extends BaseActivity implements View.OnClickListener {

    //调用相册-选择图片
    private final int REQUEST_CODE_PICKER = 108;
    private final int RESULT_CODE_PICKER = 109;

    private LoadingDialog mLoadingDialog;
    private CBRatingBar cbratingbar;
    private ImageView imageview_psimg, imageview_add, imageview_img1, imageview_img2, imageview_img3, imageview_del1, imageview_del2, imageview_del3;
    private EditText edittext_comment;
    private TextView textview_count, textview_go;
    private RelativeLayout relativelayout_img1, relativelayout_img2, relativelayout_img3;

    private String parkspace_id, parkspace_img, city_code, park_time, order_id;
    private boolean isReading = false;
    private List<ImageBean> img_list;
    private List<File> file_list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentps_layout);

        initData();
        initView();
        initEvent();
        setStyle(true);
    }

    private void initData() {
        if (UserManager.getInstance().hasLogined() && getIntent().hasExtra("parkspace_id")) {
            parkspace_id = getIntent().getStringExtra("parkspace_id");
            parkspace_img = getIntent().getStringExtra("parkspace_img");
            order_id = getIntent().getStringExtra("order_id");
            city_code = getIntent().getStringExtra("city_code");
            park_time = getIntent().getStringExtra("park_time");
        } else {
            finish();
        }
    }

    private void initView() {
        imageview_psimg = findViewById(R.id.id_activity_commentps_layout_imageview_psimg);
        imageview_add = findViewById(R.id.id_activity_commentps_layout_imageview_add);
        imageview_img1 = findViewById(R.id.id_activity_commentps_layout_imageview_img1);
        imageview_img2 = findViewById(R.id.id_activity_commentps_layout_imageview_img2);
        imageview_img3 = findViewById(R.id.id_activity_commentps_layout_imageview_img3);
        imageview_del1 = findViewById(R.id.id_activity_commentps_layout_imageview_del1);
        imageview_del2 = findViewById(R.id.id_activity_commentps_layout_imageview_del2);
        imageview_del3 = findViewById(R.id.id_activity_commentps_layout_imageview_del3);
        cbratingbar = findViewById(R.id.comment_order_crb);
        edittext_comment = findViewById(R.id.comment_order_et);
        textview_count = findViewById(R.id.id_activity_commentps_layout_textview_count);
        textview_go = findViewById(R.id.id_activity_commentps_layout_textview_go);
        relativelayout_img1 = findViewById(R.id.id_activity_commentps_layout_relativelayout_img1);
        relativelayout_img2 = findViewById(R.id.id_activity_commentps_layout_relativelayout_img2);
        relativelayout_img3 = findViewById(R.id.id_activity_commentps_layout_relativelayout_img3);

        ImageUtil.showPic(imageview_psimg, HttpConstants.ROOT_IMG_URL_PS + parkspace_img, R.mipmap.ic_img);
    }

    private void initEvent() {

        findViewById(R.id.id_activity_commentps_layout_imageview_back).setOnClickListener(this);
        imageview_add.setOnClickListener(this);
        imageview_del1.setOnClickListener(this);
        imageview_del2.setOnClickListener(this);
        imageview_del3.setOnClickListener(this);
        textview_go.setOnClickListener(this);

        edittext_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((count > 0 || start > 0) || s.toString().length() > 0) {
                    textview_count.setText(edittext_comment.getText().toString().length() + "/150");
                } else {
                    textview_count.setText("0/150");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_activity_commentps_layout_imageview_back:
                finish();
                break;
            case R.id.id_activity_commentps_layout_imageview_add:
                //调用相册
                new ImagePicker()
                        .cachePath(Environment.getExternalStorageDirectory().getAbsolutePath())
                        .needCamera(true) //是否需要在界面中显示相机入口(类似微信那样)
                        .pickType(ImagePickType.MULTI) //设置选取类型(单选SINGLE、多选MUTIL、拍照ONLY_CAMERA)
                        .maxNum(3)  //设置最大选择数量(此选项只对多选生效，拍照和单选都是1，修改后也无效)
                        .start(CommentPsActivity.this, REQUEST_CODE_PICKER);
                break;
            case R.id.id_activity_commentps_layout_imageview_del1:
                relativelayout_img1.setVisibility(View.GONE);
                imageview_add.setVisibility(View.VISIBLE);
                img_list.remove(0);
                break;
            case R.id.id_activity_commentps_layout_imageview_del2:
                relativelayout_img2.setVisibility(View.GONE);
                imageview_add.setVisibility(View.VISIBLE);
                if (img_list.size() > 1) {
                    img_list.remove(1);
                } else {
                    img_list.remove(0);
                }
                break;
            case R.id.id_activity_commentps_layout_imageview_del3:
                relativelayout_img3.setVisibility(View.GONE);
                imageview_add.setVisibility(View.VISIBLE);
                if (img_list.size() > 2) {
                    img_list.remove(2);
                } else if (img_list.size() > 1) {
                    img_list.remove(1);
                } else if (img_list.size() > 0) {
                    img_list.remove(0);
                }
                break;
            case R.id.id_activity_commentps_layout_textview_go:
                if (edittext_comment.getText().length() > 0) {
                    if (file_list == null) {
                        initLoading("提交中...");
                        requestAddPsComment();
                    } else {
                        if (file_list.size() == img_list.size()) {
                            initLoading("提交中...");
                            requestAddPsComment();
                        } else {
                            isReading = true;
                            initLoading("准备中...");
                        }
                    }
                } else {
                    MyToast.showToast(CommentPsActivity.this, "客官说点什么吧", 5);
                }

                break;
        }
    }

    private void requestAddPsComment() {

        Log.e("参数", "user_id:" + UserManager.getInstance().getUserInfo().getId() + "parkspace_id" + parkspace_id + "city_code" + city_code + "order_id" + order_id + "grade" + cbratingbar.getTouchCount() + "content" + edittext_comment.getText().toString());
        OkGo.post(HttpConstants.addPsComment)
                .tag(CommentPsActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("parkspace_id", parkspace_id)
                .params("city_code", city_code)
                .params("order_id", order_id)
                .params("grade", cbratingbar.getTouchCount() == -1 ? "1" : (cbratingbar.getTouchCount() + ""))
                .params("content", edittext_comment.getText().toString())
                .addFileParams("imgs[]", file_list)
                .execute(new JsonCallback<Base_Class_Info<NearPointPCInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<NearPointPCInfo> homePCInfoBase_class_info, Call call, Response response) {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        MyToast.showToast(CommentPsActivity.this, "评价成功", 5);
                        if (file_list != null) {
                            for (File file : file_list) {
                                if (file.exists()) {
                                    file.delete();
                                }
                            }
                        }
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        if (!DensityUtil.isException(CommentPsActivity.this, e)) {
                            Log.d("TAG", "请求失败， 信息为changeUserImage：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
                                    MyToast.showToast(CommentPsActivity.this, "评论失败", 5);
                                    break;
                                case 102:
                                    MyToast.showToast(CommentPsActivity.this, "评论失败", 5);
                                    break;
                                case 103:
                                    MyToast.showToast(CommentPsActivity.this, "评论失败", 5);
                                    break;
                                case 104:
                                    MyToast.showToast(CommentPsActivity.this, "抱歉，已评论过了哦", 5);
                                    break;
                                case 105:
                                    MyToast.showToast(CommentPsActivity.this, "评论失败", 5);
                                    break;
                                case 901:
                                    MyToast.showToast(CommentPsActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICKER && data != null) {
            img_list = data.getParcelableArrayListExtra(ImagePicker.INTENT_RESULT_DATA);
            initSomePic();
            file_list = new ArrayList<>();
            for (ImageBean imageBean : img_list) {
                //进行图片逐个压缩
                Luban.with(CommentPsActivity.this)
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
                                if (file_list.size() == img_list.size()) {
                                    //全部压缩完毕
                                    if (isReading) {
                                        isReading = false;
                                        initLoading("提交中...");
                                        requestAddPsComment();
                                    }
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

    private void initSomePic() {

        switch (img_list.size()) {
            case 1:
                relativelayout_img1.setVisibility(View.VISIBLE);
                relativelayout_img2.setVisibility(View.GONE);
                relativelayout_img3.setVisibility(View.GONE);
                ImageUtil.showPic(imageview_img1, img_list.get(0).getImagePath(), R.mipmap.ic_img);
                break;
            case 2:
                relativelayout_img1.setVisibility(View.VISIBLE);
                relativelayout_img2.setVisibility(View.VISIBLE);
                relativelayout_img3.setVisibility(View.GONE);
                ImageUtil.showPic(imageview_img2, img_list.get(1).getImagePath(), R.mipmap.ic_img);
                break;
            case 3:
                relativelayout_img1.setVisibility(View.VISIBLE);
                relativelayout_img2.setVisibility(View.VISIBLE);
                relativelayout_img3.setVisibility(View.VISIBLE);
                imageview_add.setVisibility(View.GONE);
                ImageUtil.showPic(imageview_img1, img_list.get(0).getImagePath(), R.mipmap.ic_img);
                ImageUtil.showPic(imageview_img2, img_list.get(1).getImagePath(), R.mipmap.ic_img);
                ImageUtil.showPic(imageview_img3, img_list.get(2).getImagePath(), R.mipmap.ic_img);
                break;
        }
    }

    private void initLoading(String what) {
        mLoadingDialog = new LoadingDialog(this, what);
        mLoadingDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
    }
}
