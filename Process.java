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
    private String status;
    private int remainTime;

    public Process(String id, Rand48 rand) {
        this.id = id;
        this.arriveTime = (int) rand.nextDouble();
        
        // TODO
    }
    
    public Process(Process p)
    {
    	
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