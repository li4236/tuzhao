package com.tuzhao.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.tianzhili.www.myselfsdk.okgo.interceptor.TokenInvalideException;
import com.tuzhao.application.MyApplication;
import com.tuzhao.info.User_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.mytoast.MyToast;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.tuzhao.publicwidget.dialog.LoginDialogFragment.LOGOUT_ACTION;

public class DensityUtil {

    private static Toast mToast;

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static float sp2px(Context context, float sp) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public static boolean isException(Context context, Exception e) {
        if (e instanceof ConnectException) {
            Log.d("TAG", "请求失败，" + " 信息为：连接异常" + e.toString());
            MyToast.showToast(context, "网络异常", 5);
            return true;
        } else if (e instanceof SocketTimeoutException) {
            Log.d("TAG", "请求失败，" + " 信息为：超时异常" + e.toString());
            MyToast.showToast(context, "网络异常", 5);
            return true;
        } else if (e instanceof NoRouteToHostException) {
            Log.d("TAG", "请求失败，" + " 信息为：没有路由到主机" + e.toString());
            MyToast.showToast(context, "网络异常", 5);
            return true;
        } else if (e instanceof UnknownHostException) {
            Log.d("TAG", "请求失败，" + " 信息为：设备未能上网" + e.toString());
            MyToast.showToast(context, "网络异常", 5);
            return true;
        } else if (e instanceof TokenInvalideException) {
            Log.d("TAG", "请求失败，" + " 信息为：token异常" + e.toString());
            //退出登录，设置不能再自动登录
            User_Info user_info = MyApplication.getInstance().getDatabaseImp().getUserFormDatabase();
            user_info.setAutologin("0");
            MyApplication.getInstance().getDatabaseImp().insertUserToDatabase(user_info);
            //清空缓存的登录信息
            UserManager.getInstance().setUserInfo(new User_Info());
            //发送退出登录的广播
            LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(new Intent(LOGOUT_ACTION));
            MyToast.showToast(context, "账户异常，请重新登录", 5);
            return true;
        }
        return false;
    }

    public static String MD5code(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(text.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                int number = b & 0xff;
                String hex = Integer.toHexString(number);
                if (hex.length() == 1) {
                    sb.append("0");
                    sb.append(hex);
                } else {
                    sb.append(hex);
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {

            return "";
        }
    }

    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getLocalBluetoothMAC(Context context) {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            return android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
        }

        String bluetoothMAC = "";
        if (Build.VERSION.SDK_INT < 23) {
            bluetoothMAC = btAdapter.getAddress();
        } else {
            Class<? extends BluetoothAdapter> btAdapterClass = btAdapter.getClass();
            try {
                Class<?> btClass = Class.forName("android.bluetooth.IBluetooth");
                Field bluetooth = btAdapterClass.getDeclaredField("mService");
                bluetooth.setAccessible(true);
                Method btAddress = btClass.getMethod("getAddress");
                btAddress.setAccessible(true);
                bluetoothMAC = (String) btAddress.invoke(bluetooth.get(btAdapter));
            } catch (Exception e) {
                bluetoothMAC = btAdapter.getAddress();
            }
        }

        if (bluetoothMAC.equals("02:00:00:00:00:00")) {
            return android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
        } else {
            return bluetoothMAC;
        }
    }
}  