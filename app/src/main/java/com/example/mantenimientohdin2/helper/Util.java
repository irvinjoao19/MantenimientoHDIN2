package com.example.mantenimientohdin2.helper;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.Objects;

public class Util {

    public static File getFolder(Context context) {
        File folder = new File(Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath());
        //File folder = new File(Environment.getExternalStorageDirectory(), FolderImg); deprecated
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                Log.i("TAG", "FOLDER CREADO");
            }
        }
        return folder;
    }
}