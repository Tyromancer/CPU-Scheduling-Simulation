#include <iostream>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

using namespace std;

int main(int argc, char** argv) {
    // std::cout << "Hello, World!" << std::endl;
    // check for command line arguments
    if (argc < 8 || argc > 9) {
        cerr << "ERROR: <Invalid number of arguments>" << endl;
        return EXIT_FAILURE;
    }

    // get seed for rng
    char* endPtr;
//    srand48(strtol(argv[1], &endPtr, 10));
    long seed = strtol(argv[1], &endPtr, 10);

    // lambda value
    float l = atof(argv[2]);

    // upper bound of rng
    int upperBound = atoi(argv[3]);

    // number of processes
    int n = atoi(argv[4]);

    // time that context switch takes (in milliseconds)
    int tcs = atoi(argv[5]);

    // alpba that is used in estimating CPU burst time
    int alpha = atoi(argv[6]);

    // length of time slice in milliseconds used in RR algorithm
    int lenTimeSlice = atoi(argv[7]);

    // check whether to append to the beginning of the ready queue or append to the end of it
    bool rrAdd = false;
    if (argc == 9) {
        if (strcmp(argv[8], "BEGINNING") == 0) {
            rrAdd = true;
        }
    }

    // TODO

    return EXIT_SUCCESS;
}