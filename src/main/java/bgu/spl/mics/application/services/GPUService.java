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
    private int TickUsed;
    public GPUService(String name, GPU gpu) {
        super(name);
        this.gpu = gpu;
        TickUsed=0;
    }

    @Override
    protected void initialize() {
        Callback<TickBroadcast> callback1 = (TickBroadcast c)-> {TickUsed = TickUsed+1;};
        subscribeBroadcast(TickBroadcast.class,callback1);
        Callback<TrainModelEvent> callback2 = (TrainModelEvent c)-> {gpu.train(c.getModel());};
        subscribeEvent(TrainModelEvent.class,callback2);

    }
    public int getTickUsed(){return TickUsed;}
}
