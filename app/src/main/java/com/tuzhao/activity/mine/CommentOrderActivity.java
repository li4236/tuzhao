package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cb.ratingbar.CBRatingBar;
import com.tianzhili.www.myselfsdk.luban.Luban;
import com.tianzhili.www.myselfsdk.luban.OnCompressListener;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.photopicker.controller.PhotoPickConfig;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.NearPointPCInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.utils.GlideApp;
import com.tuzhao.utils.ImageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/5/8.
 */

public class CommentOrderActivity extends BaseStatusActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_PICTURE = 0x666;

    private ImageView mParkspaceIv;

    private CBRatingBar mCBRatingBar;

    private EditText mCommentEt;

    private TextView mCommentCount;

    private ImageView mAddIv;

    private ImageView mDeleteAddIv;

    private ImageView mOneIv;

    private ImageView mDeleteOneIv;

    private ImageView mTwoIv;

    private ImageView mDeleteTwoIv;

    private TextView mApplyComment;

    private String mParkspaceId;

    private String mParkspacePic;

    private String mCityCode;

    private String mOrderId;

    private List<File> mCommentPicFiles;

    @Override
    protected int resourceId() {
        return R.layout.activity_comment_order_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mParkspaceIv = findViewById(R.id.comment_order_parkspace_iv);
        mCBRatingBar = findViewById(R.id.comment_order_crb);
        mCommentEt = findViewById(R.id.comment_order_et);
        mCommentCount = findViewById(R.id.comment_order_comment_count);
        mAddIv = findViewById(R.id.comment_order_pic);
        mDeleteAddIv = findViewById(R.id.comment_order_pic_delete);
        mOneIv = findViewById(R.id.comment_order_pic_one);
        mDeleteOneIv = findViewById(R.id.comment_order_pic_one_delete);
        mTwoIv = findViewById(R.id.comment_order_pic_two);
        mDeleteTwoIv = findViewById(R.id.comment_order_pic_two_delete);
        mApplyComment = findViewById(R.id.comment_order_apply);
    }

    @Override
    protected void initData() {
        super.initData();
        if (UserManager.getInstance().hasLogined() && getIntent().hasExtra("parkspace_id")) {
            Intent intent = getIntent();
            mParkspaceId = intent.getStringExtra("parkspace_id");
            mParkspacePic = intent.getStringExtra("parkspace_img");
            mOrderId = intent.getStringExtra("order_id");
            mCityCode = intent.getStringExtra("city_code");
        } else {
            finish();
        }

        mCommentPicFiles = new ArrayList<>(3);
        ImageUtil.showRoundPic(mParkspaceIv, HttpConstants.ROOT_IMG_URL_PS + mParkspacePic, R.mipmap.ic_img);

        mAddIv.setOnClickListener(this);
        mDeleteAddIv.setOnClickListener(this);
        mDeleteOneIv.setOnClickListener(this);
        mDeleteTwoIv.setOnClickListener(this);
        mApplyComment.setOnClickListener(this);

        mCommentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCommentCount.setText(count + "/150");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dismmisLoadingDialog();
    }

    @NonNull
    @Override
    protected String title() {
        return "填写评价";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment_order_pic:
                if (mCommentPicFiles.size() < 3) {
                    ImageUtil.startTakeMultiPhoto(CommentOrderActivity.this,3);
                }
                break;
            case R.id.comment_order_pic_delete:
                mCommentPicFiles.remove(0);
                showChooesPic();
                break;
            case R.id.comment_order_pic_one_delete:
                if (mCommentPicFiles.size() == 3) {
                    mCommentPicFiles.remove(1);
                } else {
                    mCommentPicFiles.remove(0);
                }
                showChooesPic();
                break;
            case R.id.comment_order_pic_two_delete:
                if (mCommentPicFiles.size() == 3) {
                    mCommentPicFiles.remove(2);
                } else {
                    mCommentPicFiles.remove(1);
                }
                showChooesPic();
                break;
            case R.id.comment_order_apply:
                if (mCommentEt.getText().toString().trim().length() > 0) {
                    requestAddPsComment();
                } else {
                    showFiveToast("客官说点什么吧");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICTURE && resultCode == RESULT_OK && data != null) {
            final List<String> imageBeans = data.getStringArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST);
            mCommentPicFiles.clear();
            for (String imageBean : imageBeans) {
                //进行图片逐个压缩
                Luban.with(CommentOrderActivity.this)
                        .load(imageBean)
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
                                mCommentPicFiles.add(file);
                                if (mCommentPicFiles.size() == imageBeans.size()) {
                                    showChooesPic();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlideApp.get(getApplicationContext()).clearMemory();
    }

    private void requestAddPsComment() {
        OkGo.post(HttpConstants.addPsComment)
                .tag(CommentOrderActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("parkspace_id", mParkspaceId)
                .params("city_code", mCityCode)
                .params("order_id", mOrderId)
                .params("grade", mCBRatingBar.getTouchCount() == -1 ? "1" : (mCBRatingBar.getTouchCount() + ""))
                .params("content", mCommentEt.getText().toString())
                .addFileParams("imgs[]", mCommentPicFiles)
                .execute(new JsonCallback<Base_Class_Info<NearPointPCInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<NearPointPCInfo> homePCInfoBase_class_info, Call call, Response response) {
                        for (File file : mCommentPicFiles) {
                            if (file.exists()) {
                                file.delete();
                            }
                        }

                        showFiveToast("评价成功");
                        dismmisLoadingDialog();
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    showFiveToast("评论失败");
                                    break;
                                case "102":
                                    showFiveToast("评论失败");
                                    break;
                                case "103":
                                    showFiveToast("评论失败");
                                    break;
                                case "104":
                                    showFiveToast("抱歉，已评论过了哦");
                                    break;
                                case "105":
                                    showFiveToast("评论失败");
                                    break;
                            }
                        }
                    }
                });
    }

    private void showChooesPic() {
        switch (mCommentPicFiles.size()) {
            case 0:
                setAddIvNormal();
                setPicOneVisibility(View.GONE);
                setPicTwoVisibility(View.GONE);
                break;
            case 1:
                setAddIvNormal();
                ImageUtil.showRoundPic(mOneIv, mCommentPicFiles.get(0).getAbsolutePath());
                setPicOneVisibility(View.VISIBLE);
                setPicTwoVisibility(View.GONE);
                break;
            case 2:
                setAddIvNormal();
                ImageUtil.showRoundPic(mOneIv, mCommentPicFiles.get(0).getAbsolutePath());
                ImageUtil.showRoundPic(mTwoIv, mCommentPicFiles.get(1).getAbsolutePath());
                setPicOneVisibility(View.VISIBLE);
                setPicTwoVisibility(View.VISIBLE);
                break;
            case 3:
                ImageUtil.showRoundPic(mAddIv, mCommentPicFiles.get(0).getAbsolutePath());
                ImageUtil.showRoundPic(mOneIv, mCommentPicFiles.get(1).getAbsolutePath());
                ImageUtil.showRoundPic(mTwoIv, mCommentPicFiles.get(2).getAbsolutePath());
                mDeleteAddIv.setVisibility(View.VISIBLE);
                setPicOneVisibility(View.VISIBLE);
                setPicTwoVisibility(View.VISIBLE);
                break;
        }
    }

    private void setAddIvNormal() {
        if (mDeleteAddIv.getVisibility() != View.GONE) {
            ImageUtil.showRoundPic(mAddIv, R.mipmap.ic_addimg);
            mDeleteAddIv.setVisibility(View.GONE);
        }
    }

    private void setPicOneVisibility(int visibility) {
        if (mOneIv.getVisibility() != visibility) {
            mOneIv.setVisibility(visibility);
            mDeleteOneIv.setVisibility(visibility);
        }
    }

    private void setPicTwoVisibility(int visibility) {
        if (mTwoIv.getVisibility() != visibility) {
            mTwoIv.setVisibility(visibility);
            mDeleteTwoIv.setVisibility(visibility);
        }
    }

}
