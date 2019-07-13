import java.util.*;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;

import java.lang.*;

public class Process {
	public static Process EMPTY = null;
	public static final int NA = 0;
	public static final int READY = 1;
	public static final int RUNNING = 2;
	public static final int BLOCKED = 3;
	public static final int ENDED = 4;
	
	public static Process[] generateProcesses()
	{
		Rand48.RNG.setSeed(Project.seed);
		Process[] processed = new Process[Project.numProcess];
		for (int i = 0; i < processed.length; i++) {
			processed[i] = new Process(Character.toString((char) (65 + i)));
		}
		
		return processed;
	}
	
    private String id;
    private int turnAroundTime;
    private int cpuBurst;
    private int waitTime;
    private int arriveTime;
    private int burstSize;
    private int status;
    private int remainTime;
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
        this.remainTime = arriveTime;
        this.status = NA;
        
        for (int i = 0; i < burstSize - 1; i++) {
			burstTimes[i] = (int) (random()) + 1;
			ioTimes[i] = (int) (random()) + 1;
		}
        burstTimes[burstSize - 1] = (int) (random()) + 1;
        
        if(arriveTime == 0)
        {
        	this.status = READY;
        	remainTime = burstTimes[burstIndex];
        }
        // TODO
    }
    
    public String id()
    {
    	return this.id;
    }

    public int status()
    {
    	return this.status;
    }
    
    public int remainTime()
    {
    	return this.remainTime;
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
    
    public void running()
    {
    	this.status = RUNNING;
    }
    
    /**
     * 
     * @return true if status is changed in this tick
     */
    public boolean tick()
    {
    	switch (status) {
		case NA:
			remainTime--;
			if(remainTime == 0)
			{
				status = READY;
				remainTime = burstTimes[burstIndex];
				return true;
			}
			break;

		//TODO:
		case READY:
			throw new RuntimeException("FUCK YOU BUG");
			//return false;
			
		case RUNNING:
			remainTime--;
			if(remainTime == 0)
			{
				if(burstIndex == burstSize - 1)
				{
					status = ENDED;
					return true;
				}
				else
				{
					remainTime = ioTimes[burstIndex];
					status = BLOCKED;
					return true;
				}
			}
			break;
			
		case BLOCKED:
			remainTime--;
			if(remainTime == 0)
			{
				burstIndex++;
				remainTime = burstTimes[burstIndex];
				status = READY;
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