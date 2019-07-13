import java.util.*;

public class SJF {
    private Process[] processes;
    private PriorityQueue<Process> arriveQueue;
    private PriorityQueue<Process> ioQueue;
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
                if (o1.currentBurstTime() == o2.currentBurstTime()) {
                    return o1.id().compareTo(o2.id());
                } else {
                    return o1.currentBurstTime() - o2.currentBurstTime();
                }

        });
        this.readyList = new LinkedList<>();

        this.ioQueue = new PriorityQueue<>(Comparator.comparing(Process::remainingTime));

    }

    public String runSimulation() {
        String result = "Algorithm SJF\n";
        for (Process p : processes) {
            // if p arrived at 0ms, add it to the ready queue
            if (p.getState().equals(ProcessState.READY)) {
                // this.readyList.add(p);
                this.readyQueue.add(p);
                System.out.println(String.format("Process %s has arrived at %dms", p.id(), 0));
            } else {
                arriveQueue.add(p);
            }
        }

        // check if ready list has processes in it. If it does, set the first process in the list as currently running
        Process current = Process.EMPTY;
        if (!readyQueue.isEmpty()) {
            current = readyQueue.poll();
            current.running();
        }

        int time = 0;                              // cpu time
        int numFinished = 0;                       // number of processes finished
        int numProcesses = this.processes.length;  // total number of processes

        while (numFinished != numProcesses) {

            // CPU burst completion

            if (current != Process.EMPTY) {
                boolean status = current.tick();
                if (status) {   // process changed state after tick --> end or goto io
                    if (current.getState().equals(ProcessState.ENDED)) {
                        // process ended --> print message
                        System.out.println(String.format("Process %s has ended the %d/%d burst and start IO of %dms at %dms", current.id(), current.burstIndex(), current.burstSize(), current.remainingTime(), time));
                    } else {
                        // process goes to io --> add to ioQueue
                        ioQueue.add(current);
                        System.out.println(String.format("Process %s has ended the %d/%d burst and start IO of %dms at %dms", current.id(), current.burstIndex(), current.burstSize(), current.remainingTime(), time));
                    }
                    current = Process.EMPTY;
                }
            } else {   // if no process is using the cpu --> poll from the ready queue
                       // if ready queue is not empty, remove the first process in queue and set it as current process
                if (!readyQueue.isEmpty()) {
                    current = readyQueue.poll();
                    current.running();
                    System.out.println(String.format("Process %s being processed at %dms", current.id(), time));
                }
            }

            // IO burst completion

            for (Process p : ioQueue) {
                boolean status = p.tick();

                if (status) {
                    System.out.println(String.format("Process %s has ended IO at %dms", p.id(), time));
                    // if process changed state in ioQueue --> io burst finished
                    ioQueue.remove(p);
                    // check if shorter than current process
                    // if so, do preemption
                    if (p.currentBurstTime() < current.currentBurstTime()) {
                        // TODO: perform preemption
                    } else {
                        readyQueue.add(p);
                    }
                }
            }

            // Newly arrived processes
        }

        return result;
    }
}
