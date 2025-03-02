package main.Schedule.ScheduleSubParts;

import com.gurobi.gurobi.*;
import main.Course.Course;
import main.Course.Room;
import main.Faculty.Faculty;
import main.Faculty.GradStudent;
import main.Schedule.ScheduleSubParts.ConstraintHelperFunctions.HardsetReader;

import java.io.IOException;
import java.util.Map;

import static main.Defaults.HARDSETPATH;
import static main.Defaults.MWFNUMTIMESLOTS;

public class Constraints {

  private final Course[] courses;
  private final Faculty[] faculty;
  private final Room[] rooms;
  private final String[] timeSlots;

  public Constraints(Course[] courses, Faculty[] faculty, Room[] rooms, String[] timeSlots) {
    this.courses = courses;
    this.faculty = faculty;
    this.rooms = rooms;
    this.timeSlots = timeSlots;
  }

  // Meat and potatoes of making every-fucking-thing work
  public void singletonConstraint(GRBModel model, GRBVar[][][][] assign) throws GRBException {
    // 1. Each course must be assigned to exactly one time slot by the assigned professor in one
    // room
    for (int i = 0; i < courses.length; i++) {
      GRBLinExpr courseAssignmentExpr = new GRBLinExpr();
      Faculty assignedFaculty = courses[i].getFaculty();
      int facultyIndex = -1;

      // Find the index of the assigned faculty
      for (int j = 0; j < faculty.length; j++) {
        if (faculty[j].equals(assignedFaculty)) {
          facultyIndex = j;
          break;
        }
      }

      // Ensure that the course is assigned to the correct professor in one room at one time slot
      for (int k = 0; k < timeSlots.length; k++) {
        for (int r = 0; r < rooms.length; r++) {
          courseAssignmentExpr.addTerm(1.0, assign[i][facultyIndex][k][r]);
        }
      }

      model.addConstr(
          courseAssignmentExpr, GRB.EQUAL, 1.0, "Course_Assignment_" + courses[i].getName());

      // Add constraints to prevent the course from being assigned to any other faculty
      for (int j = 0; j < faculty.length; j++) {
        if (j != facultyIndex) {
          for (int k = 0; k < timeSlots.length; k++) {
            for (int r = 0; r < rooms.length; r++) {
              model.addConstr(
                  assign[i][j][k][r],
                  GRB.EQUAL,
                  0.0,
                  "Course_Assignment_Constraint_"
                      + courses[i].getName()
                      + "_"
                      + faculty[j].getName());
            }
          }
        }
      }
    }

    for (int j = 0; j < faculty.length; j++) {
      for (int k = 0; k < timeSlots.length; k++) {
        GRBLinExpr professorTimeSlotExpr = new GRBLinExpr();
        for (int i = 0; i < courses.length; i++) {
          for (int r = 0; r < rooms.length; r++) {
            professorTimeSlotExpr.addTerm(1.0, assign[i][j][k][r]);
          }
        }
        model.addConstr(
            professorTimeSlotExpr,
            GRB.LESS_EQUAL,
            1.0,
            "Professor_TimeSlot_" + faculty[j].getName() + "_" + timeSlots[k]);
      }
    }

    // 2. Each room can only be used once per time slot
    for (int k = 0; k < timeSlots.length; k++) {
      for (int r = 0; r < rooms.length; r++) {
        GRBLinExpr roomUsageExpr = new GRBLinExpr();
        for (int i = 0; i < courses.length; i++) {
          for (int j = 0; j < faculty.length; j++) {
            roomUsageExpr.addTerm(1.0, assign[i][j][k][r]);
          }
        }
        model.addConstr(
            roomUsageExpr,
            GRB.LESS_EQUAL,
            1.0,
            "Room_Usage_" + rooms[r].name() + "_" + timeSlots[k]);
      }
    }

    // 3. Each faculty can only teach one class per time slot
    for (int j = 0; j < faculty.length; j++) {
      for (int k = 0; k < timeSlots.length; k++) {
        GRBLinExpr professorUsageExpr = new GRBLinExpr();
        for (int i = 0; i < courses.length; i++) {
          for (int r = 0; r < rooms.length; r++) {
            professorUsageExpr.addTerm(1.0, assign[i][j][k][r]);
          }
        }
        model.addConstr(
            professorUsageExpr,
            GRB.LESS_EQUAL,
            1.0,
            "Professor_Usage_" + faculty[j].getName() + "_" + timeSlots[k]);
      }
    }
  }

