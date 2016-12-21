package com.matthew.slideshow.citypass;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by yeung on 2/8/2016.
 */
public class GradeReportAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> semesters;
    private HashMap<String, List<List<Course>>> grades;
    private List<String> gpas;

    public GradeReportAdapter(Context context, List<String> semesters
            , HashMap<String, List<List<Course>>> grades) {
        this.context = context;
        this.semesters = semesters;
        this.grades = grades;
    }


    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.grades.get(this.semesters.get(groupPosition)).get(childPosition);
    }


    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View converView, ViewGroup parent) {
        List<Course> gradeContents = (List) getChild(groupPosition, childPosition);
        ListView listView;
        if (converView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            converView = layoutInflater.inflate(R.layout.row_child_grade_report, null);
        }
        listView = (ListView) converView.findViewById(R.id.holiday_list);
        GradeReportContentAdapter grca = new GradeReportContentAdapter(context,
                R.layout.row_grade_report_content,
                gradeContents);

        int numOfCourse = grades.get(this.semesters.get(groupPosition)).get(childPosition).size();
        listView.setAdapter(grca);
        int height = (int) (context.getResources().getDimension(R.dimen.grade_report_content_height));
        RelativeLayout.LayoutParams llp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (height * numOfCourse) + (int) (height / 3));
        listView.setLayoutParams(llp);

        TextView tGpa = (TextView) converView.findViewById(R.id.textView_gpa);
        tGpa.setText("GPA: " + gpas.get(groupPosition));// + getGpa(gradeContents));

        return converView;
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        return this.grades.get(this.semesters.get(groupPosition)).size();
    }


    @Override
    public Object getGroup(int groupPosition) {
        return this.semesters.get(groupPosition);
    }


    @Override
    public int getGroupCount() {
        return this.semesters.size();
    }


    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View converView, ViewGroup parent) {
        String semester = (String) getGroup(groupPosition);
        if (converView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            converView = layoutInflater.inflate(R.layout.row_header_grade_report, null);
        }
        TextView tSemester = (TextView) converView.findViewById(R.id.textView_semester);
        tSemester.setBackgroundColor(backgroundColor(groupPosition));
        tSemester.setText(semester);

        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);

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


    public class GradeReportContentAdapter extends ArrayAdapter<Course> {
        Context context;
        List<Course> courses;
        int layoutResourceId;
        private TextView tName;
        private TextView tGrade;


        public GradeReportContentAdapter(Context context, int layoutResourceId, List<Course> courses) {
            super(context, layoutResourceId, courses);
            this.context = context;
            this.layoutResourceId = layoutResourceId;
            this.courses = courses;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String courseName = courses.get(position).getCourse_code() + " " + courses.get(position).getCourse_name();
            String grade = courses.get(position).getGpa();
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }
            tName = (TextView) convertView.findViewById(R.id.textView_list_date);
            tGrade = (TextView) convertView.findViewById(R.id.textView_grade);
            tName.setText(courseName);
            tGrade.setText(grade);
            return convertView;
        }


    }

    public int backgroundColor(int a) {
        switch (a % 6) {
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
        return Color.WHITE;
    }


    public void setGpas(List<String> gpas) {
        this.gpas = gpas;
    }
}
