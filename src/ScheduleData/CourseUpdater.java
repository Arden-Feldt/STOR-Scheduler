package ScheduleData;
import Course.CourseReader;
import Course.Course;
import Course.Room;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import Course.TimeSlot;


public class CourseUpdater {
    private String path;
    private CourseReader courseReader;
    private HashSet<Course> courses;

    public CourseUpdater(String path, CourseReader courseReader) {
        this.path = path;
        this.courseReader = courseReader;
        this.courses = courseReader.getCourses();
    }

    public void updateCourses() {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            br.readLine(); // Skip the header

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String courseName = values[0].trim();
                String professorName = values[1].trim();
                String timeSlot = values[2].trim();
                String roomName = values[3].trim();

                // Find the course by name
                Course courseToUpdate = null;
                for (Course course : courses) {
                    if (course.getName().equals(courseName)) {
                        courseToUpdate = course;
                        break;
                    }
                }

                if (courseToUpdate != null) {
                    // Set the timeslot and room for the course
                    courseToUpdate.setTimeSlot(TimeSlot.valueOf(timeSlot));
                    courseToUpdate.setRoom(Room.valueOf(roomName));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
