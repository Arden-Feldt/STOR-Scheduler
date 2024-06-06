import Faculty.PreferenceReader;
import Faculty.Professor;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {

        // TODO: Implement populate classes with the data

        PreferenceReader preferenceReader = new PreferenceReader("src/ProfessorData/ProPrefFalls23.csv");

        preferenceReader.buildProfessors();

        for (Professor professor: preferenceReader.getProfessors()){
            System.out.println(professor.toString());
        }

        // TODO: implement Gurobi java API

        // TODO: Run optimizer

        // TODO: output optimal solution in legible format

    }
}