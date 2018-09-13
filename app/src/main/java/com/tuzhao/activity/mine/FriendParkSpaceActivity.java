package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.utils.ImageUtil;

/**
 * Created by juncoder on 2018/8/28.
 * <p>好友车位</p>
 */
public class FriendParkSpaceActivity extends BaseStatusActivity implements View.OnClickListener {

    @Override
    protected int resourceId() {
        return R.layout.activity_friend_park_space_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ImageUtil.showPic((ImageView) findViewById(R.id.friend_park_space_iv), R.drawable.ic_sharebanner);
        ImageUtil.showPic((ImageView) findViewById(R.id.share_list_iv), R.drawable.ic_line3);
        ImageUtil.showPic((ImageView) findViewById(R.id.appointment_park_space_iv), R.drawable.ic_pick);
        ImageUtil.showPic((ImageView) findViewById(R.id.appointment_record_iv), R.drawable.ic_reserve2);

        findViewById(R.id.share_list).setOnClickListener(this);
        findViewById(R.id.appointment_park_space).setOnClickListener(this);
        findViewById(R.id.appointment_record).setOnClickListener(this);
    }

    @Override
    protected void initData() {
    }

    @NonNull
    @Override
    protected String title() {
        return "好友车位";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_list:
                startActivity(ShareParkSpaceActivity.class);
                break;
            case R.id.appointment_park_space:
                startActivity(AppointmentParkSpaceActivity.class);
                break;
            case R.id.appointment_record:
                startActivity(UseFriendParkSpaceRecordActivity.class);
                break;
        }
    }

}
