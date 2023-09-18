package edu.uiowa.cs.warp;

import edu.uiowa.cs.warp.WarpDSL.InstructionParameters;
import java.util.HashMap;

/**
 * The LatencyAnalysis class is responsible for analyzing latency in a given program.
 * and workload configuration. It computes and reports the maximum latency for each 
 * flow instance and checks if any of them miss their deadlines. The analysis results 
 * are stored in a object for reporting.
 * 
 * @author sgoddard2
 * @version 1.6
 */
public class LatencyAnalysis {
  /**
   * Constant string used to indicate that a flow instance missed its deadline.
   */
  private static String DEADLINE_MISS = " => DEADLINE MISS";
  /**
   * A separator string used to visually separate different flow reports.
   */
  private static String FLOW_SEPARATOR = "******************************\n";
  /**
   * Description object to store the latency analysis report.
   */
  private Description latencyReport;
  /**
   * The Program configuration object representing the program being analyzed.
   */
  private Program program;
  /**
   * The WorkLoad configuration object representing the workload associated with the program.
   */
  private WorkLoad workload;
  /**
   * The ProgramSchedule object representing the schedule of program instructions.
   */
  private ProgramSchedule programTable;
  /**
   * A mapping of node names to their corresponding indexes in the program schedule.
   */
  private HashMap<String, Integer> nodeIndex;
/**
 * Constructs a LatencyAnalysis object using a WarpInterface configuration.
 * @param warp The WarpInterface configuration from which to derive the program and workload.
 */
  LatencyAnalysis(WarpInterface warp) {
    this.latencyReport = new Description();
    this.program = warp.toProgram();
    this.workload = warp.toWorkload();
    this.programTable = program.getSchedule();
    this.nodeIndex = program.getNodeMapIndex();
  }
/**
 * Constructs a LatencyAnalysis object using a provided Program configuration.
 * @param program program The Program configuration to be analyzed.
 */
  LatencyAnalysis(Program program) {
    this.latencyReport = new Description();
    this.program = program;
    this.workload = program.toWorkLoad();
    this.programTable = program.getSchedule();
    this.nodeIndex = program.getNodeMapIndex();
  }
/**
 * Computes and generates a latency analysis report for the configured program and workload.
 * @return
 */
  public Description latencyReport() {
    /*
     * Build a latency report. Flows are output in priority order (based on the priority used to
     * build the program. The latency for each instance of the flow is reported as follows
     * "Maximum latency for FlowName:Instance is Latency"
     * 
     * For flow instances that have latency > deadline, then the latency message is appended with
     * the string " => DEADLINE MISS"
     * 
     * A line of 30 '*' characters separates each group of flow instance reports.
     * 
     * When there are not enough transmissions attempted between the release and the next release of
     * an instance, then the latency is not computed (as we assume deadline <= period. Thus, the
     * report is: "UNKNOWN latency for FlowName:Instance; Not enough transmissions attempted"
     * 
     */

    var flows = workload.getFlowNamesInPriorityOrder();
    for (String flowName : flows) {
      var time = 0;

      var nodes = workload.getNodesInFlow(flowName); // names of nodes in flow
      var flowSnkIndex = nodes.length - 1;
      /* get snk of last link in the flow, which is also the Flow snk node */
      String snk = nodes[flowSnkIndex];
      /* get the src of last link in the flow */
      String src = nodes[flowSnkIndex - 1];
      /* get (column) indexes into programTable of these nodes */
      var snkIndex = nodeIndex.get(snk);
      var srcIndex = nodeIndex.get(src);
      /* get the array containing the number of transmissions required for each link in the flow */
      var numTxAttemptsPerLink = workload.getNumTxAttemptsPerLink(flowName);
      /* get the number of transmission required for the last link in the flow */
      var numTxRequired = numTxAttemptsPerLink[numTxAttemptsPerLink.length - 1];
      var numTxProcessed = 0; // num of Tx seen in the program schedule so far
      var instance = 0;
      while (time < workload.getHyperPeriod()) {
        /* get next release time and absolute deadline of the flow */
        var releaseTime = workload.nextReleaseTime(flowName, time);
        var deadline = workload.nextAbsoluteDeadline(flowName, releaseTime);
        var nextReleaseTime = workload.nextReleaseTime(flowName, deadline);
        // var latency = 0;
        time = releaseTime;
        numTxProcessed = 0; // num of Tx seen in the program schedule so far
        while (time < nextReleaseTime) {
          /* get instruction strings at these to locations */
          String instr1 = programTable.get(time, srcIndex);
          String instr2 = programTable.get(time, snkIndex);
          numTxProcessed += numMatchingTx(flowName, src, snk, instr1);
          numTxProcessed += numMatchingTx(flowName, src, snk, instr2);
          if (numTxProcessed == numTxRequired) {
            /*
             * all required Tx attempts have been made compute and record latency
             */
            var latency = time - releaseTime + 1;
            // report latency
            String latencyMsg =
                String.format("Maximum latency for %s:%d is %d", flowName, instance, latency);
            if (latency > deadline) {
              /* deadline missed, so color the text red */
              latencyMsg += DEADLINE_MISS;
            }
            latencyMsg += "\n";
            latencyReport.add(latencyMsg);
            time = nextReleaseTime;
          } else {
            time++;
          }

        }
        if (numTxProcessed < numTxRequired) {
          /*
           * This flow missed its deadline with required number of Tx!! This message should not be
           * printed with the schedulers built
           */
          String latencyMsg =
              String.format("UNKNOWN latency for %s:%d; Not enough transmissions attempted\n",
                  flowName, instance);
          latencyReport.add(latencyMsg);
        }
        instance++;
      }
      String flowSeparator = FLOW_SEPARATOR;
      latencyReport.add(flowSeparator);
    }
    return latencyReport;
  }
/**
 * Counts the number of matching transmission attempts in a program instruction. 
 * @param flow
 * @param src
 * @param snk
 * @param instr
 * @return
 */
  public Integer numMatchingTx(String flow, String src, String snk, String instr) {
    var numTx = 0;

    if (flow == null || src == null || snk == null || instr == null) {
      /* make sure all parameters are valid */
      return numTx;
    }
    /*
     * get a Warp instruction parser object and then get the instruction parameters from the
     * instruction string.
     */
    var dsl = new WarpDSL();
    var instructionParametersArray = dsl.getInstructionParameters(instr);

    for (InstructionParameters entry : instructionParametersArray) {
      String flowName = entry.getFlow();
      if (flowName.equals(flow)) {
        /*
         * This instruction is for the flow we want. (flow name is set for push/pull instructions,
         * which are all we want. If not push/pull, then we skip this instruction.) If flow, src,
         * and snk names in instruction match input parameters, then we have a Tx attempt.
         */
        if (src.equals(entry.getSrc()) && snk.equals(entry.getSnk())) {
          /* flow, src, and snk match, so increment Tx attempts */
          numTx++;
        }
      }
    }
    return numTx;
  }

}
