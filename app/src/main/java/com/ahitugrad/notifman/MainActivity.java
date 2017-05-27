package com.ahitugrad.notifman;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;

import static android.widget.Toast.LENGTH_LONG;
import static com.ahitugrad.notifman.CustomApplication.ISAVAILABLE;
import static com.ahitugrad.notifman.CustomApplication.NOTIFICATION_PREF_NAME;
import static com.ahitugrad.notifman.CustomApplication.TRACK;
import static com.ahitugrad.notifman.CustomApplication.getAndUpdateLatestId;
import static com.ahitugrad.notifman.CustomApplication.getNotId;

public class MainActivity extends AppCompatActivity {

    SharedPreferences mPrefs;
    private TextView tvWelcome;
    private RecyclerView rvNotifications;
    private RecyclerView.Adapter mAdapter;
    private Switch switch1;
    public static ArrayList<Notification> notifications = new ArrayList();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    rvNotifications.setVisibility(View.GONE);
                    tvWelcome.setVisibility(View.VISIBLE);
                    switch1.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_dashboard:
                    rvNotifications.setVisibility(View.GONE);
                    tvWelcome.setVisibility(View.GONE);
                    switch1.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_notifications:
                    rvNotifications.setVisibility(View.VISIBLE);
                    tvWelcome.setVisibility(View.GONE);
                    switch1.setVisibility(View.GONE);
                    return true;
            }
            return false;
        }

    };

    private Context context;
    private Intent service;
    private Button bRelease;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPrefs = getApplicationContext().getSharedPreferences(NOTIFICATION_PREF_NAME,MODE_PRIVATE);
        ImageView ivtest = new ImageView(this);
        ivtest.setImageDrawable(getResources().getDrawable(R.drawable.logo));




        //notifications.add(new Notification("Test","Test", ivtest,new Date()));

        Gson gson = new Gson();

        for(int i =0 ; i < getNotId(); i++){
            Log.i("Notification", " alan For'un içine girdim");
            String json = mPrefs.getString("Notification" + i, "");
            Notification not = gson.fromJson(json, Notification.class);
            notifications.add(not);
        }


        rvNotifications = (RecyclerView) findViewById(R.id.rvNotifications);
        tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        switch1 = (Switch) findViewById(R.id.switch1);

        if(TRACK){
            switch1.setChecked(true);
            context = getApplicationContext();
            service = new Intent(context, BackgroundService.class);
            context.startService(service);
        }else {
            switch1.setChecked(false);
        }

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                TRACK = isChecked;
                SharedPreferences mPrefs = getSharedPreferences(NOTIFICATION_PREF_NAME,MODE_PRIVATE);
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean("track", TRACK);
                editor.apply();

                if(!TRACK){
                    stopService(new Intent(MainActivity.this, BackgroundService.class));
                    stopService(new Intent(MainActivity.this, NotificationService.class));
                }else {
                    startService(new Intent(MainActivity.this, BackgroundService.class));
                    startService(new Intent(MainActivity.this, NotificationService.class));
                }

            }
        });
        bRelease = (Button) findViewById(R.id.bRelease);

        bRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                releaseNotifications();

            }
        });


        rvNotifications.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvNotifications.setLayoutManager(mLayoutManager);
        mAdapter = new NotificationsAdapter(notifications, getApplicationContext(), new NotificationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Notification notification) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(notification.getPackagename());
                startActivity(launchIntent);
                notifications.remove(notification);
                mAdapter.notifyDataSetChanged();
            }
        });
        rvNotifications.setAdapter(mAdapter);
        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));


    }

    private void releaseNotifications() {
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

        notifications.clear();
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if(intent == null){
            intent = new Intent();
        }
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        if(intent == null){
            intent = new Intent();
        }
        super.startActivity(intent, options);
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("OnReceive","Called in Main Activity");

            if(!ISAVAILABLE && TRACK){
                Log.v("ISAVAILABLE False", "onReceive e girdim");
                String pack = intent.getStringExtra("package");
                String title = intent.getStringExtra("title");
                String text = intent.getStringExtra("text");
                if(pack.equals("com.ahitugrad.notifman")) return;
                Notification newNot = new Notification(getAndUpdateLatestId(), title, pack, text, new Date());
                notifications.add(newNot);
                mAdapter.notifyDataSetChanged();
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(newNot); // myObject - instance of MyObject
                prefsEditor.putString("Notification" + newNot.getId(), json);
                prefsEditor.apply();
            }else {
                mAdapter.notifyDataSetChanged();
            }


        }
    };

}
