package com.matthew.slideshow.citypass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by yeung on 3/3/2016.
 */
public class CourseRW {
    public static final String COURSE_TABLE_NAME = "course";
    public static final String COURSE_ID = "course_id";
    public static final String COURSE_CODE = "course_code";
    public static final String COURSE_NAME = "course_name";

    public static final String LESSON_TABLE_NAME = "lesson";
    public static final String LESSON_ID = "lesson_id";
    public static final String LESSON_SECTION = "lesson_section";
    public static final String LESSON_LOCATION = "lesson_location";
    public static final String LESSON_TIME = "lesson_time";
    public static final String LESSON_DAYS = "lesson_days";
    public static final String LESSON_DURATION = "lesson_duration";
    public static final String LESSON_TYPE = "lesson_type";
    public static final String LESSON_ASSOCIATED_TERM = "lessong_associated_term";
    public static final String LESSON_CRN = "lesson_crn";
    public static final String LESSON_INSTRUCTOR = "lesson_instructor";
    public static final String LESSON_GRADE_MODE = "lesson_grade_mode";
    public static final String LESSON_CREDITS = "lesson_credits";
    public static final String LESSON_LEVEL = "lesson_level";
    public static final String LESSON_CAMPUS = "lesson_campus";
    public static final String LESSON_DATE_RANGE = "lesson_date_range";

    public static final String CREATE_COURSE_TABLE = "CREATE TABLE " + COURSE_TABLE_NAME + " ("
            + COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COURSE_CODE + " TEXT,"
            + COURSE_NAME + " TEXT)";

    public static final String CREATE_LESSON_TABLE = "CREATE TABLE " + LESSON_TABLE_NAME + " ("
          /*0*/ + LESSON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
          /*1*/ + COURSE_ID + " INTEGER,"
          /*2*/ + LESSON_SECTION + " TEXT,"
          /*3*/ + LESSON_LOCATION + " TEXT,"
          /*4*/ + LESSON_TIME + " TEXT,"
          /*5*/ + LESSON_DAYS + " TEXT,"
          /*6*/ + LESSON_DURATION + " INTEGER,"
          /*7*/ + LESSON_TYPE + " TEXT,"
          /*8*/ + LESSON_ASSOCIATED_TERM + " TEXT,"
          /*9*/ + LESSON_CRN + " TEXT,"
          /*10*/ + LESSON_INSTRUCTOR + " TEXT,"
          /*11*/ + LESSON_GRADE_MODE + " TEXT,"
          /*12*/ + LESSON_CREDITS + " TEXT,"
          /*13*/ + LESSON_LEVEL + " TEXT,"
          /*14*/ + LESSON_CAMPUS + " TEXT,"
          /*15*/ + LESSON_DATE_RANGE + " TEXT)";


    private SQLiteDatabase db;


    public CourseRW(Context context) {
        db = TodoListSQLite.getDatabase(context);
        //db.execSQL(DROP_COURSE_TABLE);
        //db.execSQL(DROP_LESSON_TABLE);
        //db.execSQL(CREATE_COURSE_TABLE);
        //db.execSQL(CREATE_LESSON_TABLE);
    }

    public void close() {
        db.close();
    }

    public Course insertTimetable(Course course) {

        ContentValues cv1 = new ContentValues();
        cv1.put(COURSE_CODE, course.getCourse_code());
        //cv1.put(COURSE_NAME, course.getCourse_name());

        long course_id = db.insert(COURSE_TABLE_NAME, null, cv1);
        course.setCourse_id(course_id);

        for (int a = 0; a < course.lesson.size(); a++) {
            ContentValues cv = new ContentValues();

            cv.put(COURSE_ID, course_id);
            cv.put(LESSON_CRN, course.lesson.get(a).getCrn());
            cv.put(LESSON_SECTION, course.lesson.get(a).getSection());
            cv.put(LESSON_LOCATION, course.lesson.get(a).getLocation());
            cv.put(LESSON_TIME, course.lesson.get(a).getTime());
            cv.put(LESSON_DAYS, Character.toString(course.lesson.get(a).getDays()));
            cv.put(LESSON_DURATION, course.lesson.get(a).getDuration());

            long lesson_id = db.insert(LESSON_TABLE_NAME, null, cv);

            course.lesson.get(a).setLesson_id(lesson_id);
        }

        return course;
    }

