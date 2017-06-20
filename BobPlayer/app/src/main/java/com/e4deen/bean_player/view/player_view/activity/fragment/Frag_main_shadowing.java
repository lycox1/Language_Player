package com.e4deen.bean_player.view.player_view.activity.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.e4deen.bean_player.R;
import com.e4deen.bean_player.data.Constants;
import com.e4deen.bean_player.db.Playlist_manager_db;
import com.e4deen.bean_player.util.VerticalSeekBar;
import com.e4deen.bean_player.util.Vibe;
import com.e4deen.bean_player.util.VolumeUtil;
import com.e4deen.bean_player.view.player_view.component.LoopbackPlayer;
import com.e4deen.bean_player.view.player_view.component.MediaPlayerController;

/**
 * Created by user on 2017-04-12.
 */

public class Frag_main_shadowing extends Fragment {

    String LOG_TAG = "BeanPlayer_Frag_main_tab_shadowing";
    Context mContext;
    ImageButton mBtnMyVoiceOnOff, mBtnPlayingFileOnOff;
    ImageView mIvMyVoice, mIvPlayingFile;
    SeekBar mSeekbarMyVoice, mSeekbarPlayingFile;
    VerticalSeekBar mSeekbarMyVoiceMaster, mSeekbarPlayingFileMaster;
    int mLeftVolume = 10;
    int mRightVolume = 10;

    public Frag_main_shadowing(Context context) {
        Log.d(LOG_TAG, "create Frag_main_shadowing fragment");
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag_main_shadowing, container, false);

        mIvMyVoice = (ImageView) rootView.findViewById(R.id.iv_shadowing_my_voice);
        mIvPlayingFile = (ImageView) rootView.findViewById(R.id.iv_shadowing_playing_file);

        mBtnMyVoiceOnOff = (ImageButton)rootView.findViewById(R.id.btn_shadowing_my_voice_onoff);
        mBtnMyVoiceOnOff.setOnClickListener(mOnClickListener);
        mBtnPlayingFileOnOff = (ImageButton)rootView.findViewById(R.id.btn_shadowing_playing_file_onoff);
        mBtnPlayingFileOnOff.setOnClickListener(mOnClickListener);

        mSeekbarMyVoice = (SeekBar) rootView.findViewById(R.id.seekbar_shadowing_my_voice);
        mSeekbarPlayingFile = (SeekBar) rootView.findViewById(R.id.seekbar_shadowing_playing_file);
        mSeekbarMyVoiceMaster = (VerticalSeekBar) rootView.findViewById(R.id.seekbarMyVoiceMaster);
        mSeekbarPlayingFileMaster = (VerticalSeekBar) rootView.findViewById(R.id.seekbarPlayingFileMaster);

        mSeekbarMyVoice.setOnSeekBarChangeListener(mSeekBarListenerMyVoiceBalance);
        mSeekbarPlayingFile.setOnSeekBarChangeListener(mSeekBarListenerPlayingFileBalance);
        mSeekbarMyVoiceMaster.setOnSeekBarChangeListener(mSeekBarListenerMyVoiceMaster);
        mSeekbarPlayingFileMaster.setOnSeekBarChangeListener(mSeekBarListenerPlayingFileMaster);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    Button.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Log.d(LOG_TAG,"onTouch id " + v.getId() );

            int id = v.getId();
            Vibe.mVibe.vibration(60);

