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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.e4deen.bobplayer.listview.PlayList_Adapter;
import com.e4deen.bobplayer.listview.TestCirCleButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.e4deen.bobplayer.datatype.FileParcelable;
import com.e4deen.bobplayer.listview.PlayList_Adapter;
import com.e4deen.bobplayer.datatype.BookMark;
import org.w3c.dom.Text;

import java.util.ArrayList;

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
    ImageButton btn_rew, btn_ff, btn_speed_dn, btn_speed_up, btn_bookmark_rew, btn_bookmark_ff, btn_set_repeat_period,
            btn_set_bookmark, btn_file_search, btn_loopback, btn_play_pause;
    LongPressChecker mLongPressChecker;
    ArrayList<FileParcelable> fileListParcelable;
    Vibe mVibe;
    Context mContext;
    MediaPlayerController mMediaPlayerController;
    SeekBar seekBar;
    TextView tv_total_duration, tv_elapsed_time, tv_play_speed;
    EditText et_rew_ff_time;
    ArrayList<BookMark> bookmarkList = new ArrayList<BookMark>();

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
        btn_set_bookmark = (ImageButton) findViewById(R.id.btn_set_bookmark);

        tv_elapsed_time = (TextView) findViewById(R.id.tv_elapsed_time);
        tv_total_duration = (TextView) findViewById(R.id.tv_total_duration);
        tv_play_speed = (TextView) findViewById(R.id.tv_play_speed);

        et_rew_ff_time = (EditText) findViewById(R.id.et_rew_ff_time);

        btn_rew.setOnTouchListener(mTouchEvent);
        btn_ff.setOnTouchListener(mTouchEvent);
        btn_speed_dn.setOnTouchListener(mTouchEvent);
        btn_speed_up.setOnTouchListener(mTouchEvent);
        btn_bookmark_rew.setOnTouchListener(mTouchEvent);
        btn_bookmark_ff.setOnTouchListener(mTouchEvent);
        btn_set_repeat_period.setOnTouchListener(mTouchEvent);
        btn_set_bookmark.setOnTouchListener(mTouchEvent);
        mLongPressChecker = new LongPressChecker(this);
//        mLongPressChecker.setOnLongPressListener(mOnLongPressListener);

        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(mSeekBarChangeListner);

        mVibe = new Vibe(mContext);

        mMediaPlayerController.setViews(seekBar, tv_elapsed_time, tv_total_duration, tv_play_speed);
        playListViewInit();

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

        RelativeLayout RL_Circle = (RelativeLayout) findViewById(R.id.RL_Circle);

        Log.d(LOG_TAG, "onCreateOptionsMenu  RL_Circle.getWidth() " + RL_Circle.getWidth() + ", RL_Circle.getHeight() " + RL_Circle.getHeight());
        CircleButton btn_Circle = new CircleButton(this, RL_Circle.getWidth(), RL_Circle.getHeight() );
        btn_Circle.setMediaPlayerController(mMediaPlayerController);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        RL_Circle.addView(btn_Circle, lp);

//        RL_Circle.addView(btn_TestCircle, lp);

//----------center bar -----------------------------------
        /*
        ImageView iv_center_bar = new ImageView(this);
        iv_center_bar.setId(R.id.iv_center_bar_id);
        iv_center_bar.setImageResource(R.drawable.center_bar);

        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        lp.setMargins(0,0,0,0);
        RL_Circle.addView(iv_center_bar, lp);
        */
