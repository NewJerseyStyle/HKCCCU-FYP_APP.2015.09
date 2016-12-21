package com.matthew.slideshow.citypass;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by matthew on 31/1/2016.
 */

public class TodoListSQLite extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "mydata.db";
    public static final int VERSION = 1;
    private static SQLiteDatabase database;


    public TodoListSQLite(Context context, String name, CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }


    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new TodoListSQLite(context, DATABASE_NAME,
                    null, VERSION).getWritableDatabase();
        }
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL(EventRW.CREATE_TABLE);
            db.execSQL(HolidayRW.CREATE_TABLE1);
            db.execSQL(HolidayRW.CREATE_TABLE2);
            db.execSQL(CourseRW.CREATE_COURSE_TABLE);
            db.execSQL(CourseRW.CREATE_LESSON_TABLE);

            db.execSQL(SettingsRW.CREATE_TABLE);
            db.execSQL(SettingsRW.CREATE_TABLE1);

            db.execSQL(LoginRW.CREATE_TABLE);

            db.execSQL(StudentDetailRW.CREATE_TABLE);

            db.execSQL(AnnouncementRW.CREATE_TABLE);
            db.execSQL(AnnouncementRW.CREATE_TABLE2);
            db.execSQL(FileRW.CREATE_TABLE);
        } catch (Exception e) {
            Log.e("Create error: ", e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + EventRW.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + HolidayRW.TABLE_NAME1);
        db.execSQL("DROP TABLE IF EXISTS " + HolidayRW.TABLE_NAME2);
        db.execSQL("DROP TABLE IF EXISTS " + CourseRW.COURSE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CourseRW.LESSON_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SettingsRW.TABLE_NAME);
        db.execSQL(LoginRW.DROP_TABLE);
        db.execSQL(StudentDetailRW.DROP_TABLE);
        db.execSQL(AnnouncementRW.DROP_TABLE);
        db.execSQL(AnnouncementRW.DROP_TABLE2);
        db.execSQL(FileRW.DROP_TABLE);
        onCreate(db);
    }

}
