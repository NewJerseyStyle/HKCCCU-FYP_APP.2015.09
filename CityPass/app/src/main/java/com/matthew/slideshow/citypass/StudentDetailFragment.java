package com.matthew.slideshow.citypass;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yeung on 2/10/2016.
 */
public class StudentDetailFragment extends Fragment {
    private View rootView;

    private net network;

    private StudentDetail studentDetail;
    private TextView tStudentName;
    private TextView tStudentId;
    private TextView tEid;
    private TextView tEmail;
    private TextView tDepartment;
    private TextView tMajor;
    private TextView tProgramme;
    private TextView tCampus;
    private TextView tAS;

    private StudentDetailRW studentDetailRW = null;
    private LoginRW loginRW = null;
    /*public void test()
    {
        studentDetail = new StudentDetail("Handsome"
            , "51234567"
            , "somehand2"
            , "somehand2-c@my.cityu.edu.hk"
            , "Applied Science and Technology (AST)"
            , "Information Systems Development"
            , "ASc in Info Systems Development"
            , "CCCU (MC)"
            , "Excellment (201509)"
    );
    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_student_detail, container, false);

        try {
            network = new net(StudentDetailFragment.this.getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        new InitialzeStudentDetail().execute();
        return rootView;
    }


    class InitialzeStudentDetail extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;

        final String STANDING = "AcadamicStanding";
        final String DEPARTMENT = "Department";
        final String CAMPUS = "Campus";
        final String SID = "SID";
        final String NAME = "Name";
        final String EID = "EID";
        final String EMAIL = "Email";
        final String PROG = "Programme";
        final String MAJOR = "Major";

        String standing, department, campus, sid, name, eid, email, prog, major;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);

            dialog.setMessage("It's just loading....");
            dialog.setCancelable(false);


            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        }

        @Override
        protected String doInBackground(String... args) {

            studentDetailRW = new StudentDetailRW(getActivity().getApplicationContext());
            if (studentDetailRW.getCount() > 0) {
                studentDetail = studentDetailRW.getStudentDetail();
            } else {
                try {
                    network.getPersonalData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //String temp_JSON = network.getResponseValue();


                try {
                    if (network.getError()) {
                        throw new JSONException("Error occur in network component.");
                    }
                    JSONObject j1 = network.getResponse();
                    standing = j1.getString(STANDING);
                    department = j1.getString(DEPARTMENT);
                    campus = j1.getString(CAMPUS);
                    sid = j1.getString(SID);
                    name = j1.getString(NAME);
                    eid = j1.getString(EID);
                    email = j1.getString(EMAIL);
                    prog = j1.getString(PROG);
                    major = j1.getString(MAJOR);
                    Log.d("Name", name);
                    studentDetail = new StudentDetail(name, sid, eid, email, department, major, prog, campus, standing);
                    studentDetailRW.insert(studentDetail);
                    //setStudentDetail(studentDetail);
                } catch (Exception e) {
                    Log.d("Initialze data error", e.getMessage());
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            tStudentName = (TextView) rootView.findViewById(R.id.textView_student_name);
            tStudentId = (TextView) rootView.findViewById(R.id.textView_student_id);
            tEid = (TextView) rootView.findViewById(R.id.textView_electronic_id);
            tEmail = (TextView) rootView.findViewById(R.id.textView_email);
            tDepartment = (TextView) rootView.findViewById(R.id.textView_department);
            tMajor = (TextView) rootView.findViewById(R.id.textView_major);
            tProgramme = (TextView) rootView.findViewById(R.id.textView_programme);
            tCampus = (TextView) rootView.findViewById(R.id.textView_campus);
            tAS = (TextView) rootView.findViewById(R.id.textView_academic_standing);


            tStudentName.setText(studentDetail.getStudentName());
            tStudentId.setText(studentDetail.getStudentId());
            tEid.setText(studentDetail.getEid());
            tEmail.setText(studentDetail.getEmail());
            tDepartment.setText(studentDetail.getDepartment());
            tMajor.setText(studentDetail.getMajor());
            tProgramme.setText(studentDetail.getProgramme());
            tCampus.setText(studentDetail.getCampus());
            tAS.setText(studentDetail.getAS());

            if (dialog.isShowing())
                dialog.dismiss();

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (studentDetailRW != null)
            studentDetailRW.close();
        if (loginRW != null)
            loginRW.close();
    }


}
