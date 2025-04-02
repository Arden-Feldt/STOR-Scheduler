package main.Schedule.ScheduleSubParts.ConstraintHelperFunctions;

import main.Course.Course;
import main.Course.Room;
import main.Course.TimeSlot;
import main.Faculty.Faculty;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class HardsetReader {
    private String path;
    private final Course[] courses;
    private final Faculty[] faculty;
    private final Room[] rooms;
    private final String[] timeSlots;
    private final ArrayList<Course> courseList = new ArrayList<>();
    private final ArrayList<Faculty> facultyList = new ArrayList<>();
    private final ArrayList<Room> roomList = new ArrayList<>();
    private final ArrayList<String> timeSlotList = new ArrayList<>();

    public HardsetReader(String path, Course[] courses, Faculty[] faculty, Room[] rooms, String[] timeSlots) {
        this.path = path;
        this.courses = courses;
        this.faculty = faculty;
        this.rooms = rooms;
        this.timeSlots = timeSlots;
    }

    public Map<Course, List<FacultyTimeslotRoom>> readCSV() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;

            // Create a map to store (Course -> List of FacultyTimeslotRoom)
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
                    String roomName = columns[2].trim();
                    String timeSlotName = columns[3].trim();

                    // Find existing course, faculty, room, and timeSlot
                    Course course = findCourse(courseName);
                    Faculty faculty = findFaculty(facultyName);
                    Room room = findRoom(roomName);
                    String timeSlot = findTimeSlot(timeSlotName);

                    System.out.println(course + " by " + faculty + " at " + timeSlot + " in " + room);

                    // If course is not found, skip this row (to avoid null keys in the map)
                    if (course == null || faculty == null || room == null || timeSlot == null) {
                        System.err.println("Skipping invalid row: " + Arrays.toString(columns));
                        continue;
                    }

                    // Add course to list if not already present
                    if (!courseList.contains(course)) {
                        courseList.add(course);
                    }

                    // Add faculty to list if not already present
                    if (!facultyList.contains(faculty)) {
                        facultyList.add(faculty);
                    }

                    // Add room to list if not already present
                    if (!roomList.contains(room)) {
                        roomList.add(room);
                    }

                    // Add time slot to list if not already present
                    if (!timeSlotList.contains(timeSlot)) {
                        timeSlotList.add(timeSlot);
                    }

                    // Add course if not present in the map
                    hardsetMap.putIfAbsent(course, new ArrayList<>());

                    // Create and add FacultyTimeslotRoom object
                    FacultyTimeslotRoom ftsr = new FacultyTimeslotRoom(faculty, room, timeSlot);
                    hardsetMap.get(course).add(ftsr);
                    System.out.println(ftsr);
                }
            }

            return hardsetMap;

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            throw new IOException("Failed to read in hardsets", e);
        }
    }

    private Course findCourse(String name) {
        for (Course course : courses) {
            if (name.equals(course.getName())) {
                return course;
            }
        }
        return null;
    }

    private Faculty findFaculty(String name) {
        for (Faculty prof : faculty) {
            if (name.equals(prof.getName())) {
                return prof;
            }
        }
        return null;
    }

    private Room findRoom(String name) {
        for (Room room : rooms) {
            System.out.println(room + ", named: " + room.name());
            System.out.println("Comparing: ->" + name + "<- with ->" + room.name() + "<-");
            if (name.trim().equalsIgnoreCase(room.name().trim())) {
                return room;
            }
        }
        return null;
    }

    private String findTimeSlot(String name) {
        for (String timeSlot : timeSlots) {
            if (name.equals(timeSlot)) {
                return timeSlot;
            }
        }
        return null;
    }
}
