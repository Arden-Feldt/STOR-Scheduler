import Course.CourseReader;
import Course.Room;
import Course.Course;
import Faculty.Faculty;
import Faculty.PreferenceReader;
import Faculty.Professor;
import com.gurobi.gurobi.*;

import java.io.FileWriter;
import java.io.IOException;

public class CourseScheduler {
    private Course[] courses;
    private Professor[] professors;
    private Room[] rooms;
    public String[] timeSlots;

    public CourseScheduler(PreferenceReader preferenceReader, CourseReader courseReader) {
        this.courses = courseReader.getCourses().toArray(new Course[0]);
        this.professors = preferenceReader.getProfessors().toArray(new Professor[0]);
        this.timeSlots = preferenceReader.getTIMESLOTSTRINGS();
        this.rooms = Room.values();
    }

    public void optimize() {
        try {
            // Create empty environment, create a new optimization model
            GRBEnv env = new GRBEnv();
            GRBModel model = new GRBModel(env);

            // Decision variables
            GRBVar[][][][] assign = new GRBVar[courses.length][professors.length][timeSlots.length][rooms.length];
            for (int i = 0; i < courses.length; i++) {
                for (int j = 0; j < professors.length; j++) {
                    for (int k = 0; k < timeSlots.length; k++) {
                        for (int r = 0; r < rooms.length; r++) {
                            assign[i][j][k][r] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY,
                                    "Assign_" + courses[i].getName() + "_" + professors[j].getName() + "_" + timeSlots[k] + "_" + rooms[r].name());
                        }
                    }
                }
            }

            // Objective function: maximize willingness
            GRBLinExpr expr = new GRBLinExpr();
            for (int i = 0; i < courses.length; i++) {
                for (int j = 0; j < professors.length; j++) {
                    for (int k = 0; k < timeSlots.length; k++) {
                        for (int r = 0; r < rooms.length; r++) {
                            expr.addTerm(professors[j].getWillingness()[k], assign[i][j][k][r]);
                        }
                    }
                }
            }
            model.setObjective(expr, GRB.MAXIMIZE);

            // Constraints: course assignment, professor availability, room availability, etc.
            // 1. Each course must be assigned to exactly one time slot by the assigned professor in one room
            for (int i = 0; i < courses.length; i++) {
                GRBLinExpr courseAssignmentExpr = new GRBLinExpr();
                Faculty assignedProfessor = courses[i].getFaculty();
                int professorIndex = -1;

                // Find the index of the assigned professor
                for (int j = 0; j < professors.length; j++) {
                    if (professors[j].equals(assignedProfessor)) {
                        professorIndex = j;
                        break;
                    }
                }

                // Ensure that the course is assigned to the correct professor in one room at one time slot
                for (int k = 0; k < timeSlots.length; k++) {
                    for (int r = 0; r < rooms.length; r++) {
                        courseAssignmentExpr.addTerm(1.0, assign[i][professorIndex][k][r]);
                    }
                }
                model.addConstr(courseAssignmentExpr, GRB.EQUAL, 1.0, "Course_Assignment_" + courses[i].getName());

                // Add constraints to prevent the course from being assigned to any other professor
                for (int j = 0; j < professors.length; j++) {
                    if (j != professorIndex) {
                        for (int k = 0; k < timeSlots.length; k++) {
                            for (int r = 0; r < rooms.length; r++) {
                                model.addConstr(assign[i][j][k][r], GRB.EQUAL, 0.0, "Course_Assignment_Constraint_" + courses[i].getName() + "_" + professors[j].getName());
                            }
                        }
                    }
                }
            }

            // 2. Each room can only be used once per time slot
            for (int k = 0; k < timeSlots.length; k++) {
                for (int r = 0; r < rooms.length; r++) {
                    GRBLinExpr roomUsageExpr = new GRBLinExpr();
                    for (int i = 0; i < courses.length; i++) {
                        for (int j = 0; j < professors.length; j++) {
                            roomUsageExpr.addTerm(1.0, assign[i][j][k][r]);
                        }
                    }
                    model.addConstr(roomUsageExpr, GRB.LESS_EQUAL, 1.0, "Room_Usage_" + rooms[r].name() + "_" + timeSlots[k]);
                }
            }

            // 3. Each professor can only teach one class per time slot
            for (int j = 0; j < professors.length; j++) {
                for (int k = 0; k < timeSlots.length; k++) {
                    GRBLinExpr professorUsageExpr = new GRBLinExpr();
                    for (int i = 0; i < courses.length; i++) {
                        for (int r = 0; r < rooms.length; r++) {
                            professorUsageExpr.addTerm(1.0, assign[i][j][k][r]);
                        }
                    }
                    model.addConstr(professorUsageExpr, GRB.LESS_EQUAL, 1.0, "Professor_Usage_" + professors[j].getName() + "_" + timeSlots[k]);
                }
            }

            // 4. Each professor cannot teach three (or two) classes in a row
            for (int j = 0; j < professors.length; j++) {
                if (!professors[j].getBacktoBack()) {
                    for (int k = 0; k < timeSlots.length - 1; k++) { // Adjusting the loop to ensure we do not go out of bounds
                        GRBLinExpr backToBackExpr = new GRBLinExpr();
                        for (int i = 0; i < courses.length; i++) {
                            for (int r = 0; r < rooms.length; r++) {
                                backToBackExpr.addTerm(1.0, assign[i][j][k][r]);
                                backToBackExpr.addTerm(1.0, assign[i][j][k + 1][r]);
                            }
                        }
                        model.addConstr(backToBackExpr, GRB.LESS_EQUAL, 1.0, "BackToBack_" + professors[j].getName() + "_Starting_" + timeSlots[k]);
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
                        model.addConstr(consecutiveClassesExpr, GRB.LESS_EQUAL, 2.0, "Consecutive_Classes_" + professors[j].getName() + "_Starting_" + timeSlots[k]);
                    }
                }
            }

            // Optimize the model
            model.optimize();

            // Print and save results to CSV
            FileWriter csvWriter = new FileWriter("src/ScheduleData/course_schedule.csv");
            csvWriter.append("Course,Professor,TimeSlot,Room\n");

            for (int i = 0; i < courses.length; i++) {
                for (int j = 0; j < professors.length; j++) {
                    for (int k = 0; k < timeSlots.length; k++) {
                        for (int r = 0; r < rooms.length; r++) {
                            if (assign[i][j][k][r].get(GRB.DoubleAttr.X) > 0.5) {
                                String result = "Assign " + courses[i].getName() + " to " + professors[j].getName() + " at " + timeSlots[k] + " in " + rooms[r].name();
                                System.out.println(result);
                                csvWriter.append(courses[i].getName())
                                        .append(",")
                                        .append(professors[j].getName())
                                        .append(",")
                                        .append(timeSlots[k])
                                        .append(",")
                                        .append(rooms[r].name())
                                        .append("\n");
                            }
                        }
                    }
                }
            }

            csvWriter.flush();
            csvWriter.close();

            // Dispose of model and environment
            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
