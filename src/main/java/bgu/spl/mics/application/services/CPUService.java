package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.CPU;

/**
 * CPU service is responsible for handling the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private CPU cpu;

    public CPUService(String name, CPU cpu) {
        super(name);
        this.cpu = cpu;

    }

    @Override
    protected void initialize() {
        Callback<TickBroadcast> callback1 = (TickBroadcast c)-> cpu.process();; // New! Now updates runtime :)
        subscribeBroadcast(TickBroadcast.class,callback1);
        Callback<TerminateBroadCast> callback2 = (TerminateBroadCast c) -> this.terminate();
        subscribeBroadcast(TerminateBroadCast.class,callback2);
    }


}
