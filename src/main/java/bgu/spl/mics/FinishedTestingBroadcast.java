package bgu.spl.mics;

import bgu.spl.mics.application.objects.Model;

public class FinishedTestingBroadcast implements Broadcast {

    private final Model model;

    public FinishedTestingBroadcast(Model m){
        this.model = m;
    }

    public Model getModel() {
        return model;
    }
}
