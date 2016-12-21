package com.matthew.slideshow.citypass;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Created by yeung on 26/1/2016.
 */

public class TodoListFragment extends Fragment {
    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;

    List<String> list_header;
    HashMap<String, List<List<String>>> list_child;
    List<Event> events;
    List<Event> midterm;
    List<Event> labs;
    EventRW eventRW = null;
    CourseRW courseRW = null;
    FragmentManager fragmentManager;


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


        View view = inflater.inflate(R.layout.fragment_todolist, container, false);

        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int prevGroup = -1;

            @Override
            public void onGroupExpand(int i) {

                if (i != prevGroup) {
                    expandableListView.collapseGroup(prevGroup);
                    prevGroup = i;
                } else {
                    expandableListView.collapseGroup(prevGroup);
                    prevGroup = -1;
                }
                expandableListView.setSelectedChild(i, 0, true);
            }
        });

/*
* *******************************************************************************************************************
* Changable: 1. change the listener(s)
*
*            2. initialize the data (refer to void initializeListData() method)
*
*            3. better to define a custom Adapter
*               (go to JAVA file "ExpandableListAdapter" as a reference)
* */
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                expandableListView.expandGroup(groupPosition);
                return true;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (groupPosition == 4 && events != null) {
                    TodoListDetailsFragment todoListDetailsFragment = new TodoListDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putLong("ID", events.get(childPosition).getId());
                    bundle.putInt("BackTrack", 1);
                    todoListDetailsFragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.container, todoListDetailsFragment).addToBackStack(null).commit();
                } else if (groupPosition == 3 && midterm != null) {
                    TodoListDetailsFragment todoListDetailsFragment = new TodoListDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putLong("ID", midterm.get(childPosition).getId());
                    bundle.putInt("BackTrack", 1);
                    todoListDetailsFragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.container, todoListDetailsFragment).addToBackStack(null).commit();
                } else if (groupPosition == 2 && labs != null) {
                    TodoListDetailsFragment todoListDetailsFragment = new TodoListDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putLong("ID", labs.get(childPosition).getId());
                    bundle.putInt("BackTrack", 1);
                    todoListDetailsFragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.container, todoListDetailsFragment).addToBackStack(null).commit();
                }
                return true;
            }
        });
        new InitializeData().execute();
