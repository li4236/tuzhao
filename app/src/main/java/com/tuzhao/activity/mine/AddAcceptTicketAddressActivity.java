package com.tuzhao.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.pickerview.OptionsPickerView;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.AcceptTicketAddressInfo;
import com.tuzhao.info.CityInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.CityUtil;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/3/30.
 */

public class AddAcceptTicketAddressActivity extends BaseStatusActivity implements View.OnClickListener {

    private TextView mTicketType;

    private EditText mCompanyName;

    private EditText mCompanyTelephone;

    private EditText mAcceptPersonName;

    private EditText mAcceptEmail;

    private EditText mAcceptTelephone;

    private TextView mAcceptArea;

    private EditText mAcceptDetailAddress;

    private EditText mTaxNumber;

    private EditText mBank;

    private EditText mBankNumber;

    private ConstraintLayout mAcceptEmailCl;

    private ConstraintLayout mAcceptAddressCl;

    private ConstraintLayout mAcceptBankCl;

    private ArrayList<String> mTicketTypes;

    private OptionsPickerView<String> mTicketTypeOption;

    private ArrayList<String> mProvinces;

    private ArrayList<ArrayList<String>> mCitys;

    private ArrayList<ArrayList<ArrayList<String>>> mCounties;

    private OptionsPickerView<String> mCityOption;

    @Override

    protected int resourceId() {
        return R.layout.activity_add_accept_ticket_address_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mTicketType = findViewById(R.id.ticket_type);
        mCompanyName = findViewById(R.id.company_name);
        mCompanyTelephone = findViewById(R.id.company_telephone);
        mAcceptPersonName = findViewById(R.id.accept_person_name);
        mAcceptTelephone = findViewById(R.id.accept_ticket_telephone);
        mAcceptEmail = findViewById(R.id.accept_ticket_email);
        mAcceptArea = findViewById(R.id.accept_ticket_area);
        mAcceptDetailAddress = findViewById(R.id.accept_ticket_detail_address);
        mTaxNumber = findViewById(R.id.accept_ticket_tax_number);
        mBank = findViewById(R.id.accept_ticket_bank);
        mBankNumber = findViewById(R.id.accept_ticket_bank_number);

        mAcceptEmailCl = findViewById(R.id.add_accept_ticket_address_email_cl);
        mAcceptAddressCl = findViewById(R.id.add_accept_ticket_address_address_cl);
        mAcceptBankCl = findViewById(R.id.add_accept_ticket_address_bank_cl);

        mTicketType.setOnClickListener(this);
        mAcceptArea.setOnClickListener(this);
        findViewById(R.id.save_accept_ticket_address).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        initTicketTypes();
        initCityOption();
        dismmisLoadingDialog();
    }

