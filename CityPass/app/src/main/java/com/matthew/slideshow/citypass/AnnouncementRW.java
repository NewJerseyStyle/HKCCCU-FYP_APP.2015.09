package com.matthew.slideshow.citypass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yeung on 4/23/2016.
 */
public class AnnouncementRW {
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String LASTPOST = "lastpost";

    public static final String COURSE_CODE = "course_code";

    public static final String COURSE_NAME = "course_name";


    public static final String TABLE_NAME = "announcement";
    public static final String TABLE_NAME2 = "announcement_c";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COURSE_CODE + " TEXT, " +
            TITLE + " TEXT, " +
            CONTENT + " TEXT, " +
            LASTPOST + " TEXT)";

    public static final String CREATE_TABLE2 = "CREATE TABLE " + TABLE_NAME2 + " (" +
            COURSE_CODE + " TEXT, " +
            COURSE_NAME + " TEXT)";


    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    public static final String DROP_TABLE2 = "DROP TABLE IF EXISTS " + TABLE_NAME2;

    private SQLiteDatabase db;

    public AnnouncementRW(Context context) {
        db = TodoListSQLite.getDatabase(context);
    }


    public void close() {
        //db.close();
    }

    public Announcement insertAnnouncement(Announcement a) {
        ContentValues cv = new ContentValues();

        cv.put(COURSE_CODE, a.getCourse_code());
        cv.put(TITLE, a.getTitle());
        cv.put(CONTENT, a.getContent());
        cv.put(LASTPOST, a.getLastPost());

        db.insert(TABLE_NAME, null, cv);

        return a;
    }


    public AnnouncementCourse insertCourseName(AnnouncementCourse a) {
        ContentValues cv = new ContentValues();

        cv.put(COURSE_CODE, a.getCourse_code());
        cv.put(COURSE_NAME, a.getCourse_name());

        db.insert(TABLE_NAME2, null, cv);

        return a;
    }

    public List<Announcement> getAllAnnouncement(String course_code) {
        List<Announcement> list = new ArrayList<>();
        String where = COURSE_CODE + " = '" + course_code + "'";
        Cursor cursor = db.query(TABLE_NAME, null, where, null, null, null, null, null);

        while (cursor.moveToNext()) {
            list.add(getAnnouncement(cursor));
        }
        cursor.close();

        if (list.size() == 0)
            return null;
        else
            return list;
    }


    public List<AnnouncementCourse> getAllCourse() {
        List<AnnouncementCourse> list = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME2, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            list.add(getCourse(cursor));
        }
        cursor.close();

        if (list.size() == 0)
            return null;
        else
            return list;
    }


    public Announcement getAnnouncement(Cursor cursor) {
        String mCourse_code = cursor.getString(0);
        String mTitle = cursor.getString(1);
        String mContent = cursor.getString(2);
        String mLastpost = cursor.getString(3);

        Announcement a = new Announcement(mTitle, mContent, mLastpost);
        a.setCourse_code(mCourse_code);

        return a;
    }


    public AnnouncementCourse getCourse(Cursor cursor) {
        String course_code = cursor.getString(0);
        String course_name = cursor.getString(1);

        AnnouncementCourse a = new AnnouncementCourse(course_code, course_name);

        return a;
    }

    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME2, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        cursor.close();
        return result;
    }


    public void deleteAll() {
        db.execSQL("DELETE FROM " + AnnouncementRW.TABLE_NAME);
        db.execSQL("DELETE FROM " + AnnouncementRW.TABLE_NAME2);
    }

}
