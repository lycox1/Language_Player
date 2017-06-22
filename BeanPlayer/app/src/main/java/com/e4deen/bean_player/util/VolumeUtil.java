package com.e4deen.bean_player.util;

import android.util.Log;

/**
 * Created by user on 2017-04-16.
 */

public class VolumeUtil {
    public static int MAX_VOLUME_IDX = 10;
    public static int MIN_VOLUME_IDX = 0;

    public static String LOG_TAG = "VolumeUtil";

    public static float VOLUME_0 = 0.0f;
    public static float VOLUME_1 = 0.00217f;
    public static float VOLUME_2 = 0.00466f;
    public static float VOLUME_3 = 0.01f;
    public static float VOLUME_4 = 0.03f;
    public static float VOLUME_5 = 0.08f;
    public static float VOLUME_6 = 0.19f;
    public static float VOLUME_7 = 0.3725f;
    public static float VOLUME_8 = 0.50408f;
    public static float VOLUME_9 = 0.70998f;
    public static float VOLUME_10 = 1;

    public static float mMasterVolume = 1.0f;

    public static void setmMasterVolume(int vol) {
        mMasterVolume = (float)vol / (float)10;
        Log.d(LOG_TAG, "setMasterVolume vol index " + vol + ", mMasterVolume " + mMasterVolume);
    }

    public static float getVolume(int vol) {

        float volume;
        Float result_vol;
        switch (vol) {
            case 0:
                volume = VOLUME_0;
                break;
            case 1:
                volume = VOLUME_1;
            break;
            case 2:
                volume =  VOLUME_2;
            break;
            case 3:
                volume =  VOLUME_3;
            break;
            case 4:
                volume =  VOLUME_4;
            break;
            case 5:
                volume =  VOLUME_5;
            break;
            case 6:
                volume =  VOLUME_6;
            break;
            case 7:
                volume =  VOLUME_7;
            break;
            case 8:
                volume =  VOLUME_8;
            break;
            case 9:
                volume =  VOLUME_9;
            break;
            case 10:
                volume =  VOLUME_10;
            break;

            default:
                volume =  VOLUME_10;
            break;
        }

        result_vol = volume * mMasterVolume;

        if(result_vol.isNaN()) {
            Log.d(LOG_TAG, "NaN Case volume " + volume + ", mMasterVolume " + mMasterVolume + "result_vol " + result_vol);
        }

        Log.d(LOG_TAG, "getVolume() vol " + vol + ", volume " + volume + ", mMasterVolume " + mMasterVolume + ", result_vol " + result_vol);
        if(result_vol > 1.0f) {
            result_vol = 1.0f;
        }
        else if (result_vol < 0.0f) {
            result_vol = 0.0f;
        }

        return result_vol;
    }
}
