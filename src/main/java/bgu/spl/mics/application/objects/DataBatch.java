package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {

    private int index;
    private Data data;
    private GPU gpu;

    public DataBatch(int index, Data data, GPU gpu) {
        this.data = data;
        this.index = index;
        this.gpu = gpu;
    }

    public Data.Type getType() {
        return this.data.getType();
    }

    public GPU getGPU() {
        return this.gpu;
    }

    public Data getData() {
        return this.data;
    }
}
