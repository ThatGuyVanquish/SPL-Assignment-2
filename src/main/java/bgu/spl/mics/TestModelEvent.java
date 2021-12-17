package bgu.spl.mics;

import bgu.spl.mics.application.objects.Model;

public class TestModelEvent implements Event<Model>{

    private final Model model;

    public TestModelEvent(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return this.model;
    }
}
