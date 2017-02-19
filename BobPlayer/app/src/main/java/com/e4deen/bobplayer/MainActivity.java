package com.e4deen.bobplayer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.e4deen.bobplayer.listview.PlayList_Adapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
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
    ListView listview_playList;
    public PlayList_Adapter playList_adapter;
    ImageButton btn_rew, btn_ff, btn_speed_dn, btn_speed_up, btn_bookmark_rew, btn_bookmark_ff, btn_set_repeat_period, btn_add_repeat_ab_bookmark, btn_file_search, btn_loopback, btn_set_bookmark;
    LongPressChecker mLongPressChecker;
    Vibe mVibe;
    Context mContext;
    MediaPlayerController mMediaPlayerController;
    CircleButton Image_Circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//--------------------------------- Admob Start ---------------------------------------------//
        MobileAds.initialize(this, "ca-app-pub-6490716774426103/8782548872");
        mAdView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
//--------------------------------- Admob End ---------------------------------------------//
//----------------------------- File List View Start --------------------------------------//
        listview_playList = (ListView) findViewById(R.id.playList);
        playList_adapter = new PlayList_Adapter();
        playList_adapter.resetItems();
        listview_playList.setAdapter(playList_adapter);
        playListViewInit();
//----------------------------- File List View End --------------------------------------//
//----------------------------- ETC Start --------------------------------------//
        mContext = getApplicationContext();
        mMediaPlayerController = new MediaPlayerController(mContext);

        btn_rew = (ImageButton) findViewById(R.id.btn_rew);
        btn_ff = (ImageButton) findViewById(R.id.btn_ff);
        btn_speed_dn = (ImageButton) findViewById(R.id.btn_speed_dn);
        btn_speed_up = (ImageButton) findViewById(R.id.btn_speed_up);
        btn_bookmark_rew = (ImageButton) findViewById(R.id.btn_bookmark_rew);
        btn_bookmark_ff = (ImageButton) findViewById(R.id.btn_bookmark_ff);
        btn_set_repeat_period = (ImageButton) findViewById(R.id.btn_set_repeat_period);
        btn_add_repeat_ab_bookmark = (ImageButton) findViewById(R.id.btn_add_repeat_ab_bookmark);

        btn_rew.setOnTouchListener(mTouchEvent);
        btn_ff.setOnTouchListener(mTouchEvent);
        btn_speed_dn.setOnTouchListener(mTouchEvent);
        btn_speed_up.setOnTouchListener(mTouchEvent);
        btn_bookmark_rew.setOnTouchListener(mTouchEvent);
        btn_bookmark_ff.setOnTouchListener(mTouchEvent);
        btn_set_repeat_period.setOnTouchListener(mTouchEvent);
        btn_add_repeat_ab_bookmark.setOnTouchListener(mTouchEvent);

        mLongPressChecker = new LongPressChecker(this);
//        mLongPressChecker.setOnLongPressListener(mOnLongPressListener);

        mVibe = new Vibe(mContext);
        Image_Circle = (CircleButton) findViewById(R.id.CircleButton);

        Image_Circle.setMediaPlayerController(mMediaPlayerController);

        getPermission();

//----------------------------- ETC Start --------------------------------------//
    }

    int playListViewInit() {

        /*
        if ( 1 )
        {
            // Play List 초기값을 읽어와서 Playlist list view Adapter 에 add 하는 부분
        }
        */

        Log.d(LOG_TAG, "playListViewInit ");

        playList_adapter.addItem("add new items 1");
        playList_adapter.addItem("add new items 2");
        playList_adapter.addItem("add new items 3");
        playList_adapter.addItem("add new items 4");
        playList_adapter.addItem("add new items 5");
        playList_adapter.addItem("add new items 6");
        playList_adapter.addItem("add new items 7");
        playList_adapter.addItem("add new items 8");
        playList_adapter.addItem("add new items 9");
        playList_adapter.addItem("add new items 10");

        return E_SUCCESS;
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

        btn_loopback = (ImageButton) findViewById(R.id.btn_loopback);
        btn_file_search = (ImageButton) findViewById(R.id.btn_file_search);

        btn_file_search.setOnTouchListener(mTouchEvent);
        btn_loopback.setOnTouchListener(mTouchEvent);

        Image_Circle.init();
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
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }


    View.OnTouchListener mTouchEvent = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
//            Log.d(LOG_TAG,"onTouch id " + v.getId());

            int action = event.getAction();
            int id = v.getId();

            if (action == MotionEvent.ACTION_DOWN) {
                mVibe.vibration(80);
            }

            switch (id) {
                case R.id.btn_rew:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            btn_rew.setImageDrawable(getResources().getDrawable(R.drawable.ic_common_press));
                            //mMediaPlayerController.  ();
                            break;
                        case MotionEvent.ACTION_UP:
                            btn_rew.setImageDrawable(getResources().getDrawable(R.drawable.ic_rew_release));
                            break;
                    }
                    break;

                case R.id.btn_ff:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            btn_ff.setImageDrawable(getResources().getDrawable(R.drawable.ic_common_press));
                            //mMediaPlayerController.  ();
                            break;
                        case MotionEvent.ACTION_UP:
                            btn_ff.setImageDrawable(getResources().getDrawable(R.drawable.ic_ff_release));
                            break;
                    }
                    break;

                case R.id.btn_speed_up:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            btn_speed_up.setImageDrawable(getResources().getDrawable(R.drawable.ic_common_press));
                            mMediaPlayerController.speedUp();
                            break;
                        case MotionEvent.ACTION_UP:
                            btn_speed_up.setImageDrawable(getResources().getDrawable(R.drawable.ic_speed_up_release));
                            break;
                    }
                    break;
                case R.id.btn_speed_dn:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            btn_speed_dn.setImageDrawable(getResources().getDrawable(R.drawable.ic_common_press));
                            mMediaPlayerController.speedDown();
                            break;
                        case MotionEvent.ACTION_UP:
                            btn_speed_dn.setImageDrawable(getResources().getDrawable(R.drawable.ic_speed_down_release));
                            break;
                    }
                    break;
