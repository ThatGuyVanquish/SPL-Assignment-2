package bgu.spl.mics.application.services;
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

	private final long tickTime;
	private final long duration;
	private long ticksPassed;

	public TimeService(int tickTime,int duration) {
		super("TimeService",null,null);
		this.tickTime=tickTime;
		this.duration=duration;
		this.ticksPassed = 0;
	}

	@Override
	protected void initialize() {
		Timer TIMER = new Timer();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				if(ticksPassed <= duration){
					ticksPassed++;
					sendBroadcast(new TickBroadcast());
				}
				if(ticksPassed>=duration){
					sendBroadcast(new TerminateBroadCast());
					cancel();
					terminate();
				}
			}
		};
		TIMER.scheduleAtFixedRate(timerTask,tickTime,tickTime);
		subscribeBroadcast(TerminateBroadCast.class, (TerminateBroadCast b)-> terminate());
	}
}