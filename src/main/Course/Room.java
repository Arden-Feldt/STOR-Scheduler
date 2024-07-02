package main.Course;

public enum Room {
    Hanes(100),
    Hanes1(50),
    Hanes3(50),
    Gardner(100),
    H107(40);

    private final int numSeats;

    Room(int numSeats) {
        this.numSeats = numSeats;
    }

    public int getNumSeats(){
        return this.numSeats;
    }
}
