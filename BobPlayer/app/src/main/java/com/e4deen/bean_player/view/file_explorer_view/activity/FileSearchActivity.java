package com.e4deen.bean_player.view.file_explorer_view.activity;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.e4deen.bean_player.R;
import com.e4deen.bean_player.data.Constants;
import com.e4deen.bean_player.db.Playlist_manager_db;
import com.e4deen.bean_player.util.Vibe;
import com.e4deen.bean_player.view.file_explorer_view.activity.fragment.Frag_plm_filelist;
import com.e4deen.bean_player.view.file_explorer_view.activity.fragment.Frag_plm_playlist;
import com.e4deen.bean_player.view.file_explorer_view.activity.fragment.Frag_plm_playlist_files;
import com.tsengvn.typekit.TypekitContextWrapper;

/**
 * Created by user on 2016-12-18.
 */
public class FileSearchActivity extends AppCompatActivity {
    static String LOG_TAG = "BeanPlayer_PLM_Activity";

    Context mContext;
    Vibe mVibe;
    //public Playlist_manager_db mPLM_DB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_manager);
        mContext = this;
        Vibe.mVibe = new Vibe(mContext);

        Log.d(LOG_TAG, "FileSearchActivity onCreate()" );
        //mPLM_DB = new Playlist_manager_db(this);
        //mPLM_DB.open();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.layout_playlist_manager, new Frag_plm_playlist(mContext), Constants.FRAG_PLM_PLAYLIST);
        fragmentTransaction.commit();
        fm.executePendingTransactions();
        Constants.frag_plm_state = Constants.IDX_PLM_PLAYLIST;
        getPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "FileSearchActivity onResume()");
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "FileSearchActivity onPause()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "FileSearchActivity onDestroy()");
        //mPLM_DB.close();
    }

    @Override
    public void finish() {
        super.finish();
        Log.d(LOG_TAG, "FileSearchActivity finish()");
    }

    public void finishFragment() {
        Log.d(LOG_TAG, "finishFragment");
        getFragmentManager().popBackStack();
    }

    public void fragment_switch(int fragNum) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        Log.d(LOG_TAG, "fragment_switch fragNum " + fragNum);

        if(fragNum == Constants.IDX_PLM_FILELIST) {
            fragmentTransaction.replace(R.id.layout_playlist_manager, new Frag_plm_filelist(mContext), Constants.FRAG_PLM_FILELIST);
            fragmentTransaction.addToBackStack(null);
            Constants.frag_plm_state = Constants.IDX_PLM_FILELIST;
        } else if(fragNum == Constants.IDX_PLM_PLAYLIST) {
            fragmentTransaction.replace(R.id.layout_playlist_manager, new Frag_plm_playlist(mContext), Constants.FRAG_PLM_PLAYLIST);
            Constants.frag_plm_state = Constants.IDX_PLM_PLAYLIST;
            fragmentTransaction.addToBackStack(null);
        } else if(fragNum == Constants.IDX_PLM_PLAYLIST_FILES) {
            fragmentTransaction.replace(R.id.layout_playlist_manager, new Frag_plm_playlist_files(mContext), Constants.FRAG_PLM_PLAYLIST_FILES);
            Constants.frag_plm_state = Constants.IDX_PLM_PLAYLIST_FILES;
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.commit();
        fm.executePendingTransactions();
    }

    void getPermission() {
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    0);
        }
    }
}
