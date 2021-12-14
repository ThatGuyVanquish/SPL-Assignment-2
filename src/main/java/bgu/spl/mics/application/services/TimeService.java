package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.TerminateBroadCast;
import bgu.spl.mics.TickBroadcast;

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
	//private int ticksPassed;
    private Timer timer;
	private static final MessageBusImpl MESSAGE_BUS = MessageBusImpl.getInstance();

	public TimeService(int tickTime, int duration) {
		super("Time Service");
		this._tickTime = tickTime;
		this._duration = duration;
		this.endApp = false;
		//this.ticksPassed = 0 ;
		this.timer = new Timer();
	}

	@Override
	protected void initialize() {
		TimerTask tt = new TimerTask() {
			@Override
			public void run() {
				sendBroadcast(new TickBroadcast());
				MESSAGE_BUS.addTick();
				System.out.println("tick");
			}
		};
		while (MESSAGE_BUS.getTicksPassed() <= _duration){
				timer.schedule(tt,_tickTime);
		}
		sendBroadcast(new TerminateBroadCast());
		terminate();
		}

	//public void addTick() {this.ticksPassed++;}
}
