package com.meetbutani.attendanceapp.ModelClass;

import java.io.Serializable;

public class ModelCourse implements Serializable {

    public String courseName, courseId, Class;

    public ModelCourse() {
    }

    public ModelCourse(String courseName, String courseId, String Class) {
        this.courseName = courseName;
        this.courseId = courseId;
        this.Class = Class;
    }
}
