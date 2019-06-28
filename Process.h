

#ifndef UNTITLED_PROCESS_H
#define UNTITLED_PROCESS_H
#include <iostream>

class Process {
public:

    // Constructors
    Process() {this->init();}  // default creator

    // observer functions
    int getState() {return this->state;}

    // mutator functions
    void setState(int s) {this->state = s;}

private:
    void init();   // default init

    // data representation
    int state;     // 0: READY, 1: RUNNING, 2: BLOCKED

};
std::ostream &operator<< (std::ostream &ostr, const Process &p); // overload << for output

#endif //UNTITLED_PROCESS_H
