import java.util.*;
import java.lang.*;

public class Process {
	public static final int NA = 0;
	public static final int READY = 1;
	public static final int RUNNING = 2;
	public static final int BLOCKED = 3;
	public static final int ENDED = 4;
	
    private String id;
    private int turnAroundTime;
    private int cpuBurst;
    private int waitTime;
    private int arriveTime;
    private int burstNum;
    private int status;
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

    public int status()
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