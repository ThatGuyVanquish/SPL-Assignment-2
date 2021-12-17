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

    private final String name;
    private final String department;
    private final Degree status;
    private int publications;
    private int papersRead;
    private int modelIndex;
    private int conferenceNum;
    private Vector<ConfrenceInformation> confVector;
    private Vector<Model> modelVector;

    public Student(String name, String dpt, Degree deg)
    {
        this.name = name;
        this.department = dpt;
        this.status = deg;
        this.publications = 0;
        this.papersRead = 0;
        this. modelIndex = 0;
        this.conferenceNum = 0;
        this.confVector = null;
    }

    public void addModels(Vector<Model> modelVector) {
        this.modelVector = modelVector;
    }

    public void addConferences(Vector<ConfrenceInformation> confVector) { this.confVector = confVector;}

    public ConfrenceInformation getConference(int index) {return this.confVector.get(index);}

    public String toString() {
        String ret = "Name: " + this.name + " Dept: " + this.department + " Status: " + this.status + "\n" + "Models:\n";
        int i = 1;
        for (Model m : this.modelVector) {
            if (m.getStatus() != Model.status.PreTrained && m.getStatus() != Model.status.Training) {
                ret += i + ") " + m.toString() + "\n";
                i++;
            }
        }
        ret += "Papers read: " + this.papersRead+"\n";
        return ret;
    }

    public String getName() {
        return name;
    }

    public void addPublications(int publications){
        this.publications += publications;
    }
    public void addPaperRead(int papersRead){
        this.papersRead += papersRead;
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

    public Vector<Model> getModels(){
        return modelVector; }

    public  Model getNextModel(){
        Model currModel;
        if(modelVector.size()>modelIndex) {
            currModel= modelVector.get(modelIndex);
        }
        else
            currModel = null;
        modelIndex++;
        return currModel;

    }
    public void setConferenceNum(int confNum){
        this.conferenceNum = confNum;
    }

    public int getConferenceNum() {
        return conferenceNum;
    }
}

