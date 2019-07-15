import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class RR {
	private Process[] processes;
    private PriorityQueue<Process> arriveQueue;
    private LinkedList<Process> readyList;
    private LinkedList<Process> ioList;
	
	public RR() 
	{
		this.processes = Process.generateProcesses(false);
        this.arriveQueue = new PriorityQueue<Process>(Comparator.comparing(Process::arriveTime));
        this.readyList = new LinkedList<Process>();
        this.ioList = new LinkedList<Process>();
	}
	
	public String runSimulation()
	{
		String result = "Algorithm RR\n";
		System.out.println("time 0ms: Simulator started for RR [Q <empty>]");
		
		//initialize arriveQueue and add process with arriveTime = 0 into readyList
        for (int i = 0; i < processes.length; i++) 
        {
        	
        	
        	if(processes[i].remainingTime() == 0)
        	{
        		processes[i].setState(ProcessState.READY);
        		if(Project.rrAdd.equals("END"))
        			readyList.add(processes[i]);
        		else
        			readyList.addFirst(processes[i]);
				System.out.println(String.format("time %dms: Process %s arrived; added to ready queue %s", 0, processes[i].id(), queueInfo()));
        	}
        	else
        	{
        		arriveQueue.add(processes[i]);
        	}
		}
        
        Process running = Process.EMPTY;
        int time = 0;
        int slice = Project.timeSlice;
        int endNum = 0;
        int processNum = processes.length;
        int switchNum = 0;
        int preemptNum = 0;
        LinkedList<Process> temp = new LinkedList<Process>();
        Process tmp = null;
        
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
        	
        	//Ticking process in running
        	if(running == Process.EMPTY)
        	{
        		if(!readyList.isEmpty())
        		{
        			running = readyList.removeFirst();
        			running.setState(ProcessState.SWITCHIN);
        			slice = Project.timeSlice;
        			if(running.tick())
        			{
        				switchNum++;
        				running.setState(ProcessState.RUNNING);
        				running.setRemainingTime();
        				System.out.println(String.format("time %dms: Process %s started using the CPU for %dms burst %s", time, running.id(), running.remainingTime(), queueInfo()));
					}
        		}
        	}
        	else
        	{
        		if(running.tick())
        		{
        			switch (running.state()) {
        			case SWITCHIN:
        				switchNum++;
						running.setState(ProcessState.RUNNING);
						running.setRemainingTime();
						if(running.isPreempted())
						{
							running.setPreempted(false);
							System.out.println(String.format("time %dms: Process %s started using the CPU with %dms burst remaining %s", time, running.id(), running.remainingTime(), queueInfo()));
						}
						else
						{
							System.out.println(String.format("time %dms: Process %s started using the CPU for %dms burst %s", time, running.id(), running.remainingTime(), queueInfo()));
						}
						break;
						
        			case SWITCHOUT:
        				switchNum++;
        				if(running.isPreempted())
        				{
        					tmp = running;
        					running.setState(ProcessState.READY);
        					if(Project.rrAdd.equals("END"))
                    			readyList.add(running);
                    		else
                    			readyList.addFirst(running);
        				}
        				else if(running.isLastBurst())
						{
							endNum++;
							running.setEndTime(time);
							running.resetBurstedTime();
							running.setState(ProcessState.ENDED);
	        			}
						else
						{
							running.setState(ProcessState.BLOCKED);
							running.resetBurstedTime();
	        				ioList.add(running);
						}
						running = Process.EMPTY;
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
							System.out.println(String.format("time %dms: Process %s completed a CPU burst; %d burst%s to go %s", time, running.id(), remainBurst, remainBurst==1? "":"s", queueInfo()));
							System.out.println(String.format("time %dms: Process %s switching out of CPU; will block on I/O until time %dms %s", time, running.id(), time + Project.timeSwitch / 2 + running.getIOTime(), queueInfo()));
	        			}
						break;

        			default:
						System.out.println(String.format("ERROR: Process %s", running.id()));
						break;
					}
        		}
        		else
        		{
        			if(running.state() == ProcessState.RUNNING)
        			{
        				slice--;
        				if(slice == 0)
        				{
        					if(readyList.isEmpty())
        					{
        						System.out.println(String.format("time %dms: Time slice expired; no preemption because ready queue is empty %s", time, queueInfo()));
        					}
        					else
        					{
        						System.out.println(String.format("time %dms: Time slice expired; process %s preempted with %dms to go %s", time, running.id(), running.remainingTime(), queueInfo()));
        						preemptNum++;
        						running.setPreempted(true);
        						running.setState(ProcessState.SWITCHOUT);
        					}
        					slice = Project.timeSlice;
        				}
        			}
        		}
        	}
        	
        	for(Process p : readyList)
        	{
        		if(p == tmp)
        		{
        			continue;
        		}
        		
        		p.addWaitingTime();
        	}
        	tmp = null;
        	
        	//Add from IO to READY
        	if(!temp.isEmpty())
        	{
        		temp.sort(Comparator.comparing(Process::id));
        		for(Process p : temp)
            	{
            		p.nextBurst();
            		ioList.remove(p);
            		if(Project.rrAdd.equals("END"))
            			readyList.add(p);
            		else
            			readyList.addFirst(p);
    				System.out.println(String.format("time %dms: Process %s completed I/O; added to ready queue %s", time, p.id(), queueInfo()));
            	}
        	}
        	
        	//Add from ARRIVE to READY
        	for(int i = 0; i < arriveNum; i++)
        	{
        		Process p = arriveQueue.remove();
        		if(Project.rrAdd.equals("END"))
        			readyList.add(p);
        		else
        			readyList.addFirst(p);
				System.out.println(String.format("time %dms: Process %s arrived; added to ready queue %s", time, p.id(), queueInfo()));
        	}
        	
        }
		
        System.out.println(String.format("time %dms: Simulator ended for RR %s", time, queueInfo()));
        int burstNum = 0;
        for(Process p : processes)
        {
        	burstNum += p.burstSize();
        }
        
        switchNum /= 2;
        
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
        		"-- total number of preemptions: %d\n",
        		avgBurstTime, avgWaitTime, avgTurnaroundTime, switchNum, preemptNum);

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
}
