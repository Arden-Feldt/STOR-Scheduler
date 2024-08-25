package main.Course;

import main.Faculty.Professor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


public class GradStudentInjecter {
  private final String path;
  private final String outputPath;

  public GradStudentInjecter(String path, String outputPath) {
    this.path = path;
    this.outputPath = outputPath;
  }

  public void inject() {
    try {
      throw new Exception("bazinga");
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
  }
}
