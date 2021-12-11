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
    private Vector<DataBatch> databatch;
    private DataBatch CurrentDataBatch;
    private Cluster cluster;
    public CPU(int cores){
        this.cores = cores;
        NumOfTicksPassed = 0;
        databatch = new Vector<DataBatch>();
        cluster = Cluster.getInstance();
        CurrentDataBatch = null;
    }
    public void addDataBatch(DataBatch batch){ //will be called from the cluster
        databatch.add(batch);
        if (CurrentDataBatch==null){
            CurrentDataBatch = databatch.remove(0);
            notifyAll();
        }

    }
    private void SetNextBatch(){
        cluster.AddProcessedData(CurrentDataBatch); //send processed data to cluster
        if (!databatch.isEmpty())
            CurrentDataBatch = databatch.remove(0);
        else
            CurrentDataBatch = null;
        NumOfTicksPassed=0;
    }

    public void UptadeTick(){ // called every tick
        if (CurrentDataBatch==null)
            try{
                wait();
            }catch (InterruptedException e){}

        NumOfTicksPassed++;
        Data.Type type = CurrentDataBatch.getType();
        switch (type){
            case Images:
            {
                if (NumOfTicksPassed<=(32/this.cores *4)) {
                    SetNextBatch();
                }
            }
            case Text:
            {
                if(NumOfTicksPassed<=(32/this.cores *2)) {
                    SetNextBatch();

                }
            }
            case Tabular:
            {
                if(NumOfTicksPassed<=(32/this.cores *1)) {
                    SetNextBatch();

                }
            }
        }
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
//    public boolean compute(DataBatch batch){ // ineffincent, the cluster gets blocked alot like this need to change implemention
//        NumOfTicksPassed = 0; // starting the counting of ticks only when is called
//        Data.Type type = batch.getType();
//        switch (type){
//            case Images:
//            {
//                while(NumOfTicksPassed<=(32/this.cores *4)) {
//                    try {
//                        this.wait();
//                    } catch (InterruptedException e) {}
//
//                }
//            }
//            case Text:
//            {
//                while(NumOfTicksPassed<=(32/this.cores *2)) {
//                    try {
//                        this.wait();
//                    } catch (InterruptedException e) {}
//
//                }
//            }
//            case Tabular:
//            {
//                while(NumOfTicksPassed<=(32/this.cores *1)) {
//                    try {
//                        this.wait();
//                    } catch (InterruptedException e) {}
//
//                }
//            }
//        }
//
//        return true;
//    }

}