            switch (id) {
                case R.id.btn_shadowing_my_voice_onoff:
                    if(Constants.SHADOWING_MY_VOICE_ONOFF_STATE == Constants.STATE_OFF) {
                        Constants.SHADOWING_MY_VOICE_ONOFF_STATE = Constants.STATE_ON;
                        mBtnMyVoiceOnOff.setImageResource(R.drawable.btn_circle_onoff_on);
                        mIvMyVoice.setImageResource(R.drawable.iv_my_voice_on);

                        MediaPlayerController.sController.startLoopback();
                    } else {
                        Constants.SHADOWING_MY_VOICE_ONOFF_STATE = Constants.STATE_OFF;
                        mBtnMyVoiceOnOff.setImageResource(R.drawable.btn_circle_onoff_off);
                        mIvMyVoice.setImageResource(R.drawable.iv_my_voice_off);

                        MediaPlayerController.sController.stopLoopback();
                    }
                    break;

                case R.id.btn_shadowing_playing_file_onoff:
                    if(Constants.SHADOWING_PLAYING_FILE_ONOFF_STATE == Constants.STATE_OFF) {
                        Constants.SHADOWING_PLAYING_FILE_ONOFF_STATE = Constants.STATE_ON;
                        mBtnPlayingFileOnOff.setImageResource(R.drawable.btn_circle_onoff_on);
                        mIvPlayingFile.setImageResource(R.drawable.iv_playing_file_on);

                        if(Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PLAY) {
                            Log.d(LOG_TAG, "playingFile switch ON mRightVolume " + mRightVolume + ", mLeftVolume " + mLeftVolume);
                            MediaPlayerController.sController.setPlayingFileRightVol(mRightVolume);
                            MediaPlayerController.sController.setPlayingFileLeftVol(mLeftVolume);
                        }
                    } else {
                        Constants.SHADOWING_PLAYING_FILE_ONOFF_STATE = Constants.STATE_OFF;
                        mBtnPlayingFileOnOff.setImageResource(R.drawable.btn_circle_onoff_off);
                        mIvPlayingFile.setImageResource(R.drawable.iv_playing_file_off);

                        if(Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PLAY) {
                            Log.d(LOG_TAG, "playingFile switch OFF mRightVolume " + mRightVolume + ", mLeftVolume " + mLeftVolume);
                            //MediaPlayerController.sController.setPlayingFileMasterVol(VolumeUtil.MIN_VOLUME_IDX);
                            MediaPlayerController.sController.setPlayingFileLeftVol(VolumeUtil.MIN_VOLUME_IDX);
                            MediaPlayerController.sController.setPlayingFileRightVol(VolumeUtil.MIN_VOLUME_IDX);
                        }
                    }
                    break;
            }
        }
    };

    SeekBar.OnSeekBarChangeListener mSeekBarListenerMyVoiceBalance = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.d(LOG_TAG, "onProgressChanged fromUser " + fromUser + ", progress " + progress);
            if(fromUser) {
                Log.d(LOG_TAG, "onProgressChanged Constants.PLAYER_STATUS " + Constants.PLAYER_STATUS );

                if(Constants.SHADOWING_MY_VOICE_ONOFF_STATE == Constants.STATE_ON) {
                    if(progress < 10) {
                        MediaPlayerController.sController.setLoopbackLeftVol(VolumeUtil.MAX_VOLUME_IDX);
                        MediaPlayerController.sController.setLoopbackRightVol(progress);
                    } else if (progress > 10) {
                        MediaPlayerController.sController.setLoopbackLeftVol(VolumeUtil.MAX_VOLUME_IDX - (progress - VolumeUtil.MAX_VOLUME_IDX));
                        MediaPlayerController.sController.setLoopbackRightVol(VolumeUtil.MAX_VOLUME_IDX);
                    } else {
                        MediaPlayerController.sController.setLoopbackLeftVol(VolumeUtil.MAX_VOLUME_IDX);
                        MediaPlayerController.sController.setLoopbackRightVol(VolumeUtil.MAX_VOLUME_IDX);
                    }
                }

            }
        }
    };

    SeekBar.OnSeekBarChangeListener mSeekBarListenerPlayingFileBalance = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.d(LOG_TAG, "onProgressChanged fromUser " + fromUser + ", progress " + progress);
            if(fromUser) {
                Log.d(LOG_TAG, "onProgressChanged Constants.PLAYER_STATUS " + Constants.PLAYER_STATUS );

                if(Constants.SHADOWING_PLAYING_FILE_ONOFF_STATE == Constants.STATE_ON) {
                    if(progress < 10) {
                        mLeftVolume = VolumeUtil.MAX_VOLUME_IDX;
                        mRightVolume = progress;
                    } else if (progress > 10) {
                        mLeftVolume = VolumeUtil.MAX_VOLUME_IDX - (progress - VolumeUtil.MAX_VOLUME_IDX);
                        mRightVolume = VolumeUtil.MAX_VOLUME_IDX;
                    } else {
                        mLeftVolume = VolumeUtil.MAX_VOLUME_IDX;
                        mRightVolume = VolumeUtil.MAX_VOLUME_IDX;
                    }

                    MediaPlayerController.sController.setPlayingFileLeftVol(mLeftVolume);
                    MediaPlayerController.sController.setPlayingFileRightVol(mRightVolume);
                }
            }
        }
    };

    SeekBar.OnSeekBarChangeListener mSeekBarListenerMyVoiceMaster = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        };
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.d(LOG_TAG, "onProgressChanged mSeekBarListenerMyVoiceMaster fromUser " + fromUser + ", progress " + progress);
            if(fromUser) {
                Log.d(LOG_TAG, "onProgressChanged Constants.PLAYER_STATUS " + Constants.PLAYER_STATUS );
                MediaPlayerController.sController.setLoopbackMasterVol(progress);
            }
        }
    };

    SeekBar.OnSeekBarChangeListener mSeekBarListenerPlayingFileMaster = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.d(LOG_TAG, "onProgressChanged mSeekBarListenerPlayingFileMaster fromUser " + fromUser + ", progress " + progress);
            if(fromUser) {
                Log.d(LOG_TAG, "onProgressChanged Constants.PLAYER_STATUS " + Constants.PLAYER_STATUS );
                MediaPlayerController.sController.setPlayingFileMasterVol(progress);
            }
        }
    };


}