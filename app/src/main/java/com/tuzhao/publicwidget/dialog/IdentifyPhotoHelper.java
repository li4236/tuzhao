package com.tuzhao.publicwidget.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.utils.ImageUtil;

/**
 * Created by juncoder on 2018/8/15.
 */
public class IdentifyPhotoHelper {

    private View mView;

    private TextView mCancelDialog;

    private TextView mTitle;

    private ImageView mImageView;

    private TextView mHint;

    private TextView mUpload;

    public IdentifyPhotoHelper(Context context, int type) {
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_identify_layout, null);
        mCancelDialog = mView.findViewById(R.id.cancel_dialog);
        mTitle = mView.findViewById(R.id.dialog_title);
        mImageView = mView.findViewById(R.id.sample_image);
        mHint = mView.findViewById(R.id.hint_tv);
        mUpload = mView.findViewById(R.id.upload_tv);
        initData(type);
    }

    /**
     * @param type 0（身份证正面），1（身份证反面），2（驾照主页），3（行驶证正面），4（行驶证反面），5（人车合影）
     */
    private void initData(int type) {
        String hint;
        String title;
        switch (type) {
            case 0:
                title = "身份证人像面";
                hint = "请按示例图上传身份证人像面照片\n请确保四角对齐、文字清晰、无反光、无遮挡";
                ImageUtil.showImgPic(mImageView, R.drawable.ic_idcard);
                break;
            case 1:
                title = "身份证国徽面";
                hint = "请按示例图上传身份证国徽面照片\n请确保四角对齐、文字清晰、无反光、无遮挡";
                ImageUtil.showImgPic(mImageView, R.drawable.ic_idcard2);
                break;
            case 2:
                title = "驾驶证主页照";
                hint = "请按示例图上传驾驶证主页照\n请确保四角对齐、文字清晰、无反光、无遮挡";
                ImageUtil.showImgPic(mImageView, R.drawable.driving_license_positive);
                break;
            case 3:
                title = "行驶证主页照";
                hint = "请按示例图上传行驶证主页照\n请确保四角对齐、文字清晰、无反光、无遮挡";
                ImageUtil.showImgPic(mImageView, R.drawable.vehicle_license_positive);
                break;
            case 4:
                title = "行驶证副页照";
                hint = "请按示例图上传行驶证副页照\n请确保四角对齐、文字清晰、无反光、无遮挡";
                ImageUtil.showImgPic(mImageView, R.drawable.vehicle_license_negative);
                break;
            case 5:
                title = "人车合照";
                hint = "请按示例图上传人车合照\n请保证人脸清晰，且全脸在画面中\n请将车门打开并确保车牌清晰可见";
                ImageUtil.showImgPic(mImageView, R.drawable.ic_mancar);
                break;
            default:
                title = "";
                hint = "";
        }
        mTitle.setText(title);
        mHint.setText(hint);
    }

    public void setCancelListener(View.OnClickListener listener) {
        mCancelDialog.setOnClickListener(listener);
    }

    public void setUploadListener(View.OnClickListener listener) {
        mUpload.setOnClickListener(listener);
    }

    public View getView() {
        return mView;
    }

}
