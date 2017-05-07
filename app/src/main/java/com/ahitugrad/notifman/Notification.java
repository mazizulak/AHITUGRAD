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
    String packagename;
    Date time;
    private int id;

    public Notification(int id, String title, String packagename , String text, Date time) {
        this.id = id;
        this.title = title;
        this.content = text;
        this.time = time;
        this.packagename = packagename;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setId(int id) {
        this.id = id;
    }
}
