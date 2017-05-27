package com.ahitugrad.notifman;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static com.ahitugrad.notifman.MainActivity.notifications;

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
    private Timer infiniteTimer = new Timer();
    private DBHelper dbHelper;
    public static double screencounter;
    public static double callcounter;
    public static double activitycounter;

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        Log.v("BackgroundService: ", "Has created");

        registerScreenOffReceiver();
        registerScreenOnReceiver();
        registerCallListener();
        dbHelper = new DBHelper(getApplicationContext());
        decideNotificationAllownessAndUpdateSQLTable();

        initiateSensors();
        resetCounters();
    }



    private void resetCounters() {
        screencounter = 0;
        callcounter = 0;
        activitycounter =0;
    }

    private void initiateSensors() {
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
                CustomApplication.inputData(CustomApplication.SCREEN);
                // do something, e.g. send Intent to main app
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(m_ScreenOnReceiver, filter);
    }

    @Override
    public void onDestroy()
    {
        Log.v("onDestroy: ", "called for service");
        unregisterReceiver(m_ScreenOffReceiver);
        m_ScreenOffReceiver = null;
        unregisterReceiver(m_ScreenOnReceiver);
        m_ScreenOnReceiver = null;
        sensorManager.unregisterListener(this);

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
            Log.v("Event ax: ",ax + "Event ay: " + ay + "Event az: "+ az);
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
    }

    private void decideActivityLevel() {
        //Log.d(LogUtil.TAG, "interval RESET");
        // Calculate activity and reset
        if (varianceSum >= 10.0f) {
            Log.v("Activity Level: " , "HIGH");
            CustomApplication.inputData(CustomApplication.ACTIVITY);
        } else if (varianceSum < 10.0f && varianceSum > 3.0f) {
            Log.v("Activity Level: " , "LOW");
        } else {
            Log.v("Activity Level: " , "NONE");
        }

        Log.v("VarianceSum: ", varianceSum +"");
        varianceSum = avg = sum = count = 0;
    }

    public void decideNotificationAllownessAndUpdateSQLTable(){


        infiniteTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int index = CustomApplication.getIndexOfAvailabilityArray();

                Cursor cursor = dbHelper.getData(index);
                cursor.moveToFirst();
                double pastCall = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMN_CALL));
                double pastActivity = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMN_ACTIVITY));
                double pastScreen = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMN_SCREEN));

                Log.v("15 Min Task:","Started");
                Log.v("index: ",index+"");
                Log.v("callcounter: ", callcounter+"");
                Log.v("activitycounter: ",activitycounter+"");
                Log.v("screencounter:",screencounter+"");

                Log.v("pastcallcounter: ", pastCall+"");
                Log.v("pastactivitycounter: ",pastActivity+"");
                Log.v("pastscreencounter:",pastScreen+"");

                callcounter = callcounter*0.8 + pastCall*0.2;
                activitycounter = activitycounter*0.8 + pastActivity*0.2;
                screencounter = screencounter*0.8 + pastScreen*0.2;

                //Decide the IS_AVAILABLE
                if(calculateCriticalValue(callcounter,activitycounter,screencounter)>=1){
                    CustomApplication.ISAVAILABLE = true;
                    Log.v("Yes: ", "I am Available");
                    releaseNotificationsToDevice();
                    notifications.clear();
                }else {
                    CustomApplication.ISAVAILABLE = false;
                    Log.v("No: ", "I am not Available");
                }

                dbHelper.updateData(index,callcounter,activitycounter,screencounter);
                resetCounters();
            }
        },15 * 60 * 1000, 15 * 60 * 1000 ); ///CHANGE TO 15 MIN !!!!
    }

    private void releaseNotificationsToDevice() {
        for (int i = 0; i < notifications.size() ; i++){

            Log.i("Şu Notu saldım: ", ""+i);
            Drawable appIcon = null;
            try {
                appIcon = getApplicationContext().getPackageManager().getApplicationIcon(notifications.get(i).getPackagename());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle(notifications.get(i).getTitle());
            mBuilder.setContentText(notifications.get(i).getContent());
            mBuilder.setAutoCancel(true);

            try {
                Bitmap largeIcon = (appIcon) != null ? ((BitmapDrawable) appIcon).getBitmap() : null;
                mBuilder.setLargeIcon(largeIcon);


                Intent resultIntent = getPackageManager().getLaunchIntentForPackage(notifications.get(i).getPackagename());
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                getApplicationContext(),
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                mBuilder.setContentIntent(resultPendingIntent);


                // Sets an ID for the notification
                int mNotificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
                // Gets an instance of the NotificationManager service
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // Builds the notification and issues it.
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            }catch (Exception e){
                Log.e("Yakaladım:", e.toString());
            }

        }
    }

    public double calculateCriticalValue(double call, double activity, double screen){
        return 0.8*call + 0.3*activity + 0.2*screen;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
