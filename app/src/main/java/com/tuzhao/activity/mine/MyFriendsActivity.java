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
import com.tuzhao.info.FriendInfo;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.UserManager;
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

public class MyFriendsActivity extends BaseStatusActivity {

    private BluetoothBindingAdapter mAdapter;

    private TextView mBindindFriendNumber;

    private EditText mFirendName;

    private EditText mFriendPhone;

    private TipeDialog mModifyNameDialog;

    private TipeDialog mAddFriendPhoneDialog;

    private Park_Info mPark_info;

    @Override
    protected void initView(Bundle savedInstanceState) {
        if ((mPark_info = (Park_Info) getIntent().getSerializableExtra(ConstansUtil.PARK_SPACE_INFO)) == null) {
            showFiveToast("打开失败，请退出重试");
            finish();
        }

        RecyclerView recyclerView = findViewById(R.id.base_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SkipTopBottomDivider(this, false, true));
        mAdapter = new BluetoothBindingAdapter();
        recyclerView.setAdapter(mAdapter);

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
        getOkGo(HttpConstants.getBindingFriends)
                .params("parkSpaceId", mPark_info.getId())
                .params("cityCode", mPark_info.getCitycode())
                .execute(new JsonCallback<Base_Class_List_Info<FriendInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<FriendInfo> o, Call call, Response response) {
                        mAdapter.setNewData(o.data);
                        updateFriendNumber();
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                case "102":
                                    showFiveToast("获取亲友列表失败，请退出重试");
                                    break;
                                case "103":
                                    showFiveToast("数据异常，请退出重试");
                                    break;
                                case "104":
                                    showFiveToast("你还没有添加亲友哦");
                                    break;
                            }
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
        return "我的亲友";
    }

    /**
     * 更新显示当前亲友绑定的数量
     */
    private void updateFriendNumber() {
        String friendNumber = "亲友(" + mAdapter.getData().size() + "/5)";
        mBindindFriendNumber.setText(friendNumber);
    }

    /**
     * 显示修改亲友备注的对话框
     */
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
                                modifyFriendName(mAdapter.getData().get(position).getFriendId(), mFirendName.getText().toString(), position);
                            }

                        }
                    })
                    .create();
        }
        mModifyNameDialog.show();
        mFirendName.setText(mAdapter.getData().get(position).getNoteName());
        mFirendName.setSelection(mFirendName.getText().toString().length());
    }

    /**
     * 显示添加亲友手机号的对话框
     */
    private void showPhoneDialog() {
        if (mAddFriendPhoneDialog == null) {

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
                            String phone = mFriendPhone.getText().toString().trim();
                            if (TextUtils.isEmpty(phone)) {
                                showFiveToast("请输入手机号");
                            } else if (TextUtils.equals(phone, UserManager.getInstance().getUserInfo().getUsername())) {
                                showFiveToast("不能添加自己哦");
                            } else if (DateUtil.isPhoneNumble(mFriendPhone.getText().toString().trim())) {
                                boolean isRepeat = false;
                                for (FriendInfo friendInfo : mAdapter.getData()) {
                                    if (friendInfo.getTelephone().equals(phone)) {
                                        isRepeat = true;
                                        break;
                                    }
                                }
                                if (isRepeat) {
                                    showFiveToast("该亲友已经添加过了哦");
                                } else {
                                    addFriend(phone);
                                }
                            } else {
                                showFiveToast("你输入的手机不正确哦");
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
        getOkGo(HttpConstants.modifyFriendNickname)
                .params("parkSpaceId", mPark_info.getId())
                .params("cityCode", mPark_info.getCitycode())
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
        getOkGo(HttpConstants.deleteFriend)
                .params("friendDeviceId", mAdapter.getData().get(position).getFriendId())
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

    private void addFriend(String telephone) {
        showLoadingDialog("正在添加");
        getOkGo(HttpConstants.addFriend)
                .params("parkSpaceId", mPark_info.getId())
                .params("cityCode", mPark_info.getCitycode())
                .params("telephone", telephone)
                .execute(new JsonCallback<Base_Class_Info<FriendInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<FriendInfo> o, Call call, Response response) {
                        mAdapter.addData(o.data);
                        updateFriendNumber();
                        mFriendPhone.setText("");
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                case "102":
                                case "103":
                                    showFiveToast("账号异常，请重新登录");
                                    startLogin();
                                    break;
                                case "104":
                                    showFiveToast("最多只能有5个共享亲友哦");
                                    break;
                                case "105":
                                    showFiveToast("你已经添加过该亲友了哦");
                                    break;
                                case "106":
                                    showFiveToast("服务器异常，请稍后重试");
                                    break;
                                case "107":
                                    showFiveToast("手机号不正确，请重新输入");
                                    break;
                                case "108":
                                    showFiveToast("不能添加自己哦");
                                    break;
                            }
                        }
                    }
                });

    }

    private class BluetoothBindingAdapter extends BaseAdapter<FriendInfo> {

        @Override
        protected void conver(@NonNull BaseViewHolder holder, final FriendInfo friendInfo, final int position) {
            holder.showCircleUserPic(R.id.bluetooth_binding_friend_iv, friendInfo.getImgUrl())
                    .setText(R.id.bluetooth_binding_friend_name, friendInfo.getNoteName())
                    .getView(R.id.bluetooth_binding_edit_friend).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNameDialog(position);
                }
            });

            holder.getView(R.id.bluetooth_binding_delete_friend).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = "确认删除" + friendInfo.getNoteName() + "的手机?";
                    new TipeDialog.Builder(MyFriendsActivity.this)
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
            return R.layout.item_my_friends_layout;
        }
    }

}
