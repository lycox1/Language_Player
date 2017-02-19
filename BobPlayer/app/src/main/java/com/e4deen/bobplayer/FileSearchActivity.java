package com.e4deen.bobplayer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.e4deen.bobplayer.datatype.FileParcelable;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016-12-18.
 */
public class FileSearchActivity extends ListActivity {

    static String LOG_TAG = "Jog_Player_FileSearchActivity";
    final int E_SUCCESS = 1;
    final int E_ERROR = 0;
    public List<String> item = null;
    public List<String> path = null;
    //public String root = "/";
    //public String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    public String root = Environment.getExternalStorageDirectory() + "";
    public TextView mPath;
    File file;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_search);
        mPath = (TextView) findViewById(R.id.path);
        Log.d(LOG_TAG, "onCreate root : " + root);
        getPermission();
        getDir(root);
    }

    private void getDir(String dirPath) {

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                File sel = new File(dir, filename);
                // Filters based on whether the file is hidden or not
                return (sel.isFile() || sel.isDirectory())
                        && !sel.isHidden();

            }
        };

        mPath.setText("Location: " + dirPath);
        item = new ArrayList<String>();
        path = new ArrayList<String>();
        File f = new File(dirPath);
        File[] files = f.listFiles(filter);

        String[] fList = f.list(filter);

        if (!dirPath.equals(root)) {
            item.add(root);
            path.add(root);
            item.add("../");
            path.add(f.getParent());
        }

        Log.d(LOG_TAG, "getDir dirPath 1 : " + dirPath + ", " );
        Log.d(LOG_TAG, "getDir dirPath 2 : " + dirPath + ", " + fList[0]);

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            path.add(file.getPath());
            if (file.isDirectory())
                item.add(file.getName() + "/");
            else
                item.add(file.getName());
        }
        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.listview_file_search, item);
        setListAdapter(fileList);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        file = new File(path.get(position));

        Log.d(LOG_TAG, "onListItemClick   path.get(position) : " + path.get(position));
        Log.d(LOG_TAG, "onListItemClick   file.getPath() : " + file.getPath());
        Log.d(LOG_TAG, "---------------------------------------------- " );

        if (file.isDirectory()) {
            if (file.canRead())
                getDir(path.get(position));
            else {
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_bookmark)
                        .setTitle("[" + file.getName() + "] folder can't be read!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        }).show();
            }
        } else {
            new AlertDialog.Builder(this)
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
                            setResult(0,intent);
                            // TODO Auto-generated method stub
                        }
                    }).show();
        }
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
