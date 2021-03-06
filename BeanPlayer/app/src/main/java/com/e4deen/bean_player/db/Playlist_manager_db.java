package com.e4deen.bean_player.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.e4deen.bean_player.data.Constants;
import com.e4deen.bean_player.util.Valueable_Util;
import com.e4deen.bean_player.view.player_view.data.BookMark;

import java.util.ArrayList;

/**
 * Created by user on 2017-03-19.
 */

public class Playlist_manager_db {

    String LOG_TAG = "BeanPlayer_Playlist_manager_db";
    private static final String DATABASE_NAME = "playlist.db";
    private static final int DATABASE_VERSION = 2;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;
    public static int sCurrentIndex = 0;

    public Playlist_manager_db(Context context){
        Log.d(LOG_TAG, "Playlist_manager_db");
        this.mCtx = context;
    }

    public Playlist_manager_db open() throws SQLException {
        Log.d(LOG_TAG, "database open()");
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);

        mDB = mDBHelper.getWritableDatabase();
        //mDB.execSQL("delete from " + DataBases.CreateDB._TABLENAME);
        // for test
        //mDB.execSQL("DROP TABLE IF EXISTS "+DataBases.CreateDB._TABLENAME);
        //mDB.execSQL(DataBases.CreateDB._CREATE_PLAYLIST_DB);

        Cursor mCursor = mDB.query(DataBases.CreateDB._TABLENAME, null, null, null, null, null, null);
        Log.d(LOG_TAG, "database open() test " + mCursor.getCount());
        return this;
    }

    public void deleteAll() {
        mDB.delete(DataBases.CreateDB._TABLENAME, null, null);
    }


    public void close(){
       Log.d(LOG_TAG, "close() db");

        mDB.close();
    }

    public int addFilesToPlaylist(int index, String playlistName, ArrayList<String> playlist) {

        ArrayList<String> insert_playlist = new ArrayList<String>();

        for(int i =0; i < playlist.size(); i++) {
            Cursor mCursor = mDB.query(DataBases.CreateDB._TABLENAME, null, DataBases.CreateDB.PLAYLIST_IDX + "=? AND " +
                                                                            DataBases.CreateDB.PLAYLIST_NAME + "=? AND " +
                                                                            DataBases.CreateDB.FILE_PATH + "=? AND " +
                                                                            DataBases.CreateDB.PROGRESS + "=?",
                    new String[] { String.valueOf(index), playlistName, playlist.get(i), "0" },
                    null, null, null);
            Log.d(LOG_TAG, "addFilesToPlaylist playlist loop " + i + ", mCursor.getCount() " + mCursor.getCount());
            if ( mCursor.getCount() == 0 ) {
                insert_playlist.add(playlist.get(i));
            }
        }
/*
        Cursor mCursor = mDB.query(DataBases.CreateDB._TABLENAME, null, DataBases.CreateDB.PLAYLIST_IDX + "=?",
                new String[] { String.valueOf(index)},
                null, null, null);
        mCursor.moveToFirst();
        for(int i = 0; i< mCursor.getCount(); i++) {
            Log.d(LOG_TAG, "addFilesToPlaylist i " + i + ", FILE_PATH " + mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.FILE_PATH)) );
            mCursor.moveToNext();
        }

        Log.d(LOG_TAG, "addFilesToPlaylist playlist size " + playlist.size() + ", insert_playlist size " + insert_playlist.size());
*/

        for(int i =0; i < insert_playlist.size(); i++) {
            //Log.d(LOG_TAG, "insertColumn");
            ContentValues values = new ContentValues();
            values.put(DataBases.CreateDB.PLAYLIST_IDX, index);
            values.put(DataBases.CreateDB.PLAYLIST_NAME, playlistName);
            values.put(DataBases.CreateDB.FILE_PATH, insert_playlist.get(i));
            values.put(DataBases.CreateDB.PROGRESS, 0);
            //Log.d(LOG_TAG, "Add files in db : playlist.get(i) " + playlist.get(i));
            mDB.insert(DataBases.CreateDB._TABLENAME, null, values);
        }

        return insert_playlist.size();
    }

    public Cursor getPlaylistCursorByIndex(int index) {
        Cursor mCursor = mDB.query(DataBases.CreateDB._TABLENAME, null, DataBases.CreateDB.PLAYLIST_IDX + "=? AND " + DataBases.CreateDB.PROGRESS + " =?",
                new String[] {String.valueOf(index), "0" }, null, null, null);

        //Cursor mCursor = mDB.rawQuery("SELECT * FROM " + DataBases.CreateDB._TABLENAME + " WHERE " + DataBases.CreateDB.PLAYLIST_IDX + "=" + playlist_index +
//                                " AND " + DataBases.CreateDB.PROGRESS + "= 0", null);

//        Cursor mCursor = mDB.query(DataBases.CreateDB._TABLENAME, null, "playlist_idx=? and progress=?" ,
//                new String[] {"0", "0" }, null, null, null);

//        Cursor mCursor = mDB.query(DataBases.CreateDB._TABLENAME, null, "progress=?" ,
//                new String[] {"0"}, null, null, null);


        //Cursor mCursor = mDB.query(DataBases.CreateDB._TABLENAME, null, DataBases.CreateDB.PLAYLIST_IDX + "=?" ,
        //                           new String[] {"0" }, null, null, null);

        return mCursor;
    }
    public ArrayList<String> getFilelist(int playlist_index) {

        ArrayList<String> playlist = new ArrayList<String>();
        Cursor mCursor = getPlaylistCursorByIndex(playlist_index);

        if(mCursor.getCount() > 0) {
            Log.d(LOG_TAG, "Playlist exist. playlist index " + playlist_index + ", playlist count " + mCursor.getCount());
            mCursor.moveToFirst();

            for(int i=0; i<mCursor.getCount(); i++) {
                playlist.add(mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.FILE_PATH)));
                mCursor.moveToNext();
            }
            return playlist;
        } else {
            Log.d(LOG_TAG, "There is no playlist. playlist index " + playlist_index);
        }
        return null;
    }
