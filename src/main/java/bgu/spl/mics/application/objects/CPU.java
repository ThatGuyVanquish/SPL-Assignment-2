package bgu.spl.mics.application.objects;

import java.util.Vector;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private Data data;
    private  DataBatch dataBatch;
    public CPU(int cores,Data data,DataBatch dataBatch){
        this.data = data;
        this.cores= cores;
        this.dataBatch = dataBatch;
    }
    public void Train(){

    }

    private void SentToCluster(){

    }



}
