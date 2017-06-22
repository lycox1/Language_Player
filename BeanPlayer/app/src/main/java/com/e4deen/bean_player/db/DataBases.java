package com.e4deen.bean_player.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by user on 2017-03-19.
 */


public class DataBases {

    public static final class CreateDB implements BaseColumns {
        public static final String PLAYLIST_IDX = "playlist_idx";
        public static final String PLAYLIST_NAME = "playlist_name";
        public static final String FILE_PATH = "file_path";
        public static final String PROGRESS = "progress";
        public static final String _TABLENAME = "playlist";
        public static final String _CREATE_PLAYLIST_DB =
                "create table " + _TABLENAME + "("
                        + _ID + " integer primary key autoincrement, "
                        + PLAYLIST_IDX + " INTEGER, "
                        + PLAYLIST_NAME + " text , "
                        + FILE_PATH + " text not null , "
                        + PROGRESS + " INTEGER );";
    }

    public static Playlist_manager_db mPLM_DB;
}
