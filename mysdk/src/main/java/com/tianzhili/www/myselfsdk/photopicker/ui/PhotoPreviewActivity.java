package com.tianzhili.www.myselfsdk.photopicker.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.tianzhili.www.myselfsdk.R;
import com.tianzhili.www.myselfsdk.photopicker.BaseActivity;
import com.tianzhili.www.myselfsdk.photopicker.PhotoPick;
import com.tianzhili.www.myselfsdk.photopicker.bean.Photo;
import com.tianzhili.www.myselfsdk.photopicker.bean.PhotoPreviewBean;
import com.tianzhili.www.myselfsdk.photopicker.controller.PhotoPickConfig;
import com.tianzhili.www.myselfsdk.photopicker.controller.PhotoPreviewConfig;
import com.tianzhili.www.myselfsdk.photopicker.utils.UtilsHelper;
import com.tianzhili.www.myselfsdk.photopicker.weidget.HackyViewPager;

import java.util.ArrayList;


/**
 * Describe :仿微信图片预览
 * Email:baossrain99@163.com
 * Created by Rain on 17-5-3.
 */
public class PhotoPreviewActivity extends BaseActivity {

    private static final String TAG = "PhotoPreviewActivity";

    private ArrayList<Photo> photos;    //全部图片集合
    private ArrayList<String> selectPhotos;     //选中的图片集合
    private CheckBox checkbox;
    private RadioButton radioButton;
    private int pos;
    private int maxPickSize;            //最大选择个数
    private boolean isChecked = false;
    private boolean originalPicture;    //是否选择的是原图

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Bundle bundle = getIntent().getBundleExtra(PhotoPreviewConfig.EXTRA_BUNDLE);
        if (bundle == null) {
            throw new NullPointerException("bundle is null,please init it");
        }
        PhotoPreviewBean bean = bundle.getParcelable(PhotoPreviewConfig.EXTRA_BEAN);
        if (bean == null) {
            finish();
            return;
        }
        photos = bean.getPhotos();
        if (photos == null || photos.isEmpty()) {
            finish();
            return;
        }
        originalPicture = bean.isOriginalPicture();
        maxPickSize = bean.getMaxPickSize();
        selectPhotos = bean.getSelectPhotos();
        final int beginPosition = bean.getPosition();
        setContentView(R.layout.activity_photo_select);

        radioButton = findViewById(R.id.radioButton);
        checkbox = findViewById(R.id.checkbox);
        HackyViewPager viewPager = findViewById(R.id.pager);
        toolbar = findViewById(R.id.my_toolbar);
        toolbar.setBackgroundColor(PhotoPick.getToolbarBackGround());
        toolbar.setTitle((beginPosition + 1) + "/" + photos.size());
        setSupportActionBar(toolbar);

        //照片滚动监听，更改ToolBar数据
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pos = position;
                position++;
                toolbar.setTitle(position + "/" + photos.size());
                if (selectPhotos != null && selectPhotos.contains(photos.get(pos).getPath())) {
                    checkbox.setChecked(true);
                    if (pos == 1 && selectPhotos.contains(photos.get(pos - 1).getPath())) {
                        checkbox.setChecked(true);
                    }
                } else {
                    checkbox.setChecked(false);
                }
                if (originalPicture) {
                    radioButton.setText(getString(R.string.image_size, UtilsHelper.formatFileSize(photos.get(pos).getSize())));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //选中
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectPhotos == null) {
                    selectPhotos = new ArrayList<>();
                }
                String path = photos.get(pos).getPath();
                if (selectPhotos.contains(path)) {
                    selectPhotos.remove(path);
                    checkbox.setChecked(false);
                } else {
                    if (maxPickSize == selectPhotos.size()) {
                        checkbox.setChecked(false);
                        return;
                    }
                    selectPhotos.add(path);
                    checkbox.setChecked(true);
                }
                updateMenuItemTitle();
            }
        });

        //原图
        if (originalPicture) {
            radioButton.setText(getString(R.string.image_size, UtilsHelper.formatFileSize(photos.get(beginPosition).getSize())));
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isChecked) {
                        radioButton.setChecked(false);
                        isChecked = false;
                    } else {
                        radioButton.setChecked(true);
                        isChecked = true;
                    }
                }
            });
        } else {
            radioButton.setVisibility(View.GONE);
        }

        viewPager.setAdapter(new ImagePagerAdapter());
        viewPager.setCurrentItem(beginPosition);
    }

    private void updateMenuItemTitle() {
        if (selectPhotos.isEmpty()) {
            menuItem.setTitle(R.string.send);
        } else {
            menuItem.setTitle("发送(" + selectPhotos.size() + "/" + maxPickSize+")");
        }
    }

    private MenuItem menuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ok, menu);
        menuItem = menu.findItem(R.id.ok);
        updateMenuItemTitle();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ok && !selectPhotos.isEmpty()) {
            Intent intent = new Intent();
            switch (getIntent().getIntExtra(PhotoPickConfig.REQUEST_CODE, 0)) {
                case PhotoPickConfig.PICK_SINGLE_REQUEST_CODE:
                    intent.putExtra(PhotoPickConfig.EXTRA_SINGLE_PHOTO, selectPhotos.get(0));
                    break;
                case PhotoPickConfig.PICK_MORE_REQUEST_CODE:
                    intent.putStringArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST, selectPhotos);
                    break;
            }
            setResult(Activity.RESULT_OK, intent);
            finish();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            backTo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void backTo() {
        Intent intent = new Intent();
        intent.putExtra("isBackPressed", true);
        intent.putStringArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST, selectPhotos);
        intent.putExtra(PhotoPreviewConfig.EXTRA_ORIGINAL_PIC, originalPicture);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        backTo();
        super.onBackPressed();
    }

    private boolean toolBarStatus = true;

    //隐藏ToolBar
    private void hideViews() {
        toolBarStatus = false;
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    //显示ToolBar
    private void showViews() {
        toolBarStatus = true;
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return photos.size();
        }

        @NonNull
        @Override
        public View instantiateItem(@NonNull ViewGroup container, int position) {
            String bigImgUrl = photos.get(position).getPath();
            final PhotoView photoView = new PhotoView(PhotoPreviewActivity.this);
            container.addView(photoView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            photoView.setOnViewTapListener(new OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    finish();
                }
            });
            PhotoPickConfig.imageLoader.displayImage(PhotoPreviewActivity.this, bigImgUrl, photoView, false);
            return photoView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.image_pager_exit_animation);
    }

}

/*
 *   ┏┓　　　┏┓
 * ┏┛┻━━━┛┻┓
 * ┃　　　　　　　┃
 * ┃　　　━　　　┃
 * ┃　┳┛　┗┳　┃
 * ┃　　　　　　　┃
 * ┃　　　┻　　　┃
 * ┃　　　　　　　┃
 * ┗━┓　　　┏━┛
 *     ┃　　　┃
 *     ┃　　　┃
 *     ┃　　　┗━━━┓
 *     ┃　　　　　　　┣┓
 *     ┃　　　　　　　┏┛
 *     ┗┓┓┏━┳┓┏┛
 *       ┃┫┫　┃┫┫
 *       ┗┻┛　┗┻┛
 *        神兽保佑
 *        代码无BUG!
 */
