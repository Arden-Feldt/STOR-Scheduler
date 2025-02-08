package main.Schedule.ScheduleSubParts;

import com.gurobi.gurobi.*;
import main.Course.Course;
import main.Course.Room;
import main.Faculty.Faculty;

public class DecisionVariables {

  private final Course[] courses;
  private final Faculty[] faculty;
  private final Room[] rooms;
  private final String[] timeSlots;
  public GRBVar[] gradCounterDecVar;

  public DecisionVariables(Course[] courses, Faculty[] faculty, Room[] rooms, String[] timeSlots) {
    this.courses = courses;
    this.faculty = faculty;
    this.rooms = rooms;
    this.timeSlots = timeSlots;
    gradCounterDecVar = new GRBVar[timeSlots.length];
  }

  public void initiate(GRBModel model, GRBVar[][][][] assign) throws GRBException {
    for (int i = 0; i < courses.length; i++) {
      for (int j = 0; j < faculty.length; j++) {
        for (int k = 0; k < timeSlots.length; k++) {
          for (int r = 0; r < rooms.length; r++) {
            assign[i][j][k][r] =
                model.addVar(
                    0.0,
                    1.0,
                    0.0,
                    GRB.BINARY,
                    "Assign_"
                        + courses[i].getName()
                        + "_"
                        + faculty[j].getName()
                        + "_"
                        + timeSlots[k]
                        + "_"
                        + rooms[r].name());
          }
        }
      }
    }

    // Init Grad Counter
    this.gradCounterDecVar = initGradCounterDecVar(model, assign);
  }

  private GRBVar[] initGradCounterDecVar(GRBModel model, GRBVar[][][][] assign) throws GRBException {
    for (int t = 0; t < timeSlots.length; t++) {
      gradCounterDecVar[t] =
              model.addVar(0.0, courses.length, 0.0, GRB.INTEGER, "gradCounterDecVar: " + timeSlots[t]);

      if (gradCounterDecVar[t] == null) {
        System.out.println("Failed to initialize gradCounterDecVar[" + t + "]");
      } else {
        System.out.println("Initialized gradCounterDecVar[" + t + "]: " + gradCounterDecVar[t]);
      }

      // Constraint: sum of grad class over timeslots
      GRBLinExpr sumExpr = new GRBLinExpr();

      for (int i = 0; i < courses.length; i++) {
        for (int j = 0; j < faculty.length; j++) {
          for (int r = 0; r < rooms.length; r++) {

            if (courses[i].isGraduateCourse()) {  // Ensure only grad courses are counted
              if (assign[i][j][t][r] == null) {
                throw new NullPointerException("assign[i][j][t][r] is null for "
                        + courses[i].getName() + " in timeslot " + t);
              }
              sumExpr.addTerm(1.0, assign[i][j][t][r]);
            }

          }
        }
      }
      model.addConstr(gradCounterDecVar[t], GRB.EQUAL, sumExpr, "count_timeslot_" + t);
    }
    return gradCounterDecVar;
  }

}


