import java.util.*;
import java.io.*;

public class Project {

    public static void main(String[] args) {
    	if (!(args.length == 7 || args.length == 8)) {
			System.err.println("ERROR: Invalid number of command line arguments");
			return;
		}

		long seed;
		try {
			seed = Long.parseLong(args[0]);
		} catch (NumberFormatException e) {
			System.err.println("ERROR: Invalid input for seed " + e.getMessage());
			return;
		}

		double lamb;
		try {
			lamb = Double.parseDouble(args[1]);
		} catch (NumberFormatException e) {
			System.err.println("ERROR: Invalid input for lambda value " + e.getMessage());
			return;
		}

		int upperBound;
		try {
			upperBound = Integer.parseInt(args[2]);
			if (upperBound <= 0) {
				throw new NumberFormatException("Value too small");
			}
		} catch (NumberFormatException e) {
			System.err.println("ERROR: Invalid input for upper bound value " + e.getMessage());
		}

		int numProcess;
		try {
			numProcess = Integer.parseInt(args[3]);
			if (numProcess <= 0) {
				throw new NumberFormatException("Value too small");
			}
		} catch (NumberFormatException e) {
			System.err.println("ERROR: invalid input for number of total processes");
			return;
		}

		int timeSwitch;
		try {
			timeSwitch = Integer.parseInt(args[4]);
			if (timeSwitch < 0) {
				throw new NumberFormatException("Value too small");
			}
		} catch (NumberFormatException e) {
			System.err.println("ERROR: invalid input for time cost of context switch " + e.getMessage());
			return;
		}

		double alpha;
		try {
			alpha = Double.parseDouble(args[5]);
			if (alpha < 0 || alpha >= 1) {
				throw new NumberFormatException("Value out of bound");
			}
		} catch (NumberFormatException e) {
			System.err.println("ERROR: Invalid input for alpha: " + e.getMessage());
			return;
		}

		int timeSlice;
		try {
			timeSlice = Integer.parseInt(args[6]);
			if (timeSlice < 0) {
				throw new NumberFormatException("Value too small");
			}
		} catch (NumberFormatException e) {
			System.err.println("ERROR: Invalid input for length of time slice " + e.getMessage());
		}

		String rrAdd = "END";
		if (args.length == 8 && args[7].equals("BEGINNING")) {
			rrAdd = "BEGINNING";
		}


    }

}
