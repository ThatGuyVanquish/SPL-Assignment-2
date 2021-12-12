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
    public synchronized void addDataBatch(DataBatch batch){
        dbVector.add(batch);
        if (currentDB==null){
            currentDB = dbVector.remove(0);
        }
    }

    /**
     * Sends processed data (if available) to the cluster and loads the next dataBatch (if available)
     * to the CPU to process.
     * Resets the tick counter for the next processing task
     * @post tickCounter == 0
     */
    private void setNextBatch(){
        CLUSTER.sendProcessedData(currentDB); // Packing processed data and sending it back to the cluster to be sent to the GPU
        if (!dbVector.isEmpty())
            currentDB = dbVector.remove(0);
        else
            currentDB = null;
        tickCounter = 0;
    }

    public void process() {
        if (currentDB == null) { return; }
        tickCounter++;
        Data.Type type = currentDB.getType();
        switch (type){
            case Images: {
                if (tickCounter >= (32/this.cores * 4)) {
                    setNextBatch();
                }
                break;
            }
            case Text: {
                if (tickCounter >= (32 / this.cores) * 2) {
                    setNextBatch();
                }
                break;
            }
            case Tabular: {
                if (tickCounter >= 32 / this.cores) {
                    setNextBatch();
                }
                break;
            }
        }
    }

    public String toString() {
        return "" + this.cores;
    }

}
