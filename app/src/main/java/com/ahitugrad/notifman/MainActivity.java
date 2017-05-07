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
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;

import static android.widget.Toast.LENGTH_LONG;
import static com.ahitugrad.notifman.CustomApplication.NOTIFICATION_PREF_NAME;
import static com.ahitugrad.notifman.CustomApplication.getAndUpdateLatestId;
import static com.ahitugrad.notifman.CustomApplication.getNotId;

public class MainActivity extends AppCompatActivity {

    SharedPreferences mPrefs;
    private TextView tvWelcome;
    private Button bRelease;
    private RecyclerView rvNotifications;
    private RecyclerView.Adapter mAdapter;
    private int isAvailable = 0;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Notification> notifications = new ArrayList();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    rvNotifications.setVisibility(View.GONE);
                    tvWelcome.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_dashboard:
                    rvNotifications.setVisibility(View.GONE);
                    tvWelcome.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_notifications:
                    rvNotifications.setVisibility(View.VISIBLE);
                    tvWelcome.setVisibility(View.GONE);
                    return true;
            }
            return false;
        }

    };

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
            if(json != null){
                Notification not = gson.fromJson(json, Notification.class);
                notifications.add(not);
            }
        }


        rvNotifications = (RecyclerView) findViewById(R.id.rvNotifications);
        tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        bRelease = (Button) findViewById(R.id.bRelease);

        bRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                isAvailable = 1;
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

                    try {
                        Bitmap largeIcon = ((BitmapDrawable) appIcon).getBitmap();
                        mBuilder.setLargeIcon(largeIcon);
                    }catch (Exception e){
                        Log.e("Yakaladım:", e.toString());
                    }

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
                                        int mNotificationId = 001;
                    // Gets an instance of the NotificationManager service
                                        NotificationManager mNotifyMgr =
                                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // Builds the notification and issues it.
                                        mNotifyMgr.notify(mNotificationId, mBuilder.build());

                }

                notifications.clear();
                mAdapter.notifyDataSetChanged();


            }
        });


        rvNotifications.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
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

    private TableLayout tab;
    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(isAvailable == 0){
                Log.i("onReceive e girdim","");
                String pack = intent.getStringExtra("package");
                String title = intent.getStringExtra("title");
                String text = intent.getStringExtra("text");
                Toast.makeText(getApplicationContext(), title, LENGTH_LONG).show();

                Notification newNot = new Notification(getAndUpdateLatestId(), title, pack, text, new Date());
                notifications.add(newNot);
                mAdapter.notifyDataSetChanged();
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                Gson gson = new Gson();
                Log.i("GSON", " işlemine başladım");
                String json = gson.toJson(newNot); // myObject - instance of MyObject
                Log.i("toJsonBitt","ok");
                prefsEditor.putString("Notification" + newNot.getId(), json);
                prefsEditor.apply();
            }


        }
    };

}
