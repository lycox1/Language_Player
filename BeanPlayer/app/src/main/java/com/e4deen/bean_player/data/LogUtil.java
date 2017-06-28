package com.e4deen.bean_player.data;

import android.util.Log;

import com.e4deen.bean_player.BuildConfig;

/**
 * Created by user on 2017-06-24.
 */

public class LogUtil {
    public static final void logD (String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }
}
