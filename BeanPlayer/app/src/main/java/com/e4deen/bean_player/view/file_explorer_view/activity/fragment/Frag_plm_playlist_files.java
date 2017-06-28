package com.e4deen.bean_player.view.file_explorer_view.activity.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.renderscript.Sampler;
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
import com.e4deen.bean_player.view.file_explorer_view.adapter.Adapter_plm_playlist_files;

import java.util.ArrayList;

/**
 * Created by user on 2017-03-30.
 */

public class Frag_plm_playlist_files extends Fragment {


    Context mContext;
    String LOG_TAG = "BeanPlayer_Frag_plm_playlist_files";
    ListView lv_plm_playlist_files;
    Adapter_plm_playlist_files mAdapter_plm_playlist_files;
    //public Playlist_manager_db mPLM_DB;
    int mTestCount = 0;
    public Custom_dialog_plm mCustomDialogPLM;
    //View rootView;
    ArrayList<String> mPlaylist;

    public Frag_plm_playlist_files() {
        mContext = Constants.sFileListManagerContext;
        //mContext = context;
        //mPLM_DB = db;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //View rootView = inflater.inflate(R.layout.frag_plm_playlist_files, container, false);
        View rootView = inflater.inflate(R.layout.frag_plm_playlist_files, container, false);

        lv_plm_playlist_files = (ListView) rootView.findViewById(R.id.lv_plm_playlist_files);
        mAdapter_plm_playlist_files = new Adapter_plm_playlist_files(mContext);

        rootView.findViewById(R.id.btn_plm_playlist_files_add).setOnTouchListener(mBtnOnTouchistener);
        rootView.findViewById(R.id.btn_plm_playlist_files_back).setOnTouchListener(mBtnOnTouchistener);
        rootView.findViewById(R.id.btn_plm_playlist_files_done).setOnTouchListener(mBtnOnTouchistener);

        int tempPlaylistIdx = Valueable_Util.getTempPlaylistIdx();

        mPlaylist = DataBases.mPLM_DB.getFilelist(tempPlaylistIdx);
        Log.d(LOG_TAG, "lsw onCreateView Frag_plm_playlist_files tempPlaylistIdx" + tempPlaylistIdx);
        Log.d(LOG_TAG, "lsw onCreateView mPlaylist size " + mPlaylist.size() );

        if(mPlaylist.size() == 0) {
            Log.e(LOG_TAG, "onCreateView playlist size 0 ########");
            return rootView;
        }

        for(int i=0; i < mPlaylist.size(); i++) {
            Log.d(LOG_TAG, "lsw onCreateView i " + i + " , file name " + mPlaylist.get(i) );
            mAdapter_plm_playlist_files.addItem(mPlaylist.get(i));
        }

        lv_plm_playlist_files.setAdapter(mAdapter_plm_playlist_files);
        lv_plm_playlist_files.setOnItemClickListener(listener);

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



            /*
            // TODO Auto-generated method stub
            Log.d(LOG_TAG, "onListItemClick   path.get(position) : " );

            //클릭된 아이템의 위치를 이용하여 데이터인 문자열을 Toast로 출력
            //Toast.makeText(mContext, "test", Toast.LENGTH_SHORT).show();

            file = new File(mAdapter_plm_filelist.getItem(position).getFullPath());

            //Log.d(LOG_TAG, "onListItemClick   path.get(position) : " + path.get(position));
            Log.d(LOG_TAG, "onListItemClick   file.getPath() : " + file.getPath());
            Log.d(LOG_TAG, "---------------------------------------------- " );

            if (file.isDirectory()) {
                Log.d(LOG_TAG, "test spot 1 " );
                if (file.canRead()) {
                    Log.d(LOG_TAG, "test spot 2 ");
                    //getDir(path.get(position));
                    getDir(mAdapter_plm_filelist.getItem(position).getFullPath());
                } else {
                    Log.d(LOG_TAG, "test spot 3 " );
                    new AlertDialog.Builder(mContext)
                            .setIcon(R.drawable.ic_bookmark)
                            .setTitle("[" + file.getName() + "] folder can't be read!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                }
                            }).show();
                }
            } else {
                Log.d(LOG_TAG, "test spot 4 " );
                new AlertDialog.Builder(mContext)
                        .setIcon(R.drawable.ic_bookmark)
                        .setTitle("[" + file.getName() + "]")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                FileParcelable filePath = new FileParcelable(file.getPath());
                                ArrayList<FileParcelable> fileParcerableList = new ArrayList<FileParcelable>();
                                fileParcerableList.add(filePath);
                                Intent intent = new Intent();
                                intent.putParcelableArrayListExtra("filePathList", fileParcerableList);
                                Log.d(LOG_TAG, "Send Intent filePath " + filePath.getFullPath());
                                //setResult(0,intent);
                                // TODO Auto-generated method stub
                            }
                        }).show();
            }
       */
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
                case R.id.btn_plm_playlist_files_add:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            ImageButton img_btn = (ImageButton) v;
                            img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));
                            break;
                        }

                        case MotionEvent.ACTION_UP: {
                            Log.d(LOG_TAG, "ADD Button Press and Released" );
                            ImageButton img_btn = (ImageButton) v;
                            img_btn.clearColorFilter();
                            ((FileSearchActivity)mContext).fragment_switch(Constants.IDX_PLM_FILELIST);
                            break;
                        }
                    }
                    break;

                case R.id.btn_plm_playlist_files_back:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            ImageButton img_btn = (ImageButton) v;
                            img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));
                            break;
                        }

                        case MotionEvent.ACTION_UP: {
                            Log.d(LOG_TAG, "Back Button Press and Released btn_plm_playlist_files_back" );
                            ImageButton img_btn = (ImageButton) v;
                            img_btn.clearColorFilter();
                            ((FileSearchActivity)getActivity()).finishFragment();

                            break;
                        }
                    }
                    break;

                case R.id.btn_plm_playlist_files_done:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            ImageButton img_btn = (ImageButton) v;
                            img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));
                            break;
                        }

                        case MotionEvent.ACTION_UP: {
                            Log.d(LOG_TAG, "Done Button Press and Released" );
                            ImageButton img_btn = (ImageButton) v;
                            img_btn.clearColorFilter();

                            PlaylistTitlesClass playlistTitle;
                            playlistTitle = DataBases.mPLM_DB.getPlaylistTitleItem(Valueable_Util.getTempPlaylistIdx());
                            Valueable_Util.setCurrentPlaylistIdx(playlistTitle.playlistIndex);
                            Valueable_Util.setCurrentPlaylistName(playlistTitle.name);
                            Constants.ChangeFragMainFileList = true;

                            Valueable_Util.setCurrentPlayingPosition((int)0); // player의 play filelist 가 초기화 되기 때문에 0 으로 셋팅.
                            ((FileSearchActivity)getActivity()).finishFragment();
                            ((FileSearchActivity)getActivity()).finish();
                            break;
                        }
                    }
                    break;
            }
            return true;
        }
    };

}
