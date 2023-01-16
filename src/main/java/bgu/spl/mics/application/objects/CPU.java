package bgu.spl.mics.application.objects;
import java.util.*;
/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private Collection<DataBatch> dataBatchCollection;
    private DataBatch data;
    private Cluster cluster;

    public CPU(int cores){
        this.cores = cores;
        this.data = null;
        this.cluster = Cluster.getInstance();
    }

    /**
     * @pre: None
     * @post: dataBatch is in data field and passed to the CPU service for processing
     * @return: data batch to be processed
     */
    public DataBatch receiveData() { //receives data from cluster and passes to CPU service
        data = cluster.getUnprocessedData();
        return data;
    }

    public void PassData(){
       cluster.receiveProcessedData(data);
    } //passes processed data to cluster

    /**
     * @param
     * @pre: cpuTime filed in cluster = x
     * @post: cpuTime filed in cluster = x+1
     */
    public void incrementCpuTime(){
        cluster.incrementCpuTime();
    }

    /**
     * @param
     * @pre: BatchesProcessed field in cluster = x
     * @post: BatchesProcessed field in cluster = x+1
     */
    public void incrementBatchesProcessed(){
        cluster.incrementBatchesProcessed();
    }

    /**
     * @return: returns the number of ticks it takes to process current data batch
     */
    public int getNumberOfTicks(){
        if (data.getData().getType() == Data.Type.Images){
            return (32/cores)*4;
        }
        if (data.getData().getType() == Data.Type.Text){
            return (32/cores)*2;
        }
        if (data.getData().getType() == Data.Type.Tabular){
            return (32/cores)*1;
        }
        return 0;
    }

    public DataBatch getData() {
        return data;
    }
}
