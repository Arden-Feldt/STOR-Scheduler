package main.Faculty;

import java.util.HashSet;
import java.util.NoSuchElementException;

public class FacultyManager {
    private HashSet<Professor> professors;
    public FacultyManager(HashSet<Professor> professors){
        this.professors = professors;
    }

    public void setProfessors(HashSet<Professor> professors) {
        this.professors = professors;
    }

    public HashSet<Professor> getProfessors() {
        return professors;
    }

    public boolean isProfessor(String name){
        for (Professor professor : professors){
            if (professor.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    public Faculty getProfessor(String name){
        for (Professor professor : professors){
            if (professor.getName().equals(name)){
                return professor;
            }
        }
        throw new NoSuchElementException();
    }
}
