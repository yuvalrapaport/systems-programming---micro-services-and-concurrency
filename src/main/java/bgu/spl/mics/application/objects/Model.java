package bgu.spl.mics.application.objects;

import java.util.PrimitiveIterator;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {

    public enum Status {
        PreTrained, Training, Trained, Tested
    }
    public enum Results {
        None, Good, Bad
    }
    private String name;
    private Data data;
    private Student student;
    private Status status;
    private Results results;
    private Data.Type type;
    private int size;

    public Model(String name, Student student, Data.Type dataType, int dataSize){
        this.name= name;
        this.student = student;
        this.status = Status.PreTrained;
        this.results = Results.None;
        this.type = dataType;
        this.size = dataSize;
        this.data = new Data(this.type,this.size);

    }
    public Model(Data data){this.data = data;}

    public void setRemains(Data data, Student student, Model.Results results, Model.Status status){
        this.data = data;
        this.student = student;
        this.status = Status.PreTrained;
        this.results = Results.None;
    }

    public Data getData() {
        return data;
    }

    public Status getStatus(){
        return status;
    }

    public Student getStudent() { return student; }

    public String getName() { return name; }

    public void setStatus(Status status) { this.status = status; }

    public Results getResults() {
        return results;
    }

    public void setResults(Results results) { this.results = results; }

    public int getSize() {
        return size;
    }

    public Data.Type getType() {
        return type;
    }

    public String toString(){
        return name;
    }
}
