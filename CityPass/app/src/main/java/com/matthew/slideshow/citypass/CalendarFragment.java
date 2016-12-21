package com.matthew.slideshow.citypass;


import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static java.text.DateFormat.getDateInstance;

/**
 * Created by matthew on 13/1/2016.
 */
public class CalendarFragment extends Fragment {

    View view;
    private HolidayRW holidayRW = null;
    private EventRW eventRW = null;
    private LoginRW loginRW = null;
    private HashSet<Date> eventssave = new HashSet<>();
    private FragmentManager fragmentManager;
    private WebView webView;
    private String holidayData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.notification_bar_color));
        }
        view = inflater.inflate(R.layout.calendar_main, container, false);
        fragmentManager = getActivity().getSupportFragmentManager();
        if (holidayRW == null)
            holidayRW = new HolidayRW(getActivity().getApplicationContext());
        if (eventRW == null)
            eventRW = new EventRW(getActivity().getApplicationContext());

        try {
            new InitializeData().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return view;
    }

    class InitializeData extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            int icount = holidayRW.getCountAllHoliday();
            if (icount != 0) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                if (eventRW == null)
                    eventRW = new EventRW(getActivity().getApplicationContext());
                List<Event> list = eventRW.getAllEvents();

                try {
                    if (list != null) {
                        for (int a = 0; a < list.size(); a++) {
                            Date date = dateFormat.parse(list.get(a).getDate());
                            eventssave.add(date);
                        }
                    }
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.d("Sqlite", "sqlite" + "Fail");
                }
            } else {
                final String DATA = "data";
                final String HOLIDAY = "holiday";
                final String YEAR = "year";
                final String MONTH = "month";
                final String TYPE = "type";
                final String DATE_DETAILS = "date_detail";
                final String EVENT_NAME = "holiday_or_event_name";
                final String CALENDAR_HOLIDAY = "calendar_holiday";
                final String DATE = "date";
                if (holidayRW == null)
                    holidayRW = new HolidayRW(getActivity().getApplicationContext());
                String rawHolidayData = null;
                //rawHolidayData is the point
                webView.loadUrl("http://202.125.255.5/portal_reader.php");
                while (holidayData == null) {
                    if (holidayData != null) rawHolidayData = holidayData;
                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    JSONObject jsonObject = new JSONObject(rawHolidayData);
                    String str = jsonObject.getString(DATA);
                    JSONObject j0 = new JSONObject(str);
                    JSONArray holidayArray = j0.getJSONArray(HOLIDAY);
                    holidayRW.deleteHoliday();
                    for (int i = 0; i < holidayArray.length(); i++) {
                        // get the current Plant JSON object.
                        JSONObject jsonHoliday = holidayArray.getJSONObject(i);
                        // get the data from JSON, save into Java.
                        String holiday_or_event_name = jsonHoliday.getString(EVENT_NAME);
                        String date_detail = jsonHoliday.getString(DATE_DETAILS);
                        int month = jsonHoliday.getInt(MONTH);
                        int year = jsonHoliday.getInt(YEAR);
                        String type = jsonHoliday.getString(TYPE);
                        Holiday holiday = new Holiday(holiday_or_event_name, date_detail, month, year, type);
                        holidayRW.insertHoliday(holiday);
                    }
                    Log.d("json", "sqlite    " + "step 4");
                    JSONArray calendar_holidayArray = j0.getJSONArray(CALENDAR_HOLIDAY);
                    Log.d("json", "sqlite    " + "step 5");
                    holidayRW.deleteCalendarHoliday();
                    for (int i = 0; i < calendar_holidayArray.length(); i++) {
                        // get the current Plant JSON object.
                        JSONObject jsonCHoliday = calendar_holidayArray.getJSONObject(i);
                        //Log.d("json", "sqlite    " + "step 6");
                        // get the data from JSON, save into Java.
                        String CHolidayDate = jsonCHoliday.getString(DATE);
                        String CHolidayType = jsonCHoliday.getString(TYPE);
                        holidayRW.insertCalendarHoliday(CHolidayDate, CHolidayType);
                    }
                } catch (JSONException e) {
                    Log.d("json", "sqlite    " + "Some crazy thing happen2");
                    throw new RuntimeException(e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String a) {
            CalendarView cv = ((CalendarView) view.findViewById(R.id.calendar_view));
            cv.setHolidayHash();
            cv.updateCalendar(eventssave);
            cv.setEventHandler(new CalendarView.EventHandler() {
                @Override
                public void onDayLongPress(Date date) {
                    // show returned day
                    int style = DateFormat.MEDIUM;
                    DateFormat df = getDateInstance(style, Locale.UK);
                    List<Event> events = eventRW.getAllByDate(date);
                    if (events == null) {
                        Toast.makeText(getContext(), "There are no event on that day", Toast.LENGTH_SHORT).show();
                    } else {
                        CalendarEventListFragment celf = CalendarEventListFragment.newInstance(events);
                        fragmentManager.beginTransaction().replace(R.id.container, celf).commit();
                    }
                }


            });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.add(Menu.NONE, 0, 10, "");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setIcon(R.drawable.ic_add_white_24dp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                TodoListAddFragment tlaf = new TodoListAddFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("BackTrack", 2);
                tlaf.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.container, tlaf).commit();
                //Toast.makeText(getContext(), "Add", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loginRW != null)
            loginRW.close();
        if (holidayRW != null)
            holidayRW.close();
        if (eventRW != null)
            eventRW.close();
    }
}


