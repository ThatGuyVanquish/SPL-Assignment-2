package bgu.spl.mics.application.objects;

import java.util.Vector;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    // FIELDS
    private String name;
    private int date;
    private Vector<Model> publications;

    /**
     * Class to hold all the given in formation about a conference: It's name, date, and publications
     *
     * @param name Name of current Conference
     * @param date Date of current Conference
     */
    public ConfrenceInformation(String name, int date) {
        this.name = name;
        this.date = date;
    }

    public void addModel(Model m) {
        if (m.getResult() == Model.results.Good) // Only allow publishing if model was good. Might be an "if tested" case though
            this.publications.add(m);
    }

    public String toString() {
        String ret = "Conference name: " + this.name + " Date: " + this.date + "\n" + "Published Models:\n";
        // Add published models to ToString
        return ret;
    }

    public int getDate() {
        return date;
    }
}
