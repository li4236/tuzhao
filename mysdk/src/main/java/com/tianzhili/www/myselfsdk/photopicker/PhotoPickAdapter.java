package com.tianzhili.www.myselfsdk.photopicker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.tianzhili.www.myselfsdk.R;
import com.tianzhili.www.myselfsdk.photopicker.bean.Photo;
import com.tianzhili.www.myselfsdk.photopicker.bean.PhotoPickBean;
import com.tianzhili.www.myselfsdk.photopicker.controller.PhotoPickConfig;
import com.tianzhili.www.myselfsdk.photopicker.controller.PhotoPreviewConfig;
import com.tianzhili.www.myselfsdk.photopicker.loader.ImageLoader;
import com.tianzhili.www.myselfsdk.photopicker.ui.PhotoPickActivity;
import com.tianzhili.www.myselfsdk.photopicker.utils.FileUtils;
import com.tianzhili.www.myselfsdk.photopicker.utils.UCropUtils;
import com.tianzhili.www.myselfsdk.photopicker.weidget.GalleryImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Describe : 本地所有照片列表
 * Created by Rain on 17-4-28.
 */
public class PhotoPickAdapter extends RecyclerView.Adapter<PhotoPickAdapter.PhotoPickViewHolder> {

    private static final String TAG = "PhotoPickAdapter";

    private Context context;
    private ArrayList<Photo> photos = new ArrayList<>();
    private ArrayList<String> selectPhotos = new ArrayList<>();
    private int requestCode;
    private int maxPickSize;
    private int pickMode;
    private int imageSize;
    private boolean clipCircle;
    private boolean showCamera;
    private boolean isClipPhoto;
    private boolean isOriginalPicture;
    private ImageLoader imageLoader;
    private Uri cameraUri;

    public PhotoPickAdapter(Context context, PhotoPickBean pickBean,int requestCode) {
        this.context = context;
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        this.imageSize = metrics.widthPixels / pickBean.getSpanCount();
        this.pickMode = pickBean.getPickMode();
        this.maxPickSize = pickBean.getMaxPickSize();
        this.clipCircle = pickBean.getClipMode();
        this.showCamera = pickBean.isShowCamera();
        this.isClipPhoto = pickBean.isClipPhoto();
        this.isOriginalPicture = pickBean.isOriginalPicture();
        this.imageLoader = pickBean.getImageLoader();
        this.requestCode = requestCode;
    }

