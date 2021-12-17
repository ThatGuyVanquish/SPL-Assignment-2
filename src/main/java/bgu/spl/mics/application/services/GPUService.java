package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.concurrent.CountDownLatch;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    private GPU gpu;
    public GPUService(String name, GPU gpu, CountDownLatch countDownTimer,CountDownLatch countDownStudent) {
        super(name,countDownTimer,countDownStudent);
        this.gpu = gpu;
    }

    public GPU getGpu() { return this.gpu;}

    @Override
    protected void initialize() {
        Callback<TickBroadcast> tickCallback = (TickBroadcast tickBroadcast)-> {gpu.processData();};
        subscribeBroadcast(TickBroadcast.class, tickCallback);
        Callback<TrainModelEvent> trainCallback = (TrainModelEvent e)-> {
            //System.out.println("GPU " + Thread.currentThread().getName() + " called to train the model " + e.getModel().getName());
            gpu.train(e);};
        subscribeEvent(TrainModelEvent.class, trainCallback);
        Callback<TestModelEvent> testCallback = (TestModelEvent e)-> gpu.test(e.getModel());
        subscribeEvent(TestModelEvent.class, testCallback);
        Callback<TerminateBroadCast> TerminateCallBack = (TerminateBroadCast c) -> {
            this.gpu.addRuntime();
            System.out.println("gpu time:"+gpu.getRuntime());
            System.out.println(this.getName()+gpu.getTrainingVector());
            this.terminate();};
        subscribeBroadcast(TerminateBroadCast.class,TerminateCallBack);
    }

    public void sendGPUBroadcast(Broadcast b) {
        this.sendBroadcast(b);
    }
}
