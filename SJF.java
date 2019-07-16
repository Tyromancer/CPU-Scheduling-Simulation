import java.util.*;

public class SJF {
	private Process[] processes;
    private PriorityQueue<Process> arriveQueue;
    private PriorityQueue<Process> readyQueue;
    private LinkedList<Process> ioList;

    public SJF() {
    	this.processes = Process.generateProcesses(true);
        this.arriveQueue = new PriorityQueue<Process>(Comparator.comparing(Process::arriveTime));
        this.readyQueue = new PriorityQueue<Process>(new Comparator<Process>() {

			@Override
			public int compare(Process p1, Process p2) {
				int e1 = p1.estimateTime();
				int e2 = p2.estimateTime();
				if(e1 == e2)
					return p1.id().compareTo(p2.id());
				else
					return p1.estimateTime() - p2.estimateTime();
			}
		});
        this.ioList = new LinkedList<Process>();
    }

    public String runSimulation() {
        String result = "Algorithm SJF\n";
        System.out.println("time 0ms: Simulator started for SJF [Q <empty>]");

        //initialize arriveQueue and add process with arriveTime = 0 into readyList
        for (int i = 0; i < processes.length; i++) 
        {
        	
        	
        	if(processes[i].remainingTime() == 0)
        	{
        		processes[i].setState(ProcessState.READY);
        		readyQueue.add(processes[i]);
				System.out.println(String.format("time %dms: Process %s (tau %dms) arrived; added to ready queue %s", 0, processes[i].id(), processes[i].estimateTime(), queueInfo()));
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
        				print(String.format("time %dms: Process %s (tau %dms) started using the CPU for %dms burst %s", time, running.id(), running.estimateTime(), running.remainingTime(), queueInfo()), time);
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
							print(String.format("time %dms: Process %s (tau %dms) completed a CPU burst; %d burst%s to go %s", time, running.id(), running.estimateTime(), remainBurst, remainBurst==1? "":"s", queueInfo()), time);
							print(String.format("time %dms: Recalculated tau = %dms for process %s %s", time, running.nextEstimateTime(), running.id(), queueInfo()), time);
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

					default:
						System.out.println(String.format("ERROR: Process %s", running.id()));
						break;
					}
        		}
        	}
        	else
        	{
        		if(!readyQueue.isEmpty()) 
        		{
        			running = readyQueue.poll();
        			running.setState(ProcessState.SWITCHIN);
        			if(running.tick())
        			{
        				running.setState(ProcessState.RUNNING);
        				print(String.format("time %dms: Process %s (tau %dms) started using the CPU for %dms burst %s", time, running.id(), running.estimateTime(), running.remainingTime(), queueInfo()), time);
					}
    			}
        	}
        	
        	for(Process p : readyQueue)
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
            		readyQueue.add(p);
            		print(String.format("time %dms: Process %s (tau %dms) completed I/O; added to ready queue %s", time, p.id(), p.estimateTime(), queueInfo()), time);
            	}
        	}
        	for(int i = 0; i < arriveNum; i++)
        	{
        		Process p = arriveQueue.remove();
        		readyQueue.add(p);
        		print(String.format("time %dms: Process %s (tau %dms) arrived; added to ready queue %s", time, p.id(), p.estimateTime(), queueInfo()), time);
        	}
        }
        
        System.out.println(String.format("time %dms: Simulator ended for SJF %s", time, queueInfo()));
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
        		halfOut(avgBurstTime), halfOut(avgWaitTime), halfOut(avgTurnaroundTime), switchNum);
        
        return result;
    }
    
    private double halfOut(double num)
    {
    	num *= 10000;
    	if(num % 10 == 5)
    		num--;
    	num /= 10000;
    	return num;
    }
    
    private String queueInfo() {
        if(this.readyQueue.isEmpty())
        {
            return "[Q <empty>]";
        }

        String str = "[Q";

        List<Process> tempList = new LinkedList<>();
        while (readyQueue.peek() != null) {
        	Process p = readyQueue.poll();
        	str += " " + p.id();
            tempList.add(p);
        }

        str += "]";
        readyQueue.addAll(tempList);
        
        return str;
    }
    
    private void print(String str, int time)
    {
    	if(time < 1000)
    		System.out.println(str);
    }

}
