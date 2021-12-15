package bgu.spl.mics.application.services;

import bgu.spl.mics.*;

import javax.security.auth.callback.Callback;
import java.sql.Time;
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
	private int ticksPassed;
    private  Timer TIMER;

	public TimeService(int tickTime, int duration) {
		super("Time Service",null,null);
		this._tickTime = tickTime;
		this._duration = duration;
		this.endApp = false;
		this.ticksPassed=0;
		TIMER = new Timer();
	}

	@Override
	protected void initialize() {
		while (ticksPassed<=_duration-1) {

			TIMER.schedule(new TimerTask() {
				public void run() {
					sendBroadcast(new TickBroadcast());
					ticksPassed++;
				}
			}, _tickTime);
		}
		TIMER.cancel();
		System.out.println(ticksPassed);
		System.out.println(Thread.activeCount());
		System.out.println(MessageBusImpl.getInstance().print());
		sendBroadcast(new TerminateBroadCast());
		terminate();
	}

}
