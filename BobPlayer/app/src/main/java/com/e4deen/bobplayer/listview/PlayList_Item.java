package com.e4deen.bobplayer.listview;

import android.util.Log;

import java.io.File;

/**
 * Created by user on 2016-12-18.
 */
public class PlayList_Item {

    static String LOG_TAG = "Jog_Player_FileList_Item";
    final int E_SUCCESS = 1;
    final int E_ERROR = 0;

    String path, fileName, fullPath;

    public PlayList_Item(String filePath) {
        if(filePath == null) {
            Log.d(LOG_TAG, "PlayList_Item filePath is null");
            return;
        }
        File file = new File(filePath);

        fullPath = filePath;
        path = file.getParent();
        fileName = file.getName();
    }

    public String getFullPath() {
        Log.d(LOG_TAG, "getFullPath " + fullPath);
        return fullPath;
    }

    public String getPath() {
        Log.d(LOG_TAG, "getPath " + path);
        return path;
    }

    public String getFileName() {
        Log.d(LOG_TAG, "getFileName " + fileName);
        return fileName;
    }

    public int addFile(String filePath) {

        if(filePath == null) {
            Log.d(LOG_TAG, "PlayList_Item filePath is null");
            return E_ERROR;
        }
        File file = new File(filePath);

        fullPath = filePath;
        path = file.getParent();
        fileName = file.getName();

        return E_SUCCESS;
    }
}
