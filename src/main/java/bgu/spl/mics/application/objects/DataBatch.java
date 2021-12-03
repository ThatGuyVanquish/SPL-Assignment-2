package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {

    private int index;
    private Data data;

    public DataBatch(int index, Data data) {
        this.data = data;
        this.index = index;
    }


}
