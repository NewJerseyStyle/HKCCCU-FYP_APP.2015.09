package com.matthew.slideshow.citypass;

/**
 * Created by yeung on 2/5/2016.
 */
public class Announcement {
    private String title;
    private String content;
    private String lastPost;
    private String course_code;

    public Announcement(String a, String b, String c) {
        title = a;
        content = b;
        lastPost = c;
    }


    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getLastPost() {
        return this.lastPost;
    }

    public void setLastPost() {
        this.lastPost = lastPost;
    }


    public String getCourse_code() {
        return this.course_code;
    }

    public void setCourse_code(String str) {
        this.course_code = str;
    }
}
