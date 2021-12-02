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

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void get() {
        ftr.resolve("test");
        String results = ftr.get();
        //im braindead now so if you got any strength left in you, do it
    }

    @Test
    public void resolve() {
    }

    @Test
    public void isDone() {
    }

    @Test
    public void testGet() {
    }
}