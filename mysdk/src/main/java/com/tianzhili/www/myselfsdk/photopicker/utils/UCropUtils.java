package com.tianzhili.www.myselfsdk.photopicker.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;

import com.tianzhili.www.myselfsdk.photopicker.PhotoPick;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;


/**
 * Created by Administrator on 2017/5/3 0003.
 */
public class UCropUtils {
    public static void start(Activity mActivity, File sourceFile, File destinationFile, boolean showClipCircle) {
        UCrop uCrop = UCrop.of(Uri.fromFile(sourceFile), Uri.fromFile(destinationFile));
        //.withAspectRatio(aspectRatioX, aspectRatioY)  //动态的设置图片的宽高比

        UCrop.Options options = new UCrop.Options();
        options.useSourceImageAspectRatio();                        //设置为图片原始宽高比列一样
        //options.withMaxResultSize(500, 500);           //设置将被载入裁剪图片的最大尺寸
        //设置裁剪图片可操作的手势
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        options.setCompressionQuality(100);                         //设置裁剪的图片质量
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);    //设置裁剪出来图片的格式
        options.setFreeStyleCropEnabled(true);                     //可以调整裁剪框
        if (showClipCircle) {
            options.setCircleDimmedLayer(true);                     //设置裁剪框圆形
            options.setShowCropFrame(false);                        //设置是否展示矩形裁剪框
            options.setShowCropGrid(false);                         //是否显示裁剪框网格
        } else {
            options.setShowCropFrame(true);                        //设置是否展示矩形裁剪框
            options.setShowCropGrid(true);
        }
        options.setToolbarColor(PhotoPick.getToolbarBackGround());
        options.setStatusBarColor(PhotoPick.getToolbarBackGround());

        uCrop.withOptions(options);
        uCrop.start(mActivity);
    }
}
