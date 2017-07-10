package com.e4deen.bean_player.view.file_explorer_view.activity.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.e4deen.bean_player.R;
import com.e4deen.bean_player.data.Constants;
import com.e4deen.bean_player.db.DataBases;
import com.e4deen.bean_player.db.PlaylistTitlesClass;
import com.e4deen.bean_player.db.Playlist_manager_db;
import com.e4deen.bean_player.util.Valueable_Util;
import com.e4deen.bean_player.util.Vibe;
import com.e4deen.bean_player.view.file_explorer_view.activity.FileSearchActivity;
import com.e4deen.bean_player.view.file_explorer_view.activity.fragment.custom_dialog.Custom_dialog_plm;
import com.e4deen.bean_player.view.file_explorer_view.adapter.Adapter_plm_playlist;

/**
 * Created by user on 2017-03-18.
 */

public class Frag_plm_playlist extends Fragment {

    Context mContext;
    String LOG_TAG = "BeanPlayer_Frag_plm_PlayList";
    ListView lv_plm_playlist;
    Adapter_plm_playlist mAdapter_plm_playlist;
    //public Playlist_manager_db mPLM_DB;
    int mTestCount = 0;
    public Custom_dialog_plm mCustomDialogPLM;
    View rootView;
//    int mSelectedPosition = 0;

    public Frag_plm_playlist() {
        mContext = Constants.sFileListManagerContext;
        //mPLM_DB = db;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.frag_plm_playlist, container, false);

        lv_plm_playlist = (ListView) rootView.findViewById(R.id.lv_plm_playlist);
        mAdapter_plm_playlist = new Adapter_plm_playlist(mContext);

        rootView.findViewById(R.id.btn_plm_add_new_playlist).setOnTouchListener(mBtnOnTouchistener);

        int numOfPlaylist = DataBases.mPLM_DB.getLastIndex();
        PlaylistTitlesClass playlistTitle;

        int total_num = DataBases.mPLM_DB.getTotalItems();
        int total_items = DataBases.mPLM_DB.getTotalItems();
        Log.d(LOG_TAG, "lsw onCreateView numOfPlaylist " + numOfPlaylist);
        Log.d(LOG_TAG, "lsw onCreateView total_num " + total_num + ", total_items " + total_items);

        for(int i=1; i <= numOfPlaylist; i++) {
            playlistTitle = DataBases.mPLM_DB.getPlaylistTitleItem(i);
            Log.d(LOG_TAG, "lsw onCreateView i " + i + " , name " + playlistTitle.name);
            mAdapter_plm_playlist.addItem(playlistTitle);
        }

//        mSelectedPosition = 0;
        lv_plm_playlist.setAdapter(mAdapter_plm_playlist);
        lv_plm_playlist.setOnItemClickListener(listener);

        return rootView;
    }

    AdapterView.OnItemClickListener listener= new AdapterView.OnItemClickListener() {

        //ListView의 아이템 중 하나가 클릭될 때 호출되는 메소드
        //첫번째 파라미터 : 클릭된 아이템을 보여주고 있는 AdapterView 객체(여기서는 ListView객체)
        //두번째 파라미터 : 클릭된 아이템 뷰
        //세번째 파라미터 : 클릭된 아이템의 위치(ListView이 첫번째 아이템(가장위쪽)부터 차례대로 0,1,2,3.....)
        //네번재 파리미터 : 클릭된 아이템의 아이디(특별한 설정이 없다면 세번째 파라이터인 position과 같은 값)


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Log.d(LOG_TAG, "onListItemClick position" + position + ", id " + id);

//            mSelectedPosition = position;
            PlaylistTitlesClass playlist = mAdapter_plm_playlist.getItem(position);
            Valueable_Util.setTempPlaylistIdx(playlist.playlistIndex);
            //Valueable_Util.setCurrentPlaylistName(playlist.name);

            ((FileSearchActivity)mContext).fragment_switch(Constants.IDX_PLM_PLAYLIST_FILES);
        }

    };


    View.OnTouchListener mBtnOnTouchistener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int action = event.getAction();
            int id = v.getId();

            if (action == MotionEvent.ACTION_DOWN) {
                Vibe.mVibe.vibration(30);
            }

            switch (id) {
                case R.id.btn_plm_add_new_playlist:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            ImageButton img_btn = (ImageButton) v;
                            img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));
                            break;
                        }

                        case MotionEvent.ACTION_UP: {
                            Log.d(LOG_TAG, "Button Press and Released" );
                            ImageButton img_btn = (ImageButton) v;
                            img_btn.clearColorFilter();
                            //((FileSearchActivity)getActivity()).fragment_switch(Constants.IDX_PLM_FILELIST);

                            //mCustomDialogPLM = new Custom_dialog_plm(mContext,okListener, cancelListener);
                            mCustomDialogPLM = new Custom_dialog_plm(mContext);
                            mCustomDialogPLM.show();
                            break;
                        }
                    }
                    break;
            }
            return true;
        }
    };


}
