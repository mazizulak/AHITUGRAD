package com.ahitugrad.notifman;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

/**
 * Created by maziz on 20.05.2017.
 */

public class BackgroundService extends Service implements SensorEventListener {
    private static BroadcastReceiver m_ScreenOffReceiver;
    private static BroadcastReceiver m_ScreenOnReceiver;
    private SensorManager sensorManager;
    private float ax;
    private float ay;
    private float az;
    boolean execute = false;
    private float varianceSum;
    private float avg;
    private float sum;
    private int count;
    Timer timer1 = new Timer();
    Timer timer2 = new Timer();
    private boolean doOnce = true;

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
        registerCallListener();

        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        timer1.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                execute = true;
            }
        },0, 2 * 60 * 1000 + 15 * 1000);
    }

    private void registerCallListener() {
        TelephonyManager tManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tManager.listen(new CallListener(getApplicationContext()),
                PhoneStateListener.LISTEN_CALL_STATE);
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

    @Override
    public void onSensorChanged(final SensorEvent event) {

        if (execute & doOnce){
            doOnce = false;
            timer2.schedule(new TimerTask() {
                @Override
                public void run() {
                    decideActivityLevel();
                    execute = false;
                    doOnce = true;
                }
            }, 15*1000);
        }

        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER && execute){
            ax=event.values[0];
            ay=event.values[1];
            az=event.values[2];
            update(ax,ay,az);
            Log.v("Event ax: ",ax + "");
            Log.v("Event ay: ",ay + "");
            Log.v("Event az: ",az + "");
        }

    }

    //Function below is taken from FUNF-CORE-ANDROID
    //Data collection is made by funf in a box including ActivityProbe
    //In order to feed our algorithm we must use the same calculations
    private void update(float x, float y, float z) {
        //Log.d(TAG, "UPDATE:(" + x + "," + y + "," + z + ")");
        // Iteratively calculate variance sum
        count++;
        float magnitude = (float)Math.sqrt(x*x + y*y + z*z);
        float newAvg = (count - 1)*avg/count + magnitude/count;
        float deltaAvg = newAvg - avg;
        varianceSum += (magnitude - newAvg) * (magnitude - newAvg)
                - 2*(sum - (count-1)*avg)
                + (count - 1) *(deltaAvg * deltaAvg);
        sum += magnitude;
        avg = newAvg;
        //Log.d(TAG, "UPDATED VALUES:(count, varianceSum, sum, avg) " + count + ", " + varianceSum+ ", " + sum+ ", " + avg);
    }

    private void decideActivityLevel() {
        //Log.d(LogUtil.TAG, "interval RESET");
        // Calculate activity and reset
        if (varianceSum >= 10.0f) {
            Log.v("Activity Level: " , "HIGH");
        } else if (varianceSum < 10.0f && varianceSum > 3.0f) {
            Log.v("Activity Level: " , "LOW");
        } else {
            Log.v("Activity Level: " , "NONE");
        }

        Log.v("VarianceSum: ", varianceSum +"");
        varianceSum = avg = sum = count = 0;
    }

    public void decideNotificationAllowOrNot(){
        ///MOST IMPORTANT PARTT!!!!
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
