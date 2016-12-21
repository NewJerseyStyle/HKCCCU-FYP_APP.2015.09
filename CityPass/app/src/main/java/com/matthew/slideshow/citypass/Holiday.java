package com.matthew.slideshow.citypass;


/**
 * Created by John on 2016/2/24.
 */
public class Holiday {
    public String holiday_or_event_name;
    public String date_detail;
    public int month;
    public int year;
    public String type;

    public Holiday() {
        super();
    }

    public Holiday(String holiday_or_event_name, String date_detail, int month, int year, String type) {
        super();
        this.holiday_or_event_name = holiday_or_event_name;
        this.month = month;
        this.date_detail = date_detail;
        this.year = year;
        this.type = type;
    }

    public String getName() {
        return this.holiday_or_event_name;
    }

    public void setName(String str) {
        this.holiday_or_event_name = str;
    }

    public String getDateDetail() {
        return this.date_detail;
    }

    public void setDateDetail(String str) {
        this.date_detail = str;
    }

    public int getMonth() {
        return this.month;
    }

    public void setMonth(int a) {
        this.month = a;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int a) {
        this.year = a;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String str) {
        this.type = str;
    }

}
