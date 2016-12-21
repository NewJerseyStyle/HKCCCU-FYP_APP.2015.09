package com.matthew.slideshow.citypass;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by matthew on 14/1/2016.
 */


public class TimetableFragment extends Fragment {
    public static final int NOTIFICATION_ID = 1;
    private boolean parenetIsHmFragment = false;
    private View rootView;
    private List<Course> courses;
    private LayoutInflater mInflater;
    boolean detailsClicked;

    private PopupWindow popupWindow;
    private View linearLayout;
    private View scrollView;
    private View timetableLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String temp_json;
    private net networkSimple, networkDetail;

    CourseRW courseRW = null;
    LoginRW loginRW = null;


    private FragmentManager fragmentManager;

    /*
    * Method test() is a sample
    * to demonstrate how to embed
    * the data of courses into
    * the timetable.
    * */
    public void setAlarm() {
        if (courseRW == null)
            courseRW = new CourseRW(getContext().getApplicationContext());
        CourseRW.AlarmInfo alarmInfo = courseRW.getLastestLessonDateTime();

        AlarmManager aManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent aIntent = new Intent(getContext(), AlarmManagerReceiver.class);
        aIntent.putExtra(AlarmManagerReceiver.NOTIFICATION, NOTIFICATION_ID);

        long temp_course_id = alarmInfo.getLesson().getLesson_course_id();

        //Bundle bundle = new Bundle();
        //bundle.putString(AlarmManagerReceiver.TITLE, courseRW.getCourseName(temp_course_id));
        //bundle.putString(AlarmManagerReceiver.MSG, alarmInfo.getLesson().getTime());
        //aIntent.putExtras(bundle);
        //String course_name = courseRW.getCourseName(temp_course_id);
        //String course_time = alarmInfo.getLesson().getTime();
        //aIntent.putExtra(AlarmManagerReceiver.TITLE, course_name);
        //aIntent.putExtra(AlarmManagerReceiver.MSG, course_time);
        PendingIntent pIntent = PendingIntent.getBroadcast(getContext(), NOTIFICATION_ID, aIntent, 0);
        long currentTimeInMillis = SystemClock.elapsedRealtime();


        String datetime_latest_lesson = alarmInfo.getDateTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date;
        try {
            date = sdf.parse(datetime_latest_lesson);
            long delay = date.getTime() - System.currentTimeMillis() - 60 * 60 * 1000;
            aManager.cancel(pIntent);
            aManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, currentTimeInMillis + delay, pIntent);
            Log.d("Alarm set later", Long.toString((delay / 1000) / 60));
            Log.d("Alarm DateTime", alarmInfo.getDateTime());
        } catch (Exception e) {
            Log.d("Timetable alarm error", e.getMessage());
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.notification_bar_color));
        }


        fragmentManager = getActivity().getSupportFragmentManager();


        mInflater = inflater;
        rootView = mInflater.inflate(R.layout.fragment_timetable, container, false);
        courses = new ArrayList<Course>();
        detailsClicked = false;
        createTable();

        if (courseRW == null) {
            courseRW = new CourseRW(getActivity().getApplicationContext());
        }

        Log.d("TimeTableFragment", "Initializing");
        LoginRW loginRW = new LoginRW(TimetableFragment.this.getContext());
        final String login_name = loginRW.getLoginName();
        final String login_password = loginRW.getLoginPassword();
        try {
            networkSimple = new net(getActivity().getApplicationContext());
            networkDetail = new net(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        new CreateTable().execute();

        linearLayout = (LinearLayout) rootView.findViewById(R.id.linearlayout);
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        timetableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Toast.makeText(getContext(), "Refresh", Toast.LENGTH_SHORT).show();
                pullUpRefresh();
            }
        });


        scrollView.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent m) {
                        dismissPopUpWindow();
                        return false;
                    }
                }
        );

        //setAlarm();

        return rootView;
    }


    private void pullUpRefresh() {

        new RefreshTable().execute();

    }

    private void createTable() {
        TableLayout tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout);

        tableLayout.setBackgroundColor(Color.rgb(15, 147, 242));  // table line color
        TableRow tableRow;
        for (int a = 0; a < 16; a++) //Time = {09:00, 10:00, 11:00, 12:00 ...}
        {
            tableRow = new TableRow(getContext());
            for (int b = 0; b < 7; b++) //days = {Mon, Tue, Wed, Tur, Fri, Sat}
            {
                TextView textView = new TextView(getContext());
                textView.setTag("t" + Integer.toString(b) + Integer.toString(a));
                textView.setTextSize(8);
                textView.setPadding(10, 2, 3, 15);

                //textView.setTextColor(Color.BLACK);//text color of the table title
                textView.setTextColor(Color.rgb(117, 117, 117));
                if (b == 0 && a != 0) {
                    if (a == 1)
                        textView.setText("0" + Integer.toString(a + 8) + ":00 - \n" + "0" + Integer.toString(a + 8) + ":50");
                    else
                        textView.setText(Integer.toString(a + 8) + ":00 - \n" + Integer.toString(a + 8) + ":50");
                    // textView.setBackgroundColor(Color.LTGRAY);
                    textView.setBackgroundColor(Color.rgb(255, 255, 255));// table title background
                    TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT); // set table row height and width

                    llp.setMargins(1, 1, 1, 1);
                    textView.setLayoutParams(llp);
                } else if (a == 0) {
                    textView.setTextColor(Color.WHITE);
                    textView.setPadding(8, 3, 3, 3);
                    textView.setTextSize(9);
                    textView.setGravity(Gravity.CENTER);

                    switch (b) {
                        case 0:
                            textView.setText("Time");
                            break;
                        case 1:
                            textView.setText("Monday");
                            break;
                        case 2:
                            textView.setText("Tuesday");
                            break;
                        case 3:
                            textView.setText("Wednesday");
                            break;
                        case 4:
                            textView.setText("Thursday");
                            break;
                        case 5:
                            textView.setText("Friday");
                            break;
                        case 6:
                            textView.setText("Saturday");
                            break;
                    }
                    //textView.setBackgroundColor(Color.LTGRAY);// background of the table title
                    textView.setBackgroundColor(Color.rgb(15, 147, 242));

                } else {
                    textView.setText("                       ");
                    //textView.setBackgroundColor(Color.rgb(235,235,235));// color of non title field
                    textView.setBackgroundColor(Color.rgb(255, 255, 255));

                }
                TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
                llp.setMargins(1, 1, 1, 1);
                textView.setLayoutParams(llp);
                tableRow.addView(textView);
            }
            tableLayout.addView(tableRow);
        }
    }


    class CreateTable extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private boolean firstTime = true;

        @Override
        protected void onPreExecute() {
            firstTime = (courseRW.getCourseCount() == 0);
            if (firstTime) {
                dialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);

                dialog.setMessage("It's just loading....");
                dialog.setCancelable(false);


                dialog.setCanceledOnTouchOutside(false);
                if (TimetableFragment.this.isVisible()) {
                    dialog.show();
                }
            }


        }

        @Override
        protected String doInBackground(String... args) {
            try {
                networkSimple.getTimeTable();
                networkDetail.getDetailSchedule();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            if (firstTime) {
                setJSONTimetable();
            }

            courses = courseRW.getAll();
            //Log.d("Day of week", Integer.toString(courseRW.getTodayLesson()));
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            for (int a = 0; a < courses.size(); a++) {
                embedCourseIntoTable(courses.get(a), a);
            }

            if (dialog.isShowing())
                dialog.dismiss();

        }
    }


    class RefreshTable extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... args) {

            try {
                networkSimple.getTimeTable();
                networkDetail.getDetailSchedule();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (courseRW == null) {
                courseRW = new CourseRW(getActivity().getApplicationContext());
            }
            courseRW.deleteAll();
            setJSONTimetable();
            // courses = courseRW.getAll();
            //Log.d("Day of week", Integer.toString(courseRW.getTodayLesson()));
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            swipeRefreshLayout.setRefreshing(false);


            HomePageFragment homePageFragment = new HomePageFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Primary", 0);
            homePageFragment.setArguments(bundle);

            fragmentManager.beginTransaction().replace(R.id.container, homePageFragment).commit();

        }
    }


    public boolean popUpWindowIsShowing() {
        if (popupWindow != null && popupWindow.isShowing()) {
            return true;
        } else
            return false;
    }


    public void dismissPopUpWindow() {
        if (popupWindow != null && popUpWindowIsShowing()) {
            popupWindow.dismiss();
            detailsClicked = false;
            popupWindow = null;
            if (parenetIsHmFragment)
                ((HomePageFragment) getParentFragment()).setDrawerEnabled(true);
            else
                ((HomeActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    private void embedCourseIntoTable(final Course course, int a) {
        for (int b = 0; b < course.lesson.size(); b++) {
            int time1 = course.getTimeShift(b);
            int day1 = course.getDaysShift(b);
            int backgroundColor = backgroundColor(a);
            String str = course.getCourse_code() + " \n"
                    + course.getLessonSection(b) + '\n'
                    + course.getLessonLocation(b);

            TextView temp1 = (TextView) rootView.findViewWithTag("t" + Integer.toString(day1) + Integer.toString(time1));
            temp1.setText(str);
            temp1.setTextColor(Color.WHITE);
            temp1.setTextSize(9);

            temp1.setTypeface(Typeface.DEFAULT_BOLD);
            temp1.setPadding(15, 15, 15, 15);// set course padding
            course.setColor(backgroundColor);
            temp1.setBackgroundColor(backgroundColor);

            TextView.OnClickListener listener = new View.OnClickListener() {
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.popup_details_schedule, null);

                public void onClick(View v) {
                    if (!detailsClicked) {
                        detailsClicked = true;

                        if (parenetIsHmFragment)
                            ((HomePageFragment) getParentFragment()).setDrawerEnabled(false);
                        else
                            ((HomeActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                        String course_name = course.getCourse_name();
                        ListView listView = (ListView) popupView.findViewById(R.id.popListView);
                        List<Course.Lesson> lesson = new ArrayList<>();
                        lesson.add(course.lesson.get(0));
                        MyListViewAdapter myListViewAdapter = new MyListViewAdapter(getContext(), lesson, course_name, course.getColor());
                        listView.setAdapter(myListViewAdapter);

                        int width = linearLayout.getWidth();
                        int height = linearLayout.getHeight();
                        int pWidth = (int) (width * 0.8);
                        int pHeight = (int) (height * 0.6);
                        width = (width - pWidth) / 2;
                        height = height - ((height - pHeight) / 4);

                        popupWindow = new PopupWindow(
                                popupView,
                                pWidth,
                                pHeight
                        );

                        popupWindow.showAsDropDown(linearLayout, width, -(height));
                        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.container_boarder_line));

                    }
                }
            };
            temp1.setOnClickListener(listener);

            if (course.getLessonDuration(b) > 1) {
                int time2 = time1;
                for (int c = 0; c < course.getLessonDuration(b); c++, time2++) {
                    TextView temp2 = (TextView) rootView.findViewWithTag("t" + Integer.toString(day1) + Integer.toString(time2));
                    temp2.setBackgroundColor(backgroundColor(a));
                    temp2.setOnClickListener(listener);
                    TableRow.LayoutParams llp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
                    if (c == 0)
                        llp1.setMargins(1, 1, 1, 0);
                    else if (c == course.getLessonDuration(b) - 1)
                        llp1.setMargins(1, 0, 1, 1);
                    else
                        llp1.setMargins(1, 0, 1, 0);
                    temp2.setLayoutParams(llp1);
                }
            }
        }
    }


    public int backgroundColor(int a) {
        switch (a % 6) {
            case 0:
                return Color.rgb(242, 136, 115);
            case 1:
                return Color.rgb(170, 204, 126);
            case 2:
                return Color.rgb(179, 152, 227);
            case 3:
                return Color.rgb(242, 124, 170);
            case 4:
                return Color.rgb(115, 172, 233);
            case 5:
                return Color.rgb(139, 139, 143);
        }
        return Color.WHITE;
    }


    private static class MyListViewAdapter extends ArrayAdapter<Course.Lesson> {
        private final Context context;
        private final List<Course.Lesson> lessons;
        private final String course_name;
        private final int backgroundColor;

        public MyListViewAdapter(Context context, List<Course.Lesson> lessons, String course_name, int backgroundColor) {
            super(context, R.layout.row_popup_details_schedule, lessons);
            this.context = context;
            this.lessons = lessons;
            this.course_name = course_name;
            this.backgroundColor = backgroundColor;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rootView = inflater.inflate(R.layout.row_popup_details_schedule, parent, false);

            if (position == 0) {
                TextView textView_course_name = (TextView) rootView.findViewById(R.id.textView_list_date);
                textView_course_name.setBackgroundColor(backgroundColor);
                textView_course_name.setTextColor(Color.WHITE);
                textView_course_name.setText(course_name);
            }
            if (!(position == 0 || position % 2 == 0)) {
                rootView.setBackgroundColor(Color.rgb(238, 238, 238));
            }

            //TextView textView_type = (TextView) rootView.findViewById(R.id.textView_type);
            //textView_type.setText(lessons.get(position).getType());

            TextView textView_associated_term = (TextView) rootView.findViewById(R.id.textView_associated_term);
            textView_associated_term.setText(lessons.get(position).getAssociated_term());

            //TextView textView_crn = (TextView) rootView.findViewById(R.id.textView_crn);
            //textView_crn.setText(lessons.get(position).getCrn());

            TextView textView_assigned_instructor = (TextView) rootView.findViewById(R.id.textView_assigned_instructor);
            textView_assigned_instructor.setText(lessons.get(position).getAssigned_instructor());

            TextView textView_grade_mode = (TextView) rootView.findViewById(R.id.textView_grade_mode);
            textView_grade_mode.setText(lessons.get(position).getGrade_mode());

            //TextView textView_credits = (TextView) rootView.findViewById(R.id.textView_credits);
            //textView_credits.setText(lessons.get(position).getCredits());

            TextView textView_level = (TextView) rootView.findViewById(R.id.textView_level);
            textView_level.setText(lessons.get(position).getLevel());

            TextView textView_campus = (TextView) rootView.findViewById(R.id.textView_campus);
            textView_campus.setText(lessons.get(position).getCampus());

            //TextView textView_place = (TextView) rootView.findViewById(R.id.textView_place);
            //textView_place.setText(lessons.get(position).getLocation());

            TextView textView_date_range = (TextView) rootView.findViewById(R.id.textView_date_range);
            textView_date_range.setText(lessons.get(position).getDate_range());

            //TextView textView_Time = (TextView) rootView.findViewById(R.id.textView_time);
            //textView_Time.setText(lessons.get(position).getTime());


            return rootView;
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        parenetIsHmFragment = getArguments().getBoolean("HomeFragment");
    }

    public void setJSONTimetable() {

        final String COURSE = "course";
        final String COURSE_CODE = "course_code";

        final String LESSON = "lesson";
        final String CRN = "crn";
        final String STARTTIME = "starttime";
        final String DAYS = "days";
        final String SECTION = "section";
        final String LOCATION = "location";
        final String DURATION = "duration";


        //suppose to connect to server to catch the JSON of the timetable,
        //below method "setJSONDetails()" will catch the further details
        while (networkSimple.getResponse() == null) {
            try {
                Thread.sleep(550);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            if (networkSimple.getError()) {
                throw new JSONException("Error occur in network component.");
            }
            temp_json = networkSimple.getResponse().toString();//jsonObject.get("data").toString();

            //temp_json = "";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject j = new JSONObject(temp_json);
            //   Log.d("Timetable temp json", temp_json);
            JSONArray jArr_course = j.getJSONArray(COURSE);
            for (int a = 0; a < jArr_course.length(); a++) {
                String course_code;
                JSONObject j1 = jArr_course.getJSONObject(a);
                course_code = j1.getString(COURSE_CODE);

                Course course = new Course(course_code);

                JSONArray jArr_lesson = j1.getJSONArray(LESSON);
                for (int b = 0; b < jArr_lesson.length(); b++) {
                    String crn;
                    String starttime;
                    String days;
                    String section;
                    String location = "";
                    int duration;


                    JSONObject j2 = jArr_lesson.getJSONObject(b);
                    crn = j2.getString(CRN);
                    starttime = j2.getString(STARTTIME);
                    days = j2.getString(DAYS);
                    section = j2.getString(SECTION);
                    String[] temp = j2.getString(LOCATION).split(",");
                    for (int i = 0; i < temp.length; i++) {
                        location += temp[i];
                        if (temp.length > 1 && i < temp.length - 1)
                            location += "/\n";
                    }
                    duration = (j2.getString(DURATION).charAt(0)) - 48;


                    course.addLesson(crn, starttime, days, section, location, duration);

                }

                if (courseRW == null)
                    courseRW = new CourseRW(getActivity().getApplicationContext());

                courseRW.insertTimetable(course);

            }
        } catch (Exception e) {
            Log.d("Json get fail", e.getMessage());
        }

        //this method will catch the "Detail Schedule" JSON and then insert into SQLite database
        setJSONDetails();

    }

    public void setJSONDetails() {
        //Suppose this part will catch all of the course name


        ArrayList<String> list = new ArrayList<String>();

        final String TOTAL_CREDITS = "TotalCreditHours";
        //final String DATA = "data";
        final String COURSE_NAME = "courseName";
        final String COURSE = "course";

        //Suppose this part will connect the server to catch the JSON of the "Detail Schedule"
        String temp_JSON = null;
        while (networkDetail.getResponse() == null) {
            try {
                Thread.sleep(550);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            // Log.d("UI", network.getResponseValue());
            if (networkDetail.getError()) {
                throw new JSONException("Error occur in network component.");
            }
            String temp = networkDetail.getResponse().toString();
            if (temp.isEmpty()) {
                return;
            }
            temp = temp.replace("\\", "");
            // Log.d("temp", temp);
            JSONObject jsonObject = new JSONObject(temp);
            //jsonObject = new JSONObject(jsonObject.getString("data"));
            JSONArray jsonArray = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                jsonArray = jsonObject.getJSONArray(COURSE_NAME);//new JSONArray(jsonObject.get("courseName"));
            }
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    list.add(jsonArray.get(i).toString());
                }
            }
            temp_JSON = jsonObject.getString(COURSE);//get("course").toString();
        } catch (JSONException e) {
            Log.d("setJSON Details", e.getMessage());

        }
        String[] course_names = new String[list.size()];
        course_names = list.toArray(course_names);
        //Log.d("json course", temp_JSON);

        for (int a = 0; a < course_names.length; a++) {
            setDetails(course_names[a], temp_JSON);
            //Log.d("Course Name", course_names[a]);

        }

        setAlarm();

    }

    public void setDetails(String course_name, String json) {


        final String ASSOCIATIED_TERM = "AssociatedTerm";
        final String CREDITS = "Credits";

        final String SMEETINGTIME = "ScheduledMeetingTimes";
        final String WEEKREF = "WeekRef";

        final String WEEK0 = "Week0";
        final String TIME = "Time";
        final String PLACE = "Place";
        final String DATERANGE = "DateRange";

        final String CAMPUS = "Campus";
        final String ASS_INSTR = "AssignedInstructor";
        final String GREDE_MODE = "GradeMode";
        final String CRN = "CRN";
        final String LEVEL = "Level";

        String asso_term;
        String credits;
        String weekRef;
        // String week0;

        String time;
        String place;
        String dateRange;
        String campus;
        String ass_instr;
        String gradeMode;
        String crn;
        String level;


        try {
            JSONObject j = new JSONObject(json);
            JSONObject j_course = j.getJSONObject(course_name);
            asso_term = j_course.getString(ASSOCIATIED_TERM);
            //credits = j_course.getString(CREDITS);
            campus = j_course.getString(CAMPUS);
            ass_instr = j_course.getString(ASS_INSTR);
            gradeMode = j_course.getString(GREDE_MODE);
            //crn = j_course.getString(CRN);
            level = j_course.getString(LEVEL);


            JSONObject j_schedu_meeting = j_course.getJSONObject(SMEETINGTIME);
            //weekRef = j_schedu_meeting.getString(WEEKREF);

            JSONObject j_week0 = j_schedu_meeting.getJSONObject(WEEK0);
            //time = j_week0.getString(TIME);
            //place = j_week0.getString(PLACE);
            dateRange = j_week0.getString(DATERANGE);

            if (courseRW == null)
                courseRW = new CourseRW(getActivity().getApplicationContext());

            courseRW.insertDetails(course_name, "TYPE", asso_term, ass_instr, gradeMode, "CREDITS", level, campus, dateRange);
            // Log.d("course_id", Long.toString(z));
        } catch (Exception e) {
            Log.d("setDetail(S", e.getMessage());
        }
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d("on destory", "timetable");
        if (courseRW != null)
            courseRW.close();
        if (loginRW != null)
            loginRW.close();
    }
}
