package bgu.spl.mics;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.services.ConferenceService;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static class SingletonHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}
	private ConcurrentHashMap<MicroService, LinkedBlockingDeque<Message>> MicroDict;
	private ConcurrentHashMap<Message, Future> MsgToFutr;
	private ConcurrentHashMap<Class<? extends Event<?>>, Vector<MicroService>> eventToMicro;
	private ConcurrentHashMap<Class<? extends  Broadcast>,Vector<MicroService>> broadToMicro;
	private Vector<ConfrenceInformation> conferences;
	private int nextConference;

	private  final Object lockRoundRobin = new Object();
	private final Object broadCastLock = new Object();

	private MessageBusImpl(){
     this.MicroDict = new ConcurrentHashMap<MicroService, LinkedBlockingDeque<Message>>();
	 this.MsgToFutr = new ConcurrentHashMap<Message, Future>();
	 this.eventToMicro = new ConcurrentHashMap<Class<? extends Event<?>>, Vector<MicroService>>();
	 this.broadToMicro = new ConcurrentHashMap<Class<? extends Broadcast>, Vector<MicroService>>();
	 this.conferences = new Vector<>();
	 this.nextConference = 0;
	}

	 public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}

	@Override
	public   <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (eventToMicro) {
			if (!eventToMicro.containsKey(type))
				eventToMicro.put(type, new Vector<MicroService>());
		 }
			eventToMicro.get(type).add(m);
	}

	/**
	 * @param type The type to subscribe to.
	 * @param m    The subscribing micro-service.
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (broadCastLock) {
			if (!broadToMicro.containsKey(type)) {
				broadToMicro.put(type, new Vector<MicroService>());
			}
			broadToMicro.get(type).add(m);
		}
	}

	public String print() { return this.MicroDict.toString();}

	/**
	 * @param e      The completed event.
	 * @param result The resolved result of the completed event.
	 * @param <T>
	 */
	@Override
	public <T> void complete(Event<T> e, T result) {
		synchronized (MsgToFutr) {
			MsgToFutr.get(e).resolve(result);
			MsgToFutr.remove(e);
		}
	}

	@Override
	public  void sendBroadcast(Broadcast b) {
		synchronized (broadCastLock){
		Vector<MicroService> broad = broadToMicro.get(b.getClass());
		 if(broad!=null) {
			for (MicroService microService : broad) {
				MicroDict.get(microService).add(b);

			}
		 }
		}

	}

	@Override
	public  <T> Future<T> sendEvent(Event<T> e) {
		Future<T>  result = new Future<T>();
		if (!eventToMicro.containsKey(e)){
			System.out.println("nullisthrown");
			return null;
		}

		synchronized (lockRoundRobin) {
			MicroService s = eventToMicro.get(e).remove(0); //round robin implement
			MicroDict.get(s).add(e);
			MsgToFutr.put(e, result);
			eventToMicro.get(e).add(s);
	  }
		return result;
	}

	public void register(MicroService m) {
		MicroDict.put(m,new LinkedBlockingDeque<Message>());
	}

	@Override
	public void unregister(MicroService m) {
		LinkedBlockingDeque<Message> Subsricedto = MicroDict.remove(m);
		for(Message messege : Subsricedto){
			if(messege instanceof Broadcast)
				broadToMicro.get(messege.getClass()).remove(m);
			else
			   eventToMicro.get(messege.getClass()).remove(m);
		}
	}

	@Override
	public   Message awaitMessage(MicroService m) throws InterruptedException {
		try {
			return MicroDict.get(m).take();
		} catch (InterruptedException e) {
			System.out.println("interrupted");
			throw e;
		}
	}

	@Override
	public boolean isRegistered(MicroService m) {
		return MicroDict.containsKey(m);
	}

	@Override
	public <T> boolean isSubscribedToEvent(Class<? extends Event<T>> event, MicroService m) {
		return eventToMicro.get(event).contains(m);
	}

	@Override
	public boolean isSubscribedToBroadcast(Class<? extends Broadcast> broadcast, MicroService m) {
		return broadToMicro.get(broadcast).contains(m);
	}

	@Override
	public <T> Future getFuture(Event<T> e) {
		return MsgToFutr.get(e);
	}

	public void addConferences(Vector<ConfrenceInformation> vc) { this.conferences = vc; }

	public void nextConference() { this.nextConference++; }

	public ConfrenceInformation getNextConference() { return this.conferences.get(nextConference);}
}