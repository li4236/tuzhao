package com.tuzhao.fragment.addParkSpace;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lwkandroid.imagepicker.ImagePicker;
import com.lwkandroid.imagepicker.data.ImageBean;
import com.lwkandroid.imagepicker.data.ImagePickType;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.bean.ParkBean;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.activity.mine.SelectParkSpaceActivity;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkSpaceInfo;
import com.tuzhao.info.PropertyPhoto;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.io.File;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by juncoder on 2018/6/4.
 */
public class ParkSpaceInfoFragment extends BaseStatusFragment implements View.OnClickListener, IntentObserver {

    private TextView mParkLotName;

    private TextView mParkSpaceHint;

    private TextView mRevenueRatioTv;

    private TextView mRevenueRatio;

    private EditText mRealName;

    private EditText mParkSpaceDescription;

    private ImageView mIdCardPositivePhoto;

    private TextView mIdCardPositiveUploadTv;

    private TextView mIdCardPositivePhotoTv;

    private ImageView mIdCardNegativePhoto;

    private TextView mIdCardNegativeUploadTv;

    private TextView mIdCardNegativePhotoTv;

    private ConstraintLayout mPropertyPhotoCl;

    private ConstraintLayout mTakePropertyPhotoCl;

    private PropertyAdapter mPropertyAdapter;

    private ImagePicker mImagePicker;

    private ImagePicker mPropertyImagePicker;

    private CustomDialog mCustomDialog;

    private ParkSpaceInfo mParkSpaceInfo;

    private int mChoosePosition;

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
        mRealName = view.findViewById(R.id.real_name_et);
        mParkSpaceHint = view.findViewById(R.id.park_space_hint);
        mParkSpaceDescription = view.findViewById(R.id.park_space_description);
        mIdCardPositivePhoto = view.findViewById(R.id.id_card_positive_photo_iv);
        mIdCardPositiveUploadTv = view.findViewById(R.id.id_card_positive_upload_tv);
        mIdCardPositivePhotoTv = view.findViewById(R.id.id_card_positive_photo_tv);
        mIdCardNegativePhoto = view.findViewById(R.id.id_card_negative_photo_iv);
        mIdCardNegativeUploadTv = view.findViewById(R.id.id_card_negative_upload_tv);
        mIdCardNegativePhotoTv = view.findViewById(R.id.id_card_negative_photo_tv);
        mPropertyPhotoCl = view.findViewById(R.id.property_photos_cl);
        mTakePropertyPhotoCl = view.findViewById(R.id.take_property_photo_cl);

        RecyclerView recyclerView = view.findViewById(R.id.property_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mPropertyAdapter = new PropertyAdapter();
        recyclerView.setAdapter(mPropertyAdapter);
        mPropertyAdapter.addData(new PropertyPhoto());

        view.findViewById(R.id.parking_lot_name_cl).setOnClickListener(this);
        mIdCardPositivePhotoTv.setOnClickListener(this);
        mIdCardPositiveUploadTv.setOnClickListener(this);
        mIdCardPositivePhoto.setOnClickListener(this);
        mIdCardNegativePhotoTv.setOnClickListener(this);
        mIdCardNegativeUploadTv.setOnClickListener(this);
        mIdCardNegativePhoto.setOnClickListener(this);
        mTakePropertyPhotoCl.setOnClickListener(this);
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
            mParkSpaceInfo = getArguments().getParcelable(ConstansUtil.PARK_SPACE_INFO);
            if (mParkSpaceInfo == null) {
                mParkSpaceInfo = new ParkSpaceInfo();
            }

            mParkLotName.setText(mParkSpaceInfo.getParkLotName());
            mRevenueRatio.setText(mParkSpaceInfo.getRevenueRatio());
            mRealName.setText(mParkSpaceInfo.getRealName());
            mParkSpaceDescription.setText(mParkSpaceInfo.getParkSpaceDescription());

            mRevenueRatioTv.setVisibility(View.VISIBLE);
            mRevenueRatio.setVisibility(View.VISIBLE);

            if (!mParkSpaceInfo.getIdCardPositiveUrl().equals("-1")) {
                showPhoto(mParkSpaceInfo.getIdCardPositiveUrl(), 0);
            }

            if (!mParkSpaceInfo.getIdCardNegativeUrl().equals("-1")) {
                showPhoto(mParkSpaceInfo.getIdCardNegativeUrl(), 1);
            }

            if (!mParkSpaceInfo.getPropertyFirstUrl().equals("-1")) {
                showPhoto(mParkSpaceInfo.getPropertyFirstUrl(), 2);
            }

            if (!mParkSpaceInfo.getPropertySecondUrl().equals("-1")) {
                showPhoto(mParkSpaceInfo.getPropertySecondUrl(), 3);
            }

            if (!mParkSpaceInfo.getPropertyThirdUrl().equals("-1")) {
                showPhoto(mParkSpaceInfo.getPropertyThirdUrl(), 4);
            }

        } else {
            setArguments(new Bundle());
            mParkSpaceInfo = new ParkSpaceInfo();

            ImageUtil.showPic(mIdCardPositivePhoto, R.drawable.ic_idcard);
            ImageUtil.showPic(mIdCardNegativePhoto, R.drawable.ic_idcard2);
        }

