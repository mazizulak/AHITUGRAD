package com.ahitugrad.notifman;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private RecyclerView rvNotifications;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList notifications = new ArrayList<Notification>();

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
        ImageView ivtest = new ImageView(this);
        ivtest.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        notifications.add(new Notification("Test","Test", ivtest,new Date()));
        notifications.add(new Notification("Test","Test", ivtest,new Date()));
        notifications.add(new Notification("Test","Test", ivtest,new Date()));
        notifications.add(new Notification("Test","Test", ivtest,new Date()));
        notifications.add(new Notification("Test","Test", ivtest,new Date()));
        notifications.add(new Notification("Test","Test", ivtest,new Date()));
        notifications.add(new Notification("Test","Test", ivtest,new Date()));
        rvNotifications = (RecyclerView) findViewById(R.id.rvNotifications);
        tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        rvNotifications.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        rvNotifications.setLayoutManager(mLayoutManager);
        mAdapter = new NotificationsAdapter(notifications, getApplicationContext(), new NotificationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Notification notification) {
                //// TODO: 01/05/2017
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
            String pack = intent.getStringExtra("package");
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
            Toast.makeText(getApplicationContext(),title, LENGTH_LONG).show();
            byte[] b = intent.getByteArrayExtra("icon");
            Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
            ImageView image = new ImageView(getApplicationContext());
            image.setImageBitmap(bmp);
            notifications.add(new Notification(title,text,image,new Date()));
            mAdapter.notifyDataSetChanged();
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();

        }
    };

}
