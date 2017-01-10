package com.matthew.slideshow.citypass;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by matthew on 26/3/2016.
 */
public class SettingsFragment extends Fragment {

    private View rootView;
    private Switch switch_notification;
    //private LinearLayout lin_content_notification;
    private Switch switch_timetable_share;
    //private LinearLayout lin_add_parameter;
    private SettingsRW settingsRW = null;
    private EventRW eventRW = null;
    private CourseRW courseRW = null;

    private boolean settingsAlerm0 = true;
    private boolean settingsAlerm1 = true;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
/*
* *******************************************************************************************************************
* Changable: change the layout that is going to inflate
*            and the id of the ViewPager
*
* */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.notification_bar_color));
        }

        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        settingsRW = new SettingsRW(getActivity().getApplicationContext());

        if (settingsRW.getCountOfAlerm() < 1) {
            settingsRW.insertAlerm(0, 1);
            settingsRW.insertAlerm(1, 1);
        }

        settingsAlerm0 = settingsRW.getAlerm(0);
        settingsAlerm1 = settingsRW.getAlerm(1);

        switch_notification = (Switch) rootView.findViewById(R.id.switch_notification);

        //lin_content_notification = (LinearLayout) rootView.findViewById(R.id.linearlayout_content_notification);
        switch_timetable_share = (Switch) rootView.findViewById(R.id.switch_timetable);
        //lin_add_parameter = (LinearLayout) rootView.findViewById(R.id.linearlayout_add_parameter);

        switch_notification.setChecked(settingsAlerm0);
        switch_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    settingsRW.setAlerm(0, true);
                } else {
                    settingsRW.setAlerm(0, false);
                }
                setEventAlarm();
            }
        });

        switch_timetable_share.setChecked(settingsAlerm1);
        switch_timetable_share.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    settingsRW.setAlerm(1, true);

                } else {
                    settingsRW.setAlerm(1, false);
                }
                setTimeTableAlarm();
            }
        });

        if (settingsRW.getCount() == 0) {
            settingsRW.insertAlerm(0, 1);
            settingsRW.insertAlerm(1, 1);
        }

        return rootView;
    }

    public void setEventAlarm() {
        eventRW = new EventRW(getContext().getApplicationContext());
        Event event = eventRW.getNotification();
        if (event != null) {
            int notificationId = 0;
            AlarmManager aManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            Intent aIntent = new Intent(getContext(), AlarmManagerReceiver.class);
            aIntent.putExtra(AlarmManagerReceiver.NOTIFICATION, notificationId);
            //PendingIntent.getBroadcast(Context context, int requestCode, Intent intent, int flags)
            PendingIntent pIntent = PendingIntent.getBroadcast(getContext(), notificationId, aIntent, 0);

            Date date;
            try {

                //Log.d("SystemClock current Time: ", Long.toString(currentTimeInMillis));
                //Log.d("system Current Time: ", Long.toString(System.currentTimeMillis()));
                //Log.d("Alarm Start Time: ", Long.toString(delay));
                if (settingsRW.getAlerm(0)) {
                    aManager.cancel(pIntent);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String time = event.getDateTime();
                    date = sdf.parse(time);
                    long currentTimeInMillis = SystemClock.elapsedRealtime();
                    long delay = date.getTime() - System.currentTimeMillis();
                    aManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, currentTimeInMillis + delay, pIntent);
                    Log.d("Alarm set later", Long.toString((delay / 1000) / 60));
                    Log.d("Alarm DateTime", time);
                } else {
                    aManager.cancel(pIntent);
                }

            } catch (Exception e) {
                Log.d("Set Alarm exception", e.getMessage());
            }
        }

    }

    public void setTimeTableAlarm() {
        courseRW = new CourseRW(getActivity().getApplicationContext());
        int NOTIFICATION_ID = 1;
        CourseRW.AlarmInfo alarmInfo = courseRW.getLastestLessonDateTime();

        AlarmManager aManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent aIntent = new Intent(getContext(), AlarmManagerReceiver.class);
        aIntent.putExtra(AlarmManagerReceiver.NOTIFICATION, NOTIFICATION_ID);

        PendingIntent pIntent = PendingIntent.getBroadcast(getContext(), NOTIFICATION_ID, aIntent, 0);

        try {
            if (settingsRW.getAlerm(1)) {
                String datetime_latest_lesson = alarmInfo.getDateTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date;
                date = sdf.parse(datetime_latest_lesson);
                long currentTimeInMillis = SystemClock.elapsedRealtime();
                long delay = date.getTime() - System.currentTimeMillis() - 60 * 60 * 1000;
                aManager.cancel(pIntent);
                aManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, currentTimeInMillis + delay, pIntent);
                Log.d("Alarm set later", Long.toString((delay / 1000) / 60));
                Log.d("Alarm DateTime", alarmInfo.getDateTime());
                //Log.d("course name", course_name);
                //Log.d("lesson time", course_time);
            } else {
                aManager.cancel(pIntent);
            }
        } catch (Exception e) {
            Log.d("Timetable alarm error", e.getMessage());
        }
    }

/*
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (eventRW != null)
            eventRW.close();
        if (courseRW != null)
            courseRW.close();
        if (settingsRW != null)
            settingsRW.close();
    }
    */
}
