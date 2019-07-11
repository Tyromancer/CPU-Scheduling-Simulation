import java.util.*;
import java.lang.*;

public class Process {
	//public static final String 
	public static final String READY = "READY";
	public static final String RUNNING = "RUNNING";
	public static final String BLOCKED = "BLOCKED";
	public static final String ENDED = "ENDED";
	
    private String id;
    private int turnAroundTime;
    private int cpuBurst;
    private int waitTime;
    private int arriveTime;
    private int burstNum;
    private String status;
    private int remainTime;
    private int[] burstTimes;
    private int[] ioTimes;

    public Process(String id, Rand48 rand, Double lamb) {
        this.id = id;
        this.arriveTime = (int) random(rand, lamb);
        this.burstNum = (int) (rand.nextDouble() * 100) + 1;
        this.burstTimes = new int[burstNum];
        this.ioTimes = new int[burstNum];
        
        for (int i = 0; i < burstNum - 1; i++) {
			burstTimes[i] = (int) (random(rand, lamb)) + 1;
			ioTimes[i] = (int) (random(rand, lamb)) + 1;
		}
        burstTimes[burstNum - 1] = (int) (random(rand, lamb)) + 1;
        
        System.out.println("Process " + id + ": ArriveTime: " + arriveTime);
        System.out.println("BurstNum: " + burstNum);
        // TODO
    }

    public String status()
    {
    	return this.status;
    }
    
    public int remainTime()
    {
    	return this.remainTime;
    }
    
    public void run()
    {
    	
    }
    
    private double random(Rand48 rand, Double lamb)
    {
    	return Math.log(rand.nextDouble()) * -1 / lamb;
    }

}