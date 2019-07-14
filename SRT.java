import java.util.*;

public class SRT {
    private PriorityQueue<Process> arriveQueue;
    private PriorityQueue<Process> readyQueue;
    private List<Process> ioList;
    private Process[] processes;

    public SRT() {
        this.processes = Process.generateProcesses(true);
        this.readyQueue = new PriorityQueue<>(new Comparator<Process>() {
            @Override
            public int compare(Process o1, Process o2) {
                int time1 = o1.estimateTime() - o1.burstedTime();
                int time2 = o2.estimateTime() - o2.burstedTime();
                if (time1 == time2) {
                    return o1.id().compareTo(o2.id());
                } else {
                    return time1 - time2;
                }
            }
        });
        this.ioList = new ArrayList<>();
        this.arriveQueue = new PriorityQueue<>(Comparator.comparing(Process::arriveTime));
    }

    public String runSimulation() {
        String result = "Algorithm SRT:\n";
        System.out.println("time 0ms: Simulator started for SRT [Q <empty>]");

        for (Process p : this.processes) {
            if (p.remainingTime() == 0) {
                p.setState(ProcessState.READY);
                this.readyQueue.add(p);
                System.out.println(String.format("time %dms: Process %s (tau %dms) arrived; added to ready queue %s", 0, p.id(), p.estimateTime(), queueInfo()));

            } else {
                this.arriveQueue.add(p);
            }
        }

        Process current = Process.EMPTY;

        int time = 0;
        int finished = 0;
        int numProcesses = this.processes.length;
        List<Process> finishedIO = new ArrayList<>();
        boolean ioPreemption = false;

        while (finished != numProcesses) {
            time++;

            if (!finishedIO.isEmpty()) {
                finishedIO.clear();
            }

            // loop through io list to see if any process finished io
            for (Process p : ioList) {
                boolean status = p.tick();
                if ( status ) {
                    p.resetBurstedTime();
                    finishedIO.add(p);      // if process finished IO, add it to the temporary list for later processing
                }
            }

            int arriveNum = 0;
            for (Process p : arriveQueue) {
                boolean changedState = p.tick();
                if (changedState) {
                    p.resetBurstedTime();
                    arriveNum++;
                }
            }

            // process current process using cpu
            if (current != Process.EMPTY) {
                boolean changedState = current.tick();
                if (changedState) {
                    switch ( current.state() ) {

                        case SWITCHIN:
                            current.setState(ProcessState.RUNNING);
                            System.out.println(String.format("time %dms: Process %s (tau %dms) started using the CPU with %dms burst remaining %s", time, current.id(), current.estimateTime(), current.remainingTime() - current.burstedTime(), queueInfo()));
                            if ( !readyQueue.isEmpty() && readyQueue.peek().estimateTime() < current.estimatedRemainingTime()) {
                                System.out.println(String.format("time %dms: Process %s (tau %dms) will preempt %s %s", time, readyQueue.peek().id(), readyQueue.peek().estimateTime(), current.id(), queueInfo()));
                                current.setPreempted(true);
                                current.setState(ProcessState.SWITCHOUT);  // if finished switch in and need to perform preemption, switch out
                                current.setRemainingTime();
                                readyQueue.add(current);
                                current = null;
                                ioPreemption = false;
                            }
                            break;

                        case RUNNING:
                            current.setState(ProcessState.SWITCHOUT);
                            ioPreemption = false;
                            if (current.isLastBurst()) {
                                System.out.println(String.format("time %dms: Process %s terminated %s", time, current.id(), queueInfo()));
                            }
                            else {
                                System.out.println(String.format("time %dms: Process %s (tau %dms) completed a CPU burst; %d bursts to go %s", time, current.id(), current.estimateTime(), current.burstSize() - current.burstIndex() - 1, queueInfo()));
                                System.out.println(String.format("time %dms: Recalculated tau = %dms for process %s %s", time, current.nextEstimateTime(), current.id(), queueInfo()));
                                System.out.println(String.format("time %dms: Process %s switching out of CPU; will block on I/O until time %dms %s", time, current.id(), time + Project.timeSwitch / 2 + current.getIOTime(), queueInfo()));
                            }
                            break;

                        case SWITCHOUT:
                            if (current.isEnded()) {
                                finished++;
                                current.setState(ProcessState.ENDED);

                            } else {
                                // if current was preempted, move the process to the ready queue
                                if (current.isPreempted() && current.remainingTime() != 0) {
                                    current.setState(ProcessState.READY);
                                    readyQueue.add(current);
                                } else {
                                    current.setState(ProcessState.BLOCKED);
                                    current.resetBurstedTime();
                                    current.setPreempted(false);
                                    ioList.add(current);
                                }
                            }

                            current = Process.EMPTY;
                            break;

                        default:
                            System.out.println("Should not enter default branch");
                            break;
                    }
                } else if (!readyQueue.isEmpty() && readyQueue.peek().estimateTime() < current.remainingTime()) {
                    // current process did not change state (running) and preeempt
                    current.setState(ProcessState.SWITCHOUT);
                    current.setPreempted(true);
                    current.setRemainingTime();
                    ioPreemption = true;

                }
            } else {
                if (!this.readyQueue.isEmpty()) {
                    current = this.readyQueue.poll();
                    current.setState(ProcessState.SWITCHIN);

                    boolean changedState = current.tick();
                    if ( changedState ) {
                        current.setState(ProcessState.RUNNING);
                        System.out.println(String.format("time %dms: Process %s (tau %dms) started using the CPU with %dms burst remaining %s", time, current.id(), current.estimateTime(), current.remainingTime() - current.burstedTime(), queueInfo()));

                    }
                }
            }

            List<Process> tempProcesses = new ArrayList<>();
            // finished io processes
            for (Process p : finishedIO) {
                p.nextBurst();               // move p to the next burst
                ioList.remove(p);
                readyQueue.add(p);
                tempProcesses.add(p);
                if (ioPreemption != true && current != null && current.state().equals(ProcessState.RUNNING) && p.estimateTime() < current.remainingTime()) {
                    ioPreemption = true;
                }
            }

            // sort the new unblocked processes for printing
            if (ioPreemption && !tempProcesses.isEmpty()) {
                Collections.sort(tempProcesses, new ProcessComparator());
                for (int i = 0; i < tempProcesses.size(); i++) {
                    if (i == 0 && current != null && !current.state().equals(ProcessState.RUNNING) && tempProcesses.get(0).estimateTime() < current.estimatedRemainingTime()) {
                        System.out.println(String.format("time %dms: Process %s (tau %dms) completed I/O; preempting %s %s", time, tempProcesses.get(0).id(), tempProcesses.get(0).estimateTime(), current.id(), queueInfo()));
                    } else {
                        System.out.println(String.format("time %dms: Process %s (tau %dms) completed I/O; added to ready queue %s", time, tempProcesses.get(i).id(), tempProcesses.get(i).estimateTime(), queueInfo()));
                    }
                }
            }

            for (int i = 0; i < arriveNum; i++) {
                Process p = arriveQueue.poll();
                p.setState(ProcessState.READY);
                p.resetBurstedTime();
                readyQueue.add(p);
                System.out.println(String.format("time %dms: Process %s (tau %dms) arrived; added to ready queue %s", time, p.id(), p.estimateTime(), queueInfo()));
            }
        }

        return result;
    }

    private String queueInfo() {
        if(this.readyQueue.isEmpty())
        {
            return "[Q <empty>]";
        }

        String str = "[Q";
        for(Process p : readyQueue)
        {
            str += String.format(" %s", p.id());
        }
        str += "]";
        return str;
    }

    private class ProcessComparator implements Comparator<Process> {
        @Override
        public int compare(Process o1, Process o2) {
            int t1 = o1.estimateTime() - o1.burstedTime();
            int t2 = o2.estimateTime() - o2.burstedTime();
            if (t1 == t2) {
                return o1.id().compareTo(o2.id());
            } else {
                return t1 - t2;
            }
        }
    }
}
