package com.matthew.slideshow.citypass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by yeung on 3/28/2016.
 */
public class SettingsRW {


    public static final String TABLE_NAME = "settings";
    public static final String ID = "id";
    public static final String PRINTER = "printer";
    public static final String COPIES = "copies";
    public static final String PRINT_TYPE = "print_type";
    public static final String COMBINATION = "combination";
    public static final String ORIENTATION = "orientation";
    public static final String BIND_POSITION = "binding_position";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PRINTER + " INTEGER, " +
            COPIES + " INTEGER, " +
            PRINT_TYPE + " INTEGER, " +
            COMBINATION + " INTEGER, " +
            ORIENTATION + " INTEGER, " +
            BIND_POSITION + " INTEGER);";
    public static final String TABLE_NAME1 = "notification";
    public static final String ALERM = "alerm";
    public static final String CREATE_TABLE1 = "CREATE TABLE " + TABLE_NAME1 + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ALERM + " INTEGER);";

    public void insertAlerm(int id, int alerm)
    //0 = Event notification, 1 = Timetable notification
    //0 = false, 1 = true
    {
        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(ALERM, alerm);
        db.insert(TABLE_NAME1, null, cv);
    }

    public boolean getAlerm(int id) {
        //0 = Event, 1 = Timetable
        String where = ID + " = " + id;
        boolean alerm = false;
        Cursor c = db.query(TABLE_NAME1, null, where, null, null, null, null, null);
        if (c.moveToNext()) {
            int temp = c.getInt(1);
            if (temp == 0)
                alerm = false;
            else
                alerm = true;
        }
        c.close();
        return alerm;
    }

    public void setAlerm(int id, boolean alerm) {
        String where = ID + " = " + id;
        int tempAlerm = 0;
        if (alerm) {
            tempAlerm = 1;
        } else {
            tempAlerm = 0;
        }
        ContentValues cv = new ContentValues();
        cv.put(ALERM, tempAlerm);
        db.update(TABLE_NAME1, cv, where, null);
        //return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    public int getCountOfAlerm() {
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME1;
        int result = 0;
        Cursor c = db.rawQuery(query, null);

        if (c.moveToNext())
            result = c.getInt(0);

        c.close();
        return result;
    }

    private SQLiteDatabase db;

    public SettingsRW(Context context) {
        db = TodoListSQLite.getDatabase(context);
        //db.execSQL(CREATE_TABLE);
        //db.execSQL(CREATE_TABLE1);
    }

    public void close() {
        db.close();
    }

    public int getCount() {
        int result = 0;
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        if (c.moveToNext()) {
            result = c.getInt(0);
        }
        c.close();
        return result;
    }
}
