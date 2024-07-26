package main.Course;

public enum Room {
  Hanes120(100),
  Hanes107(50), // this is the weird one
  Hanes130(50),
  H125(50),
  Gardner(100),
  GradStudentRoom(100);

  private final int numSeats;

  Room(int numSeats) {
    this.numSeats = numSeats;
  }

  public int getNumSeats() {
    return this.numSeats;
  }
}
