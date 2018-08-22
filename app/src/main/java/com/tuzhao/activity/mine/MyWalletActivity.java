package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.util.Objects;

/**
 * Created by TZL12 on 2017/11/17.
 */

public class MyWalletActivity extends BaseActivity implements IntentObserver {

    private ImageView imageview_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mywallet_layout);

        initView();//初始化控件
        initEvent();//初始化事件
        setStyle(true);
        IntentObserable.registerObserver(this);
    }

    private void initView() {

        imageview_back = findViewById(R.id.id_activity_mywallet_imageview_back);
        ((TextView) findViewById(R.id.id_activity_mywallet_layout_textview_yue)).setText(UserManager.getInstance().getUserInfo().getBalance());
    }

    private void initEvent() {
        imageview_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.id_activity_mywallet_layout_linearlayout_yue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyWalletActivity.this, MyBalanceActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.id_activity_mywallet_layout_linearlayout_discount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyWalletActivity.this, DiscountActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.id_activity_mywallet_layout_linearlayout_invoice_situation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyWalletActivity.this, InvoiceSituationActivity.class));
            }
        });

        findViewById(R.id.id_activity_mywallet_layout_linearlayout_monthly_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyWalletActivity.this, MyMonthlyCardActivity.class));
            }
        });

        findViewById(R.id.id_activity_mywallet_layout_linearlayout_bill).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyWalletActivity.this, BillActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.id_activity_mywallet_layout_linearlayout_invoice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyWalletActivity.this, InvoiceReimbursementActivity.class));
            }
        });
    }

    @Override
    public void onReceive(Intent intent) {
        if (Objects.equals(intent.getAction(), ConstansUtil.WITHDRAWL_SUCCESS)) {
            ((TextView) findViewById(R.id.id_activity_mywallet_layout_textview_yue)).setText(UserManager.getInstance().getUserInfo().getBalance());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
    }

}
