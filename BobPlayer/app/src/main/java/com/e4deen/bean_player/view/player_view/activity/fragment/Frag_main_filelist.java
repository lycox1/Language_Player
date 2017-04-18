package com.e4deen.bean_player.view.player_view.activity.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.e4deen.bean_player.R;
import com.e4deen.bean_player.data.Constants;
import com.e4deen.bean_player.data.FileUtility;
import com.e4deen.bean_player.db.Playlist_manager_db;
import com.e4deen.bean_player.view.player_view.adapter.Adapter_Main_PlayList;
import com.e4deen.bean_player.view.player_view.component.MediaPlayerController;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by user on 2017-04-09.
 */

public class Frag_main_filelist extends Fragment {

    String LOG_TAG = "Frag_main_filelist";
    Context mContext;
    public Playlist_manager_db mPLM_DB;
    public Adapter_Main_PlayList mAdapterMainPlayList;
    ListView listview_playList;
    MediaPlayerController mMediaPlayerController;
    ArrayList<String> filelist;

    public Frag_main_filelist(Context context, Playlist_manager_db db, MediaPlayerController mediaPlayerController ) {
        mContext = context;
        mPLM_DB = db;
        mMediaPlayerController = mediaPlayerController;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag_main_filelist, container, false);

        listview_playList = (ListView) rootView.findViewById(R.id.playList);
        mAdapterMainPlayList = new Adapter_Main_PlayList();
        mAdapterMainPlayList.resetItems();
        listview_playList.setAdapter(mAdapterMainPlayList);
        listview_playList.setOnItemClickListener(listener);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        playListViewInit();
    }

    boolean playListViewInit() {
        Log.d(LOG_TAG, "playListViewInit ");
        mAdapterMainPlayList.resetItems();

        if(Constants.mCurrentPlaylistIdx > 0) {

            int numOfItems = mPLM_DB.getNumOfItemsInPlaylist(Constants.mCurrentPlaylistIdx);
            filelist = mPLM_DB.getFilelist(Constants.mCurrentPlaylistIdx);
            Log.d(LOG_TAG, "playListViewInit mCurrentPlaylistIdx " + Constants.mCurrentPlaylistIdx);

            for(int i = 0; i < numOfItems; i++ ) {
                String filepath = filelist.get(i);
                //Log.d(LOG_TAG, "playListViewInit fileName " + filepath);
                int index = filepath.lastIndexOf("/");
                String fileName = filepath.substring(index+1);
                mAdapterMainPlayList.addItem(fileName);
                //Log.d(LOG_TAG, "playListViewInit fileName " + fileName);
            }

            if(numOfItems > 0) {
                mMediaPlayerController.setPlayFile(filelist.get(0));
                mMediaPlayerController.setDuration();
                Constants.FILE_READY_STATUS = Constants.FILE_READY;
            }
        }

        listview_playList.setAdapter(mAdapterMainPlayList);

        return Constants.SUCCESS;
    }

    AdapterView.OnItemClickListener listener= new AdapterView.OnItemClickListener() {

        //ListView의 아이템 중 하나가 클릭될 때 호출되는 메소드
        //첫번째 파라미터 : 클릭된 아이템을 보여주고 있는 AdapterView 객체(여기서는 ListView객체)
        //두번째 파라미터 : 클릭된 아이템 뷰
        //세번째 파라미터 : 클릭된 아이템의 위치(ListView이 첫번째 아이템(가장위쪽)부터 차례대로 0,1,2,3.....)
        //네번재 파리미터 : 클릭된 아이템의 아이디(특별한 설정이 없다면 세번째 파라이터인 position과 같은 값)
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
            Log.d(LOG_TAG, "onListItemClick path.get(position) : " + position);
            Log.d(LOG_TAG, "onListItemClick mCurrentPlaylistIdx : " + Constants.mCurrentPlaylistIdx);

            mMediaPlayerController.stopPlay();
            mMediaPlayerController.setPlayFile(filelist.get(position));
            mMediaPlayerController.setDuration();
            mMediaPlayerController.startPlay();

            mAdapterMainPlayList.setSelectedIndex(position);

            listview_playList.setAdapter(mAdapterMainPlayList);


        }
    };

}
