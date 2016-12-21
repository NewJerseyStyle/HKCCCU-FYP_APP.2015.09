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
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yeung on 2/8/2016.
 */
public class GradeReportFragment extends Fragment {
    private View rootView;
    private List<String> courses = new ArrayList<>();
    private HashMap<String, List<List<Course>>> semesters = new HashMap<>();
    private ExpandableListView expandableListView;

    private List<String> gpas = new ArrayList<>();

    private net network = new net(GradeReportFragment.this.getContext());

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


        rootView = inflater.inflate(R.layout.fragment_grade_report, container, false);
        expandableListView = (ExpandableListView) rootView.findViewById(R.id.expendalistView);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                return true;
            }
        });
        //test();
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
            getJSON();

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            GradeReportAdapter gradeReportAdapter = new GradeReportAdapter(
                    getContext(), courses, semesters
            );
            gradeReportAdapter.setGpas(gpas);
            expandableListView.setAdapter(gradeReportAdapter);

            if (dialog.isShowing())
                dialog.dismiss();
        }
    }


    public void getJSON() {
        /*final String SEM1 = "sem1";
        final String SEM2 = "sem2";
        final String SEM3 = "sem3";
        final String SEM4 = "sem4";

        final String[] arr_SEM4 = {"semName", "GPA", "AST20613 Computer System Development", "AST21119 Ethics In Technology", "CGE22402 Understanding Cultures and Heritage in Contemporary Hong Kong"
                , "AST20404 Information Systems in Business", "AST21112 Mathematical Analysis"};

        final String[] arr_SEM1 = {"semName", "GPA", "CGE1500 English for Academic Studies B", "CGE13204 Food and Health", "AST10106 Introduction to Programming",
                "AST10111 Basic Calculus and Linear Algebra", "AST10303 Understanding the Network-Centric World"};



        gpas.add(getJSONCourse(SEM1, arr_SEM1));
        gpas.add(getJSONCourse(SEM4, arr_SEM4));*/

        final String DATA = "data";
        final String NAME = "Name";
        final String GPA = "GPA";
        final String COURSE = "Course";

        String[] SEM = {"", "", "", ""};
        for (int a = 0; a < SEM.length; a++) {
            SEM[a] = "sem" + Integer.toString(a + 1);
        }

        try {
            network.getMyAcademicRecord();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (network.getError()) {
                throw new JSONException("Error occur in network component.");
            }
            JSONObject j1 = network.getResponse();

            for (int a = 0; a < SEM.length; a++) {
                String temp_sem = SEM[a];
                JSONObject j2 = j1.getJSONObject(temp_sem);
                //getJSONCourse(j2);
                String sem_name = j2.getString(NAME);
                JSONArray jArr1 = j2.getJSONArray(COURSE);
                getJSONCourse(sem_name, jArr1);
                String gpa = j2.getString(GPA);
                gpas.add(gpa);

            }
        } catch (Exception e) {
            Log.d("Grade Report Json error", e.getMessage());
        }
    }

    public String getJSONCourse(String sem_name, JSONArray jArr1) {


        final String GRADE = "Grade";
        final String NAME = "Name";
        try {

            //String sem_name = jsonObject.getString(NAME);
            courses.add(sem_name);

            List<Course> child1 = new ArrayList<>();
            List<List<Course>> header1 = new ArrayList<>();


            for (int a = 0; a < jArr1.length(); a++) {
                JSONObject j1 = jArr1.getJSONObject(a);
                String[] temp_course_name = j1.getString(NAME).split(" ", 2);

                String course_code = temp_course_name[0];
                String course_name = temp_course_name[1];
                String grade = j1.getString(GRADE);

                Log.d("Course code + name", course_code + course_name);
                Course course = new Course(course_code, course_name);
                course.setGpa(grade);
                Log.d("course GPA", grade);

                child1.add(course);
            }

            header1.add(child1);
            semesters.put(sem_name, header1);
        } catch (Exception e) {
            Log.d("JSON grade", e.getMessage());
        }


        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loginRW != null)
            loginRW.close();
    }
}
