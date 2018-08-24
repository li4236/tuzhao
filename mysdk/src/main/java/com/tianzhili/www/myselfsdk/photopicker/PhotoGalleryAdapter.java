package com.tianzhili.www.myselfsdk.photopicker;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.R;
import com.tianzhili.www.myselfsdk.photopicker.bean.Photo;
import com.tianzhili.www.myselfsdk.photopicker.bean.PhotoDirectory;
import com.tianzhili.www.myselfsdk.photopicker.controller.PhotoPickConfig;
import com.tianzhili.www.myselfsdk.photopicker.weidget.GalleryImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe : 相册列表展示
 * Created by Rain on 17-4-28.
 */
public class PhotoGalleryAdapter extends RecyclerView.Adapter {

    private Context context;
    private int selected;
    private List<PhotoDirectory> directories = new ArrayList<>();
    private int imageSize;

    public PhotoGalleryAdapter(Context context) {
        this.context = context;
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        this.imageSize = metrics.widthPixels / 6;
    }

    public void refresh(List<PhotoDirectory> directories) {
        this.directories.clear();
        this.directories.addAll(directories);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_gallery, parent, false);
        return new PhotoGalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((PhotoGalleryViewHolder) holder).showData(getItem(position), position);
    }

    @Override
    public int getItemCount() {
        return directories.size();
    }

    private PhotoDirectory getItem(int position) {
        return this.directories.get(position);
    }

    private void changeSelect(int position) {
        this.selected = position;
        notifyDataSetChanged();
    }

    private class PhotoGalleryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private GalleryImageView imageView;
        private ImageView photo_gallery_select;
        private TextView name, num;

        PhotoGalleryViewHolder(View view) {
            super(view);
            imageView = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.name);
            num = itemView.findViewById(R.id.num);
            photo_gallery_select = itemView.findViewById(R.id.photo_gallery_select);
            imageView.getLayoutParams().height = imageSize;
            imageView.getLayoutParams().width = imageSize;
            itemView.setOnClickListener(this);
        }

        void showData(PhotoDirectory directory, int position) {
            if (directory == null || directory.getCoverPath() == null) {
                return;
            }
            if (selected == position) {
                photo_gallery_select.setImageResource(R.mipmap.select_icon);
            } else {
                photo_gallery_select.setImageBitmap(null);
            }
            name.setText(directory.getName());
            num.setText(context.getString(R.string.gallery_num, String.valueOf(directory.getPhotoPaths().size())));
            if (PhotoPickConfig.imageLoader != null) {
                PhotoPickConfig.imageLoader.displayImage(context, directory.getCoverPath(), imageView, true);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (view.getId() == R.id.photo_gallery_rl) {
                if (onItemClickListener != null) {
                    changeSelect(position);
                    onItemClickListener.onClick(getItem(position).getPhotos());
                }
            }
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(List<Photo> photos);
    }
}