  // Profs can only teach backTobacks if they consent to it
  public void backToBackConstraint(GRBModel model, GRBVar[][][][] assign) throws GRBException {
    // 4. Each professor cannot teach three (or two) classes in a row
    for (int j = 0; j < faculty.length; j++) {
      if (!faculty[j].getBacktoBack()) {
        for (int k = 0;
            k < timeSlots.length - 1;
            k++) { // Adjusting the loop to ensure we do not go out of bounds
          GRBLinExpr backToBackExpr = new GRBLinExpr();
          for (int i = 0; i < courses.length; i++) {
            for (int r = 0; r < rooms.length; r++) {
              backToBackExpr.addTerm(1.0, assign[i][j][k][r]);
              backToBackExpr.addTerm(1.0, assign[i][j][k + 1][r]);
            }
          }
          model.addConstr(
              backToBackExpr,
              GRB.LESS_EQUAL,
              1.0,
              "BackToBack_" + faculty[j].getName() + "_Starting_" + timeSlots[k]);
        }
      } else {
        for (int k = 0;
            k < timeSlots.length - 2;
            k++) { // Adjusting the loop to ensure we do not go out of bounds
          GRBLinExpr consecutiveClassesExpr = new GRBLinExpr();
          for (int i = 0; i < courses.length; i++) {
            for (int r = 0; r < rooms.length; r++) {
              consecutiveClassesExpr.addTerm(1.0, assign[i][j][k][r]);
              consecutiveClassesExpr.addTerm(1.0, assign[i][j][k + 1][r]);
              consecutiveClassesExpr.addTerm(1.0, assign[i][j][k + 2][r]);
            }
          }
          model.addConstr(
              consecutiveClassesExpr,
              GRB.LESS_EQUAL,
              2.0,
              "Consecutive_Classes_" + faculty[j].getName() + "_Starting_" + timeSlots[k]);
        }
      }
    }
  }

  // Grad student rooms can only be used by classes taught by grad students
  public void gradStudentRoomConstraint(GRBModel model, GRBVar[][][][] assign) throws GRBException {
    // 5. Only GradStudents can be assigned to GradStudentRoom
    for (int i = 0; i < courses.length; i++) {
      for (int j = 0; j < faculty.length; j++) {
        if (!(faculty[j] instanceof GradStudent)) {
          for (int k = 0; k < timeSlots.length; k++) {
            model.addConstr(
                assign[i][j][k][Room.GradStudentRoom.ordinal()],
                GRB.EQUAL,
                0.0,
                "Non_GradStudent_Cannot_Assign_"
                    + courses[i].getName()
                    + "_"
                    + faculty[j].getName());
          }
        }
      }
    }
  }

  // You can only teach big classes in big rooms
  public void enoughSeatsConstraint(GRBModel model, GRBVar[][][][] assign) throws GRBException {
    // 6. Courses must be assigned to rooms with enough seats
    for (int i = 0; i < courses.length; i++) {
      for (int r = 0; r < rooms.length; r++) {
        if (courses[i].getSectionStudents() > rooms[r].getNumSeats()) {
          for (int j = 0; j < faculty.length; j++) {
            for (int k = 0; k < timeSlots.length; k++) {
              model.addConstr(
                  assign[i][j][k][r],
                  GRB.EQUAL,
                  0.0,
                  "Room_Seats_" + courses[i].getName() + "_" + rooms[r].name());
            }
          }
        }
      }
    }
  }

