package com.e4deen.bean_player.util;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by user on 2017-01-01.
 */
public class Vibe {

    private static Vibrator vibe;
    long[] vibePattern = {0, 60, 40};

    public static Vibe mVibe;

    public Vibe(Context context) {

      //  LinearLayout layout = (LinearLayout) ((Activity)context).findViewById(R.id.seekBarLay);

/*
        if (layout.isInEditMode()) {

        } else {
            vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
*/

        vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void Vibe_Start() {
        vibe.vibrate(vibePattern,0);
    }

    public void Vibe_Stop() {
        vibe.cancel();
    }

    public void vibration(int mSec) {
        vibe.vibrate(mSec);
    }

}
