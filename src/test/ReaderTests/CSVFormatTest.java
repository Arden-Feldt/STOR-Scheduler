package test.ReaderTests;

import main.Builder;
import main.Course.Course;
import main.Course.CourseReader;
import main.Faculty.FacultyManager;
import main.Faculty.PreferenceReader;
import main.Faculty.Professor;
import org.junit.Test;
import static org.junit.Assert.*;

import static main.Defaults.*;

/**
 * Test to verify that the CSV files are formatted correctly and can be read
 */
public class CSVFormatTest {

    @Test
    public void testProfPref26CSV() {
        System.out.println("Testing profPref26.csv...");
        
        PreferenceReader preferenceReader = new PreferenceReader(PREFRENCEPATH, 3);
        preferenceReader.buildProfessors();
        
        assertNotNull("Professors should not be null", preferenceReader.getProfessors());
        assertFalse("Should have loaded professors", preferenceReader.getProfessors().isEmpty());
        
        System.out.println("Loaded " + preferenceReader.getProfessors().size() + " professors");
        
        for (Professor professor : preferenceReader.getProfessors()) {
            assertNotNull("Professor should not be null", professor);
            assertNotNull("Professor name should not be null", professor.getName());
            assertFalse("Professor name should not be empty", professor.getName().trim().isEmpty());
            assertNotNull("Willingness array should not be null", professor.getWillingness());
            assertEquals("Willingness array should have correct length", 
                18, professor.getWillingness().length);
        }
        
        System.out.println("✓ profPref26.csv is formatted correctly!");
    }

    @Test
    public void testClassAssignments26CSV() {
        System.out.println("Testing classAssignments26.csv...");
        
        // First load professors
        PreferenceReader preferenceReader = new PreferenceReader(PREFRENCEPATH, 3);
        preferenceReader.buildProfessors();
        FacultyManager facultyManager = new FacultyManager(preferenceReader.getProfessors());
        
        // Then load courses
        CourseReader courseReader = new CourseReader(COURSEDATAPATH, facultyManager);
        courseReader.buildCourses();
        
        assertNotNull("Courses should not be null", courseReader.getCourses());
        assertFalse("Should have loaded courses", courseReader.getCourses().isEmpty());
        
        System.out.println("Loaded " + courseReader.getCourses().size() + " courses");
        
        for (Course course : courseReader.getCourses()) {
            assertNotNull("Course should not be null", course);
            assertNotNull("Course name should not be null", course.getName());
            assertFalse("Course name should not be empty", course.getName().trim().isEmpty());
            assertNotNull("Course faculty should not be null", course.getFaculty());
        }
        
        System.out.println("✓ classAssignments26.csv is formatted correctly!");
    }

    @Test
    public void testFullBuilderIntegration() {
        System.out.println("Testing full Builder integration...");
        
        Builder builder = new Builder();
        builder.readDataIn(PREFRENCEPATH, COURSEDATAPATH);
        
        assertNotNull("Builder should have course reader", builder.getCourseReader());
        assertNotNull("Builder should have preference reader", builder.getPreferenceReader());
        assertNotNull("Builder should have faculty manager", builder.getFacultyManager());
        
        assertFalse("Should have loaded courses", 
            builder.getCourseReader().getCourses().isEmpty());
        assertFalse("Should have loaded professors", 
            builder.getPreferenceReader().getProfessors().isEmpty());
        
        System.out.println("✓ Full integration test passed!");
        System.out.println("  - Courses: " + builder.getCourseReader().getCourses().size());
        System.out.println("  - Professors: " + builder.getPreferenceReader().getProfessors().size());
    }
}


