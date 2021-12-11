package bgu.spl.mics;

import bgu.spl.mics.application.objects.Model;

public class TrainModelEvent implements Event<Model>{ // Not sure Event<Model> is the correct implementation

    private Model model;

    public TrainModelEvent(Model model){
        this.model = model;
    }

    public Model getModel() {
        return this.model;
    }
}
