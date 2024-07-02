package ScheduleData;
import Course.CourseReader;
import Course.Course;
import Course.Room;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import Course.TimeSlot;


public class CourseUpdater2 {
    private String path;
    private HashSet<Course> courses;

    public CourseUpdater2(String path, CourseReader courseReader) {
        this.path = path;
        this.courses = courseReader.getCourses();
    }

    public void updateCourses() {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            br.readLine(); // Skip the header

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length < 4) {
                    System.err.println("Skipping line due to missing data: " + line);
                    continue;
                }

                String courseName = values[0].trim();
                String professorName = values[1].trim();
                String timeSlot = values[2].trim();
                String roomName = values[3].trim();

                /*
                if (courseName.equals("155") && professorName.equals("OA")){
                    System.out.println();
                    System.out.println("sanity check ----------------------------------");
                    for (Course course : courses) {
                        if (course.getName().equalsIgnoreCase(courseName)) {
                            course.setTimeSlot(TimeSlot.valueOf(timeSlot));
                            course.setRoom(Room.valueOf(roomName));
                        }
                    }
                }
                */

                // Find the course by name
                for (Course course : courses) {
                    if (course.getName().equalsIgnoreCase(courseName)) {
                        course.setTimeSlot(TimeSlot.valueOf(timeSlot));
                        course.setRoom(Room.valueOf(roomName));
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

