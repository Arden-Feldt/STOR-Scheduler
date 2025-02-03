package main.Schedule.ScheduleSubParts;

import com.gurobi.gurobi.GRB;
import com.gurobi.gurobi.GRBException;
import com.gurobi.gurobi.GRBModel;
import com.gurobi.gurobi.GRBVar;
import main.Course.Course;
import main.Course.CourseManager;
import main.Course.Room;
import main.Faculty.Faculty;
import main.Faculty.FacultyManager;
import main.Schedule.CourseScheduler;

import java.util.Arrays;

public class DecisionVariables {

  private final Course[] courses;
  private final Faculty[] faculty;
  private final Room[] rooms;
  private final String[] timeSlots;
  private GRBVar[][][][] assign;
  private GRBVar[] gradCount;

  public DecisionVariables(Course[] courses, Faculty[] faculty, Room[] rooms, String[] timeSlots) {
    this.courses = courses;
    this.faculty = faculty;
    this.rooms = rooms;
    this.timeSlots = timeSlots;
    this.assign = new GRBVar[courses.length][faculty.length][timeSlots.length][rooms.length];
    this.gradCount = new GRBVar[timeSlots.length];

  }

  public void initiate(GRBModel model) throws GRBException {
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

            if (assign[i][j][k][r] == null){
              throw new NullPointerException("Shit is mega null");
            }
          }
        }
      }
    }

    // Initialize the gradCount variables (integer slack variables for each time slot)
    for (int k = 0; k < timeSlots.length; k++) {
      gradCount[k] =
              model.addVar(
                      0.0, Double.POSITIVE_INFINITY, 0.0, GRB.INTEGER, "grad_count_" + timeSlots[k]);
    }
  }

  public GRBVar[] getGradCount() {
    return gradCount;
  }
}
