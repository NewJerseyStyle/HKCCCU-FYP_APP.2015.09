package com.matthew.slideshow.citypass;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Created by a7med on 28/06/2015.
 */
public class CalendarView extends LinearLayout {
    ListView listView;


    // for logging
    private static final String LOGTAG = "Calendar View";

    private HolidayRW holidayRW = null;

    // how many days to show, defaults to six weeks, 42 days
    private static final int DAYS_COUNT = 42;
    // default date format
    private static final String DATE_FORMAT = "MMM yyyy";
    // date format
    private String dateFormat;
    // current displayed month
    private Calendar currentDate = Calendar.getInstance();
    //event handling
    private EventHandler eventHandler = null;
    private HashSet<Date> eventDaysKeep;

    private HashSet<Date> HolidayDaysKeep = new HashSet<>();
    private HashSet<Date> SchoolEventDaysKeep = new HashSet<>();


    // keep the event day
    // internal components
    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;
    // seasons' rainbow
    int[] rainbow = new int[]{
            R.color.summer,
            R.color.fall,
            R.color.winter,
            R.color.spring
    };

    // month-season association (northern hemisphere, sorry australia :)
    int[] monthSeason = new int[]{2, 2, 3, 3, 3, 0, 0, 0, 1, 1, 1, 2};