/*
    public boolean isExistCheckItem( int playlist_index, String path ) {
        ArrayList<String> playlist = new ArrayList<String>();

        Cursor mCursor = mDB.query(DataBases.CreateDB._TABLENAME, null, DataBases.CreateDB.PLAYLIST_IDX + "=" + playlist_index, null, null, null, null);
        if(mCursor.getCount() > 0) {

            mCursor.moveToFirst();

            for(int i=0; i<mCursor.getCount(); i++) {
                if (0 == path.compareTo(mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.FILE_PATH)))) {
                    Log.d(LOG_TAG, "isExistCheck() : " + path + " is exist. In the playlist index " + playlist_index);
                    return true;
                }
            }
            return false;
        }

        Log.d(LOG_TAG, "isExistCheck() : " + path + " is not exist. In the playlist index " + playlist_index);
        return false;
    }
*/
    public int getTotalItems() {
        Cursor mCursor = mDB.query(DataBases.CreateDB._TABLENAME, null, null, null, null, null, null);
        return mCursor.getCount();
    }

    public int getNumOfItemsInPlaylist(int index) {
        Cursor mCursor = getPlaylistCursorByIndex(index);
        return mCursor.getCount();
    }

    public int getLastIndex() {
        Cursor mCursor = mDB.query(DataBases.CreateDB._TABLENAME, null, DataBases.CreateDB.PROGRESS + " = 0", null, null, null, null);

        if(mCursor.getCount() == 0)
            return 0;

        mCursor.moveToFirst();
        int totalItems = mCursor.getCount();
        int index = 1;

        for(int i = 0; i < totalItems; i++) {
            if(index < mCursor.getInt(mCursor.getColumnIndex(DataBases.CreateDB.PLAYLIST_IDX)))
                index++;

            mCursor.moveToNext();
        }
        return index;
    }

    public int getNewIndex() {
        return getLastIndex() + 1;
    }

//    public void setCurrentIndex(int index) {
//        sCurrentIndex = index;
//    }

