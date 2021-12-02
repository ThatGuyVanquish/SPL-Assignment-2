import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;
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
    }

    @Test
    public void testGet() {
     // i think it is unnecessary
    }
}