    public CalendarView(Context context) {
        super(context);
    }


    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }

    /**
     * Load control xml layout
     */

    public void setHolidayHash() {
        if (holidayRW == null)
            holidayRW = new HolidayRW(getContext());
        String[] allDateH = holidayRW.getAllDateH();
        String[] allDateE = holidayRW.getAllDateE();


        // change the string array to "Date" type format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int j = 0; j < allDateH.length && allDateH[0] != null; j++) {
            try {
                Date date = dateFormat.parse(allDateH[j]);
                HolidayDaysKeep.add(date);

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("Sqlite", "Chang string format    " + "Fail");
            }


            for (int k = 0; k < allDateE.length && allDateE[0] != null; k++) {
                try {
                    Date date = dateFormat.parse(allDateE[k]);
                    //Log.d("Sqlite", "Change string format    " + "try2");

                    SchoolEventDaysKeep.add(date);
                    //Log.d("Sqlite", "Change string format    " + "success2");
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.d("Sqlite", "Chang string format    " + "Fail");
                }
            }


        }

    }


    private Holiday[] initEventList(String month, int year) {
        if (holidayRW == null)
            holidayRW = new HolidayRW(getContext());
        List<Holiday> holidayList = holidayRW.getHoliday(month, year);
        Holiday[] holidays = {new Holiday("No event or holiday in this month", "  ", 2, 2016, "E")};
        if (holidayList.size() != 0) {
            holidays = new Holiday[holidayList.size()];

            for (int a = 0; a < holidayList.size(); a++) {
                holidays[a] = holidayList.get(a);
            }
        }
        return holidays;
    }

    private void initControl(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_calendar, this);

        loadDateFormat(attrs);
        assignUiElements();
        assignClickHandlers();

        updateCalendar();
    }

    private void loadDateFormat(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);

        try {
            // try to load provided date format, and fallback to default otherwise
            dateFormat = ta.getString(R.styleable.CalendarView_dateFormat);
            if (dateFormat == null)
                dateFormat = DATE_FORMAT;
        } finally {
            ta.recycle();
        }
    }

    private void assignUiElements() {
        // layout is inflated, assign local variables to components
        header = (LinearLayout) findViewById(R.id.calendar_header);
        btnPrev = (ImageView) findViewById(R.id.calendar_prev_button);
        btnNext = (ImageView) findViewById(R.id.calendar_next_button);
        txtDate = (TextView) findViewById(R.id.calendar_date_display);
        grid = (GridView) findViewById(R.id.calendar_grid);
        listView = (ListView) findViewById(R.id.holiday_list);
    }

    private void assignClickHandlers() {
        // add one month and refresh UI
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, 1);
                updateCalendar(eventDaysKeep);
            }
        });

        // subtract one month and refresh UI
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, -1);
                updateCalendar(eventDaysKeep);
            }
        });

        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> view, View cell, int position, long id) {
                // handle long-press
                if (eventHandler == null)
                    return false;

                // eventHandler.onDayLongPress((Date) view.getItemAtPosition(position));
                return true;
            }
        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> view, View cell, int position, long id) {

                Object selected = grid.getItemAtPosition(position);
                eventHandler.onDayLongPress((Date) view.getItemAtPosition(position));
                //       Log.e("DEBUG", selected.toString())
                Log.d(LOGTAG, "ITEM CLICK" + position);


            }
        });
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar() {
        updateCalendar(null);
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar(HashSet<Date> events) {
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar) currentDate.clone();

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // fill cells
        while (cells.size() < DAYS_COUNT) {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid
        grid.setAdapter(new CalendarAdapter(getContext(), cells, events));

        // update title
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        txtDate.setText(sdf.format(currentDate.getTime()));
        SimpleDateFormat sdf1 = new SimpleDateFormat("M");
        String month1 = sdf1.format(currentDate.getTime());


        // set header color according to current season
        int month = currentDate.get(Calendar.MONTH);
        int season = monthSeason[month];
        int color = rainbow[season];
        int year = currentDate.get(Calendar.YEAR);
        //Log.d(LOGTAG, " month  " + month1);
        //Log.d(LOGTAG, " year  " + year);


        header.setBackgroundColor(getResources().getColor(color));


        Holiday[] holiday_data = initEventList(month1, year);

        HolidayAdapter adapter = new HolidayAdapter(getContext(), R.layout.listview_item_row, holiday_data);
        listView.setAdapter(adapter);
        listView.setClickable(false);
        listView.setOnItemClickListener(null);
        listView.setDivider(null);
        listView.setDividerHeight(0);
    }


    private class CalendarAdapter extends ArrayAdapter<Date> {

        // days with events
        private HashSet<Date> eventDays;

        // for view inflation
        private LayoutInflater inflater;

        public CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDays) {

            super(context, R.layout.control_calendar_day, days);
            this.eventDays = eventDays;
            //Log.d(LOGTAG, " aDEPTOR tURE");

            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            // day in question
            Date date = getItem(position);
            int day = date.getDate();
            int month = date.getMonth();
            int year = date.getYear();
            int weekday = date.getDay();
            int cMonth = currentDate.get(Calendar.MONTH);
            int cDay = currentDate.get(Calendar.DATE);
            int cYear = currentDate.get(Calendar.YEAR);
            eventDaysKeep = eventDays;

            // today
            Date today = new Date();
            boolean todayEvent = false;

            // inflate item if it does not exist yet
            if (view == null)
                view = inflater.inflate(R.layout.control_calendar_day, parent, false);

            // if this day has an event, specify event image
            view.setBackgroundResource(0);
            if (eventDaysKeep != null) {
                for (Date eventDate : eventDaysKeep) {

                    if (eventDate.getDate() == day &&
                            eventDate.getMonth() == month &&
                            eventDate.getYear() == year &&
                            !(eventDate.getYear() == today.getYear() &&
                                    eventDate.getMonth() == today.getMonth() &&
                                    eventDate.getDate() == today.getDate())) {
                        // mark this day for event
                        if ((!(date.getMonth() == cMonth)))
                            view.setBackgroundResource(R.drawable.reminderout);
                        else
                            view.setBackgroundResource(R.drawable.reminder);

                    } else if ((eventDate.getDate() == day &&    // if  is is today and with event, mark it will have special backgroud.
                            eventDate.getMonth() == month &&
                            eventDate.getYear() == year) &&
                            (eventDate.getYear() == today.getYear() &&
                                    eventDate.getMonth() == today.getMonth()
                                    && eventDate.getDate() == today.getDate())) {
                        view.setBackgroundResource(R.drawable.eventtoday);
                        todayEvent = true;
                    }
                }
            }

            // clear styling
            ((TextView) view).setTypeface(null, Typeface.NORMAL);
            ((TextView) view).setTextColor(getResources().getColor(R.color.blacked_out));

            if (weekday == 0 && !(month == today.getMonth() && date.getYear() == today.getYear() && day == today.getDate())) {
                // if this day is a holiday, red it out
                ((TextView) view).setTypeface(null, Typeface.NORMAL);
                ((TextView) view).setTextColor(getResources().getColor(R.color.reded_out));
            }
            if (!(date.getMonth() == cMonth)) {
                // if this day is outside current month, grey it out
                ((TextView) view).setTextColor(getResources().getColor(R.color.greyed_out));
                ((TextView) view).setTypeface(null, Typeface.NORMAL);


            }

            //else if (date.getDate() == today.getDate()&& date.getMonth() == today.getMonth()&& date.getYear() == today.getYear())
            else if (date.getDate() == today.getDate() && date.getMonth() == today.getMonth() && date.getYear() == today.getYear()) {// if it is today, set it to blue/bold
                ((TextView) view).setTypeface(null, Typeface.BOLD);
                ((TextView) view).setTextColor(getResources().getColor(R.color.today));
                if (todayEvent == false)
                    view.setBackgroundResource(R.drawable.today);
            }

            if (SchoolEventDaysKeep != null)// mark day for school special event day
            {
                for (Date SchoolEventDate : SchoolEventDaysKeep) {
                    if (SchoolEventDate.getDate() == day &&
                            SchoolEventDate.getMonth() == month &&
                            SchoolEventDate.getYear() == year &&
                            !(SchoolEventDate.getYear() == today.getYear() &&
                                    SchoolEventDate.getMonth() == today.getMonth() &&
                                    SchoolEventDate.getDate() == today.getDate())) {
                        // mark this day for event
                        if ((date.getMonth() == cMonth)) {
                            /*if (SchoolEventDate.getDate() == today.getDate()
                                    && SchoolEventDate.getMonth() == today.getMonth()
									&& SchoolEventDate.getYear() == today.getYear()
									&& todayEvent == true)
								view.setBackgroundResource(R.drawable.eventtodayreminder);
							else if (SchoolEventDate.getDate()== today.getDate()
									&& SchoolEventDate.getMonth() == today.getMonth()
									&& SchoolEventDate.getYear() == today.getYear()
									&& todayEvent == false)
								view.setBackgroundResource(R.drawable.eventtoday);
							else*/
                            {
                                ((TextView) view).setTypeface(null, Typeface.BOLD);
                                view.setBackgroundResource(R.drawable.event);
                            }

                        }

                    }

                }
            }


            if (HolidayDaysKeep != null)// mark day for school holiday
            {
                for (Date HolidayDate : HolidayDaysKeep) {
                    if (HolidayDate.getDate() == day &&
                            HolidayDate.getMonth() == month &&
                            HolidayDate.getYear() == year &&
                            !(HolidayDate.getYear() == today.getYear() &&
                                    HolidayDate.getMonth() == today.getMonth() &&
                                    HolidayDate.getDate() == today.getDate())) {
                        // mark this day for event
                        if ((date.getMonth() == cMonth)) {
                            ((TextView) view).setTypeface(null, Typeface.BOLD);
                            ((TextView) view).setTextColor(getResources().getColor(R.color.reded_out));
                        }

                    }

                }
            }


            // set text
            ((TextView) view).setText(String.valueOf(date.getDate()));

            return view;
        }
    }

    /**
     * Assign event handler to be passed needed events
     */
    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    /**
     * This interface defines what events to be reported to
     * the outside world
     */
    public interface EventHandler {
        void onDayLongPress(Date date);

        //void OnDayPress( Date date);
    }


}
