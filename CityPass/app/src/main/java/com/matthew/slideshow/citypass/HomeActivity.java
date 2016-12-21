package com.matthew.slideshow.citypass;

/**
 * Created by matthew on 2/1/2016.
 */

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

public class HomeActivity extends AppCompatActivity {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private ActionBar actionBar;
    private FragmentManager fragmentManager;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        LoginRW loginRW = new LoginRW(getApplicationContext());
        Log.d("user name", loginRW.getLoginName());
        Log.d("user password", loginRW.getLoginPassword());
        fragmentManager = getSupportFragmentManager();
        mNavigationDrawerFragment = (NavigationDrawerFragment) fragmentManager.findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        HomePageFragment homePageFragment = new HomePageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("Primary", 0);
        homePageFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.container, homePageFragment)
                .commit();
        SettingsRW settingsRW = new SettingsRW(getApplicationContext());
        if (settingsRW.getCountOfAlerm() < 1) {
            settingsRW.insertAlerm(0, 1);
            settingsRW.insertAlerm(1, 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void restoreActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
    }


    @Override
    public void onBackPressed() {
        // do nothing.
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        /*Toast.makeText(HomeActivity.this, "HomeActivity onRestart", Toast.LENGTH_SHORT).show();*/
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d("on destory", "main");
        SQLiteDatabase db = TodoListSQLite.getDatabase(getApplicationContext());
        db.close();
    }


}