//----------center circle -----------------------------------
        btn_play_pause = new ImageButton(this);
        btn_play_pause.setImageResource(R.drawable.btn_play);
        //btn_play_pause.setImageResource(R.drawable.btn_pause);
        btn_play_pause.setId(R.id.btn_play_pause_id);

        btn_play_pause.setOnTouchListener(mTouchEvent);
        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //lp.addRule(RelativeLayout.ABOVE, R.id.iv_center_bar_id );
        //lp.addRule(RelativeLayout.CENTER_HORIZONTAL );
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
                            btn_rew.setImageResource(R.drawable.ic_common_press);
                            if(Constants.FILE_READY_STATUS == Constants.FILE_READY)
                                mMediaPlayerController.rewPlay(Integer.valueOf(et_rew_ff_time.getText().toString()) * Constants.SEC);
                            break;
                        case MotionEvent.ACTION_UP:
                            btn_rew.setImageResource(R.drawable.ic_rew_release);
                            break;
                    }
                    break;

                case R.id.btn_ff:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            btn_ff.setImageResource(R.drawable.ic_common_press);
                            if(Constants.FILE_READY_STATUS == Constants.FILE_READY)
                                mMediaPlayerController.ffPlay(Integer.valueOf(et_rew_ff_time.getText().toString()) * Constants.SEC );
                            break;
                        case MotionEvent.ACTION_UP:
                            btn_ff.setImageResource(R.drawable.ic_ff_release);
                            break;
                    }
                    break;

                case R.id.btn_speed_up:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            btn_speed_up.setImageResource(R.drawable.ic_common_press);
                            if(Constants.FILE_READY_STATUS == Constants.FILE_READY)
                                mMediaPlayerController.speedUp();
                            break;
                        case MotionEvent.ACTION_UP:
                            btn_speed_up.setImageResource(R.drawable.ic_speed_up_release);
                            break;
                    }
                    break;
                case R.id.btn_speed_dn:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            btn_speed_dn.setImageResource(R.drawable.ic_common_press);
                            if(Constants.FILE_READY_STATUS == Constants.FILE_READY)
                                mMediaPlayerController.speedDown();
                            break;
                        case MotionEvent.ACTION_UP:
                            btn_speed_dn.setImageResource(R.drawable.ic_speed_down_release);
                            break;
                    }
                    break;
                case R.id.btn_play_pause_id:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            btn_play_pause.setImageResource(R.drawable.ic_common_press);
                            if(Constants.FILE_READY_STATUS == Constants.FILE_READY) {
                                if(Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PLAY) {
                                    Log.d(LOG_TAG, "btn_play_pause_id call pausePlay()");
                                    mMediaPlayerController.pausePlay();
                                    Constants.PLAYER_STATUS = Constants.PLAYER_STATUS_PAUSE;
                                } else {
                                    Log.d(LOG_TAG, "btn_play_pause_id call startPlay()");
                                    mMediaPlayerController.startPlay();
                                    Constants.PLAYER_STATUS = Constants.PLAYER_STATUS_PLAY;
                                }
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if(Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PLAY) {
                                btn_play_pause.setImageResource(R.drawable.btn_pause);
                            } else {
                                btn_play_pause.setImageResource(R.drawable.btn_play);
                            }
                            break;
                    }
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
                                mMediaPlayerController.setRepeat();
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
                            btn_file_search.setImageResource(R.drawable.ic_file_search);
                            Intent intent = new Intent(mContext, FileSearchActivity.class);
                            startActivityForResult(intent, 0);
                            break;
                        case MotionEvent.ACTION_UP:
                            btn_file_search.setImageResource(R.drawable.ic_file_search);
                            break;
                    }
                    break;



                case R.id.btn_set_bookmark:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            btn_set_bookmark.setImageResource(R.drawable.ic_common_press);
                            setBookmark();
                            //setRepeatPeriod_A();
                            //setRepeatPeriod_B();
                            mLongPressChecker.deliverMotionEvent(v, event);

                            break;
                        case MotionEvent.ACTION_UP:
                            btn_set_bookmark.setImageResource(R.drawable.ic_set_bookmark_release);
                            mLongPressChecker.deliverMotionEvent(v, event);
                            break;
                    }
                    break;
                case R.id.btn_set_repeat_period:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            btn_set_repeat_period.setImageResource(R.drawable.ic_common_press);
