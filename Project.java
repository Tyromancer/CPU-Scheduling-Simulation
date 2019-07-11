import java.util.*;
import java.io.*;

public class Project {

    public static void main(String[] args) {
    	if (args.length != 7 || args.length != 8) {
		System.out.println("ERROR: Invalid number of command line arguments");
		return;
	}

	long seed;
	try {
		seed = Long.parseLong(args[1]);
	} catch (NumberFormatException e) {
		System.out.println("ERROR: Invalid input for seed " + e.getMessage());
		return;
	}

	double lamb;
	try {
		lamb = Double.parseDouble(args[2]);
	} catch (NumberFormatException e) {
		System.out.println("ERROR: Invalid input for lambda value " + e.getMessage());
		return;
	}
	
	int upperBound;
	try {
		upperBound = Integer.parseInt(args[3]);
		if (upperBound <= 0) {
			throw new NumberFormatException("Value too small");
		}
	} catch (NumberFormatException e) {
		System.out.println("ERROR: Invalid input for upper bound value " + e.getMessage());
	}

	int numProcess;
	try {
		numProcess = Integer.parseInt(args[4]);
		if (numProcess <= 0) {
			throw new NumberFormatException("Value too small");
		}
	} catch (NumberFormatException e) {
		System.out.println("ERROR: invalid input for number of total processes");
		return;
	}

	int timeSwitch;
	try {
		timeSwitch = Integer.parseInt(args[5]);
		if (timeSwitch < 0) {
			throw new NumberFormatException("Value too small");
		}
	} catch (NumberFormatException e) {
		System.out.println("ERROR: invalid input for time cost of context switch " + e.getMessage());
	}




    }
}
