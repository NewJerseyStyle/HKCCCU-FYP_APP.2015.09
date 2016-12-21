package com.matthew.slideshow.citypass;

/**
 * Created by matthew on 1/2/2016.
 */
public class Event {
    private long id;
    private String name;
    private int year;
    private int month;
    private int day;
    private int hour1;
    private int hour2;
    private int min1;
    private int min2;
    private String location;
    private int notify; //0 = false, 1 = true

    public Event() {
        this.id = 0;
        this.name = "default name";
        this.year = 0;
        this.month = 0;
        this.day = 0;
        this.hour1 = 0;
        this.hour2 = 0;
        this.min1 = 0;
        this.min2 = 0;
        this.location = "location";
        this.notify = 0;
    }

    public Event(long id, String name, int year, int month, int day, int hour1, int hour2, int min1, int min2, String location, int notidy) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour1 = hour1;
        this.hour2 = hour2;
        this.min1 = min1;
        this.min2 = min2;
        this.location = location;
        this.notify = notidy;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }


    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }


    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }


    public int getHour1() {
        return hour1;
    }

    public void setHour1(int hour) {
        this.hour1 = hour;
    }


    public int getHour2() {
        return hour2;
    }

    public void setHour2(int hour) {
        this.hour2 = hour;
    }


    public int getMin1() {
        return min1;
    }

    public void setMin1(int min) {
        this.min1 = min;
    }


    public int getMin2() {
        return min2;
    }

    public void setMin2(int min) {
        this.min2 = min;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getDate() {
        String _year;
        String _month;
        String _day;

        _year = Integer.toString(year);
        if (month < 10)
            _month = "0" + month;
        else
            _month = Integer.toString(month);

        if (day < 10)
            _day = "0" + day;
        else
            _day = Integer.toString(day);

        return _year + "-" + _month + "-" + _day;
    }


    public String getTime1() {
        String _hour1;
        String _min1;

        if (hour1 < 10)
            _hour1 = "0" + hour1;
        else
            _hour1 = Integer.toString(hour1);
        if (min1 < 10)
            _min1 = "0" + min1;
        else
            _min1 = Integer.toString(min1);

        return _hour1 + ':' + _min1;
    }


    public String getTime2() {
        String _hour2;
        String _min2;

        if (hour2 < 10)
            _hour2 = "0" + hour2;
        else
            _hour2 = Integer.toString(hour2);
        if (min2 < 10)
            _min2 = "0" + min2;
        else
            _min2 = Integer.toString(min2);

        return _hour2 + ':' + _min2;
    }


    public String getDateTime() {
        String date = getDate();
        String time = getTime1();
        return date + " " + time;
    }


    public int getNotify() {
        return this.notify;
    }

    public void setNotify(int a) {
        this.notify = a;
    }
}