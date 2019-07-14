import java.util.*;


import java.lang.*;


public class Process {
	public static final Process EMPTY = null;
	
	public static Process[] generateProcesses(boolean showTau)
	{
		Rand48.RNG.setSeed(Project.seed);
		Process[] processed = new Process[Project.numProcess];
		for (int i = 0; i < processed.length; i++) 
		{
			processed[i] = new Process(Character.toString((char) (65 + i)));
			
			if(showTau) {
                System.out.print(String.format(" (tau %dms)", processed[i].estimateTime()));
            }
			System.out.println();
		}
		
		return processed;
	}

	private ProcessState state;
    private String id;
    private int arriveTime;
    private int burstSize;
    private int remainingTime;
    private int burstedTime;
    private int burstIndex;
    private boolean preempted;
    private int[] burstTimes;
    private int[] ioTimes;
    private int[] estimateBurstTimes;

    public Process(String id) {
        this.id = id;
        this.arriveTime = (int) random();
        this.burstSize = (int) (Rand48.RNG.nextDouble() * 100) + 1;
        this.burstIndex = 0;
        this.burstTimes = new int[burstSize];
        this.estimateBurstTimes = new int[burstSize];
        this.ioTimes = new int[burstSize];
        this.remainingTime = arriveTime;
        this.state = ProcessState.NA;
        this.preempted = false;
        this.burstedTime = 0;
        
        for (int i = 0; i < burstSize - 1; i++) {
			burstTimes[i] = (int) (random()) + 1;    // generate actual burst times

			if (i == 0) {
				estimateBurstTimes[i] = (int) Math.ceil(1.0 / Project.lamb);   //
			} else {
				estimateBurstTimes[i] = (int) Math.ceil((Project.alpha * burstTimes[i - 1]) + ((1 - Project.alpha) * estimateBurstTimes[i - 1]));
			}
			ioTimes[i] = (int) (random()) + 1;
		}
        burstTimes[burstSize - 1] = (int) (random()) + 1;
        if (burstSize > 1) {
            estimateBurstTimes[burstSize - 1] = (int) Math.ceil((Project.alpha * burstTimes[burstSize - 2]) + ((1 - Project.alpha) * estimateBurstTimes[burstSize - 2]));
        } else if (burstSize == 1) {
            estimateBurstTimes[0] = (int) Math.ceil(1.0 / Project.lamb);
        }

        if (burstSize != 1) {
            System.out.print(String.format("Process %s [NEW] (arrival time %d ms) %d CPU bursts", id, arriveTime, burstSize));
        } else {
            System.out.print(String.format("Process %s [NEW] (arrival time %d ms) 1 CPU burst", id, arriveTime));
        }
        
    }

    public ProcessState state() 
    {
    	return this.state; 
    }

    public String id()
    {
    	return this.id;
    }
    
    public int remainingTime()
    {
    	return this.remainingTime;
    }
    
    public int burstedTime()
    {
    	return this.burstedTime;
    }
    
    public void resetBurstedTime()
    {
    	this.burstedTime = 0;
    }

    public void setRemainingTime() {
    	this.remainingTime = burstTimes[burstIndex] - burstedTime;
	}

	public void changeRemainingTime() { this.remainingTime -= this.burstedTime; }
    
    public int arriveTime()
    {
    	return this.arriveTime;
    }
    
    public int burstIndex()
    {
    	return this.burstIndex;
    }
    
    public int burstSize()
    {
    	return this.burstSize;
    }
    
    public void setPreempted(boolean b)
    {
    	this.preempted = b;
    }
    
    public boolean isPreempted()
    {
    	return this.preempted;
    }
    
    public int getIOTime()
    {
    	return this.ioTimes[burstIndex];
    }
    
    public boolean isLastBurst()
    {
    	return this.burstIndex == burstSize-1;
    }
    
    public boolean isEnded()
    {
    	return this.burstIndex == burstSize-1 && remainingTime == 0 && this.burstedTime == burstTimes[burstIndex];
    }
    
    /**
     * also change the remainingTime
     * @param stat
     */
    public void setState(ProcessState stat)
    {
    	this.state = stat;
    	switch (state) {
		case READY:
			break;
			
		case SWITCHIN:
			this.remainingTime = Project.timeSwitch / 2;
			break;
			
		case RUNNING:
			this.remainingTime = burstTimes[burstIndex];
			break;
			
		case SWITCHOUT:
			this.remainingTime = Project.timeSwitch / 2;
			break;
			
		case BLOCKED:
			this.remainingTime = ioTimes[burstIndex];
			break;

		case ENDED:
			break;
			
		default:
    		System.out.println(String.format("Error: Process %s setState to unexpected", id));
			break;
		}
    }
    
    public void nextBurst()
    {
    	this.burstIndex++;
    }
    
    /**
     * 
     * @return true if remainingTime == 0
     */
    public boolean tick()
    {
    	if(remainingTime == 0)
    	{
    		System.out.println(String.format("Error: Process %s Ticking when remainingTime == 0", id));
    	}
    	remainingTime--;
    	
    	if(state == ProcessState.RUNNING)
    		burstedTime++;
    	
    	return remainingTime == 0;
    }

    public int estimatedRemainingTime() {
        return this.estimateBurstTimes[burstIndex] - this.burstedTime;
    }

    public int estimateTime()
	{
    	return estimateBurstTimes[burstIndex];
    }
    
    public int nextEstimateTime()
    {
    	return estimateBurstTimes[burstIndex+1];
    }
    
    private double random()
    {
    	while(true)
    	{
    		double num = Math.log(Rand48.RNG.nextDouble()) * -1 / Project.lamb;
    		if(num > Project.upperBound)
    			continue;
    		else
    			return num;
    	}
    }

}