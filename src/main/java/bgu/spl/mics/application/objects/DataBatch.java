package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private Data data;
    private int start_index;
    private int gpuId;

    public DataBatch(Data data, int start){
        this.data = data;
        this.start_index = start;
        this.gpuId = -1;
    }

    public int getGpuId() {
        return gpuId;
    }

    public void setGpuId(int gpuId) {
        this.gpuId = gpuId;
    }

    public Data getData(){
        return data;
    }
}
