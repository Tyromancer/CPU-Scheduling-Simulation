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
    private int arriveTime;
    private int burstSize;
    private int remainingTime;
    private int burstIndex;
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
        
        for (int i = 0; i < burstSize - 1; i++) {
			burstTimes[i] = (int) (random()) + 1;    // generate actual burst times

			if (i == 0) {
				estimateBurstTimes[i] = (int) Math.ceil(1.0 / Project.lamb);   //
			} else {
				estimateBurstTimes[i] = (int) Math.ceil((Project.alpha * burstTimes[i - 1]) / (Project.alpha * estimateBurstTimes[i - 1]));
			}
			ioTimes[i] = (int) (random()) + 1;
		}
        burstTimes[burstSize - 1] = (int) (random()) + 1;
        
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
    
    public boolean isEnded()
    {
    	return this.burstIndex == burstSize-1 && remainingTime == 0;
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
    	return remainingTime == 0;
    	
//    	switch (this.state) {
//        case NA:
//        	// if the process has not arrived yet, decrease remaining time by one
//			remainingTime--;
//			// remaining time == 0 --> process has just arrived and is ready to use the cpu
//			if(remainingTime == 0)
//			{
//			    this.state = ProcessState.READY;
//				// status = READY;
//				remainingTime = burstTimes[burstIndex];
//				return true;
//			}
//			break;
//
//		//TODO:
//		case READY:
//			throw new RuntimeException("FUCK YOU BUG");
//			//return false;
//			
//		case RUNNING:             // the process is using the cpu
//			remainingTime--;      // decrease remaining cpu burst time by one
//
//			if(remainingTime == 0)
//			{
//				if(burstIndex == burstSize - 1)         // finished current cpu burst
//				{
//					// status = ENDED;
//                    this.state = ProcessState.ENDED;    // current burst is the last cpu burst of this process --> process finished
//					return true;
//				}
//				else
//				{
//					// more cpu bursts togo --> go to io burst
//					remainingTime = ioTimes[burstIndex];
//					this.state = ProcessState.BLOCKED;
//					// status = BLOCKED;
//					return true;
//				}
//			}
//			break;
//			
//		case BLOCKED:
//			// process is in io burst
//			// decrease current io burst time by one
//			remainingTime--;
//
//			if(remainingTime == 0)   // process finishes io burst
//			{
//				burstIndex++;                               // goto next cpu burst
//				remainingTime = burstTimes[burstIndex];     // set remaining time for cpu burst
//				this.state = ProcessState.READY;            // change process state
//				// status = READY;
//				return true;
//			}
//			break;
//			
//		//TODO:
//		case ENDED:
//			System.out.println("FUCK YOU BUG, Process " + id + "ENDED");
//			return false;
//			
//		default:
//			System.out.println("FUCK YOU BUG");
//			return false;
//		}
//    	return false;
    }

    public int estimateTime()
	{
    	return estimateBurstTimes[burstIndex];
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