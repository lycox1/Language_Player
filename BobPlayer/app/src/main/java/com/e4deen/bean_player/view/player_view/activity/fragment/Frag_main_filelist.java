package com.e4deen.bean_player.view.player_view.activity.fragment;

import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.e4deen.bean_player.R;
import com.e4deen.bean_player.data.Constants;
import com.e4deen.bean_player.db.DataBases;
import com.e4deen.bean_player.db.Playlist_manager_db;
import com.e4deen.bean_player.util.Valueable_Util;
import com.e4deen.bean_player.view.player_view.activity.MainActivity;
import com.e4deen.bean_player.view.player_view.adapter.Adapter_Main_PlayList;
import com.e4deen.bean_player.view.player_view.component.MediaPlayerController;

import java.util.ArrayList;

/**
 * Created by user on 2017-04-09.
 */

public class Frag_main_filelist extends Fragment {

    String LOG_TAG = "Frag_main_filelist";
    Context mContext;
    public Adapter_Main_PlayList mAdapterMainPlayList;
    ListView listview_playList;
    ArrayList<String> mFilelist;
    int mCurrentPlayingPosition = 0;
    //Handler mMainHandler;

    public Frag_main_filelist(Context context) {
        mContext = context;
        //mMainHandler = mainHandler;

        MediaPlayerController.OnPlayingCompleteCb onPlayingCompleteCb = new MediaPlayerController.OnPlayingCompleteCb() {
            @Override
            public void onPlayingCompleteCb() {
                Log.d(LOG_TAG, "OnPlayingCompleteCb Log Constants.OneRepeatMode " + Constants.OneRepeatMode);

                if(Constants.OneRepeatMode == true) {
                    Log.d(LOG_TAG, "OnPlayingCompleteCb OneRepeatMode true");
                    //MediaPlayerController.sController.stopPlay();
                    //MediaPlayerController.sController.startPlay();
                    return;
                }

                if(mFilelist.size() > mCurrentPlayingPosition + 1) {
                    mCurrentPlayingPosition++;
                    Valueable_Util.setCurrentPlayingPosition(mCurrentPlayingPosition);
                    MediaPlayerController.sController.stopPlay();
                    MediaPlayerController.sController.setPlayFile(mFilelist.get(mCurrentPlayingPosition));
                    ((MainActivity)mContext).btn_play_pause.setImageResource(R.drawable.btn_pause);
                    MediaPlayerController.sController.startPlay();
                    mAdapterMainPlayList.setSelectedIndex(mCurrentPlayingPosition);

                    listview_playList.setAdapter(mAdapterMainPlayList);
                    listview_playList.setSelection(mCurrentPlayingPosition);
                }
            }
        };

        MediaPlayerController.sController.setOnPlayingCompleteCb(onPlayingCompleteCb);
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

        try {
            playListViewInit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "onResume() Frag_main_filelist");
        try {
            if(Constants.ChangeFragMainFileList == true) {
                Log.d(LOG_TAG, "onResume() ChangeFragMainFileList is true");
                playListViewInit();
                Constants.ChangeFragMainFileList = false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    boolean playListViewInit() throws InterruptedException {
        Log.d(LOG_TAG, "playListViewInit ");
        mAdapterMainPlayList.resetItems();

        int currentPlaylistIdx = Valueable_Util.getCurrentPlaylistIdx();

        if(currentPlaylistIdx > 0) {

            int numOfItems = DataBases.mPLM_DB.getNumOfItemsInPlaylist(currentPlaylistIdx);
            mFilelist = DataBases.mPLM_DB.getFilelist(currentPlaylistIdx);
            Log.d(LOG_TAG, "playListViewInit currentPlaylistIdx " + currentPlaylistIdx);

            for(int i = 0; i < numOfItems; i++ ) {
                String filepath = mFilelist.get(i);
                //Log.d(LOG_TAG, "playListViewInit fileName " + filepath);
                int index = filepath.lastIndexOf("/");
                String fileName = filepath.substring(index+1);
                mAdapterMainPlayList.addItem(fileName);
                //Log.d(LOG_TAG, "playListViewInit fileName " + fileName);
            }

            if(numOfItems > 0) {
                Log.d(LOG_TAG, "playListViewInit numOfItems " + numOfItems);
                Log.d(LOG_TAG, "playListViewInit set file name " + mFilelist.get(0));

                if(MediaPlayerController.sController.mMediaPlayerInitComplete == false) {
                    Log.d(LOG_TAG, "playListViewInit Media Player initialize not complete case");
                    Thread myThread = new Thread(new Runnable() {
                        public void run() {
                            while (MediaPlayerController.sController.mMediaPlayerInitComplete == false) {
                                try {
                                    Log.d(LOG_TAG, "playListViewInit wait for Media Player initialize");
                                    Thread.sleep(300);
                                } catch (Throwable t) {
                                }
                            }
                            Log.d(LOG_TAG, "playListViewInit complete Media Player initialize");
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            MediaPlayerController.sController.setPlayFile(mFilelist.get(0));
                        }
                    });

                    myThread.start();
                } else {
                    Log.d(LOG_TAG, "playListViewInit Media Player initialize complete case");
                    MediaPlayerController.sController.stopPlay();
                    MediaPlayerController.sController.setPlayFile(mFilelist.get(0));
                    //(ImageButton)((AppCompatActivity)mContext).findViewById(R.id.btn_play_pause_id).callOnClick();
                    ((MainActivity)mContext).btn_play_pause.setImageResource(R.drawable.btn_play);
                    ((MainActivity)mContext).seekBar.setProgress(0);
                }
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
            int currentPlaylistIdx = Valueable_Util.getCurrentPlaylistIdx();
            Log.d(LOG_TAG, "onListItemClick CurrentPlaylistIdx : " + currentPlaylistIdx);

            MediaPlayerController.sController.stopPlay();
            MediaPlayerController.sController.setPlayFile(mFilelist.get(position));
            ((MainActivity)mContext).btn_play_pause.setImageResource(R.drawable.btn_pause);
            MediaPlayerController.sController.startPlay();

            mAdapterMainPlayList.setSelectedIndex(position);
            mCurrentPlayingPosition = position;
            Valueable_Util.setCurrentPlayingPosition(mCurrentPlayingPosition);
            listview_playList.setAdapter(mAdapterMainPlayList);
            listview_playList.setSelection(position);
        }
    };



}
