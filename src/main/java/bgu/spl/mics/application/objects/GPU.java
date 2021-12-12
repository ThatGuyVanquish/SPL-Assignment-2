package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.TrainModelEvent;
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
    private static final Cluster CLUSTER = Cluster.getInstance();
    private static final MessageBusImpl MESSAGE_BUS = MessageBusImpl.getInstance();
    private Type type;
    private long tickCounter;
    private Event<Model> currentEvent;
    private Vector<DataBatch> processedDataBatch;

    public GPU(Type t) {
        this.type = t;
        this.currentModel = null;
        this.currentEvent = null;
        processedDataBatch = new Vector<DataBatch>();
        switch (this.type) {
            case GTX1080: {
              processedDataBatch.setSize(8);
            }
            case RTX2080: {
                processedDataBatch.setSize(16);
            }
            case RTX3090: {
                processedDataBatch.setSize(32);
            }

        }
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
     * @param trainEvent TrainModelEvent called to train a model
     * @pre !data.isDone()
     * @pre model.getStatus() == PreTrained
     * @inv model.getData().processed@pre <= model.getData().processed
     * @post model.getStatus() == Trained
     * @post model.isDone()
     */
    public  void train(Model e){ //
        currentModel = e;
        this.currentModel.setStatus(Model.status.Training);
        CLUSTER.
    }

    public void CalledOnTick(){
        if (currentModel.getStatus() == Model.status.Training){

        }
    }
//
//    public long train(TrainModelEvent trainEvent) {
//        this.currentEvent = trainEvent;
//        this.currentModel = trainEvent.getModel();
//        currentModel.setStatus(Model.status.Training);
//        Data data = currentModel.getData();
//        switch (this.type) {
//            case RTX3090: { // Waits 1 tick
//                while (!data.isDone())
//                {
//                    CLUSTER.processData(32);
//                    try {
//                        this.wait();
//                    }
//                    catch (InterruptedException e){tickCounter++;}
//                }
//            }
//            case RTX2080: { // Wait 2 Ticks
//                while (!data.isDone())
//                {
//                    CLUSTER.processData(16);
//                    for (int i = 0; i < 2; i++) {
//                        try {
//                            this.wait();
//                        } catch (InterruptedException e) {
//                            tickCounter++;
//                        }
//                    }
//                }
//            }
//            case GTX1080: { // Wait 4 Ticks
//                while (!data.isDone())
//                {
//                    CLUSTER.processData(8);
//                    for (int i = 0; i < 4; i++) {
//                        try {
//                            this.wait();
//                        } catch (InterruptedException e) {
//                            tickCounter++;
//                        }
//                    }
//                }
//            }
//        }
//        MESSAGE_BUS.getFuture(this.currentEvent).resolve(this.currentModel);
//        // call service to send a message through MessageBus to set future to resolved
//        this.currentModel.setStatus(Model.status.Trained);
//        return tickCounter;
//    }

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
                break;
            }
            case MSc: {
                if (Math.random() >= 0.9)
                    model.setResult(Model.results.Good);
                else model.setResult((Model.results.Bad));
                break;
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
