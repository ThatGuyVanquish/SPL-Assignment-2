package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    private String name;
    private Data data;
    private Student student;
    public enum status {PreTrained, Training, Trained, Tested};
    public enum results {None, Good, Bad};
    private status currentStatus;
    private results currentResult;

    public Model(String name, Data data, Student student) {
        this.name = name;
        this.data = data;
        this.student = student;
        this.currentStatus = status.PreTrained;
        this.currentResult = results.None;
    }

    public String getName() { return this.name;}
    public Data getData() { return this.data;}
    public Student getStudent() { return this.student;}
    public status getStatus() { return this.currentStatus;}
    public results getResult() { return this.currentResult;}

    public synchronized void setResult(results result) { this.currentResult = result;}
    public synchronized void setStatus(status status) { this.currentStatus = status;}

    public String toString() {
        return "Model Name: " + this.name + " Data: " + this.data.toString() + " Status: " + this.currentStatus.toString() + " Result: " + this.currentResult.toString();
    }

}
