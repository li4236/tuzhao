package com.tuzhao.publicwidget.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.tuzhao.R;

/**
 * Created by juncoder on 2018/7/28.
 */
public class Spinner extends android.support.v7.widget.AppCompatTextView {

    private PopupWindow mPopupWindow;

    private ListView mListView;

    private ArrayAdapter<String> mAdapter;

    private AdapterView.OnItemClickListener mOnItemClickListener;

    private int mMaxWidth;

    public Spinner(Context context) {
        super(context);
        init();
    }

    public Spinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Spinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (mPopupWindow == null) {
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.popup_list_layout, null);
            mListView = linearLayout.findViewById(R.id.popup_lv);
            mPopupWindow = new PopupWindow(linearLayout, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setTouchable(true);

            linearLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                }
            });
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                    } else {
                        show();
                    }
                }
            });
        }
    }

    private void show() {
        if (getWidth() != mMaxWidth) {
            mMaxWidth = getWidth();
            mPopupWindow.setWidth(mMaxWidth);
        }
        mPopupWindow.showAsDropDown(this);
    }

    public void setAdapter(ArrayAdapter<String> adapter) {
        if (adapter != null) {
            mAdapter = adapter;
            mListView.setAdapter(adapter);
        }
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        if (onItemClickListener != null) {
            if (mListView != null && mAdapter != null) {
                mOnItemClickListener = onItemClickListener;
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mOnItemClickListener.onItemClick(parent, view, position, id);
                        mPopupWindow.dismiss();
                    }
                });
            }
        }
    }

}
