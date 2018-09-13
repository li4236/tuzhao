package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
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

import com.tianzhili.www.myselfsdk.SuspensionIndexBar.bean.ParkBean;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.photopicker.controller.PhotoPickConfig;
import com.tianzhili.www.myselfsdk.pickerview.OptionsPickerView;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkSpaceInfo;
import com.tuzhao.info.UploadPhotoInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.upload.MyFile;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.GlideApp;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/6/4.
 */
public class ChangeApplyParkSpaceInfoActivity extends BaseStatusActivity implements View.OnClickListener {

    private TextView mParkLotName;

    private TextView mRevenueRatio;

    private EditText mParkSpaceDescription;

    private EditText mRealName;

    private ImageView mIdCardPositivePhoto;

    private TextView mIdCardPositiveUploadTv;

    private TextView mIdCardPositivePhotoTv;

    private ImageView mIdCardNegativePhoto;

    private TextView mIdCardNegativeUploadTv;

    private TextView mIdCardNegativePhotoTv;

    private ConstraintLayout mPropertyPhotoCl;

    private ConstraintLayout mTakePropertyPhotoCl;

    private TextView mParkSpaceHint;

    private PropertyAdapter mPropertyAdapter;

    private CustomDialog mCustomDialog;

    private ArrayList<String> mAppointmentDays;

    private ArrayList<ArrayList<String>> mAppointmentTimeFramne;

    private OptionsPickerView<String> mAppointmentOption;

    private TextView mChooseAppointmentTime;

    private ParkSpaceInfo mParkSpaceInfo;

    private int mChoosePosition;

    private int mSixtyDp;

    private int mEightyDp;

