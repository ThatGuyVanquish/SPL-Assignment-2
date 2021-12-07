package bgu.spl.mics;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private ConcurrentHashMap<MicroService, Vector<Message>> MicroDict;
	private ConcurrentHashMap<Message, Future> MsgToFutr;
	private ConcurrentHashMap<Class<? extends Message>, Vector<MicroService>> MsgToMicro;
	private static MessageBusImpl instance = null;
	private  Object lockRoundRobin;

	private MessageBusImpl(){
     MicroDict = new ConcurrentHashMap<MicroService, Vector<Message>>();
	 MsgToFutr = new ConcurrentHashMap<Message, Future>();
	 MsgToMicro = new ConcurrentHashMap<Class<? extends Message>, Vector<MicroService>>();
	}

	 public static MessageBusImpl getInstance() {
		if (instance==null){
			instance = new MessageBusImpl();
		}
		return  instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (!MsgToMicro.containsKey(type))
			MsgToMicro.put(type,new Vector<MicroService>());
		MsgToMicro.get(type).add(m);
	}

	/**
	 * @param type The type to subscribe to.
	 * @param m    The subscribing micro-service.
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (!MsgToMicro.containsKey(type))
			MsgToMicro.put(type,new Vector<MicroService>());
		MsgToMicro.get(type).add(m);

	}

	/**
	 * @param e      The completed event.
	 * @param result The resolved result of the completed event.
	 * @param <T>
	 */
	@Override
	public <T> void complete(Event<T> e, T result) {
		MsgToFutr.get(e).resolve(result);
		MsgToFutr.remove(e);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		Vector<MicroService> broad = MsgToMicro.get(b);
		for(MicroService microService : broad){
			MicroDict.get(broad).add(b);
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T>  result = new Future<T>();
		if (!MsgToMicro.containsKey(e))
			return null;
		synchronized (lockRoundRobin) {
			MicroService s = MsgToMicro.get(e).remove(0); //round robin implement
			MicroDict.get(s).add(e);
			MsgToFutr.put(e, result);
			MsgToMicro.get(e).add(s);
		}
		return result;
	}


	public void register(MicroService m) {
		MicroDict.put(m,new Vector<Message>());
	}

	@Override
	public void unregister(MicroService m) {
		MicroDict.remove(m);
		// need to delete in other queues as well?
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		try {
			while (MicroDict.get(m).isEmpty()) {
				m.wait();
			}
		}
		catch (InterruptedException e){ };
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

}