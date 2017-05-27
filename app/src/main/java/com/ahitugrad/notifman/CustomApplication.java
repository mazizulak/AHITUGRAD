package com.ahitugrad.notifman;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by huseyin on 07/05/2017.
 */

public class CustomApplication extends Application {

    public static int notId = -1;
    public static boolean ISAVAILABLE = false;
    public static final String NOTIFICATION_PREF_NAME = "notifications";
    public static final int SCREEN = 0;
    public static final int CALL = 1;
    public static final int ACTIVITY = 2;
    public static boolean TRACK = true;


    private DBHelper db;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences mPrefs = getSharedPreferences(NOTIFICATION_PREF_NAME,MODE_PRIVATE);
        TRACK = mPrefs.getBoolean("track",true);
        if(notId == -1){
            mPrefs.edit().putInt("latestid", 0);
            db = new DBHelper(getApplicationContext());
            String testcall = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,1,3,2,1,2,0,0,1,1,0,1,0,5,2,4,2,1,0,5,1,0,3,1,3,5,2,0,2,5,7,2,0,0,2,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,1,0,0,0,3,0,1,0,0,0,0,0,0,0,0,4,1,0,0,0,0,1,4,1,5,6,4,2,0,3,9,0,4,8,4,3,5,1,0,5,8,2,0,0,1,3,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,2,1,0,1,0,0,0,0,0,1,1,0,0,0,0,0,1,1,1,4,2,4,3,0,1,0,0,0,0,2,6,1,3,1,4,1,1,1,5,9,0,0,6,6,3,0,2,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,3,0,1,0,1,0,0,0,0,0,1,1,0,1,4,3,0,1,0,0,0,0,2,3,5,5,8,5,1,7,8,0,1,1,5,6,3,0,0,0,1,0,0,0,1,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,1,1,2,2,4,0,2,1,0,0,0,0,2,3,2,0,0,2,4,5,0,1,1,3,2,1,4,1,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,1,1,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,1,0,1,0,0,0,0,3,1,0,1,2,1,1,2,0,1,0,1,1,4,4,3,1,8,8,2,0,2,1,0,0,2,5,2,0,0,1,0,0,1,0,0,0,0,0,0,0,0,0,1,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,2,2,3,2,2,2,1,1,1,1,3,2,2,0,1,0,0,7,4,2,2,0,3,3,2,1,0,0,5,1,0,4,1,2,0,1,1,3,0,3,0,1,0,0,0,0";
            String testscreen = "3,0,0,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,4,1,0,0,1,0,4,7,4,3,0,0,0,0,2,4,4,1,1,6,15,5,7,6,17,4,13,6,1,0,5,3,0,2,3,3,0,0,0,0,2,8,0,0,2,4,0,0,1,0,0,1,1,4,1,0,0,0,0,0,1,0,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,1,0,0,1,0,2,0,0,0,3,3,2,2,8,0,2,6,3,1,12,5,8,5,0,1,3,2,6,1,1,5,0,4,5,0,0,1,7,4,7,0,0,0,1,0,3,6,9,1,0,2,0,3,1,0,0,1,4,0,0,2,3,2,3,1,1,2,0,0,0,0,2,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,6,4,8,5,0,1,4,1,3,8,8,6,0,1,15,6,13,5,6,3,3,4,1,2,0,0,0,3,4,4,0,0,0,1,0,0,0,1,1,0,2,1,1,0,2,9,0,1,0,0,0,0,1,1,2,1,1,3,1,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,5,5,2,1,0,3,0,4,2,4,5,3,14,10,5,7,3,2,9,7,7,20,12,0,5,6,7,12,7,7,12,7,6,6,16,5,13,9,2,0,6,1,1,3,3,9,0,2,6,8,4,2,1,6,1,1,4,1,0,1,6,4,0,8,11,1,1,5,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,2,2,10,5,5,1,4,4,0,2,10,4,3,7,10,8,11,7,15,4,0,1,5,10,13,5,9,4,3,0,0,0,1,4,3,9,3,1,1,6,0,0,2,4,0,5,0,0,1,6,1,3,2,0,0,0,0,5,1,1,2,3,6,4,0,2,2,0,0,6,1,4,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,6,6,4,5,9,10,2,1,0,2,1,4,2,0,0,2,3,6,2,6,1,3,0,0,5,3,0,1,5,4,3,5,2,2,0,3,0,5,3,4,2,5,4,1,1,0,5,5,16,6,2,0,4,7,0,7,7,0,0,0,0,0,0,0,0,0,1,0,4,1,3,0,4,3,11,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,8,12,6,6,11,10,3,0,0,0,1,0,2,3,0,3,7,10,8,2,4,4,6,13,1,6,7,5,8,7,4,2,0,0,1,0,6,4,3,12,5,4,18,12,7,4";
            String testactivity = "1,1,0,0,0,0,1,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,2,0,0,0,0,1,0,2,0,0,0,1,0,1,0,0,2,0,0,0,1,2,0,0,0,2,0,2,0,0,0,0,0,2,0,0,1,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,2,2,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,2,0,2,0,0,1,0,0,2,1,0,1,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,2,2,0,0,0,0,0,2,0,0,2,0,2,0,0,0,0,0,0,2,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,2,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,2,1,0,1,0,0,0,2,0,0,4,0,2,0,0,2,1,2,0,0,0,2,0,2,0,2,0,0,0,0,2,0,0,0,0,2,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,2,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,2,0,0,2,0,0,0,2,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,2,1,0,0,0,1,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,2,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,3,1,0,2,0,0,2,0,0,0,2,1,1,2,0,0,0,0,1,0,0,0,2,0,0,2,2,1,0,0";
            db.init(testcall, testscreen, testactivity); //Preload last week's data for test
        }
        notId = mPrefs.getInt("latestid",0);

    }

    public static int getAndUpdateLatestId(){
        notId += 1;

        return notId-1;
    }

    public  static int getIndexOfAvailabilityArray(){

        Calendar c = Calendar.getInstance();
        c.setTime(new GregorianCalendar().getTime());
        int index = (int) Math.floor((c.get(Calendar.MINUTE)/15) + 4*c.get(Calendar.HOUR) + 96*(c.get(Calendar.DAY_OF_WEEK)-1)+1);
        Log.v("Day Of Week:", c.get(Calendar.DAY_OF_WEEK)+"");
        Log.v("HOUR:", c.get(Calendar.HOUR)+"");
        Log.v("MINUTE:", c.get(Calendar.MINUTE)+"");
        Log.v("Array Index: ", index + "");
        return index;

    }
    public static void inputData(int type){
        switch (type){
            case SCREEN:
                BackgroundService.screencounter++;
                break;
            case CALL:
                BackgroundService.callcounter++;
                break;
            case ACTIVITY:
                BackgroundService.activitycounter++;
                break;
            default:
                Log.v("inputData: ","Unexpected input");
                break;
        }
    }

    public  static int getNotId(){
        return notId;
    }
}
