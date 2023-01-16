package bgu.spl.mics;


import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;


import static org.junit.Assert.*;

public class MessageBusTest {

    private static MessageBus msg;

    private class testMS extends MicroService {
        testMS(String name){ super(name); }
        @Override
        protected void initialize() {
        }
    }

    @Before
    public void setUp() throws Exception {
        msg = MessageBusImpl.getInstance();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void subscribeEvent() throws InterruptedException {
        testMS m = new testMS("testMS");
        TestModelEvent event = new TestModelEvent();
        assertThrows(IllegalStateException.class, ()-> msg.subscribeEvent(event.getClass(),m));
        msg.register(m);
        Future<String> future = msg.sendEvent(event);
        assertNull(future);
        msg.subscribeEvent(event.getClass(),m);
        msg.sendEvent(event);
        assertEquals(event,msg.awaitMessage(m));
        msg.unregister(m);
    }

    @Test
    public void subscribeBroadcast() throws InterruptedException {
        testMS m1 = new testMS("testMS1");
        testMS m2 = new testMS("testMS2");
        PublishConferenceBroadcast broadcast = new PublishConferenceBroadcast();
        assertThrows(IllegalStateException.class, ()-> msg.subscribeBroadcast(broadcast.getClass(),m1));
        assertThrows(IllegalStateException.class, ()-> msg.subscribeBroadcast(broadcast.getClass(),m2));
        msg.register(m1);
        msg.register(m2);
        msg.subscribeBroadcast(broadcast.getClass(),m1);
        msg.subscribeBroadcast(broadcast.getClass(),m2);
        msg.sendBroadcast(broadcast);
        Message msg1 = msg.awaitMessage(m1);
        Message msg2 = msg.awaitMessage(m2);
        assertEquals(broadcast,msg1);
        assertEquals(broadcast,msg2);
        msg.unregister(m1);
        msg.unregister(m2);
    }

    @Test
    public void complete() {
        TestModelEvent event = new TestModelEvent();
        testMS m = new testMS("testMS");
        msg.register(m);
        msg.subscribeEvent(event.getClass(),m);
        Future<String> f = msg.sendEvent(event);
        msg.complete(event, "completed");
        assertEquals("completed",f.get());
        msg.unregister(m);
    }

    @Test
    public void sendBroadcast() throws InterruptedException {
        PublishConferenceBroadcast broadcast = new PublishConferenceBroadcast();
        testMS m1 = new testMS("testMS1");
        testMS m2 = new testMS("testMS2");
        msg.register(m1);
        msg.register(m2);
        msg.subscribeBroadcast(broadcast.getClass(),m1);
        msg.subscribeBroadcast(broadcast.getClass(),m2);
        msg.sendBroadcast(broadcast);
        Message msg1 = msg.awaitMessage(m1);
        Message msg2 = msg.awaitMessage(m2);
        assertEquals(broadcast,msg1);
        assertEquals(broadcast,msg2);
        msg.unregister(m1);
        msg.unregister(m2);

    }

    @Test
    public void sendEvent() throws InterruptedException {
        TestModelEvent event = new TestModelEvent();
        testMS m = new testMS("testMS");
        msg.register(m);
        Future<String> future = msg.sendEvent(event);
        assertNull(future);
        msg.subscribeEvent(event.getClass(),m);
        future = msg.sendEvent(event);
        msg.complete(event, "completed");
        assertEquals("completed",future.get() );
        assertEquals(event,msg.awaitMessage(m));
        msg.unregister(m);
    }

    @Test
    public void register() throws InterruptedException {
        testMS m = new testMS("testMS");
        TestModelEvent event = new TestModelEvent();
        assertThrows(IllegalStateException.class, ()->msg.subscribeEvent(event.getClass(),m));
        msg.register(m);
        msg.subscribeEvent(event.getClass(),m);
        msg.sendEvent(event);
        assertEquals(event,msg.awaitMessage(m));
        msg.unregister(m);
    }

    @Test
    public void unregister() {
        TestModelEvent event = new TestModelEvent();
        testMS m = new testMS("testMS");
        msg.register(m);
        msg.subscribeEvent(event.getClass(),m);
        msg.unregister(m);
        Future<String> f = msg.sendEvent(event);
        assertNull(f);
        assertThrows(IllegalStateException.class, ()->msg.subscribeEvent(event.getClass(),m));
    }

    @Test
    public void awaitMessage() throws InterruptedException {
        TestModelEvent event = new TestModelEvent();
        testMS m = new testMS("testMS");
        assertThrows(IllegalStateException.class,()->msg.awaitMessage(m));
        msg.register(m);
        msg.subscribeEvent(event.getClass(),m);
        msg.sendEvent(event);
        assertEquals(event,msg.awaitMessage(m));
        msg.unregister(m);
    }
}