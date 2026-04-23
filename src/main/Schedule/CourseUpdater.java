package main.Schedule;

import main.Course.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class CourseUpdater {
  private final String path;
  private final HashSet<Course> courses;
  private int numCoursesUpdated = 0;

  public CourseUpdater(String path, CourseManager courseManager) {
    this.path = path;
    this.courses = courseManager.getCourses();
  }

  public void updateCourses() {
    System.out.println("Starting CourseUpdater. Total courses to update: " + courses.size());
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      String line;
      br.readLine(); // Skip the header

      HashSet<Course> updatedCourses = new HashSet<>();
      int linesProcessed = 0;

      while ((line = br.readLine()) != null) {
        linesProcessed++;
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
          if (course.getName().equalsIgnoreCase(courseName)
              && course.getFaculty().getName().trim().equalsIgnoreCase(professorName.trim())
              && !updatedCourses.contains(course)) {
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
            numCoursesUpdated++;
            System.out.println("Updated: " + courseName + " with " + professorName + " at " + timeSlot + " in " + roomName);
          } catch (IllegalArgumentException e) {
            System.err.println("Invalid timeSlot or room name: " + timeSlot + " / " + roomName + " - " + e.getMessage());
          }
        } else {
          System.err.println(
              "Course not found or already updated: " + courseName + " with " + professorName);
          // Debug: show available courses with this name
          System.err.println("Available courses with name '" + courseName + "':");
          for (Course course : courses) {
            if (course.getName().equalsIgnoreCase(courseName)) {
              System.err.println("  - " + course.getName() + " with " + course.getFaculty().getName());
            }
          }
        }
      }
      System.out.println("CourseUpdater finished. Processed " + linesProcessed + " lines, updated " + numCoursesUpdated + " courses.");
      System.out.println("Total courses in set: " + courses.size() + ", Updated: " + numCoursesUpdated);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public int getNumCoursesUpdated() {
    return numCoursesUpdated;
  }
}