  // Prevent 600 lvl classes from directly overlapping
  public void sixHundredOverlap(GRBModel model, GRBVar[][][][] assign) throws GRBException {
    for (int k = 0; k < timeSlots.length; k++) { // Loop over time slots
      GRBLinExpr expr = new GRBLinExpr();
      for (int i = 0; i < courses.length; i++) { // Loop over courses
        if (courses[i].isGraduateCourse()) { // Check if 600-level course
          for (int j = 0; j < faculty.length; j++) { // Loop over faculty
            for (int r = 0; r < rooms.length; r++) { // Loop over rooms
              expr.addTerm(1, assign[i][j][k][r]); // Add all possible assignments
            }
          }
        }
      }

      // Constraint: Sum of grad courses assigned in the same timeslot must be <= 1
      model.addConstr(expr, GRB.LESS_EQUAL, 1, "600_level_conflict_timeslot_" + k);
    }
  }

  // Classes can't be taught immediately after grad classes in same room
  public void blockRoomAfterGradCourse(GRBModel model, GRBVar[][][][] assign) throws GRBException {
    int M = faculty.length * courses.length;  // Large enough upper bound

    for (int k = 0; k < MWFNUMTIMESLOTS - 1; k++) { // Ensure k+1 is valid
      for (int r = 0; r < rooms.length; r++) { // Loop over all rooms

        // Create a binary variable to indicate if a grad course is scheduled in (k, r)
        GRBVar gradCourseAssigned = model.addVar(0, 1, 0, GRB.BINARY, "grad_assigned_" + k + "_" + r);

        // Expression to check if a grad course is scheduled in (k, r)
        GRBLinExpr gradExpr = new GRBLinExpr();
        for (int i = 0; i < courses.length; i++) {
          if (courses[i].isGraduateCourseInclusive()) {
            for (int j = 0; j < faculty.length; j++) {
              gradExpr.addTerm(1, assign[i][j][k][r]);
            }
          }
        }

        // Ensure gradCourseAssigned is 1 if any grad course is assigned to (k, r)
        model.addConstr(gradExpr, GRB.GREATER_EQUAL, gradCourseAssigned, "force_grad_var_" + k + "_" + r);
        GRBLinExpr gradUpperBound = new GRBLinExpr();
        gradUpperBound.addTerm(M, gradCourseAssigned);
        model.addConstr(gradExpr, GRB.LESS_EQUAL, gradUpperBound, "limit_grad_var_" + k + "_" + r);

        // Expression to check if any course is scheduled in (k+1, r)
        GRBLinExpr blockExpr = new GRBLinExpr();
        for (int i = 0; i < courses.length; i++) {
          for (int j = 0; j < faculty.length; j++) {
            blockExpr.addTerm(1, assign[i][j][k + 1][r]);
          }
        }

        // If gradCourseAssigned is 1, ensure no courses in (k+1, r)
        GRBLinExpr blockLimit = new GRBLinExpr();
        blockLimit.addConstant(M);
        blockLimit.addTerm(-M, gradCourseAssigned);
        model.addConstr(blockExpr, GRB.LESS_EQUAL, blockLimit, "block_if_grad_" + k + "_" + r);
      }
    }
  }

