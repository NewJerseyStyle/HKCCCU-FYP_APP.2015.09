package com.matthew.slideshow.citypass;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matthew on 7/1/2016.
 */
public class HomePageFragment extends Fragment {
    private View view;
    private PagerAdapter mPagerAdapater;
    private ViewPager mViewPager;
    private android.support.v7.app.ActionBar mActionBar;
    private List<Fragment> fragments;
    private TimetableFragment timetableFragment;
    private TodoListFragment todoListFragment;
    private int primaryPage;
    private PrintingTabFragment printFragment;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (mViewPager != null) {
            // before screen rotation it's better to detach pagerAdapter from the ViewPager, so
            // pagerAdapter can remove all old fragments, so they're not reused after rotation.
            mViewPager.setAdapter(null);
        }
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
/*
* *******************************************************************************************************************
* Changable: change the layout that is going to inflate
*            and the id of the ViewPager
*
* */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.notification_bar_color));
        }


        view = inflater.inflate(R.layout.fragment_main, container, false);
        fragments = new ArrayList<Fragment>();

        timetableFragment = (TimetableFragment) new TimetableFragment();


        printFragment = new PrintingTabFragment();
        todoListFragment = (TodoListFragment) new TodoListFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean("HomeFragment", true);
        timetableFragment.setArguments(bundle);

        fragments.add(timetableFragment);

        fragments.add(printFragment);

        fragments.add(todoListFragment);

        mPagerAdapater = new PagerAdapter(getChildFragmentManager(), fragments);

        mViewPager = (ViewPager) view.findViewById(R.id.fragment_main_paper);
        mViewPager.setAdapter(mPagerAdapater);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(primaryPage);
            }
        }, 100);
//end of Changable
//*******************************************************************************************************************
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                setSelectedNavigationItem(position);
            }
        });

        return view;
    }


    public void setSelectedNavigationItem(int a) {
        if (mActionBar.getTabCount() > 0)
            mActionBar.setSelectedNavigationItem(a);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        setTabBar();
        super.onCreateOptionsMenu(menu, inflater);

    }


    public void setDrawerEnabled(boolean b) {
        mActionBar.setDisplayHomeAsUpEnabled(b);
    }


    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
        @Override
        public void onTabSelected(Tab tab, android.support.v4.app.FragmentTransaction ft) {
            mViewPager.setCurrentItem(tab.getPosition());
            timetableFragment.dismissPopUpWindow();
        }

        @Override
        public void onTabUnselected(Tab tab, android.support.v4.app.FragmentTransaction ft) {
        }

        @Override
        public void onTabReselected(Tab tab, android.support.v4.app.FragmentTransaction ft) {
        }
    };


    public boolean setTabBar() {
        mActionBar.setHomeButtonEnabled(false);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        if (mActionBar.getTabCount() > 0) {
            return false;
        }
/*
* *******************************************************************************************************************
* Changable: Change the tab name
*
* */
        for (int b = 0; b < 3; b++) {
            String str = null;
            switch (b) {
                case 0:
                    str = "TimeTable";
                    break;
                case 1:
                    str = "Printing";
                    break;
                case 2:
                    str = "To-do List";
                    break;
            }
            ActionBar.Tab tab = mActionBar.newTab().setText(str).setTabListener(tabListener);
            mActionBar.addTab(tab);
        }

//end of Changable
//*******************************************************************************************************************
        return true;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActionBar = ((AppCompatActivity) activity).getSupportActionBar();
        primaryPage = getArguments().getInt("Primary");
    }


    @Override
    public void onDestroy() {
        mActionBar.removeAllTabs();
        super.onDestroy();
    }


}


