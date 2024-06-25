package javaWork;

import Course.TimeSlot;

import java.util.ArrayList;

public class Faculty {
    private String name;
    private ArrayList<TimeSlot> timeSlots;
    private boolean backToBack;
    private boolean getsOpinion;

    public String getName() {
        return name;
    }

    public ArrayList<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public boolean getBacktoBack() {
        return backToBack;
    }

    public boolean getGetsOpinion() {
        return getsOpinion;
    }
}
