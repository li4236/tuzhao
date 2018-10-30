package com.tianzhili.www.myselfsdk.photopicker.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Describe :
 * Email:baossrain99@163.com
 * Created by Rain on 17-5-3.
 */
public class FileUtils {

    private static String imagePath;

    /**
     * 创建保存的图片文件
     */
    public static File createImageFile(Context context, String dir) {
        File filesDir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //文件目录
            filesDir = context.getExternalFilesDir(dir).getAbsoluteFile();
        } else {
            filesDir = new File(Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + dir);
        }
        if (!filesDir.exists()) filesDir.mkdirs();
        File imageFile = new File(filesDir, createFileName());
        //image = File.createTempFile(createFileName(), ".png", fileDir);
        setImagePath(imageFile.getAbsolutePath());
        return imageFile;
    }

    /**
     * 设置创建图片的路径
     */
    private static String setImagePath(String path) {
        imagePath = path;
        return imagePath;
    }

    /**
     * 获取图片路径
     */
    public static String getImagePath() {
        return imagePath;
    }

    /**
     * 创建图片文件
     */
    private static String createFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return dateFormat.format(new Date()) + ".png";
    }

}