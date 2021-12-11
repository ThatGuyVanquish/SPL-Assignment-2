package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.DataPreProcessEvent;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.TickBroadcast;
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
    private int TickUsed;
    public CPUService(String name, CPU cpu) {
        super(name);
        this.cpu = cpu;
        TickUsed = 0;
    }

    @Override
    protected void initialize() {
        Callback<TickBroadcast> callback1 = (TickBroadcast c)-> {cpu.UptadeTick();};
        subscribeBroadcast(TickBroadcast.class,callback1);

    }
    public int getTickUsed(){return TickUsed;}

}