//                            set_period_repeat();
                            mLongPressChecker.deliverMotionEvent(v, event);
                            break;

                        case MotionEvent.ACTION_UP:
                            btn_set_repeat_period.setImageResource(R.drawable.ic_set_repeat_period_release);
                            mLongPressChecker.deliverMotionEvent(v, event);
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
                            btn_bookmark_rew.setImageResource(R.drawable.ic_common_press);
                            //rewBookmark();
                            break;
                        case MotionEvent.ACTION_UP:
                            btn_bookmark_rew.setImageResource(R.drawable.ic_bookmark_rew_release);
                            break;
                    }
                    break;
                case R.id.btn_bookmark_ff:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            btn_bookmark_ff.setImageResource(R.drawable.ic_common_press);
                            //ffBookmark();
                            break;
                        case MotionEvent.ACTION_UP:
                            btn_bookmark_ff.setImageResource(R.drawable.ic_bookmark_ff_release);
                            break;
                    }
                    break;
            }
            return true;
        }

    };


    public void setBookmark() {
        ImageView iv = new ImageView(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT );

        RelativeLayout rl_seekbar = (RelativeLayout) findViewById(R.id.rl_seekbar);

        layoutParams.width = 30;
        layoutParams.height = 100;

        float x = seekBar.getPaddingLeft() + ( (float)seekBar.getProgress() / (float)seekBar.getMax() ) * ((float)seekBar.getWidth() - seekBar.getPaddingLeft() -seekBar.getPaddingRight() )
                - layoutParams.width/2;
//        float y = seekBar.getPaddingLeft() + seekBar.getThumb().getBounds().left;  // same

        Log.d(LOG_TAG, "x " + x + ", calc " + ( (float)seekBar.getProgress() / (float)seekBar.getMax() ) * ((float)seekBar.getWidth() - seekBar.getPaddingLeft() -seekBar.getPaddingRight() ));
//        Log.d(LOG_TAG, "y " + x + ", seekBar.getThumb().getBounds().left " + seekBar.getThumb().getBounds().left );
/*
        Log.d(LOG_TAG, "setBookmark getPaddingLeft() " + seekBar.getPaddingLeft() + ", getWidth " + seekBar.getWidth() +
                ", getProgress() " + seekBar.getProgress() + ", getMax " + seekBar.getMax() + ", x " + x + ", progress/max " + (float)seekBar.getProgress() / (float)seekBar.getMax() +
                " , (progress/max) * width " + ( (float)seekBar.getProgress() / (float)seekBar.getMax() ) * (float)seekBar.getWidth() );
*/
        iv.setBackground(getResources().getDrawable(R.drawable.iv_bookmark));
        //layoutParams.addRule(RelativeLayout.ALIGN_BASELINE, R.id.seekbar);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        iv.setLayoutParams(layoutParams);
        //iv.setScaleType(ImageView.ScaleType.FIT_END);
        //iv.setPadding(0, 0, 0, 30);
        iv.setX(x);

        int compareIndex, i, j, bookmark_progress;
        bookmark_progress = seekBar.getProgress();

        Log.d(LOG_TAG, "setBookmark set progress " + bookmark_progress );

        BookMark bookMark = new BookMark();
        bookMark.progress = bookmark_progress;
        bookMark.position_x = x;
        bookMark.imageView = iv;

        synchronized (bookmarkList) {
            if (bookmarkList.size() == 0) { // bookmark 를 처음 만들었을 경우
                bookMark.progress = bookmark_progress;
                bookmarkList.add(bookMark);
            } else {   // bookmark 를 두개 이상 만들 경우
                Log.d(LOG_TAG, "setBookmark before bookmarkList.size() " + bookmarkList.size());
                // 뒤쪽에 있는 book mark 부터 확인하며 앞으로 오며 오름차순 정렬하여 add 한다.
                for (compareIndex = (bookmarkList.size() - 1); compareIndex >= 0; compareIndex--) {
                    Integer compareProgress = bookmarkList.get(compareIndex).progress;

                    if (0 > compareProgress.compareTo(bookmark_progress)) {
                        Log.d(LOG_TAG, "setBookmark before add a index " + compareIndex+1 + ", bookmark_progress : " + bookmark_progress);

                        bookmarkList.add(compareIndex+1, bookMark);
                        break;
                    } else if(0 == compareProgress.compareTo(bookmark_progress)) {
                        Log.e(LOG_TAG, "samebookmark can't be set");
                        return;
                    }
                }
            }
        }

        rl_seekbar.addView(iv);

    }


    //startActivityForResult 로 호출한 activity 종료시에 호출되는 callback
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
                /*
                File file = new File(fileListParcelable.get(0).getFilePath());
                Log.d(LOG_TAG,"onActivityResult test start ------------------------------");
                Log.d(LOG_TAG,"file.getAbsolutePath() : " + file.getAbsolutePath());
                Log.d(LOG_TAG,"file.getPath() : " + file.getPath());
                Log.d(LOG_TAG,"file.getName() : " + file.getName());
                Log.d(LOG_TAG,"file.getParent() : " + file.getParent());
                Log.d(LOG_TAG,"onActivityResult test end ------------------------------");
                */
                /*
                Get Intent. file path : /vendor/lib/lib-imsSDP.so
                onActivityResult test start ------------------------------
                file.getAbsolutePath() : /vendor/lib/lib-imsSDP.so
                file.getPath() : /vendor/lib/lib-imsSDP.so
                file.getName() : lib-imsSDP.so
                file.getParent() : /vendor/lib
                onActivityResult test end ------------------------------
                */
                break;

        }
    }

    int playListViewUpdate(ArrayList<FileParcelable> filelist) {

        if ( filelist.size() == 0 ) {
            Log.d(LOG_TAG, "playListViewUpdate fileListParcelable is null");
        }

        Log.d(LOG_TAG, "playListViewUpdate ");
        playList_adapter.resetItems();

        if(filelist.size() > 0) {
            for (int i = 0; i < filelist.size(); i++) {
                Log.d(LOG_TAG, "playListViewUpdate addItem " + filelist.get(i).getFileName());
                Log.d(LOG_TAG, "playListViewUpdate addItem " + filelist.get(i).getFullPath());
                playList_adapter.addItem(filelist.get(i).getFileName());
            }

            listview_playList.setAdapter(playList_adapter);

            mMediaPlayerController.setPlayFile(filelist.get(0).getFullPath());
            mMediaPlayerController.setDuration();
            Constants.FILE_READY_STATUS = Constants.FILE_READY;
        }

        return E_SUCCESS;
    }


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

    SeekBar.OnSeekBarChangeListener mSeekBarChangeListner = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            Log.d(LOG_TAG, "onProgressChanged fromUser " + fromUser + ", progress " + progress);
            if(fromUser) {
                if(Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PLAY)
                    mMediaPlayerController.seekTo(progress);
            } else {
/*                if (progress_check != (int)(progress/100)) {  // 연상량을 줄이기 위해 100 으로 나누어서 사용함(0.1초)
                    updateProgressBarTime(progress);
                    if(repeatEnable == true) {
                        if( progress >= mPeriodRepeatEnd) {
                            mMediaPlayerController.seekTo(mPeriodRepeatStart);
                        }
                    }
                }
                progress_check = (int)(progress / 100);
*/
            }
        }
    };


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



