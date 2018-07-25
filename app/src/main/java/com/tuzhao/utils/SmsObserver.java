package com.tuzhao.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by juncoder on 2018/7/23.
 * <p>
 * 读取短信的内容
 * </p>
 */
public class SmsObserver extends ContentObserver {

    private static final String SMS_URI_INBOX = "content://sms/inbox";
    private Context mContext;
    private SmsListener listener;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public SmsObserver(Handler handler, Context context, SmsListener listener) {
        super(handler);
        mContext = context;
        this.listener = listener;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(Uri.parse(SMS_URI_INBOX), new String[]{"_id", "address", "body", "read"}, "body like ? and read = ?",
                new String[]{"%途找停车%", "0"}, "date desc");
        if (cursor != null) {
            //华为,小米读取不到短信的
            if (cursor.moveToFirst()) {
                String smsBody = cursor.getString(cursor.getColumnIndex("body"));
                String regEx = "[^0-9]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(smsBody);
                String smsContent = m.replaceAll("").trim();
                if (!TextUtils.isEmpty(smsContent)) {
                    listener.onResult(smsContent);
                }
                cursor.close();
            }
        }
    }

    /*
     * 短信回调接口
     */
    public interface SmsListener {

        /**
         * 接受sms状态
         */
        void onResult(String smsContent);
    }

}
