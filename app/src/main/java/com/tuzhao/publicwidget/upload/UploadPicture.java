package com.tuzhao.publicwidget.upload;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.adapter.BaseAdapter;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.UploadPhotoInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.GlideApp;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;
import com.tuzhao.utils.ViewUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/7/28.
 */
public class UploadPicture<AD extends BaseAdapter<UploadPhotoInfo>> implements IntentObserver {

    private static final String TAG = "UploadPicture";

    private CustomDialog mCustomDialog;

    private int mMaxNum = 3;

    private Activity mActivity;

    private AD mAdapter;

    private int mChoosePosition;

    private int mPictureType;

    private String mBasePictureUrl;

    private boolean mShouldAdjustPadding = true;

    private int mTakePhotoRes = R.drawable.ic_addimg;

    private int mStartPadding;

    private int mTopPadding;

    private LoadingDialog mLoadingDialog;

    public UploadPicture(Activity activity, AD adapter, int pictureType) {
        mActivity = activity;
        mAdapter = adapter;
        mPictureType = pictureType;
        mStartPadding = DensityUtil.dp2px(mActivity, 16);
        mTopPadding = DensityUtil.dp2px(mActivity, 18);
        initBasePictureUrl();
        initDialog();
        IntentObserable.registerObserver(this);
    }

    private void initBasePictureUrl() {
        switch (mPictureType) {
            case 0:
                mBasePictureUrl = HttpConstants.ROOT_IMG_URL_ID_CARD;
                break;
            case 1:
                mBasePictureUrl = HttpConstants.ROOT_IMG_URL_PROPERTY;
            case 2:
                mBasePictureUrl = HttpConstants.ROOT_IMG_URL_PSCOM;
                break;
            case 3:
                mBasePictureUrl = HttpConstants.ROOT_IMG_URL_DRIVER_LICENSE;
                break;
            case 4:
                mBasePictureUrl = HttpConstants.ROOT_IMG_URL_COMPLAINT;
                break;
            case 7:
                mBasePictureUrl = HttpConstants.ROOT_IMG_URL_LOCK_FAULT;
                break;
            default:
                mBasePictureUrl = "";
        }
    }

    public void startTakePropertyPhoto() {
        int maxNum = 1;
        if (mAdapter.get(mAdapter.getDataSize() - 1).getPath().equals("-1")) {
            maxNum = mMaxNum - mAdapter.getDataSize() + 1;
        }
        ImageUtil.startTakeMultiPhoto(mActivity, maxNum);
    }

    public void startTakePropertyPhoto(int maxNum) {
        ImageUtil.startTakeMultiPhoto(mActivity, maxNum);
    }

