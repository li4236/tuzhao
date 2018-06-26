package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.PayActivity;
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkSpaceInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.others.SkipTopBottomDivider;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/5/5.
 */

public class AuditParkSpaceActivity extends BaseRefreshActivity<ParkSpaceInfo> implements IntentObserver {

    private String mDepositSum;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mRecyclerView.addItemDecoration(new SkipTopBottomDivider(this, true, true));
    }

    @Override
    protected void initData() {
        super.initData();
        IntentObserable.registerObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
    }

    @Override
    protected int resourceId() {
        return 0;
    }

    @NonNull
    @Override
    protected String title() {
        return "车位审核";
    }

    @Override
    protected RecyclerView.LayoutManager createLayouManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    protected void loadData() {
        getOkgo(HttpConstants.getPassingParkFromUser)
                .execute(new JsonCallback<Base_Class_List_Info<ParkSpaceInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<ParkSpaceInfo> o, Call call, Response response) {
                        loadDataSuccess(o);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        loadDataFail(e, new LoadFailCallback() {
                            @Override
                            public void onLoadFail(Exception e) {

                            }
                        });
                    }
                });
    }

    private void getDepositSum(final ParkSpaceInfo parkSpaceInfo) {
        getOkGo(HttpConstants.getDepositSum)
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> o, Call call, Response response) {
                        mDepositSum = DateUtil.deleteZero(o.data) + "元";
                        payDepositSum(parkSpaceInfo);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast("获取押金金额失败，请稍后重试");
                        }
                    }
                });
    }

    private void payDepositSum(ParkSpaceInfo parkSpaceInfo) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstansUtil.PAY_TYPE, "1");
        bundle.putString(ConstansUtil.PAY_MONEY, mDepositSum);
        bundle.putString(ConstansUtil.PARK_SPACE_ID, parkSpaceInfo.getId());
        bundle.putString(ConstansUtil.CITY_CODE, parkSpaceInfo.getCityCode());
        startActivity(PayActivity.class, bundle);
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_audit_parkspace_layout;
    }

    @Override
    protected void bindData(final BaseViewHolder holder, final ParkSpaceInfo parkSpaceInfo, int position) {
        String auditStatus;
        switch (parkSpaceInfo.getStatus()) {
            case "0":
                auditStatus = "已提交";
                break;
            case "1":
                if (parkSpaceInfo.getType().equals("1")) {
                    auditStatus = "安装审核";
                } else {
                    auditStatus = "拆卸审核";
                }
                break;
            case "2":
                auditStatus = "审核通过";
                break;
            case "3":
                if (parkSpaceInfo.getType().equals("1")) {
                    auditStatus = "上门安装";
                } else {
                    auditStatus = "上门拆卸";
                }
                break;
            case "4":
                auditStatus = "审核失败";
                break;
            case "5":
                if (parkSpaceInfo.getType().equals("1")) {
                    auditStatus = "安装完毕";
                } else {
                    auditStatus = "拆卸完毕";
                }
                break;
            case "6":
                auditStatus = "已取消";
                break;
            case "7":
                auditStatus = "押金退还中";
                break;
            default:
                auditStatus = "未知状态";
                break;
        }
        if (!auditStatus.equals("已取消")) {
            if (parkSpaceInfo.getDepositStatus().equals("0")) {
                auditStatus = "待缴押金";
            }
        }
        holder.setText(R.id.my_parkspace_description, parkSpaceInfo.getParkSpaceDescription())
                .setText(R.id.my_parkspace_park_location, parkSpaceInfo.getParkLotName())
                .setText(R.id.audit_parkspace_status, auditStatus)
                .itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getText((TextView) holder.getView(R.id.audit_parkspace_status)).equals("待缴押金")) {
                    if (mDepositSum == null) {
                        getDepositSum(parkSpaceInfo);
                    } else {
                        payDepositSum(parkSpaceInfo);
                    }
                } else {
                    Intent intent = new Intent(AuditParkSpaceActivity.this, ApplyParkSpaceProgressActivity.class);
                    intent.putExtra(ConstansUtil.PARK_SPACE_INFO, parkSpaceInfo);
                    startActivity(intent);
                }
            }
        });

        if (auditStatus.equals("上门安装")) {
            holder.setText(R.id.audit_parkspace_install_time, "预计安装时间:" + parkSpaceInfo.getInstallTime());
        } else if (auditStatus.equals("上门拆卸")) {
            holder.setText(R.id.audit_parkspace_install_time, "预计拆卸时间:" + parkSpaceInfo.getInstallTime());
        } else if (auditStatus.equals("审核失败")) {
            holder.setText(R.id.audit_parkspace_install_time, "原因:" + parkSpaceInfo.getReason());
        } else {
            ConstraintLayout constraintLayout = (ConstraintLayout) holder.itemView;
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.audit_parkspace_status, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(R.id.audit_parkspace_status, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            constraintSet.connect(R.id.my_parkspace_park_location, ConstraintSet.END, R.id.audit_parkspace_status, ConstraintSet.START);
            constraintSet.applyTo(constraintLayout);
        }

    }

    @Override
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ConstansUtil.CANCEL_APPLY_PARK_SPACE:
                    String parkSpaceId = intent.getStringExtra(ConstansUtil.PARK_SPACE_ID);
                    for (int i = 0; i < mCommonAdapter.getDataSize(); i++) {
                        if (mCommonAdapter.get(i).getId().equals(parkSpaceId)) {
                            mCommonAdapter.notifyRemoveData(i);
                            break;
                        }
                    }
                    break;
                case ConstansUtil.MODIFY_AUDIT_PARK_SPACE_INFO:
                    ParkSpaceInfo parkSpaceInfo = intent.getParcelableExtra(ConstansUtil.PARK_SPACE_INFO);
                    for (int i = 0; i < mCommonAdapter.getDataSize(); i++) {
                        if (mCommonAdapter.get(i).getId().equals(parkSpaceInfo.getId())) {
                            mCommonAdapter.notifyDataChange(i, parkSpaceInfo);
                            break;
                        }
                    }
                    break;
                case ConstansUtil.PAY_DEPOSIT_SUM_SUCCESS:
                    String spaceId = intent.getStringExtra(ConstansUtil.PARK_SPACE_ID);
                    for (int i = 0; i < mCommonAdapter.getDataSize(); i++) {
                        if (mCommonAdapter.get(i).getId().equals(spaceId)) {
                            ParkSpaceInfo paySpaceInfo = mCommonAdapter.get(i);
                            paySpaceInfo.setDepositStatus("1");
                            mCommonAdapter.notifyDataChange(i, paySpaceInfo);
                            break;
                        }
                    }
                    break;
            }
        }
    }

}
