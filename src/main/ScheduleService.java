package main;

import main.Course.CourseManager;
import main.Schedule.CourseScheduler;
import main.Schedule.CourseUpdater;
import main.Schedule.ScheduleDisplayer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.HashSet;
import main.Course.Course;

import static main.Defaults.*;

public class ScheduleService {

  private String lastSchedulePath = null;

  /**
   * Runs the complete course scheduling process
   * @return A response message indicating success or failure
   */
  public ScheduleResponse runScheduling() {
    try {
      Builder builder = new Builder();
      builder.readDataIn(PREFRENCEPATH, COURSEDATAPATH);

      CourseManager courseManager = new CourseManager(builder.getCourseReader().getCourses());

      // init optimizer
      CourseScheduler courseScheduler =
          new CourseScheduler(builder.getFacultyManager(), courseManager, RAWSCHEDULEPATH);
      // Run Optimizer
      courseScheduler.optimize();

      CourseUpdater courseUpdater =
          new CourseUpdater(courseScheduler.getOutput_path(), courseManager);
      courseUpdater.updateCourses();

      ScheduleDisplayer scheduleDisplayer =
          new ScheduleDisplayer(DISPLAYSCHEDULECSVPATH, courseManager);
      scheduleDisplayer.save_schedule();

      lastSchedulePath = DISPLAYSCHEDULECSVPATH;

      return new ScheduleResponse(
          true,
          "Scheduling completed successfully",
          RAWSCHEDULEPATH,
          DISPLAYSCHEDULECSVPATH);
    } catch (Exception e) {
      return new ScheduleResponse(
          false,
          "Error during scheduling: " + e.getMessage(),
          null,
          null);
    }
  }

  /**
   * Runs the complete course scheduling process with uploaded files
   * @param preferenceFilePath Path to the preference CSV file
   * @param courseDataFilePath Path to the course data CSV file
   * @param hardsetFilePath Optional path to the hardset CSV file (can be null)
   * @param conflictFilePath Optional path to the conflict CSV file (can be null)
   * @return A response message indicating success or failure
   */
  public ScheduleResponse runSchedulingWithFiles(String preferenceFilePath, String courseDataFilePath,
                                                 String hardsetFilePath, String conflictFilePath) {
    try {
      // Create temporary output paths
      String sessionId = UUID.randomUUID().toString();
      String rawSchedulePath = "temp/" + sessionId + "_raw_schedule.csv";
      String displaySchedulePath = "temp/" + sessionId + "_display_schedule.csv";

      // Ensure temp directory exists
      Path tempDir = Paths.get("temp");
      if (!Files.exists(tempDir)) {
        Files.createDirectories(tempDir);
      }

      // Verify files exist
      Path prefPath = Paths.get(preferenceFilePath);
      Path coursePath = Paths.get(courseDataFilePath);
      
      if (!Files.exists(prefPath)) {
        return new ScheduleResponse(
            false,
            "Preference file not found: " + preferenceFilePath,
            null,
            null);
      }
      
      if (!Files.exists(coursePath)) {
        return new ScheduleResponse(
            false,
            "Course data file not found: " + courseDataFilePath,
            null,
            null);
      }

      System.out.println("Reading preference file: " + preferenceFilePath);
      System.out.println("Reading course data file: " + courseDataFilePath);

      Builder builder = new Builder();
      builder.readDataIn(preferenceFilePath, courseDataFilePath);

      HashSet<Course> courses = builder.getCourseReader().getCourses();
      System.out.println("Number of courses loaded: " + courses.size());
      
      if (courses.isEmpty()) {
        return new ScheduleResponse(
            false,
            "No courses were loaded from the files. Please check the file format.",
            null,
            null);
      }

      CourseManager courseManager = new CourseManager(courses);

      // init optimizer with optional hardset and conflict files
      CourseScheduler courseScheduler;
      if (hardsetFilePath != null || conflictFilePath != null) {
        courseScheduler = new CourseScheduler(
            builder.getFacultyManager(), courseManager, rawSchedulePath,
            hardsetFilePath, conflictFilePath);
      } else {
        courseScheduler = new CourseScheduler(
            builder.getFacultyManager(), courseManager, rawSchedulePath);
      }
      // Run Optimizer
      System.out.println("Running optimizer...");
      try {
        courseScheduler.optimize();
      } catch (RuntimeException e) {
        return new ScheduleResponse(
            false,
            "Optimizer failed: " + e.getMessage(),
            null,
            null);
      }

      // Verify output file was created
      Path outputFile = Paths.get(rawSchedulePath);
      if (!Files.exists(outputFile)) {
        return new ScheduleResponse(
            false,
            "Optimizer did not create output file. The Gurobi license may be expired or there was an error during optimization.",
            null,
            null);
      }

      CourseUpdater courseUpdater =
          new CourseUpdater(courseScheduler.getOutput_path(), courseManager);
      try {
        courseUpdater.updateCourses();
      } catch (Exception e) {
        return new ScheduleResponse(
            false,
            "Error updating courses: " + e.getMessage() + ". The optimizer output file may be missing or invalid.",
            null,
            null);
      }

      ScheduleDisplayer scheduleDisplayer =
          new ScheduleDisplayer(displaySchedulePath, courseManager);
      scheduleDisplayer.save_schedule();

      lastSchedulePath = displaySchedulePath;

      System.out.println("Schedule generated successfully at: " + displaySchedulePath);

      return new ScheduleResponse(
          true,
          "Scheduling completed successfully",
          rawSchedulePath,
          displaySchedulePath);
    } catch (Exception e) {
      e.printStackTrace();
      String errorMsg = "Error during scheduling: " + e.getMessage();
      if (e.getCause() != null) {
        errorMsg += " (Cause: " + e.getCause().getMessage() + ")";
      }
      return new ScheduleResponse(
          false,
          errorMsg,
          null,
          null);
    }
  }

