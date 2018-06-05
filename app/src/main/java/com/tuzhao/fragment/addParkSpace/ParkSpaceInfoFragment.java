package com.tuzhao.fragment.addParkSpace;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.SuspensionIndexBar.bean.ParkBean;
import com.tuzhao.R;
import com.tuzhao.activity.mine.SelectParkSpaceActivity;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

/**
 * Created by juncoder on 2018/6/4.
 */
public class ParkSpaceInfoFragment extends BaseStatusFragment implements View.OnClickListener, IntentObserver {

    private TextView mParkLotName;

    private TextView mParkSpaceHint;

    private TextView mRevenueRatioTv;

    private TextView mRevenueRatio;

    private EditText mParkSpaceNumber;

    private EditText mParkSpaceDescription;

    private TextView mIdCardTv;

    private ImageView mIdCardPositivePhoto;

    private TextView mIdCardPositivePhotoTv;

    private ImageView mIdCardNegativePhoto;

    private TextView mIdCardNegativePhotoTv;

    private TextView mPropertyPhotoTv;

    private ImageView mPropertyPositivePhoto;

    private TextView mPropertyPositivePhotoTv;

    private ImageView mPropertyNegativePhoto;

    private TextView mPropertyNeagtivePhotoTv;

    private String mIdCardPositivePath = "";

    private String mIdCardNegativePath = "";

    private String mPropertyPositivePath = "";

    private String mPropertyNegativePath = "";

    @Override
    protected int resourceId() {
        return R.layout.fragment_park_space_info_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mParkLotName = view.findViewById(R.id.car_number);
        mRevenueRatioTv = view.findViewById(R.id.revenue_ratio_tv);
        mRevenueRatio = view.findViewById(R.id.revenue_ratio);
        mParkSpaceNumber = view.findViewById(R.id.park_space_number);
        mParkSpaceHint = view.findViewById(R.id.park_space_hint);
        mParkSpaceDescription = view.findViewById(R.id.park_space_description);
        mIdCardTv = view.findViewById(R.id.id_card);
        mIdCardPositivePhoto = view.findViewById(R.id.id_card_positive_photo_iv);
        mIdCardPositivePhotoTv = view.findViewById(R.id.id_card_positive_photo_tv);
        mIdCardNegativePhoto = view.findViewById(R.id.id_card_negative_photo_iv);
        mIdCardNegativePhotoTv = view.findViewById(R.id.id_card_negative_photo_tv);
        mPropertyPhotoTv = view.findViewById(R.id.property_photo);
        mPropertyPositivePhoto = view.findViewById(R.id.property_positive_photo_iv);
        mPropertyPositivePhotoTv = view.findViewById(R.id.property_positive_photo_tv);
        mPropertyNegativePhoto = view.findViewById(R.id.property_negative_photo_iv);
        mPropertyNeagtivePhotoTv = view.findViewById(R.id.property_negative_photo_tv);

        view.findViewById(R.id.parking_lot_name_cl).setOnClickListener(this);
        view.findViewById(R.id.idCard_cl).setOnClickListener(this);
        view.findViewById(R.id.property_photo_cl).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        String hint = "温馨提醒：\n" +
                "1、请填写正确的车位信息\n" +
                "2、审核期间会有专人电访，请保持电话畅通\n" +
                "3、车位锁将免费提供，用户需缴纳押金，押金退锁即退";
        mParkSpaceHint.setText(hint);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mParkLotName.setText(bundle.getString(ConstansUtil.PARK_LOT_NAME, ""));
            mRevenueRatio.setText(bundle.getString(ConstansUtil.REVENUE_RATIO, ""));
            mParkSpaceNumber.setText(bundle.getString(ConstansUtil.PARK_SPACE_NUMBER, ""));
            mParkSpaceDescription.setText(bundle.getString(ConstansUtil.PARK_SPACE_DESCRIPTION, ""));
            mIdCardPositivePath = bundle.getString(ConstansUtil.ID_CARD_POSITIVE_PHOTO, "");
            mIdCardNegativePath = bundle.getString(ConstansUtil.ID_CARD_NEGATIVE_PHOTO, "");
            mPropertyPositivePath = bundle.getString(ConstansUtil.PROPERTY_POSITIVE_PHOTO, "");
            mPropertyNegativePath = bundle.getString(ConstansUtil.PROPERTY_NEGATIVE_PHOTO, "");

            mRevenueRatio.setVisibility(View.VISIBLE);
            showIdCardPhoto();
            showPropertyPhoto();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Bundle bundle = getArguments();
        if (bundle != null) {
            bundle.putString(ConstansUtil.PARK_LOT_NAME, getText(mParkLotName));
            bundle.putString(ConstansUtil.REVENUE_RATIO, getText(mRevenueRatio));
            bundle.putString(ConstansUtil.PARK_SPACE_NUMBER, getText(mParkSpaceNumber));
            bundle.putString(ConstansUtil.PARK_SPACE_DESCRIPTION, getText(mParkSpaceDescription));
            bundle.putString(ConstansUtil.ID_CARD_POSITIVE_PHOTO, mIdCardPositivePath);
            bundle.putString(ConstansUtil.ID_CARD_NEGATIVE_PHOTO, mIdCardNegativePath);
            bundle.putString(ConstansUtil.PROPERTY_POSITIVE_PHOTO, mPropertyPositivePath);
            bundle.putString(ConstansUtil.PROPERTY_NEGATIVE_PHOTO, mPropertyNegativePath);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parking_lot_name_cl:
                Intent intent = new Intent(getActivity(), SelectParkSpaceActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.idCard_cl:

                break;
            case R.id.property_photo_cl:

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1 && data.hasExtra("park")) {
            ParkBean mPark = (ParkBean) data.getSerializableExtra("park");
            mParkLotName.setText(mPark.getparkStation());
            String[] ccc = mPark.getProfit_ratio().split(":");
            mRevenueRatio.setText(ccc[0] + " : " + ccc[1] + " : " + ccc[2] + " （车位主 : 物业 : 平台）");
            mRevenueRatioTv.setVisibility(View.VISIBLE);
            mRevenueRatio.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ConstansUtil.NEXT_STEP:
                    if (intent.getIntExtra(ConstansUtil.POSITION, -1) == 0) {
                        verifyInfo();
                    }
                    break;
            }
        }
    }

