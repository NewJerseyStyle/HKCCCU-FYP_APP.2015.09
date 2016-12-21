package com.matthew.slideshow.citypass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by matthew on 18/3/2016.
 */
public class HolidayRW {
    public static final String TABLE_NAME1 = "holiday";
    public static final String COLUMN_HOLIDAY_EVENT_NAME = "holiday_event_name";
    public static final String COLUMN_DATE_DETAIL = "date_detail";
    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_TYPE = "type";

    public static final String TABLE_NAME2 = "calendar_holiday";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_EVENT_TYPE = "event_type";

    public static final String CREATE_TABLE1 = "CREATE TABLE " + TABLE_NAME1
            + "("
            + COLUMN_HOLIDAY_EVENT_NAME + " TEXT , "
            + COLUMN_DATE_DETAIL + "  TEXT NOT NULL ,"
            + COLUMN_MONTH + " INTEGER NOT NULL ,"
            + COLUMN_YEAR + " INTEGER NOT NULL,"
            + COLUMN_TYPE + " TEXT NOT NULL "
            + ");";


    public static final String DROP_TABLE1 = "DROP TABLE IF EXISTS " + TABLE_NAME1;
    public static final String DROP_TABLE2 = "DROP TABLE IF EXISTS " + TABLE_NAME2;
    //Second table create statement

    //Third table create statement
    public static final String CREATE_TABLE2 = "CREATE TABLE " + TABLE_NAME2
            + " ("
            + COLUMN_DATE + " TEXT, "
            + COLUMN_EVENT_TYPE + " TEXT);";

    public SQLiteDatabase db;

    public HolidayRW(Context context) {
        db = TodoListSQLite.getDatabase(context);
        db.execSQL(DROP_TABLE1);
        db.execSQL(DROP_TABLE2);
        db.execSQL(CREATE_TABLE1);
        db.execSQL(CREATE_TABLE2);
    }


    public void close() {
        db.close();
    }


    public void insertHoliday(Holiday h) {
        String where = COLUMN_HOLIDAY_EVENT_NAME + " = \"" + h.getName() + "\"";
        List<Holiday> temp_holiday_list = new ArrayList<>();

        Cursor c = db.query(TABLE_NAME1, null, where, null, null, null, null);
        while (c.moveToNext()) {
            temp_holiday_list.add(getRecord(c));
        }

        if (!(temp_holiday_list.size() > 0)) {
            ContentValues cv = new ContentValues();

            cv.put(COLUMN_HOLIDAY_EVENT_NAME, h.getName());
            cv.put(COLUMN_DATE_DETAIL, h.getDateDetail());
            cv.put(COLUMN_MONTH, h.getMonth());
            cv.put(COLUMN_YEAR, h.getYear());
            cv.put(COLUMN_TYPE, h.getType());


            db.insert(TABLE_NAME1, null, cv);
        }

    }


    public void insertCalendarHoliday(String date, String type) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_EVENT_TYPE, type);

        db.insert(TABLE_NAME2, null, cv);
    }


    public String[] getAllDateH() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String query = "SELECT " + COLUMN_DATE + " FROM " + TABLE_NAME2 + " WHERE + " + COLUMN_EVENT_TYPE + " ='H' AND DATE(" + COLUMN_DATE + ") > DATE('" + Integer.toString(year - 1) + "-12-31');";
        Cursor c = db.rawQuery(query, null);
        int numOfC = c.getCount();
        String[] date = new String[numOfC];
        int a = 0;
        while (c.moveToNext()) {
            date[a] = c.getString(0);
            a++;
        }
        c.close();
        return date;
    }


    public String[] getAllDateE() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String query = "SELECT " + COLUMN_DATE + " FROM " + TABLE_NAME2 + " WHERE + " + COLUMN_EVENT_TYPE + " ='E'  AND DATE(" + COLUMN_DATE + ") > DATE('" + Integer.toString(year - 1) + "-12-31');";
        Cursor c = db.rawQuery(query, null);
        int numOfC = c.getCount();

        String[] date = new String[numOfC];
        int a = 0;
        while (c.moveToNext()) {
            date[a] = c.getString(0);
            a++;
        }
        c.close();
        return date;
    }


    public List<Holiday> getHoliday(String month, int year) {
        List<Holiday> list = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        int currYear = calendar.get(Calendar.YEAR);

        if (year >= currYear) {
            String where = " month = "
                    + month
                    + " AND year = "
                    + year
                    + ";";
            Cursor c = db.query(TABLE_NAME1, null, where, null, null, null, null, null);

            while (c.moveToNext()) {
                list.add(getRecord(c));
            }


            c.close();
        }


        return list;
    }


    public Holiday getRecord(Cursor cursor) {
        Holiday h = new Holiday();
        h.setName(cursor.getString(0));
        h.setDateDetail(cursor.getString(1));
        h.setMonth(cursor.getInt(2));
        h.setYear(cursor.getInt(3));
        h.setType(cursor.getString(4));

        return h;
    }


    public int getCountAllHoliday() {
        String count = "SELECT count(*) FROM  holiday";
        Cursor mCursor = db.rawQuery(count, null);
        mCursor.moveToFirst();
        int icount = mCursor.getInt(0);

        return icount;
    }

    public void deleteHoliday() {
        db.execSQL("DELETE FROM " + TABLE_NAME1);
    }

    public void deleteCalendarHoliday() {
        db.execSQL("DELETE FROM " + TABLE_NAME2);
    }
}
