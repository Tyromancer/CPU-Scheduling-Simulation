import java.util.*;


import java.lang.*;


public class Process {
	public static final Process EMPTY = null;
	
	public static Process[] generateProcesses()
	{
		Rand48.RNG.setSeed(Project.seed);
		Process[] processed = new Process[Project.numProcess];
		for (int i = 0; i < processed.length; i++) {
			processed[i] = new Process(Character.toString((char) (65 + i)));
		}
		
		return processed;
	}

	private ProcessState state;
    private String id;
    private int turnAroundTime;
    private int cpuBurst;
    private int waitTime;
    private int arriveTime;
    private int burstSize;
    private int remainingTime;
    private int burstIndex;
    private int[] burstTimes;
    private int[] ioTimes;

    public Process(String id) {
        this.id = id;
        this.arriveTime = (int) random();
        this.burstSize = (int) (Rand48.RNG.nextDouble() * 100) + 1;
        this.burstIndex = 0;
        this.burstTimes = new int[burstSize];
        this.ioTimes = new int[burstSize];
        this.remainingTime = arriveTime;
        // this.status = NA;
        this.state = ProcessState.NA;
        
        for (int i = 0; i < burstSize - 1; i++) {
			burstTimes[i] = (int) (random()) + 1;
			ioTimes[i] = (int) (random()) + 1;
		}
        burstTimes[burstSize - 1] = (int) (random()) + 1;
        
        if(arriveTime == 0)
        {
        	// this.status = READY;
        	this.state = ProcessState.READY;
        	remainingTime = burstTimes[burstIndex];
        }
        // TODO
    }

    public ProcessState getState() { return this.state; }

    public String id()
    {
    	return this.id;
    }

//    public int status()
//    {
//    	return this.status;
//    }
    
    public int remainingTime()
    {
    	return this.remainingTime;
    }

    public int currentBurstTime() { return this.burstTimes[this.burstIndex]; }
    
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
    
    public void running()
    {
    	// this.status = RUNNING;
        this.state = ProcessState.RUNNING;
    }
    
    /**
     * 
     * @return true if status is changed in this tick
     */
    public boolean tick()
    {
    	switch (this.state) {
        case NA:
        	// if the process has not arrived yet, decrease remaining time by one
			remainingTime--;
			// remaining time == 0 --> process has just arrived and is ready to use the cpu
			if(remainingTime == 0)
			{
			    this.state = ProcessState.READY;
				// status = READY;
				remainingTime = burstTimes[burstIndex];
				return true;
			}
			break;

		//TODO:
		case READY:
			throw new RuntimeException("FUCK YOU BUG");
			//return false;
			
		case RUNNING:             // the process is using the cpu
			remainingTime--;      // decrease remaining cpu burst time by one

			if(remainingTime == 0)
			{
				if(burstIndex == burstSize - 1)         // finished current cpu burst
				{
					// status = ENDED;
                    this.state = ProcessState.ENDED;    // current burst is the last cpu burst of this process --> process finished
					return true;
				}
				else
				{
					// more cpu bursts togo --> go to io burst
					remainingTime = ioTimes[burstIndex];
					this.state = ProcessState.BLOCKED;
					// status = BLOCKED;
					return true;
				}
			}
			break;
			
		case BLOCKED:
			// process is in io burst
			// decrease current io burst time by one
			remainingTime--;

			if(remainingTime == 0)   // process finishes io burst
			{
				burstIndex++;                               // goto next cpu burst
				remainingTime = burstTimes[burstIndex];     // set remaining time for cpu burst
				this.state = ProcessState.READY;            // change process state
				// status = READY;
				return true;
			}
			break;
			
		//TODO:
		case ENDED:
			System.out.println("FUCK YOU BUG, Process " + id + "ENDED");
			return false;
			
		default:
			System.out.println("FUCK YOU BUG");
			return false;
		}
    	return false;
    }
    
    public int estimateTime()
    {
    	return 0;
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