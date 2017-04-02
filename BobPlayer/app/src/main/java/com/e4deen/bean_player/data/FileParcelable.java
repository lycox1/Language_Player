package com.e4deen.bean_player.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;

/**
 * Created by user on 2016-12-18.
 */
public class FileParcelable implements Parcelable {

    static String LOG_TAG = "Jog_Player_FileList_Parcelable";
    final int E_SUCCESS = 1;
    final int E_ERROR = 0;
    String path, fileName, fullPath;

    public FileParcelable() {
    }

    public FileParcelable(String filePath) {

        if(filePath == null) {
            Log.d(LOG_TAG, "FileParcelable filePath is null");
            return;
        }

        File file = new File(filePath);

        fullPath = filePath;
        path = file.getParent();
        fileName = file.getName();
    }

    public FileParcelable(Parcel in) {

        File file = new File(in.readString());

        fullPath = file.getPath();
        path = file.getParent();
        fileName = file.getName();

//        Log.d(LOG_TAG, "FileParcelable fullPath " + fullPath + ", path " + path + ", fileName " + fileName + ", in.readString " + in.readString() + ", file.getPath()" + file.getPath());
    }

    public int addFilePath(String filePath) {
        if(filePath == null) {
            Log.d(LOG_TAG, "addFilePath filePath is null");
            return E_ERROR;
        }
        File file = new File(filePath);

        fullPath = filePath;
        path = file.getParent();
        fileName = file.getName();

        return E_SUCCESS;
    }

    public String getFullPath() {
//        Log.d(LOG_TAG, "getFullPath " + fullPath);
        return fullPath;
    }

    public String getPath() {
//        Log.d(LOG_TAG, "getPath " + path);
        return path;
    }

    public String getFileName() {
//        Log.d(LOG_TAG, "getFileName " + fileName);
        return fileName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullPath);
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public FileParcelable createFromParcel(Parcel in) {
            return new FileParcelable(in);
        }

        @Override
        public FileParcelable[] newArray(int size) {
            // TODO Auto-generated method stub
            return new FileParcelable[size];
        }

    };
}
