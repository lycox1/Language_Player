package com.e4deen.bean_player;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

/**
 * Created by user on 2017-06-21.
 */

public class ApplicationBase extends Application {
    @Override public void onCreate() {
        super.onCreate();
        // 폰트 정의
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "Montserrat-Regular.ttf"))
                .addBold(Typekit.createFromAsset(this, "Montserrat-Bold.ttf"));
    }
}



