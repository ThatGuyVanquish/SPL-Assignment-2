package bgu.spl.mics;

import bgu.spl.mics.application.objects.Model;

public class FinishedTrainingEvent implements Event<Model>{
    private Model model;
    public FinishedTrainingEvent(Model model){
        this.model = model;
    }

    public Model getModel() {
        return model;
    }
}