  /**
   * Reads the schedule CSV file and returns it as a list of maps
   * @param schedulePath Path to the schedule CSV file
   * @return List of maps representing the schedule rows
   */
  public List<Map<String, String>> getScheduleData(String schedulePath) {
    List<Map<String, String>> scheduleData = new ArrayList<>();
    
    if (schedulePath == null || !Files.exists(Paths.get(schedulePath))) {
      return scheduleData;
    }

    try (BufferedReader br = new BufferedReader(new FileReader(schedulePath))) {
      String headerLine = br.readLine();
      if (headerLine == null) {
        return scheduleData;
      }

      String[] headers = headerLine.split(",");
      
      String line;
      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");
        Map<String, String> row = new LinkedHashMap<>();
        
        for (int i = 0; i < headers.length && i < values.length; i++) {
          row.put(headers[i].trim(), values[i].trim());
        }
        
        scheduleData.add(row);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return scheduleData;
  }

  /**
   * Response class for scheduling operations
   */
  public static class ScheduleResponse {
    private boolean success;
    private String message;
    private String rawSchedulePath;
    private String displaySchedulePath;

    public ScheduleResponse(
        boolean success, String message, String rawSchedulePath, String displaySchedulePath) {
      this.success = success;
      this.message = message;
      this.rawSchedulePath = rawSchedulePath;
      this.displaySchedulePath = displaySchedulePath;
    }

    public boolean isSuccess() {
      return success;
    }

    public void setSuccess(boolean success) {
      this.success = success;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public String getRawSchedulePath() {
      return rawSchedulePath;
    }

    public void setRawSchedulePath(String rawSchedulePath) {
      this.rawSchedulePath = rawSchedulePath;
    }

    public String getDisplaySchedulePath() {
      return displaySchedulePath;
    }

    public void setDisplaySchedulePath(String displaySchedulePath) {
      this.displaySchedulePath = displaySchedulePath;
    }
  }
}

