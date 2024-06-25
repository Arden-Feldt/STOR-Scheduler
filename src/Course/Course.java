package Course;

import Faculty.Faculty;

public class Course{
    private String name;
    private TimeSlot time;
    private Faculty faculty;
    private Room room;
    private int totalStudents;

    public Course(String name, Faculty faculty, int totalStudents){
        this.name = name;
        this.faculty = faculty;
        this.totalStudents = totalStudents;
    }

    public String getName() {
        return name;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public Room getRoom() {
        return room;
    }

    public TimeSlot getTime() {
        return time;
    }

    public int getTotalStudents() {
        return totalStudents;
    }
}
