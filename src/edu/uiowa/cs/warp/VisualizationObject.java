/**
 * The class is being defined within the warp package.
 */
package edu.uiowa.cs.warp;

/**
 * 
 * This class is creating a visualization object. It also handles various initialization scenarios based 
 * the parameters and setting up common properties for the objects. The implementation details and 
 * specific functionalities would be provided by the subclass that extends the base class.
 * @author sgoddard
 * @version 1.5
 *
 */
abstract class VisualizationObject {
  /**
   * 
   * declaring a private field that hold a reference to a FileManager object. 
   */
  private FileManager fm;
  /**
   * 
   * Declare a private field that store a string suffix for visualization.
   */
  private String suffix;
  /**
   * 
   * Declare a private field to store a string name extension for the visual.
   */
  private String nameExtension;
  /**
   * 
   * Declare a constant string that indicates a visualization that has not been implemented. 
   */
  private static final String NOT_IMPLEMENTED = "This visualization has not been implemented.";
  /**
   * 
   * Declare a protected two-dimensional array to hold visualization data.
   */
  protected String[][] visualizationData;
/**
 * Constructor initialize fields using FileManager, WorkLoad, and suffix parameters. It then 
 * assigned the filemanager to the fm field. It will then generate a nameExtension based on the workload
 * parameters. Making the suffix parameters to a field.  
 * 
 * @param fm
 * @param workLoad
 * @param suffix
 */
  VisualizationObject(FileManager fm, WorkLoad workLoad, String suffix) {
    this.fm = fm;
    this.nameExtension = String.format("-%sM-%sE2E",
        String.valueOf(workLoad.getMinPacketReceptionRate()), String.valueOf(workLoad.getE2e()));
    this.suffix = suffix;
    visualizationData = null;
  }
/**
 * Constructor: Initialize fields using FileManager, SystemAttributes, and suffix parameters
 * Assign the FileManager parameter to the 'fm' field.Determine 'nameExtension' based on 
 * SystemAttributes parameters and faults. Assign the 'suffix' parameter to the 'suffix' field.
 * Initialize 'visualizationData' as null.
 * 
 * @param fm
 * @param warp
 * @param suffix
 */
  VisualizationObject(FileManager fm, SystemAttributes warp, String suffix) {
    this.fm = fm;
    if (warp.getNumFaults() > 0) {
      this.nameExtension = nameExtension(warp.getSchedulerName(), warp.getNumFaults());
    } else {
      this.nameExtension =
          nameExtension(warp.getSchedulerName(), warp.getMinPacketReceptionRate(), warp.getE2e());
    }
    this.suffix = suffix;
    visualizationData = null;
  }
/**
 * Constructor: Initialize fields using FileManager, SystemAttributes, nameExtension, and suffix parameters
 * Assign the FileManager parameter to the 'fm' field. Determine 'nameExtension' based on SystemAttributes 
 * parameters and faults, and append 'nameExtension'. Assign the 'suffix' parameter to the 'suffix' field. 
 * Initialize 'visualizationData' as null.
 * @param fm
 * @param warp
 * @param nameExtension
 * @param suffix
 */
  VisualizationObject(FileManager fm, SystemAttributes warp, String nameExtension, String suffix) {
    this.fm = fm;
    if (warp.getNumFaults() > 0) {
      this.nameExtension =
          nameExtension(warp.getSchedulerName(), warp.getNumFaults()) + nameExtension;
    } else {
      this.nameExtension =
          nameExtension(warp.getSchedulerName(), warp.getMinPacketReceptionRate(), warp.getE2e())
              + nameExtension;
    }

    this.suffix = suffix;
    visualizationData = null;
  }
/**
 * Constructor: Initialize fields using FileManager, nameExtension, and suffix parameters.
 * Assign the FileManager parameter to the 'fm' field. Assign the 'nameExtension' parameter 
 * to the 'nameExtension' field. Assign the 'suffix' parameter to the 'suffix' field.
 * Initialize 'visualizationData' as null
 * @param fm
 * @param nameExtension
 * @param suffix
 */
  VisualizationObject(FileManager fm, String nameExtension, String suffix) {
    this.fm = fm;
    this.nameExtension = nameExtension;
    this.suffix = suffix;
    visualizationData = null;
  }
/**
 * Define a private method to generate a name extension for visualization based on scheduler name, 
 * min packet reception rate, and end-to-end delay. Format the extension string using provided parameters.
 * @param schName
 * @param m
 * @param e2e
 * @return
 */
  private String nameExtension(String schName, Double m, double e2e) {
    String extension =
        String.format("%s-%sM-%sE2E", schName, String.valueOf(m), String.valueOf(e2e));
    return extension;
  }
/**
 * Define a private method to generate a name extension for visualization based 
 * on scheduler name and the number of faults.Format the extension string using provided parameters
 * @param schName
 * @param numFaults
 * @return
 */
  private String nameExtension(String schName, Integer numFaults) {
    String extension = String.format("%s-%sFaults", schName, String.valueOf(numFaults));
    return extension;
  }

  /**
   * Getter method to retrieve the FileManager object.
   * 
   * @return the fm
   */
  public FileManager getFileManager() {
    return fm;
  }
/**
 * Method to create a Description object for visualization. Then it will Create a 
 * header row string and add it to the Description. Lastly it will Add a message indicating that 
 * the visualization is not implemented.
 * @return
 */
  public Description visualization() {
    Description content = new Description();
    var data = createVisualizationData();

    if (data != null) {
      String nodeString = String.join("\t", createColumnHeader()) + "\n";
      content.add(nodeString);

      for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
        var row = data[rowIndex];
        String rowString = String.join("\t", row) + "\n";
        content.add(rowString);
      }
    } else {
      content.add(NOT_IMPLEMENTED);
    }
    return content;
  }
/**
 * Method to create a file with a given file name template.
 * @param fileNameTemplate
 * @return
 */
  public String createFile(String fileNameTemplate) {
    return fm.createFile(fileNameTemplate, nameExtension, suffix);
  }
/**
 * Method to create a Description object for the file visualization. It first Create 
 * a Description object for the header. Then it add visualization content to the Description.
 * After it will add footer content to the Description. Then it will return the filecontent.
 * @return
 */
  public Description fileVisualization() {
    Description fileContent = createHeader();
    fileContent.addAll(visualization());
    fileContent.addAll(createFooter());
    return fileContent;
  }
/**
 * Method for displaying the visualization in a graphical user interface.
 * @return
 */
  public GuiVisualization displayVisualization() {
    return null; // not implemented
  }
/**
 * Protected method to create the header for the visualization
 * @return
 */
  protected Description createHeader() {
    Description header = new Description();
    return header;
  }
/**
 * Protected method to create the footer for the visualization
 * @return
 */
  protected Description createFooter() {
    Description footer = new Description();
    return footer;
  }
/**
 * Protected method to create column headers for the visualization
 * @return
 */
  protected String[] createColumnHeader() {
    return new String[] {NOT_IMPLEMENTED};
  }
/**
 * Protected method to create the visualization data (returns null, to be implemented in subclasses).
 * After if its not implemented then it will return null. 
 * @return
 */
  protected String[][] createVisualizationData() {
    return visualizationData; // not implemented--returns null
  }
}
