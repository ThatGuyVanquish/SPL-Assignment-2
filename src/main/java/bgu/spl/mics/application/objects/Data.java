package bgu.spl.mics.application.objects;

import java.util.Vector;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    public enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;
    private int size;

    public Data(Type type, int size) {
        this.type = type;
        this.size = size;
        this.processed = 0;
    }

    /**
    Creates a new batch
     */
    public synchronized DataBatch batch(GPU gpu) {
        if (!isDone())
            return new DataBatch(size-processed, this, gpu);
        return null;
    }

    public synchronized Vector<DataBatch> batch(int size, GPU gpu) { // Splits into a vector based on size
        if (!isDone()){
            Vector<DataBatch> ret = new Vector<>();
            int index = this.size-this.processed;
            while (index < this.size) {
                ret.add(new DataBatch(index, this, gpu));
                index += 1000;
            }
            return ret;
        }
        return null;
    }

    public boolean isDone() {
        if (this.size <= this.processed){
            return true;
        }

        return false;
    }

    public Type getType() {
        return this.type;
    }

    public synchronized boolean processData() {
        this.processed += 1000;
        return isDone();
    }

    public String toString() {
        return "Type: " + this.type.toString() + " Size: " + this.size;
    }

    public int getProcessed() {
        return processed;
    }

    public int getSize() {
        return size;
    }
}
