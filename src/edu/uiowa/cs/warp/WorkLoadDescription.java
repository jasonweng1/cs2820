/**
 * 
 */
package edu.uiowa.cs.warp;
import java.util.Arrays;
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
  
  public static String unbrace(String braceful) {
	// Get rid of the braces and subsequent empty lines
	braceful = braceful.replaceAll("\\{", "");
	braceful = braceful.replaceAll("\\}", "");
	braceful = braceful.replaceAll("(?m)^[ \\t]*\\r?\\n", "\n");
	return braceful;
  }
  
  public static String[] splitString(String unsplitted) {
	// Split the string into the title and the data
	String[] stringSplit = unsplitted.split("\n", 2);
	return stringSplit;
  }
  
  public static String[] sort(String unsorted) {
	// Put the data into an array, sort it, and reverse it
	String[] linesArray = unsorted.split("\n");
	Arrays.sort(linesArray);
	Collections.reverse(Arrays.asList(linesArray));
	return linesArray;
  }
  
  public static String[] label(String[] data) {
	// Label each line of data with "Flow x: "
	for (int i = 0; i < data.length; i++) {
		data[i] = "Flow " + (i + 1) + ": " + data[i];
	}
	return data;
  }
  
  public static String format(String text) {
	  // Process the text into organized data (with the methods above)
	  text = unbrace(text);
	  
	  String title = splitString(text)[0];
	  String data = splitString(text)[1];
	  
	  String[] sortedData = sort(data);
	  String[] labeledData = label(sortedData);

	  // Return the title combined with the (finally labeled and sorted) data
	  String result = "";
	  
	  result += title;
	  for (int i = 0; i < labeledData.length; i++) {
		  result += labeledData[i];
	  }
	  
	  return result;
  }

  public static void main(String[] args) {
	  // Make a new WorkLoadDescription and get its contents as a string
	  WorkLoadDescription test = new WorkLoadDescription("StressTest.txt");
	  String testString = test.toString();
	  
	  // Format the string and then print the results
	  String result = format(testString);
	  System.out.println(result);
  }
  
}
