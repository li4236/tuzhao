package com.tuzhao.publicwidget.swipetoloadlayout;

import android.support.v7.widget.RecyclerView;

/**
 * Created by TZL12 on 2017/5/20.
 */

public interface ChangeScrollCallback {
    public void scroll(RecyclerView recyclerView, int dx, int dy);
}
