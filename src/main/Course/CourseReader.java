package main.Course;

import main.Faculty.FacultyManager;
import main.Faculty.Professor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

public class CourseReader {
    private String path;
    private FacultyManager facultyManager;
    private HashSet<Course> courses;
    private int numCourses = 0;

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
            for (int i = 3; i < row.length; i++) {
                if (facultyManager.isProfessor(row[i])){
                    System.out.println(facultyManager.isProfessor(row[i]));
                    int totalStudents = 50;
                    if (!row[1].isEmpty()) {
                        totalStudents = Integer.parseInt(row[1]);
                    }
                    courses.add(new Course(row[0], facultyManager.getProfessor(row[i]), totalStudents));
                    numCourses ++;
                } else if(row[i].equalsIgnoreCase("NH") || row[i].equalsIgnoreCase("DS")){ // TODO: Specific to STOR, make general
                    Professor newProf = new Professor(row[i]);
                    courses.add(new Course(row[0], newProf, 50));
                    facultyManager.addProf(newProf);
                } else {
                    throw new NoSuchElementException("Course Reader: For class " + row[i] + ", prof not found");
                }
                System.out.print(row[i] + "\t"); // Print each field (tab-separated)
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

    public int getNumCourses(){
        return numCourses;
    }
}