    private void showIdCardPhoto() {
        ImageUtil.showPic(mIdCardPositivePhoto, mIdCardPositivePath);
        ImageUtil.showPic(mIdCardNegativePhoto, mIdCardNegativePath);
        mIdCardTv.setVisibility(View.INVISIBLE);
        mIdCardPositivePhoto.setVisibility(View.VISIBLE);
        mIdCardPositivePhotoTv.setVisibility(View.VISIBLE);
        mIdCardNegativePhoto.setVisibility(View.VISIBLE);
        mIdCardNegativePhotoTv.setVisibility(View.VISIBLE);
    }

    private void showPropertyPhoto() {
        ImageUtil.showPic(mPropertyPositivePhoto, mPropertyPositivePath);
        ImageUtil.showPic(mPropertyNegativePhoto, mPropertyNegativePath);
        mPropertyPhotoTv.setVisibility(View.INVISIBLE);
        mPropertyPositivePhoto.setVisibility(View.VISIBLE);
        mPropertyPositivePhotoTv.setVisibility(View.VISIBLE);
        mPropertyNegativePhoto.setVisibility(View.VISIBLE);
        mPropertyNeagtivePhotoTv.setVisibility(View.VISIBLE);
    }

    /**
     * 验证车位信息是否都全部输入了
     */
    public void verifyInfo() {
        if (TextUtils.isEmpty(getText(mParkLotName))) {
            showFiveToast("请选择车场");
        } else if (TextUtils.isEmpty(getText(mParkSpaceNumber).trim())) {
            showFiveToast("请输入车位号码");
        } else if (TextUtils.isEmpty(getText(mParkSpaceDescription))) {
            showFiveToast("请输入车位描述");
        } else if (mIdCardPositivePath.equals("")) {
            showFiveToast("请上传身份证正面照");
        } else if (mIdCardNegativePath.equals("")) {
            showFiveToast("请上传身份证反面照");
        } else if (mPropertyPositivePath.equals("")) {
            showFiveToast("请上传车位产权正面照");
        } else if (mPropertyNegativePath.equals("")) {
            showFiveToast("请上传车位产权反面照");
        } else {
            Intent intent = new Intent();
            intent.setAction(ConstansUtil.JUMP_TO_TIME_SETTING);
            IntentObserable.dispatch(intent);
        }
    }

}
