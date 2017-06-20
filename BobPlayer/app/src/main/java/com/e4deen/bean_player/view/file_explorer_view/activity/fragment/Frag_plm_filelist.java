package com.e4deen.bean_player.view.file_explorer_view.activity.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.e4deen.bean_player.R;
import com.e4deen.bean_player.data.Constants;
import com.e4deen.bean_player.data.FileUtility;
import com.e4deen.bean_player.db.DataBases;
import com.e4deen.bean_player.db.Playlist_manager_db;
import com.e4deen.bean_player.util.Valueable_Util;
import com.e4deen.bean_player.util.Vibe;
import com.e4deen.bean_player.view.file_explorer_view.activity.FileSearchActivity;
import com.e4deen.bean_player.view.file_explorer_view.adapter.Adapter_plm_filelist;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by user on 2017-03-18.
 */

public class Frag_plm_filelist extends Fragment {

    String LOG_TAG = "BeanPlayer_Frag_plm_filelist";
    File file;
    ListView lv_ObjectList;
    Adapter_plm_filelist mAdapter_plm_filelist;
    public TextView mPath;
    static public String root = Environment.getExternalStorageDirectory() + "";
    Context mContext;
    //public Playlist_manager_db mPLM_DB;
    public ArrayList<String> mFilelist;
    int numOfFiles = 0;
    boolean mSelectAllMode = false;

    public Frag_plm_filelist(Context context) {
        mContext = context;
        //mPLM_DB = db;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag_plm_filelist, container, false);

        mPath = (TextView) rootView.findViewById(R.id.path);

        lv_ObjectList = (ListView) rootView.findViewById(R.id.lv_file_explorer);
        mAdapter_plm_filelist = new Adapter_plm_filelist();
        Log.d(LOG_TAG, "onCreateView setAdapter");
        lv_ObjectList.setAdapter(mAdapter_plm_filelist);
        lv_ObjectList.setOnItemClickListener(listener);

        rootView.findViewById(R.id.btn_plm_add).setOnTouchListener(mBtnOnTouchistener);
        rootView.findViewById(R.id.btn_plm_done).setOnTouchListener(mBtnOnTouchistener);
        rootView.findViewById(R.id.btn_plm_select_all).setOnTouchListener(mBtnOnTouchistener);

        getDir(root);

        Log.d(LOG_TAG, "onCreateView CurrentPlaylistIdx " + Valueable_Util.getCurrentPlaylistIdx());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getDir(String dirPath) {

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                File sel = new File(dir, filename);
                // Filters based on whether the file is hidden or not
                if( sel.isFile() && FileUtility.isAudioFile(sel) ) {
                    //return true;
//                } else if( sel.isDirectory() && FileUtility.hasAudioFolder(sel) ) {
                } else if( sel.isDirectory() && FileUtility.FolderWithMusic(sel.getAbsolutePath())) {
                    //return true;
                } else {
                    return false;
                }

                if(sel.isHidden()) {
                    return false;
                }
                return true;
            }
        };

        mAdapter_plm_filelist.resetItems();

        mPath.setText("Location: " + dirPath);
        File f = new File(dirPath);
        File[] files = f.listFiles(filter);
        numOfFiles = files.length;

        if (!dirPath.equals(root)) {
            mAdapter_plm_filelist.addItem(root);
            mAdapter_plm_filelist.addItem(f.getParent());
        }

        for (int i = 0; i < numOfFiles; i++) {
            File file = files[i];
            mAdapter_plm_filelist.addItem(file.getAbsolutePath());
        }

        if(mSelectAllMode) {
            mSelectAllMode = false;
            mAdapter_plm_filelist.setSelectAllMode(mSelectAllMode);
        }

        mFilelist = new ArrayList<String>();
        lv_ObjectList.setAdapter(mAdapter_plm_filelist);
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
            //Log.d(LOG_TAG, "onListItemClick   path.get(position) : " + position );

            //클릭된 아이템의 위치를 이용하여 데이터인 문자열을 Toast로 출력
            //Toast.makeText(mContext, "test", Toast.LENGTH_SHORT).show();

            file = new File(mAdapter_plm_filelist.getItem(position).getFullPath());

            Log.d(LOG_TAG, "onListItemClick position " + position + ", file.getPath() : " + file.getPath());

            if (file.isDirectory()) {
                if (file.canRead()) {
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


                Paint paint = new Paint();

                //if(false == mPLM_DB.isExistCheck( mCurrentDbIndex, file.getPath() ) ) {
                if( false == FileUtility.isExistInArrayList(mFilelist, file.getPath()) ) {
                    paint.setColor(R.color.colorPrimary);
                    paint.setAlpha(40);
                    mFilelist.add(file.getPath());
                    Log.d(LOG_TAG, "selected item position " + position + ", file name " + file.getPath() + " is selected");
                    mAdapter_plm_filelist.setSelectionForIndex(position, true);
                }
                else {
                    paint.setColor(Color.WHITE);
                    paint.setAlpha(0);
                    if(mFilelist.size() > 0) {
                        int idx = FileUtility.getIndexFromArrayList(mFilelist, file.getPath());
                        mFilelist.remove(idx);
                        Log.d(LOG_TAG, "selected item position " + position + ", file name " + file.getPath() + " is un-selected");
                        mAdapter_plm_filelist.setSelectionForIndex(position, false);
                    }
                }
                view.setBackgroundColor(paint.getColor());
            }
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
                case R.id.btn_plm_add:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {

                            ImageButton img_btn = (ImageButton) v;
                            img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));

