package main.Schedule.ScheduleSubParts.ConstraintHelperFunctions;

import main.Course.Room;
import main.Course.TimeSlot;
import main.Faculty.Faculty;

public class FacultyTimeslotRoom {
    private Faculty faculty;
    private Room room;
    private String timeSlot;

    // Constructor
    public FacultyTimeslotRoom(Faculty faculty, Room room, String timeSlot) {
        this.faculty = faculty;
        this.room = room;
        this.timeSlot = timeSlot;
    }

    // Getters
    public Faculty getFaculty() {
        return faculty;
    }

    public Room getRoom() {
        return room;
    }

    public String getTimeSlot() {
        return timeSlot;
    }
}

