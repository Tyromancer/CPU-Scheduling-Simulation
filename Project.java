import java.util.*;
import java.io.*;

public class Project {
	public static long seed;
	public static double lamb;
	public static int upperBound;
	public static int numProcess;
	public static int timeSwitch;
	public static double alpha;
	public static int timeSlice;
	public static String rrAdd;

    public static void main(String[] args) {
		// check number of command line arguments
    	if (!(args.length == 7 || args.length == 8)) {
			System.err.println("ERROR: Invalid number of command line arguments");
			return;
		}

		// check and setup seed for rng
		try {
			seed = Long.parseLong(args[0]);
		} catch (NumberFormatException e) {
			System.err.println("ERROR: Invalid input for seed " + e.getMessage());
			return;
		}

		// check and setup lambda
		try {
			lamb = Double.parseDouble(args[1]);
		} catch (NumberFormatException e) {
			System.err.println("ERROR: Invalid input for lambda value " + e.getMessage());
			return;
		}

		// check and setup upper bound for random numbers
		try {
			upperBound = Integer.parseInt(args[2]);
			if (upperBound <= 0) {
				throw new NumberFormatException("Value too small");
			}
		} catch (NumberFormatException e) {
			System.err.println("ERROR: Invalid input for upper bound value " + e.getMessage());
		}

		// check and setup number of processes
		try {
			numProcess = Integer.parseInt(args[3]);
			if (numProcess <= 0) {
				throw new NumberFormatException("Value too small");
			}
		} catch (NumberFormatException e) {
			System.err.println("ERROR: invalid input for number of total processes");
			return;
		}

		// check and setup time cost for context switch
		try {
			timeSwitch = Integer.parseInt(args[4]);
			if (timeSwitch < 0) {
				throw new NumberFormatException("Value too small");
			}
		} catch (NumberFormatException e) {
			System.err.println("ERROR: invalid input for time cost of context switch " + e.getMessage());
			return;
		}

		// check and setup alpha
		try {
			alpha = Double.parseDouble(args[5]);
			if (alpha < 0 || alpha >= 1) {
				throw new NumberFormatException("Value out of bound");
			}
		} catch (NumberFormatException e) {
			System.err.println("ERROR: Invalid input for alpha: " + e.getMessage());
			return;
		}

		// check and setup length of time slice
		try {
			timeSlice = Integer.parseInt(args[6]);
			if (timeSlice < 0) {
				throw new NumberFormatException("Value too small");
			}
		} catch (NumberFormatException e) {
			System.err.println("ERROR: Invalid input for length of time slice " + e.getMessage());
		}

		// check and set if add to beginning or end for RR algorithm
		rrAdd = "END";
		if (args.length == 8 && args[7].equals("BEGINNING")) {
			rrAdd = "BEGINNING";
		}

		// TODO: create algo instances and run them
//		FCFS fcfs = new FCFS();
//		String fcfsStat = fcfs.runSimulation();
//
//		System.out.println();
//		System.out.println("FCFS STAT:");
//		System.out.println(fcfsStat);
//
//		SJF sjf = new SJF();
//		sjf.runSimulation();
		SRT srt = new SRT();
		srt.runSimulation();
    }

}
