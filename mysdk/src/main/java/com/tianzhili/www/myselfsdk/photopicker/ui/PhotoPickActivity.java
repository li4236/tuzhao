package com.tianzhili.www.myselfsdk.photopicker.ui;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.tianzhili.www.myselfsdk.R;
import com.tianzhili.www.myselfsdk.photopicker.BaseActivity;
import com.tianzhili.www.myselfsdk.photopicker.PhotoGalleryAdapter;
import com.tianzhili.www.myselfsdk.photopicker.PhotoPick;
import com.tianzhili.www.myselfsdk.photopicker.PhotoPickAdapter;
import com.tianzhili.www.myselfsdk.photopicker.bean.Photo;
import com.tianzhili.www.myselfsdk.photopicker.bean.PhotoDirectory;
import com.tianzhili.www.myselfsdk.photopicker.bean.PhotoPickBean;
import com.tianzhili.www.myselfsdk.photopicker.controller.PhotoPickConfig;
import com.tianzhili.www.myselfsdk.photopicker.controller.PhotoPreviewConfig;
import com.tianzhili.www.myselfsdk.photopicker.loader.MediaStoreHelper;
import com.tianzhili.www.myselfsdk.photopicker.utils.FileUtils;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * Descriptions :照片选择器
 * GitHub : https://github.com/Rain0413
 * Blog   : http://blog.csdn.net/sinat_33680954
 * Created by Rain on 16-12-7.
 */
public class PhotoPickActivity extends BaseActivity {

    private static final String TAG = "PhotoPickActivity";

    //权限相关
    public static final int REQUEST_CODE_SDCARD = 100;             //读写权限请求码
    public static final int REQUEST_CODE_CAMERA = 200;             //拍照权限请求码

    public static final int REQUEST_CODE_SHOW_CAMERA = 0;// 拍照
    public static final int REQUEST_CODE_CLIP = 1;//裁剪头像

