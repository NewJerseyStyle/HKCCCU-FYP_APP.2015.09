package com.matthew.slideshow.citypass;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matthew on 27/12/2015.
 */
public class AnnouncementFragment extends Fragment {

    ListView listView;
    //private AnnouncementAdapter announcementAdapter;
    //String []data = null;
    FragmentManager fragmentManager;

/*
    private List<AnnouncementCourse> announcementCourses;
    private List<String> course_names = new ArrayList<>();

    private LMSExt network = new LMSExt(AnnouncementFragment.this.getContext());
*/

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
//    private AnnouncementRW announcementRW = null;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.notification_bar_color));
        }

        View view = inflater.inflate(
                R.layout.fragment_announcement, container, false);

        fragmentManager = getActivity().getSupportFragmentManager();
        listView = (ListView) view.findViewById(R.id.holiday_list);

        /*new entryJSON().execute();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Refresh().execute();
            }
        });
*/
        return view;
    }

/*
    private void getJSON() {
        announcementRW = new AnnouncementRW(getContext().getApplicationContext());


        int count = announcementRW.getCount();
        if (count == 0) {
            final String CANVAS = "canvas";
            final String COURSE = "course";
            final String COURSE_CODE = "course_code";

            final String ANNOUNCEMENT = "annoucement";
            final String HEADER = "header";
            final String POST_AT = "posted_at";
            final String BODY = "body";
            final String NAME = "name";

            network.getAnnouncement();

            try {
                if (network.getError()) {
                    throw new JSONException("Error occur in network component.");
                }
                String str1 = network.getResponse().toString();
                str1 = str1.replace("\\u003C", "<").replace("\\u003E", ">").replace("\\r", "\n").replace("\\n", "\n").replace("\\u0026", "&").replace("&#39;", "'");
                str1 = str1.replace("<p>", "").replace("</p>", "");
                JSONObject j1 = new JSONObject(str1);
                JSONArray jArr1 = j1.getJSONArray(COURSE);


                for (int a = 0; a < jArr1.length(); a++) {
                    JSONObject j2 = jArr1.getJSONObject(a);
                    String course_name = j2.getString(NAME);
                    String course_code = j2.getString(COURSE_CODE);

                    AnnouncementCourse announcementCourse = new AnnouncementCourse(course_code, course_name.replace("'", "&#39;"));

                    announcementRW.insertCourseName(announcementCourse);
                }

                JSONArray jArr2 = j1.getJSONArray(ANNOUNCEMENT);
                for (int a = 1; a < jArr2.length(); a++) {
                    JSONObject j3 = jArr2.getJSONObject(a);
                    String title = j3.getString(HEADER);
                    String post_at = j3.getString(POST_AT);
                    String message = j3.getString(BODY);

                    String course = j3.getString(COURSE_CODE);
                    if (!course.equals("55")) {
                        Announcement announcement = new Announcement(title.replace("'", "&#39;"), message.replace("'", "&#39;"), post_at.replace("'", "&#39;"));
                        announcement.setCourse_code(course);

                        announcementRW.insertAnnouncement(announcement);
                    }

                }


            } catch (Exception e) {
                Log.d("Announcement json error", e.getMessage());
            }
        }

        announcementCourses = announcementRW.getAllCourse();
        if (announcementCourses == null)
            announcementCourses = new ArrayList<>();
        else {
            for (int a = 0; a < announcementCourses.size(); a++) {
                course_names.add(announcementCourses.get(a).getCourse_name().replace("&#39;", "'"));
            }
        }
    }
*/

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
/*        Log.d("on destory", "main");
        if (announcementRW != null)
            announcementRW.close();
*/    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.d("on pause", "main");
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.d("on resume", "main");
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Log.d("on start", "main");
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Log.d("on stop", "main");
    }

/*
    public class AnnouncementAdapter extends ArrayAdapter<String> {
        List<String> data;
        int layoutResourceId;
        TextView textView;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rootView = convertView;
            if (rootView == null) {
                LayoutInflater inflater = ((AppCompatActivity) getActivity()).getLayoutInflater();
                rootView = inflater.inflate(layoutResourceId, parent, false);
            }


            textView = (TextView) rootView.findViewById(R.id.cource_name_textview);
            textView.setText(data.get(position));

            switch (position % 6) {
                case 0:
                    textView.setBackgroundColor(Color.rgb(242, 136, 115));
                    break;
                case 1:
                    textView.setBackgroundColor(Color.rgb(170, 204, 126));
                    break;
                case 2:
                    textView.setBackgroundColor(Color.rgb(179, 152, 227));
                    break;
                case 3:
                    textView.setBackgroundColor(Color.rgb(242, 124, 170));
                    break;
                case 4:
                    textView.setBackgroundColor(Color.rgb(115, 172, 233));
                    break;
                case 5:
                    textView.setBackgroundColor(Color.rgb(139, 139, 143));
                    break;
            }

            return rootView;
        }
    }
*/
}