    @Override
    protected int resourceId() {
        return R.layout.activity_change_apply_park_space_info_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mParkLotName = findViewById(R.id.car_number);
        mRevenueRatio = findViewById(R.id.revenue_ratio);
        mRealName = findViewById(R.id.real_name_et);
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
        mParkSpaceHint = findViewById(R.id.park_space_hint);

        RecyclerView recyclerView = findViewById(R.id.property_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mPropertyAdapter = new PropertyAdapter();
        recyclerView.setAdapter(mPropertyAdapter);
        mPropertyAdapter.addData(new UploadPhotoInfo());

        findViewById(R.id.parking_lot_name_cl).setOnClickListener(this);
        findViewById(R.id.modify_info_tv).setOnClickListener(this);
        mRevenueRatio.setOnClickListener(this);
        mIdCardPositivePhotoTv.setOnClickListener(this);
        mIdCardPositiveUploadTv.setOnClickListener(this);
        mIdCardPositivePhoto.setOnClickListener(this);
        mIdCardNegativePhotoTv.setOnClickListener(this);
        mIdCardNegativeUploadTv.setOnClickListener(this);
        mIdCardNegativePhoto.setOnClickListener(this);
        mTakePropertyPhotoCl.setOnClickListener(this);
        mChooseAppointmentTime.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        String hint = "温馨提醒：\n" +
                "1、资料有误可在审核前修改，审核成功后不可修改\n" +
                "2、审核成功后不可取消，可在安装后申请取消车位";
        mParkSpaceHint.setText(hint);

        mSixtyDp = DensityUtil.dp2px(this, 16);
        mEightyDp = DensityUtil.dp2px(this, 18);

        if (getIntent() != null) {
            mParkSpaceInfo = getIntent().getParcelableExtra(ConstansUtil.PARK_SPACE_INFO);

            assert mParkSpaceInfo != null;
            mParkLotName.setText(mParkSpaceInfo.getParkLotName());
            String revenueRatio = mParkSpaceInfo.getRevenueRatio() + " （车位主 : 物业 : 平台）";
            mRevenueRatio.setText(revenueRatio);
            mParkSpaceDescription.setText(mParkSpaceInfo.getParkSpaceDescription());
            setSelection(mParkSpaceDescription);
            mRealName.setText(mParkSpaceInfo.getRealName());

            if (!mParkSpaceInfo.getIdCardPositiveUrl().equals("-1")) {
                showPhoto(HttpConstants.ROOT_IMG_URL_ID_CARD + mParkSpaceInfo.getIdCardPositiveUrl(), 0);
            }

            if (!mParkSpaceInfo.getIdCardNegativeUrl().equals("-1")) {
                showPhoto(HttpConstants.ROOT_IMG_URL_ID_CARD + mParkSpaceInfo.getIdCardNegativeUrl(), 1);
            }
            if (!mParkSpaceInfo.getPropertyFirstUrl().equals("-1")) {
                showPhoto(HttpConstants.ROOT_IMG_URL_PROPERTY + mParkSpaceInfo.getPropertyFirstUrl(), 2);
            }

            if (!mParkSpaceInfo.getPropertySecondUrl().equals("-1")) {
                showPhoto(HttpConstants.ROOT_IMG_URL_PROPERTY + mParkSpaceInfo.getPropertySecondUrl(), 3);
            }

            if (!mParkSpaceInfo.getPropertyThirdUrl().equals("-1")) {
                showPhoto(HttpConstants.ROOT_IMG_URL_PROPERTY + mParkSpaceInfo.getPropertyThirdUrl(), 4);
            }

            Calendar installCalendar = DateUtil.getYearToDayCalendar(mParkSpaceInfo.getInstallTime().split(" ")[0], false);
            Calendar nowCalendar = Calendar.getInstance();
            StringBuilder installTime = new StringBuilder();
            if (DateUtil.isInSameDay(installCalendar, nowCalendar)) {
                installTime.append("今天");
            } else {
                nowCalendar.add(Calendar.DAY_OF_MONTH, 1);
                if (DateUtil.isInSameDay(installCalendar, nowCalendar)) {
                    installTime.append("明天");
                } else {
                    nowCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    if (DateUtil.isInSameDay(installCalendar, nowCalendar)) {
                        installTime.append("后天");
                    } else {
                        installTime.append(DateUtil.getCalendarMonthToDayWithText(installCalendar));
                    }
                }
            }
            installTime.append(" ");
            installTime.append(mParkSpaceInfo.getInstallTime().split(" ")[1]);
            mChooseAppointmentTime.setText(installTime.toString());

            initAppointmentOption();
        } else {
            showFiveToast("获取申请资料失败，请稍后重试");
            finish();
        }

    }

    @NonNull
    @Override
    protected String title() {
        return "修改资料";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parking_lot_name_cl:
            case R.id.revenue_ratio:
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
                startTakePhoto();
                break;
            case R.id.choose_appointment_time:
                mAppointmentOption.show();
                break;
            case R.id.modify_info_tv:
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

            mParkSpaceInfo.setParkLotId(mPark.getParkId());
            mParkSpaceInfo.setParkLotName(mPark.getParkStation());
            mParkSpaceInfo.setRevenueRatio(revenueRatio);
        } else if (requestCode == PhotoPickConfig.PICK_MORE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //选择的图片
            final List<String> imageBeans = data.getStringArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST);
            showLoadingDialog("压缩中...");
            if (imageBeans.size() == 1) {
                ImageUtil.compressPhoto(this, imageBeans.get(0), new SuccessCallback<MyFile>() {
                    @Override
                    public void onSuccess(MyFile file) {
                        handleCompressPhoto(file, mChoosePosition);
                    }
                });
            } else {
                handleImageBean(imageBeans);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlideApp.get(getApplicationContext()).clearMemory();
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
                String chooseTime = mAppointmentDays.get(options1) + " " + mAppointmentTimeFramne.get(options1).get(option2);
                mChooseAppointmentTime.setText(chooseTime);
            }
        });
    }

    private void startTakePhoto() {
        int maxNum = 1;
        if (mChoosePosition != 0 && mChoosePosition != 1) {
            if (mPropertyAdapter.get(0).getPath().equals("-1")) {
                maxNum = 3;
            } else if (mPropertyAdapter.getDataSize() == 2 && mPropertyAdapter.get(1).getPath().equals("-1")) {
                maxNum = 2;
            }
        }
        ImageUtil.startTakeMultiPhoto(this, maxNum);
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
                UploadPhotoInfo firstProperty = new UploadPhotoInfo(url);
                firstProperty.setUploadSuccess(true);
                firstProperty.setShowProgress(false);
                mPropertyAdapter.notifyAddData(0, firstProperty);
                break;
            case 3:
                UploadPhotoInfo secondProperty = new UploadPhotoInfo(url);
                secondProperty.setUploadSuccess(true);
                secondProperty.setShowProgress(false);
                mPropertyAdapter.notifyAddData(1, secondProperty);
                break;
            case 4:
                UploadPhotoInfo thirdProperty = mPropertyAdapter.get(2);
                thirdProperty.setUploadSuccess(true);
                thirdProperty.setShowProgress(false);
                thirdProperty.setPath(url);
                mPropertyAdapter.notifyDataChange(2, thirdProperty);
                break;
        }
    }

    private void handleCompressPhoto(File file, int position) {
        dismmisLoadingDialog();
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
                    UploadPhotoInfo firstProperty = new UploadPhotoInfo(file.getAbsolutePath());
                    mPropertyAdapter.notifyAddData(0, firstProperty);
                } else {
                    UploadPhotoInfo firstProperty = mPropertyAdapter.getData().get(0);
                    firstProperty.setPath(file.getAbsolutePath());
                    firstProperty.setShowProgress(true);
                    firstProperty.setProgress("0%");
                    mPropertyAdapter.notifyDataChange(0, firstProperty);
                }
                uploadPhoto(file, 1, position);
                break;
            case 3:
                if (mPropertyAdapter.getDataSize() <= 2) {
                    UploadPhotoInfo secondProperty = new UploadPhotoInfo(file.getAbsolutePath());
                    mPropertyAdapter.notifyAddData(mPropertyAdapter.getDataSize() - 1, secondProperty);
                } else {
                    UploadPhotoInfo secondProperty = mPropertyAdapter.getData().get(1);
                    secondProperty.setPath(file.getAbsolutePath());
                    secondProperty.setProgress("0%");
                    secondProperty.setShowProgress(true);
                    mPropertyAdapter.notifyDataChange(1, secondProperty);
                }
                uploadPhoto(file, 1, position);
                break;
            case 4:
                if (mPropertyAdapter.getDataSize() < 3) {
                    UploadPhotoInfo thirdProperty = new UploadPhotoInfo(file.getAbsolutePath());
                    mPropertyAdapter.notifyAddData(mPropertyAdapter.getDataSize() - 1, thirdProperty);
                } else if (mPropertyAdapter.getDataSize() == 3) {
                    UploadPhotoInfo thirdProperty = mPropertyAdapter.getData().get(2);
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

    private void handleImageBean(final List<String> imageBeans) {
        if (imageBeans.size() == 2) {
            switch (mChoosePosition) {
                case 2:
                    compressFirstPhoto(imageBeans.get(0), new SuccessCallback<MyFile>() {
                        @Override
                        public void onSuccess(MyFile file) {
                            handleCompressPhoto(file, 2);
                            compressSecondPhoto(imageBeans.get(1), new SuccessCallback<MyFile>() {
                                @Override
                                public void onSuccess(MyFile file) {
                                    handleCompressPhoto(file, 3);
                                }
                            });
                        }
                    });
                    break;
                case 3:
                    compressSecondPhoto(imageBeans.get(0), new SuccessCallback<MyFile>() {
                        @Override
                        public void onSuccess(MyFile file) {
                            handleCompressPhoto(file, 3);
                            compressThirdPhoto(imageBeans.get(1), new SuccessCallback<MyFile>() {
                                @Override
                                public void onSuccess(MyFile file) {
                                    handleCompressPhoto(file, 4);
                                }
                            });
                        }
                    });
                    break;
            }
        } else {
            compressFirstPhoto(imageBeans.get(0), new SuccessCallback<MyFile>() {
                @Override
                public void onSuccess(MyFile file) {
                    handleCompressPhoto(file, 2);
                    compressSecondPhoto(imageBeans.get(1), new SuccessCallback<MyFile>() {
                        @Override
                        public void onSuccess(MyFile file) {
                            handleCompressPhoto(file, 3);
                            compressThirdPhoto(imageBeans.get(2), new SuccessCallback<MyFile>() {
                                @Override
                                public void onSuccess(MyFile file) {
                                    handleCompressPhoto(file, 4);
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    private void compressFirstPhoto(String path, SuccessCallback<MyFile> callback) {
        ImageUtil.compressPhoto(this, path, callback);
    }

    private void compressSecondPhoto(String path, SuccessCallback<MyFile> callback) {
        ImageUtil.compressPhoto(this, path, callback);
    }

    private void compressThirdPhoto(String path, SuccessCallback<MyFile> callback) {
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
                        setUploadProgress(file.getAbsolutePath(), position, 1);
                        if (type == 0) {
                            setServerUrl(file.getAbsolutePath(), HttpConstants.ROOT_IMG_URL_ID_CARD + stringBase_class_info.data, position);
                        } else {
                            setServerUrl(file.getAbsolutePath(), HttpConstants.ROOT_IMG_URL_PROPERTY + stringBase_class_info.data, position);
                        }
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
                        Log.e(TAG, "onError: " + position);
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
                        UploadPhotoInfo firstProperty = mPropertyAdapter.get(i);
                        firstProperty.setPath(url);
                        firstProperty.setShowProgress(false);
                        firstProperty.setUploadSuccess(true);
                        mPropertyAdapter.notifyDataChange(i, firstProperty, 1);
                        break;
                    }
                }
                break;
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
                        UploadPhotoInfo firstProperty = mPropertyAdapter.getData().get(i);
                        firstProperty.setProgress(progressString);
                        mPropertyAdapter.notifyDataChange(i, firstProperty, 1);
                        break;
                    }
                }
                break;
        }
    }

    private void showDialog() {
        if (mCustomDialog == null) {
            View view = getLayoutInflater().inflate(R.layout.dialog_selete_photo_layout, null);
            mCustomDialog = new CustomDialog(this, view, true);
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
                    if (mChoosePosition > 1) {
                        deletePhoto(mPropertyAdapter.get(mChoosePosition - 2).getPath(), mChoosePosition);
                    } else {
                        deletePhoto("-1", mChoosePosition);
                    }
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

        ImageUtil.showPicWithNoAnimate(mIdCardPositivePhoto, path, new LoadFailCallback() {
            @Override
            public void onLoadFail(Exception e) {
                deletePhoto(e.getMessage(), 0);
            }
        });

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

        ImageUtil.showPicWithNoAnimate(mIdCardNegativePhoto, path, new LoadFailCallback() {
            @Override
            public void onLoadFail(Exception e) {
                deletePhoto(e.getMessage(), 1);
            }
        });

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

    private void deletePhoto(String filePath, int position) {
        switch (position) {
            case 0:
                ImageUtil.showPic(mIdCardPositivePhoto, R.drawable.ic_idcard);
                mIdCardPositivePhotoTv.setVisibility(View.VISIBLE);
                showProgressStatus(mIdCardPositiveUploadTv, false);
                mParkSpaceInfo.setIdCardPositiveUrl("-1");
                mParkSpaceInfo.setIdCardPhoto("-1," + mParkSpaceInfo.getIdCardNegativeUrl());
                break;
            case 1:
                ImageUtil.showPic(mIdCardNegativePhoto, R.drawable.ic_idcard2);
                mIdCardNegativePhotoTv.setVisibility(View.VISIBLE);
                showProgressStatus(mIdCardNegativeUploadTv, false);
                mParkSpaceInfo.setIdCardNegativeUrl("-1");
                mParkSpaceInfo.setIdCardPhoto(mParkSpaceInfo.getIdCardPositiveUrl() + ",-1");
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
                    mPropertyAdapter.addData(new UploadPhotoInfo());
                }
                if (mPropertyAdapter.getDataSize() == 1 && mPropertyAdapter.get(0).getPath().equals("-1")) {
                    //如果没有图片则显示大的拍摄图
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
        if (TextUtils.isEmpty(getText(mParkSpaceDescription))) {
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
                if (!mPropertyAdapter.get(0).isUploadSuccess()) {
                    showFiveToast("请等待产权照上传完成");
                } else {
                    modifyAuditInfo();
                }
            } else if (mPropertyAdapter.getDataSize() == 3) {
                if (!mPropertyAdapter.get(0).isUploadSuccess() ||
                        (!mPropertyAdapter.get(1).getPath().equals("-1") && !mPropertyAdapter.get(1).isUploadSuccess())
                        || (!mPropertyAdapter.get(2).getPath().equals("-1") && !mPropertyAdapter.get(2).isUploadSuccess())) {
                    showFiveToast("请等待产权照上传完成");
                } else {
                    modifyAuditInfo();
                }
            }
        }
    }

    private void modifyAuditInfo() {
        showLoadingDialog("正在修改");

        mParkSpaceInfo.setParkLotName(getText(mParkLotName));
        mParkSpaceInfo.setRevenueRatio(getText(mRevenueRatio).split(" （")[0]);
        mParkSpaceInfo.setParkSpaceDescription(getText(mParkSpaceDescription).trim());
        mParkSpaceInfo.setRealName(getText(mRealName).trim());

        mParkSpaceInfo.setIdCardPhoto(mParkSpaceInfo.getIdCardPositiveUrl().replace(HttpConstants.ROOT_IMG_URL_ID_CARD, "")
                + "," + mParkSpaceInfo.getIdCardNegativeUrl().replace(HttpConstants.ROOT_IMG_URL_ID_CARD, ""));

        StringBuilder propertyPhoto = new StringBuilder(mPropertyAdapter.get(0).getPath().replace(HttpConstants.ROOT_IMG_URL_PROPERTY, ""));
        propertyPhoto.append(",");
        if (mPropertyAdapter.getDataSize() == 3) {
            propertyPhoto.append(mPropertyAdapter.get(1).getPath().replace(HttpConstants.ROOT_IMG_URL_PROPERTY, ""));
            propertyPhoto.append(",");
            if (!mPropertyAdapter.get(2).getPath().equals("-1")) {
                propertyPhoto.append(mPropertyAdapter.get(2).getPath().replace(HttpConstants.ROOT_IMG_URL_PROPERTY, ""));
                propertyPhoto.append(",");
            }
        }
        propertyPhoto.deleteCharAt(propertyPhoto.length() - 1);
        mParkSpaceInfo.setPropertyPhoto(propertyPhoto.toString());

        final Calendar calendar = Calendar.getInstance();
        String appointmentDate = getText(mChooseAppointmentTime);
        if (appointmentDate.startsWith("明天")) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        } else if (appointmentDate.startsWith("后天")) {
            calendar.add(Calendar.DAY_OF_MONTH, 2);
        } else if (!appointmentDate.startsWith("今天")) {
            calendar.set(Calendar.MONTH, Integer.valueOf(appointmentDate.substring(0, appointmentDate.indexOf("月"))) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(appointmentDate.substring(appointmentDate.indexOf("月") + 1,
                    appointmentDate.indexOf("日"))));
        }
        appointmentDate = DateUtil.getCalendarYearToDay(calendar) + " " +
                appointmentDate.substring(appointmentDate.length() - 2, appointmentDate.length());
        mParkSpaceInfo.setInstallTime(appointmentDate);

        getOkGo(HttpConstants.modifyAuditParkSpaceInfo)
                .params("parkAuditId", mParkSpaceInfo.getId())
                .params("parkLotId", mParkSpaceInfo.getParkLotId())
                .params("citycode", mParkSpaceInfo.getCityCode())
                .params("address_memo", mParkSpaceInfo.getParkSpaceDescription())
                .params("applicant_name", mParkSpaceInfo.getRealName())
                .params("idCardPhoto", mParkSpaceInfo.getIdCardPhoto())
                .params("propertyPhoto", mParkSpaceInfo.getPropertyPhoto())
                .params("install_time", mParkSpaceInfo.getInstallTime())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        Intent intent = new Intent(ConstansUtil.MODIFY_AUDIT_PARK_SPACE_INFO);
                        mParkSpaceInfo.setIdCardPositiveUrl("-1");      //防止下次再读取时会带有url前缀
                        mParkSpaceInfo.setIdCardNegativeUrl("-1");
                        mParkSpaceInfo.setPropertyFirstUrl("-1");
                        mParkSpaceInfo.setPropertySecondUrl("-1");
                        mParkSpaceInfo.setPropertyThirdUrl("-1");
                        intent.putExtra(ConstansUtil.PARK_SPACE_INFO, mParkSpaceInfo);
                        IntentObserable.dispatch(intent);
                        Log.e(TAG, "onSuccess: " + mParkSpaceInfo);
                        showFiveToast("修改成功");
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                case "102":
                                    showFiveToast("请选择车场");
                                    break;
                                case "103":
                                    showFiveToast("请输入车位描述");
                                    break;
                                case "104":
                                    showFiveToast("请您的真实姓名");
                                    break;
                                case "105":
                                    showFiveToast("请上传身份证");
                                    break;
                                case "106":
                                    showFiveToast("请上传车位产权照");
                                    break;
                                case "107":
                                    showFiveToast("请选择安装时间");
                                    break;
                                case "108":
                                    showFiveToast("图片上传失败，请重新选择");
                                    break;
                                case "109":
                                    showFiveToast("添加审核失败，请稍后重试");
                                    break;
                            }
                        }
                    }
                });
    }

    class PropertyAdapter extends BaseAdapter<UploadPhotoInfo> {

        @Override
        protected void conver(@NonNull BaseViewHolder holder, final UploadPhotoInfo uploadPhotoInfo, final int position) {
            ImageView imageView = holder.getView(R.id.property_photo_iv);
            TextView textView = holder.getView(R.id.property_upload_tv);
            if (uploadPhotoInfo.getPath().equals("-1")) {
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
                ImageUtil.showPicWithNoAnimate(imageView, uploadPhotoInfo.getPath(), new LoadFailCallback() {
                    @Override
                    public void onLoadFail(Exception e) {
                        deletePhoto(e.getMessage(), -1);
                    }
                });
                showProgressStatus(textView, uploadPhotoInfo.isShowProgress());
                textView.setText(uploadPhotoInfo.getProgress());
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mChoosePosition = position + 2;
                    if (uploadPhotoInfo.getPath().equals("-1")) {
                        startTakePhoto();
                    } else {
                        showDialog();
                    }
                }
            });

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //为了上传时不可点击
                }
            });
        }

        @Override
        protected int itemViewId() {
            return R.layout.item_property_photo_layout;
        }

    }
}
