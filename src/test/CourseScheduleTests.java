package test;

import main.Course.CourseManager;
import main.Course.CourseReader;
import main.Faculty.FacultyManager;
import main.Faculty.PreferenceReader;
import main.Faculty.Professor;
import main.Schedule.CourseScheduler;
import main.Schedule.CourseUpdater;
import main.Schedule.ScheduleDisplayer;
import org.junit.Test;

import static main.Paths.*;
import static org.junit.Assert.assertEquals;

public class CourseScheduleTests {
    @Test
    public void right_num_courses() {
        PreferenceReader preferenceReader = new PreferenceReader(PREFRENCEPATH);
        preferenceReader.buildProfessors();

        FacultyManager facultyManager = new FacultyManager(preferenceReader.getProfessors());

        CourseReader courseReader = new CourseReader(COURSEDATAPATH, facultyManager);
        courseReader.buildCourses();

        int initial_num_courses = courseReader.getNumCourses();

        CourseManager courseManager = new CourseManager(courseReader.getCourses());

        int firstReadNumCourses = courseManager.getNumCourses();


        CourseScheduler courseScheduler = new CourseScheduler(facultyManager, courseManager, RAWSCHEDULEPATH);
        // Run Optimizer
        courseScheduler.optimize();

        CourseUpdater courseUpdater = new CourseUpdater(courseScheduler.getOutput_path(), courseManager);
        courseUpdater.updateCourses();

        int updatedNumCourses = courseUpdater.getNumCoursesUpdated();

        ScheduleDisplayer scheduleDisplayer = new ScheduleDisplayer(DISPLAYSCHEDULECSVPATH, courseManager);
        int numDisplayedCourses = scheduleDisplayer.getCoursesSize();

        assertEquals(initial_num_courses, firstReadNumCourses);
        assertEquals(firstReadNumCourses, updatedNumCourses);
        assertEquals(updatedNumCourses, numDisplayedCourses);

        System.out.println("Num courses: " + numDisplayedCourses);
  }
}
