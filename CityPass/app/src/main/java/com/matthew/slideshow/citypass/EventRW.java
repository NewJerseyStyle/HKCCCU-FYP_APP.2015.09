package com.matthew.slideshow.citypass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by matthew on 1/2/2016.
 */
public class EventRW {
    public static final String TABLE_NAME = "todolist";
    public static final String ID = "_id";
    public static final String NAME_COL = "name";
    public static final String YEAR_COL = "year";
    public static final String MONTH_COL = "mon";
    public static final String DAY_COL = "day";
    public static final String HOUR1_COL = "hour1";
    public static final String HOUR2_COL = "hour2";
    public static final String MIN1_COL = "min1";
    public static final String MIN2_COL = "min2";
    public static final String LOCATION_COL = "location";
    public static final String DATETIME_COL = "datetime";

    public static final String NOTIFY_COL = "notify";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME_COL + " TEXT, " +
            YEAR_COL + " INTEGER, " +
            MONTH_COL + " INTEGER, " +
            DAY_COL + " INTEGER, " +
            HOUR1_COL + " INTEGER, " +
            HOUR2_COL + " INTEGER, " +
            MIN1_COL + " INTEGER, " +
            MIN2_COL + " INTEGER, " +
            LOCATION_COL + " TEXT, " +
            DATETIME_COL + " TEXT, " +
            NOTIFY_COL + " INTEGER)";

    private SQLiteDatabase db;


    public EventRW(Context context) {
        db = TodoListSQLite.getDatabase(context);
        //db.execSQL(DROP_TABLE);
        //db.execSQL(CREATE_TABLE);
    }


    public void close() {
        //db.close();
    }


    public Event insert(Event event) {
        ContentValues cv = new ContentValues();

        cv.put(NAME_COL, event.getName());
        cv.put(YEAR_COL, event.getYear());
        cv.put(MONTH_COL, event.getMonth());
        cv.put(DAY_COL, event.getDay());
        cv.put(HOUR1_COL, event.getHour1());
        cv.put(HOUR2_COL, event.getHour2());
        cv.put(MIN1_COL, event.getMin1());
        cv.put(MIN2_COL, event.getMin2());
        cv.put(LOCATION_COL, event.getLocation());
        cv.put(DATETIME_COL, event.getDateTime());
        cv.put(NOTIFY_COL, event.getNotify());

        long id = db.insert(TABLE_NAME, null, cv);

        event.setId(id);
        return event;
    }


    public boolean update(Event event) {
        String where = ID + "=" + event.getId();
        ContentValues cv = new ContentValues();

        cv.put(NAME_COL, event.getName());
        cv.put(YEAR_COL, event.getYear());
        cv.put(MONTH_COL, event.getMonth());
        cv.put(DAY_COL, event.getDay());
        cv.put(HOUR1_COL, event.getHour1());
        cv.put(HOUR2_COL, event.getHour2());
        cv.put(MIN1_COL, event.getMin1());
        cv.put(MIN2_COL, event.getMin2());
        cv.put(LOCATION_COL, event.getLocation());
        cv.put(DATETIME_COL, event.getDateTime());
        cv.put(NOTIFY_COL, event.getNotify());

        return db.update(TABLE_NAME, cv, where, null) > 0;
    }


    public boolean delete(long id) {
        String where = ID + "=" + id;

        return db.delete(TABLE_NAME, where, null) > 0;
    }


    public List<Event> getAllEvents() {
        List<Event> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        String where = NAME_COL + " not like '%mid term%' and " +
                NAME_COL + " not like '%mid-term%' and " +
                NAME_COL + " not like '%mid_term%' and " +
                NAME_COL + " not like '%midterm%' and " +
                NAME_COL + " not like '%exam%' and " +
                NAME_COL + " not like '%final%' and " +
                NAME_COL + " not like '%test%' and " +
                NAME_COL + " not like '%quiz%' and " +
                NAME_COL + " not like '%examination%' and " +
                NAME_COL + " not like '%assignment%' and " +
                NAME_COL + " not like '%lab%'";

        Cursor cursor = db.query(TABLE_NAME, null, where, null, null, null, "datetime(" + DATETIME_COL + ")", null);

        while (cursor.moveToNext()) {
            list.add(getRecord(cursor));
        }

        cursor.close();
        if (list.size() > 0)
            return list;
        else
            return null;
    }

    public List<Event> getAllMidTerm() {
        List<Event> list = new ArrayList<>();
        String where = NAME_COL + " like '%mid term%' or " +
                NAME_COL + " like '%mid-term%' or " +
                NAME_COL + " like '%mid_term%' or " +
                NAME_COL + " like '%midterm%' or " +
                NAME_COL + " like '%final%' or " +
                NAME_COL + " like '%test%' or " +
                NAME_COL + " like '%quiz%' or " +
                NAME_COL + " like '%examination%' or " +
                NAME_COL + " like '%exam%'";
        Cursor cursor = db.query(TABLE_NAME, null, where, null, null, null, "datetime(" + DATETIME_COL + ")", null);
        while (cursor.moveToNext()) {
            list.add(getRecord(cursor));
        }
        cursor.close();
        if (list.size() > 0)
            return list;
        else
            return null;
    }


    public List<Event> getAllLab() {
        List<Event> list = new ArrayList();
        String where = NAME_COL + " like '%assignment%' or " +
                NAME_COL + " like '%lab%'";
        Cursor cursor = db.query(TABLE_NAME, null, where, null, null, null, "datetime(" + DATETIME_COL + ")", null);
        while (cursor.moveToNext()) {
            list.add(getRecord(cursor));
        }
        cursor.close();
        if (list.size() > 0)
            return list;
        else
            return null;

    }


    public Event getNotification() {
        Event event;
        String where = NOTIFY_COL + " = 1";
        Cursor cursor = db.query(TABLE_NAME, null, where, null, null, null, "datetime(" + DATETIME_COL + ")", "1");

        if (cursor.moveToFirst()) {
            event = getRecord(cursor);
            cursor.close();
            return event;
        }

        cursor.close();
        return null;

    }


    public List<Event> getAllByDate(Date date) {
        List<Event> events = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String str = sdf.format(date);
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE SUBSTR(" + DATETIME_COL + ", 0, 11) = '" + str + "';";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            events.add(getRecord(cursor));
        }
        cursor.close();
        if (events.size() == 0)
            return null;
        else
            return events;
    }


    public Event get(long id) {
        Event event;
        String where = ID + "=" + id;
        Cursor cursor = db.query(TABLE_NAME, null, where, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            event = getRecord(cursor);
            cursor.close();
            return event;
        }

        cursor.close();
        return null;
    }


    public Event getRecord(Cursor cursor) {
        Event event = new Event();

        event.setId(cursor.getLong(0));
        event.setName(cursor.getString(1));
        event.setYear(cursor.getInt(2));
        event.setMonth(cursor.getInt(3));
        event.setDay(cursor.getInt(4));
        event.setHour1(cursor.getInt(5));
        event.setHour2(cursor.getInt(6));
        event.setMin1(cursor.getInt(7));
        event.setMin2(cursor.getInt(8));
        event.setLocation(cursor.getString(9));

        try {
            int temp = cursor.getInt(11);
            event.setNotify(temp);
            Log.d("cursor.getInt(11)", Integer.toString(temp));
        } catch (Exception e) {
            Log.e("getRecord: ", e.getMessage());
        }


        return event;
    }


}







