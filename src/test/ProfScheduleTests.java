package test;

import main.Faculty.PreferenceReader;
import main.Faculty.Professor;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ProfScheduleTests {
    @Test
    public void professors_exist() {
        PreferenceReader preferenceReader = new PreferenceReader("src/main/Faculty/ProfessorData/ProPrefFalls23.csv", 3);
        preferenceReader.buildProfessors();
        for (Professor professor : preferenceReader.getProfessors()){
            assertNotNull(professor);
            assertNotNull(professor.getName());
            assertNotNull(professor.getWillingness());
        }
    }
}
