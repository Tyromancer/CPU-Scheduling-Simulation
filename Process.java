import java.util.*;
import java.lang.*;

public class Process {
	public static final String READY = "READY";
	public static final String RUNNING = "RUNNING";
	public static final String BLOCKED = "BLOCKED";
	
    private String id;
    private int turnAroundTime;
    private int cpuBurst;
    private int waitTime;
    private int arriveTime;
    private int burstNum;
    private String status;
    private int remainTime;

    public Process(String id, Rand48 rand, Double lamb) {
        this.id = id;
        this.arriveTime = (int) (Math.log(rand.nextDouble())*-1/lamb);
        this.burstNum = (int) (rand.nextDouble() * 100) + 1;
        System.out.println("ArriveTime: " + arriveTime);
        System.out.println("BurstNum: " + burstNum);
        // TODO
    }
    
    public Process(Process p) {
    	
    }

    public String status()
    {
    	return this.status;
    }
    
    public int remainTime()
    {
    	return this.remainTime;
    }

}