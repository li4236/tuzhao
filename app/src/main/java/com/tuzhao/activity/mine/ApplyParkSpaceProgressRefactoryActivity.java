package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.View;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkSpaceInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.others.CircleView;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/7/31.
 */
public class ApplyParkSpaceProgressRefactoryActivity extends BaseStatusActivity implements View.OnClickListener, IntentObserver {

    private TextView mParkLotName;

    private TextView mParkSpaceName;

    private TextView mApplyTime;

    private CircleView mUnderReviewCv;

    private View mEndUnderReviewLine;

    private View mStartWatingInstallationLine;

    private CircleView mWatingInstallationCv;

    private View mEndWatingInstallationLine;

    private View mStartInstallationCompleteLine;

    private CircleView mInstallationCompleteCv;

    private TextView mUnderReviewTv;

    private TextView mWatingInstallationTv;

    private TextView mInstallationCompleteTv;

    private TextView mExpectedInstallTime;

    private TextView mHandleResult;

    private TextView mCancelApply;

    private TextView mModifyInfo;

    private ParkSpaceInfo mParkSpaceInfo;

    private TipeDialog mCancelDialog;

    @Override
    protected int resourceId() {
        return R.layout.activity_apply_park_space_progress_refactory_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mParkLotName = findViewById(R.id.park_lot_name);
        mParkSpaceName = findViewById(R.id.park_space_name);
        mApplyTime = findViewById(R.id.apply_time);
        mUnderReviewCv = findViewById(R.id.under_review_cv);
        mEndUnderReviewLine = findViewById(R.id.end_under_review_line);
        mUnderReviewTv = findViewById(R.id.under_review_tv);
        mStartWatingInstallationLine = findViewById(R.id.start_waiting_installation_line);
        mWatingInstallationCv = findViewById(R.id.waiting_installation_cv);
        mWatingInstallationTv = findViewById(R.id.waiting_installation_tv);
        mEndWatingInstallationLine = findViewById(R.id.end_waiting_installation_line);
        mStartInstallationCompleteLine = findViewById(R.id.start_installation_complete_line);
        mInstallationCompleteCv = findViewById(R.id.installation_complete_cv);
        mInstallationCompleteTv = findViewById(R.id.installation_complete_tv);
        mExpectedInstallTime = findViewById(R.id.expected_install_time);
        mHandleResult = findViewById(R.id.handle_result);
        mCancelApply = findViewById(R.id.cancel_apply_tv);
        mModifyInfo = findViewById(R.id.modify_info_tv);

        findViewById(R.id.contact_service).setOnClickListener(this);
        mCancelApply.setOnClickListener(this);
        mModifyInfo.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        if ((mParkSpaceInfo = getIntent().getParcelableExtra(ConstansUtil.PARK_SPACE_INFO)) != null) {
            mParkLotName.setText(mParkSpaceInfo.getParkLotName());
            mParkSpaceName.setText(mParkSpaceInfo.getParkSpaceDescription());
            mApplyTime.setText(mParkSpaceInfo.getApplyTime().substring(0, mParkSpaceInfo.getApplyTime().indexOf(" ")));
            switch (mParkSpaceInfo.getStatus()) {
                case "0":
                case "1":
                    mHandleResult.setText("正在审核中，会有客服电话致电，请保持正常通讯，留意接听");
                    if (!isInstallation()) {
                        goneInstallTime();
                        goneView(mModifyInfo);
                        becomeBigger();
                    } else {
                        goneInstallTime();
                    }
                    break;
                case "2":
                    mExpectedInstallTime.setText("预计上门：" + mParkSpaceInfo.getInstallTime());
                    if (isInstallation()) {
                        mHandleResult.setText("恭喜已通过审核，近期会有安装师傅上门安装车锁，请保持正常通讯。");
                    } else {
                        mWatingInstallationTv.setText("拆卸中");
                        mInstallationCompleteTv.setText("已退押金");
                        mHandleResult.setText("已通过审核，近期会有安装师傅上门拆卸车锁，请保持正常通讯。");
                    }
                    setWatingInstallationChoose();
                    goneButton();
                    break;
                case "3":
                    if (isInstallation()) {
                        mExpectedInstallTime.setText("预计上门：" + mParkSpaceInfo.getInstallTime());
                        mHandleResult.setText("恭喜您的车位已完成安装并上线，车位被租用，您将会获得收益");
                    } else {
                        mWatingInstallationTv.setText("拆卸中");
                        mInstallationCompleteTv.setText("已退押金");
                        goneInstallTime();
                        if (mParkSpaceInfo.getDepositStatus().equals("2")) {
                            mHandleResult.setText("车位已完成拆卸并下线，车锁押金已退回原支付账户");
                            setInstallationCompleteChoose();
                        } else {
                            mHandleResult.setText("车锁已完成拆卸，正在退款中，押金将在24小时内退回原支付账户");
                        }
                    }
                    setWatingInstallationChoose();
                    goneButton();
                    break;
                case "4":
                    goneInstallTime();
                    mHandleResult.setText("抱歉，审核未通过。" + mParkSpaceInfo.getReason());
                    break;
                case "5":
                    mWatingInstallationTv.setText("拆卸中");
                    mInstallationCompleteTv.setText("已退押金");
                    mHandleResult.setText("车位已完成拆卸并下线，车锁押金已退回原支付账户");
                    setInstallationCompleteChoose();
                    setWatingInstallationChoose();
                    goneInstallTime();
                    goneButton();
                    break;
                case "6":
                    mUnderReviewCv.setColor(ConstansUtil.G10_COLOR);
                    mEndUnderReviewLine.setBackgroundColor(ConstansUtil.G10_COLOR);
                    mUnderReviewTv.setTextColor(ConstansUtil.G10_COLOR);
                    if (isInstallation()) {
                        if (mParkSpaceInfo.getDepositStatus().equals("2")) {
                            mHandleResult.setText("已取消申请安装，押金已退回原支付账户");
                        } else {
                            mHandleResult.setText("已取消申请安装，押金将在24小时内退回原支付账户");
                        }
                    } else {
                        mHandleResult.setText("已取消申请拆卸，车位已重新上线");
                    }
                    goneInstallTime();
                    goneButton();
                    break;
            }
            IntentObserable.registerObserver(this);
        } else {
            showFiveToast("获取申请进度失败，请稍后重试");
        }
    }

