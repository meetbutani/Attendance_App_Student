package com.meetbutani.attendanceapp.ModelClass;

import java.io.Serializable;

public class ModelCourse implements Serializable {

    public String courseName, courseId;

    public ModelCourse() {
    }

    public ModelCourse(String courseName, String courseId) {
        this.courseName = courseName;
        this.courseId = courseId;
    }
}