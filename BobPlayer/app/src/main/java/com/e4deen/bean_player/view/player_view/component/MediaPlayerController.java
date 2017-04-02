package com.e4deen.bean_player.view.player_view.component;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.e4deen.bean_player.data.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 2016-12-19.
 */
public class MediaPlayerController {

    static String LOG_TAG = "Jog_Player_MediaPlayerController";
    final int E_SUCCESS = 1;
    final int E_ERROR = 0;
    String playFile = null;
    SeekBar mSeekBar;
    TextView mDurationTextView, mCurrentPositionTextView, mTextview_playback_speed;

    static final int COMMAND_CONNECT= 0;
    static final int COMMAND_PLAY = 1;
    static final int COMMAND_PAUSE = 2;
    static final int COMMAND_STOP = 3;
    static final int COMMAND_FF = 4;
    static final int COMMAND_REW = 5;
    static final int COMMAND_SET_FILE = 6;
    static final int COMMAND_SPEED_UP = 7;
    static final int COMMAND_SPEED_DOWN = 8;
    static final int COMMAND_SET_BOOKMARK = 9;
    static final int COMMAND_FF_BOOKMARK = 10;
    static final int COMMAND_REW_BOOKMARK = 11;
    static final int COMMAND_GET_DURATION = 12;
    static final int COMMAND_UPDATE_SEEKBAR = 13;
    static final int COMMAND_UPDATE_SEEKTO = 14;
    static final int COMMAND_SET_REPEAT = 15;
    static final int COMMAND_UPDATE_PLAYBACK_SPEED = 16;

    static final int COMMAND_DISCONNECT = 99;

    Context mContext;
    private Messenger mRemote;
    int duration, timeElapsed=0;
    Handler durationHandler = new Handler();

    public MediaPlayerController(Context context) {
        mContext = context;
        playerInit();
    }