//    public int getsCurrentIndex() {
//        return sCurrentIndex;
//    }

    public PlaylistTitlesClass getPlaylistTitleItem(int playlistIndex) { // playlist index 는 1부터시작
        Cursor mCursor = getPlaylistCursorByIndex(playlistIndex);

        if (mCursor.getCount() <= 0) {
            return null;
        }

        mCursor.moveToFirst();
        PlaylistTitlesClass playlistTitleItem = new PlaylistTitlesClass();

        playlistTitleItem.numOfFiles = mCursor.getCount();
        playlistTitleItem.name = mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.PLAYLIST_NAME));
        playlistTitleItem.playlistIndex = playlistIndex;

        return playlistTitleItem;
    }


    public boolean dupCheckPlaylistName(String playlistName) {

        if(playlistName.length() == 0) {
            Log.d(LOG_TAG, "dupCheckPlaylistName get null string");
            return Constants.FAIL;
        }
        int totalNumOfIndex = getLastIndex();

        for(int i = 1; i <= totalNumOfIndex; i++ ) {
            if(0 == playlistName.compareTo(getPlaylistTitleItem(i).name)) {
                Log.d(LOG_TAG, "dupCheckPlaylistName has duplicated string");
                return Constants.FAIL;
            }
        }

        return Constants.SUCCESS;
    }

/*
    ArrayList<PlaylistTitlesClass> getPlaylistTitles(int playlistIndex) {
        Cursor mCursor = mDB.query(DataBases.CreateDB._TABLENAME, null, null, null, null, null, null);
        if(mCursor.getCount() <= 0) {
            return null;
        }

        mCursor.moveToFirst();
        ArrayList<PlaylistTitlesClass> playListTitles;
        int totalItems = mCursor.getCount();
        int index = 1;
        int fileCount = 0;
        PlaylistTitlesClass playlistTitle;

        for(int i = 0; i < totalItems; i++) {
            if(index < mCursor.getInt(mCursor.getColumnIndex(DataBases.CreateDB.PLAYLIST_IDX))) {
                index++;
                playlistTitle.numOfFiles = fileCount;
                fileCount = 0;

                mCursor.moveToPrevious();
                playlistTitle.title = mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB._TABLENAME));
                mCursor.moveToNext();

                playListTitles.add(playlistTitle);
            }
            fileCount++;
            mCursor.moveToNext();

            if( i == (totalItems - 1) ) { // last item needs to be saved
                playlistTitle.numOfFiles = fileCount;
                playlistTitle.title = mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB._TABLENAME));
                playListTitles.add(playlistTitle);
            }
        }
        return playListTitles;
    }
*/
    public void removePlaylist(int index) {
        int lastIndex = getLastIndex();
        int updateIndex = index;

        mDB.delete(DataBases.CreateDB._TABLENAME, DataBases.CreateDB.PLAYLIST_IDX + "=" + index , null);

        if(lastIndex > index) {
            Cursor mCursor = mDB.query(DataBases.CreateDB._TABLENAME, null, null, null, null, null, null);

            do {
                ContentValues values = new ContentValues();
                values.put(DataBases.CreateDB.PLAYLIST_IDX, updateIndex);
                updateIndex++;
                mDB.update(DataBases.CreateDB._TABLENAME, values, DataBases.CreateDB.PLAYLIST_IDX + "=" + updateIndex, null );
            } while(updateIndex < lastIndex );
        }
    }

    public int removeItemInPlaylist(int index, String Path) {

        mDB.delete(DataBases.CreateDB._TABLENAME, DataBases.CreateDB.PLAYLIST_IDX + "=" + index + " and " + DataBases.CreateDB.FILE_PATH + "=" + Path, null);

        return 0;
    }

    public void updateBookmark(ArrayList<BookMark> bookmarkList) {

        mDB.delete( DataBases.CreateDB._TABLENAME, DataBases.CreateDB.PLAYLIST_IDX + "=? AND " +
                                                   DataBases.CreateDB.FILE_PATH + "=? AND " +
                                                   DataBases.CreateDB.PROGRESS + " !=?",
                    new String[] {String.valueOf( Valueable_Util.getCurrentPlaylistIdx() ), Valueable_Util.getCurrentPlayingFilePath(), "0" } );

        for(int i = 0; i < bookmarkList.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(DataBases.CreateDB.PLAYLIST_IDX, Valueable_Util.getCurrentPlaylistIdx());
            values.put(DataBases.CreateDB.FILE_PATH, Valueable_Util.getCurrentPlayingFilePath());
            values.put(DataBases.CreateDB.PLAYLIST_NAME, "bookmark");
            values.put(DataBases.CreateDB.PROGRESS, bookmarkList.get(i).progress);

            Log.d(LOG_TAG, "updateBookmark i " + i + " idx : " + Valueable_Util.getCurrentPlaylistIdx() +
                                                     ", path " +  Valueable_Util.getCurrentPlayingFilePath() +
                                                     ", progress " +  bookmarkList.get(i).progress
            );
            mDB.insert(DataBases.CreateDB._TABLENAME, null, values);
        }
    }

    public ArrayList<BookMark> getBookmarkList() {

        ArrayList<BookMark> bookmarkList = new ArrayList<BookMark>();;
        Cursor mCursor = mDB.query(DataBases.CreateDB._TABLENAME, null, DataBases.CreateDB.PLAYLIST_IDX + "=? AND " +
                        DataBases.CreateDB.FILE_PATH + "=? AND " +
                        DataBases.CreateDB.PROGRESS + " !=?",
                new String[] { String.valueOf(Valueable_Util.getCurrentPlaylistIdx()), Valueable_Util.getCurrentPlayingFilePath(), "0" },
                null, null, null);

        mCursor.moveToFirst();
        Log.d(LOG_TAG, "getBookmarkList mCursor size " + mCursor.getCount() );

        for(int i = 0; i < mCursor.getCount(); i++) {
            BookMark bookMark = new BookMark();
            bookMark.progress = mCursor.getInt(mCursor.getColumnIndex(DataBases.CreateDB.PROGRESS));
            bookmarkList.add(bookMark);
            mCursor.moveToNext();
            Log.d(LOG_TAG, "getBookmarkList i " + i + " , progress " + bookMark.progress );
        }

        return bookmarkList;
    }
