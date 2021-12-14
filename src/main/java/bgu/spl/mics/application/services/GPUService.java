package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    private GPU gpu;
    public GPUService(String name, GPU gpu) {
        super(name);
        this.gpu = gpu;
    }

    @Override
    protected void initialize() {
        Callback<TickBroadcast> tickCallback = (TickBroadcast tickBroadcast)-> {gpu.processData();};
        subscribeBroadcast(TickBroadcast.class, tickCallback);
        Callback<TrainModelEvent> trainCallback = (TrainModelEvent e)-> {gpu.train(e);};
        subscribeEvent(TrainModelEvent.class, trainCallback);
        Callback<TestModelEvent> testCallback = (TestModelEvent e)-> gpu.test(e.getModel());
        subscribeEvent(TestModelEvent.class, testCallback);
        Callback<TerminateBroadCast> TerminateCallBack = (TerminateBroadCast c) -> {this.gpu.addRuntime(); this.terminate();};
        subscribeBroadcast(TerminateBroadCast.class,TerminateCallBack);
    }
}
