package bgu.spl.mics.application.objects;

import java.util.Vector;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    // FIELDS
    private final String name;
    private final int date;
    private final Vector<Model> publications;

    /**
     * Class to hold all the given in formation about a conference: It's name, date, and publications
     *
     * @param name Name of current Conference
     * @param date Date of current Conference
     */
    public ConfrenceInformation(String name, int date) {
        this.name = name;
        this.date = date;
        this.publications = new Vector<>();
    }

    public void addModel(Model m) {
        if (m.getResult() == Model.results.Good) {
            this.publications.add(m);
        }
    }

    public String toString() {
        String stringModelsName="";
        for(Model model : publications){
            stringModelsName += " " + model.toString();
        }
        return "Conference name: " + this.name + " Date: " + this.date + "\n" + "Published Models:\n" + stringModelsName;
    }

    public int getDate() {
        return date;
    }

    public  synchronized int papersRead(Student student) {
        int ret = 0;
        for (Model model : this.publications) {
            if (model.getStudent() != student) ret++;
        }
        return ret;
    }
    public Vector<Model> getPublications(){
        return this.publications;
    }

}