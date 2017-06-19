package com.e4deen.bean_player.view.player_view.activity;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.e4deen.bean_player.db.DataBases;
import com.e4deen.bean_player.db.Playlist_manager_db;
import com.e4deen.bean_player.util.Valueable_Util;
import com.e4deen.bean_player.view.player_view.activity.fragment.Frag_main_filelist;
import com.e4deen.bean_player.view.player_view.activity.fragment.Frag_main_script;
import com.e4deen.bean_player.view.player_view.activity.fragment.Frag_main_shadowing;
import com.e4deen.bean_player.view.player_view.component.CircleButton;
import com.e4deen.bean_player.data.Constants;
import com.e4deen.bean_player.view.file_explorer_view.activity.FileSearchActivity;
import com.e4deen.bean_player.util.LongPressChecker;
import com.e4deen.bean_player.view.player_view.component.MediaPlayerController;
import com.e4deen.bean_player.R;
import com.e4deen.bean_player.util.Vibe;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.e4deen.bean_player.data.FileParcelable;
import com.e4deen.bean_player.view.player_view.data.BookMark;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Main Activity. Inflates main activity xml and child fragments.
 */
//앱 ID: ca-app-pub-6490716774426103~7305815677
//광고 단위 ID: ca-app-pub-6490716774426103/8782548872


public class MainActivity extends AppCompatActivity {

    static String LOG_TAG = "Loopback_Player_Main";
    final int E_SUCCESS = 1;
    final int E_ERROR = 0;

    private AdView mAdView;

    public ImageButton btn_file_search, btn_play_pause;

    ArrayList<FileParcelable> fileListParcelable;
    public static Context mContext;
    public SeekBar seekBar;
    TextView tv_total_duration, tv_elapsed_time, tv_play_speed, tv_rew_ff_time;

    ArrayList<BookMark> bookmarkList = new ArrayList<BookMark>();
    ArrayList<ImageView> mBookmarkViewlist = new ArrayList<ImageView>();
    ImageView mPeriodRepeatIV;
    float mDensity;
    //public Playlist_manager_db mPLM_DB;
    boolean isLongPress  = false;
    boolean mBookmarkInit = false;
    boolean mPeriodRepeatMode = false;
    int mPeriodRepeatFrom, mPeriodRepeatTo;
    Handler mHandler;
    Runnable mRunableForPrevBookmark, mRunableForNextBookmark;
    public ImageButton btn_one_repeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "MainActivity onCreate()" );
//--------------------------------- Admob Start ---------------------------------------------//
        MobileAds.initialize(this, "ca-app-pub-6490716774426103/8782548872");
        mAdView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
//--------------------------------- Admob End ---------------------------------------------//
//--------------------------------- ETC Start --------------------------------------//
        //mContext = getApplicationContext();
        mContext = this;
        MediaPlayerController.sController = new MediaPlayerController(mContext, mMainHandler);

        findViewById(R.id.btn_seek_rew).setOnTouchListener(mTouchEvent);
        findViewById(R.id.btn_seek_ff).setOnTouchListener(mTouchEvent);
        findViewById(R.id.btn_seek_time_up).setOnTouchListener(mTouchEvent);
        findViewById(R.id.btn_seek_time_down).setOnTouchListener(mTouchEvent);
        findViewById(R.id.btn_speed_dn).setOnTouchListener(mTouchEvent);
        findViewById(R.id.btn_speed_up).setOnTouchListener(mTouchEvent);

        findViewById(R.id.btn_bookmark_rew).setOnTouchListener(mTouchEvent);
        findViewById(R.id.btn_bookmark_ff).setOnTouchListener(mTouchEvent);
        //findViewById(R.id.btn_bookmark_rew).setOnLongClickListener(mLongPressListener);
        //findViewById(R.id.btn_bookmark_ff).setOnLongClickListener(mLongPressListener);

        btn_one_repeat = (ImageButton) findViewById(R.id.btn_one_repeat);
        btn_one_repeat.setOnTouchListener(mTouchEvent);
        findViewById(R.id.btn_set_repeat_period).setOnTouchListener(mTouchEvent);
        findViewById(R.id.btn_set_bookmark).setOnTouchListener(mTouchEvent);

        tv_elapsed_time = (TextView) findViewById(R.id.tv_elapsed_time);
        tv_total_duration = (TextView) findViewById(R.id.tv_total_duration);

        tv_play_speed = (TextView) findViewById(R.id.tv_play_speed);
        tv_rew_ff_time = (TextView) findViewById(R.id.tv_rew_ff_time);

        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(mSeekBarChangeListner);
        seekBar.getThumb().mutate().setAlpha(0);

        Vibe.Vibe_Create(this);

        MediaPlayerController.sController.setViews(seekBar, tv_elapsed_time, tv_total_duration, tv_play_speed);
        //playListViewInit();

        getPermission();

        mDensity = getResources().getDisplayMetrics().density;
        Log.d(LOG_TAG, "onCreate() density " + mDensity );

        DataBases.mPLM_DB = new Playlist_manager_db(this);
        DataBases.mPLM_DB.open();

        Valueable_Util.init_Valueable_Util(this);
