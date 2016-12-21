package com.matthew.slideshow.citypass;

import java.util.ArrayList;

/**
 * Created by yeung on 2/5/2016.
 */
public class Course {
    private long course_id = 0;

    private String course_code;
    private String course_name;
    public ArrayList<Lesson> lesson;
    public int backgroundColor;
    private String gpa = "";

    public Course(String c_code) {
        course_code = c_code;
        lesson = new ArrayList<Lesson>();
    }


    public Course(String c_code, String c_name) {
        course_code = c_code;
        course_name = c_name;
        lesson = new ArrayList<Lesson>();
    }

    public void addLesson(String crn, String starttime, String days, String section, String location, int duration) {
        Lesson temp = new Lesson(crn, starttime, days, section, location, duration);
        temp.setLesson_course_id(course_id);
        lesson.add(temp);
    }


    public int getColor() {
        return backgroundColor;
    }

    public void setColor(int color) {
        backgroundColor = color;
    }

    public String getCourse_code() {
        return course_code;
    }

    public String getCourse_name() {
        return course_name;
    }

    public long getCourse_id() {
        return this.course_id;
    }

    public void setCourse_id(long a) {
        this.course_id = a;
    }

    public String getLessonSection(int index) {
        return lesson.get(index).getSection();
    }

    public String getLessonLocation(int index) {
        return lesson.get(index).getLocation();
    }

    public int getTimeShift(int index) {
        return lesson.get(index).getTimeShift();
    }
    //getTimeShift(int) is o transform the String time into index of the table

    public int getDaysShift(int index) {
        return lesson.get(index).getDaysShift();
    }
    //getDaysShift(int) is to transform the char days into index of the table

    public int getLessonDuration(int index) {
        return lesson.get(index).getDuration();
    }

    public String getGpa() {
        return this.gpa;
    }

    public void setGpa(String gpa) {
        this.gpa = gpa;
    }

    public static class Lesson {
        private long lesson_id = 0;
        private long lesson_course_id = 0;
        private String section;
        private String location;
        private String time;
        private char days; //Mon, Thue, Wed, Thus, Fri...
        private int duration;

        private String type;
        private String associated_term;
        private String crn;
        private String assigned_instructor;
        private String grade_mode;
        private String credits;
        private String level;
        private String campus;
        private String date_range;

        public Lesson(String crn, String starttime, String days, String section, String location, int duration) {
            this.crn = crn;
            this.time = starttime;
            this.days = days.charAt(0);
            this.section = section;
            this.location = location;
            this.duration = duration;
        }


        public Lesson(String s, String l, String t, char da, int d,
                      String type, String associated_term, String crn,
                      String assigned_instructor, String grade_mode, String credits, String level,
                      String campus, String date_range
        ) {
            section = s;
            location = l;
            time = t;
            days = da;
            duration = d;

            this.type = type;
            this.associated_term = associated_term;
            this.crn = crn;
            this.assigned_instructor = assigned_instructor;
            this.grade_mode = grade_mode;
            this.credits = credits;
            this.level = level;
            this.campus = campus;
            this.date_range = date_range;
        }


        public long getLesson_id() {
            return this.lesson_id;
        }

        public void setLesson_id(long a) {
            this.lesson_id = a;
        }

        public long getLesson_course_id() {
            return this.lesson_course_id;
        }

        public void setLesson_course_id(long a) {
            this.lesson_course_id = a;
        }

        public String getSection() {
            return section;
        }

        public void setSection(String s) {
            section = s;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String s) {
            location = s;
        }

        public String getTime() {
            return time;
        }


        public int getTimeShift() {
            if (time.charAt(0) == '0') {
                switch (time.charAt(1)) {
                    case '9':
                        return 1;
                }
            } else if (time.charAt(0) == '1') {
                switch (time.charAt(1)) {
                    case '0':
                        return 2;
                    case '1':
                        return 3;
                    case '2':
                        return 4;
                    case '3':
                        return 5;
                    case '4':
                        return 6;
                    case '5':
                        return 7;
                    case '6':
                        return 8;
                    case '7':
                        return 9;
                    case '8':
                        return 10;
                    case '9':
                        return 11;
                }
            } else if (time.charAt(0) == '2') {
                switch (time.charAt(1)) {
                    case '0':
                        return 12;
                    case '1':
                        return 13;
                    case '2':
                        return 14;
                }
            }
            return -1;
        }


        public void setTime(String s) {
            time = s;
        }

        public char getDays() {
            return days;
        }


        public int getDaysShift() {
            switch (days) {
                case '1':
                    return 1;
                case '2':
                    return 2;
                case '3':
                    return 3;
                case '4':
                    return 4;
                case '5':
                    return 5;
                case '6':
                    return 6;
                default:
                    return -1;
            }
        }


        public void setDays(char s) {
            days = s;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int i) {
            duration = i;
        }

        public String getType() {
            return type;
        }

        public void setType(String str) {
            type = str;
        }

        public String getAssociated_term() {
            return associated_term;
        }

        public void setAssociated_term(String str) {
            associated_term = str;
        }

        public String getCrn() {
            return crn;
        }

        public void setCrn(String str) {
            crn = str;
        }

        public String getAssigned_instructor() {
            return assigned_instructor;
        }

        public void setAssigned_instructor(String str) {
            assigned_instructor = str;
        }

        public String getGrade_mode() {
            return grade_mode;
        }

        public void setGrade_mode(String str) {
            grade_mode = str;
        }

        public String getCredits() {
            return credits;
        }

        public void setCredits(String str) {
            credits = str;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String str) {
            level = str;
        }

        public String getCampus() {
            return campus;
        }

        public void setCampus(String str) {
            campus = str;
        }

        public String getDate_range() {
            return date_range;
        }

        public void setDate_range(String str) {
            date_range = str;
        }
    }
}