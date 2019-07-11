#import time
#import sys


if __name__ == "__main__":
    if len(sys.argv) != 7 or len(sys.argv) != 8:
        print("ERROR: Invalid number of arguments")
        exit(1)

    #TO DO: parse all command line arguments and validate
    seed = argv[1]
    if !seed.isdigit():
        print("ERROR: invalid input for random number seed")
        exit(1)
    seed = int(argv[1])

    lamb = argv[2]
    try:
        lamb = float(argv[2])
    catch ValueError e:
        pass

