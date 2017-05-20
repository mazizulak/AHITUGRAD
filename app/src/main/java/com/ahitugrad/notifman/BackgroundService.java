package com.ahitugrad.notifman;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import static android.content.ContentValues.TAG;

/**
 * Created by maziz on 20.05.2017.
 */

public class BackgroundService extends Service {
    private static BroadcastReceiver m_ScreenOffReceiver;
    private static BroadcastReceiver m_ScreenOnReceiver;
    private static BroadcastReceiver m_CallEndReceiver;
    private TelephonyManager tManager;

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        registerScreenOffReceiver();
        registerScreenOnReceiver();
        registerCallEndReceiver();
        tManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tManager.listen(new CallListener(getApplicationContext()),
                PhoneStateListener.LISTEN_CALL_STATE);


    }



    private void registerCallEndReceiver() {

        m_CallEndReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String state = intent.getAction();
                Log.v("state: ", state);
                if (state == null) {

                    //Outgoing call
                    String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                    Log.e("tag", "Outgoing number : " + number);

                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

                    Log.e("tag", "EXTRA_STATE_OFFHOOK");

                } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

                    Log.e("tag", "EXTRA_STATE_IDLE");

                } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                    //Incoming call
                    String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    Log.e("tag", "Incoming number : " + number);

                } else
                    Log.e("tag", "none");

                // do something, e.g. send Intent to main app
            }
        };
        IntentFilter filter = new IntentFilter(String.valueOf(PhoneStateListener.LISTEN_CALL_STATE));
        registerReceiver(m_CallEndReceiver, filter);
    }

    private void registerScreenOnReceiver() {
        m_ScreenOnReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Log.v(TAG, "ACTION_SCREEN_ON");
                Toast.makeText(context,"Hello",Toast.LENGTH_LONG).show();
                // do something, e.g. send Intent to main app
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(m_ScreenOnReceiver, filter);
    }

    @Override
    public void onDestroy()
    {
        unregisterReceiver(m_ScreenOffReceiver);
        m_ScreenOffReceiver = null;
        unregisterReceiver(m_ScreenOnReceiver);
        m_ScreenOnReceiver = null;
        unregisterReceiver(m_CallEndReceiver);
        m_CallEndReceiver = null;
    }

    private void registerScreenOffReceiver()
    {
        m_ScreenOffReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Log.v(TAG, "ACTION_SCREEN_OFF");
                // do something, e.g. send Intent to main app
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(m_ScreenOffReceiver, filter);
    }
}
