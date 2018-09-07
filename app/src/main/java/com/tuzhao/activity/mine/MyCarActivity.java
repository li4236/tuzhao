package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Car;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.ViewUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/8/20.
 */
public class MyCarActivity extends BaseRefreshActivity<Car> {

    /**
     * true(从预订车位跳转过来，选择车来停车的)
     */
    private boolean mChooseCar;

    private int REQUEST_CODE = 0x123;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mRecyclerView.setRefreshEnabled(false);
        mRecyclerView.setLoadingMoreEnable(false);
        mChooseCar = getIntent().getBooleanExtra(ConstansUtil.INTENT_MESSAGE, false);
        if (mChooseCar) {
            goneView(findViewById(R.id.add_car_tv));
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            layoutParams.topToBottom = R.id.top_divider;
            layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            mRecyclerView.setLayoutParams(layoutParams);
            mCommonAdapter.setPlaceholderFooterView(16);
        } else {
            findViewById(R.id.add_car_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!UserManager.getInstance().getUserInfo().isCertification()) {
                        ViewUtil.showCertificationDialog(MyCarActivity.this, "添加车辆");
                    } else {
                        startActivityForResult(AddNewCarActivity.class, ConstansUtil.REQUSET_CODE);
                    }
                }
            });
        }
    }

    @Override
    protected RecyclerView.LayoutManager createLayouManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    protected void initData() {
        if (mChooseCar && getIntent().hasExtra(ConstansUtil.CAR_NUMBER)) {
            mCommonAdapter.setNewData(getIntent().<Car>getParcelableArrayListExtra(ConstansUtil.CAR_NUMBER));
        } else {
            super.initData();
        }
    }

    @Override
    protected void loadData() {
        getOkgos(HttpConstants.getCarNumber)
                .execute(new JsonCallback<Base_Class_List_Info<Car>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<Car> o, Call call, Response response) {
                        if (mChooseCar) {
                            //如果是选择车辆的则只显示已通过的车辆
                            ArrayList<Car> cars = new ArrayList<>();
                            for (Car car : o.data) {
                                if (car.getStatus().equals("2")) {
                                    cars.add(car);
                                }
                            }
                            o.data = cars;
                        } else {
                            Collections.sort(o.data, new Comparator<Car>() {
                                @Override
                                public int compare(Car o1, Car o2) {
                                    return o1.getSortStatus() - o2.getSortStatus();
                                }
                            });
                        }
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

    private void deleteCar(final Car car) {
        getOkGo(HttpConstants.deleteUserCarNumber)
                .params("car_number", car.getCarNumber())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        mCommonAdapter.notifyRemoveData(car);
                        if (mCommonAdapter.getDataSize() == 0) {
                            mRecyclerView.showEmpty();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast("删除失败，请稍后重试");
                        }
                    }
                });

    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_my_car_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, final Car car, int position) {
        holder.setText(R.id.car_number, car.getCarNumber());
        if (mChooseCar || car.getStatus().equals("1")) {
            //如果是选择车辆的则禁止侧滑删除
            ((SwipeMenuLayout) holder.itemView).setSwipeEnable(false);
            if (car.getStatus().equals("1")) {
                holder.setOnClickListener(R.id.car_number_cv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(TAG, "onClick: " + car.getCarNumber());
                        startActivityForResult(AuditCarActivity.class, REQUEST_CODE, ConstansUtil.INTENT_MESSAGE, car);
                    }
                });
            }
        } else {
            holder.setOnClickListener(R.id.delete_car_number, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TipeDialog.Builder(MyCarActivity.this)
                            .setMessage("确定删除该车辆吗？")
                            .setTitle("提示")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteCar(car);
                                }
                            }).create()
                            .show();
                }
            });
        }

        switch (car.getStatus()) {
            case "1":
                holder.showPic(R.id.car_status, R.drawable.ic_audit);
                break;
            case "2":
                holder.showPic(R.id.car_status, R.drawable.ic_adopt);
                if (mChooseCar) {
                    holder.setOnClickListener(R.id.car_number, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra(ConstansUtil.INTENT_MESSAGE, car.getCarNumber());
                            intent.putParcelableArrayListExtra(ConstansUtil.CAR_NUMBER, (ArrayList<? extends Parcelable>) mCommonAdapter.getData());
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                }
                break;
            case "3":
                holder.showPic(R.id.car_status, R.drawable.ic_fail);
                break;
        }
    }

    @Override
    protected int resourceId() {
        return R.layout.activity_my_car_layout;
    }

    @NonNull
    @Override
    protected String title() {
        return "我的车辆";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstansUtil.REQUSET_CODE && resultCode == RESULT_OK && data != null) {
            //添加车辆返回的
            Car car = new Car();
            car.setCarNumber(data.getStringExtra(ConstansUtil.INTENT_MESSAGE));
            car.setStatus("1");
            int position = 0;
            for (int i = 0; i < mCommonAdapter.getDataSize(); i++) {
                if (!mCommonAdapter.get(i).getStatus().equals("2")) {
                    position = i;
                    break;
                }
            }
            mCommonAdapter.addData(position, car);
            mRecyclerView.showData();
        } else if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //取消申请添加车辆
            mCommonAdapter.notifyRemoveData((Car) data.getParcelableExtra(ConstansUtil.INTENT_MESSAGE));
            if (mCommonAdapter.getDataSize() == 0) {
                mRecyclerView.showEmpty();
            }
        }
    }

}
