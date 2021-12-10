package bgu.spl.mics.application.objects;

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
    public synchronized DataBatch batch() {
        if (!isDone())
            return new DataBatch(size-processed, this);
        return null;
    }

    public boolean isDone() {
        if (this.size == this.processed)
            return true;
        return false;
    }

    public Type getType() {
        return this.type;
    }

    public synchronized boolean processData(int batches) {
        this.processed += batches;
        return isDone();
    }

    public String toString() {
        return "Type: " + this.type.toString() + " Size: " + this.size;
    }
}
