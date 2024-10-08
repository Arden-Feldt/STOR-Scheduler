package main.Faculty;

import main.Course.TimeSlot;

import java.util.ArrayList;

import static main.Defaults.NUMTIMESLOTS;

public class Professor extends Faculty {
  private final String name;
  private ArrayList<TimeSlot> timeSlots;

  private int[] willingness;
  private boolean backToBack;
  private boolean getsOpinion = true;

  public Professor(String name) {
    super(name);
    this.name = name;
    this.willingness = new int[NUMTIMESLOTS];
  }

  public void setTimeSlots(ArrayList<TimeSlot> timeSlots) {
    this.timeSlots = timeSlots;
  }

  public void setBackToBack(boolean backToBack) {
    this.backToBack = backToBack;
  }

  public void setGetsOpinion(boolean getsOpinion) {
    this.getsOpinion = getsOpinion;
  }

  public void setWillingness(int[] willingness) {
    this.willingness = willingness;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean getBacktoBack() {
    return backToBack;
  }

  @Override
  public boolean getGetsOpinion() {
    return getsOpinion;
  }

  @Override
  public ArrayList<TimeSlot> getTimeSlots() {
    return timeSlots;
  }

  public int[] getWillingness() {
    return willingness;
  }

  public boolean isGetsOpinion() {
    return getsOpinion;
  }

  @Override
  public String toString() {
    return name;
  }
}
