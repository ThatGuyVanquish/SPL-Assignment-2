package bgu.spl.mics;

/**
 * The message-bus is a shared object used for communication between
 * micro-services.
 * It should be implemented as a thread-safe singleton.
 * The message-bus implementation must be thread-safe as
 * it is shared between all the micro-services in the system.
 * You must not alter any of the given methods of this interface. 
 * You cannot add methods to this interface.
 */
public interface MessageBus {

    /**
     * Subscribes {@code m} to receive {@link Event}s of type {@code type}.
     * <p>
     * @param <T>  The type of the result expected by the completed event.
     * @param type The type to subscribe to
     * @param m    The subscribing micro-service.
     * @pre Trivial
     * @post isSubsribdToeEvent(type,m)==true
     */
    <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m);

    /**
     * Subscribes {@code m} to receive {@link Broadcast}s of type {@code type}.
     * <p>
     * @param type 	The type to subscribe to.
     * @param m    	The subscribing micro-service.
     * @pre Trivial
     * @post isSubsribedToBroadcast(type,m) == true
     */
    void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m);

    /**
     * Notifies the MessageBus that the event {@code e} is completed and its
     * result was {@code result}.
     * When this method is called, the message-bus will resolve the {@link Future}
     * object associated with {@link Event} {@code e}.
     * <p>
     * @param <T>    The type of the result expected by the completed event.
     * @param e      The completed event.
     * @param result The resolved result of the completed event.
     * @pre !getFuture(e).isDone()
     * @post getFuture(e).isDone()
     */
    <T> void complete(Event<T> e, T result);

    /**
     * Adds the {@link Broadcast} {@code b} to the message queues of all the
     * micro-services subscribed to {@code b.getClass()}.
     * <p>
     * @param b 	The message to added to the queues.
     */
    void sendBroadcast(Broadcast b);

    /**
     * Adds the {@link Event} {@code e} to the message queue of one of the
     * micro-services subscribed to {@code e.getClass()} in a round-robin
     * fashion. This method should be non-blocking.
     * <p>
     * @param <T>    	The type of the result expected by the event and its corresponding future object.
     * @param e     	The event to add to the queue.
     * @return {@link Future<T>} object to be resolved once the processing is complete,
     * 	       null in case no micro-service has subscribed to {@code e.getClass()}.
     */
    <T> Future<T> sendEvent(Event<T> e);

    /**
     * Allocates a message-queue for the {@link MicroService} {@code m}.
     * <p>
     * @param m the micro-service to create a queue for.
     * @pre !isRegistered(m)
     * @post isRegistered(m)
     */
    void register(MicroService m);

    /**
     * Removes the message queue allocated to {@code m} via the call to
     * {@link #register(bgu.spl.mics.MicroService)} and cleans all references
     * related to {@code m} in this message-bus. If {@code m} was not
     * registered, nothing should happen.
     * <p>
     * @param m the micro-service to unregister.
     * @pre isRegistered()
     * @post !isRegistered()
     */
    void unregister(MicroService m);

    /**
     * Using this method, a <b>registered</b> micro-service can take message
     * from its allocated queue.
     * This method is blocking meaning that if no messages
     * are available in the micro-service queue it
     * should wait until a message becomes available.
     * The method should throw the {@link IllegalStateException} in the case
     * where {@code m} was never registered.
     * <p>
     * @param m The micro-service requesting to take a message from its message
     *          queue.
     * @return The next message in the {@code m}'s queue (blocking).
     * @throws InterruptedException if interrupted while waiting for a message
     *                              to became available.
     * @pre isRegistered(m)
     */
    Message awaitMessage(MicroService m) throws InterruptedException;

    /**
     *
     * @param m micro
     * @return true if microservice is registered to the hashmap
     */
    boolean isRegistered(MicroService m);

    /**
     * Checks if @param m is subscribed to events of type @param event
     * @param m microservice that is supposed to be subscribed to event
     * @param event event class to subscribe to
     * @return true if microservice is subscribed
     */
    <T> boolean isSubscribedToEvent(Class<? extends Event<T>> event, MicroService m);

    /**
     *
     * Checks if @param m is subscribed to broadcast of type @param broadcast
     * @param m microservice that is supposed to be subscribed to event
     * @param broadcast broadcast class to subscribe to
     * @return true if microservice is subscribed
     */
    boolean isSubscribedToBroadcast(Class<? extends Broadcast> broadcast, MicroService m);

    /**
     * Gets future related to event e
     * @param e Event future is reated to
     * @param <T> Class type of Future
     * @return Future relatedto event e
     */
    <T> Future getFuture(Event<T> e);
}
