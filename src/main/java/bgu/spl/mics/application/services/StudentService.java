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
    private int published;
    private static final MessageBusImpl MESSAGE_BUS = MessageBusImpl.getInstance();

    public StudentService(String name, Student student) {
        super(name);
        this.student = student;
        this.published = 0;
    }

    @Override
    protected void initialize() {
        Vector<Model> studentModels = student.getModels();
        for (Model model : studentModels){
            sendEvent(new TrainModelEvent(model));
        }
        Callback<TerminateBroadCast> TerminateCallBack = (TerminateBroadCast c) -> this.terminate();
        subscribeBroadcast(TerminateBroadCast.class,TerminateCallBack);
        Callback<FinishedTrainingEvent> finishedTrainingEventCallback = (FinishedTrainingEvent c) -> {sendEvent(new TestModelEvent(c.getModel()));};
        subscribeEvent(FinishedTrainingEvent.class, finishedTrainingEventCallback);
        Callback<FinishedTestedEvent> finishedTestedEventCallback = (FinishedTestedEvent c) ->
        {
            sendEvent(new PublishResultsEvent(c.getModel()));
        };
        subscribeEvent(FinishedTestedEvent.class,finishedTestedEventCallback);
        Callback<PublishConfrenceBroadcast> PublishConfrenceBroadcastCallBack = (PublishConfrenceBroadcast e) ->
        {
            for (Model model : studentModels){
                if (model.getStatus() == Model.status.Tested && model.getResult() == Model.results.Good) {
                    published++;
                    MESSAGE_BUS.getNextConference().addModel(model);
                    model.setStatus(Model.status.Published);
                }
            }
            student.addPaperRead(MESSAGE_BUS.getNextConference().papersRead(this.student));
        };
        subscribeBroadcast(PublishConfrenceBroadcast.class,PublishConfrenceBroadcastCallBack);
    }
}
