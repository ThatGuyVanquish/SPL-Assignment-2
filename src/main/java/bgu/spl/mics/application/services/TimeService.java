package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.TerminateBroadCast;
import bgu.spl.mics.TickBroadcast;

import javax.security.auth.callback.Callback;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private int _tickTime;
	private int _duration;
	private boolean endApp;
	private int tickPassed;
    private static final Timer TIMER = new Timer();
//	private static TimerTask wrap(Runnable r) { //secend method, using a wrapper to using lambda fucn instead of annonymos class
//		return new TimerTask() {
//
//			@Override
//			public void run() {
//				r.run();
//			}
//		};
//	}
	public TimeService(int tickTime, int duration) {
		super("Time Service");
		this._tickTime = tickTime;
		this._duration = duration;
		this.endApp = false;
		tickPassed=0;

	}

	@Override
	protected void initialize() {
		while (tickPassed<=_duration){
			TIMER.schedule(new  TimerTask(){ public void run(){ sendBroadcast(new TickBroadcast());tickPassed++;};},_tickTime);
		}
		TIMER.cancel();
		sendBroadcast(new TerminateBroadCast());
		terminate();
	}
}
