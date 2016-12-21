package com.matthew.slideshow.citypass;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yeung on 2/6/2016.
 */
public class ExamTimetableFragment extends Fragment {
    private View rootView;
    private ListView listView;
    private ArrayList<ExamInfo> examInfos = new ArrayList<>();
    private net network = new net(ExamTimetableFragment.this.getContext());
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


        rootView = inflater.inflate(R.layout.fragment_exam_timtable, container, false);
        listView = (ListView) rootView.findViewById(R.id.holiday_list);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        //test();
        new entryJSONData().execute();


        return rootView;
    }

    public class ExamTimetableAdapter extends ArrayAdapter<ExamInfo> {
        ArrayList<ExamInfo> examInfos;
        int layoutResourceId;
        private TextView tCourseName;
        private TextView tDate;
        private TextView tTime;
        private TextView tPlace;
        private TextView tSeatNo;
        private RelativeLayout relativeLayout2;

        private int color;

        public ExamTimetableAdapter(Context context, int layoutResourceId, ArrayList<ExamInfo> examInfos) {
            super(context, layoutResourceId, examInfos);
            this.layoutResourceId = layoutResourceId;
            this.examInfos = examInfos;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rootView = convertView;
            if (rootView == null) {
                LayoutInflater inflater = ((AppCompatActivity) getActivity()).getLayoutInflater();
                rootView = inflater.inflate(layoutResourceId, parent, false);
            }

            tCourseName = (TextView) rootView.findViewById(R.id.textView_list_date);
            tDate = (TextView) rootView.findViewById(R.id.textView_date);
            tTime = (TextView) rootView.findViewById(R.id.textView_time);
            tPlace = (TextView) rootView.findViewById(R.id.textView_place);
            tSeatNo = (TextView) rootView.findViewById(R.id.textView_seatno);
            CardView cardView = (CardView) rootView.findViewById(R.id.wrapper);

            tCourseName.setText(examInfos.get(position).getCourseName());
            tDate.setText(examInfos.get(position).getDate());
            tTime.setText(examInfos.get(position).getTime());
            tPlace.setText(examInfos.get(position).getPlace());
            tSeatNo.setText(examInfos.get(position).getSeatNo());


            color = setBkColor(position);
            tCourseName.setBackgroundColor(color);
            relativeLayout2 = (RelativeLayout) rootView.findViewById(R.id.relativeLayout2);
            relativeLayout2.setBackgroundColor(color);


            return rootView;
        }

        public int setBkColor(int position) {
            switch (position % 6) {
                case 0:
                    return Color.rgb(242, 136, 115);
                case 1:
                    return Color.rgb(170, 204, 126);
                case 2:
                    return Color.rgb(179, 152, 227);
                case 3:
                    return Color.rgb(242, 124, 170);
                case 4:
                    return Color.rgb(115, 172, 233);
                case 5:
                    return Color.rgb(139, 139, 143);
            }
            return Color.rgb(242, 136, 115);
        }
    }

    public class entryJSONData extends AsyncTask<String, String, String> {
        private String EXAM = "exam";
        private String CAMPUS = "campus";
        private String SEAT_NUM = "seat_number";
        private String DATE = "date";
        private String TIME = "time";
        private String COURSE_TITLE = "course_title";
        private String BUILDING = "building";
        private String COURSE_CODE = "course_code";
        private String ROOM = "room";


        private ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);

            dialog.setMessage("It's just loading...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                network.getMyExaminations();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String campus;
            String seat_num;
            String date;
            String time;
            String course_title;
            String building;
            String course_code;
            String room;

            try {
                if (network.getError()) {
                    throw new JSONException("Error occur in network component.");
                }
                JSONObject j1 = network.getResponse();
                //j1 = j1.getJSONObject("");
                JSONArray jArr1 = j1.getJSONArray(EXAM);
                for (int a = 0; a < jArr1.length(); a++) {
                    JSONObject j2 = jArr1.getJSONObject(a);
                    campus = j2.getString(CAMPUS);
                    seat_num = j2.getString(SEAT_NUM);
                    date = j2.getString(DATE);
                    time = j2.getString(TIME);
                    course_title = j2.getString(COURSE_TITLE);
                    building = j2.getString(BUILDING);
                    course_code = j2.getString(COURSE_CODE);
                    room = j2.getString(ROOM);
                    //"course code + course title", "date", "time", "building + room", "seat number"
                    ExamInfo examInfo = new ExamInfo(course_code + " " + course_title, date, time, building + " " + room, seat_num);
                    examInfos.add(examInfo);
                }


            } catch (Exception e) {
                Log.d("EXAM JSON error", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            listView.setAdapter(new ExamTimetableAdapter(
                    getContext()
                    , R.layout.row_exam_timetable
                    , examInfos
            ));

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

}
