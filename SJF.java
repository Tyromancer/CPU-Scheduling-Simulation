import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class SJF {
    private Process[] processes;
    private PriorityQueue<Process> arriveQueue;

//    public SJF(Rand48 rng, int numProcess, int timeSwitch, double alpha, double lamb) {
//        this.rng = rng;
//        this.numProcess = numProcess;
//        this.timeSwitch = timeSwitch;
//        this.alpha = alpha;
//        this.processes = new ArrayList<>();
//        this.readyQueue = new ArrayList<>();
//    }
    public SJF() {
        this.processes = Process.generateProcesses();
        this.arriveQueue = new PriorityQueue<Process>(new Comparator<Process>() {

			@Override
			public int compare(Process p1, Process p2) {
				return p1.arriveTime() - p2.arriveTime();
			}
		});
    }

    public String runSimulation() {
        String result = "Algorithm SJF\n";

        

        return result;
    }
}
