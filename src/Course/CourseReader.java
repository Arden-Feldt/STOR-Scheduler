package Course;

import Faculty.FacultyManager;
import Faculty.Professor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CourseReader {
    private String path;
    private FacultyManager facultyManager;
    private HashSet<Course> courses;

    public CourseReader(String path, FacultyManager facultyManager){
        this.path = path;
        this.facultyManager = facultyManager;
        courses = new HashSet<Course>();
    }

    public void buildCourses(){
        String line;
        String delimiter = ",";

        List<String[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String headerLine = br.readLine(); // Gets the header line out of the way

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(delimiter);
                data.add(fields);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Now `data` contains the parsed data rows as arrays of strings
        for (String[] row : data) {
            for (String field : row) {
                int i = 0;
                if (facultyManager.isProfessor(field)){
                    if (!row[1].equals("")) {
                        courses.add(new Course(row[0], facultyManager.getProfessor(field), Integer.parseInt(row[1])));
                    } else {
                        courses.add(new Course(row[0], facultyManager.getProfessor(field), 50));
                    }
                }
                System.out.print(field + "\t"); // Print each field (tab-separated)
            }
            System.out.println(); // Move to the next line for the next row
        }
    }

    public HashSet<Course> getCourses() {
        return courses;
    }

    public void printCourses(){
        for (Course course : courses){
            System.out.println(course.getName() + " " + course.getFaculty());
        }
    }
}
