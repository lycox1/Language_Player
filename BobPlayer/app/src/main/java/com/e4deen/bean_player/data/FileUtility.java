package com.e4deen.bean_player.data;

import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by user on 2017-03-11.
 */

public class FileUtility {

    public static String LOG_TAG = "BeanPlayer_FileUtility";

    public static boolean hasAudioFolder(File file) {
        return hasAudioFolderCheck(file);
    }

    public static boolean hasAudioFolder(String path) {
        File file = new File(path);
        return hasAudioFolderCheck(file);
    }

    public static boolean hasAudioFolderCheck(File file) {
        Log.d(LOG_TAG, "hasAudioFolderCheck file name " + file.getName() );
        if( file.isDirectory() ) {
            File[] fileList = file.listFiles();

            for (int i = 0; i < fileList.length; i++) {
                File file_t = fileList[i];

                if( hasAudioFolder(file_t) ) {
                    return true;
                }
            }
        } else if(isAudioFile(file))  {
            return true;
        }
        return false;
    }

    public static boolean isAudioFile(File file) {
        return isAudioFileCheck(file);
    }

    public static boolean isAudioFile(String filePath) {
        File file = new File(filePath);
        return isAudioFileCheck(file);
    }

    public static boolean isAudioFileCheck(File file) {
        boolean result = false;

        if( file.isDirectory() ) {
            return false;
        }

        String split_data[] = file.getName().split("\\.");
        if(split_data.length < 2) {
            return false;
        }

        String fileExtend = split_data[split_data.length - 1];
        String mime_type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtend);
        if(mime_type != null)
            result = mime_type.contains("audio");

        Log.d(LOG_TAG, "isAudioFileCheck file name " +file.getName() + ", mime type " + mime_type);

        //String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtend.toLowerCase());

        return result;
    }

    public static boolean isExistInArrayList(ArrayList<String> list, String string) {

        if(list.size() > 0) {
            for(int i = 0; i < list.size(); i++ ) {
                String temp = list.get(i);
                if( 0 == temp.compareTo(string) ) {
                    return true;
                }
            }
        }
        return false;
    }


    public static int getIndexFromArrayList(ArrayList<String> list, String string) {

        if(list.size() > 0) {
            for(int i = 0; i < list.size(); i++ ) {
                String temp = list.get(i);
                if( 0 == temp.compareTo(string) ) {
                    return i;
                }
            }
        }
        return -1;
    }
}
