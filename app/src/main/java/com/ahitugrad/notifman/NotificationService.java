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


public class NotificationService extends NotificationListenerService {

    Context context;

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
        String text = extras.getCharSequence("android.text").toString();
        int iconId = extras.getInt(Notification.EXTRA_SMALL_ICON);

        Context remotePackageContext = null;
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



        Log.i("Package", pack);
        //Log.i("Ticker", ticker);
        Log.i("Title", title);
        Log.i("Text", text);

        Intent msgrcv = new Intent("Msg");
        msgrcv.putExtra("package", pack);
        //msgrcv.putExtra("ticker", ticker);
        msgrcv.putExtra("title", title);
        msgrcv.putExtra("text", text);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();

        Log.i("iconId","iconid " + iconId);
        msgrcv.putExtra("icon",b);

        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);

        if(!ISAVAILABLE){
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