//----------------------------- ETC End               ----------------------------------//
//---------------------------- Fragment Setting Start ----------------------------------//
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.layout_main_tab, new Frag_main_filelist(mContext), Constants.FRAG_MAIN_TAB_FILELIST);
        fragmentTransaction.commit();
        fm.executePendingTransactions();
        Constants.frag_main_tab_state = Constants.IDX_MAIN_TAB_FILELIST;

        findViewById(R.id.iv_tab_file_list).setOnTouchListener(mTabTouchEvent);
        findViewById(R.id.iv_tab_shadowing).setOnTouchListener(mTabTouchEvent);
        findViewById(R.id.iv_tab_script).setOnTouchListener(mTabTouchEvent);

        ((ImageView)findViewById(R.id.iv_tab_file_list)).setImageResource(R.drawable.tab_file_list_press);
//---------------------------- Fragment Setting End   ----------------------------------//
//---------------------------- Callback setting start   ----------------------------------//
        MediaPlayerController.OnDrawBookmarkCb onDrawBookmarkCb = new MediaPlayerController.OnDrawBookmarkCb() {
            @Override
            public void onDrawBookmarkCb() {
                Log.d(LOG_TAG, "onDrawBookmarkCb Log Test");
                //drawBookmarkList();
            }
        };

        MediaPlayerController.sController.setOnDrawBookmarkCb(onDrawBookmarkCb);
//---------------------------- Callback Setting End ----------------------------------//

        //DataBases.mPLM_DB.deleteAll();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        Log.d(LOG_TAG, "MainActivity onResume()" );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.

        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.layout_actionbar, null);

        actionBar.setCustomView(actionbar);
        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar) actionbar.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        btn_file_search = (ImageButton) findViewById(R.id.btn_file_search);

        btn_file_search.setOnTouchListener(mTouchEvent);

        RelativeLayout RL_Circle = (RelativeLayout) findViewById(R.id.RL_Circle);

        Log.d(LOG_TAG, "onCreateOptionsMenu  RL_Circle.getWidth() " + RL_Circle.getWidth() + ", RL_Circle.getHeight() " + RL_Circle.getHeight());
        CircleButton btn_Circle = new CircleButton(this, RL_Circle.getWidth(), RL_Circle.getHeight() );
        //btn_Circle.setMediaPlayerController(MediaPlayerController.sController);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        RL_Circle.addView(btn_Circle, lp);

        btn_play_pause = new ImageButton(this);
        btn_play_pause.setImageResource(R.drawable.btn_play);
        btn_play_pause.setId(R.id.btn_play_pause_id);

        btn_play_pause.setOnTouchListener(mTouchEvent);
        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT );
        lp.setMargins(0,0,0,0);
        btn_play_pause.setPadding(0,10,0,10);
        btn_play_pause.setBackgroundColor(0xffffff);

        RL_Circle.addView(btn_play_pause, lp);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
        Log.d(LOG_TAG, "MainActivity onPause()" );
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        MediaPlayerController.sController.PlayerDestroy();
        super.onDestroy();
        Log.d(LOG_TAG, "MainActivity onDestroy()" );
    }

    View.OnTouchListener mTouchEvent = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d(LOG_TAG, "onTouch id " + v.getId());

            int action = event.getAction();
            int id = v.getId();
            ImageButton img_btn = (ImageButton) v;

            if (action == MotionEvent.ACTION_DOWN) {
                Vibe.vibration(80);
            }

            switch (id) {
                case R.id.btn_speed_up:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));
                            if (Constants.FILE_READY_STATUS == Constants.FILE_READY)
                                MediaPlayerController.sController.speedUp();
                            break;
                        case MotionEvent.ACTION_UP:
                            img_btn.clearColorFilter();
                            break;
                    }
                    break;
                case R.id.btn_speed_dn:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));
                            if (Constants.FILE_READY_STATUS == Constants.FILE_READY)
                                MediaPlayerController.sController.speedDown();
                            break;
                        case MotionEvent.ACTION_UP:
                            img_btn.clearColorFilter();
                            break;
                    }
                    break;

                case R.id.btn_seek_time_up:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            //btn_ff.setImageResource(R.drawable.ic_common_press);
                            img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));

                            int seek_time = Integer.valueOf(tv_rew_ff_time.getText().toString());

                            if (seek_time >= 30)
                                seek_time = 30;
                            else
                                seek_time++;

                            tv_rew_ff_time.setText(String.valueOf(seek_time));

                            break;
                        case MotionEvent.ACTION_UP:
                            img_btn.clearColorFilter();
                            //btn_ff.setImageResource(R.drawable.ic_ff_release);
                            break;
                    }
                    break;

                case R.id.btn_seek_time_down:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            //Log.d(LOG_TAG, "btn_seek_rew click");
                            //btn_ff.setImageResource(R.drawable.ic_common_press);
                            img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));

                            int seek_time = Integer.valueOf(tv_rew_ff_time.getText().toString());

                            if (seek_time <= 1)
                                seek_time = 1;
                            else
                                seek_time--;

                            tv_rew_ff_time.setText(String.valueOf(seek_time));
                            break;
                        case MotionEvent.ACTION_UP:
                            img_btn.clearColorFilter();
                            //btn_ff.setImageResource(R.drawable.ic_ff_release);
                            break;
                    }
                    break;

                case R.id.btn_seek_rew:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            //btn_rew.setImageResource(R.drawable.ic_common_press);
                            img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));

                            if (Constants.FILE_READY_STATUS == Constants.FILE_READY)
                                MediaPlayerController.sController.rewPlay(Integer.valueOf(tv_rew_ff_time.getText().toString()) * Constants.SEC);


                            break;
                        case MotionEvent.ACTION_UP:
                            img_btn.clearColorFilter();
                            //btn_rew.setImageResource(R.drawable.ic_rew_release);
                            break;
                    }
                    break;

                case R.id.btn_seek_ff:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            //btn_ff.setImageResource(R.drawable.ic_common_press);
                            img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));
                            if (Constants.FILE_READY_STATUS == Constants.FILE_READY)
                                MediaPlayerController.sController.ffPlay(Integer.valueOf(tv_rew_ff_time.getText().toString()) * Constants.SEC);
                            break;
                        case MotionEvent.ACTION_UP:
                            img_btn.clearColorFilter();
                            //btn_ff.setImageResource(R.drawable.ic_ff_release);
                            break;
                    }
                    break;

                case R.id.btn_play_pause_id:
                    Log.d(LOG_TAG, "btn_play_pause_id call enter FILE_READY_STATUS " + Constants.FILE_READY_STATUS + ", PLAYER_STATUS " + Constants.PLAYER_STATUS);
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            if (Constants.FILE_READY_STATUS == Constants.FILE_READY) {
                                if (Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PLAY) {
                                    Log.d(LOG_TAG, "btn_play_pause_id call pausePlay()");
                                    MediaPlayerController.sController.pausePlay();
                                    Constants.PLAYER_STATUS = Constants.PLAYER_STATUS_PAUSE;
                                } else {
                                    Log.d(LOG_TAG, "btn_play_pause_id call startPlay()");
                                    MediaPlayerController.sController.startPlay();
                                    Constants.PLAYER_STATUS = Constants.PLAYER_STATUS_PLAY;
                                }
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PLAY) {
                                btn_play_pause.setImageResource(R.drawable.btn_pause);
                            } else {
                                btn_play_pause.setImageResource(R.drawable.btn_play);
                            }
                            break;
                    }

