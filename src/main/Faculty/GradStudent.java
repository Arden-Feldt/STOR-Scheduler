package main.Faculty;

import main.Course.TimeSlot;

import java.util.ArrayList;
import java.util.Arrays;

import static main.Paths.NUMTIMESLOTS;

public class GradStudent extends Faculty{
    private String name;
    private boolean getsOpinion = false;
    private boolean backToBack = true;
    private int[] willingness = new int[NUMTIMESLOTS];


    public GradStudent (String name) {
        super(name);
        this.name = name;
        Arrays.fill(this.willingness, 10);
    }

    @Override
    public int[] getWillingness() {
        return willingness;
    }
}
