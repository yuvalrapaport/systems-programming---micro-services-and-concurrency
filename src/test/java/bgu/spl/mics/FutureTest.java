package bgu.spl.mics;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {

    private static Future<String> future;

    @Before
    public void setUp() throws Exception {
        future = new Future<>();
    }

    @Test
    public void get() {
        String s = "toTest";
        future.resolve(s);
        assertTrue(future.get().equals(s));
    }

    @org.junit.Test
    public void resolve() {
        String s = "toTest";
        assertFalse(future.isDone());
        future.resolve(s);
        assertTrue(future.get().equals(s));
        assertTrue(future.isDone());
    }

    @Test
    public void isDone() {
        assertFalse(future.isDone());
        String s = "toTest";
        future.resolve(s);
        assertTrue(future.isDone());
    }

    @Test
    public void testGet() {
        String s = "toTest";
        assertNull(future.get(1000, TimeUnit.MILLISECONDS));
        future.resolve(s);
        assertTrue(future.get(1000, TimeUnit.MILLISECONDS).equals(s));
    }


}