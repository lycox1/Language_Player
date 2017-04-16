package com.e4deen.bean_player.view.player_view.component;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;
import java.lang.Math;

import static java.lang.Math.tan;

/**
 * Created by user on 2017-04-14.
 */

    public class LoopbackPlayer {
        String LOG_TAG = "Bean_Player_MediaPlayerService_LoopbackPlayer";
        private int freq = 16000;
        private AudioRecord audioRecord = null;
        private Thread Rthread = null;
        private AudioTrack audioTrack = null;
        public int mShiftForLeftVol, mShiftForRightVol = 0;
        public float mLeftVol =1;
        public float mRightVol = 1;
        public double mLeftMultiplier, mRightMultiplier = 80;
        public static int MAX_VOLUME = 10;
        public static int MIN_VOLUME = 0;

        boolean isPlaying = false;

        public void Start()
        {
            Log.d(LOG_TAG, "Loopback Start button");
            isPlaying = true;
            loopback();
        }

        public void Stop()
        {
            Log.d(LOG_TAG, "Loopback Stop button");
            isPlaying = false;
            audioTrack.stop();
            audioTrack.release();
            audioRecord.stop();
            audioRecord.release();
        }

        public void setLeftVol(float vol) { // Max 10, Min 0 temp test
            mLeftVol = (float)(vol / 10);
            //double mLeftMultiplier = tan((double)(mLeftVol/100.0));
            Log.d(LOG_TAG, "setLeftVol " + vol + ", mLeftVol " + mLeftVol);
            if(isPlaying == true)
                audioTrack.setStereoVolume(mLeftVol, mRightVol);
        }

        public void setRightVol(float vol) { // Max 10, Min 0 temp test
            mRightVol = (float)(vol / 10);
            //double mRightMultiplier = tan((double)(mRightVol/100.0));
            Log.d(LOG_TAG, "setRightVol " + vol + ", mRightVol " + mRightVol);
            if(isPlaying == true)
                audioTrack.setStereoVolume(mLeftVol, mRightVol);
         }

    protected void loopback() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        final int bufferSize = AudioRecord.getMinBufferSize(freq,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, freq,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, freq,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize,
                AudioTrack.MODE_STREAM);

        audioTrack.setPlaybackRate(freq);
        audioTrack.setStereoVolume(mLeftVol, mRightVol);

        final byte[] buffer = new byte[bufferSize];

        Log.i(LOG_TAG, "Audio Recording started");
        audioRecord.startRecording();
        Log.i(LOG_TAG, "Audio Playing started");
        audioTrack.play();

        Rthread = new Thread(new Runnable() {
            public void run() {
                while (isPlaying) {
                    try {
                        audioRecord.read(buffer, 0, bufferSize);
                        audioTrack.write(buffer, 0, buffer.length);
                    } catch (Throwable t) {
                        Log.e("Error", "Read write failed");
                        t.printStackTrace();
                    }
                }
            }
        });
        Rthread.start();
        }
    }

