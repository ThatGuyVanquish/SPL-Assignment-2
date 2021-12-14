package bgu.spl.mics;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.services.ConferenceService;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static class SingletonHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}
	private ConcurrentHashMap<MicroService, Vector<Message>> MicroDict;
	private ConcurrentHashMap<Message, Future> MsgToFutr;
	private ConcurrentHashMap<Class<? extends Message>, Vector<MicroService>> MsgToMicro;
	private Vector<ConfrenceInformation> conferences;
	private int nextConference;

	private  Object lockRoundRobin;

	private MessageBusImpl(){
     this.MicroDict = new ConcurrentHashMap<MicroService, Vector<Message>>();
	 this.MsgToFutr = new ConcurrentHashMap<Message, Future>();
	 this.MsgToMicro = new ConcurrentHashMap<Class<? extends Message>, Vector<MicroService>>();
	 this.conferences = new Vector<>();
	 this.nextConference = 0;
	}

	 public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}

	@Override
	public synchronized  <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		//synchronized (MsgToMicro) {
			if (!MsgToMicro.containsKey(type))
				MsgToMicro.put(type, new Vector<MicroService>());

			MsgToMicro.get(type).add(m);
		//}
	}

	/**
	 * @param type The type to subscribe to.
	 * @param m    The subscribing micro-service.
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (MsgToMicro) {
			if (!MsgToMicro.containsKey(type)) {
				MsgToMicro.put(type, new Vector<MicroService>());
			}
			MsgToMicro.get(type).add(m);
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
	public synchronized void sendBroadcast(Broadcast b) {
		Vector<MicroService> broad = MsgToMicro.get(b.getClass());
		if(broad!=null) {
			for (MicroService microService : broad) {
				MicroDict.get(microService).add(b);
				//System.out.println("sdfsdf");
			}
		}
		notifyAll();
	}

	@Override
	public  synchronized <T> Future<T> sendEvent(Event<T> e) {
		Future<T>  result = new Future<T>();
		if (!MsgToMicro.containsKey(e))
			return null;
	//	synchronized (lockRoundRobin) {
			MicroService s = MsgToMicro.get(e).remove(0); //round robin implement
			MicroDict.get(s).add(e);
			MsgToFutr.put(e, result);
			MsgToMicro.get(e).add(s);
			notifyAll();//MicroDict.get(s).notifyAll();
	//	}
		return result;
	}

	public void register(MicroService m) {
		MicroDict.put(m,new Vector<Message>());
	}

	@Override
	public void unregister(MicroService m) {
		Vector<Message> Subsricedto = MicroDict.remove(m);
		for(Message messege : Subsricedto){
			MsgToMicro.get(messege).remove(m);
		}
	}

	@Override
	public  synchronized Message awaitMessage(MicroService m) throws InterruptedException {
	//	synchronized (lockRoundRobin) {
			while (MicroDict.get(m).isEmpty()) {
				wait();//MicroDict.get(m).wait();
			}
	//	}
		Message msg = MicroDict.get(m).remove(0);
		return msg;
	}

	@Override
	public boolean isRegistered(MicroService m) {
		return MicroDict.containsKey(m);
	}

	@Override
	public <T> boolean isSubscribedToEvent(Class<? extends Event<T>> event, MicroService m) {
		return MsgToMicro.get(event).contains(m);
	}

	@Override
	public boolean isSubscribedToBroadcast(Class<? extends Broadcast> broadcast, MicroService m) {
		return MsgToMicro.get(broadcast).contains(m);
	}

	@Override
	public <T> Future getFuture(Event<T> e) {
		return MsgToFutr.get(e);
	}

	public void addConferences(Vector<ConfrenceInformation> vc) { this.conferences = vc; }

	public void nextConference() { this.nextConference++; }

	public ConfrenceInformation getNextConference() { return this.conferences.get(nextConference);}
}