package test.ReaderTests;

import main.Course.TimeSlot;
import main.Faculty.PreferenceReader;
import main.Faculty.Professor;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

public class FacultyReaderTests {
    @Test
    public void proffessors_exist() {
        PreferenceReader preferenceReader = new PreferenceReader("src/main/Faculty/ProfessorData/ProPrefFalls23.csv", 3);
        preferenceReader.buildProfessors();
        for (Professor professor : preferenceReader.getProfessors()){
            assertNotNull(professor);
            assertNotNull(professor.getName());
            assertNotNull(professor.getWillingness());
        }

    }

    @Test
    public void willingness_built_right() {
        PreferenceReader preferenceReader = new PreferenceReader("src/main/Faculty/ProfessorData/ProPrefFalls23.csv", 3);
        preferenceReader.buildProfessors();
        for (Professor professor : preferenceReader.getProfessors()){
            assertEquals(professor.getWillingness().length, TimeSlot.getAllTimeSlots().size());
            for (int willingness : professor.getWillingness()){
                assertTrue(willingness >= 0);
            }

        }
    }
}

