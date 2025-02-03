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

      GRBLinExpr profPref = new GRBLinExpr();
      GRBLinExpr gradOverlap = new GRBLinExpr();

      // Add terms for assigning courses to time slots and rooms
      for (int i = 0; i < courses.length; i++) {
        for (int j = 0; j < faculty.length; j++) {
          for (int k = 0; k < timeSlots.length; k++) {
            for (int r = 0; r < rooms.length; r++) {
              if (assign[i][j][k][r] == null) {
                System.out.println("assign[" + i + "][" + j + "][" + k + "][" + r + "] is null.");
              } else {
                profPref.addTerm(faculty[j].getWillingness()[k], assign[i][j][k][r]);
              }
            }
          }
        }
      }


      // Add terms for grad_count (slack variables) to penalize more graduate-level courses
      for (int k = 0; k < timeSlots.length; k++) {
        // Apply a penalty term to the grad_count variable to penalize the number of grad courses
        if (gradCount[k] != null) {
          gradOverlap.addTerm(GRADOVERLAPPENALTY, gradCount[k]);
        } else {
          System.out.println("gradCount[" + k + "] is null");
        }
      }

      if (assign == null || gradCount == null) {
        throw new IllegalArgumentException("Assign or gradCount is null.");
      }

      profPref.add(gradOverlap);

      model.setObjective(profPref, GRB.MINIMIZE);

    } catch (GRBException e) {
      e.printStackTrace();
    }
  }
}
