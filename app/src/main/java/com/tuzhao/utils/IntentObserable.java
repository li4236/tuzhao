package com.tuzhao.utils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by juncoder on 2018/5/30.
 */

public class IntentObserable {

    private static List<IntentObserver> mIntentObservers;

    static {
        mIntentObservers = new LinkedList<>();
    }

    public static void dispatch(String action) {
        Intent intent = new Intent(action);
        for (IntentObserver observer : mIntentObservers) {
            observer.onReceive(intent);
        }
    }

    public static void dispatch(String action, Bundle value) {
        Intent intent = new Intent(action);
        intent.putExtras(value);
        for (IntentObserver observer : mIntentObservers) {
            observer.onReceive(intent);
        }
    }

    public static void dispatch(String action, String key, String value) {
        Intent intent = new Intent(action);
        intent.putExtra(key, value);
        if (ConstansUtil.DELETE_PARK_SPACE.equals(action)) {
            //当fragment发出这个通知的时候，Activity收到会把fragment删除，fragment会解除注册IntentObserver，如果还像原来一样通知会导致同步修改异常
            List<IntentObserver> list = new ArrayList<>(mIntentObservers);
            for (IntentObserver observer : list) {
                observer.onReceive(intent);
            }
        } else {
            for (IntentObserver observer : mIntentObservers) {
                observer.onReceive(intent);
            }
        }
    }

    public static void dispatch(String action, String... keyWithValue) {
        Intent intent = new Intent(action);
        for (int i = 0; i < keyWithValue.length; i += 2) {
            intent.putExtra(keyWithValue[i], keyWithValue[i + 1]);
        }
        for (IntentObserver observer : mIntentObservers) {
            observer.onReceive(intent);
        }
    }

    public static void dispatch(String action, String key, boolean value) {
        Intent intent = new Intent(action);
        intent.putExtra(key, value);
        for (IntentObserver observer : mIntentObservers) {
            observer.onReceive(intent);
        }
    }

    public static void dispatch(String action, String key, int value) {
        Intent intent = new Intent(action);
        intent.putExtra(key, value);
        for (IntentObserver observer : mIntentObservers) {
            observer.onReceive(intent);
        }
    }

    public static void dispatch(String action, String key, Parcelable value) {
        Intent intent = new Intent(action);
        intent.putExtra(key, value);
        for (IntentObserver observer : mIntentObservers) {
            observer.onReceive(intent);
        }
    }

    public static void dispatch(String action, String key, Bundle value) {
        Intent intent = new Intent(action);
        intent.putExtra(key, value);
        for (IntentObserver observer : mIntentObservers) {
            observer.onReceive(intent);
        }
    }

    public static void dispatch(String action, String key, ArrayList<? extends Parcelable> value) {
        Intent intent = new Intent(action);
        intent.putParcelableArrayListExtra(key, value);
        for (IntentObserver observer : mIntentObservers) {
            observer.onReceive(intent);
        }
    }

    public static void dispatch(String action, String key, String value, String otherKey, boolean otherValue) {
        Intent intent = new Intent(action);
        intent.putExtra(key, value);
        intent.putExtra(otherKey, otherValue);
        for (IntentObserver observer : mIntentObservers) {
            observer.onReceive(intent);
        }
    }

    public static void dispatch(String action, String key, int value, String otherKey, int otherValue) {
        Intent intent = new Intent(action);
        intent.putExtra(key, value);
        intent.putExtra(otherKey, otherValue);
        for (IntentObserver observer : mIntentObservers) {
            observer.onReceive(intent);
        }
    }

    public static void dispatch(Intent intent) {
        for (IntentObserver observer : mIntentObservers) {
            observer.onReceive(intent);
        }
    }

    public static void registerObserver(IntentObserver observer) {
        if (!mIntentObservers.contains(observer)) {
            mIntentObservers.add(observer);
        }
    }

    public static void unregisterObserver(IntentObserver observer) {
        mIntentObservers.remove(observer);
    }

}
