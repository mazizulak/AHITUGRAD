package com.ahitugrad.notifman;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
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
    String a  = Calls.DURATION ;

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
    }

    private void registerCallEndReceiver() {
        m_CallEndReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Log.v(TAG, "Call Ended");
                // do something, e.g. send Intent to main app
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_CALL);
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
