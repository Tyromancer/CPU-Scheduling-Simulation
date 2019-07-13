import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class SJF {
    private Process[] processes;
    private PriorityQueue<Process> arriveQueue;
    private PriorityQueue<Process> ioQueue;
    private List<Process> readyList;

    public SJF() {
        this.processes = Process.generateProcesses();
        this.arriveQueue = new PriorityQueue<>(Comparator.comparing(Process::arriveTime));
//        this.arriveQueue = new PriorityQueue<Process>(new Comparator<Process>() {
//			@Override
//			public int compare(Process p1, Process p2) {
//				return p1.arriveTime() - p2.arriveTime();
//			}
//		});
        this.readyList = new ArrayList<>();
        this.ioQueue = new PriorityQueue<>(Comparator.comparing(Process::remainingTime));

    }

    public String runSimulation() {
        String result = "Algorithm SJF\n";
        for (Process p : processes) {
            if (p.getState().equals(ProcessState.NA)) {
                this.readyList.add(p);
                System.out.println(String.format("Process %s has arrived at %dms", p.id(), 0));
            } else {
                arriveQueue.add(p);
            }
        }

        

        return result;
    }
}
