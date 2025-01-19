package main.Schedule.ScheduleSubParts.slackVariables;

import com.gurobi.gurobi.*;
import main.Course.Course;
import main.Course.Room;
import main.Faculty.Faculty;

public class gradConflicts {
    public static int calculateConflicts(
            GRBVar[][][][] assign,
            Course[] courses,
            Faculty[] faculty,
            String[] timeSlots,
            Room[] rooms
    ) {
        int conflictCount = 0;

        // Example: Count graduate student conflicts (dummy logic for illustration)
        for (int k = 0; k < timeSlots.length; k++) {
            for (int i = 0; i < courses.length; i++) {
                for (int j = i + 1; j < faculty.length; j++) {
                    for (int r = 0; r < rooms.length; r++) {
                        try {
                            // Check if both courses are assigned to the same time slot
                            for (int r1 = 0; r1 < rooms.length; r1++) {
                                for (int r2 = 0; r2 < rooms.length; r2++) {
                                    if (assign[i][k][k][r1].get(GRB.DoubleAttr.X) > 0.5 &&
                                            assign[j][k][k][r2].get(GRB.DoubleAttr.X) > 0.5) {
                                        conflictCount++; // Increment conflict counter
                                        System.out.println("Conflict detected: " + courses[i].getName() + " and " + courses[j].getName() + " at time slot " + timeSlots[k]);
                                        break; // No need to check further once a conflict is detected
                                    }
                                }
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(courses[i].getName() + " or " + courses[j].getName() + " is not an int");
                        } catch (GRBException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }}

        return conflictCount;
    }
}
