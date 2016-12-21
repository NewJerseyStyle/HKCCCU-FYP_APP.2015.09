package com.matthew.slideshow.citypass;

/**
 * Created by matthew on 2/1/2016.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class MainActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    private boolean up = false;
    private LoginRW loginRW;
    private net login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login = new net(MainActivity.this);
        new TestNetwork().execute();
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.notification_bar_color));
        }
        try {
            getSupportActionBar().hide();
            loginRW = new LoginRW(getApplicationContext());
            final float imgPos = ((ImageView) findViewById(R.id.imageView)).getY();
            final Context context = MainActivity.this;
            ((RelativeLayout) findViewById(R.id.activity_main)).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int heightDiff = ((RelativeLayout) findViewById(R.id.activity_main)).getRootView().getHeight() - ((RelativeLayout) findViewById(R.id.activity_main)).getHeight();
                    if (heightDiff > 200) {
                        // if more than 100 pixels, its probably a keyboard......do something here
                        if (up) {
                            ((TextView) findViewById(R.id.textView2)).setVisibility(TextView.VISIBLE);
                            ((TextView) findViewById(R.id.textView3)).setVisibility(TextView.VISIBLE);
                            ((ImageView) findViewById(R.id.imageView)).setY(imgPos + 200);
                            ((TextView) findViewById(R.id.textView)).setY(imgPos + 450);
                            ((EditText) findViewById(R.id.EID)).setY(imgPos + 1120);
                            ((EditText) findViewById(R.id.password)).setY(imgPos + 1275);
                            ((Button) findViewById(R.id.login_button)).setY(imgPos + 1445);
                            up = false;
                        } else if (!up && ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).isAcceptingText()) {
                            // ((TextView) findViewById(R.id.textView)).setVisibility(TextView.INVISIBLE);
                            ((TextView) findViewById(R.id.textView2)).setVisibility(TextView.INVISIBLE);
                            ((TextView) findViewById(R.id.textView3)).setVisibility(TextView.INVISIBLE);
                            ((ImageView) findViewById(R.id.imageView)).setY(imgPos + 80);
                            ((TextView) findViewById(R.id.textView)).setY(imgPos + 300);
                            ((EditText) findViewById(R.id.EID)).setY(imgPos + 550);
                            ((EditText) findViewById(R.id.password)).setY(imgPos + 705);
                            ((Button) findViewById(R.id.login_button)).setY(imgPos + 875);
                            up = true;
                        }
                    }
                }
            });
        } catch (Exception ignored) {
        }
        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, 2016);
        c.set(Calendar.MONTH, 3 - 1);
        c.set(Calendar.DAY_OF_MONTH, 30);
        c.set(Calendar.HOUR_OF_DAY, 8);
        c.add(Calendar.HOUR_OF_DAY, 1);
        c.add(Calendar.MINUTE, 15);
        Log.d("Hour", Integer.toString(c.get(Calendar.HOUR_OF_DAY)));
        Log.d("MIN", Integer.toString(c.get(Calendar.MINUTE)));
        if (!isNetworkAvailable()) {
            // Make a note about the failed load.
            Log.d("Network", "Host unable to reach");
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Fail-與伺服器通訊失敗")
                    .setMessage("請確認電波訊號。")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
        if (loginRW.getCount() != 0) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void onclickLogin(View view) {
        Log.d("Login", "Clicked");

        if (!isNetworkAvailable()) {
            // cannot connect
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Fail-與伺服器通訊失敗")
                    .setMessage("請確認電波訊號。")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
            Log.d("Network", "Host unable to reach");
            return;
        }
        new LoadNetwork().execute();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onBackPressed() {
        finish();
        try {
            login.logout2AIMS();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class LoadNetwork extends AsyncTask<String, String, String> {
        private String login_name, login_password;
        private ProgressDialog progressDialog;
        private boolean logged;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this, "", "Loading...");
            progressDialog.setProgressStyle(R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(MainActivity.this.getResources().getDrawable(R.drawable.my_spinner));
            progressDialog.show();
            logged = false;
            //suppose need the user-typed EID and password
            login_name = ((EditText) findViewById(R.id.EID)).getEditableText().toString();
            login_password = ((EditText) findViewById(R.id.password)).getEditableText().toString();

            if (login_name.isEmpty() || login_password.isEmpty()) {
                // Please input the CityU EID and Password
                Log.d("Login", "Please input the CityU EID and Password");
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Notice")
                        .setMessage("Please input the CityU EID and Password")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                return;
            }
        }

        @Override
        protected String doInBackground(String... args) {
            if(login.login(login_name, login_password)){
                logged = true;
                loginRW.insert(login_name, login_password);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (logged) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Log.d("Login", "cannot login, wrong eid or password");
                // cannot login, wrong eid or password
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Notice")
                        .setMessage("Invalid EID or password")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        }
    }

    class TestNetwork extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;
        private AlertDialog ad;
        private boolean bool;

        @Override
        protected void onPreExecute() {
            if (MainActivity.this.isDestroyed()) {
                TestNetwork.this.cancel(true);
            }
            progressDialog = ProgressDialog.show(MainActivity.this, "", "Loading...");
            progressDialog.setProgressStyle(R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(MainActivity.this.getResources().getDrawable(R.drawable.my_spinner));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            bool = activeNetworkInfo != null && activeNetworkInfo.isConnected();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (!bool) {
                ad = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Fail-與伺服器通訊失敗")
                        .setMessage("請確認電波訊號。")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ;
                            }
                        })
                        .show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (ad.isShowing()) {
                            ad.dismiss();
                        }
                        new TestNetwork().execute();
                    }
                }, 2000);
            }
        }
    }

}
