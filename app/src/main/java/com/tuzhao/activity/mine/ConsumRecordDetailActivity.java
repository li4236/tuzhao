package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.info.ConsumRecordInfo;

/**
 * Created by TZL12 on 2017/10/21.
 */

public class ConsumRecordDetailActivity extends BaseActivity {

    private ConsumRecordInfo consumrecord_info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumrecorddetail_layout);

        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件
        setStyle(true);
    }

    private void initView() {
    }

    private void initData() {

        if (getIntent().hasExtra("consumrecord_info")) {
            consumrecord_info = (ConsumRecordInfo) getIntent().getSerializableExtra("consumrecord_info");

            try {
                switch (Integer.parseInt(consumrecord_info.getType())) {
                    case 1:
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_fee)).setText("+"+consumrecord_info.getIncome_amount());
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_n1)).setText("出租车位");
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_n2)).setText("出租时间");
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_n3)).setText("到账时间");
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_n4)).setText("到账方式");
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_place)).setText(consumrecord_info.getPark_name());
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_time1)).setText(consumrecord_info.getPark_time());
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_time2)).setText(consumrecord_info.getIncome_time());
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_way)).setText("余额");
                        break;
                    case 2:
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_fee)).setText("-"+consumrecord_info.getActual_pay_fee());
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_n1)).setText("停车场");
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_n2)).setText("停车时间");
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_n3)).setText("付款时间");
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_n4)).setText("付款方式");
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_place)).setText(consumrecord_info.getConsume_describe());
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_time1)).setText(consumrecord_info.getPark_time());
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_time2)).setText(consumrecord_info.getPay_time());
                        String[] aaa = consumrecord_info.getPay_way().split(",");
                        String way = "";
                        if (aaa.length>1){
                            for (int i = 0; i<aaa.length;i++){
                                if (i==0){
                                    switch (Integer.parseInt(aaa[0])){
                                        case 1:
                                            way = "支付宝";
                                            break;
                                        case 2:
                                            way = "微信";
                                            break;
                                        case 3:
                                            way = "余额";
                                            break;
                                    }
                                }else if (i == 1){
                                    switch (Integer.parseInt(aaa[0])){
                                        case 1:
                                            way = way + " + 支付宝";
                                            break;
                                        case 2:
                                            way = " + 微信";
                                            break;
                                        case 3:
                                            way = " + 余额";
                                            break;
                                    }
                                }

                            }
                        }
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_way)).setText(way);
                        break;
                    case 3:
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_fee)).setText("-"+consumrecord_info.getAmount());
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_n1)).setText("提现金额");
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_n2)).setText("提现时间");
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_n3)).setText("到账时间");
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_n4)).setText("到账账户");
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_place)).setText(consumrecord_info.getAmount()+"元");
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_time1)).setText(consumrecord_info.getApplymoney_time());
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_time2)).setText(consumrecord_info.getGetmoney_time());
                        ((TextView) findViewById(R.id.id_activity_consumrecorddetail_layout_textview_way)).setText("支付宝");
                        break;
                }
            } catch (Exception e) {
            }
        } else {
            finish();
        }
    }

    private void initEvent() {

        findViewById(R.id.id_activity_consumrecorddetail_layout_imageview_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
