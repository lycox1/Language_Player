package com.e4deen.bean_player.view.file_explorer_view.adapter;

import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.e4deen.bean_player.R;
import com.e4deen.bean_player.data.Constants;
import com.e4deen.bean_player.db.DataBases;
import com.e4deen.bean_player.db.PlaylistTitlesClass;
import com.e4deen.bean_player.db.Playlist_manager_db;
import com.e4deen.bean_player.util.Valueable_Util;
import com.e4deen.bean_player.util.Vibe;
import com.e4deen.bean_player.view.file_explorer_view.activity.FileSearchActivity;

import java.util.ArrayList;

/**
 * Created by user on 2017-03-19.
 */

public class Adapter_plm_playlist extends BaseAdapter {


    static String LOG_TAG = "Bean_Player_PLM_PlayList_Adapter";
    final int E_SUCCESS = 1;
    final int E_ERROR = 0;
    //public Playlist_manager_db mPLM_DB;
    Context mContext;

    ArrayList<PlaylistTitlesClass> mPlaylistTitleItems = new ArrayList<PlaylistTitlesClass>();

    public Adapter_plm_playlist(Context context) {
        //mPLM_DB = db;
        mContext = context;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return mPlaylistTitleItems.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lv_plm_item_playlist, parent, false);
        }

        TextView playlistNameView = (TextView) convertView.findViewById(R.id.tv_playlist_name);
        TextView playlistFilesView = (TextView) convertView.findViewById(R.id.tv_plm_num_of_files);
        //ImageView btn_plm_playlist_play = ((ImageView) convertView.findViewById(R.id.btn_plm_playlist_play)).setOnTouchListener(mBtnOnTouchistener);
        //ImageView btn_plm_playlist_delete = ((ImageView) convertView.findViewById(R.id.btn_plm_playlist_delete)).setOnTouchListener(mBtnOnTouchistener);
        // setTag 이용 필요

        ImageView btn_plm_playlist_play = (ImageView) convertView.findViewById(R.id.btn_plm_playlist_play);
        ImageView btn_plm_playlist_delete = (ImageView) convertView.findViewById(R.id.btn_plm_playlist_delete);
        btn_plm_playlist_play.setOnTouchListener(mBtnOnTouchistener);
        btn_plm_playlist_delete.setOnTouchListener(mBtnOnTouchistener);

        PlaylistTitlesClass playlistTitleItem = mPlaylistTitleItems.get(position);

        btn_plm_playlist_play.setTag(playlistTitleItem.playlistIndex);
        btn_plm_playlist_delete.setTag(playlistTitleItem.playlistIndex);

        playlistNameView.setText(playlistTitleItem.name);
        playlistFilesView.setText("Consist of " + String.valueOf(playlistTitleItem.numOfFiles + " files"));

        return convertView;
    }

    @Override
    public PlaylistTitlesClass getItem(int position) {
        return mPlaylistTitleItems.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(PlaylistTitlesClass playlistTitleItem) {

        mPlaylistTitleItems.add(playlistTitleItem);
    }

    public void resetItems() {
        Log.d(LOG_TAG, "resetItems");
        mPlaylistTitleItems.clear();
    }

    View.OnTouchListener mBtnOnTouchistener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int action = event.getAction();
            int id = v.getId();

            if (action == MotionEvent.ACTION_DOWN) {
                Vibe.mVibe.vibration(30);
            }

            Log.d(LOG_TAG, "mBtnOnTouchistener test" + v.getTag());

            switch (id) {
                case R.id.btn_plm_playlist_play:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            ImageView img_btn = (ImageView) v;
                            img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));
                            break;
                        }

                        case MotionEvent.ACTION_UP: {
                            Log.d(LOG_TAG, "btn_plm_playlist_play Button Press and Released" + v.getTag());
                            ImageView img_btn = (ImageView) v;
                            img_btn.clearColorFilter();

                            Valueable_Util.setCurrentPlaylistIdx((int)v.getTag());
                            Constants.ChangeFragMainFileList = true;

                            ((FileSearchActivity)mContext).finishFragment();
                            ((FileSearchActivity)mContext).finish();

                            break;
                        }
                    }
                    break;

                case R.id.btn_plm_playlist_delete:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            ImageView img_btn = (ImageView) v;
                            img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));
                            break;
                        }

                        case MotionEvent.ACTION_UP: {
                            Log.d(LOG_TAG, "btn_plm_playlist_delete Button Press and Released" + v.getTag());
                            //http://recipes4dev.tistory.com/45
                            ImageView img_btn = (ImageView) v;
                            img_btn.clearColorFilter();

                            Log.d(LOG_TAG, "btn_plm_playlist_delete test log before numOfPlaylist" + DataBases.mPLM_DB.getLastIndex() );
                            DataBases.mPLM_DB.removePlaylist((int)(v.getTag()));
                            int numOfPlaylist = DataBases.mPLM_DB.getLastIndex();
                            Log.d(LOG_TAG, "btn_plm_playlist_delete test log after numOfPlaylist" + numOfPlaylist );
                            PlaylistTitlesClass playlistTitle;
                            mPlaylistTitleItems.clear();
                            for(int i=1; i <= numOfPlaylist; i++) {
                                playlistTitle = DataBases.mPLM_DB.getPlaylistTitleItem(i);
                                Log.d(LOG_TAG, "lsw onCreateView i " + i + " , name " + playlistTitle.name);
                                mPlaylistTitleItems.add(playlistTitle);
                            }

                            //mPlaylistTitleItems.remove((int)(v.getTag()));
                            notifyDataSetChanged();
                            break;
                        }
                    }
                    break;
            }
            return true;
        }
    };

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }
    @Override
    public boolean isEnabled(int arg0) {
        return true;
    }
    @Override
    public long getItemId(int position) {
        return position ;
    }


}
