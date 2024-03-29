package com.tuzhao.activity.mine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
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
import com.tuzhao.utils.ViewUtil;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/3/30.
 * <p>
 * 新建收票地址和修改收票地址
 * </p>
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

    private ConstraintLayout mAcceptTicketMainCl;

    private ConstraintLayout mAcceptEmailCl;

    private ConstraintLayout mAcceptAddressCl;

    private ConstraintLayout mAcceptBankCl;

    private ConstraintSet mConstraintSet;

    private ArrayList<String> mTicketTypes;

    private OptionsPickerView<String> mTicketTypeOption;

    private ArrayList<String> mProvinces;

    private ArrayList<ArrayList<String>> mCitys;

    private ArrayList<ArrayList<ArrayList<String>>> mCounties;

    private OptionsPickerView<String> mCityOption;

    //如果是要编辑收票地址则不为空
    private AcceptTicketAddressInfo mAddressInfo;

    @Override

    protected int resourceId() {
        return R.layout.activity_add_accept_ticket_address_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mAddressInfo = getIntent().getParcelableExtra(ConstansUtil.CHAGNE_ACCEPT_ADDRESS);

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

        mAcceptTicketMainCl = findViewById(R.id.add_accept_ticket_address_main_cl);
        mAcceptEmailCl = findViewById(R.id.add_accept_ticket_address_email_cl);
        mAcceptAddressCl = findViewById(R.id.add_accept_ticket_address_address_cl);
        mAcceptBankCl = findViewById(R.id.add_accept_ticket_address_bank_cl);

        mTicketType.setOnClickListener(this);
        mAcceptArea.setOnClickListener(this);

        TextView taxNumber = findViewById(R.id.accept_ticket_tax_number_tv);
        SpannableString spannableString = new SpannableString("税占位号");
        spannableString.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 1, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        taxNumber.setText(spannableString);

        TextView saveTicketAddress = findViewById(R.id.save_accept_ticket_address);
        saveTicketAddress.setText(mAddressInfo == null ? "新建收票地址" : "保存修改");
        saveTicketAddress.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mConstraintSet = new ConstraintSet();
        setTicketValue();
        initTicketTypes();
        initCityOption();
        dismmisLoadingDialog();
    }

    @NonNull
    @Override
    protected String title() {
        return mAddressInfo == null ? "新建收票地址" : "修改收票地址";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ticket_type:
                ViewUtil.closeInputMethod(getWindow().peekDecorView());
                mTicketTypeOption.show();
                break;
            case R.id.accept_ticket_area:
                ViewUtil.closeInputMethod(getWindow().peekDecorView());
                mCityOption.show();
                break;
            case R.id.save_accept_ticket_address:
                if (isEmpty(mTicketType)) {
                    showToast(mTicketType);
                    ViewUtil.closeInputMethod(getWindow().peekDecorView());
                    mTicketTypeOption.show();
                } else if (allNoEmpty()) {
                    if (mAddressInfo == null) {
                        uploadNewAddress();
                    } else {
                        changeTicketAddress();
                    }
                }
                break;
        }
    }

    /**
     * 如果是编辑收票地址则把对应的值显示出来
     */
    private void setTicketValue() {
        if (mAddressInfo != null) {
            mTicketType.setText(mAddressInfo.getType());
            mCompanyName.setText(mAddressInfo.getCompany());
            mCompanyTelephone.setText(mAddressInfo.getCompanyPhone());
            mAcceptPersonName.setText(mAddressInfo.getAcceptPersonName().equals("-1") ? "" : mAddressInfo.getAcceptPersonName());
            mCompanyName.setSelection(mCompanyName.getText().length());
            mTaxNumber.setText(mAddressInfo.getTaxNumber().equals("-1") ? "" : mAddressInfo.getTaxNumber());

            switch (mAddressInfo.getType()) {
                case "电子":
                    mAcceptEmail.setText(mAddressInfo.getAcceptPersonEmail().equals("-1") ? "" : mAddressInfo.getAcceptPersonEmail());
                    showEmail();
                    break;
                case "专票":
                    mBank.setText(mAddressInfo.getBank().equals("-1") ? "" : mAddressInfo.getBank());
                    mBankNumber.setText(mAddressInfo.getBankNumber().equals("-1") ? "" : mAddressInfo.getBankNumber());
                case "普票":
                    mAcceptTelephone.setText(mAddressInfo.getAcceptPersonTelephone().equals("-1") ? "" : mAddressInfo.getAcceptPersonTelephone());
                    mAcceptArea.setText(mAddressInfo.getAcceptArea().equals("-1") ? "" : mAddressInfo.getAcceptArea());
                    mAcceptDetailAddress.setText(mAddressInfo.getAcceptAddress().equals("-1") ? "" : mAddressInfo.getAcceptAddress());
                    showAddress();
                    break;
            }
            if (mAddressInfo.getType().equals("专票")) {
                showBank();
            }
        }
    }

    /**
     * 初始化发票类型选择器
     */
    private void initTicketTypes() {
        mTicketTypes = new ArrayList<>(3);
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
                String area;
                if (mProvinces.get(options1).equals(mCitys.get(options1).get(option2))) {
                    area = mProvinces.get(options1) + mCounties.get(options1).get(option2).get(options3);
                } else {
                    area = mProvinces.get(options1) + mCitys.get(options1).get(option2) + mCounties.get(options1).get(option2).get(options3);
                }
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

        mConstraintSet.clone(mAcceptTicketMainCl);
        mConstraintSet.connect(R.id.add_accept_ticket_address_tax_number_cl, ConstraintSet.TOP, R.id.add_accept_ticket_address_email_cl, ConstraintSet.BOTTOM);
        mConstraintSet.applyTo(mAcceptTicketMainCl);
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

        mConstraintSet.clone(mAcceptTicketMainCl);
        mConstraintSet.connect(R.id.add_accept_ticket_address_tax_number_cl, ConstraintSet.TOP, R.id.add_accept_ticket_address_address_cl, ConstraintSet.BOTTOM);
        mConstraintSet.applyTo(mAcceptTicketMainCl);
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

        mConstraintSet.clone(mAcceptTicketMainCl);
        mConstraintSet.connect(R.id.add_accept_ticket_address_tax_number_cl, ConstraintSet.TOP, R.id.add_accept_ticket_address_address_cl, ConstraintSet.BOTTOM);
        mConstraintSet.applyTo(mAcceptTicketMainCl);
    }

    /**
     * 把textView的hint显示出来
     */
    private void showToast(TextView textView) {
        showFiveToast(textView.getHint().toString());
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
        } else if (getTextLength(mCompanyTelephone) < 6) {
            showFiveToast("公司电话格式不正确哦");
        } else if (isEmpty(mAcceptPersonName)) {
            showToast(mAcceptPersonName);
            return false;
        } else if (isEmpty(mTaxNumber)) {
            showToast(mTaxNumber);
            return false;
        } else if (getTextLength(mTaxNumber) < 15) {
            showFiveToast("你的纳税人识别号长度不够哦");
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
        } else if (!DateUtil.isPhoneNumble(getText(mAcceptTelephone))) {
            showFiveToast("手机号码不正确哦");
            return false;
        } else if (isEmpty(mAcceptArea)) {
            showToast(mAcceptArea);
            return false;
        } else if (isEmpty(mAcceptDetailAddress)) {
            showToast(mAcceptDetailAddress);
            return false;
        }
        return true;
    }

    /**
     * @return ture:对公银行，银行卡号都不为空
     */
    private boolean bankNoEmpty() {
        if (isEmpty(mBank)) {
            showToast(mBank);
            return false;
        } else if (isEmpty(mBankNumber)) {
            showToast(mBankNumber);
            return false;
        }
        return true;
    }

    /**
     * @return ture:全部内容都不为空，并且手机号和邮箱验证通过
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
     * 上传收票地址,只上传对应发票类型的数据
     */
    private void uploadNewAddress() {
        showLoadingDialog("正在保存");
        getOkGo(HttpConstants.addAcceptTicketAddress)
                .params("type", getType())
                .params("company", getText(mCompanyName))
                .params("companyPhone", getText(mCompanyTelephone))
                .params("acceptPersonName", getText(mAcceptPersonName))
                .params("taxNumber", getText(mTaxNumber))
                .params("acceptPersonTelephone", isTypeOne() ? "-1" : getText(mAcceptTelephone))
                .params("acceptPersonEmail", isTypeOne() ? getText(mAcceptEmail) : "-1")
                .params("acceptArea", isTypeOne() ? "-1" : getText(mAcceptArea))
                .params("acceptAddress", isTypeOne() ? "-1" : getText(mAcceptDetailAddress))
                .params("bank", isTypeThree() ? getText(mBank) : "-1")
                .params("bankNumber", isTypeThree() ? getText(mBankNumber) : "-1")
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
                            switch (e.getMessage()) {
                                case "803":
                                    showFiveToast("数据异常，请稍后再试");
                                    finish();
                                case "101":
                                case "806":
                                    showFiveToast("最多只能创建10个地址哦");
                                    break;
                            }
                        }
                    }
                });
    }

    /**
     * 上传修改后的收票地址,只上传对应发票类型的数据
     */
    private void changeTicketAddress() {
        showLoadingDialog("正在修改");
        getOkGo(HttpConstants.changeAcceptTicketAddress)
                .params("ticketId", mAddressInfo.getTicketId())
                .params("type", getType())
                .params("company", getText(mCompanyName))
                .params("companyPhone", getText(mCompanyTelephone))
                .params("acceptPersonName", getText(mAcceptPersonName))
                .params("acceptPersonTelephone", isTypeOne() ? "-1" : getText(mAcceptTelephone))
                .params("acceptPersonEmail", isTypeOne() ? getText(mAcceptEmail) : "-1")
                .params("acceptArea", isTypeOne() ? "-1" : getText(mAcceptArea))
                .params("acceptAddress", isTypeOne() ? "-1" : getText(mAcceptDetailAddress))
                .params("taxNumber", getText(mTaxNumber))
                .params("bank", isTypeThree() ? getText(mBank) : "-1")
                .params("bankNumber", isTypeThree() ? getText(mBankNumber) : "-1")
                .execute(new JsonCallback<Base_Class_Info<AcceptTicketAddressInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<AcceptTicketAddressInfo> o, Call call, Response response) {
                        dismmisLoadingDialog();
                        Intent intent = new Intent();
                        intent.putExtra(ConstansUtil.CHAGNE_ACCEPT_ADDRESS, o.data);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dismmisLoadingDialog();
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    showFiveToast("你填写的信息有误哦");
                                    break;
                                case "102":
                                    showFiveToast("邮箱格式错误");
                                    break;
                                case "103":
                                    showFiveToast("邮箱类型只能是电子，普票，专票");
                                    break;
                                case "104":
                                    showFiveToast("你的收票地址不存在");
                                    finish();
                                    break;
                                case "105":
                                    finish();
                                    break;
                            }
                        }
                    }
                });
    }

    /**
     * 根据选择的发票类型返回后台需要的数字类型
     */
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
     * @return true(发票类型为电子发票)
     */
    private boolean isTypeOne() {
        return getText(mTicketType).equals("电子");
    }

    /**
     * @return true(发票类型为专票)
     */
    private boolean isTypeThree() {
        return getText(mTicketType).equals("专票");
    }

    @Override
    public void onBackPressed() {
        if (mTicketTypeOption.isShowing() || mCityOption.isShowing()) {
            mTicketTypeOption.dismiss();
            mCityOption.dismiss();
        } else {
            super.onBackPressed();
        }
    }

}
