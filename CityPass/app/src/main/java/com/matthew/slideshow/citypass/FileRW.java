package com.matthew.slideshow.citypass;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by yeung on 4/24/2016.
 */
public class FileRW {
    public static final String TABLE_NAME = "file";

    public static final String COURSE_CODE = "file_course_code";
    public static final String COURSE_NAME = "file_course_name";


    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " +
            COURSE_CODE + " TEXT, " +
            COURSE_NAME + " TEXT)";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private SQLiteDatabase db;


}
