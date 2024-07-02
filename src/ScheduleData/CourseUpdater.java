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

            HashSet<Course> updatedCourses = new HashSet<>();

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

                // Find the course by name and professor, and check if it has been updated already
                Course courseToUpdate = null;
                for (Course course : courses) {
                    if (course.getName().equalsIgnoreCase(courseName) &&
                            course.getFaculty().getName().equals(professorName) &&
                            !updatedCourses.contains(course)) {
                        courseToUpdate = course;
                        updatedCourses.add(course);
                        break;
                    }
                }

                if (courseToUpdate != null) {
                    // Set the timeslot and room for the course
                    try {
                        courseToUpdate.setTimeSlot(TimeSlot.valueOf(timeSlot));
                        courseToUpdate.setRoom(Room.valueOf(roomName));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid room name: " + roomName);
                    }
                } else {
                    System.err.println("Course not found or already updated: " + courseName + " with " + professorName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void printCourses(){
        for (Course course : courses){
            System.out.println(course.getName() + " " + course.getFaculty());
        }
    }
}
