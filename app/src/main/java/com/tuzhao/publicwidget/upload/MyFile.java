package com.tuzhao.publicwidget.upload;

import android.support.annotation.NonNull;

import java.io.File;

/**
 * Created by juncoder on 2018/8/15.
 */
public class MyFile extends File {

    private String uncompressName = "-1";

    public MyFile(@NonNull String pathname) {
        super(pathname);
    }

    public String getUncompressName() {
        return uncompressName;
    }

    public MyFile setUncompressName(String uncompressName) {
        this.uncompressName = uncompressName;
        return this;
    }

}
