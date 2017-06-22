package com.e4deen.bean_player.view.player_view.activity.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.e4deen.bean_player.R;
import com.e4deen.bean_player.db.Playlist_manager_db;
import com.e4deen.bean_player.view.player_view.adapter.Adapter_Main_PlayList;
import com.e4deen.bean_player.view.player_view.component.MediaPlayerController;

/**
 * Created by user on 2017-04-13.
 */

public class Frag_main_script extends Fragment {

    String LOG_TAG = "BeanPlayer_Frag_main_filelist";
    Context mContext;
    public Adapter_Main_PlayList mAdapterMainPlayList;
    ListView listview_playList;

    public Frag_main_script(Context context) {
        Log.d(LOG_TAG, "create Frag_main_shadowing fragment");
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag_main_shadowing, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