/*
    public long insertColumn(String name, String contact, String email) {
        Log.d(LOG_TAG, "insertColumn");
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.NAME, name);
        values.put(DataBases.CreateDB.CONTACT, contact);
        values.put(DataBases.CreateDB.EMAIL, email);
        return mDB.insert(DataBases.CreateDB._TABLENAME, null, values);
    }

    public boolean updateColumn(long id, String name, String contact, String email) {
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.NAME, name);
        values.put(DataBases.CreateDB.CONTACT, contact);
        values.put(DataBases.CreateDB.EMAIL, email);
        return mDB.update(DataBases.CreateDB._TABLENAME, values, "_id="+id, null) > 0;
    }

    public boolean deleteColumn(long id) {
        return mDB.delete(DataBases.CreateDB._TABLENAME, "_id=" + id, null) > 0;
    }

    //커서 전체를 선택하는 메소드
    public Cursor getAllColumns() {
        return mDB.query(DataBases.CreateDB._TABLENAME, null, null, null, null, null, null);
    }

    //ID 컬럼 얻어오기
    public Cursor getColumn(long id) {
        Cursor c = mDB.query(DataBases.CreateDB._TABLENAME, null,
                "_id="+id, null, null, null, null);
        //받아온 컬럼이 null이 아니고 0번째가 아닐경우 제일 처음으로 보냄
        if (c != null && c.getCount() != 0)
            c.moveToFirst();
        return c;
    }

    //이름으로 검색하기 (rawQuery)
    public Cursor getMatchName(String name) {
        Cursor c = mDB.rawQuery( "Select * from address where name" + "'" + name + "'", null);
        return c;
    }

    public void test() {
        String[] condition = { "1" };
        //Cursor mCursor = mDB.query(DataBases.CreateDB._TABLENAME, null, "name=?", condition, null, null, null);
        Cursor mCursor = mDB.query(DataBases.CreateDB._TABLENAME, null, "name=" + "1", null, null, null, null);
        if(mCursor.getCount() != 0) {
            mCursor.moveToFirst();
            Log.d(LOG_TAG, "test total count " + mCursor.getCount() + ", " + mCursor.getInt(mCursor.getColumnIndex("_id")) + ", " + mCursor.getString(mCursor.getColumnIndex("name")));
        } else {
            Log.d(LOG_TAG, "test db count is 0");
        }
    }
*/

    public class DatabaseHelper extends SQLiteOpenHelper {

        // 생성자
        public DatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);

        }

        // 최초 DB를 만들때 한번만 호출된다.
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "database onCreate()");
            db.execSQL(DataBases.CreateDB._CREATE_PLAYLIST_DB);

        }

        // 버전이 업데이트 되었을 경우 DB를 다시 만들어 준다.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+DataBases.CreateDB._TABLENAME);
            onCreate(db);
        }
    }

}
