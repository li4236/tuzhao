package com.tuzhao.utils;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juncoder on 2018/5/30.
 */

public class IntentObserable {

    private static List<IntentObserver> mIntentObservers;

    static {
        mIntentObservers = new ArrayList<>();
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
