package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkSpaceInfo;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.others.SkipTopBottomDivider;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.IntentObserver;

import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/5/5.
 */

public class AuditParkSpaceActivity extends BaseRefreshActivity<ParkSpaceInfo> implements IntentObserver {

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mRecyclerView.addItemDecoration(new SkipTopBottomDivider(this, true, true));
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

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_audit_parkspace_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, final ParkSpaceInfo parkSpaceInfo, int position) {
        String auditStatus;
        switch (parkSpaceInfo.getStatus()) {
            case "0":
                auditStatus = "已提交";
                break;
            case "1":
                auditStatus = "审核中";
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
        holder.setText(R.id.my_parkspace_description, parkSpaceInfo.getParkSpaceDescription())
                .setText(R.id.my_parkspace_park_location, parkSpaceInfo.getParkLotName())
                .setText(R.id.audit_parkspace_status, auditStatus)
                .itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuditParkSpaceActivity.this, ApplyParkSpaceProgressActivity.class);
                intent.putExtra(ConstansUtil.PARK_SPACE_INFO, parkSpaceInfo);
                startActivity(intent);
            }
        });

        if (auditStatus.equals("上门安装")) {
            holder.setText(R.id.audit_parkspace_install_time, "预计安装时间:" + parkSpaceInfo.getInstallTime());
        } else if (auditStatus.equals("上门拆卸")) {
            holder.setText(R.id.audit_parkspace_install_time, "预计拆卸时间:" + parkSpaceInfo.getInstallTime());
        } else if (auditStatus.equals("审核失败") || auditStatus.equals("审核失败")) {
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
        if (Objects.equals(intent.getAction(), ConstansUtil.CANCEL_APPLY_PARK_SPACE)) {
            String parkSpaceId = intent.getStringExtra(ConstansUtil.PARK_SPACE_ID);
            for (int i = 0; i < mCommonAdapter.getDataSize(); i++) {
                if (mCommonAdapter.get(i).getId().equals(parkSpaceId)) {
                    mCommonAdapter.notifyRemoveData(i);
                    break;
                }
            }
        } else if (Objects.equals(intent.getAction(), ConstansUtil.MODIFY_AUDIT_PARK_SPACE_INFO)) {
            ParkSpaceInfo parkSpaceInfo = intent.getParcelableExtra(ConstansUtil.PARK_SPACE_INFO);
            for (int i = 0; i < mCommonAdapter.getDataSize(); i++) {
                if (mCommonAdapter.get(i).getId().equals(parkSpaceInfo.getId())) {
                    mCommonAdapter.notifyDataChange(i, parkSpaceInfo);
                    break;
                }
            }
        }
    }

}
