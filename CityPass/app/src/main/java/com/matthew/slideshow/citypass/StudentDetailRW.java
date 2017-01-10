package com.matthew.slideshow.citypass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by matthew on 22/4/2016.
 */
public class StudentDetailRW {
    public static final String NAME = "name";
    public static final String SID = "sid";
    public static final String EID = "eid";
    public static final String EMAIL = "email";
    public static final String DEPARTMENT = "department";
    public static final String MAJOR = "major";
    public static final String PROGRAMME = "programme";
    public static final String CAMPUS = "campus";
    public static final String AS = "ac_stand";

    public static final String TABLE_NAME = "student_detail";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            NAME + " TEXT, " +
            SID + " TEXT, " +
            EID + " TEXT, " +
            EMAIL + " TEXT, " +
            DEPARTMENT + " TEXT, " +
            MAJOR + " TEXT, " +
            PROGRAMME + " TEXT, " +
            CAMPUS + " TEXT, " +
            AS + " TEXT)";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    private SQLiteDatabase db;

    public StudentDetailRW(Context context) {
        db = TodoListSQLite.getDatabase(context);
        //db.execSQL(CREATE_TABLE);
    }

    public void close() {
        //db.close();
    }

    public StudentDetail insert(StudentDetail s) {
        ContentValues cv = new ContentValues();

        cv.put(NAME, s.getStudentName());
        cv.put(SID, s.getStudentId());
        cv.put(EID, s.getEid());
        cv.put(EMAIL, s.getEmail());
        cv.put(DEPARTMENT, s.getDepartment());
        cv.put(MAJOR, s.getMajor());
        cv.put(PROGRAMME, s.getProgramme());
        cv.put(CAMPUS, s.getCampus());
        cv.put(AS, s.getAS());

        db.insert(TABLE_NAME, null, cv);

        return s;
    }


    public StudentDetail getRecord(Cursor cursor) {
        String name = cursor.getString(0);
        String sid = cursor.getString(1);
        String eid = cursor.getString(2);
        String email = cursor.getString(3);
        String department = cursor.getString(4);
        String major = cursor.getString(5);
        String programme = cursor.getString(6);
        String campus = cursor.getString(7);
        String as = cursor.getString(8);

        StudentDetail s = new StudentDetail(name, sid, eid, email, department, major, programme, campus, as);

        return s;
    }


    public StudentDetail getStudentDetail() {
        StudentDetail s;
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, "1");
        if (cursor.moveToNext()) {
            s = getRecord(cursor);
            cursor.close();
            return s;
        }
        cursor.close();
        return null;
    }

    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext())
            result = cursor.getInt(0);

        cursor.close();
        return result;
    }


}
