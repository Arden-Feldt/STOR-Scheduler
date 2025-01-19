package main.Schedule.ScheduleSubParts;

import com.gurobi.gurobi.*;
import main.Course.Course;
import main.Course.Room;
import main.Faculty.Faculty;

public class ObjectiveFunction {

  private final Course[] courses;
  private final Faculty[] faculty;
  private final Room[] rooms;
  private final String[] timeSlots;

  public ObjectiveFunction(Course[] courses, Faculty[] faculty, Room[] rooms, String[] timeSlots) {
    this.courses = courses;
    this.faculty = faculty;
    this.rooms = rooms;
    this.timeSlots = timeSlots;
  }

  public void initFunction(
      GRBModel model, GRBVar[][][][] assign, double penaltyFactor)
      throws GRBException {
    try {

      GRBLinExpr expr = new GRBLinExpr();
      GRBLinExpr objective = new GRBLinExpr();

      for (int i = 0; i < courses.length; i++) {
        for (int j = 0; j < faculty.length; j++) {
          for (int k = 0; k < timeSlots.length; k++) {
            for (int r = 0; r < rooms.length; r++) {
              expr.addTerm(faculty[j].getWillingness()[k], assign[i][j][k][r]);
            }
          }
        }
      }

      // Add penalty for graduate conflicts
      // Add conflict penalties
      for (int k = 0; k < timeSlots.length; k++) {
        for (int i = 0; i < courses.length; i++) {
          for (int j = i + 1; j < courses.length; j++) {
            // Graduate-level course check
            if ((courses[i].isGraduateCourse() ) && courses[j].isGraduateCourse()) {
              for (int r1 = 0; r1 < rooms.length; r1++) {
                for (int r2 = 0; r2 < rooms.length; r2++) {
                  // Add penalty for scheduling conflicts
                  objective.addTerm(penaltyFactor, assign[i][k][k][r1]); // Variable for course i
                  objective.addTerm(penaltyFactor, assign[j][k][k][r2]); // Variable for course j
                }
              }
            }
          }
        }
      }
      // TODO: YOU SET THIS TO MIN BE CAREFUL
      model.setObjective(expr, GRB.MINIMIZE);
    } catch (GRBException e) {
      e.printStackTrace();
    }
  }
}
