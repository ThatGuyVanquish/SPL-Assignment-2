package bgu.spl.mics.application.objects;

import org.junit.Before;
import org.junit.Test;
import bgu.spl.mics.application.objects.*;
import static org.junit.Assert.*;

public class CPUTest {

    private CPU cpu;
    private Data data;

    @Before
    public void setUp() throws Exception {
        this.cpu = new CPU(69420);
        this.data = new Data(Data.Type.Tabular, 1000);
    }

    @Test
    public void compute() {
        assertTrue(this.cpu.compute(data.batch()));
    }
}