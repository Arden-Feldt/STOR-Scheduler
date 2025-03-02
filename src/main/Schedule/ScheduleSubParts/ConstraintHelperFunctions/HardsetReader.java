package main.Schedule.ScheduleSubParts.ConstraintHelperFunctions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class HardsetReader {
    private String path;

    public HardsetReader(String path) {
        this.path = path;
    }

    public Map<String, Map<String, String>> readCSV() throws IOException {
        // Create a BufferedReader to read the file
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            // Create a map to store (courseName -> faculty -> timeSlot)
            Map<String, Map<String, String>> hardsetMap = new HashMap<>();

            // Skip the header row
            br.readLine();

            // Read each line
            while ((line = br.readLine()) != null) {
                // Split the line by commas
                String[] columns = line.split(",");

                if (columns.length == 3) {
                    String courseName = columns[0].trim();
                    String facultyName = columns[1].trim();
                    String timeSlot = columns[2].trim();

                    // If the course name is not already in the map, add it
                    if (!hardsetMap.containsKey(courseName)) {
                        hardsetMap.put(courseName, new HashMap<>());
                    }

                    // Add the faculty and corresponding time slot
                    hardsetMap.get(courseName).put(facultyName, timeSlot);
                }
            }

            return hardsetMap;

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            throw new IOException("Failed to read in hardsets");
        }
    }
}
