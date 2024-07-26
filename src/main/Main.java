package main;

import main.Course.CourseManager;
import main.Schedule.CourseScheduler;
import main.Schedule.CourseUpdater;
import main.Schedule.ScheduleDisplayer;

import static main.Defaults.*;

public class Main {

  public static void main(String[] args) {

    // TODO: ADD TESTS

    Builder builder = new Builder();
    builder.readDataIn(PREFRENCEPATH, COURSEDATAPATH);

    CourseManager courseManager = new CourseManager(builder.getCourseReader().getCourses());

    // init optimizer
    CourseScheduler courseScheduler =
        new CourseScheduler(builder.getFacultyManager(), courseManager, RAWSCHEDULEPATH);
    // Run Optimizer
    courseScheduler.optimize();

    CourseUpdater courseUpdater =
        new CourseUpdater(courseScheduler.getOutput_path(), courseManager);
    courseUpdater.updateCourses();

    ScheduleDisplayer scheduleDisplayer =
        new ScheduleDisplayer(DISPLAYSCHEDULECSVPATH, courseManager);
    scheduleDisplayer.save_schedule();
  }
}
