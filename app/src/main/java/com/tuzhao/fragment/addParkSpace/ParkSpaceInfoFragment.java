package com.tuzhao.fragment.addParkSpace;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lwkandroid.imagepicker.ImagePicker;
import com.lwkandroid.imagepicker.data.ImageBean;
import com.lwkandroid.imagepicker.data.ImagePickType;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.bean.ParkBean;
import com.tuzhao.R;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.activity.mine.SelectParkSpaceActivity;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.io.File;
import java.util.List;

import static android.app.Activity.RESULT_OK;

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

    private ImageView mIdCardPositivePhoto;

    private TextView mIdCardPositivePhotoTv;

    private ImageView mIdCardNegativePhoto;

    private TextView mIdCardNegativePhotoTv;

    private ConstraintLayout mPropertyPhotoCl;

    private ImageView mPropertyFirstPhoto;

    private ImageView mPropertySecondPhoto;

    private ImageView mPropertyThirdPhoto;

    private ConstraintLayout mTakePropertyPhotoCl;

    private String mIdCardPositivePath = "-1";

    private String mIdCardNegativePath = "-1";

    private String mPropertyFirstPath = "-1";

    private String mPropertySecondPath = "-1";

    private String mPropertyThirdPath = "-1";

    private ImagePicker mImagePicker;

    private int mChoosePosition;

    private CustomDialog mCustomDialog;

    private int mSixtyDp;

    private int mEightyDp;

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
        mIdCardPositivePhoto = view.findViewById(R.id.id_card_positive_photo_iv);
        mIdCardPositivePhotoTv = view.findViewById(R.id.id_card_positive_photo_tv);
        mIdCardNegativePhoto = view.findViewById(R.id.id_card_negative_photo_iv);
        mIdCardNegativePhotoTv = view.findViewById(R.id.id_card_negative_photo_tv);
        mPropertyPhotoCl = view.findViewById(R.id.property_photos_cl);
        mPropertyFirstPhoto = view.findViewById(R.id.property_first_photo_iv);
        mPropertySecondPhoto = view.findViewById(R.id.property_second_photo_iv);
        mPropertyThirdPhoto = view.findViewById(R.id.property_third_photo_iv);
        mTakePropertyPhotoCl = view.findViewById(R.id.take_property_photo_cl);

        view.findViewById(R.id.parking_lot_name_cl).setOnClickListener(this);
        mIdCardPositivePhotoTv.setOnClickListener(this);
        mIdCardPositivePhoto.setOnClickListener(this);
        mIdCardNegativePhotoTv.setOnClickListener(this);
        mIdCardNegativePhoto.setOnClickListener(this);
        mTakePropertyPhotoCl.setOnClickListener(this);
        mPropertyFirstPhoto.setOnClickListener(this);
        mPropertySecondPhoto.setOnClickListener(this);
        mPropertyThirdPhoto.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        String hint = "温馨提醒：\n" +
                "1、请填写正确的车位信息\n" +
                "2、审核期间会有专人电访，请保持电话畅通\n" +
                "3、车位锁将免费提供，用户需缴纳押金，押金退锁即退";
        mParkSpaceHint.setText(hint);

        mSixtyDp = DensityUtil.dp2px(requireContext(), 16);
        mEightyDp = DensityUtil.dp2px(requireContext(), 18);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mParkLotName.setText(bundle.getString(ConstansUtil.PARK_LOT_NAME, ""));
            mRevenueRatio.setText(bundle.getString(ConstansUtil.REVENUE_RATIO, ""));
            mParkSpaceNumber.setText(bundle.getString(ConstansUtil.PARK_SPACE_NUMBER, ""));
            mParkSpaceDescription.setText(bundle.getString(ConstansUtil.PARK_SPACE_DESCRIPTION, ""));
            mIdCardPositivePath = bundle.getString(ConstansUtil.ID_CARD_POSITIVE_PHOTO, "-1");
            mIdCardNegativePath = bundle.getString(ConstansUtil.ID_CARD_NEGATIVE_PHOTO, "-1");
            mPropertyFirstPath = bundle.getString(ConstansUtil.PROPERTY_FIRST_PHOTO, "-1");
            mPropertySecondPath = bundle.getString(ConstansUtil.PROPERTY_SECOND_PHOTO, "-1");
            mPropertyThirdPath = bundle.getString(ConstansUtil.PROPERTY_THIRD_PHOTO, "-1");

            mRevenueRatio.setVisibility(View.VISIBLE);
            showIdCardPositivePhoto();
            showIdCardNegativePhoto();
            showPropertyFirstPhoto();
            showPropertySecondPhoto();
            showPropertyFirstPhoto();
        } else {
            ImageUtil.showPic(mIdCardPositivePhoto, R.drawable.ic_idcard);
            ImageUtil.showPic(mIdCardNegativePhoto, R.drawable.ic_idcard2);
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
            bundle.putString(ConstansUtil.PROPERTY_FIRST_PHOTO, mPropertyFirstPath);
            bundle.putString(ConstansUtil.PROPERTY_SECOND_PHOTO, mPropertySecondPath);
            bundle.putString(ConstansUtil.PROPERTY_THIRD_PHOTO, mPropertyThirdPath);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parking_lot_name_cl:
                Intent intent = new Intent(getActivity(), SelectParkSpaceActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.id_card_positive_photo_tv:
            case R.id.id_card_positive_photo_iv:
                mChoosePosition = 0;
                if (mIdCardPositivePath.equals("-1")) {
                    startTakePhoto();
                } else {
                    showDialog();
                }
                break;
            case R.id.id_card_negative_photo_tv:
            case R.id.id_card_negative_photo_iv:
                mChoosePosition = 1;
                if (mIdCardNegativePath.equals("-1")) {
                    startTakePhoto();
                } else {
                    showDialog();
                }
                break;
            case R.id.take_property_photo_cl:
            case R.id.property_first_photo_iv:
                mChoosePosition = 2;
                if (mPropertyFirstPath.equals("-1")) {
                    startTakePhoto();
                } else {
                    showDialog();
                }
                break;
            case R.id.property_second_photo_iv:
                if (!mPropertyFirstPath.equals("-1")) {
                    mChoosePosition = 3;
                    if (mPropertySecondPath.equals("-1")) {
                        startTakePhoto();
                    } else {
                        showDialog();
                    }
                }
                break;
            case R.id.property_third_photo_iv:
                if (!mPropertySecondPath.equals("-1")) {
                    mChoosePosition = 4;
                    if (mPropertyThirdPath.equals("-1")) {
                        startTakePhoto();
                    } else {
                        showDialog();
                    }
                }
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
        } else if (requestCode == ConstansUtil.PICTURE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            final List<ImageBean> imageBeans = data.getParcelableArrayListExtra(ImagePicker.INTENT_RESULT_DATA);
            ImageUtil.compressPhoto(requireContext(), imageBeans.get(0).getImagePath(), new SuccessCallback<File>() {
                @Override
                public void onSuccess(File file) {
                    showPhoto(file);
                }
            });
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

    private void startTakePhoto() {
        if (mImagePicker == null) {
            mImagePicker = new ImagePicker()
                    .cachePath(Environment.getExternalStorageDirectory().getAbsolutePath())
                    .needCamera(true) //是否需要在界面中显示相机入口(类似微信那样)
                    .pickType(ImagePickType.SINGLE); //设置选取类型(单选SINGLE、多选MUTIL、拍照ONLY_CAMERA)
        }
        mImagePicker.start(this, ConstansUtil.PICTURE_REQUEST_CODE);
    }

    private void showPhoto(File file) {
        switch (mChoosePosition) {
            case 0:
                mIdCardPositivePath = file.getAbsolutePath();
                showIdCardPositivePhoto();
                break;
            case 1:
                mIdCardNegativePath = file.getAbsolutePath();
                showIdCardNegativePhoto();
                break;
            case 2:
                mPropertyFirstPath = file.getAbsolutePath();
                showPropertyFirstPhoto();
                break;
            case 3:
                mPropertySecondPath = file.getAbsolutePath();
                showPropertySecondPhoto();
                break;
            case 4:
                mPropertyThirdPath = file.getAbsolutePath();
                showPropertyThirdPhoto();
                break;
        }
    }

    private void showIdCardPositivePhoto() {
        ImageUtil.showPic(mIdCardPositivePhoto, mIdCardPositivePath);
        if (mIdCardPositivePhotoTv.getVisibility() != View.GONE) {
            mIdCardPositivePhotoTv.setVisibility(View.GONE);
        }
    }

    private void showIdCardNegativePhoto() {
        ImageUtil.showPic(mIdCardNegativePhoto, mIdCardNegativePath);
        if (mIdCardNegativePhotoTv.getVisibility() != View.GONE) {
            mIdCardNegativePhotoTv.setVisibility(View.GONE);
        }
    }

    private void showPropertyFirstPhoto() {
        if (mTakePropertyPhotoCl.getVisibility() == View.VISIBLE) {
            mTakePropertyPhotoCl.setVisibility(View.GONE);
            mPropertyPhotoCl.setVisibility(View.VISIBLE);
        }

        ImageUtil.showPic(mPropertyFirstPhoto, mPropertyFirstPath);

        if (mPropertySecondPath.equals("-1")) {
            if (mPropertySecondPhoto.getVisibility() != View.VISIBLE) {
                mPropertySecondPhoto.setVisibility(View.VISIBLE);
            }
            mPropertySecondPhoto.setPadding(mSixtyDp, mEightyDp, mSixtyDp, mEightyDp);
            mPropertySecondPhoto.setBackgroundResource(R.drawable.y3_all_1dp);
            ImageUtil.showPic(mPropertySecondPhoto, R.drawable.ic_photo);
        }
    }

    private void showPropertySecondPhoto() {
        mPropertySecondPhoto.setPadding(0, 0, 0, 0);
        mPropertySecondPhoto.setBackgroundResource(0);
        ImageUtil.showPic(mPropertySecondPhoto, mPropertySecondPath);
        if (mPropertyThirdPath.equals("-1")) {
            if (mPropertyThirdPhoto.getVisibility() != View.VISIBLE) {
                mPropertyThirdPhoto.setVisibility(View.VISIBLE);
            }
            setPropertyPhotoNormal(mPropertyThirdPhoto);
        }
    }

    private void showPropertyThirdPhoto() {
        mPropertyThirdPhoto.setPadding(0, 0, 0, 0);
        mPropertyThirdPhoto.setBackgroundResource(0);
        ImageUtil.showPic(mPropertyThirdPhoto, mPropertyThirdPath);
    }

    private void setPropertyPhotoNormal(ImageView imageView) {
        imageView.setPadding(mSixtyDp, mEightyDp, mSixtyDp, mEightyDp);
        imageView.setBackgroundResource(R.drawable.y3_all_1dp);
        ImageUtil.showPic(imageView, R.drawable.ic_photo);
    }

    private void showDialog() {
        if (mCustomDialog == null) {
            View view = getLayoutInflater().inflate(R.layout.dialog_selete_photo_layout, null);
            mCustomDialog = new CustomDialog(requireContext(), view, true);
            view.findViewById(R.id.exchang_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTakePhoto();
                    mCustomDialog.dismiss();
                }
            });

            view.findViewById(R.id.delete_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletePhoto();
                    mCustomDialog.dismiss();
                }
            });

            view.findViewById(R.id.cancel_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCustomDialog.dismiss();
                }
            });
        }

        mCustomDialog.show();
    }

    private void deletePhoto() {
        switch (mChoosePosition) {
            case 0:
                mIdCardPositivePath = "-1";
                ImageUtil.showPic(mIdCardPositivePhoto, R.drawable.ic_idcard);
                mIdCardPositivePhotoTv.setVisibility(View.VISIBLE);
                break;
            case 1:
                mIdCardNegativePath = "-1";
                ImageUtil.showPic(mIdCardNegativePhoto, R.drawable.ic_idcard2);
                mIdCardNegativePhotoTv.setVisibility(View.VISIBLE);
                break;
            case 2:
                if (mPropertySecondPath.equals("-1") && mPropertyThirdPath.equals("-1")) {
                    mTakePropertyPhotoCl.setVisibility(View.VISIBLE);
                    mPropertyPhotoCl.setVisibility(View.GONE);
                    mPropertyFirstPath = "-1";
                } else if (!mPropertySecondPath.equals("-1") && !mPropertyThirdPath.equals("-1")) {
                    mPropertyFirstPath = mPropertySecondPath;
                    mPropertySecondPath = mPropertyThirdPath;
                    mPropertyThirdPath = "-1";
                    showPropertyFirstPhoto();
                    showPropertySecondPhoto();
                } else if (!mPropertySecondPath.equals("-1")) {
                    mPropertyFirstPath = mPropertySecondPath;
                    mPropertySecondPath = "-1";
                    showPropertyFirstPhoto();
                    setPropertyPhotoNormal(mPropertySecondPhoto);
                    mPropertyThirdPhoto.setVisibility(View.INVISIBLE);
                }
                break;
            case 3:
                if (mPropertyThirdPath.equals("-1")) {
                    mPropertySecondPath = "-1";
                    setPropertyPhotoNormal(mPropertySecondPhoto);
                    mPropertyThirdPhoto.setVisibility(View.INVISIBLE);
                } else {
                    mPropertySecondPath = mPropertyThirdPath;
                    mPropertyThirdPath = "-1";
                    showPropertySecondPhoto();
                }
                break;
            case 4:
                mPropertyThirdPath = "-1";
                setPropertyPhotoNormal(mPropertyThirdPhoto);
                break;
        }
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
        } else if (mIdCardPositivePath.equals("-1")) {
            showFiveToast("请上传身份证正面照");
        } else if (mIdCardNegativePath.equals("-1")) {
            showFiveToast("请上传身份证反面照");
        } else if (mPropertyFirstPath.equals("-1")) {
            showFiveToast("请上传车位产权照");
        } else {
            Intent intent = new Intent();
            intent.setAction(ConstansUtil.JUMP_TO_TIME_SETTING);
            IntentObserable.dispatch(intent);
        }
    }

}