    private SlidingUpPanelLayout slidingUpPanelLayout;
    private PhotoGalleryAdapter galleryAdapter;
    private PhotoPickAdapter adapter;
    private PhotoPickBean pickBean;
    private Uri cameraUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pick, true);

        Bundle bundle = getIntent().getBundleExtra(PhotoPickConfig.EXTRA_PICK_BUNDLE);
        if (bundle == null) {
            throw new NullPointerException("bundle is null,please init it");
        }
        pickBean = bundle.getParcelable(PhotoPickConfig.EXTRA_PICK_BEAN);
        if (pickBean == null) {
            finish();
            return;
        }

        //申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermission();
        else init();
    }

    /**
     * 初始化控件
     */
    private void init() {
        PhotoPick.init(this);

        //设置ToolBar
        toolbar.setTitle(R.string.select_photo);
        toolbar.setBackgroundColor(PhotoPick.getToolbarBackGround());

        //全部相册照片列表
        RecyclerView recyclerView = this.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, pickBean.getSpanCount()));
        adapter = new PhotoPickAdapter(this, pickBean, getIntent().getIntExtra(PhotoPickConfig.REQUEST_CODE, 0));
        recyclerView.setAdapter(adapter);

        //相册列表
        RecyclerView gallery_rv = this.findViewById(R.id.gallery_rcl);
        gallery_rv.setLayoutManager(new LinearLayoutManager(this));
        galleryAdapter = new PhotoGalleryAdapter(this);
        gallery_rv.setAdapter(galleryAdapter);

        //当选择照片的时候更新toolbar的标题
        adapter.setOnUpdateListener(new PhotoPickAdapter.OnUpdateListener() {
            @Override
            public void updateToolBarTitle(String title) {
                toolbar.setTitle(title);
            }
        });

        //相册列表item选择的时候关闭slidingUpPanelLayout并更新照片adapter
        galleryAdapter.setOnItemClickListener(new PhotoGalleryAdapter.OnItemClickListener() {
            @Override
            public void onClick(List<Photo> photos) {
                if (adapter != null) {
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    adapter.refresh(photos);
                }
            }
        });
        //获取全部照片
        MediaStoreHelper.getPhotoDirs(this, new MediaStoreHelper.PhotosResultCallback() {
            @Override
            public void onResultCallback(final List<PhotoDirectory> directories) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.refresh(directories.get(0).getPhotos());
                        galleryAdapter.refresh(directories);
                    }
                });
            }
        });

        slidingUpPanelLayout = this.findViewById(R.id.slidingUpPanelLayout);
        slidingUpPanelLayout.setAnchorPoint(0.5f);
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            }
        });
        slidingUpPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

    }

    //请求权限(先检查)
    private void requestPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_SDCARD);
        } else {
            init();
        }
    }


    //权限申请回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_SDCARD) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("温馨提示");
                builder.setMessage(getString(R.string.permission_tip_SD));
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        } else if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectPicFromCamera();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("温馨提示");
                builder.setMessage(getString(R.string.permission_tip_video));
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        }
    }


    /**
     * 启动Camera拍照
     */
    public void selectPicFromCamera() {
        if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, R.string.cannot_take_pic, Toast.LENGTH_SHORT).show();
            return;
        }
        // 直接将拍到的照片存到手机默认的文件夹
       /* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        cameraUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);*/

        //保存到自定义目录

        File imageFile = FileUtils.createImageFile(this, "/images");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Android7.0以上URI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //通过FileProvider创建一个content类型的Uri
            cameraUri = FileProvider.getUriForFile(this, "com.tuzhao.photopicker.provider", imageFile);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件,私有目录读写权限
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            cameraUri = Uri.fromFile(imageFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, PhotoPickActivity.REQUEST_CODE_SHOW_CAMERA);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!pickBean.isClipPhoto()) {
            getMenuInflater().inflate(R.menu.menu_ok, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ok) {
            if (adapter != null && !adapter.getSelectPhotos().isEmpty()) {
                Intent intent = new Intent();
                switch (getIntent().getIntExtra(PhotoPickConfig.REQUEST_CODE, 0)) {
                    case PhotoPickConfig.PICK_SINGLE_REQUEST_CODE:
                        intent.putExtra(PhotoPickConfig.EXTRA_SINGLE_PHOTO, adapter.getSelectPhotos().get(0));
                        setResult(Activity.RESULT_OK, intent);
                        break;
                    case PhotoPickConfig.PICK_MORE_REQUEST_CODE:
                        intent.putStringArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST, adapter.getSelectPhotos());
                        setResult(Activity.RESULT_OK, intent);
                        break;
                }
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout != null &&
                (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                        slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_SHOW_CAMERA://相机
                findPhoto(adapter.getCameraUri());
                break;
            case UCrop.REQUEST_CROP:    //裁剪
                findClipPhoto(UCrop.getOutput(data));
                break;
            case UCrop.RESULT_ERROR:
                Throwable cropError = UCrop.getError(data);
                break;
            case PhotoPreviewConfig.REQUEST_CODE:
                boolean isBackPressed = data.getBooleanExtra("isBackPressed", false);
                if (!isBackPressed) {//如果上个activity不是按了返回键的，就是按了"发送"按钮
                    setResult(Activity.RESULT_OK, data);
                    finish();
                } else {//用户按了返回键，合并用户选择的图片集合
                    ArrayList<String> photoLists = data.getStringArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST);
                    if (photoLists == null) {
                        return;
                    }
                    ArrayList<String> selectedList = adapter.getSelectPhotos();//之前已经选了的图片
                    List<String> deleteList = new ArrayList<>();//这是去图片预览界面需要删除的图片
                    for (String s : selectedList) {
                        if (!photoLists.contains(s)) {
                            deleteList.add(s);
                        }
                    }
                    selectedList.removeAll(deleteList);//删除预览界面取消选择的图片
                    deleteList.clear();
                    //合并相同的数据
                    HashSet<String> set = new HashSet<>(photoLists);
                    set.addAll(selectedList);
                    selectedList.clear();
                    selectedList.addAll(set);
                    toolbar.setTitle(adapter.getTitle());
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private void findClipPhoto(Uri uri) {
        Intent intent = new Intent();
        intent.putExtra(PhotoPickConfig.EXTRA_CLIP_PHOTO, uri.toString());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void findPhoto(Uri imageUri) {
        // String filePath = UtilsHelper.getRealPathFromURI(imageUri, this);
        if (imageUri == null) {
            Toast.makeText(this, R.string.unable_find_pic, Toast.LENGTH_LONG).show();
        } else {
            if (pickBean.isClipPhoto()) {//拍完照之后，如果要启动裁剪，则去裁剪再把地址传回来
                adapter.startClipPic(FileUtils.getImagePath());
            } else {
                Intent intent = new Intent();
                intent.putExtra(PhotoPickConfig.EXTRA_SINGLE_PHOTO, FileUtils.getImagePath());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.getImageLoader().clearMemoryCache();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.image_pager_exit_animation);
    }

}
