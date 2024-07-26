package main.Schedule.ScheduleSubParts;

import com.gurobi.gurobi.GRB;
import com.gurobi.gurobi.GRBException;
import com.gurobi.gurobi.GRBModel;
import com.gurobi.gurobi.GRBVar;
import main.Course.Course;
import main.Course.Room;
import main.Faculty.Faculty;

import java.io.FileWriter;
import java.io.IOException;

public class Exporter {

    private final Course[] courses;
    private final Faculty[] faculty;
    private final Room[] rooms;
    private final String[] timeSlots;
    private final String output_path;

    public Exporter(Course[] courses, Faculty[] faculty, Room[] rooms, String[] timeSlots, String output_path) {
        this.courses = courses;
        this.faculty = faculty;
        this.rooms = rooms;
        this.timeSlots = timeSlots;
        this.output_path = output_path;
    }

    public void export (GRBModel model, GRBVar[][][][] assign) throws IOException, GRBException {
        // Print and save results to CSV
        FileWriter csvWriter = new FileWriter(output_path);
        csvWriter.append("Course,Professor,TimeSlot,Room\n");

        for (int i = 0; i < courses.length; i++) {
            for (int j = 0; j < faculty.length; j++) {
                for (int k = 0; k < timeSlots.length; k++) {
                    for (int r = 0; r < rooms.length; r++) {
                        if (assign[i][j][k][r].get(GRB.DoubleAttr.X) > 0.5) {
                            String result = "Assign " + courses[i].getName() + " to " + faculty[j].getName() + " at " + timeSlots[k] + " in " + rooms[r].name();
                            System.out.println(result);
                            csvWriter.append(courses[i].getName())
                                    .append(",")
                                    .append(faculty[j].getName())
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
    }
}
