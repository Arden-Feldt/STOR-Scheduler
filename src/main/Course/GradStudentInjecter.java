package main.Course;

import main.Faculty.Professor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class GradStudentInjecter {
    private String path;
    private String outputPath;
    public GradStudentInjecter (String path, String outputPath) {
            this.path = path;
            this.outputPath = outputPath;
    }

    public void inject(){
        String line;
        String delimiter = ",";

        List<String[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String headerLine = br.readLine(); // Gets the header line out of the way

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(delimiter);
                data.add(fields);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String[] row : data) {


        }
    }

}
