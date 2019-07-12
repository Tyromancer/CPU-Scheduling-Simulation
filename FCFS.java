import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * A class that represents the First Come First Serve CPU scheduling algorithm
 */
public class FCFS {
	private Process[] processes;
    private PriorityQueue<Process> arriveQueue;
    
    public FCFS() {
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
        
        for (int i = 0; i < processes.length; i++) {
			arriveQueue.add(processes[i]);
		}
        
        Process p;
        while(!arriveQueue.isEmpty())
        {
        	p = arriveQueue.poll();
        	System.out.println(String.format("Process: %s with arriveTime: %d", p.id(), p.arriveTime()));
        }

        return result;
    }
}
