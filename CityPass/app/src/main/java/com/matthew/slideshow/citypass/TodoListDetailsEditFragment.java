package com.matthew.slideshow.citypass;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yeung on 2/5/2016.
 */
public class TodoListDetailsEditFragment extends Fragment {
    private Button bCancel;

    private Button bEdit;
    private EditText eName;
    private EditText eDate;
    private EditText eTime1;
    private EditText eTime2;
    private EditText eLocation;
    private TimePickerDialog timePickerDialog1;
    private TimePickerDialog timePickerDialog2;
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private RadioGroup radioGroup;
    private RadioButton radioButton1;
    private RadioButton radioButton2;


    private Long id;
    private int backTrack;
    private String name = null;
    private int mYear = -1;
    private int mMonth = -1;
    private int mDay = -1;
    private int mHour1 = -1;
    private int mMinute1 = -1;
    private int mHour2 = -1;
    private int mMinute2 = -1;
    private String location = null;
    private int notify = 0;

    private EventRW eventRW = null;
    private Event event;

    private FragmentManager fragmentManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.notification_bar_color));
        }


        View rootView = inflater.inflate(
                R.layout.fragment_todolist_details_edit, container, false);

        eventRW = new EventRW(getActivity().getApplicationContext());
        event = eventRW.get(id);
        fragmentManager = getActivity().getSupportFragmentManager();

        name = event.getName();
        mYear = event.getYear();
        mMonth = event.getMonth();
        mDay = event.getDay();
        mHour1 = event.getHour1();
        mHour2 = event.getHour2();
        mMinute1 = event.getMin1();
        mMinute2 = event.getMin2();
        location = event.getLocation();
        notify = event.getNotify();

        // Toast.makeText(getContext(), "getNotify(): " + Integer.toString(notify), Toast.LENGTH_SHORT).show();

        bCancel = (Button) rootView.findViewById(R.id.button_cancel);

        bEdit = (Button) rootView.findViewById(R.id.button_edit);
        eName = (EditText) rootView.findViewById(R.id.editText_name);
        eName.setText(name);
        eDate = (EditText) rootView.findViewById(R.id.textView_date);
        eDate.setText(event.getDate());
        eTime1 = (EditText) rootView.findViewById(R.id.textView_time1);
        eTime1.setText(event.getTime1());
        eTime2 = (EditText) rootView.findViewById(R.id.editText_time2);
        eTime2.setText(event.getTime2());
        eLocation = (EditText) rootView.findViewById(R.id.textView_location);
        eLocation.setText(location);
        calendar = Calendar.getInstance();
        radioGroup = (RadioGroup) rootView.findViewById(R.id.radiogroup1);
        radioButton1 = (RadioButton) rootView.findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) rootView.findViewById(R.id.radioButton2);

        eDate.setInputType(InputType.TYPE_NULL);
        eTime1.setInputType(InputType.TYPE_NULL);
        eTime2.setInputType(InputType.TYPE_NULL);

        if (notify == 0) {
            radioButton2.setChecked(true);
        } else if (notify == 1) {
            radioButton1.setChecked(true);
        }


        eName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final StringBuilder sb = new StringBuilder((s.length()));
                sb.append(s);
                name = sb.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        eLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final StringBuilder sb = new StringBuilder((s.length()));
                sb.append(s);
                location = sb.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                populateSetDate(year, month + 1, day);
            }

            public void populateSetDate(int year, int month, int day) {
                mYear = year;
                mMonth = month;
                mDay = day;
                eDate.setText(month + "/" + day + "/" + year);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

        timePickerDialog1 = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                mHour1 = h;
                mMinute1 = m;
                String _hour;
                String _min;

                if (h < 10)
                    _hour = "0" + h;
                else
                    _hour = Integer.toString(h);
                if (m < 10)
                    _min = "0" + m;
                else
                    _min = Integer.toString(m);

                if (mHour2 == -1 && mMinute2 == -1) {
                    eTime1.setText(_hour + ':' + _min);
                } else {
                    if (mHour1 < mHour2 || (mHour1 == mHour2 && mMinute1 < mMinute2)) {
                        eTime1.setText(_hour + ':' + _min);
                    } else {
                        eTime1.setText(_hour + ':' + _min);
                        eTime2.setText(" ");
                        mHour2 = -1;
                        mMinute2 = -1;
                    }
                }
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

        timePickerDialog2 = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                mHour2 = h;
                mMinute2 = m;
                String _hour;
                String _min;

                if (h < 10)
                    _hour = "0" + h;
                else
                    _hour = Integer.toString(h);
                if (m < 10)
                    _min = "0" + m;
                else
                    _min = Integer.toString(m);

                if ((mHour2 > mHour1) || (mHour2 == mHour1 && mMinute2 > mMinute1)) {

                    eTime2.setText(_hour + ':' + _min);
                } else {
                    Toast.makeText(getContext(), "Invalid Time, Please Try AGAIN", Toast.LENGTH_SHORT).show();
                }
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoListDetailsFragment todoListDetailsFragment = new TodoListDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("ID", id);
                bundle.putInt("BackTrack", backTrack);
                todoListDetailsFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.container, todoListDetailsFragment).commit();


            }
        });

        bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!("".equals(name))) {
                    if ((mYear != -1)) {
                        if ((mHour1 != -1)) {
                            if ((mHour2 != -1)) {
                                event.setName(name);
                                event.setYear(mYear);
                                event.setMonth(mMonth);
                                event.setDay(mDay);
                                event.setHour1(mHour1);
                                event.setHour2(mHour2);
                                event.setMin1(mMinute1);
                                event.setMin2(mMinute2);
                                event.setLocation(location);
                                event.setNotify(notify);

                                eventRW.update(event);
                                TodoListDetailsFragment todoListDetailsFragment = new TodoListDetailsFragment();
                                Bundle bundle = new Bundle();
                                bundle.putLong("ID", id);
                                bundle.putInt("BackTrack", backTrack);
                                todoListDetailsFragment.setArguments(bundle);
                                fragmentManager.beginTransaction().replace(R.id.container, todoListDetailsFragment).commit();
                                Toast.makeText(getContext(), "The event has been edited", Toast.LENGTH_SHORT).show();
                                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                                if (notify == 1) {
                                    setAlarm();

                                }
                            } else {
                                Toast.makeText(getContext(), "Time cannot be empty", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Time cannot be empty", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getContext(), "Date cannot be empty", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                }


            }
        });

        View.OnTouchListener calendarListener = new View.OnTouchListener() {
            // CalendarDialog calendarDialog = null;
            @Override
            public boolean onTouch(View v, MotionEvent m) {
                getActivity().showDialog(0);
                //   if(calendarDialog == null)
                //      calendarDialog = new CalendarDialog();
                //calendarDialog.show(getFragmentManager(), "DatePicker");
                datePickerDialog.show();
                return false;
            }
        };

        eDate.setOnTouchListener(calendarListener);


        eTime1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                timePickerDialog1.show();
                return false;
            }
        });

        eTime2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                timePickerDialog2.show();
                return false;
            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == radioButton1.getId()) {
                    notify = 1;
                    Toast.makeText(getContext(), "button1", Toast.LENGTH_SHORT).show();
                } else if (i == radioButton2.getId()) {
                    notify = 0;
                    Toast.makeText(getContext(), "button2", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }


    public void setAlarm() {
        //new AlarmManagerReceiver().setAlarm();

        AlarmManager aManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent aIntent = new Intent(getContext(), AlarmManagerReceiver.class);

        //PendingIntent.getBroadcast(Context context, int requestCode, Intent intent, int flags)
        PendingIntent pIntent = PendingIntent.getBroadcast(getContext(), 0, aIntent, 0);
        long currentTimeInMillis = SystemClock.elapsedRealtime();
        eventRW = new EventRW(getActivity().getApplicationContext());
        Event event = eventRW.getNotification();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = event.getDateTime();
        Date date;
        try {
            date = sdf.parse(time);
            long delay = date.getTime() - System.currentTimeMillis();
            aManager.cancel(pIntent);
            aManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, currentTimeInMillis + delay, pIntent);
        } catch (Exception e) {
            Log.e("Set Alarm exception", e.getMessage());
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        id = getArguments().getLong("ID");
        backTrack = getArguments().getInt("BackTrack");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (eventRW != null)
            eventRW.close();
    }

}
