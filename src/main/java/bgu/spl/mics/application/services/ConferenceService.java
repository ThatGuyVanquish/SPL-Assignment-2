package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.ConfrenceInformation;

import java.util.concurrent.CountDownLatch;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {

    private ConfrenceInformation conf;
    private  int tickPassed;
    private static final MessageBusImpl MESSAGE_BUS = MessageBusImpl.getInstance();

    //after the time of the conf, the service need to PublicConferenceBroadcast, and then register
    public ConferenceService(String name, ConfrenceInformation conf, CountDownLatch countDownTimer,CountDownLatch countDownStudent) {
        super(name, countDownTimer,countDownStudent);
        this.conf = conf;
        tickPassed =0;
    }

    @Override
    protected void initialize() {
        Callback<TickBroadcast> tickCallback = (TickBroadcast tickBroadcast)-> {
            tickPassed++; if(tickPassed>=conf.getDate()){
                sendBroadcast(new PublishConfrenceBroadcast(this.conf.getPublications()));
                System.out.println(conf.getDate());
                terminate();
            }
        };
        subscribeBroadcast(TickBroadcast.class, tickCallback);
        Callback<PublishResultsEvent> publishCallBack =  (PublishResultsEvent c) -> {
              conf.addModel(c.getModel());
        };
        subscribeEvent(PublishResultsEvent.class,publishCallBack);
        Callback<TerminateBroadCast> TerminateCallBack = (TerminateBroadCast c) -> {this.terminate();};
        subscribeBroadcast(TerminateBroadCast.class,TerminateCallBack);
    }
}
