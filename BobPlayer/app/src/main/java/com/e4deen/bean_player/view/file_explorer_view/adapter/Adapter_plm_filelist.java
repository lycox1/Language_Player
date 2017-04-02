package com.e4deen.bean_player.view.file_explorer_view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.e4deen.bean_player.R;
import com.e4deen.bean_player.data.FileUtility;
import com.e4deen.bean_player.view.file_explorer_view.adapter.listview_item.Lv_item_filelist;

import java.util.ArrayList;

/**
 * Created by user on 2017-03-05.
 */

public class Adapter_plm_filelist extends BaseAdapter {

    static String LOG_TAG = "Jog_Player_PlayList_Adapter";
    final int E_SUCCESS = 1;
    final int E_ERROR = 0;
    boolean mSelectAllMode = false;

    ArrayList<Lv_item_filelist> playListFiles = new ArrayList<Lv_item_filelist>();

    public Adapter_plm_filelist() {}

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

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lv_plm_item_file, parent, false);
        }

        ImageView fileImageView = (ImageView) convertView.findViewById(R.id.iv_file_explorer);
        TextView fileNameView = (TextView) convertView.findViewById(R.id.tv_file_name);
        TextView fileDateView = (TextView) convertView.findViewById(R.id.tv_file_date);

        Lv_item_filelist listViewItem = playListFiles.get(position);

        if( (position == 0 || position == 1) && (playListFiles.get(0).getFileName().equals("0") ) ) {
            fileImageView.setImageResource(0);
        } else if(listViewItem.isFolder()) {
            fileImageView.setImageResource(R.drawable.ic_folder);
        } else if(FileUtility.isAudioFile(listViewItem.getFile())){
            fileImageView.setImageResource(R.drawable.ic_music);
        } else {
            fileImageView.setImageResource(0);
        }

        Log.e(LOG_TAG, "getView listViewItem.getFileName() : " + listViewItem.getFileName() );

        if(playListFiles.get(0).getFileName().equals("0")) { // root folder 가 아닌경우

            if(position == 0) {
                fileNameView.setText("Root");
                fileDateView.setText("");
                fileImageView.setImageResource(0);
            } else if (position == 1) {
                fileNameView.setText("Up");
                fileDateView.setText("");
                fileImageView.setImageResource(0);
            } else {
                fileNameView.setText(listViewItem.getFileName());
                fileDateView.setText("Updated " + listViewItem.getLastModifiedDate());

                if(listViewItem.isFolder()) {
                    fileImageView.setImageResource(R.drawable.ic_folder);
                } else if(FileUtility.isAudioFile(listViewItem.getFile())){
                    fileImageView.setImageResource(R.drawable.ic_music);
                } else {
                    fileImageView.setImageResource(0);
                }
            }
        } else {
            fileNameView.setText(listViewItem.getFileName());
            fileDateView.setText("Updated " + listViewItem.getLastModifiedDate());
            if(listViewItem.isFolder()) {
                fileImageView.setImageResource(R.drawable.ic_folder);
            } else if(FileUtility.isAudioFile(listViewItem.getFile())){
                fileImageView.setImageResource(R.drawable.ic_music);
            } else {
                fileImageView.setImageResource(0);
            }
        }

        if(mSelectAllMode == true) {
            Paint paint = new Paint();
            paint.setColor(R.color.colorPrimary);
            paint.setAlpha(40);
            convertView.setBackgroundColor(paint.getColor());
        }

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
