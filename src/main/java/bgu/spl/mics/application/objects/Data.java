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

    private final Type type;
    private int processed;
    private int sentToCPU;
    private final int size;

    public Data(Type type, int size) {
        this.type = type;
        this.size = size;
        this.processed = 0;
        this.sentToCPU = 0;
    }

    /**
    Creates a new batch
     */
    public synchronized DataBatch batch(GPU gpu) {
        if (!isDone()) {
            this.sentToCPU += 1000;
            return new DataBatch(size - processed, this, gpu);
        }
        return null;
    }

    /**
     * Splits Data into data batches based on the given size. If it can create a vector of the given size, do so
     * otherwise it will create a vector of batches from the entire size of the data.
     * @param size Requested vector size
     * @param gpu GPU who trains this data
     * @return Vector of data batches
     */
    public synchronized  Vector<DataBatch> batch(int size, GPU gpu) {
        Vector<DataBatch> ret = new Vector<>();
        size = Integer.min(size, this.size / 1000);
        for (int i = 0; i < size * 1000; i += 1000)
            ret.add(new DataBatch(i, this, gpu));
        this.sentToCPU = 1000 * ret.size();
        return ret;
    }

    public boolean isDone() {
        return this.size == this.processed;
    }

    public Type getType() {
        return this.type;
    }

    public synchronized void processData() {
        this.processed += 1000;
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

    public boolean sentAllToCPU() {
        return this.sentToCPU == this.size;
    }

}