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
        GRBLinExpr expr = new GRBLinExpr();
        for (int i = 0; i < courses.length; i++) {
            for (int j = 0; j < faculty.length; j++) {
                for (int k = 0; k < timeSlots.length; k++) {
                    for (int r = 0; r < rooms.length; r++) {
                        expr.addTerm(faculty[j].getWillingness()[k], assign[i][j][k][r]);
                    }
                }
            }
        }


        GRBLinExpr obj = new GRBLinExpr();
        for (int i = 0; i < timeSlots.length; i++){
            obj.addTerm(GRADOVERLAPPENALTY, decisionVariables.gradCounterDecVar[i]);
        }

        // TODO: YOU SET THIS TO MIN BE CAREFUL
        model.setObjective(expr, GRB.MINIMIZE);
    }
}
