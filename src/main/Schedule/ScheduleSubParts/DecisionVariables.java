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
  }
}


