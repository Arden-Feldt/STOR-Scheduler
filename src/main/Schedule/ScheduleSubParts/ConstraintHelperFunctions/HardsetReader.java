package main.Schedule.ScheduleSubParts.ConstraintHelperFunctions;

import main.Course.Course;
import main.Course.Room;
import main.Course.TimeSlot;
import main.Faculty.Faculty;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HardsetReader {
  private String path;
  private final Course[] courses;
  private final Faculty[] faculty;
  private final Room[] rooms;
  private final String[] timeSlots;
  private ArrayList<Course> courseList;
  private ArrayList<Faculty> facultyList;
    private ArrayList<Room> roomList;

    private ArrayList<String> timeSlotList;


  public HardsetReader(
      String path, Course[] courses, Faculty[] faculty, Room[] rooms, String[] timeSlots) {
    this.path = path;
    this.courses = courses;
    this.faculty = faculty;
    this.rooms = rooms;
    this.timeSlots = timeSlots;
  }

  public Map<Course, List<FacultyTimeslotRoom>> readCSV() throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      String line;

      // Create a map to store courseName -> List of FacultyTimeSlotRoom
      Map<Course, List<FacultyTimeslotRoom>> hardsetMap = new HashMap<>();

      // Skip the header row
      br.readLine();

      // Read each line
      while ((line = br.readLine()) != null) {
        // Split the line by commas
        String[] columns = line.split(",");

        if (columns.length == 4) {
          String courseName = columns[0].trim();
          String facultyName = columns[1].trim();
          String roomName = columns[3].trim();
          String timeSlot = columns[2].trim();


          courseList.add(getCourse(courseName));
          facultyList.add(getFaculty(facultyName));
          roomList.add(getRoom(roomName));
          timeSlotList.add(getTimeSlot(timeSlot));


          // Retrieve objects from your lists
          Faculty faculty = findFaculty(facultyName); // Implement this method to find faculty by name
          Room room = findRoom(roomName); // Implement this method to find room by name
          TimeSlot ts = findTimeSlot(timeSlot); // Implement this method to find timeSlot by name

          // Add course if not present in the map
          hardsetMap.putIfAbsent(getCourse(courseName), new ArrayList<>());

          // Create the FacultyTimeSlotRoom object and add to the list
          FacultyTimeslotRoom ftsr = new FacultyTimeslotRoom(faculty, room, ts);
          hardsetMap.get(courseName).add(ftsr);
        }
      }

      return hardsetMap;

    } catch (IOException e) {
      System.err.println("Error reading the file: " + e.getMessage());
      throw new IOException("Failed to read in hardsets");
    }
  }

    private Course getCourse(String name){
        for(Course course : courses){
            if (name.equals(course.getName())){
                return course;
            }
        }
        return null;
    }
    private Faculty getFaculty(String name){
        for(Faculty professor : faculty){
            if (name.equals(professor.getName())){
                return professor;
            }
        }
        return null;
    }

    private Room getRoom(String name){
        for(Room room : rooms){
            if (name.equals(room.name())){
                return room;
            }
        }
        return null;
    }

    private String getTimeSlot(String name){
        for(String timeSlot : timeSlots){
            if (name.equals(timeSlot)){
                return timeSlot;
            }
        }
        return null;
    }
}
