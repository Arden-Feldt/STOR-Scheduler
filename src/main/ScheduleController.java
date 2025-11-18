package main;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/schedule")
@CrossOrigin(origins = "*")
public class ScheduleController {

  private final ScheduleService scheduleService;
  private static final String UPLOAD_DIR = "temp/uploads/";

  public ScheduleController(ScheduleService scheduleService) {
    this.scheduleService = scheduleService;
    // Ensure upload directory exists
    try {
      Path uploadPath = Paths.get(UPLOAD_DIR);
      if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Endpoint to trigger the course scheduling process
   * @return Response with scheduling results
   */
  @PostMapping("/run")
  public ResponseEntity<ScheduleService.ScheduleResponse> runScheduling() {
    ScheduleService.ScheduleResponse response = scheduleService.runScheduling();
    
    if (response.isSuccess()) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  /**
   * GET endpoint as an alternative to POST
   * @return Response with scheduling results
   */
  @GetMapping("/run")
  public ResponseEntity<ScheduleService.ScheduleResponse> runSchedulingGet() {
    return runScheduling();
  }

  /**
   * Endpoint to upload CSV files and run scheduling
   * @param preferenceFile The preference CSV file (required)
   * @param courseDataFile The course data CSV file (required)
   * @param hardsetFile The hardset CSV file (optional)
   * @param conflictFile The conflict CSV file (optional)
   * @return Response with scheduling results
   */
  @PostMapping("/upload-and-run")
  public ResponseEntity<Map<String, Object>> uploadAndRun(
      @RequestParam("preferenceFile") MultipartFile preferenceFile,
      @RequestParam("courseDataFile") MultipartFile courseDataFile,
      @RequestParam(value = "hardsetFile", required = false) MultipartFile hardsetFile,
      @RequestParam(value = "conflictFile", required = false) MultipartFile conflictFile) {
    
    Map<String, Object> response = new HashMap<>();
    
    if (preferenceFile.isEmpty() || courseDataFile.isEmpty()) {
      response.put("success", false);
      response.put("message", "Preference file and course data file are required");
      return ResponseEntity.badRequest().body(response);
    }

    try {
      // Save uploaded files
      String sessionId = UUID.randomUUID().toString();
      String preferencePath = UPLOAD_DIR + sessionId + "_preferences.csv";
      String courseDataPath = UPLOAD_DIR + sessionId + "_courseData.csv";

      Files.write(Paths.get(preferencePath), preferenceFile.getBytes());
      Files.write(Paths.get(courseDataPath), courseDataFile.getBytes());

      // Save optional files if provided
      String hardsetPath = null;
      String conflictPath = null;
      
      if (hardsetFile != null && !hardsetFile.isEmpty()) {
        hardsetPath = UPLOAD_DIR + sessionId + "_hardsets.csv";
        Files.write(Paths.get(hardsetPath), hardsetFile.getBytes());
        System.out.println("Hardset file uploaded: " + hardsetPath);
      }
      
      if (conflictFile != null && !conflictFile.isEmpty()) {
        conflictPath = UPLOAD_DIR + sessionId + "_conflicts.csv";
        Files.write(Paths.get(conflictPath), conflictFile.getBytes());
        System.out.println("Conflict file uploaded: " + conflictPath);
      }

      // Run scheduling
      ScheduleService.ScheduleResponse scheduleResponse = 
          scheduleService.runSchedulingWithFiles(preferencePath, courseDataPath, hardsetPath, conflictPath);

      if (scheduleResponse.isSuccess()) {
        response.put("success", true);
        response.put("message", scheduleResponse.getMessage());
        response.put("schedulePath", scheduleResponse.getDisplaySchedulePath());
        return ResponseEntity.ok(response);
      } else {
        response.put("success", false);
        response.put("message", scheduleResponse.getMessage());
        response.put("error", scheduleResponse.getMessage());
        // Return 200 so frontend can display the error message
        return ResponseEntity.ok(response);
      }
    } catch (IOException e) {
      response.put("success", false);
      response.put("message", "Error saving uploaded files: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("message", "Error during scheduling: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  /**
   * Endpoint to retrieve schedule data
   * @param schedulePath Path to the schedule file
   * @return Schedule data as JSON
   */
  @GetMapping("/schedule-data")
  public ResponseEntity<Map<String, Object>> getScheduleData(
      @RequestParam("schedulePath") String schedulePath) {
    
    Map<String, Object> response = new HashMap<>();
    
    try {
      List<Map<String, String>> scheduleData = scheduleService.getScheduleData(schedulePath);
      response.put("success", true);
      response.put("data", scheduleData);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("message", "Error reading schedule: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  /**
   * Endpoint to download the schedule CSV file
   * @param schedulePath Path to the schedule file
   * @return The CSV file as a download
   */
  @GetMapping("/download-schedule")
  public ResponseEntity<org.springframework.core.io.Resource> downloadSchedule(
      @RequestParam("schedulePath") String schedulePath) {
    
    try {
      Path filePath = Paths.get(schedulePath);
      org.springframework.core.io.Resource resource = 
          new org.springframework.core.io.UrlResource(filePath.toUri());
      
      if (resource.exists() && resource.isReadable()) {
        return ResponseEntity.ok()
            .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"schedule.csv\"")
            .contentType(org.springframework.http.MediaType.parseMediaType("text/csv"))
            .body(resource);
      } else {
        return ResponseEntity.notFound().build();
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Health check endpoint
   * @return Simple status message
   */
  @GetMapping("/health")
  public ResponseEntity<String> health() {
    return ResponseEntity.ok("Schedule API is running");
  }
}

