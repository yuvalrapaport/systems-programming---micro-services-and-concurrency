package bgu.spl.mics.application.objects;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications = 0;
    private int papersRead = 0;
    private Model [] models;

    public Student(String name, String department, Degree status, Model[]models){
        this.name = name;
        this.department = department;
        this.status = status;
        this.models = models;
    }

    public String getName() {
        return name;
    }

    public Model[] getModels() {
        return models;
    }

    public Degree getStatus() {
        return status;
    }

    public String getDepartment() { return department; }

    public int getPapersRead() {
        return papersRead;
    }

    public int getPublications() {
        return publications;
    }

    public void incrementPublications() {
        this.publications ++;
    }

    public void incrementPapersRead() {
        this.papersRead ++;
    }

}