/*
                    if(Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PLAY) {
                        btn_play_pause.setImageResource(R.drawable.btn_pause);
                        Constants.PLAYER_STATUS = Constants.PLAYER_STATUS_PAUSE;
                    } else {
                        btn_play_pause.setImageResource(R.drawable.btn_play);
                        Constants.PLAYER_STATUS = Constants.PLAYER_STATUS_PLAY;
                    }
*/
                    break;

/*
                case R.id.btn_shuffle:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            _Shuffle.setImageResource(R.drawable.ic_shffule_pressed);
//                            LinearLayout temp = (LinearLayout) findViewById(R.id.seekBarLay);
//                            Log.d(LOG_TAG, "temp getLeft " + temp.getLeft() + ", getRight " + temp.getRight() + ", getWidth " + temp.getWidth());
                            break;
                        case MotionEvent.ACTION_UP:

                            if(Constants.SHUFFLE_STATE == Constants.NOT_SUFFLE) {
                                _Shuffle.setImageResource(R.drawable.ic_shffule);
                            }
                            else {
                                _Shuffle.setImageResource(R.drawable.ic_shffule_pressed);

                                if(Constants.REPEAT_STATE == Constants.REPEAT) {
                                    _Set_Repeat.setImageResource(R.drawable.ic_period_repeat);
                                }
                            }
                            break;
                    }
                    break;

                case R.id.btn_repeat_play:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            Button_Set_Repeat.setImageResource(R.drawable.ic_period_repeat_pressed);
                            if(Constants.FILE_READY_STATUS == Constants.FILE_READY) {
                                MediaPlayerController.sController.setRepeat();
                            } else {
                                Log.e(LOG_TAG, "Repeat button is pressed. But FILE_READY_STATUS is FILE_NOT_READY.");
                                Toast.makeText(mContext, "File is not ready", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if(Constants.REPEAT_STATE == Constants.NOT_REPEAT) {
                                Button_Set_Repeat.setImageResource(R.drawable.ic_period_repeat);
                            }
                            else {
                                Button_Set_Repeat.setImageResource(R.drawable.ic_period_repeat_pressed);

                                if(Constants.SHUFFLE_STATE == Constants.SHUFFLE) {
                                    Button_Shuffle.setImageResource(R.drawable.ic_shffule);
                                }
                            }
                            break;
                    }
                    break;
*/
                case R.id.btn_file_search:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));
                            Intent intent = new Intent(mContext, FileSearchActivity.class);
                            //startActivityForResult(intent, 0);
                            startActivity(intent);
                            break;
                        case MotionEvent.ACTION_UP:
                            img_btn.clearColorFilter();
                            break;
                    }
                    break;


                case R.id.btn_set_bookmark:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));
                            setBookmark();

                            break;
                        case MotionEvent.ACTION_UP:
                            img_btn.clearColorFilter();
                            break;
                    }
                    break;
                case R.id.btn_set_repeat_period:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            Log.d(LOG_TAG, "btn_set_repeat_period ACTION_DOWN");
                            setPeriodRepeat();
                            img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));
                            break;

                        case MotionEvent.ACTION_UP:
                            Log.d(LOG_TAG, "btn_set_repeat_period ACTION_UP");
                            img_btn.clearColorFilter();
                            break;
                    }
                    break;

                case R.id.btn_one_repeat:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            Log.d(LOG_TAG, "btn_one_repeat ACTION_DOWN");
                            if(Constants.OneRepeatMode == false) {
                                Constants.OneRepeatMode = true;
                                img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));
                                MediaPlayerController.sController.setRepeat();
                            } else {
                                img_btn.clearColorFilter();
                                Constants.OneRepeatMode = false;
                                MediaPlayerController.sController.setRepeat();
                            }

                            break;

                        case MotionEvent.ACTION_UP:
                            Log.d(LOG_TAG, "btn_one_repeat ACTION_UP");
                            break;
                    }
                    break;

