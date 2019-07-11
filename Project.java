import java.util.*;
import java.io.*;

public class Project {

    public static void main(String[] args) {
    	if (args.length != 7 || args.length !- 8) {
		System.out.println("ERROR: Invalid number of command line arguments");
		return;
	}

	long seed;
	try {
		seed = Long.parseLong(argv[1]);
	} catch (NumberFormatException e) {
		System.out.println("Invalid input for seed");
		return;
	}

	double lamb;
	try {
		lamb = Double.parseDouble(argv[2]);
	} catch (NumberFormatException e) {
		System.out.println("Invalid input for lambda value");
		return;
	}


    }
}