    public void refresh(List<Photo> photos) {
        this.photos.clear();
        this.photos.addAll(photos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhotoPickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_pick, parent, false);
        return new PhotoPickViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoPickViewHolder holder, int position) {
        holder.showData(position);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoPickViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            holder.checkbox.setChecked(selectPhotos.contains(getItem(position).getPath()));
        }
    }

    @Override
    public int getItemCount() {
        return showCamera ? (photos == null ? 0 : photos.size() + 1) : (photos == null ? 0 : photos.size());
    }

    private Photo getItem(int position) {
        return showCamera ? photos.get(position - 1) : photos.get(position);
    }

    class PhotoPickViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private GalleryImageView imageView;
        private CheckBox checkbox;

        PhotoPickViewHolder(View view) {
            super(view);
            imageView = itemView.findViewById(R.id.imageView);
            checkbox = itemView.findViewById(R.id.checkbox);
            imageView.getLayoutParams().height = imageSize;
            imageView.getLayoutParams().width = imageSize;
            checkbox.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        void showData(int position) {
            if (showCamera && position == 0) {
                checkbox.setVisibility(View.GONE);
                imageView.setImageResource(R.mipmap.take_photo);
            } else {
                Photo photo = getItem(position);
                if (isClipPhoto) {
                    checkbox.setVisibility(View.GONE);
                } else {
                    checkbox.setVisibility(View.VISIBLE);
                    checkbox.setChecked(selectPhotos.contains(photo.getPath()));
                }
                String url = photo.getPath();
                imageLoader.displayImage(context, url, imageView, true);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (view.getId() == R.id.checkbox) {
                if (selectPhotos.contains(getItem(position).getPath())) {
                    //取消之前选的
                    checkbox.setChecked(false);
                    selectPhotos.remove(getItem(position).getPath());
                } else {
                    if (pickMode == PhotoPickConfig.MODE_PICK_SINGLE) {
                        if (!selectPhotos.isEmpty()) {
                            //如果是单选的话则把上一次的取消，选择新的
                            int lastSelectPosition = photos.indexOf(new Photo(selectPhotos.get(0)));
                            if (showCamera) {
                                lastSelectPosition += 1;
                            }
                            selectPhotos.clear();
                            notifyItemChanged(lastSelectPosition, lastSelectPosition);
                            checkbox.setChecked(true);
                            selectPhotos.add(getItem(position).getPath());
                        } else {
                            checkbox.setChecked(true);
                            selectPhotos.add(getItem(position).getPath());
                        }
                    } else {
                        if (selectPhotos.size() == maxPickSize) {
                            checkbox.setChecked(false);
                            Toast.makeText(context, "最多只能选择" + maxPickSize + "张哦", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            checkbox.setChecked(true);
                            selectPhotos.add(getItem(position).getPath());
                        }
                    }
                }
                if (onUpdateListener != null) {
                    onUpdateListener.updateToolBarTitle(getTitle());
                }
            } else if (view.getId() == R.id.photo_pick_rl) {
                if (showCamera && position == 0) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        //申请权限
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, PhotoPickActivity.REQUEST_CODE_CAMERA);
                    } else {
                        selectPicFromCamera();
                    }
                } else if (isClipPhoto) {
                    //头像裁剪
                    startClipPic(getItem(position).getPath());
                } else {
                    //查看大图
                    new PhotoPreviewConfig.Builder((Activity) context)
                            .setPosition(showCamera ? position - 1 : position)
                            .setMaxPickSize(maxPickSize)
                            .setPhotos(photos)
                            .setSelectPhotos(selectPhotos)
                            .setOriginalPicture(isOriginalPicture)
                            .setRequestCode(requestCode)
                            .build();
                }
            }
        }
    }

    /**
     * 裁剪图片
     */
    public void startClipPic(String picPath) {
        File corpFile = FileUtils.createImageFile(context, "/clip");
        UCropUtils.start((Activity) context, new File(picPath), corpFile, clipCircle);
    }

    /**
     * 启动Camera拍照
     */
    private void selectPicFromCamera() {
        if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, R.string.cannot_take_pic, Toast.LENGTH_SHORT).show();
            return;
        }
        // 直接将拍到的照片存到手机默认的文件夹
/*        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        cameraUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        ((Activity) context).startActivityForResult(intent, PhotoPickActivity.REQUEST_CODE_SHOW_CAMERA);*/

        //保存到自定义目录

        File imageFile = FileUtils.createImageFile(context, "/images");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Android7.0以上URI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //通过FileProvider创建一个content类型的Uri
            cameraUri = FileProvider.getUriForFile(context, "com.tuzhao.photopicker.provider", imageFile);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件,私有目录读写权限
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            cameraUri = Uri.fromFile(imageFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        ((Activity) context).startActivityForResult(intent, PhotoPickActivity.REQUEST_CODE_SHOW_CAMERA);
    }

    public Uri getCameraUri() {
        return cameraUri;
    }

    //如果是多选title才会变化，要不然单选的没有变
    public String getTitle() {
        String title = context.getString(R.string.select_photo);
        if (pickMode == PhotoPickConfig.MODE_PICK_MORE && selectPhotos.size() >= 1) {//不是单选，更新title
            title = selectPhotos.size() + "/" + maxPickSize;
        }
        return title;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    /**
     * 获取已经选择了的图片
     *
     * @return selected photos
     */
    public ArrayList<String> getSelectPhotos() {
        return selectPhotos;
    }

    private OnUpdateListener onUpdateListener;

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    public interface OnUpdateListener {
        void updateToolBarTitle(String title);
    }

}
