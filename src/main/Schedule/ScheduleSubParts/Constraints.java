package main.Schedule.ScheduleSubParts;

import com.gurobi.gurobi.*;
import main.Course.Course;
import main.Course.Room;
import main.Faculty.Faculty;
import main.Faculty.GradStudent;

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

    public void singletonConstraint (GRBModel model, GRBVar[][][][] assign) throws GRBException {
        // 1. Each course must be assigned to exactly one time slot by the assigned professor in one room
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

            model.addConstr(courseAssignmentExpr, GRB.EQUAL, 1.0, "Course_Assignment_" + courses[i].getName());

            // Add constraints to prevent the course from being assigned to any other faculty
            for (int j = 0; j < faculty.length; j++) {
                if (j != facultyIndex) {
                    for (int k = 0; k < timeSlots.length; k++) {
                        for (int r = 0; r < rooms.length; r++) {
                            model.addConstr(assign[i][j][k][r], GRB.EQUAL, 0.0, "Course_Assignment_Constraint_" + courses[i].getName() + "_" + faculty[j].getName());
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
                model.addConstr(professorTimeSlotExpr, GRB.LESS_EQUAL, 1.0, "Professor_TimeSlot_" + faculty[j].getName() + "_" + timeSlots[k]);
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
                model.addConstr(roomUsageExpr, GRB.LESS_EQUAL, 1.0, "Room_Usage_" + rooms[r].name() + "_" + timeSlots[k]);
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
                model.addConstr(professorUsageExpr, GRB.LESS_EQUAL, 1.0, "Professor_Usage_" + faculty[j].getName() + "_" + timeSlots[k]);
            }
        }
    }

    public void backToBackConstraint (GRBModel model, GRBVar[][][][] assign) throws GRBException {
        // 4. Each professor cannot teach three (or two) classes in a row
        for (int j = 0; j < faculty.length; j++) {
            if (!faculty[j].getBacktoBack()) {
                for (int k = 0; k < timeSlots.length - 1; k++) { // Adjusting the loop to ensure we do not go out of bounds
                    GRBLinExpr backToBackExpr = new GRBLinExpr();
                    for (int i = 0; i < courses.length; i++) {
                        for (int r = 0; r < rooms.length; r++) {
                            backToBackExpr.addTerm(1.0, assign[i][j][k][r]);
                            backToBackExpr.addTerm(1.0, assign[i][j][k + 1][r]);
                        }
                    }
                    model.addConstr(backToBackExpr, GRB.LESS_EQUAL, 1.0, "BackToBack_" + faculty[j].getName() + "_Starting_" + timeSlots[k]);
                }
            } else {
                for (int k = 0; k < timeSlots.length - 2; k++) { // Adjusting the loop to ensure we do not go out of bounds
                    GRBLinExpr consecutiveClassesExpr = new GRBLinExpr();
                    for (int i = 0; i < courses.length; i++) {
                        for (int r = 0; r < rooms.length; r++) {
                            consecutiveClassesExpr.addTerm(1.0, assign[i][j][k][r]);
                            consecutiveClassesExpr.addTerm(1.0, assign[i][j][k + 1][r]);
                            consecutiveClassesExpr.addTerm(1.0, assign[i][j][k + 2][r]);
                        }
                    }
                    model.addConstr(consecutiveClassesExpr, GRB.LESS_EQUAL, 2.0, "Consecutive_Classes_" + faculty[j].getName() + "_Starting_" + timeSlots[k]);
                }
            }
        }
    }

    public void gradStudentRoomConstraint (GRBModel model, GRBVar[][][][] assign) throws GRBException {
        // 5. Only GradStudents can be assigned to GradStudentRoom
        for (int i = 0; i < courses.length; i++) {
            for (int j = 0; j < faculty.length; j++) {
                if (!(faculty[j] instanceof GradStudent)) {
                    for (int k = 0; k < timeSlots.length; k++) {
                        model.addConstr(assign[i][j][k][Room.GradStudentRoom.ordinal()], GRB.EQUAL, 0.0, "Non_GradStudent_Cannot_Assign_" + courses[i].getName() + "_" + faculty[j].getName());
                    }
                }
            }
        }
    }

    public void enoughSeatsConstraint (GRBModel model, GRBVar[][][][] assign) throws GRBException {
        // 6. Courses must be assigned to rooms with enough seats
        for (int i = 0; i < courses.length; i++) {
            for (int r = 0; r < rooms.length; r++) {
                if (courses[i].getSectionStudents() > rooms[r].getNumSeats()) {
                    for (int j = 0; j < faculty.length; j++) {
                        for (int k = 0; k < timeSlots.length; k++) {
                            model.addConstr(assign[i][j][k][r], GRB.EQUAL, 0.0, "Room_Seats_" + courses[i].getName() + "_" + rooms[r].name());
                        }
                    }
                }
            }
        }
    }

    // Contstraint 7: 600 level courses can't be at the same time
    public void sixHundredOverlap(GRBModel model, GRBVar[][][][] assign) throws GRBException {
        for (int k = 0; k < timeSlots.length; k++) {
            for (int i = 0; i < courses.length; i++) {
                for (int j = i + 1; j < courses.length; j++) {
                    try{
                        if (Integer.parseInt(courses[i].getName()) >= 600 &&
                                Integer.parseInt(courses[i].getName()) < 700 &&
                                Integer.parseInt(courses[j].getName()) >= 600 &&
                                Integer.parseInt(courses[j].getName()) < 700) {
                            GRBLinExpr expr = new GRBLinExpr();
                            for (int r = 0; r < rooms.length; r++) {
                                expr.addTerm(1, assign[i][0][k][r]); // TODO: IMPL index or something
                                expr.addTerm(1, assign[j][0][k][r]);
                            }
                            model.addConstr(expr, GRB.LESS_EQUAL, 1, "600_level_" + courses[i].getName() + "_" + courses[j].getName() + "_" + timeSlots[k]);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(courses[i].getName() + " or " + courses[j].getName() + " is not an int");
                    }

                }
            }
        }
    }

    // Constraint 8: 600+ courses take two periods MWF
    public void gradClassesDoublePeriod(GRBModel model, GRBVar[][][][] assign) throws GRBException {
        for (int i = 0; i < courses.length; i++) {
            try{
                if (Integer.parseInt(courses[i].getName()) >= 600) {
                    for (int k = 0; k < timeSlots.length - 1; k++) {
                        for (int j = 0; j < faculty.length; j++) {
                            for (int r = 0; r < rooms.length; r++) {
                                // Ensure if the course is scheduled at time slot k, it must also be scheduled at time slot k+1
                                model.addConstr(assign[i][j][k][r], GRB.EQUAL, assign[i][j][k + 1][r],
                                        "600_level_double_timeslot_" + courses[i].getName() + "_" + timeSlots[k]);
                            }
                        }
                    }
            }
            } catch (NumberFormatException e){
                System.out.println(courses[i].getName() + " is not an int");
            }
        }
    }

    // Constrain9: Back to back can't make gardner to hanes
}
