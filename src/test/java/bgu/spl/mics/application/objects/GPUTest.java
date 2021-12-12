package bgu.spl.mics.application.objects;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import bgu.spl.mics.application.objects.*;

import java.util.Vector;

public class GPUTest {

    private Model m;
    private GPU gpu;
    private Cluster c;
    private Data data;
    private Student std;

    @Before
    public void setUp() {
        this.c = new Cluster();
        this.data = new Data(Data.Type.Tabular, 1);
        this.gpu = new GPU(GPU.Type.RTX3090);
        this.m = new Model("Batmobile", this.data, this.std);
        this.std = new Student("Alfred", "Batman's Laundry", Student.Degree.PhD);
    }

    @Test
    public void getType() {
        assertSame(this.gpu.getType(), GPU.Type.RTX3090);
    }

    @Test
    public void getModel() {
        //this.gpu.train(this.m);
        assertSame(this.m, gpu.getModel());
    }

    @Test
    public void train() {
        assertSame(this.gpu.getModel().getStatus(), Model.status.PreTrained);
        //this.gpu.train(this.m);
        assertSame(this.gpu.getModel().getStatus(), Model.status.Trained);
    }

    @Test
    public void test() {
        assertSame(this.gpu.getModel().getStatus(), Model.status.Trained);
        this.gpu.test(this.m);
        assertSame(this.gpu.getModel().getStatus(), Model.status.Tested);
    }
}