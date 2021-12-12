package bgu.spl.mics.application.objects;

import java.util.Vector;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private int  tickCounter;
    private Vector<DataBatch> dbVector;
    private DataBatch currentDB;
    private static final Cluster CLUSTER = Cluster.getInstance();
    public CPU(int cores){
        this.cores = cores;
        this.tickCounter = 0;
        this.dbVector = new Vector<DataBatch>();
        this.currentDB = null;
    }

    /**
     * Method to add a single databatch to the CPU's "to do" DataBatch vector
     * @param batch
     * @post currentDB != null
     */
    public void addDataBatch(DataBatch batch){ // Why isn't this receiving a vector? Also shouldn't this be synchronized?
        dbVector.add(batch);
        if (currentDB==null){
            currentDB = dbVector.remove(0);
            notifyAll(); // Would the CPU be waiting as this is called?
        }
    }

    /**
     * Sends processed data (if available) to the cluster and loads the next dataBatch (if available)
     * to the CPU to process.
     * Resets the tick counter for the next processing task
     * @post tickCounter == 0
     */
    private void setNextBatch(){
        CLUSTER.addProcessedData(currentDB); // Packing processed data and sending it back to the cluster to be sent to the GPU
        if (!dbVector.isEmpty())
            currentDB = dbVector.remove(0);
        else
            currentDB = null;
        tickCounter = 0;
    }

    public long process() {
        if (currentDB == null) {
            try {
                wait();
            }
            catch (InterruptedException e) {
                tickCounter++;
            }
        }
        Data.Type type = currentDB.getType();
        switch (type){
            case Images:
            {
                if (tickCounter<=(32/this.cores *4)) {
                    setNextBatch();
                }
            }
            case Text:
            {
                if (tickCounter <= (32 / this.cores) * 2) {
                    setNextBatch();
                }
            }
            case Tabular:
            {
                if (tickCounter <= 32 / this.cores) {
                    setNextBatch();
                }
            }
        }
        return (long)tickCounter;
    }

    public String toString() {
        return "" + this.cores;
    }

}
