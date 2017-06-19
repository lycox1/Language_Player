package com.e4deen.bean_player.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by user on 2017-04-22.
 */

public class Valueable_Util {

    static String LOG_TAG = "BeanPlayer_ValueableUtility";
    static String PREF_NAME_CURRENT_PLAYLIST_IDX = "pref_name_current_playlist_idx";
    static String KEY_CURRENT_PLAYLIST_IDX = "key_current_playlist_idx";

    static String PREF_NAME_TEMP_PLAYLIST_IDX = "pref_name_temp_playlist_idx";
    static String KEY_TEMP_PLAYLIST_IDX = "key_temp_playlist_idx";

    static String PREF_NAME_CURRENT_PLAYLIST_NAME = "pref_name_current_playlist_name";
    static String KEY_CURRENT_PLAYLIST_NAME = "key_current_playlist_name";

    static String PREF_NAME_TEMP_PLAYLIST_NAME = "pref_name_temp_playlist_name";
    static String KEY_TEMP_PLAYLIST_NAME = "key_temp_playlist_name";

    static String PREF_NAME_CURRENT_PLAYING_POSITION = "pref_name_current_playing_position";
    static String KEY_CURRENT_PLAYING_POSITION = "key_current_playing_position";

    static String PREF_NAME_CURRENT_PLAYING_FILE_PATH = "pref_name_current_playing_file_path";
    static String KEY_CURRENT_PLAYING_FILE_PATH = "key_current_playing_file_path";

    static Context mContext;

    static public void init_Valueable_Util(Context context) { // initialized just once
        Log.d(LOG_TAG, "Valueable_Util initialized");
        mContext = context;
    }

    static public void setTempPlaylistIdx(int idx) {
        if(mContext == null) {
            Log.d(LOG_TAG, "getTempPlaylistIdx() mContext is null");
            return;
        }
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME_TEMP_PLAYLIST_IDX, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_TEMP_PLAYLIST_IDX, idx);
        editor.commit();
    }

    static public int getTempPlaylistIdx() {
        if(mContext == null) {
            Log.d(LOG_TAG, "getTempPlaylistIdx() mContext is null");
            return 0;
        }

        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME_TEMP_PLAYLIST_IDX, MODE_PRIVATE);
        int idx = prefs.getInt(KEY_TEMP_PLAYLIST_IDX, 0);
        return idx;
    }

    static public void setCurrentPlaylistIdx(int idx) {
        if(mContext == null) {
            Log.d(LOG_TAG, "getCurrentPlaylistIdx() mContext is null");
            return;
        }
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME_CURRENT_PLAYLIST_IDX, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_CURRENT_PLAYLIST_IDX, idx);
        editor.commit();
    }

    static public int getCurrentPlaylistIdx() {
        if(mContext == null) {
            Log.d(LOG_TAG, "getCurrentPlaylistIdx() mContext is null");
            return 0;
        }

        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME_CURRENT_PLAYLIST_IDX, MODE_PRIVATE);
        int idx = prefs.getInt(KEY_CURRENT_PLAYLIST_IDX, 0);
        return idx;
    }

    static public void setTempPlaylistName(String Name) {
        if(mContext == null) {
            Log.d(LOG_TAG, "setCurrentPlaylistName() mContext is null");
            return;
        }
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME_TEMP_PLAYLIST_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TEMP_PLAYLIST_NAME, Name);
        editor.commit();
    }

    static public String getTempPlaylistName() {
        if(mContext == null) {
            Log.d(LOG_TAG, "getCurrentPlaylistIdx() mContext is null");
            return null ;
        }

        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME_TEMP_PLAYLIST_NAME, MODE_PRIVATE);
        String name = prefs.getString(KEY_TEMP_PLAYLIST_NAME, "");
        return name;
    }

    static public void setCurrentPlaylistName(String Name) {
        if(mContext == null) {
            Log.d(LOG_TAG, "setCurrentPlaylistName() mContext is null");
            return;
        }
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME_CURRENT_PLAYLIST_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_CURRENT_PLAYLIST_NAME, Name);
        editor.commit();
    }

    static public String getCurrentPlaylistName() {
        if(mContext == null) {
            Log.d(LOG_TAG, "getCurrentPlaylistIdx() mContext is null");
            return null ;
        }

        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME_CURRENT_PLAYLIST_NAME, MODE_PRIVATE);
        String name = prefs.getString(KEY_CURRENT_PLAYLIST_NAME, "");
        return name;
    }

    static public void setCurrentPlayingFilePath(String Name) {
        if(mContext == null) {
            Log.d(LOG_TAG, "setCurrentPlayingFilePath() mContext is null");
            return;
        }
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME_CURRENT_PLAYING_FILE_PATH, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_CURRENT_PLAYING_FILE_PATH, Name);
        editor.commit();
    }

    static public String getCurrentPlayingFilePath() {
        if(mContext == null) {
            Log.d(LOG_TAG, "getCurrentPlayingFilePath() mContext is null");
            return null ;
        }

        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME_CURRENT_PLAYING_FILE_PATH, MODE_PRIVATE);
        String name = prefs.getString(KEY_CURRENT_PLAYING_FILE_PATH, "");
        return name;
    }

    static public void setCurrentPlayingPosition(int position) {
        if(mContext == null) {
            Log.d(LOG_TAG, "setCurrentPlaylistName() mContext is null");
            return;
        }
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME_CURRENT_PLAYING_POSITION, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_CURRENT_PLAYING_POSITION, position);
        editor.commit();
    }

    static public int getCurrentPlayingPosition() {
        if(mContext == null) {
            Log.d(LOG_TAG, "getCurrentPlayingPosition() mContext is null");
            return -1 ;
        }

        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME_CURRENT_PLAYING_POSITION, MODE_PRIVATE);
        int position = prefs.getInt(KEY_CURRENT_PLAYING_POSITION, 0);
        return position;
    }
}
