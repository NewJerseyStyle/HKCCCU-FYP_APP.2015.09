package com.matthew.slideshow.citypass;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yeung on 2/10/2016.
 */
public class MyFinancialFragment extends Fragment {
    private View rootView;
    private TextView dueDate;
    private TextView releaseDate;
    private TextView balance;
    private String dueDateData;
    private String releaseDateData;
    private String balanceData;


    private net network;


    private LoginRW loginRW = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.notification_bar_color));
        }


        rootView = inflater.inflate(R.layout.fragment_my_financial, container, false);

        releaseDate = (TextView) rootView.findViewById(R.id.releaseDate);
        dueDate = (TextView) rootView.findViewById(R.id.dueDate);
        balance = (TextView) rootView.findViewById(R.id.balance);

        try {
            network = new net(MyFinancialFragment.this.getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        new GetJSON().execute();

        return rootView;
    }

    class GetJSON extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);

            dialog.setIndeterminate(true);
            dialog.setIndeterminateDrawable(getActivity().getResources().getDrawable(R.drawable.my_spinner));
            dialog.setMessage("It's just loading....");
            dialog.setCancelable(false);


            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            String FINANCIALDATA = "financialData";
            String BALANCE = "Balance";

            String NAME = "name";
            String TRANSDATE = "TransDate";
            String DUEDATE = "DueDate";

            try {
                network.getMyFinance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //String temp_JSON = network.getResponseValue();
            try {
                if (network.getError()) {
                    throw new JSONException("Error occur in network component.");
                }

                JSONObject j1 = network.getResponse();
                JSONArray jArr = j1.getJSONArray(FINANCIALDATA);

                // get the current Plant JSON object.
                JSONObject j2 = jArr.getJSONObject(0);

                //String name = j2.getString(NAME);
                releaseDateData = j2.getString(TRANSDATE);
                dueDateData = j2.getString(DUEDATE);

                JSONObject j3 = jArr.getJSONObject(jArr.length() - 1);
                balanceData = j3.getString(BALANCE);




            /*JSONObject jsonFinancial = financialArray.getJSONObject(0);
            // get the data from JSON, save into Java.

            balanceData = jsonFinancial.getString("balance");
            dueDateData = jsonFinancial.getString("dueDate");
            releaseDateData = jsonFinancial.getString("releaseDate");*/

            } catch (JSONException e) {
                Log.d("json", "sqlite    " + "Some fucking crazy thing happen2");
                throw new RuntimeException(e);
            }


            if (balanceData.equals("0.00")) {
                dueDateData = "N/A";
                releaseDateData = "N/A";

            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            releaseDate.setText(releaseDateData);
            dueDate.setText(dueDateData);
            balance.setText(balanceData);
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loginRW != null)
            loginRW.close();
    }

   /* private Runnable getJson = new Runnable()
    {
        @Override
        public void run()
        {
            String rawFinancial="\n" +
                    "{  \n" +
                    "   \"financialData\":[  \n" +
                    "      {  \n" +
                    "         \"releaseDate\":\"26-JAN-2016\",\n" +
                    "         \"dueDate\":\"23-FEB-2016\",\n" +
                    "         \"balance\":\"22,500.00\"\n" +
                    "      }]\n" +
                    "}";

            try
            {
                JSONObject jsonObject = new JSONObject(rawFinancial);
                JSONArray financialArray = jsonObject.getJSONArray("financialData");
                    // get the current Plant JSON object.
                    JSONObject jsonFinancial = financialArray.getJSONObject(0);
                    // get the data from JSON, save into Java.

                    balanceData = jsonFinancial.getString("balance");
                    dueDateData = jsonFinancial.getString("dueDate");
                    releaseDateData = jsonFinancial.getString("releaseDate");

            }

            catch (JSONException e)
            {
                Log.d("json", "sqlite    " + "Some fucking crazy thing happen2");
                throw new RuntimeException(e);
            }


        }
    };
    @Override

    public void onDestroy(){

        super.onDestroy();

        if(getFinancialDataThreadHandler != null){

            getFinancialDataThreadHandler.removeCallbacks(getJson);

        }

        if(getFinancialDataThread != null){

            getFinancialDataThread.quit();

        }

    }
    */
}








