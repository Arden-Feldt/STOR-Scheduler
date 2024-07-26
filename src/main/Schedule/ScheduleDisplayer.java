package main.Schedule;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;

import main.Course.Course;
import main.Course.CourseManager;
import main.Course.Room;
import main.Course.TimeSlot;


public class ScheduleDisplayer {
    private final String path;
    private final HashSet<Course> courses;
    private final int numCourses = 0;

    public ScheduleDisplayer(String path, CourseManager courseManager) {
        this.courses = courseManager.getCourses();
        this.path = path;
    }

    public void save_schedule() {
        // Initialize a map to store the schedule
        Map<String, Map<Room, String>> schedule = new HashMap<>();

        // Populate the schedule map with courses
        for (Course course : courses) {
            if (course.getTimeSlot() == null) {
                continue;
                // throw new NullPointerException("Course has not timeslot!: " + course.getName() + " with " + course.getFaculty());
            }
            String timeSlot = course.getTimeSlot().toString();
            Room room = course.getRoom();
            String value = course.getName() + " " + course.getFaculty().getName();

            if (!schedule.containsKey(timeSlot)) {
                schedule.put(timeSlot, new HashMap<>());
            }

            schedule.get(timeSlot).put(room, value);
        }

        // Write the schedule to a CSV file
        try (FileWriter writer = new FileWriter(path)) {
            // Write header row with room names
            writer.append("TimeSlot");
            for (Room room : Room.values()) {
                writer.append(",").append(room.name());
            }
            writer.append("\n");

            // Write each row for each timeslot
            for (TimeSlot timeSlot : TimeSlot.getAllTimeSlots()) {
                writer.append(timeSlot.name());
                Map<Room, String> roomAssignments = schedule.get(timeSlot.name());

                for (Room room : Room.values()) {
                    writer.append(",");
                    if (roomAssignments != null && roomAssignments.containsKey(room)) {
                        writer.append(roomAssignments.get(room));
                    }
                }
                writer.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNumCourses() {
        return -1;
        // return numCourses;
    }

    public int getCoursesSize(){
        return courses.size();
    }
}

