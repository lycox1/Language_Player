package com.e4deen.bean_player.view.entry_view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ContentFrameLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.e4deen.bean_player.R;
import com.e4deen.bean_player.data.Constants;
import com.e4deen.bean_player.view.file_explorer_view.activity.FileSearchActivity;
import com.e4deen.bean_player.view.player_view.activity.MainActivity;
import com.e4deen.bean_player.view.player_view.component.MediaPlayerController;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.w3c.dom.Text;

/**
 * Created by user on 2017-06-20.
 */

public class EntryActivity extends AppCompatActivity {

    static String LOG_TAG = "BeanPlayer_Player_Entry";
    Context mContext;
    Handler h;
    TextView tv_version;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_entry);
        Log.d(LOG_TAG, "onCreate pid " + android.os.Process.myPid());
        if(Constants.sPID == 0) {
            Constants.sPID = android.os.Process.myPid();
        } else {
            Log.d(LOG_TAG, "onCreate pid " + android.os.Process.myPid() + ", no need to entry activity");
            finish();
        }
        mContext = this;
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText("0.8.2");
        h= new Handler();
        h.postDelayed(mrun, 1000);
    }

    Runnable mrun = new Runnable(){
        @Override
        public void run(){
            Intent i = new Intent(EntryActivity.this, MainActivity.class); //인텐트 생성(현 액티비티, 새로 실행할 액티비티)
            startActivity(i);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            //overridePendingTransition 이란 함수를 이용하여 fade in,out 효과를줌. 순서가 중요
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        h.removeCallbacks(mrun);
    }
}
