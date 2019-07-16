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
        // TODO :
        // average CPU burst time
        // average wait time
        // average turnaround time
        // total number of context switches
        // total number of preemptions

        String result = "Algorithm SRT\n";
        System.out.println("time 0ms: Simulator started for SRT [Q <empty>]");
        int total = 0;
        int totalBursts = 0;
        for (Process p : this.processes) {
            total += p.totalBurstTime();
            totalBursts += p.burstSize();
        }
        result = result.concat(String.format("-- average CPU burst time: %.3f ms\n", (double) total / (double) totalBursts));

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

        int totalContextSwitches = 0;
        int totalPreemptions = 0;

        int numProcesses = this.processes.length;
        List<Process> finishedIO = new ArrayList<>();

        String preemptedID = "";

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
                            current.setPreempted(false);
                            if (time < 1000) {
                                System.out.println(String.format("time %dms: Process %s (tau %dms) started using the CPU with %dms burst remaining %s", time, current.id(), current.estimateTime(), current.remainingTime() - current.burstedTime(), queueInfo()));
                            }
                            current.changeRemainingTime();
                            if ( !readyQueue.isEmpty() && readyQueue.peek().estimateTime() < current.estimatedRemainingTime()) {
                                if (time < 1000) {
                                    System.out.println(String.format("time %dms: Process %s (tau %dms) will preempt %s %s", time, readyQueue.peek().id(), readyQueue.peek().estimateTime(), current.id(), queueInfo()));
                                }
                                totalPreemptions++;
                                current.setPreempted(true);
                                current.setState(ProcessState.SWITCHOUT);  // if finished switch in and need to perform preemption, switch out
                                totalContextSwitches++;
                            }
                            break;

                        case RUNNING:
                            current.setState(ProcessState.SWITCHOUT);
                            totalContextSwitches++;
                            if (current.isLastBurst()) {
                                System.out.println(String.format("time %dms: Process %s terminated %s", time, current.id(), queueInfo()));
                            }
                            else {
                                if (current.burstSize() - current.burstIndex() - 1 != 1) {
                                    if (time < 1000) {
                                        System.out.println(String.format("time %dms: Process %s (tau %dms) completed a CPU burst; %d bursts to go %s", time, current.id(), current.estimateTime(), current.burstSize() - current.burstIndex() - 1, queueInfo()));
                                    }
                                } else {
                                    if (time < 1000) {
                                        System.out.println(String.format("time %dms: Process %s (tau %dms) completed a CPU burst; %d burst to go %s", time, current.id(), current.estimateTime(), current.burstSize() - current.burstIndex() - 1, queueInfo()));
                                    }
                                }

                                if (time < 1000) {
                                    System.out.println(String.format("time %dms: Recalculated tau = %dms for process %s %s", time, current.nextEstimateTime(), current.id(), queueInfo()));
                                    System.out.println(String.format("time %dms: Process %s switching out of CPU; will block on I/O until time %dms %s", time, current.id(), time + Project.timeSwitch / 2 + current.getIOTime(), queueInfo()));
                                }
                            }
                            break;

                        case SWITCHOUT:
                            if (current.isEnded()) {
                                finished++;

                                current.setState(ProcessState.ENDED);
                                current.setEndTime(time);
                            } else {
                                // if current was preempted, move the process to the ready queue
                                if (current.isPreempted()) {
                                    current.setState(ProcessState.READY);
                                    current.setRemainingTime();
                                    readyQueue.add(current);
                                    preemptedID = current.id();
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
                }
            } else {
                if (!this.readyQueue.isEmpty()) {
                    current = this.readyQueue.poll();
                    current.setState(ProcessState.SWITCHIN);
                    totalContextSwitches++;

                    boolean changedState = current.tick();
                    if ( changedState ) {
                        current.setState(ProcessState.RUNNING);
                        if (time < 1000) {
                            System.out.println(String.format("time %dms: Process %s (tau %dms) started using the CPU with %dms burst remaining %s", time, current.id(), current.estimateTime(), current.remainingTime() - current.burstedTime(), queueInfo()));
                        }
                    }
                }
            }

            // add waiting time for all processes in the ready queue
            for (Process p : readyQueue) {
                if (!p.id().equals(preemptedID)) {
                    p.addWaitingTime();
                }

            }
            preemptedID = "";

            Collections.sort(finishedIO, Comparator.comparing(Process::id));
            // finished io processes
            for (int i = 0; i < finishedIO.size(); i++) {
                Process p = finishedIO.get(i);
                p.nextBurst();               // move p to the next burst
                ioList.remove(p);
                readyQueue.add(p);
                if (i == 0 && current != null && current.state().equals(ProcessState.RUNNING) && p.estimateTime() < current.estimatedRemainingTime()) {
                    // TODO: time 24086, test 4
                    if (time < 1000) {
                        System.out.println(String.format("time %dms: Process %s (tau %dms) completed I/O; preempting %s %s", time, p.id(), p.estimateTime(), current.id(), queueInfo()));
                    }
                    current.setState(ProcessState.SWITCHOUT);
                    totalPreemptions++;
                    totalContextSwitches++;
                    current.setPreempted(true);
                } else {
                    if (time < 1000) {
                        System.out.println(String.format("time %dms: Process %s (tau %dms) completed I/O; added to ready queue %s", time, p.id(), p.estimateTime(), queueInfo()));
                    }
                }
            }

            for (int i = 0; i < arriveNum; i++) {
                Process p = arriveQueue.poll();
                p.setState(ProcessState.READY);
                p.resetBurstedTime();
                readyQueue.add(p);

                if (time < 1000) {
                    System.out.println(String.format("time %dms: Process %s (tau %dms) arrived; added to ready queue %s", time, p.id(), p.estimateTime(), queueInfo()));
                }
            }
        }


        int totalWaitingTime = 0;
        for (Process p : processes) {
            totalWaitingTime += p.getWaitingTime();
        }
        result += String.format("-- average wait time: %.3f ms\n", (double) totalWaitingTime / (double) totalBursts);

        int totalTurnaroundTime = 0;
        for (Process p : processes) {
            totalTurnaroundTime += (p.endTime() - p.arriveTime() - p.getTotalIOTime());
        }
        result += String.format("-- average turnaround time: %.3f ms\n", (double) totalTurnaroundTime / (double) totalBursts);

        result += String.format("-- total number of context switches: %d\n", totalContextSwitches / 2);
        result += String.format("-- total number of preemptions: %d\n", totalPreemptions);

        System.out.println(String.format("time %dms: Simulator ended for SRT [Q <empty>]", time));
        return result;
    }

    private String queueInfo() {
        if(this.readyQueue.isEmpty())
        {
            return "[Q <empty>]";
        }

        String str = "[Q";

        List<Process> tempList = new LinkedList<>();
        while (readyQueue.peek() != null) {
            tempList.add(readyQueue.poll());
        }
        Collections.sort(tempList, new ProcessComparator());
        for (int i = 0; i < tempList.size(); i++) {
            str += String.format(" %s", tempList.get(i).id());
        }
        str += "]";
        for (int i = 0; i < tempList.size(); i++) {
            readyQueue.add(tempList.get(i));
        }
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
