package com.ahitugrad.notifman;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.CursorLoader;
import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by maziz on 20.05.2017.
 */

public class CallListener extends PhoneStateListener {
    Context mContext;
    public static String LOG_TAG = "CallListener";

    public CallListener(Context context) {
        mContext = context;
    }


    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                Log.i(LOG_TAG, "onCallStateChanged: CALL_STATE_IDLE");
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        Cursor c = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
                        int duration = c.getColumnIndex(CallLog.Calls.DURATION);// for duration
                        int number = c.getColumnIndex(CallLog.Calls.NUMBER);
                        c.moveToLast();
                        //while (c.moveToNext()){
                            String sure = c.getString(duration);
                            String phNumber = c.getString(number);
                            Log.v("Callog Duration: ",sure);
                            Log.v("Call Number: ", phNumber);
                        //}

                    }
                },4 * 1000);


                break;
            case TelephonyManager.CALL_STATE_RINGING:
                Log.i(LOG_TAG, "onCallStateChanged: CALL_STATE_RINGING");
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:

                Log.i(LOG_TAG, "onCallStateChanged: CALL_STATE_OFFHOOK");
                break;
            default:
                Log.i(LOG_TAG, "UNKNOWN_STATE: " + state);
                break;
        }
    }

}
