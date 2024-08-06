package main.Schedule;

import main.Course.CourseManager;
import main.Course.Room;
import main.Course.Course;
import main.Faculty.Faculty;
import main.Faculty.FacultyManager;
import com.gurobi.gurobi.*;
import main.Schedule.ScheduleSubParts.Constraints;
import main.Schedule.ScheduleSubParts.DecisionVariables;
import main.Schedule.ScheduleSubParts.Exporter;
import main.Schedule.ScheduleSubParts.ObjectiveFunction;

import java.io.FileWriter;
import java.io.IOException;

public class CourseScheduler {
  private final FacultyManager facultyManager;
  private final CourseManager courseManager;
  private final Course[] courses;
  private final Faculty[] faculty;
  private final Room[] rooms;
  private final String[] timeSlots;
  private final String output_path;

  public CourseScheduler(
      FacultyManager facultyManager, CourseManager courseManager, String output_path) {
    this.facultyManager = facultyManager;
    this.courseManager = courseManager;

    this.courses = courseManager.getCourseArray();
    this.faculty = facultyManager.getFaculty().toArray(new Faculty[0]);
    this.timeSlots = facultyManager.getTIMESLOTSTRINGS();
    this.rooms = Room.values();
    this.output_path = output_path;
  }

  public void optimize() {
    try {
      // Create empty environment, create a new optimization model
      GRBEnv env = new GRBEnv();
      GRBModel model = new GRBModel(env);

      // Decision variables
      GRBVar[][][][] assign =
          new GRBVar[courses.length][faculty.length][timeSlots.length][rooms.length];
      DecisionVariables decisionVariables =
          new DecisionVariables(courses, faculty, rooms, timeSlots);
      decisionVariables.initiate(model, assign);

      // Objective function: maximize willingness
      ObjectiveFunction objectiveFunction =
          new ObjectiveFunction(courses, faculty, rooms, timeSlots);
      objectiveFunction.initFunction(model, assign);

      // Constraints: course assignment, professor availability, room availability, etc.
      Constraints constraints = new Constraints(courses, faculty, rooms, timeSlots);

      constraints.singletonConstraint(model, assign);
      constraints.backToBackConstraint(model, assign);
      constraints.gradStudentRoomConstraint(model, assign);
      constraints.enoughSeatsConstraint(model, assign);
      constraints.sixHundredOverlap(model, assign);
      constraints.gradClassesDoublePeriod(model, assign);

      // Optimize the model
      model.optimize();

      // Print and save results to CSV
      Exporter exporter = new Exporter(courses, faculty, rooms, timeSlots, output_path);
      exporter.export(model, assign);

      // Dispose of model and environment
      model.dispose();
      env.dispose();

    } catch (GRBException e) {
      System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String getOutput_path() {
    return output_path;
  }
}
