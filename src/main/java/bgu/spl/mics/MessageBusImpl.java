package bgu.spl.mics;

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
		private static final MessageBusImpl instance = new MessageBusImpl();
	}
	private ConcurrentHashMap<MicroService, LinkedBlockingDeque<Message>> MicroDict;
	private ConcurrentHashMap<Message, Future> MsgToFutr;
	private ConcurrentHashMap<Class<? extends Event<?>>, Vector<MicroService>> eventToMicro;
	private ConcurrentHashMap<Class<? extends  Broadcast>,Vector<MicroService>> broadToMicro;

	private  final Object lockRoundRobin = new Object();
	private final Object broadCastLock = new Object();

	private MessageBusImpl(){
     this.MicroDict = new ConcurrentHashMap<MicroService, LinkedBlockingDeque<Message>>();
	 this.MsgToFutr = new ConcurrentHashMap<Message, Future>();
	 this.eventToMicro = new ConcurrentHashMap<Class<? extends Event<?>>, Vector<MicroService>>();
	 this.broadToMicro = new ConcurrentHashMap<Class<? extends Broadcast>, Vector<MicroService>>();
	}

	 public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}

	@Override
	public   <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (lockRoundRobin) {
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
	public void sendBroadcast(Broadcast b) {
		synchronized (broadCastLock) {
				Vector<MicroService> broad = broadToMicro.get(b.getClass());
				if (broad != null) {
					for (MicroService microService : broad) {
						if (MicroDict.containsKey(microService))
						MicroDict.get(microService).add(b);
				}
			}
		}
	}

	@Override
	public  <T> Future<T> sendEvent(Event<T> e) {
		synchronized (lockRoundRobin) {
		Future<T> result = new Future<>();
		if (!eventToMicro.containsKey(e.getClass())){
			System.out.println(e.getClass());
			return null;
		}
			MicroService s = eventToMicro.get(e.getClass()).remove(0);
			eventToMicro.get(e.getClass()).add(s);
			MicroDict.get(s).add(e);
			MsgToFutr.put(e, result);

			return result;
	  }
	}

	public void register(MicroService m) {
		MicroDict.put(m,new LinkedBlockingDeque<>());
	}

	@Override
	public void unregister(MicroService m) {
		synchronized (lockRoundRobin) {
			synchronized (broadCastLock){
			LinkedBlockingDeque<Message> subscribedTo = MicroDict.remove(m);
			for (Message message : subscribedTo) {
				if (message instanceof Broadcast) {
					broadToMicro.get(message.getClass()).remove(m);
				} else {
					eventToMicro.get(message.getClass()).remove(m);
				}
			}
			}
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		//if (MicroDict.isEmpty()) return null;
		try {
				return MicroDict.get(m).take();
		}
		catch (InterruptedException e) {
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

	public String toString() { // DELETE BEFORE UPLOADING
		String ret = "";
		for (MicroService m : MicroDict.keySet()) ret += m.getName();
		return ret;
	}

}