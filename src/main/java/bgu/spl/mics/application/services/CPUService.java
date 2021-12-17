package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.CPU;
import java.util.concurrent.CountDownLatch;

/**
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private final CPU cpu;

    public CPUService(String name, CPU cpu,CountDownLatch countDownTimer,CountDownLatch countDownStudent) {
        super(name, countDownTimer,countDownStudent);
        this.cpu = cpu;
    }

    @Override
    protected void initialize() {
        Callback<TickBroadcast> callback1 = (TickBroadcast c)-> cpu.process();
        subscribeBroadcast(TickBroadcast.class,callback1);

        Callback<TerminateBroadCast> callback2 = (TerminateBroadCast c) -> {this.cpu.addRuntime(); this.terminate();};
        subscribeBroadcast(TerminateBroadCast.class,callback2);
    }
}