    private void initDialog() {
        View view = mActivity.getLayoutInflater().inflate(R.layout.dialog_selete_photo_layout, null);
        mCustomDialog = new CustomDialog(mActivity, view, true);
        view.findViewById(R.id.exchang_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTakePropertyPhoto();
                mCustomDialog.dismiss();
            }
        });

        view.findViewById(R.id.delete_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePhoto(mAdapter.get(mChoosePosition).getPath());
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

    private void showDialog() {
        mCustomDialog.show();
    }

    private void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(mActivity, "压缩中..");
        }
        mLoadingDialog.show();
    }

    private void dismissLoading() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    private void cancelLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
    }

    private void handleImageBean(final List<String> imageBeans) {
        if (imageBeans.size() == 2) {
            switch (mChoosePosition) {
                case 0:
                    ImageUtil.compressPhoto(mActivity, imageBeans.get(0), new SuccessCallback<MyFile>() {
                        @Override
                        public void onSuccess(MyFile file) {
                            handleCompressPhoto(file, 0);
                            ImageUtil.compressPhoto(mActivity, imageBeans.get(1), new SuccessCallback<MyFile>() {
                                @Override
                                public void onSuccess(MyFile file) {
                                    dismissLoading();
                                    handleCompressPhoto(file, 1);
                                }
                            });
                        }
                    });
                    break;
                case 1:
                    ImageUtil.compressPhoto(mActivity, imageBeans.get(0), new SuccessCallback<MyFile>() {
                        @Override
                        public void onSuccess(MyFile file) {
                            handleCompressPhoto(file, 1);
                            ImageUtil.compressPhoto(mActivity, imageBeans.get(1), new SuccessCallback<MyFile>() {
                                @Override
                                public void onSuccess(MyFile file) {
                                    dismissLoading();
                                    handleCompressPhoto(file, 2);
                                }
                            });
                        }
                    });
                    break;
            }
        } else {
            ImageUtil.compressPhoto(mActivity, imageBeans.get(0), new SuccessCallback<MyFile>() {
                @Override
                public void onSuccess(MyFile file) {
                    handleCompressPhoto(file, 0);
                    ImageUtil.compressPhoto(mActivity, imageBeans.get(1), new SuccessCallback<MyFile>() {
                        @Override
                        public void onSuccess(MyFile file) {
                            handleCompressPhoto(file, 1);
                            ImageUtil.compressPhoto(mActivity, imageBeans.get(2), new SuccessCallback<MyFile>() {
                                @Override
                                public void onSuccess(MyFile file) {
                                    dismissLoading();
                                    handleCompressPhoto(file, 2);
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    private void handleCompressPhoto(File file, int position) {
        switch (position) {
            case 0:
                if (mAdapter.getDataSize() == 1 && mAdapter.get(0).getPath().equals("-1")) {
                    //还没有图片
                    UploadPhotoInfo firstProperty = new UploadPhotoInfo(file.getAbsolutePath());
                    mAdapter.notifyAddData(0, firstProperty);
                } else {
                    UploadPhotoInfo firstProperty = mAdapter.getData().get(0);
                    firstProperty.setPath(file.getAbsolutePath());
                    firstProperty.setShowProgress(true);
                    firstProperty.setProgress("0%");
                    mAdapter.notifyDataChange(0);
                }
                uploadPhoto(file);
                break;
            case 1:
                if (mAdapter.getDataSize() < 3) {
                    UploadPhotoInfo secondProperty = new UploadPhotoInfo(file.getAbsolutePath());
                    mAdapter.notifyAddData(mAdapter.getDataSize() - 1, secondProperty);
                } else {
                    UploadPhotoInfo secondProperty = mAdapter.getData().get(1);
                    secondProperty.setPath(file.getAbsolutePath());
                    secondProperty.setProgress("0%");
                    secondProperty.setShowProgress(true);
                    mAdapter.notifyDataChange(1);
                }
                uploadPhoto(file);
                break;
            case 2:
                if (mAdapter.getDataSize() < 3) {
                    UploadPhotoInfo thirdProperty = new UploadPhotoInfo(file.getAbsolutePath());
                    mAdapter.notifyAddData(thirdProperty);
                } else if (mAdapter.getDataSize() == 3) {
                    UploadPhotoInfo thirdProperty = mAdapter.getData().get(2);
                    thirdProperty.setPath(file.getAbsolutePath());
                    thirdProperty.setUploadSuccess(false);
                    thirdProperty.setShowProgress(true);
                    thirdProperty.setProgress("0%");
                    mAdapter.notifyDataChange(2);
                }
                uploadPhoto(file);
                break;
        }
    }

    private void uploadPhoto(final File file) {
        OkGo.post(HttpConstants.uploadPicture)
                .retryCount(0)
                .headers("token", com.tuzhao.publicmanager.UserManager.getInstance().getUserInfo().getToken())
                .params("type", mPictureType)
                .params("picture", file)
                .execute(new JsonCallback<Base_Class_Info<String>>() {

                    @Override
                    public void onSuccess(Base_Class_Info<String> stringBase_class_info, Call call, Response response) {
                        setServerUrl(file.getAbsolutePath(), mBasePictureUrl + stringBase_class_info.data);
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.upProgress(currentSize, totalSize, progress, networkSpeed);
                        if (progress != 1) {
                            setUploadProgress(file.getAbsolutePath(), progress);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        deletePhoto(file.getAbsolutePath());
                    }
                });
    }

    private void setUploadProgress(String filePath, float progress) {
        String progressString = (int) (progress * 100) + "%";
        for (int i = 0; i < mAdapter.getDataSize(); i++) {
            if (mAdapter.get(i).getPath().equals(filePath)) {
                UploadPhotoInfo firstProperty = mAdapter.getData().get(i);
                firstProperty.setProgress(progressString);
                if (progress == 1.0) {
                    firstProperty.setShowProgress(false);
                }
                mAdapter.notifyDataChange(i, 1);
                break;
            }
        }
    }

    private void setServerUrl(String filePath, String url) {
        for (int i = 0; i < mAdapter.getDataSize(); i++) {
            if (mAdapter.get(i).getPath().equals(filePath)) {
                UploadPhotoInfo firstProperty = mAdapter.get(i);
                firstProperty.setPath(url);
                firstProperty.setProgress("0%");
                firstProperty.setShowProgress(false);
                firstProperty.setUploadSuccess(true);
                mAdapter.notifyDataChange(i, 1);
                break;
            }
        }
    }

    public void deletePhoto(String filePath) {
        for (int i = 0; i < mAdapter.getDataSize(); i++) {
            if (mAdapter.get(i).getPath().equals(filePath)) {
                if (i == mMaxNum - 1) {
                    mAdapter.notifyDataChange(mMaxNum - 1, new UploadPhotoInfo());
                } else {
                    mAdapter.notifyRemoveData(i);
                }
                break;
            }
        }

        if (mAdapter.getDataSize() == 0 || !mAdapter.get(mAdapter.getDataSize() - 1).getPath().equals("-1")) {
            //如果没有拍摄图或最后一张不是拍摄图，则添加拍摄图
            mAdapter.notifyAddData(new UploadPhotoInfo());
        }

    }

    public String getUploadPictures() {
        StringBuilder stringBuilder = new StringBuilder();
        for (UploadPhotoInfo uploadPhotoInfo : mAdapter.getData()) {
            if (!uploadPhotoInfo.getPath().equals("-1")) {
                stringBuilder.append(uploadPhotoInfo.getPath().replace(mBasePictureUrl, ""));
                stringBuilder.append(",");
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        } else {
            stringBuilder.append("-1");
        }
        return stringBuilder.toString();
    }

    protected void setTakePhotoPic(ImageView imageView) {
        imageView.setPadding(mStartPadding, mTopPadding, mStartPadding, mTopPadding);
        imageView.setBackgroundResource(R.drawable.stroke_y3_corner_1dp);
        ImageUtil.showPicWithNoAnimate(imageView, R.drawable.ic_photo);
    }

    public void conver(ImageView imageView, TextView textView, final UploadPhotoInfo uploadPhotoInfo, final int position) {
        if (uploadPhotoInfo.getPath().equals("-1")) {
            if (mShouldAdjustPadding) {
                setTakePhotoPic(imageView);
            } else {
                ImageUtil.showPic(imageView, mTakePhotoRes);
            }
            if (ViewUtil.isVisible(textView)) {
                textView.setVisibility(View.GONE);
            }
            ViewUtil.showProgressStatus(textView, false);
        } else {
            if (mShouldAdjustPadding) {
                if (imageView.getPaddingTop() != 0) {
                    imageView.setPadding(0, 0, 0, 0);
                    imageView.setBackgroundResource(0);
                }
            }
            ImageUtil.showPicWithNoAnimate(imageView, uploadPhotoInfo.getPath(), new LoadFailCallback() {
                @Override
                public void onLoadFail(Exception e) {
                    deletePhoto(e.getMessage());
                }
            });
            ViewUtil.showProgressStatus(textView, uploadPhotoInfo.isShowProgress());
            textView.setText(uploadPhotoInfo.getProgress());
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChoosePosition = position;
                if (uploadPhotoInfo.getPath().equals("-1")) {
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

    public void conver(TextView textView, final UploadPhotoInfo uploadPhotoInfo) {
        ViewUtil.showProgressStatus(textView, uploadPhotoInfo.isShowProgress());
        textView.setText(uploadPhotoInfo.getProgress());
    }

    public void onDestroy() {
        mCustomDialog.cancel();
        IntentObserable.unregisterObserver(this);
        GlideApp.get(mActivity.getApplicationContext()).clearMemory();
        mActivity = null;
        cancelLoading();
    }

    @Override
    public void onReceive(Intent intent) {
        if (Objects.equals(intent.getAction(), ConstansUtil.PHOTO_IMAGE)) {
            ArrayList<String> list = intent.getStringArrayListExtra(ConstansUtil.INTENT_MESSAGE);
            showLoading();
            if (list.size() == 1) {
                ImageUtil.compressPhoto(mActivity, list.get(0), new SuccessCallback<MyFile>() {
                    @Override
                    public void onSuccess(MyFile file) {
                        dismissLoading();
                        handleCompressPhoto(file, mChoosePosition);
                    }
                });
            } else {
                handleImageBean(list);
            }
        }
    }

    public AD getAdapter() {
        return mAdapter;
    }

    public void setAdapter(AD adapter) {
        mAdapter = adapter;
    }

    public void setChoosePosition(int choosePosition) {
        mChoosePosition = choosePosition;
    }

    public void setMaxNum(int maxNum) {
        mMaxNum = maxNum;
    }

    public void setShouldAdjustPadding(boolean shouldAdjustPadding) {
        mShouldAdjustPadding = shouldAdjustPadding;
    }

    public void setTakePhotoRes(int takePhotoRes) {
        mTakePhotoRes = takePhotoRes;
    }

    public void setStartPadding(int startPadding) {
        mStartPadding = startPadding;
    }

    public void setTopPadding(int topPadding) {
        mTopPadding = topPadding;
    }

}
