package main.Schedule.ScheduleSubParts.ConstraintHelperFunctions;

import main.Course.Course;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ConflictReader {

    private String path;

    public ConflictReader(String path){
        this.path = path;
    }

  public HashMap<String, String> readCSV() throws IOException {
    System.out.println("Start Conflict read csv");
    HashMap<String, String> conflictClasses = new HashMap<>();

    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      String line;

      // Skip the header row
      br.readLine();

      // Read each line
      while ((line = br.readLine()) != null) {
        // Split the line by commas
        String[] columns = line.split(",");

        if (columns.length == 2) {
            conflictClasses.put(columns[0], columns[1]);
        }
      }

      return conflictClasses;

    } catch (IOException e) {
      System.err.println("Error reading the file: " + e.getMessage());
      throw new IOException("Failed to read in conflicts ", e);
    }
  }
}
