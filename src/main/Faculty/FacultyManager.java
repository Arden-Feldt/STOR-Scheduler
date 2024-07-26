package main.Faculty;

import main.Course.Course;
import main.Course.TimeSlot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;

public class FacultyManager {
  private final HashSet<Faculty> faculty = new HashSet<>();

  public FacultyManager(HashSet<Professor> professors) {
    faculty.addAll(professors);
  }

  public HashSet<Professor> getProfessors() {
    HashSet<Professor> result = new HashSet<>();
    for (Faculty facultyMember : faculty) {
      if (facultyMember instanceof Professor) {
        result.add((Professor) facultyMember);
      }
    }
    return result;
  }

  public boolean isProfessor(String name) {
    for (Professor professor : getProfessors()) {
      if (professor.getName().equalsIgnoreCase(name)) {
        return true;
      }
    }
    return false;
  }

  public Faculty getProfessor(String name) {
    for (Professor professor : getProfessors()) {
      if (professor.getName().equalsIgnoreCase(name)) {
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

  public int getNumProf() {
    int i = 0;
    for (Professor professor : getProfessors()) {
      i++;
    }
    return i;
  }

  public void addProf(Professor professor) {
    faculty.add(professor);
  }

  public void addGrad(GradStudent gradStudent) {
    faculty.add(gradStudent);
  }

  public HashSet<Faculty> getFaculty() {
    return faculty;
  }
}
