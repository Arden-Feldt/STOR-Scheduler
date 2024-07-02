package main;

import main.Course.CourseManager;
import main.Schedule.CourseScheduler;
import main.Schedule.CourseUpdater;
import main.Schedule.ScheduleDisplayer;


public class Main {
    public static void main(String[] args) {

        // TODO: ADD TESTS

        Builder builder = new Builder();
        builder.readDataIn("src/main/Faculty/ProfessorData/ProPrefFalls23.csv", "src/main/Faculty/ProfessorData/TeachingAssignmentsFall.csv");


        CourseManager courseManager = new CourseManager(builder.getCourseReader().getCourses());

        // init optimizer
        CourseScheduler courseScheduler = new CourseScheduler(builder.getFacultyManager(), courseManager, "src/main/Schedule/course_schedule.csv");
        // Run Optimizer
        courseScheduler.optimize();

        CourseUpdater courseUpdater = new CourseUpdater(courseScheduler.getOutput_path(), courseManager);
        courseUpdater.updateCourses();

        ScheduleDisplayer scheduleDisplayer = new ScheduleDisplayer("src/main/Schedule/legible_schedule.csv", courseManager);
        scheduleDisplayer.save_schedule();
    }
}