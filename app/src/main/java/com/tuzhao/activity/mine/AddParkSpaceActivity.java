package com.tuzhao.activity.mine;

import android.content.Intent;
import android.graphics.Color;
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
import com.tianzhili.www.myselfsdk.pickerview.OptionsPickerView;
import com.tuzhao.R;
import com.tuzhao.activity.PayActivity;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkSpaceInfo;
import com.tuzhao.info.PropertyPhoto;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.GlideApp;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/6/4.
 */
public class AddParkSpaceActivity extends BaseStatusActivity implements View.OnClickListener, IntentObserver {

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

    private ArrayList<String> mAppointmentDays;

    private ArrayList<ArrayList<String>> mAppointmentTimeFramne;

    private OptionsPickerView<String> mAppointmentOption;

    private TextView mChooseAppointmentTime;

    private TextView mPayDeposit;

    private int mChoosePosition;

    private int mSixtyDp;

    private int mEightyDp;

    private ParkSpaceInfo mParkSpaceInfo;

    @Override
    protected int resourceId() {
        return R.layout.activity_add_park_space_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mParkLotName = findViewById(R.id.car_number);
        mRevenueRatioTv = findViewById(R.id.revenue_ratio_tv);
        mRevenueRatio = findViewById(R.id.revenue_ratio);
        mRealName = findViewById(R.id.real_name_et);
        mParkSpaceHint = findViewById(R.id.park_space_hint);
        mParkSpaceDescription = findViewById(R.id.park_space_description);
        mIdCardPositivePhoto = findViewById(R.id.id_card_positive_photo_iv);
        mIdCardPositiveUploadTv = findViewById(R.id.id_card_positive_upload_tv);
        mIdCardPositivePhotoTv = findViewById(R.id.id_card_positive_photo_tv);
        mIdCardNegativePhoto = findViewById(R.id.id_card_negative_photo_iv);
        mIdCardNegativeUploadTv = findViewById(R.id.id_card_negative_upload_tv);
        mIdCardNegativePhotoTv = findViewById(R.id.id_card_negative_photo_tv);
        mPropertyPhotoCl = findViewById(R.id.property_photos_cl);
        mTakePropertyPhotoCl = findViewById(R.id.take_property_photo_cl);
        mChooseAppointmentTime = findViewById(R.id.choose_appointment_time);
        mPayDeposit = findViewById(R.id.pay_deposit_tv);

        RecyclerView recyclerView = findViewById(R.id.property_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mPropertyAdapter = new PropertyAdapter();
        recyclerView.setAdapter(mPropertyAdapter);
        mPropertyAdapter.addData(new PropertyPhoto());

        mIdCardPositivePhotoTv.setOnClickListener(this);
        mIdCardPositiveUploadTv.setOnClickListener(this);
        mIdCardPositivePhoto.setOnClickListener(this);
        mIdCardNegativePhotoTv.setOnClickListener(this);
        mIdCardNegativeUploadTv.setOnClickListener(this);
        mIdCardNegativePhoto.setOnClickListener(this);
        mTakePropertyPhotoCl.setOnClickListener(this);
        mChooseAppointmentTime.setOnClickListener(this);
        mPayDeposit.setOnClickListener(this);
        findViewById(R.id.parking_lot_name_cl).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        getDepositSum(false);
        String hint = "温馨提醒：\n" +
                "1、请填写正确的车位信息\n" +
                "2、审核期间会有专人电访，请保持电话畅通\n" +
                "3、车位锁将免费提供，用户需缴纳押金，押金退锁即退";
        mParkSpaceHint.setText(hint);
        ImageUtil.showPic(mIdCardPositivePhoto, R.drawable.ic_idcard);
        ImageUtil.showPic(mIdCardNegativePhoto, R.drawable.ic_idcard2);

        mSixtyDp = DensityUtil.dp2px(this, 16);
        mEightyDp = DensityUtil.dp2px(this, 18);

        mParkSpaceInfo = new ParkSpaceInfo();
        initAppointmentOption();
        IntentObserable.registerObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
    }

    @NonNull
    @Override
    protected String title() {
        return "添加车位";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parking_lot_name_cl:
                Intent intent = new Intent(this, SelectParkSpaceActivity.class);
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
            case R.id.choose_appointment_time:
                mAppointmentOption.show();
                break;
            case R.id.pay_deposit_tv:
                verifyInfo();
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
            mParkSpaceInfo.setCityCode(mPark.getCitycode());
            mParkSpaceInfo.setParkLotName(mPark.getParkStation());
            mParkSpaceInfo.setRevenueRatio(revenueRatio);
        } else if (requestCode == ConstansUtil.PICTURE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //选择的图片
            final List<ImageBean> imageBeans = data.getParcelableArrayListExtra(ImagePicker.INTENT_RESULT_DATA);
            if (imageBeans.size() == 1) {
                ImageUtil.compressPhoto(this, imageBeans.get(0).getImagePath(), new SuccessCallback<File>() {
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

    private void getDepositSum(final boolean startPay) {
        showCantCancelLoadingDialog();

        getOkGo(HttpConstants.getDepositSum)
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> o, Call call, Response response) {
                        String depositSum = "缴纳" + DateUtil.deleteZero(o.data) + "元押金";
                        mPayDeposit.setText(depositSum);
                        dismmisLoadingDialog();
                        if (startPay) {
                            verifyInfo();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast("获取押金金额失败，请稍后重试");
                        }
                    }
                });
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

    private void handleImageBean(final List<ImageBean> imageBeans) {
        if (imageBeans.size() == 2) {
            switch (mChoosePosition) {
                case 2:
                    compressFirstPhoto(imageBeans.get(0).getImagePath(), new SuccessCallback<File>() {
                        @Override
                        public void onSuccess(File file) {
                            handleCompressPhoto(file, 2);
                            compressSecondPhoto(imageBeans.get(1).getImagePath(), new SuccessCallback<File>() {
                                @Override
                                public void onSuccess(File file) {
                                    handleCompressPhoto(file, 3);
                                }
                            });
                        }
                    });
                    break;
                case 3:
                    compressSecondPhoto(imageBeans.get(0).getImagePath(), new SuccessCallback<File>() {
                        @Override
                        public void onSuccess(File file) {
                            handleCompressPhoto(file, 3);
                            compressThirdPhoto(imageBeans.get(1).getImagePath(), new SuccessCallback<File>() {
                                @Override
                                public void onSuccess(File file) {
                                    handleCompressPhoto(file, 4);
                                }
                            });
                        }
                    });
                    break;
            }
        } else {
            compressFirstPhoto(imageBeans.get(0).getImagePath(), new SuccessCallback<File>() {
                @Override
                public void onSuccess(File file) {
                    handleCompressPhoto(file, 2);
                    compressSecondPhoto(imageBeans.get(1).getImagePath(), new SuccessCallback<File>() {
                        @Override
                        public void onSuccess(File file) {
                            handleCompressPhoto(file, 3);
                            compressThirdPhoto(imageBeans.get(2).getImagePath(), new SuccessCallback<File>() {
                                @Override
                                public void onSuccess(File file) {
                                    handleCompressPhoto(file, 4);
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    private void compressFirstPhoto(String path, SuccessCallback<File> callback) {
        ImageUtil.compressPhoto(this, path, callback);
    }

    private void compressSecondPhoto(String path, SuccessCallback<File> callback) {
        ImageUtil.compressPhoto(this, path, callback);
    }

    private void compressThirdPhoto(String path, SuccessCallback<File> callback) {
        ImageUtil.compressPhoto(this, path, callback);
    }

    private void uploadPhoto(final File file, final int type, final int position) {
        OkGo.post(HttpConstants.uploadPicture)
                .retryCount(0)
                .headers("token", com.tuzhao.publicmanager.UserManager.getInstance().getUserInfo().getToken())
                .params("type", type)
                .params("picture", file)
                .execute(new JsonCallback<Base_Class_Info<String>>() {

                    @Override
                    public void onSuccess(Base_Class_Info<String> stringBase_class_info, Call call, Response response) {
                        if (type == 0) {
                            setServerUrl(file.getAbsolutePath(), HttpConstants.ROOT_IMG_URL_ID_CARD + stringBase_class_info.data, position);
                        } else {
                            setServerUrl(file.getAbsolutePath(), HttpConstants.ROOT_IMG_URL_PROPERTY + stringBase_class_info.data, position);
                        }
                        setUploadProgress(file.getAbsolutePath(), position, 1);
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.upProgress(currentSize, totalSize, progress, networkSpeed);
                        if (progress != 1) {
                            setUploadProgress(file.getAbsolutePath(), position, progress);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        deletePhoto(file.getAbsolutePath(), position);
                    }
                });
    }

    private void setServerUrl(String filePath, String url, int position) {
        switch (position) {
            case 0:
                mParkSpaceInfo.setIdCardPositiveUrl(url);
                ImageUtil.showPicWithNoAnimate(mIdCardPositivePhoto, url);
                break;
            case 1:
                mParkSpaceInfo.setIdCardNegativeUrl(url);
                ImageUtil.showPicWithNoAnimate(mIdCardNegativePhoto, url);
                break;
            default:
                for (int i = 0; i < mPropertyAdapter.getDataSize(); i++) {
                    if (mPropertyAdapter.get(i).getPath().equals(filePath)) {
                        PropertyPhoto firstProperty = mPropertyAdapter.get(i);
                        firstProperty.setPath(url);
                        firstProperty.setShowProgress(false);
                        firstProperty.setUploadSuccess(true);
                        mPropertyAdapter.notifyDataChange(i, firstProperty, 1);
                        break;
                    }
                }
                break;
            /*case 2:
                mParkSpaceInfo.setPropertyFirstUrl(url);
                PropertyPhoto firstProperty = mPropertyAdapter.get(0);
                firstProperty.setPath(url);
                firstProperty.setUploadSuccess(true);
                mPropertyAdapter.notifyDataChange(0, firstProperty, 1);
                break;
            case 3:
                mParkSpaceInfo.setPropertySecondUrl(url);
                PropertyPhoto secondProperty = mPropertyAdapter.get(1);
                secondProperty.setPath(url);
                secondProperty.setUploadSuccess(true);
                mPropertyAdapter.notifyDataChange(1, secondProperty, 1);
                break;
            case 4:
                mParkSpaceInfo.setPropertyThirdUrl(url);
                PropertyPhoto thirdProperty = mPropertyAdapter.get(2);
                thirdProperty.setPath(url);
                thirdProperty.setUploadSuccess(true);
                mPropertyAdapter.notifyDataChange(2, thirdProperty, 1);
                break;*/
        }
    }

    private void setUploadProgress(String filePath, int position, float progress) {
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
            default:
                for (int i = 0; i < mPropertyAdapter.getDataSize(); i++) {
                    if (mPropertyAdapter.get(i).getPath().equals(filePath)) {
                        PropertyPhoto firstProperty = mPropertyAdapter.getData().get(i);
                        firstProperty.setProgress(progressString);
                        if (progress == 1.0) {
                            firstProperty.setShowProgress(false);
                        }
                        mPropertyAdapter.notifyDataChange(i, firstProperty, 1);
                        break;
                    }
                }
                break;
            /*case 2:
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
                break;*/
        }
    }

    private void showDialog() {
        if (mCustomDialog == null) {
            View view = getLayoutInflater().inflate(R.layout.dialog_selete_photo_layout, null);
            mCustomDialog = new CustomDialog(this, view, true);
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
                    deletePhoto(mPropertyAdapter.get(mChoosePosition - 2).getPath(), mChoosePosition);
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
        GlideApp.with(imageView.getContext())
                .clear(imageView);
        ImageUtil.showPicWithNoAnimate(imageView, R.drawable.ic_photo);
    }

    private void deletePhoto(String filePath, int position) {
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
            default:
                for (int i = 0; i < mPropertyAdapter.getDataSize(); i++) {
                    if (mPropertyAdapter.get(i).getPath().equals(filePath)) {
                        mPropertyAdapter.notifyRemoveData(i);
                        break;
                    }
                }
                if (mPropertyAdapter.getDataSize() == 0 || !mPropertyAdapter.get(mPropertyAdapter.getDataSize() - 1).getPath().equals("-1")) {
                    //如果最后那张不是拍摄图，则添加拍摄图
                    mPropertyAdapter.addData(new PropertyPhoto());
                    Log.e(TAG, "deletePhoto: ");
                }
                if (mPropertyAdapter.getDataSize() == 1 && mPropertyAdapter.get(0).getPath().equals("-1")) {
                    //如果没有图片则显示大的拍摄图
                    mTakePropertyPhotoCl.setVisibility(View.VISIBLE);
                    mPropertyPhotoCl.setVisibility(View.GONE);
                }
                break;
            /*case 2:
                if (mPropertyAdapter.getDataSize() > 0) {
                    mPropertyAdapter.notifyRemoveData(0);
                }

                if (mPropertyAdapter.getDataSize() == 0 || !mPropertyAdapter.get(mPropertyAdapter.getDataSize() - 1).getPath().equals("-1")) {
                    //如果最后那张不是拍摄图，则添加拍摄图
                    mPropertyAdapter.notifyAddData(new PropertyPhoto());
                }
                if (mPropertyAdapter.getDataSize() == 1 && mPropertyAdapter.get(0).getPath().equals("-1")) {
                    //如果没有图片则显示大的拍摄图
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
                break;*/
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

    private void initAppointmentOption() {
        mAppointmentDays = new ArrayList<>(7);
        mAppointmentTimeFramne = new ArrayList<>(7);
        Calendar calendar = Calendar.getInstance();
        mAppointmentDays.add("明天");
        mAppointmentDays.add("后天");
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        for (int i = 1; i <= 5; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            mAppointmentDays.add(DateUtil.getCalendarMonthToDayWithText(calendar));
        }

        ArrayList<String> timeFrame;
        for (int i = 0; i < 7; i++) {
            timeFrame = new ArrayList<>(2);
            timeFrame.add("上午");
            timeFrame.add("下午");
            mAppointmentTimeFramne.add(timeFrame);
        }

        mAppointmentOption = new OptionsPickerView<>(this);
        mAppointmentOption.setPicker(mAppointmentDays, mAppointmentTimeFramne, true);
        mAppointmentOption.setTextSize(16);
        mAppointmentOption.setCyclic(false);
        mAppointmentOption.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                mChooseAppointmentTime.setTextColor(Color.parseColor("#323232"));
                String chooseTime = mAppointmentDays.get(options1) + " " + mAppointmentTimeFramne.get(options1).get(option2);
                mChooseAppointmentTime.setText(chooseTime);
            }
        });
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
        } else if (getText(mChooseAppointmentTime).startsWith("选择")) {
            showFiveToast("请选择安装时间");
        } else if (isVisible(mIdCardPositiveUploadTv) || isVisible(mIdCardNegativeUploadTv)) {
            showFiveToast("请等待身份证照上传完成");
        } else {
            if (mPropertyAdapter.getDataSize() == 2) {
                if (!mPropertyAdapter.get(0).isUploadSuccess() ||
                        !mPropertyAdapter.get(1).getPath().equals("-1") && !mPropertyAdapter.get(1).isUploadSuccess()) {
                    showFiveToast("请等待产权照上传完成");
                } else {
                    addUserPark();
                }
            } else if (mPropertyAdapter.getDataSize() == 3) {
                if (!mPropertyAdapter.get(0).isUploadSuccess() ||
                        !mPropertyAdapter.get(1).getPath().equals("-1") && !mPropertyAdapter.get(1).isUploadSuccess()
                        || !mPropertyAdapter.get(2).getPath().equals("-1") && !mPropertyAdapter.get(2).isUploadSuccess()) {
                    showFiveToast("请等待产权照上传完成");
                } else {
                    addUserPark();
                }
            }
        }
    }

    private void addUserPark() {
        if (!getText(mPayDeposit).contains("元")) {
            getDepositSum(true);
            return;
        }

        showLoadingDialog("正在提交");
        Log.e(TAG, "addUserPark: " + mParkSpaceInfo);
        StringBuilder propertyPhoto = new StringBuilder(mPropertyAdapter.get(0).getPath().replace(HttpConstants.ROOT_BASE_IMG_URL, ""));
        propertyPhoto.append(",");
        if (mPropertyAdapter.getDataSize() == 2 && !mPropertyAdapter.get(1).getPath().equals("-1")) {
            propertyPhoto.append(mPropertyAdapter.get(1).getPath().replace(HttpConstants.ROOT_BASE_IMG_URL, ""));
            propertyPhoto.append(",");
        }
        if (mPropertyAdapter.getDataSize() == 3 && !mPropertyAdapter.get(2).getPath().equals("-1")) {
            propertyPhoto.append(mPropertyAdapter.get(2).getPath().replace(HttpConstants.ROOT_BASE_IMG_URL, ""));
            propertyPhoto.append(",");
        }
        propertyPhoto.deleteCharAt(propertyPhoto.length() - 1);
        Log.e(TAG, "addUserPark: " + propertyPhoto);

        Calendar calendar = Calendar.getInstance();
        String appointmentDate = getText(mChooseAppointmentTime);
        if (appointmentDate.startsWith("明天")) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        } else if (appointmentDate.startsWith("后天")) {
            calendar.add(Calendar.DAY_OF_MONTH, 2);
        } else {
            calendar.set(Calendar.MONTH, Integer.valueOf(appointmentDate.substring(0, appointmentDate.indexOf("月"))));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(appointmentDate.substring(appointmentDate.indexOf("月") + 1,
                    appointmentDate.indexOf("日"))));
        }
        appointmentDate = DateUtil.getCalendarYearToDay(calendar) + " " +
                appointmentDate.substring(appointmentDate.length() - 2, appointmentDate.length());

        getOkGo(HttpConstants.addUserPark)
                .params("parkspace_id", mParkSpaceInfo.getParkLotId())
                .params("citycode", mParkSpaceInfo.getCityCode())
                .params("address_memo", getText(mParkSpaceDescription))
                .params("applicant_name", getText(mRealName).trim())
                .params("idCardPhoto", mParkSpaceInfo.getIdCardPositiveUrl().replace(HttpConstants.ROOT_BASE_IMG_URL, "")
                        + "," + mParkSpaceInfo.getIdCardNegativeUrl().replace(HttpConstants.ROOT_BASE_IMG_URL, ""))
                .params("propertyPhoto", propertyPhoto.toString())
                .params("install_time", appointmentDate)
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> o, Call call, Response response) {
                        String payMoney = getText(mPayDeposit);
                        payMoney = payMoney.substring(payMoney.indexOf("缴纳"), payMoney.indexOf("元") + 1);

                        Bundle bundle = new Bundle();
                        bundle.putString(ConstansUtil.PAY_TYPE, "1");
                        bundle.putString(ConstansUtil.PAY_MONEY, payMoney);
                        bundle.putString(ConstansUtil.PARK_SPACE_ID, o.data);
                        bundle.putString(ConstansUtil.CITY_CODE, mParkSpaceInfo.getCityCode());
                        startActivity(PayActivity.class, bundle);
                        finish();
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
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(ConstansUtil.PAY_SUCCESS)) {

            }
        }
    }

    class PropertyAdapter extends BaseAdapter<PropertyPhoto> {

        @Override
        protected void conver(@NonNull BaseViewHolder holder, final PropertyPhoto propertyPhoto, final int position) {
            ImageView imageView = holder.getView(R.id.property_photo_iv);
            TextView textView = holder.getView(R.id.property_upload_tv);
            Log.e(TAG, "conver: " + propertyPhoto.getPath());
            if (propertyPhoto.getPath().equals("-1")) {
                Log.e(TAG, "conver: ");
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

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //do nothing
                }
            });
        }

       /* @Override
        public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
            if (payloads.isEmpty()) {
                onBindViewHolder(holder, position);
            } else {
                showProgressStatus((TextView) holder.getView(R.id.property_upload_tv), get(position).isShowProgress());
                holder.setText(R.id.property_upload_tv, get(position).getProgress());
                Log.e(TAG, "onBindViewHolder: " + position + "  path:" + get(position).getPath());
                ImageUtil.showPicWithNoAnimate((ImageView) holder.getView(R.id.property_photo_iv), get(position).getPath());
            }
        }*/

        @Override
        protected int itemViewId() {
            return R.layout.item_property_photo_layout;
        }

    }

}
