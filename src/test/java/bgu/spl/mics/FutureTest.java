package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bgu.spl.mics.Future;
import static org.junit.Assert.*;

public class FutureTest {

    private Future<String> ftr;

    @Before
    public void setUp() throws Exception {
        ftr = new Future();
    }

    @Test
    public void get() {
        ftr.resolve("test");
        assertSame("test", ftr.get());
    }

    @Test
    public void resolve() {
        ftr.resolve("test");
        assertTrue(ftr.isDone());
        assertSame("test", ftr.get());
    }

    @Test
    public void testGet() {
     // i think it is unnecessary
    }
}