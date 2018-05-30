package com.tuzhao.fragment.parkorder;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cb.ratingbar.CBRatingBar;
import com.lwkandroid.imagepicker.ImagePicker;
import com.lwkandroid.imagepicker.data.ImageBean;
import com.lwkandroid.imagepicker.data.ImagePickType;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.NearPointPCInfo;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;
import com.tuzhao.utils.SoftKeyBroadManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by juncoder on 2018/5/30.
 */

public class CommentOrderFragment extends BaseStatusFragment implements View.OnClickListener, IntentObserver {

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

    private String mParkspacePic = "";

    private List<File> mCommentPicFiles;

    private ParkOrderInfo mParkOrderInfo;

    private SoftKeyBroadManager.SoftKeyboardStateListener mKeyboardStateListener;

    private SoftKeyBroadManager mSoftKeyBroadManager;

    public static CommentOrderFragment newInstance(ParkOrderInfo parkOrderInfo) {
        CommentOrderFragment fragment = new CommentOrderFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstansUtil.PARK_ORDER_INFO, parkOrderInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int resourceId() {
        return R.layout.fragment_coment_order_layout;
    }

    @Override
    protected void initView(final View view, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mParkOrderInfo = (ParkOrderInfo) getArguments().getSerializable(ConstansUtil.PARK_ORDER_INFO);
        }

        mParkspaceIv = view.findViewById(R.id.comment_order_parkspace_iv);
        mCBRatingBar = view.findViewById(R.id.comment_order_crb);
        mCommentEt = view.findViewById(R.id.comment_order_et);
        mCommentCount = view.findViewById(R.id.comment_order_comment_count);
        mAddIv = view.findViewById(R.id.comment_order_pic_add);
        mDeleteAddIv = view.findViewById(R.id.comment_order_pic_add_delete);
        mOneIv = view.findViewById(R.id.comment_order_pic_one);
        mDeleteOneIv = view.findViewById(R.id.comment_order_pic_one_delete);
        mTwoIv = view.findViewById(R.id.comment_order_pic_two);
        mDeleteTwoIv = view.findViewById(R.id.comment_order_pic_two_delete);
        mApplyComment = view.findViewById(R.id.comment_order_apply);
    }

    @Override
    protected void initView(View view, final ViewGroup container, Bundle savedInstanceState) {
        super.initView(view, container, savedInstanceState);
        mSoftKeyBroadManager = new SoftKeyBroadManager(view);
        mKeyboardStateListener = new SoftKeyBroadManager.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(container, "translationY", 0, -keyboardHeightInPx / 4);
                objectAnimator.setDuration(100);
                objectAnimator.start();
            }

            @Override
            public void onSoftKeyboardClosed() {
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(container, "translationY", 0);
                objectAnimator.setDuration(100);
                objectAnimator.start();
            }
        };
        mSoftKeyBroadManager.addSoftKeyboardStateListener(mKeyboardStateListener);
        IntentObserable.registerObserver(this);
    }

    @Override
    protected void initData() {
        if (!mParkOrderInfo.getPictures().equals("-1")) {
            mParkspacePic = mParkOrderInfo.getPictures().split(",")[0];
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

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCommentCount.setText(mCommentEt.getText().length() + "/150");
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSoftKeyBroadManager.removeSoftKeyboardStateListener(mKeyboardStateListener);
        IntentObserable.unregisterObserver(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment_order_pic_add:
                if (mCommentPicFiles.size() < 3) {
                    if (getActivity() != null) {
                        new ImagePicker()
                                .cachePath(Environment.getExternalStorageDirectory().getAbsolutePath())
                                .needCamera(true) //是否需要在界面中显示相机入口(类似微信那样)
                                .pickType(ImagePickType.MULTI) //设置选取类型(单选SINGLE、多选MUTIL、拍照ONLY_CAMERA)
                                .maxNum(3 - mCommentPicFiles.size())  //设置最大选择数量(此选项只对多选生效，拍照和单选都是1，修改后也无效)
                                .start(getActivity(), ConstansUtil.PICTURE_REQUEST_CODE);
                    }
                }
                break;
            case R.id.comment_order_pic_add_delete:
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

    private void requestAddPsComment() {
        OkGo.post(HttpConstants.addPsComment)
                .tag(TAG)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("parkspace_id", mParkOrderInfo.getBelong_park_space())
                .params("city_code", mParkOrderInfo.getCitycode())
                .params("order_id", mParkOrderInfo.getId())
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
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
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
            ImageUtil.showPic(mAddIv, R.drawable.ic_addimg);
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

    @Override
    public void onReceive(Intent intent) {
        if (Objects.equals(intent.getAction(), ConstansUtil.PHOTO_IMAGE)) {
            final List<ImageBean> imageBeans = intent.getParcelableArrayListExtra(ImagePicker.INTENT_RESULT_DATA);
            for (ImageBean imageBean : imageBeans) {
                //进行图片逐个压缩
                if (getContext() != null)
                    Luban.with(getContext())
                            .load(imageBean.getImagePath())
                            .ignoreBy(1)
                            .setTargetDir(getContext().getApplicationContext().getFilesDir().getAbsolutePath())
                            .setCompressListener(new OnCompressListener() {
                                @Override
                                public void onStart() {
                                    // 压缩开始前调用，可以在方法内启动 loading UI
                                }

                                @Override
                                public void onSuccess(File file) {
                                    // 压缩成功后调用，返回压缩后的图片文件
                                    mCommentPicFiles.add(file);
                                    showChooesPic();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    // 当压缩过程出现问题时调用

                                }
                            }).launch();
            }
        }
    }
}