//end of Changableeeeeeeeeee
//*******************************************************************************************************************
        return view;
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
                bundle.putInt("BackTrack", 1);
                tlaf.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.container, tlaf).commit();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        fragmentManager = getActivity().getSupportFragmentManager();
    }


    class InitializeData extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* pDialog = new ProgressDialog(getContext());
            pDialog.setIndeterminate(true);
            pDialog.setIndeterminateDrawable(getContext().getResources().getDrawable(R.drawable.my_spinner));
            pDialog.setMessage("Deleting Record...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();*/
        }

        @Override
        protected String doInBackground(String... args) {
            courseRW = new CourseRW(getActivity().getApplicationContext());
            list_header = new ArrayList<String>();
            list_child = new HashMap<String, List<List<String>>>();

            list_header.add("Today Lessons");
            list_header.add("Tomorrow Lessons");
            list_header.add("Assignment / Lab deadline");
            list_header.add("Mid-term / Exam");
            list_header.add("Others");

            List<List<String>> header1 = new ArrayList<List<String>>();
            List<List<String>> header2 = new ArrayList<List<String>>();
            List<List<String>> header3 = new ArrayList<List<String>>();
            List<List<String>> header4 = new ArrayList<List<String>>();
            List<List<String>> header5 = new ArrayList<List<String>>();


            List<String> child2 = new ArrayList<>();

            List<Course.Lesson> list = courseRW.getDaysLesson(0);
            if (list != null) {
                for (int a = 0; a < list.size(); a++) {
                    List<String> child1 = new ArrayList<>();
                    String course_name = courseRW.getCourseName(list.get(a).getLesson_course_id());
                    child1.add(course_name);
                    Calendar calendar = Calendar.getInstance();
                    Date date = calendar.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String str = sdf.format(date);
                    child1.add(str);
                    child1.add(list.get(a).getTime());
                    String[] arr = list.get(a).getLocation().split("\n");
                    String location = "";
                    for (int b = 0; b < arr.length; b++)
                        location += arr[b];

                    child1.add(location);
                    header1.add(child1);
                }
            } else {
                List<String> child1 = new ArrayList<>();
                child1.add("No Lesson");
                child1.add("");
                child1.add("");
                child1.add("");
                header1.add(child1);
            }

            list = courseRW.getDaysLesson(1);
            if (list != null) {
                for (int a = 0; a < list.size(); a++) {
                    List<String> child1 = new ArrayList<>();
                    String course_name = courseRW.getCourseName(list.get(a).getLesson_course_id());
                    child1.add(course_name);
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, 1);
                    Date date = calendar.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String str = sdf.format(date);
                    child1.add(str);
                    child1.add(list.get(a).getTime());
                    child1.add(list.get(a).getLocation());
                    header2.add(child1);
                }
            } else {
                List<String> child1 = new ArrayList<>();
                child1.add("No Lesson");
                child1.add("");
                child1.add("");
                child1.add("");
                header2.add(child1);
            }

            eventRW = new EventRW(getActivity().getApplicationContext());
            events = eventRW.getAllEvents();


            if (events != null) {
                for (int a = 0; a < events.size(); a++) {
                    List<String> child_content = new ArrayList<String>();
                    child_content.add(events.get(a).getName());
                    child_content.add(events.get(a).getDate());
                    child_content.add(events.get(a).getTime1() + "-" + events.get(a).getTime2());
                    child_content.add(events.get(a).getLocation());
                    header5.add(child_content);
                }
            } else {
                List<String> child_content = new ArrayList<>();
                child_content.add("No Event");
                child_content.add("");
                child_content.add("");
                child_content.add("");
                header5.add(child_content);
            }

            midterm = eventRW.getAllMidTerm();
            if (midterm != null) {
                for (int a = 0; a < midterm.size(); a++) {
                    List<String> child_content = new ArrayList<String>();
                    child_content.add(midterm.get(a).getName());
                    child_content.add(midterm.get(a).getDate());
                    child_content.add(midterm.get(a).getTime1() + "-" + midterm.get(a).getTime2());
                    child_content.add(midterm.get(a).getLocation());
                    header4.add(child_content);
                }
            } else {
                List<String> child_content = new ArrayList<>();
                child_content.add("No Midterm");
                child_content.add("");
                child_content.add("");
                child_content.add("");
                header4.add(child_content);
            }


            labs = eventRW.getAllLab();
            if (labs != null) {
                for (int a = 0; a < labs.size(); a++) {
                    List<String> child_content = new ArrayList<String>();
                    child_content.add(labs.get(a).getName());
                    child_content.add(labs.get(a).getDate());
                    child_content.add(labs.get(a).getTime1() + "-" + labs.get(a).getTime2());
                    child_content.add(labs.get(a).getLocation());
                    header3.add(child_content);
                }
            } else {
                List<String> child_content = new ArrayList<>();
                child_content.add("No Labs");
                child_content.add("");
                child_content.add("");
                child_content.add("");
                header3.add(child_content);
            }


            list_child.put(list_header.get(0), header1);
            list_child.put(list_header.get(1), header2);
            list_child.put(list_header.get(2), header3);
            list_child.put(list_header.get(3), header4);
            list_child.put(list_header.get(4), header5);
            return null;
        }

        @Override
        protected void onPostExecute(String a) {
            expandableListAdapter = new ExpandableListAdapter(getContext(), list_header, list_child);
            expandableListView.setAdapter(expandableListAdapter);
            // pDialog.dismiss();
        }
    }


    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private List<String> list_header;
        private HashMap<String, List<List<String>>> list_child;

        public ExpandableListAdapter(Context context, List<String> list_header
                , HashMap<String, List<List<String>>> list_child) {
            this.context = context;
            this.list_header = list_header;
            this.list_child = list_child;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return this.list_child.get(this.list_header.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View converView, ViewGroup parent) {
            final List<String> childContent = (List) getChild(groupPosition, childPosition);
            if (converView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            /*
            ********************************************************************************************************************
            * Changable: change the child row (item layout)
            *
            * */
                converView = layoutInflater.inflate(R.layout.row_child_expandable_listview, null);
            }
            TextView tName = (TextView) converView.findViewById(R.id.textView_name);
            TextView tDate = (TextView) converView.findViewById(R.id.textView_date);
            TextView tTime = (TextView) converView.findViewById(R.id.textView_time);
            TextView tLocation = (TextView) converView.findViewById(R.id.textView_location);

            tName.setText(childContent.get(0));
            tDate.setText(childContent.get(1));
            tTime.setText(childContent.get(2));
            tLocation.setText(childContent.get(3));
            //TextView text2 = (TextView) converView.findViewById(R.id.child_textview2);
            //TextView text3 = (TextView) converView.findViewById(R.id.child_textview3);

            //text.setText(childText);
            //end of Changable
            //*******************************************************************************************************************

            return converView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.list_child.get(this.list_header.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.list_header.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this.list_header.size();
        }

        //???
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View converView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (converView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            /*
            * *******************************************************************************************************************
            * Changable: change the header row (item layout)
            *
            * */
                converView = layoutInflater.inflate(R.layout.row_header_expandable_listview, null);
                //end of Changable
                //*******************************************************************************************************************
            }
        /*
        * *******************************************************************************************************************
        * Changable: change the backgroud dynamically
        *
        * */
            View child = converView.findViewById(R.id.header_textview);
            switch (groupPosition % 6) {
                case 0:
                    child.setBackgroundColor(Color.rgb(242, 136, 115));
                    break;
                case 1:
                    child.setBackgroundColor(Color.rgb(170, 204, 126));
                    break;
                case 2:
                    child.setBackgroundColor(Color.rgb(179, 152, 227));
                    break;
                case 3:
                    child.setBackgroundColor(Color.rgb(242, 124, 170));
                    break;
                case 4:
                    child.setBackgroundColor(Color.rgb(115, 172, 233));
                    break;
                case 5:
                    child.setBackgroundColor(Color.rgb(139, 139, 143));
                    break;
            }
            TextView textViewHeader = (TextView) converView.findViewById(R.id.header_textview);
            textViewHeader.setTypeface(null, Typeface.BOLD);
            textViewHeader.setText(headerTitle);
            //end of Changable
            //*******************************************************************************************************************

            return converView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (eventRW != null)
            eventRW.close();
        if (courseRW != null)
            courseRW.close();
    }
}