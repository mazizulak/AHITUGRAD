package com.ahitugrad.notifman;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
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
                    return true;
                case R.id.navigation_dashboard:
                    rvNotifications.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_notifications:
                    rvNotifications.setVisibility(View.VISIBLE);
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
        notifications.add(new Notification("Test",ivtest,new Date()));
        notifications.add(new Notification("Test",ivtest,new Date()));
        notifications.add(new Notification("Test",ivtest,new Date()));
        notifications.add(new Notification("Test",ivtest,new Date()));
        notifications.add(new Notification("Test",ivtest,new Date()));
        notifications.add(new Notification("Test",ivtest,new Date()));
        notifications.add(new Notification("Test",ivtest,new Date()));
        notifications.add(new Notification("Test",ivtest,new Date()));
        notifications.add(new Notification("Test",ivtest,new Date()));
        rvNotifications = (RecyclerView) findViewById(R.id.rvNotifications);
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
        rvNotifications.setVisibility(View.GONE);
        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



    }

}
