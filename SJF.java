import java.util.ArrayList;
import java.util.List;

public class SJF {
    private List<Process> processes;
    private List<Process> readyQueue;

//    public SJF(Rand48 rng, int numProcess, int timeSwitch, double alpha, double lamb) {
//        this.rng = rng;
//        this.numProcess = numProcess;
//        this.timeSwitch = timeSwitch;
//        this.alpha = alpha;
//        this.processes = new ArrayList<>();
//        this.readyQueue = new ArrayList<>();
//    }
    public SJF() {
        this.processes = new ArrayList<>();
        this.readyQueue = new ArrayList<>();
    }

    public String runSimulation() {
        String result = "Algorithm SJF\n";
        Rand48.RNG.setSeed(Project.seed);
        for (int i = 0; i < Project.numProcess; i++) {
            // TODO: generate process

            //                                         id,                               rand ,      lamb
            this.processes.add(new Process(Character.toString((char) (65 + i)), Rand48.RNG, Project.lamb));
        }



        return result;
    }
}
