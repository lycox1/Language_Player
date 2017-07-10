package com.e4deen.bean_player.view.file_explorer_view.adapter.listview_item;

import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by user on 2017-03-05.
 */

public class Lv_item_filelist {

    static String LOG_TAG = "BeanPlayer_FileList_Item";
    final int E_SUCCESS = 1;
    final int E_ERROR = 0;
    final int FILE_EXPLORER_OBJ_TYPE_FILE = 0;
    final int FILE_EXPLORER_OBJ_TYPE_FOLDER = 1;
    int objType;
    boolean isAudioFile = false;
    boolean mSelected = false;

    String path, fileName, fullPath;
    File file = null;

    public Lv_item_filelist(String filePath) {
        if(filePath == null) {
            Log.d(LOG_TAG, "Lv_Main_PlayList_Item filePath is null");
            return;
        }

        file = new File(filePath);

        if(file.isDirectory()) {
            objType = FILE_EXPLORER_OBJ_TYPE_FOLDER;
        } else {
            objType = FILE_EXPLORER_OBJ_TYPE_FILE;
        }

        //isAudioFile();

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

    public File getFile() {
        Log.d(LOG_TAG, "getFile " + file.getName());
        return file;
    }

    public String getFileName() {
        Log.d(LOG_TAG, "getFileName " + fileName);
        return fileName;
    }

    public String getLastModifiedDate() {

        Date lastModifiedDate = new Date(file.lastModified());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", new Locale("en"));

        String timeStamp = dateFormat.format(lastModifiedDate);

        Log.d(LOG_TAG, fileName + "'s getLastModifiedDate : " + timeStamp);
        return timeStamp;
    }

    public void setSelection(boolean set) {
        mSelected =  set;
    }

    public boolean getSelection() {
        return mSelected;
    }

    public static String getFileType(String url)
    {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public Boolean isFolder() {
        Log.d(LOG_TAG, "isFolder objType " + objType);

        if(objType == FILE_EXPLORER_OBJ_TYPE_FOLDER) {
            return true;
        }
        return false;
    }

    public int addFile(String filePath) {

        if(filePath == null) {
            Log.d(LOG_TAG, "Lv_Main_PlayList_Item filePath is null");
            return E_ERROR;
        }
        file = new File(filePath);

        if(file.isDirectory()) {
            objType = FILE_EXPLORER_OBJ_TYPE_FOLDER;
        } else {
            objType = FILE_EXPLORER_OBJ_TYPE_FILE;
        }

        fullPath = filePath;
        path = file.getParent();
        fileName = file.getName();

        return E_SUCCESS;
    }
}
