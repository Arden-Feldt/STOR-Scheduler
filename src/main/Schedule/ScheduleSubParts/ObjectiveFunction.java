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

  public ObjectiveFunction(Course[] courses, Faculty[] faculty, Room[] rooms, String[] timeSlots) {
    this.courses = courses;
    this.faculty = faculty;
    this.rooms = rooms;
    this.timeSlots = timeSlots;
  }

  public void initFunction(
      GRBModel model, GRBVar[][][][] assign, GRBVar[] gradCount)
      throws GRBException {
    try {

      GRBLinExpr expr = new GRBLinExpr();
      GRBLinExpr objective = new GRBLinExpr();

      // Add terms for assigning courses to time slots and rooms
      for (int i = 0; i < courses.length; i++) {
        for (int j = 0; j < faculty.length; j++) {
          for (int k = 0; k < timeSlots.length; k++) {
            for (int r = 0; r < rooms.length; r++) {
              expr.addTerm(faculty[j].getWillingness()[k], assign[i][j][k][r]);
            }
          }
        }
      }

      // Add terms for grad_count (slack variables) to penalize more graduate-level courses
      for (int k = 0; k < timeSlots.length; k++) {
        // You can apply a penalty term to the grad_count variable to penalize the number of grad courses
        objective.addTerm(GRADOVERLAPPENALTY, gradCount[k]);
      }

      if (assign == null || gradCount == null) {
        throw new IllegalArgumentException("Assign or gradCount is null.");
      }

      model.setObjective(expr, GRB.MINIMIZE);
    } catch (GRBException e) {
      e.printStackTrace();
    }
  }
}
