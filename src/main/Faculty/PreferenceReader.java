package main.Faculty;

import main.Course.TimeSlot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class PreferenceReader {

  private final String path;
  private HashSet<Professor> professors;

  public PreferenceReader(String path) {
    this.path = path;
  }

  public void buildProfessors() {
    this.professors = new HashSet<>();
    String[] timeslotstrings = getTIMESLOTSTRINGS();

    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      String headerLine = br.readLine(); // Gets the header line out of the way

      String line;
      while ((line = br.readLine()) != null) {
        System.out.println(line); // Bugtesting

        String[] value = line.split(",");

        // Init prof
        Professor professor = new Professor(value[1]);
        professors.add(professor);

        // Build body for friends
        ArrayList<TimeSlot> timeslots = new ArrayList<>();
        int[] willingness = new int[15]; // TODO: Remove hardcoding

        for (int i = 3; i <= 17; i++) { // TODO: Remove hardcoding
          if (value[i].isEmpty() || value[i] == null) {
            willingness[i - 3] = 0;
          } else {
            try {
              willingness[i - 3] = Integer.parseInt(value[i]); // Use Integer.parseInt
              if (willingness[i - 3] > 0) {
                timeslots.add(TimeSlot.valueOf(timeslotstrings[i - 3]));
              }
            } catch (NumberFormatException e) {
              willingness[i - 3] = 0; // Handle invalid integer format
            }
          }
        }

        professor.setWillingness(willingness);
        professor.setTimeSlots(timeslots);
        professor.setBackToBack((value[2]).equals("1"));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public HashSet<Professor> getProfessors() {
    return professors;
  }

  public String[] getTIMESLOTSTRINGS() {
    ArrayList<String> result = new ArrayList();
    for (TimeSlot timeSlot : TimeSlot.values()) {
      result.add(timeSlot.name());
    }
    return result.toArray(new String[0]);
  }

  public String getPath() {
    return path;
  }
}
