package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.GPU;

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
    private int ticksRunning;
    public GPUService(String name, GPU gpu) {
        super(name);
        this.gpu = gpu;
        ticksRunning=0;
    }

    @Override
    protected void initialize() {
        Callback<TickBroadcast> tickCallback = (TickBroadcast tickBroadcast)-> {ticksRunning++;}; // Shouldn't add one as it would count ALL ticks as runtime
        subscribeBroadcast(TickBroadcast.class, tickCallback);
        Callback<TrainModelEvent> trainCallback = (TrainModelEvent e)-> {gpu.train(e);};
        subscribeEvent(TrainModelEvent.class, trainCallback);
        Callback<TestModelEvent> testCallback = (TestModelEvent e)-> gpu.test(e.getModel());
        Callback<TerminateBroadCast> TerminateCallBack = (TerminateBroadCast c) -> this.terminate();
        subscribeBroadcast(TerminateBroadCast.class,TerminateCallBack);

    }
    public int getTickUsed(){return ticksRunning;}
}
