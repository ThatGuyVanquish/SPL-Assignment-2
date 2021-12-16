package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.Vector;
import java.util.concurrent.CountDownLatch;

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
    private Model currModel;
    private Vector<Model> studentModels;



    public StudentService(String name, Student student, CountDownLatch countDownTimer) {
        super(name,countDownTimer,null);
        this.student = student;
        currModel = student.getNextModel();
        studentModels = student.getModels();
    }

    @Override
    protected void initialize() {
        sendEvent(new TrainModelEvent(currModel));
        Callback<TerminateBroadCast> TerminateCallBack = (TerminateBroadCast c) -> {this.terminate();};
        subscribeBroadcast(TerminateBroadCast.class,TerminateCallBack);
        Callback<FinishedTrainingEvent> finishedTrainingEventCallback = (FinishedTrainingEvent c) -> {sendEvent(new TestModelEvent(c.getModel()));};
        subscribeEvent(FinishedTrainingEvent.class, finishedTrainingEventCallback);
        Callback<FinishedTestedEvent> finishedTestedEventCallback = (FinishedTestedEvent c) ->
        {
            sendEvent(new PublishResultsEvent(c.getModel()));
            currModel = student.getNextModel();
            if (currModel!=null){
                sendEvent(new TrainModelEvent(currModel));
            }
        };
        subscribeEvent(FinishedTestedEvent.class,finishedTestedEventCallback);
        Callback<PublishConfrenceBroadcast> PublishConfrenceBroadcastCallBack = (PublishConfrenceBroadcast e) ->
        {
            int published = 0, papersRead = 0;
            this.student.addPublications(published);
            for (Model model : e.getPublishedModels()) {
                if (model.getStudent() != this.student) papersRead++;
                else published++;
                model.setStatus(Model.status.Published);
            }
            this.student.addPublications(published);
            this.student.addPaperRead(papersRead);
        };
        subscribeBroadcast(PublishConfrenceBroadcast.class,PublishConfrenceBroadcastCallBack);
    }
}