    @NonNull
    @Override
    protected String title() {
        return "申请进度";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_apply_tv:
                showCancelDialog();
                break;
            case R.id.modify_info_tv:
                startActivity(ChangeApplyParkSpaceInfoActivity.class, ConstansUtil.PARK_SPACE_INFO, mParkSpaceInfo);
                break;
            case R.id.contact_service:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:4006505058"));
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onReceive(Intent intent) {
        if (Objects.equals(intent.getAction(), ConstansUtil.MODIFY_AUDIT_PARK_SPACE_INFO)) {
            mParkSpaceInfo = getIntent().getParcelableExtra(ConstansUtil.PARK_SPACE_INFO);
            mParkLotName.setText(mParkSpaceInfo.getParkLotName());
            mParkSpaceName.setText(mParkSpaceInfo.getParkSpaceDescription());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
    }

    private boolean isInstallation() {
        return Objects.equals(mParkSpaceInfo.getType(), "1");
    }

    private void setWatingInstallationChoose() {
        mStartWatingInstallationLine.setBackgroundColor(ConstansUtil.Y2_COLOR);
        mWatingInstallationCv.setColor(ConstansUtil.Y2_COLOR);
        mEndWatingInstallationLine.setBackgroundColor(ConstansUtil.Y2_COLOR);
        mWatingInstallationTv.setTextColor(ConstansUtil.B1_COLOR);
    }

    private void setInstallationCompleteChoose() {
        mStartInstallationCompleteLine.setBackgroundColor(ConstansUtil.Y2_COLOR);
        mInstallationCompleteCv.setColor(ConstansUtil.Y2_COLOR);
        mEndWatingInstallationLine.setBackgroundColor(ConstansUtil.Y2_COLOR);
        mInstallationCompleteTv.setTextColor(ConstansUtil.B1_COLOR);
    }

    private void goneInstallTime() {
        goneView(mExpectedInstallTime);
        ConstraintLayout constraintLayout = findViewById(R.id.top_cl);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.setMargin(R.id.handle_result_tv, ConstraintSet.TOP, dpToPx(20));
        constraintSet.applyTo(constraintLayout);
    }

    private void becomeBigger() {
        ConstraintLayout constraintLayout = findViewById(R.id.apply_park_space_progress_refactory_cl);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.constrainWidth(R.id.cancel_apply_tv, 0);
        constraintSet.clear(R.id.cancel_apply_tv, ConstraintSet.END);
        constraintSet.connect(R.id.cancel_apply_tv, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, dpToPx(24));
        constraintSet.setMargin(R.id.cancel_apply_tv, ConstraintSet.START, dpToPx(24));
        constraintSet.applyTo(constraintLayout);
    }

    private void goneButton() {
        goneView(mCancelApply);
        goneView(mModifyInfo);
    }

    private void showCancelDialog() {
        if (mCancelDialog == null) {
            String message;
            if (isInstallation()) {
                message = "确认取消申请?\n(押金将在24小时内退回原支付账户)";
            } else {
                message = "确认取消申请?\n(取消后将有工作人员上门拆卸)";
            }
            mCancelDialog = new TipeDialog.Builder(this)
                    .setTitle("取消申请")
                    .setMessage(message)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelApplyParkSpace();
                        }
                    })
                    .create();
        }
        mCancelDialog.show();
    }

    private void cancelApplyParkSpace() {
        showLoadingDialog("正在取消");
        getOkGo(HttpConstants.cancelApplyParkSpace)
                .params("parkAuditId", mParkSpaceInfo.getId())
                .params("cityCode", mParkSpaceInfo.getCityCode())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        Intent intent = new Intent();
                        intent.setAction(ConstansUtil.CANCEL_APPLY_PARK_SPACE);
                        intent.putExtra(ConstansUtil.PARK_SPACE_ID, mParkSpaceInfo.getId());
                        IntentObserable.dispatch(intent);
                        showFiveToast("取消成功");
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                case "102":
                                case "104":
                                    showFiveToast("取消申请失败，请稍后重试");
                                    finish();
                                    break;
                                case "103":
                                    showFiveToast("已安排师傅，不可取消");
                                    break;
                                case "105":
                                    showFiveToast("押金退还失败，请稍后重试");
                                    break;
                            }
                        }
                    }
                });
    }

}
