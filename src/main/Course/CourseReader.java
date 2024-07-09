package main.Course;

import main.Faculty.FacultyManager;
import main.Faculty.GradStudent;
import main.Faculty.Professor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static main.Defaults.DEFAULTSECTIONSIZE;

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
            for (int i = 4; i < row.length; i++) {

                int sectionStudents = initSectionSize(row[2]);
                int totalStudents = initTotalStudents(row[1]);


                if (facultyManager.isProfessor(row[i])){
                    courses.add(new Course(row[0], facultyManager.getProfessor(row[i]), totalStudents, sectionStudents));
                    numCourses ++;
                } else if(row[i].equalsIgnoreCase("NH") || row[i].equalsIgnoreCase("DS") || row[i].equalsIgnoreCase("NOASSIGNMENT")){ // TODO: Specific to STOR, make general
                    Professor newProf = new Professor(row[i]);
                    courses.add(new Course(row[0], newProf, totalStudents, sectionStudents));
                    facultyManager.addProf(newProf);
                } else if(row[i].equalsIgnoreCase("GS")){
                    GradStudent gradStudent = new GradStudent(row[i]);
                    courses.add(new Course(row[0], gradStudent, totalStudents, sectionStudents));
                    facultyManager.addGrad(gradStudent);
                } else if(row[i].isEmpty()){
                    throw new NoSuchElementException("Element Empty: " + Arrays.toString(row));
                } else {
                    throw new NoSuchElementException("Course Reader: For class " + row[i] + ", prof not found");
                }
                System.out.print(row[i] + "\t"); // Print each field (tab-separated)
            }
            System.out.println(); // Move to the next line for the next row
        }
    }

    private int initSectionSize (String section) {
        if (section.isEmpty()){
            return DEFAULTSECTIONSIZE;
        } else {
            return Integer.parseInt(section);
        }
    }

    private int initTotalStudents (String section) {
        if (section.isEmpty()){
            return -1; // TODO: This is ass
        } else {
            return Integer.parseInt(section);
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
