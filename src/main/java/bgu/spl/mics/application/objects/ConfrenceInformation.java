package bgu.spl.mics.application.objects;

import java.util.*;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    private LinkedList<Model> sucModels;

    public ConfrenceInformation(String name, int date){
        this.name = name;
        this.date = date;
    }

    public void setRemains(){
        this.sucModels = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public LinkedList<Model> getSucModels() {
        return sucModels;
    }

    public void setSucModels(Model sucModel){
     this.sucModels.add(sucModel);
    }

    public int getDate() {
        return date;
    }


}
