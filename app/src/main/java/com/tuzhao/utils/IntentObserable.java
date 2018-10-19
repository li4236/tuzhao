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
        if (mIntentObservers.contains(observer)) {
            mIntentObservers.remove(observer);
        }
    }

}
