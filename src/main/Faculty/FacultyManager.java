package main.Faculty;

import main.Course.Course;
import main.Course.TimeSlot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;

public class FacultyManager {
    private HashSet<Faculty> faculty = new HashSet<>();
    private HashSet<Professor> professors;
    private HashSet<GradStudent> gradStudents = new HashSet<>();

    public FacultyManager(HashSet<Professor> professors){
        this.professors = professors;
        faculty.addAll(professors);
    }

    public void setProfessors(HashSet<Professor> professors) {
        this.professors = professors;
    }

    public HashSet<Professor> getProfessors() {
        return professors;
    }

    public boolean isProfessor(String name){
        for (Professor professor : professors){
            if (professor.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }

    public Faculty getProfessor(String name){
        for (Professor professor : professors){
            if (professor.getName().equalsIgnoreCase(name)){
                return professor;
            }
        }
        throw new NoSuchElementException();
    }

    public String[] getTIMESLOTSTRINGS() {
        ArrayList<String> result = new ArrayList();
        for (TimeSlot timeSlot : TimeSlot.values()) {
            result.add(timeSlot.name());
        }
        return result.toArray(new String[0]);
    }

    public int getNumProf(){
        int i = 0;
        for (Professor professor : professors){
            i ++;
        }
        return i;
    }

    public void addProf (Professor professor) {
        professors.add(professor);
        faculty.add(professor);
    }

    public void addGrad (GradStudent gradStudent) {
        gradStudents.add(gradStudent);
        faculty.add(gradStudent);
    }
}