/*
                case R.id.btn_set_bookmark:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            btn_set_bookmark.setImageResource(R.drawable.ic_bookmark_pressed);
                            setBookmark();
                            break;
                        case MotionEvent.ACTION_UP:
                            btn_set_bookmark.setImageResource(R.drawable.ic_bookmark);
                            break;
                    }
                    break;
*/

                case R.id.btn_bookmark_rew:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            isLongPress = true;
                            mHandler = new Handler();
                            img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));
                            Log.d(LOG_TAG, "btn_bookmark_rew ACTION_DOWN");

                            mRunableForPrevBookmark = new Runnable() {
                                @Override
                                public void run() {
                                    if (isLongPress) {
                                        isLongPress = false;
                                        Log.d(LOG_TAG, "btn_bookmark_rew isLongPress true");
                                        prevBookmarkRemove();
                                        Vibe.vibration(80);
                                    } else {
                                        Log.d(LOG_TAG, "btn_bookmark_rew LongPress false");
                                    }
                                }};

                            mHandler.postDelayed( mRunableForPrevBookmark, 700);
                            break;

                        case MotionEvent.ACTION_UP:
                            if (isLongPress == true) {
                                Log.d(LOG_TAG, "btn_bookmark_rew Short Press");
                                rewBookmark();
                            }
                            mHandler.removeCallbacks(mRunableForPrevBookmark);
                            img_btn.clearColorFilter();
                            isLongPress = false;
                            break;
                    }
                    break;

                case R.id.btn_bookmark_ff:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            isLongPress = true;
                            mHandler = new Handler();
                            img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));
                            Log.d(LOG_TAG, "btn_bookmark_ff ACTION_DOWN");

                            mRunableForNextBookmark = new Runnable() {
                                @Override
                                public void run() {
                                    if (isLongPress) {
                                        isLongPress = false;
                                        Log.d(LOG_TAG, "btn_bookmark_ff Long Press");
                                        nextBookmarkRemove();
                                        Vibe.vibration(80);
                                    } else {
                                        Log.d(LOG_TAG, "btn_bookmark_ff LongPress false");
                                    }
                                }};
                            mHandler.postDelayed(mRunableForNextBookmark, 700);
                            break;

                        case MotionEvent.ACTION_UP:
                            if (isLongPress == true) {
                                Log.d(LOG_TAG, "btn_bookmark_ff Short Press");
                                ffBookmark();
                            }
                            mHandler.removeCallbacks(mRunableForNextBookmark);
                            img_btn.clearColorFilter();
                            isLongPress = false;
                            break;
                    }
                    break;
            }

            return true;
        }
    };

    View.OnTouchListener mTabTouchEvent = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int action = event.getAction();
            int id = v.getId();

            if (action == MotionEvent.ACTION_DOWN) {
                Vibe.vibration(80);
            }

            switch (id) {
                case R.id.iv_tab_file_list:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            main_tab_fragment_switch(Constants.IDX_MAIN_TAB_FILELIST);
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                    }
                    break;
                case R.id.iv_tab_shadowing:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            main_tab_fragment_switch(Constants.IDX_MAIN_TAB_SHADOWING);
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                    }
                    break;
                case R.id.iv_tab_script:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            main_tab_fragment_switch(Constants.IDX_MAIN_TAB_SCRIPT);
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                    }
                    break;
            }

            return true;
        }
    };

    public void main_tab_fragment_switch(int fragNum) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        Log.d(LOG_TAG, "main_tab_fragment_switch fragNum " + fragNum);

        if(fragNum == Constants.IDX_MAIN_TAB_FILELIST) {
            ((ImageView)findViewById(R.id.iv_tab_file_list)).setImageResource(R.drawable.tab_file_list_press);
            ((ImageView)findViewById(R.id.iv_tab_shadowing)).setImageResource(R.drawable.tab_shadowing_release);
            ((ImageView)findViewById(R.id.iv_tab_script)).setImageResource(R.drawable.tab_script_release);

            fragmentTransaction.replace(R.id.layout_main_tab, new Frag_main_filelist(mContext), Constants.FRAG_MAIN_TAB_FILELIST);
            //fragmentTransaction.addToBackStack(null);
            Constants.frag_main_tab_state = Constants.IDX_MAIN_TAB_FILELIST;
        } else if(fragNum == Constants.IDX_MAIN_TAB_SHADOWING) {
            ((ImageView)findViewById(R.id.iv_tab_file_list)).setImageResource(R.drawable.tab_file_list_release);
            ((ImageView)findViewById(R.id.iv_tab_shadowing)).setImageResource(R.drawable.tab_shadowing_press);
            ((ImageView)findViewById(R.id.iv_tab_script)).setImageResource(R.drawable.tab_script_release);

            fragmentTransaction.replace(R.id.layout_main_tab, new Frag_main_shadowing(mContext), Constants.FRAG_MAIN_TAB_SHADOWING);
            Constants.frag_main_tab_state = Constants.IDX_MAIN_TAB_SHADOWING;
            //fragmentTransaction.addToBackStack(null);
        } else if(fragNum == Constants.IDX_MAIN_TAB_SCRIPT) {
            ((ImageView)findViewById(R.id.iv_tab_file_list)).setImageResource(R.drawable.tab_file_list_release);
            ((ImageView)findViewById(R.id.iv_tab_shadowing)).setImageResource(R.drawable.tab_shadowing_release);
            ((ImageView)findViewById(R.id.iv_tab_script)).setImageResource(R.drawable.tab_script_press);

            fragmentTransaction.replace(R.id.layout_main_tab, new Frag_main_script(mContext), Constants.FRAG_MAIN_TAB_SCRIPT);
            Constants.frag_main_tab_state = Constants.IDX_MAIN_TAB_SCRIPT;
            //fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.commit();
        fm.executePendingTransactions();
    }

    public void rewBookmark() {
/*
        if(repeatEnable == true) {
            Log.d(LOG_TAG, "rewBookmark() repeatEnable is true. rewBookmark() cant't work");
            return;
        }
*/
        int current_progress, compareIndex, rew_progress;
        current_progress= seekBar.getProgress();

        synchronized (bookmarkList) {
            if(bookmarkList.size() <= 0) {
                Log.e(LOG_TAG, "rewBookmark bookmarkList.size() is 0");
                return;
            }

            for (compareIndex = bookmarkList.size() - 1; compareIndex >= 0; compareIndex--) {
                Integer compareProgress = bookmarkList.get(compareIndex).progress;

                if (0 > compareProgress.compareTo(current_progress - 500)) {  // compareProgress 가 current_progress 보다 작으면 음수
                    Log.d(LOG_TAG, "rewBookmark compareIndex " + compareIndex + ", goto prev bookmark_progress : " + bookmarkList.get(compareIndex).progress);
                    rew_progress = bookmarkList.get(compareIndex).progress;
                    MediaPlayerController.sController.seekTo(rew_progress);
                    seekBar.setProgress(rew_progress);
                    break;
                }  else if(compareIndex == 0) {
                    Log.d(LOG_TAG, "rewBookmark compareIndex " + compareIndex + ", goto beginning of file : " + bookmarkList.get(compareIndex).progress);
                    rew_progress = 10;
                    MediaPlayerController.sController.seekTo(rew_progress);
                    seekBar.setProgress(rew_progress);
                }
            }
        }
        return;
    }

    public void ffBookmark() {
/*
        if(repeatEnable == true) {
            Log.d(LOG_TAG, "ffBookmark() repeatEnable is true. ffBookmark() cant't work");
            return;
        }
  */
        int current_progress, compareIndex, ff_progress;
        current_progress = seekBar.getProgress();

        Log.e(LOG_TAG, "ffBookmark bookmarkList.size() is " + bookmarkList.size() + " and Progress " + current_progress);

        synchronized (bookmarkList) {
            if (bookmarkList.size() <= 0) {
                Log.e(LOG_TAG, "ffBookmark bookmarkList.size() is 0");
                return;
            }

            if (current_progress < bookmarkList.get(0).progress) {
                MediaPlayerController.sController.seekTo(bookmarkList.get(0).progress);
                seekBar.setProgress(bookmarkList.get(0).progress);
            } else {
                for (compareIndex = (bookmarkList.size() - 1); compareIndex >= 0; compareIndex--) {
                    Integer compareProgress = bookmarkList.get(compareIndex).progress;

                    if (0 > compareProgress.compareTo(current_progress)) {
                        Log.d(LOG_TAG, "ffBookmark index " + compareIndex + ", compareProgress " + compareProgress + ", current_progress : " + current_progress);
                        if (bookmarkList.size() > compareIndex + 1) {
                            ff_progress = bookmarkList.get(compareIndex + 1).progress;
                            MediaPlayerController.sController.seekTo(ff_progress);
                            seekBar.setProgress(ff_progress);
                        }
                        break;
                    } else if (0 == compareProgress.compareTo(current_progress)) {
                        Log.d(LOG_TAG, "ffBookmark progress and bookmark are same");
                        if (bookmarkList.size() > compareIndex + 1) {
                            ff_progress = bookmarkList.get(compareIndex + 1).progress;
                            MediaPlayerController.sController.seekTo(ff_progress);
                            seekBar.setProgress(ff_progress);
                        }
                        break;
                    }
                }
            }
        }
        return;
    }

    public void setPeriodRepeat() {

        int current_progress, compareIndex, ff_progress;
        current_progress = seekBar.getProgress();
        Log.e(LOG_TAG, "setPeriodRepeat bookmarkList.size() is " + bookmarkList.size() + " and Progress " + current_progress);

        if(bookmarkList.size() == 0) {
            Log.d(LOG_TAG, "setPeriodRepeat() bookmarkList size is 0");
            return;
        }

        synchronized (bookmarkList) {

            for (compareIndex = (bookmarkList.size() - 1); compareIndex >= 0; compareIndex--) {
                Integer compareProgress = bookmarkList.get(compareIndex).progress;

                if (0 > compareProgress.compareTo(current_progress)) {
                    Log.d(LOG_TAG, "setPeriodRepeat index " + compareIndex + ", compareProgress " + compareProgress + ", current_progress : " + current_progress);
                    mPeriodRepeatFrom = compareProgress;

                    if (bookmarkList.size() > compareIndex + 1) {
                        mPeriodRepeatTo = bookmarkList.get(compareIndex + 1).progress;
                    } else {
                        mPeriodRepeatTo = seekBar.getMax();
                    }
                    break;
                } else if (compareIndex == 0) {
                    if (0 < compareProgress.compareTo(current_progress)) {
                        Log.d(LOG_TAG, "setPeriodRepeat first bookmark case");
                        mPeriodRepeatTo = bookmarkList.get(0).progress;
                        mPeriodRepeatFrom = 1;
                        break;
                    }
                }
            }
        }

        SeekBar seekbar = (SeekBar) findViewById(R.id.seekbar);

        int start_x = (int)( seekBar.getPaddingLeft() + ( (float)mPeriodRepeatFrom / (float)seekBar.getMax() ) * ((float)seekBar.getWidth() - seekBar.getPaddingLeft() -seekBar.getPaddingRight() ) );
        int end_x = (int) (seekBar.getPaddingLeft() + ( (float)mPeriodRepeatTo / (float)seekBar.getMax() ) * ((float)seekBar.getWidth() - seekBar.getPaddingLeft() -seekBar.getPaddingRight() ) );
        int top_y = 0 + 30;
        int bottom_y = seekbar.getHeight() - 30;

        Log.d(LOG_TAG, "setPeriodRepeat repeatFrom " + mPeriodRepeatFrom + ", repeatTo " + mPeriodRepeatTo );
        Log.d(LOG_TAG, "setPeriodRepeat start_x " + start_x + ", end_x " + end_x + ", top_y " + top_y + ", bottom_y " + bottom_y);
        Log.d(LOG_TAG, "setPeriodRepeat getPaddingLeft " + seekBar.getPaddingLeft() + ", getLeft() " + seekbar.getThumb().getMinimumWidth());

        Bitmap bitmap = Bitmap.createBitmap(end_x - start_x , seekbar.getHeight(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(80);

        Canvas canvas = new Canvas(bitmap);
        //canvas.drawColor(Color.RED);   // 이거 풀면 전체 canvas 영역이 빨간색으로 그려짐

        canvas.drawRect(0, top_y, end_x - start_x, bottom_y, paint);

        RelativeLayout rl_seekbar = (RelativeLayout) findViewById(R.id.rl_seekbar);

        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT );
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        imageView.setLayoutParams(layoutParams);
        imageView.setX(start_x);

        if( (mPeriodRepeatMode == true) && (mPeriodRepeatIV.getX() == imageView.getX()) ) {
            mPeriodRepeatMode = false;
            rl_seekbar.removeView(mPeriodRepeatIV);
            return;
        }

        rl_seekbar.addView(imageView);
        rl_seekbar.removeView(mPeriodRepeatIV);
        mPeriodRepeatIV = imageView;
        mPeriodRepeatMode = true;
        //MediaPlayerController.sController.setPeroidRepeat(repeatFrom,repeatTo);
    }

    public void setBookmark() {
        ImageView iv = new ImageView(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT );

        RelativeLayout rl_seekbar = (RelativeLayout) findViewById(R.id.rl_seekbar);
        SeekBar seekbar = (SeekBar) findViewById(R.id.seekbar);

        layoutParams.width = 40;
        layoutParams.height = seekbar.getHeight() - 40;

        float x = seekBar.getPaddingLeft() + ( (float)seekBar.getProgress() / (float)seekBar.getMax() ) * ((float)seekBar.getWidth() - seekBar.getPaddingLeft() -seekBar.getPaddingRight() )
                - layoutParams.width/2;

        Log.d(LOG_TAG, "setBookmark x " + x + ", calc " + ( (float)seekBar.getProgress() / (float)seekBar.getMax() ) * ((float)seekBar.getWidth() - seekBar.getPaddingLeft() -seekBar.getPaddingRight() ));

        iv.setBackground(getResources().getDrawable(R.drawable.iv_bookmark));
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        iv.setLayoutParams(layoutParams);
        iv.setX(x);

        int compareIndex, i, j, new_bookmark_progress;
        new_bookmark_progress = seekBar.getProgress();

        Log.d(LOG_TAG, "setBookmark set progress " + new_bookmark_progress );

        BookMark bookMark = new BookMark();
        bookMark.progress = new_bookmark_progress;
        bookMark.position_x = x;
        bookMark.imageView = iv;

        synchronized (bookmarkList) {
            if (bookmarkList.size() == 0) { // bookmark 를 처음 만들었을 경우
                bookMark.progress = new_bookmark_progress;
                bookmarkList.add(bookMark);
                Log.d(LOG_TAG, "setBookmark initial add progress " + new_bookmark_progress + " bookmarkList.size() " + bookmarkList.size());
            } else {   // bookmark 를 두개 이상 만들 경우
                Log.d(LOG_TAG, "setBookmark before bookmarkList.size() " + bookmarkList.size());
                // 뒤쪽에 있는 book mark 부터 확인하며 앞으로 오며 오름차순 정렬하여 add 한다.
                for (compareIndex = (bookmarkList.size() - 1); compareIndex >= 0; compareIndex--) {
                    Integer compareProgress = bookmarkList.get(compareIndex).progress;

                    if (0 > compareProgress.compareTo(new_bookmark_progress)) { // 같으면 0,  compareProgress 가 더 작으면 음수, 더 크면 양수
                        Log.d(LOG_TAG, "setBookmark add a index " + (compareIndex+1) + ", new_bookmark_progress : " + new_bookmark_progress);

                        bookmarkList.add(compareIndex+1, bookMark);
                        break;
                    } else if(0 == compareProgress.compareTo(new_bookmark_progress)) {
                        Log.e(LOG_TAG, "samebookmark can't be set");
                        return;
                    } else if(compareIndex == 0) {
                        bookmarkList.add(0, bookMark);
                        Log.d(LOG_TAG, "setBookmark before add a vv index 0 " + ", new_bookmark_progress : " + new_bookmark_progress);
                    }
                }
            }
        }

        DataBases.mPLM_DB.updateBookmark(bookmarkList);
        rl_seekbar.addView(iv);
        mBookmarkViewlist.add(iv);
    }

    public void prevBookmarkRemove() {

        int current_progress, compareIndex;
        current_progress= seekBar.getProgress();

        synchronized (bookmarkList) {
            if(bookmarkList.size() <= 0) {
                Log.e(LOG_TAG, "rewBookmark bookmarkList.size() is 0");
                return;
            }

            for (compareIndex = bookmarkList.size() - 1; compareIndex >= 0; compareIndex--) {
                Integer compareProgress = bookmarkList.get(compareIndex).progress;

                if (0 > compareProgress.compareTo(current_progress)) {  // compareProgress 가 current_progress 보다 작으면 음수
                    Log.d(LOG_TAG, "prevBookmarkRemove compareIndex " + compareIndex);

                    Log.d(LOG_TAG, "prevBookmarkRemove before size " + bookmarkList.size());
                    for(int i = 0 ; i< bookmarkList.size(); i++)
                        Log.d(LOG_TAG, "prevBookmarkRemove index i" + i + ", " + bookmarkList.get(i) );

                    bookmarkList.remove(compareIndex);

                    Log.d(LOG_TAG, "prevBookmarkRemove after size " + bookmarkList.size());
                    for(int i = 0 ; i< bookmarkList.size(); i++)
                        Log.d(LOG_TAG, "prevBookmarkRemove index i" + i + ", " + bookmarkList.get(i) );

                    DataBases.mPLM_DB.updateBookmark(bookmarkList);
                    mMainHandler.sendEmptyMessage(MediaPlayerController.HANDLER_DRAW_BOOKMARK);
                    break;
                }
            }
        }
        return;
    }

    public void nextBookmarkRemove() {

        int current_progress, compareIndex;
        current_progress= seekBar.getProgress();

        synchronized (bookmarkList) {
            if(bookmarkList.size() <= 0) {
                Log.e(LOG_TAG, "nextBookmarkRemove bookmarkList.size() is 0");
                return;
            }

            for (compareIndex = 0; compareIndex <= bookmarkList.size() - 1; compareIndex++) {
                Integer compareProgress = bookmarkList.get(compareIndex).progress;

                if (0 < compareProgress.compareTo(current_progress)) {  // compareProgress 가 current_progress 보다 작으면 음수
                    Log.d(LOG_TAG, "nextBookmarkRemove compareIndex " + compareIndex);
                    bookmarkList.remove(compareIndex);
                    DataBases.mPLM_DB.updateBookmark(bookmarkList);
                    mMainHandler.sendEmptyMessage(MediaPlayerController.HANDLER_DRAW_BOOKMARK);
                    break;
                }
            }
        }
        return;
    }

    final Handler mMainHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==MediaPlayerController.HANDLER_DRAW_BOOKMARK){
                Log.d(LOG_TAG, "handler HANDLER_DRAW_BOOKMARK");

                if(mBookmarkInit == false) {
                    mBookmarkInit = true;
                    mMainHandler.sendEmptyMessageDelayed(MediaPlayerController.HANDLER_DRAW_BOOKMARK, 500);
                    return;
                }

                bookmarkList.clear();
                bookmarkList = DataBases.mPLM_DB.getBookmarkList();

                RelativeLayout rl_seekbar = (RelativeLayout) findViewById(R.id.rl_seekbar);
                SeekBar seekbar = (SeekBar) findViewById(R.id.seekbar);

                Log.d(LOG_TAG, "draw bookmark list - size of mBookmarkViewlist " + mBookmarkViewlist.size() );

                if(mBookmarkViewlist.size() > 0) {  // 기존에 seekbar 에 그렸던 bookmark 지우기
                    for(int i = 0; i < mBookmarkViewlist.size(); i++) {
                        rl_seekbar.removeView(mBookmarkViewlist.get(i));
                    }
                    for(int i = 0; i < mBookmarkViewlist.size(); i++) {
                        mBookmarkViewlist.remove(i);
                    }
                }

                Log.d(LOG_TAG, "draw bookmark list - size of bookmarkList " + bookmarkList.size() );

                if(bookmarkList.size() > 0 ) {
                    for (int i = 0; i < bookmarkList.size(); i++) {
                        ImageView iv = new ImageView(mContext);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT);

                        layoutParams.width = 40;
                        layoutParams.height = seekbar.getHeight() - 40;
                        float x = seekBar.getPaddingLeft() + ((float) bookmarkList.get(i).progress / (float) seekBar.getMax()) *
                                ((float) seekBar.getWidth() - seekBar.getPaddingLeft() - seekBar.getPaddingRight()) - layoutParams.width / 2;

                        Log.d(LOG_TAG, "draw bookmark list -  progress " + bookmarkList.get(i).progress);
                        Log.d(LOG_TAG, "draw bookmark getPaddingLeft " + seekBar.getPaddingLeft() + ", seekBar.getMax() " + seekBar.getMax() +
                                ", seekBar.getWidth() " + seekBar.getWidth() + ", seekBar.getPaddingLeft() " + seekBar.getPaddingLeft() + ", seekBar.getPaddingRight() "
                                + seekBar.getPaddingRight() + ", layoutParams.width " + layoutParams.width);

                        //Log.d(LOG_TAG, "draw bookmark list - x " + x + ", calc " + ((float) bookmarkList.get(i).progress / (float) seekBar.getMax()) *
                        //        ((float) seekBar.getWidth() - seekBar.getPaddingLeft() - seekBar.getPaddingRight()));

                        iv.setBackground(getResources().getDrawable(R.drawable.iv_bookmark));
                        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                        iv.setLayoutParams(layoutParams);
                        iv.setX(x);
                        rl_seekbar.addView(iv);
                        mBookmarkViewlist.add(iv);
                    }
                }
            }
        }
    };

 /*   //startActivityForResult 로 호출한 activity 종료시에 호출되는 callback
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case 0:
                fileListParcelable = data.getParcelableArrayListExtra("filePathList");
                Log.d(LOG_TAG, "Get Intent. fileListParcelable size is " + fileListParcelable.size());
                Log.d(LOG_TAG, "Get Intent. file path : " + fileListParcelable.get(0).getFullPath());

                playListViewUpdate(fileListParcelable);

//                File file = new File(fileListParcelable.get(0).getFilePath());
//                Log.d(LOG_TAG,"onActivityResult test start ------------------------------");
//                Log.d(LOG_TAG,"file.getAbsolutePath() : " + file.getAbsolutePath());
//                Log.d(LOG_TAG,"file.getPath() : " + file.getPath());
//                Log.d(LOG_TAG,"file.getName() : " + file.getName());
//                Log.d(LOG_TAG,"file.getParent() : " + file.getParent());
//                Log.d(LOG_TAG,"onActivityResult test end ------------------------------");

//                Get Intent. file path : /vendor/lib/lib-imsSDP.so
//                onActivityResult test start ------------------------------
//                file.getAbsolutePath() : /vendor/lib/lib-imsSDP.so
//                file.getPath() : /vendor/lib/lib-imsSDP.so
//                file.getName() : lib-imsSDP.so
//                file.getParent() : /vendor/lib
//                onActivityResult test end ------------------------------
                break;

        }
    }
*/

