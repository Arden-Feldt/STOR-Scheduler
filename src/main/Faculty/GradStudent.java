package main.Faculty;

import java.util.Arrays;

import static main.Defaults.NUMTIMESLOTS;

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
