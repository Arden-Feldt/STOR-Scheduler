package main.Course;

import java.util.ArrayList;
import java.util.HashSet;

public class CourseManager {
    private HashSet<Course> courses;
    public CourseManager(HashSet<Course> courses){
        this.courses = courses;
    }

    public HashSet<Course> getCourses() {
        return courses;
    }

    public void printCourses(){
        for (Course course : courses){
            System.out.println(course.getName() + " " + course.getFaculty());
        }
    }

    public Course[] getCourseArray(){
        return courses.toArray(new Course[0]);
    }
}
