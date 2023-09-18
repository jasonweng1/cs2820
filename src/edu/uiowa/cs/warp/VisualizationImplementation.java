/**
 * 
 */
package edu.uiowa.cs.warp;

import java.io.File;

/**
 * This class handles visualizations for the WARP system.
 * It creates and displays visualizations based on system or workload choices.
 * 
 * @author sgoddard
 * @version 1.5
 */
public class VisualizationImplementation implements Visualization {

  /**
   * Holds the visualization content.
   */
  private Description visualization;
  
  /**
   * Stores content to be written to a file.
   */
  private Description fileContent;
  
  /**
   * Manages the GUI window for displaying the visualization.
   */
  private GuiVisualization window;
  
  /**
   * The name of the file where the visualization will be saved.
   */
  private String fileName;
  
  /**
   * The name of the input file used in the visualization.
   */
  private String inputFileName;
  
  /**
   * A template for generating file names in the output directory.
   */
  private String fileNameTemplate;
  
  /**
   * Handles file operations.
   */
  private FileManager fm = null;
  
  /**
   * Represents the WARP system interface.
   */
  private WarpInterface warp = null;
  
  /**
   * Represents the system's workload.
   */
  private WorkLoad workLoad = null;
  
  /**
   * Manages the creation and display of visualizations.
   */
  private VisualizationObject visualizationObject;


  /**
   * Constructs a VisualizationImplementation for system-related 
   * visualizations.
   *
   * @param warp			The WARP interface.
   * @param outputDirectory The directory for output files.
   * @param choice			The system choice.
   */
  public VisualizationImplementation(WarpInterface warp, String outputDirectory,
      SystemChoices choice) {
    this.fm = new FileManager();
    this.warp = warp;
    inputFileName = warp.toWorkload().getInputFileName();
    this.fileNameTemplate = createFileNameTemplate(outputDirectory);
    visualizationObject = null;
    createVisualization(choice);
  }

  /**
   * Constructs a VisualizationImplementation for workload-related 
   * visualizations.
   *
   * @param workLoad		The workload.
   * @param outputDirectory	The directory for output files.
   * @param choice			The workload choice.
   */
  public VisualizationImplementation(WorkLoad workLoad, String outputDirectory,
      WorkLoadChoices choice) {
    this.fm = new FileManager();
    this.workLoad = workLoad;
    inputFileName = workLoad.getInputFileName();
    this.fileNameTemplate = createFileNameTemplate(outputDirectory);
    visualizationObject = null;
    createVisualization(choice);
  }

  /**
   * Displays the visualization.
   */
  @Override
  public void toDisplay() {
    // System.out.println(displayContent.toString());
    window = visualizationObject.displayVisualization();
    if (window != null) {
      window.setVisible();
    }
  }

  /**
   * Writes the visualization to a file.
   */
  @Override
  public void toFile() {
    fm.writeFile(fileName, fileContent.toString());
  }

  /**
   * Returns the string representation of the visualization.
   *
   * @return The string representation.
   */
  @Override
  public String toString() {
    return visualization.toString();
  }
  
  /**
   * Creates the visualization based on the chosen system.
   *
   * @param choice The system choice.
   */
  private void createVisualization(SystemChoices choice) {
    switch (choice) { // select the requested visualization
      case SOURCE:
        createVisualization(new ProgramVisualization(warp));
        break;

      case RELIABILITIES:
        // TODO Implement Reliability Analysis Visualization
        createVisualization(new ReliabilityVisualization(warp));
        break;

      case SIMULATOR_INPUT:
        // TODO Implement Simulator Input Visualization
        createVisualization(new NotImplentedVisualization("SimInputNotImplemented"));
        break;

      case LATENCY:
        // TODO Implement Latency Analysis Visualization
        createVisualization(new LatencyVisualization(warp));
        break;

      case CHANNEL:
        // TODO Implement Channel Analysis Visualization
        createVisualization(new ChannelVisualization(warp));
        break;

      case LATENCY_REPORT:
        createVisualization(new ReportVisualization(fm, warp,
            new LatencyAnalysis(warp).latencyReport(), "Latency"));
        break;

      case DEADLINE_REPORT:
        createVisualization(
            new ReportVisualization(fm, warp, warp.toProgram().deadlineMisses(), "DeadlineMisses"));
        break;

      default:
        createVisualization(new NotImplentedVisualization("UnexpectedChoice"));
        break;
    }
  }

  /**
   * Creates the visualization based on the chosen workload.
   *
   * @param choice The workload choice.
   */
  private void createVisualization(WorkLoadChoices choice) {
    switch (choice) { // select the requested visualization
      case COMUNICATION_GRAPH:
        // createWarpVisualization();
        createVisualization(new CommunicationGraph(fm, workLoad));
        break;

      case GRAPHVIZ:
        createVisualization(new GraphViz(fm, workLoad.toString()));
        break;

      case INPUT_GRAPH:
        createVisualization(workLoad);
        break;

      default:
        createVisualization(new NotImplentedVisualization("UnexpectedChoice"));
        break;
    }
  }

  /**
   * Creates the visualization object and associated content.
   *
   * @param obj The visualization object.
   */
  private <T extends VisualizationObject> void createVisualization(T obj) {
    visualization = obj.visualization();
    fileContent = obj.fileVisualization();
    /* display is file content printed to console */
    fileName = obj.createFile(fileNameTemplate); // in output directory
    visualizationObject = obj;
  }

  /**
   * Creates a filename template based on the output directory and input 
   * filename.
   *
   * @param	outputDirectory The output directory.
   * @return The filename template.
   */
  private String createFileNameTemplate(String outputDirectory) {
    String fileNameTemplate;
    var workingDirectory = fm.getBaseDirectory();
    var newDirectory = fm.createDirectory(workingDirectory, outputDirectory);
    // Now create the fileNameTemplate using full output path and input filename
    if (inputFileName.contains("/")) {
      var index = inputFileName.lastIndexOf("/") + 1;
      fileNameTemplate = newDirectory + File.separator + inputFileName.substring(index);
    } else {
      fileNameTemplate = newDirectory + File.separator + inputFileName;
    }
    return fileNameTemplate;
  }

}
