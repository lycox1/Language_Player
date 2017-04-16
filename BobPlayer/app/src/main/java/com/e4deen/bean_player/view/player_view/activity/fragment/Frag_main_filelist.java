package com.e4deen.bean_player.view.player_view.activity.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.e4deen.bean_player.R;
import com.e4deen.bean_player.data.Constants;
import com.e4deen.bean_player.db.Playlist_manager_db;
import com.e4deen.bean_player.view.player_view.adapter.Adapter_Main_PlayList;
import com.e4deen.bean_player.view.player_view.component.MediaPlayerController;

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
            ArrayList<String> filelist = mPLM_DB.getFilelist(Constants.mCurrentPlaylistIdx);

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
}
