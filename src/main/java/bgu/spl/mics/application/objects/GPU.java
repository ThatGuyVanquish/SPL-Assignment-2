package bgu.spl.mics.application.objects;

import bgu.spl.mics.*;


import java.util.Queue;
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
    private Vector<DataBatch> awaitingProcessing;
    private DataBatch currentDB;
    private int tickCounter;
    private int processDuration;
    private int runtime;
    int j = 0;
    private Vector<Model> trainingVector;
    private Vector<Model> testingVector;

    public GPU(Type t) {
        this.type = t;
        this.currentModel = null;
        this.currentDB = null;
        this.tickCounter = 0;
        this.runtime = 0;
        this.awaitingProcessing = new Vector<DataBatch>();
        this.trainingVector = new Vector<>();
        this.testingVector = new Vector<>();
        switch (this.type) {
            case GTX1080: {
                this.awaitingProcessing.setSize(8);
                this.processDuration = 4;
                break;
            }
            case RTX2080: {
                this.awaitingProcessing.setSize(16);
                this.processDuration = 2;
                break;
            }
            case RTX3090: {
                this.awaitingProcessing.setSize(32);
                this.processDuration = 1;
                break;
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
    public void train(TrainModelEvent trainEvent){
        if (this.currentModel == null) {
            trainEvent.getModel().setStatus(Model.status.Training);
            this.currentModel = trainEvent.getModel();
            Vector<DataBatch> initialBatch = this.currentModel.getData().batch(this.awaitingProcessing.size(), this);
            CLUSTER.processData(initialBatch);
        }
        else {
            this.trainingVector.add(trainEvent.getModel());
            //System.out.println(Thread.currentThread().getName());
            //System.out.println("current model is " + this.currentModel);
            //System.out.println("training vecotr 2 is " + this.trainingVector);
        }
    }

    public synchronized void addBatch(DataBatch dataBatch) {
        this.awaitingProcessing.add(dataBatch);
    }

    private void setNextBatch(){
        if (!this.awaitingProcessing.isEmpty()) {
            this.currentDB = this.awaitingProcessing.remove(0);
            if (!this.currentModel.getData().sentAllToCPU()) CLUSTER.processData(this.currentModel.getData().batch(this));
        }
        else {
            this.currentDB = null;
        }
        tickCounter = 0;
    }

    public void processData(){
        if (this.currentModel == null) {
            if (!this.testingVector.isEmpty()) test(testingVector.remove(0));
            if (!this.trainingVector.isEmpty()) this.currentModel = this.trainingVector.remove(0);
            return; }
        if (this.currentModel.getData().isDone()) {
            this.currentModel.setStatus(Model.status.Trained);
            MESSAGE_BUS.sendEvent(new FinishedTrainingEvent(this.currentModel));
            //System.out.println("Finished training he model " + this.currentModel.getName());
            this.currentModel = null;
            this.tickCounter = 0;
            //System.out.println(this.trainingVector);
            //System.out.println(this.currentModel);
            return;
        }
        if (this.currentModel.getStatus() == Model.status.Training){
            this.tickCounter++;
            this.runtime++;
            if (this.tickCounter >= this.processDuration) {
                this.currentModel.getData().processData();
                setNextBatch();
            }
        }
       if(this.currentModel.getStatus() == Model.status.PreTrained){
           //System.out.println("current model is " + this.currentModel.getName());
           //System.out.println("this is the training vector" + this.trainingVector);
           this.currentModel.setStatus(Model.status.Training);
           //System.out.println("current model status is " + this.currentModel.getStatus());
       }
    }

    /**
     *
     * @param model model to test
     * @return true
     * @pre Model.getStatus == Trained
     * @post Model.getStatus == Tested
     */
    public void test(Model model) {
        if (currentModel == null) {
            switch (model.getStudent().getStatus()) {
                case PhD: {
                    if (Math.random() >= 0.2)
                        model.setResult(Model.results.Good);
                    else model.setResult((Model.results.Bad));
                    break;
                }
                case MSc: {
                    if (Math.random() >= 0.4)
                        model.setResult(Model.results.Good);
                    else model.setResult((Model.results.Bad));
                    break;
                }
            }
            model.setStatus(Model.status.Tested);
            MESSAGE_BUS.sendEvent(new FinishedTestedEvent(model));
            if (!this.testingVector.isEmpty()) {
                test(testingVector.remove(0));
            }
        }
        else {
            this.testingVector.add(model);
        }
    }

    public void addRuntime() {CLUSTER.addGPURuntime(this.runtime); }

    public int getRuntime() {
        return runtime;
    }

    public Vector<Model> getTrainingVector() { return this.trainingVector;}
}