  // Profs can either teach MWF or TTh
  public void profsTeachOneDay(GRBModel model, GRBVar[][][][] assign) throws GRBException {
    int numFaculty = faculty.length;
    int numTimeSlots = timeSlots.length;
    int numCourses = courses.length;
    int numRooms = rooms.length;

    // Binary variables indicating if faculty teaches in MWF/TTh slots
    GRBVar[] scheduledMWF = new GRBVar[numFaculty];
    GRBVar[] scheduledTTh = new GRBVar[numFaculty];

    for (int j = 0; j < numFaculty; j++) {
      scheduledMWF[j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "scheduledMWF_" + faculty[j].getName());
      scheduledTTh[j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "scheduledTTh_" + faculty[j].getName());
    }

    // Constraint 1: If a faculty teaches in an MWF slot, set scheduledMWF[j] = 1
    for (int j = 0; j < numFaculty; j++) {
      GRBLinExpr earlySum = new GRBLinExpr();
      for (int i = 0; i < numCourses; i++) {
        for (int k = 0; k < MWFNUMTIMESLOTS; k++) { // MWF slots
          for (int r = 0; r < numRooms; r++) {
            earlySum.addTerm(1.0, assign[i][j][k][r]);
          }
        }
      }

      // Ensure scheduledMWF[j] is 1 if faculty teaches in an MWF slot
      GRBLinExpr rhsEarly = new GRBLinExpr();
      rhsEarly.addTerm(courses.length, scheduledMWF[j]);
      model.addConstr(earlySum, GRB.LESS_EQUAL, rhsEarly, "Faculty_Early_" + faculty[j].getName());

      // Ensure scheduledMWF[j] is 0 if no MWF slots are assigned
      model.addConstr(earlySum, GRB.GREATER_EQUAL, scheduledMWF[j], "Faculty_MinEarly_" + faculty[j].getName());
    }

    // Constraint 2: If a faculty teaches in a late slot, set scheduledTTh[j] = 1
    for (int j = 0; j < numFaculty; j++) {
      GRBLinExpr lateSum = new GRBLinExpr();
      for (int i = 0; i < numCourses; i++) {
        for (int k = MWFNUMTIMESLOTS; k < numTimeSlots; k++) { // Late slots
          for (int r = 0; r < numRooms; r++) {
            lateSum.addTerm(1.0, assign[i][j][k][r]);
          }
        }
      }

      // Ensure scheduledTTh[j] is 1 if faculty teaches in a TTh slot
      GRBLinExpr rhsLate = new GRBLinExpr();
      rhsLate.addTerm(courses.length, scheduledTTh[j]);
      model.addConstr(lateSum, GRB.LESS_EQUAL, rhsLate, "Faculty_Late_" + faculty[j].getName());

      // Ensure scheduledTTh[j] is 0 if no TTh slots are assigned
      model.addConstr(lateSum, GRB.GREATER_EQUAL, scheduledTTh[j], "Faculty_MinLate_" + faculty[j].getName());
    }

    // Constraint 3: A faculty cannot teach both MWF and TTh slots
    for (int j = 0; j < numFaculty; j++) {
      GRBLinExpr sumExpr = new GRBLinExpr();
      sumExpr.addTerm(1.0, scheduledMWF[j]);
      sumExpr.addTerm(1.0, scheduledTTh[j]);
      model.addConstr(sumExpr, GRB.LESS_EQUAL, 1, "Faculty_EitherMWFOrTTh_" + faculty[j].getName());
    }
  }



  // Can't have different sections of the same class in the same time slot
  public void classDuplicateTime(GRBModel model, GRBVar[][][][] assign) throws GRBException {
    for (int i = 0; i < courses.length; i++) {
      for (int j = i + 1; j < courses.length; j++) {
        // Check if both courses have the same class number
        if (courses[i].parseNumber() == (courses[j].parseNumber())) {
          // Now create a constraint to ensure they are not assigned to the same time slot and room
          for (int k = 0; k < timeSlots.length; k++) { // Loop over all time slots
            for (int r = 0; r < rooms.length; r++) { // Loop over all rooms
              for (int f1 = 0; f1 < faculty.length; f1++) { // Loop over all faculty for course i
                for (int f2 = 0; f2 < faculty.length; f2++) { // Loop over all faculty for course j
                  GRBLinExpr expr = new GRBLinExpr();
                  expr.addTerm(1.0, assign[i][f1][k][r]); // Course i with faculty f1
                  expr.addTerm(1.0, assign[j][f2][k][r]); // Course j with faculty f2
                  // The sum should be less than or equal to 1 (not both can be scheduled at the same time)
                  model.addConstr(
                          expr,
                          GRB.LESS_EQUAL,
                          1,
                          "SameTimeConstraint_" + courses[i].getName() + "_" + k + "_" + r + "_" + f1 + "_" + f2);
                }
              }
            }
          }
        }
      }
    }
  }

