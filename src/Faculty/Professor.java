package Faculty;

import Course.TimeSlot;

public class Professor extends Faculty{
    private String name;
    private TimeSlot[] timeSlots;
    private boolean backToBack;
    private boolean getsOpinion;

    public Professor(String name){
        this.name = name;
        this.getsOpinion = true;
    }

    public void readInPrefrences() {
        ProfessorPreferenceReader professorPrefrenceReader = new ProfessorPreferenceReader("src/ProfessorData/ProfessorPrefrencesXLSXFall23.xlsx");
        this.timeSlots = null;
        this.backToBack = false;
    }

}
