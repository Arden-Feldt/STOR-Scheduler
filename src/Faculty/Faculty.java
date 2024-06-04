package Faculty;

import Course.TimeSlot;

public class Faculty {
    private String name;
    private TimeSlot[] timeSlots;
    private boolean backToBack;
    private boolean getsOpinion;

    public String getName() {
        return name;
    }

    public TimeSlot[] getTimeSlots() {
        return timeSlots;
    }

    public boolean getBacktoBack() {
        return backToBack;
    }

    public boolean getGetsOpinion() {
        return getsOpinion;
    }
}
