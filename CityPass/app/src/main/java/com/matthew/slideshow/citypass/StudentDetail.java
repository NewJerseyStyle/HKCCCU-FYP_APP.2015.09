package com.matthew.slideshow.citypass;

/**
 * Created by matthew on 11/2/2016.
 */
public class StudentDetail {
    private String studentName;
    private String studentId;
    private String eid;
    private String email;
    private String department;
    private String major;
    private String programme;
    private String campus;
    private String AS;

    StudentDetail(String studentName, String studentId, String eid, String email,
                  String department, String major, String programme, String campus, String AS) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.eid = eid;
        this.email = email;
        this.department = department;
        this.major = major;
        this.programme = programme;
        this.campus = campus;
        this.AS = AS;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public String getEid() {
        return this.eid;
    }

    public String getEmail() {
        return this.email;
    }

    public String getDepartment() {
        return this.department;
    }

    public String getMajor() {
        return this.major;
    }

    public String getProgramme() {
        return this.programme;
    }

    public String getCampus() {
        return this.campus;
    }

    public String getAS() {
        return this.AS;
    }
}