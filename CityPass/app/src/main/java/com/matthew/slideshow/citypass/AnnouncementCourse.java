package com.matthew.slideshow.citypass;

/**
 * Created by yeung on 4/23/2016.
 */
public class AnnouncementCourse {
    private String course_code = "";
    private String course_name = "";

    AnnouncementCourse(String a, String b) {
        course_code = a;
        course_name = b;
    }

    public String getCourse_code() {
        return this.course_code;
    }

    public void setCourse_code(String str) {
        this.course_code = str;
    }

    public String getCourse_name() {
        return this.course_name;
    }

    public void setCourse_name(String str) {
        this.course_name = str;
    }
}
