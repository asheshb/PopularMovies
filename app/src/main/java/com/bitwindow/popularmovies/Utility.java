package com.bitwindow.popularmovies;

import android.content.Context;
import android.content.pm.PackageManager;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by ashbey on 9/5/2015.
 * Miscellaneous helper functions
 */
class Utility {

    public final static int POPULAR=0;
    public final static int RATING=1;
    public final static int FAVORITE=2;

    static String formatDate(long dateInMillis) {
        Date date = new Date(dateInMillis);
        return DateFormat.getDateInstance().format(date);
    }

    public static boolean isAppInstalled(String uri, Context context) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public static String implodeInt(int[] list){
        String sep = ",";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.length; i++) {
            sb.append(Integer.toString(list[i]));
            if (i != list.length - 1) {
                sb.append(sep);
            }
        }
        return sb.toString();
    }


}
