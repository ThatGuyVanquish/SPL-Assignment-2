package bgu.spl.mics;

import bgu.spl.mics.application.objects.Model;

public class FinishedTrainingBroadcast implements Broadcast{
    private Model model;
    public FinishedTrainingBroadcast(Model model){
        this.model = model;
    }

    public Model getModel() {
        return model;
    }
}
