import Faculty.PreferenceReader;
import Faculty.Professor;

import java.util.HashSet;


public class Main {
    public static void main(String[] args) {

        // TODO: Implement populate classes with the data

        PreferenceReader preferenceReader = new PreferenceReader("src/ProfessorData/ProPrefFalls23.csv");

        preferenceReader.buildProfessors();

        test_prof_impl(preferenceReader);

        // TODO: implement Gurobi java API

        // TODO: Run optimizer

        // TODO: output optimal solution in legible format

    }

    public static void test_prof_impl(PreferenceReader preferenceReader){
        HashSet<Professor> professors = preferenceReader.getProfessors();

        for (Professor professor: professors){
            System.out.println(professor.toString());
        }
    }
}