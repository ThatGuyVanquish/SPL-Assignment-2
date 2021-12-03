package bgu.spl.mics.application.objects;

import java.util.Vector;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;

    public CPU(int cores){
        this.cores = cores;
    }

    /**
     *
     * @param batch batch to work on
     * @return the process is complete
     */
    public boolean compute(DataBatch batch){
        Data.Type type = batch.getType();
        switch (type){
            case Images:
            {
                for (int i = 0; i < 4 * (32/this.cores); i++)
                    try {
                        this.wait();
                    }
                    catch (InterruptedException e) {};
            }
            case Text:
            {
                for (int i = 0; i < 2 * (32/this.cores); i++)
                    try {
                        this.wait();
                    }
                    catch (InterruptedException e) {};
            }
            case Tabular:
            {
                for (int i = 0; i < 32/this.cores; i++)
                    try {
                        this.wait();
                    }
                    catch (InterruptedException e) {};
            }
        }
        return true;
    }

    private void SentToCluster(){

    }



}