    @NonNull
    @Override
    protected String title() {
        return "新建收票地址";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ticket_type:
                closeKeyboard();
                mTicketTypeOption.show();
                break;
            case R.id.accept_ticket_area:
                closeKeyboard();
                mCityOption.show();
                break;
            case R.id.save_accept_ticket_address:
                if (isEmpty(mTicketType)) {
                    showToast(mTicketType);
                    closeKeyboard();
                    mTicketTypeOption.show();
                } else if (allNoEmpty()) {
                    uploadNewAddress();
                }
                break;
        }
    }

    /**
     * 初始化发票类型选择器
     */
    private void initTicketTypes() {
        mTicketTypes = new ArrayList<>();
        mTicketTypes.add("电子");
        mTicketTypes.add("普票");
        mTicketTypes.add("专票");

        mTicketTypeOption = new OptionsPickerView<>(this);
        mTicketTypeOption.setPicker(mTicketTypes);
        mTicketTypeOption.setCyclic(false);
        mTicketTypeOption.setSelectOptions(0);
        mTicketTypeOption.setTextSize(16);
        mTicketTypeOption.setLabels(null);
        mTicketTypeOption.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                if (options1 == 0) {
                    showEmail();
                } else if (options1 == 1) {
                    showAddress();
                } else {
                    showBank();
                }
                mTicketType.setText(mTicketTypes.get(options1));
            }
        });
    }

    /**
     * 初始化城市列表选择器
     */
    private void initCityOption() {
        ArrayList<CityInfo> cityInfoArrayList = CityUtil.loadCityData(this);
        mProvinces = new ArrayList<>(cityInfoArrayList.size());
        mCitys = new ArrayList<>(cityInfoArrayList.size());
        mCounties = new ArrayList<>(cityInfoArrayList.size());

        ArrayList<String> citys;
        ArrayList<String> counties;
        ArrayList<ArrayList<String>> countyList;

        for (CityInfo cityInfo : cityInfoArrayList) {
            //省
            mProvinces.add(cityInfo.getName());

            citys = new ArrayList<>(cityInfo.getCityList().size());
            countyList = new ArrayList<>(cityInfo.getCityList().size());

            for (CityInfo.CityListBeanX city : cityInfo.getCityList()) {
                //市
                citys.add(city.getName());
                counties = new ArrayList<>(city.getCityList().size());
                for (CityInfo.CityListBeanX.CityListBean county : city.getCityList()) {
                    //区
                    counties.add(county.getName());
                }
                countyList.add(counties);
            }

            mCitys.add(citys);
            mCounties.add(countyList);
        }

        mCityOption = new OptionsPickerView<>(this);
        mCityOption.setPicker(mProvinces, mCitys, mCounties, true);
        mCityOption.setTextSize(16);
        mCityOption.setCyclic(false);
        mCityOption.setSelectOptions(0, 0, 0);
        mCityOption.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                String area = mProvinces.get(options1) + mCitys.get(options1).get(option2) + mCounties.get(options1).get(option2).get(options3);
                mAcceptArea.setText(area);
            }
        });
    }

    /**
     * 显示电子邮箱布局
     */
    private void showEmail() {
        if (mAcceptEmailCl.getVisibility() == View.GONE) {
            mAcceptEmailCl.setVisibility(View.VISIBLE);
        }

        if (mAcceptBankCl.getVisibility() != View.GONE) {
            mAcceptBankCl.setVisibility(View.GONE);
        }

        if (mAcceptAddressCl.getVisibility() != View.GONE) {
            mAcceptAddressCl.setVisibility(View.GONE);
        }
    }

    /**
     * 显示收件地址布局
     */
    private void showAddress() {
        if (mAcceptEmailCl.getVisibility() != View.GONE) {
            mAcceptEmailCl.setVisibility(View.GONE);
        }

        if (mAcceptAddressCl.getVisibility() == View.GONE) {
            mAcceptAddressCl.setVisibility(View.VISIBLE);
        }

        if (mAcceptBankCl.getVisibility() != View.GONE) {
            mAcceptBankCl.setVisibility(View.GONE);
        }
    }

    /**
     * 显示银行相关布局
     */
    private void showBank() {
        if (mAcceptEmailCl.getVisibility() != View.GONE) {
            mAcceptEmailCl.setVisibility(View.GONE);
        }

        if (mAcceptAddressCl.getVisibility() == View.GONE) {
            mAcceptAddressCl.setVisibility(View.VISIBLE);
        }

        if (mAcceptBankCl.getVisibility() == View.GONE) {
            mAcceptBankCl.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(TextView textView) {
        showFiveToast(textView.getHint().toString());
    }

    private boolean isEmpty(TextView textView) {
        return TextUtils.isEmpty(textView.getText().toString().trim());
    }

    /**
     * @return ture:发票类型，公司名称，电话，收件名都不为空
     */
    private boolean commonNoEmpty() {
        if (isEmpty(mCompanyName)) {
            showToast(mCompanyName);
            return false;
        } else if (isEmpty(mCompanyTelephone)) {
            showToast(mCompanyTelephone);
            return false;
        } else if (isEmpty(mAcceptPersonName)) {
            showToast(mAcceptPersonName);
            return false;
        }
        return true;
    }

    /**
     * @return ture:收件号码，收件地区，详细地址都不为空
     */
    private boolean addressNoEmpty() {
        if (isEmpty(mAcceptTelephone)) {
            showToast(mAcceptTelephone);
            return false;
        } else if (isEmpty(mAcceptArea)) {
            showToast(mAcceptArea);
            return false;
        } else if (isEmpty(mAcceptDetailAddress)) {
            showToast(mAcceptDetailAddress);
            return false;
        } else if (!DateUtil.isPhoneNumble(getText(mAcceptTelephone))) {
            showFiveToast("手机号码不正确哦");
            return false;
        }
        return true;
    }

    /**
     * @return ture:税务编号，对公银行，银行卡号都不为空
     */
    private boolean bankNoEmpty() {
        if (isEmpty(mTaxNumber)) {
            showToast(mTaxNumber);
            return false;
        } else if (isEmpty(mBank)) {
            showToast(mBank);
            return false;
        } else if (isEmpty(mBankNumber)) {
            showToast(mBankNumber);
            return false;
        }
        return true;
    }

    /**
     * @return ture:全部内容都不为空
     */
    private boolean allNoEmpty() {
        if (!commonNoEmpty()) {
            return false;
        }

        boolean noEmpty = false;
        switch (mTicketType.getText().toString()) {
            case "电子":
                if (isEmpty(mAcceptEmail)) {
                    showToast(mAcceptEmail);
                } else if (!DateUtil.isEmail(getText(mAcceptEmail))) {
                    showFiveToast("邮箱地址不正确哦");
                } else {
                    noEmpty = true;
                }
                break;
            case "普票":
                if (addressNoEmpty()) {
                    noEmpty = true;
                }
                break;
            case "专票":
                if (addressNoEmpty() && bankNoEmpty()) {
                    noEmpty = true;
                }
                break;
        }
        return noEmpty;
    }

    /**
     * 关闭软键盘，防止在输入时弹出选择器被挡住
     */
    private void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /**
     * 上传收票地址,只上传对应发票类型的数据
     */
    private void uploadNewAddress() {
        showLoadingDialog("正在保存");
        getOkGo(HttpConstants.addAcceptTicketAddress)
                .params("type", getType())
                .params("company", getText(mCompanyName))
                .params("companyPhone", getText(mCompanyTelephone))
                .params("acceptPersonName", getText(mAcceptPersonName))
                .params("acceptPersonTelephone", isTypeOne() ? "" : getText(mAcceptTelephone))
                .params("acceptPersonEmail", isTypeOne() ? getText(mAcceptEmail) : "")
                .params("acceptArea", isTypeOne() ? "" : getText(mAcceptArea))
                .params("acceptAddress", isTypeOne() ? "" : getText(mAcceptDetailAddress))
                .params("taxNumber", isTypeThree() ? getText(mTaxNumber) : "")
                .params("bank", isTypeThree() ? getText(mBank) : "")
                .params("bankNumber", isTypeThree() ? getText(mBankNumber) : "")
                .execute(new JsonCallback<Base_Class_Info<AcceptTicketAddressInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<AcceptTicketAddressInfo> o, Call call, Response response) {
                        dismmisLoadingDialog();
                        Intent intent = new Intent();
                        intent.putExtra(ConstansUtil.ADD_ACCEPT_ADDRESS, o.data);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dismmisLoadingDialog();
                        if (!handleException(e)) {
                            showFiveToast(e.getMessage());
                        }
                    }
                });
    }

    private String getType() {
        switch (getText(mTicketType)) {
            case "普票":
                return "2";
            case "专票":
                return "3";
            default:
                return "1";
        }
    }

    /**
     * @return 发票类型为电子发票
     */
    private boolean isTypeOne() {
        return getText(mTicketType).equals("电子");
    }

    /**
     * @return 发票类型为专票
     */
    private boolean isTypeThree() {
        return getText(mTicketType).equals("专票");
    }

}
