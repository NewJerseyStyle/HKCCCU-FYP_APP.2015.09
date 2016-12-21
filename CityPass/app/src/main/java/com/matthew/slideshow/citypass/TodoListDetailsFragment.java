package com.matthew.slideshow.citypass;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by yeung on 2/5/2016.
 */
public class TodoListDetailsFragment extends Fragment {
    private View rootView;
    private TextView tName;
    private TextView tDate;
    private TextView tTime1;
    private TextView tTime2;
    private TextView tLocation;
    private Button backButton;
    private Long id;
    private int backTrack;
    private Event event;
    private EventRW eventRW = null;

    private FragmentManager fragmentManager;


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


        rootView = inflater.inflate(R.layout.fragment_todolist_details, container, false);
        tName = (TextView) rootView.findViewById(R.id.textView_name);
        tDate = (TextView) rootView.findViewById(R.id.textView_date);
        tTime1 = (TextView) rootView.findViewById(R.id.textView_time1);
        tTime2 = (TextView) rootView.findViewById(R.id.textView_time2);
        tLocation = (TextView) rootView.findViewById(R.id.textView_location);
        backButton = (Button) rootView.findViewById(R.id.button_back);

        fragmentManager = getActivity().getSupportFragmentManager();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (backTrack == 1) {
                    HomePageFragment homePageFragment = new HomePageFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("Primary", 2);
                    homePageFragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.container, homePageFragment).commit();
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String time = event.getDate();
                    Date date;
                    try {
                        date = sdf.parse(time);
                        List<Event> events = eventRW.getAllByDate(date);
                        CalendarEventListFragment celf = CalendarEventListFragment.newInstance(events);
                        fragmentManager.beginTransaction().replace(R.id.container, celf).commit();
                    } catch (Exception e) {
                        Log.e("Set Alarm exception", e.getMessage());
                    }


                }

            }
        });

        eventRW = new EventRW(getActivity().getApplicationContext());
        event = eventRW.get(id);

        tName.setText(event.getName());
        tDate.setText(event.getDate());
        tTime1.setText(event.getTime1());
        tTime2.setText(event.getTime2());
        tLocation.setText(event.getLocation());

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


        MenuItem item2 = menu.add(Menu.NONE, 1, 10, "hi");
        item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item2.setIcon(R.drawable.ic_edit);

        MenuItem item = menu.add(Menu.NONE, 0, 10, "hi");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setIcon(R.drawable.ic_delete);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                eventRW.delete(id);

                /*HomePageFragment homePageFragment = new HomePageFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("Primary", 2);
                homePageFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.container, homePageFragment).commit();*/

                if (backTrack == 1) {
                    HomePageFragment homePageFragment = new HomePageFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("Primary", 2);
                    homePageFragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.container, homePageFragment).commit();
                    Toast.makeText(getContext(), "The event has been deleted", Toast.LENGTH_SHORT).show();
                } else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new CalendarFragment())
                            .commit();
                    Toast.makeText(getContext(), "The event has been deleted", Toast.LENGTH_SHORT).show();
                }

                return true;
            case 1:
                TodoListDetailsEditFragment todoListDetailsEditFragment = new TodoListDetailsEditFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putLong("ID", id);
                bundle1.putInt("BackTrack", backTrack);
                todoListDetailsEditFragment.setArguments(bundle1);
                fragmentManager.beginTransaction().replace(R.id.container, todoListDetailsEditFragment).commit();
        }
        return super.onOptionsItemSelected(item);
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
