package test.ReaderTests;

import main.Course.Course;
import main.Course.CourseReader;
import main.Faculty.FacultyManager;
import main.Faculty.PreferenceReader;
import main.Main;
import org.junit.Test;
import static org.junit.Assert.*;


import static main.Paths.COURSEDATAPATH;
import static main.Paths.PREFRENCEPATH;

public class CourseReaderTests {
    private String[] avalibleCourses = {"113", "120", "FYL20", "151", "155", "215", "305", "315", "320", "415", "435",
            "445", "455", "471", "475", "520", "535", "555", "557", "565", "566", "612", "634", "641", "654", "664",
            "701", "702", "765", "743", "8XX", "8XX"};

    @Test
    public void confirmAllCoursesAreScheduled() {
        boolean allCoursesRead = true;
        PreferenceReader preferenceReader = new PreferenceReader(PREFRENCEPATH);
        preferenceReader.buildProfessors();

        FacultyManager facultyManager = new FacultyManager(preferenceReader.getProfessors());

        CourseReader courseReader = new CourseReader(COURSEDATAPATH, facultyManager);
        courseReader.buildCourses();

        for (String avalibileCourse : avalibleCourses){
            boolean found = false;
            for (Course readCourse : courseReader.getCourses()){
                if (readCourse.getName().equals(avalibileCourse)){
                    found = true;
                }
            }
            if (!found){
                allCoursesRead = false;
                System.out.println("Failed to find: " + avalibileCourse);
            }
        }
        assertTrue(allCoursesRead);
    }

}
