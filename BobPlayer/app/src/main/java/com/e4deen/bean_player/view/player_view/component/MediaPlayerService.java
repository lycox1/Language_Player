package com.e4deen.bean_player.view.player_view.component;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.e4deen.bean_player.data.Constants;
import com.e4deen.bean_player.util.VolumeUtil;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by user on 2016-12-19.
 */
public class MediaPlayerService extends Service {

    static String LOG_TAG = "Bean_Player_MediaPlayerService";
    final int E_SUCCESS = 1;
    final int E_ERROR = 0;
    String playFile = null;
    int mDuration = 0;
    float mPlaybackSpeed = 1.0f;
    float mLeftVol = 1.0f;
    float mRightVol = 1.0f;
    int mLeftVolIdx = 10;
    int mRightVolIndex = 10;
    int mMasterVolIndex = 10;
    MediaPlayer mediaPlayer = null;
    private Messenger mRemote;
    DecimalFormat format_PlaybackSpeed;
    VolumeUtil mVolumeUtil;
    LoopbackPlayer mLoopbackPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        playerInit();
        Log.d(LOG_TAG, "Test mLeftVol " + mLeftVol + ", mRightVol " + mRightVol);
        mVolumeUtil = new VolumeUtil();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Messenger(new RemoteHandler()).getBinder();
    }

    // Send message to Activity (MediaPlayerController)
    public void remoteSendMessage(int what, String data) {
        if (mRemote != null) {
            Message msg = new Message();
            msg.what = what;
            msg.obj = data;
            try {
                mRemote.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    // Service handler - receive msg from Activity(MediaPlayerController)
    private class RemoteHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            Log.d(LOG_TAG, "handleMessage msg.what : " + msg.what);

            switch (msg.what) {
                case MediaPlayerController.COMMAND_CONNECT:  // 0
                    // Register activity hander
                    mRemote = (Messenger) msg.obj;
                    playerInit();
                    Log.d(LOG_TAG, "handleMessage MediaPlayerService is connected");
                    break;
                case MediaPlayerController.COMMAND_PLAY:    // 1
                    Log.d(LOG_TAG, "handleMessage COMMAND_PLAY");
                    try {
                        Constants.PLAYER_STATUS = Constants.PLAYER_STATUS_PLAY;
                        startPlay();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case MediaPlayerController.COMMAND_PAUSE:   // 2

                    Constants.PLAYER_STATUS = Constants.PLAYER_STATUS_PAUSE;
                    Log.d(LOG_TAG, "handleMessage COMMAND_PAUSE");
                    pausePlay();
                    break;
                case MediaPlayerController.COMMAND_STOP:    // 3
                    if(Constants.FILE_READY_STATUS == Constants.FILE_READY &&
                        ( Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PLAY || Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PAUSE ) ) {
                        Constants.PLAYER_STATUS = Constants.PLAYER_STATUS_STOP;
                    }
                    Log.d(LOG_TAG, "handleMessage COMMAND_STOP");
                    break;
                case MediaPlayerController.COMMAND_FF:      // 4
                    if(Constants.FILE_READY_STATUS == Constants.FILE_READY ) {
                        String mSec = ((String)msg.obj);
                        ffPlay(Integer.parseInt(mSec));
                    } else {
//                        Log.d(LOG_TAG, "handleMessage COMMAND_FF. FILE_READY_STATUS " + Constants.FILE_READY_STATUS);
                    }
                    break;
                case MediaPlayerController.COMMAND_REW:     // 5
                    if(Constants.FILE_READY_STATUS == Constants.FILE_READY) {
                        String mSec = ((String)msg.obj);
                        rewPlay(Integer.parseInt(mSec));
                    } else {
//                        Log.d(LOG_TAG, "handleMessage COMMAND_REW. FILE_READY_STATUS " + Constants.FILE_READY_STATUS);
                    }
                    break;
                case MediaPlayerController.COMMAND_SET_FILE:    // 6
                    playFile = (String)msg.obj;
                    Log.d(LOG_TAG, "handleMessage COMMAND_SET_FILE / playFile : " + playFile);
                    setPlayFile(playFile);
                    break;
                case MediaPlayerController.COMMAND_SPEED_UP:    // 7
                    mPlaybackSpeed += 0.1;
                    format_PlaybackSpeed = new DecimalFormat(".#");

                    remoteSendMessage(MediaPlayerController.COMMAND_UPDATE_PLAYBACK_SPEED, format_PlaybackSpeed.format(mPlaybackSpeed) );
                    //remoteSendMessage(MediaPlayerController.COMMAND_UPDATE_PLAYBACK_SPEED, Float.toString(mPlaybackSpeed) );
                    Log.d(LOG_TAG, "handleMessage COMMAND_SPEED_UP mPlaybackSpeed " + mPlaybackSpeed);
                    if(Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PLAY) {
                        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(mPlaybackSpeed));
                    }
                    break;
                case MediaPlayerController.COMMAND_SPEED_DOWN:  // 8
                    mPlaybackSpeed -= 0.1;
                    //DecimalFormat format2 = new DecimalFormat(".#");
                    remoteSendMessage(MediaPlayerController.COMMAND_UPDATE_PLAYBACK_SPEED, format_PlaybackSpeed.format(mPlaybackSpeed) );
                    //remoteSendMessage(MediaPlayerController.COMMAND_UPDATE_PLAYBACK_SPEED, Float.toString(mPlaybackSpeed) );
                    Log.d(LOG_TAG, "handleMessage COMMAND_SPEED_DOWN " + mPlaybackSpeed);
                    if(Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PLAY) {
                        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(mPlaybackSpeed));
                    }
                    break;
                case MediaPlayerController.COMMAND_SET_BOOKMARK:    // 9
                    Log.d(LOG_TAG, "handleMessage COMMAND_SET_BOOKMARK");
                    break;
                case MediaPlayerController.COMMAND_FF_BOOKMARK:     // 10
                    Log.d(LOG_TAG, "handleMessage COMMAND_FF_BOOKMARK");
                    break;
                case MediaPlayerController.COMMAND_REW_BOOKMARK:    // 11
                    Log.d(LOG_TAG, "handleMessage COMMAND_REW_BOOKMARK");
                    break;
                case MediaPlayerController.COMMAND_GET_DURATION:      // 12
                    if(Constants.FILE_READY_STATUS == Constants.FILE_READY) {
                        getDration();
                    }
                    break;
                case MediaPlayerController.COMMAND_UPDATE_SEEKBAR:      // 13
                    if(Constants.FILE_READY_STATUS == Constants.FILE_READY) {
                        updateSeekBar();
                    }
                    break;
                case MediaPlayerController.COMMAND_UPDATE_SEEKTO:      // 14
                    if(Constants.FILE_READY_STATUS == Constants.FILE_READY) {
                        String progress = ((String)msg.obj);
                        SeekTo(Integer.parseInt(progress));
                    }
                    break;
                case MediaPlayerController.COMMAND_SET_REPEAT:      // 15
                    if(Constants.FILE_READY_STATUS == Constants.FILE_READY) {
                        setRepeat();
                    }
                    break;

                case MediaPlayerController.COMMAND_START_LOOPBACK:      // 17
                    Log.d(LOG_TAG, "handleMessage COMMAND_START_LOOPBACK");
                    startLoopback();
                    break;

                case MediaPlayerController.COMMAND_STOP_LOOPBACK:      // 18
                    Log.d(LOG_TAG, "handleMessage COMMAND_STOP_LOOPBACK");
                    stopLoopback();
                    break;

                case MediaPlayerController.COMMAND_SET_LEFT_LOOPBACK_VOL:      // 19
                    Log.d(LOG_TAG, "handleMessage COMMAND_SET_LEFT_LOOPBACK_VOL");
                    String vol = ((String)msg.obj);
                    setLoopbackLeftVol(Integer.parseInt(vol));
                    break;

                case MediaPlayerController.COMMAND_SET_RIGHT_LOOPBACK_VOL:      // 20
                    Log.d(LOG_TAG, "handleMessage COMMAND_SET_RIGHT_LOOPBACK_VOL");
                    vol = ((String)msg.obj);
                    setLoopbackRightVol(Integer.parseInt(vol));
                    break;

                case MediaPlayerController.COMMAND_SET_LEFT_PLAYING_FILE_VOL:      // 21
                    Log.d(LOG_TAG, "handleMessage COMMAND_SET_LEFT_PLAYING_FILE_VOL");
                    vol = ((String)msg.obj);
                    setPlayingFileLeftVol(Integer.parseInt(vol));
                    break;

                case MediaPlayerController.COMMAND_SET_RIGHT_PLAYING_FILE_VOL:      // 22
                    Log.d(LOG_TAG, "handleMessage COMMAND_SET_RIGHT_PLAYING_FILE_VOL");
                    vol = ((String)msg.obj);
                    setPlayingFileRightVol(Integer.parseInt(vol));
                    break;

                case MediaPlayerController.COMMAND_SET_LOOPBACK_MASTER_VOL:      // 23
                    Log.d(LOG_TAG, "handleMessage COMMAND_SET_LOOPBACK_MASTER_VOL");
                    vol = ((String)msg.obj);
                    setLoopbackMasterVol(Integer.parseInt(vol));
                    break;

                case MediaPlayerController.COMMNAD_SET_PLAYING_FILE_MASTER_VOL:      // 24
                    Log.d(LOG_TAG, "handleMessage COMMNAD_SET_PLAYING_FILE_MASTER_VOL");
                    vol = ((String)msg.obj);
                    setPlayingFileMasterVol(Integer.parseInt(vol));
                    break;

                case MediaPlayerController.COMMAND_DISCONNECT:      // 99
                    Log.d(LOG_TAG, "handleMessage COMMAND_DISCONNECT");
                    break;

                default :
                    remoteSendMessage(0, "TEST");
                    break;
            }
        }
    }