    public void setViews(SeekBar seekBar, TextView currentPostion, TextView duration, TextView textview_playback_speed) {
        mSeekBar = seekBar; mCurrentPositionTextView = currentPostion; mDurationTextView =  duration; mTextview_playback_speed = textview_playback_speed;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // service 하고 연결될때
            mRemote = new Messenger(service);

            // Activity handler를 service에 전달하기
            if (mRemote != null) {
                Message msg = new Message();
                msg.what = 0;
                msg.obj = new Messenger(new RemoteHandler());
                try {
                    mRemote.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // service 하고 연결이 끊길때
            mRemote = null;
        }
    };

    // 메세지 받기
    public class RemoteHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //Log.d(LOG_TAG, "Receive message from MediaPlayerService msg.what : " + msg.what);
            Date date;
            DateFormat formatter;
            String dateFormatted;

            switch (msg.what) {
                case MediaPlayerController.COMMAND_GET_DURATION:  // 12
                    // update time scroll bar

                    date = new Date(Long.parseLong((String)msg.obj));
                    formatter = new SimpleDateFormat("mm:ss");
                    dateFormatted = formatter.format(date);
                    mCurrentPositionTextView.setText("00:00");
                    mDurationTextView.setText(dateFormatted);

//                    duration = (int)TimeUnit.MILLISECONDS.toSeconds( Long.parseLong((String)msg.obj) );
                    duration = (int) Long.parseLong((String)msg.obj);
                    mSeekBar.setMax(duration);
//                    Log.d(LOG_TAG, "handleMessage COMMAND_GET_DURATION " + (String) msg.obj + ", duration " + duration + ", dateFormatted " + dateFormatted);
                    break;

                case MediaPlayerController.COMMAND_UPDATE_SEEKBAR:  // 13
                    // update time scroll bar
                    timeElapsed = (int) Long.parseLong((String)msg.obj);
                    mSeekBar.setProgress(timeElapsed);

                    date = new Date(Long.parseLong((String)msg.obj));
                    formatter = new SimpleDateFormat("mm:ss");
                    dateFormatted = formatter.format(date);
                    mCurrentPositionTextView.setText(dateFormatted);
//                    Log.d(LOG_TAG, "handleMessage COMMAND_UPDATE_SEEKBAR " + (String) msg.obj + ", dateFormatted " + dateFormatted + ", timeElapsed " + timeElapsed);
                    break;

                case MediaPlayerController.COMMAND_UPDATE_PLAYBACK_SPEED:  // 13
                    // update time scroll bar

                    mTextview_playback_speed.setText((String)msg.obj);
                    break;

            }
        }
    }

    // 메세지 보내기
    public void sendMessage(int command,String... VarArgs) {
        if (mRemote != null) {
            Message msg = new Message();
            msg.what = command;
//            Log.d(LOG_TAG, "sendMessage from MediaPlayerController to MediaPlayerService command : " + command);

            for(String s: VarArgs) {
                msg.obj = s;
            }

            try {
                mRemote.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

//------------------------------------------------------------------------


    public int playerInit()  {

        // service 연결 시도
        Intent serviceIntent = new Intent(mContext, MediaPlayerService.class);
        mContext.bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);

        return E_SUCCESS;
    }

    public int setPlayFile(String fullPath)  {
        playFile = fullPath;
        Log.d(LOG_TAG, "setPlayFile fullPath = " + playFile);
        sendMessage(COMMAND_SET_FILE, playFile);
        return E_SUCCESS;
    }

    public int startPlay() {
        sendMessage(COMMAND_PLAY);
        updateSeekBar();
        return E_SUCCESS;
    }

    public int pausePlay() {
        sendMessage(COMMAND_PAUSE);
        return E_SUCCESS;
    }

    public int stopPlay() {
        // service 연결 해제
        sendMessage(COMMAND_STOP);
        mContext.unbindService(mConnection);
        return E_SUCCESS;
    }

    public int rewPlay(int mSec) {
//        Log.d(LOG_TAG, "rewPlay ");
        sendMessage(COMMAND_REW, Integer.toString(mSec));

        return E_SUCCESS;
    }

    public int ffPlay(int mSec) {
        Log.d(LOG_TAG, "ffPlay ");
        sendMessage(COMMAND_FF, Integer.toString(mSec));

        return E_SUCCESS;
    }

    public int setBookmarkAB() {
        return E_SUCCESS;
    }

    public int setBookmark() {
        return E_SUCCESS;
    }

    public int ffBookmark() {
        return E_SUCCESS;
    }

    public int rewBookmark() {
        return E_SUCCESS;
    }

    public int speedUp() {
        Log.d(LOG_TAG, "speedUp ");
        sendMessage(COMMAND_SPEED_UP);
        return E_SUCCESS;
    }

    public int speedDown() {
        sendMessage(COMMAND_SPEED_DOWN);
        return E_SUCCESS;
    }

    public int setDuration() {
        Log.d(LOG_TAG, "setDuration ");
        sendMessage(COMMAND_GET_DURATION);

        return E_SUCCESS;
    }

    public int setRepeat() {
        Log.d(LOG_TAG, "setRepeat ");
        sendMessage(COMMAND_SET_REPEAT);

        return E_SUCCESS;
    }

    public int seekTo(int progress) {
        Log.d(LOG_TAG, "seekTo " + progress);
        sendMessage(COMMAND_UPDATE_SEEKTO, Integer.toString(progress));
        return E_SUCCESS;
    }

    public int updateSeekBar() {
        Log.d(LOG_TAG, "updateSeekBar ");
        sendMessage(COMMAND_UPDATE_SEEKBAR);
        durationHandler.postDelayed(updateSeekBarTime, 100);
        return E_SUCCESS;
    }

    //handler to change seekBarTime
    public Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            //get current position

            if(Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PLAY) {
//                Log.d(LOG_TAG, "updateSeekBarTime ");
                sendMessage(COMMAND_UPDATE_SEEKBAR);
                durationHandler.postDelayed(this, 100);
            }
        }
    };
}
