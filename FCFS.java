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
    private LinkedList<Process> ioList;
    
    public FCFS() 
    {
        this.processes = Process.generateProcesses(false);
        this.arriveQueue = new PriorityQueue<Process>(Comparator.comparing(Process::arriveTime));
        this.readyList = new LinkedList<Process>();
        this.ioList = new LinkedList<Process>();
    }

    public String runSimulation() 
    {
        String result = "Algorithm FCFS\n";
        System.out.println("time 0ms: Simulator started for FCFS [Q <empty>]");
        
        //initialize arriveQueue and add process with arriveTime = 0 into readyList
        for (int i = 0; i < processes.length; i++) 
        {
        	
        	
        	if(processes[i].remainingTime() == 0)
        	{
        		processes[i].setState(ProcessState.READY);
        		readyList.add(processes[i]);
        		print(String.format("time %dms: Process %s arrived; added to ready queue %s", 0, processes[i].id(), queueInfo()), 0);
        	}
        	else
        	{
        		arriveQueue.add(processes[i]);
        	}
		}
        
        Process running = Process.EMPTY;
        int time = 0;
        int endNum = 0;
        int processNum = processes.length;
        LinkedList<Process> temp = new LinkedList<Process>();
        
        while(running != Process.EMPTY || endNum != processNum)
        {
        	time++;
        	temp.clear();
        	
        	//Ticking processes in ioList
        	for(Process p : ioList)
        	{
        		if(p.tick())
        		{
        			temp.add(p);
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
        	
        	//Ticking processes in running
        	if(running != Process.EMPTY)
        	{
        		if(running.tick())
        		{
        			switch (running.state()) {
					case SWITCHIN:
						running.setState(ProcessState.RUNNING);
						print(String.format("time %dms: Process %s started using the CPU for %dms burst %s", time, running.id(), running.remainingTime(), queueInfo()), time);
						break;
						
					case RUNNING:
						running.setState(ProcessState.SWITCHOUT);
						if(running.isLastBurst())
						{
							System.out.println(String.format("time %dms: Process %s terminated %s", time, running.id(), queueInfo()));
						}
						else
						{
							int remainBurst = running.burstSize() - running.burstIndex() - 1;
							print(String.format("time %dms: Process %s completed a CPU burst; %d burst%s to go %s", time, running.id(), remainBurst, remainBurst==1? "":"s", queueInfo()), time);
							print(String.format("time %dms: Process %s switching out of CPU; will block on I/O until time %dms %s", time, running.id(), time + Project.timeSwitch / 2 + running.getIOTime(), queueInfo()), time);
	        			}
						break;
						
					case SWITCHOUT:
						if(running.isLastBurst())
						{
							endNum++;
							running.setEndTime(time);
							running.setState(ProcessState.ENDED);
	        			}
						else
						{
							running.setState(ProcessState.BLOCKED);
	        				ioList.add(running);
						}
						
						running = Process.EMPTY;
						break;

					}
        		}
        	}
        	else
        	{
        		if(!readyList.isEmpty()) 
        		{
        			running = readyList.removeFirst();
        			running.setState(ProcessState.SWITCHIN);
        			if(running.tick())
        			{
        				running.setState(ProcessState.RUNNING);
        				print(String.format("time %dms: Process %s started using the CPU for %dms burst %s", time, running.id(), running.remainingTime(), queueInfo()), time);
					}
    			}
        	}
        	
        	for(Process p : readyList)
        	{
        		p.addWaitingTime();
        	}
        	
        	//Add from IO to READY
        	if(!temp.isEmpty())
        	{
        		temp.sort(Comparator.comparing(Process::id));
        		for(Process p : temp)
            	{
            		p.nextBurst();
            		ioList.remove(p);
            		readyList.add(p);
            		print(String.format("time %dms: Process %s completed I/O; added to ready queue %s", time, p.id(), queueInfo()), time);
            	}
        	}
        	for(int i = 0; i < arriveNum; i++)
        	{
        		Process p = arriveQueue.remove();
    			readyList.add(p);
				print(String.format("time %dms: Process %s arrived; added to ready queue %s", time, p.id(), queueInfo()), time);
        	}
        }
        
        System.out.println(String.format("time %dms: Simulator ended for FCFS %s", time, queueInfo()));
        
        int burstNum = 0;
        for(Process p : processes)
        {
        	burstNum += p.burstSize();
        }
        int switchNum = burstNum;
        
        double avgBurstTime = 0;
        for(Process p : processes)
        {
        	avgBurstTime += p.totalBurstTime();
        }
        avgBurstTime = avgBurstTime / burstNum;
        
        double avgWaitTime = 0;
        for(Process p : processes)
        {
        	avgWaitTime += p.getWaitingTime();
        }
        avgWaitTime /= burstNum;
        
        double avgTurnaroundTime = 0;
        for(Process p : processes)
        {
        	avgTurnaroundTime += p.endTime() - p.arriveTime() - p.getTotalIOTime();
        }
        avgTurnaroundTime /= burstNum;
        
        result += String.format("-- average CPU burst time: %.3f ms\n" + 
        		"-- average wait time: %.3f ms\n" + 
        		"-- average turnaround time: %.3f ms\n" + 
        		"-- total number of context switches: %d\n" + 
        		"-- total number of preemptions: 0\n",
        		avgBurstTime, avgWaitTime, avgTurnaroundTime, switchNum);
        return result;
    }
    
    private String queueInfo()
    {
    	if(readyList.isEmpty())
    	{
    		return "[Q <empty>]";
    	}
    	
    	String str = "[Q";
    	for(Process p : readyList)
    	{
    		str += String.format(" %s", p.id());
    	}
    	str += "]";
    	return str;
    }
    
    private void print(String str, int time)
    {
    	if(time < 1000)
    		System.out.println(str);
    }

}