//--------------------------------------------------------------------------------------------
    public int playerInit()  {

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Constants.FILE_READY_STATUS = Constants.FILE_NOT_READY;
                // TODO Auto-generated method stub
                stopSelf();
            }
        });

        mLoopbackPlayer = new LoopbackPlayer();

        return E_SUCCESS;
    }

    public int setPlayFile(String fullPath)  {

        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    Constants.FILE_READY_STATUS = Constants.FILE_NOT_READY;
                    // TODO Auto-generated method stub
                    stopSelf();
                }
            });

            mediaPlayer.setDataSource(fullPath);

            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        Constants.FILE_READY_STATUS = Constants.FILE_READY;

        return E_SUCCESS;
    }

    public int startPlay() throws IOException {
        Log.d(LOG_TAG, "startPlay mLeftVol " + mLeftVol + ", mRightVol " + mRightVol);
        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(mPlaybackSpeed));
        mediaPlayer.start();
        mediaPlayer.setVolume(mLeftVol, mRightVol);

        return E_SUCCESS;
    }

    public int pausePlay() {
        Log.d(LOG_TAG, "pausePlay");
        mediaPlayer.pause();
        return E_SUCCESS;
    }

    public int stopPlay() {
        Log.d(LOG_TAG, "stopPlay");
        mediaPlayer.stop();
        return E_SUCCESS;
    }

    public int rewPlay(int mSec) {
        int tempCurrentPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.seekTo(tempCurrentPosition - mSec);
//        Log.d(LOG_TAG, "rewPlay mSec " + mSec + ", currentPosition " + tempCurrentPosition);

        return E_SUCCESS;
    }

    public int ffPlay(int mSec) {
        int tempCurrentPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.seekTo(tempCurrentPosition + mSec);
        Log.d(LOG_TAG, "ffPlay mSec " + mSec + ", currentPosition " + tempCurrentPosition);
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

        return E_SUCCESS;
    }

    public int speedDown() {

        return E_SUCCESS;
    }

    public int getDration() {
        if(Constants.FILE_READY_STATUS == Constants.FILE_READY) {
            mDuration = mediaPlayer.getDuration();
            remoteSendMessage(MediaPlayerController.COMMAND_GET_DURATION, Integer.toString(mDuration) );
            Log.d(LOG_TAG, "getDration FILE_READY_STATUS " + mDuration);
        } else {
            Log.d(LOG_TAG, "getDration FILE_READY_STATUS is wrong " + Constants.FILE_READY_STATUS);
        }

        return E_SUCCESS;
    }

    public int setRepeat() {
        if( Constants.FILE_READY_STATUS == Constants.FILE_READY) {
            if (Constants.REPEAT_STATE == Constants.NOT_REPEAT) {
                Constants.REPEAT_STATE = Constants.REPEAT;
                mediaPlayer.setLooping(true);


                if (Constants.SHUFFLE_STATE == Constants.SHUFFLE) {
                    Constants.SHUFFLE_STATE = Constants.NOT_SUFFLE;
                }
            } else {
                Constants.REPEAT_STATE = Constants.NOT_REPEAT;
                mediaPlayer.setLooping(false);
            }
        } else {
            Log.e(LOG_TAG, "setRepeat() can't work. FILE_READY_STATUS is FILE_NOT_READY");
            return E_ERROR;
        }
        return E_SUCCESS;
    }

    public int updateSeekBar() {
        if(Constants.FILE_READY_STATUS == Constants.FILE_READY) {
            remoteSendMessage(MediaPlayerController.COMMAND_UPDATE_SEEKBAR, Integer.toString(mediaPlayer.getCurrentPosition()) );
//            Log.d(LOG_TAG, "updateSeekBar FILE_READY_STATUS " + mediaPlayer.getCurrentPosition());
        } else {
            Log.d(LOG_TAG, "updateSeekBar FILE_READY_STATUS is wrong " + Constants.FILE_READY_STATUS);
        }

        return E_SUCCESS;
    }

    public int SeekTo(int progress) {
        //mediaPlayer.seekTo(progress);
        Log.d(LOG_TAG,"SeekTo progress " + progress);
        mediaPlayer.seekTo(progress);

        return E_SUCCESS;
    }

    public int startLoopback() {
        mLoopbackPlayer.Start();
        return E_SUCCESS;
    }

    public int stopLoopback() {
        Log.d(LOG_TAG,"stopLoopback ");
        mLoopbackPlayer.Stop();

        return E_SUCCESS;
    }

    public int setLoopbackLeftVol(int vol) {
        Log.d(LOG_TAG,"setLoopbackLeftVol " + vol);
        mLoopbackPlayer.setLeftVol(vol);

        return E_SUCCESS;
    }
    public int setLoopbackRightVol(int vol) {
        Log.d(LOG_TAG,"setLoopbackRightVol " + vol);
        mLoopbackPlayer.setRightVol(vol);

        return E_SUCCESS;
    }
    public int setPlayingFileLeftVol(int vol) {

        mLeftVolIdx = vol;
        mLeftVol = mVolumeUtil.getVolume(vol);

        if(Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PLAY) {
            Log.d(LOG_TAG,"setPlayingFileLeftVol vol " + vol + ", mLeftVol " + mLeftVol + ", mRightVol " + mRightVol);
            mediaPlayer.setVolume(mLeftVol, mRightVol);
        }

        return E_SUCCESS;
    }
    public int setPlayingFileRightVol(int vol) {

        mRightVolIndex = vol;
        mRightVol = mVolumeUtil.getVolume(vol);

        if(Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PLAY) {
            Log.d(LOG_TAG,"setPlayingFileRightVol " + vol + ", mLeftVol " + mLeftVol + ", mRightVol " + mRightVol);
            mediaPlayer.setVolume(mLeftVol, mRightVol);
        }
        return E_SUCCESS;
    }

    public int setLoopbackMasterVol(int vol) {
        Log.d(LOG_TAG,"setLoopbackMasterVol " + vol);
        mLoopbackPlayer.setLoopbackMasterVol(vol);

        return E_SUCCESS;
    }

    public int setPlayingFileMasterVol(int vol) {

        mMasterVolIndex = vol;
        mVolumeUtil.setmMasterVolume(mMasterVolIndex);

        mRightVol = mVolumeUtil.getVolume(mRightVolIndex);
        mLeftVol = mVolumeUtil.getVolume(mLeftVolIdx);

        if(Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PLAY) {
            Log.d(LOG_TAG,"setPlayingFileMasterVol " + vol + ", mRightVol " + mRightVol + ", mLeftVol " + mLeftVol);
            mediaPlayer.setVolume(mLeftVol, mRightVol);
        }
        return E_SUCCESS;
    }


}
