package main;

import main.Course.CourseManager;
import main.ScheduleData.CourseUpdater;
import main.Course.Course;
import main.Course.CourseReader;
import main.Faculty.FacultyManager;
import main.Faculty.PreferenceReader;
import main.Faculty.Professor;
import main.ScheduleData.ScheduleDisplayer;


import java.util.Arrays;
import java.util.HashSet;


public class Main {
    public static void main(String[] args) {

        // TODO: ADD TESTS

        Builder builder = new Builder();
        builder.readDataIn("src/main/ProfessorData/ProPrefFalls23.csv", "src/main/ProfessorData/TeachingAssignmentsFall.csv");


        CourseManager courseManager = new CourseManager(builder.getCourseReader().getCourses());

        // init optimizer
        CourseScheduler courseScheduler = new CourseScheduler(builder.getFacultyManager(), courseManager, "src/main/ScheduleData/course_schedule.csv");
        // Run Optimizer
        courseScheduler.optimize();

        CourseUpdater courseUpdater = new CourseUpdater(courseScheduler.getOutput_path(), courseManager);
        courseUpdater.updateCourses();

        ScheduleDisplayer scheduleDisplayer = new ScheduleDisplayer("src/main/ScheduleData/legible_schedule.csv", courseManager);
        scheduleDisplayer.save_schedule();
    }
}