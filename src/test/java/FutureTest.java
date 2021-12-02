

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;
import static org.junit.Assert.*;

public class FutureTest {

    private Future<Model> preTrained;
    private Future<Model> trained;
    @Before
    public void setUp() throws Exception {
        preTrained = new Future();
        trained = new Future();

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void get() {
        try {

        }

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