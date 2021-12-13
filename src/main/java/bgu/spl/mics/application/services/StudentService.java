package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.Vector;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private String name;
    private Student student;
    public StudentService(String name, Student student) {
        super(name);
        this.student = student;
    }

    @Override
    protected void initialize() {
        Vector<Model> studentModel= student.getModels();
        for (Model model:studentModel){
            sendEvent(new TrainModelEvent(model));
        }
        Callback<TerminateBroadCast> TerminateCallBack = (TerminateBroadCast c) -> this.terminate();
        subscribeBroadcast(TerminateBroadCast.class,TerminateCallBack);
        Callback<FinishedTrainingEvent> finishedTrainingEventCallback = (FinishedTrainingEvent c) -> {sendEvent(new TestModelEvent(c.getModel()));};
        subscribeEvent(FinishedTrainingEvent.class, finishedTrainingEventCallback);
        sendEvent(new PublishResultsEvent(studentModel));
        subscribeBroadcast(PublishConfrenceBroadcast.class); // Needs callback
    }
}
