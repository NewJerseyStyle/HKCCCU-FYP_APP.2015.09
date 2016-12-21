package com.matthew.slideshow.citypass;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class net {
    private String name, pass;
    private JSONObject response;
    private boolean error;
    private HttpURLConnection myClient;
    private String last_url;

    net(Context context) {
        CookieHandler.setDefault(new CookieManager());
        String test = get("https://banweb.cityu.edu.hk/pls/PROD/twgkpswd_cityu.P_WWWLogin", null);
        assert test != null;
        if(!test.contains("<INPUT TYPE=\"submit\" VALUE=\" Login \" class=\"input_button\">")) {
            error = true;
        }
        LoginRW loginRW = new LoginRW(context);
        name = loginRW.getLoginName();
        pass = loginRW.getLoginPassword();
        response = null;
        //loginRW.close();
    }

    public void getTimeTable() throws Exception {
        login2AIMS();
        if (error) {
            response = null;
            error = false;
            return;
        }
        get("https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=bmenu.P_MainMnu", null);
        get("https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=bmenu.P_MainMnu", "https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=bmenu.P_MainMnu");
        String res = get("https://banweb.cityu.edu.hk/pls/PROD/hwsstmtbl_matrix_cityu.Show", "https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=bmenu.P_MainMnu");
        if (res.contains("<SELECT NAME=\"term_in\" SIZE=1>")) {
            int index = res.indexOf("<OPTION VALUE=\"");
            String value = res.substring(index + 15, index + 21);
            res = post("https://banweb.cityu.edu.hk/pls/PROD/hwsstmtbl_matrix_cityu.Show", "term_in="+value, "https://banweb.cityu.edu.hk/pls/PROD/hwsstmtbl_matrix_cityu.Show");
        }
        //Log.d("Netword", "GetTimeTable: " + res);
        String data = res.trim().replaceAll(" +", " ");
        if (data.contains("No class schedule found for selected term.")) {
            response = new JSONObject();
            return;
        }
        data = data.replaceAll("\n+", "");
        if (data.indexOf("<TH class=\"ctt-matrix-th-day\">Sunday</TH>") == -1) {
            response = new JSONObject();
            return;
        }
        data = data.substring(data.indexOf("<TH class=\"ctt-matrix-th-day\">Sunday</TH>"));
        data = data.substring(0, data.indexOf("</TABLE>"));
        String[] timeTable = data.split("</TD>");
        int day = 0;
        int timeCount = 0;
        String time = "";
        JSONObject jsonObject = new JSONObject();
        boolean[][] summary = new boolean[7][15];
        for (int i = 0; i != 7; i++) {
            for (int j = 0; j != 15; j++) {
                summary[i][j] = false;
            }
        }
        JSONArray jsonArray = new JSONArray();
        for (String val : timeTable) {
            if (val.startsWith("</TR><TR><TD class=\"ctt-matrix-td-time\">")) {
                time = val.substring(40, 11);
                timeCount = Integer.parseInt(time.substring(0, time.indexOf(":"))) - 8;
                day = 0;
            } else if (val.startsWith("<TD class=\"ctt-matrix-cell-blank\"")) {
                while (summary[day][timeCount]) {
                    day++;
                }
                day++;
            } else if (val.startsWith("<TD class=\"ctt-matrix-cell-class\" ")) {
                while (summary[day][timeCount]) {
                    day++;
                }
                String duration = val.substring(42, 1);
                for (int i = 0; i < Integer.parseInt(val.substring(42, 1)); i++) {
                    summary[day][timeCount + i] = true;
                }
                String theCourse = val.substring(val.indexOf(" valign=top>") + 12);
                JSONObject singleObject = new JSONObject();
                try {
                    singleObject.put("crn", theCourse.substring(0, theCourse.indexOf(" <BR>")));
                    theCourse = theCourse.substring(theCourse.indexOf(" <BR>") + 5);
                    JSONObject set = new JSONObject();
                    set.put("course_code", theCourse.substring(0, theCourse.indexOf("-")));
                    theCourse = theCourse.substring(theCourse.indexOf("-") + 1);
                    singleObject.put("section", theCourse.substring(0, theCourse.indexOf("<BR>")));
                    theCourse = theCourse.substring(theCourse.indexOf("<BR>") + 4);
                    theCourse = theCourse.replaceAll("<BR>", "");
                    singleObject.put("location", theCourse);
                    singleObject.put("starttime", time);
                    singleObject.put("days", day + 1);
                    singleObject.put("duration", duration);
                    set.put("lesson", singleObject);
                    jsonArray.put(set);
                } catch (JSONException e) {
                    e.printStackTrace();
                    error = true;
                    response = null;
                    return;
                }
                day++;
            } else if (val.startsWith("<TD class=\"ctt-matrix-cell-overlap\" ")) {
                while (summary[day][timeCount]) {
                    day++;
                }
                String duration = val.substring(44, 1);
                for (int i = 0; i < Integer.parseInt(duration); i++) {
                    summary[day][timeCount + i] = true;
                }
                String theCourse = val.substring(val.indexOf(" valign=top>") + 12);
                JSONObject singleObject = new JSONObject();
                try {
                    singleObject.put("crn", theCourse.substring(0, theCourse.indexOf(" <BR>")));
                    theCourse = theCourse.substring(theCourse.indexOf(" <BR>") + 5);
                    JSONObject set = new JSONObject();
                    set.put("course_code", theCourse.substring(0, theCourse.indexOf("-")));
                    theCourse = theCourse.substring(theCourse.indexOf("-") + 1);
                    singleObject.put("section", theCourse.substring(0, val.indexOf("<BR>")));
                    theCourse = theCourse.substring(theCourse.indexOf("<BR>") + 4);
                    theCourse = theCourse.replaceAll("<BR>", "");
                    singleObject.put("location", theCourse);
                    singleObject.put("starttime", time);
                    singleObject.put("days", day + 1);
                    singleObject.put("duration", duration);
                    set.put("lesson", singleObject);
                    jsonArray.put(set);
                } catch (JSONException e) {
                    e.printStackTrace();
                    error = true;
                    response = null;
                    return;
                }
                day++;
            }
            try {
                jsonObject.put("course", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
                error = true;
                response = null;
                return;
            }
        }
                        /* To format the value */
        JSONArray resultTmp = new JSONArray();
        for (int i = 0; i != jsonArray.length(); i++) {
            for (int j = resultTmp.length(); j >= 0; j--) {
                if (j == 0) {
                    JSONObject set = new JSONObject();
                    try {
                        set.put("course_code", jsonArray.getJSONObject(i).get("course_code"));
                        JSONObject singleLesson = new JSONObject();
                        singleLesson.put("duration", jsonArray.getJSONObject(i).getJSONObject("lesson").get("duration"));
                        singleLesson.put("starttime", jsonArray.getJSONObject(i).getJSONObject("lesson").get("starttime"));
                        singleLesson.put("days", jsonArray.getJSONObject(i).getJSONObject("lesson").get("days"));
                        singleLesson.put("crn", jsonArray.getJSONObject(i).getJSONObject("lesson").get("crn"));
                        singleLesson.put("section", jsonArray.getJSONObject(i).getJSONObject("lesson").get("section"));
                        singleLesson.put("location", jsonArray.getJSONObject(i).getJSONObject("lesson").get("location"));
                        set.put("lesson", new JSONArray().put(singleLesson));
                        resultTmp.put(set);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        error = true;
                        response = null;
                        return;
                    }
                } else try {
                    if (jsonArray.getJSONObject(i).get("course_code").equals(resultTmp.getJSONObject(j).get("course_code"))) {
                        JSONObject set = resultTmp.getJSONObject(j);
                        try {
                            JSONObject singleLesson = new JSONObject();
                            singleLesson.put("duration", jsonArray.getJSONObject(i).getJSONObject("lesson").get("duration"));
                            singleLesson.put("starttime", jsonArray.getJSONObject(i).getJSONObject("lesson").get("starttime"));
                            singleLesson.put("days", jsonArray.getJSONObject(i).getJSONObject("lesson").get("days"));
                            singleLesson.put("crn", jsonArray.getJSONObject(i).getJSONObject("lesson").get("crn"));
                            singleLesson.put("section", jsonArray.getJSONObject(i).getJSONObject("lesson").get("section"));
                            singleLesson.put("location", jsonArray.getJSONObject(i).getJSONObject("lesson").get("location"));
                            JSONArray tmp = set.getJSONArray("lesson");
                            tmp.put(singleLesson);
                            set.put("lesson", tmp);
                            resultTmp.put(j, set);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            error = true;
                            response = null;
                            return;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    error = true;
                    response = null;
                    return;
                }
            }
        }
        try {
            jsonObject.put("course", resultTmp);
            response = jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            error = true;
            response = null;
        }
        //logout2AIMS();
    }

    public void getDetailSchedule() throws Exception {
        login2AIMS();
        if (error) {
            response = null;
            error = false;
            return;
        }
        get("https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=amenu.P_RegMnu", null);
        String res = get("https://banweb.cityu.edu.hk/pls/PROD/bwskfshd.P_CrseSchdDetl", "https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=amenu.P_RegMnu");
        String select = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        //if (res.contains("06\">Summer ")) {
            int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
            if( month > 2 && month < 6 ) {
                select += "02";
            } else if ( month > 5 && month < 9 ) {
                select += "06";
            } else {
                select += "09";
            }
        //}
        res = post("https://banweb.cityu.edu.hk/pls/PROD/bwskfshd.P_CrseSchdDetl", "term_id="+select, "https://banweb.cityu.edu.hk/pls/PROD/bwskfshd.P_CrseSchdDetl");
        String data = res.trim().replaceAll(" +", " ").replaceAll("\n+", "");
        if (data.contains("You are not currently registered for the term.")) {
            response = new JSONObject();
            return;
        }
        data = betweenStr(data, "<H2>Student Detail Schedule</H2>", "<A HREF=\"javascript:history.go(-1)\" onMouseOver=\"window.status='Return to Previous'; return true\" onFocus=\"window.status='Return to Previous'; return true\" onMouseOut=\"window.status=''; return true\"onBlur=\"window.status=''; return true\">Return to Previous</A>", EXCL);
        JSONObject jsonObject = new JSONObject();
        JSONArray courseDetail = new JSONArray();
        JSONArray courseName = new JSONArray();
        JSONObject scheduledMeetingTimes = new JSONObject();
        try {
            jsonObject.put("TotalCreditHours", betweenStr(data, "Total Credit Hours: ", "<BR>", EXCL));
            String[] courses = data.split("<TABLE CLASS=\"datadisplaytable\" SUMMARY=\"This layout table is used to present the schedule course detail\" cellspacing=\"0\" cellpadding=2><CAPTION class=\"captiontext\">");
            Hashtable<String, String> tmpHash = new Hashtable<>();
            for (String val : courses) {
                String a = val.substring(0, val.indexOf("</CAPTION>"));
                a = a.substring(0, a.indexOf("-", a.indexOf("-") + 1));
                if (!(a.contains("<") && a.contains(">") && a.contains("/"))) {
                    data = data.replaceAll("</TR>", "").replaceAll("<TR>", "");
                    if (!tmpHash.containsKey(a)) {
                        tmpHash.put(a, a);
                        courseName.put(a);
                    }
                    JSONObject singleCourse = new JSONObject();
                    singleCourse.put("AssociatedTerm", betweenStr(val, "Associated Term:</TH><TD CLASS=\"dddefault\">", "</TD>", EXCL));
                    singleCourse.put("CRN", betweenStr(val, "CRN</ACRONYM>:</TH><TD CLASS=\"dddefault\">", "</TD>", EXCL));
                    singleCourse.put("AssignedInstructor", betweenStr(val, "Assigned Instructor:</TH><TD CLASS=\"dddefault\">", "</TD>", EXCL));
                    singleCourse.put("GradeMode", betweenStr(val, "Grade Mode:</TH><TD CLASS=\"dddefault\">", "</TD>", EXCL));
                    singleCourse.put("Credits", betweenStr(val, "Credits:</TH><TD CLASS=\"dddefault\">", "</TD>", EXCL));
                    singleCourse.put("Level", betweenStr(val, "Level:</TH><TD CLASS=\"dddefault\">", "</TD>", EXCL));
                    singleCourse.put("Campus", betweenStr(val, "Campus:</TH><TD CLASS=\"dddefault\">", "</TD>", EXCL));
                    String[] tmp = betweenStr(val, "<TH CLASS=\"ddheader\" scope=\"col\" >Instructors</TH>", "</TABLE>", EXCL).split("<TD CLASS=\"dddefault\">");
                    JSONObject singleMeetingTimes = new JSONObject();
                    singleMeetingTimes.put("Time", tmp[3].substring(0, tmp[3].indexOf("</TD>")));
                    singleMeetingTimes.put("Place", tmp[4].substring(0, tmp[4].indexOf("</TD>")));
                    singleMeetingTimes.put("DateRange", tmp[5].substring(0, tmp[5].indexOf("</TD>")));
                    scheduledMeetingTimes.put("Week0", singleMeetingTimes);
                    scheduledMeetingTimes.put("WeekRef", 0);
                    for (int i = 1; i < 13; i++) {
                        if (tmp[i * 7 + 3].isEmpty()) {
                            break;
                        }
                        singleMeetingTimes = new JSONObject();
                        singleMeetingTimes.put("Time", tmp[i * 7 + 3].substring(0, tmp[i * 7 + 3].indexOf("</TD>")));
                        singleMeetingTimes.put("Place", tmp[i * 7 + 4].substring(0, tmp[i * 7 + 4].indexOf("</TD>")));
                        singleMeetingTimes.put("DateRange", tmp[i * 7 + 5].substring(0, tmp[i * 7 + 5].indexOf("</TD>")));
                        scheduledMeetingTimes.put("Week" + Integer.toString(i), singleMeetingTimes);
                    }
                    singleCourse.put("ScheduledMeetingTimes", scheduledMeetingTimes);
                    courseDetail.put(singleCourse);
                }
            }
            jsonObject.put("course", courseDetail);
            jsonObject.put("courseName", courseName);
            response = jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            error = true;
            response = null;
        }
        //logout2AIMS();
    }

    public void getMyExaminations() throws Exception {
        login2AIMS();
        if (error) {
            response = null;
            error = false;
            return;
        }
        get("https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=amenu.P_AdminMnu", null);
        get("https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=amenu_cityu.P_StudExamMnu", "https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=amenu.P_AdminMnu");
        String res = get("https://banweb.cityu.edu.hk/pls/PROD/hwsrsett_cityu.P_DispSchd", "https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=amenu_cityu.P_StudExamMnu");
        JSONArray exam = new JSONArray();
        String data = res.trim().replaceAll(" +", " ").replaceAll("\n+", "");
        data = betweenStr(data, "<TABLE border>", "</TABLE>", EXCL);
        String[] course = data.split("<TR>");
        for (String x : course) {
            if (x.contains("<TD>")) {
                String[] info = x.split("<TD>");
                JSONObject singleExam = new JSONObject();
                try {
                    singleExam.put("course_code", info[1].substring(0, info[1].indexOf("</TD>")));
                    singleExam.put("course_title", info[2].substring(0, info[2].indexOf("</TD>")));
                    singleExam.put("date", info[3].substring(0, info[3].indexOf("</TD>")));
                    singleExam.put("time", info[4].substring(0, info[4].indexOf("</TD>")));
                    singleExam.put("room", info[5].substring(0, info[5].indexOf("</TD>")));
                    singleExam.put("building", info[6].substring(0, info[6].indexOf("</TD>")));
                    singleExam.put("campus", info[7].substring(0, info[7].indexOf("</TD>")));
                    singleExam.put("seat_number", info[8].substring(0, info[8].indexOf("</TD>")));
                    exam.put(singleExam);
                } catch (JSONException e) {
                    e.printStackTrace();
                    error = true;
                    response = null;
                    return;
                }
            }
        }
        try {
            response = new JSONObject();
            response.put("exam", exam);
        } catch (JSONException e) {
            e.printStackTrace();
            error = true;
            response = null;
        }
        //logout2AIMS();
    }

    public void getPersonalData() throws Exception {
        login2AIMS();
        if (error) {
            response = null;
            error = false;
            return;
        }
        get("https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=amenu.P_AdminMnu", null);
        get("https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=amenu_cityu.P_StudStatusMnu", "https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=amenu.P_AdminMnu");
        String res = get("https://banweb.cityu.edu.hk/pls/PROD/hwskfste_cityu.P_RegDispStud", "https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=amenu_cityu.P_StudStatusMnu");
        response = new JSONObject();
        String data = res.trim().replaceAll(" +", " ").replaceAll("\n+", "");
        String nameInfo = betweenStr(data, "<H3>Student Status Information for: ", ")", EXCL);
        String sid = nameInfo.substring(nameInfo.indexOf("(") + 1);
        nameInfo = nameInfo.substring(0, nameInfo.indexOf("(")).replaceAll(",", "");
        try {
            response.put("Name", nameInfo);
            response.put("SID", sid);
            response.put("EID", betweenStr(data, "</TD></TR><TR><TD><B>EID : </B>", "</TD></TR></TABLE><TABLE ></TABLE>", EXCL));
            response.put("Email", betweenStr(data, "</TD></TR><TR></TR><TR><TD><B>Email : </B>", "</TD></TR><TR><TD><B>EID : </B>", EXCL));
            response.put("Department", betweenStr(data, "</TD></TR><TR><TD><B>Department : </B>", "</TD></TR><TR><TD><B>Programme : </B>", EXCL));
            response.put("Major", betweenStr(data, "</TD></TR><TR><TD><B>Major : </B>", "</TD></TR><TR><TD><B>Gender : </B>", EXCL));
            response.put("Programme", betweenStr(data, "</TD></TR><TR><TD><B>Programme : </B>", "</TD></TR><TR><TD><B>Cohort : </B>", EXCL));
            response.put("Campus", betweenStr(data, "</TD></TR><TR><TD><B>Campus : </B>", "</TD></TR><TR><TD><B>Level : </B>", EXCL));
            response.put("AcadamicStanding", betweenStr(data, "</TD></TR></TABLE><TABLE ><TR><TD><B>Academic Standing : </B>", "</TD></TR></TABLE><TABLE ><TR><TD><B>Student Status : </B>", EXCL));
        } catch (JSONException e) {
            e.printStackTrace();
            error = true;
            response = null;
        }
        //logout2AIMS();
    }

    public void getMyAcademicRecord() throws Exception {
        login2AIMS();
        if (error) {
            response = null;
            error = false;
            return;
        }
        get("https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=amenu.P_AdminMnu", null);
        get("https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=amenu_cityu.P_StudAcadRecMnu", "https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=amenu.P_AdminMnu");
        String res = get("https://banweb.cityu.edu.hk/pls/PROD/hwsrcrtr_cityu.sw_gen_crtr_stud2", "https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=amenu_cityu.P_StudAcadRecMnu");
        String value = res.substring(res.indexOf("<TD CLASS=\"pldefault\"width=50><INPUT TYPE=\"radio\" NAME=\"") + 56);
        value = value.substring(value.indexOf("VALUE=\"") + 7);
        value = value.substring(0, value.indexOf("\""));
        res = post("https://banweb.cityu.edu.hk/pls/PROD/hwsrcrtr_cityu.sw_print_stud_grade2", "in_programme="+value, "https://banweb.cityu.edu.hk/pls/PROD/hwsrcrtr_cityu.sw_gen_crtr_stud2");
        String data = res.replaceAll(" +", " ");
        JSONObject grade = new JSONObject();
        String[] a = data.split("<TD CLASS=\"pldefault\"bgcolor=#99CCFF colspan=5 align=center><B>");
        int wastedVar = 1;
        for (String x : a) {
            JSONArray courses = new JSONArray();
            JSONObject singleRecord = new JSONObject();
            String b = x.substring(0, x.indexOf("</B></TD>"));
            if (b.startsWith("Semester")) {
                try {
                    singleRecord.put("Name", b);
                    String[] c = x.split("<TR>");
                    for (String y : c) {
                        y = y.replaceAll("</FONT></TD><TD CLASS=\"pldefault\"NOWRAP width=450 bgcolor=><FONT COLOR=>", " ");
                        String d = y.substring(60, y.indexOf("</FONT></TD>") - 60);
                        if (!d.contains("<") && !d.contains(">") && Character.isUpperCase(d.charAt(0))) {
                            JSONObject aCourse = new JSONObject();
                            aCourse.put("Name", d);
                            String[] singleGrade = y.split("</FONT></TD>");
                            if (singleGrade[2].startsWith("<TD CLASS=\"pldefault\"NOWRAP bgcolor=><FONT COLOR=>/")) {
                                aCourse.put("Grade", singleGrade[2].substring(50));
                            } else {
                                aCourse.put("Grade", "Unknown");
                            }
                            courses.put(aCourse);
                        }
                    }
                    singleRecord.put("Course", courses);
                    String gpa = betweenStr(x, "<TD CLASS=\"pldefault\"align=right NOWRAP width=95><B><B>GPA: </B></B></TD>", "</TD>", EXCL).replaceAll("\\n<TD CLASS=\"pldefault\"NOWRAP width=60>", "").replaceAll(" +", "");
                    if (Character.isDigit(gpa.charAt(0))) {
                        singleRecord.put("GPA", gpa);
                    } else {
                        singleRecord.put("GPA", "Unknown");
                    }
                    grade.put("sem" + wastedVar, singleRecord);
                    wastedVar++;
                } catch (JSONException e) {
                    e.printStackTrace();
                    error = true;
                    response = null;
                    return;
                }
            }
        }
        response = grade;
        //logout2AIMS();
    }

    public void getMyFinance() throws Exception {
        login2AIMS();
        if (error) {
            response = null;
            error = false;
            return;
        }
        get("https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=amenu.P_AdminMnu", null);
        get("https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=amenu_cityu.P_StudFinanceMnu", "https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=amenu.P_AdminMnu");
        String res = get("https://banweb.cityu.edu.hk/pls/PROD/hwskeqac_cityu.P_ViewAcct", "https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=amenu_cityu.P_StudFinanceMnu");
        String data = betweenStr(res, "<TH BGCOLOR=\"#ccccff\"><B>Balance</B></TH>", "</TABLE>", EXCL);
        data = data.replaceAll(" +", " ");
        JSONArray jsonArray = new JSONArray();
        String[] b = data.split("<TR ALIGN=\"left\">");
        for (String x : b) {
            String[] c = x.split("<TD>");
            JSONObject singlePay = new JSONObject();
            if (c[1].startsWith("<FONT SIZE=\"-1\">")) {
                try {
                    singlePay.put("name", betweenStr(c[1], "<FONT SIZE=\"-1\">", "</FONT></TD>", EXCL));
                    singlePay.put("TransDate", betweenStr(c[2], "<FONT SIZE=\"-1\">", "</FONT></TD>", EXCL));
                    singlePay.put("DueDate", betweenStr(c[3], "<FONT SIZE=\"-1\">", "</FONT></TD>", EXCL));
                    singlePay.put("balance", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                    error = true;
                    response = null;
                    return;
                }
            }
            if (x.startsWith("<TD COLSPAN=\"5\" BGCOLOR=\"#ccccff\"><H4>")) {
                String balance = x.substring(x.indexOf("<TD ALIGN=\"right\" BGCOLOR=\"#ccccff\"><H4>"));
                balance = balance.substring(40, balance.indexOf("</H4></TD></TR>") - 40).replaceAll(" +", "");
                try {
                    singlePay.put("balance", balance);
                } catch (JSONException e) {
                    e.printStackTrace();
                    error = true;
                    response = null;
                    return;
                }
            }
            jsonArray.put(singlePay);
        }
        response = new JSONObject();
        try {
            response.put("financialData", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            error = true;
            response = null;
        }
        //logout2AIMS();
    }

    private void login2AIMS() {
        error = false;
        if (name.isEmpty() || pass.isEmpty()) {
            error = true;
            return;
        }
        String currentPage = get("https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=bmenu.P_MainMnu", null);
        if(currentPage.contains("\"twbkwbis.P_Logout\"")) {
            Log.d("NetLogin", "Already login");
            return;
        }
        currentPage = get("https://banweb.cityu.edu.hk/pls/PROD/twgkpswd_cityu.P_WWWLogin", null);
        /*
        Pattern pattern = Pattern.compile("^( document\\.P_WWWLogin\\.User)([0-9]+)(\\.focus\\(\\);)$");
        Matcher matcher = pattern.matcher(currentPage);
        String userid = "User";
        if (matcher.find()){
            userid += matcher.group(2);
        }
        */
        String p_username = name;
        String p_password = pass;
        String p_sess_id = currentPage.substring(currentPage.indexOf("<INPUT TYPE=\"hidden\" NAME=\"p_sess_id\" VALUE=\"") + 45);
        String p_ip = currentPage.substring(currentPage.indexOf("<INPUT TYPE=\"hidden\" NAME=\"p_ip\" VALUE=\"") + 40);
        String to_url = currentPage.substring(currentPage.indexOf("<INPUT TYPE=\"hidden\" NAME=\"to_url\" VALUE=\"") + 42);
        p_sess_id = p_sess_id.substring(0, p_sess_id.indexOf("\""));
        p_ip = p_ip.substring(0, p_ip.indexOf("\""));
        to_url = to_url.substring(0, to_url.indexOf("\""));
        post("https://banweb.cityu.edu.hk/pls/PROD/twgkpswd_cityu.P_WWWLogin", "p_username="+p_username+"&p_password="+p_password+"&p_sess_id="+p_sess_id+"&p_ip="+p_ip+"&to_url="+to_url, last_url);
        currentPage = get("https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_GenMenu?name=amenu.P_AdminMnu", myClient.getURL().toString());
        if (currentPage == null || !currentPage.contains("\"twbkwbis.P_Logout\"")) {
            error = true;
        }
    }

    public boolean login(String user, String password) {
        name = user;
        pass = password;
        login2AIMS();
        Log.d("NetLogin", "No exception");
        if (error) {
            error = false;
            return false;
        }
        return true;
    }

    public void logout2AIMS() throws Exception {
        get("https://banweb.cityu.edu.hk/pls/PROD/twbkwbis.P_Logout", last_url);
        myClient.disconnect();
    }

    private final boolean EXCL = false;
    private final boolean INCL = true;

    private String betweenStr(String target, String a, String b, boolean incl) {
        Log.d("Network", target);
        if (incl) {
            target = target.substring(target.indexOf(a));
            return target.substring(0, target.indexOf(b) + b.length());
        }
        target = target.substring(target.indexOf(a) + a.length());
        return target.substring(0, target.indexOf(b));
    }

    public JSONObject getResponse() {
        JSONObject tmp = response;
        response = null;
        return tmp;
    }

    public boolean getError() {
        return error;
    }

    private String get(String url, String ref) {
        last_url = url;
        try {
            myClient = (HttpURLConnection) (new URL(url)).openConnection();
            myClient.setInstanceFollowRedirects(true);
            myClient.setRequestMethod("GET");
            myClient.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.0; WOW64; Trident/5.0)");
            if (ref != null) {
                if (!ref.isEmpty()) {
                    myClient.setRequestProperty("Referer", ref);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ExecutorService pool = Executors.newFixedThreadPool(3);
        Callable<String> callable = new callGet();
        Future<String> future = pool.submit(callable);
        try {
            while (!future.isDone()) {
                Thread.sleep(400);
            }
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class callGet implements Callable<String>{
        public String call() throws Exception{
            Thread.sleep(300);
            int code = myClient.getResponseCode();
            if (code != 200) {
                return null;
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(myClient.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                Log.d("NetTesterRecieved: ", inputLine);
            }
            in.close();
            return response.toString();
        }
    }

    private String post(String url, String urlParameters, String ref) {
        Log.d("POSTING", urlParameters);
        last_url = url;
        try {
            myClient = (HttpURLConnection) (new URL(url)).openConnection();
            myClient.setInstanceFollowRedirects(true);
            myClient.setRequestMethod("POST");
            myClient.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.0; WOW64; Trident/5.0)");
            if (ref != null) {
                if (!ref.isEmpty()) {
                    myClient.setRequestProperty("Referer", ref);
                }
            }
            myClient.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(myClient.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ExecutorService pool = Executors.newFixedThreadPool(3);
        Callable<String> callable = new callGet();
        Future<String> future = pool.submit(callable);
        try {
            while (!future.isDone()) {
                Thread.sleep(400);
            }
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
