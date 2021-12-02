package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.GPUService;

import java.util.Vector;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}
    private Model currentModel;
    private Cluster cluster;
    private Type type;

    public GPU(Type t, Cluster c) {
        this.type = t;
        this.currentModel = null;
        this.cluster = c;
    }

    public Type getType() { return this.type;}

    public Model getModel() { return this.currentModel;}

    /*
    IDEAS
    -------------------
    A vector to hold Data Batches which are currently being processed
     */

}
