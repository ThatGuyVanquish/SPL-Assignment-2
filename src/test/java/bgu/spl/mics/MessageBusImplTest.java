package bgu.spl.mics;

import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleEventHandlerService;

public class MessageBusImplTest {

    private static MessageBusImpl messageBus;
    private static ExampleEvent event;
    private static ExampleBroadcast broadcast;
    private static MicroService ms_event;
    private static MicroService ms_broadcast;
    private static Future<String> ftr;

    @Before
    public void setup() {
        messageBus = MessageBusImpl.getInstance();
        event = new ExampleEvent("Event");
        ms_event = new ExampleEventHandlerService("EventHandler", new String[]{"tst,tst"});
        broadcast = new ExampleBroadcast("Broadcast");
        ftr = new Future<>();
        messageBus.register(ms_event);
        messageBus.register(ms_broadcast);
    }

    @Test
    public static void subscribeEvent() {
        messageBus.subscribeEvent(event.getClass(), ms_event);
        assertTrue(messageBus.isSubscribedToEvent(event.getClass(), ms_event));
    }

    @Test
    public static void subscribeBroadcast() {
        messageBus.subscribeBroadcast(broadcast.getClass(), ms_broadcast);
        assertTrue(messageBus.isSubscribedToBroadcast(broadcast.getClass(), ms_broadcast));
    }

    @Test
    public void complete() {
        ftr.resolve("Mask on, mask off");
        assertTrue(ftr.isDone());
    }

    @Test
    public void sendBroadcast(){
        messageBus.subscribeBroadcast(broadcast.getClass(), ms_broadcast);
        messageBus.sendBroadcast(broadcast);
        Message msg = null;
        try {
            msg = messageBus.awaitMessage(ms_broadcast);
        }
        catch (InterruptedException e) {}
        assertEquals(broadcast, msg);
    }

    @Test
    public void sendEvent(){
        messageBus.subscribeEvent(event.getClass(), ms_event);
        messageBus.sendEvent(event);
        Message msg = null;
        try {
            msg = messageBus.awaitMessage(ms_event);
        }
        catch (InterruptedException e) {}
        assertEquals(event, msg);
    }

    @Test
    public void register(){
        assertTrue(messageBus.isRegistered(ms_event));
    }

    @Test
    public void unregister(){
        messageBus.unregister(ms_event);
        assertFalse(messageBus.isRegistered(ms_event));
    }

}