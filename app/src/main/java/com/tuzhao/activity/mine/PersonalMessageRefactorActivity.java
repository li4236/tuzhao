package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.User_Info;
import com.tuzhao.publicwidget.others.CircleImageView;
import com.tuzhao.utils.ImageUtil;

import java.util.Objects;

/**
 * Created by juncoder on 2018/7/14.
 */
public class PersonalMessageRefactorActivity extends BaseStatusActivity implements View.OnClickListener {

    private CircleImageView mCircleImageView;

    private TextView mNickname;

    private TextView mUserGender;

    private ImageView mUserGenderIv;

    private TextView mBirthday;

    private TextView mNumberOfPark;

    private TextView mUserLevel;

    private TextView mTelephoneNumber;

    private TextView mWechat;

    private TextView mAlipay;

    private TextView mSesame;

    private TextView mRealName;

    @Override
    protected int resourceId() {
        return R.layout.activity_personal_message_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mCircleImageView = findViewById(R.id.user_protrait);
        mNickname = findViewById(R.id.nickname);
        mUserGender = findViewById(R.id.user_gender);
        mUserGenderIv = findViewById(R.id.user_gender_iv);
        mBirthday = findViewById(R.id.birthday);
        mNumberOfPark = findViewById(R.id.number_of_park);
        mUserLevel = findViewById(R.id.user_level);
        mTelephoneNumber = findViewById(R.id.telephone_number_tv);
        mWechat = findViewById(R.id.wechat_bingding_tv);
        mAlipay = findViewById(R.id.alipay_bingding_tv);
        mSesame = findViewById(R.id.sesame_certification_tv);
        mRealName = findViewById(R.id.real_name_certification_tv);

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
        String nickname;
        if (userInfo.getNickname().equals("-1")) {
            nickname = "昵称（未设置）";
        } else {
            nickname = com.tuzhao.publicmanager.UserManager.getInstance().getUserInfo().getNickname();
        }
        mNickname.setText(nickname);

        if (Objects.equals(userInfo.getGender(), "1")) {
            mUserGender.setText("男");
            ImageUtil.showPic(mUserGenderIv, R.drawable.ic_man);
        } else {
            mUserGender.setText("女");
            ImageUtil.showPic(mUserGenderIv, R.drawable.ic_woman);
        }
        mBirthday.setText(userInfo.getBirthday());
        if (userInfo.getNumberOfPark() != null && !userInfo.getNumberOfPark().equals("-1")) {
            int numberOfPark = Integer.parseInt(userInfo.getNumberOfPark());
            if (0 < numberOfPark && numberOfPark <= 20) {
                mUserLevel.setText("停车小白");
            } else if (numberOfPark <= 50) {
                mUserLevel.setText("泊车达人");
            } else if (numberOfPark <= 100) {
                mUserLevel.setText("资深途友");
            } else {
                mUserLevel.setText("神级泊客");
            }
            mNumberOfPark.setText("总停车次数" + numberOfPark + "次");
        }

        if (!userInfo.getUsername().equals("-1")) {
            StringBuilder telephone = new StringBuilder(userInfo.getUsername());
            if (telephone.length() > 6) {
                telephone.replace(3, 8, "*****");
            }
            mTelephoneNumber.setText(telephone.toString());
        }

        if (!Objects.equals(userInfo.getWechatName(), "-1")) {
            mWechat.setText(userInfo.getWechatName());
        }

        if (!Objects.equals(userInfo.getAliNickName(), "-1")) {
            mAlipay.setText(userInfo.getAliNickName());
        }

        if (!Objects.equals(userInfo.getSesameFraction(), "-1")) {
            mSesame.setText(userInfo.getSesameFraction());
        }

        if (userInfo.getRealName() != null && !userInfo.getRealName().equals("-1")) {
            mRealName.setText(userInfo.getRealName());
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
