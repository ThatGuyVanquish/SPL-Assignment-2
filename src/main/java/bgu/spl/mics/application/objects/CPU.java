package bgu.spl.mics.application.objects;

import java.util.Vector;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private int  NumOfTicksPassed;
    public CPU(int cores){
        this.cores = cores;
        NumOfTicksPassed = 0;
    }
    public void UptadeTick(){
        NumOfTicksPassed++;
        notifyAll();
    }



    public String toString() {
        return "" + this.cores;
    }

    /**
     *
     * @param batch batch to work on
     * @return the process is complete
     * @inv return true
     */
    public boolean compute(DataBatch batch){ // ineffincent, the cluster gets blocked alot like this need to change implemention
        NumOfTicksPassed = 0; // starting the counting of ticks only when is called
        Data.Type type = batch.getType();
        switch (type){
            case Images:
            {
                while(NumOfTicksPassed<=(32/this.cores *4)) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {}

                }
            }
            case Text:
            {
                while(NumOfTicksPassed<=(32/this.cores *2)) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {}

                }
            }
            case Tabular:
            {
                while(NumOfTicksPassed<=(32/this.cores *1)) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {}

                }
            }
        }

        return true;
    }

}
