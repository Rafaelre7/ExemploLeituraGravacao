package com.solinftec.exemploleituraescrita.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Helper {

    private static boolean DEBUG = false;
    private static String TAG = "Utils";

    public final static File otgViewerCachePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OTGViewer/cache");

    public static String retornarData() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");


        return sdf.format(c.getTime());
    }

    public static void deleteCache(File cachePath) {
        long cacheSize = getDirSize(cachePath);

        if (DEBUG)
            Log.d(TAG, "cacheSize: " + cacheSize);

        if (getDirSize(cachePath) > Constants.CACHE_THRESHOLD) {
            if (DEBUG)
                Log.d(TAG, "Erasing cache folder");

            deleteDir(cachePath);
        }

        deleteDir(otgViewerCachePath);
    }


    public static long getDirSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty or this is a file so delete it
        return dir.delete();
    }

}