package com.e4deen.bean_player.view.player_view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.e4deen.bean_player.R;
import com.e4deen.bean_player.view.player_view.adapter.listview_item.Lv_Main_PlayList_Item;

import java.util.ArrayList;

/**
 * Created by user on 2016-12-18.
 */
public class Adapter_Main_PlayList extends BaseAdapter {

    static String LOG_TAG = "Jog_Player_PlayList_Adapter";
    final int E_SUCCESS = 1;
    final int E_ERROR = 0;
    int mSelectedIndex = 0;

    ArrayList<Lv_Main_PlayList_Item> playListFiles = new ArrayList<Lv_Main_PlayList_Item>();

    public Adapter_Main_PlayList() {}

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

        Log.e(LOG_TAG, "getView position " + position);
        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_playlist, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView fileNameView = (TextView) convertView.findViewById(R.id.tv_playlist) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        Lv_Main_PlayList_Item listViewItem = playListFiles.get(position);
        //Log.e(LOG_TAG, "getView listViewItem.getFileName() : " + listViewItem.getFileName() );
        // 아이템 내 각 위젯에 데이터 반영

        fileNameView.setText(listViewItem.getFileName());

        if(mSelectedIndex == position) {
            Log.e(LOG_TAG, "getView draw mSelectedIndex " + mSelectedIndex );
            Paint paint = new Paint();
            paint.setColor(R.color.colorPrimary);
            paint.setAlpha(40);
            convertView.setBackgroundColor(paint.getColor());
        } else {
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setAlpha(0);
            convertView.setBackgroundColor(paint.getColor());
        }

        return convertView;
    }

    public void setSelectedIndex(int position) {
        mSelectedIndex = position;

    }
    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return playListFiles.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String filePath) {
        Lv_Main_PlayList_Item item = new Lv_Main_PlayList_Item(filePath);
        playListFiles.add(item);
    }

    public void resetItems() {
        Log.d(LOG_TAG, "resetItems");
        playListFiles.clear();
    }

}
