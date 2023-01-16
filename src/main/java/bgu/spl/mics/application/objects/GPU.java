package bgu.spl.mics.application.objects;

import bgu.spl.mics.MicroService;
import java.util.*;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU{

    /**
     * Enum representing the type of the GPU.
     */
    public enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Model model;
    private Cluster cluster;
    protected Vector<DataBatch> processedData;
    protected Vector<DataBatch> unprocessedData;
    private int batchesToTrain;
    private int capacity;
    private int id;

    public GPU(Type type, int id){
        this.type = type;
        this.id = id;
        this. model = null;
        this.cluster = Cluster.getInstance();
        this.unprocessedData = new Vector<>();
        this.processedData = new Vector<>();
        this.batchesToTrain =0;
        if (type == Type.RTX3090)
            this.capacity = 32;
        if (type == Type.RTX2080)
            this.capacity = 16;
        if (type == Type.GTX1080)
            this.capacity = 8;
    }

    public Model getModel(){ //added
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * @pre: unprocessedData doesn't contain any data
     * @post: the data of the current model is divided and updated in unprocessedData
     */
    public void splitData(){
        Data d = model.getData();
        int countBatches =  d.getSize() / 1000;
        for (int i = 1; i<=d.getSize(); i=i+1000 ){
            DataBatch DB = new DataBatch(d,i);
            unprocessedData.add(DB);
            DB.setGpuId(cluster.getGpus().indexOf(this));
        }
        batchesToTrain = countBatches;
    }

    /**
     * @pre: there's unprocessed data to be processed
     * @post: the data that was sent is not waiting as unprocessed
     * @return
     */
    public void sendData(){
        cluster.receiveDataFromGpu(unprocessedData);
        unprocessedData = new Vector<>();
    }

    /**
     * @pre: model's status = PreTrained
     * @post: model's status = Trained, batchesToTrain = 0
     * @return boolean indicating whether model finished training
     */
    public void trainModel(){
       model.setStatus(Model.Status.Trained);
       cluster.addTrainedModel(model.getName());
    }

    public void decrementBatchesToTrain(){
        processedData.remove(0);
        batchesToTrain--;
    }

    public int getUnprocessedSize(){
        return unprocessedData.size();
    }

    public int getProcessedSize(){
        return processedData.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public int getBatchesToTrain() {
        return batchesToTrain;
    }

    public int getNumberOfTicks(){
        if (this.type== Type.GTX1080)
            return 4;
        if (this.type == Type.RTX2080)
            return 2;
        if(this.type == Type.RTX3090)
            return 1;

        return 0;
    }

    public void addProcessedData(){ //gpu service uses to get processed data from cluster
        cluster.addToGpu(id);
    }

    public void addToProcessedData(DataBatch DB){ //cluster uses to add to our field
        processedData.add(DB);
    }

    public void incrementGpuTime(){
        cluster.incrementGpuTime();
    }

    public int getId() {
        return id;
    }
}