/*
                case R.id.btn_shuffle:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            _Shuffle.setImageDrawable(getResources().getDrawable(R.drawable.ic_shffule_pressed));
//                            LinearLayout temp = (LinearLayout) findViewById(R.id.seekBarLay);
//                            Log.d(LOG_TAG, "temp getLeft " + temp.getLeft() + ", getRight " + temp.getRight() + ", getWidth " + temp.getWidth());
                            break;
                        case MotionEvent.ACTION_UP:

                            if(Constants.SHUFFLE_STATE == Constants.NOT_SUFFLE) {
                                _Shuffle.setImageDrawable(getResources().getDrawable(R.drawable.ic_shffule));
                            }
                            else {
                                _Shuffle.setImageDrawable(getResources().getDrawable(R.drawable.ic_shffule_pressed));

                                if(Constants.REPEAT_STATE == Constants.REPEAT) {
                                    _Set_Repeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_period_repeat));
                                }
                            }
                            break;
                    }
                    break;

                case R.id.btn_repeat_play:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            Button_Set_Repeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_period_repeat_pressed));
                            if(Constants.FILE_READY_STATUS == Constants.FILE_READY) {
                                mMediaPlayerController.setRepeat();
                            } else {
                                Log.e(LOG_TAG, "Repeat button is pressed. But FILE_READY_STATUS is FILE_NOT_READY.");
                                Toast.makeText(mContext, "File is not ready", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if(Constants.REPEAT_STATE == Constants.NOT_REPEAT) {
                                Button_Set_Repeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_period_repeat));
                            }
                            else {
                                Button_Set_Repeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_period_repeat_pressed));

                                if(Constants.SHUFFLE_STATE == Constants.SHUFFLE) {
                                    Button_Shuffle.setImageDrawable(getResources().getDrawable(R.drawable.ic_shffule));
                                }
                            }
                            break;
                    }
                    break;

                case R.id.btn_file_search:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            btn_file_search.setImageDrawable(getResources().getDrawable(R.drawable.ic_filelist_pressed));
                            Intent intent = new Intent(mContext, FileSearchActivity.class);
                            startActivityForResult(intent, 0);
                            break;
                        case MotionEvent.ACTION_UP:
                            btn_file_search.setImageDrawable(getResources().getDrawable(R.drawable.ic_filelist));
                            break;
                    }
                    break;
*/


                case R.id.btn_add_repeat_ab_bookmark:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            btn_add_repeat_ab_bookmark.setImageDrawable(getResources().getDrawable(R.drawable.ic_common_press));
                            //setBookmark();
                            //setRepeatPeriod_A();
                            //setRepeatPeriod_B();
                            mLongPressChecker.deliverMotionEvent(v, event);

                            break;
                        case MotionEvent.ACTION_UP:
                            btn_add_repeat_ab_bookmark.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_repeat_bookmark_release));
                            mLongPressChecker.deliverMotionEvent(v, event);
                            break;
                    }
                    break;
                case R.id.btn_set_repeat_period:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            btn_set_repeat_period.setImageDrawable(getResources().getDrawable(R.drawable.ic_common_press));
//                            set_period_repeat();
                            mLongPressChecker.deliverMotionEvent(v, event);
                            break;

                        case MotionEvent.ACTION_UP:
                            btn_set_repeat_period.setImageDrawable(getResources().getDrawable(R.drawable.ic_set_repeat_period_release));
                            mLongPressChecker.deliverMotionEvent(v, event);
                            break;
                    }
                    break;

/*
                case R.id.btn_set_bookmark:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            btn_set_bookmark.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_pressed));
                            setBookmark();
                            break;
                        case MotionEvent.ACTION_UP:
                            btn_set_bookmark.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark));
                            break;
                    }
                    break;
*/

                case R.id.btn_bookmark_rew:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            btn_bookmark_rew.setImageDrawable(getResources().getDrawable(R.drawable.ic_common_press));
                            //rewBookmark();
                            break;
                        case MotionEvent.ACTION_UP:
                            btn_bookmark_rew.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_rew_release));
                            break;
                    }
                    break;
                case R.id.btn_bookmark_ff:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            btn_bookmark_ff.setImageDrawable(getResources().getDrawable(R.drawable.ic_common_press));
                            //ffBookmark();
                            break;
                        case MotionEvent.ACTION_UP:
                            btn_bookmark_ff.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_ff_release));
                            break;
                    }
                    break;
            }
            return true;
        }

    };



/*
    LongPressChecker.OnLongPressListener mOnLongPressListener =  new LongPressChecker.OnLongPressListener() {
        @Override
        public void onLongPressed(View view, MotionEvent event) {
            //to do
            switch(view.getId()){
                case R.id.button_ff:
            if(Constants.FILE_READY_STATUS == Constants.FILE_READY)
                mMediaPlayerController.ffPlay(200);
            break;

                case R.id.btn_set_bookmark_ab:
                    if(Constants.FILE_READY_STATUS == Constants.FILE_READY)
                        Log.d(LOG_TAG, "OnLongPressListener " + view.getId());
                    //mMediaPlayerController.rewPlay(200);
                    break;
            }
        }
    };
*/

    void getPermission() {
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    0);
        }
    }



}



