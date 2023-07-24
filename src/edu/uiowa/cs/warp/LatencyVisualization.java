package edu.uiowa.cs.warp;

/**
 * LatencyVisualization creates the visualizations for the liability analysis of the WARP program.
 * <p>
 * 
 * CS2820 Fall 2023 Project: Implement this class to create the file visualization that is
 * requested in Warp.
 * 
 * @author sgoddard
 * @version 1.6
 *
 */
public class LatencyVisualization extends VisualizationObject {

  // TODO Auto-generated class stub for unimplemented visualization

  private static final String SOURCE_SUFFIX = ".la";
  private static final String OBJECT_NAME = "Latency Analysis";
  private WarpInterface warp;
  private LatencyAnalysis la;

  LatencyVisualization(WarpInterface warp) {
    super(new FileManager(), warp, SOURCE_SUFFIX);
    this.warp = warp;
    this.la = warp.toLatencyAnalysis();
  }
}
