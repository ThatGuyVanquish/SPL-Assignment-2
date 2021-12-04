package bgu.spl.mics;

import org.junit.Before;
import org.junit.Test;

import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleEventHandlerService;

public class MessageBusImplTest {

    private static MessageBusImpl messageBus;
    private static ExampleEvent e;
    private static MicroService m;
    private static MicroService m2;
    private static ExampleEvent e2;
    @Before
    public void setup() {
        messageBus = MessageBusImpl.getInstance();
         e = new ExampleEvent("Test1");
         m = new ExampleEventHandlerService("Test2", new String[]{"tst,tst"});
         e2 = new ExampleEvent("Test3");
         m2 = new ExampleEventHandlerService("Test4", new String[]{"tst,tst"});
    }

    @Test
    public static void subscribeEvent() {

    }

    @Test
    public static void subscribeBroadcast() {

    }

    @Test
    public void complete() {

    }


    @Test
    public void sendBroadcast(){

    }

    @Test
    public void sendEvent(){

    }

    @Test
    public void register(){

    }

    @Test
    public void unregister(){

    }



}