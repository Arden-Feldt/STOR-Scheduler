package main.Schedule.ScheduleSubParts;

import com.gurobi.gurobi.*;
import main.Course.Course;
import main.Course.Room;
import main.Faculty.Faculty;

import static main.Defaults.GRADOVERLAPPENALTY;

public class ObjectiveFunction {

    private final Course[] courses;
    private final Faculty[] faculty;
    private final Room[] rooms;
    private final String[] timeSlots;
    private final DecisionVariables decisionVariables; // REMOVE AFTER ENCAPSULATION!!

    public ObjectiveFunction(Course[] courses, Faculty[] faculty, Room[] rooms, String[] timeSlots, DecisionVariables decisionVariables) {
        this.courses = courses;
        this.faculty = faculty;
        this.rooms = rooms;
        this.timeSlots = timeSlots;
        this.decisionVariables = decisionVariables;
    }

    public void initFunction (GRBModel model, GRBVar[][][][] assign) throws GRBException {

        GRBLinExpr facultyWillingness = calculateFacultyWillingness(assign);
        GRBLinExpr timeslotPenalty = calculateTimeslotOverlapPenalty();

        GRBLinExpr totalObjective = new GRBLinExpr();
        totalObjective.add(facultyWillingness);
        totalObjective.add(timeslotPenalty);

        model.setObjective(totalObjective, GRB.MINIMIZE);
    }

    // Calculates the faculty willingness score
    private GRBLinExpr calculateFacultyWillingness(GRBVar[][][][] assign) throws GRBException {
        GRBLinExpr expr = new GRBLinExpr();
        for (int i = 0; i < courses.length; i++) {
            for (int j = 0; j < faculty.length; j++) {
                for (int k = 0; k < timeSlots.length; k++) {
                    for (int r = 0; r < rooms.length; r++) {
                        if (assign[i][j][k][r] == null) {
                            System.out.println("Null assign at: [" + i + "][" + j + "][" + k + "][" + r + "]");
                            throw new NullPointerException("Hey man assign as nulls in it, and idk why");
                        }
                        expr.addTerm(faculty[j].getWillingness()[k], assign[i][j][k][r]);
                    }
                }
            }
        }
        return expr;
    }

    // Calculates the penalty for overlapping timeslots
    private GRBLinExpr calculateTimeslotOverlapPenalty() throws GRBException {
        GRBLinExpr obj = new GRBLinExpr();
        for (int i = 0; i < timeSlots.length; i++) {
            if (decisionVariables.gradCounterDecVar[i] == null) {
                System.out.println("Null gradCounterDecVar at index: " + i);
                throw new NullPointerException("Grad counter is full of null!");
            }
            obj.addTerm(GRADOVERLAPPENALTY, decisionVariables.gradCounterDecVar[i]);
        }
        return obj;
    }

}
