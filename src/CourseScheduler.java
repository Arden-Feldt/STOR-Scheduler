import Course.CourseReader;
import Course.Course;
import Faculty.PreferenceReader;
import Faculty.Professor;
import com.gurobi.gurobi.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CourseScheduler {
    private Course[] courses;
    private Professor[] professors;
    public String[] timeSlots;

    public CourseScheduler(PreferenceReader preferenceReader, CourseReader courseReader){
        this.courses = courseReader.getCourses().toArray(new Course[0]);
        this.professors = preferenceReader.getProfessors().toArray(new Professor[0]);
        this.timeSlots = preferenceReader.getTIMESLOTSTRINGS();


    }

    public  void optimize() {
        try {
            // Create empty environment, create a new optimization model
            GRBEnv env = new GRBEnv();
            GRBModel model = new GRBModel(env);

            // Define courses, professors, time slots, rooms, and willingness data here...


            // Decision variables
            GRBVar[][][] assign = new GRBVar[courses.length][professors.length][timeSlots.length];
            for (int i = 0; i < courses.length; i++) {
                for (int j = 0; j < professors.length; j++) {
                    for (int k = 0; k < timeSlots.length; k++) {
                        assign[i][j][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY,
                                "Assign_" + courses[i].getName() + "_" + professors[j].getName() + "_" + timeSlots[k]);
                    }
                }
            }

            // Objective function: maximize willingness
            GRBLinExpr expr = new GRBLinExpr();
            for (int i = 0; i < courses.length; i++) {
                for (int j = 0; j < professors.length; j++) {
                    for (int k = 0; k < timeSlots.length; k++) {
                        expr.addTerm(professors[j].getWillingness()[k], assign[i][j][k]);
                    }
                }
            }
            model.setObjective(expr, GRB.MAXIMIZE);

            // Constraints: course assignment, professor availability, room availability, etc.
            // Example constraints:
            // 1. Each course must be assigned to exactly one time slot
            for (int i = 0; i < courses.length; i++) {
                GRBLinExpr courseAssignmentExpr = new GRBLinExpr();
                for (int j = 0; j < professors.length; j++) {
                    for (int k = 0; k < timeSlots.length; k++) {
                        courseAssignmentExpr.addTerm(1.0, assign[i][j][k]);
                    }
                }
                model.addConstr(courseAssignmentExpr, GRB.EQUAL, 1.0, "Course_Assignment_" + courses[i].getName());
            }

            // Additional constraints based on professor availability, room availability, etc.

            // Optimize the model
            model.optimize();

            // Print results
            for (int i = 0; i < courses.length; i++) {
                for (int j = 0; j < professors.length; j++) {
                    for (int k = 0; k < timeSlots.length; k++) {
                        if (assign[i][j][k].get(GRB.DoubleAttr.X) > 0.5) {
                            System.out.println("Assign " + courses[i].getName() + " to " + professors[j].getName() + " at " + timeSlots[k]);
                        }
                    }
                }
            }

            // Dispose of model and environment
            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
        }
    }
}

