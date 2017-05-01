package com.ahitugrad.notifman;

import android.media.Image;
import android.widget.ImageView;

import java.util.Date;

/**
 * Created by huseyin on 01/05/2017.
 */

public class Notification {
    String title;
    String content;
    ImageView image;
    Date time;

    public Notification(String title, ImageView image, Date time) {
        this.title = title;
        this.content = "No content given for this notification";
        this.image = image;
        this.time = time;
    }
}
