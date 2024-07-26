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


public class DecisionVariables {

    private Course[] courses;
    private Faculty[] faculty;
    private Room[] rooms;
    private String[] timeSlots;

    public DecisionVariables(Course[] courses, Faculty[] faculty, Room[] rooms, String[] timeSlots) {
        this.courses = courses;
        this.faculty = faculty;
        this.rooms = rooms;
        this.timeSlots = timeSlots;
    }

    public void initiate(GRBModel model, GRBVar[][][][] assign) throws GRBException {
        for (int i = 0; i < courses.length; i++) {
            for (int j = 0; j < faculty.length; j++) {
                for (int k = 0; k < timeSlots.length; k++) {
                    for (int r = 0; r < rooms.length; r++) {
                        assign[i][j][k][r] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY,
                                "Assign_" + courses[i].getName() + "_"
                                        + faculty[j].getName() + "_"
                                        + timeSlots[k] + "_"
                                        + rooms[r].name());
                    }
                }
            }
        }
    }
}
