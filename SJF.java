import java.util.*;

public class SJF {
    private Process[] processes;
    private PriorityQueue<Process> arriveQueue;
    private List<Process> ioList;
    private LinkedList<Process> readyList;
    private PriorityQueue<Process> readyQueue;

    public SJF() {
        this.processes = Process.generateProcesses();
        this.arriveQueue = new PriorityQueue<>(Comparator.comparing(Process::arriveTime));
//        this.arriveQueue = new PriorityQueue<Process>(new Comparator<Process>() {
//			@Override
//			public int compare(Process p1, Process p2) {
//				return p1.arriveTime() - p2.arriveTime();
//			}
//		});
        this.readyQueue = new PriorityQueue<>((Process o1, Process o2) -> {
                if (o1.estimateTime() == o2.estimateTime()) {
                    return o1.id().compareTo(o2.id());
                } else {
                    return o1.estimateTime() - o2.estimateTime();
                }

        });
        this.readyList = new LinkedList<>();

        this.ioList = new ArrayList<>();

    }

    public String runSimulation() {
        String result = "Algorithm SJF\n";
        for (Process p : processes) {
            // if p arrived at 0ms, add it to the ready queue
            if (p.state().equals(ProcessState.READY)) {
                this.readyQueue.add(p);
                System.out.println(String.format("Process %s has arrived at %dms", p.id(), 0));
            } else {
                arriveQueue.add(p);
            }
        }

        // check if ready list has processes in it. If it does, set the first process in the list as currently running
        Process current = Process.EMPTY;

        int time = 1;                              // cpu time
        int numFinished = 0;                       // number of processes finished
        int numProcesses = this.processes.length;  // total number of processes
        List<Process> finishedIO = new ArrayList<>();

        while (numFinished != numProcesses) {

            // IO completion
            if (!finishedIO.isEmpty()) {
                finishedIO.clear();
            }
            
            for (Process p : ioList) {
                if ( p.tick() ) {
                    finishedIO.add(p);
                }
            }

            int arriveNum = 0;
            for (Process p : arriveQueue) {
                if ( p.tick() ) {
                    arriveNum++;
                }
            }

            // CPU burst completion
            if (current != Process.EMPTY) {
                boolean status = current.tick();
                if ( status ) {
                    // TODO: add print
                    switch ( current.state() ) {
                        case SWITCHIN:

                            current.setState(ProcessState.RUNNING);
                            break;

                        case RUNNING:
                            current.setState(ProcessState.SWITCHOUT);
                            break;
                    }
                }
            }
            

//            if (current != Process.EMPTY) {
//                boolean status = current.tick();
//                if (status) {   // process changed state after tick --> end or goto io
//                    if (current.state().equals(ProcessState.ENDED)) {
//                        // process ended --> print message
//                        System.out.println(String.format("Process %s has ended the %d/%d burst and start IO of %dms at %dms", current.id(), current.burstIndex(), current.burstSize(), current.remainingTime(), time));
//                    } else {
//                        // process goes to io --> add to ioQueue
//                        ioQueue.add(current);
//                        System.out.println(String.format("Process %s has ended the %d/%d burst and start IO of %dms at %dms", current.id(), current.burstIndex(), current.burstSize(), current.remainingTime(), time));
//                    }
//                    current = Process.EMPTY;
//                }
//            } else {   // if no process is using the cpu --> poll from the ready queue
//                       // if ready queue is not empty, remove the first process in queue and set it as current process
//                if (!readyQueue.isEmpty()) {
//                    current = readyQueue.poll();
//                    current.setState(ProcessState.RUNNING);
//                    System.out.println(String.format("Process %s being processed at %dms", current.id(), time));
//                }
//            }
            

            // IO burst completion


            // Newly arrived processes
        }

        return result;
    }
}