        IntentObserable.registerObserver(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Bundle bundle = getArguments();
        if (bundle != null) {
            mParkSpaceInfo.setPropertyFirstUrl(mPropertyAdapter.get(0).getPath());
            if (mPropertyAdapter.getDataSize() == 2) {
                mParkSpaceInfo.setPropertySecondUrl(mPropertyAdapter.get(1).getPath());
            } else if (mPropertyAdapter.getDataSize() == 3) {
                mParkSpaceInfo.setPropertySecondUrl(mPropertyAdapter.get(1).getPath());
                mParkSpaceInfo.setPropertyThirdUrl(mPropertyAdapter.get(2).getPath());
            }
            bundle.putParcelable(ConstansUtil.PARK_SPACE_INFO, mParkSpaceInfo);
        }

        IntentObserable.unregisterObserver(this);
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
                if (mParkSpaceInfo.getIdCardPositiveUrl().equals("-1")) {
                    startTakePhoto();
                } else {
                    showDialog();
                }
                break;
            case R.id.id_card_negative_photo_tv:
            case R.id.id_card_negative_photo_iv:
                mChoosePosition = 1;
                if (mParkSpaceInfo.getIdCardNegativeUrl().equals("-1")) {
                    startTakePhoto();
                } else {
                    showDialog();
                }
                break;
            case R.id.take_property_photo_cl:
                mChoosePosition = 2;
                startTakePropertyPhoto();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1 && data.hasExtra("park")) {
            //选择的停车场
            ParkBean mPark = (ParkBean) data.getSerializableExtra("park");
            mParkLotName.setText(mPark.getparkStation());
            String[] ccc = mPark.getProfit_ratio().split(":");
            String revenueRatio = ccc[0] + " : " + ccc[1] + " : " + ccc[2] + " （车位主 : 物业 : 平台）";
            mRevenueRatio.setText(revenueRatio);
            if (mRevenueRatioTv.getVisibility() != View.VISIBLE) {
                mRevenueRatioTv.setVisibility(View.VISIBLE);
                mRevenueRatio.setVisibility(View.VISIBLE);
            }

            mParkSpaceInfo.setParkLotId(mPark.getParkID());
            mParkSpaceInfo.setParkLotName(mPark.getParkStation());
            mParkSpaceInfo.setRevenueRatio(revenueRatio);
        } else if (requestCode == ConstansUtil.PICTURE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //选择的图片
            final List<ImageBean> imageBeans = data.getParcelableArrayListExtra(ImagePicker.INTENT_RESULT_DATA);
            if (imageBeans.size() == 1) {
                ImageUtil.compressPhoto(requireContext(), imageBeans.get(0).getImagePath(), new SuccessCallback<File>() {
                    @Override
                    public void onSuccess(File file) {
                        handleCompressPhoto(file, mChoosePosition);
                    }
                });
            } else {
                handleImageBean(imageBeans);
            }
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

    private void startTakePropertyPhoto() {
        if (mPropertyImagePicker == null) {
            mPropertyImagePicker = new ImagePicker()
                    .cachePath(Environment.getExternalStorageDirectory().getAbsolutePath())
                    .needCamera(true)
                    .pickType(ImagePickType.MULTI)
                    .maxNum(3);
        }
        int maxNum = 1;
        if (mPropertyAdapter.get(0).getPath().equals("-1")) {
            maxNum = 3;
        } else if (mPropertyAdapter.getDataSize() == 2 && mPropertyAdapter.get(1).getPath().equals("-1")) {
            maxNum = 2;
        }
        mPropertyImagePicker.maxNum(maxNum);
        mPropertyImagePicker.start(this, ConstansUtil.PICTURE_REQUEST_CODE);
    }

    private void showPhoto(String url, int position) {
        switch (position) {
            case 0:
                showIdCardPositivePhoto(url, false);
                break;
            case 1:
                showIdCardNegativePhoto(url, false);
                break;
            case 2:
                if (isVisible(mTakePropertyPhotoCl)) {
                    mTakePropertyPhotoCl.setVisibility(View.GONE);
                    mPropertyPhotoCl.setVisibility(View.VISIBLE);
                }
                PropertyPhoto firstProperty = new PropertyPhoto(url);
                firstProperty.setPath(url);
                firstProperty.setUploadSuccess(true);
                firstProperty.setShowProgress(false);
                mPropertyAdapter.notifyAddData(0, firstProperty);
                break;
            case 3:
                PropertyPhoto secondProperty = new PropertyPhoto(url);
                secondProperty.setPath(url);
                secondProperty.setUploadSuccess(true);
                secondProperty.setShowProgress(false);
                mPropertyAdapter.notifyAddData(1, secondProperty);
                break;
            case 4:
                PropertyPhoto thirdProperty = mPropertyAdapter.get(2);
                thirdProperty.setUploadSuccess(true);
                thirdProperty.setShowProgress(false);
                thirdProperty.setPath(url);
                mPropertyAdapter.notifyDataChange(2, thirdProperty);
                break;
        }
    }

    private void handleCompressPhoto(File file, int position) {
        switch (position) {
            case 0:
                mParkSpaceInfo.setIdCardPositiveUrl(file.getAbsolutePath());
                showIdCardPositivePhoto(mParkSpaceInfo.getIdCardPositiveUrl(), true);
                uploadPhoto(file, 0, position);
                break;
            case 1:
                mParkSpaceInfo.setIdCardNegativeUrl(file.getAbsolutePath());
                showIdCardNegativePhoto(mParkSpaceInfo.getIdCardNegativeUrl(), true);
                uploadPhoto(file, 0, position);
                break;
            case 2:
                if (mPropertyAdapter.getDataSize() == 1) {
                    if (isVisible(mTakePropertyPhotoCl)) {
                        mTakePropertyPhotoCl.setVisibility(View.GONE);
                        mPropertyPhotoCl.setVisibility(View.VISIBLE);
                    }
                    //还没有图片
                    PropertyPhoto firstProperty = new PropertyPhoto(file.getAbsolutePath());
                    mPropertyAdapter.notifyAddData(0, firstProperty);
                } else {
                    PropertyPhoto firstProperty = mPropertyAdapter.getData().get(0);
                    firstProperty.setPath(file.getAbsolutePath());
                    firstProperty.setShowProgress(true);
                    firstProperty.setProgress("0%");
                    mPropertyAdapter.notifyDataChange(0, firstProperty);
                }
                uploadPhoto(file, 1, position);
                break;
            case 3:
                if (mPropertyAdapter.getDataSize() <= 2) {
                    PropertyPhoto secondProperty = new PropertyPhoto(file.getAbsolutePath());
                    mPropertyAdapter.notifyAddData(mPropertyAdapter.getDataSize() - 1, secondProperty);
                } else {
                    PropertyPhoto secondProperty = mPropertyAdapter.getData().get(1);
                    secondProperty.setPath(file.getAbsolutePath());
                    secondProperty.setProgress("0%");
                    secondProperty.setShowProgress(true);
                    mPropertyAdapter.notifyDataChange(1, secondProperty);
                }
                uploadPhoto(file, 1, position);
                break;
            case 4:
                if (mPropertyAdapter.getDataSize() < 3) {
                    PropertyPhoto thirdProperty = new PropertyPhoto(file.getAbsolutePath());
                    mPropertyAdapter.notifyAddData(mPropertyAdapter.getDataSize() - 1, thirdProperty);
                } else if (mPropertyAdapter.getDataSize() == 3) {
                    PropertyPhoto thirdProperty = mPropertyAdapter.getData().get(2);
                    thirdProperty.setPath(file.getAbsolutePath());
                    thirdProperty.setUploadSuccess(false);
                    thirdProperty.setShowProgress(true);
                    thirdProperty.setProgress("0%");
                    mPropertyAdapter.notifyDataChange(2, thirdProperty);
                }
                uploadPhoto(file, 1, position);
                break;
        }
    }

    private void handleImageBean(List<ImageBean> imageBeans) {
        if (imageBeans.size() == 2) {
            switch (mChoosePosition) {
                case 2:
                    compressFirstPhoto(imageBeans.get(0).getImagePath());
                    compressSecondPhoto(imageBeans.get(1).getImagePath());
                    break;
                case 3:
                    compressSecondPhoto(imageBeans.get(0).getImagePath());
                    compressThirdPhoto(imageBeans.get(1).getImagePath());
                    break;
                case 4:
                    compressFirstPhoto(imageBeans.get(0).getImagePath());
                    compressSecondPhoto(imageBeans.get(1).getImagePath());
                    compressThirdPhoto(imageBeans.get(2).getImagePath());
                    break;
            }
        } else {
            compressFirstPhoto(imageBeans.get(0).getImagePath());
            compressSecondPhoto(imageBeans.get(1).getImagePath());
            compressThirdPhoto(imageBeans.get(2).getImagePath());
        }
    }

    private void compressFirstPhoto(String path) {
        ImageUtil.compressPhoto(requireContext(), path, new SuccessCallback<File>() {
            @Override
            public void onSuccess(File file) {
                handleCompressPhoto(file, 2);
            }
        });
    }

    private void compressSecondPhoto(String path) {
        ImageUtil.compressPhoto(requireContext(), path, new SuccessCallback<File>() {
            @Override
            public void onSuccess(File file) {
                handleCompressPhoto(file, 3);
            }
        });
    }

    private void compressThirdPhoto(String path) {
        ImageUtil.compressPhoto(requireContext(), path, new SuccessCallback<File>() {
            @Override
            public void onSuccess(File file) {
                handleCompressPhoto(file, 4);
            }
        });
    }

    private void uploadPhoto(File file, final int type, final int position) {
        OkGo.post(HttpConstants.uploadPicture)
                .retryCount(0)
                .headers("token", com.tuzhao.publicmanager.UserManager.getInstance().getUserInfo().getToken())
                .params("type", type)
                .params("picture", file)
                .execute(new JsonCallback<Base_Class_Info<String>>() {

                    @Override
                    public void onSuccess(Base_Class_Info<String> stringBase_class_info, Call call, Response response) {
                        if (type == 0) {
                            setServerUrl(HttpConstants.ROOT_IMG_URL_ID_CARD + stringBase_class_info.data, position);
                        } else {
                            setServerUrl(HttpConstants.ROOT_IMG_URL_PROPERTY + stringBase_class_info.data, position);
                        }
                        setUploadProgress(position, 1);
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.upProgress(currentSize, totalSize, progress, networkSpeed);
                        if (progress != 1) {
                            setUploadProgress(position, progress);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        deletePhoto(position);
                        Log.e(TAG, "onError: " + position);
                    }
                });
    }

    private void setServerUrl(String url, int position) {
        switch (position) {
            case 0:
                mParkSpaceInfo.setIdCardPositiveUrl(url);
                ImageUtil.showPicWithNoAnimate(mIdCardPositivePhoto, url);
                break;
            case 1:
                mParkSpaceInfo.setIdCardNegativeUrl(url);
                ImageUtil.showPicWithNoAnimate(mIdCardNegativePhoto, url);
                break;
            case 2:
                PropertyPhoto firstProperty = mPropertyAdapter.get(0);
                firstProperty.setPath(url);
                firstProperty.setUploadSuccess(true);
                mPropertyAdapter.notifyDataChange(0, firstProperty, 1);
                break;
            case 3:
                PropertyPhoto secondProperty = mPropertyAdapter.get(1);
                secondProperty.setPath(url);
                secondProperty.setUploadSuccess(true);
                mPropertyAdapter.notifyDataChange(1, secondProperty, 1);
                break;
            case 4:
                PropertyPhoto thirdProperty = mPropertyAdapter.get(2);
                thirdProperty.setPath(url);
                thirdProperty.setUploadSuccess(true);
                mPropertyAdapter.notifyDataChange(2, thirdProperty, 1);
                break;
        }
    }

    private void setUploadProgress(int position, float progress) {
        String progressString = (int) (progress * 100) + "%";
        switch (position) {
            case 0:
                mIdCardPositiveUploadTv.setText(progressString);
                if (progress == 1.0) {
                    mIdCardPositiveUploadTv.setVisibility(View.GONE);
                }
                break;
            case 1:
                mIdCardNegativeUploadTv.setText(progressString);
                if (progress == 1.0) {
                    mIdCardNegativeUploadTv.setVisibility(View.GONE);
                }
                break;
            case 2:
                PropertyPhoto firstProperty = mPropertyAdapter.getData().get(0);
                firstProperty.setProgress(progressString);
                if (progress == 1.0) {
                    firstProperty.setShowProgress(false);
                }
                mPropertyAdapter.notifyDataChange(0, firstProperty, 1);
                break;
            case 3:
                PropertyPhoto secondProperty = mPropertyAdapter.getData().get(1);
                secondProperty.setProgress(progressString);
                if (progress == 1.0) {
                    secondProperty.setShowProgress(false);
                }
                mPropertyAdapter.notifyDataChange(1, secondProperty, 1);
                break;
            case 4:
                PropertyPhoto thirdProperty = mPropertyAdapter.getData().get(2);
                thirdProperty.setProgress(progressString);
                if (progress == 1.0) {
                    thirdProperty.setShowProgress(false);
                }
                mPropertyAdapter.notifyDataChange(2, thirdProperty, 1);
                break;
        }
    }

    private void showDialog() {
        if (mCustomDialog == null) {
            View view = getLayoutInflater().inflate(R.layout.dialog_selete_photo_layout, null);
            mCustomDialog = new CustomDialog(requireContext(), view, true);
            view.findViewById(R.id.exchang_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mChoosePosition == 0 || mChoosePosition == 1) {
                        startTakePhoto();
                    } else {
                        startTakePropertyPhoto();
                    }
                    mCustomDialog.dismiss();
                }
            });

            view.findViewById(R.id.delete_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletePhoto(mChoosePosition);
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

    private void showIdCardPositivePhoto(String path, boolean showUpload) {
        if (mIdCardPositivePhotoTv.getVisibility() == View.VISIBLE) {
            mIdCardPositivePhotoTv.setVisibility(View.GONE);
        }

        ImageUtil.showPicWithNoAnimate(mIdCardPositivePhoto, path);

        if (showUpload) {
            if (!isVisible(mIdCardPositiveUploadTv)) {
                mIdCardPositiveUploadTv.setVisibility(View.VISIBLE);
                mIdCardPositiveUploadTv.setText("0%");
            }
        } else {
            if (isVisible(mIdCardPositiveUploadTv)) {
                mIdCardPositiveUploadTv.setVisibility(View.GONE);
            }
        }
    }

    private void showIdCardNegativePhoto(String path, boolean showUpload) {
        if (mIdCardNegativePhotoTv.getVisibility() == View.VISIBLE) {
            mIdCardNegativePhotoTv.setVisibility(View.GONE);
        }

        ImageUtil.showPicWithNoAnimate(mIdCardNegativePhoto, path);

        if (showUpload) {
            if (!isVisible(mIdCardNegativeUploadTv)) {
                mIdCardNegativeUploadTv.setVisibility(View.VISIBLE);
                mIdCardNegativeUploadTv.setText("0%");
            }
        } else {
            if (isVisible(mIdCardNegativeUploadTv)) {
                mIdCardNegativeUploadTv.setVisibility(View.GONE);
            }
        }
    }

    private void setTakePhotoPic(ImageView imageView) {
        imageView.setPadding(mSixtyDp, mEightyDp, mSixtyDp, mEightyDp);
        imageView.setBackgroundResource(R.drawable.y3_all_1dp);
        ImageUtil.showPic(imageView, R.drawable.ic_photo);
    }

    private void deletePhoto(int position) {
        switch (position) {
            case 0:
                ImageUtil.showPic(mIdCardPositivePhoto, R.drawable.ic_idcard);
                mIdCardPositivePhotoTv.setVisibility(View.VISIBLE);
                showProgressStatus(mIdCardPositiveUploadTv, false);
                mParkSpaceInfo.setIdCardPositiveUrl("-1");
                break;
            case 1:
                ImageUtil.showPic(mIdCardNegativePhoto, R.drawable.ic_idcard2);
                mIdCardNegativePhotoTv.setVisibility(View.VISIBLE);
                showProgressStatus(mIdCardNegativeUploadTv, false);
                mParkSpaceInfo.setIdCardNegativeUrl("-1");
                break;
            case 2:
                if (mPropertyAdapter.getDataSize() > 0) {
                    mPropertyAdapter.notifyRemoveData(0);
                }

                if (mPropertyAdapter.getDataSize() == 0 || !mPropertyAdapter.get(mPropertyAdapter.getDataSize() - 1).getPath().equals("-1")) {
                    mPropertyAdapter.notifyAddData(new PropertyPhoto());
                }
                if (mPropertyAdapter.getDataSize() == 1 && mPropertyAdapter.get(0).getPath().equals("-1")) {
                    mTakePropertyPhotoCl.setVisibility(View.VISIBLE);
                    mPropertyPhotoCl.setVisibility(View.GONE);
                }
                break;
            case 3:
                if (mPropertyAdapter.getDataSize() > 1) {
                    mPropertyAdapter.notifyRemoveData(mPropertyAdapter.getDataSize() - 2);
                    if (mPropertyAdapter.getDataSize() == 0 || !mPropertyAdapter.getData().get(mPropertyAdapter.getDataSize() - 1).getPath().equals("-1")) {
                        mPropertyAdapter.notifyAddData(new PropertyPhoto());
                    }
                    if (mPropertyAdapter.getDataSize() == 1 && mPropertyAdapter.get(0).getPath().equals("-1")) {
                        mTakePropertyPhotoCl.setVisibility(View.VISIBLE);
                        mPropertyPhotoCl.setVisibility(View.GONE);
                    }
                } else if (mPropertyAdapter.getDataSize() == 1) {
                    deletePhoto(4);
                }
                break;
            case 4:
                PropertyPhoto thirdProperty = mPropertyAdapter.getData().get(mPropertyAdapter.getDataSize() - 1);
                thirdProperty.setPath("-1");
                thirdProperty.setProgress("0%");
                thirdProperty.setShowProgress(false);
                thirdProperty.setUploadSuccess(false);
                mPropertyAdapter.notifyDataChange(mPropertyAdapter.getDataSize() - 1, thirdProperty);
                if (mPropertyAdapter.getDataSize() == 1 && mPropertyAdapter.get(0).getPath().equals("-1")) {
                    mTakePropertyPhotoCl.setVisibility(View.VISIBLE);
                    mPropertyPhotoCl.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void showProgressStatus(TextView textView, boolean showProgress) {
        if (showProgress) {
            if (!isVisible(textView)) {
                textView.setVisibility(View.VISIBLE);
                textView.setText("0%");
            }
        } else {
            if (isVisible(textView)) {
                textView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 验证车位信息是否都全部输入了
     */
    public void verifyInfo() {
        if (TextUtils.isEmpty(getText(mParkLotName))) {
            showFiveToast("请选择车场");
        } else if (TextUtils.isEmpty(getText(mParkSpaceDescription))) {
            showFiveToast("请输入车位描述");
        } else if (TextUtils.isEmpty(getText(mRealName).trim())) {
            showFiveToast("请您的真实姓名");
        } else if (mParkSpaceInfo.getIdCardPositiveUrl().equals("-1")) {
            showFiveToast("请上传身份证正面照");
        } else if (mParkSpaceInfo.getIdCardNegativeUrl().equals("-1")) {
            showFiveToast("请上传身份证反面照");
        } else if (mPropertyAdapter.get(0).getPath().equals("-1")) {
            showFiveToast("请上传车位产权照");
        } else if (isVisible(mIdCardPositiveUploadTv) || isVisible(mIdCardNegativeUploadTv)) {
            showFiveToast("请等待身份证照上传完成");
        } else {
            if (mPropertyAdapter.getDataSize() == 2) {
                if (!mPropertyAdapter.get(0).isUploadSuccess() ||
                        !mPropertyAdapter.get(1).getPath().equals("-1") && !mPropertyAdapter.get(1).isUploadSuccess()) {
                    showFiveToast("请等待产权照上传完成");
                } else {
                    Intent intent = new Intent();
                    intent.setAction(ConstansUtil.JUMP_TO_TIME_SETTING);
                    IntentObserable.dispatch(intent);
                }
            } else if (mPropertyAdapter.getDataSize() == 3) {
                if (!mPropertyAdapter.get(0).isUploadSuccess() ||
                        !mPropertyAdapter.get(1).getPath().equals("-1") && !mPropertyAdapter.get(1).isUploadSuccess()
                        || !mPropertyAdapter.get(2).getPath().equals("-1") && !mPropertyAdapter.get(2).isUploadSuccess()) {
                    showFiveToast("请等待产权照上传完成");
                } else {
                    Intent intent = new Intent();
                    intent.setAction(ConstansUtil.JUMP_TO_TIME_SETTING);
                    IntentObserable.dispatch(intent);
                }
            }
        }
    }

    public ParkSpaceInfo getParkSpaceInfo() {
        return mParkSpaceInfo;
    }

    class PropertyAdapter extends BaseAdapter<PropertyPhoto> {

        @Override
        protected void conver(@NonNull BaseViewHolder holder, final PropertyPhoto propertyPhoto, final int position) {
            ImageView imageView = holder.getView(R.id.property_photo_iv);
            TextView textView = holder.getView(R.id.property_upload_tv);
            if (propertyPhoto.getPath().equals("-1")) {
                setTakePhotoPic(imageView);
                if (isVisible(textView)) {
                    textView.setVisibility(View.GONE);
                }
                showProgressStatus(textView, false);
            } else {
                if (imageView.getPaddingTop() != 0) {
                    imageView.setPadding(0, 0, 0, 0);
                    imageView.setBackgroundResource(0);
                }
                ImageUtil.showPicWithNoAnimate(imageView, propertyPhoto.getPath());
                showProgressStatus(textView, propertyPhoto.isShowProgress());
                textView.setText(propertyPhoto.getProgress());
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mChoosePosition = position + 2;
                    if (propertyPhoto.getPath().equals("-1")) {
                        startTakePropertyPhoto();
                    } else {
                        showDialog();
                    }
                }
            });

        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
            if (payloads.isEmpty()) {
                onBindViewHolder(holder, position);
            } else {
                showProgressStatus((TextView) holder.getView(R.id.property_upload_tv), get(position).isShowProgress());
                holder.setText(R.id.property_upload_tv, get(position).getProgress());
            }
        }

        @Override
        protected int itemViewId() {
            return R.layout.item_property_photo_layout;
        }

    }
}
