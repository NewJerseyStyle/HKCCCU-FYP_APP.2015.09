package com.matthew.slideshow.citypass;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by matthew on 20/3/2016.
 */
public class CalendarEventListFragment extends Fragment {
    private View rootView;
    private ListView listView;
    private ImageButton imageButton;
    private FragmentManager fragmentManager;
    private List<Event> events;

    public static CalendarEventListFragment newInstance(List<Event> events) {
        CalendarEventListFragment celf = new CalendarEventListFragment();
        celf.setEvents(events);
        return celf;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.notification_bar_color));
        }


        rootView = inflater.inflate(R.layout.fragment_calendar_events_list, container, false);

        fragmentManager = getActivity().getSupportFragmentManager();
        listView = (ListView) rootView.findViewById(R.id.holiday_list);
        imageButton = (ImageButton) rootView.findViewById(R.id.button_back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new CalendarFragment())
                        .commit();
            }
        });

        CalendarEventListAdapter adapter = new CalendarEventListAdapter(getContext(), R.layout.row_cardview_event_items, events);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putLong("ID", events.get(position).getId());
                bundle.putInt("BackTrack", 2);
                TodoListDetailsFragment tldf = new TodoListDetailsFragment();
                tldf.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.container, tldf).commit();
            }
        });
        return rootView;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }


    public class CalendarEventListAdapter extends ArrayAdapter<Event> {
        private List<Event> events;
        private int layoutResourceId;


        public CalendarEventListAdapter(Context context, int layoutResourceId, List<Event> events) {
            super(context, layoutResourceId, events);
            this.layoutResourceId = layoutResourceId;
            this.events = events;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = ((AppCompatActivity) getActivity()).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }


            CardView wrap = (CardView) convertView.findViewById(R.id.wrapper);
            TextView tName = (TextView) convertView.findViewById(R.id.textView_name);

            TextView tTime = (TextView) convertView.findViewById(R.id.textView_time);
            TextView tLocation = (TextView) convertView.findViewById(R.id.textView_location);

            tName.setText(events.get(position).getName());
            tTime.setText(events.get(position).getTime1() + "-" + events.get(position).getTime2());
            tLocation.setText(events.get(position).getLocation());


            return convertView;

        }
    }
}
