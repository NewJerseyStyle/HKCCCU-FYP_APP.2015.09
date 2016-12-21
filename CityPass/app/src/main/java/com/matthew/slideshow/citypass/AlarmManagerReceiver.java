package com.matthew.slideshow.citypass;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yeung on 2/27/2016.
 */
public class AlarmManagerReceiver extends BroadcastReceiver {
    //private static final int NOTIFICATION_ID = 1;
    public static final String NOTIFICATION = "NOTIFICATION";

    private Context context;
    private int notificationId;
    private EventRW eventRW;

    private CourseRW courseRW;
    //private CourseRW.AlarmInfo alarmInfo;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Intent service = new Intent(context, AlarmService.class);
        //context.startService(service);
        this.context = context;
        notificationId = intent.getIntExtra(NOTIFICATION, 0);

        if (notificationId == 0) {
            eventRW = new EventRW(this.context);
            Event event = eventRW.getNotification();

            if (event != null) {
                //nManager = (NotificationManager) this.context.getSystemService(context.NOTIFICATION_SERVICE);
                Intent intent1 = new Intent(this.context, HomeActivity.class);

                //pIntent = PendingIntent.getActivity(this.context, notificationId, intent1, PendingIntent.FLAG_CANCEL_CURRENT);

                //eventName = event.getName();
                //eventDateTime = event.getDateTime();

                event.setNotify(0); //when notified, disabled the Event of Notification
                eventRW.update(event);

                if (eventRW.getNotification() != null) {
                    setEventAlarm();
                    //Toast.makeText(context, "Schedule Notification", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(context, "No futher notification", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Null Alarm Event", Toast.LENGTH_SHORT).show();
            }
        } else {
            CourseRW courseRW = new CourseRW(this.context);

            //alarmInfo = courseRW.getLastestLessonDateTime();

            //nManager = (NotificationManager) this.context.getSystemService(context.NOTIFICATION_SERVICE);
            Intent intent1 = new Intent(this.context, HomeActivity.class);

            //pIntent = PendingIntent.getActivity(this.context, notificationId, intent1, PendingIntent.FLAG_CANCEL_CURRENT);

            //Course.Lesson lesson = alarmInfo.getLesson();
            //long course_id = lesson.getLesson_course_id();
            //Bundle bundle = intent.getExtras();
            Course.Lesson lesson = courseRW.getLatestLesson();
            long id = lesson.getLesson_course_id();
            String course_name = courseRW.getCourseName(id);
            //intent.getStringExtra(TITLE);//bundle.getString(TITLE);
            String course_time = lesson.getTime();
            //intent.getStringExtra(MSG);//bundle.getString(MSG);
        }

    }


    public void setEventAlarm() {
        AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent aIntent = new Intent(context, AlarmManagerReceiver.class);
        aIntent.putExtra(AlarmManagerReceiver.NOTIFICATION, notificationId);
        //PendingIntent.getBroadcast(Context context, int requestCode, Intent intent, int flags)
        PendingIntent pIntent = PendingIntent.getBroadcast(context, notificationId, aIntent, 0);
        long currentTimeInMillis = SystemClock.elapsedRealtime();
        EventRW eventRW = new EventRW(context.getApplicationContext());
        Event event = eventRW.getNotification();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = event.getDateTime();
        Date date;
        try {
            date = sdf.parse(time);
            long delay = date.getTime() - System.currentTimeMillis();
            //Log.d("SystemClock current Time: ", Long.toString(currentTimeInMillis));
            //Log.d("system Current Time: ", Long.toString(System.currentTimeMillis()));
            //Log.d("Alarm Start Time: ", Long.toString(delay));
            aManager.cancel(pIntent);
            aManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, currentTimeInMillis + delay, pIntent);
        } catch (Exception e) {
            Log.e("Set Alarm exception", e.getMessage());
        }
    }


}
