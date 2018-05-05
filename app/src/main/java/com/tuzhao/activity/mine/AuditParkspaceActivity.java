package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.others.SkipTopBottomDivider;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/5/5.
 */

public class AuditParkspaceActivity extends BaseRefreshActivity<Park_Info> {

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
                .execute(new JsonCallback<Base_Class_List_Info<Park_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<Park_Info> o, Call call, Response response) {
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
    protected void bindData(BaseViewHolder holder, Park_Info park_info, int position) {
        String auditStatus;
        switch (park_info.getPark_status()) {
            case "1":
                auditStatus = "安装审核中";
                break;
            case "2":
                auditStatus = "待安装";
                break;
            case "3":
                auditStatus = "安装成功";
                break;
            case "4":
                auditStatus = "安装车位未通过审核";
                break;
            case "5":
                auditStatus = "拆卸审核中";
                break;
            case "6":
                auditStatus = "待拆卸";
                break;
            case "7":
                auditStatus = "押金退还中";
                break;
            case "8":
                auditStatus = "已删除";
                break;
            case "9":
                auditStatus = "车位拆卸未通过审核";
                break;
            default:
                auditStatus = "未知状态";
                break;
        }
        holder.setText(R.id.my_parkspace_description, park_info.getParkspace_name())
                .setText(R.id.my_parkspace_park_location, park_info.getLocation_describe())
                .setText(R.id.audit_parkspace_status, auditStatus);

        if (auditStatus.equals("待安装")) {
            holder.setText(R.id.audit_parkspace_install_time, "预计安装时间:" + park_info.getInstallTime());
        } else if (auditStatus.equals("待拆卸")) {
            holder.setText(R.id.audit_parkspace_install_time, "预计拆卸时间:" + park_info.getInstallTime());
        } else if (auditStatus.equals("车位拆卸未通过审核") || auditStatus.equals("安装车位未通过审核")) {
            holder.setText(R.id.audit_parkspace_install_time, "原因:" + park_info.getReason());
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

}
