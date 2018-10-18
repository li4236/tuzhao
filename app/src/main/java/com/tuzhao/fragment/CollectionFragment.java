package com.tuzhao.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.tuzhao.R;
import com.tuzhao.activity.ChargestationDetailActivity;
import com.tuzhao.activity.ParkspaceDetailActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.fragment.base.BaseRefreshFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.CollectionInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/9/28.
 */
public class CollectionFragment extends BaseRefreshFragment<CollectionInfo> implements IntentObserver {

    private boolean mIsEdit;

    private String mType;

    public static CollectionFragment getInstance(String type) {
        CollectionFragment fragment = new CollectionFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ConstansUtil.TYPE, type);
        fragment.setArguments(bundle);
        fragment.setTAG(fragment.getTAG() + ",type:" + type);
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getString(ConstansUtil.TYPE);
        }
        IntentObserable.registerObserver(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            //如果点进去详情页那里删除了的，则这边也删除
            CollectionInfo collectionInfo = data.getParcelableExtra(ConstansUtil.INTENT_MESSAGE);
            notifyRemoveData(collectionInfo);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IntentObserable.unregisterObserver(this);
    }

    @Override
    protected void loadData() {
        getOkgos(HttpConstants.getCollectionDatas, "type", mType)
                .execute(new JsonCallback<Base_Class_List_Info<CollectionInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<CollectionInfo> o, Call call, Response response) {
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

    private void deleteCollection(final CollectionInfo collectionInfo) {
        getOkGo(HttpConstants.deleteCollection)
                .params("belong_id", "1".equals(mType) ? collectionInfo.getParkspace_id() : collectionInfo.getChargestation_id())
                .params("type", mType)
                .params("citycode", collectionInfo.getCitycode())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        notifyRemoveData(collectionInfo);
                        if (getCheckCount() == 0) {
                            //删除全部选中的之后，变为未编辑状态，把选中框隐藏
                            mIsEdit = false;
                            mCommonAdapter.notifyDataSetChanged(false);
                            if (getActivity() != null && getActivity() instanceof SuccessCallback) {
                                ((SuccessCallback) getActivity()).onSuccess(true);
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        loadDataFail(e, new LoadFailCallback() {
                            @Override
                            public void onLoadFail(Exception e) {
                                showFiveToast("删除失败，请稍后重试");
                            }
                        });
                    }
                });
    }

    public int getCheckCount() {
        int count = 0;
        for (CollectionInfo collectionInfo : mCommonAdapter.getData()) {
            if (collectionInfo.isSelect()) {
                count++;
                //知道有选中的数据就行了
                break;
            }
        }
        return count;
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_collection_refactory_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, final CollectionInfo collectionInfo, int position) {
        switch (collectionInfo.getType()) {
            case "1":
                holder.showImpPic(R.id.collection_iv, HttpConstants.ROOT_IMG_URL_PS + (collectionInfo.getPimgs_url() != null ?
                        ((collectionInfo.getPimgs_url().split(","))[0]) : ""))
                        .setText(R.id.collection_name_tv, collectionInfo.getPs_name())
                        .setText(R.id.collection_cout_tv, collectionInfo.getP_count() + "个车位")
                        .setText(R.id.collection_address_tv, collectionInfo.getPs_address())
                        .showView(R.id.collection_cout_tv);
                break;
            case "2":
                holder.showImpPic(R.id.collection_iv, HttpConstants.ROOT_IMG_URL_PS + (collectionInfo.getCimgs_url() != null ?
                        ((collectionInfo.getPimgs_url().split(","))[0]) : ""))
                        .setText(R.id.collection_name_tv, collectionInfo.getCs_name())
                        .setText(R.id.collection_cout_tv, collectionInfo.getC_count() + "个电桩")
                        .setText(R.id.collection_address_tv, collectionInfo.getCs_address())
                        .showView(R.id.collection_cout_tv);
                break;
        }

        final android.widget.CheckBox checkBox = holder.getView(R.id.collection_cb);
        final CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                collectionInfo.setSelect(isChecked);
            }
        };
        checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsEdit) {
                    checkBox.setOnCheckedChangeListener(null);
                    checkBox.setChecked(!checkBox.isChecked());
                    checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
                    collectionInfo.setSelect(checkBox.isChecked());
                } else {
                    startActivity(collectionInfo);
                }
            }
        });
    }

    @Override
    protected void bindData(BaseViewHolder holder, CollectionInfo collectionInfo, int position, List<Object> payloads) {
        super.bindData(holder, collectionInfo, position, payloads);
        if (mIsEdit) {
            holder.showView(R.id.collection_cb);
        } else {
            holder.goneView(R.id.collection_cb)
                    .setCheckboxCheck(R.id.collection_cb, false);
            collectionInfo.setSelect(false);
        }

    }

    private void startActivity(CollectionInfo collectionInfo) {
        switch (collectionInfo.getType()) {
            case "1":
                startActivityForResultByFragment(ParkspaceDetailActivity.class, ConstansUtil.REQUSET_CODE, "parkspace_id", collectionInfo.getParkspace_id(),
                        "city_code", collectionInfo.getCitycode());
                break;
            case "2":
                startActivityForResultByFragment(ChargestationDetailActivity.class, ConstansUtil.REQUEST_CODE_TWO, "chargestation_id", collectionInfo.getChargestation_id(),
                        "city_code", collectionInfo.getCitycode());
                break;
        }
    }

    @Override
    protected int resourceId() {
        return 0;
    }

    @Override
    public void onReceive(Intent intent) {
        if (ConstansUtil.EDIT_STATUS.equals(intent.getAction())) {
            mIsEdit = intent.getBooleanExtra(ConstansUtil.INTENT_MESSAGE, false);
            mCommonAdapter.notifyDataSetChanged(mIsEdit);
        } else if (ConstansUtil.DELETE_COLLECTION.equals(intent.getAction())) {
            for (CollectionInfo collectionInfo : mCommonAdapter.getData()) {
                if (collectionInfo.isSelect()) {
                    deleteCollection(collectionInfo);
                }
            }

        }
    }

}
