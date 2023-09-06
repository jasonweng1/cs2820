/**
 * 
 */
package edu.uiowa.cs.warp;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Reads the input file, whose name is passed as input parameter to the constructor, and builds a
 * Description object based on the contents. Each line of the file is an entry (string) in the
 * Description object.
 * 
 * @author sgoddard
 * @version 1.4 Fall 2022
 */
public class WorkLoadDescription extends VisualizationObject {

  private static final String EMPTY = "";
  private static final String INPUT_FILE_SUFFIX = ".wld";

  private Description description;
  private String inputGraphString;
  private FileManager fm;
  private String inputFileName;

  WorkLoadDescription(String inputFileName) {
    super(new FileManager(), EMPTY, INPUT_FILE_SUFFIX); // VisualizationObject constructor
    this.fm = this.getFileManager();
    initialize(inputFileName);
  }

  @Override
  public Description visualization() {
    return description;
  }

  @Override
  public Description fileVisualization() {
    return description;
  }

  // @Override
  // public Description displayVisualization() {
  // return description;
  // }

  @Override
  public String toString() {
    return inputGraphString;
  }

  public String getInputFileName() {
    return inputFileName;
  }

  private void initialize(String inputFile) {
    // Get the input graph file name and read its contents
    InputGraphFile gf = new InputGraphFile(fm);
    inputGraphString = gf.readGraphFile(inputFile);
    this.inputFileName = gf.getGraphFileName();
    description = new Description(inputGraphString);
  }
  public static void main(String[] args) {
	  // Creating a WorkLoadDescription object with filename "StressTest.txt"
      WorkLoadDescription p = new WorkLoadDescription("StressTest.txt");
      // Taking the header from the input filename by removing ".txt" extension
      String head = p.getInputFileName().replace(".txt", "");
      // Creating an ArrayList that can store lines from the WorkLoadDescription, excluding the last line
      ArrayList<String> lines = new ArrayList<>();
      for (int i = 1; i < p.visualization().size(); i++) {
          String line = p.visualization().get(i);
          // Checking each line for "{}", and exclude each line that contain it 
          if (!line.contains("{") && !line.contains("}")) {
              lines.add(line);
          }
      }
      // Sorting each of the line in order
      Collections.sort(lines);
      // Reversing order of lines
      Collections.reverse(lines);
      // Print the header
      System.out.println(head);
      // sorted lines is printed in reverse order 
      for (int j = 0; j < lines.size(); j++) {
          System.out.println("Flow " + (j + 1) + ": " + lines.get(j));
      }
  }
  }

