package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.objects.Model;
import java.util.Vector;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    public enum Type {RTX3090, RTX2080, GTX1080}

    private Model currentModel;
    private Cluster cluster;
    private Type type;
    private int unprocessedData;

    public GPU(Type t, Cluster c) {
        this.type = t;
        this.currentModel = null;
        this.cluster = c;
        this.unprocessedData = 0;
    }

    public String toString() {
        return "GPU: " + this.type.toString();
    }

    public Type getType() {
        return this.type;
    }

    public Model getModel() {
        return this.currentModel;
    }

    /**
     * Trains a model, therefore it should receive processed data batches from the CPU and run them based on the type:
     * 3090 - 1 Tick, 32 Batches, 2080 - 2 Ticks, 16 Batches, 1080 - 4 Ticks, 8 Batches
     *
     * @param model current Model to train
     * @pre !data.isDone()
     * @pre model.getStatus() == PreTrained
     * @inv model.getData().processed@pre <= model.getData().processed
     * @post model.getStatus() == Trained
     * @post model.isDone()
     */
    public void train(Model model) {
        this.currentModel = model;
        model.setStatus(Model.status.Training);
        Data data = model.getData();
        int processed = 0;
        switch (this.type) {
            case RTX3090: { // Wait 1 tick
                //this.cluster.process(data); // Sends all the data to the cluster
                while (!data.processData(processed))
                {
                    //processed = this.cluster.askForData(32); // Asking for as many processed batches cluster has, up to 32
                    try {
                        this.wait();
                    }
                    catch (InterruptedException e){};
                }
                // call service to send a message through MessageBus to set future to resolved
            }
            case RTX2080: { // Wait 2 Ticks
                //this.cluster.process(data); // Sends all the data to the cluster
                while (!data.processData(processed))
                {
                    //processed = this.cluster.askForData(32); // Asking for as many processed batches cluster has, up to 32
                    try {
                        for (int i = 0; i < 2; i++)
                            this.wait();
                    }
                    catch (InterruptedException e){};
                }
                // call service to send a message through MessageBus to set future to resolved
            }
            case GTX1080: { // Wait 4 Ticks
                //this.cluster.process(data); // Sends all the data to the cluster
                while (!data.processData(processed))
                {
                    //processed = this.cluster.askForData(32); // Asking for as many processed batches cluster has, up to 32
                    try {
                        for (int i = 0; i < 4; i++)
                            this.wait();
                    }
                    catch (InterruptedException e){};
                }
                // call service to send a message through MessageBus to set future to resolved
            }
        }
        model.setStatus(Model.status.Trained);
    }

    /**
     *
     * @param model model to test
     * @return true
     * @pre Model.getStatus == Trained
     * @post Model.getStatus == Tested
     */
    public boolean test(Model model) {
        switch (model.getStudent().getStatus()){
            case PhD: {
                if (Math.random() >= 0.8)
                   model.setResult(Model.results.Good);
                else model.setResult((Model.results.Bad));
            }
            case MSc: {
                if (Math.random() >= 0.9)
                    model.setResult(Model.results.Good);
                else model.setResult((Model.results.Bad));
            }
        }
        model.setStatus(Model.status.Tested);
        return true;
    }
}
    /*
    IDEAS
    -------------------
    A vector to hold Data Batches which are currently being processed

    GPU receives a data item, then checks type and sends a number times the time in ticks
    it takes to the cpu to process it and receives data back doesnt matter which data

    Cluster will hold processed data until gpu asks for it
     */
