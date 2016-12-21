package com.matthew.slideshow.citypass;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yeung on 2/27/2016.
 */
public class AlarmService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private Context context;
    private NotificationManager nManager;
    private PendingIntent pIntent;
    private String eventName = "";
    private String eventDateTime = "";
    private EventRW eventRW;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        context = this.getApplicationContext();
        nManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(this, HomeActivity.class);
        pIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);

        eventRW = new EventRW(context);
        Event event = eventRW.getNotification();

        if (event != null) {
            eventName = event.getName();
            eventDateTime = event.getDateTime();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle(eventName);
            builder.setContentText(eventDateTime);
            builder.setSmallIcon(R.mipmap.ic_app_icon);
            builder.setContentIntent(pIntent);

            nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nManager.notify(NOTIFICATION_ID, builder.build());
            event.setNotify(0); //when notified, disabled the Event of Notification
            eventRW.update(event);
        }


        if (eventRW.getNotification() != null) {
            setAlarm();
            Toast.makeText(context, "New Notification: " + eventRW.getNotification().getName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "No More Notification", Toast.LENGTH_SHORT).show();
        }

    }


    public void setAlarm() {
        AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent aIntent = new Intent(context, AlarmManagerReceiver.class);

        //PendingIntent.getBroadcast(Context context, int requestCode, Intent intent, int flags)
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, aIntent, 0);
        long currentTimeInMillis = SystemClock.elapsedRealtime();
        EventRW eventRW = new EventRW(context.getApplicationContext());
        Event event = eventRW.getNotification();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = event.getDateTime();
        Date date;
        try {
            date = sdf.parse(time);
            long delay = date.getTime() - System.currentTimeMillis();
            Log.d("SystemClock current Time: ", Long.toString(currentTimeInMillis));
            Log.d("system Current Time: ", Long.toString(System.currentTimeMillis()));
            Log.d("Alarm Start Time: ", Long.toString(delay));
            aManager.cancel(pIntent);
            aManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, currentTimeInMillis + delay, pIntent);
        } catch (Exception e) {
            Log.e("Set Alarm exception", e.getMessage());

        }

    }
}
