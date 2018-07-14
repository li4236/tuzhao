package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.User_Info;
import com.tuzhao.publicwidget.others.CircleImageView;
import com.tuzhao.utils.ImageUtil;

import java.util.Objects;
import java.util.Random;

/**
 * Created by juncoder on 2018/7/14.
 */
public class PersonalMessageRefactorActivity extends BaseStatusActivity implements View.OnClickListener {

    private CircleImageView mCircleImageView;

    private TextView mNickname;

    private TextView mUserGender;

    private TextView mBirthday;

    private TextView mNumberOfPark;

    private TextView mUserLevel;

    private TextView mTelephoneNumber;

    private TextView mWechat;

    private TextView mAlipay;

    private TextView mSesame;

    @Override
    protected int resourceId() {
        return R.layout.activity_personal_message_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mCircleImageView = findViewById(R.id.user_protrait);
        mNickname = findViewById(R.id.nickname);
        mUserGender = findViewById(R.id.user_gender);
        mBirthday = findViewById(R.id.birthday);
        mNumberOfPark = findViewById(R.id.number_of_park);
        mUserLevel = findViewById(R.id.user_level);
        mTelephoneNumber = findViewById(R.id.telephone_number_tv);
        mWechat = findViewById(R.id.wechat_bingding_tv);
        mAlipay = findViewById(R.id.alipay_bingding_tv);
        mSesame = findViewById(R.id.sesame_certification_tv);

        mCircleImageView.setOnClickListener(this);
        findViewById(R.id.edit_personal_message).setOnClickListener(this);
        findViewById(R.id.telephone_number_cl).setOnClickListener(this);
        findViewById(R.id.modify_password_cl).setOnClickListener(this);
        findViewById(R.id.wechat_bingding_cl).setOnClickListener(this);
        findViewById(R.id.alipay_bingding_cl).setOnClickListener(this);
        findViewById(R.id.sesame_certification_cl).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        User_Info userInfo = com.tuzhao.publicmanager.UserManager.getInstance().getUserInfo();
        ImageUtil.showPic(mCircleImageView, HttpConstants.ROOT_IMG_URL_USER + userInfo.getImg_url());
        StringBuilder stringBuilder = new StringBuilder();
        if (userInfo.getNickname().equals("-1")) {
            stringBuilder.append("tuzhao");
            Random random = new Random(10);
            for (int i = 0; i < 3; i++) {
                stringBuilder.append(random.nextInt());
            }
        } else {
            stringBuilder.append(userInfo.getNickname());
        }

        if (userInfo.getRealName() == null || Objects.equals(userInfo.getRealName(), "") || Objects.equals(userInfo.getRealName(), "-1")) {
            stringBuilder.append("（未实名）");
        } else {
            stringBuilder.append("（");
            stringBuilder.append(userInfo.getRealName());
            stringBuilder.append("）");
        }
        mNickname.setText(stringBuilder.toString());

        mUserGender.setText(Objects.equals(userInfo.getGender(), "1") ? "男" : "女");
        mBirthday.setText(userInfo.getBirthday());
        mNumberOfPark.setText("停车次数" + userInfo.getNumberOfPark() + "次");

        if (!userInfo.getUsername().equals("-1")) {
            mTelephoneNumber.setText(userInfo.getUsername());
        }

        if (!Objects.equals(userInfo.getWechatName(), "-1")) {
            mWechat.setText(userInfo.getWechatName());
        }

        if (!Objects.equals(userInfo.getAliNickName(), "-1")) {
            mAlipay.setText(userInfo.getAliNickName());
        }

        if (!Objects.equals(userInfo.getSerect_code(), "-1")) {
            mSesame.setText(userInfo.getSerect_code());
        }

    }

    @NonNull
    @Override
    protected String title() {
        return "个人信息";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_protrait:

                break;
            case R.id.edit_personal_message:

                break;
            case R.id.telephone_number_cl:

                break;
            case R.id.modify_password_cl:

                break;
            case R.id.wechat_bingding_cl:

                break;
            case R.id.alipay_bingding_cl:

                break;
            case R.id.sesame_certification_cl:

                break;
        }
    }

}
