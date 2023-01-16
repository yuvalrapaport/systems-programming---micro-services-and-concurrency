package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    public enum Type {
        Images, Text, Tabular
    }

    public Data(Type type,int size){
        this.type = type;
        processed = 0;
        this.size = size;
    }

    private Type type;
    private int processed;
    private int size;

    public int getSize() {
        return size;
    }

    public int getProcessed(){
        return processed;
    }

    public Type getType() {
        return type;
    }
}
