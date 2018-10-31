package com.tuzhao.publicwidget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tuzhao.R;

/**
 * Created by juncoder on 2018/10/12.
 */
public class SelectDialog extends Dialog {

    public SelectDialog(@NonNull Context context, String[] content, AdapterView.OnItemClickListener onItemClickListener) {
        super(context, R.style.CustomDialogStyle);
        View view = getLayoutInflater().inflate(R.layout.dialog_list_cancel_layout, null);
        ListView listView = view.findViewById(R.id.dialog_lv);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.item_center_text_blue2_layout, content);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onItemClickListener);

        view.findViewById(R.id.cancel_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        setContentView(view);
        setCanceledOnTouchOutside(false);

        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.SlideAnimationStyle);
        }
    }

}
