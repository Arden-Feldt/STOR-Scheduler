import com.gurobi.gurobi.*;
import Course.Course;
import Course.CourseReader;
import Faculty.FacultyManager;
import Faculty.PreferenceReader;
import Faculty.Professor;


import java.util.Arrays;
import java.util.HashSet;


public class Main {
    public static void main(String[] args) {

        PreferenceReader preferenceReader = new PreferenceReader("src/ProfessorData/ProPrefFalls23.csv");
        preferenceReader.buildProfessors();
        test_prof_impl(preferenceReader);

        FacultyManager facultyManager = new FacultyManager(preferenceReader.getProfessors());

        CourseReader courseReader = new CourseReader("src/ProfessorData/TeachingAssignmentsFall.csv",
                facultyManager);
        courseReader.buildCourses();
        test_course_impl(courseReader);

        // TODO: implement Gurobi java API
        CourseScheduler courseScheduler = new CourseScheduler(preferenceReader, courseReader);
        courseScheduler.optimize();

        // TODO: Run optimizer

        // TODO: output optimal solution in legible format


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
            System.out.println(course.getName() + ", with: " + course.getFaculty() + course.getTotalStudents());

        }
    }
}