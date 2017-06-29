/**
 * Created by user on 2017-03-25.
 */

package com.e4deen.bean_player.view.file_explorer_view.activity.fragment.custom_dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.e4deen.bean_player.R;
import com.e4deen.bean_player.data.Constants;
import com.e4deen.bean_player.db.DataBases;
//import com.e4deen.bean_player.db.Playlist_manager_db;
import com.e4deen.bean_player.util.Valueable_Util;
import com.e4deen.bean_player.view.file_explorer_view.activity.FileSearchActivity;

public class Custom_dialog_plm extends Dialog {

    String LOG_TAG="BeanPlayer_PLM_Custom_Dialog";
    private TextView mContentView;
    private Button mOkButton;
    private Button mCancelButton;
    private String mTitle;
    private String mContent;
    //public Playlist_manager_db mPLM_DB;
    public Context mContext;
    private View.OnClickListener mOkButtonListener;
    private View.OnClickListener mCencelButtonListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = (float)0.50;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.custom_dialog_plm);

        mOkButton = (Button) findViewById(R.id.btn_left);
        mCancelButton = (Button) findViewById(R.id.btn_right);

        // 클릭 이벤트 셋팅
        if (mOkButtonListener != null && mCencelButtonListener != null) {
            mOkButton.setOnClickListener(mOkButtonListener);
            mCancelButton.setOnClickListener(mCencelButtonListener);
        } else if (mOkButtonListener != null
                && mCencelButtonListener == null) {
            mOkButton.setOnClickListener(mOkButtonListener);
        } else {

        }
    }
    /*
    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public Custom_dialog_plm(Context context,
                        View.OnClickListener singleListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mOkButtonListener = singleListener;
    }

    // 클릭버튼이 확인과 취소 두개일때 생성자 함수로 이벤트를 받는다
    public Custom_dialog_plm(Context context,View.OnClickListener okButtonListner,
                        View.OnClickListener cancelButtonListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mOkButtonListener = okButtonListner;
        this.mCencelButtonListener = cancelButtonListener;
    }
*/
    public Custom_dialog_plm(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        mContext = context;
        //mPLM_DB = db;
        this.mOkButtonListener = okListener;
        this.mCencelButtonListener = cancelListener;
    }

    private View.OnClickListener cancelListener = new View.OnClickListener() {
        public void onClick(View v) {
            dismiss();
        }
    };

    private View.OnClickListener okListener = new View.OnClickListener() {
        public void onClick(View v) {

            String newPlaylistName = ((EditText)findViewById(R.id.newPlaylistName)).getText().toString();
            if(0 == newPlaylistName.length()) {
                Log.d(LOG_TAG,"ok button listener - null case ");
                Toast.makeText(mContext, "Please input the new playlist name.", Toast.LENGTH_SHORT).show();
            } else if ( Constants.FAIL == DataBases.mPLM_DB.dupCheckPlaylistName(newPlaylistName)) {
                Toast.makeText(mContext, newPlaylistName +" already exist. Please type another name.", Toast.LENGTH_SHORT).show();
            } else {
                Valueable_Util.setTempPlaylistName(newPlaylistName);
                Valueable_Util.setTempPlaylistIdx(DataBases.mPLM_DB.getNewIndex());
                ((FileSearchActivity)mContext).fragment_switch(Constants.IDX_PLM_FILELIST);
                dismiss();
            }
            Log.d(LOG_TAG,"ok button listener - newPlaylistName " + newPlaylistName);

        }
    };


}