  public void hardsets(GRBModel model, GRBVar[][][][] assign) throws GRBException, IOException {
    HardsetReader hardsetReader = new HardsetReader(HARDSETPATH);

    Map<String, Map<String, String>> hardsetMap = hardsetReader.readCSV();

    for (Map.Entry<String, Map<String, String>> courseEntry : hardsetMap.entrySet()) {
      String courseName = courseEntry.getKey();
      Map<String, String> facultyTimeMap = courseEntry.getValue();

      // Loop over the faculties and their time slots for this course
      for (Map.Entry<String, String> facultyTimeEntry : facultyTimeMap.entrySet()) {
        String facultyName = facultyTimeEntry.getKey();
        String timeSlot = facultyTimeEntry.getValue();

        // Find the faculty index, course index, and timeSlot index
        int facultyIndex = facultyList.indexOf(facultyName); // Implement this function
        int courseIndex = courseList.indexOf(courseName);   // Implement this function
        int timeSlotIndex = timeSlotList.indexOf(timeSlot); // Implement this function

        // Add the constraint: course[i] assigned to faculty[j] at time slot[k]
        GRBLinExpr expr = new GRBLinExpr();
        expr.addTerm(1.0, assign[courseIndex][facultyIndex][timeSlotIndex][0]); // Assuming room 0
        model.addConstr(expr, GRB.EQUAL, 1.0, "HardsetConstraint_" + courseName + "_" + facultyName + "_" + timeSlot);
      }
    }
  }

  // Constrain9: Back to back can't make gardner to hanes
  // TODO: ensure it works
  public void gardnerToHanes(GRBModel model, GRBVar[][][][] assign) throws GRBException {
    // 6. If a professor teaches a class in Gardner, any back-to-back class must also be in Gardner
    for (int j = 0; j < faculty.length; j++) { // Iterate over each professor
      for (int k = 0;
          k < timeSlots.length - 1;
          k++) { // Iterate over time slots (except the last one)
        for (int i = 0; i < courses.length; i++) { // Iterate over each course
          GRBLinExpr gardnerBackToBackExpr = new GRBLinExpr();

          // If a professor teaches in Gardner at time slot k
          gardnerBackToBackExpr.addTerm(1.0, assign[i][j][k][Room.Gardner.ordinal()]);

          // If the professor teaches any class at time slot k+1, it must also be in Gardner
          for (int r = 0; r < rooms.length; r++) {
            if (r != Room.Gardner.ordinal()) {
              gardnerBackToBackExpr.addTerm(1.0, assign[i][j][k + 1][r]);
            }
          }

          model.addConstr(
              gardnerBackToBackExpr,
              GRB.LESS_EQUAL,
              1.0,
              "Gardner_BackToBack_" + faculty[j].getName() + "_Time_" + timeSlots[k]);

          // If a professor teaches a class in the room Gardner and one not in Gardner
          // consecutively, prevent this.
          GRBLinExpr nonGardnerBackToBackExpr = new GRBLinExpr();

          // If a professor teaches in Gardner at time slot k, and any non-Gardner class at k+1
          nonGardnerBackToBackExpr.addTerm(1.0, assign[i][j][k][Room.Gardner.ordinal()]);

          for (int r = 0; r < rooms.length; r++) {
            if (r != Room.Gardner.ordinal()) {
              nonGardnerBackToBackExpr.addTerm(-1.0, assign[i][j][k + 1][r]);
            }
          }

          model.addConstr(
              nonGardnerBackToBackExpr,
              GRB.LESS_EQUAL,
              0.0,
              "Non_Gardner_BackToBack_" + faculty[j].getName() + "_Time_" + timeSlots[k]);
        }
      }
    }
  }
}
