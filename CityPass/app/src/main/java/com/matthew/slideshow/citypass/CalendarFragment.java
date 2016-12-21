package com.matthew.slideshow.citypass;

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
import android.widget.Toast;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    private net net;
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
        private final boolean EXCL = false;
        private final boolean INCL = true;

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

                String target = "http://www6.cityu.edu.hk/arro/files/file/hk/calendar/CityU_Academic_Calendar.ics";
                String reference = "https://www.cityu.edu.hk/portal/";
                String cityu_portal = net.get(target, reference);

                String page = betweenStr(cityu_portal, "<div class=\"item month-", "<a href=\"http://www6.cityu.edu.hk/arro/files/file/hk/calendar/CityU_Academic_Calendar.ics\"><i class=\"icon-download\"></i> Download iCalendar (.ics)</a>", EXCL);

                Calendar calendar = null;
                try {
                    calendar = (new CalendarBuilder()).build(new StringReader(page));
                } catch (IOException | ParserException e) {
                    e.printStackTrace();
                }
                assert calendar != null;
                HashMap<String, List<HashMap<String, String>>> date_holiday_array = new HashMap<>();
                List<HashMap<String, String>> holiday_array = new ArrayList<>();
                List<HashMap<String, String>> calendar_holiday = new ArrayList<>();
                for (CalendarComponent value: calendar.getComponents()
                     ) {
                    HashMap<String, String> single = new HashMap<>();
                    String date_detail_s = value.getProperty(Property.DTSTART).getValue().substring(6);
                    String date_detail_e = value.getProperty(Property.DTEND).getValue().substring(6);
                    date_detail_e = String.valueOf(Integer.parseInt(date_detail_e) - 1);
                    int flag = Integer.parseInt(date_detail_s) - Integer.parseInt(date_detail_e);
                    if (flag > 0) {
                        switch (value.getProperty(Property.DTSTART).getValue().substring(4, 2)) {
                            case "01":
                                date_detail_s += " Jan ";
                                break;

                            case "02":
                                date_detail_s += " Feb ";
                                break;

                            case "03":
                                date_detail_s += " Mar ";
                                break;

                            case "04":
                                date_detail_s += " Apr ";
                                break;

                            case "05":
                                date_detail_s += " May ";
                                break;

                            case "06":
                                date_detail_s += " Jun ";
                                break;

                            case "07":
                                date_detail_s += " Jul ";
                                break;

                            case "08":
                                date_detail_s += " Aug ";
                                break;

                            case "09":
                                date_detail_s += " Sep ";
                                break;

                            case "10":
                                date_detail_s += " Oct ";
                                break;

                            case "11":
                                date_detail_s += " Nov ";
                                break;

                            case "12":
                                date_detail_s += " Dec ";
                                break;

                            default:
                                // code...
                                break;
                        }
                        switch (value.getProperty(Property.DTEND).getValue().substring(4, 2)) {
                            case "01":
                                date_detail_e += " Jan ";
                                break;

                            case "02":
                                date_detail_e += " Feb ";
                                break;

                            case "03":
                                date_detail_e += " Mar ";
                                break;

                            case "04":
                                date_detail_e += " Apr ";
                                break;

                            case "05":
                                date_detail_e += " May ";
                                break;

                            case "06":
                                date_detail_e += " Jun ";
                                break;

                            case "07":
                                date_detail_e += " Jul ";
                                break;

                            case "08":
                                date_detail_e += " Aug ";
                                break;

                            case "09":
                                date_detail_e += " Sep ";
                                break;

                            case "10":
                                date_detail_e += " Oct ";
                                break;

                            case "11":
                                date_detail_e += " Nov ";
                                break;

                            case "12":
                                date_detail_e += " Dec ";
                                break;

                            default:
                                // code...
                                break;
                        }
                    }
                    if (value.getProperty(Property.DTSTART).getValue().substring(0, 4).equals(value.getProperty(Property.DTEND).getValue().substring(0, 4))) {
                        date_detail_s += value.getProperty(Property.DTSTART).getValue().substring(0, 4);
                        date_detail_e += value.getProperty(Property.DTEND).getValue().substring(0, 4);
                    }
                    String date_detail = date_detail_s;
                    if (flag > 0) {
                        date_detail += " - "+date_detail_e;
                    }
                    single.put("year",value.getProperty(Property.DTSTART).getValue().substring(0, 4));
                    single.put("month",value.getProperty(Property.DTSTART).getValue().substring(4, 2));
                    single.put("date_detail",date_detail);
                    single.put("holiday_or_event_name", value.toString().contains("SUMMARY;LANGUAGE=en-us")?value.getProperty("SUMMARY;LANGUAGE=en-us").getValue():value.getProperty("SUMMARY;LANGUAGE=zh-hk").getValue());
                    if (value.toString().contains("CATEGORIES")) {
                        single.put("type", "H");
                    }else{
                        if (page.contains(single.get("holiday_or_event_name"))) {
                            single.put("type", "E");
                        }else{
                            continue;
                        }
                    }
                    holiday_array.add(single);
                    String date = value.getProperty(Property.DTSTART).getValue().substring(0, 4)+"-"+value.getProperty(Property.DTSTART).getValue().substring(4, 2)+"-"+value.getProperty(Property.DTSTART).getValue().substring(6, 2);
                    String end_date = value.getProperty(Property.DTEND).getValue().substring(0, 4)+"-"+value.getProperty(Property.DTEND).getValue().substring(4, 2)+"-"+value.getProperty(Property.DTEND).getValue().substring(6, 2);
                    if (single.containsKey("type")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            while (sdf.parse(date).before(sdf.parse(end_date))) {
                                HashMap<String, String> tmp = new HashMap<>();
                                tmp.put("date", date);
                                tmp.put("type", single.get("type"));
                                calendar_holiday.add(tmp);
                                java.util.Calendar c = java.util.Calendar.getInstance();
                                c.setTime(sdf.parse(date));
                                c.add(java.util.Calendar.DATE, 1);
                                date = sdf.format(c.getTime());
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                date_holiday_array.put("holiday", holiday_array);
                date_holiday_array.put("calendar_holiday", calendar_holiday);
                try {
                    JSONObject jsonObject = new JSONObject(date_holiday_array);
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

        private String betweenStr(String target, String a, String b, boolean incl) {
            Log.d("Network", target);
            if (incl) {
                target = target.substring(target.indexOf(a));
                return target.substring(0, target.indexOf(b) + b.length());
            }
            target = target.substring(target.indexOf(a) + a.length());
            return target.substring(0, target.indexOf(b));
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