                            /*
                            int numOfInsertedFile = 0;
                            numOfInsertedFile = DataBases.mPLM_DB.addFilesToPlaylist(Valueable_Util.getTempPlaylistIdx(), Valueable_Util.getTempPlaylistName(), mFilelist);

                            Log.d(LOG_TAG, "Add files number of files " + mFilelist.size());
                            for(int i = 0; i < mFilelist.size(); i++) {
                                Log.d(LOG_TAG, "Add files : " + mFilelist.get(i));
                            }

                            Toast.makeText(mContext, "Add " + numOfInsertedFile + " files. \n" +
                                    "Total " + DataBases.mPLM_DB.getNumOfItemsInPlaylist(Valueable_Util.getTempPlaylistIdx()) + " files in playlist.", Toast.LENGTH_SHORT).show();

                            //mAdapter_plm_filelist.notifyDataSetChanged();
                            //mFilelist = new ArrayList<String>();
                            lv_ObjectList.setAdapter(mAdapter_plm_filelist);

                            Valueable_Util.setCurrentPlaylistIdx(Valueable_Util.getTempPlaylistIdx());
                            Valueable_Util.setCurrentPlaylistName(Valueable_Util.getTempPlaylistName());
                            Valueable_Util.setCurrentPlayingPosition((int)0); // player의 play filelist 가 초기화 되기 때문에 0 으로 셋팅.
                            Constants.ChangeFragMainFileList = true;
                            */
                            addFilesToPlaylist();
                           break;
                        }

                        case MotionEvent.ACTION_UP: {
                            ImageButton img_btn = (ImageButton) v;
                            img_btn.clearColorFilter();
                            break;
                        }
                    }
                    break;

                case R.id.btn_plm_select_all:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            ImageButton img_btn = (ImageButton) v;
                            //img_btn.setColorFilter(0xc0c0c0, PorterDuff.Mode.MULTIPLY);
                            img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));

                            if(numOfFiles > 0) {
                                mSelectAllMode = (mSelectAllMode == false)?true:false;

                                mAdapter_plm_filelist.setSelectAllMode(mSelectAllMode);
                                lv_ObjectList.setAdapter(mAdapter_plm_filelist);
                                mFilelist = new ArrayList<String>();

                                if(mSelectAllMode) {
                                    for(int i =2 ; i < mAdapter_plm_filelist.getCount(); i++) {
                                        mFilelist.add(mAdapter_plm_filelist.getItem(i).getFullPath());
                                    }
                                }
                            }
                            break;
                        }

                        case MotionEvent.ACTION_UP: {
                            ImageButton img_btn = (ImageButton) v;
                            img_btn.clearColorFilter();
                            break;
                        }
                    }
                    break;

                case R.id.btn_plm_done:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            ImageButton img_btn = (ImageButton) v;
                            img_btn.setColorFilter(new ColorMatrixColorFilter(Constants.NEGATIVE));
                            break;
                        }

                        case MotionEvent.ACTION_UP: {
                            ImageButton img_btn = (ImageButton) v;
                            img_btn.clearColorFilter();
                            Log.d(LOG_TAG, "btn_plm_done ACTION_UP");
                            /*
                            Valueable_Util.setCurrentPlaylistIdx(Valueable_Util.getTempPlaylistIdx());
                            Valueable_Util.setCurrentPlaylistName(Valueable_Util.getTempPlaylistName());
                            Valueable_Util.setCurrentPlayingPosition((int)0); // player의 play filelist 가 초기화 되기 때문에 0 으로 셋팅.
                            Constants.ChangeFragMainFileList = true;
                            */
                            addFilesToPlaylist();
                            ((FileSearchActivity)getActivity()).finishFragment();
                            ((FileSearchActivity)getActivity()).finish();
                            // finish activity 하고 Main player 로 복귀하는 코드도 추가할 것
                            break;
                        }
                    }
                    break;
            }
            return true;
        }
    };

    void addFilesToPlaylist() {

        int numOfInsertedFile = 0;
        numOfInsertedFile = DataBases.mPLM_DB.addFilesToPlaylist(Valueable_Util.getTempPlaylistIdx(), Valueable_Util.getTempPlaylistName(), mFilelist);

        Log.d(LOG_TAG, "Add files number of files " + mFilelist.size());
        for(int i = 0; i < mFilelist.size(); i++) {
            Log.d(LOG_TAG, "Add files : " + mFilelist.get(i));
        }

        Toast.makeText(mContext, "Add " + numOfInsertedFile + " files. \n" +
                "Total " + DataBases.mPLM_DB.getNumOfItemsInPlaylist(Valueable_Util.getTempPlaylistIdx()) + " files in playlist.", Toast.LENGTH_SHORT).show();

        //mAdapter_plm_filelist.notifyDataSetChanged();
        //mFilelist = new ArrayList<String>();
        lv_ObjectList.setAdapter(mAdapter_plm_filelist);

        Valueable_Util.setCurrentPlaylistIdx(Valueable_Util.getTempPlaylistIdx());
        Valueable_Util.setCurrentPlaylistName(Valueable_Util.getTempPlaylistName());
        Valueable_Util.setCurrentPlayingPosition((int)0); // player의 play filelist 가 초기화 되기 때문에 0 으로 셋팅.
        Constants.ChangeFragMainFileList = true;
    }
}
