package com.ahitugrad.notifman;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by maziz on 23.05.2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "User.db";
    public static final String TABLE_NAME = "records";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CALL = "call";
    public static final String COLUMN_SCREEN = "screen";
    public static final String COLUMN_ACTIVITY = "activity";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table "+TABLE_NAME+" " +
                        "("+COLUMN_ID+" integer, "+COLUMN_CALL+" real,"+COLUMN_SCREEN+" real,"+COLUMN_ACTIVITY+" real)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void init(String testcall, String testscreen, String testactivity) {
        String[] callparts = testcall.split(",");
        String[] screenparts = testscreen.split(",");
        String[] activityparts = testactivity.split(",");
        for(int i = 0; i < callparts.length; i++){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_ID, i+1);
            contentValues.put(COLUMN_CALL, Integer.parseInt(callparts[i]));
            contentValues.put(COLUMN_SCREEN, Integer.parseInt(screenparts[i]));
            contentValues.put(COLUMN_ACTIVITY, Integer.parseInt(activityparts[i]));
            db.insert(TABLE_NAME, null, contentValues);
        }
    }

    public void updateData(int id, double call, double screen, double activity) {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_CALL, call);
            contentValues.put(COLUMN_SCREEN, screen);
            contentValues.put(COLUMN_ACTIVITY, activity);
            db.update(TABLE_NAME,contentValues,"id = ? ",new String[] { Integer.toString(id) });

    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
        return res;
    }
}
