import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * A class that represents the First Come First Serve CPU scheduling algorithm
 */
public class FCFS {
	private Process[] processes;
    private PriorityQueue<Process> arriveQueue;
    private LinkedList<Process> readyList;
    private PriorityQueue<Process> ioQueue;
    
    public FCFS() {
        this.processes = Process.generateProcesses();
//        this.arriveQueue = new PriorityQueue<Process>(new Comparator<Process>() {
//
//			@Override
//			public int compare(Process p1, Process p2) {
//				return p1.arriveTime() - p2.arriveTime();
//			}
//		});
        this.arriveQueue = new PriorityQueue<>(Comparator.comparing(Process::arriveTime));
        this.readyList = new LinkedList<Process>();
        this.ioQueue = new PriorityQueue<>(Comparator.comparing(Process::remainingTime));
//        this.ioQueue = new PriorityQueue<Process>(new Comparator<Process>() {
//
//			@Override
//			public int compare(Process p1, Process p2) {
//				return p1.remainingTime() - p2.remainingTime();
//			}
//		});
        
    }

    public String runSimulation() {
        String result = "Algorithm FCFS\n";
        //initialize arriveQueue and add process with arriveTime = 0 into readyList
        for (int i = 0; i < processes.length; i++) {
        	if(processes[i].getState().equals(ProcessState.NA))
        	{
        		readyList.add(processes[i]);
				System.out.println(String.format("Process %s has arrived at %dms", processes[i].id(), 0));
        	}
        	else
        	{
        		arriveQueue.add(processes[i]);
        	}
		}
        
        //get the first running process if there is process with arriveTime = 0
        Process running = Process.EMPTY;
        if(!readyList.isEmpty())
        {
        	running = readyList.removeFirst();
        	running.running();
        }
        
        int time = 0;
        int endNum = 0;
        int processNum = processes.length;
        
        LinkedList<Process> temp = new LinkedList<Process>();
        while(running != Process.EMPTY || endNum != processNum)
        {
        	//System.out.println(String.format("Total: %d Ended: %d", processNum, endNum));
        	temp.clear();
        	
        	//Ticking processes in ioList
        	for(Process p : ioQueue)
        	{
        		if(p.tick())
        		{
        			temp.add(p);
        		}
        	}
        	
        	//Ticking processes in running
        	if(running != Process.EMPTY)
        	{
        		if(running.tick())
        		{
        			if(running.getState().equals(ProcessState.ENDED))
        			{
        				endNum++;
        				System.out.println(String.format("Process %s has terminated at %dms IOList: %d", running.id(), time, ioQueue.size()));
        				running = Process.EMPTY;
        			}
        			else
        			{
        				//status == Process.BLOCKED
        				ioQueue.add(running);
        				System.out.println(String.format("Process %s has ended the %d/%d burst and start IO of %dms at %dms", running.id(), running.burstIndex(), running.burstSize(), running.remainingTime(), time));
        				running = Process.EMPTY;
        			}
        		}
        	}
        	else
        	{
        		if(!readyList.isEmpty()) 
        		{
        			running = readyList.removeFirst();
        			running.running();
    				System.out.println(String.format("Process %s being processed at %dms", running.id(), time));
        		}
        	}
        	
        	//Ticking processes in arriveQueue
        	int arriveNum = 0;
        	for(Process p : arriveQueue)
        	{
        		if(p.tick())
        		{
        			arriveNum++;
        		}
        	}
        	
        	for(Process p : temp)
        	{
        		ioQueue.remove(p);
        		readyList.add(p);
				System.out.println(String.format("Process %s has ended IO at %dms", p.id(), time));
        	}
        	for(int i = 0; i < arriveNum; i++)
        	{
        		Process p = arriveQueue.remove();
    			readyList.add(p);
				System.out.println(String.format("Process %s has arrived at %dms", p.id(), time));
        	}
        	
        	time++;
        }
        System.out.println(String.format("Total: %d Ended: %d IOLIST: %d", processNum, endNum, ioQueue.size()));

        return result;
    }

}
