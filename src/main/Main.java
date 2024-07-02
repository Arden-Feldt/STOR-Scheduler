package main;

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

        PreferenceReader preferenceReader = new PreferenceReader("src/main/ProfessorData/ProPrefFalls23.csv");
        preferenceReader.buildProfessors();
        test_prof_impl(preferenceReader);

        FacultyManager facultyManager = new FacultyManager(preferenceReader.getProfessors());

        CourseReader courseReader = new CourseReader("src/main/ProfessorData/TeachingAssignmentsFall.csv",
                facultyManager);
        courseReader.buildCourses();
        test_course_impl(courseReader);

        // init optimizer
        CourseScheduler courseScheduler = new CourseScheduler(preferenceReader, courseReader, "src/main/ScheduleData/course_schedule.csv");
        // Run Optimizer
        courseScheduler.optimize();

        test_course_impl(courseReader);
        CourseUpdater courseUpdater = new CourseUpdater(courseScheduler.getOutput_path(), courseReader);
        courseUpdater.updateCourses();
        test_course_impl(courseReader);

        ScheduleDisplayer scheduleDisplayer = new ScheduleDisplayer("src/main/ScheduleData/legible_schedule.csv");
        scheduleDisplayer.save_schedule(courseReader.getCourses());
    }

    public static void test_prof_impl(PreferenceReader preferenceReader){
        HashSet<Professor> professors = preferenceReader.getProfessors();

        for (Professor professor: professors){
            System.out.println(professor.toString() +
                    Arrays.toString(professor.getWillingness()) +
                    professor.getBacktoBack() +
                    professor.getGetsOpinion());

        }
    }

    public static void test_course_impl(CourseReader courseReader){
        HashSet<Course> courses = courseReader.getCourses();

        for (Course course: courses){
            System.out.println(course.getName() +
                    ", with: " + course.getFaculty() +
                    " in " + course.getRoom() +
                    " at " + course.getTimeSlot());

        }
    }
}