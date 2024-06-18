package Faculty;

import Course.TimeSlot;

import java.util.ArrayList;

public class Professor extends Faculty{
    private String name;
    private ArrayList<TimeSlot> timeSlots;

    private int[] willingness;
    private boolean backToBack;
    private boolean getsOpinion;

    public Professor(String name){
        this.name = name;
        this.getsOpinion = true;
        this.willingness = new int[18];
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

    public void setWillingness(int[] willingness) {
        this.willingness = willingness;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean getBacktoBack() {
        return super.getBacktoBack();
    }

    @Override
    public boolean getGetsOpinion() {
        return super.getGetsOpinion();
    }

    @Override
    public ArrayList<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public int[] getWillingness() {
        return willingness;
    }

    public boolean isGetsOpinion() {
        return getsOpinion;
    }

    @Override
    public String toString() {
        return name;
    }
}
