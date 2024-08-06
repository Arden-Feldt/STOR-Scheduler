package main;

import main.Course.CourseReader;
import main.Faculty.FacultyManager;
import main.Faculty.PreferenceReader;

public class Builder {
  private PreferenceReader preferenceReader;
  private FacultyManager facultyManager;
  private CourseReader courseReader;

  public Builder() {}

  public void readDataIn(String prefrencesPath, String courseDataPath) {
    PreferenceReader preferenceReader = new PreferenceReader(prefrencesPath, 3);
    preferenceReader.buildProfessors();

    FacultyManager facultyManager = new FacultyManager(preferenceReader.getProfessors());

    CourseReader courseReader = new CourseReader(courseDataPath, facultyManager);
    courseReader.buildCourses();

    this.preferenceReader = preferenceReader;
    this.facultyManager = facultyManager;
    this.courseReader = courseReader;
  }

  public CourseReader getCourseReader() {
    return courseReader;
  }

  public PreferenceReader getPreferenceReader() {
    return preferenceReader;
  }

  public FacultyManager getFacultyManager() {
    return facultyManager;
  }
}
