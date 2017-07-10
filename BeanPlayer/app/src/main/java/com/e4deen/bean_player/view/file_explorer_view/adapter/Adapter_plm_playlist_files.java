package com.e4deen.bean_player.view.file_explorer_view.adapter;

import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
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
import com.e4deen.bean_player.data.FileUtility;
import com.e4deen.bean_player.db.PlaylistTitlesClass;
import com.e4deen.bean_player.db.Playlist_manager_db;
import com.e4deen.bean_player.util.Vibe;
import com.e4deen.bean_player.view.file_explorer_view.activity.FileSearchActivity;
import com.e4deen.bean_player.view.file_explorer_view.adapter.listview_item.Lv_item_filelist;

import java.util.ArrayList;

/**
 * Created by user on 2017-04-02.
 */

public class Adapter_plm_playlist_files extends BaseAdapter {

    static String LOG_TAG = "BeanPlayer_PLM_PlayListFiles_Adapter";
    final int E_SUCCESS = 1;
    final int E_ERROR = 0;
    boolean mSelectAllMode = false;
    Context mContext;
    //Playlist_manager_db mPLM_DB;

    ArrayList<Lv_item_filelist> playListFiles = new ArrayList<Lv_item_filelist>();

    public Adapter_plm_playlist_files(Context context) {
        mContext = context;
        //mPLM_DB = db;
    }

    public void setSelectAllMode(boolean onOff) {
        mSelectAllMode = onOff;
    }
    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return playListFiles.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        Log.e(LOG_TAG, "getView position : " + position );

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lv_plm_item_playlist_files, parent, false);
        }

        ImageView fileImageView = (ImageView) convertView.findViewById(R.id.iv_item_plm_playlist_files);
        TextView fileNameView = (TextView) convertView.findViewById(R.id.tv_item_plm_playlist_files_file_name);
        TextView fileDateView = (TextView) convertView.findViewById(R.id.tv_item_plm_playlist_files_file_date);

        Lv_item_filelist listViewItem = playListFiles.get(position);

        Log.e(LOG_TAG, "getView listViewItem.getFileName() : " + listViewItem.getFileName() );

        fileNameView.setText(listViewItem.getFileName());
        fileDateView.setText("Updated " + listViewItem.getLastModifiedDate());

        fileImageView.setImageResource(R.drawable.ic_music);

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Lv_item_filelist getItem(int position) {
        return playListFiles.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String filePath) {
        Lv_item_filelist item = new Lv_item_filelist(filePath);
        playListFiles.add(item);
    }

    public void resetItems() {
        Log.d(LOG_TAG, "resetItems");
        playListFiles.clear();
    }
/*
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

                            Constants.mCurrentPlaylistIdx = (int)v.getTag();

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

                            Log.d(LOG_TAG, "btn_plm_playlist_delete test log before numOfPlaylist" + mPLM_DB.getLastIndex() );
                            mPLM_DB.removePlaylist((int)(v.getTag()));
                            int numOfPlaylist = mPLM_DB.getLastIndex();
                            Log.d(LOG_TAG, "btn_plm_playlist_delete test log after numOfPlaylist" + numOfPlaylist );
                            PlaylistTitlesClass playlistTitle;
                            mPlaylistTitleItems.clear();
                            for(int i=1; i <= numOfPlaylist; i++) {
                                playlistTitle = mPLM_DB.getPlaylistTitleItem(i);
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
*/


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
