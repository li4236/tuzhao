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
import com.tuzhao.info.BluetoothBindingFriendInfo;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.others.SkipTopBottomDivider;
import com.tuzhao.utils.DateUtil;

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

    @Override
    protected void initView(Bundle savedInstanceState) {
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
        BluetoothBindingFriendInfo friendInfo;
        for (int i = 0; i < 3; i++) {
            friendInfo = new BluetoothBindingFriendInfo();
            friendInfo.setNoteName("亲友" + i);
            friendInfo.setImgUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1522131551312&di=52422f4384734a296b537d5040c2e89c&imgtype=0&src=http%3A%2F%2F4493bz.1985t.com%2Fuploads%2Fallimg%2F141025%2F4-141025144557.jpg");
            mAdapter.addData(friendInfo);
        }
        updateFriendNumber();
        dismmisLoadingDialog();
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
                                mAdapter.getData().get(position).setNoteName(mFirendName.getText().toString());
                                mAdapter.notifyItemChanged(position);
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
                                BluetoothBindingFriendInfo friendInfo = new BluetoothBindingFriendInfo();
                                friendInfo.setNoteName(mFriendPhone.getText().toString());
                                friendInfo.setImgUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1522131551312&di=52422f4384734a296b537d5040c2e89c&imgtype=0&src=http%3A%2F%2F4493bz.1985t.com%2Fuploads%2Fallimg%2F141025%2F4-141025144557.jpg");
                                mAdapter.addData(friendInfo);
                                updateFriendNumber();
                                mFriendPhone.setText("");
                            } else {
                                showFiveToast("你输入的手机不正确");
                            }
                        }
                    })
                    .create();
        }
        mAddFriendPhoneDialog.show();
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
                                    notifyRemoveData(position);
                                    updateFriendNumber();
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
