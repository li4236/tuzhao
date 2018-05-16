package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.others.SkipTopBottomDivider;
import com.tuzhao.publicwidget.others.Voltage;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/5/3.
 */

public class MyParkSpaceActivity extends BaseRefreshActivity<Park_Info> {

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        mRecyclerView.addItemDecoration(new SkipTopBottomDivider(this, false, true));
        mRecyclerView.setLoadingMoreEnable(false);

        findViewById(R.id.my_parkspace_audit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AuditParkspaceActivity.class);
            }
        });

        findViewById(R.id.id_activity_mypark_textview_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AddParkActivity.class);
            }
        });
    }

    @Override
    protected int resourceId() {
        return R.layout.activity_my_parkspace_layout;
    }

    @NonNull
    @Override
    protected String title() {
        return "我的车位";
    }

    @Override
    protected RecyclerView.LayoutManager createLayouManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    protected void loadData() {
        getOkgo(HttpConstants.getParkFromUser)
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
                                int code = Integer.parseInt(e.getMessage());
                                switch (code) {
                                    case 901:
                                        showFiveToast("服务器正在维护中");
                                        break;
                                }
                            }
                        });
                    }
                });
    }

    private String getParkSpaceStatus(Park_Info park_info) {
        if (!park_info.getParking_user_id().equals("-1")) {
            return "出租中";
        }

        String nowDate = DateUtil.getCurrentYearToMinutes();
        String afterTwoMinutesDate = DateUtil.getCurrentYearToMinutes(System.currentTimeMillis() + 1000 * 60 * 1);

        if (DateUtil.isInShareDate(nowDate, afterTwoMinutesDate, park_info.getOpen_date()) == 0) {
            return "停租";
        }

        if (0 == DateUtil.isInPauseDate(nowDate, afterTwoMinutesDate, park_info.getPauseShareDate())) {
            return "停租";
        }

        if (0 == DateUtil.isInShareDay(nowDate, afterTwoMinutesDate, park_info.getShareDay())) {
            return "停租";
        }

        if (null == DateUtil.isInShareTime(nowDate, afterTwoMinutesDate, park_info.getOpen_time(), false)) {
            return "停租";
        }

        if (DateUtil.isInOrderDate(nowDate, afterTwoMinutesDate, park_info.getOrder_times())) {
            return "已预约";
        }

        return "空闲";
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_my_parkspace_layout;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstansUtil.REQUSET_CODE && resultCode == RESULT_OK && data != null) {
            if (data.hasExtra(ConstansUtil.FOR_REQUEST_RESULT)) {
                Park_Info parkInfo = (Park_Info) data.getSerializableExtra(ConstansUtil.FOR_REQUEST_RESULT);
                //如果在车位设置或者共享时间修改那里改了数据的则同步数据
                for (int i = 0; i < mCommonAdapter.getData().size(); i++) {
                    if (parkInfo.getId().equals(mCommonAdapter.getData().get(i).getId())) {
                        mCommonAdapter.getData().set(i, parkInfo);
                        mCommonAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            } else if (data.hasExtra(ConstansUtil.PARK_SPACE_ID)) {
                String parkspaceId = data.getStringExtra(ConstansUtil.PARK_SPACE_ID);
                for (int i = 0; i < mCommonAdapter.getData().size(); i++) {
                    if (parkspaceId.equals(mCommonAdapter.getData().get(i).getId())) {
                        mCommonAdapter.removeData(i);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void bindData(BaseViewHolder holder, final Park_Info park_info, int position) {
        holder.setText(R.id.my_parkspace_description, park_info.getLocation_describe())
                .setText(R.id.my_parkspace_park_location, park_info.getParkspace_name());
        ((Voltage) holder.getView(R.id.my_parkspace_voltage)).setVoltage((int) ((Double.valueOf(park_info.getVoltage()) - 4.8) * 100 / 1.2));
        TextView status = holder.getView(R.id.my_parkspace_status);
        ImageView statusIv = holder.getView(R.id.my_parkspace_status_iv);
        if (park_info.getPark_status().equals("1")) {
            status.setText("未开放");
            statusIv.setBackgroundResource(R.drawable.circle_r5);
        } else if (park_info.getPark_status().equals("3")) {
            status.setText("停租");
            statusIv.setBackgroundResource(R.drawable.circle_r5);
        } else if (park_info.getPark_status().equals("2")) {
            status.setText(getParkSpaceStatus(park_info));
            if (status.getText().toString().equals("空闲")) {
                statusIv.setBackgroundResource(R.drawable.circle_green7);
            } else {
                statusIv.setBackgroundResource(R.drawable.circle_r5);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(ParkSpaceSettingActivity.class, ConstansUtil.REQUSET_CODE, ConstansUtil.PARK_SPACE_INFO, park_info);
            }
        });
    }

}
