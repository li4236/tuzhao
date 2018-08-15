package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkSpaceInfo;
import com.tuzhao.publicwidget.customView.CircleView;
import com.tuzhao.utils.ConstansUtil;

/**
 * Created by juncoder on 2018/6/23.
 */
public class ApplyParkSpaceProgressActivity extends BaseStatusActivity {

    private View mStartReviewLine;

    private CircleView mReviewCv;

    private View mEndReviewLine;

    private View mStartAuditedLine;

    private CircleView mAuditedCv;

    private View mEndAuditedLine;

    private View mStartReadyInstallLine;

    private CircleView mReadyInstallCv;

    private View mEndReadyInstallLine;

    private View mStartInstallFinishLine;

    private CircleView mInstallFinishCv;

    private TextView mReviewTv;

    private TextView mAuditedTv;

    private TextView mReadyInstallTv;

    private TextView mInstallFinishTv;

    @Override
    protected int resourceId() {
        return R.layout.activity_apply_park_space_progress_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mStartReviewLine = findViewById(R.id.start_review_line);
        mReviewCv = findViewById(R.id.review_cv);
        mEndReviewLine = findViewById(R.id.end_review_line);
        mStartAuditedLine = findViewById(R.id.start_audited_line);
        mAuditedCv = findViewById(R.id.audited_cv);
        mEndAuditedLine = findViewById(R.id.end_audited_line);
        mStartReadyInstallLine = findViewById(R.id.start_ready_install_line);
        mReadyInstallCv = findViewById(R.id.ready_install_cv);
        mEndReadyInstallLine = findViewById(R.id.end_ready_install_line);
        mStartInstallFinishLine = findViewById(R.id.start_install_finish_line);
        mInstallFinishCv = findViewById(R.id.install_finish_cv);
        mReviewTv = findViewById(R.id.review_tv);
        mAuditedTv = findViewById(R.id.audited_tv);
        mReadyInstallTv = findViewById(R.id.ready_install_tv);
        mInstallFinishTv = findViewById(R.id.install_finish_tv);
    }

    @Override
    protected void initData() {
        ParkSpaceInfo parkSpaceInfo;
        if ((parkSpaceInfo = getIntent().getParcelableExtra(ConstansUtil.PARK_SPACE_INFO)) != null) {
            switch (parkSpaceInfo.getStatus()) {
                case "0":

                    break;
                case "1":
                    setReviewChoose();
                    break;
                case "2":
                    setReviewChoose();
                    setAuditedChoose();
                    break;
                case "3":
                    setReviewChoose();
                    setAuditedChoose();
                    setReadyInstallChoose();
                    if (parkSpaceInfo.getType().equals("2")) {
                        mReadyInstallTv.setText("上门拆卸");
                    }
                    break;
                case "4":
                    setReviewChoose();
                    mReadyInstallTv.setText("审核失败");
                    break;
                case "5":
                    setReviewChoose();
                    setAuditedChoose();
                    setReadyInstallChoose();
                    if (parkSpaceInfo.getType().equals("2")) {
                        mReadyInstallTv.setText("上门拆卸");
                    }

                    setInstallFinishChoose();
                    if (parkSpaceInfo.getType().equals("2")) {
                        mInstallFinishTv.setText("拆卸完毕");
                    }
                    break;
                /*case "6":
                    findViewById(R.id.submitted_cl).setVisibility(View.GONE);
                    findViewById(R.id.review_cl).setVisibility(View.GONE);
                    findViewById(R.id.audited_cl).setVisibility(View.GONE);
                    findViewById(R.id.ready_install_cl).setVisibility(View.GONE);
                    findViewById(R.id.install_finish_cl).setVisibility(View.GONE);
                    break;*/
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            String idCardPhotos[] = parkSpaceInfo.getIdCardPhoto().split(",");
            parkSpaceInfo.setIdCardPositiveUrl(HttpConstants.ROOT_IMG_URL_ID_CARD + idCardPhotos[0]);
            parkSpaceInfo.setIdCardNegativeUrl(HttpConstants.ROOT_IMG_URL_ID_CARD + idCardPhotos[1]);

            String propertyPhotos[] = parkSpaceInfo.getPropertyPhoto().split(",");
            parkSpaceInfo.setPropertyFirstUrl(HttpConstants.ROOT_IMG_URL_PROPERTY + propertyPhotos[0]);
            if (propertyPhotos.length > 1) {
                parkSpaceInfo.setPropertySecondUrl(HttpConstants.ROOT_IMG_URL_PROPERTY + propertyPhotos[1]);
            }
            if (propertyPhotos.length > 2) {
                parkSpaceInfo.setPropertyThirdUrl(HttpConstants.ROOT_IMG_URL_PROPERTY + propertyPhotos[2]);
            }

            //transaction.replace(R.id.apply_park_space_progress_container, ChangeApplyParkSpaceInfoActivity.newInstance(parkSpaceInfo));
            transaction.commit();
        } else {
            showFiveToast("获取申请进度失败，请稍后重试");
        }
    }

    @NonNull
    @Override
    protected String title() {
        return "申请进度";
    }

    private void setReviewChoose() {
        mStartReviewLine.setBackgroundColor(ConstansUtil.Y2_COLOR);
        mReviewCv.setColor(ConstansUtil.Y2_COLOR);
        mEndReviewLine.setBackgroundColor(ConstansUtil.Y2_COLOR);
        mReviewTv.setTextColor(ConstansUtil.B1_COLOR);
    }

    private void setAuditedChoose() {
        mStartAuditedLine.setBackgroundColor(ConstansUtil.Y2_COLOR);
        mAuditedCv.setColor(ConstansUtil.Y2_COLOR);
        mEndAuditedLine.setBackgroundColor(ConstansUtil.Y2_COLOR);
        mAuditedTv.setTextColor(ConstansUtil.B1_COLOR);
    }

    private void setReadyInstallChoose() {
        mStartReadyInstallLine.setBackgroundColor(ConstansUtil.Y2_COLOR);
        mReadyInstallCv.setColor(ConstansUtil.Y2_COLOR);
        mEndReadyInstallLine.setBackgroundColor(ConstansUtil.Y2_COLOR);
        mReadyInstallTv.setTextColor(ConstansUtil.B1_COLOR);
    }

    private void setInstallFinishChoose() {
        mStartInstallFinishLine.setBackgroundColor(ConstansUtil.Y2_COLOR);
        mInstallFinishCv.setColor(ConstansUtil.Y2_COLOR);
        mInstallFinishTv.setTextColor(ConstansUtil.B1_COLOR);
    }

}
