package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.ConfrenceInformation;

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
    //after the time of the conf, the service need to PublicConferenceBroadcast, and then register
    public ConferenceService(String name, ConfrenceInformation conf) {
        super(name);
        this.conf = conf;
        tickPassed =0;
    }

    @Override
    protected void initialize() {
        Callback<TickBroadcast> tickCallback = (TickBroadcast tickBroadcast)-> {
            tickPassed++; if(tickPassed>=conf.getDate()){
                sendBroadcast(new PublishConfrenceBroadcast(conf.getSuccsecfulModelNum())); terminate();
            }
        };
        subscribeBroadcast(TickBroadcast.class, tickCallback);
        Callback<PublishResultsEvent> publishCallBack =  (PublishResultsEvent c) -> {
              conf.addModel(c.get_studentModel());
        };
        subscribeEvent(PublishResultsEvent.class,publishCallBack);

    }
}
