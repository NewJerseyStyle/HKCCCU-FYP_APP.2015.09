package com.matthew.slideshow.citypass;

/**
 * Created by matthew on 11/2/2016.
 */
public class ExamInfo {
    private String courseName;
    private String date;
    private String time;
    private String place;
    private String seatNo;

    public ExamInfo(String courseName, String date, String time, String place, String seatNo) {
        this.courseName = courseName;
        this.date = "Date: " + date;
        this.time = "Time: " + time;
        this.place = "Place: " + place;
        this.seatNo = "Seat no: " + seatNo;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public String getDate() {
        return this.date;
    }

    public String getTime() {
        return this.time;
    }

    public String getPlace() {
        return this.place;
    }

    public String getSeatNo() {
        return this.seatNo;
    }

}
