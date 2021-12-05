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
    private static MicroService ms;
    private static Future<String> ftr;

    @Before
    public void setup() {
        messageBus = MessageBusImpl.getInstance();
         event = new ExampleEvent("Event");
         ms = new ExampleEventHandlerService("EventHandler", new String[]{"tst,tst"});
         broadcast = new ExampleBroadcast("Broadcast");
         ftr = new Future<>();
         messageBus.register(ms);
    }

    @Test
    public static void subscribeEvent() {
        messageBus.subscribeEvent(event.getClass(), ms);
        assertTrue(messageBus.isSubscribedToEvent(event.getClass(), ms));
    }

    @Test
    public static void subscribeBroadcast() {
        messageBus.subscribeBroadcast(broadcast.getClass(), ms);
        assertTrue(messageBus.isSubscribedToBroadcast(broadcast.getClass(), ms));
    }

    @Test
    public void complete() {
        ftr.resolve("Mask on, mask off");
        assertTrue(ftr.isDone());
    }

    @Test
    public void sendBroadcast(){
        messageBus.subscribeBroadcast(broadcast.getClass(), ms);
        messageBus.sendBroadcast(broadcast);
        Message msg;
        try {
            msg = messageBus.awaitMessage(ms);
        }
        catch (InterruptedException e) {
            msg = null;
            fail();
        }
        assertEquals(broadcast, msg);
    }

    @Test
    public void sendEvent(){
        messageBus.subscribeEvent(event.getClass(), ms);
        messageBus.sendEvent(event);
        Message msg;
        try {
            msg = messageBus.awaitMessage(ms);
        }
        catch (InterruptedException e) {
            msg = null;
            fail();
        }
        assertEquals(event, msg);
    }

    @Test
    public void register(){
        assertTrue(messageBus.isRegistered(ms));
    }

    @Test
    public void unregister(){
        messageBus.unregister(ms);
        assertFalse(messageBus.isRegistered(ms));
    }

}