    public void insertDetails(String course_name, String type, String associated_term, String assigned_instructor, String grade_mode
            , String credits, String level, String campus, String date_range) {
        ContentValues cv1 = new ContentValues();
        cv1.put(COURSE_NAME, course_name);

        String[] arr1 = course_name.split(" ");
        String course_code = arr1[arr1.length - 2] + arr1[arr1.length - 1];

        String where1 = COURSE_CODE + " = '" + course_code + "'";
        db.update(COURSE_TABLE_NAME, cv1, where1, null);

        String[] col = {COURSE_ID};
        Cursor c = db.query(COURSE_TABLE_NAME, col, where1, null, null, null, null, "1");

        long temp_course_id = 0;
        if (c.moveToNext())
            temp_course_id = c.getLong(0);

        c.close();


        ContentValues cv2 = new ContentValues();
        cv2.put(LESSON_TYPE, type);
        cv2.put(LESSON_ASSOCIATED_TERM, associated_term);
        cv2.put(LESSON_INSTRUCTOR, assigned_instructor);
        cv2.put(LESSON_GRADE_MODE, grade_mode);
        cv2.put(LESSON_CREDITS, credits);
        cv2.put(LESSON_LEVEL, level);
        cv2.put(LESSON_CAMPUS, campus);
        cv2.put(LESSON_DATE_RANGE, date_range);

        String where2 = COURSE_ID + " = " + Long.toString(temp_course_id);

        db.update(LESSON_TABLE_NAME, cv2, where2, null);

    }


    public Course insert(Course course) {

        ContentValues cv1 = new ContentValues();
        cv1.put(COURSE_CODE, course.getCourse_code());
        cv1.put(COURSE_NAME, course.getCourse_name());

        long course_id = db.insert(COURSE_TABLE_NAME, null, cv1);
        course.setCourse_id(course_id);

        for (int a = 0; a < course.lesson.size(); a++) {
            ContentValues cv = new ContentValues();
            cv.put(COURSE_ID, course_id);
            cv.put(LESSON_SECTION, course.lesson.get(a).getSection());
            cv.put(LESSON_LOCATION, course.lesson.get(a).getLocation());
            cv.put(LESSON_TIME, course.lesson.get(a).getTime());
            cv.put(LESSON_DAYS, Character.toString(course.lesson.get(a).getDays()));
            cv.put(LESSON_DURATION, course.lesson.get(a).getDuration());
            cv.put(LESSON_TYPE, course.lesson.get(a).getType());
            cv.put(LESSON_ASSOCIATED_TERM, course.lesson.get(a).getAssociated_term());
            cv.put(LESSON_CRN, course.lesson.get(a).getCrn());
            cv.put(LESSON_INSTRUCTOR, course.lesson.get(a).getAssigned_instructor());
            cv.put(LESSON_GRADE_MODE, course.lesson.get(a).getGrade_mode());
            cv.put(LESSON_CREDITS, course.lesson.get(a).getCredits());
            cv.put(LESSON_LEVEL, course.lesson.get(a).getLevel());
            cv.put(LESSON_CAMPUS, course.lesson.get(a).getCampus());
            cv.put(LESSON_DATE_RANGE, course.lesson.get(a).getDate_range());

            long lesson_id = db.insert(LESSON_TABLE_NAME, null, cv);

            course.lesson.get(a).setLesson_id(lesson_id);
        }

        return course;
    }


    public Course getCourseRecord(Cursor cursor) {
        long course_id = cursor.getLong(0);
        String course_code = cursor.getString(1);
        String course_name = cursor.getString(2);
        Course course = new Course(course_code, course_name);
        course.setCourse_id(course_id);
        return course;
    }

    public Course.Lesson getLessonRecords(Cursor cursor) {
        long lesson_id = cursor.getLong(0);
        long course_id = cursor.getLong(1);
        String section = cursor.getString(2);
        String location = cursor.getString(3);
        String time = cursor.getString(4);
        char days = cursor.getString(5).charAt(0); //Mon, Thue, Wed, Thus, Fri...
        int duration = cursor.getInt(6);

        String type = cursor.getString(7);
        String associated_term = cursor.getString(8);
        String crn = cursor.getString(9);
        String assigned_instructor = cursor.getString(10);
        String grade_mode = cursor.getString(11);
        String credits = cursor.getString(12);
        String level = cursor.getString(13);
        String campus = cursor.getString(14);
        String date_range = cursor.getString(15);

        Course.Lesson lesson = new Course.Lesson(section, location, time,
                days, duration, type, associated_term, crn, assigned_instructor
                , grade_mode, credits, level, campus, date_range);

        lesson.setLesson_id(lesson_id);
        lesson.setLesson_course_id(course_id);

        return lesson;
    }

