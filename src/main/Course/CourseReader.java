package main.Course;

import main.Faculty.FacultyManager;
import main.Faculty.GradStudent;
import main.Faculty.Professor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static main.Defaults.DEFAULTSECTIONSIZE;

public class CourseReader {
  private final String path;
  private final FacultyManager facultyManager;
  private final HashSet<Course> courses;
  private int numCourses = 0;

  public CourseReader(String path, FacultyManager facultyManager) {
    this.path = path;
    this.facultyManager = facultyManager;
    courses = new HashSet<Course>();
  }

  public void buildCourses() {
    String line;
    String delimiter = ",";

    List<String[]> data = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      String headerLine = br.readLine(); // Gets the header line out of the way

      while ((line = br.readLine()) != null) {
        String[] fields = line.split(delimiter);
        data.add(fields);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Now `data` contains the parsed data rows as arrays of strings
    System.out.println("Processing " + data.size() + " rows from CSV");
    for (String[] row : data) {
      if (row.length < 4) {
        System.err.println("Skipping row with insufficient columns (length " + row.length + "): " + Arrays.toString(row));
        continue;
      }
      
      String courseName = row[0].trim();
      int instructorCount = 0;
      
      // Instructors start at index 3 (after ClassNum, StudentNum, Recitations)
      // The header has "Instructors" at index 3, but the actual instructor data also starts there
      for (int i = 3; i < row.length; i++) {
        // Trim whitespace from instructor name
        String instructorName = row[i].trim();

        int sectionStudents = initSectionSize(row[2]);
        int totalStudents = initTotalStudents(row[1]);

        if (facultyManager.isProfessor(instructorName)) {
          Course newCourse = new Course(
              courseName, facultyManager.getProfessor(instructorName), totalStudents, sectionStudents);
          courses.add(newCourse);
          numCourses++;
          instructorCount++;
        } else if (instructorName.equalsIgnoreCase("NH")
            || instructorName.equalsIgnoreCase("DS")
            || instructorName.equalsIgnoreCase("NOASSIGNMENT")) { // TODO: Specific to STOR, make general
          Professor newProf = new Professor(instructorName);
          courses.add(new Course(courseName, newProf, totalStudents, sectionStudents));
          facultyManager.addProf(newProf);
          numCourses++;
          instructorCount++;
        } else if (instructorName.equalsIgnoreCase("GS")) {
          GradStudent gradStudent = new GradStudent(instructorName);
          courses.add(new Course(courseName, gradStudent, totalStudents, sectionStudents));
          facultyManager.addGrad(gradStudent);
          numCourses++;
          instructorCount++;
        } else if (instructorName.isEmpty()) {
          // Skip empty instructor fields
          continue;
        } else {
          // Auto-create professor if not found (for placeholder instructors like NewTAPDS1, etc.)
          System.out.println("Warning: Professor '" + instructorName + "' not found in preferences. Creating new professor.");
          Professor newProf = new Professor(instructorName);
          courses.add(new Course(courseName, newProf, totalStudents, sectionStudents));
          facultyManager.addProf(newProf);
          numCourses++;
          instructorCount++;
        }
        System.out.print(instructorName + "\t"); // Print each field (tab-separated)
      }
      if (instructorCount == 0) {
        System.err.println("Warning: Course '" + courseName + "' has no instructors assigned!");
      }
      System.out.println(); // Move to the next line for the next row
    }
    System.out.println("CourseReader finished. Total courses created: " + numCourses + ", Unique courses in set: " + courses.size());
  }

  private int initSectionSize(String section) {
    if (section.isEmpty()) {
      return DEFAULTSECTIONSIZE;
    } else {
      return Integer.parseInt(section);
    }
  }

  private int initTotalStudents(String section) {
    if (section.isEmpty()) {
      return -1; // TODO: This is ass
    } else {
      return Integer.parseInt(section);
    }
  }

  public HashSet<Course> getCourses() {
    return courses;
  }

  public void printCourses() {
    for (Course course : courses) {
      System.out.println(course.getName() + " " + course.getFaculty());
    }
  }

  public int getNumCourses() {
    return numCourses;
  }
}
