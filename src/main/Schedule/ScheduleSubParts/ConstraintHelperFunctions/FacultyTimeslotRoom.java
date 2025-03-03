package main.Schedule.ScheduleSubParts.ConstraintHelperFunctions;

import main.Course.Room;
import main.Course.TimeSlot;
import main.Faculty.Faculty;

public class FacultyTimeslotRoom {
    private Faculty faculty;
    private Room room;
    private TimeSlot timeSlot;

    // Constructor
    public FacultyTimeslotRoom(Faculty faculty, Room room, TimeSlot timeSlot) {
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

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }
}

