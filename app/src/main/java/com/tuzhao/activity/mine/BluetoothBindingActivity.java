package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.BluetoothBindingFriendInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.others.SkipTopBottomDivider;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/4/8.
 */

public class BluetoothBindingActivity extends BaseStatusActivity {

    private BluetoothBindingAdapter mAdapter;

    private TextView mBindindFriendNumber;

    private TextView mBindindTv;

    private TextView mBindingStatus;

    private EditText mFirendName;

    private EditText mFriendPhone;

    private TipeDialog mModifyNameDialog;

    private TipeDialog mAddFriendPhoneDialog;

    private DateUtil mDateUtil;

    private String mParkSpaceId;

    @Override
    protected void initView(Bundle savedInstanceState) {
        if ((mParkSpaceId = getIntent().getStringExtra(ConstansUtil.PARK_SPACE_ID)) == null) {
            showFiveToast("打开失败，请退出重试");
            finish();
        }

        RecyclerView recyclerView = findViewById(R.id.base_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SkipTopBottomDivider(this, false, true));
        mAdapter = new BluetoothBindingAdapter();
        recyclerView.setAdapter(mAdapter);

        mBindindTv = findViewById(R.id.bluetooth_binding_bing);
        mBindingStatus = findViewById(R.id.bluetooth_binding_bing_status);
        mBindindFriendNumber = findViewById(R.id.bluetooth_binding_bind_number);
        TextView bindingHint = findViewById(R.id.bluetooth_binding_hint);
        String hint = "温馨提醒:\n1、蓝牙绑定后，当绑定蓝牙的手机靠近车位锁时，车位锁将自动放下";
        bindingHint.setText(hint);

        findViewById(R.id.bluetooth_binding_add_device).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.getData().size() >= 5) {
                    showFiveToast("最多只能添加5个亲友设备哦");
                } else {
                    showPhoneDialog();
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        getOkGo(HttpConstants.getFriendDevice)
                .params("parkSpaceId", mParkSpaceId)
                .execute(new JsonCallback<Base_Class_List_Info<BluetoothBindingFriendInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<BluetoothBindingFriendInfo> o, Call call, Response response) {
                        mAdapter.addData(o.data);
                        updateFriendNumber();
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {

                        }
                    }
                });
    }

    @Override
    protected int resourceId() {
        return R.layout.activity_bluetooth_binding_layout;
    }

    @NonNull
    @Override
    protected String title() {
        return "蓝牙绑定";
    }

    private void updateFriendNumber() {
        String friendNumber = "亲友绑定(" + mAdapter.getData().size() + "/5)";
        mBindindFriendNumber.setText(friendNumber);
    }

    private void showNameDialog(final int position) {
        if (mModifyNameDialog == null) {
            ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.dialog_edit_layout, null);
            mFirendName = constraintLayout.findViewById(R.id.dialog_et);
            mFirendName.setHint("请输入亲友备注");

            mModifyNameDialog = new TipeDialog.Builder(this)
                    .setContentView(constraintLayout)
                    .setTitle("修改备注")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("确认修改", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (TextUtils.isEmpty(mFirendName.getText().toString().trim())) {
                                showFiveToast("备注不能为空哦");
                            } else {
                                modifyFriendName(mAdapter.getData().get(position).getFriendDeviceId(), mFirendName.getText().toString(), position);
                            }

                        }
                    })
                    .create();
        }
        mModifyNameDialog.show();
        mFirendName.setText(mAdapter.getData().get(position).getNoteName());
        mFirendName.setSelection(mFirendName.getText().toString().length());
    }

    private void showPhoneDialog() {
        if (mAddFriendPhoneDialog == null) {
            mDateUtil = new DateUtil();

            ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.dialog_edit_layout, null);
            mFriendPhone = constraintLayout.findViewById(R.id.dialog_et);
            mFriendPhone.setHint("请输入亲友手机号");
            mFriendPhone.setInputType(EditorInfo.TYPE_CLASS_PHONE);

            mAddFriendPhoneDialog = new TipeDialog.Builder(this)
                    .setContentView(constraintLayout)
                    .setTitle("添加手机")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("确认修改", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mDateUtil.isPhoneNumble(mFriendPhone.getText().toString().trim())) {
                                addFriendDevice(mFriendPhone.getText().toString().trim());
                            } else {
                                showFiveToast("你输入的手机不正确");
                            }
                        }
                    })
                    .create();
        }
        mAddFriendPhoneDialog.show();
    }

    /**
     * @param friendDeviceId 亲友设备的id
     * @param noteName       亲友设备的备注
     * @param position       所在的位置
     */
    private void modifyFriendName(String friendDeviceId, final String noteName, final int position) {
        showLoadingDialog("正在修改");
        getOkGo(HttpConstants.modifyFriendDeviceName)
                .params("parkSpaceId", mParkSpaceId)
                .params("friendDeviceId", friendDeviceId)
                .params("noteName", noteName)
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        mAdapter.getData().get(position).setNoteName(noteName);
                        mAdapter.notifyItemChanged(position);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {

                        }
                    }
                });
    }

    private void deleteFriendDevice(final int position) {
        showLoadingDialog("正在删除");
        getOkGo(HttpConstants.deleteFriendDevice)
                .params("friendDeviceId", mAdapter.getData().get(position).getFriendDeviceId())
                .execute(new JsonCallback() {
                    @Override
                    public void onSuccess(Object o, Call call, Response response) {
                        mAdapter.notifyRemoveData(position);
                        updateFriendNumber();
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {

                        }
                    }
                });
    }

    private void addFriendDevice(String telephone) {
        showLoadingDialog("正在添加");
        getOkGo(HttpConstants.addFriendDevice)
                .params("parkSpaceId", mParkSpaceId)
                .params("teltephone", telephone)
                .execute(new JsonCallback<Base_Class_Info<BluetoothBindingFriendInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<BluetoothBindingFriendInfo> o, Call call, Response response) {
                        mAdapter.addData(o.data);
                        updateFriendNumber();
                        mFriendPhone.setText("");
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {

                        }
                    }
                });

    }

    private class BluetoothBindingAdapter extends BaseAdapter<BluetoothBindingFriendInfo> {

        @Override
        protected void conver(@NonNull BaseViewHolder holder, final BluetoothBindingFriendInfo bluetoothBindingFriendInfo, final int position) {
            holder.showCirclePic(R.id.bluetooth_binding_friend_iv, bluetoothBindingFriendInfo.getImgUrl())
                    .setText(R.id.bluetooth_binding_friend_name, bluetoothBindingFriendInfo.getNoteName())
                    .getView(R.id.bluetooth_binding_edit_friend).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNameDialog(position);
                }
            });

            holder.getView(R.id.bluetooth_binding_delete_friend).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = "确认删除" + bluetoothBindingFriendInfo.getNoteName() + "的手机?";
                    new TipeDialog.Builder(BluetoothBindingActivity.this)
                            .setTitle("删除手机")
                            .setMessage(message)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteFriendDevice(position);
                                }
                            })
                            .create()
                            .show();
                }
            });
        }

        @Override
        protected int itemViewId() {
            return R.layout.item_bluetooth_binding_friends;
        }
    }

}
