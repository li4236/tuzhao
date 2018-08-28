package com.tuzhao.publicwidget.alipay;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.ZhimaInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.OnLoadCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.utils.DeviceUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/7/24.
 * <p>
 * 芝麻认证
 * </p>
 */
public class ZhimaCertification {

    private static final String TAG = "ZhimaCertification";

    private Context mContext;

    private boolean mIsCertifyZhima;

    private String mBizNo;

    public ZhimaCertification(Context context) {
        mContext = context;
    }

    public void getCertifyZhimaUrl(String name,String idCardNumber,final LoadFailCallback loadFailCallback) {
        OkGo.post(HttpConstants.getCertifyZhimaUrl)
                .tag(TAG)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("name",name)
                .params("idCardNumber",idCardNumber)
                .params("returnUrl","tuzhao://certify.zhima.activity")
                .execute(new JsonCallback<Base_Class_Info<ZhimaInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ZhimaInfo> o, Call call, Response response) {
                        Log.e(TAG, "onSuccess: " + o.data.getBiz_no());
                        if (DeviceUtils.isInstallAlipay(mContext)) {
                            try {
                                mBizNo = o.data.getBiz_no();
                                mIsCertifyZhima = true;
                                Intent action = new Intent(Intent.ACTION_VIEW);
                                String builder = "alipays://platformapi/startapp?appId=20000067&url=" +
                                        URLEncoder.encode(o.data.getUrl(), "UTF-8");
                                action.setData(Uri.parse(builder));
                                mContext.startActivity(action);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                mIsCertifyZhima = false;
                            }
                        } else {
                            new AlertDialog.Builder(mContext)
                                    .setMessage("是否下载并安装支付宝完成认证?")
                                    .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent action = new Intent(Intent.ACTION_VIEW);
                                            action.setData(Uri.parse("https://m.alipay.com"));
                                            mContext.startActivity(action);
                                        }
                                    }).setNegativeButton("算了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mIsCertifyZhima = false;
                        loadFailCallback.onLoadFail(e);
                    }
                });
    }

    public void getCertifyZhimaResult(final OnLoadCallback<User_Info, Exception> onLoadCallback) {
        OkGo.post(HttpConstants.getCertifyZhimaResult)
                .tag(TAG)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("biz_no", mBizNo)
                .execute(new JsonCallback<Base_Class_Info<User_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<User_Info> o, Call call, Response response) {
                        mIsCertifyZhima = false;
                        onLoadCallback.onSuccess(o.data);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        onLoadCallback.onFail(e);
                    }
                });
    }

    public boolean isCertifyZhima() {
        return mIsCertifyZhima;
    }

    public void setCertifyZhima(boolean certifyZhima) {
        mIsCertifyZhima = certifyZhima;
    }

    public void onDestroy() {
        OkGo.getInstance().cancelTag(TAG);
        mContext = null;
    }

}
