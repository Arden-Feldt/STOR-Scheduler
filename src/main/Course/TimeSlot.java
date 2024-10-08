package main.Course;

import java.util.ArrayList;
import java.util.Arrays;

public enum TimeSlot {
  MWF800850(),
  MWF905955,
  MWF10101100,
  MWF11151205,
  MWF1220110,
  MWF125215,
  MWF230320,
  MWF545635,
  TTH800915,
  TTH9301045,
  TTH11001215(true),
  TTH1230145(true),
  TTH200315(true),
  TTH330445,
  TTH500615;

  private final boolean highRequest;

  TimeSlot() {
    this.highRequest = false;
  }

  // TODO: Make sure i have the right highRequest times

  // TODO: am i missing MWF 4pm??

  TimeSlot(boolean highRequest) {
    this.highRequest = highRequest;
  }

  public static ArrayList<TimeSlot> getAllTimeSlots() {
    return new ArrayList<>(Arrays.asList(TimeSlot.values()));
  }

  public static int getNumTimeSlots () {
    return getAllTimeSlots().size();
  }
}