    public int getCourseCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + COURSE_TABLE_NAME, null);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }

    public List<Course> getAll() {
        List<Course> list = new ArrayList<>();
        Cursor c1 = db.query(COURSE_TABLE_NAME, null, null, null, null, null, null);

        while (c1.moveToNext()) {
            Course course = getCourseRecord(c1);
            String where = COURSE_ID + " = " + course.getCourse_id();
            Cursor c2 = db.query(LESSON_TABLE_NAME, null, where, null, null, null, null, null);
            while (c2.moveToNext()) {
                course.lesson.add(getLessonRecords(c2));
            }
            c2.close();
            list.add(course);
        }

        c1.close();
        return list;
    }


    public List<Course.Lesson> getDaysLesson(int a) {
        /*
        * if a == 0, indicates today lesson
        * if a == 1, indicates tmr lesson
        * */
        List<Course.Lesson> list = new ArrayList<>();
        Calendar c = Calendar.getInstance();

        Date date = new Date();
        c.setTime(date);

        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        char days = 'S';
        if (a >= 0 && a <= 1) {
            switch ((dayOfWeek + a) % 7) {
                case 1: //Sunday
                    days = '0';
                    break;
                case 2: //Monday
                    days = '1';
                    break;
                case 3: //Tuesday
                    days = '2';
                    break;
                case 4: //Wednesday
                    days = '3';
                    break;
                case 5: //Thursday
                    days = '4';
                    break;
                case 6: //Friday
                    days = '5';
                    break;
                case 0: //Saturday
                    days = '6';
                    break;
                default:
                    days = '0';
            }

            String where = LESSON_DAYS + " = '" + days + "';";
            //String order_by = "time(substr(" + LESSON_TIME + ", 0, 6))";
            //Cursor cursor = db.query(LESSON_TABLE_NAME, null, where, null, null, null, order_by, null);
            String query = "select * from " + LESSON_TABLE_NAME + " where " + LESSON_DAYS + "= '"
                    + days + "' order by time(substr(" + LESSON_TIME + ", 0, 6));";
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                list.add(getLessonRecords(cursor));
            }

            cursor.close();
            if (list.size() > 0)
                return list;
            else
                return null;
        } else {
            return null;
        }
    }

    public String getCourseName(Long course_id) {
        String where = COURSE_ID + " = " + course_id + ";";
        String[] selection = {COURSE_NAME};
        Cursor cursor = db.query(COURSE_TABLE_NAME, selection, where, null, null, null, null);
        String course_name = "";
        if (cursor.moveToNext()) {
            course_name = cursor.getString(0);
        }

        return course_name;
    }


    public AlarmInfo getLastestLessonDateTime() {
        List<Course.Lesson> list = new ArrayList<>();

        Calendar c = Calendar.getInstance();

        /*Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, 2016);
        c.set(Calendar.MONTH, 3-1);
        c.set(Calendar.DAY_OF_MONTH, 30);
        c.set(Calendar.HOUR_OF_DAY, 14);*/

        c.add(Calendar.HOUR_OF_DAY, 1);
        c.add(Calendar.MINUTE, 15);


        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        int hour = c.get(Calendar.HOUR_OF_DAY);
        String str_hour = "";
        char days = 'S';

        if (hour < 10) {
            str_hour = "0" + Integer.toString(hour) + ":00";
        } else {
            str_hour = Integer.toString(hour) + ":00";
        }

        Cursor cursor = null;


        while (list.size() == 0) {
            switch ((dayOfWeek) % 7) {
                case 1: //Sunday
                    days = '0';
                    break;
                case 2: //Monday
                    days = '1';
                    break;
                case 3: //Tuesday
                    days = '2';
                    break;
                case 4: //Wednesday
                    days = '3';
                    break;
                case 5: //Thursday
                    days = '4';
                    break;
                case 6: //Friday
                    days = '5';
                    break;
                case 0: //Saturday
                    days = '6';
                    break;
                default:
                    days = '0';
            }
            //select * from lesson where lesson_days = '4' and time(substr(lesson_time,0,6)) > time('11:00') order by time(substr(lesson_time, 0, 6)) limit 1;
            String query = "select * from " + LESSON_TABLE_NAME + " where " + LESSON_DAYS + " = '"
                    + days + "' and time(substr(" + LESSON_TIME + ", 0, 6)) > time('" + str_hour + "') " + " order by time(substr(" + LESSON_TIME + ", 0, 6)) limit 1;";
            cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                list.add(getLessonRecords(cursor));
            }

            if (list.size() == 0) {
                dayOfWeek++;
                str_hour = "08:00";
                c.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        cursor.close();


        String year = Integer.toString(c.get(Calendar.YEAR));
        int temp = c.get(Calendar.MONTH) + 1;
        String month = null;
        String day = null;

        if (temp < 10)
            month = "0" + Integer.toString(temp);
        else
            month = Integer.toString(temp);

        temp = c.get(Calendar.DAY_OF_MONTH);

        if (temp < 10)
            day = "0" + Integer.toString(temp);
        else
            day = Integer.toString(temp);

        String time = list.get(0).getTime().split("-")[0];
        String datetime = year + "-" + month + "-" + day + " " + time;


        AlarmInfo alarmInfo = new AlarmInfo(datetime, list.get(0));
        return alarmInfo;
    }


    public Course.Lesson getLatestLesson() {
        List<Course.Lesson> list = new ArrayList<>();

        Calendar c = Calendar.getInstance();

        /*Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, 2016);
        c.set(Calendar.MONTH, 3-1);
        c.set(Calendar.DAY_OF_MONTH, 30);
        c.set(Calendar.HOUR_OF_DAY, 14);*/

        //c.add(Calendar.HOUR_OF_DAY, 1);
        //c.add(Calendar.MINUTE, 15);


        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        int hour = c.get(Calendar.HOUR_OF_DAY);
        String str_hour = "";
        char days = 'S';

        if (hour < 10) {
            str_hour = "0" + Integer.toString(hour) + ":00";
        } else {
            str_hour = Integer.toString(hour) + ":00";
        }

        Cursor cursor = null;


        while (list.size() == 0) {
            switch ((dayOfWeek) % 7) {
                case 1: //Sunday
                    days = '0';
                    break;
                case 2: //Monday
                    days = '1';
                    break;
                case 3: //Tuesday
                    days = '2';
                    break;
                case 4: //Wednesday
                    days = '3';
                    break;
                case 5: //Thursday
                    days = '4';
                    break;
                case 6: //Friday
                    days = '5';
                    break;
                case 0: //Saturday
                    days = '6';
                    break;
                default:
                    days = '0';
            }
            //select * from lesson where lesson_days = '4' and time(substr(lesson_time,0,6)) > time('11:00') order by time(substr(lesson_time, 0, 6)) limit 1;
            String query = "select * from " + LESSON_TABLE_NAME + " where " + LESSON_DAYS + " = '"
                    + days + "' and time(substr(" + LESSON_TIME + ", 0, 6)) > time('" + str_hour + "') " + " order by time(substr(" + LESSON_TIME + ", 0, 6)) limit 1;";
            cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                list.add(getLessonRecords(cursor));
            }

            if (list.size() == 0) {
                dayOfWeek++;
                str_hour = "08:00";
                c.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        cursor.close();


        String year = Integer.toString(c.get(Calendar.YEAR));
        int temp = c.get(Calendar.MONTH) + 1;
        String month = null;
        String day = null;

        if (temp < 10)
            month = "0" + Integer.toString(temp);
        else
            month = Integer.toString(temp);

        temp = c.get(Calendar.DAY_OF_MONTH);

        if (temp < 10)
            day = "0" + Integer.toString(temp);
        else
            day = Integer.toString(temp);

        String time = list.get(0).getTime().split("-")[0];
        String datetime = year + "-" + month + "-" + day + " " + time;


        // AlarmInfo alarmInfo = new AlarmInfo(datetime, list.get(0));
        return list.get(0);
    }

    public void deleteAll() {
        db.execSQL("DELETE FROM " + CourseRW.COURSE_TABLE_NAME);
        db.execSQL("DELETE FROM " + CourseRW.LESSON_TABLE_NAME);
    }


    public static class AlarmInfo {
        public String datetime;
        public Course.Lesson lesson;

        AlarmInfo(String datetime, Course.Lesson lesson) {
            this.datetime = datetime;
            this.lesson = lesson;
        }

        public String getDateTime() {
            return this.datetime;
        }

        ;

        public Course.Lesson getLesson() {
            return this.lesson;
        }
    }
}
