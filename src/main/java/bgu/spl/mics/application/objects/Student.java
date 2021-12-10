package bgu.spl.mics.application.objects;

import java.util.Vector;

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
    private int publications;
    private int papersRead;
    private Vector<Model> modelVector;

    public Student(String name, String dpt, Degree deg)
    {
        this.name = name;
        this.department = dpt;
        this.status = deg;
        this.publications = 0;
        this.papersRead = 0;
    }

    public void addModels(Vector<Model> modelVector) {
        this.modelVector = modelVector;
    }

    public String toString() {
        String ret = "Name: " + this.name + " Dept: " + this.department + " Status: " + this.status + "\n" + "Models:\n";
        int i = 1;
        for (Model m : this.modelVector) {
            ret += i + ") " + m.toString() + "\n";
            i++;
        }
        return ret;
    }
    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public Degree getStatus() {
        return status;
    }

    public int getPublications() {
        return publications;
    }

    public int getPapersRead() {
        return papersRead;
    }
}
