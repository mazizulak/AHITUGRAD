package com.ahitugrad.notifman;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by huseyin on 07/05/2017.
 */

public class CustomApplication extends Application {

    public static int notId = -1;
    public static boolean ISAVAILABLE = false;
    public static final String NOTIFICATION_PREF_NAME = "notifications";

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences mPrefs = getSharedPreferences("notifications",MODE_PRIVATE);
        if(notId == -1){
            mPrefs.edit().putInt("latestid", 0);
        }
        notId = mPrefs.getInt("latestid",0);
    }

    public static int getAndUpdateLatestId(){
        notId += 1;

        return notId-1;
    }

    public  static int getNotId(){
        return notId;
    }
}
