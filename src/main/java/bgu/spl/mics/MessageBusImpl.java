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
		// TODO Auto-generated method stub

	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T>  result = new Future<T>();
		if (!MsgToMicro.containsKey(e))
			return null;
		MicroService s = MsgToMicro.get(e).get(0);//need to implement "round robin", maybe add field?
		MicroDict.get(s).add(e);
		MsgToFutr.put(e,result);
		return result;
	}


	public void register(MicroService m) {
		MicroDict.put(m,new Vector<Message>());
	}

	@Override
	public void unregister(MicroService m) {
		MicroDict.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRegistered(MicroService m) {
		// TODO I generated this method stub
		return false;
	}

	@Override
	public boolean isSubscribedToEvent(MicroService m) {
		return false;
	}

	@Override
	public boolean isSubscribedToBroadcast(MicroService m) {
		return false;
	}

}