/*
    int playListViewUpdate(ArrayList<FileParcelable> filelist) {

        if ( filelist.size() == 0 ) {
            Log.d(LOG_TAG, "playListViewUpdate fileListParcelable is null");
        }

        Log.d(LOG_TAG, "playListViewUpdate ");
        mAdapterMainPlayList.resetItems();

        if(filelist.size() > 0) {
            for (int i = 0; i < filelist.size(); i++) {
                Log.d(LOG_TAG, "playListViewUpdate addItem " + filelist.get(i).getFileName());
                Log.d(LOG_TAG, "playListViewUpdate addItem " + filelist.get(i).getFullPath());
                mAdapterMainPlayList.addItem(filelist.get(i).getFileName());
            }

            listview_playList.setAdapter(mAdapterMainPlayList);

            MediaPlayerController.sController.setPlayFile(filelist.get(0).getFullPath());
            MediaPlayerController.sController.setDuration();
            Constants.FILE_READY_STATUS = Constants.FILE_READY;
        }

        return E_SUCCESS;
    }
*/

/*
    LongPressChecker.OnLongPressListener mOnLongPressListener =  new LongPressChecker.OnLongPressListener() {
        @Override
        public void onLongPressed(View view, MotionEvent event) {
            //to do
            switch(view.getId()){
                case R.id.button_ff:
            if(Constants.FILE_READY_STATUS == Constants.FILE_READY)
                MediaPlayerController.sController.ffPlay(200);
            break;

                case R.id.btn_set_bookmark_ab:
                    if(Constants.FILE_READY_STATUS == Constants.FILE_READY)
                        Log.d(LOG_TAG, "OnLongPressListener " + view.getId());
                    //MediaPlayerController.sController.rewPlay(200);
                    break;
            }
        }
    };
*/

    SeekBar.OnSeekBarChangeListener mSeekBarChangeListner = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            Log.d(LOG_TAG, "onProgressChanged fromUser " + fromUser + ", progress " + progress);
            if(fromUser) {
                Log.d(LOG_TAG, "onProgressChanged Constants.PLAYER_STATUS " + Constants.PLAYER_STATUS );

                if(Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PLAY)
                    MediaPlayerController.sController.seekTo(progress);
                else if( (progress >= 0) && (Constants.FILE_READY_STATUS == Constants.FILE_READY)) {
                    Date date = new Date((long)progress);
                    DateFormat formatter = new SimpleDateFormat("mm:ss");
                    String dateFormatted = formatter.format(date);
                    tv_elapsed_time.setText(dateFormatted);
                }
            } else {
                if(mPeriodRepeatMode == true) {
                    if ((progress > mPeriodRepeatTo) && (progress < mPeriodRepeatTo + 100)) {
                        MediaPlayerController.sController.seekTo(mPeriodRepeatFrom);
                    }
                }

                //Log.d(LOG_TAG, "onProgressChanged not fromUser progress " + progress );
            }
        }
    };


    void getPermission() {
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck3 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED ||
                permissionCheck3 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.RECORD_AUDIO},
                    0);
        }
    }



}



