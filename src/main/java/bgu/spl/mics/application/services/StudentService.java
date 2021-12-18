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
    private final Student student;
    private Model currModel;
    private final Vector<Model> studentModels;

    public StudentService(String name, Student student, CountDownLatch countDownTimer) {
        super(name,countDownTimer,null);
        this.student = student;
        this.currModel = student.getNextModel();
        this.studentModels = student.getModels();
    }

    @Override
    protected void initialize() {
        sendEvent(new TrainModelEvent(currModel));

        Callback<FinishedTrainingBroadcast> finishedTrainingEventCallback = (FinishedTrainingBroadcast c) ->
        {
            if (this.studentModels.contains(c.getModel()))
                sendEvent(new TestModelEvent(c.getModel()));
        };
        subscribeBroadcast(FinishedTrainingBroadcast.class, finishedTrainingEventCallback);

        Callback<FinishedTestingBroadcast> finishedTestingBroadcastCallback = (FinishedTestingBroadcast c) ->
        {
            if (this.studentModels.contains(c.getModel())) {
                if (student.getConferenceNum()>0){
                    sendEvent(new PublishResultsEvent(c.getModel()));
                }
                currModel = student.getNextModel();
                if (currModel!=null){
                    sendEvent(new TrainModelEvent(currModel));
                }
            }
        };
        subscribeBroadcast(FinishedTestingBroadcast.class, finishedTestingBroadcastCallback);

        Callback<PublishConfrenceBroadcast> PublishConfrenceBroadcastCallBack = (PublishConfrenceBroadcast e) ->
        {
            this.student.setConferenceNum(student.getConferenceNum()-1);
            int published = 0;
            int papersRead = 0;
            for (Model model: e.getModels()) {
                model.setStatus(Model.status.Published);
                if (model.getStudent() == this.student)
                    published++;
                else
                    papersRead++;
            }
                student.addPublications(published);
                student.addPaperRead(papersRead);
        };
        subscribeBroadcast(PublishConfrenceBroadcast.class,PublishConfrenceBroadcastCallBack);

        Callback<TerminateBroadCast> TerminateCallBack = (TerminateBroadCast c) -> this.terminate();
        subscribeBroadcast(TerminateBroadCast.class,TerminateCallBack);
    }

}
