package Faculty;

import Course.TimeSlot;

import java.util.ArrayList;

public class Professor extends Faculty{
    private String name;
    private ArrayList<TimeSlot> timeSlots;
    private boolean backToBack;
    private boolean getsOpinion;

    public Professor(String name){
        this.name = name;
        this.getsOpinion = true;
    }

    public void setTimeSlots(ArrayList<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public void setBackToBack(boolean backToBack) {
        this.backToBack = backToBack;
    }

    public void setGetsOpinion(boolean getsOpinion) {
        this.getsOpinion = getsOpinion;
    }

    @Override
    public String toString() {
        return name;
    }
}
