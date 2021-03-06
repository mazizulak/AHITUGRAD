package com.ahitugrad.notifman;


import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.ByteArrayOutputStream;

import static com.ahitugrad.notifman.CustomApplication.ISAVAILABLE;
import static com.ahitugrad.notifman.CustomApplication.TRACK;


public class NotificationService extends NotificationListenerService {

    Context context;
    private String text;

    @Override
    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        String pack = sbn.getPackageName();
        //String ticker = sbn.getNotification().tickerText.toString();
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");
        if(extras.getCharSequence("android.text") !=null){
            text = extras.getCharSequence("android.text").toString();
        }
        int iconId = extras.getInt(Notification.EXTRA_SMALL_ICON);

        Context remotePackageContext;
        Bitmap bmp = null;
        try {
            remotePackageContext = getApplicationContext().createPackageContext(pack, 0);
            Drawable icon = remotePackageContext.getResources().getDrawable(iconId);
            if(icon !=null) {
                bmp = ((BitmapDrawable) icon).getBitmap();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent msgrcv = new Intent("Msg");
        msgrcv.putExtra("package", pack);
        msgrcv.putExtra("title", title);
        msgrcv.putExtra("text", text);

        if(bmp !=null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            msgrcv.putExtra("icon",b);
        }


        Log.v("Notification","Service içindeyim");
        Log.v("ISAVAILABLE: ", ""+ISAVAILABLE );
        Log.v("TRACK: ", TRACK+"");
        Log.v("Package: ", pack);
        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);

        if(!ISAVAILABLE && TRACK && !pack.equals("com.ahitugrad.notifman")){
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                cancelNotification(sbn.getPackageName(), sbn.getTag(), sbn.getId());
            }
            else {
                cancelNotification(sbn.getKey());
            }
        }


    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");

    }
}