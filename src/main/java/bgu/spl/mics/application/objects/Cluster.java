package bgu.spl.mics.application.objects;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster  {
	private static Cluster cluster;
	private Vector<GPU> gpus;
	private Vector<CPU> cpus;
	private LinkedList<String> modelsTrained;
	private Queue<Vector<DataBatch>> unprocessedData;
	private Vector<Queue<DataBatch>> processedData;
	//for statistics
	private AtomicInteger batchesProcessed;
	private AtomicInteger cpuTime;
	private AtomicInteger gpuTime;


	// constructor
	public Cluster(){
		this.gpus = new Vector<>();
		this.cpus = new Vector<>();
		this.unprocessedData = new LinkedList<>();
		this.modelsTrained = new LinkedList<>();
		this.processedData = new Vector<>();
		batchesProcessed = new AtomicInteger(0);
		gpuTime = new AtomicInteger(0);
		cpuTime = new AtomicInteger(0);
	}

	/**
     * Retrieves the single instance of this class.
     */
	public synchronized static Cluster getInstance() {
		if (cluster == null)
			cluster = new Cluster();
		return cluster;
	}

	public synchronized void receiveDataFromGpu(Vector<DataBatch> gpuData){
			unprocessedData.add(gpuData);
	}

	public synchronized void receiveProcessedData(DataBatch data){ //receives from CPU
		int id = data.getGpuId();
		processedData.elementAt(id).add(data);
	}

	public synchronized void addToGpu(int id){
		Queue<DataBatch> q = processedData.elementAt(id);
		while(!q.isEmpty() && gpus.elementAt(id).getProcessedSize() < gpus.elementAt(id).getCapacity()){
			gpus.elementAt(id).addToProcessedData(q.poll());
		}
	}

	public synchronized DataBatch getUnprocessedData(){ //CPU uses to take unprocessed data
		Vector<DataBatch> dataVector = unprocessedData.poll();
		if (dataVector == null)
			return null;
		DataBatch DB = dataVector.remove(0);
		if (!dataVector.isEmpty())
			unprocessedData.add(dataVector);
		return DB;
	}

	public void addGPU (GPU gpu){
		gpus.add(gpu);
		processedData.add(new LinkedList<>());
	} //used in main

	public void addCPU(CPU cpu){
		cpus.add(cpu);
	} //used in main

	public synchronized Vector<GPU> getGpus() {
		return gpus;
	} //used in split data to set gpu id

	public synchronized void incrementGpuTime (){ //used in GPU service
		gpuTime.incrementAndGet();
	}

	public synchronized void incrementCpuTime (){ //used in CPU service
		cpuTime.incrementAndGet();
	}

	public synchronized void incrementBatchesProcessed (){ //used in CPU service
		batchesProcessed.incrementAndGet();
	}

	public AtomicInteger getBatchesProcessed() { //used for output file
		return batchesProcessed;
	}

	public AtomicInteger getCpuTime() { //used for output file
		return cpuTime;
	}

	public AtomicInteger getGpuTime() { //used for output file
		return gpuTime;
	}

	public LinkedList<String> getModelsTrained(){
		return modelsTrained;
	}

	public synchronized void addTrainedModel(String modelName) {
		this.modelsTrained.add(modelName);
	}


}
