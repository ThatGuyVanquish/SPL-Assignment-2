package bgu.spl.mics.application.services;
import bgu.spl.mics.Callback;
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

	private long tickTime;
	private long duration;
	private long ticksPassed;
	private long time;

	public TimeService(int tickTime,int duration) {
		super("TimeService",null,null);
		this.tickTime=tickTime;
		this.duration=duration;
		ticksPassed = 0;
		time=0;
	}

	private void setTime (long time) {this.time=time;}
	private long getTime (){return time;}
	@Override
	protected void initialize() {
		Timer t = new Timer();
		TimerTask tt = new TimerTask() {
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
			};
		};
		t.scheduleAtFixedRate(tt,tickTime,tickTime);
		// creating timer task, timer
		//imerTask tasknew = new TimerScheduleFixedRateDelay();
		//Callback<TimerTask> aa = (TimerTask e) -> {sendEvent(new TestModelEvent(e.getModel()));};
		//Timer timer1 = new Timer();

		// scheduling the task at fixed rate delay
		//timer.scheduleAtFixedRate(tasknew,500,1000);
//		timer.scheduleAtFixedRate(() -> {
//
//		}, tickTime, tickTime);


		subscribeBroadcast(TerminateBroadCast.class, (TerminateBroadCast b)->{ terminate();});
//		while (ticksPassed<=duration-1) {
//			timer.schedule(new TimerTask() {
//				public void run() {
//					sendBroadcast(new TickBroadcast());
//					ticksPassed++;
//				}
//			}, ticksPassed);
//		}
//
//		timer.cancel();
//		System.out.println("terminate");
//		sendBroadcast(new TerminateBroadcast());


	}

}
