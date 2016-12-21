package com.matthew.slideshow.citypass;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by yeung on 3/31/2016.
 */
public class LoginRW {
    public static final String TABLE_NAME = "login";
    public static final String LOGIN_NAME = "login_name";
    public static final String LOGIN_PASSWORD = "login_password";
    private Context context;

    public static final String CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME + " (" +
            LOGIN_NAME + " TEXT, " +
            LOGIN_PASSWORD + " TEXT)";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private SQLiteDatabase db;

    public LoginRW(Context context) {
        this.context = context;
        db = TodoListSQLite.getDatabase(context);
    }


    public void close() {
        db.close();
    }

    public void insert(String login_name, String login_password) {
        if (getCount() == 0) {
            ContentValues cv = new ContentValues();
            cv.put(LOGIN_NAME, login_name);
            cv.put(LOGIN_PASSWORD, login_password);
            db.insert(TABLE_NAME, null, cv);
        }
    }

    public String getLoginName() {
        String login_name = "";
        String[] selection = {LOGIN_NAME};
        Cursor c = db.query(TABLE_NAME, selection, null, null, null, null, null, null);
        if (c.moveToNext())
            login_name = c.getString(0);
        c.close();
        return login_name;
    }

    public String getLoginPassword() {
        String login_password = "";
        String[] selection = {LOGIN_PASSWORD};
        Cursor c = db.query(TABLE_NAME, selection, null, null, null, null, null, null);
        if (c.moveToNext())
            login_password = c.getString(0);
        c.close();
        return login_password;
    }


    public int getCount() {
        int result = 0;
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME;
        Cursor c = db.rawQuery(query, null);
        if (c.moveToNext())
            result = c.getInt(0);
        c.close();
        return result;

    }

    public void logout() {
        cancelEventAlarm();
        cancelTimeTableAlarm();
        db.execSQL("DELETE FROM " + EventRW.TABLE_NAME);
        db.execSQL("DELETE FROM " + HolidayRW.TABLE_NAME1);
        db.execSQL("DELETE FROM " + HolidayRW.TABLE_NAME2);
        db.execSQL("DELETE FROM " + CourseRW.COURSE_TABLE_NAME);
        db.execSQL("DELETE FROM " + CourseRW.LESSON_TABLE_NAME);
        db.execSQL("DELETE FROM " + SettingsRW.TABLE_NAME);
        db.execSQL("DELETE FROM " + LoginRW.TABLE_NAME);
        db.execSQL("DELETE FROM " + StudentDetailRW.TABLE_NAME);
        db.execSQL("DELETE FROM " + AnnouncementRW.TABLE_NAME);
        db.execSQL("DELETE FROM " + AnnouncementRW.TABLE_NAME2);
        db.execSQL("DELETE FROM " + FileRW.TABLE_NAME);
    }


    public void cancelEventAlarm() {
        EventRW eventRW = new EventRW(context);
        Event event = eventRW.getNotification();
        if (event != null) {
            int notificationId = 0;
            AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent aIntent = new Intent(context, AlarmManagerReceiver.class);
            aIntent.putExtra(AlarmManagerReceiver.NOTIFICATION, notificationId);
            //PendingIntent.getBroadcast(Context context, int requestCode, Intent intent, int flags)
            PendingIntent pIntent = PendingIntent.getBroadcast(context, notificationId, aIntent, 0);
            try {
                aManager.cancel(pIntent);
            } catch (Exception e) {
                Log.d("cancel alarm event", e.getMessage());
            }
        }

    }


    public void cancelTimeTableAlarm() {
        int NOTIFICATION_ID = 1;
        AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent aIntent = new Intent(context, AlarmManagerReceiver.class);
        aIntent.putExtra(AlarmManagerReceiver.NOTIFICATION, NOTIFICATION_ID);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, NOTIFICATION_ID, aIntent, 0);
        try {
            aManager.cancel(pIntent);
        } catch (Exception e) {
            Log.d("cancel alarm timetble", e.getMessage());
        }
    }
}
