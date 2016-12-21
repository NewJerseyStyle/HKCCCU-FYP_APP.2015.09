package com.matthew.slideshow.citypass;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerExpandableListView;
    private View mFragmentContainerView;
    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private CharSequence mTitle;
    private ActionBar actionBar;


    public NavigationDrawerFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
        mTitle = getString(R.string.app_name);
        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Drawer ListView
        mDrawerExpandableListView = (ExpandableListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int prevGroup = -1;


            @Override
            public void onGroupExpand(int i) {
                if (i != prevGroup) {
                    mDrawerExpandableListView.collapseGroup(prevGroup);
                    prevGroup = i;
                } else {
                    mDrawerExpandableListView.collapseGroup(prevGroup);
                    prevGroup = -1;
                }
            }
        });

        mDrawerExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                FragmentManager fragmentManager = getFragmentManager();

                switch (groupPosition) {
                    case 0:
                        HomePageFragment homePageFragment = new HomePageFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("Primary", 0);
                        homePageFragment.setArguments(bundle);
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, homePageFragment)
                                .commit();
                        selectItem(groupPosition);
                        break;
                    case 1:
                        break;

                    case 3:
                        /*
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, new AnnouncementFragment())
                                .commit();
                        selectItem(groupPosition);
                        */
                        break;
                    case 4:
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, new CalendarFragment())
                                .commit();
                        selectItem(groupPosition);
                        break;
                    case 7:
                        fragmentManager.beginTransaction().replace(R.id.container, new SettingsFragment()).commit();
                        selectItem(groupPosition);
                        break;
                    case 8:
                        showLogOutDialog();

                        break;
                    default:
                        mDrawerExpandableListView.expandGroup(groupPosition);
                }
                onSectionAttached(groupPosition);
                actionBar.setTitle(mTitle);
                /*switch (groupPosition) {
                    case 0:
                        mTitle  = getString(R.string.title_home);
                        actionBar.setTitle(mTitle);
                        break;
                    case 1:
                        mTitle = getString(R.string.title_Printing);
                        actionBar.setTitle(mTitle);
                        break;
                    case 2:
                        //mTitle = getString(R.string.title_student_records);
                        break;
                    case 3:
                        mTitle = getString(R.string.title_announcements);
                        actionBar.setTitle(mTitle);
                        break;
                    case 4:
                        mTitle = getString(R.string.title_my_calendar);
                        actionBar.setTitle(mTitle);
                        break;
                    case 5:
                        //mTitle = getString(R.string.title_timetable);
                        break;
                    case 6:
                        //mTitle = getString(R.string.title_useful_info);
                        break;
                    case 7:
                        mTitle = getString(R.string.title_settings);
                        actionBar.setTitle(mTitle);
                        break;
                }*/
                return true;
            }
        });

        mDrawerExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                FragmentManager fragmentManager = getFragmentManager();
                if (groupPosition == 5) {
                    switch (childPosition) {
                        case 0:
                            TimetableFragment timetableFragment = new TimetableFragment();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("HomeFragment", false);
                            timetableFragment.setArguments(bundle);
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, timetableFragment)
                                    .commit();
                            onSectionAttached(groupPosition);
                            actionBar.setTitle(mTitle);
                            selectItem(groupPosition);
                            break;
                        case 1:
                            ExamTimetableFragment examTimetableFragment = new ExamTimetableFragment();
                            fragmentManager.beginTransaction().replace(R.id.container, examTimetableFragment).commit();
                            onSectionAttached(groupPosition);
                            actionBar.setTitle(mTitle);
                            selectItem(groupPosition);
                            break;

                    }
                } else if (groupPosition == 2) {
                    switch (childPosition) {
                        case 0:
                            StudentDetailFragment studentDetailFragment = new StudentDetailFragment();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, studentDetailFragment)
                                    .commit();
                            onSectionAttached(groupPosition);
                            actionBar.setTitle(mTitle);
                            selectItem(groupPosition);
                            break;
                        case 1:
                            GradeReportFragment gradeReportFragment = new GradeReportFragment();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, gradeReportFragment)
                                    .commit();
                            onSectionAttached(groupPosition);
                            actionBar.setTitle(mTitle);
                            selectItem(groupPosition);
                            break;
                        case 2:
                            MyFinancialFragment myFinancialFragment = new MyFinancialFragment();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, myFinancialFragment)
                                    .commit();
                            //mTitle = getString(R.string.title_student_records);
                            onSectionAttached(groupPosition);
                            actionBar.setTitle(mTitle);
                            selectItem(groupPosition);
                            break;
                    }
                } else if (groupPosition == 6) {
                    switch (childPosition) {
                        case 0:
                            UsefulPhoneNumberFragment usefulPhoneNumberFragment = new UsefulPhoneNumberFragment();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, usefulPhoneNumberFragment)
                                    .commit();
                            //mTitle = getString(R.string.title_useful_info);
                            onSectionAttached(groupPosition);
                            actionBar.setTitle(mTitle);
                            selectItem(groupPosition);
                            break;
                        case 1:
                            FacilitiesChargesListsFragment facilitiesChargesListsFragment = new FacilitiesChargesListsFragment();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, facilitiesChargesListsFragment)
                                    .commit();
                            //mTitle = getString(R.string.title_useful_info);
                            onSectionAttached(groupPosition);
                            actionBar.setTitle(mTitle);
                            selectItem(groupPosition);
                            break;
                        case 2:
                            FacilitiesOpenTimeListsFragment facilitiesOpenTimeFragment = new FacilitiesOpenTimeListsFragment();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, facilitiesOpenTimeFragment)
                                    .commit();
                            //mTitle = getString(R.string.title_useful_info);
                            onSectionAttached(groupPosition);
                            actionBar.setTitle(mTitle);
                            selectItem(groupPosition);
                    }

                } else {
                    Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

        List<String> list_header = new ArrayList<String>();
        HashMap list_child = new HashMap<String, List<String>>();
        list_header.add(getString(R.string.title_home));
        list_header.add(getString(R.string.title_Printing));
        list_header.add(getString(R.string.title_student_records));
        list_header.add(getString(R.string.title_announcements));
        list_header.add(getString(R.string.title_my_calendar));
        list_header.add(getString(R.string.title_timetable));
        list_header.add(getString(R.string.title_useful_info));
        list_header.add(getString(R.string.title_settings));
        list_header.add(getString(R.string.title_logout));

        List<String> tHome = new ArrayList<String>();
        List<String> tPrinting = new ArrayList<String>();

        //index: 2
        List<String> tStudentRecords = new ArrayList<String>();
        tStudentRecords.add("Student Details");
        tStudentRecords.add("Grade Report");
        tStudentRecords.add("Financial Summary");

        List<String> tAnnouncement = new ArrayList<String>();
        List<String> tMyCalendar = new ArrayList<String>();

        //index: 5
        List<String> tTimetables = new ArrayList<String>();
        tTimetables.add("Weekly Timetable");
        tTimetables.add("Examination TimeTable");


        //index: 6
        List<String> tUsefulInfo = new ArrayList<String>();
        tUsefulInfo.add("Useful Phone Numbers");
        tUsefulInfo.add("Facilities Charges Lists");
        tUsefulInfo.add("Facilities Opening Hours");

        List<String> tSettings = new ArrayList<String>();
        List<String> tLogout = new ArrayList<String>();

        //    List<String> header4 = new ArrayList<String>();


        list_child.put(list_header.get(0), tHome);
        list_child.put(list_header.get(1), tPrinting);
        list_child.put(list_header.get(2), tStudentRecords);
        list_child.put(list_header.get(3), tAnnouncement);
        list_child.put(list_header.get(4), tMyCalendar);
        list_child.put(list_header.get(5), tTimetables);
        list_child.put(list_header.get(6), tUsefulInfo);
        list_child.put(list_header.get(7), tSettings);
        list_child.put(list_header.get(8), tLogout);

        DrawerManuAdapter drawerManuAdapter = new DrawerManuAdapter(getActionBar().getThemedContext(), list_header, list_child);
        mDrawerExpandableListView.setAdapter(drawerManuAdapter);
        mDrawerExpandableListView.setItemChecked(mCurrentSelectedPosition, true);
        return mDrawerExpandableListView;
    }


    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_drawer);
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                false,
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {


            @Override
            public void onDrawerClosed(View drawerView) {
                actionBar.setTitle(mTitle);
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }
                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }


            @Override
            public void onDrawerOpened(View drawerView) {
                actionBar.setTitle(R.string.app_name);
                super.onDrawerOpened(drawerView);

                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }
                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(getContext(), "mDrawerLayout.post", Toast.LENGTH_SHORT).show();
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


    private void selectItem(int position) {
        mCurrentSelectedPosition = position;

        if (mDrawerExpandableListView != null) {
            mDrawerExpandableListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }


    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.title_home);
                break;
            case 1:
                mTitle = getString(R.string.title_Printing);
                break;
            case 2:
                mTitle = getString(R.string.title_student_records);
                break;
            case 3:
                mTitle = getString(R.string.title_announcements);
                break;
            case 4:
                mTitle = getString(R.string.title_my_calendar);
                break;
            case 5:
                mTitle = getString(R.string.title_timetable);
                break;
            case 6:
                mTitle = getString(R.string.title_useful_info);
                break;
            case 7:
                mTitle = getString(R.string.title_settings);
                break;
            case 8:
                //mTitle = getString(R.string.title_logout);
                break;
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        //actionBar.setTitle(mTitle);
        //actionBar.setTitle(R.string.app_name);
    }


    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    public void showLogOutDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Notice")
                .setMessage("Are you sure to Logout?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new Logout().execute();
                    }
                })
                .setNegativeButton("Cencel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
    }


    class Logout extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity());

            dialog.setIndeterminate(true);
            dialog.setIndeterminateDrawable(getActivity().getResources().getDrawable(R.drawable.my_spinner));
            dialog.setMessage("Loading...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
            LoginRW loginRW = new LoginRW(getActivity().getApplicationContext());
            loginRW.logout();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {


            if (dialog.isShowing())
                dialog.dismiss();

            Toast.makeText(getContext(), "Thank You For Using City Pass", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    //public static interface NavigationDrawerCallbacks {

    /**
     * Called when an item in the navigation drawer is selected.
     */
    //  void onNavigationDrawerItemSelected(int position);
    //}


    private static class DrawerManuAdapter extends BaseExpandableListAdapter {
        private Context context;
        private List<String> list_header;
        private HashMap<String, List<String>> list_child;


        public DrawerManuAdapter(Context context, List<String> list_header
                , HashMap<String, List<String>> list_child) {
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
            final String childText = (String) getChild(groupPosition, childPosition);

            if (converView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                converView = layoutInflater.inflate(R.layout.row_child_navigation_drawer, null);
            }
            TextView text = (TextView) converView.findViewById(R.id.child_textview);
            text.setText(childText);
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


        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }


        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View converView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);

            if (converView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                converView = layoutInflater.inflate(R.layout.row_header_navigation_drawer, null);
            }

            TextView textViewHeader = (TextView) converView.findViewById(R.id.header_textview);
            textViewHeader.setTypeface(null, Typeface.BOLD);
            textViewHeader.setText(headerTitle);

            ImageView iIcon = (ImageView) converView.findViewById(R.id.icon);
            int imageResource = 0;
            switch (groupPosition) {
                case 0: //Home
                    imageResource = R.drawable.ic_menu_0;
                    break;
                case 1: //printing
                    imageResource = R.drawable.ic_menu_1;
                    break;
                case 2: //student records
                    imageResource = R.drawable.ic_menu_2;
                    break;
                case 3: //announcements
                    imageResource = R.drawable.ic_menu_3;
                    break;
                case 4: //my calendar
                    imageResource = R.drawable.ic_menu_4;
                    break;
                case 5: //timetable
                    imageResource = R.drawable.ic_menu_5;
                    break;
                case 6: //useful information
                    imageResource = R.drawable.ic_menu_6;
                    break;
                case 7: //settings
                    imageResource = R.drawable.ic_menu_7;
                    break;
                case 8: //logout
                    imageResource = R.drawable.ic_menu_8;
                    break;
            }
            iIcon.setImageResource(imageResource);

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
}
